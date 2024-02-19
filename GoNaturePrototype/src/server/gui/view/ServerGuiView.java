package server.gui.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import server.logic.GoNatureServer;

/**
 * This is the main application class, which extends the javaFX application class.
 * @Author GalBitton
 * @version 1.0.0
 */
public class ServerGuiView extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ServerGui.fxml"));
		AnchorPane root = fxmlLoader.load();
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
