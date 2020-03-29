$(function() {
    createEditor();
    getExampleOffering()
    getOfferingCategories();
});



// set the editor
function createEditor() {
    editor = ace.edit("editor"); // define editor in the div with id="editor"
    editor.renderer.setShowGutter(false); // hide line numbers
    editor.setReadOnly(true); // false to make it editable
    jsonMode = ace.require("ace/mode/json").Mode;
    editor.session.setMode(new jsonMode()); // set json mode
    editor.setTheme("ace/theme/twilight");
    editor.setOptions({
        maxLines: 2000
    });
    editor.$blockScrolling = Infinity; // remove scrollbar
}


function getExampleOffering() {
    // get default json template 
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
        if (xhr.readyState == XMLHttpRequest.DONE) {
            editor.setValue(xhr.responseText, -1); // we have read the json file and set the value to editor
            fo = JSON.parse(xhr.responseText); // fo : file object   
        }
    }
    xhr.open('GET', 'app/scripts/integrator/offeringTemplate.json', true);
    xhr.send(null);
}


// runs on new form input, matches json values to input fields
function updateEditor() {

    fo.description.name = $('#nameofoffering').val();

    fo.endpoints[0].uri = $('#endpointurl').val();

    fo.description.rdfType.uri = $('#category').val();

    fo.description.rdfType.provider = $('#provider').val()

    fo.endpoints[0].endpointType = $('#endpointType').val();

    fo.endpoints[0].contentType = $('#contentType').val();

    fo.endpoints[0].acceptType = $('#acceptType').val();

    fo.region.cityName = $('#cityName').val();

    fo.price.accountingType = $('#accountingType').val();

    fo.price.money.amount = $('#amount').val();

    fo.price.money.currency = $('#currency').val();

    fo.license.type = $('#lisans').val();

    // set inputData
    for (var i in fo.inputData) {
        //  console.log(fo.inputData[i]);
        fo.inputData[i].name = $('#name' + io[i]).val();

        fo.inputData[i].rdfType.uri = $('#rdf' + io[i]).val();

        fo.inputData[i].parameterType = $('#ptype' + io[i]).val();

        if ($('#placeholder' + io[i]).val()) {
            fo.inputData[i].placeholder = $('#placeholder' + io[i]).val();
        } 
        
        fo.inputData[i].valueType = $('#vtype' + io[i]).val();
    }

    // set outputData
    for (var i in fo.outputData) {

        fo.outputData[i].name = $('#name' + oo[i]).val();

        fo.outputData[i].rdfType.uri = $('#rdf' + oo[i]).val();

        fo.outputData[i].parameterType = $('#ptype' + oo[i]).val();
        
        if ($('#placeholder' + oo[i]).val()) {
            fo.outputData[i].placeholder = $('#placeholder' + oo[i]).val();
        } 

        fo.outputData[i].valueType = $('#vtype' + oo[i]).val();
    }

    fo.requestTemplates.template = $('#templateRequest').val();

    fo.responseTemplates.template = $('#templateResponse').val();

    print(fo);

}


function print(what) {
    var prettyJSON = JSON.stringify(what, null, "\t");
    editor.setValue(prettyJSON, -1);
}