package eu.bigiot.marketplace.appgen.recipemodel;

import java.util.Map;

/**
 * Created by Jan Seeger on 08.02.2017.
 */
public class ConditionNode extends RecipeNode {

    public ConditionNode(String name, String label, Map<String, String> conditionConfig) {
        super(name, label, conditionConfig);
    }

    public String getLeft() {
        return getOfferingParameter("variable");
    }

    public String getOperator() {
        return getOfferingParameter("operator");
    }

    public String getValue() {
        return getOfferingParameter("value");
    }

    @Override
    public String getTemplateName() {
        return "conditionActor";
    }


    public String toString() {
        return String.format("%s{ConditionNode{%s, %s, %s}}", super.toString(), getLeft(), getOperator(), getValue());
    }
}
