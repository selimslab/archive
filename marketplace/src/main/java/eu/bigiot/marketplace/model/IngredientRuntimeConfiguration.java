package eu.bigiot.marketplace.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class IngredientRuntimeConfiguration extends Ingredient {

	public String ircName;
	public String ircID;
	public List<Offering> listOfSelectedOfferings;
	public List<OSR> OSRList;
	public int presentCardinality;
	public String OSRQueryString;
	
	public static final String FORMAT = "N3";
	
	public IngredientRuntimeConfiguration(String id, String name, String category){
		super(id, name, category);

		this.listOfSelectedOfferings = new ArrayList<Offering>();
		this.OSRList = new ArrayList<OSR>();
	}

	public IngredientRuntimeConfiguration(Map<String, Object> ircMap, String uniqID, String ingredientId, String ingredientName, String category) {
		super(ingredientId, ingredientName, category);
		ircName = ingredientName + uniqID;
		ircID =  ingredientId + uniqID;
		presentCardinality = ircMap.get("presentCardinality") != null ? (int) ircMap.get("presentCardinality") : 0;
		
		listOfSelectedOfferings = new ArrayList<>();
		List<Map<String, Object>> selectedOfferingsList = (List<Map<String, Object>>) ircMap.get("listOfSelectedOfferings");
		if(selectedOfferingsList != null){
			for(Map<String, Object> selectedOffering : selectedOfferingsList){
				Offering offering = new Offering(selectedOffering);
				listOfSelectedOfferings.add(offering);
			}

			OSRList = new ArrayList<>();
			List<Map<String, Object>> listOfOSRs = (List<Map<String, Object>>) ircMap.get("OSRList");
			if(listOfOSRs != null){
				for(Map<String, Object> thisOSR : listOfOSRs){
					String OSRCategory = (String) thisOSR.get("OSRCategory");

					if(OSR.OSRCATEGORY_DIRECTOSR.equals(OSRCategory)){
						DirectOSR OSRObj = new DirectOSR(thisOSR);
						OSRList.add(OSRObj);
					}
					else if(OSR.OSRCATEGORY_OPERATOROSR.equals(OSRCategory)){
						OperatorOSR OSRObj = new OperatorOSR(thisOSR);
						OSRList.add(OSRObj);
					}
					else if(OSR.OSRCATEGORY_COMPLEXOSR.equals(OSRCategory)){
						String rule = (String) thisOSR.get("rule");
						switch (rule) {
						case ComplexOSR.RULE_SAME_AS:
							SameAsCOSR sacOSRObj = new SameAsCOSR(thisOSR);
							OSRList.add(sacOSRObj);
							break;
						case ComplexOSR.RULE_CARDINALITY:
							CardinalityCOSR ccOSRObj = new CardinalityCOSR(thisOSR);
							OSRList.add(ccOSRObj);
							break;
						default: 
							break;
						}
					}
				}
			}
		}
	}
}
