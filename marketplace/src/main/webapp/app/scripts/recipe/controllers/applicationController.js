/*Code for changing the view of the code on dropdown selection */
app.controller('ApplicationController', function($scope, $http, $document, $rootScope, ServerUrl) {

    $scope.lang = "java";

    $rootScope.$on("create_script", function(event, values) {
        $scope.script_params = values.script_params;
        $http({
            method: "POST",
            url: ServerUrl + 'application',
            data: JSON.stringify(values.script_params)
        }).then(function(response) {
            $scope.java_code = response.data;
            $scope.javascript_code = '';
            $scope.updateScript();
        }, function(response) {
            $scope.errorMessage = response.statusText;
        });
        /*
        $http.post(ServerUrl + 'application', JSON.stringify(values.script_params)).success(function(data, status) {
            $scope.java_code = data;
            $scope.javascript_code = '';
            $scope.updateScript();
        });
        */
    });

    $scope.updateScript = function() {
        if ($scope.lang == 'java') {
            $scope.application_script = $scope.java_code;
        } else if ($scope.lang == 'javascript') {
            $scope.application_script = $scope.javascript_code;
        }

    }

});