

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Servlet implementation class ICServlet
 */
@WebServlet("/ICServlet")
public class ICServlet extends HttpServlet {
	//private static final long serialVersionUID = 1L;
		private PrintWriter out;

		public void init(ServletConfig config) throws ServletException 
		{
			//TODO load sql data
		}


		public void destroy() 
		{
			//TODO close sql connection
		}

		public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
		{
			out = response.getWriter();
			//out.println("<h1>" + "Yep" + "</h1>");
					

			response.setContentType("text/html");
			
			//out.println("Starting");
			if(request.getParameter("request") != null)
			{
			switch(request.getParameter("request").toLowerCase())
			{
				case "getalertsfor":
					if(request.getParameter("stationid") != null)
					{
						String stationID = request.getParameter("stationid");
						getAlertsForStation(stationID);
					}
				break;
				
				default: out.println("Error: Unknown Command"); break;
			}
			}
			else
			{
				out.print("No request received");
			}
		}

		protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
		{
			// TODO Auto-generated method stub
			doGet(request, response);
		}
		
		private void getAlertsForStation(String inStationID)
		{
			// JDBC driver name and database URL
		    final String JDBC_DRIVER="com.mysql.jdbc.Driver";  
		    final String DB_URL="jdbc:mysql://localhost/InstructaconDatabase";
		    
		    //  Database credentials
		    final String USER = "user";
		    final String PASS = "";
		    Statement stmt = null;
		    Connection conn = null;
		    
		    int count = 0;
		    
		    try
		    {
		         // Register JDBC driver
		         Class.forName("com.mysql.jdbc.Driver");

		         // Open a connection
		         conn = DriverManager.getConnection(DB_URL, USER, PASS);

		         // Execute SQL query
		         stmt = conn.createStatement();
		         String sql;
		         sql = "SELECT DISTINCT alert FROM alerts where isActive = true;";
		         ResultSet rs = stmt.executeQuery(sql);
		         
		         ArrayList<String> returnAlerts = new ArrayList<String>();         


		         // Extract data from result set
		         while(rs.next())
		         {
		        	 count++;
		            //Retrieve by column name
		        	 //out.print("checking id " + rs.getInt("id"));
		        	 String currentAlert = rs.getString("alert");
		        	 returnAlerts.add(currentAlert);		        	 
		         }
		         
		         JSONArray jsonOut = new JSONArray();
		         int i = 0;
		         for(String aAlert: returnAlerts)
		         {
		        	 i++;
		        	 JSONObject obj = new JSONObject();
		        	 obj.put("alert", "Alert " + i + ". " + aAlert);
		        	 jsonOut.add(obj);
		         }
		         //out.println("Returning IDS");
		         out.println(jsonOut);
		         
		         
		         sql = "UPDATE alerts set isActive = false where stationID = '" + inStationID + "' AND isActive = true;";
		         stmt.executeUpdate(sql);

		         // Clean-up environment
		         rs.close();
		         stmt.close();
		         conn.close();
		    }
		    catch(SQLException se)
		    {
		         //Handle errors for JDBC
		         se.printStackTrace();
		    }
		    catch(Exception e)
		    {
		         //Handle errors for Class.forName
		         e.printStackTrace();
		    }
		    finally
		    {
		         //finally block used to close resources
		         try
		         {
		            if(stmt!=null){stmt.close();};
		         }
		         catch(SQLException se2)
		         {
		         }// nothing we can do
		         try
		         {
		            if(conn!=null){conn.close();}
		         }
		         catch(SQLException se)
		         {
		            se.printStackTrace();
		         }//end finally try
		     } //end try
		}
		
		
		
		
}
