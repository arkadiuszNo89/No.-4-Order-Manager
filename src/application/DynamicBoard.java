package application;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Set;

import data.Order;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import physics.General;
import tools.InitTools;
import tools.Timer;

public class DynamicBoard extends BorderPane
{
	private ScrollPane scroll;
	public VBox board;
	
	public DynamicBoard()
	{
		scroll = new ScrollPane();
		board = new VBox();	
		
		this.setCenter(scroll);
		scroll.setContent(board);
		
		board.prefWidthProperty().bind(this.widthProperty());
		board.setPadding(new Insets(5,0,0,0));
		
		InitTools.addLabelLine(board, "Consolas", 20, "#m#center#"+"Brak zamówieñ");
	}
	
	public void refresh (Collection <OrderBelt> values)
	{
		board.getChildren().clear();
		board.getChildren().addAll(values);
		if (values.isEmpty()) InitTools.addLabelLine(board, "Consolas", 20, "#m#center#"+"Brak zamówieñ");
	}
}
