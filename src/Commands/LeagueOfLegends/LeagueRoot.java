package Commands.LeagueOfLegends;

import Commands.BasicCommand;
import Commands.LeagueOfLegends.Champions.ChampionRoot;

public class LeagueRoot extends BasicCommand {
    public LeagueRoot(){
        subcommands = new BasicCommand[1];
        subcommands[0] = new ChampionRoot();
    }
    public String getCommandText(){
        return "league";
    }
    public String getInformation(){
        return "Access the League of Legends subcommands";
    }
}
