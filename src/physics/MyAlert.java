package physics;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class MyAlert extends Alert
{
	private boolean decision;
	
	public MyAlert(String txt)
	{
		super(AlertType.CONFIRMATION);
		this.setTitle("Alert");
		this.setContentText(txt);
		decision = false;
		
		Optional<ButtonType> result = this.showAndWait(); 	
		
		if (result.isPresent())
		{
			if (result.get().equals(ButtonType.CANCEL))
			{
				decision = false;
			}
			else if (result.get().equals(ButtonType.OK))
			{
				decision = true;
			}
		}	
	}
	
	public boolean getDecision()
	{
		return decision;
	}
}
