package eu.bigiot.marketplace.model;

import java.util.ArrayList;
import java.util.List;

public class Ingredient {
	
	public String id;
	public String name;
	public String category;
	public List<Offering> list_of_offerings;	
	
	public Ingredient(String id, String name){
		this.id = id;
		this.name = name;
		this.list_of_offerings = new ArrayList<Offering>();
	}
	
	public Ingredient(String id, String name, String category){
		this.id = id;
		this.name = name;
		this.category = category;
		this.list_of_offerings = new ArrayList<Offering>();
	}
	
}
