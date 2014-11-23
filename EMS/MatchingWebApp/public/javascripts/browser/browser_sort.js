 $(document).on("click", ".sort", function(event){
 	var attrName = $(this).closest("th").children(".browser-header-name").attr("attrName");
 	browserJsRoutes.controllers.browser.BrowserController.getSortedIds(attrName).ajax({
		success : function(data) {
			window.ids = eval(data);
			prepareFirstTablePage();		    
	    },
        error: function(xhr, status, error) {
			$('body').html(xhr.responseText);
		}
	});
 });