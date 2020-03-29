package eu.bigiot.marketplace.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/*
 * DirectOSR: OSR of type 'is' or 'equals' 
 * e.g. Select a new Offering for which "Place is Munich"
 * Covers Place, Location, Spatial Coverage, Provider Id, etc.
 * OSRs of type DirectOSR can be defined directly in the database
 */
public class DirectOSR extends OSR {

	public DirectOSR(@JsonProperty("name") String name, @JsonProperty("label") String label, @JsonProperty("value") String value,
			@JsonProperty("OSRValueType") String OSRValueType, @JsonProperty("OSRCategory") String OSRCategory, @JsonProperty("operation") String operation){
		super(name, label, value, OSRValueType, OSRCategory, operation);
	}
	
	public DirectOSR(@JsonProperty("name") String name, @JsonProperty("label") String label, @JsonProperty("value") String value,
			@JsonProperty("OSRValueType") String OSRValueType, @JsonProperty("OSRCategory") String OSRCategory){
		super(name, label, value, OSRValueType, OSRCategory);
	}

	public DirectOSR(Map<String, Object> thisOSR) {
		super(thisOSR);
	}

}
