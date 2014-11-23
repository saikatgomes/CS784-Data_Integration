 
 $(document).on("click", ".filter", function(event){
  	handleFacetOpen();
 	var attrName = $(this).closest("th").children(".browser-header-name").attr("attrName");
	$("#facets").append(getFilterFacetHtml(attrName)); 
 });
 
 $(document).on("click", ".query", function(event){
    handleFacetOpen();
 	var attrName = $(this).closest("th").children(".browser-header-name").attr("attrName");
	$("#facets").append(getQueryFacetHtml(attrName)); 
 });
 
 $(document).on("click", ".filterHeader > .close", function(event) {
 	handleFilterQueryClose();
 });
 
 $(document).on("click", ".queryHeader > .close", function(event){
 	handleFilterQueryClose();
 });

$(document).on("keyup paste", ".queryTextArea, .filterTextArea", function(event) {
 	handleQuery();
});

$(document).on("change", ".queryOperation", function(event) {
 	handleQuery();
});

function handleQuery() {

	var attrNames = [];
 	var vals = [];
 	var ops = [];
 	
 	$( ".queryTextArea" ).each( function() {
		attrName = $(this).closest(".queryContainer").children(".queryHeader").attr("attrName");
		attrNames.push(attrName);
		val = $(this).val();
		vals.push(val);
		op = $(this).closest(".queryBody").children(".queryOperation").val();
		ops.push(op);
	});
	
	
	$( ".filterTextArea" ).each( function() {
		attrName = $(this).closest(".filterContainer").children(".filterHeader").attr("attrName");
		attrNames.push(attrName);
		val = $(this).val();
		vals.push(val)
		ops.push("CONTAINS");
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
		     "attrNames": attrNames,
		     "vals": vals,
		     "ops": ops
		};
		
		JsonMsg = JSON.stringify(JsonMsg);

  		browserJsRoutes.controllers.browser.BrowserController.getBrowserQueryResults().ajax({
	
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
	
function handleFilterQueryClose() {
 	if (!$('.filterContainer').length && !$('.queryContainer').length) {
		refresh();
 	}
 	handleQuery();		 	
}

function getQueryFacetHtml(attrName) {
	return '<div class="queryContainer">' +
				'<div class="queryHeader" attrName="'+ attrName + '">' +
					'<a href="javascript:{}" class="close">×</a>' +
					'<strong> Query on '+ attrName + ' </strong>' +
				'</div>' +
				'<div class="queryBody">' +
					'<select class="form-control queryOperation">' +
					    '<option value="EQUALS" selected>=</option>' +
					    '<option value="GREATER_THAN">&gt;</option>' +
					    '<option value="GREATER_THAN_EQUALS">&gt;=</option>' +
					    '<option value="LESS_THAN">&lt;</option>' +
					    '<option value="LESS_THAN_EQUALS">&lt;=</option>' +
					'</select>' +
					'<textarea class="queryTextArea"></textarea>' +
				'</div>' +
			'</div>';
}

function getFilterFacetHtml(attrName) {
	return	'<div class="filterContainer">' +
				'<div class="filterHeader" attrName="' + attrName + '">' +
					'<a href="javascript:{}" class="close">×</a> ' +
					'<strong> Search for string in ' + attrName + '</strong>' +
					'</div>' +
					'<div class="filterBody">' +
					'<textarea class="filterTextArea"></textarea>' +
				'</div>' +
			'</div>';
}

