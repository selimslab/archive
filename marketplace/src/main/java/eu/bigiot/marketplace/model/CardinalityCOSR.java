package eu.bigiot.marketplace.model;

import java.util.Map;

public class CardinalityCOSR extends ComplexOSR{
	
	public int minCardinality;
	public int maxCardinality;
	
	public CardinalityCOSR(String name, String label, String value, String OSRValueType, String OSRCategory, String operation, String rule, 
			int minCardinality, int maxCardinality) {
		super(name, label, value, OSRValueType, OSRCategory, operation, rule);
		this.minCardinality = minCardinality;
		this.maxCardinality = maxCardinality;
	}
	
	public CardinalityCOSR(String name, String label, String value, String OSRValueType, String OSRCategory, String rule, 
			int minCardinality, int maxCardinality) {
		super(name, label, value, OSRValueType, OSRCategory, rule);
		this.minCardinality = minCardinality;
		this.maxCardinality = maxCardinality;
	}

	public CardinalityCOSR(Map<String, Object> thisOSR) {
		super(thisOSR);
		if(thisOSR.get("minCardinality") instanceof String){
			minCardinality = Integer.parseInt((String) thisOSR.get("minCardinality"));
		}else if(thisOSR.get("minCardinality") instanceof Integer){
			minCardinality = (Integer) thisOSR.get("minCardinality");
		}
		
		if(thisOSR.get("maxCardinality") instanceof String){
			maxCardinality = Integer.parseInt((String) thisOSR.get("maxCardinality"));
		}else if(thisOSR.get("maxCardinality") instanceof Integer){
			maxCardinality = (Integer) thisOSR.get("maxCardinality");
		}
	}

	@Override
	public String interpretRuleToQuery() {
		return "";
	}
	
}
