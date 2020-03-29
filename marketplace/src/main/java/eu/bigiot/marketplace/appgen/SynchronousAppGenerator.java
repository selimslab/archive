package eu.bigiot.marketplace.appgen;

import java.util.*;

public class SynchronousAppGenerator implements GeneratorIf {
	Set<String> importSet;
	List<String> ingredients;
	List<String> data;
	List<String> recipeInputs;
	List<String> recipeOutputs;
	Set<String> availableData;
	List<String> executionOrder;
	Map<String, Set<String>> conditions;
	Map<String, Set<String>> loops;
	
	Map<String,List<String>> adjacencyList;
	Map<String,List<String>> reverseAdjacencyList;
	Map<String,String> elementLabels;
	Map<String,String> elementTypes;
	Map<String,Map<String,String>> configAttributes;
	Map<String,Object> offerings;
	
	//This method is called by the controller to generate the application script
	public String generateScript(Map<String,Object> recipePattern, Map<String,Object> offerings){
		
		importSet = new HashSet<String>();
		ingredients = new ArrayList<String>();
		data = new ArrayList<String>();
		
		importSet.add("java.util.Map");
		importSet.add("java.util.HashMap");
		importSet.add("com.github.jsonldjava.utils.JsonUtils");
		
		StringBuilder ingredientMethods = new StringBuilder();

		//Create independent methods to access each ingredient
		adjacencyList = (Map<String, List<String>>) recipePattern.get("adjacencyList");
		elementLabels = (Map<String, String>) recipePattern.get("elementLabels");
		elementTypes = (Map<String, String>) recipePattern.get("elementTypes");
		configAttributes = (Map<String, Map<String, String>>) recipePattern.get("configAttributes");
		this.offerings = offerings;
		
		//Creating the reverse version of the adjacency list
		reverseAdjacencyList = new HashMap<String,List<String>>();
		for(Map.Entry<String,List<String>> entry : adjacencyList.entrySet()){
			String source = entry.getKey();
			List<String> destList = entry.getValue();
			if(!reverseAdjacencyList.containsKey(source)) reverseAdjacencyList.put(source, new ArrayList<String>());
			for(String dest : destList){
				if(!reverseAdjacencyList.containsKey(dest)){
					List<String> srcList = new ArrayList<String>();
					srcList.add(source);
					reverseAdjacencyList.put(dest, srcList);
				}
				else{
					List<String> srcList = reverseAdjacencyList.get(dest);
					srcList.add(source);
				}
			}
		}
		
		for(String element : elementTypes.keySet()){
			String elementType = elementTypes.get(element);
			if(elementType.equals("offering")){
				Map<String,Object> offeringParams = (Map<String,Object>) offerings.get(element);
				ingredientMethods.append(createIngredientMethod(element, offeringParams, "\t"));
				ingredientMethods.append("\n");
				ingredients.add(element);
			}
			else if(elementType.equals("condition") || elementType.equals("loop")){
				ingredients.add(element);
			}
			else data.add(element);
		}
		
		recipeInputs = new ArrayList<String>(); //Data which are not returned by an ingredient
		recipeOutputs = new ArrayList<String>(); //Data which are not used by any ingredient (to be written to console)
		availableData = new HashSet<String>(); //Used during execution to keep track on the data available at some point
		for (String element : data) {
		    if(reverseAdjacencyList.get(element).isEmpty()) {
		    	recipeInputs.add(element);
		    	availableData.add(element);
		    }
		    if(adjacencyList.get(element).isEmpty()) {
		    	recipeOutputs.add(element);
		    }
		}
		
		//We read external inputs using a scanner
		if(!recipeInputs.isEmpty()) importSet.add("java.util.Scanner");
		
		//Create an order to execute ingredients (to make sure that the inputs of an ingredient are computed before it)
		createExecutionOrder();
		
		//Start building the script
		StringBuilder applicationScript = new StringBuilder();
		
		//Initial code
		applicationScript.append("package eu.bigiot.marketplace.sample_scripts;\n\n");
		for(String imp : importSet) applicationScript.append("import " + imp + ";\n");
		String recipeName = recipePattern.get("recipeName").toString();
		applicationScript.append("\npublic class " + recipeName + "{\n\n");
		applicationScript.append("\tpublic static void main(String[] args){\n");
		
		//Fetching recipe inputs from command line
		if(!recipeInputs.isEmpty()){
			applicationScript.append("\n" + "\t\tMap<String,Object> userInputs = new HashMap<String,Object>();\n");
			applicationScript.append("\t\tScanner scanner = new Scanner(System.in);\n\n");
			for(int i=0 ; i<recipeInputs.size(); i++) {
				String inputID = recipeInputs.get(i);
				String inputLabel = elementLabels.get(inputID);
				applicationScript.append("\t\t//Fetching the value for " + inputLabel + "\n");
				applicationScript.append("\t\ttry {\n");
				applicationScript.append("\t\t\tSystem.out.print(\"\\nEnter the value for " + inputLabel + " :\");\n");
				applicationScript.append("\t\t\tuserInputs.put(\""+ inputLabel +"\", JsonUtils.fromString(scanner.nextLine()));\n");
				applicationScript.append("\t\t} catch (Exception e) {\n");
				applicationScript.append("\t\t\tSystem.out.println(\"Input Error : Invalid value provided for " + inputLabel + "!\");\n");
				applicationScript.append("\t\t\tscanner.close();\n");
				applicationScript.append("\t\t\treturn;\n");
				applicationScript.append("\t\t}\n\n");
			}
			applicationScript.append("\t\tscanner.close();\n");
		}
		
/*		//Fetching recipe inputs from args
		if(!recipeInputs.isEmpty()){
			//Checking number of args
			applicationScript.append("\t\tif (args.length != " + recipeInputs.size() + ") {\n");
			applicationScript.append("\t\t\tSystem.out.println(\"Error : Following arguments are needed : " + recipeInputs.get(0));
			for(int i=1 ; i<recipeInputs.size(); i++) applicationScript.append(", " + recipeInputs.get(i));
			applicationScript.append(".\");\n");
			applicationScript.append("\t\t\treturn;\n");
			applicationScript.append("\t\t}\n\n");
			
			//Generating recipe inputs from args
			applicationScript.append("\t\ttry {\n");
			for(int i=0 ; i<recipeInputs.size(); i++) {
				String variableName = recipeInputs.get(i);
				String dataType = dataTypes.get(variableName);
				String typeCasting = dataType.equals("Object") ? "" : "(" + dataType + ") ";
				applicationScript.append("\t\t\t"+ variableName +" = " + typeCasting + "JsonUtils.fromString(args[" + i + "]);\n");
			}
			applicationScript.append("\t\t} catch (Exception e) {\n");
			applicationScript.append("\t\t\te.printStackTrace();\n");
			applicationScript.append("\t\t\treturn;\n");
			applicationScript.append("\t\t}\n\n");
		}*/
				
		String currentIndent = "\t\t";
		String currentCondition = "";
		Set<String> currentLoops = new HashSet<String>();
		
		//Execution of ingredients in the order
		for(String currentExecution : executionOrder){
			
			String ingredientName = elementLabels.get(currentExecution);
			String ingredientCategory = elementTypes.get(currentExecution);
			Map<String,Object> offeringParams = (Map<String,Object>) offerings.get(currentExecution);
			Map<String,String> configMap = configAttributes.get(currentExecution);
			
			boolean bypass = ingredientCategory.equals("condition") || ingredientCategory.equals("loop");
			
			//Decide whether to stay in the current if condition or to create a new if condition
			String condition = "";
			if(conditions.containsKey(currentExecution)){
				Set<String> conditionSet = conditions.get(currentExecution);
				Iterator<String> iterator = conditionSet.iterator();
				if(iterator.hasNext()){
					condition = iterator.next();
					while(iterator.hasNext()) condition += " && " + iterator.next();
				}
			}
			
			if(!bypass && !condition.equals(currentCondition)){
				if(!currentCondition.equals("")){
					currentIndent = currentIndent.substring(1);
					applicationScript.append("\n" + currentIndent + "}\n");
				}
				if(!condition.equals("")){
					applicationScript.append("\n" + currentIndent + "if (" + condition + ") {\n");
					currentIndent += "\t";
				}
				currentCondition = condition;
			}
			
			//Create a new loop if needed
			if(!bypass && loops.containsKey(currentExecution)){
				for(String loop : loops.get(currentExecution)){
					if(!currentLoops.contains(loop)){
						currentLoops.add(loop);
						applicationScript.append("\n" + currentIndent + "for (" + loop + ") {\n");
						currentIndent += "\t";
						
						//Adding delay to the loop
						applicationScript.append("\n" + currentIndent + "//Waiting for " + 1000 + " milliseconds before the execution\n");
						applicationScript.append(currentIndent + "try {\n");
						applicationScript.append(currentIndent + "\tThread.sleep(" + String.valueOf(1000) + ");\n");
						applicationScript.append(currentIndent + "} catch (InterruptedException e) {\n");
						applicationScript.append(currentIndent + "\te.printStackTrace();\n");
						applicationScript.append(currentIndent + "}\n");
					}
				}
			}
			
			//Fetching inputs for the ingredient
			List<String> inputData = reverseAdjacencyList.get(currentExecution);
			if(!bypass){
				applicationScript.append("\n" + currentIndent + "//Collecting input parameters for the ingredient " + ingredientName + "\n");
				applicationScript.append(currentIndent + "Map<String,Object> " + ingredientName + "_inputs = new HashMap<String,Object>();\n");
			}
			
			for(Object input : inputData){
				String inputName = bypass? ((String)input).substring(((String)input).lastIndexOf('#')+1) : (String) offeringParams.get(input);
				if(recipeInputs.contains(input)){
					if(bypass){
						applicationScript.append(currentIndent + inputName + " = " + 
							"userInputs.get(\"" + elementLabels.get(input) + "\");\n");
					}
					else {
						applicationScript.append(currentIndent + ingredientName + "_inputs.put(\"" + inputName + "\", " + 
							"userInputs.get(\"" + elementLabels.get(input) + "\"));\n");
					}
				}
				else {
					String prevOutput = reverseAdjacencyList.get(input).get(0);
					String prevIng = reverseAdjacencyList.get(prevOutput).get(0);
					Map<String,Object> prevOffering = (Map<String,Object>) offerings.get(prevIng);
					String prevOfferingOutputs = elementLabels.get(prevIng) + "_outputs";				
					String prevIngCategory = elementTypes.get(prevIng);
					
					if(bypass){
						if(prevIngCategory.equals("condition") || prevIngCategory.equals("loop"))
							applicationScript.append(currentIndent + inputName + " = " + 
								prevOutput.substring(prevOutput.lastIndexOf('#')+1) + ";\n");
						else applicationScript.append(currentIndent + inputName + " = " + 
							prevOfferingOutputs + ".get(\"" + (String) prevOffering.get(prevOutput) + "\");\n");
					}
					else{
						if(prevIngCategory.equals("condition") || prevIngCategory.equals("loop"))
							applicationScript.append(currentIndent + ingredientName + "_inputs.put(\"" + inputName + "\", " + 
								prevOutput.substring(prevOutput.lastIndexOf('#')+1) + ");\n");
						else applicationScript.append(currentIndent + ingredientName + "_inputs.put(\"" + inputName + "\", " + 
							prevOfferingOutputs + ".get(\"" + (String) prevOffering.get(prevOutput) + "\"));\n");
					}
				}
			}
			
			//Executing the ingredient
			if(bypass){
				//applicationScript.append(currentIndent + "Map<String,Object> " + ingredientName + "_outputs = new HashMap<String,Object>();\n");
				for(Object output : adjacencyList.get(currentExecution)){
					if(!offeringParams.containsKey((String)output)) {
						System.out.println("Error : No input value can be mapped for the output " + output + "!");
						applicationScript.append(currentIndent + "Object " + ((String)output).substring(((String)output).lastIndexOf('#')+1) + " = null;\n");
					}
					else {
						String inputName = (String) offeringParams.get((String)output);
						applicationScript.append(currentIndent + "Object " + ((String)output).substring(((String)output).lastIndexOf('#')+1) + " = " + 
							inputName.substring(inputName.lastIndexOf('#')+1) + ";\n");
					}
				}
			} else {
				applicationScript.append("\n" + currentIndent + "//Executing the ingredient " + ingredientName + "\n");
				if(adjacencyList.get(currentExecution).isEmpty()) applicationScript.append(currentIndent + ingredientName + "(" + ingredientName + "_inputs);\n");
				else applicationScript.append(currentIndent + "Map<String,Object> " + ingredientName + "_outputs = " + ingredientName + "(" + ingredientName + "_inputs);\n");
			}
			
			//Output values to be printed
			List<String> toPrint = new ArrayList<String>();
			for(Object output : adjacencyList.get(currentExecution)){
				if(recipeOutputs.contains((String)output)){
					String outputName = (String) ( bypass? output : offeringParams.get(output));
					toPrint.add(outputName);
				}
			}

			//Writing the output data to console
			if(!toPrint.isEmpty()){
				applicationScript.append(currentIndent + "\ntry {\n");
				applicationScript.append(currentIndent + "\t//Displaying output values\n");
				for(String output : toPrint) {
					String outputName = elementLabels.get(output);
					if(bypass) {
						applicationScript.append(currentIndent + "\tSystem.out.println(\""+ outputName +" = \" + "
							+ "JsonUtils.toPrettyString(" + outputName + "));\n");
					}
					else {
						applicationScript.append(currentIndent + "\tSystem.out.println(\""+ outputName +" = \" + "
							+ "JsonUtils.toPrettyString(" + ingredientName + "_outputs.get(\"" + outputName + "\")));\n");
					}
				}
				applicationScript.append(currentIndent + "} catch (IOException e) {\n");
				applicationScript.append(currentIndent + "\te.printStackTrace();\n");
				applicationScript.append(currentIndent + "}\n");
			}
		}
		
		//Closing the if conditions (if any)
		if(!currentCondition.equals("")){
			currentIndent = currentIndent.substring(1);
			applicationScript.append("\n" + currentIndent + "}\n");
		}
		
		//Closing the for loops (if any)
		for(int i=0; i<currentLoops.size(); i++){
			currentIndent = currentIndent.substring(1);
			applicationScript.append("\n" + currentIndent + "}\n");
		}
		
		applicationScript.append("\n\t}\n\n");
		
		applicationScript.append(ingredientMethods);
		
		applicationScript.append("}\n\n");
		
		return applicationScript.toString();
	}
	
	
	//This method decides the java variable type of a data element given its 'dataType'
	private String getVariableType(String dataType){
		if(dataType==null || dataType.length()==0) return "Object";
		else if(dataType.equals("xsd:string")) return "String";
		else if(dataType.equals("xsd:integer")) return "Integer";
		else if(dataType.equals("xsd:float")) return "Float";
		else if(dataType.equals("xsd:double")) return "Double";
		else if(dataType.equals("xsd:boolean")) return "Boolean";
		else if(dataType.equals("xsd:dateTime")){
			importSet.add("java.util.Date");
			return "Date";
		}
		else return "Object";
	}
	
	
	//This method decides the default value for each data type
	private String getDefaultValue(String dataType){
		if(dataType.equals("Integer")) return "0";
		else if(dataType.equals("Float")) return "0f";
		else if(dataType.equals("Double")) return "0.0";
		else if(dataType.equals("Boolean")) return "false";
		else return "null";
	}
	
	//This method creates an order in which the ingredients can be executed.
	//The goal is to make sure that all the inputs of an ingredient are computed before the ingredient.
	//i.e. : If an output of the ingredient 'A' is an input of the ingredient 'B', then 'A' should be executed before 'B'.
	private void createExecutionOrder(){
		//Initializing the data structures
		executionOrder = new ArrayList<String>();
		conditions = new HashMap<String, Set<String>>();
		loops = new HashMap<String, Set<String>>();
		//Calling the recursive method 'addToExecutionOrder' with any (random) ingredient
		if(!ingredients.isEmpty()) addToExecutionOrder(ingredients.get(0));
	}
	
	
	//This method adds ingredients to 'executionOrder' recursively
	private void addToExecutionOrder(String ingredientID){
		Map<String,Object> offeringParams = (Map<String, Object>) offerings.get(ingredientID);
		if(!executionOrder.contains(ingredientID)){
			List<String> inputs = reverseAdjacencyList.get(ingredientID);
			for(String input : inputs){
				if(!availableData.contains(input)){
					//If an input value is not computed yet, traverse to the ingredient which computes that value
					List<String> priorOutList = reverseAdjacencyList.get(input);
					if(priorOutList.isEmpty()) System.out.println("Error : Input " + input + " is not reachable!");
					else{
						String priorOut = priorOutList.get(0);
						List<String> priorExecutionList = reverseAdjacencyList.get(priorOut);
						if(priorOutList.isEmpty()) System.out.println("Error : Output " + priorOut + " is not reachable!");
						else{
							String priorExecution = priorExecutionList.get(0);
							if(ingredients.contains(priorExecution)){
								addToExecutionOrder(priorExecution);
							}
							else System.out.println("Error : Element " + priorExecution + " is not an ingredient!");
						}
					}
					return;
				}
			}
			
			//Collect the conditions and loops from inputs. The ingredient should be subjected to all of them.
			Set<String> excecutionConditions = new HashSet<String>();
			Set<String> excecutionLoops = new HashSet<String>();
			for(String input : inputs){
				if(conditions.containsKey(input)) excecutionConditions.addAll(conditions.get(input));
				if(loops.containsKey(input)) excecutionLoops.addAll(loops.get(input));
			}
			
			//If the ingredient is a condition, create the condition statement and add it to the ingredient.
			//This condition will be passed downwards in the execution tree.
			if(elementTypes.get(ingredientID).equals("condition")){
				Map<String,String> configMap = configAttributes.get(ingredientID);
				String operator = configMap.get("operator");
				String variable = configMap.get("variable");
				String value = configMap.get("value");
				if(operator!=null && variable!=null && value!=null){
					operator = operator.endsWith("equalTo") ? " == " : (operator.endsWith("lessThan") ? " < " : " > ");
					excecutionConditions.add(variable + operator + value);
				}
			}
			
			//If the ingredient is a loop, create the loop statement and add it to the ingredient.
			//This loop will be passed downwards in the execution tree.
			if(elementTypes.get(ingredientID).equals("loop")){
				Map<String,String> configMap = configAttributes.get(ingredientID);
				
				String init = "";
				String initType = configMap.get("initType");
				String initVariable = configMap.get("initVariable");
				String initValue = configMap.get("initValue");
				if(initType!=null && initVariable!=null && initValue!=null){
					init = getVariableType(initType) + " " + initVariable + " = " + initValue;
				}
				
				String condition = "";
				String operator = configMap.get("operator");
				String variable = configMap.get("variable");
				String value = configMap.get("value");
				if(operator!=null && variable!=null && value!=null){
					operator = operator.endsWith("equalTo") ? " == " : (operator.endsWith("lessThan") ? " < " : " > ");
					condition = variable + operator + value;
				}
				
				String increment = "";
				String incrementVariable = configMap.get("incrementVariable");
				String incrementValue = configMap.get("incrementValue");
				if(incrementVariable!=null && incrementValue!=null){
					increment = incrementVariable + " += " + incrementValue;
				}
				excecutionLoops.add(init + " ; " + condition + " ; " + increment);
			}
			
			//Add the ingredient, conditions and loops to the respective lists
			executionOrder.add(ingredientID);
			conditions.put(ingredientID, excecutionConditions);
			loops.put(ingredientID, excecutionLoops);
			
			//Traverse downwards in the execution tree through each output.
			List<String> outputs = adjacencyList.get(ingredientID);
			for(String output : outputs){
				List<String> postInputs = adjacencyList.get(output);
				for(String input : postInputs){
					availableData.add(input);
					conditions.put(input, excecutionConditions);
					loops.put(input, excecutionLoops);
					List<String> laterExecutions = adjacencyList.get(input);
					for(String execution : laterExecutions){
						if(ingredients.contains(execution)){
							addToExecutionOrder(execution);
						}
						else System.out.println("Error : Element " + execution + " is not an ingredient!");
					}
				}
			}
		}
	}

	
	//This method generates a method to access a particular ingredient with the use of 'offeringParams'
	private String createIngredientMethod(String ingredient, Map<String,Object> offeringParams, String indent){
		
		StringBuilder code = new StringBuilder();
		code.append(indent + "//The method to access the ingredient " + elementLabels.get(ingredient) + "\n");
		code.append(indent + "private static Map<String,Object> " + elementLabels.get(ingredient) + "(Map<String,Object> inputParams) {\n");
		
		code.append(indent + "\ttry {\n");
		
		List<String> inputParams = new ArrayList<String>();
		List<String> inputData = reverseAdjacencyList.get(ingredient);
		for(String data : inputData) inputParams.add((String)offeringParams.get(data));
		
		if(offeringParams.get("protocol").equals("HTTP")){
			
			importSet.add("org.apache.http.HttpEntity");
			importSet.add("org.apache.http.HttpResponse");
			importSet.add("java.io.InputStream");
			importSet.add("org.apache.http.client.HttpClient");
			importSet.add("org.apache.http.impl.client.HttpClientBuilder");
			
			code.append(indent + "\t\tHttpClient httpClient = HttpClientBuilder.create().build();\n");
			
			if(offeringParams.get("method").equals("GET")){
				importSet.add("org.apache.http.client.methods.HttpGet");
				String url = offeringParams.get("url").toString();
				code.append(indent + "\t\tString url = \"" + url + "\";\n");
				String separator = "?";
				for(String param : inputParams) {
					code.append(indent + "\t\turl = url + \"" + separator + param + "=\" + " + 
							"inputParams.get(\"" + param + "\").toString().replaceAll(\"\\\\s+\", \"+\");\n");
					separator = "&";
				}
				code.append(indent + "\t\tHttpGet httpGet = new HttpGet(url);\n");
				code.append(indent + "\t\tHttpResponse httpResponse = httpClient.execute(httpGet);\n");
			}
			else if(offeringParams.get("method").equals("POST")){
				importSet.add("org.apache.http.client.methods.HttpPost");
				importSet.add("org.apache.http.entity.StringEntity");
				String url = offeringParams.get("url").toString();
				code.append(indent + "\t\tString url = \"" + url + "\";\n");
				code.append(indent + "\t\tStringEntity entity = new StringEntity(JsonUtils.toPrettyString(inputParams));\n");
				code.append(indent + "\t\tHttpPost httpPost = new HttpPost(url);\n");
				code.append(indent + "\t\thttpPost.setHeader(\"Content-type\", \"application/json\");\n");
				code.append(indent + "\t\thttpPost.setEntity(entity);\n");
				code.append(indent + "\t\tHttpResponse httpResponse = httpClient.execute(httpPost);\n");
			}
			
			code.append(indent + "\t\tHttpEntity httpEntity = httpResponse.getEntity();\n");
			code.append(indent + "\t\tInputStream is = httpEntity.getContent();\n");
			code.append(indent + "\t\tMap<String,Object> outputParams = (Map<String,Object>) JsonUtils.fromInputStream(is, \"iso-8859-1\");\n");
			code.append(indent + "\t\treturn outputParams;\n");
			
		}
		
		else if(offeringParams.get("protocol").equals("COAP")){
			
			importSet.add("org.eclipse.californium.core.CoapClient");
			importSet.add("org.eclipse.californium.core.CoapResponse");

			String url = offeringParams.get("url").toString();
			code.append(indent + "\t\tCoapClient client = new CoapClient(\"" + url + "\");\n");
			
			if(offeringParams.get("method").equals("GET")){
				code.append(indent + "\t\tCoapResponse response = client.get();\n");
			}
			else if(offeringParams.get("method").equals("PUT")){
				importSet.add("org.eclipse.californium.core.coap.MediaTypeRegistry");
				if(!inputParams.isEmpty()) {
					String inputName = inputParams.get(0);
					code.append(indent + "\t\tString data = inputParams.get(\"" + inputName + "\").toString();\n");
					code.append(indent + "\t\tCoapResponse response = client.put(data, MediaTypeRegistry.TEXT_PLAIN);\n");
				}
			}
			
			List<String> outputs = adjacencyList.get(ingredient);
			if(!outputs.isEmpty()) {
				String output = outputs.get(0);
				String outputName = (String) offeringParams.get(output);
				code.append(indent + "\t\tString payload = new String(response.getPayload());\n");
				code.append(indent + "\t\tMap<String,Object> outputParams = new HashMap<String,Object>();\n");
				code.append(indent + "\t\toutputParams.put(\""+ outputName + "\", payload);\n");
				code.append(indent + "\t\treturn outputParams;\n");
			}
			else code.append(indent + "\t\treturn null;\n");
		}
		
		code.append(indent + "\t} catch (Exception e) {\n");
		code.append(indent + "\t\te.printStackTrace();\n");
		code.append(indent + "\t\treturn null;\n");
		code.append(indent + "\t}\n");

		code.append(indent + "}\n");
		return code.toString();
	}
	
}
