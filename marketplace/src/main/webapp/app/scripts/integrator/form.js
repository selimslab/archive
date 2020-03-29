  $(function() {
      // enable tooltips
      $('[data-toggle="tooltip"]').tooltip();
  });

  // Auto complete city names
  var placesAutocomplete = places({
      container: document.querySelector('#cityName'),
      type: 'city',
      aroundLatLngViaIP: false

  });

  function getOfferingCategories() { 
      var xhr = new XMLHttpRequest();
      xhr.onreadystatechange = function() {
          if (xhr.readyState == XMLHttpRequest.DONE) {

              var categories = JSON.parse(xhr.responseText);

              categories.forEach((category) => {
                  $('#category').append("<option value='" + category + "'>" + category + "</option>");
              });

          }
      }
      xhr.open('GET', 'offering_categories', true);
      xhr.send(null);
  }


