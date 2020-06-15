package tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class SaveCreator 
{
	public static enum Mode 
	{
		SAVE, SAVE_AS, LOAD, ADD_TO_SAVE_FILE
	}
	private String titleOfProject;
	private String typeDescription;
	private int incrementNumber;
	private int incrementCounter;
	private FileChooser fileChooser;
	private File file;
	private boolean addingFlag;
	private boolean status;
	public static boolean loadSupportMode = false;
	public Map <String, Integer> objectTypes;
		
	
	public SaveCreator (String titleOfProject)
	{
		this.titleOfProject = titleOfProject;
		this.incrementCounter = 0;
		objectTypes = new HashMap<>();
		fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new ExtensionFilter("*.txt", "*.txt"));
		addingFlag = false;
		status = false;
	}
	
	public SaveCreator (String titleOfProject, String typeDescription, int incrementNumber)
	{
		this(titleOfProject);	
		setObjectType (typeDescription, incrementNumber);
	}
	
	public boolean getStatus()
	{
		return status;
	}
	
	public void setObjectType (String typeDescription, int incrementNumber)
	{
		this.typeDescription = typeDescription;
		this.incrementNumber = incrementNumber;
		objectTypes.put(typeDescription, incrementNumber);
	}
	
	public void act (Mode mode, List <String> data)
	{
		switch (mode)
		{
			case SAVE:
				
				if (file != null) 
				{
					save(data);
				}
				else
				{
					file = fileChooser.showSaveDialog(null);
					save(data);
				}
				break;
				
			case SAVE_AS:
				
				file = fileChooser.showSaveDialog(null);
				save(data);
				break;
			
			case ADD_TO_SAVE_FILE:
				
				if (file != null) 
				{
					addingFlag = true;
					save(data);
					addingFlag = false;
				}
				break;
				
			default:
				
				Dialog.showDialogAlert("Nie wybrano odpowiedniej funkcji");
				break;		
		}
	}
	
	public List <String> act (Mode mode)
	{
		switch (mode)
		{
			case LOAD:
				
				return load();
				
			default:
				
				Dialog.showDialogAlert("Nie mo¿na zapisaæ danych bez argumentów");
				return null;
		}
	}
	
	private void save(List <String> data)
	{
		try
		{
			FileWriter writer = new FileWriter(file, addingFlag);
			if (!addingFlag) writer.write("#"+titleOfProject);
			
			for (String line : data)
			{
				if (incrementCounter == 0) writer.write("\n#"+typeDescription);
				writer.write("\n"+line);
				incrementCounter++;
				if (incrementCounter == incrementNumber) incrementCounter = 0;
			}
			writer.close();
			status = true;
		}
		catch (Exception e)
		{
			status = false;
		}		
	}
	
	private ArrayList <String> load()
	{
		file = fileChooser.showOpenDialog(null);
//		file = new File("C:/Users/Arek/Desktop/save.txt");
		
		try
		{
			Scanner reader = new Scanner(new BufferedReader(new FileReader(file)));
			ArrayList <String> lista = new ArrayList <String> ();
			if (reader.nextLine().equals("#"+titleOfProject))
			{
				while (reader.hasNextLine())
				{
					lista.add(reader.nextLine());
				}
			}
			reader.close();
			status = true;
			return lista;
		}
		catch (Exception e)
		{
			status = false;
			return null;
		}
	}
}

class Dialog 
{	
	public static void showDialogAlert(String name)
	{
		Alert informationAlert = new Alert(Alert.AlertType.INFORMATION);
		informationAlert.setTitle("Info Alert");
		informationAlert.setHeaderText(name);
		
		informationAlert.show();
	}
}





