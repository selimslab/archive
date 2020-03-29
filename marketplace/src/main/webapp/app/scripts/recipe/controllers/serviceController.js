

/*Declaring a controller to load services dynamically and to handle click events*/
app.controller('ServiceController', function($scope, $http, $document, $rootScope, ServerUrl) {

    $rootScope.$on("city_selected", function(event, values) {
        $scope.selected_city = values.city;
        $scope.updateServices();
    });

    $scope.updateServices = function() {
        $http.get(ServerUrl + 'services?city=' + $scope.selected_city).success(function(data, status) {
            $scope.services = data;
            $scope.imageMap = {};
            for (var i = 0; i < data.length; i++) $scope.imageMap[data[i].name] = data[i].image;
        });
    }

    $scope.saveServiceandScroll = function(clickEvent) {
        //$(clickEvent.currentTarget).toggleClass('selected');
        $rootScope.$emit("service_selected", { service: clickEvent.currentTarget.id, icon: $scope.imageMap[clickEvent.currentTarget.id] });
        $("#page3").show();
        document.getElementById("page4").style.display = 'none';
        document.getElementById("page5").style.display = 'none';
        document.getElementById("page6").style.display = 'none';
        document.getElementById("page7").style.display = 'none';
           $('html, body').animate({
        scrollTop: $("#page3").offset().top -100
    }, 800);
    }

});