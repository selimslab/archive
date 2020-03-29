package eu.bigiot.marketplace.appgen.recipemodel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import eu.bigiot.marketplace.database.BIGIoT_DAO;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Jan Seeger on 31.03.2017.
 */
public class SemanticsFactory {

    private final BIGIoT_DAO dao;
    private static final Logger logger = LoggerFactory.getLogger(SemanticsFactory.class);

    public SemanticsFactory(BIGIoT_DAO dao) {
        this.dao = dao;
    }

    public SemanticsFactory addEdgeTypeInformation(RecipeGraph g) {
        for (RecipeNode n: g.getNodes()) {
            Set<RecipeEdge> toprocess = new HashSet<>(n.getOutputs());
            toprocess.addAll(n.getInputs());
            for (RecipeEdge e: toprocess) {
                if (e.getExtra("inputType") == null && !e.getInputName().isEmpty()) {
                    Set<String> inTypes = dao.getRDFType(e.getInputName());
                    e.setExtra("inputType", Joiner.on(",").join(inTypes));
                }
                if (e.getExtra("outputType") == null && !e.getOutputName().isEmpty()) {
                    Set<String> outTypes = dao.getRDFType(e.getOutputName());
                    e.setExtra("outputType", Joiner.on(",").join(outTypes));
                }
            }
        }
        return this;
    }

    public SemanticsFactory addEndpointInformation(RecipeGraph g) {
        ObjectMapper om = new ObjectMapper();
        Map<String, Object> semantics = new HashMap<>();
        // Get endpoint data type information.
        Map<String, String> collected = new HashMap<>();
        for (RecipeEdge e: g.getInputNode().getOutputs()) {
            if (e.isDummyEdge())
                continue;
            collected.put(e.getInputLabel(), e.getExtra("inputType"));
        }
        semantics.put("datatype", collected);

        // Get all semantic information about input nodes.
        for (RecipeNode n: g.getInputNode().getOutputNodes()) {
            String facts = dao.getFacts(n.getName());
            try {
                // We need to decode the JSON to reencode it correctly -- otherwise we get a stringified version.
                semantics.put(n.getLabel(), om.readValue(facts, Object.class));
                // All of these exceptions shouldn't happen, as we've used Jena's JSON encoding.
            } catch (JsonProcessingException e) {
                logger.error("Failed to decode JSON-LD: {}", e.getMessage());
            } catch (IOException e) {
                logger.error("JSON decoding IO exception: {}", e.getMessage());
            }
        }

        try {
            g.getInputNode().setExtra("semantics", StringEscapeUtils.escapeJava(om.writeValueAsString(semantics)));
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize input semantic facts to JSON: {}", e.getMessage());
            return this;
        }
        collected.clear();
        semantics.clear();
        // Get datatype information
        for (RecipeEdge e: g.getOutputNode().getInputs()) {
            if (e.isDummyEdge())
                continue;
            collected.put(e.getOutputLabel(), e.getExtra("outputType"));
        }
        semantics.put("datatype", collected);
        for (RecipeNode n: g.getOutputNode().getInputNodes()) {
            String facts = dao.getFacts(n.getName());
            try {
                semantics.put(n.getName(), om.readValue(facts, Object.class));
            } catch (JsonProcessingException e) {
                logger.error("Failed to decode JSON-LD: {}", e.getMessage());
            } catch (IOException e) {
                logger.error("JSON decoding IO exception: {}", e.getMessage());
            }

        }

        try {
            g.getOutputNode().setExtra("semantics", StringEscapeUtils.escapeJava(om.writeValueAsString(semantics)));
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize output semantic facts to JSON: {}", e.getMessage());
            return this;
        }
        return this;
    }
}
