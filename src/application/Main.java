package application;
	
import java.time.LocalDate;
import java.time.LocalTime;

import data.Order;
import javafx.application.Application;
import javafx.stage.Screen;
import javafx.stage.Stage;
import physics.General;
import tools.PaneData;
import tools.Timer;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;


public class Main extends Application 
{
	public static BorderPane root;
	
	@Override
	public void start(Stage primaryStage) 
	{
		try 
		{
			new General();
			root = new BorderPane();
			Scene scene = new Scene(root);	
			primaryStage.setScene(scene);	
			root.setTop(PaneData.get("Top Belt"));
			TopBeltController.controller.hBox.prefWidthProperty().bind(root.widthProperty());

			Rectangle2D screenBounds = Screen.getPrimary().getBounds();
			primaryStage.setMinHeight(433);
			primaryStage.setMinWidth(600);
			primaryStage.setX((screenBounds.getMaxX() - 600) / 2);
			primaryStage.setY((screenBounds.getMaxY() - 433) / 2);
			primaryStage.show();
			root.setCenter(PaneData.get("Dynamic Board"));
		} 
		catch(Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
