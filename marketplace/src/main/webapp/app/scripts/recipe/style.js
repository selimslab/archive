$(function() {

    document.getElementById("page2").style.display = 'none';
    document.getElementById("page3").style.display = 'none';
    document.getElementById("page4").style.display = 'none';
    document.getElementById("page5").style.display = 'none';
    document.getElementById("page6").style.display = 'none';
    document.getElementById("page7").style.display = 'none';
    $("html, body").animate({ scrollTop: 0 }, 800);

    // Auto complete city names
    var placesAutocomplete = places({
        container: document.querySelector('#city'),
        type: 'city',
        aroundLatLngViaIP: false

    });


});

function showDeployStatus() {

    $('.deployStatus').each(function(i) {

        if (i == 0) {
            $(this).children('#success').hide();
            $(this).children('#error').hide();
            $(this).children('#statusText').text('Deploying...');
            document.getElementById("statusText").style.color = '#ffc107';

            setTimeout(function() {
              
                $(this).children("#loader").hide();
                $(this).children('#success').show();
                document.getElementById("statusText").style.color = '#28a745';
                $(this).children('#statusText').empty();
                $(this).children('#statusText').text('Successfully Deployed.');
            }, 1000);

        } else {
            $(this).children('#success').hide();
            $(this).children('#error').hide();
            $(this).children('#statusText').text('Deploying...');
            document.getElementById("statusText").style.color = '#ffc107';

            setTimeout(function() {
               
                $(this).children("#loader").hide();
                $(this).children('#error').show();
                document.getElementById("statusText").style.color = '#28a745';
                $(this).children('#statusText').empty();
                $(this).children('#statusText').text('Error');
            }, 1000);
        }

    });

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


function getSelectedIngredient() {
    console.log(this);
}