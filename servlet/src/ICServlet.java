

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
						if(request.getParameter("stationid") != null && request.getParameter("tagid") != null)
						{
							String stationID = request.getParameter("stationid").replaceAll("_", " ");
							String tagID = request.getParameter("tagid"); //.replaceAll("", " ");
							getAlertsForStation(stationID, tagID);
						}
					break;
				
					case "getsecurityalertsfor":
						if(request.getParameter("stationid") != null && request.getParameter("tagid") != null)
						{
							String stationID = request.getParameter("stationid").replaceAll("_", " ");
							String tagID = request.getParameter("tagid"); //.replaceAll("", " ");
							getSecurityAlertsForStation(stationID, tagID);
						}
					break;
					
					case "addalert":
						if(request.getParameter("stationid") != null && request.getParameter("alerttext") != null && request.getParameter("alerttype") != null)
						{
							String stationID = request.getParameter("stationid").replaceAll("_", " ");
							String alertText = request.getParameter("alerttext").replaceAll("_", " ");
							String alertType = request.getParameter("alerttype");
							addAlertForStationOfType(stationID, alertType, alertText);
						}
						break;
						
					case "getlatestsignins":
						getLatestSignins();
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
		
		private void getAlertsForStation(String inStationID, String inTagID)
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
		         
		         DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		         java.util.Date currentTimeStamp = new java.util.Date();
		         
		         sql = "insert into signins (stationID, tagID, timestamp) VALUES ('" + inStationID + "', '" + inTagID + "', '" + dateFormat.format(currentTimeStamp) + "');";
		         stmt.executeUpdate(sql);
		         		         
		         
		         
		         
		         
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
		         
		         //Add the name assocaited with the tag to the start of the json set.
		         JSONObject nameObj = new JSONObject();
		         nameObj.put("name", getNameForTag(inTagID, conn, stmt));
		         jsonOut.add(nameObj);
		         
		         
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
		
		private void getSecurityAlertsForStation(String inStationID, String inTagID)
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
		         
		         DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		         java.util.Date currentTimeStamp = new java.util.Date();
		         
		         sql = "insert into signins(stationID, tagID, timestamp) VALUES ('" + inStationID + "', '" + inTagID + "', '" + dateFormat.format(currentTimeStamp) + "');";
		         stmt.executeUpdate(sql);
		         
		         
		         sql = "SELECT DISTINCT alert FROM securityAlerts where isActive = true;";
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
		         
		       //Add the name assocaited with the tag to the start of the json set.
		         JSONObject nameObj = new JSONObject();
		         nameObj.put("name", getNameForTag(inTagID, conn, stmt));
		         jsonOut.add(nameObj);
		         
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
		         
		         
		         sql = "UPDATE securityAlerts set isActive = false where stationID = '" + inStationID + "' AND isActive = true;";
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
		
		private void addAlertForStationOfType(String inStationID, String inAlertType, String inAlertText)
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
		         String sql = "";
		       
		         
		         switch(inAlertType)
		         {
		        	 case "security": 
		        		 sql = "insert into securityAlerts(stationID, alert, isActive) VALUES ('" + inStationID + "', '" + inAlertText + "', true)"; 
		        		 break;
		        		 
		        	 case "janitor": 
		        		 sql = "insert into alerts(stationID, alert, isActive) VALUES ('" + inStationID + "', '" + inAlertText + "', true)"; 
		        		 break;
		        		 
		        	 default: out.println("Error: Unknown Alert Type");
		         }
		         stmt.executeUpdate(sql);

		         // Clean-up environment
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
		
		
		private void getLatestSignins()
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
		         
		         DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		         java.util.Date currentTimeStamp = new java.util.Date();
		         
		         
		     
		         sql = "select t3.name, t1.stationID, t1.timestamp from signins t1 join (tags t3) on (t1.tagID = t3.tagID) where t1.timestamp = (select max(t2.timestamp) from signins t2 where t1.tagID = t2.tagID);";
		         ResultSet rs = stmt.executeQuery(sql);
		         
		         ArrayList<String> returnStations = new ArrayList<String>(); 
		         ArrayList<String> returnNames = new ArrayList<String>(); 
		         ArrayList<String> returnTimestamps = new ArrayList<String>(); 


		         // Extract data from result set
		         while(rs.next())
		         {
		        	 count++;
		            //Retrieve by column name
		        	 //out.print("checking id " + rs.getInt("id"));
		        	 String currentStation = rs.getString("t1.stationID");
		        	 String currentName = rs.getString("t3.name");
		        	 String currentTimestamp = rs.getDate("t1.timestamp").toString();
		        	 returnStations.add(currentStation);	
		        	 returnNames.add(currentName);
		        	 returnTimestamps.add(currentTimestamp);
		         }
		         
		         JSONArray jsonOut = new JSONArray();
		         int i = 0;
		         for(String aStationID: returnStations)
		         {
		        	 
		        	 JSONObject obj = new JSONObject();
		        	 obj.put("stationID", aStationID);
		        	 obj.put("name", returnNames.get(i));
		        	 obj.put("timestamp", returnTimestamps.get(i));
		        	 jsonOut.add(obj);
		        	 i++;
		         }
		         //out.println("Returning IDS");
		         out.println(jsonOut);
		         

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
		
		private String getNameForTag(String tagID, Connection conn, Statement stmt)
		{
			// JDBC driver name and database URL
		    final String JDBC_DRIVER="com.mysql.jdbc.Driver";  
		    final String DB_URL="jdbc:mysql://localhost/InstructaconDatabase";
		    
		    //  Database credentials
		    final String USER = "user";
		    final String PASS = "";
		    
		    int count = 0;
		    
		    try
		    {


		         // Execute SQL query
		         stmt = conn.createStatement();
		         String sql;
		         
		         
		     
		         sql = "select name from tags where tagID = '" + tagID + "';";
		         ResultSet rs = stmt.executeQuery(sql);
		         
		         // Extract data from result set
		         while(rs.next())
		         {
		        	 String result = rs.getString("name");
		        	 rs.close();
		        	 return result;
		         }
		         
		         return "No Name Found";
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
		    } //end try
		    return "This should never be returned";
		}
}
