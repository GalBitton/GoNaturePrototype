package server.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import common.logic.Order;
import server.gui.controller.ServerGuiController;

/**
 * The DB Controller class is the way our application "talk" with the database.
 * This class use "mysql-connector-java-8.0.13".
 * @Author GalBitton
 * @version 1.0.0
 */
public class DBController {
	
	/**
	 * This method is trying to connect to mySQL database, using jdbc driver.
	 * This method is being called from server, and return it's connection.
	 * @param db - class which contains the required information for database (hostname,username,password)
	 * @return if connected successfully -> return the new Connection.
	 * 		   else -> return null.
	 */
	public static Connection connectToMySqlDB(DBConnectionDetails db) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			System.out.println("Driver definition succeed");
		}catch (Exception ex) {
			System.out.println("Driver definition failed");
		}
		
		try {
			Connection conn = DriverManager.getConnection(db.getName(),db.getUsername(),db.getPassword());
			return conn;
		}catch(SQLException ex) {
			System.out.println("SQLException: "+ex.getMessage());
			System.out.println("SQLState: "+ex.getSQLState());
			System.out.println("VendorError: "+ex.getErrorCode());
			return null;
		}
	}

	/**
	 * This method is executing query to database in order to get the requested Order. 
	 * @param conn - The Connection instance with the database.
	 * @param id - The required order ID.
	 * @param serverController - The server controller -> in order to print to log screen in server gui.
	 * @return if found order in database -> return the requested Order instance.
	 *         else -> return null.
	 */
	public static Order searchOrder(Connection conn, Integer id, ServerGuiController serverController) {
		Statement stmt;
		try {
			// create the requested query
			stmt=conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * From Orders WHERE order_number = "+id.toString()+";");
			
			// if the query run successfully, but returned as empty table.
			if(!rs.next()) {
				return null;
			}
			
			// create new Order instance and initialize it with relevant data.
			Order retOrder = new Order();
			retOrder.setParkName(rs.getString(1));
			retOrder.setOrderNumber(Integer.parseInt(rs.getString(2)));
			retOrder.setTimeOfVisit(rs.getString(3));
			retOrder.setNumberOfVisitors(Integer.parseInt(rs.getString(4)));
			retOrder.setTelephoneNumber(rs.getString(5));
			retOrder.setEmail(rs.getString(6));
			
			return retOrder;
			
		}catch(SQLException ex) {
			serverController.printToLogConsole("Query for search for order failed");
			return null;
		}
		// any other exception occurred
		catch(Exception e) {
			serverController.printToLogConsole(e.getMessage());
			return null;
		}
		
	}
	
	/**
	 * This method is trying to update the old order in Database with new order details.
	 * @param conn - The Connection instance with the database.
	 * @param orderNumber - The old order ID.
	 * @param parkName - The new park name. (can be either old).
 	 * @param phoneNumber - The new phone number. (can be either old).
	 * @param serverController - The server controller -> in order to print to log screen in server gui.
	 * @return if updated successfully -> return true.
	 *         else -> return false.
	 */
	public static boolean updateOrder(Connection conn,Integer orderNumber, String parkName, String phoneNumber, ServerGuiController serverController) {
		Statement stmt;
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(String.format("UPDATE Orders SET park_name = '%s', phone_number = '%s' WHERE order_number = %d;",
					parkName,phoneNumber,orderNumber));
			return true;
		}catch(SQLException ex) {
			serverController.printToLogConsole("Query for update order failed");
			ex.printStackTrace();
			return false;
		}
	}
	
	
}
