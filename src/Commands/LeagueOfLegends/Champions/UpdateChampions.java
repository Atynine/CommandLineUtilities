package Commands.LeagueOfLegends.Champions;

import Commands.BasicCommand;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class UpdateChampions extends BasicCommand {
    private static final String[] championNames = {"Aatrox","Ahri","Akali","Alistar","Amumu","Anivia","Annie","Ashe", "AurelionSol", "Azir", "Bard", "Blitzcrank", "Brand", "Braum", "Caitlyn", "Cassiopeia", "Chogath", "Corki", "Darius", "Diana", "DrMundo", "Draven", "Ekko", "Elise", "Evelynn", "Ezreal", "FiddleSticks", "Fiora", "Fizz", "Galio", "Gangplank", "Garen", "Gnar","Gragas", "Graves", "Hecarim", "Heimerdinger", "Illaoi", "Irelia", "Janna", "JarvanIV", "Jax", "Jayce", "Jhin", "Jinx", "Kalista", "Karma", "Karthus", "Kassadin", "Katarina", "Kayle", "Kennen", "KhaZix", "Kindred", "KogMaw", "Leblanc", "LeeSin", "Leona", "Lissandra", "Lucian", "Lulu", "Lux","Malphite", "Malzahar", "Maokai", "MasterYi", "MissFortune", "Mordekaiser","Morgana", "Nami", "Nasus","Nautilus", "Nidalee", "Nocturne", "Nunu", "Olaf", "Orianna", "Pantheon", "Poppy", "Quinn", "Rammus", "RekSai", "Renekton", "Rengar", "Riven", "Rumble", "Ryze", "Sejuani" , "Shaco", "Shen", "Shyvana", "Singed", "Sion", "Sivir", "Skarner", "Sona", "Soraka", "Swain", "Syndra", "TahmKench", "Taliyah", "Talon", "Taric", "Teemo", "Thresh", "Tristana","Trundle","Tryndamere","TwistedFate","Twitch","Udyr","Urgot","Varus","Vayne","VelKoz","Vi","Viktor","Vladimir","Volibear","Warwick","MonkeyKing","Xerath","XinZhao","Yasuo","Yorick","Zac","Zed","Ziggs","Zilean","Zyra"};
    private static final String baseChampionURL = "http://champion.gg/champion/";
    private static final int[] UTILITIES = {2003,2043,3363,3364,2139,2140,2138};
    public void runCommand(String input){
        println("Starting creation of item sets - This could take a few minutes");
        for(String championName : championNames){
            //Load Champion.gg data for this champion
            println("> Creating champion builds for " + championName);
            String htmlText = readURL(baseChampionURL + championName);
            if(htmlText.equals("")) continue;
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
            String[] split = htmlText.split("/champion/"+championName+"/");
            for(int i = 1; i < split.length; i++){
                String role = split[i].substring(0,split[i].indexOf("\">"));
                htmlText = readURL(baseChampionURL + championName + "/" + role);
                println("Generating Most Frequent build for " + role);
                generateMostFrequent(championName,htmlText,role);
                println("Generating Highest Win Percent build for " + role);
                generateHighestWinPercent(championName,htmlText,role);
            }
        }
        println("> Finished updating item sets");
    }


    private void generateHighestWinPercent(String championName, String htmlText, String role){
        //Add header to json file
        ArrayList<String> lines = new ArrayList<>();
        lines.add("{\"title\": \""+role+" Highest Win Percent\",\"type\": \"custom\",\"map\": \"any\",\"mode\": \"any\",\"priority\": false,\"sortrank\": 1,");
        lines.add("\"champion\": \""+championName+"\",");
        lines.add("\"blocks\": [{");
        //Read html to find most frequent starting items
        String temp = htmlText.substring(htmlText.indexOf("Highest Win % Starters"));
        temp = temp.substring(0, temp.indexOf("<div class=\"build-text"));
        extractItems(lines,"Starters",temp,3340);
        lines.add(",{");
        println("Generated Highest Win Percent starting items");
        //Read html to find most frequent core build
        temp = htmlText.substring(htmlText.indexOf("Highest Win % Core Build"));
        temp = temp.substring(0, temp.indexOf("<div class=\"build-text"));
        extractItems(lines,"Core Build",temp);
        println("Generated Highest Win Percent core build");
        lines.add(",{");
        //Add utility items to build
        println("Adding Utilities to Highest Win Percent build");
        extractItems(lines,"Utilities","",UTILITIES);
        lines.add("]}");
        try{
            Path file = Paths.get("Config/Champions/"+championName+"/Recommended/"+role+"HighestWinRate.json");
            Files.write(file,lines, Charset.forName("UTF-8"));
        }catch(IOException e){
            printError("Failed to save item set file");
        }
    }
    private void generateMostFrequent(String championName, String htmlText, String role){
        //Add header to json file
        ArrayList<String> lines = new ArrayList<>();
        lines.add("{\"title\": \""+role+" Most Frequent\",\"type\": \"custom\",\"map\": \"any\",\"mode\": \"any\",\"priority\": false,\"sortrank\": 1,");
        lines.add("\"champion\": \""+championName+"\",");
        lines.add("\"blocks\": [{");
        //Read html to find most frequent starting items
        String temp = htmlText.substring(htmlText.indexOf("Most Frequent Starters"));
        temp = temp.substring(0, temp.indexOf("<div class=\"build-text"));
        extractItems(lines,"Starters",temp,3340);
        lines.add(",{");
        println("Generated Most Frequent starting items");
        //Read html to find most frequent core build
        temp = htmlText.substring(htmlText.indexOf("Most Frequent Core Build"));
        temp = temp.substring(0, temp.indexOf("<div class=\"build-text"));
        extractItems(lines,"Core Build",temp);
        println("Generated Most Frequent core build");
        lines.add(",{");
        //Add utility items to build
        println("Adding Utilities to Most Frequent build");
        extractItems(lines,"Utilities","",UTILITIES);
        lines.add("]}");
        try{
            Path file = Paths.get("Config/Champions/"+championName+"/Recommended/"+role+"MostFrequent.json");
            Files.write(file,lines, Charset.forName("UTF-8"));
        }catch(IOException e){
            printError("Failed to save item set file");
        }
    }
    private void extractItems(ArrayList<String> list, String title, String html, int... additionalItems){
        String[] items = html.split(".png");
        list.add("\"items\": [");
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        for(int i = 0; i < items.length-1; i++){
            String s = items[i];
            String itemID = s.substring(s.lastIndexOf("/",s.length())).replaceAll("/","");
            if(map.containsKey(itemID)){
                map.put(itemID,map.get(itemID)+1);
            }else{
                map.put(itemID,1);
            }
        }
        for(int i = 0; i < additionalItems.length; i++){
            map.put(additionalItems[i]+"",1);
        }
        Iterator it = map.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry pair = (Map.Entry)it.next();
            String itemID = pair.getKey().toString();
            int count = Integer.parseInt(pair.getValue().toString());
            addItem(list,itemID,count);
            it.remove();
            if(it.hasNext()){
                list.add(",");
            }
        }
        list.add("],\"type\": \""+title+"\"}");
    }
    private void addItem(ArrayList<String> items, String itemID, int count){
        if(itemID.equals("2003") || itemID.equals("2010")){
            items.add("{\"count\":" + count + "," + "\"id\":\"2003\"},");
            items.add("{\"count\":" + count + "," + "\"id\":\"2010\"}");
        }else{
            items.add("{\"count\":" + count + "," + "\"id\":\"" + itemID + "\"}");
        }
    }

    private String readURL(String u){
        println("Attempting to connect to " + u);
        String text = "";
        try{
            URL url = new URL(u);
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            //Copy all text from opened URL to text variable
            String line;
            while((line = in.readLine()) != null){
                text+=line;
            }
            in.close();
        }catch(MalformedURLException e){
            printError("Invalid URL: " + u);
        }catch(IOException e){
            printError("Could not connect to " + u);
        }
        return text;
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
