package commandline;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class CommandGame
{
    private GameHandler game;
    
    private String[] category;
    
    private int currentCateIndex;
    
    private StringBuilder finalLog = new StringBuilder();
    
    public CommandGame()
    {
        game = new GameHandler("StarCitizenDeck.txt", 4);
        category = game.getCategoryDetail();
        this.initFinalLog();
    }
    
    public String cardWithCate(String card)
    {
        String[] cardInfo = card.split(" ");
        StringBuilder cardWithCate = new StringBuilder();
        for (int i = 0; i < cardInfo.length; i++)
        {
            if (i == 0)
                cardWithCate.append(cardInfo[0]);
            else
                cardWithCate.append("," + category[i] + ":" + cardInfo[i]);
        }
        return cardWithCate.toString();
    }
    
    public void roundInfo()
    {
        int roundNum = game.getRoundCount();
        int playerIndex = game.getCurrentPlayer();
        String activePlayer = game.playerName(playerIndex);
        finalLog.append(String.format("%s%n",
            "Round Number: " + roundNum + ", active player: " + activePlayer));
        
        String cardDrawn = cardWithCate(game.getPlayersWithCards()[playerIndex][0]);
        
        System.out
            .println("Round Number: " + roundNum + ", active player: " + activePlayer);
        System.out.println("current card: " + cardDrawn);
    }
    
    public int inputCategory()
    {
        Scanner input = new Scanner(System.in);
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < category.length; i++)
        {
            sb.append("," + category[i]);
        }
        sb.deleteCharAt(0);
        System.out.print("input a category: " + sb);
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
        if (index == -1)
        {
            System.out.println("Your input is invalid,please retry");
            return inputCategory();
        }
        else
            return index;
    }
    
    public void selectedCategory()
    {
        String cat = null;
        int playerIndex = game.getCurrentPlayer();
        if (playerIndex == 0)
            this.currentCateIndex = inputCategory();
        else
            this.currentCateIndex = game.AISelectCateIndex();
        cat = category[currentCateIndex];
        String[][] firstCardsDetail = game.getFirstCardsDetail();
        finalLog.append(String.format("%s%n", " " + cat + " has been selected,value is : "
            + firstCardsDetail[playerIndex][currentCateIndex]));
        System.out.println(cat + " has been selected");
    }
    
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
    
    public void processGame()
    {
        roundInfo();
        selectedCategory();
        String[] firstCards = game.pickFirstCards();
        finalLog.append(String.format("%s%n", "current cards in play: "));
        for (int i = 0; i < firstCards.length; i++)
        {
            if (firstCards[i] != null)
                finalLog.append(
                    String.format("%s%n", game.playerName(i) + ":" + firstCards[i]));
        }
        String[] communalPile = game.getCommunalPile();
        int winnerIndex = game.winRoundCards(currentCateIndex);
        if (winnerIndex != -1)
        {
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
            System.out.println("it was a draw");
            System.out.println("communal pile cards");
            System.out.println(winnedCards(communalPile));
            finalLog.append(String.format("%s%n", "communal pile cards:"));
            finalLog.append(String.format("%s", getCards(communalPile)));
        }
        finalLog.append(playerCardsAfterRound());
        isEliminated();
    }
    
    public boolean isRun()
    {
        return game.isRun();
    }
    
    public void isEliminated()
    {
        String[][] playersWithCards = game.getPlayersWithCards();
        for (int i = 0; i < playersWithCards.length; i++)
        {
            for (int j = 0; j < playersWithCards[i].length; j++)
            {
                if (playersWithCards[i][0] == null)
                {
                    System.out.println(game.playerName(i) + ": no cards left");
                    break;
                }
            }
        }
    }
    
    public void finalWinner()
    {
        String winnerInfo = game.finalWinnerInfo();
        finalLog.append(winnerInfo);
        System.out.println(winnerInfo);
        game.insertInfo();
    }
    
    public String getCards(String[] cards)
    {
        StringBuilder sb = new StringBuilder();
        for (String card : cards)
        {
            if (card == null)
                break;
            sb.append(String.format("%s%n", card));
        }
        return sb.toString();
    }
    
    public String playerCardsAfterRound()
    {
        StringBuilder sb = new StringBuilder();
        String[][] playersWithCards = game.getPlayersWithCards();
        for (int i = 0; i < playersWithCards.length; i++)
        {
            if (playersWithCards[i][0] != null)
            {
                sb.append(String.format("%s's cards :%n", game.playerName(i)));
                sb.append(getCards(playersWithCards[i]));
            }
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
    
    public void initFinalLog()
    {
        finalLog.append(String.format("%s%n", "the complete deck :"));
        finalLog.append(getCards(game.getAllCards()));
        finalLog.append(
            String.format("%s%n", "-----------------------------------------------"));
        finalLog.append(String.format("%s%n", "deck after it has been shuffled :"));
        finalLog.append(getCards(game.getShuffleCard()));
        finalLog.append(
            String.format("%s%n", "-----------------------------------------------"));
        
        finalLog.append(playerCardsAfterRound());
    }
    
    public void writeLog()
    {
        FileWriter fw = null;
        try
        {
            try
            {
                fw = new FileWriter("toptrumps.log");
                fw.write(finalLog.toString());
            }
            finally
            {
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
