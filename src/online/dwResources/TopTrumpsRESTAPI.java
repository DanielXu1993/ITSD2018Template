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
        //game = new GameHandler(conf.getDeckFile(), conf.getNumAIPlayers());
    }
    
    @GET
    @Path("/firstCards")
    public String firstCards()
        throws IOException
    {
        List<String> listOfWords = new ArrayList<String>();
        for (String card : game.pickFirstCards())
        {
            listOfWords.add(card);
        }
        String listAsJSONString = oWriter.writeValueAsString(listOfWords);
        
        return listAsJSONString;
    }
    
    @GET
    @Path("/cateInfo")
    public String category()
        throws IOException
    {
        List<String> listOfWords = new ArrayList<String>();
        for (String cateInfo : game.getCategoryDetail())
        {
            listOfWords.add(cateInfo);
        }
        String listAsJSONString = oWriter.writeValueAsString(listOfWords);
        return listAsJSONString;
    }
    
    @GET
    @Path("/cardCount")
    public String cardCount()
        throws IOException
    {
        List<String> listOfWords = new ArrayList<String>();
        for (String[] cards : game.getPlayersWithCards())
        {
            int size = game.minNullIndex(cards);
            if (size == -1)
                size = cards.length;
            listOfWords.add(size + "");
        }
        
        String listAsJSONString = oWriter.writeValueAsString(listOfWords);
        return listAsJSONString;
    }
    
    @GET
    @Path("/getCurrentPlayer")
    public String getCurrentPlayer()
        throws IOException
    {
        String listAsJSONString = oWriter.writeValueAsString(game.getCurrentPlayer());
        return listAsJSONString;
    }
    
    @GET
    @Path("/getCurrentRound")
    public String getCurrentRound()
        throws IOException
    {
        String listAsJSONString = oWriter.writeValueAsString(game.getRoundCount() + "");
        return listAsJSONString;
    }
    
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
    
    @GET
    @Path("/getRunStatus")
    public String getRunStatus()
        throws IOException
    {
        return game.isRun() + "";
    }
    
    @GET
    @Path("/communalPileLength")
    public String communalPileLength()
        throws IOException
    {
        int length = 0;
        String[] communalPile = game.getCommunalPile();
        for (int i = 0; i < communalPile.length; i++)
        {
            if (communalPile[i] == null)
            {
                length = i;
                break;
            }
        }
        String listAsJSONString = oWriter.writeValueAsString(length + "");
        return listAsJSONString;
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
