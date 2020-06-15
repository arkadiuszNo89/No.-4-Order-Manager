package application;

import java.util.Map;
import java.util.TreeSet;

import data.Company;
import data.Order;
import data.Product;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import physics.General;
import tools.InitTools;
import tools.PaneData;

public class CompaniesController {

    @FXML
    private ListView <Company> listView;

    @FXML
    private TextField txt_companyName;

    @FXML
    private TextField txt_email;

    @FXML
    private TextField txt_NIP;

    @FXML
    private TextField txt_contactNumber;

    @FXML
    private TextField txt_city;

    @FXML
    private TextField txt_country;

    @FXML
    private TextField txt_deliveryTime;
    
    @FXML
    private TextField txt_deliveryPrice;

    @FXML
    private Button but_shoppingBag;

    @FXML
    private Button but_edit;

    @FXML
    private Button but_done;

    @FXML
    public Button but_add;
    
    @FXML
    private Button but_cancelOrder;
    
    @FXML
    private Label label_realizationTime;

    @FXML
    private Label label_cost;
     
    
    public static CompaniesController controller;
    
    private TextField [] listOfTextFields;
    public EventHandler <ActionEvent> addProduct_step1_action, cancel_action, addProduct_step2_action, 
    	edit_step1_action, edit_step2_action, delete_action;
    private boolean flag_editProductMode, flag_orderInLine;
    private Order orderInLine;
    									
    
    
    public void initialize()
    {
    	controller = this;
    	listOfTextFields = new TextField [] {txt_companyName, txt_email, txt_country, txt_city, 
			txt_contactNumber, txt_NIP, txt_deliveryPrice, txt_deliveryTime};
    	flag_editProductMode = false;
    	flag_orderInLine = false;
    	but_cancelOrder.setVisible(false);
    	
    	initActions();
    	initTextFields();
    	primalState();
    }
    
    private void initActions()
    {	    	
    	addProduct_step1_action = e ->
    	{
    		clearTextFields();
    		for (TextField field : listOfTextFields) field.setEditable(true);
    		listView.setDisable(true);
    		but_add.setText("Zapisz");
    		but_edit.setText("Anuluj");
        	but_add.setOnAction(addProduct_step2_action);
        	but_edit.setOnAction(cancel_action);
        	but_shoppingBag.setDisable(true);
        	but_done.setDisable(true);
           	InitTools.setVisables(false, TopBeltController.controller.getIcons(), TopBeltController.controller.icon_company);
    	};
 
    	addProduct_step2_action = e ->
    	{
    		int aloudMaster = 0;
    		for (TextField field : listOfTextFields) 
    			{
    				if (!field.getText().isEmpty()) aloudMaster++;
    				else General.showInfo("Uzupe³nij wszystkie pola");
    			}
    		
    		if (aloudMaster == 8)
    		{
    			Company company = new Company(listOfTextFields);
    			
    			boolean compareExists = true;
    			for (Company item : General.listOfCompanies.values()) 
    				if (item.toString().toLowerCase().equals(company.toString().toLowerCase())) compareExists = false;	

    			if (!compareExists) General.showInfo("Dostawca ju¿ istnieje");
    			else
    			{
    	    		General.listOfCompanies.put(company.toString(), company);
    	    		listView.getItems().clear();
    	    		listView.getItems().addAll(General.listOfCompanies.values());
    	    		primalState();
    			}
    		}	
    	};
    	
    	cancel_action = e ->
    	{
    		primalState();
    	};
    	 	
    	edit_step1_action = e ->
    	{
    		if (listView.getSelectionModel().getSelectedItem() != null)
    		{
	    		but_add.setText("Usuñ");
	    		but_edit.setText("Zapisz");
	        	but_add.setOnAction(delete_action);
	        	but_edit.setOnAction(edit_step2_action); 
	        	but_shoppingBag.setDisable(true);
	        	but_done.setDisable(true);
	        	flag_editProductMode = true;
	        	for (TextField field : listOfTextFields) 
	        	{
	        		if (field.getId().equals("txt_companyName")) continue;
	        		field.setEditable(true);	        		
	        	}
	        	InitTools.setVisables(false, TopBeltController.controller.getIcons(), TopBeltController.controller.icon_company);
    		}
    		else General.showInfo("Wska¿ dostawcê");
    	};    	
    	
    	edit_step2_action = e ->
    	{
    		listView.getSelectionModel().getSelectedItem().setCompanyData(listOfTextFields);	
    		primalState();
    	};    
    	
    	delete_action = e ->
    	{
    		Company company = listView.getSelectionModel().getSelectedItem();  		
    		
    		for (Product product : company.getCompanyProducts().values())
    		{
    			General.mapOfProduct_for_company.get(product).remove(company);
        		if (General.mapOfProduct_for_company.get(product).isEmpty()) 
        		{
        			General.mapOfProduct_for_company.remove(product);
        		}	
    		}	    		
	    		flag_editProductMode = false;
	    		General.listOfCompanies.remove(company.toString());
    			ProductsController.controller.refreshListsView();
	    		listView.getItems().clear();
	    		listView.getItems().addAll(General.listOfCompanies.values());
	    		primalState();
    	};    	
    	
    /*
   	 * 		Final events
   	 */
    	
    	listView.setOnMouseClicked( e ->
    	{
    		if (listView.getSelectionModel().getSelectedItem() != null)
    		{
	    		Company company = listView.getSelectionModel().getSelectedItem();
	    		setTextFields(company.getCompanyData());
    		}
    	});
    	
    	but_shoppingBag.setOnAction(e ->
    	{
    		if (listView.getSelectionModel().getSelectedItem() != null) 
    			{
        			Main.root.setCenter(PaneData.get("Order Panel"));
        			InitTools.setVisables(false, TopBeltController.controller.getIcons());
        			if (!flag_orderInLine) OrderPanelController.controller.entry(listView.getSelectionModel().getSelectedItem());
    			}
    		else General.showInfo("Nie wybrano dostawcy");
    	});
    	
    	but_done.setOnAction( e -> 
    	{
    		if (flag_orderInLine)
    		{
        		General.addOrder(orderInLine);
        		orderInLine.showTicket();
        		expendOrder_secondGate(null);
        		General.showInfo("Dodano zamówienie");
    		}
    		else General.showInfo("Brak zamówienia");

    	});
    	
    	but_cancelOrder.setOnAction( e -> 
    	{
    		expendOrder_secondGate(null);
    	});
    }
    
    private void initTextFields()
    {
    	InitTools.textFieldSetter(30, txt_companyName, txt_email);
    	InitTools.textFieldSetter(16, txt_country, txt_city);
    	InitTools.textFieldSetter(13, true, txt_contactNumber);
    	InitTools.textFieldSetter(10, true, txt_NIP, txt_deliveryPrice);
    	InitTools.textFieldSetter(3, true, txt_deliveryTime);
    }
    
    private void primalState()
    {
		listView.setDisable(false);
		if (!flag_editProductMode)
		{
			listView.getSelectionModel().clearSelection();
			clearTextFields();
		}
		flag_editProductMode = false;
		for (TextField field : listOfTextFields) field.setEditable(false);
		but_add.setText("Dodaj");
		but_edit.setText("Edytuj");
    	but_add.setOnAction(addProduct_step1_action);
    	but_edit.setOnAction(edit_step1_action);
    	but_shoppingBag.setDisable(false);
    	but_done.setDisable(false);
    	InitTools.setVisables(true, TopBeltController.controller.getIcons());
    }
    
    ///////////////////// OUTER METHODS /////////////////////////
    
    public void setTextField(String text, int pos)
    {
    	if (pos < 8) listOfTextFields[pos].setText(text);
    }
    
    public void setTextFields(String [] containerOfVals)
    {
    	if (containerOfVals.length == 8)
    	{
    		for (int x = 0; x < 8; x++) listOfTextFields[x].setText(containerOfVals[x]); 
    	}
    }
    
    public void refreshListView()
    {
    	listView.getItems().clear();
    	listView.getItems().addAll(General.listOfCompanies.values());
    }
    
    public void clearTextFields()
    {
    	for (TextField field : listOfTextFields) field.clear();
    }
    
    public void clearListView() 
    {
    	listView.getItems().clear();
    }
    
    public void setPrimalState()
    {
    	primalState();
    }
    
    public void expendOrder_secondGate(Order order)
    {
    	if (order != null)
    	{
    		orderInLine = order;
    		label_cost.setText(order.getTotalCost());
    		label_realizationTime.setText(order.getRealizationTime());
    		flag_orderInLine = true;
    		but_cancelOrder.setVisible(true);
    	}
    	else 
    	{
    		orderInLine = null;
    		label_cost.setText("0 z³");
    		label_realizationTime.setText("0 dni");
    		flag_orderInLine = false;
    		but_cancelOrder.setVisible(false);
    		OrderPanelController.controller.primalState();
    	}
    }
}












