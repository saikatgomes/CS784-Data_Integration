$(document).on("mouseenter", ".edit-container", function(event){
 	$(this).children(".data-table-cell-edit").css("visibility","visible");
});

$(document).on("mouseleave", ".edit-container", function(event){
 	$(this).children(".data-table-cell-edit").css("visibility","hidden");
});

$(document).on("click", ".edit-container", function(event){
 	var self = $(this);
	var attrName = self.closest(".editable-cell").attr("attrName");
	var itemid = self.closest(".editable-cell").attr("itemid");
	var value = self.closest(".editable-cell").children(".value").text();
	$('#cell-edit-modal').find('textarea').val(value);
	
	// This is how I transfer these values to the modal.
	// TODO: Is this a principled way?
	$('#cell-edit-modal').attr("attrName", attrName);
	$('#cell-edit-modal').attr("itemid", itemid);
	
	$('#cell-edit-modal').modal('show');
});

$(document).on("click", "#cell-edit-modal-submit", function(event) {

	var itemid = $("#cell-edit-modal").attr("itemid");
	var attrName = $("#cell-edit-modal").attr("attrName");
	var value = $('#cell-edit-modal').find('textarea').val();
		 		
	JsonMsg = {
	     "attrName": attrName,
	     "itemid": itemid,
	     "value": value
	};
	
	
	JsonMsg = JSON.stringify(JsonMsg);
			
	browserJsRoutes.controllers.browser.BrowserController.saveCellEdit().ajax({
	
		contentType : 'application/json; charset=utf-8',	        
        data: JsonMsg,
		success : function(data) { 
			handleStatus(data);
    		$('#cell-edit-modal').modal('hide');
    		//update the cell.
    		$('.editable-cell[itemid="' + itemid + '"][attrName="' + attrName + '"]').children(".value").html(value);
	    },
        error: function(xhr, status, error) {
			$('body').html(xhr.responseText);
		}
	});
});	
	
