package eu.bigiot.marketplace.appgen;

import eu.bigiot.marketplace.appgen.recipemodel.GraphFactory;
import eu.bigiot.marketplace.appgen.recipemodel.RecipeGraph;
import eu.bigiot.marketplace.appgen.recipemodel.SemanticsFactory;
import eu.bigiot.marketplace.database.BIGIoT_DAO;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import java.util.Map;

public class AkkaAppGenerator implements GeneratorIf {

	private final STGroup akkagroup = new STGroupFile("templates/akkatemplates.stg");
	private final BIGIoT_DAO dao;

	public AkkaAppGenerator(BIGIoT_DAO dao) {
		this.dao = dao;
	}
	
	//This method is called by the controller to generate the application script
	public String generateScript(Map<String,Object> recipePattern, Map<String,Object> offerings){
		RecipeGraph g = GraphFactory.buildGraph(recipePattern, offerings);
		new SemanticsFactory(dao).addEdgeTypeInformation(g).addEndpointInformation(g);
		ST code = akkagroup.getInstanceOf("program");
		code.add("graph", g);
		return code.render();
   }
}
