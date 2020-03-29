package eu.bigiot.marketplace.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/*
 * OperatorOSR: OSR that defines its own operator (e.g. <, >, <=, >=) to guide the selection of offerings 
 * e.g. Select a new Offering for which "Price <= 20"
 * Covers Price, etc.
 * OSRs of type OperatorOSR can be defined directly in the database
 */
public class OperatorOSR extends OSR {

	public String operator;

	public OperatorOSR(@JsonProperty("name") String name, @JsonProperty("label") String label, @JsonProperty("value") Object value,
			@JsonProperty("OSRValueType") String OSRValueType, @JsonProperty("OSRCategory") String OSRCategory, @JsonProperty("operation") String operation, 
			@JsonProperty("operator") String operator){
		super(name, label, value, OSRValueType, OSRCategory, operation); 
		this.operator = operator;
	}
	
	public OperatorOSR(@JsonProperty("name") String name, @JsonProperty("label") String label, @JsonProperty("value") Object value,
			@JsonProperty("OSRValueType") String OSRValueType, @JsonProperty("OSRCategory") String OSRCategory, 
			@JsonProperty("operator") String operator){
		super(name, label, value, OSRValueType, OSRCategory); 
		this.operator = operator;
	}

	public OperatorOSR(Map<String, Object> thisOSR) {
		super(thisOSR);
		operator = (String) thisOSR.get("operator");
	}

}
