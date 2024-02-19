package server.logic;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.sql.Connection;
import java.util.Enumeration;

import server.ocsf.AbstractServer;
import server.ocsf.ConnectionToClient;
import server.gui.controller.ServerGuiController;
import server.jdbc.DBConnectionDetails;
import server.jdbc.DBController;
import common.logic.Order;


/**
 * The GoNatureServer class is the main system's server class.
 * This class extends AbstractServer ocsf class, and manage all the Client-Server design.
 * @Author GalBitton
 * @version 1.0.0
 */
public class GoNatureServer extends AbstractServer {

	// Use Singleton DesignPattern -> only 1 server may be running in our system.
	private static GoNatureServer server = null;
	private ServerGuiController serverController;
	private Connection dbConn;
	
	/**
	 * Constructor
	 * @param port - The port number to connect on
	 * @param serverController - ServerGuiController to send information to the server gui view.
	 * @param dbConn - The Connection instance to the database.
	 */
	public GoNatureServer(int port, ServerGuiController serverController, Connection dbConn) {
		super(port);
		this.serverController=serverController;
		this.dbConn=dbConn;
	}
	
	/**
	 * This method handle the message from client and sends them to the correct method
	 * according to the sent Object instance.
	 * TODO: in our main project -> this method will include all instances.
	 * @param msg - The Object instance the client sent to the server.
	 * @param client - The ConnectionToClient instance which include the details of the client in order to be able send him back answer.
	 */
	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		// get the ip of the client who have sent the message.
		String clientIp = client.getInetAddress().getHostAddress();
		
		if(msg instanceof String) {
			handleStringMsgFromClient((String)msg,client);
			return;
		}
		
		if(msg instanceof Integer) {
			handleOrderSearchFromClient((Integer)msg,client,clientIp);
			return;
		}
		
		if(msg instanceof Order) {
			handleOrderUpdateFromClient((Order)msg,client,clientIp);
			return;
		}
	}
	
	/**
	 * This method handle a specific String message from client
	 * for now relevant only for message when client disconnecting the server.
	 * @param msg - The string message the client sent to the server.
	 * @param client -  The ConnectionToClient instance which include the details of the client in order to be able send him back answer.
	 */
	private void handleStringMsgFromClient(String msg, ConnectionToClient client) {
		if(msg.equals("Disconnect")) {
			clientDisconnected(client);
			return;
		}
	}
	
	/**
	 * This method handle a specific Integer msg from client.
	 * for now relevant only for searching Order in database by it's order ID (Integer).
	 * @param msg - The Integer message the client sent to the server.
	 * @param client - The ConnectionToClient instance which include the details of the client in order to be able send him back answer.
	 * @param clientIp - The client IP.
	 */
	private void handleOrderSearchFromClient(Integer msg, ConnectionToClient client, String clientIp) {
		server.serverController.printToLogConsole(String.format("Requested recieved from client %s to search for order id %s.",clientIp,msg.toString()));
		// search for the order in database.
		Order returnOrder = DBController.searchOrder(dbConn,msg,server.serverController);
		// order found.
		if(returnOrder!=null) {
			respondOrderSearchToClient(returnOrder,client);
			return;
		}
		
		try {
			//notify the client that the order wasn't found.
			client.sendToClient("Order not found");
			//write to log
			serverController.printToLogConsole("Order with id - "+msg+" was not found!");
		}catch(IOException ex) {
			serverController.printToLogConsole("Error while sending order not found to client");
		}
	}
	
	/**
	 * The method called only if the order was found in database.
	 * It sends the client the requested Order entity.
	 * @param returnOrder - The requested Order.
	 * @param client -  The ConnectionToClient instance which include the details of the client in order to be able send him back answer.
	 */
	private void respondOrderSearchToClient(Order returnOrder,ConnectionToClient client) {
		try {
			serverController.printToLogConsole(String.format("Order with id %s was found and sent to client",returnOrder.getOrderNumber().toString()));
			// try to send the client the requested order.
			client.sendToClient((Object)returnOrder);
		}catch(IOException ex) {
			serverController.printToLogConsole("Error while sending order to client");
		}
	}
	
	/**
	 * This method handle specific "Order" message from client.
	 * for now, this method is relevant only for updating old order in database.
	 * In the main project, this method should handle multiple actions on Order instance.
	 * @param order - The new order instance.
	 * @param client - The ConnectionToClient instance which include the details of the client in order to be able send him back answer.
	 * @param clientIp - The client's IP.
	 */
	private void handleOrderUpdateFromClient(Order order, ConnectionToClient client, String clientIp) {
		Order newOrder = (Order)order;
		serverController.printToLogConsole(String.format("Request received from client %s to update details of order with id %s", clientIp,newOrder.getOrderNumber().toString()));
		// try to update the old order in database.
		boolean isUpdated = DBController.updateOrder(dbConn, newOrder.getOrderNumber(),newOrder.getParkName(),newOrder.getTelephoneNumber(),serverController);
		// assume the order didn't update.
		String message = "Order not updated";
		if(isUpdated) {
			message="Order updated";
			serverController.printToLogConsole(String.format("Order (%s) details were updated in database",newOrder.getOrderNumber().toString()));
		}
		
		try {
			// send the client the answer (updated or not updated)
			client.sendToClient(message);
		}catch(IOException ex) {
			serverController.printToLogConsole("Error while sending update message to client");
		}
	}
	
	/**
	 * This method write to log screen the server has been started.
	 */
	@Override
	protected void serverStarted() {
		serverController.printToLogConsole(String.format("Server listening for connnections on address %s:%s",getServerIpAddress(),getPort()));
	}
	
	/**
	 * The method enumerate through the network interface eliminate local host and virtual machines to 
	 * return the right IP;
	 * @return a String containing the correct IP
	 */
	private String getServerIpAddress() {
		String ip;
		try {
			@SuppressWarnings("rawtypes")
			Enumeration e1 =NetworkInterface.getNetworkInterfaces();
			while(e1.hasMoreElements()) {
				NetworkInterface network = (NetworkInterface)e1.nextElement();
				@SuppressWarnings("rawtypes")
				Enumeration e2 = network.getInetAddresses();
				while(e2.hasMoreElements()) {
					InetAddress inetAddress = (InetAddress) e2.nextElement();
					ip=inetAddress.getHostAddress();
					if(ip.contains(".")&&!ip.equals("127.0.0.1")&&!network.getDisplayName().toLowerCase().contains("virtual")) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		}catch(SocketException ex) {
			ex.printStackTrace();
		}
		return "Not found network addresses. please use ipconfig in commandline";
	}
	
	/**
	 * This method write to log screen the server has been stopped.
	 */
	@Override
	protected void serverStopped() {
		serverController.printToLogConsole("Server has stopped listening for connections");
	}
	
	/**
	 * This method write to log screen the server has been stopped.
	 */
	@Override
	protected void serverClosed() {
		serverController.printToLogConsole("Server has been closed");
	}
	
	/**
	 * This method called when new client connect to the server. 
	 * add the client to the server table view and write to log.
	 * @param client - The ConnectionToClient instance which include the details of the client in order to be able send him back answer.
	 */
	@Override
	protected void clientConnected(ConnectionToClient client) {
		InetAddress details = client.getInetAddress();
		serverController.printToLogConsole("Client "+details.getHostName()+" with IP:"+details.getHostAddress()+ " Connected");
		serverController.addToConnected(client);
	}
	
	/**
	 * This method called when a client disconnected from the server.
	 * remove the client from the server's table view and write to log.
	 * @param client - The ConnectionToClient instance which include the details of the client in order to be able send him back answer.
	 */
	@Override
	synchronized protected void clientDisconnected(ConnectionToClient client) {
		InetAddress details = client.getInetAddress();
		serverController.printToLogConsole("Client "+details.getHostName()+" with IP:"+details.getHostAddress()+ " Disconnected");
		serverController.removeFromConnected(client);
	}
	
	/**
	 * This method stop the server to listen on a specific port and close the server.
	 */
	public static void stopServer() {
		// there is no server yet
		if(server==null)
			return;
		
		try {
			// first tell all the clients to disconnect.
			server.sendToAllClients("Disconnect");
			server.stopListening();
			server.close();
		}catch(IOException ex) {
			System.out.println("Error while closing server");
			ex.printStackTrace();
		}
	}
	
	/**
	 * This method start the server to listen on a specific port and connect to database via database controller.
	 * @param db - The DBConnectionDetails entity which contains all required data to connect the database.
	 * @param port - The port number to connect on
	 * @param serverController - ServerGuiController to send information to the server gui view.
	 */
	public static void startServer(DBConnectionDetails db, Integer port, ServerGuiController serverController) {
		// try to connect the database
		Connection dbConn = DBController.connectToMySqlDB(db);
		// if failed -> can't start the server.
		if(dbConn==null) {
			serverController.printToLogConsole("Can't start server! Connection to database failed!");
			return;
		}
		
		serverController.printToLogConsole("Connection to database succeed");
		
		// Singleton DesignPattern. Only 1 instance of server is available.
		if(server!=null) {
			serverController.printToLogConsole("There is already a connected server");
			return;
		}
		
		server=new GoNatureServer(port, serverController,dbConn);
		
		try {
			server.listen();
			// update connection in server gui.
			serverController.connectionSuccessfull();
		}catch(Exception ex) {
			ex.printStackTrace();
			serverController.printToLogConsole("Error - could not listen for clients!");
			stopServer();
			server=null;
		}
	}
	
	
	
}
