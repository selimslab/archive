   
    let api_endpoint = 'https://westapi.herokuapp.com/crawl'

    //let api_endpoint = 'http://localhost:7000/crawl';

	$('form').submit( (event) => { 
        event.preventDefault();
        let urls = $("#urls").val();
        let pagination = document.getElementById("pagination").checked;
        let coordinates = document.getElementById("coordinates").checked;

        urls = urls.replace(/['"]+/g, ''); // remove " "
  
        data = {'urls': urls, 'pagination' : pagination, 'coordinates': coordinates};
        $('#response').html('sayfa taranıyor...');

        $.ajax({
            type: 'GET',
            url: api_endpoint, // modify as necessary
            ContentType: 'application/json',
            data: data,
            success: function(response) {
                console.log(response) 
                $('#response').empty();
                $('#response').append(response);

                jsonResponse =  JSON.parse(response);
                for (line of jsonResponse){
                    let pretty_line = JSON.stringify(line, null, 2);
                    $('#response').append("<li>"+pretty_line+"</li>")
                }


            }, 
            error: function(error) {
                console.log(error);
                $('#response').html(error.message)
            }
        }).done();
    
    })

