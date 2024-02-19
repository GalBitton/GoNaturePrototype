package client.logic;

import java.io.IOException;

import client.gui.controller.ClientGuiController;
import common.logic.Order;
import client.ocsf.AbstractClient;

public class VisitorClient extends AbstractClient {
	private static VisitorClient client = null;
	private ClientGuiController clientController;
	
	private VisitorClient(String host, int port,ClientGuiController clientController) throws IOException {
		super(host, port);
		this.clientController=clientController;
		// method of AbstractClient
		openConnection();
	}
	
	public static void connectClientToServer(String host, String port, ClientGuiController clientController) {
		// Design Pattern - Singleton.
		// Only create one instance of client.
		if(client!=null) {
			clientController.connected();
		}
		
		try {
			client= new VisitorClient(host,Integer.parseInt(port),clientController);
		}catch(IOException ex) {
			clientController.printToConsole("Error while connection Client to Server");
		}
	}
	
	public static void disconnectClientFromServer() {
		// client is not connected and has not been created yet.
		if(client==null)
			return;
		
		try {
			client.sendToServer("Disconnect");
			client.closeConnection();
			client.clientController.disconnected();
			client=null;
		}catch(IOException ex) {
			client.clientController.printToConsole("Error while disconnecting Client from Server");
			ex.printStackTrace();
		}
		
	}
	
	public static void sendSearchOrderToServer(Integer orderNumber) {
		try {
			client.sendToServer(orderNumber);
		}catch(IOException ex) {
			client.clientController.printToConsole("Error while sending search request to the Server");
		}
	}
	
	public static void sendUpdatesToServer(Order order) {
		try {
			client.sendToServer(order);
		}catch(IOException ex) {
			client.clientController.printToConsole("Error while sending update request to the Server");
		}
	}
	
	private void handleStringMessageFromServer(String msg) {
		if(msg.equals("Order not found")) {
			clientController.searchNotFound();
			return;
		}
		
		if(msg.equals("Order not updated")||msg.equals("Order updated")) {
			clientController.updateOrderMessage(msg);
			return;
		}
		
		if(msg.equals("Disconnect")) {
			try {
				client.closeConnection();
				client=null;
			}catch(IOException ex) {
				clientController.printToConsole("Error while closing Client connection after Server closed");
			}
			clientController.disconnected();
		}
	}
	
	private void handleOrderFromServer(Order order) {
		clientController.displayOrder(order);
	}
	
	@Override
	protected void connectionEstablished() {
		clientController.printToConsole("Connect to "+getHost()+", on Port: "+getPort());
		clientController.connected();
	}
	
	@Override
	protected void handleMessageFromServer(Object msg) {
		if(msg instanceof Order) {
			handleOrderFromServer((Order)msg);
			return;
		}
		
		if(msg instanceof String) {
			handleStringMessageFromServer((String)msg);
			return;
		}
		
	}

}
