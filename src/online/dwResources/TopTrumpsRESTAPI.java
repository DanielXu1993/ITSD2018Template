package online.dwResources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import commandline.DBHandler;
import commandline.GameHandler;
import online.configuration.TopTrumpsJSONConfiguration;

@Path("/toptrumps") // Resources specified here should be hosted at http://localhost:7777/toptrumps
@Produces(MediaType.APPLICATION_JSON) // This resource returns JSON content
@Consumes(MediaType.APPLICATION_JSON) // This resource can take JSON content as input
/**
 * This is a Dropwizard Resource that specifies what to provide when a user
 * requests a particular URL. In this case, the URLs are associated to the
 * different REST API methods that you will need to expose the game commands
 * to the Web page.
 * 
 * Below are provided some sample methods that illustrate how to create
 * REST API methods in Dropwizard. You will need to replace these with
 * methods that allow a TopTrumps game to be controled from a Web page.
 */
public class TopTrumpsRESTAPI
{
    
    /**
     * A Jackson Object writer. It allows us to turn Java objects
     * into JSON strings easily.
     */
    ObjectWriter oWriter = new ObjectMapper().writerWithDefaultPrettyPrinter();
    
    private TopTrumpsJSONConfiguration conf;
    
    private GameHandler game;
    
    /**
     * Contructor method for the REST API. This is called first. It provides
     * a TopTrumpsJSONConfiguration from which you can get the location of
     * the deck file and the number of AI players.
     * 
     * @param conf
     */
    public TopTrumpsRESTAPI(TopTrumpsJSONConfiguration conf)
    {
        this.conf = conf;
    }
    
    /*
     * read the information about the first card
     */
    @GET
    @Path("/firstCards")
    public String firstCards()
        throws IOException
    {
    		//define a list to collect the information about the cards
        List<String> listOfWords = new ArrayList<String>();
        for (String card : game.pickFirstCards())
        {
        		//get the information about the card and add these cards into the list
            listOfWords.add(card);
        }
        //write the values of these cards as String
        String listAsJSONString = oWriter.writeValueAsString(listOfWords);
        
        return listAsJSONString;//return the list of values
    }
    
    /*
     * read the information about the category
     */
    @GET
    @Path("/cateInfo")
    public String category()
        throws IOException
    {
        List<String> listOfWords = new ArrayList<String>();
        for (String cateInfo : game.getCategoryDetail())
        {	
        		//get the information of different categories and add it into the list
            listOfWords.add(cateInfo);
        }
        String listAsJSONString = oWriter.writeValueAsString(listOfWords);
        return listAsJSONString;//return the list of category information
    }
    
    
    /*
     * read the parameter of the card count for each player
     */
    @GET
    @Path("/cardCount")
    public String cardCount()
        throws IOException
    {	
    	
        List<String> listOfWords = new ArrayList<String>();
        for (String[] cards : game.getPlayersWithCards())
        {
            int size = game.minNullIndex(cards);//get the card count for each player
            if (size == -1)
                size = cards.length;
            listOfWords.add(size + "");//add the card count into the list
        }
        
        String listAsJSONString = oWriter.writeValueAsString(listOfWords);
        return listAsJSONString;//get the list of card count for each player
    }
    
    /*
     * read the parameter of the player who still in game
     */
    @GET
    @Path("/getCurrentPlayer")
    public String getCurrentPlayer()
        throws IOException
    {
        String listAsJSONString = oWriter.writeValueAsString(game.getCurrentPlayer());
        return listAsJSONString;
    }
    
    
    /*
     * read the parameter of the current round number of the game
     */
    @GET
    @Path("/getCurrentRound")
    public String getCurrentRound()
        throws IOException
    {
        String listAsJSONString = oWriter.writeValueAsString(game.getRoundCount() + "");
        return listAsJSONString;
    }
    
    /*
     * read the information about the AI option and get the result
     */
    @GET
    @Path("/selectionAndResult")
    public String selectionAndResult(@QueryParam("index") int index)
        throws IOException
    {
        if (index == -1)
        {
            index = game.AISelectCateIndex();
        }
        int winnerIndex = game.winRoundCards(index);
        List<String> listOfWords = new ArrayList<String>();
        listOfWords.add(index + "");
        listOfWords.add(winnerIndex + "");
        String listAsJSONString = oWriter.writeValueAsString(listOfWords);
        
        return listAsJSONString;
    }
    
    /*
     * read the information about the status of the game
     */
    @GET
    @Path("/getRunStatus")
    public String getRunStatus()
        throws IOException
    {
        return game.isRun() + "";//return the status of game running
    }
    
    /*
     * read the information about how many cards in the communal pile
     */
    @GET
    @Path("/communalPileLength")
    public String communalPileLength()
        throws IOException
    {
        int length = 0;
        String[] communalPile = game.getCommunalPile();//define the array to collect the card in the communal pile
        for (int i = 0; i < communalPile.length; i++)
        {
        		//while there is no card in i of the array, it means that there are i cards in the communal pile 
            if (communalPile[i] == null)
            {
                length = i;//get the number of cards
                break;
            }
        }
        String listAsJSONString = oWriter.writeValueAsString(length + "");//write into the list 
        return listAsJSONString;//return the number of cards
    }
    
    @GET
    @Path("/finalWinner")
    public String finalWinner()
        throws IOException
    {
        String winnerInfo = game.finalWinnerInfo();
        String listAsJSONString = oWriter.writeValueAsString(winnerInfo);
        return listAsJSONString;
    }
    
    @GET
    @Path("/displayStats")
    public String displayStats()
        throws IOException
    {
        DBHandler db = new DBHandler();
        String listAsJSONString =
            oWriter.writeValueAsString(db.displayStats().toString());
        return listAsJSONString;
    }
    
    @GET
    @Path("/storeResult")
    public String storeResult()
        throws IOException
    {
        game.insertInfo();
        return "";
    }
    
    @GET
    @Path("/newGame")
    public String newGame()
        throws IOException
    {
        game = new GameHandler(conf.getDeckFile(), conf.getNumAIPlayers());
        return "";
    }
}
