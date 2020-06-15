package tools;

import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Stack;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Callback;

public class InitTools 
{
	
	public static void initSpinners (Spinner <String> spinner, int from, int till)
	{
		ObservableList <String> tempListNumbers = FXCollections.observableArrayList();
		for (int x = from; x < till; x++) 
		{
			if (x < 10) 
				{
					tempListNumbers.add("0"+x);
				}
			else
				{
					tempListNumbers.add(x+"");
				}
		}
		SpinnerValueFactory <String> valueFactory = new SpinnerValueFactory.ListSpinnerValueFactory<String>(tempListNumbers);
		
		spinner.setValueFactory(valueFactory); 
		
		spinner.setEditable(true); 
		
		spinner.valueProperty().addListener(new javafx.beans.value.ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0, String olds, String news) {
				
				try
				{
					int x = Integer.parseInt(news);
					if (x >= from && x < 10 && x < till)	spinner.getValueFactory().setValue("0"+x);
					else if (x >= 10 && x < till)	spinner.getValueFactory().setValue(""+x);
					else tempListNumbers.remove(news);
				}
				catch (Exception e)
				{
					spinner.getValueFactory().setValue(olds);
				}
			}
		});	
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static void disableOldDays (DatePicker picker)
	{
		Callback <DatePicker, DateCell> dayCellFactory = new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(final DatePicker datePicker) 
            {
                return new DateCell() 
                {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) 
                    {
                        super.updateItem(item, empty);
                       
                        if (item.isBefore(LocalDate.now())) 
                        { 
                                setDisable(true);
                                setStyle("-fx-background-color: #ffc0cb;");
                        }
                    }
                };
            	}};
        picker.setDayCellFactory(dayCellFactory);
        picker.setEditable(false);
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static void textFieldSetter (int max, TextField... fields)
	{	
		for (TextField field : fields)
		{
			field.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) ->
			{
				if(newValue.length() > max) field.setText(oldValue);
			});
		}
	}
	public static void textFieldSetter (int max, String promptText, TextField... fields)
	{
		for (TextField field : fields) field.setPromptText(promptText);		
		textFieldSetter (max, fields);
	}
	public static void textFieldSetter (int max, boolean onlyInt, TextField... fields)
	{	
		for (TextField field : fields)
		{
			field.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) ->
			{
				if(onlyInt)
				{
					try
					{
						if (!field.getText().isEmpty()) Long.parseLong(newValue);
					}
					catch (Exception e)
					{
						field.setText(oldValue);
					}
				}
				if(newValue.length() > max) field.setText(oldValue);
			});
		}
	}
	public static void textFieldSetter (int max, String promptText, boolean onlyInt, TextField... fields)
	{
		for (TextField field : fields) field.setPromptText(promptText);			
		textFieldSetter (max, onlyInt, fields);
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static <T extends Node> void setVisables(boolean state, T [] arrayOfObjects)
	{
		for (T object : arrayOfObjects) object.setVisible(state);
	}
	public static <T extends Node> void setVisables(boolean state, T [] arrayOfObjects, T specialObject)
	{
		for (T object : arrayOfObjects) 
			{
				if (object == specialObject) continue;
				object.setVisible(state);
			}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static void addLabelLine(Pane pane, String style, int fontSize, String... textArray)
	{
		increment:
		for (String txt : textArray)
		{		
			Label label = new Label();
			label.setFont(Font.font(style, fontSize));
			label.prefWidthProperty().bind(pane.widthProperty());
			label.setPadding(new Insets(0, 10, 0, 10));
			
			if (txt.contains("#m#"))
			{
				Queue <String> kolejka = new ArrayDeque<String>();
				int searchFrom = 3;
				int end = 0;			
				
				while(true)
				{
					end = txt.indexOf("#", searchFrom);
					if (end == -1) break;
					kolejka.add(txt.substring(searchFrom, end));
					searchFrom = end + 1;	
				}
				txt = txt.substring(searchFrom, txt.length());
				while(kolejka.peek() != null)
				{
					switch (kolejka.poll())
					{
						case "bold":
							label.setFont(Font.font(style, FontWeight.BOLD ,fontSize));
							break;
						case "center":
							label.setAlignment(Pos.CENTER);
							break;
						case "right":
							label.setAlignment(Pos.CENTER_RIGHT);
							break;
						case "nl":
							label.setText("");
							pane.getChildren().add(label);
							continue increment;		
					}
				}						
			}
			
			label.setText(txt);
			pane.getChildren().add(label);		
		}
	}
}














