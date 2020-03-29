package eu.bigiot.marketplace.appgen.recipemodel;

import java.util.Map;

/**
 * Created by Jan Seeger on 01.03.2017.
 */
public class CoapNode extends RecipeNode {
    public CoapNode(String ingredientName, String label, Map<String, String> offeringParameters) {
        super(ingredientName, label, offeringParameters);
    }

    public String getURL() {
        return getOfferingParameter("url");
    }

    public String getMethod() {
        return getOfferingParameter("method");
    }

    @Override
    public String getTemplateName() {
        return "coapActor";
    }


    @Override
    public String toString() {
        return String.format("%s{CoapNode{%s, %s}}", super.toString(), getMethod(), getURL());
    }
}
