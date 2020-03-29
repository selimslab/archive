


var io = []; // io: input object, holds input ids # ["10","11","12" ...], 1 indicates it is input
var oo = []; // oo: output object, holds output ids # ["20","21","22" ... ], 2 indicates it is output

function addCard(fieldType, name) {

    // scope of these string and object are important 
    var dataString = "{\"name\":\"\",\"rdfType\":{\"name\":\"\",\"uri\":\"\"},\"members\":[],\"valueType\":\"\",\"parameterType\":\"\"";
    // if (name) {
   // dataString += ",\"placeholder\":\"\"";
    // }
    dataString += "}";

    var dataObject = JSON.parse(dataString);


    if (fieldType == 1) {
        // add a new parameter to json file
        var temp = fo.inputData;
        temp.push(dataObject);
        // fo : file object, JSON object of offering template, first defined in createEditor() at editor.js 
        fo.inputData = temp;

        // add to form
        var parameterId = "1" + io.length;
        io.push(parameterId);

        var newParameter = newCard(parameterId, 'in', name);
        $('#cards').append(newParameter);

        if (name) {
            $("#name" + io[io.length - 1]).val(name);
        }

    } else if (fieldType == 2) {
        // add a new parameter to json file
        var temp = fo.outputData;
        temp.push(dataObject);
        fo.outputData = temp;

        var parameterId = "2" + oo.length;
        oo.push(parameterId);

        var newParameter = newCard(parameterId, 'out', name);
        $('#cards').append(newParameter);

        if (name) {
            $("#name" + oo[oo.length - 1]).val(name);
        }
    }


    updateEditor();

}



function removeCard(id) {


    $('#form' + id).remove();
    // take first digit in id 
    var firstDigit = id.toString()[0];
    var fieldType = parseInt(firstDigit);
    var index = id.toString().slice(1);
    index = parseInt(index);

    if (fieldType == 1) {
        io.splice(index, 1);
        fo.inputData.splice(index, 1);
    } else if (fieldType == 2) {
        oo.splice(index, 1);
        fo.outputData.splice(index, 1);
    }

    updateEditor();

}

// name is optional, we have name only when the card is created from template  
function newCard(index, title, name) {
    console.log( $('#category').val() );
    var fieldString =
        "<form id='form" + index + "' class='col-sm-10 col-xl-5 card " + title + "'>" +
        "<h6 class='card-title'>" + title +
        "<a class='removeIcon' onclick='removeCard(" + index + ")'>&#10006;</a></h6>" +
        "<div class='card-block'>" +

        "<div class='form-group'>" +
        "<h6>Name</h6>" +
        "<input type='text' id='name" + index + "' class='form-control'>" +
        "</div>" +

        "<div class='form-group'>" +
        "<h6>RDF Annotation</h6>" +
        "<select id='rdf" + index + "' class='form-control'>" +
        "</select>" +
        "</div>";

    if (name) {
        fieldString += "<div class='form-group'>" +
            "<h6>Parameter Type</h6>" +
            "<select id='ptype" + index + "' class='form-control'>" +
            "<option value='TEMPLATE'>TEMPLATE</option>" +
            "<option value='QUERY'>QUERY</option>" +
            "<option value='PATH'>PATH</option>" +
            "<option value='BODY'>BODY</option>" +
            "</select>" +
            "</div>";
        fieldString += "<h6>Placeholder in Template</h6>" +
            "<input type='text' class='form-control' value='@@"+name+"@@' id='placeholder"+index+"' data-toggle='tooltip' data-placement='top' title=''>";
    } else {
        fieldString += "<div class='form-group'>" +
            "<h6>Parameter Type</h6>" +
            "<select id='ptype" + index + "' class='form-control'>" +
            "<option value='QUERY'>QUERY</option>" +
            "<option value='TEMPLATE'>TEMPLATE</option>" +
            "<option value='PATH'>PATH</option>" +
            "<option value='BODY'>BODY</option>" +
            "</select>" +
            "</div>";
    }

    fieldString += "<div class='form-group'>" +
        "<h6>Value Type</h6>" +
        "<select id='vtype" + index + "' class='form-control' id='valueType'>" +
        "<option value='text'>text</option>" +
        "<option value='number'>number</option>" +
        "</select>" +
        "</div>" +

        "</div></form>";


        getRDF(index);


    return $(fieldString);
}

  function getRDF(index){
  var xhr = new XMLHttpRequest();
      xhr.onreadystatechange = function() {
          if (xhr.readyState == XMLHttpRequest.DONE) {

              var rdfs = JSON.parse(xhr.responseText) ;

              rdfs.forEach((rdf) => {
                  $('#rdf' + index).append("<option value='" + rdf + "'>" + rdf + "</option>");
              });

          }
      }

      var rdfPath = 'data_types?category=' + $('#category').val();
      xhr.open('GET', rdfPath , true);
      xhr.send(null);

  }