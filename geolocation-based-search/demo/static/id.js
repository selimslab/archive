   



    let api_url =  'https://westapi.herokuapp.com/'


    // trigger id search
    $('#id').change( (event)=> { 
      $('#results').empty()

      let id = $("#id").val();
      query = 'ads?where=id=='+ id;
      url = api_url + query

      $.ajax({
        type: 'GET',
        url: url,// modify as necessary
        ContentType: 'application/json',

        // if 200 OK 
        success: function(response) {
              item = response['_items'][0]

              let markup = `
              <div class='card-body'>
                <table class='table'>
                  <tr>
                      <td><a :href="item.link">{{item.title}}</a><td>
                      <td>{{item.price}}</td>
                      <td>{{item.date}}</td>
                  <tr>
                <table>
              </div>`


              $('#results').append(markup)

              // render dynamically
              var app = new Vue({

                  el: '#results',
                  data: { item: item  }     
              })
        }, error: function(error) {
              console.log(error);
              $('#results').html(error);         
        }
     }).done();

    })