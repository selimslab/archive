//statically creating values for selecting ingredients which later has to be read from a the final recipe and displayed.
app.controller('OfferingsController', function($scope, $http, $document, $rootScope, ServerUrl, $window) {

//	$scope.offering = {};
//	$scope.offering.endpoints = [{}];
//	$scope.inputData = [];
//	$scope.outputData = [];
	
	$scope.registerOffering = function(){
		$http.post(ServerUrl + 'register_offering', JSON.stringify($scope.offering)).success(function(data, status) {
            $scope.registerOfferingFeedback = data;
        });
	}
	
    $rootScope.$on("recipe_selected", function(event, values) {
        $scope.recipe_params = {};
        $scope.recipe_params.OSRMap = {};

        $scope.recipe_params.recipe = values['recipe_params']['recipe'];
        $scope.recipe_params.json = values['recipe_params']['recipePattern'];

        $scope.findOfferings();
    });

    // first call to fetch ingredients and matching offerings
    $scope.findOfferings = function() {
        $http.post(ServerUrl + 'offerings', JSON.stringify($scope.recipe_params)).success(function(data, status) {
            $rootScope.list_of_ingredients = data;
            // set default presentCardinality, minCardinality and maxCardinality 
        	for(var i = 0; i < $rootScope.list_of_ingredients.length; i++){
        		$rootScope.list_of_ingredients[i].minCardinality = 1;
        		$rootScope.list_of_ingredients[i].maxCardinality = 1;
        		$rootScope.list_of_ingredients[i].presentCardinality = 0;
            }
            $rootScope.showParams = false;
        });
    }
    
    // subsequent calls to update the offerings for the given ingredient based on entered OSRs
    $scope.updateOfferings = function() {
    	$scope.params = {};
    	$scope.params.ingredient = $scope.currentIngredient;
    	$scope.params.OSRList = $scope.OSRList[$scope.currentIngredient.id];
        $http.post(ServerUrl + 'offerings_for_ingredient', JSON.stringify($scope.params)).success(function(data, status) {
        	// only the list_of_offerings for the current ingredient will change here
        	var x = $rootScope.list_of_ingredients.indexOf($scope.currentIngredient);
        	$rootScope.list_of_ingredients[x].list_of_offerings = data;
            $rootScope.showParams = false;
        });
    }

    $scope.onClickOffering = function(clickEvent, offering) {
        $rootScope.showParams = true;
        $scope.title = offering.name;
        $rootScope.showIngredientSettings = false;
        $scope.nfpMap = offering.nfpMap;
    }


    $scope.createScript = function(clickEvent) {
        var script_params = {};
        var offeringsSelected = true;
        script_params['recipe'] = $scope.recipe_params['recipe'];
        for (var i = 0; i < $scope.list_of_ingredients.length; i++) {
        	if($scope.list_of_ingredients[i].listOfSelectedOfferings != undefined && $scope.list_of_ingredients[i].listOfSelectedOfferings != null
        			&& $scope.list_of_ingredients[i].listOfSelectedOfferings.length != 0){
        		script_params[$scope.list_of_ingredients[i].id] = $scope.list_of_ingredients[i].listOfSelectedOfferings[0].id;
        	}
        	else{
        		offeringsSelected = false;
        	}
        }
        
        if(offeringsSelected){
	        $rootScope.$emit("create_script", { script_params: script_params });
	        $("#page7").show();
	        $('html, body').animate({
	            scrollTop: $("#page7").offset().top - 100
	        }, 600);
	        }
        else{
        	$window.alert('Please select offerings for each Ingredient!');
        	//showAlert('Please select offerings for each Ingredient!');
        	console.log("Error: Offerings not selected!");
        }
    }
    
    // put this function in a new file - utils.js
    /*showAlert = function(message) {
		new joint.ui.FlashMessage({
            type: 'alert',
            closeAnimation: false,
            modal: true,
            title: 'Create Application Script',
            content: message
            }).open();
	}*/

    $scope.setConfig = function(clickEvent, currentOffering) {

        $scope.currentIngredient = currentOffering.id;
        config = $scope.recipe_params.json["configAttributes"][$scope.currentIngredient];
        var i = 0;
        $rootScope.configAttributes = [];
        $scope.offeringCategoryList = [];
        _.each(config, function(value, key) {
            if (key != 'type') {
                $rootScope.configAttributes[i] = new Object();
                $rootScope.configAttributes[i].key = key;
                $rootScope.configAttributes[i].value = value;
                if (key == 'category')
                    $scope.offeringCategoryList[$scope.currentIngredient] = $rootScope.configAttributes[i].value;
                i++;
            }
        });

    };

    
    // OSR related functions ahead ->
    
    // variables:
    // $rootScope.globalOSRList : lookup for OSRs, same for all the ingredients, remains constant
    // $scope.globalTempOSRList : a local copy of $rootScope.globalOSRList, displayed to the user to capture user inputs, contains both empty and non-empty OSRs
    // $scope.tempOSRList : contains only user selected OSRs, for the given ingredient
    // $scope.OSRList : the final resulting OSRList, for the given ingredient, formed on click of 'Save' button from the $scope.tempOSRList
    $scope.configureOSR = function(currentIngredient) {
        // don't show both
        $rootScope.showIngredientSettings = true;
        $rootScope.showParams = false;
        $scope.title = currentIngredient.name;
        $scope.currentIngredient = currentIngredient;

        //$scope.indx = 0;	// index of the selected OSR from the drop-down list
        $scope.i = 0;		// index for $scope.tempOSRList - index would be necessary to retain the order of the OSRs added, which is necessary for.. 
        					// ..the order of the operations to be inserted in-between them
        $scope.osrStored = false;
        $scope.deleted = false;

        if ($scope.OSRList === undefined){
        	$scope.OSRList = {};
        }
        
        if ($scope.OSRList[$scope.currentIngredient.id] === undefined) {
        	$scope.tempOSRList = [{}];
        	$scope.globalTempOSRList = [];
        	angular.copy($rootScope.globalOSRList, $scope.globalTempOSRList);
        } else {
        	angular.copy($scope.OSRList[$scope.currentIngredient.id], $scope.tempOSRList);
        	$scope.i = $scope.tempOSRList.length;
        	if($scope.i == 0){
        		$scope.tempOSRList = [{}];
        	}else{
        		$scope.osrStored = true;
        	}
        }
    };
    
    $scope.addNewRow = function(){
    	if($scope.indx != undefined){
    	if($scope.osrStored == false){
    		var currentOSR = $scope.globalTempOSRList[$scope.indx];
    		// required fields must not be empty
    		if(currentOSR.operation == null){
    			currentOSR.operation = 'AND';
    		}
    		switch(currentOSR.OSRCategory) {
    	    case 'DirectOSR':
    	    	if(currentOSR.value != null && currentOSR.value != ""){
    				angular.copy($scope.globalTempOSRList[$scope.indx], $scope.tempOSRList[$scope.i++]);
    				$scope.osrStored = true;
    			}
    	        break;
    	    case 'OperatorOSR':
    	    	if((currentOSR.operator != null && currentOSR.operator != "") && (currentOSR.value != null && currentOSR.value != "")){
    				angular.copy($scope.globalTempOSRList[$scope.indx], $scope.tempOSRList[$scope.i++]);
    				$scope.osrStored = true;
    			}
    	        break;
    	    case 'ComplexOSR':
    	    	switch(currentOSR.rule) {
    	        case 'sameAs':
    	        	if(currentOSR.offeringURI != null && currentOSR.offeringURI != ""){
        				angular.copy($scope.globalTempOSRList[$scope.indx], $scope.tempOSRList[$scope.i++]);
        				$scope.osrStored = true;
        			}
    	            break;
    	        case 'cardinality':
    	        	if((currentOSR.minCardinality != null && currentOSR.minCardinality != "") && (currentOSR.maxCardinality != null && currentOSR.maxCardinality != "")){
        				angular.copy($scope.globalTempOSRList[$scope.indx], $scope.tempOSRList[$scope.i++]);
        				$scope.osrStored = true;
        			}
    	            break;
    	        case 'combinedPrice':
    	        	if((currentOSR.operator != null && currentOSR.operator != "") && (currentOSR.value != null && currentOSR.value != "")){
        				angular.copy($scope.globalTempOSRList[$scope.indx], $scope.tempOSRList[$scope.i++]);
        				$scope.osrStored = true;
        			}
    	            break;
    	    	}
    	        break;
    		}
    	}
    		if($scope.osrStored){
    			angular.copy($rootScope.globalOSRList, $scope.globalTempOSRList);
    			$scope.tempOSRList.push({});
    			$scope.osrStored = false;
    		}
	    	
    }	
    };
    
    $scope.captureIndex = function(selectedOSR){
    	$scope.indx = $scope.globalTempOSRList.indexOf(selectedOSR);
    };

    $scope.saveAndApply = function() {
    	if($scope.indx != undefined){
    	// 1. Save OSRs
    	if($scope.osrStored == false){
    		var currentOSR = $scope.globalTempOSRList[$scope.indx];
    		// required fields must not be empty

    		if(currentOSR.operation == null){
    			currentOSR.operation = 'AND';
    		}
    		switch(currentOSR.OSRCategory) {
    	    case 'DirectOSR':
    	    	if(currentOSR.value != null && currentOSR.value != ""){
    				angular.copy($scope.globalTempOSRList[$scope.indx], $scope.tempOSRList[$scope.i++]);
    				$scope.osrStored = true;
    			}
    	        break;
    	    case 'OperatorOSR':
    	    	if((currentOSR.operator != null && currentOSR.operator != "") && (currentOSR.value != null && currentOSR.value != "")){
    				angular.copy($scope.globalTempOSRList[$scope.indx], $scope.tempOSRList[$scope.i++]);
    				$scope.osrStored = true;
    			}
    	        break;
    	    case 'ComplexOSR':
    	    	switch(currentOSR.rule) {
    	        case 'sameAs':
    	        	if(currentOSR.offeringURI != null && currentOSR.offeringURI != ""){
        				angular.copy($scope.globalTempOSRList[$scope.indx], $scope.tempOSRList[$scope.i++]);
        				$scope.osrStored = true;
        			}
    	            break;
    	        case 'cardinality':
    	        	if((currentOSR.minCardinality != null && currentOSR.minCardinality != "") && (currentOSR.maxCardinality != null && currentOSR.maxCardinality != "")){
        				angular.copy($scope.globalTempOSRList[$scope.indx], $scope.tempOSRList[$scope.i++]);
        				$scope.osrStored = true;
        				// update minCardinality and maxCardinality for the current ingredient
        				$scope.currentIngredient.minCardinality = currentOSR.minCardinality;
        				$scope.currentIngredient.maxCardinality = currentOSR.maxCardinality;
        			}
    	            break;
    	        case 'combinedPrice':
    	        	if((currentOSR.operator != null && currentOSR.operator != "") && (currentOSR.value != null && currentOSR.value != "")){
        				angular.copy($scope.globalTempOSRList[$scope.indx], $scope.tempOSRList[$scope.i++]);
        				$scope.osrStored = true;
        			}
    	            break;
    	    	}
    	        break;
    		}
    	}
    	
    	if($scope.osrStored || $scope.deleted){
    		$scope.OSRList[$scope.currentIngredient.id] = [];
    		angular.copy($scope.tempOSRList, $scope.OSRList[$scope.currentIngredient.id]);
    		// OSRs saved
    		// 2. Fetch offerings for the current ingredient based on entered OSRs
    		angular.copy($scope.OSRList, $scope.recipe_params.OSRMap);
    		$scope.currentIngredient.OSRList = $scope.OSRList;
    		$scope.updateOfferings();
    		$scope.deleted = false;
    	}
    }
    };
    
    $scope.cancel = function() {
    	$scope.configureOSR($scope.currentIngredient);
    };
    
    $scope.deleteRow = function(tempOSR){
    	var index = $scope.tempOSRList.indexOf(tempOSR);
        if(typeof $scope.tempOSRList === 'undefined' || $scope.tempOSRList === null){
            $scope.tempOSRList = [];
        }
        $scope.tempOSRList.splice(index, 1); 
        $scope.i--;
        $scope.deleted = true;

        if($scope.i == 0){
        }
    };
    
    $scope.updateSelection = function(offering, ingredient){
    	if(ingredient.listOfSelectedOfferings === undefined){
    		ingredient.listOfSelectedOfferings = [];
    	}
    	if(offering.isChecked){
    		ingredient.listOfSelectedOfferings.push(offering);
    		ingredient.presentCardinality++;
    	}else{
    		var idx = ingredient.listOfSelectedOfferings.indexOf(offering);
    		if(idx != -1){
    			ingredient.listOfSelectedOfferings.splice(offering);
    			ingredient.presentCardinality--;
    		}
    	}
    };

});