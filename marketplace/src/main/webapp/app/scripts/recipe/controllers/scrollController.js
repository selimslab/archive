/* animated scrolling */
app.controller('Scroller', function($scope, $document, $rootScope) {

    $scope.proceedToIngr = function(clickEvent) {
        $("#page5").show();
        $('html, body').animate({
            scrollTop: $("#page5").offset().top -100
        }, 800);
    }

    $scope.backToCity = function(clickEvent) {
        $('html, body').animate({
            scrollTop: $("#page1").offset().top -100 
        }, 800);
    }

    $scope.backToService = function(clickEvent) {
        $('html, body').animate({
            scrollTop: $("#page2").offset().top -100 
        }, 800);
    }

    $scope.backToRecipe = function(clickEvent) {
        $('html, body').animate({
            scrollTop: $("#page3").offset().top -100 
        }, 800);
    }

    $scope.backToConfig = function(clickEvent) {
        $('html, body').animate({
            scrollTop: $("#page4").offset().top -100 
        }, 800);
    }

    $scope.backToIngr = function(clickEvent) {
        $('html, body').animate({
            scrollTop: $("#page5").offset().top -100 
        }, 800);
    }

});