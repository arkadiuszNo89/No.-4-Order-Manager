package application;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import data.Order;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import physics.General;

public class OrderBelt extends HBox
{
	private Label title;
	private Label timeLeft;
	private ProgressBar progressBar;
	private Order order;
	private LocalTime firstClick;
	private LocalTime secondClick;
	private static List <OrderBelt> allBelts = new ArrayList <OrderBelt> ();
	private static OrderBelt belt = null;
	
	public OrderBelt(String name, Order order)
	{
		this.title = new Label(name);
		this.timeLeft = new Label();
		this.progressBar = new ProgressBar();
		this.order = order;
		
		this.setPrefHeight(25);
		this.setPadding(new Insets(3, 0, 3, 0));
		title.setMinWidth(265);
		title.prefHeightProperty().bind(this.heightProperty());
		title.setPadding(new Insets(0,0,0,10));
		timeLeft.setMinWidth(100);
		timeLeft.prefHeightProperty().bind(this.heightProperty());
		timeLeft.setPadding(new Insets(0,0,0,10));
		progressBar.prefWidthProperty().bind(this.widthProperty());
		progressBar.prefHeightProperty().bind(this.heightProperty());
		this.getChildren().addAll(title, progressBar, timeLeft);
		
		allBelts.add(this);
		prepare_OneAndDoubleClick();
	}
	private void prepare_OneAndDoubleClick()
	{
		firstClick = LocalTime.now();
		secondClick = LocalTime.now();
		
		this.setOnMouseClicked( e -> 
		{
//			One click:
			if (!General.getLineOfDeadOrders().contains(this.order))
			{
				if (belt != this) 
				{
					if (belt != null) belt.setBackground(new Background(new BackgroundFill(null, null, null)));
					this.setBackground(new Background(new BackgroundFill(Color.HONEYDEW, null, null)));
					belt = this;
				}
				else 
				{
					this.setBackground(new Background(new BackgroundFill(null, null, null)));
					belt = null;
				}
			}

			
//			Double click:
			secondClick = LocalTime.now();
			if (firstClick.plusNanos(600000000).isAfter(secondClick))
			{
				order.showTicket();			
				belt = this;
				if (!General.getLineOfDeadOrders().contains(this.order))
					this.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
			}
			firstClick = LocalTime.now();
		});
	}
	
	public Label getTitleLabel()
	{
		return title;
	}
	
	public Label getTimeLeftLabel()
	{
		return timeLeft;
	}
	
	public void setTimeLeft(String timeLeft)
	{
		this.timeLeft.setText(timeLeft);
	}
	
	public void setProgress(double volume)
	{
		progressBar.setProgress(volume);
	}
}
