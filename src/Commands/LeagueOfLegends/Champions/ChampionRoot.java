package Commands.LeagueOfLegends.Champions;

import Commands.BasicCommand;

public class ChampionRoot extends BasicCommand {
    public ChampionRoot(){
        subcommands = new BasicCommand[2];
        subcommands[0] = new UpdateChampions();
        subcommands[1] = new ListChampions();
    }
    public String getCommandText(){
        return "champions";
    }
    public String getInformation(){
        return "Displays information about all champions";
    }
}
