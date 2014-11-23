$(document).on("click", ".status-filter", function(event){
		handleFacetOpen();
    	$("#facets").append(getStatusFilterHtml()); 
 });
 
 
$(document).on("change", "#debug_status_selector", function(event) {
	checked = $('#debug_status_selector input[type=radio]:checked');
	status = checked.val();
	label = $("label[for='"+checked.attr('id')+"']").text();
 	setIdsByMatchingStatus(status);
});

$(document).on("click", ".status_selector", function(event) {
	status = $(this).attr("value");
	label = $(this).attr("label");
	setIdsByMatchingStatus(status);
});

function setIdsByMatchingStatus(status){
	debugJsRoutes.controllers.debug.DebugController.getIdsByMatchingStatus(status).ajax({
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
 
function getStatusFilterHtml() {

	return  '<div class="statusFilterContainer">' +
			'	<div class="statusFilterHeader">' +
			'		<a href="javascript:{}" class="close">×</a> ' +
			'		<strong> Filter by match status </strong>' +
			'	</div>' +
			'	<div class="statusFilterBody">' +
			'		<form id="debug_status_selector" action="">' +
			'	  		<input type="radio" name="selection" id="all" value="ALL" checked="checked"> <label for="all" style="display:inline">All pairs in the candidate set</label><br>' +
			'			<input type="radio" name="selection" id="matched" value="MATCHED"> <label for="matched" style="display:inline">Matched pairs</label><br>' +
			'			<input type="radio" name="selection" id="nonmatched" value="NONMATCHED"> <label for="nonmatched" style="display:inline">Non-matched pairs</label>' +
			'		</form>' +
			'	</div>' +
			'</div>';
}