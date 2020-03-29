package eu.bigiot.marketplace.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class RecipeRuntimeConfiguration extends Recipe {

	public String rrcName;
	public String rrcID;
	public String uniqID;
	
	public List<IngredientRuntimeConfiguration> ircList;
	
	public static final String NAMESPACE_URL = "http://w3c.github.io/bigiot/";
	public static final String FORMAT = "N3";
	
	public RecipeRuntimeConfiguration(String id, String name, String description) {
		super(id, name, description);
	}

	// JSON to RecipeRuntimeConfiguration object
	public RecipeRuntimeConfiguration(Map<String, Object> rrcData, String recipeName, String recipeID, String recipeDescription) {
		super(recipeName, recipeID, recipeDescription);
		final long uniqId = new Date().getTime();		// Assign unique id to each Recipe Runtime Configuration
    	uniqID = String.valueOf(uniqId);
		rrcName = recipeName + "RRC" + uniqID;
    	rrcID = NAMESPACE_URL + rrcName;
    	ircList = new ArrayList<>();
    	
    	List<Map<String, Object>> listOfIRCs = (List<Map<String, Object>>) rrcData.get("listOfIRCs");
    	if(listOfIRCs != null){
			for(Map<String, Object> ircMap : listOfIRCs){
				String ingredientId = (String) ircMap.get("id");
				String ingredientName = ingredientId.substring(ingredientId.indexOf('#')+1);
				String category = (String) ircMap.get("category");
				IngredientRuntimeConfiguration irc = new IngredientRuntimeConfiguration(ircMap, uniqID, ingredientId, ingredientName, category);
				ircList.add(irc);
			}
		}
	}

}
