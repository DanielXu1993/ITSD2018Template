package commandline;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// aim to operate databases which procedures are equipped to estiblish a connection to SQL server, execute the SQL and close existing connections
public class DBHandler
{
    public DBHandler()
    {
        
    }
    
    public Connection getConnection()
        //if a database access error occur
        throws SQLException
    {
        Connection con = null;
        
        // Get database Connection
        con = DriverManager.getConnection(
            "jdbc:postgresql://127.0.0.1:5432/" + "toptrumps", "postgres", "root");
        
        return con;
    }
    
    /**
     * Close the current connection,statement and resultSet
     * 
     * @throws SQLException
     * 
     */
    /** declare close class through connection variable and throw SQLException value; otherwise, it will occur NullReferenceExcepion error (object reference not set to an instance of an object) while the connection is not null in case of calling close() function
     */
    // release this ResultSet object's database and JDBC resources immediately instead of waiting for this to happen when it is automatically closed
    public void close(Connection con,Statement st,ResultSet rs)
        throws SQLException
    {
        if (con != null)
            con.close();
		if (st != null)
            st.close();
		if (rs != null)
            rs.close();
    }
    // declare to insert data into databases
    public void insert(String sql)
    {
        //connections are likly to be null in general
        Connection con = null;
        Statement st = null;
        try
        {
            try
            {
                con = getConnection();
                st = con.createStatement();// create a  statement object
                st.executeUpdate(sql);// execute SQL to update database
            }
            // close afterwards
            finally
            {
                close(con,st,null);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    
    // declare general query method from database
	// item is the aliases in sql
    public int queryStats(String sql, String item)
    {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        int stats = 0;
        try
        {
            try
            {
                con = getConnection();
                // create a statement to implement SQL statements
                st = con.createStatement();
                // execute a query, call an execute method from SQL statements and return a ResultSet object
                rs = st.executeQuery(sql);
                // move cursor forward to next data
                while (rs.next())
                {
                    stats = rs.getInt(item);//get the data from database according to alias
                }
            }
            // immediately release the resources 
            finally
            {
                close(con,st,rs);
            }
            
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return stats;
    }
    // return average draw count from database
    public double queryAvgDraw()
    {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        double avgDraw = 0;
        String sql = " select avg(drawcount) avgDraw from game ";
        try
        {
            try
            {
                con = getConnection();
                st = con.createStatement();
                rs = st.executeQuery(sql);
                while (rs.next())
                {
                    avgDraw = rs.getDouble("avgDraw");
                }
            }
            finally
            {
                close(con,st,rs);
            }
            
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return avgDraw;
    }
    
    public int queryGameNumber()
    {
        String sql = " select max(gamenumber) gamenumber from game ";
        return queryStats(sql, "gamenumber");
    }
    
    public int queryHumanWin()
    {
        String sql = " select count(gamenumber) humanwin from game where winner = 'You' ";
        return queryStats(sql, "humanwin");
    }
    
    public int queryAIWin()
    {
        String sql = " select count(gamenumber) aiwin from game where winner != 'You' ";
        return queryStats(sql, "aiwin");
    }
    
    public int queryLongestGame()
    {
        String sql = " select max(totalround) longestGame from game ";
        return queryStats(sql, "longestGame");
    }
    // summarize and display above information
    public String displayStats()
    {
        // initialize a new instance of the StringBuilder class
        StringBuilder sb = new StringBuilder();
        // append the specified string to this instance and following character sequence
        sb.append(String.format("%s%n", "Number of Games: " + queryGameNumber()));
        sb.append(String.format("%s%n", "Number of Human Wins: " + queryHumanWin()));
        sb.append(String.format("%s%n", "Number of AI wins: " + queryAIWin()));
        sb.append(String.format("%s%.2f%n", "Average Draws per game: ", queryAvgDraw()));
        sb.append(String.format("%s%n", "Longest Game: " + queryLongestGame()));
        // toString() method returns the string representation of the sb object, convert the value of this instance to a String (override object.toString)
        return sb.toString();
    }
}
