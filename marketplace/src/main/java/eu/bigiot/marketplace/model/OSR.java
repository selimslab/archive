package eu.bigiot.marketplace.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/*
 * OSR: The parent class for Offering Selection Rules
 * OSRs facilitate the dynamic replacement of the offerings at runtime
 */
public class OSR {

	public String name;
	public String label;
	public Object value;
	public String OSRValueType;
	//public String forOfferingCategory;
	public Offering appliedTo;
	public String operation;
	
	public String OSRCategory;	// DirectOSR, OperatorOSR, ComplexOSR
	
	public String forIngredient;
	
	public static final String OSRCATEGORY_OSR = "OSR";
	public static final String OSRCATEGORY_DIRECTOSR = "DirectOSR";
	public static final String OSRCATEGORY_OPERATOROSR = "OperatorOSR";
	public static final String OSRCATEGORY_COMPLEXOSR = "ComplexOSR";
	
	public static final String FORMAT = "N3";
	
	public OSR(@JsonProperty("name") String name, @JsonProperty("label") String label, @JsonProperty("value") Object value, 
			@JsonProperty("OSRValueType") String OSRValueType, @JsonProperty("OSRCategory") String OSRCategory, @JsonProperty("operation") String operation){
		this.name = name;
		this.label = label;
		this.value = value;
		this.OSRCategory = OSRCategory;
		this.OSRValueType = OSRValueType;
		this.operation = operation;
	}
	
	public OSR(@JsonProperty("name") String name, @JsonProperty("label") String label, @JsonProperty("value") Object value, 
			@JsonProperty("OSRValueType") String OSRValueType, @JsonProperty("OSRCategory") String OSRCategory){
		this.name = name;
		this.label = label;
		this.value = value;
		this.OSRCategory = OSRCategory;
		this.OSRValueType = OSRValueType;
	}

	public OSR(Map<String, Object> thisOSR) {
		name = (String) thisOSR.get("name");
		OSRCategory = (String) thisOSR.get("OSRCategory");
		label = (String) thisOSR.get("label");
		value = thisOSR.get("value");
		OSRValueType = (String) thisOSR.get("OSRValueType");
		operation = (String) thisOSR.get("operation");
	}

}
