$(document).on("click", "#table-save-button", function(event){
		browserJsRoutes.controllers.browser.BrowserController.saveTable().ajax({
			success : function(data) { 
				handleStatus(data);
				var status_message = data['status-message'];
				$("#status-message").html(status_message);
				$("#status-message").show();
				$("#status-message").delay(1000).fadeOut();
		    },
		    error: function(xhr, status, error) {
				$('body').html(xhr.responseText);
			}
		});
});