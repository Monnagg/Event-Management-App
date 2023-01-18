import java.util.Random;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class A02MiddleTier {
	//This class will contain your code for interacting with Database, acquire the query result and display it in the GUI text area.
	private static String username = "root";
	private static String password = "123456";//root1234
	private static String driverName = "com.mysql.cj.jdbc.Driver";
	private static String connectionString = "jdbc:mysql://localhost:3306/a02schema?useSSL=false&serverTimezone=UTC";
	private static Connection connection;
	public static int id = 0;

	public static String sqlConference = "insert into EventConference(EventID,City,Country,EvDate) values(?,NULL,NULL,?)";
	public static String sqlJournal = "insert into EventJournal(EventID,JournalName,Publisher) values(?,?,NULL)";
	public static String sqlEvent = "insert into Event(ID,Name,EventWebLink,CFPText) values(?,?,NULL,NULL)";
	public static String sqlBook = "insert into EventBook(EventID,Publisher) values(?,NULL)";

	private static ResultSet rs = null;
	private static PreparedStatement ps = null;
	public static String outPut="";
	
	public A02MiddleTier(){
		try{
			Class.forName(driverName);
			connection = DriverManager.getConnection(connectionString, username, password);
			connection.close();

		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	static String EventName;
	String insertMethod(int sl, String EvName, String EvDate, String JoName) throws SQLException {
		try {
			outPut = "";
			Class.forName(driverName);
			connection = DriverManager.getConnection(connectionString, username, password);
			if(sl == 1){
				id++;
			
				if(EvDate.equals("")) {
					outPut +="Event date cannot be empty";
				}else {
					insertEvent(EvName);
					insertEventConference(EvDate);
				}				

			}else if(sl == 2){
				id++;
				if(JoName.equals("")) {
					outPut +="Journal name cannot be empty";
				}else {
					insertEvent(EvName);
					insertEventJournal(JoName);
				}
				
				

			}else if(sl == 3){
				EventName = "EvBook";
				if(EvName.equals("" )) {
					outPut += "Event name cannot be empty";
				}else {
					insertEventBook(EvName, EvDate, JoName);
				}
				

			}
		
			//System.out.println(outPut);
			return outPut;
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			connection.close();

			
		}
		return "";
	}
	
	
	static void insertEvent(String EvName) {
		try {
			ps = connection.prepareStatement(sqlEvent);
			ps.setObject(1, id);//id
			ps.setObject(2, EvName);
			ps.executeUpdate();
			//display all the results
			ps = connection.prepareStatement("select * from Event");
			//ps.setObject(1, "Event");
			rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			outPut = "Event:\n";
			for (int i = 1; i <= columnCount; i++) {
				outPut +=  rsmd.getColumnName(i) + "\t";
			}
			while (rs.next()) {
				outPut += "\n" + rs.getInt(1) + "\t" + rs.getString(2) + "\t" + rs.getString(3) + "\t\t" + rs.getString(4)
						+ "\t";
			}
		} catch (Exception e) {
			e.printStackTrace();
		
		} finally { 
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}	

		}
	}
	
	static void insertEventJournal(  String JoName) {
		try {
			ps = connection.prepareStatement(sqlJournal);
			ps.setObject(1, id);
			ps.setObject(2, JoName);
			ps.executeUpdate();
			//display all the results
			ps = connection.prepareStatement("select * from EventJournal");
			rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			outPut+= "\nEventJournal:\n";
			for (int i = 1; i <= columnCount; i++) {
				outPut +=rsmd.getColumnName(i) +"\t" ;
			}
			while(rs.next()) {
				outPut += "\n" +rs.getInt(1)+"\t" +rs.getString(2)+"\t\t" + rs.getString(3)+"\t";
			}
		}catch (Exception e) {
			e.printStackTrace();
		
		} finally { 
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}	

		}
	}
	
	static  void insertEventConference(String EvDate) throws ParseException {
		try {
			ps = connection.prepareStatement(sqlConference);
			//convert string to date
			java.text.SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date d =  formatter.parse(EvDate);
			java.sql.Date date = new java.sql.Date(d.getTime());
			ps.setObject(1, id);
			ps.setObject(2, date);
			ps.executeUpdate();
			//display
			ps = connection.prepareStatement("select * from EventConference");
			rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			outPut+= "\nEventConference:\n";
			for (int i = 1; i <= columnCount; i++) {
				outPut += rsmd.getColumnName(i) +"\t" ;
			}
			while(rs.next()) {
				outPut += "\n" +rs.getInt(1)+"\t" +rs.getString(2)+"\t" + rs.getString(3)+"\t"+rs.getString(4)+"\t";
			}
		}catch (Exception e) {
			e.printStackTrace();
		
		} finally { 
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}	

		}
	}
	
	static  void insertEventBook(String EvName, String EvDate, String JoName) {
		try {
			int EventId=0;
		
			if(!EvName.equals("") ) {
				ps = connection.prepareStatement("select ID from Event where Name=?");
				ps.setObject(1, EvName);
				rs = ps.executeQuery();
				if(rs.next()) {
					EventId= rs.getInt(1);
				}
			}

			ps = connection.prepareStatement("select * from EventBook where EventId=?");
			ps.setObject(1, EventId);
			rs = ps.executeQuery();
			if(rs.next()) {
				outPut += "The event has already been booked";
			}
			else {
				ps = connection.prepareStatement(sqlBook);
				ps.setObject(1, EventId);
				ps.executeUpdate();
				//display
				ps = connection.prepareStatement("select * from EventBook");
				rs = ps.executeQuery();
				ResultSetMetaData rsmd = rs.getMetaData();
				int columnCount = rsmd.getColumnCount();
				outPut+= "\nEventBook:\n";
				for (int i = 1; i <= columnCount; i++) {
					outPut += rsmd.getColumnName(i) +"\t" ;
				}
				while(rs.next()) {
					outPut += "\n" +rs.getInt(1)+"\t" +rs.getString(2)+"\t" ;
				}
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		
		} finally { 
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}	

		}
	}
	
	
}
