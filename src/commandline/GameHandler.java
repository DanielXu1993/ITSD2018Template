package commandline;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class GameHandler
{
    private String[] categoryDetail;// define a String array to store the category detail of cards
    
    private String[] allCards;// to store the card information
    
    private String[] shuffleCard;// to store the information after shuffling the cards
    
    private String[][] playersWithCards;// to store the information of player with the card
    
    private String[][] firstCardsDetail;// to collect the details of the first card which are shown on the screen
    
    private String[] communalPile;// to store the cards that are compared in the communal pile
    
    private int currentPlayer = -1;// initialise there is no player and players need to choose the number of real player
    
    private int[] winCount;// to collect the number of winner for each player
    
    private boolean isRun = true;// Initialise the status of the game is running
    
    private int roundCount = 1;// Initialise the round number start from 1. e.g. refresh the web and the game from round
                               // 1
    
    // define the number of game winner and the number of draw round
    private int gameWinner;
    
    private int drawCount;
    
    public GameHandler(String fileName, int numAIPlayers)
    {
        this.getAllDecks(fileName);
        this.shuffleCard();
        this.dealCard(numAIPlayers);
        this.setFirstPlayer();
        winCount = new int[numAIPlayers + 1];
        communalPile = new String[allCards.length];
    }
    
    /**
     * this method is used to decide the game player
     * 
     * @param index User insert the number to decide the player information
     * @return if user insert 0 then return the real player, e.g. you, otherwise return AI player
     */
    public String playerName(int index)
    {
        if (index == 0)
            return "You";
        else
            return "AI player" + index;
    }
    
    /**
     * this method is used to collect and store the card information into the card array
     * 
     * @param fileName get the file which has the card information
     */
    public void getAllDecks(String fileName)
    {
        FileReader reader = null;
        List<String> deckList = new ArrayList<String>();
        try
        {
            try
            {
                // read the card information from the file
                reader = new FileReader(fileName);
                Scanner in = new Scanner(reader);
                while (in.hasNextLine())
                {
                    // get the information from the file
                    String line = in.nextLine().trim();
                    if (!"".equals(line))
                        deckList.add(line);
                }
            }
            finally
            {
                // after reading all information, then close the FileReader
                if (reader != null)
                    reader.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        // collect the category detail of the cards splitting the space and store into the category array
        categoryDetail = deckList.get(0).split(" ");
        allCards = new String[deckList.size() - 1];
        for (int i = 1; i < deckList.size(); i++)
        {
            // collect the card information from the deck list and store into the card array
            allCards[i - 1] = deckList.get(i);
        }
    }
    
    /**
     * This method is used to collect and store the shuffled cards
     */
    public void shuffleCard()
    {
        LinkedList<String> list = new LinkedList<String>();
        // Add the card from the allCards array to the list
        for (String card : this.allCards)
        {
            list.add(card);
        }
        Random random = new Random();
        String[] shuffledDecks = new String[allCards.length];
        // This loop is used to get the random index of the card, remove the existed card and add new card into the
        // index
        for (int i = 0; i < shuffledDecks.length; i++)
        {
            int index = random.nextInt(list.size());
            shuffledDecks[i] = list.get(index);
            list.remove(index);
        }
        this.shuffleCard = shuffledDecks;
    }
    
    /**
     * This method is used to deal with the information of AI player with the cards
     * 
     * @param numAIPlayers Insert the number of AI player to build the array
     */
    public void dealCard(int numAIPlayers)
    {
        playersWithCards = new String[numAIPlayers + 1][shuffleCard.length];
        int index = 0;
        // This loop is used to store each AI player with the shuffled cards
        // e.g. the first AI player with the card in the index is 0 of shuffledCard array
        for (int i = 0; i < shuffleCard.length; i++)
        {
            playersWithCards[i % playersWithCards.length][index] = shuffleCard[i];
            if (i % playersWithCards.length == playersWithCards.length - 1)
                index++;
        }
        dealCardToPlayer();
    }
    
    /**
     * This method is used to get the new order of the player
     */
    public void dealCardToPlayer()
    {
        String[][] newOrder = new String[playersWithCards.length][];
        Random random = new Random();
        int index = 0;
        // This loop is to get the new random index and get the new player order with the new index
        while (index < newOrder.length)
        {
            // get the new random index
            int newIndex = random.nextInt(playersWithCards.length);
            if (newOrder[newIndex] == null)// if there is nothing in the new index of the array
            {
                // put the information from playerWithCard into the newOrder
                newOrder[newIndex] = playersWithCards[index];
                index++;
            }
        }
        playersWithCards = newOrder;
    }
    
    /**
     * This method is to get the detail of the first card
     */
    public void getFirstCardDetail()
    {
        // pick the first card and store the card information into the array
        String[] firstCards = this.pickFirstCards();
        firstCardsDetail = new String[firstCards.length][];
        // This loop is to store the card details when the information is not null in the index of the array
        for (int i = 0; i < firstCardsDetail.length; i++)
        {
            if (firstCards[i] != null)
                firstCardsDetail[i] = firstCards[i].split(" ");// get the card details without space
        }
    }
    
    /**
     * This method is used to compare the category of different cards
     * 
     * @param cateIndex This is to get the category of the card b using the index number. e.g. speed, 0
     * @return This is to return the index of the win player to get the win player
     */
    public int compareRound(int cateIndex)
    {
        this.getFirstCardDetail();
        // 1<=cateIndex<=firstCards.length-1
        int[] numValue = new int[firstCardsDetail.length];
        // This loop is to get and store the specific category value for each card into the new array
        for (int i = 0; i < firstCardsDetail.length; i++)
        {
            if (firstCardsDetail[i] != null)
                numValue[i] = Integer.parseInt(firstCardsDetail[i][cateIndex]);
            else
                numValue[i] = -1;
        }
        // initialise the maxValue and the maxIndex first
        int maxValue = -1;
        int maxIndex = -1;
        // Compare the specific value of each card and get the max value, then get the maxIndex of the player
        for (int i = 0; i < numValue.length; i++)
        {
            if (numValue[i] > maxValue)
            {
                maxValue = numValue[i];
                maxIndex = i;
            }
        }
        // If the specific value is same, then return the draw round
        for (int i = 0; i < numValue.length; i++)
        {
            if (numValue[i] == maxValue && i != maxIndex)
                return -1;
        }
        return maxIndex;// Finally return the winner player index
    }
    
    /**
     * This method is to get the arrays of player with cards
     * 
     * @return Return the firstCard array for each player
     */
    public String[] pickFirstCards()
    {
        String[] pickedCards = new String[playersWithCards.length];
        // This loop is to get the card from the playerWithCard array and store the picked card into a new array
        for (int i = 0; i < playersWithCards.length; i++)
        {
            pickedCards[i] = playersWithCards[i][0];
        }
        return pickedCards;
    }
    
    /**
     * This method is to get the index of there is no card in the card array
     * 
     * @param cards Insert the cards array to judge if there is already no card in the specific index
     * @return Return the index where there is no card
     */
    public int minNullIndex(String[] cards)
    {
        int index = -1;
        // This loop is to get the index of there is no card
        for (int i = 0; i < cards.length; i++)
        {
            if (cards[i] == null)
            {
                index = i;
                break;
            }
        }
        return index;
    }
    
    /**
     * This method is used to merge the card for the winner player to get the communal pile card
     * 
     * @param fromCards From the communal pile card array
     * @param toCards To the winner player card array
     */
    public void mergeCards(String[] fromCards, String[] toCards)
    {
        int startIndex = this.minNullIndex(toCards);
        if (startIndex != -1)
        {
            // This loop is to put the card from one card array to another
            for (int i = 0; i < fromCards.length; i++)
            {
                if (fromCards[i] != null)// to judge if it is not null in the specific index of the fromCard array
                {
                    toCards[startIndex] = fromCards[i];
                    startIndex++;
                }
            }
        }
    }
    
    /**
     * This method is used to remove the first card from the firstCard array
     */
    public void removeFirstCard()
    {
        for (int i = 0; i < playersWithCards.length; i++)
        {
            String[] newCards = new String[playersWithCards[i].length];
            for (int j = 1; j < playersWithCards[i].length; j++)
            {
                
                if (playersWithCards[i][j] == null)// if there is no card in specific index, then end the loop
                    break;
                // otherwise change the first card
                newCards[j - 1] = playersWithCards[i][j];
            }
            playersWithCards[i] = newCards;
        }
    }
    
    /**
     * This method is for AI player to select the category by the index number
     * 
     * @return Return the category max value index number
     */
    public int AISelectCateIndex()
    {
        getFirstCardDetail();
        String[] currentCardDetail = firstCardsDetail[currentPlayer];
        // initialise the manIndex and manValue
        int maxIndex = 1;
        int maxValue = 0;
        // This loop is to compare the card detail and get the max value index number
        for (int i = 1; i < currentCardDetail.length; i++)
        {
            int value = Integer.parseInt(currentCardDetail[i]);
            if (value > maxValue)
            {
                maxValue = value;
                maxIndex = i;
            }
        }
        return maxIndex;// return the max value index number here
    }
    
    /**
     * This method is to make the winner user merge the card from the communal pile to his own cards
     * 
     * @param cateIndex Insert the specific category index number to get comparison
     * @return Return the index of winner to get the winner player
     */
    public int winRoundCards(int cateIndex)
    {
        // to get the winner index of winner player
        int winnerIndex = this.compareRound(cateIndex);
        String[] pickedCards = this.pickFirstCards();
        // After one round ,remove the first card and get the new first card
        this.removeFirstCard();
        // To judge if this round is a draw round
        if (winnerIndex == -1)
        {
            this.mergeCards(pickedCards, communalPile);
            drawCount++;// collect the draw round number
        }
        else// if the round is not draw round
        {
            // merge the cards from the communal pile to the winner cards
            this.mergeCards(pickedCards, playersWithCards[winnerIndex]);
            if (communalPile[0] != null)
            {
                this.mergeCards(communalPile, playersWithCards[winnerIndex]);
                communalPile = new String[communalPile.length];
            }
            this.currentPlayer = winnerIndex;
            winCount[winnerIndex]++;
        }
        this.setRunStatus();
        roundCount++;
        return winnerIndex;// return the winner index with the new card number
    }
    
    /**
     * This method is to set the first player who choose the category by using the random number
     */
    public void setFirstPlayer()
    {
        Random random = new Random();
        int index = random.nextInt(playersWithCards.length);
        this.currentPlayer = index;
    }
    
    /**
     * This method is to set the status of the game
     */
    public void setRunStatus()
    {
        for (int i = 0; i < playersWithCards.length; i++)
        {
            // if there is no card in the i-1 index
            if (playersWithCards[i][playersWithCards[i].length - 1] != null)
            {
                this.gameWinner = i;// then the game winner is the index of i
                this.isRun = false;// stop the game
                break;// break the loop
            }
        }
        
    }
    
    /**
     * This method is to get and display the final winner information
     * 
     * @return Return toString to display the final information
     */
    public String finalWinnerInfo()
    {
        // get the winner index
        int gameWinner = getGameWinner();
        StringBuilder sb = new StringBuilder();
        int[] winCount = getWinCount();// store the win count into the array
        // to display the information by using the format
        sb.append(String.format("%s%n", "The winner was " + playerName(gameWinner)
            + ", they won " + winCount[gameWinner] + " rounds"));
        for (int j = 0; j < winCount.length; j++)
        {
            // display the player who lose the game before the game stop
            if (j != gameWinner)
            {
                sb.append(String.format("%s%n",
                    playerName(j) + " lost overall, but won " + winCount[j] + " rounds"));
            }
        }
        return sb.toString();// return the two kinds of display formats
    }
    
    /**
     * This method is to insert the information of game into the database
     */
    public void insertInfo()
    {
        DBHandler db = new DBHandler();
        int totalRound = drawCount;
        int gameNumber = db.queryGameNumber() + 1;
        // build the table for the player to store the information of them
        StringBuilder sqlRound = new StringBuilder(
            " insert into winround(player_name,win_round_count,gamenumber) values ");
        for (int i = 0; i < winCount.length; i++)
        {
            // display the information of player name, win count number and game number
            sqlRound.append(
                "('" + playerName(i) + "'," + winCount[i] + "," + gameNumber + "),");
            totalRound += winCount[i];
        }
        sqlRound.deleteCharAt(sqlRound.length() - 1);
        // insert the information into the database by using SQL sentence
        String sqlGame =
            " insert into game(gamenumber,winner,drawCount,totalround) values ("
                + gameNumber + ",'" + playerName(gameWinner) + "'," + drawCount + ","
                + totalRound + ") ";
        // insert the value into the two tables
        db.insert(sqlGame);
        db.insert(sqlRound.toString());
    }
    
    // These following method is several mutator methods to get the values of this class
    public String[] getCategoryDetail()
    {
        return categoryDetail;
    }
    
    public String[][] getPlayersWithCards()
    {
        return playersWithCards;
    }
    
    public String[] getCommunalPile()
    {
        return communalPile;
    }
    
    public int getCurrentPlayer()
    {
        return currentPlayer;
    }
    
    public boolean isRun()
    {
        return isRun;
    }
    
    public int getRoundCount()
    {
        return roundCount;
    }
    
    public String[] getAllCards()
    {
        return allCards;
    }
    
    public String[] getShuffleCard()
    {
        return shuffleCard;
    }
    
    public String[][] getFirstCardsDetail()
    {
        getFirstCardDetail();
        return firstCardsDetail;
    }
    
    public int[] getWinCount()
    {
        return winCount;
    }
    
    public int getGameWinner()
    {
        return gameWinner;
    }
    
}
