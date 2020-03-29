//the file is responsible for the shapes that appear in the left hand side of the editor.

var Stencil = {};

Stencil.groups = {
	offerings: { index: 1, label: 'Offerings' },
	conditions: { index: 2, label: 'Conditions' },
	loops: { index: 3, label: 'Loops' }
};

Stencil.shapes = {
    offerings: [
        new joint.shapes.basic.Rect({
        	id: 'offering',
	        size: { width: 300, height: 150 },
	        attrs: {
	            rect: { rx: 5, ry: 5, fill: '#27AE40',stroke: '#555' },
	            text: { text: 'Offering', fill: '#ffffff', 'font-size': 14, stroke: '#000000', 'stroke-width': 0 },
	            custom: { type: 'offering' }
	        }
	    })
    ],
    conditions: [
        new joint.shapes.erd.IdentifyingRelationship({ id: 'ifcond', attrs: { text: { text: 'If', 'font-size': 14 }, custom: { type: 'condition' } } })
    ],
    loops: [
        new joint.shapes.erd.IdentifyingRelationship({ id: 'forloop', attrs: { text: { text: 'For', 'font-size': 14 }, custom: { type: 'loop' } } })
    ]
};
