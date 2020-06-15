package application;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import data.Company;
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

public class ProductsController {

    @FXML
    private ListView <Product> listView;

    @FXML
    private TextField txt_productName;

    @FXML
    private TextField txt_leadTime;

    @FXML
    private TextField txt_minQty;

    @FXML
    private TextField txt_price;

    @FXML
    private Button but_edit;
    
    @FXML
    private Button but_add;

    @FXML
    private ListView <Company> listView_companies;

    @FXML
    private Label label_delPrice;
    
    
    public static ProductsController controller;
    private TextField [] listOfTextFields;
    public EventHandler <ActionEvent> addProduct_step1_action, addProduct_step2_action, 
    		cancel_action, edit_step1_action, edit_step2_action, delete_action;
    private boolean flag_addProductMode;
    private boolean flag_editProductMode;
    
    
    public void initialize()
    {
    	controller = this;
    	listOfTextFields = new TextField [] {txt_productName, txt_price, txt_leadTime, txt_minQty};
    	flag_editProductMode = false;
    	
    	initFields();
    	addActions();
    	primalState();
    }

    private void addActions()
    {
    	/*
    	 * 		EventHandlers
    	 */
    	addProduct_step1_action = e ->
    	{
    		clearTextFields();
    		for (TextField field : listOfTextFields) field.setEditable(true);
    		listView_companies.getItems().clear();
    		listView_companies.getItems().addAll(General.listOfCompanies.values());
    		listView.setDisable(true);
    		but_add.setText("Zapisz");
    		but_edit.setText("Anuluj");
        	but_add.setOnAction(addProduct_step2_action);
        	but_edit.setOnAction(cancel_action);
        	flag_addProductMode = true;
        	InitTools.setVisables(false, TopBeltController.controller.getIcons(), TopBeltController.controller.icon_product);
    	};
    	
    	addProduct_step2_action = e ->
    	{
    		int aloudMaster = 0;
    		for (TextField field : listOfTextFields) 
    			{
    				if (!field.getText().isEmpty()) aloudMaster++;
    				else General.showInfo("Uzupe³nij wszystkie pola");
    			}
    		if (listView_companies.getSelectionModel().getSelectedItem() != null) aloudMaster++;
    		else General.showInfo("Wybierz dostawcê");
    		
    		if (aloudMaster == 5)
    		{
    			Company company = listView_companies.getSelectionModel().getSelectedItem();
    			Product product = new Product(listOfTextFields); 	

    			boolean compareExists = true;
    			for (String item : company.getCompanyProducts().keySet()) 
    				if (item.toLowerCase().equals(product.toString().toLowerCase())) compareExists = false;	

    			if (!compareExists) General.showInfo("Dostawca ju¿ go posiada");
    			else
    			{
    				company.addProduct(product);
    	    		if (General.mapOfProduct_for_company.containsKey(product))
    	    		{
    	    			General.mapOfProduct_for_company.get(product).add(company);
    	    		}
    	    		else 
    	    		{
    	    			General.mapOfProduct_for_company.put(product, new TreeSet <> ());
    	    			General.mapOfProduct_for_company.get(product).add(company);
        	    		listView.getItems().clear();
        	    		listView.getItems().addAll(General.mapOfProduct_for_company.keySet());
    	    		}
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
    		if (listView.getSelectionModel().getSelectedItem() != null && listView_companies.getSelectionModel().getSelectedItem() != null)
    		{
	    		but_add.setText("Usuñ");
	    		but_edit.setText("Zapisz");
	        	but_add.setOnAction(delete_action);
	        	but_edit.setOnAction(edit_step2_action); 
	        	flag_editProductMode = true;
	        	for (TextField field : listOfTextFields) 
	        	{
	        		if (field.getId().equals("txt_productName")) continue;
	        		field.setEditable(true);	        		
	        	}
	        	InitTools.setVisables(false, TopBeltController.controller.getIcons(), TopBeltController.controller.icon_product);
    		}
    		else General.showInfo("Wska¿ wybrany produkt");
    	};    	
    	
    	edit_step2_action = e ->
    	{
    		Company company = listView_companies.getSelectionModel().getSelectedItem();
    		Product product = listView.getSelectionModel().getSelectedItem(); 		
    		company.getCompanyProducts().get(product.toString()).setProductData(listOfTextFields);
    		primalState();
    	};    
    	
    	delete_action = e ->
    	{
    		Company company = listView_companies.getSelectionModel().getSelectedItem();
    		Product product = listView.getSelectionModel().getSelectedItem();
    		
    		company.getCompanyProducts().remove(product.toString());
    		General.mapOfProduct_for_company.get(product).remove(company);
    		if (General.mapOfProduct_for_company.get(product).isEmpty()) 
    		{
    			General.mapOfProduct_for_company.remove(product);
	    		listView.getItems().clear();
	    		listView.getItems().addAll(General.mapOfProduct_for_company.keySet());
	    		flag_editProductMode = false;
	    		primalState();
    		}
    		else
    		{
    			primalState();
    			listView.getOnMouseClicked().handle(null);
    		}
    	};    	
    	
    	/*
    	 * 		Final events
    	 */
    	
    	listView.setOnMouseClicked( e ->
    	{
    		if (listView.getSelectionModel().getSelectedItem() != null)
    		{
	    		listView_companies.getItems().clear();
	    		listView_companies.getItems().addAll(General.mapOfProduct_for_company.get(listView.getSelectionModel().getSelectedItem()));
    		}
    	});
    	
    	listView_companies.setOnMouseClicked( e ->
    	{
    		if (listView_companies.getSelectionModel().getSelectedItem() != null && !flag_addProductMode
    				&& listView.getSelectionModel().getSelectedItem() != null)
    		{
    			Map <String, Product> mapa = listView_companies.getSelectionModel().getSelectedItem().getCompanyProducts();
    			Product product = mapa.get(listView.getSelectionModel().getSelectedItem().toString());
    			setTextFields(product.getProductData());
    		}
    	});
    }
    
    private void primalState()
    {
		listView.setDisable(false);
		if (!flag_editProductMode)
		{
			listView_companies.getItems().clear();
			listView.getSelectionModel().clearSelection();
			clearTextFields();
		}
		flag_editProductMode = false;
		flag_addProductMode = false;
		for (TextField field : listOfTextFields) field.setEditable(false);
		but_add.setText("Dodaj");
		but_edit.setText("Edytuj");
    	but_add.setOnAction(addProduct_step1_action);
    	but_edit.setOnAction(edit_step1_action);
    	InitTools.setVisables(true, TopBeltController.controller.getIcons());
    }
    
    private void initFields()
    {
    	InitTools.textFieldSetter(30, txt_productName);
    	InitTools.textFieldSetter(10, true, txt_price);
    	InitTools.textFieldSetter(3, true, txt_leadTime);
    	InitTools.textFieldSetter(7, true, txt_minQty);
    }
    
    ///////////////////// OUTER METHODS /////////////////////////
    
    public void clearListsView()
    {
    	listView.getItems().clear();
    	listView_companies.getItems().clear();
    }
    
    public void refreshListsView()
    {
		listView.getItems().clear();
		listView.getItems().addAll(General.mapOfProduct_for_company.keySet());
		listView_companies.getItems().clear();
    }
    
    public ListView <Product> getListView()
    {
    	return listView;
    }
    
    public ListView <Company> getListViewCompanies()
    {
    	return listView_companies;
    }
    
    public void clearTextFields()
    {
    	for (TextField field : listOfTextFields) field.clear();
    }
    
    public void setPrimalState()
    {
    	primalState();
    }
    
    public void setTextFields(String [] containerOfVals)
    {
    	if (containerOfVals.length == 4)
    	{
    		for (int x = 0; x < 4; x++) listOfTextFields[x].setText(containerOfVals[x]);
    	}
    }
    
    public void setTextField(String text, int pos)
    {
    	if (pos < 4) listOfTextFields[pos].setText(text);
    }
}




