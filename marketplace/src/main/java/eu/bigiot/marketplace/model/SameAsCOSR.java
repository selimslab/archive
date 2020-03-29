package eu.bigiot.marketplace.model;

import java.util.Map;

public class SameAsCOSR extends ComplexOSR {
	
	public String property;	
	public String offeringURI;

	public SameAsCOSR(String name, String label, String value, String OSRValueType, String OSRCategory, String operation, String rule, 
			String property, String offeringURI) {
		super(name, label, value, OSRValueType, OSRCategory, operation, rule);
		this.property = property;
		this.offeringURI = offeringURI;
	}
	
	public SameAsCOSR(String name, String label, String value, String OSRValueType, String OSRCategory, String rule, 
			String property, String offeringURI) {
		super(name, label, value, OSRValueType, OSRCategory, rule);
		this.property = property;
		this.offeringURI = offeringURI;
	}
	
	public SameAsCOSR(Map<String, Object> thisOSR) {
		super(thisOSR);
		property = (String) thisOSR.get("property");
		offeringURI = (String) thisOSR.get("offeringURI");
	}

	@Override
	public String interpretRuleToQuery(){
		String offeringsQueryString = "";
		offeringsQueryString += " <" + this.offeringURI + "> " + this.property + " ?loc . ";
		offeringsQueryString += " ?offering " + this.property + " ?loc . ";
		return offeringsQueryString;
	}

}
