
var Rappid = Backbone.Router.extend({

    routes: {
        '*path': 'home'
    },

    initialize: function(options) {
        
        this.options = options || {};
    },

    home: function() {

        this.initializeEditor();
    },

    initializeEditor: function() {
    	
        this.inputMap = {};
        this.outputMap = {};
        this.inLinks = {};
        this.outLinks = {};
        
        this.allowedDataTypes = {};
        
        this.offerings = [];
        this.conditions = [];
        this.loops = [];

        this.inspectorClosedGroups = {};

        //this.loadRecipeCategories();
        this.loadOfferingCategories();
        this.loadAllDataTypes();
        this.initializeCommandManager();
        this.initializePaper();
        this.initializeStencil();
        this.initializeSelection();
        this.initializeHaloAndInspector();
        this.initializeNavigator();
        this.initializeClipboard();
        this.initializeToolbar();
         
        // Intentionally commented out. See the `initializeValidator()` method for reasons.
        // Uncomment for demo purposes.
        // this.initializeValidator();
        // Commented out by default. You need to run `node channelHub.js` in order to make
        // channels working. See the documentation to the joint.com.Channel plugin for details.
        //this.initializeChannel('ws://jointjs.com:4141');
        if (this.options.channelUrl) {
            this.initializeChannel(this.options.channelUrl);
        }
        
        $('#btn-create').on('click', _.bind(this.createRecipe, this));
    },
    

    loadOfferingCategories: function() {	
    	scope = this;
        var xmlHttp = new XMLHttpRequest();
        xmlHttp.onreadystatechange = function() {
            if (xmlHttp.readyState == 4 && xmlHttp.status == 200){
            	scope.offeringCategories = JSON.parse(xmlHttp.responseText);
            	for(var i=0; i<scope.offeringCategories.length; i++) {
            		$('#recipe_category').append($('<option>', { value: (i+1), text: scope.offeringCategories[i] }));
            	}
            }
        }
        xmlHttp.open( "GET", './offering_categories', true );
        xmlHttp.send(null);
    },
    
    
    loadAllDataTypes: function() {	
    	scope = this;
        var xmlHttp = new XMLHttpRequest();
        xmlHttp.onreadystatechange = function() {
            if (xmlHttp.readyState == 4 && xmlHttp.status == 200){
            	var dataTypes = JSON.parse(xmlHttp.responseText);
            	scope.allowedDataTypes['all'] = dataTypes;
            }
        }
        xmlHttp.open( "GET", './data_types?category=bigiot:allOfferings', true );
        xmlHttp.send(null);
    },
    
    
    loadDataTypes: function(offeringCategory) {	
    	scope = this;
        var xmlHttp = new XMLHttpRequest();
        xmlHttp.onreadystatechange = function() {
            if (xmlHttp.readyState == 4 && xmlHttp.status == 200){
            	scope.allowedDataTypes[offeringCategory] = JSON.parse(xmlHttp.responseText);
            }
        }
        xmlHttp.open( "GET", './data_types?category='+offeringCategory, true );
        xmlHttp.send(null);
    },
    
    
    initializeCommandManager: function() {

        this.commandManager = new joint.dia.CommandManager({ graph: this.graph });

        KeyboardJS.on('ctrl + z', _.bind(function() {

            this.commandManager.undo();
            this.selectionView.cancelSelection();
        }, this));
        
    },
    
    
    // Create a graph, paper and wrap the paper in a PaperScroller.
    initializePaper: function() {
        
        this.graph = new joint.dia.Graph;

        this.graph.on('add', function(cell, collection, opt) {
        	        	
    		if(cell.attributes.type === 'basic.Rect' || cell.attributes.type === 'erd.IdentifyingRelationship') {
    			this.inputMap[cell.id] = [];
    			this.outputMap[cell.id] = [];
    			
    			if(cell.attributes.attrs.custom.type == 'offering'){
    				this.offerings.push(cell.id);
    				cell.attributes.attrs.text.text = 'Offering' + this.offerings.length;
    			}
    			else if(cell.attributes.attrs.custom.type == 'condition'){
    				this.conditions.push(cell.id);
    				cell.attributes.attrs.text.text = 'If' + this.conditions.length;
    			}
    			else if(cell.attributes.attrs.custom.type == 'loop'){
    				this.loops.push(cell.id);
    				cell.attributes.attrs.text.text = 'For' + this.loops.length;
    			}
    		}
        	
            if (opt.stencil) {
//                this.createInspector(cell);
//                this.commandManager.stopListening();
//                this.inspector.updateCell();
//                this.commandManager.listen();
//                this.inspector.$('[data-attribute]:first').focus();
            }
        }, this);

        this.paper = new joint.dia.Paper({
            width: 1000,
            height: 1000,
            gridSize: 10,
            model: this.graph,
            defaultLink: function(element) {   //Defining default links for each elements so different arrows can be drawn from it
	            //this differentiates that when we connect relationship and final recipe, we get solid arrow or else dotted ones
	            if(element.model.attributes.type=== 'basic.Circle') {
	            	return new joint.shapes.fsa.Arrow({
	        	        attrs: { '.connection': { stroke: '#aa0000', 'stroke-width': 4 }, '.marker-target': { fill: '#aa0000', stroke: '#aa0000'}, '.marker-arrowheads': { display: 'none' }},
	        	        smooth: false
	        	    });
	            }
	            else {
	            	return new joint.shapes.fsa.Arrow({
	        	        attrs: { '.connection': { stroke: '#000000', 'stroke-width': 2 }, '.link-tools': {display: 'none'}, '.marker-arrowheads': { display: 'none' }},
	        	        smooth: false
	        	    });
	            }
            },
            
            interactive: function(cellView) {
                if (cellView.model instanceof joint.dia.Link) {
                    return { vertexAdd: false };
                }
                return true;
            }
        });


        this.paperScroller = new joint.ui.PaperScroller({
            autoResizePaper: true,
            padding: 0,
            paper: this.paper
        });

        this.paperScroller.$el.appendTo('.paper-container');

        this.paperScroller.center();

        this.graph.on('add', this.initializeLinkTooltips, this);

        $('.paper-scroller').on('mousewheel DOMMouseScroll', _.bind(function(evt) {

            if (_.contains(KeyboardJS.activeKeys(), 'alt')) {
                evt.preventDefault();
                var delta = Math.max(-1, Math.min(1, (evt.originalEvent.wheelDelta || -evt.originalEvent.detail)));
	        var offset = this.paperScroller.$el.offset();
	        var o = this.paperScroller.toLocalPoint(evt.pageX - offset.left, evt.pageY - offset.top);
                this.paperScroller.zoom(delta / 10, { min: 0.2, max: 5, ox: o.x, oy: o.y });
            }

        }, this));

        this.snapLines = new joint.ui.Snaplines({ paper: this.paper });
        
    },
    

    // Create and populate stencil.
    initializeStencil: function() {

        this.stencil = new joint.ui.Stencil({
            paper: this.paperScroller,
            width: 240,
            groups: Stencil.groups,
            dropAnimation: true,
            search: {
                '*': ['type','attrs/text/text','attrs/.label/text'],
                'org.Member': ['attrs/.rank/text','attrs/.name/text']
            }
        });

        $('.stencil-container').append(this.stencil.render().el);

        this.stencil.$el.on('contextmenu', function(evt) { evt.preventDefault(); });
        $('.stencil-paper-drag').on('contextmenu', function(evt) { evt.preventDefault(); });

        var layoutOptions = {
            columnWidth: this.stencil.options.width / 2 - 10,
            columns: 1,
            rowHeight: 80,
            resizeToFit: true,
            dy: 10,
            dx: 10
        };

        _.each(Stencil.groups, function(group, name) {
            
            this.stencil.load(Stencil.shapes[name], name);
            joint.layout.GridLayout.layout(this.stencil.getGraph(name), layoutOptions);
            this.stencil.getPaper(name).fitToContent(1, 1, 10);

        }, this);

        this.stencil.on('filter', function(graph) {
            joint.layout.GridLayout.layout(graph, layoutOptions);
        });

        $('.stencil-container .btn-expand').on('click', _.bind(this.stencil.openGroups, this.stencil));
        $('.stencil-container .btn-collapse').on('click', _.bind(this.stencil.closeGroups, this.stencil));
        
    },
    
    
    initializeSelection: function() {
        
        this.selection = new Backbone.Collection;
        this.selectionView = new joint.ui.SelectionView({ paper: this.paper,  model: this.selection });

        // Initiate selecting when the user grabs the blank area of the paper while the Shift key is pressed.
        // Otherwise, initiate paper pan.
        this.paper.on('blank:pointerdown', function(evt, x, y) {
        	$(".inout button").hide();
        	
        	if(this.inspector!=undefined) {
	        	this.inspector.updateCell();
	            this.inspector.remove();
        	}
            
            if (_.contains(KeyboardJS.activeKeys(), 'shift')) {
                this.selectionView.startSelecting(evt, x, y);
            } else {
                this.selectionView.cancelSelection();
                this.paperScroller.startPanning(evt, x, y);
            }
        }, this);

        this.paper.on('cell:pointerdown', function(cellView, evt) {
            // Select an element if CTRL/Meta key is pressed while the element is clicked.
            if ((evt.ctrlKey || evt.metaKey) && !(cellView.model instanceof joint.dia.Link)) {
                this.selection.add(cellView.model);
                this.selectionView.createSelectionBox(cellView);
            }
        }, this);

        this.selectionView.on('selection-box:pointerdown', function(evt) {
            // Unselect an element if the CTRL/Meta key is pressed while a selected element is clicked.
            if (evt.ctrlKey || evt.metaKey) {
                var cell = this.selection.get($(evt.target).data('model'));
                this.selection.reset(this.selection.without(cell));
                this.selectionView.destroySelectionBox(this.paper.findViewByModel(cell));
            }
        }, this);

        // Disable context menu inside the paper.
        // This prevents from context menu being shown when selecting individual elements with Ctrl in OS X.
        this.paper.el.oncontextmenu = function(evt) { evt.preventDefault(); };

        KeyboardJS.on('delete, backspace', _.bind(function(evt, keys) {

            if (!$.contains(evt.target, this.paper.el)) {
                // remove selected elements from the paper only if the target is the paper
                return;
            }
            
            this.commandManager.initBatchCommand();
            this.selection.invoke('remove');
            this.commandManager.storeBatchCommand();
            this.selectionView.cancelSelection();

            // Prevent Backspace from navigating one page back (happens in FF).
            if (_.contains(keys, 'backspace') && !$(evt.target).is("input, textarea")) {
                evt.preventDefault();
            }

        }, this));
    },
    

    createInspector: function(cellView) {

        var cell = cellView.model || cellView;

        // No need to re-render inspector if the cellView didn't change.
        //if (!this.inspector || this.inspector.options.cell !== cell) {

            // Is there an inspector that has not been removed yet.
            // Note that an inspector can be also removed when the underlying cell is removed.
            if (this.inspector && this.inspector.el.parentNode) {
            	
                this.inspectorClosedGroups[this.inspector.options.cell.id] = _.map(app.inspector.$('.group.closed'), function(g) {
                	return $(g).attr('data-name');
                });
                
                // Clean up the old inspector if there was one.
                if(this.inspector!=undefined) {
                	this.inspector.updateCell();
                	this.inspector.remove();
                }
            }
            
            createInspectorDefs(this);
            
            var inspectorDefs = this.InspectorDefs[cell.attributes.attrs.custom.type];
            //console.log("Inspector : " + JSON.stringify(inspectorDefs));
            
            if(cell.attributes.attrs.custom.type=='input' || cell.attributes.attrs.custom.type=='output') {
            	var offeringCell = this.graph.getCell(cell.attributes.attrs.custom.offering);
            	if(offeringCell.attributes.attrs.custom.type=='condition' || offeringCell.attributes.attrs.custom.type=='loop') {
            		inspectorDefs.inputs.attrs.custom.datatype.options = ["<-- Select -->"].concat(this.allowedDataTypes['all']);
            	}
            	else{
	        		var offeringCategory = offeringCell.attributes.attrs.custom['category'];
	        		if(offeringCategory != undefined && this.allowedDataTypes[offeringCategory] != undefined) {
	        			inspectorDefs.inputs.attrs.custom.datatype.options = ["<-- Select -->"].concat(this.allowedDataTypes[offeringCategory]);
	        		}
            	}
            }
            
            this.inspector = new joint.ui.Inspector({
                inputs: inspectorDefs ? inspectorDefs.inputs : {},
                groups: inspectorDefs ? inspectorDefs.groups : {},
                cell: cell
            });
            
            this.inspector.render();
            $('.inspector-container').html(this.inspector.el);

            if (this.inspectorClosedGroups[cell.id]) {

		_.each(this.inspectorClosedGroups[cell.id], this.inspector.closeGroup, this.inspector);

            } else {
                //this.inspector.$('.group:not(:first-child)').addClass('closed');
            }
            
            if(cell.attributes.attrs.custom.type=='offering') {
            	var scope = this;
            	$('.inspector-container select').on('change', function (e) {
            		var offeringCategory = this.value;
            		if(scope.allowedDataTypes[offeringCategory]==undefined) {
            			scope.allowedDataTypes[offeringCategory] = [];
            			scope.loadDataTypes(offeringCategory);
        			}
            	});
            }

            $('.inspector-container textarea').keypress(function (e) {
                var regex = new RegExp("^[a-zA-Z0-9]+$");
                var str = String.fromCharCode(!e.charCode ? e.which : e.charCode);
                if (regex.test(str)) {
                    return true;
                }
                
                e.preventDefault();
                return false;
            });

       // }
    },
    
    
    initializeHaloAndInspector: function() {
    	
    	$(".inout button").hide();
    	this.selectedCell = null;
    	$('#btn-input').on('click', _.bind(this.addInput, this));
    	$('#btn-output').on('click', _.bind(this.addOutput, this));

        this.paper.on('cell:pointerup', function(cellView, evt) {
        	
            if (cellView.model instanceof joint.dia.Link || this.selection.contains(cellView.model)) return;
            
            if (cellView.model instanceof joint.shapes.basic.Rect || cellView.model instanceof joint.shapes.erd.IdentifyingRelationship) {
            	this.selectedCell = cellView;
            	$(".inout button").show();
            }
            else $(".inout button").hide();

            this.createHalo(cellView);
            
        }, this);
        
        var scope = this;

        this.paper.on('link:options', function(evt, cellView, x, y) {
            //this.createInspector(cellView);
        }, this);
        
        this.graph.on('remove', function(cell, collection, opt) {
    	   if (cell.isLink()) {
    		   if(cell.get('target')!=undefined) {
    			   if(scope.inLinks[cell.get('target').id]!=undefined){
	    			   var delIndex = scope.inLinks[cell.get('target').id].indexOf(cell.id);
	    			   if (delIndex >= 0) scope.inLinks[cell.get('target').id].splice(delIndex, 1);
    			   }
    			   if(scope.outLinks[cell.get('source').id]!=undefined){
	    			   var delIndex = scope.outLinks[cell.get('source').id].indexOf(cell.id);
	    			   if (delIndex >= 0) scope.outLinks[cell.get('source').id].splice(delIndex, 1);
    			   }
    		   }
    	   }
    	   else {
    		   if(cell.attributes.type === 'basic.Rect' || cell.attributes.type === 'erd.IdentifyingRelationship') {
    			   $(".inout button").hide();
    			   if(scope.offerings.indexOf(cell.id)>=0) scope.offerings.splice(scope.offerings.indexOf(cell.id), 1);
    			   else if(scope.conditions.indexOf(cell.id)>=0) scope.conditions.splice(scope.conditions.indexOf(cell.id), 1);
    			   else if(scope.loops.indexOf(cell.id)>=0) scope.loops.splice(scope.loops.indexOf(cell.id), 1);
    			   
    		       if(scope.inputMap[cell.id]!=undefined) {
    		    	   var inputs = scope.inputMap[cell.id].slice();
    		    	   for(var i = 0; i < inputs.length; i++) {
     					   var inputCell = scope.graph.getCell(inputs[i]);
     					   if(inputCell!=undefined) inputCell.remove();
     				   }
     			   }
     			   if(scope.outputMap[cell.id]!=undefined) {
     				   var outputs = scope.outputMap[cell.id].slice();
     				   for(var i = 0; i < outputs.length; i++) {
     					   var outputCell = scope.graph.getCell(outputs[i]);
    					   if(outputCell!=undefined) outputCell.remove();
     				   }
     			   }
    		   }
    		   else if(cell.attributes.type === 'basic.Circle') {
    			   /*var type = cell.attributes.attrs.custom['type'];
    			   var offering = cell.attributes.attrs.custom['offering'];
    			   if(offering != undefined) {
    				   if(type==='input' && scope.inputMap[offering]!=undefined){
    	    			   var delIndex = scope.inputMap[offering].indexOf(cell.id);
    	    			   if (delIndex >= 0) scope.inputMap[offering].splice(delIndex, 1);
        			   }
    				   else if(type==='output' && scope.outputMap[offering]!=undefined){
    	    			   var delIndex = scope.outputMap[offering].indexOf(cell.id);
    	    			   if (delIndex >= 0) scope.outputMap[offering].splice(delIndex, 1);
        			   }
    			   }*/
    			   
    			   var arrow = cell.attributes.attrs.custom['arrow'];
    			   if(arrow != undefined) {
    				   var arowCell = scope.graph.getCell(arrow);
    				   if(arowCell!=undefined) arowCell.remove();
    			   }
    			   
    			   if(scope.inLinks[cell.id]!=undefined) {
    				   for(var i = 0; i < scope.inLinks[cell.id].length; i++) {
    					   var linkCell = scope.graph.getCell(scope.inLinks[cell.id][i]);
    					   if(linkCell!=undefined) linkCell.remove();
    				   }
    			   }
    			   if(scope.outLinks[cell.id]!=undefined) {
    				   for(var i = 0; i < scope.outLinks[cell.id].length; i++) {
    					   var linkCell = scope.graph.getCell(scope.outLinks[cell.id][i]);
    					   if(linkCell!=undefined) linkCell.remove();
    				   }
    			   }
    		   }
    	   }
    	});
        
    },
    
    
    checkCategory: function(offeringID) {
		var offeringCell = this.graph.getCell(offeringID);
		if(offeringCell.attributes.attrs.custom.type=='condition' || offeringCell.attributes.attrs.custom.type=='loop') {
			return true;
    	}
		var offeringCategory = offeringCell.attributes.attrs.custom['category'];
		if(offeringCategory==undefined || offeringCategory==='<-- Select -->') {
	    	new joint.ui.FlashMessage({
	            type: 'alert',
	            closeAnimation: false,
	            modal: true,
	            title: 'Create Recipe',
	            content: 'Please specify the offering category for ' + offeringCell.attributes.attrs.text['text']
	            }).open();
	    	return false;
		}
		else return true;
    },
    
    
    addInput: function() {
    	
    	var offering = this.selectedCell.model;
    	if(this.graph.getCell(offering.id)==undefined) return;
    	//console.log("inboundLinks " + this.graph.getConnectedLinks(this.graph.getCell(offering.id), { inbound: true }));
    	
    	if(!this.checkCategory(offering.id)) return;
    	
    	var index = this.inputMap[offering.id].length;
    	var xpos = index%2==0 ? (index)/2 : -(index+1)/2;
    	var input = new joint.shapes.basic.Circle({
    		position: {x: offering.attributes.position.x + 100*xpos , y: offering.attributes.position.y - 80},
	        size: { width: 80, height: 40 },
	        attrs: {
	        	circle: { fill: '#5c68cc' },
	            text: { text: "input"+(index+1), fill: '#ffffff', 'font-size': 14, stroke: '#000000', 'stroke-width': 0 },
	            custom: { type: 'input', 'offering': offering.id}
	        }
	    });
        this.graph.addCell(input);
        
        var arrow = new joint.shapes.fsa.Arrow({
	        source: {id: input},
	        target: {id: offering},
	        attrs: { '.connection': { stroke: '#000000', 'stroke-width': 2 }, '.link-tools': {display: 'none'}, '.marker-arrowheads': { display: 'none' }},
	        smooth: false,
	    });
        this.graph.addCell(arrow);
        input.attributes.attrs.custom.arrow = arrow.id;
        this.inputMap[offering.id].push(input.id);
        
        arrow.on('change',function(element,value) {
        	console.log("arrow changed");
        });
    },
    
    
    addOutput: function() {
    	var offering = this.selectedCell.model;
    	if(this.graph.getCell(offering.id)==undefined) return;
    	//console.log("offering : " + JSON.stringify(offering));
    	
    	if(!this.checkCategory(offering.id)) return;
    	
    	var index = this.outputMap[offering.id].length;
    	var xpos = index%2==0 ? (index)/2 : -(index+1)/2;
    	var output = new joint.shapes.basic.Circle({
    		position: {x: offering.attributes.position.x + 100*xpos , y: offering.attributes.position.y + 80},
	        size: { width: 80, height: 40 },
	        attrs: {
	        	circle: { fill: '#ac689c' },
	            text: { text: "output"+(index+1), fill: '#ffffff', 'font-size': 14, stroke: '#000000', 'stroke-width': 0 },
	            custom: { type: 'output', 'offering': offering.id}
	        }
	    });
        this.graph.addCell(output);
        
        var arrow = new joint.shapes.fsa.Arrow({
	        source: {id: offering},
	        target: {id: output},
	        attrs: { '.connection': { stroke: '#000000', 'stroke-width': 2 }, '.link-tools': {display: 'none'}, '.marker-arrowheads': { display: 'none' }},
	        smooth: false,
	    });
        this.graph.addCell(arrow);
        output.attributes.attrs.custom.arrow = arrow.id;
        this.outputMap[offering.id].push(output.id);
    },
    
    
    createHalo: function(cellView) {
    	
    	// In order to display halo link magnets on top of the freetransform div we have to create the
        // freetransform first. This is necessary for IE9+ where pointer-events don't work and we wouldn't
        // be able to access magnets hidden behind the div.
        var freetransform = new joint.ui.FreeTransform({ cellView: cellView, allowRotation: false });
        var halo = new joint.ui.Halo({ cellView: cellView, boxContent: false });

        // As we're using the FreeTransform plugin, there is no need for an extra resize tool in Halo.
        // Therefore, remove the resize tool handle and reposition the clone tool handle to make the
        // handles nicely spread around the elements.
        halo.removeHandle('resize');
        halo.removeHandle('clone');
        halo.removeHandle('fork');
        halo.removeHandle('unlink');
        halo.removeHandle('rotate');
        
        if(cellView.model.attributes.attrs.custom['type'] !== 'output') {
        	halo.removeHandle('link');
        }
        
        /*if(cellView.model.attributes.type === 'basic.Rect' || cellView.model.attributes.type === 'erd.IdentifyingRelationship') {
            //halo.removeHandle('remove');
            //halo.changeHandle('link',{position: 'w'});
        	//halo.changeHandle('link',{position: 'w'});
        }*/

        var scope = this;

        //logic on the link that we create
        halo.on('action:link:add', function(link) {
            scope.linkValidation(link);
        });

        freetransform.render();
        halo.render();
        this.initializeHaloTooltips(halo);
        this.createInspector(cellView);
        this.selectionView.cancelSelection();
        this.selection.reset([cellView.model]);
    },
    

    //adding validation on every connection that we create between the elements in the graph.
    linkValidation: function(link) {

        if(link.getTargetElement() === null) {
            link.remove();
            return;
        }
        
        var source = link.getSourceElement();
        var target = link.getTargetElement();
        
        var class1 = source.attributes.type;
        var class2 = target.attributes.type;
        
        var type1 = source.attributes.attrs.custom['type'];
        var type2 = target.attributes.attrs.custom['type'];
        
        var offering1 = source.attributes.attrs.custom['offering'];
        var offering2 = target.attributes.attrs.custom['offering'];
        
        //console.log("connect "+offering1+" "+offering2);
        //console.log("connect "+source.id+" "+target.id);

        if(class1 === 'basic.Circle' && class2 === 'basic.Circle' && type1 === 'output' && type2 === 'input' && offering1 !== offering2) {
        	
        	if(this.inLinks[target.id]!=undefined && this.inLinks[target.id].length>0) {
            	(new joint.ui.FlashMessage({
                    type: 'alert',
                    closeAnimation: false,
                    modal: true,
                title: 'Validation:',
                content: 'An input can be connected only with one output!'
                })).open();
                link.remove();
                return;
            }
        	
            var datatype1 = source.attributes.attrs.custom['datatype'];
            var datatype2 = target.attributes.attrs.custom['datatype'];
            
            if(datatype1 == undefined || datatype2 == undefined || datatype1 === '<-- Select -->' || datatype2 === '<-- Select -->') {
            	new joint.ui.FlashMessage({
                    type: 'alert',
                    closeAnimation: false,
                    modal: true,
                    title: 'Validation:',
                    content: 'Please specify the data types of the nodes before connecting.'
                    }).open();
                    link.remove();
            }
            else if(datatype1 !== datatype2) {
            	new joint.ui.FlashMessage({
                    type: 'alert',
                    closeAnimation: false,
                    modal: true,
                    title: 'Validation:',
                    content: 'The data type of the nodes are not matching.'
                    }).open();
                    link.remove();
            }
            else {
            	//Link is added in this case
            	if(this.inLinks[target.id]==undefined) this.inLinks[target.id] = [];
            	this.inLinks[target.id].push(link.id);
            	
            	if(this.outLinks[source.id]==undefined) this.outLinks[source.id] = [];
            	this.outLinks[source.id].push(link.id);
            }
        }
        else {
        	(new joint.ui.FlashMessage({
                type: 'alert',
                closeAnimation: false,
                modal: true,
            title: 'Validation:',
            content: 'A connection can be made only from an output to an input of another offering!'
            })).open();
            link.remove();
        }

    },
    

    initializeNavigator: function() {

        var navigator = this.navigator = new joint.ui.Navigator({
            width: 240,
            height: 115,
            paperScroller: this.paperScroller,
            zoomOptions: { max: 5, min: 0.2 }
        });

        navigator.$el.appendTo('.navigator-container');
        navigator.render();
    },
    

    initializeHaloTooltips: function(halo) {

        new joint.ui.Tooltip({
            className: 'tooltip small',
            target: halo.$('.remove'),
            content: 'Click to remove the object',
            direction: 'right',
            right: halo.$('.remove'),
            padding: 15
        });
        
        new joint.ui.Tooltip({   
            className: 'tooltip small',
            target: halo.$('.link'),
            content: 'Click and drag to connect the object',
            direction: 'left',
            left: halo.$('.link'),
            padding: 15
        });

    },
    

    initializeClipboard: function() {
        this.clipboard = new joint.ui.Clipboard;
    },
    

    initializeToolbar: function() {
    	$('#btn-open').on('click', _.bind(this.openRecipe, this));
        $('#btn-save').on('click', _.bind(this.saveRecipe, this));
        $('#btn-undo').on('click', _.bind(this.commandManager.undo, this.commandManager));
        $('#btn-redo').on('click', _.bind(this.commandManager.redo, this.commandManager));
        $('#btn-clear').on('click', _.bind(this.graph.clear, this.graph));
      
        $('#file-input').on('change', _.bind(this.readRecipeFile, this));
    },
    
    
    openRecipe: function() {
        $('#file-input').trigger('click');
    },
    
    
    readRecipeFile: function(e) {
    	var file = e.target.files[0];
    	scope = this;
    	if (file) {
	    	var reader = new FileReader();
	    	reader.onload = function(e) {
	    		var contents = e.target.result;
	    		//console.log("File content " + contents);JSON.stringify(recipe_params)
	    		var request = new XMLHttpRequest();
		        request.onload = function () {
		        	var status = request.status;
		        	var data = JSON.parse(request.responseText);
                    console.log(data);
		        	drawRecipeGraph(scope.graph, data);
		        	scope.paper.fitToContent({padding:50,allowNewOrigin:'any',minWidth:1000,minHeight:1000});
		        	$('#recipe_name').val(data["recipeName"]);
		        	$('#description').val(data["recipeDescription"]);
		        	$('#recipe_category').val(scope.offeringCategories.indexOf(data["recipeCategory"])+1);
		        	scope.createDataStructures();
		    	}
	    		request.open("POST", './load_recipe_file', true);
		        request.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
		        request.send(JSON.stringify({'data':contents, 'format':'N3'}));
	    	};
	    	reader.readAsText(file);
    	}
    },
    
    
    createDataStructures: function() {
    	this.inputMap = {};
        this.outputMap = {};
        this.inLinks = {};
        this.outLinks = {};
        
        this.offerings = [];
        this.conditions = [];
        this.loops = [];
        
        var cells = this.graph.getCells();
        for(var i=0; i<cells.length; i++){
        	var cell = cells[i];
        	if(cell.attributes.type === 'basic.Rect' || cell.attributes.type === 'erd.IdentifyingRelationship') {
    			this.inputMap[cell.id] = [];
    			this.outputMap[cell.id] = [];
    			
    			if(cell.attributes.attrs.custom.type == 'offering'){
    				this.offerings.push(cell.id);
    			}
    			else if(cell.attributes.attrs.custom.type == 'condition'){
    				this.conditions.push(cell.id);
    			}
    			else if(cell.attributes.attrs.custom.type == 'loop'){
    				this.loops.push(cell.id);
    			}
    		}
        }
        for(var i=0; i<cells.length; i++){
        	var cell = cells[i];
        	if (cell.isLink()) {
    			
        		if(cell.get('source')!=undefined && cell.get('target')!=undefined) {
        		
        			var source = this.graph.getCell(cell.get('source').id);
        			var target = this.graph.getCell(cell.get('target').id);
        			
        			if(source!=undefined && target!=undefined) {
        				if(source.attributes.type === 'basic.Rect' || source.attributes.type === 'erd.IdentifyingRelationship') {
        					target.attributes.attrs.custom['offering'] = source.id;
        					this.outputMap[source.id].push(target.id);
        				}
        				else if(target.attributes.type === 'basic.Rect' || target.attributes.type === 'erd.IdentifyingRelationship') {
        					source.attributes.attrs.custom['offering'] = target.id;
        					this.inputMap[target.id].push(source.id);
        				}
        				else {
        	            	if(this.inLinks[target.id]==undefined) this.inLinks[target.id] = [];
        	            	this.inLinks[target.id].push(cell.id);
        	            	
        	            	if(this.outLinks[source.id]==undefined) this.outLinks[source.id] = [];
        	            	this.outLinks[source.id].push(cell.id);
        				}
        			}
     		    }
    		}
        }
    },
    
    
    validateGraph: function(writeToFile) {
    	
    	this.recipeName = $('#recipe_name').val();
    	this.description = $('#description').val();
    	
    	if(this.recipeName==undefined || this.recipeName==null || this.recipeName.length==0) return 'Please enter a recipe name!';
    	//this.recipeName = this.recipeName.replace(/\s/g,'');
    	var regex = new RegExp("^[a-zA-Z0-9]+$");
        if (!regex.test(this.recipeName)) {
            return 'Recipe name should only contain alpha numeric characters!';
        }
    	
    	if(this.description==undefined || this.description==null) return 'Please enter a recipe description!';
    	this.description = this.description.replace(/^\s+|\s+$/g, '');
       	if(this.description.length==0) return 'Please enter a recipe description!';

       	if($( "#recipe_category" ).val()==0) return 'Please select a recipe category!';
    	this.recipeCategory = $( "#recipe_category option:selected" ).text();
    	
    	allOfferings = [];
    	var union = this.offerings.concat(this.conditions).concat(this.loops);
    	for(var i = 0; i < union.length; i++) {
    		if(this.graph.getCell(union[i])!=undefined) {
    			allOfferings.push(union[i]);
    		}
    	}
    	if(allOfferings.length==0) return 'The graph is empty!';
    	
    	this.offeringNames = [];
    	this.offeringTypesMap = {};
    	this.offeringCategoriesMap = {};
    	this.dataNamesMap = {};
    	this.dataTypesMap = {};
    	this.extraParamsMap = {};
    	this.extraParamsMap = {};
    	
    	for(var i = 0; i < allOfferings.length; i++) {
    		var offeringCell = this.graph.getCell(allOfferings[i]);
    		this.offeringNames[i] = offeringCell.attributes.attrs.text['text'];
    		if(this.offeringNames[i]==undefined || this.offeringNames[i]==null) return 'Error : One of the offerings does not have a name!';
    		this.offeringNames[i] = this.offeringNames[i].replace(/\s/g,'');
    		if(this.offeringNames[i].length==0) return 'Error : One of the offerings does not have a name!';
    		for(var j = 0; j < i; j++) {
    			if(this.offeringNames[j]===this.offeringNames[i]) return 'Error : Duplicate offering name : ' + this.offeringNames[i];
    		}
    		
    		var offeringType = offeringCell.attributes.attrs.custom['type'];
    		if(offeringType === 'offering'){
    			this.offeringTypesMap[allOfferings[i]] = 'bigiot:Offering';
	    		var offeringCategory = offeringCell.attributes.attrs.custom['category'];
	    		if(offeringCategory==undefined || offeringCategory==='<-- Select -->') return 'Please specify the offering category for ' + this.offeringNames[i];
	    		this.offeringCategoriesMap[allOfferings[i]] = offeringCategory;
    		}
    		else if(offeringType === 'condition') this.offeringTypesMap[allOfferings[i]] = 'pattern:If_Condition';
    		else if(offeringType === 'loop') this.offeringTypesMap[allOfferings[i]] = 'pattern:For_Loop';
    		
    		var inputNames = [];
    		var inputs = this.inputMap[allOfferings[i]];
			if(inputs!=undefined) {
	     	   	for(var j = 0; j < inputs.length; j++) {
	     	   		var inputCell = this.graph.getCell(inputs[j]);
	     	   		if(inputCell!=undefined) {
	     	   			inputNames[j] = inputCell.attributes.attrs.text['text'];
		     	   		if(inputNames[j]==undefined || inputNames[j]==null) return 'Error : One of the inputs of ' +  this.offeringNames[i] + ' does not have a name!';
		     	   		inputNames[j] = inputNames[j].replace(/\s/g,'');
		        		if(inputNames[j].length==0) return 'Error : One of the inputs of ' +  this.offeringNames[i] + ' does not have a name!';
		        		for(var k = 0; k < j; k++) {
		        			if(inputNames[k]===inputNames[j]) return 'Error : Duplicate input name : ' + inputNames[j];
		        		}
		        		this.dataNamesMap[inputs[j]] = this.offeringNames[i] + '_input_' + inputNames[j];
		        		
		        		var dataType = inputCell.attributes.attrs.custom['datatype'];
		        		if(dataType==undefined || dataType==='<-- Select -->') return 'Please specify the data type for the input \'' + inputNames[j] + '\' of ' + this.offeringNames[i];
		        		for(var k = 0; k < j; k++) {
		        			if(this.dataTypesMap[inputs[k]]===dataType) return this.offeringNames[i] + 'has multiple inputs of type ' + dataType;
		        		}
		        		this.dataTypesMap[inputs[j]] = dataType;
	     	   		}
	     	   		else console.log('input cell ' + inputs[j] + ' is missing');
	     	   	}
			}
			
			var outputNames = [];
    		var outputs = this.outputMap[allOfferings[i]];
			if(outputs!=undefined) {
	     	   	for(var j = 0; j < outputs.length; j++) {
	     	   		var outputCell = this.graph.getCell(outputs[j]);
	     	   		if(outputCell!=undefined) {
	     	   		outputNames[j] = outputCell.attributes.attrs.text['text'];
		     	   		if(outputNames[j]==undefined || outputNames[j]==null) return 'Error : One of the outputs of ' +  this.offeringNames[i] + ' does not have a name!';
		     	   		outputNames[j] = outputNames[j].replace(/\s/g,'');
		        		if(outputNames[j].length==0) return 'Error : One of the outputs of ' +  this.offeringNames[i] + ' does not have a name!';
		        		for(var k = 0; k < j; k++) {
		        			if(outputNames[k]===outputNames[j]) return 'Error : Duplicate output name : ' + outputNames[j];
		        		}
		        		this.dataNamesMap[outputs[j]] = this.offeringNames[i] + '_output_' + outputNames[j];
		        		
		        		var dataType = outputCell.attributes.attrs.custom['datatype'];
		        		if(dataType==undefined || dataType==='<-- Select -->') return 'Please specify the data type for the output \'' + outputNames[j] + '\' of ' + this.offeringNames[i];
		        		for(var k = 0; k < j; k++) {
		        			if(this.dataTypesMap[outputs[k]]===dataType) return this.offeringNames[i] + ' has multiple outputs of type ' + dataType;
		        		}
		        		this.dataTypesMap[outputs[j]] = dataType;
	     	   		}
	     	   		else console.log('output cell ' + outputs[j] + ' is missing');
	     	   	}
			}
			
			var extraParams = {};
			
			if(offeringType === 'condition') {
				var variable1 = offeringCell.attributes.attrs.custom['variable'];
				if(variable1==undefined || variable1==null || variable1.length==0) return 'Error : Condition variable 1 of ' +  this.offeringNames[i] + ' is not defined!';
				if(this.undefinedVariable(variable1, inputNames)) return 'Error : The value ' + variable1 + ' specified for the condition variable 1 of ' +  this.offeringNames[i] + ' is not available!';
				extraParams['pattern:hasVariable'] = "\"" + (this.isAnInput(variable1, inputNames)? this.offeringNames[i] + '_input_' + variable1 : variable1) + "\"^^xsd:string";
				
				var variable2 = offeringCell.attributes.attrs.custom['value'];
				if(variable2==undefined || variable2==null || variable2.length==0) return 'Error : Condition variable 2 of ' +  this.offeringNames[i] + ' is not defined!';
				if(this.undefinedVariable(variable2, inputNames)) return 'Error : The value ' + variable2 + ' specified for the condition variable 2 of ' +  this.offeringNames[i] + ' is not available!';
				extraParams['pattern:hasValue'] = "\"" + (this.isAnInput(variable2, inputNames)? this.offeringNames[i] + '_input_' + variable2 : variable2) + "\"^^xsd:string";
			
				extraParams['pattern:hasRelationalOperator'] = offeringCell.attributes.attrs.custom['operator'];
			}
			
			if(offeringType === 'loop') {
				var initType = offeringCell.attributes.attrs.custom['initType'];
				var initVariable = offeringCell.attributes.attrs.custom['initVariable'];
				var initValue = offeringCell.attributes.attrs.custom['initValue'];
				if(initVariable!=undefined && initVariable!=null && initVariable.length>0) {
					if(!isNaN(initVariable)) return 'Error : Invalid name (' + initVariable + ') provided for Init variable of ' +  this.offeringNames[i] + '!';
					if(initValue==undefined || initValue==null || initValue.length==0) return 'Error : The initial value of the Init variable ' + initVariable + ' of ' + this.offeringNames[i] + ' is not defined!'; 
					if(this.undefinedVariable(initValue, inputNames)) return 'Error : The value ' + initValue + ' specified for Init value of ' +  this.offeringNames[i] + ' is not available!';
					extraParams['pattern:initType'] = initType;
					extraParams['pattern:initVariable'] = "\"" + initVariable + "\"^^xsd:string";
					extraParams['pattern:initValue'] = "\"" + (this.isAnInput(initValue, inputNames)? this.offeringNames[i] + '_input_' + initValue : initValue) + "\"^^xsd:string";
				}
				
				var variable1 = offeringCell.attributes.attrs.custom['variable'];
				if(variable1==undefined || variable1==null || variable1.length==0) return 'Error : Condition variable 1 of ' +  this.offeringNames[i] + ' is not defined!';
				if(initVariable!==variable1 && this.undefinedVariable(variable1, inputNames)) return 'Error : The value ' + variable1 + ' specified for the condition variable 1 of ' +  this.offeringNames[i] + ' is not available!';
				extraParams['pattern:hasVariable'] = "\"" + (this.isAnInput(variable1, inputNames)? this.offeringNames[i] + '_input_' + variable1 : variable1) + "\"^^xsd:string";
				
				var variable2 = offeringCell.attributes.attrs.custom['value'];
				if(variable2==undefined || variable2==null || variable2.length==0) return 'Error : Condition variable 2 of ' +  this.offeringNames[i] + ' is not defined!';
				if(initVariable!==variable2 && this.undefinedVariable(variable2, inputNames)) return 'Error : The value ' + variable2 + ' specified for the condition variable 2 of ' +  this.offeringNames[i] + ' is not available!';
				extraParams['pattern:hasValue'] = "\"" + (this.isAnInput(variable2, inputNames)? this.offeringNames[i] + '_input_' + variable2 : variable2) + "\"^^xsd:string";
				
				extraParams['pattern:hasRelationalOperator'] = offeringCell.attributes.attrs.custom['operator'];
				
				var incrementVariable = offeringCell.attributes.attrs.custom['incrementVariable'];
				var incrementValue = offeringCell.attributes.attrs.custom['incrementValue'];
				if(incrementVariable!=undefined && incrementVariable!=null && incrementVariable.length>0) {
					if(!isNaN(incrementVariable)) return 'Error : Invalid name (' + incrementVariable + ') provided for Increment variable of ' +  this.offeringNames[i] + '!';
					if(initVariable!==incrementVariable && this.undefinedVariable(incrementVariable, inputNames)) return 'Error : The value ' + incrementVariable + ' specified for Increment variable of ' +  this.offeringNames[i] + ' is not available!';
					extraParams['pattern:incrementVariable'] = "\"" + (this.isAnInput(incrementVariable, inputNames)? this.offeringNames[i] + '_input_' + incrementVariable : incrementVariable) + "\"^^xsd:string";
					
					if(incrementValue==undefined || incrementValue==null || incrementValue.length==0) return 'Error : The Increment value for the Increment variable ' + incrementVariable + ' of ' + this.offeringNames[i] + ' is not defined!'; 
					if(initVariable!==incrementValue && this.undefinedVariable(incrementValue, inputNames)) return 'Error : The value ' + incrementValue + ' specified for Increment value of ' +  this.offeringNames[i] + ' is not available!';
					extraParams['pattern:incrementValue'] = "\"" + (this.isAnInput(incrementValue, inputNames)? this.offeringNames[i] + '_input_' + incrementValue : incrementValue) + "\"^^xsd:string";
				}
				
				var iterationDelay = offeringCell.attributes.attrs.custom['iterationDelay'];
				if (iterationDelay==undefined || iterationDelay==null || iterationDelay.length==0) iterationDelay = "0";
				var regex = new RegExp("^[0-9]+$");
                var str = String.fromCharCode(!e.charCode ? e.which : e.charCode);
                if (!regex.test(iterationDelay)) {
                	return 'Error : Invalid value (' + iterationDelay + ') entered as the Iteration delay of the loop ' + this.offeringNames[i] + '. It should be an integer!'; 
                }
				extraParams['pattern:iterationDelay'] = "\"" + iterationDelay + "\"^^xsd:string";
			}
			
			this.extraParamsMap[allOfferings[i]] = extraParams;
			
    	}
    	
    	this.interactions = [];
    	
    	this.invalidLink = null;
    	this.message = '';
    	scope = this;
    	
    	_.each(this.outLinks, function(links, sourceID) {
    		for(var i = 0; i < links.length; i++) {
   				var link = scope.graph.getCell(links[i]);
   				if(link!=undefined) {
	   				var targetID = link.get('target').id;
	   				if(scope.graph.getCell(targetID)==undefined) {
	   					scope.invalidLink = link;
	   					scope.message = 'Invalid link : no target node!';
	   					return;
	   				}
	   				else {
	   					var offering1 = scope.graph.getCell(sourceID).attributes.attrs.custom['offering'];
	   		            var offering2 = scope.graph.getCell(targetID).attributes.attrs.custom['offering'];
	   		            
		   		        if(offering1 != undefined && offering2 != undefined) {
		   					var datatype1 = scope.dataTypesMap[sourceID];
		   		            var datatype2 = scope.dataTypesMap[targetID];
		   		            
			   		        if(datatype1 != undefined && datatype2 != undefined) {
				   		        if(datatype1 != datatype2) {
				   		        	scope.invalidLink = link;
				   					scope.message = 'Invalid link between ' + scope.dataNamesMap[sourceID] + ' and ' + 
				   						scope.dataNamesMap[targetID] + ' : data-types of source and target are not matching!';
				   					return;
				   		        }
				   		        else {
				   		        	var offeringName1 = scope.graph.getCell(offering1).attributes.attrs.text['text'];
				   		        	var offeringName2 = scope.graph.getCell(offering2).attributes.attrs.text['text'];
				   		        	
				   		        	offeringName1 = offeringName1.replace(/\s/g,'');
				   		        	offeringName2 = offeringName2.replace(/\s/g,'');
				   		        	
					   		        var interactionName = "Interaction" + (scope.interactions.length+1);
				   		        	scope.interactions.push({'name':interactionName, 'from':offeringName1, 'to':offeringName2,
				   		        		'output':scope.dataNamesMap[sourceID], 'input':scope.dataNamesMap[targetID]});
				   		        }
			   		        } else console.log('data-type for ' + sourceID + ' or ' + targetID + ' is missing');
		   		        } else console.log('offering for ' + sourceID + ' or ' + targetID + ' is missing');
	   				}
	   			}
    		}
    	});
    	
    	if(this.invalidLink != null) {
    		return this.message;
    	}
    	
    	this.adjacencyList = {};
    	this.reverseList = {};
    	this.visited = {};
    	this.covered = {};
    	
    	for(var i = 0; i < allOfferings.length; i++) {
			this.adjacencyList[allOfferings[i]] = [];
			this.reverseList[allOfferings[i]] = [];
			this.visited[allOfferings[i]] = false;
			this.covered[allOfferings[i]] = false;
    	}
    	
    	this.createAdjacencyList(allOfferings);
    	
    	this.traverseGraph(allOfferings[0]);
    	for(var i = 0; i < allOfferings.length; i++) {
    		if(!this.visited[allOfferings[i]]) return 'The graph is not connected!';
    	}
    	
    	this.cycle = false;
    	this.onPath = {};
    	
    	for(var i = 0; i < allOfferings.length; i++) {
			if(!this.covered[allOfferings[i]]) this.detectCycle(allOfferings[i]);
			if(this.cycle) return 'Cyclic graphs are currently not supported!';
    	}
    	
    	return this.writeRecipe(allOfferings, writeToFile);
    },
    
    
    undefinedVariable: function(variable, matches) {
    	if(isNaN(variable)) {
	    	return !this.isAnInput(variable, matches);
    	}
    	else return false;
    },
    
    
    isAnInput: function(variable, matches) {
    	for(var i=0; i<matches.length; i++){
    		if(matches[i]===variable) return true;
    	}
    	return false;
    },
    
    
    writeRecipe: function(offeringsList, writeToFile) {
    	
    	var nameSpaceUrl = 'http://w3c.github.io/bigiot/';
    	
    	var recipeString = '';
    	recipeString += "@prefix bigiot: <http://big-iot.eu/core#> .\n";
    	recipeString += "@prefix offeringRecipe: <http://w3c.github.io/bigiot/offeringRecipeModel#> .\n";
    	recipeString += "@prefix owl: <http://www.w3.org/2002/07/owl#> .\n";
    	recipeString += "@prefix pattern: <http://w3c.github.io/bigiot/RecipePatternModel#> .\n";
    	recipeString += "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n";
    	recipeString += "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .\n";
    	recipeString += "@prefix schema: <http://schema.org/> .\n";
    	recipeString += "@prefix td: <http://w3c.github.io/wot/w3c-wot-td-ontology.owl#> .\n";
    	recipeString += "@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .\n";
    	recipeString += "@prefix " + this.recipeName + ": <" + nameSpaceUrl + this.recipeName + "#> .\n";
    	recipeString += "\n";
    	
    	recipeString += "offeringRecipe:Recipe\n";
    	recipeString += "\trdf:type owl:Class ;\n";
    	recipeString += "\trdfs:subClassOf bigiot:Offering ;\n";
    	recipeString += ".\n";
    	recipeString += "pattern:If_Condition\n";
    	recipeString += "\trdf:type owl:Class ;\n";
    	recipeString += "\trdfs:subClassOf bigiot:Offering ;\n";
    	recipeString += ".\n";
    	recipeString += "pattern:For_Loop\n";
    	recipeString += "\trdf:type owl:Class ;\n";
    	recipeString += "\trdfs:subClassOf bigiot:Offering ;\n";
    	recipeString += ".\n";
    	
    	recipeString += this.recipeName + ":" + this.recipeName + "\n";
    	recipeString += "\trdf:type offeringRecipe:Recipe;\n";
    	recipeString += "\tschema:category " + this.recipeCategory + ";\n";
    	recipeString += "\tofferingRecipe:description \"" + this.description + "\"^^xsd:string;\n";
        for (var i = 0; i < this.offeringNames.length; i++)
    		recipeString += "\tofferingRecipe:hasIngredient " + this.recipeName + ":" + this.offeringNames[i] + ";\n";
        for (var i = 0; i < this.interactions.length; i++)
    		recipeString += "\tofferingRecipe:hasInteraction " + this.recipeName + ":" + this.interactions[i]['name'] + ";\n";
    	recipeString += ".\n";
    	
        for(var i = 0; i < offeringsList.length; i++) {
        	var offering = offeringsList[i];
        	var offeringName = this.offeringNames[i];
        	var offeringType = this.offeringTypesMap[offering];
        	var offeringCategory = this.offeringCategoriesMap[offering];
        	recipeString += this.recipeName + ":" + offeringName + "\n";
        	recipeString += "\trdf:type " + offeringType + ";\n";
        	if(offeringCategory!=undefined) recipeString += "\tschema:category " + offeringCategory + ";\n";
        	var inputs = this.inputMap[offering];
			if(inputs!=undefined) {
	     	   	for(var j = 0; j < inputs.length; j++) {
	     	   		if(this.graph.getCell(inputs[j])!=undefined) {
	     	   			recipeString += "\ttd:hasInput " + this.recipeName + ":" + this.dataNamesMap[inputs[j]] + ";\n";
	     	   		}
	     	   	}
			}
        	var outputs = this.outputMap[offering];
			if(outputs!=undefined) {
	     	   	for(var j = 0; j < outputs.length; j++) {
	     	   		if(this.graph.getCell(outputs[j])!=undefined) {
	     	   			recipeString += "\ttd:hasOutput " + this.recipeName + ":" + this.dataNamesMap[outputs[j]] + ";\n";
	     	   		}
	     	   	}
			}
			var extraParams = this.extraParamsMap[offering];
			for (var param in extraParams) {
				recipeString += "\t" + param + " " + extraParams[param] + ";\n";
			}
			recipeString += ".\n";
    	}
        for (var data in this.dataNamesMap) {
        	recipeString += this.recipeName + ":" + this.dataNamesMap[data] + "\n";
        	recipeString += "\trdf:type bigiot:Data;\n";
        	recipeString += "\tbigiot:rdfType " + this.dataTypesMap[data] + ";\n";
        	recipeString += ".\n";
    	}
        for(var i = 0; i < this.interactions.length; i++) {
        	var interaction = this.interactions[i];
        	recipeString += this.recipeName + ":" + interaction['name'] + "\n";
        	recipeString += "\trdf:type offeringRecipe:Interaction;\n";
        	recipeString += "\tofferingRecipe:hasIngredientFrom " + this.recipeName + ":" + interaction['from'] + ";\n";
        	recipeString += "\tofferingRecipe:hasIngredientTo " + this.recipeName + ":" + interaction['to'] + ";\n";
        	recipeString += "\tofferingRecipe:hasIngredientOutput " + this.recipeName + ":" + interaction['output'] + ";\n";
        	recipeString += "\tofferingRecipe:hasIngredientInput " + this.recipeName + ":" + interaction['input'] + ";\n";
        	recipeString += "\tofferingRecipe:hasOperation offeringRecipe:Update;\n";
        	recipeString += ".\n";
        }
        
        console.log('Recipe :\n');
        console.log(recipeString);
        
        if(writeToFile) {
        	var blob = new Blob([recipeString], {type: "text/plain;charset=utf-8"});
    		saveAs(blob, this.recipeName+".ttl");
        }
        else {
	        var request = new XMLHttpRequest();
	        request.onload = function () {
	        	var status = request.status;
	        	var data = request.responseText;
	        	
	        	new joint.ui.FlashMessage({
	                type: 'alert',
	                closeAnimation: false,
	                modal: true,
	                title: 'Create Recipe',
	                content: data
	                }).open();
	    	}
	        
	        request.open("POST", './create_recipe', true);
	        request.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
	        request.send(JSON.stringify({'recipeName':this.recipeName, 'recipeID':(nameSpaceUrl+this.recipeName+'#'+this.recipeName), 'data':recipeString, 'format':'N3'}));
	        return null;
        }
        
    },
    
    
    createAdjacencyList: function(offeringsList) {
    	for(var i = 0; i < offeringsList.length; i++) {
    		var offering = offeringsList[i];
			var outputs = this.outputMap[offering];
			if(outputs!=undefined) {
	     	   	for(var j = 0; j < outputs.length; j++) {
	     	   		if(this.graph.getCell(outputs[j])!=undefined) { //console.log('processing  outputs ' + outputs[j]);
	     	   			if(this.outLinks[outputs[j]]!=undefined) {
		     	   			var links = this.outLinks[outputs[j]];
		     	   			for(var k = 0; k < links.length; k++) {
		     	   				var link = this.graph.getCell(links[k]);
		     	   				if(link!=undefined) {
			     	   				var neighbor = this.graph.getCell(link.get('target').id);
			     	   				if(neighbor!=undefined) { //console.log('processing  neighbor ' + neighbor.id);
			     	   					var neighborOffering = neighbor.attributes.attrs.custom['offering'];
			     	   					if(neighborOffering!=undefined && this.graph.getCell(neighborOffering)!=undefined && this.adjacencyList[offering].indexOf(neighborOffering)<0) {
			     	   						this.adjacencyList[offering].push(neighborOffering);
			     	   						this.reverseList[neighborOffering].push(offering);
			     	   						//var offeringName = this.graph.getCell(offering).attributes.attrs.text['text'];
			     	   						//var neighborName = this.graph.getCell(neighborOffering).attributes.attrs.text['text'];
			     	   						//console.log('Check : ' + offeringName + ' is connected to ' + neighborName);
			     	   					} else console.log('neighborOffering ' + neighborOffering + ' is missing');
			     	   				} else console.log('target of ' + links[k] + ' is missing');
		     	   				} else console.log('link' + links[k] + ' is missing');
		     	   			}
	     	   			}// else console.log('outLinks[' + outputs[j] + '] is missing');
	     	   		} else console.log('output ' + outputs[j] + ' is missing');
	     	   	}
			} else console.log('outputMap[' + offering + '] is missing');
    	}
    },
    
    
    traverseGraph: function(cell) {
    	this.visited[cell] = true;
    	var neighbors = _.union(this.adjacencyList[cell], this.reverseList[cell]);
    	if(neighbors!=undefined) {
    		for(var i = 0; i < neighbors.length; i++) {
	    		if(this.visited[neighbors[i]]!==true) this.traverseGraph(neighbors[i]);
	    	}
    	}
    },
    
    
    detectCycle: function(cell) {
    	this.covered[cell] = true;
    	var neighbors = this.adjacencyList[cell];
    	if(neighbors!=undefined) {
    		this.onPath[cell] = true;
    		for(var i = 0; i < neighbors.length; i++) {
	    		if(this.onPath[neighbors[i]]==true) this.cycle = true;
	    		else this.detectCycle(neighbors[i]);
	    		if(this.cycle) return;
	    	}
    		this.onPath[cell] = false;
    	}
    },
    
    
    createRecipe: function() {
    	var errorMessage = this.validateGraph(false);
    	if(errorMessage!=null){
	    	new joint.ui.FlashMessage({
	            type: 'alert',
	            closeAnimation: false,
	            modal: true,
	            title: 'Create Recipe',
	            content: errorMessage
	            }).open();
    	}
    },
    
    saveRecipe: function() {
    	var errorMessage = this.validateGraph(true);
    	if(errorMessage!=null){
	    	new joint.ui.FlashMessage({
	            type: 'alert',
	            closeAnimation: false,
	            modal: true,
	            title: 'Save Recipe',
	            content: errorMessage
	            }).open();
    	}
    }
    
   
});

