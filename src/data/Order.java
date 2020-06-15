package data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import application.OrderBelt;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import physics.General;
import physics.MyAlert;
import tools.InitTools;
import tools.SaveCreator;
import tools.Timer;

public class Order extends BorderPane implements Comparable <Order>
{
	private String title;
	private Company company;
	private String totalCost;
	private String realizationTime;
	private List <String> shopList;
	private List <String> containerOfVals;
	private LocalDate startDate;
	private LocalDate endDate;
	private LocalTime startTime;
	private LocalTime endTime;
	private int minutesLong;
	public boolean flagArchive;

	private OrderBelt orderBelt;
	private Stage stage;
	private Scene scene;
	public ScrollPane scrollPane;
	private VBox box;
	private HBox bottomBox;
	private Button but_delete, but_moveToArch, but_colapseAll;
	private Button buttonsArray[];
	
	
	public Order (String title, Company company, LocalDate endDate, LocalTime endTime, String totalCost, String realizationTime, List <String> shopList)
	{
		this.title = title;
		this.company = company;
		this.totalCost = totalCost;
		this.realizationTime = realizationTime;
		this.shopList = shopList;
		this.flagArchive = false;
		this.endDate = endDate;
		this.endTime = endTime;
		this.orderBelt = new OrderBelt(title, this);
		
		if (!SaveCreator.loadSupportMode)
		{
			this.startDate = LocalDate.now();
			this.startTime = LocalTime.now();
			this.minutesLong = Timer.minutesLongCounter(endDate, endTime);
			initTicket();
			createStringContainer();
		}
	}
	public Order (List <String> container, List <String> shopList)
	{
		this(container.get(0), General.listOfCompanies.get(container.get(1)), LocalDate.parse(container.get(2)),
				LocalTime.parse(container.get(3)), container.get(4), container.get(5), shopList);
		startDate = LocalDate.parse(container.get(6));
		startTime = LocalTime.parse(container.get(7));
		minutesLong = Integer.parseInt(container.get(8));
		initTicket();
		createStringContainer();
	}
	
	private void initButtons()
	{
		bottomBox = new HBox();
		but_delete = new Button("Usuñ");
		but_moveToArch = new Button("Do archiwum");
		but_colapseAll = new Button("Przenieœ wszystkie");
		buttonsArray = new Button [] {but_delete, but_moveToArch, but_colapseAll};
		this.setBottom(bottomBox);
		
		bottomBox.prefWidthProperty().bind(this.widthProperty());
		bottomBox.setAlignment(Pos.CENTER_RIGHT);
		
		for (Button button : buttonsArray) 
		{
			button.setPrefHeight(25);
			button.setFont(Font.font("Consolas", 10));
		}
		
		but_delete.setOnAction( e ->
		{
			if (new MyAlert("Usun¹æ wybrane zamówienie?").getDecision())
			{
				stage.close();
				General.deleteOrder(this);
			}
		});
		
		but_moveToArch.setOnAction( e ->
		{
			if (new MyAlert("Przenieœæ wybrane zamówienie do archiwum?").getDecision()) General.moveToArchive(this, false);
		});
		
		but_colapseAll.setOnAction( e ->
		{	
			if (new MyAlert("Przenieœæ wszystkie zakoñczone zamówienia do archiwum?").getDecision()) General.flagColapse = true;
		});
	}
	
	private void createStringContainer()
	{	
		containerOfVals = new ArrayList <String> ();
		containerOfVals.addAll(Arrays.asList(title, company.toString(), endDate.toString(), endTime.toString(),
				totalCost, realizationTime, startDate.toString(), startTime.toString(),  minutesLong+""));
		containerOfVals.addAll(shopList);
		containerOfVals.add("#end");
	}
	
	public List <String> getStringData()
	{
		return containerOfVals;
	}
	
	public int getMinutesLong()
	{
		return minutesLong;
	}
	
	public OrderBelt getOrderBelt()
	{
		return orderBelt;
	}
	
	public LocalDate getStartDate() {
		return startDate;
	}

	public LocalTime getStartTime() {
		return startTime;
	}
	
	public LocalDate getEndDate()
	{
		return endDate;
	}
	
	public LocalTime getEndTime()
	{
		return endTime;
	}
	
	public String getTotalCost()
	{
		return totalCost;
	}
	
	public String getRealizationTime()
	{
		return realizationTime;
	}
	
	public HBox getBottomBox()
	{
		return bottomBox;
	}
	
	@Override
	public String toString()
	{
		return title;
	}
	
	@Override
	public int compareTo(Order tmp)
	{			
		int x = 1;
		if (flagArchive) x = -1;
		if (this.endDate.isBefore(tmp.endDate)) return -x;
		else if (this.endDate.isAfter(tmp.endDate)) return x;
		else if (this.endTime.isBefore(tmp.endTime)) return -x;
		else if (this.endTime.isAfter(tmp.endTime)) return x;
		else return this.title.compareTo(tmp.title);
	}
	
	private void initTicket()
	{
		scene = new Scene(this);
		stage = new Stage();
		scrollPane = new ScrollPane();
		box = new VBox();
		stage.setScene(scene);
		this.setCenter(scrollPane);
		scrollPane.setContent(box);
		
		this.setPrefSize(400, 500);
		box.prefWidthProperty().bind(this.widthProperty());
		box.prefHeightProperty().bind(this.heightProperty());
		box.setPadding(new Insets(10, 20, 10, 0));
		stage.setResizable(false);
		initButtons();
		
		InitTools.addLabelLine(box, "Consolas", 18, "#m#center#bold#" + title);
		InitTools.addLabelLine(box, "Consolas", 14, "#m#nl#", "#m#nl#",
				"#m#right#"+"Data wystawienia:", "#m#right#"+startDate+"  "+startTime.format(DateTimeFormatter.ofPattern("HH:mm")),
				"#m#right#"+"Dane dostawicy:", "#m#right#"+company.getCompanyData()[0],
				"#m#right#"+company.getCompanyData()[3] +", "+company.getCompanyData()[2],
				"#m#right#"+"NIP: "+company.getCompanyData()[5], "#m#right#"+"Tel.: "+company.getCompanyData()[4],
				"#m#right#"+company.getCompanyData()[1], "#m#nl#", "#m#center#"+"Zamówienie: ", "#m#nl#");
		for (String item : shopList) InitTools.addLabelLine(box, "Consolas", 14, item);
		InitTools.addLabelLine(box, "Consolas", 14, "Koszt dostawy - " + company.getCompanyData()[6] +" z³.", "#m#nl#",
				"----------------------------------------------", "Total:", "#m#bold#"+totalCost, realizationTime, 
				"("+endDate+"  "+endTime.format(DateTimeFormatter.ofPattern("HH:mm"))+")");
	}
	
	
    ///////////////////// OUTER METHODS /////////////////////////

		
	public void showTicket()
	{
		stage.show();
	}
	
	public void setButtonsOnTicket(int level)
	{
		bottomBox.getChildren().clear();
		for (int x = 0; x < level; x++) 
		{
			if (x > 2) break;
			bottomBox.getChildren().add(buttonsArray[x]);
			buttonsArray[x].prefWidthProperty().bind(this.widthProperty());
		}
	}
	
	public void setBackgroundOfTicket(Color color)
	{
		box.setBackground(new Background(new BackgroundFill(color, null, null)));
	}
	
	public void flashingBelt(Color color1, Color color2, Color txtColor1, Color txtColor2, boolean flaga)
	{
		if (flaga)
		{
			orderBelt.setBackground(new Background(new BackgroundFill(color1, new CornerRadii(5), null)));
			orderBelt.getTitleLabel().setTextFill(txtColor1);
			orderBelt.getTimeLeftLabel().setTextFill(txtColor1); 
		}
		else
		{
			orderBelt.setBackground(new Background(new BackgroundFill(color2, new CornerRadii(5), null)));
			orderBelt.getTitleLabel().setTextFill(txtColor2);
			orderBelt.getTimeLeftLabel().setTextFill(txtColor2); 
		}
	}	
	
	public void freezeOrder()
	{
		String endDateString = endDate.format(DateTimeFormatter.ofPattern("dd-MM-YY"));
		String endTimeString = endTime.format(DateTimeFormatter.ofPattern("HH:mm"));	
		orderBelt.setTimeLeft(endDateString+" "+endTimeString);
		orderBelt.setProgress(1);
	}
}















