package Commands.LeagueOfLegends.Champions;

import Commands.BasicCommand;
import JSON.JSONArray;
import JSON.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class ListChampions extends BasicCommand {
    private static final String API_KEY_PATH = "APIKey.ini";
    private static final String URL_CHAMPION_DATA = "https://global.api.pvp.net/api/lol/static-data/na/v1.2/champion?api_key=";
    public void runCommand(String input){
        try{
            String key = Files.readAllLines(Paths.get(API_KEY_PATH)).get(0);
            String json = readURL(URL_CHAMPION_DATA+key);
            JSONArray championNames = new JSONObject(json).getJSONObject("data").names();
            println(championNames.toString());
        }catch(IOException | IndexOutOfBoundsException e){
            println("Missing API Key Config file. Place API Key in " + API_KEY_PATH);
        }
    }

    @Override
    public String getCommandText() {
        return "list";
    }

    @Override
    public String getInformation() {
        return "Lists all champion names";
    }
}
