    // download
    function down() {
        var data = "text/json;charset=utf-8," + encodeURIComponent(editor.getValue());
        $('#dwn').attr('href', 'data:' + data);
    }




    // custom style file input
    $('#fileInput').change(function() {
        var path = $(this).val();
        path = path.split('\\');
        path = path[path.length - 1];
        $('#status').text(path);
        readFile();
    });

    // use FileAPI to play with files
    function readFile() {

        var input, file, reader;

        $('#status').text('');
        // check the browser 
        if (typeof window.FileReader !== 'function') {
            $('#status').text("The file API isn't supported on this browser yet.");
            return;
        }

        // get the value of file input field
        input = document.getElementById('fileInput');

        // check file status 
        if (!input) {
            $('#status').text("Couldn't find the file input element.");
        } else if (!input.files) {
            $('#status').text("This browser doesn't seem to support the 'files' property of file inputs.");
        } else if (!input.files[0]) {
            $('#status').text("Please select a file before clicking 'Open'");
        }

        // the file import is OK 
        else {
            // get file
            file = input.files[0];
            // create new reader 
            reader = new FileReader();
            // read file as text
            reader.readAsText(file);
            // when file is read, start to play with it  
            reader.onload = function(event) { action(); };
        }


        function action() {

            // global variable!
            fileString = reader.result;

            // -1 to prevent auto select all
            editor.setValue(fileString, -1);

            fileObject = JSON.parse(fileString);

            importFile(fileObject);

        }

    }

    function importFile(fo) {

        $('#nameofoffering').val(fo.description.name);

        $('#endpointurl').val(fo.endpoints[0].uri);

        $('#category').val(fo.description.rdfType.uri);

        $('#provider').val(fo.description.rdfType.provider);

        $('#endpointType').val(fo.endpoints[0].endpointType);

        $('#contentType').val(fo.endpoints[0].contentType);

        $('#acceptType').val(fo.endpoints[0].acceptType);

        $('#cityName').val(fo.region.cityName);

        $('#accountingType').val(fo.price.accountingType);

        $('#amount').val(fo.price.money.amount);

        $('#currency').val(fo.price.money.currency);

        $('#lisans').val(fo.license.type);

        // set inputData
        for (var i in fo.inputData) {
            //  console.log(fo.inputData[i]);
            $('#name' + io[i]).val(fo.inputData[i].name);
            $('#rdf' + io[i]).val(fo.inputData[i].rdfType.uri);
            $('#ptype' + io[i]).val(fo.inputData[i].parameterType);
            $('#vtype' + io[i]).val(fo.inputData[i].valueType);
            addCard(1, fo.inputData[i].name);
        }

        // set outputData
        for (var i in fo.outputData) {

            $('#name' + oo[i]).val(fo.outputData[i].name);
            $('#rdf' + oo[i]).val(fo.outputData[i].rdfType.uri);
            $('#ptype' + oo[i]).val(fo.outputData[i].parameterType);
            $('#vtype' + oo[i]).val(fo.outputData[i].valueType);
            addCard(2, fo.outputData[i].name);
        }

        showTemplates();

        $('#templateRequest').val(fo.requestTemplates.template);

        $('#templateResponse').val(fo.responseTemplates.template);

        print(fo);

    }