package commandline;

import java.io.IOException;
import java.util.Scanner;

/**
 * Top Trumps command line application
 */
public class TopTrumpsCLIApplication
{
    
    /**
     * This main method is called by TopTrumps.java when the user specifies that they want to run in
     * command line mode. The contents of args[0] is whether we should write game logs to a file.
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args)
    {
        
        boolean writeGameLogsToFile = false; // Should we write game logs to file?
        if (args[0].equalsIgnoreCase("true"))
            writeGameLogsToFile = true; // Command line selection
            
        // State
        boolean userWantsToQuit = false; // flag to check whether the user wants to quit the application
        
        // Loop until the user wants to exit the game
        while (!userWantsToQuit)
        {
            String userChoice = inputChoice();
            if ("1".equals(userChoice))
            {
                DBHandler db = new DBHandler();
                System.out.println(db.displayStats());
            }
            else if ("0".equals(userChoice))
                userWantsToQuit = true; // use this when the user wants to exit the game
            else
            {
                CommandGame game = new CommandGame();
                while ("2".equals(userChoice))
                {
                    game.processGame();
                    System.out.println(
                        "----------------------------------------------------------------");
                    if (!game.isRun())
                    {
                        game.finalWinner();
                        if (writeGameLogsToFile)
                            game.writeLog();
                        break;
                    }
                }
            }
            
        }
        
    }
    
    public static String inputChoice()
    {
        Scanner input = new Scanner(System.in);
        System.out.print(
            "Do you want to print the statistics of games or play a new game or exit?");
        System.out.println("[statistics : 1 , new game : 2 , exit : 0]");
        String choice = input.next().trim();
        if ((!"1".equals(choice)) && (!"2".equals(choice)) && (!"0".equals(choice)))
        {
            System.out.println("Your input is invalid,please retry");
            return inputChoice();
        }
        else
            return choice;
    }
}
