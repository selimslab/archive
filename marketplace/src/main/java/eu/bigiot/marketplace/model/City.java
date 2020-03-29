package eu.bigiot.marketplace.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class City {

	public String name;
	public String image;
	
	public City(@JsonProperty("name") String name, @JsonProperty("image") String image){
		this.name = name;
		this.image = image;
	}

}
