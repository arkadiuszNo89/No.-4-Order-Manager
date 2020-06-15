package application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.Company;
import data.Order;
import data.Product;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import physics.General;
import tools.PaneData;
import tools.SaveCreator;
import tools.Timer;
import tools.SaveCreator.Mode;

public class TopBeltController {

    @FXML
    public HBox hBox;
    
    @FXML
    public ImageView icon_company;

    @FXML
    public ImageView icon_product;

    @FXML
    public ImageView icon_find;
    
    @FXML
    private ImageView icon_orderBoard;
    
    @FXML
    private ImageView icon_saveAs;

    @FXML
    private ImageView icon_save;

    @FXML
    private ImageView icon_load;
    
    @FXML
    private ImageView icon_add;  

    @FXML
    private Label infoLabel;
    
    
    public static TopBeltController controller;
    
    private SaveCreator saveCreator;
    private final int data_companySize = 8;
    private final int data_productSize = 5;
    private final int data_OrderSize = 9;
    private ImageView [] iconsTab;
    private Map <ImageView, String> iconsNames;
	private List <String> orderDataList;
	private List <String> shopList;
	ColorAdjust effect;
	public boolean addData;
    
    
    public void initialize()
    {
    	controller = this;
    	iconsTab = new ImageView [] {icon_orderBoard, icon_company, icon_product, icon_find, icon_saveAs, icon_save, icon_load, icon_add};
    	initIconsNamesMap();
    	saveCreator = new SaveCreator("OrderManager");
    	infoLabel.prefWidthProperty().bind(hBox.widthProperty());
    	effect = new ColorAdjust(0, 0, 0.9, 0); 
    	addData = false;
    	
    	icon_orderBoard.setOnMouseClicked( e ->
    	{
    		Main.root.setCenter(PaneData.get("Dynamic Board"));
    	});
    	
    	
    	icon_company.setOnMouseClicked( e ->
    	{
    		Main.root.setCenter(PaneData.get("Companies"));
    	});
    	
    	
    	icon_product.setOnMouseClicked( e ->
    	{
    		Main.root.setCenter(PaneData.get("Products"));
    	});
    	
    	
    	icon_saveAs.setOnMouseClicked( e ->
    	{
    		saveData(Mode.SAVE_AS);
    	});
    	
    	
    	icon_save.setOnMouseClicked( e ->
    	{
    		saveData(Mode.SAVE);
    	});
    	
    	
    	icon_load.setOnMouseClicked( e ->
    	{
    		List <String> total = saveCreator.act(Mode.LOAD);
    		
    		if (total != null)
    		{
        		General.clearLists();    		
        		loadData(total);
        		if (saveCreator.getStatus()) General.showInfo("Za³adowano dane");
    		}
    	});
    	
    	
    	icon_add.setOnMouseClicked( e ->
    	{
    		List <String> total = saveCreator.act(Mode.LOAD);
    		
    		if (total != null) 
    			{
    				addData = true;
    				loadData(total);
    				if (saveCreator.getStatus()) General.showInfo("Dodano dane");
    			}
    	});
    	
    	icon_find.setOnMouseClicked( e -> 
    	{
    		Main.root.setCenter(PaneData.get("Archive"));
    	});
    	
    	for (ImageView icon : iconsTab)
    	{
    		icon.setOnMouseEntered ( e -> 
    		{
    			icon.setEffect(effect);
    			General.pauseTheClock(true);
    			setInfoLabel(iconsNames.get(e.getSource()));
    		});
    		icon.setOnMouseExited ( e -> 
    		{
    			icon.setEffect(null);
    			General.pauseTheClock(false);
    		});
    	}
    }
    
    private void saveData (SaveCreator.Mode mode)
    {	
    	// # 1
		List <String> total = new ArrayList <String> ();    		
		for (Company company : General.listOfCompanies.values()) total.addAll(Arrays.asList(company.getCompanyData()));
		
		saveCreator.setObjectType("Company", 8);
		saveCreator.act(mode, total);
		
		// # 2
		total = new ArrayList <String> (); 
		for (Company company : General.listOfCompanies.values())
			for (Product product : company.getCompanyProducts().values())
			{
				total.add(company.toString());
				total.addAll(Arrays.asList(product.getProductData()));
			}
		
		saveCreator.setObjectType("Product", 5);
		saveCreator.act(Mode.ADD_TO_SAVE_FILE, total);
		
		// # 3
		for (Order order : General.activeOrdersList.keySet())
		{
			saveCreator.setObjectType("ActiveOrder", order.getStringData().size());
			saveCreator.act(Mode.ADD_TO_SAVE_FILE, order.getStringData());
		}
		
		// # 4
		total = new ArrayList <String> (); 
		for (Order order : General.archiveOrdersList.keySet())
		{
			saveCreator.setObjectType("ArchOrder", order.getStringData().size());
			saveCreator.act(Mode.ADD_TO_SAVE_FILE, order.getStringData());
		}
		

		
		if (saveCreator.getStatus()) General.showInfo("Zapisano stan");
    }
    
    private void loadData (List <String> total)
    {
    	SaveCreator.loadSupportMode = true;
		Company company = null;
    	boolean flagaCompany = false;
		boolean flagaProduct = false;
		boolean flagaActiveOrder = false;
		boolean flagaArchOrder = false;
		int counter = 0;
		
		for (int x = 0; x < total.size(); x++)
		{
			if (flagaCompany)
			{
				CompaniesController.controller.setTextField(total.get(x), counter);
				counter++;
				if (counter == data_companySize)
				{
					counter = 0;
					flagaCompany = false;
					CompaniesController.controller.addProduct_step2_action.handle(null);
				}
			}
			else if (flagaProduct)
			{
				if (counter == 0) company = General.listOfCompanies.get(total.get(x));
				if (counter > 0) ProductsController.controller.setTextField(total.get(x), counter-1);			
				
				counter++;
				if (counter == data_productSize)
				{
					counter = 0;
					flagaProduct = false;
					ProductsController.controller.getListViewCompanies().getSelectionModel().select(company);
					ProductsController.controller.addProduct_step2_action.handle(null);
				}
			}
			else if (flagaActiveOrder || flagaArchOrder)
			{
				if (counter == 0)
				{
					orderDataList = new ArrayList <String> ();
					shopList = new ArrayList <String> ();
				}
				if (counter < data_OrderSize) 
				{
					orderDataList.add(total.get(x));
				}
				else if (!total.get(x).equals("#end"))
				{
					shopList.add(total.get(x));
				}
				if (total.get(x).equals("#end"))
				{
					Order order = new Order(orderDataList, shopList);
					if (flagaActiveOrder) General.addOrder(order);
					if (flagaArchOrder) 
						{
							General.moveToArchive(order, true);									
							order.freezeOrder();
						}
					counter = 0;
					flagaActiveOrder = false;
					flagaArchOrder = false;
					continue;
				}
				counter++;
			}
			
		if (total.get(x).equals("#Company")) flagaCompany = true;
		else if (total.get(x).equals("#Product")) flagaProduct = true;
		else if (total.get(x).equals("#ActiveOrder") && !addData) flagaActiveOrder = true;
		else if (total.get(x).equals("#ArchOrder") && !addData) flagaArchOrder = true;
		}
		CompaniesController.controller.setPrimalState();
		ProductsController.controller.setPrimalState();
		SaveCreator.loadSupportMode = false;
    }
    
    private void initIconsNamesMap()
    {
    	iconsNames = new HashMap<>();
    	iconsNames.put(icon_orderBoard, "Aktywne zamówienia");
    	iconsNames.put(icon_company, "Dostawcy");
    	iconsNames.put(icon_product, "Produkty");
    	iconsNames.put(icon_find, "Archiwum");
    	iconsNames.put(icon_saveAs, "Zapisz jako");
    	iconsNames.put(icon_save, "Zapisz");
    	iconsNames.put(icon_load, "Za³aduj dane");
    	iconsNames.put(icon_add, "Dodaj dane");
    }
    
    
    ///////////////////// PUBLIC METHODS /////////////////////////
    
    
    public void setInfoLabel(String txt)
    {
    	infoLabel.setText(txt);
    }
    
    public ImageView [] getIcons()
    {
    	return iconsTab;
    }
}







