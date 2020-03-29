package eu.bigiot.marketplace.appgen.recipemodel;

import java.util.Map;

/**
 * Created by Jan Seeger on 08.02.2017.
 */
public class LoopNode extends RecipeNode {

    public LoopNode(String name, String label, Map<String, String> loopConfig) {
        super(name, label, loopConfig);
    }

    @Override
    public String getTemplateName() {
        return "loopActor";
    }

    public String getVariable() {
        return getOfferingParameter("initVariable");
    }

    public String getInitValue() {
        return getOfferingParameter("initValue");
    }

    public String getOperator() {
        switch(getOfferingParameter("operator")) {
            case "less_than":
                return "LSS";
            case "greater_than":
                return "GTR";
            case "less_or_equal":
                return "LEQ";
            case "greater_or_equal":
                return "GEQ";
            case "equal":
                return "EQ";
            case "not_equal":
                return "NEQ";
            default:
                throw new RuntimeException("Unknown boolean operator: " + getOfferingParameter("operator"));
        }
    }

    public String getBoolVariable() {
        return getOfferingParameter("variable");
    }

    public String getBoolValue() {
        return getOfferingParameter("value");
    }

    public String getIncrementOper() {
        return "PLUS";
    }

    public String getIncrementValue() {
        return getOfferingParameter("incrementValue");
    }

}
