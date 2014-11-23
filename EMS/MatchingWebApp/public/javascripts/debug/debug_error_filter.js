$(document).on("click", ".gold-filter", function(event){
		handleFacetOpen();
    	$("#facets").append(getGoldFilterHtml()); 
 });
 
$(document).on("change", "#debug_gold_selector", function(event) {
	checked = $('#debug_gold_selector input[type=radio]:checked');
	status = checked.val();
	label = $("label[for='"+checked.attr('id')+"']").text();
	setIdsByErrorStatus(status);	
});

$(document).on("click", ".gold_selector", function(event) {
	status = $(this).attr("value");
	label = $(this).attr("label");
	setIdsByErrorStatus(status);
});

function setIdsByErrorStatus(status) {
	debugJsRoutes.controllers.debug.DebugController.getIdsByErrorStatus(status).ajax({
			success : function(data) { 
				window.ids = eval(data);   
				prepareFirstTablePage();
				updateHeaderMessage(label + ": " + window.ids.length + " item pairs");
		    },
		    error: function(xhr, status, error) {
				$('body').html(xhr.responseText);
			}
		});
}
 
function getGoldFilterHtml() {
	return  '<div class="goldFilterContainer">' +
			'	<div class="goldFilterHeader">' +
			'		<a href="javascript:{}" class="close">×</a> ' +
			'		<strong> Show precision/recall errors </strong>' +
			'	</div>' +
			'	<div class="goldFilterBody">' +
			'		<form id="debug_gold_selector" action="">' +
			'	  		<input type="radio" name="selection" id="FP" value="FP"> <label for="FP" style="display:inline">Precision errors</label><br>' +
			'			<input type="radio" name="selection" id="FN" value="FN"> <label for="FN" style="display:inline">Recall errors</label><br>' +
			'		</form>' +
			'	</div>' +
			'</div>';
}
 