function initMap() {
        var mapDiv = document.getElementById('map');
        var map = new google.maps.Map(mapDiv, {
          center: {lat: 48.1351, lng: 11.5820},
          zoom: 8,
          mapTypeId: google.maps.MapTypeId.ROADMAP
        });

        
      }