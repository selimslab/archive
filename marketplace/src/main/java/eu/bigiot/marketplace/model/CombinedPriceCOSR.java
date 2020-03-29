package eu.bigiot.marketplace.model;

import java.util.Map;

public class CombinedPriceCOSR extends ComplexOSR{
	
	public String operator;
	
	public CombinedPriceCOSR(Map<String, Object> thisOSR) {
		super(thisOSR);
		operator = (String) thisOSR.get("thisOSR");
	}

	public CombinedPriceCOSR(String name, String label, String value, String OSRValueType, String OSRCategory, String operation, String rule, 
			String operator) {
		super(name, label, value, OSRValueType, OSRCategory, operation, rule);
		this.operator = operator;
	}
	
	public CombinedPriceCOSR(String name, String label, String value, String OSRValueType, String OSRCategory, String rule, 
			String operator) {
		super(name, label, value, OSRValueType, OSRCategory, rule);
		this.operator = operator;
	}

	@Override
	public String interpretRuleToQuery() {
		String offeringsQueryString = "";
		// not implemented yet
		return offeringsQueryString;
	}

}
