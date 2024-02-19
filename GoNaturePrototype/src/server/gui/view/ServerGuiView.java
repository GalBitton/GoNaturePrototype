package server.gui.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import server.gui.controller.ServerGuiController;
import server.logic.GoNatureServer;

public class ServerGuiView extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception {
		// test comment
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ServerGui.fxml"));
		AnchorPane root = fxmlLoader.load();
		@SuppressWarnings("unused")
		ServerGuiController controller = fxmlLoader.getController();
		
		Scene scene = new Scene(root);
		Image icon = new Image("/common/resources/icon.png");
		primaryStage.getIcons().add(icon);
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		// disconnect before closing waiting to run on main javaFX thread.
		primaryStage.setOnCloseRequest(e -> Platform.runLater(()->GoNatureServer.stopServer()));
		primaryStage.setTitle("GoNature - Parks&Vacations - Server Side");
		primaryStage.show();
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
