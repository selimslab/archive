package eu.bigiot.marketplace.appgen.recipemodel;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A relation between src and tgt. Src has an output called inputLabel that should be provided to tgt with the name
 * outputLabel.
 *
 * Created by Jan Seeger on 06.02.2017.
 */
public class RecipeEdge {
    private final Map<String,String> extra = new HashMap<>();

    private final RecipeNode src;
    private final RecipeNode tgt;

    private final String outputLabel;
    private final String inputLabel;

    private final String outputName;
    private final String inputName;

    public RecipeEdge(RecipeNode src, RecipeNode tgt, String outputLabel, String inputLabel, String outputName, String inputName) {
        this.src = src;
        this.tgt = tgt;
        this.inputLabel = inputLabel;
        this.outputLabel = outputLabel;
        this.outputName = outputName;
        this.inputName = inputName;
    }


    public RecipeNode getSrc() {
        return src;
    }

    public RecipeNode getTgt() {
        return tgt;
    }

    public String getInputLabel() {
        return inputLabel;
    }

    public String getOutputLabel() {
        return outputLabel;
    }

    public String getOutputName() {
        return outputName;
    }

    public String getInputName() {
        return inputName;
    }

    public String getExtra(String key) {
        return extra.get(key);
    }

    public Map<String, String> getExtra() {
        return Collections.unmodifiableMap(extra);
    }

    protected void setExtra(String key, String value) {
        extra.put(key, value);
    }

    public boolean isDummyEdge() {
        return this.inputLabel.equals("dummy") && this.outputLabel.equals("dummy") && this.inputName.isEmpty() &&
                this.outputName.isEmpty();
    }
}
