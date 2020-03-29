
var app = angular.module('offerings', []);
app.constant('ServerUrl', './');


app.controller('allOfferingsController', function allOfferingsController($scope, $http, ServerUrl) {

	$http({
        method : "GET",
        url : ServerUrl +"allOfferings"
    }).then(function mySuccess(response) {
        $scope.allOfferings = response.data;
    }, function myError(response) {
        $scope.allOfferings = response.statusText;
    });

});