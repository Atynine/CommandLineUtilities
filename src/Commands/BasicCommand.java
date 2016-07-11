package Commands;

import java.util.Scanner;

public abstract class BasicCommand {
    protected BasicCommand [] subcommands = {};

    public String getInput(){
        final Scanner INPUT_READER = new Scanner(System.in);
        print("> ");
        String input = INPUT_READER.nextLine();
        if(input.equalsIgnoreCase("help")){
            displayHelp();
            return "";
        }
        return input;
    }
    public BasicCommand getCommand(String input){
        //If empty command is input, return no command
        if(input == null || input.equals("")) return null;
        //Make input string lowercase to compare to available commands
        input = input.toLowerCase();
        for(BasicCommand command : this.subcommands){
            //Check if input text starts with command text
            if(command.validate(input)){
                return command;
            }
        }
        return null;
    }

    public void displayHelp(){
        println("\n> Available commands");
        println("help - Lists all commands available");
        for(BasicCommand c : this.subcommands){
                println(c.getCommandText() + " - " + c.getInformation());
        }
        println("");
    }
    public void runCommand(String input){
        //Default runCommand setup, override to set different behaviour
        //Check if command should end here or continue digging
        if(input.startsWith(getCommandText())){
            //Remove this commands prefix from the input so we don't pass it on the the nested command
            input = input.replace(getCommandText(), "");
            input = input.trim();
            //Check if /directory/ help was input
            if(input.equals("") || input.equalsIgnoreCase("help")){
                displayHelp();
                return;
            }else{
                BasicCommand command = getCommand(input);
                if(command == null){
                    println("Invalid command.");
                    displayHelp();
                }else{
                    command.runCommand(input);
                    return;
                }
            }
        }else{
            displayHelp();
        }
    }

    public boolean validate(String input){
        if(input.startsWith(this.getCommandText())) return true;
        return false;
    }

    public abstract String getCommandText();
    public abstract String getInformation();

    public static void println(Object o){
        System.out.println("> " + o.toString());
    }
    public static void print(Object o){
        System.out.print(o);
    }
    public static void printError(Object o){System.out.println("- ERROR- " + o);}
}
