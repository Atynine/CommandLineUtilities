import Commands.BasicCommand;
import Commands.LeagueOfLegends.Champions.UpdateChampions;
import Commands.LeagueOfLegends.LeagueRoot;

public class Boot extends BasicCommand {
    private boolean end = false;
    public Boot(){
        subcommands = new BasicCommand[2];
        subcommands[0] = new LeagueRoot();
        subcommands[1] = new UpdateChampions();
        while(!end){
            //Get user input
            String input = getInput();
            if(input.equals("")){
                continue;
            }
            BasicCommand command = getCommand(input);
            //Check if command is valid
            if(command == null){
                System.out.println("> Invalid command. Enter 'help' to view available commands");
                continue;
            }else{
                //Run command
                command.runCommand(input);
            }
        }
    }


    public static void main(String[] args){
        new Boot();
    }

    @Override
    public String getCommandText() {
        return null;
    }

    @Override
    public String getInformation() {
        return null;
    }
}
