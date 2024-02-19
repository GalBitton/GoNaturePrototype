package client.logic;

import java.io.IOException;

import client.gui.controller.ClientGuiController;
import common.logic.Order;
import client.ocsf.AbstractClient;


/**
 * 
 * @Author NadavReubens
 * @version 1.0.0
 */
public class VisitorClient extends AbstractClient {
	private static VisitorClient client = null;
	private ClientGuiController clientController;
	
	/**
	 * 
	 * @param host -ip of server 
	 * @param port
	 * @param clientController
	 * @throws IOException
	 */
	private VisitorClient(String host, int port,ClientGuiController clientController) throws IOException {
		super(host, port);
		this.clientController=clientController;
		// method of AbstractClient
		openConnection();
	}
	
	/**
	 * the method check if Client already connected and if not 
	 * try to connect with host and port provided
	 * @param host -ip of server 
	 * @param port
	 * @param clientController
	 * @return
	 */
	public static boolean connectClientToServer(String host, String port, ClientGuiController clientController) {
		// Design Pattern - Singleton.
		// Only create one instance of client.
		if(client!=null) {
			clientController.printToConsole("Client already connected");
			return false;
		}
		
		try {
			client= new VisitorClient(host,Integer.parseInt(port),clientController);
			return true;
		}catch(IOException ex) {
			clientController.printToConsole("Error while connection Client to Server");
			return false;
		}catch(Exception e) {
			clientController.printToConsole(e.getMessage());
			return false;
		}
	}
	
	/**
	 * the method checks if Client is connected and try to disconnect him 
	 * the method return true if disconnected succeeded false otherwise
	 */
	public static boolean disconnectClientFromServer() {
		// client is not connected and has not been created yet.
		if(client==null)
			return false;
		
		try {
			client.sendToServer("Disconnect");
			client.closeConnection();
			client.clientController.disconnected();
			client=null;
		}catch(IOException ex) {
			client.clientController.printToConsole("Error while disconnecting Client from Server");
			ex.printStackTrace();
			return false;
		}
		return true;
		
	}
	/**
	 * the method gets order number and send it to server
	 * in purpose to get order detail from server
	 * @param orderNumber
	 */
	public static void sendSearchOrderToServer(Integer orderNumber) {
		try {
			client.sendToServer(orderNumber);
		}catch(IOException ex) {
			client.clientController.printToConsole("Error while sending search request to the Server");
		}
	}
	
	/**
	 * the method gets order that contains parkName and phoneNumberField
	 * and send it to server 
	 * in purpose to update the order details
	 * @param order
	 */
	public static void sendUpdatesToServer(Order order) {
		try {
			client.sendToServer(order);
		}catch(IOException ex) {
			client.clientController.printToConsole("Error while sending update request to the Server");
		}
	}
	
	/**
	 * method gets message from server and act accordingly
	 * @param msg
	 */
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
	
	/**
	 * the method gets order from server and call displayOrder method
	 * from clientController 
	 * @param order
	 */
	private void handleOrderFromServer(Order order) {
		clientController.displayOrder(order);
	}
	
	/**
	 * the method print to Console about establishing connection
	 */
	@Override
	protected void connectionEstablished() {
		clientController.printToConsole("Connect to "+getHost()+", on Port: "+getPort());
		clientController.connected();
	}
	
	/**
	 * the method gets  message from server and classify instanceOf which Object 
	 * and act accordingly 
	 * @param msg
	 */
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
