package Commands.LeagueOfLegends.Champions;

import Commands.BasicCommand;
import JSON.JSONArray;
import JSON.JSONObject;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class UpdateChampions extends BasicCommand {
    private static final String baseChampionURL = "http://champion.gg/champion/";
    private static final int[] UTILITIES = {2003,2043,3363,3364,2139,2140,2138};

    public void runCommand(String input){
        List<String> championNames = getChampionNames();
        if(championNames.size() == 0) return;
        println("Starting creation of item sets - This could take a few minutes");
        for(String championName : championNames){
            //Load Champion.gg data for this champion
            println("> Creating champion builds for " + championName);
            //Create directory for champion
            File directory = new File("Config/Champions/"+championName+"/Recommended");
            if(directory.mkdirs()){
                println("Created directory for " + championName);
            }
            String[]entries = directory.list();
            println("Deleting old item sets");
            for(String s: entries){
                File currentFile = new File(directory.getPath(),s);
                currentFile.delete();
            }
            //Get all roles for this champion
            for(String role : getRoles(championName)){
                String htmlText = readURL(baseChampionURL + championName + "/" + role);
                println("Generating Most Frequent build for " + role);
                generateMostFrequent(championName,htmlText,role);
                println("Generating Highest Win Percent build for " + role);
                generateHighestWinRate(championName,htmlText,role);
            }
        }
        println("> Finished updating item sets");
    }

    private List<String> getChampionNames(){
        //Load champion names from Riot Games API
        final String API_KEY_PATH = "APIKey.ini";
        final String URL_CHAMPION_DATA = "https://global.api.pvp.net/api/lol/static-data/na/v1.2/champion?api_key=";
        try{
            String key = Files.readAllLines(Paths.get(API_KEY_PATH)).get(0);
            String json = readURL(URL_CHAMPION_DATA+key);
            if(json.equals("")){
                println("Could not load champion names from API");
                return new ArrayList<>();
            }
            JSONArray championNames = new JSONObject(json).getJSONObject("data").names();
            return championNames.toList().stream()
                    .map(object -> (object != null ? object.toString() : null))
                    .collect(Collectors.toList());
        }catch(IOException | IndexOutOfBoundsException e){
            println("Missing API Key Config file. Place API Key in " + API_KEY_PATH);
        }
        return new ArrayList<>();
    }
    private List<String> getRoles(String championName){
        String html = readURL(baseChampionURL + championName);
        ArrayList<String> roles = new ArrayList<>();
        String[] split = html.split("/champion/"+championName+"/");
        for(int i = 1; i < split.length; i++) {
            roles.add(split[i].substring(0, split[i].indexOf("\"")));
        }
        return roles;
    }

    private void generateHighestWinRate(String championName, String htmlText, String role){
        //Setup root of json file
        JSONObject root = new JSONObject();
        root.put("title", role + " Highest WinRate");
        root.put("champion", championName);
        root.put("type", "custom");
        root.put("map", "any");
        root.put("mode", "any");
        root.put("priority", false);
        root.put("sortrank", 1);

        JSONArray itemBlocks = new JSONArray();

        //Read html to find most frequent starting items
        String temp = htmlText.substring(htmlText.indexOf("Highest Win % Starters"));
        temp = temp.substring(0, temp.indexOf("<div class=\"build-text"));
        itemBlocks.put(extractItems("Starters",temp,3340));
        println("Generated Highest WinRate starting items");
        //Read html to find most frequent core build
        temp = htmlText.substring(htmlText.indexOf("Highest Win % Core Build"));
        temp = temp.substring(0, temp.indexOf("<div class=\"build-text"));
        itemBlocks.put(extractItems("Core Build",temp));
        println("Generated Highest WinRate core build");
        //Add utility items to build
        println("Adding Utilities to Highest WinRate build");
        itemBlocks.put(extractItems("Utilities","",UTILITIES));

        root.put("blocks", itemBlocks);
        try{
            Path file = Paths.get("Config/Champions/"+championName+"/Recommended/"+role+"HighestWinRate.json");
            Files.write(file,root.toString().getBytes());
        }catch(IOException e){
            printError("Failed to save item set file");
        }
    }
    private void generateMostFrequent(String championName, String htmlText, String role){
        //Setup root of json file
        JSONObject root = new JSONObject();
        root.put("title", role + " Most Frequent");
        root.put("champion", championName);
        root.put("type", "custom");
        root.put("map", "any");
        root.put("mode", "any");
        root.put("priority", false);
        root.put("sortrank", 1);

        JSONArray itemBlocks = new JSONArray();

        //Read html to find most frequent starting items
        String temp = htmlText.substring(htmlText.indexOf("Most Frequent Starters"));
        temp = temp.substring(0, temp.indexOf("<div class=\"build-text"));
        itemBlocks.put(extractItems("Starters",temp,3340));
        println("Generated Most Frequent starting items");
        //Read html to find most frequent core build
        temp = htmlText.substring(htmlText.indexOf("Most Frequent Core Build"));
        temp = temp.substring(0, temp.indexOf("<div class=\"build-text"));
        itemBlocks.put(extractItems("Core Build",temp));
        println("Generated Most Frequent core build");
        //Add utility items to build
        println("Adding Utilities to Most Frequent build");
        itemBlocks.put(extractItems("Utilities","",UTILITIES));

        root.put("blocks", itemBlocks);
        try{
            Path file = Paths.get("Config/Champions/"+championName+"/Recommended/"+role+"MostFrequent.json");
            Files.write(file,root.toString().getBytes());
        }catch(IOException e){
            printError("Failed to save item set file");
        }
    }
    private JSONObject extractItems(String title, String html, int... additionalItems){
        String[] items = html.split(".png");
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        //Seek and add items to map
        for(int i = 0; i < items.length-1; i++){
            String s = items[i];
            String itemID = s.substring(s.lastIndexOf("/",s.length())).replaceAll("/","");
            if(map.containsKey(itemID)){
                map.put(itemID,map.get(itemID)+1);
            }else{
                map.put(itemID,1);
            }
        }
        //Add additional items to map
        for(int i : additionalItems){
            map.put(i+"",1);
        }
        JSONObject block = new JSONObject();
        block.put("type", title);
        block.put("items", createBlocks(map));
        return block;
    }
    private JSONArray createBlocks(LinkedHashMap<String, Integer> map){
        //Create JSONArray of items from input map
        JSONArray blocks = new JSONArray();
        Iterator it = map.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry pair = (Map.Entry)it.next();
            String itemID = pair.getKey().toString();
            int count = Integer.parseInt(pair.getValue().toString());
            if(itemID.equals("2003") || itemID.equals("2010")){
                //If it is a health potion or biscuit, also add the other
                blocks.put(createItemBlock("2003",count));
                blocks.put(createItemBlock("2010",count));
            }else{
                blocks.put(createItemBlock(itemID,count));
            }
            it.remove();
        }
        return blocks;
    }
    private JSONObject createItemBlock(String itemID, int count){
        //Create JSONObject of item from input itemID and count
        JSONObject obj = new JSONObject();
        obj.put("id", itemID);
        obj.put("count", count);
        return obj;
    }


    @Override
    public String getCommandText() {
        return "update";
    }
    @Override
    public String getInformation() {
        return "Creates or updates itemsets for all champions";
    }
}
