/*Declaring a controller to load recipes dynamically and to handle click events*/
app.controller('RecipeController',
    function($scope, $http, $document, $rootScope, ServerUrl, $log) {

        $scope.fetchOSRList = function() {
            $http.get(ServerUrl + 'osr_list').success(function(data, status) {
                $rootScope.globalOSRList = data;
                $scope.offeringCategoryList = [];
            });
        }

        $rootScope.$on("service_selected", function(event, values) {
            $scope.selected_service = values.service;
            $rootScope.icon = values.icon;
            $scope.updateRecipes();
            $scope.fetchOSRList();
        });




        $scope.updateRecipes = function() {
            $http.get(ServerUrl + 'recipes?service=' + $scope.selected_service).success(function(data, status) {
                $scope.recipes = data;
            });
        };

        // call this function onClick of recipe, but don't $emit("recipe_selected") yet, since we don't have the OSR details at this stage 
        $scope.saveRecipeandScroll = function(clickEvent) {
            $scope.recipe_params = {}
            $scope.recipe_params['recipe'] = clickEvent.currentTarget.id;

            $http.post(ServerUrl + 'recipe_pattern', JSON.stringify($scope.recipe_params)).success(function(data, status) {
                $scope.recipe_pattern = data;
                $scope.drawRecipe($scope.recipe_pattern);
                $scope.recipe_params['recipePattern'] = $scope.recipe_pattern;
                $rootScope.$emit("recipe_selected", { recipe_params: $scope.recipe_params });
            });
            

            $("#page4").show();
            $("#page5").hide();
            $("#page6").hide();
            $("#page7").hide();
            $('html, body').animate({
                scrollTop: $("#page4").offset().top - 100
            }, 800);
        };

        $rootScope.$on("recipe_params", function(event, values) {
            $scope.recipe_params = values['recipe_params'];
        });

        // we have the OSR details now, can proceed to $emit("recipe_selected") and load offering suggestions based on OSRs
        $scope.saveRecipeDataandEmit = function(clickEvent) {
            //$rootScope.$emit("recipe_selected", { recipe_params: $scope.recipe_params });           
            $("#page5").hide();
            $("#page6").hide();
            $("#page7").hide();
        };


        





      
        $scope.drawRecipe = function(json) {

            document.getElementById('paper-holder-loading').innerHTML = "";

            var graph = new joint.dia.Graph;
            var paper = new joint.dia.Paper({
                el: $('#paper-holder-loading'),
                gridSize: 1,
                model: graph
            });

            //console.log('Diagram Data = ' + JSON.stringify(json));

            drawRecipeGraph(graph, json);
            paper.fitToContent({ padding: 10, allowNewOrigin: 'any', minWidth: $('#paper-holder-loading').width(), minHeight: $('#paper-holder-loading').height() });

            /* On click pop up box with attributes*/

            paper.on('cell:pointerdblclick', function(cellView) {

                $scope.currentOffering = cellView.model.get('id');
                config = json["configAttributes"][$scope.currentOffering];

                if (config != null && !isEmptyObject(config)) {

                    var i = 0;
                    $rootScope.configAttributes = [];

                    _.each(config, function(value, key) {
                        if (key != 'type') {
                            $rootScope.configAttributes[i] = new Object();
                            $rootScope.configAttributes[i].key = key;
                            $rootScope.configAttributes[i].value = value;
                            if (key == 'category')
                                $scope.offeringCategoryList[$scope.currentOffering] = $rootScope.configAttributes[i].value;
                            i++;
                        }
                    });

                    $scope.updateOSRModal('lg', $scope.offeringCategoryList, $scope.currentOffering); // open modal for OSRs
                }
            });
        };

        $scope.resetOfferingView = function(){
        	$rootScope.showParams = false;
            $rootScope.showIngredientSettings = false;
        }


    });