package eu.bigiot.marketplace.appgen.recipemodel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by Jan Seeger on 06.02.2017.
 */
public class GraphFactory {

    private static final Logger l = LoggerFactory.getLogger(GraphFactory.class);

    public static RecipeGraph buildGraph(Map<String, Object> recipePattern, Map<String, Object> offerings_old) {
        Map<String, Map<String, String>> offeringParams = (Map) offerings_old;
        Map<String, List<String>> adjacencyList = (Map) recipePattern.get("adjacencyList");
        Map<String, String> types = (Map) recipePattern.get("elementTypes");
        Map<String, String> labels = (Map) recipePattern.get("elementLabels");
        Map<String, Map<String, String>> config = (Map) recipePattern.get("configAttributes");
        Map<String, List<String>> reverseAdjacencyList = reverseAdjacencyList(adjacencyList);

        Set<String> done = new HashSet<>();
        Map<String, RecipeNode> computed = new HashMap<>();
        RecipeNode input = new RecipeNode("input", "input", Collections.<String, String>emptyMap());
        RecipeNode output = new RecipeNode("output", "output", Collections.<String, String>emptyMap());

        Deque<String> nodesToProcess = new ArrayDeque<>();
        // Do first loop separately so we can add relations to the input node.
        for (String inputname : findSinks(reverseAdjacencyList)) {
            // Input nodes get a dummy relation with the "input" node.
            if (isNode(inputname, types)) {
                RecipeNode newnode = createNode(inputname, types, config, offeringParams, labels);
                computed.put(inputname, newnode);
                RecipeEdge rel = new RecipeEdge(input, newnode, "dummy", "dummy", "", "");
                newnode.addInput(rel);
                input.addOutput(rel);
                nodesToProcess.add(inputname);
            } else {
                // Input node.
                Set<List<String>> pathsToIngredient = pathsToNextIngredient(inputname, adjacencyList, types);
                for (List<String> path : pathsToIngredient) {
                    // Paths should all be of length two, because we presumably have only <input> -> <ingredient> paths.
                    if (path.size() != 2)
                        throw new AssertionError("Path of invalid length: " + path);
                    String nodename = path.get(1);
                    RecipeNode newnode = createNode(nodename, types, config, offeringParams, labels);
                    computed.put(nodename, newnode);
                    String edgename = newnode.getOfferingParameter(path.get(0));
                    RecipeEdge rel = new RecipeEdge(input, newnode, edgename, edgename, "", path.get(0));
                    newnode.addInput(rel);
                    input.addOutput(rel);
                    nodesToProcess.add(path.get(1));
                }
            }
        }

        // Walk through graph
        while (!nodesToProcess.isEmpty()) {
            String currentnode = nodesToProcess.pop();
            if (done.contains(currentnode))
                continue;
            Set<List<String>> pathsToNextIngredient = pathsToNextIngredient(currentnode, adjacencyList, types);
            for (List<String> path : pathsToNextIngredient) {
                if (path.size() == 4) {
                    // Regular path between two ingredients
                    String srcname = path.get(0);
                    String outputname = path.get(1);
                    String inputname = path.get(2);
                    String tgtname = path.get(3);
                    if (!computed.containsKey(srcname))
                        computed.put(srcname, createNode(srcname, types, config, offeringParams, labels));
                    if (!computed.containsKey(tgtname))
                        computed.put(tgtname, createNode(tgtname, types, config, offeringParams, labels));
                    RecipeNode srcnode = computed.get(srcname);
                    RecipeNode tgtnode = computed.get(tgtname);
                    String outputEdgeLabel = srcnode.getOfferingParameter(outputname);
                    String inputEdgeLabel = tgtnode.getOfferingParameter(inputname);
                    RecipeEdge rel = new RecipeEdge(srcnode, tgtnode, outputEdgeLabel, inputEdgeLabel,
                            outputname, inputname);
                    tgtnode.addInput(rel);
                    srcnode.addOutput(rel);
                    nodesToProcess.push(tgtname);
                } else if (path.size() == 2) {
                    // Output path
                    String srcname = path.get(0);
                    String outputname = path.get(1);
                    if (!computed.containsKey(srcname))
                        computed.put(srcname, createNode(srcname, types, config, offeringParams, labels));
                    RecipeNode srcnode = computed.get(srcname);
                    String outputEdgeLabel = srcnode.getOfferingParameter(outputname);
                    RecipeEdge rel = new RecipeEdge(srcnode, output, outputEdgeLabel, outputEdgeLabel,
                            outputname,"");
                    srcnode.addOutput(rel);
                    output.addInput(rel);
                } else if (path.size() == 1) {
                    // Ingredient without any outputs
                    String nodename = path.get(0);
                    if (!computed.containsKey(nodename))
                        computed.put(nodename, createNode(nodename, types, config, offeringParams, labels));
                    RecipeNode srcnode = computed.get(nodename);
                    RecipeEdge rel = new RecipeEdge(srcnode, output, "dummy", "dummy", "", "");
                    srcnode.addOutput(rel);
                    output.addInput(rel);
                }
                done.add(currentnode);
            }
        }
        RecipeGraph result = new RecipeGraph(output, input, new ArrayList<>(computed.values()),
                (String) recipePattern.get("recipeName"));

        computed.values().forEach((n) -> n.setGraph(result));
        return result;
    }

    private static RecipeNode createNode(String name,
                                         Map<String, String> types,
                                         Map<String, Map<String, String>> configAttributes,
                                         Map<String, Map<String, String>> offeringParameters,
                                         Map<String, String> labels) {
        switch (types.get(name)) {
            case "offering":
                switch (offeringParameters.get(name).get("protocol")) {
                    case "COAP":
                        return new CoapNode(name, labels.get(name), offeringParameters.get(name));
                    case "HTTP":
                        return new HttpNode(name, labels.get(name), offeringParameters.get(name));
                    default:
                        throw new RuntimeException("Invalid endpoint type " +
                                offeringParameters.get(name).get("protocol"));
                }
            case "loop":
                Map<String, String> loopConfig = configAttributes.get(name);
                return new LoopNode(name, labels.get(name), loopConfig);
            case "condition":
                return new ConditionNode(name, labels.get(name), configAttributes.get(name));
            default:
                throw new RuntimeException("Invalid node type: " + types.get(name));
        }
    }

    // TODO: This requires a path that is loop-free.
    private static Set<List<String>> pathsToNextIngredient(String srcnode, Map<String,
            List<String>> adjacency, Map<String, String> types) {
        Set<List<String>> paths = new HashSet<>();
        ArrayDeque<ArrayDeque<String>> next = new ArrayDeque<>();
        next.add(new ArrayDeque<>(Collections.singleton(srcnode)));
        while (!next.isEmpty()) {
            ArrayDeque<String> currentPath = next.pop();
            List<String> nextnodes = adjacency.get(currentPath.getLast());
            if (nextnodes == null || nextnodes.isEmpty()) {
                // Output or input node. Also add to paths.
                paths.add(new ArrayList<>(currentPath));
            } else {
                for (String nextnode : nextnodes) {
                    if (isNode(nextnode, types)) {
                        // Found a path
                        currentPath.add(nextnode);
                        paths.add(new ArrayList<>(currentPath));
                    } else {
                        ArrayDeque<String> newpath = currentPath.clone();
                        newpath.add(nextnode);
                        next.push(newpath);
                    }
                }
            }
        }
        return paths;
    }

    // This + reverse adjacency list into "findSources"
    private static Set<String> findSinks(Map<String, List<String>> adjacency) {
        Set<String> result = new HashSet<>();
        for (String k : adjacency.keySet()) {
            if (adjacency.get(k).isEmpty())
                result.add(k);
        }
        return result;
    }

    private static boolean isNode(String name, Map<String, String> type) {
        return (type.get(name).equals("offering") || type.get(name).equals("loop")) || type.get(name).equals
                ("condition");
    }

    private static Map<String, List<String>> reverseAdjacencyList(Map<String, List<String>> adjacencyList) {
        Map<String, List<String>> result = new HashMap<>();
        for (String src : adjacencyList.keySet()) {
            List<String> tgts = adjacencyList.get(src);
            // Keep empty nodes, importent for recipe input/output.
            if (!result.containsKey(src))
                result.put(src, new ArrayList<String>());
            for (String tgt : tgts) {
                if (result.containsKey(tgt)) {
                    result.get(tgt).add(src);
                } else {
                    List<String> current = new ArrayList<>();
                    current.add(src);
                    result.put(tgt, current);
                }
            }
        }
        return result;
    }
}
