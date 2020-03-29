/* 
use case for templates : create new cards for every placeholder in pasted text
*/

$(function() {
    $('#templates').hide();
});

var matches = []; // ['@@latitude@@','@@longtitude@@','@@shape@@']
var p = []; // temporary parameter array
var c = []; // created field id's
var n = []; // created field names



// "on" used to attach a handler to future elements
$("form").on("change", "select", function() {
    if ($(this).val() == 'TEMPLATE') {
        var id = $(this).attr('id');
        id = id.slice(-2);
        var placeholder = $(
            "<h6>Placeholder in Template</h6>" +
            "<input type='text' class='form-control' id='placeholder"+id+"'>");
        $($(this).parent()).append(placeholder);
        // ADD PLACEHOLDER FIELD TO JSON

       

    }
});



var area, areaId;

// runs when textarea has new input
function setFromTemplate() {
    autoExpand();
    $('textarea').each(function() {
        area = this;
        areaId = this.id;
        read();
        if (matches) {
            placeholderToName();
            crossCheck();
        }
       
    });
}


function read() {
    // define what to catch
    var regex = /@@([_A-Za-z0-9\.\-]+)\@\@/gi;
    // take the area text
    var str = $(area).val();
    // push matched parts into the array
    matches = regex[Symbol.match](str);
}



function placeholderToName() {
    // loop through the array
    for (var i in matches) {
        var item = matches[i];
        if (!p.includes(item)) {
            p.push(item.slice(2, item.length - 2));
        }
    }
}

function crossCheck() {

    console.log("crossCheck p: " + p);
    console.log("c: " + c);
    console.log("n: " + n);

    for (var j in n) {
        if (!p.includes(n[j])) {
            console.log(j);
            removePlaceholder(c[j]);
        }
    }

    for (var i in p) {
        if (!n.includes(p[i])) {
            addPlaceholder(p[i]);
        }
    }



}

function removePlaceholder(placeholderId) {
    var index = n.indexOf(placeholderId);
    n.splice(index, 1);
    c.splice(index, 1);
    removeCard(placeholderId);
    console.log("removeCard p: " + p);
    console.log("c: " + c);
    console.log("n: " + n);
}

function addPlaceholder(placeholderName) {
    // keep track of created fields
    n.push(placeholderName);

    if (areaId == 'templateRequest') {
        // key indicates the type and order of the parameter
        // 1 for input, 2 for output parameters
        var key = "1" + io.length;
        c.push(key);
        addCard(1, placeholderName);

    } else if (areaId == 'templateResponse') {
        var key = "2" + oo.length;
        c.push(key);
        addCard(2, placeholderName);
    }

    console.log("addPlaceholder p: " + p);
    console.log("c: " + c);
    console.log("n: " + n);
}





function autoExpand() {
    $('textarea').each(function() {
        resizeIt(this);
    }).on('input', function() {
        resizeIt(this);
    });
}

// out of document.ready to enable call from other files
function resizeIt(e) {
    $(e).css({ 'height': 'auto', 'overflow-y': 'hidden' }).height(e.scrollHeight);
}

function showTemplates() {
    $('#templates').show();
}