window.myPageSize = 10;
window.startRow = 1;
window.ids = [];


function beforeLoad() {

}

// TODO Ask: Is this the right way? I am assuming that the ids can be
// sent from the controller to the browser many times
// in case of search. What if there are millions of rows?
// Also, I am assuming that ids can be stored in the window...
function getAllIds() {
	browserJsRoutes.controllers.browser.BrowserController.getAllIds().ajax({
		async: false,
		success : function(data) {
			// TODO: alternative to eval.
			window.ids = eval(data);
	    },
	    error: function(xhr, status, error) {
			$('body').html(xhr.responseText);
		}
	});
}

function getDefaultHeaderMessage() {
		return window.ids.length + " rows";
}

function afterAjax() {
}

function getTuplesForIds(inIds) {

	// Note: ajax post failed with 10000 inIds.
	JsonMsg = {
		"ids": inIds
	};

	JsonMsg = JSON.stringify(JsonMsg);
			
	browserJsRoutes.controllers.browser.BrowserController.getTuplesForIds().ajax({
		contentType : 'application/json; charset=utf-8',	        
	    data: JsonMsg,
	    async: false,
		success : function(data) {
			tuples = data;
		},
	    error: function(xhr, status, error) {
			$('body').html(xhr.responseText);
		}
	});	
	
	return tuples;
}

function getFacetsDesc() {
	return  '<strong> Using facets and filters </strong>' + 
			'<br>' +
			'Use facets and filters to select subsets of your data to act on.' + 
			'<br>' + 
			'Choose facet and filter methods from the menus at the top of each' + 
			'data column.';
}

function getFacetsHeader() {
	return 'Facets and Filters';
}