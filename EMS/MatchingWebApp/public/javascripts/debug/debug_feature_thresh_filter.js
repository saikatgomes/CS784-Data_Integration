$(document).on("click", ".featureThresh-filter", function(event){
  	handleFacetOpen();
	$("#facets").append(getFeatureThreshFacetHtml()); 
});


$(document).on("change", ".featureSelection, .queryOperation", function(event) {
 	handleQuery();
});

$(document).on("keyup paste", ".queryTextArea", function(event) {
 	handleQuery();
});

// TODO: Both browser and the debug have the query 
// operations. I should abstract away some code and share.
function handleQuery() {

	var featureNames = [];
 	var vals = [];
 	var ops = [];
 	
 	$( ".queryTextArea" ).each( function() {
		featureName = $(this).siblings(".featureSelection").val();
		featureNames.push(featureName);
		val = $(this).val();
		vals.push(val);
		op = $(this).siblings(".queryOperation").val();
		ops.push(op);
	});
	
	// check special case that all the values get
	// empty and we need to refresh.
	allEmpty = 1;
	for (var i = 0; i < vals.length; i++) {
		if (vals[i] != "") {
			allEmpty = 0;
		}
	}
	
 	if (allEmpty) {
 		refresh();
 	} else {

		JsonMsg = {
		     "featureNames": featureNames,
		     "vals": vals,
		     "ops": ops
		};
		
		JsonMsg = JSON.stringify(JsonMsg);

  		debugJsRoutes.controllers.debug.DebugController.getDebugQueryResults().ajax({
			contentType : 'application/json; charset=utf-8',	        
	        data: JsonMsg,
			success : function(data) { 
				window.ids = eval(data);   
				prepareFirstTablePage();
				updateHeaderMessage(getDefaultHeaderMessage());
			},
	        error: function(xhr, status, error) {
				$('body').html(xhr.responseText);
			}
		});
    }   
}

$(document).on("click", ".featureThreshHeader > .close", function(event){
 	handleQueryClose();
});
	
function handleQueryClose() {
 	if (!$('.featureThreshContainer').length) {
		refresh();
 	}
 	// TODO: Do I need this statement? Remove and test.
 	handleQuery();		 	
}

function getFeatureThreshFacetHtml() {
	var html = '<div class="featureThreshContainer">' +
				'<div class="featureThreshHeader">' +
					'<a href="javascript:{}" class="close">×</a>' +
					'<strong> Filter by feature and threshold</strong>' +
				'</div>' +
				'<div class="featureThreshBody">' +
					'Feature name'+ 
					  '<br>' + 
					   '<select class="form-control featureSelection">';
					  	featureNames = getAllFeatureNames();
						for (var i = 0; i < featureNames.length; i++) {
							html = html +
					    	'<option value="' + featureNames[i] + '">' + featureNames[i] + '</option>';
					    }
			html = html + 
						'</select>' + 
					'<select class="form-control queryOperation">' +
					    '<option value="EQUALS" selected>=</option>' +
					    '<option value="GREATER_THAN">&gt;</option>' +
					    '<option value="GREATER_THAN_EQUALS">&gt;=</option>' +
					    '<option value="LESS_THAN">&lt;</option>' +
					    '<option value="LESS_THAN_EQUALS">&lt;=</option>' +
					'</select>' +
					'Threshold' + 
					'<br>' + 
					'<textarea class="queryTextArea"></textarea>' +
				'</div>' +
			'</div>';
	return html;
}
