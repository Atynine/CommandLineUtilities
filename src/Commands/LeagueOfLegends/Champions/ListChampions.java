package Commands.LeagueOfLegends.Champions;

import Commands.BasicCommand;

import java.io.File;
import java.io.FilenameFilter;

public class ListChampions extends BasicCommand {

    public void runCommand(String input){
        File file = new File("Config/Champions");
        String[] directories = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        System.out.print("> ");
        for(int i = 0; i < directories.length; i++){
            System.out.print("\""+directories[i]+"\"");
            if(i < directories.length-1){
                System.out.print(",");
            }
        }
        System.out.print("\n");
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
