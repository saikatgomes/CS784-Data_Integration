$(document).on("click", ".remove-column", function(event){

 	var attrName = $(this).closest("th").children(".browser-header-name").attr("attrName");
 	
 	browserJsRoutes.controllers.browser.BrowserController.removeColumn(attrName).ajax({
		success : function(data) { 
			prepareFirstTablePage();
			handleStatus(data);	    
	    },
        error: function(xhr, status, error) {
			$('body').html(xhr.responseText);
		}
	});
 });	 
 
$(document).on("click", ".row-selector", function(event) {

	var itemid = $(this).closest(".item-row").attr("itemid");
	var isSelected = $(this).is(":checked");
	
	if (isSelected) {
		window.selectedIds[itemid] = 1;
	} else {
		delete window.selectedIds[itemid];
	}
	
});

$(document).on("click", ".remove-rows", function(event) {

	JsonMsg = {
		"ids": Object.keys(window.selectedIds)
	};	

	JsonMsg = JSON.stringify(JsonMsg);
			
	browserJsRoutes.controllers.browser.BrowserController.removeRows().ajax({
		contentType : 'application/json; charset=utf-8',	        
	    data: JsonMsg,
		success : function(data) {
			handleStatus(data);	 
			for (itemid in window.selectedIds) {	
				// TODO ask: In case we are in the filter mode
				// we need to update our ids to be current.
				// Otherwise, it will try to display the item that
				// does not exist.
				// How do we handle delete from the list, which is expensive?
				// Note assuming window.ids exists.
				window.ids.splice(window.ids.indexOf(itemid), 1);
			}			
			prepareFirstTablePage();
			updateHeaderMessage(getDefaultHeaderMessage());
		},
	    error: function(xhr, status, error) {
			$('body').html(xhr.responseText);
		}
	});	
});