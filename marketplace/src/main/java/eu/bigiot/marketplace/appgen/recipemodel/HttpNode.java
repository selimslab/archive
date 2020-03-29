package eu.bigiot.marketplace.appgen.recipemodel;

import java.util.Map;

/**
 * Created by Jan Seeger on 01.03.2017.
 */
public class HttpNode extends RecipeNode {
    public HttpNode(String ingredientName, String label, Map<String, String> offeringParameters) {
        super(ingredientName, label, offeringParameters);
    }

    public String getMethod() {
        return getOfferingParameter("method");
    }

    public String getURL() {
        return getOfferingParameter("url");
    }

    @Override
    public String getTemplateName() {
        return "httpActor";
    }

    @Override
    public String toString() {
        return String.format("%s{HttpNode{%s}}", super.toString(), getOfferingParameter("endpointUrl"));
    }
}
