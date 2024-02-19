package server.gui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import server.logic.ClientConnection;
import server.logic.GoNatureServer;
import server.ocsf.ConnectionToClient;
import server.jdbc.DBConnectionDetails;


/**
 * The ServerGuiController class is the controller which connect the Server Gui Screen 
 * with the GoNatureServer instance (and the user who click on the buttons)
 * @Author GalBitton
 * @version 1.0.0
 */
public class ServerGuiController {
	
	//javaFX binding elements
	@FXML
	private Button connectServer;
	@FXML
	private Button disconnectServer;
	@FXML
	private TextField ipField;
	@FXML
	private TextField portField;
	@FXML
	private TextField dbNameField;
	@FXML
	private TextField dbUsernameField;
	@FXML
	private TextField dbPasswordField;
	@FXML
	private TextArea serverLog;
	@FXML
	private TableView<ClientConnection> clientsTable;
	@FXML
	private TableColumn<ClientConnection,String> ipColumn;
	@FXML
	private TableColumn<ClientConnection,String> hostColumn;
	@FXML
	private TableColumn<ClientConnection,String> statusColumn;
	
	// Observable collection in order to bind it to the table view.
	private final ObservableList<ClientConnection> connectedClientsList = FXCollections.observableArrayList();
	// Serial number of lines in log screen.
	private int logLine=1;
	
	// Empty Constructor
	public ServerGuiController() {}
	
	/**
	 * This method write to log.
	 * @param msg - The String message we want to write to log screen.
	 */
	public void printToLogConsole(String msg) {
		System.out.println(msg);
 	    serverLog.appendText(""+(logLine++)+". "+msg+"\n");
	}
	
	/**
	 * This method called when the connect server button was clicked in the gui screen.
	 * This method get the data from the text fields in gui screen. and create DBConnectionDetails instance
	 * and empty table view.
	 */
	public void onConnectServerClicked() {
		DBConnectionDetails database = new DBConnectionDetails();
		
		Integer portNumber;
		try {
			portNumber=Integer.parseInt(portField.getText());
		}catch(Exception ex) {
			printToLogConsole("Port must be a number");
			return;
		}
		
		try {
			database.setIp(ipField.getText());
			database.setName(dbNameField.getText());
			database.setUsername(dbUsernameField.getText());
			database.setPassword(dbPasswordField.getText());
			
		}catch(Exception ex) {
			printToLogConsole("All field must be filled");
			return;
		}
		
		// start the server
		GoNatureServer.startServer(database, portNumber, this);
		// initialize the table view
		initializeViewTable();
	}
	
	/**
	 * This method called when the disconnect server was clicked in the gui screen.
	 * This method called to stop the server, clear the table view, and update the gui.
	 */
	public void onDisconnectServerClicked() {
		GoNatureServer.stopServer();
		disconnectServer.setDisable(true);
		connectServer.setDisable(false);
		disableFields(false);
		connectedClientsList.clear();
	}
	
	/**
	 * This method add specific client to the connected clients table view in the gui screen.
	 * @param client- The ConnectionToClient instance which include the details of the client.
	 */
	public void addToConnected(ConnectionToClient client) {
		connectedClientsList.add(new ClientConnection(client.getInetAddress().getHostAddress(), client.getInetAddress().getHostName(),"Connected"));
	}
	
	/**
	 * This method remove specific client from the connected clients table view in the gui screen.
	 * This method called from server when client has been disconnected.
	 * @param client - The ConnectionToClient instance which include the details of the client.
	 */
	public void removeFromConnected(ConnectionToClient client) {
		for(ClientConnection c: connectedClientsList) {
			if(c.getHostIp().equals(client.getInetAddress().getHostAddress())&&c.getHostName().equals(client.getInetAddress().getHostName())) {
				connectedClientsList.remove(c);
				return;
			}
		}
	}
	
	/**
	 * This method enable the disconnect server button and update all other gui elements.
	 */
	public void connectionSuccessfull() {
		disconnectServer.setDisable(false);
		disableFields(true);
	}
	
	/**
	 * This method initialize the table view, and bind it to the connectedClientsList.
	 */
	private void initializeViewTable() {
		ipColumn.setCellValueFactory(new PropertyValueFactory<ClientConnection, String>("hostIp"));
		hostColumn.setCellValueFactory(new PropertyValueFactory<ClientConnection, String>("hostName"));
		statusColumn.setCellValueFactory(new PropertyValueFactory<ClientConnection, String>("status"));
		clientsTable.setItems(connectedClientsList);
	}
	
	/**
	 * This method disable/enable all fields, except disconnected.
	 * @param flag - boolean flag, to set the gui elements disabled or enabled.
	 */
	private void disableFields(boolean flag) {
		connectServer.setDisable(flag);
		ipField.setDisable(flag);
		portField.setDisable(flag);
		dbNameField.setDisable(flag);
		dbUsernameField.setDisable(flag);
		dbPasswordField.setDisable(flag);
	}
}
