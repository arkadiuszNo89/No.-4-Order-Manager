package data;

import java.util.Set;
import java.util.TreeSet;

import javafx.scene.control.TextField;

public class Product implements Comparable <Product>
{
	private String name;
	private String price;
	private String leadTime;
	private String minimalQty;

	private String [] containerOfVals;
	

	public Product(TextField [] listOfTextFields)
	{
		containerOfVals = new String [] {name, price, leadTime, minimalQty};
		setProductData(listOfTextFields);
	}

	public void setProductData(TextField [] listOfTextFields)
	{
		for (int x = 0; x < 4; x++) containerOfVals[x] = listOfTextFields[x].getText();
		
		this.name = containerOfVals[0];
		this.price = containerOfVals[1];
		this.leadTime = containerOfVals[2];
		this.minimalQty = containerOfVals[3];
	}
		
	///////////////////// OUTER METHODS /////////////////////////
//	# 1. Getters

	public String [] getProductData() 
	{
		return containerOfVals;
	}
	
//	# 2. Overrides
	
	@Override
	public String toString()
	{
		return name;
	}
	
	@Override
	public int compareTo(Product tmp)
	{	
		if (this.name.toLowerCase().equals(tmp.name.toLowerCase())) return 0;
		return this.name.toLowerCase().compareTo(tmp.name.toLowerCase());
	}

	
}









