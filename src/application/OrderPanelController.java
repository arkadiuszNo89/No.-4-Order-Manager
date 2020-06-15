package application;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import data.Company;
import data.Order;
import data.Product;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import physics.General;
import tools.InitTools;
import tools.PaneData;

public class OrderPanelController {

    @FXML
    private ListView <String> listView_toBuy;

    @FXML
    private ListView <Product> listView_products;

    @FXML
    private TextField txt_orderName;

    @FXML
    private TextField txt_Qty;    

    @FXML
    private TextField txt_endHour;

    @FXML
    private Button but_add;

    @FXML
    private Button but_delete;

    @FXML
    private Label label_Cost;

    @FXML
    private Label label_DeliveryCost;

    @FXML
    private Label label_RealizationTime;

    @FXML
    private Label label_DeliveryTime;

    @FXML
    private Label label_LeadTime;

    @FXML
    private Label label_MinQty;

    @FXML
    private Label label_Price;

    @FXML
    private Button but_confirm;

    @FXML
    private Button but_cancel;
    
    
    public static OrderPanelController controller;
    private Company company;
    private int endHour;
    private int deliveryTime;
    private int totalLeadTime;
    private int totalRealizationTime;
    private long totalCost;
    private Map <Product, Long> mapOfProduct_for_Qty;
    private Map <Product, String> mapOfProduct_for_String;
    
    
    public void initialize()
    {
    	controller = this;
        mapOfProduct_for_Qty = new HashMap<>();
        mapOfProduct_for_String = new TreeMap<>();
    	primalState();
    	initActions();
    	
    	InitTools.textFieldSetter(30, txt_orderName);
    	InitTools.textFieldSetter(10, true, txt_Qty);
    }
    
    private void initActions()
    {
    	but_cancel.setOnAction( e -> 
    	{
			Main.root.setCenter(PaneData.get("Companies"));
			primalState();
    	});
    	
    	but_add.setOnMouseClicked( e -> 
    	{
    		if (listView_products.getSelectionModel().getSelectedItem() != null)
    		{
    			Product product = listView_products.getSelectionModel().getSelectedItem();
    			long qty = Long.parseLong(txt_Qty.getText());
    			long price = Long.parseLong(product.getProductData()[1]);
    			int leadTime = Integer.parseInt(product.getProductData()[2]);
    			
    			if (qty >= Integer.parseInt(product.getProductData()[3]))
    			{			
    				if (mapOfProduct_for_Qty.containsKey(product))
    				{
    					long oldQty = mapOfProduct_for_Qty.get(product);
    					setFinalOrderConditions(0, -(oldQty * price));
    					qty += oldQty;
    					listView_toBuy.getItems().remove(mapOfProduct_for_String.get(product));
    					mapOfProduct_for_Qty.remove(product);
    					mapOfProduct_for_String.remove(product);
    				}   				
        			long cost = qty * price;
    				String txtPosition = product.toString()+"  "+qty+"szt. - "+cost+" z³";
    				mapOfProduct_for_Qty.put(product, qty);
    				mapOfProduct_for_String.put(product, txtPosition);
    				listView_toBuy.getItems().clear();
    				listView_toBuy.getItems().addAll(mapOfProduct_for_String.values());
    				setFinalOrderConditions(leadTime, cost);
    			}
    			else General.showInfo("Za ma³a iloœæ");
    		}
    		else General.showInfo("Wybierz produkt");
    	});
    	
    	but_delete.setOnMouseClicked( e -> 
    	{
    		if (listView_toBuy.getSelectionModel().getSelectedItem() != null)
    		{
    			String onList = listView_toBuy.getSelectionModel().getSelectedItem();
    			Product product = null;    	
    			for (Entry<Product, String> entry : mapOfProduct_for_String.entrySet()) 
    			{
    				if (entry.getValue().equals(onList)) product = entry.getKey();
    			}
    			long minusCost = -(Long.parseLong(product.getProductData()[1]) * mapOfProduct_for_Qty.get(product));
    			 			
	    		mapOfProduct_for_Qty.remove(product);
	    		mapOfProduct_for_String.remove(product);
	    		listView_toBuy.getItems().remove(onList);
	    		setFinalOrderConditions(-1, minusCost);
	    		for (Product item : mapOfProduct_for_Qty.keySet()) setFinalOrderConditions(Integer.parseInt(item.getProductData()[2]), 0);
    		}
    	});
    	
    	but_confirm.setOnMouseClicked( e -> 
    	{
    		int counter = 0;
    		if (txt_orderName.getText().isEmpty()) General.showInfo("Nie nadano nazwy");
    		else counter++;
    		if (listView_toBuy.getItems().isEmpty()) General.showInfo("Lista zakupów jest pusta");
    		else counter++;

    		if (counter == 2)
    		{
    			LocalDate dateEnd = LocalDate.now().plusDays(totalRealizationTime);
    			LocalTime timeEnd = LocalTime.parse(endHour<10 ? "0"+endHour+":00" : endHour+":00");
    			List <String> shopList = new ArrayList <String> ();
    			shopList.addAll(mapOfProduct_for_String.values());
    			
    			Order order = new Order(txt_orderName.getText(), company,dateEnd, timeEnd, 
    					label_Cost.getText(), label_RealizationTime.getText(), shopList );
    			CompaniesController.controller.expendOrder_secondGate(order);
    			Main.root.setCenter(PaneData.get("Companies"));
    		}
    	});
    	
    	listView_products.setOnMouseClicked( e->
    	{
    		if (listView_products.getSelectionModel().getSelectedItem() != null)
    		{
    			Product product = listView_products.getSelectionModel().getSelectedItem();
    			label_Price.setText(product.getProductData()[1] + " z³");
    			label_LeadTime.setText(product.getProductData()[2] + " dni");
    			label_MinQty.setText(product.getProductData()[3] + " szt");
    			txt_Qty.setText(product.getProductData()[3]);
    		}
    	});
    	
    	
//    	Focus listener i change text listener do manipulacji godzin¹ zakoñczenia
    	
    	txt_endHour.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) 
			{
				try
				{
					if (!txt_endHour.getText().isEmpty() && txt_endHour.isFocused()) 
						{							
							int hour = Integer.parseInt(newValue);
							if (hour >= 0 && hour < 24) 
							{
								txt_endHour.setText(newValue);
								endHour = hour;
							}
							else txt_endHour.setText(endHour+"");
						}
				}
				catch (Exception e)
				{
					txt_endHour.setText(oldValue);
				}
			}
		});
    	
    	txt_endHour.focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean newFocus) {
				
				if (newFocus)
				{
					txt_endHour.setText(endHour+"");
				}
				else
				{
					if (endHour < 10) txt_endHour.setText("0"+endHour+":00");
					else txt_endHour.setText(endHour+":00");
				}
			}
		});
    }
    
    public void primalState()
    {
    	InitTools.setVisables(true, TopBeltController.controller.getIcons());
    	txt_orderName.clear();
    	txt_Qty.clear();
    	listView_products.getItems().clear();
    	listView_toBuy.getItems().clear();
        mapOfProduct_for_Qty.clear();
        mapOfProduct_for_String.clear();
    	company = null;
    	
    	deliveryTime = 0;
    	totalRealizationTime = 0;
    	totalLeadTime = 0;
    	totalCost = 0;
    	endHour = 16;
    	txt_endHour.setText("16:00");
    	
		label_Cost.setText("0 z³");
		label_DeliveryCost.setText("0 z³");
		label_RealizationTime.setText("0 dni");
		label_DeliveryTime.setText("0 dni");
		label_LeadTime.setText("0 dni");
		label_MinQty.setText("0 szt");
		label_Price.setText("0 z³");
    }
    
    private void setFinalOrderConditions(int time, long cost)
    {
    	if (time == -1) totalLeadTime = 0;
    	totalLeadTime = (time > totalLeadTime ? time : totalLeadTime);
    	totalRealizationTime = deliveryTime + totalLeadTime;
    	totalCost += cost;
    	
    	label_RealizationTime.setText(totalRealizationTime != 1 ? totalRealizationTime+" dni" : totalRealizationTime+" dzieñ");
    	label_Cost.setText(totalCost + " z³");
    }

    public void entry(Company company)
    {
    	this.company = company;
    	deliveryTime = Integer.parseInt(company.getCompanyData()[7]);
    	
    	label_DeliveryCost.setText(company.getCompanyData()[6] + " z³");
    	label_DeliveryTime.setText(deliveryTime != 1 ? deliveryTime+" dni" : deliveryTime+" dzieñ");
    	listView_products.getItems().addAll(company.getCompanyProducts().values());
    	  	
    	setFinalOrderConditions(0, Integer.parseInt(company.getCompanyData()[6]));
    }
    
    public OrderBelt getOrderBelt()
    {
    	return this.getOrderBelt();
    }
}







