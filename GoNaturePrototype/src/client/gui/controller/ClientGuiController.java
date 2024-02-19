package client.gui.controller;


import java.net.URL;
import java.util.ResourceBundle;

import client.logic.VisitorClient;
import common.enums.ParkEnum;
import common.logic.Order;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;

 
/**
 * 
 * @Author NadavReubens
 * @version 1.0.0 
 */
public class ClientGuiController implements Initializable{

	@FXML
	private TextField serverIpField;
	
	@FXML
	private TextField serverPortField;
	
	@FXML
	private TextField ticketIdField;
	
	@FXML
	private ComboBox<ParkEnum> parkNameField;
	
	@FXML
	private TextField phoneNumberField;
	
	@FXML
	private TextField timeField;
	
	@FXML
	private TextField emailField;
	
	@FXML
	private TextField numberOfVisitorField;
	
	@FXML
	private TextField orderNumberField;
	
	@FXML
	private Button connectServerBtn;
	
	@FXML
	private Button disconnectServerBtn;
	
	@FXML
	private Button searchBtn;

	@FXML
	private Button updateOrderBtn;
	
	@FXML
	private Label clientStatusLbl;
	
	@FXML
	private Label orderStatusLbl;
	
	@FXML
	private Label updateRequestLbl;
	
	
	private ObservableList<ParkEnum> ParksList=FXCollections.observableArrayList(
			ParkEnum.Hermon_Mountain,
			ParkEnum.Agmon_HaHula,
			ParkEnum.Carmel_Forest
			);
	
	private String parkName;
	
	private static Order requestedOrder;
	
	public ClientGuiController() {}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// disable irrelevant text fields
		timeField.setDisable(true);
		emailField.setDisable(true);
		numberOfVisitorField.setDisable(true);
		orderNumberField.setDisable(true);
		
		disconnectServerBtn.setDisable(true);//added by nadav
		searchBtn.setDisable(true);
		updateOrderBtn.setDisable(true);
		parkNameField.getItems().addAll(ParksList);
		parkNameField.setOnAction(this::OnChangeAccountType);
	}
	
	/**
	 * the method responsible to change the value of parkName field
	 * in the Client view page when event occurred
	 * @param event-button pressed or keyframe finished
	 */
	public void OnChangeAccountType(ActionEvent event) {
		ParkEnum park = parkNameField.getValue();

		switch (park) {
		case Hermon_Mountain:
			parkName="Hermon Mountain";
			break;
			
		case Agmon_HaHula:
			parkName="Agmon HaHula";
			break;
			
		case Carmel_Forest:
			parkName="Carmel Forest";
			break;
			
		default:
			parkName="Unregistered Park";
				break;
		}
		
	}
	
	/**
	 * the method fetch entered serverIpField and serverPortField
	 * and call method connectClientToServer from VisitorClient class to try and connect
	 */
	public void connectToServer() {
		boolean isConnected=VisitorClient.connectClientToServer(serverIpField.getText(), serverPortField.getText(), this);
		if(isConnected) {
		disconnectServerBtn.setDisable(false);//adjust view 
		connectServerBtn.setDisable(true);
		searchBtn.setDisable(false);
		connected();
		}
	}
	
	/**
	 * onClick on disconnectServerBtn the method call the method 
	 * disconnectClientFromServer from VisitorClient class to try and disconnect
	 */
	public void disconnectFromServer() {
		boolean isDissconected=VisitorClient.disconnectClientFromServer();
		if(isDissconected) {
		disconnectServerBtn.setDisable(true);//adjust view 
		connectServerBtn.setDisable(false);
		searchBtn.setDisable(true);
		updateOrderBtn.setDisable(true);
		disconnected();
		}
	}
	
	/**
	 * onClick on searchOrderPressed the method fetch the ticketIdField and 
	 * call sendSearchOrderToServer method from VisitorClient class to search
	 * order by ticketID
	 */
	public void searchOrderPressed() {
		try {
			VisitorClient.sendSearchOrderToServer(Integer.parseInt(ticketIdField.getText()));
		}catch(NumberFormatException ex) {
			printToConsole("Client tried to enter invalid ticket ID, must be numeric value");
		}
	}
	
	/**
	 * onClick on updateOrderPressed the method fetch the parkName and phoneNumberField 
	 * and call sendUpdatesToServer method from VisitorClient class to update those values
	 */
	public void updateOrderPressed() {
		try {
			requestedOrder.setParkName(parkName);
			requestedOrder.setTelephoneNumber(phoneNumberField.getText());
			VisitorClient.sendUpdatesToServer(requestedOrder);
		}catch(NumberFormatException ex) {
			printToConsole("Phone Number and Number of Visitors should be numeric values");
		}
	}
	
	/**
	 * the method gets a message and print to console
	 * @param msg
	 */
	public void printToConsole(String msg) {
		System.out.println(msg);
	}
	
	/**
	 * the method update the Client UI after Client disconnected
	 */
	public void disconnected() {
		Platform.runLater(()->clientStatusLbl.setText("Disconnected"));
		Platform.runLater(()->clientStatusLbl.setTextFill(Paint.valueOf("Red")));
		
	}
	
	/**
	 * the method update the Client UI after Client connected
	 */
	public void connected() {
		Platform.runLater(()->clientStatusLbl.setText("Connected"));
		Platform.runLater(()->clientStatusLbl.setTextFill(Paint.valueOf("Green")));
	}
	
	/**
	 * the method update the Client UI after search didn't succeed
	 */
	public void searchNotFound() {
		Platform.runLater(()-> orderStatusLbl.setText("Not Found"));
		Platform.runLater(()->orderStatusLbl.setTextFill(Paint.valueOf("Red")));
	}
	
	/**
	 * the method update the Client UI after search succeeded
	 */
	public void searchFound() {
		Platform.runLater(()-> orderStatusLbl.setText("Found"));
		Platform.runLater(()->orderStatusLbl.setTextFill(Paint.valueOf("Green")));
		updateOrderBtn.setDisable(false);
	}
	
	/**
	 * the method update the Client UI after updating Order 
	 * if succeeded green text 
	 * if failed red text 
	 * @param msg -message if succeeded/failed
	 */
	public void updateOrderMessage(String  msg) {
		Platform.runLater(()-> updateRequestLbl.setText(msg));
		if(msg.equals("Order not updated"))
			Platform.runLater(()->updateRequestLbl.setTextFill(Paint.valueOf("Red")));
		else
			Platform.runLater(()->updateRequestLbl.setTextFill(Paint.valueOf("Green")));
		updateOrderBtn.setDisable(true);
	}
	
	/**
	 * the method update the Client UI 
	 * shows order details 
	 * @param order
	 */
	public void displayOrder(Order order) {
		requestedOrder=order;
		emailField.setText(order.getEmail());
		setParkNameFromOrder(order.getParkName());
		timeField.setText(order.getTimeOfVisit());
		numberOfVisitorField.setText(order.getNumberOfVisitors().toString());
		phoneNumberField.setText(order.getTelephoneNumber());
		orderNumberField.setText(order.getOrderNumber().toString());
		searchFound();
		updateOrderBtn.setDisable(false);
	}
	
	/**
	 * the method update the Client UI 
	 * the method gets name of park put it in the parkNameField
	 * @param park
	 */
	private void setParkNameFromOrder(String park) {
		switch(park) {
		case "Hermon Mountain":
			Platform.runLater(()->parkNameField.setValue(ParksList.get(0)));
			break;
			
		case "Agmon HaHula":
			Platform.runLater(()->parkNameField.setValue(ParksList.get(1)));
			break;
			
		case "Carmel Forest":
			Platform.runLater(()->parkNameField.setValue(ParksList.get(2)));
			break;
			
		}
	}
}
