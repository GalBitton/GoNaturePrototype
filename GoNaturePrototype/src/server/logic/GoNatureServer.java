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

public class GoNatureServer extends AbstractServer {

	private static GoNatureServer server = null;
	private ServerGuiController serverController;
	private Connection dbConn;
	
	public GoNatureServer(int port, ServerGuiController serverController, Connection dbConn) {
		super(port);
		this.serverController=serverController;
		this.dbConn=dbConn;
	}
	
	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
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
	
	private void handleStringMsgFromClient(String msg, ConnectionToClient client) {
		if(msg.equals("Disconnect")) {
			clientDisconnected(client);
			return;
		}
	}
	
	private void handleOrderSearchFromClient(Integer msg, ConnectionToClient client, String clientIp) {
		server.serverController.printToLogConsole(String.format("Requested recieved from client %s to search for order id %s.",clientIp,msg.toString()));
		Order returnOrder = DBController.searchOrder(dbConn,msg,server.serverController);
		
		if(returnOrder!=null) {
			respondOrderSearchToClient(returnOrder,client);
			return;
		}
		
		try {
			client.sendToClient("Order not found");
			serverController.printToLogConsole("Order with id - "+msg+" was not found!");
		}catch(IOException ex) {
			serverController.printToLogConsole("Error while sending order not found to client");
		}
	}
	
	private void respondOrderSearchToClient(Order returnOrder,ConnectionToClient client) {
		try {
			serverController.printToLogConsole(String.format("Order with id %s was foudn and sent to client",returnOrder.getOrderNumber().toString()));
			client.sendToClient((Object)returnOrder);
		}catch(IOException ex) {
			serverController.printToLogConsole("Error while sending order to client");
		}
	}
	
	private void handleOrderUpdateFromClient(Order order, ConnectionToClient client, String clientIp) {
		Order newOrder = (Order)order;
		serverController.printToLogConsole(String.format("Request received from client %s to update details of order with id %s", clientIp,newOrder.getOrderNumber().toString()));
		boolean isUpdated = DBController.updateOrder(dbConn, newOrder.getOrderNumber(),newOrder.getParkName(),newOrder.getTelephoneNumber(),serverController);
		String message = "Order not updated";
		if(isUpdated) {
			message="Order updated";
			serverController.printToLogConsole(String.format("Order (%s) details were updated in database",newOrder.getOrderNumber().toString()));
		}
		
		try {
			client.sendToClient(message);
		}catch(IOException ex) {
			serverController.printToLogConsole("Error while sending update message to client");
		}
	}
	
	@Override
	protected void serverStarted() {
		serverController.printToLogConsole(String.format("Server listening for connnections on address %s:%s",getServerIpAddress(),getPort()));
	}
	
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
	
	@Override
	protected void serverStopped() {
		serverController.printToLogConsole("Server has stopped listening for connections");
	}
	
	@Override
	protected void serverClosed() {
		serverController.printToLogConsole("Server has been closed");
	}
	
	@Override
	protected void clientConnected(ConnectionToClient client) {
		InetAddress details = client.getInetAddress();
		serverController.printToLogConsole("Client "+details.getHostName()+" with IP:"+details.getHostAddress()+ " Connected");
		serverController.addToConnected(client);
	}
	
	@Override
	synchronized protected void clientDisconnected(ConnectionToClient client) {
		InetAddress details = client.getInetAddress();
		serverController.printToLogConsole("Client "+details.getHostName()+" with IP:"+details.getHostAddress()+ " Disconnected");
		serverController.removeFromConnected(client);
	}
	
	public static void stopServer() {
		if(server==null)
			return;
		
		try {
			server.sendToAllClients("Disconnect");
			server.stopListening();
			server.close();
		}catch(IOException ex) {
			System.out.println("Error while closing server");
			ex.printStackTrace();
		}
	}
	
	public static void startServer(DBConnectionDetails db, Integer port, ServerGuiController serverController) {
		Connection dbConn = DBController.connectToMySqlDB(db);
		if(dbConn==null) {
			serverController.printToLogConsole("Connection to database failed!");
			return;
		}
		
		serverController.printToLogConsole("Connection to database succeed");
		
		// SINGLETON
		if(server!=null) {
			serverController.printToLogConsole("There is already a connected server");
			return;
		}
		
		server=new  GoNatureServer(port, serverController,dbConn);
		
		try {
			server.listen();
			serverController.connectionSuccessfull();
		}catch(Exception ex) {
			ex.printStackTrace();
			serverController.printToLogConsole("Error - could not listen for clients!");
			server=null;
		}
	}
	
	
	
}
