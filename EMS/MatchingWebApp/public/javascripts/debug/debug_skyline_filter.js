$(document).on("click", ".sim-filter", function(event){
		handleFacetOpen();
    	$("#facets").append(getSimFilterHtml());
    	// Store the ids before going to the skyline.
    	window.selectedIds =  window.ids.slice(0);
 });
 
$(document).on("click", "#debug_sim_selector", function(event) {
	setIdsBySkyline();
});

$(document).on("change", "#debug_sim_count", function(event) {
	setIdsBySkyline();
});

function setIdsBySkyline() {
	checked = $('#debug_sim_selector input[type=radio]:checked');
	simSelection = checked.val();
	label = $("label[for='" + checked.attr('id') + "']").text();
	simCount = $("#debug_sim_count").val();
	
	JsonMsg = {
		"ids": window.selectedIds,
		"simSelection" : simSelection,
		"simCount" : simCount,
	};
			
	JsonMsg = JSON.stringify(JsonMsg);
		
 	debugJsRoutes.controllers.debug.DebugController.getIdsBySkyline().ajax({
 		contentType : 'application/json; charset=utf-8',	        
	    data: JsonMsg,
	    async: false,
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
 
function getSimFilterHtml() {
	return  '<div class="simFilterContainer">' +
			'	<div class="simFilterHeader">' +
			'		<a href="javascript:{}" class="close">×</a> ' +
			'		<strong> Find skyline pairs </strong>' +
			'	</div>' +
			'	<div class="simFilterBody">' +
			'		<form id="debug_sim_selector" action="">' +
			'			<input type="radio" name="selection" id="mostSimilar" value="MOST_SIM"> <label for="mostSimilar" style="display:inline">Most similar pairs</label><br>' +
			'			<input type="radio" name="selection" id="leastSimilar" value="LEAST_SIM"> <label for="leastSimilar" style="display:inline">Least similar pairs</label><br>' +
			'		</form>' +
			'		Number of pairs to find: <br>' +
			'		<select id="debug_sim_count">' +
			'			  <option value="2">2</option>' +
			'			  <option value="5">5</option>' +
			'			  <option value="10">10</option>' +
			'			  <option value="20">20</option>' +
			'		</select>' +
			'	</div>' +
			'</div>';
}