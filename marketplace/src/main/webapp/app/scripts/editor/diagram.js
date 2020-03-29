
function buildGraphFromAdjacencyList(adjacencyList, elementLabels, elementTypes, dataTypes, configAttributes) {

    var elements = [];
    var links = [];
    
    _.each(adjacencyList, function(targets, vertex) {
        elements.push(makeElement(vertex, elementLabels[vertex], elementTypes[vertex], dataTypes[vertex], configAttributes[vertex]));

        _.each(targets, function(target) {
            links.push(makeLink(vertex, target, elementTypes[vertex], elementTypes[target]));
        });
    });

    return elements.concat(links);
}

function makeLink(vertex, target, vertexType, targetType) {
	
	//console.log("Added link : " + vertex + " " +  target);
	
	if(vertexType=='output' && targetType=='input'){
		return new joint.shapes.fsa.Arrow({
	        source: {id: vertex},
	        target: {id: target},
	        attrs: { '.connection': { stroke: '#aa0000', 'stroke-width': 4 } , '.marker-target': { fill: '#aa0000', stroke: '#aa0000'}, '.link-tools': {display: 'none'}, '.marker-arrowheads': { display: 'none' }},
	        smooth: false,
	      //  labels: [
	           //      { position: 0.5, attrs: { text: { text: edgeinfo[1], fill: '#400080', 'font-size': 12, 'font-family': 'sans-serif' }, rect: { stroke: '#ffff00', 'stroke-width': 12, rx: 3, ry: 3 } }}
	          //   ]
	    });
	}
	else {
		//return new joint.shapes.erd.Line({
		return new joint.shapes.fsa.Arrow({
	        source: {id: vertex},
	        target: {id: target},
	        attrs: { '.connection': { stroke: '#000000', 'stroke-width': 2 }, '.link-tools': {display: 'none'}, '.marker-arrowheads': { display: 'none' }},
	        smooth: false,
	    });
	}
}

function makeElement(name, label, elementClass, dataType, config) {
	
	//console.log("Added item : " + name + " " +  elementClass);
	
	label = label.substring(label.lastIndexOf('_')+1);

    var maxLineLength = _.max(label.split('\n'), function(l) { return l.length; }).length;

    // Compute width/height of the rectangle based on the number
    // of lines in the label and the letter size. 0.6 * letterSize is
    // an approximation of the monospace font letter width.
    var letterSize = (elementClass=='input'||elementClass=='output') ? 12 : 16;
    var width = 20 + (letterSize * (0.6 * maxLineLength + 1));
    var height = 2 * ((label.split('\n').length + 1) * letterSize);
    
    if(config==undefined) config = {};
    config['type'] = elementClass;

    if(elementClass=='offering'){
    	return new joint.shapes.basic.Rect({
	        id: name,
	        size: { width: width, height: height },
	        attrs: {
	            rect: { rx: 5, ry: 5, fill: '#27AE40',stroke: '#555' },
	            text: { text: label, fill: '#ffffff', 'font-size': letterSize, stroke: '#000000', 'stroke-width': 0 },
	            custom: config
	        }
	    });
    }
    else if(elementClass=='input'){
    	//var fillColor = (name.endsWith('#Then')||name.endsWith('#Else')) ? '#fc3870' : ( name.endsWith('#Loop') ? '#ec7000' : '#7c68fc' );
	    return new joint.shapes.basic.Circle({
	        id: name,
	        size: { width: width, height: height },
	        attrs: {
	        	circle: { fill: '#5c68cc' },//#7c68fc'
	            text: { text: label, fill: '#ffffff', 'font-size': letterSize, stroke: '#000000', 'stroke-width': 0 },
	            custom: { 'type' : 'input', 'datatype': dataType }
	        }
	    }); 
    }
    else if(elementClass=='output'){
    	//var fillColor = (name.endsWith('#Then')||name.endsWith('#Else')) ? '#fc3870' : ( name.endsWith('#Loop') ? '#ec7000' : '#7c68fc' );
	    return new joint.shapes.basic.Circle({
	        id: name,
	        size: { width: width, height: height },
	        attrs: {
	        	circle: { fill: '#ac689c' },
	            text: { text: label, fill: '#ffffff', 'font-size': letterSize, stroke: '#000000', 'stroke-width': 0 },
	            custom: { 'type' : 'output', 'datatype': dataType }
	        }
	    }); 
    }
    else if(elementClass=='condition' || elementClass=='loop'){
	    return new joint.shapes.erd.IdentifyingRelationship({
	        id: name,
	        size: { width: width, height: height },
	        attrs: {
	            text: { text: label, 'font-size': letterSize },
	            custom: config
	        }
	    });
    }
    else{
    	return new joint.shapes.basic.Rect({
	        id: name,
	        size: { width: width, height: height },
	        attrs: {
	            text: { text: label, 'font-size': letterSize, 'font-family': 'monospace' },
	            rect: {
	                width: width, height: height,
	                rx: 5, ry: 5,
	                stroke: '#555',
	                custom: config
	            }
	        }
	    });
    }
}


function isEmptyObject( obj ) {
    for ( var name in obj ) {
        return false;
    }
    return true;
}


function drawRecipeGraph(graph, jsonData) {
	var adjacencyList = jsonData["adjacencyList"];
    var elementLabels = jsonData["elementLabels"];
    var elementTypes = jsonData["elementTypes"];
    var dataTypes = jsonData["dataTypes"];
    var configAttributes = jsonData["configAttributes"];

    //console.log("Json data " + JSON.stringify(jsonData));
    var cells = buildGraphFromAdjacencyList(adjacencyList, elementLabels, elementTypes, dataTypes, configAttributes);
//    graph.clear();
//    for(var i=0; i<cells.length; i++){
//    	graph.addCell(cells[i]);
//    }
    graph.resetCells(cells);
    joint.layout.DirectedGraph.layout(graph, { setLinkVertices: false });
}



function drawRecipe(json) {
	
	document.getElementById('paper-holder-loading').innerHTML = "";

    var graph = new joint.dia.Graph;
    var paper = new joint.dia.Paper({
        el: $('#paper-holder-loading'),
        gridSize: 1,
        model: graph
    });
	
    //console.log('Diagram Data = ' + JSON.stringify(json));
    
    drawRecipeGraph(graph, json);
    paper.fitToContent({padding:10,allowNewOrigin:'any',minWidth:$('#paper-holder-loading').width(),minHeight:$('#paper-holder-loading').height()});

/* On click pop up box with attributes*/

    paper.on('cell:pointerdblclick',function(cellView){
    	
    	var item = cellView.model.get('id');
    	config = json["configAttributes"][item];
    	
    	if(config != null && !isEmptyObject(config)){
    		
	    	var modal = document.getElementById('popup_model');
	    	var modal_rows = document.getElementById('modal_rows');
	    	var btn_config = document.getElementById('btn-config');
	    	var span = document.getElementsByClassName("closeButton")[0];
	    	modal.style.display = "block";
	    	
	    	window.onclick = function(event) {
	    	    if (event.target == modal) {
	    	        modal.style.display = "none";
	    	    }
	    	}
	    	
	    	span.onclick = function() {
	    	    modal.style.display = "none";
	    	}
	    	
	    	btn_config.onclick = function() {
	    	    modal.style.display = "none";
	    	}

	    	while(modal_rows.firstChild){
	    		modal_rows.removeChild(modal_rows.firstChild);
	    	}
	    	modal_rows.style.display = 'inline-block';
	    	
	    	_.each(config, function(value, key) {
	    		if(key!='type'){
		    		var row = document.createElement('div');
		    		var input = document.createElement('input');
		    		input.value = value;
		    		row.innerHTML = "<br>" + key + "<br>";
		    		row.style.display = 'inline-block';
		    		row.appendChild(input);
		    		modal_rows.appendChild(row);
		    		modal_rows.appendChild(document.createElement('br'));
	    		}
	    	});
    	
    	}
    
    });

}
    
    
