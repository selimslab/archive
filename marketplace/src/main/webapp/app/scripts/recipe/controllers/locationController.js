/*Declaring a controller to load cities dynamically and to handle click events*/
app.controller('LocationController', function($scope, $http, $document, $rootScope, ServerUrl) {

    $http.get(ServerUrl + 'locations').success(function(data, status) {
        $scope.cities = data;
    });

    $scope.saveCityandScroll = function(clickEvent) {
        $rootScope.$emit("city_selected", { city: clickEvent.currentTarget.id });
        $("#page2").show();
        document.getElementById("page3").style.display = 'none';
        document.getElementById("page4").style.display = 'none';
        document.getElementById("page5").style.display = 'none';
        document.getElementById("page6").style.display = 'none';
        document.getElementById("page7").style.display = 'none';
        $('html, body').animate({
            scrollTop: $("#page2").offset().top - 100
        }, 800);


    }

});