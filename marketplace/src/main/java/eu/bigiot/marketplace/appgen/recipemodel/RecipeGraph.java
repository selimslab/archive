package eu.bigiot.marketplace.appgen.recipemodel;

import java.util.*;

/**
 * Created by Jan Seeger on 06.02.2017.
 */
public class RecipeGraph {
    private final RecipeNode output;
    private final RecipeNode input;
    private final List<? extends RecipeNode> nodes;
    private final String recipeName;

    RecipeGraph(RecipeNode output, RecipeNode input, List<? extends RecipeNode> nodes, String recipeName) {
        this.output = output;
        this.input = input;
        this.nodes = nodes;
        this.recipeName = recipeName;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public String getPackageName() {
        return "eu.bigiot.marketplace.sample_scripts";
    }

    private List<? extends RecipeNode> topoSort() {
        List<RecipeNode> result = new ArrayList<>(nodes.size());
        Set<RecipeEdge> removed = new HashSet<>();

        //Kahn's Algorithm, topological sort
        Deque<RecipeNode> reachable = new ArrayDeque<>(Collections.singleton(input));
        while (!reachable.isEmpty()) {
            RecipeNode currentNode = reachable.pop();
            result.add(currentNode);
            removed.addAll(currentNode.getOutputs());
            for (RecipeEdge output: currentNode.getOutputs()) {
                // Check whether all input edges of the target have been removed.
                boolean finished = true;
                for (RecipeEdge input: output.getTgt().getInputs()) {
                    if (!removed.contains(input))
                        finished = false;
                }
                if (finished)
                    reachable.add(output.getTgt());
            }
        }
        // Result contains input and output nodes as well.
        if (result.size() != nodes.size() + 2)
            throw new AssertionError("Tried to sort circular graph topologically.");
        result.remove(0);
        result.remove(result.size() - 1);
        return result;
    }

    public List<? extends RecipeNode> getSortedNodes() {
        return topoSort();
    }

    public List<? extends RecipeNode> getNodes() {
        return nodes;
    }

    public RecipeNode getOutputNode() {
        return output;
    }

    public RecipeNode getInputNode() {
        return input;
    }
}
