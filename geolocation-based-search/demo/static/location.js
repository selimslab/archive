
      // Note: This example requires that you consent to location sharing when
      // prompted by your browser. If you see the error "The Geolocation service
      // failed.", it means you probably did not give permission for the browser to
      // locate you.
        
      
      let map, pos, infoWindow;

      // auto init map
      function initMap() {
        map = new google.maps.Map(document.getElementById('map'), {
          center: {lat: 39, lng: 42}, // make istanbul 
          zoom: 6
        });
        infoWindow = new google.maps.InfoWindow;

        // Try HTML5 geolocation.
        if (navigator.geolocation) {
          navigator.geolocation.getCurrentPosition(function(position) {
            pos = {
              lat: position.coords.latitude,
              lng: position.coords.longitude
            };
            bounds  = new google.maps.LatLngBounds();
            loc = new google.maps.LatLng(pos.lat, pos.lng);
            bounds.extend(loc);


            infoWindow.setPosition(pos);
            infoWindow.setContent('buradasınız.');
            infoWindow.open(map);

            map.setCenter(pos);
            map.fitBounds(bounds); //auto zoom
            map.panToBounds(bounds); // auto center 

          }, function() {
            handleLocationError(true, infoWindow, map.getCenter());
          });
        } else {
          // Browser doesn't support Geolocation
          handleLocationError(false, infoWindow, map.getCenter());
        }
      }

      function handleLocationError(browserHasGeolocation, infoWindow, pos) {
        infoWindow.setPosition(pos);
        infoWindow.setContent(browserHasGeolocation ?
                              'Error: The Geolocation service failed.' :
                              'Error: Your browser doesn\'t support geolocation.');
        infoWindow.open(map);
      }

    





    // send coordinates to the backend server 
    function sendLocation(pos, radius) {
        let location_data = {'lat': pos.lat, 'lon': pos.lng, 'radius': radius};
        $('#results').empty()
        let results = [];
        $.ajax({
            type: 'POST',
            url: 'https://westapi.herokuapp.com/close', // modify as necessary
            ContentType: 'application/json',
            data: location_data,

            // if 200 OK 
            success: function(response) {

              
              jsonData = JSON.parse(response)
              ilanlar = Array.from(jsonData)
              
              if (!Array.isArray(jsonData) || !jsonData.length){
                $('#results').html('bu mesafede bir ilan bulamadık.')
              }

              else{
                
                for (let ilan of ilanlar){

                    let item = {
                      title: ilan['title'],
                      link: ilan['link'],
                      coordinates : ilan['coordinates'],
                      date: ilan['date'],
                      year: ilan['year'],
                      price: ilan['price']
                    };

                    results.push(item);
             
                }

                   // define results
                  const result_list = `
                  <li class='card' v-for="item in items">
                    <div class='card-body'>
                      <table class='table'>
                        <tr>
                            <td><a :href="item.link">{{item.title}}</a></td>
                            <td>{{item.price}}</td>
                        <tr>
                      <table>
                    </div>
                  </li>
                  `
                // append results
                $('#results').append(result_list)

                // render dynamically
                var app = new Vue({

                  el: '#results',
                  data: { items: results }     
               })

            }  
        }, 
            error: function(error) {
                  $('#results').html(error);         
            }
        }).done();
    
    }



    // trigger search
    $('#radius').change( (event)=> { 
      event.preventDefault();
      let radius = $("#radius").val();
      $('#results').empty()
      sendLocation(pos, parseFloat(radius) );
    })











