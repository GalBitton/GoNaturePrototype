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
	
	private final ObservableList<ClientConnection> connectedClientsList = FXCollections.observableArrayList();
	private int logLine=1;
	
	public ServerGuiController() {}
	
	public void printToLogConsole(String msg) {
		System.out.println(msg);
 	    serverLog.appendText(""+(logLine++)+". "+msg+"\n");
	}
	
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
		
		GoNatureServer.startServer(database, portNumber, this);
		initializeViewTable();
	}
	
	public void onDisconnectServerClicked() {
		GoNatureServer.stopServer();
		disconnectServer.setDisable(true);
		connectServer.setDisable(false);
		disableFields(false);
		connectedClientsList.clear();
	}
	
	public void addToConnected(ConnectionToClient client) {
		connectedClientsList.add(new ClientConnection(client.getInetAddress().getHostAddress(), client.getInetAddress().getHostName(),"Connected"));
	}
	
	public void removeFromConnected(ConnectionToClient client) {
		for(ClientConnection c: connectedClientsList) {
			if(c.getHostIp().equals(client.getInetAddress().getHostAddress())&&c.getHostName().equals(client.getInetAddress().getHostName())) {
				connectedClientsList.remove(c);
				return;
			}
		}
	}
	
	public void connectionSuccessfull() {
		disconnectServer.setDisable(false);
		disableFields(true);
	}
	
	private void initializeViewTable() {
		ipColumn.setCellValueFactory(new PropertyValueFactory<ClientConnection, String>("hostIp"));
		hostColumn.setCellValueFactory(new PropertyValueFactory<ClientConnection, String>("hostName"));
		statusColumn.setCellValueFactory(new PropertyValueFactory<ClientConnection, String>("status"));
		clientsTable.setItems(connectedClientsList);
	}
	
	private void disableFields(boolean flag) {
		connectServer.setDisable(flag);
		ipField.setDisable(flag);
		portField.setDisable(flag);
		dbNameField.setDisable(flag);
		dbUsernameField.setDisable(flag);
		dbPasswordField.setDisable(flag);
	}
}
