package client.gui.view;

import client.gui.controller.ClientGuiController;
import client.logic.VisitorClient;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * ClientGuiView responsible to load and open the view 
 * of the first page for the Client
 * @Author \NadavReubens
 * @version 1.0.0
 */
public class ClientGuiView extends Application{

	
	@Override
	public void start(Stage primaryStage) throws Exception {
		//add comment
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ClientGui.fxml"));
		AnchorPane root = fxmlLoader.load();
		@SuppressWarnings("unused")
		ClientGuiController controller = fxmlLoader.getController();
		
		Scene scene = new Scene(root);
		
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.setOnCloseRequest(e -> VisitorClient.disconnectClientFromServer());
		primaryStage.setTitle("GoNature - Parks&Vacations - Client Side");
		primaryStage.show();
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
