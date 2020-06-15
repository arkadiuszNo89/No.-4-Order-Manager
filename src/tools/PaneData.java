package tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

public class PaneData
{
	private static ArrayList <Pane> list = new ArrayList <> ();
	private static Map <String, Integer> map = new HashMap<>();
	
	
	public static <T extends Pane> void addPane(String name, T pane)
	{
		list.add(((T)pane));
		map.put(name, Integer.valueOf(list.size()-1));
	}
	
	public static <T extends Pane> void addFXML(String name, String path) throws IOException
	{
		T pane = FXMLLoader.load(PaneData.class.getResource(path));
		addPane(name, pane);
	}
	
//	Get by number
	public static Pane get(int num)
	{
		return list.get(num);
	}
//	Get by name
	public static Pane get(String name)
	{
		return list.get(map.get(name));
	}
	
}