package server.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import common.logic.Order;
import server.gui.controller.ServerGuiController;

public class DBController {

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
	
	public static Order searchOrder(Connection conn, Integer id, ServerGuiController serverController) {
		Statement stmt;
		try {
			stmt=conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * From Orders WHERE order_number = "+id.toString()+";");
			
			if(!rs.next()) {
				return null;
			}
			
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
		
	}
	
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
