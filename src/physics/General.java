package physics;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import application.CompaniesController;
import application.DynamicBoard;
import application.OrderBelt;
import application.ProductsController;
import application.TopBeltController;
import data.Company;
import data.Product;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import data.Order;
import tools.PaneData;
import tools.Timer;

public class General 
{
	public static Timer timer;
	private static Timer.TimeEvent timeEvent1, timeEvent2, timeEvent3, timeEvent4;
	private static boolean flagInfo, flash, clockPause;
	public static boolean flagColapse = false;
	private static int counterInfo;
	private static LinkedList <Order> lineOfDeadOrders;
	
	public static Map <String, Company> listOfCompanies;
	public static Map <Product, Set<Company>> mapOfProduct_for_company; 
	public static Map <Order, OrderBelt> activeOrdersList;
	public static Map <Order, OrderBelt> archiveOrdersList;
	
	public General()
	{
		listOfCompanies = new TreeMap <> (new Comparator <String> ()
				{
					@Override
					public int compare(String o1, String o2) {
						if (o1.toLowerCase().equals(o2.toLowerCase())) return 0;
						return o1.toLowerCase().compareTo(o2.toLowerCase());
					}
				});	
		mapOfProduct_for_company = new TreeMap <> ();
		activeOrdersList = new TreeMap <> ();
		archiveOrdersList = new TreeMap <> ();
		lineOfDeadOrders = new LinkedList <> ();
		
		addPanes();
		createTimeEvents();
		TopBeltController.controller.setInfoLabel("Order Manager");
		flagInfo = false;
		flash = false;
		clockPause = false;
				
		timer = new Timer(1000);
		timer.addTimeEvent(timeEvent1, timeEvent2, timeEvent3, timeEvent4);
		timer.play(true);
	}
	
	private void addPanes()
	{
		try
		{
			PaneData.addFXML("Top Belt", "/application/TopBeltView.fxml");
			PaneData.addPane("Dynamic Board", new DynamicBoard());
			PaneData.addPane("Archive", new DynamicBoard());
			PaneData.addFXML("Companies", "/application/CompaniesView.fxml");
			PaneData.addFXML("Products", "/application/ProductsView.fxml");	
			PaneData.addFXML("Order Panel", "/application/OrderPanelView.fxml");	
		}
		catch (Exception e)
		{
			
		}
	}
	
	private void createTimeEvents()
	{
		timeEvent1 = (Timer timer) ->
		{
			if (!clockPause)
			{
				if (!flagInfo) TopBeltController.controller.setInfoLabel(timer.getDate()+"   "+timer.getTime());
				else counterInfo--;
				if (counterInfo < 1) flagInfo = false;
			}
		};
		
		timeEvent2 = (Timer timer) ->
		{
			for (Order order : activeOrdersList.keySet())
			{
				if (!lineOfDeadOrders.contains(order)) 
				{
					order.getOrderBelt().setProgress(timer.progressCounter(order.getMinutesLong(), order.getEndDate(), order.getEndTime()));
					order.getOrderBelt().setTimeLeft(timer.timeLeft(order.getEndDate(), order.getEndTime()));
				}
				
				if (order.getEndDate().isEqual(LocalDate.now()) && LocalTime.now().isAfter(order.getEndTime())
						|| LocalDate.now().isAfter(order.getEndDate()))
				{
					if (!lineOfDeadOrders.contains(order)) 
					{
						lineOfDeadOrders.add(order);
						order.freezeOrder();
					}
					if (checkIfTheButtonsCorrect(order, true)) order.setButtonsOnTicket(3);
					else if (checkIfTheButtonsCorrect(order, false)) order.setButtonsOnTicket(2);
				}
			}
		};
		
		timeEvent3 = (Timer timer) ->
		{
			if (flagColapse)
			{
				while (lineOfDeadOrders.peek() != null)
				{
					moveToArchive(lineOfDeadOrders.poll(), false);
				}
				flagColapse = false;
			}	
		};
		
		timeEvent4 = (Timer timer) ->
		{
			if (flash) flash = false;
			else flash = true;
			for (Order order : lineOfDeadOrders)
			{
				order.flashingBelt(Color.YELLOW, Color.BLACK, Color.BLACK, Color.WHITE, flash);
			}
		};
	}
	
	private boolean checkIfTheButtonsCorrect(Order order, boolean mode)
	{
		if (mode) return order.getBottomBox().getChildren().size() < 3 && lineOfDeadOrders.size() > 1;
		else return order.getBottomBox().getChildren().size() == 3 && lineOfDeadOrders.size() == 1;
	}
	
	///////////////////// PUBLIC METHODS /////////////////////////
	
	public static LinkedList <Order> getLineOfDeadOrders()
	{
		return lineOfDeadOrders;
	}
	
	public static void clearLists()
	{
		listOfCompanies.clear();
		mapOfProduct_for_company.clear();
		activeOrdersList.clear();
		archiveOrdersList.clear();;
		CompaniesController.controller.clearListView();
		ProductsController.controller.clearListsView();
		((DynamicBoard)PaneData.get("Dynamic Board")).refresh(activeOrdersList.values());
		((DynamicBoard)PaneData.get("Archive")).refresh(archiveOrdersList.values());
	}
	
	public static void showInfo(String txt)
	{
		TopBeltController.controller.setInfoLabel(txt);
		flagInfo = true;
		counterInfo = 3;
	}
	
	public static void addOrder(Order order)
	{
		activeOrdersList.put(order, order.getOrderBelt());
		order.setButtonsOnTicket(2);
		((DynamicBoard)PaneData.get("Dynamic Board")).refresh(activeOrdersList.values());
	}
	
	public static void moveToArchive(Order order, boolean loading)
	{
		if (!loading)
		{
			activeOrdersList.remove(order);
			((DynamicBoard)PaneData.get("Dynamic Board")).refresh(activeOrdersList.values());
			if (lineOfDeadOrders.contains(order)) lineOfDeadOrders.remove(order);
		}
		order.flagArchive = true;
		archiveOrdersList.put(order, order.getOrderBelt());
		order.setButtonsOnTicket(1);
		order.setBackgroundOfTicket(Color.LIGHTGOLDENRODYELLOW);
		order.flashingBelt(null, null, Color.BLACK, Color.BLACK, flash);
		((DynamicBoard)PaneData.get("Archive")).refresh(archiveOrdersList.values());
	}
	
	public static void deleteOrder(Order order)
	{
		if (activeOrdersList.containsKey(order)) activeOrdersList.remove(order);
		if (archiveOrdersList.containsKey(order)) archiveOrdersList.remove(order);
		if (lineOfDeadOrders.contains(order)) lineOfDeadOrders.remove(order);
		
		((DynamicBoard)PaneData.get("Dynamic Board")).refresh(activeOrdersList.values());
		((DynamicBoard)PaneData.get("Archive")).refresh(archiveOrdersList.values());
	}
	
	public static void pauseTheClock(boolean state)
	{
		clockPause = state;
	}
}





