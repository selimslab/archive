/*Controller to store recipe instances, load recipes instances dynamically and to handle click events*/
app.controller('RecipeRuntimeConfigurationController', function($scope, $http, $document, $rootScope, ServerUrl, $window) {
	
    // store the RecipeRuntimeConfiguration first, the server will take care of deployment
    $scope.createAndDeployRRC = function (clickEvent)
    {
    	var rrc_params = {};
    	rrc_params.recipe = $scope.recipe_params['recipe'];
    	rrc_params.listOfIRCs = [];
		var offeringsSelected = true;
    	
    	for(var i = 0; i < $rootScope.list_of_ingredients.length; i++){
    		
    		var thisIngredient = $rootScope.list_of_ingredients[i];
    		
    		if(thisIngredient.listOfSelectedOfferings != undefined && thisIngredient.listOfSelectedOfferings != null
					&& thisIngredient.listOfSelectedOfferings.length != 0){
    		
	    		rrc_params.listOfIRCs[i] = {};
	    		rrc_params.listOfIRCs[i].id = thisIngredient.id;
	    		rrc_params.listOfIRCs[i].category = thisIngredient.category;
	    		rrc_params.listOfIRCs[i].presentCardinality = thisIngredient.presentCardinality;
	    		rrc_params.listOfIRCs[i].listOfSelectedOfferings = thisIngredient.listOfSelectedOfferings;
	
	    		if(thisIngredient.OSRList != undefined && thisIngredient.OSRList[thisIngredient.id] != undefined){
	    			var thisIngredientsOSRs = thisIngredient.OSRList[thisIngredient.id];
	    			var y = 0;
	    			
		    		for(var x = 0; x < thisIngredientsOSRs.length; x++){
		    			var currentOSR = thisIngredientsOSRs[x];
	
		    			switch(currentOSR.OSRCategory) {
		        	    case 'DirectOSR':
		        	    	if(currentOSR.value != null && currentOSR.value != ""){
		        	    		if(rrc_params.listOfIRCs[i].OSRList === undefined){
			    					rrc_params.listOfIRCs[i].OSRList = [];
			    				}
			    				rrc_params.listOfIRCs[i].OSRList[y++] = currentOSR;
		        			}
		        	        break;
		        	    case 'OperatorOSR':
		        	    	if((currentOSR.operator != null && currentOSR.operator != "") && (currentOSR.value != null && currentOSR.value != "")){
		        	    		if(rrc_params.listOfIRCs[i].OSRList === undefined){
			    					rrc_params.listOfIRCs[i].OSRList = [];
			    				}
			    				rrc_params.listOfIRCs[i].OSRList[y++] = currentOSR;
		        			}
		        	        break;
		        	    case 'ComplexOSR':
		        	    	switch(currentOSR.rule) {
		        	        case 'sameAs':
		        	        	if(currentOSR.offeringURI != null && currentOSR.offeringURI != ""){
		        	        		if(rrc_params.listOfIRCs[i].OSRList === undefined){
		    	    					rrc_params.listOfIRCs[i].OSRList = [];
		    	    				}
		    	    				rrc_params.listOfIRCs[i].OSRList[y++] = currentOSR;
		            			}
		        	            break;
		        	        case 'cardinality':
		        	        	if((currentOSR.minCardinality != null && currentOSR.minCardinality != "") && (currentOSR.maxCardinality != null && currentOSR.maxCardinality != "")){
		        	        		if(rrc_params.listOfIRCs[i].OSRList === undefined){
		    	    					rrc_params.listOfIRCs[i].OSRList = [];
		    	    				}
		    	    				rrc_params.listOfIRCs[i].OSRList[y++] = currentOSR;
		            			}
		        	            break;
		        	        case 'combinedPrice':
		        	        	if((currentOSR.operator != null && currentOSR.operator != "") && (currentOSR.value != null && currentOSR.value != "")){
		        	        		if(rrc_params.listOfIRCs[i].OSRList === undefined){
		    	    					rrc_params.listOfIRCs[i].OSRList = [];
		    	    				}
		    	    				rrc_params.listOfIRCs[i].OSRList[y++] = currentOSR;
		            			}
		        	            break;
		        	    	}
		        	        break;
		        		}
		    		}
	    		}
    		}
			else{
				offeringsSelected = false;
			}
		}
    	
    	if(offeringsSelected){
			$http.post(ServerUrl + 'create_recipe_runtime_configuration', JSON.stringify(rrc_params))
				.success(function(data, status){
					//$scope.ri_data = data;
					$window.alert('Recipe Runtime Configuration created successfully!');
					console.log('RecipeRuntimeConfiguration created successfully!');
		    })
		    	.error(function(data) {
		    		console.log('Error: ' + data);
	        });
			//$document.scrollTopAnimated($('#page5').offset().top-$('#page1').offset().top,800);
	
	/*
	        setTimeout(function() {
	            showDeployStatus();
	        }, 500);
	*/
    	}
		else{
			$window.alert('Please select offerings for each Ingredient!');
			//showAlert('Please select offerings for each Ingredient!');
			console.log("Error: Offerings not selected!");
		}
    }

});




app.directive('durum', function() {

    return {

        link: function() {

            document.getElementById("statusText").style.color = '#ffc107';
            $('#statusText').text('Deploying...');

            setTimeout(function() {
                $('#statusBadge').removeClass("loader");
                document.getElementById("statusText").style.color = '#28a745';
                $('#statusText').text('Successfully Deployed.');
                //   scope.statusImage = "app/images/success.png";
            }, 1500);

        }

    }
})