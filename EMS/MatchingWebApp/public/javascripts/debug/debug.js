
$(document).ready(function(){
	debugInit();	
});

function debugInit() {
	$(".summary").trigger('click');
}

function getAllRuleNames() {
	debugJsRoutes.controllers.debug.DebugController.getAllRuleNames().ajax({
		async: false,
		success : function(data) {
			// TODO: alternative to eval.
			ruleNames = eval(data);
	    },
	    error: function(xhr, status, error) {
			$('body').html(xhr.responseText);
		}
	});
	return ruleNames;
}

function getAllFeatureNames() {
	debugJsRoutes.controllers.debug.DebugController.getAllFeatureNames().ajax({
		async: false,
		success : function(data) {
			// TODO: alternative to eval.
			featureNames = eval(data);
	    },
	    error: function(xhr, status, error) {
			$('body').html(xhr.responseText);
		}
	});
	return featureNames;
}

function uncheckRadios() {
	// empty all other checkboxes.
	$('#debug_status_selector input[type=radio]:checked').prop('checked', false);
	$('#debug_sim_selector input[type=radio]:checked').prop('checked', false);
}

