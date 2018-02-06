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
    private String[] categoryDetail;
    
    private String[] allCards;
    
    private String[] shuffleCard;
    
    private String[][] playersWithCards;
    
    private String[][] firstCardsDetail;
    
    private String[] communalPile;
    
    private int currentPlayer = -1;
    
    private int[] winCount;
    
    private boolean isRun = true;
    
    private int roundCount = 1;
    
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
    
    public String playerName(int index)
    {
        if (index == 0)
            return "You";
        else
            return "AI player" + index;
    }
    
    public void getAllDecks(String fileName)
    {
        FileReader reader = null;
        List<String> deckList = new ArrayList<String>();
        try
        {
            try
            {
                reader = new FileReader(fileName);
                Scanner in = new Scanner(reader);
                while (in.hasNextLine())
                {
                    String line = in.nextLine().trim();
                    if (!"".equals(line))
                        deckList.add(line);
                }
            }
            finally
            {
                if (reader != null)
                    reader.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        categoryDetail = deckList.get(0).split(" ");
        allCards = new String[deckList.size() - 1];
        for (int i = 1; i < deckList.size(); i++)
        {
            allCards[i - 1] = deckList.get(i);
        }
    }
    
    public void shuffleCard()
    {
        LinkedList<String> list = new LinkedList<String>();
        for (String card : this.allCards)
        {
            list.add(card);
        }
        Random random = new Random();
        String[] shuffledDecks = new String[allCards.length];
        for (int i = 0; i < shuffledDecks.length; i++)
        {
            int index = random.nextInt(list.size());
            shuffledDecks[i] = list.get(index);
            list.remove(index);
        }
        this.shuffleCard = shuffledDecks;
    }
    
    public void dealCard(int numAIPlayers)
    {
        playersWithCards = new String[numAIPlayers + 1][shuffleCard.length];
        int index = 0;
        for (int i = 0; i < shuffleCard.length; i++)
        {
            playersWithCards[i % playersWithCards.length][index] = shuffleCard[i];
            if (i % playersWithCards.length == playersWithCards.length - 1)
                index++;
        }
        dealCardToPlayer();
    }
    
    public void dealCardToPlayer()
    {
        String[][] newOrder = new String[playersWithCards.length][];
        Random random = new Random();
        int index = 0;
        while (index < newOrder.length)
        {
            int newIndex = random.nextInt(playersWithCards.length);
            if (newOrder[newIndex] == null)
            {
                newOrder[newIndex] = playersWithCards[index];
                index++;
            }
        }
        playersWithCards = newOrder;
    }
    
    public void getFirstCardDetail()
    {
        String[] firstCards = this.pickFirstCards();
        firstCardsDetail = new String[firstCards.length][];
        
        for (int i = 0; i < firstCardsDetail.length; i++)
        {
            if (firstCards[i] != null)
                firstCardsDetail[i] = firstCards[i].split(" ");
        }
    }
    
    public int compareRound(int cateIndex)
    {
        this.getFirstCardDetail();
        // 1<=cateIndex<=firstCards.length-1
        int[] numValue = new int[firstCardsDetail.length];
        for (int i = 0; i < firstCardsDetail.length; i++)
        {
            if (firstCardsDetail[i] != null)
                numValue[i] = Integer.parseInt(firstCardsDetail[i][cateIndex]);
            else
                numValue[i] = -1;
        }
        int maxValue = -1;
        int maxIndex = -1;
        for (int i = 0; i < numValue.length; i++)
        {
            if (numValue[i] > maxValue)
            {
                maxValue = numValue[i];
                maxIndex = i;
            }
        }
        for (int i = 0; i < numValue.length; i++)
        {
            if (numValue[i] == maxValue && i != maxIndex)
                return -1;// 平局
        }
        return maxIndex;// 赢家下标
    }
    
    public String[] pickFirstCards()
    {
        String[] pickedCards = new String[playersWithCards.length];
        for (int i = 0; i < playersWithCards.length; i++)
        {
            pickedCards[i] = playersWithCards[i][0];
        }
        return pickedCards;
    }
    
    public int minNullIndex(String[] cards)
    {
        int index = -1;
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
    
    public void mergeCards(String[] fromCards, String[] toCards)
    {
        int startIndex = this.minNullIndex(toCards);
        if (startIndex != -1)
        {
            for (int i = 0; i < fromCards.length; i++)
            {
                if (fromCards[i] != null)
                {
                    toCards[startIndex] = fromCards[i];
                    startIndex++;
                }
            }
        }
    }
    
    public void removeFirstCard()
    {
        for (int i = 0; i < playersWithCards.length; i++)
        {
            String[] newCards = new String[playersWithCards[i].length];
            for (int j = 1; j < playersWithCards[i].length; j++)
            {
                if (playersWithCards[i][j] == null)
                    break;
                newCards[j - 1] = playersWithCards[i][j];
            }
            playersWithCards[i] = newCards;
        }
    }
    
    public int AISelectCateIndex()
    {
        getFirstCardDetail();
        String[] currentCardDetail = firstCardsDetail[currentPlayer];
        int maxIndex = 1;
        int maxValue = 0;
        for (int i = 1; i < currentCardDetail.length; i++)
        {
            int value = Integer.parseInt(currentCardDetail[i]);
            if (value > maxValue)
            {
                maxValue = value;
                maxIndex = i;
            }
        }
        return maxIndex;
    }
    
    public int winRoundCards(int cateIndex)
    {
        int winnerIndex = this.compareRound(cateIndex);
        String[] pickedCards = this.pickFirstCards();
        this.removeFirstCard();
        if (winnerIndex == -1)
        {
            this.mergeCards(pickedCards, communalPile);
            drawCount++;
        }
        else
        {
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
        return winnerIndex;
    }
    
    public void setFirstPlayer()
    {
        Random random = new Random();
        int index = random.nextInt(playersWithCards.length);
        this.currentPlayer = index;
    }
    
    public void setRunStatus()
    {
        for (int i = 0; i < playersWithCards.length; i++)
        {
            if (playersWithCards[i][playersWithCards[i].length - 1] != null)
            {
                this.gameWinner = i;
                this.isRun = false;
                break;
            }
        }
        
    }
    
    public String finalWinnerInfo()
    {
        int gameWinner = getGameWinner();
        StringBuilder sb = new StringBuilder();
        int[] winCount = getWinCount();
        sb.append(String.format("%s%n", "The winner was " + playerName(gameWinner)
            + ", they won " + winCount[gameWinner] + " rounds"));
        for (int j = 0; j < winCount.length; j++)
        {
            if (j != gameWinner)
            {
                sb.append(String.format("%s%n",
                    playerName(j) + " lost overall, but won " + winCount[j] + " rounds"));
            }
        }
        return sb.toString();
    }
    
    public void insertInfo()
    {
        DBHandler db = new DBHandler();
        int totalRound = drawCount;
        int gameNumber = db.queryGameNumber() + 1;
        StringBuilder sqlRound = new StringBuilder(
            " insert into winround(player_name,win_round_count,gamenumber) values ");
        for (int i = 0; i < winCount.length; i++)
        {
            sqlRound.append(
                "('" + playerName(i) + "'," + winCount[i] + "," + gameNumber + "),");
            totalRound += winCount[i];
        }
        sqlRound.deleteCharAt(sqlRound.length() - 1);
        String sqlGame =
            " insert into game(gamenumber,winner,drawCount,totalround) values ("
                + gameNumber + ",'" + playerName(gameWinner) + "'," + drawCount + ","
                + totalRound + ") ";
        db.insert(sqlGame);
        db.insert(sqlRound.toString());
    }
    
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
