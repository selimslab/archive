package eu.bigiot.marketplace.appgen;

import java.util.Map;

/**
 * Created by Jan Seeger on 10.03.2017.
 */
public interface GeneratorIf {
    String generateScript(Map<String,Object> recipePattern, Map<String,Object> offerings);
}
