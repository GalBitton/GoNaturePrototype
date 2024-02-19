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
		
		parkNameField.getItems().addAll(ParksList);
		parkNameField.setOnAction(this::OnChangeAccountType);
	}
	
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
	
	public void connectToServer() {
		VisitorClient.connectClientToServer(serverIpField.getText(), serverPortField.getText(), this);
		disconnectServerBtn.setDisable(false);
		connectServerBtn.setDisable(true);
	}
	
	public void disconnectFromServer() {
		VisitorClient.disconnectClientFromServer();
		disconnectServerBtn.setDisable(true);
		connectServerBtn.setDisable(false);
	}
	
	public void searchOrderPressed() {
		try {
			VisitorClient.sendSearchOrderToServer(Integer.parseInt(ticketIdField.getText()));
		}catch(NumberFormatException ex) {
			printToConsole("Client tried to enter invalid ticket ID, must be numeric value");
		}
	}
	
	public void updateOrderPressed() {
		try {
			requestedOrder.setParkName(parkName);
			requestedOrder.setTelephoneNumber(phoneNumberField.getText());
			VisitorClient.sendUpdatesToServer(requestedOrder);
		}catch(NumberFormatException ex) {
			printToConsole("Phone Number and Number of Visitors should be numeric values");
		}
	}
	
	public void printToConsole(String msg) {
		System.out.println(msg);
	}
	
	public void disconnected() {
		Platform.runLater(()->clientStatusLbl.setText("Disconnected"));
		Platform.runLater(()->clientStatusLbl.setTextFill(Paint.valueOf("Red")));
		searchBtn.setDisable(true);
		disconnectServerBtn.setDisable(true);
		connectServerBtn.setDisable(false);
		updateOrderBtn.setDisable(true);
	}
	
	public void connected() {
		Platform.runLater(()->clientStatusLbl.setText("Connected"));
		Platform.runLater(()->clientStatusLbl.setTextFill(Paint.valueOf("Green")));
		searchBtn.setDisable(false);
		connectServerBtn.setDisable(true);
		disconnectServerBtn.setDisable(false);
	}
	
	public void searchNotFound() {
		Platform.runLater(()-> orderStatusLbl.setText("Not Found"));
		Platform.runLater(()->orderStatusLbl.setTextFill(Paint.valueOf("Red")));
	}
	
	public void searchFound() {
		Platform.runLater(()-> orderStatusLbl.setText("Found"));
		Platform.runLater(()->orderStatusLbl.setTextFill(Paint.valueOf("Green")));
		updateOrderBtn.setDisable(false);
	}
	
	public void updateOrderMessage(String  msg) {
		Platform.runLater(()-> updateRequestLbl.setText(msg));
		if(msg.equals("Order not updated"))
			Platform.runLater(()->updateRequestLbl.setTextFill(Paint.valueOf("Red")));
		else
			Platform.runLater(()->updateRequestLbl.setTextFill(Paint.valueOf("Green")));
		updateOrderBtn.setDisable(true);
	}
	
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
