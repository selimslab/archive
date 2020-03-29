package eu.bigiot.marketplace.appgen.recipemodel;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Jan Seeger on 06.02.2017.
 */
public class RecipeNode {
    private final Map<String, String> extra = new HashMap<>();

    private final Set<RecipeEdge> inputs = new HashSet<>();
    private final Set<RecipeEdge> outputs = new HashSet<>();
    private final Map<String, String> parameters;
    private final String label;
    private final String name;

    private RecipeGraph g;

    public void setGraph(RecipeGraph g) {
        this.g = g;
    }

    public String getLabel() {
        return label;
    }

    public List<String> getOutputLabels() {
        List<String> result = new ArrayList<>(outputs.size());
        for (RecipeNode n: getOutputNodes()) {
            result.add(n.getLabel());
        }
        return result;
    }

    public RecipeNode(String name, String label, Map<String, String> parameters) {
        this.label = label;
        this.name = name;
        this.parameters = parameters;
    }

    public String getName() {
        return name;
    }

    protected void addInput(RecipeEdge rel) {
        if (rel.getTgt() != this) throw new AssertionError("Input relation with target != this added.");
        inputs.add(rel);
    }

    protected void addOutput(RecipeEdge edge) {
        if (edge.getSrc() != this) throw new AssertionError("Output relation with src != this added.");
        outputs.add(edge);
    }

    private Set<RecipeNode> getNodes(Set<RecipeEdge> nodes, boolean out) {
        Set<RecipeNode> result = new HashSet<>();
        for (RecipeEdge rel: nodes)
            result.add(out ? rel.getTgt() : rel.getSrc());
        return result;
    }

    public Set<RecipeNode> getOutputNodes() {
        return getNodes(outputs, true);
    }

    public Set<RecipeNode> getInputNodes() {
        return getNodes(inputs, false);
    }

    public Set<RecipeEdge> getInputs() {
        return inputs;
    }

    private Set<RecipeEdge> getNonDummy(Set<RecipeEdge> inputs) {
        Set<RecipeEdge> result = new HashSet<>();
        for (RecipeEdge e: inputs) {
            if (!e.isDummyEdge())
                result.add(e);
        }
        return result;
    }

    public Set<RecipeEdge> getNonDummyInputs() {
        return getNonDummy(inputs);
    }

    public Set<RecipeEdge> getNonDummyOutputs() {
        return getNonDummy(outputs);
    }

    public Set<RecipeEdge> getNamedRecipeInputs() {
        return inputs.stream()
                .filter((e) -> e.getSrc() == g.getInputNode())
                .filter((e) -> !e.isDummyEdge())
                .collect(Collectors.toSet());
    }

    public Set<RecipeEdge> getOutputs() {
        return outputs;
    }

    protected String getOfferingParameter(String key) {
        return parameters.get(key);
    }

    public String getTemplateName() {
        return "baseNode";
    }

    protected void setExtra(String key, String value) {
        extra.put(key, value);
    }

    public Map<String, String> getExtra() {
        return Collections.unmodifiableMap(extra);
    }

    @Override
    public String toString() {
        return "RecipeNode{" +
                label + ',' +
                name  + '}';
    }
}
