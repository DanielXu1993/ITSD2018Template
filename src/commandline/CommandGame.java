package commandline;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class CommandGame
{
    // define fields of the CommandGame class by following line of codes and variables
    private GameHandler game;// define the object of GameHandler class
    
    private String[] category;// record category title
    
    private int currentCateIndex;// define the current category index
    
    private StringBuilder finalLog = new StringBuilder(); // record log data for the final log file
    
    public CommandGame()
    {
        // the initial value is defined in this constructor
        game = new GameHandler("StarCitizenDeck.txt", 4);
        category = game.getCategoryDetail();
        this.initFinalLog();
    }
    /**
     * the cardWithCate method aims to display card information that includes its category
     */
    public String cardWithCate(String card)
    {
        // define the array to store the card information split by a space
        String[] cardInfo = card.split(" ");
        // collect the card information here
        // utilizing the StringBuilder is a principal operation to append the String representation of the cardInfo array elements to the method of cardWithCate
        StringBuilder cardWithCate = new StringBuilder();
        for (int i = 0; i < cardInfo.length; i++)
        {
            if (i == 0)
                cardWithCate.append(cardInfo[0]);
            else
                // add cardInfo from String back to cardWithCate
                cardWithCate.append("," + category[i] + ":" + cardInfo[i]);
        }
        // return String "card" back to cardWithCate method
        return cardWithCate.toString();
    }
    
    // declare the roundInfo method as collecting the information of the game round
    public void roundInfo()
    {
        // define how many round and which player's turn in this game
        int roundNum = game.getRoundCount();
        int playerIndex = game.getCurrentPlayer();
        // get and display the information of game round number and active player
        String activePlayer = game.playerName(playerIndex);
        finalLog.append(String.format("%s%n",
            "Round Number: " + roundNum + ", active player: " + activePlayer));
        // draw a card and display its category through get() and arrays[element index][0], each element is accessed by its numerical index which begins with 0
        String cardDrawn = cardWithCate(game.getPlayersWithCards()[playerIndex][0]);
        // print the information of the player and game round in the console
        System.out
            .println("Round Number: " + roundNum + ", active player: " + activePlayer);
        System.out.println("current card: " + cardDrawn);
    }
    
    // define this method to reveal different card's categories
    public int inputCategory()
    {
        // create a new instance of Scanner class and store it in the variable "input" while call the constructor of this class, with the parameter "System.in"
        Scanner input = new Scanner(System.in);
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < category.length; i++)
        {
            // get the number of player's choice
            sb.append("," + category[i]);
        }
        // delete a charactor "," from integer and return to sb
        sb.deleteCharAt(0);
        System.out.print("input a category: " + sb);
        // input.next() inputs value of a string variable "categotyName", and trim() eliminates leading and trailing spaces
        String categoryName = input.next().trim();
        int index = -1;
        for (int i = 1; i < category.length; i++)
        {
            if (categoryName.equals(category[i]))
            {
                index = i;
                break;
            }
        }
        // if input is nothing to read or the wrong number of the category, then it will draw attention to invalid and retry messages
        if (index == -1)
        {
            System.out.println("Your input is invalid,please retry");
            return inputCategory();
        }
        else
            return index;
    }
    // define this method to choose the category that want to compare with computer players
    public void selectedCategory()
    {
        // define the initial category of the card is null first
        String cat = null;
        int playerIndex = game.getCurrentPlayer();
        if (playerIndex == 0)
            this.currentCateIndex = inputCategory();
        else
            this.currentCateIndex = game.AISelectCateIndex();
        // get the category of the card from the card array by the card index
        cat = category[currentCateIndex];
        String[][] firstCardsDetail = game.getFirstCardsDetail();
        // display the card information selected by the player
        finalLog.append(String.format("%s%n", " " + cat + " has been selected,value is : "
            + firstCardsDetail[playerIndex][currentCateIndex]));
        System.out.println(cat + " has been selected");
    }
    // define this method to display the card which wins in this round
    public String winnedCards(String[] cards)
    {
        StringBuilder sb = new StringBuilder();
        for (String card : cards)
        {
            if (card != null)
                sb.append(String.format("%s%n", cardWithCate(card)));
        }
        return sb.toString();
    }
    
    // define this method to process the game
    public void processGame()
    {
        //  get the round information
        roundInfo();
        // get the selected category information
        selectedCategory();
        // get the first chosen card
        String[] firstCards = game.pickFirstCards();
        finalLog.append(String.format("%s%n", "current cards in play: "));
        for (int i = 0; i < firstCards.length; i++)
        {
            if (firstCards[i] != null)
                finalLog.append(
                    String.format("%s%n", game.playerName(i) + ":" + firstCards[i]));
        }
        // create the communal pile of cards and compare the card category with other players
        String[] communalPile = game.getCommunalPile();
        int winnerIndex = game.winRoundCards(currentCateIndex);
        if (winnerIndex != -1)
        {
            // get the winner's name and print it out with cards, after that, winner remain drawing a card from communal card pile
            String winner = game.playerName(winnerIndex);
            System.out.println(winner + " won");
            System.out.println("winning cards: ");
            System.out.print(winnedCards(firstCards));
            System.out.println(winnedCards(communalPile));
            if (communalPile[0] != null)
            {
                finalLog
                    .append(String.format("%s%n", winner + " take communal pile cards"));
            }
        }
        else
        {
            //when this round game is a draw and then print the draw game
            System.out.println("it was a draw");
            System.out.println("communal pile cards");
            System.out.println(winnedCards(communalPile));
            finalLog.append(String.format("%s%n", "communal pile cards:"));
            finalLog.append(String.format("%s", getCards(communalPile)));
        }
        finalLog.append(playerCardsAfterRound());
        isEliminated();
    }
    // define the method to judge whether the game runs
    public boolean isRun()
    {
        return game.isRun();
    }
    // eliminate the players who is no more cards in the game
    public void isEliminated()
    {
        // define an array of arrays (multidimensional array whose components are themselves arrays) to collect the information of [playerIndex][0] with the number of cards
        String[][] playersWithCards = game.getPlayersWithCards();
        for (int i = 0; i < playersWithCards.length; i++)
        {
            for (int j = 0; j < playersWithCards[i].length; j++)
            {
                //when players do not have any cards,they will leave the game
                if (playersWithCards[i][0] == null)
                {
                    System.out.println(game.playerName(i) + ": no cards left");
                    break;
                }
            }
        }
    }
    // define this method to get the final winner of the game and count how many times did the winner triumph during this round
    public void finalWinner()
    {
        String winnerInfo = game.finalWinnerInfo();
        finalLog.append(winnerInfo);
        System.out.println(winnerInfo);
        game.insertInfo();
        
    }
    // define the method to display the name of the card
    public String getCards(String[] cards)
    {
        StringBuilder sb = new StringBuilder();
        // for (int = i; i < cards.length; i++), sb = cards[0]
        for (String card : cards)
        {
            if (card == null)
                break;
            sb.append(String.format("%s%n", card));
        }
        return sb.toString();
    }
    // define this method to display the card number of players after the game
    public String playerCardsAfterRound()
    {
        // define the StringBuilder to display the information
        StringBuilder sb = new StringBuilder();
        // define the 2D array to collect the information about player with cards
        String[][] playersWithCards = game.getPlayersWithCards();
        for (int i = 0; i < playersWithCards.length; i++)
        {
            //if the player still have card, this can show the number of cards for each player
            if (playersWithCards[i][0] != null)
            {
                sb.append(String.format("%s's cards :%n", game.playerName(i)));
                sb.append(getCards(playersWithCards[i]));
            }
            // if any players do not have any cards, "no cards left" will display
            else
            {
                sb.append(game.playerName(i) + ": ");
                sb.append(String.format("%s's cards :%n", "no cards left"));
            }
            sb.append(String.format("%n"));
        }
        sb.append(
            String.format("%s%n", "-----------------------------------------------"));
        return sb.toString();
    }
    
    // create this method to display the final information about the winner
    public void initFinalLog()
    {
        // once finish the deck of card, the information about the complete deck should be displayed
        finalLog.append(String.format("%s%n", "the complete deck :"));
        finalLog.append(getCards(game.getAllCards()));
        finalLog.append(
            String.format("%s%n", "-----------------------------------------------"));
        // after on round game, cards should be shuffled for next round game
        finalLog.append(String.format("%s%n", "deck after it has been shuffled :"));
        finalLog.append(getCards(game.getShuffleCard()));
        finalLog.append(
            String.format("%s%n", "-----------------------------------------------"));
        
        finalLog.append(playerCardsAfterRound());
    }
    // the try block of writeLog method execute finally block if an unexpected exception occurs and the new FileWriter statment fails and throws an IOException
    public void writeLog()
    {
        FileWriter fw = null;
        try
        {
            try
            {
                // write the final log into the file named toptrumps.log
                fw = new FileWriter("toptrumps.log");
                fw.write(finalLog.toString());
            }
            finally
            {
                // while there is nothing to write, finally block cleans up and closes the FileWriter
                if (fw != null)
                    fw.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
