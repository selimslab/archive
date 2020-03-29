//this file is the file for creating the on click selection option called inspector in the right hand side of the editor.

function createInspectorDefs(scope) {

	var commonAttributes = {
		text: { 
			'text': { type: 'textarea', group: 'common', index: 1, label: 'Name' } 
		}
	};
	
	var offeringAttributes = {
	    custom: {
	    	'category': { type: 'select', class : 'form-control', options: ['<-- Select -->'].concat(scope.offeringCategories),
	    		group: 'offering', index: 1, label: 'Offering Category', attrs: {   'label': { 'data-tooltip': 'Select the suitable offering category from the options.' } } } 
	    }
	};
	
	var dataAttributes = {
		custom: { 
			'datatype': { type: 'select', options: ['<-- Select -->'].concat(scope.dataTypes),
				group: 'data', index: 1, label: 'Data Type', attrs: { 'label': { 'data-tooltip': 'Select the suitable data type from the options.' } } } 
		}
	};
	
	var conditionAttributes = {
		custom: {
			'operator': { type: 'select', options: ['pattern:equalTo','pattern:lessThan','pattern:greaterThan'],
				group: 'condition', index: 1, label: 'Condition Operator', attrs: { 'label': { 'data-tooltip': 'Select the suitable operator from the options.' } } },
			'variable': { type: 'textarea', group: 'condition', index: 2, label: 'Condition variable 1' },
			'value': { type: 'textarea', group: 'condition', index: 3, label: 'Condition variable 2' }
		}
	};
	
	var loopAttributes = {
		custom: {
			'operator': { type: 'select', options: ['pattern:equalTo','pattern:lessThan','pattern:greaterThan'],
				group: 'loop', index: 1, label: 'Condition Operator', attrs: { 'label': { 'data-tooltip': 'Select the suitable operator from the options.' } } },
			'variable': { type: 'textarea', group: 'loop', index: 2, label: 'Condition variable 1' },
			'value': { type: 'textarea', group: 'loop', index: 3, label: 'Condition variable 2' },
			'initType': { type: 'select', options: ['xsd:integer','xsd:long','xsd:float','xsd:double','xsd:string'],
				group: 'loop', index: 4, label: 'Init variable type', attrs: { 'label': { 'data-tooltip': 'Select the suitable variable type from the options.' } } },
			'initVariable': { type: 'textarea', group: 'loop', index: 5, label: 'Init variable' },
			'initValue': { type: 'textarea', group: 'loop', index: 6, label: 'Init value' },
			'incrementVariable': { type: 'textarea', group: 'loop', index: 7, label: 'Increment variable' },
			'incrementValue': { type: 'textarea', group: 'loop', index: 8, label: 'Increment value' },
			'iterationDelay': { type: 'textarea', group: 'loop', index: 9, label: 'Iteration delay (ms)' }
		}
	};
	
	var offeringInspectorGroup = {
		common: { label: 'Common Attributes', index: 1 },
		offering: { label: 'Offering Attributes', index: 2 }
	};
	
	var dataInspectorGroup = {
		common: { label: 'Common Attributes', index: 1 },
		data: { label: 'Data Attributes', index: 2 }
	};
	
	var conditionInspectorGroup = {
		common: { label: 'Common Attributes', index: 1 },
		condition: { label: 'Condition Attributes', index: 2 }
	};
	
	var loopInspectorGroup = {
		common: { label: 'Common Attributes', index: 1 },
		loop: { label: 'Loop Attributes', index: 2 }
	};
	
	scope.InspectorDefs = {
	
	    'offering': {
	//        inputs: _.extend( {attrs: commonAttributes}, offeringAttributes),
	        inputs: { attrs: _.extend( {}, commonAttributes, offeringAttributes) },
	        groups: offeringInspectorGroup
	    },
	    
	    'condition': {
	        inputs:  { attrs: _.extend( {}, commonAttributes, conditionAttributes) },
	        groups: conditionInspectorGroup
	    },
	    
	    'loop': {
	        inputs:  { attrs: _.extend( {}, commonAttributes, loopAttributes) },
	        groups: loopInspectorGroup
	    },
	
	    'input': {
	        inputs: { attrs: _.extend( {}, commonAttributes, dataAttributes) },
	        groups: dataInspectorGroup
	    },
	    
	    'output': {
	        inputs: { attrs: _.extend( {}, commonAttributes, dataAttributes) },
	        groups: dataInspectorGroup
	    }
	
	};
	
};


