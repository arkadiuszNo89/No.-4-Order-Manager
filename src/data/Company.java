package data;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javafx.scene.control.TextField;

public class Company implements Comparable <Company>
{
	private String name;
	private String email;
	private String country;
	private String city;
	private String phoneNumber;
	private String NIP;
	private String deliveryTime;
	private String deliveryPrice;

	private String [] containerOfVals;
	private Map <String, Product> listOfProducts;
	
	
	public Company(TextField [] listOfTextFields) 
	{
		containerOfVals = new String [] {name, email, country, city, phoneNumber, NIP, deliveryPrice, deliveryTime};		
		setCompanyData(listOfTextFields);
		listOfProducts = new TreeMap <> ();
	}
	
	public void setCompanyData(TextField [] listOfTextFields)
	{
		for (int x = 0; x < 8; x++) containerOfVals[x] = listOfTextFields[x].getText();
		
		this.name = containerOfVals[0];
		this.email = containerOfVals[1];
		this.country = containerOfVals[2];
		this.city = containerOfVals[3];
		this.phoneNumber = containerOfVals[4];
		this.NIP = containerOfVals[5];
		this.deliveryPrice = containerOfVals[6];
		this.deliveryTime = containerOfVals[7];
	}
	
	///////////////////// OUTER METHODS /////////////////////////
	
	public String [] getCompanyData()
	{
		return this.containerOfVals;
	}
	
	public Map <String, Product> getCompanyProducts()
	{
		return this.listOfProducts;
	}
	
	@Override
	public String toString()
	{
		return name;
	}
	
	@Override
	public int compareTo(Company tmp)
	{	
		if (this.name.toLowerCase().equals(tmp.name.toLowerCase())) return 0;
		return this.name.toLowerCase().compareTo(tmp.name.toLowerCase());
	}
	
	public void addProduct(Product product)
	{
		listOfProducts.put(product.toString(), product);
	}
}
