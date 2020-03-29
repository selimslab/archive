package eu.bigiot.marketplace.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/*
 * ComplexOSR: OSR that defines its own rule to guide the selection of offerings
 * e.g. Select a new Offering A for which "Place is same as Offering B's Place"
 */
public class ComplexOSR extends OSR {

	public String rule;
	
	// Defined rules
	public final static String RULE_SAME_AS = "sameAs";
	public final static String RULE_CARDINALITY = "cardinality";
	public final static String RULE_COMBINED_PRICE = "combinedPrice";
	
	public static final String COMPLEXOSR_CATEGORY_SAMEAS = "SameAs";

	public ComplexOSR(@JsonProperty("name") String name, @JsonProperty("label") String label, @JsonProperty("value") String value, 
			@JsonProperty("OSRValueType") String OSRValueType, @JsonProperty("OSRCategory") String OSRCategory, @JsonProperty("operation") String operation, @JsonProperty("rule") String rule){
		super(name, label, value, OSRValueType, OSRCategory, operation);
		this.rule = rule;
	}
	
	public ComplexOSR(@JsonProperty("name") String name, @JsonProperty("label") String label, @JsonProperty("value") String value, 
			@JsonProperty("OSRValueType") String OSRValueType, @JsonProperty("OSRCategory") String OSRCategory, @JsonProperty("rule") String rule){
		super(name, label, value, OSRValueType, OSRCategory);
		this.rule = rule;
	}
	
	public ComplexOSR(Map<String, Object> thisOSR) {
		super(thisOSR);
		rule = (String) thisOSR.get("rule");
	}

	public String interpretRuleToQuery(){
		return "";
	}

}
