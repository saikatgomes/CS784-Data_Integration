window.myPageSize = 10;
window.startRow = 1;
window.ids = [];

function beforeLoad() {
	mode = getOperatingMode();
}

function getOperatingMode() {
	/* operating mode:
		1: Using matching and eval results.
		2: Using matching results.
		3: No matching or eval is done
	*/
	
	debugJsRoutes.controllers.debug.DebugController.getOperationMode().ajax({
		async: false,
		success : function(data) {
			// TODO: alternative to eval.
			mymode = eval(data);
	    },
	    error: function(xhr, status, error) {
			$('body').html(xhr.responseText);
		}
	});
	
	return mymode;
}

// TODO Ask: Is this the right way? I am assuming that the ids can be
// sent from the controller to the browser many times
// in case of search. What if there are millions of rows?
// Also, I am assuming that ids can be stored in the window...
function getAllIds() {
	debugJsRoutes.controllers.debug.DebugController.getAllIds().ajax({
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

function getTuplesForIds(inIds) {

	// Note: ajax post failed with 10000 inIds.
	JsonMsg = {
		"ids": inIds
	};

	JsonMsg = JSON.stringify(JsonMsg);
			
	debugJsRoutes.controllers.debug.DebugController.getTuplesForIds().ajax({
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
			'Use facets and filters to select subsets of the data and' + 
			' change the way data is shown.';
}

function getDefaultHeaderMessage() {
	return "All pairs in the candidate set: " + window.ids.length + " item pairs";
}

function getFacetsHeader() {
	html =  '		Facets and Filters' +
			'		<div class="dropdown header-dropdown debug-header-dropdown">' +
			'			  <button class="btn btn-default dropdown-toggle" type="button" id="dropdownMenu1" data-toggle="dropdown">' +
			'			    <span class="caret"></span>' +
			'			  </button>' +
			' 			<ul class="dropdown-menu" role="menu" aria-labelledby="dropdownMenu1">' +
			'			    <li role="presentation">';
	if (mode == 1 || mode == 2) {
		html = html +
			'			   		<a role="menuitem" tabindex="-1" href="javascript:{}" ' +
			'			    		class="summary">' +
			'			    		Matching Summary' +
			'			    	</a>' +
			'			   	 	<a role="menuitem" tabindex="-1" href="javascript:{}" ' +
			'			    		class="status-filter">' +
			'			    		Filter by matching prediction' +
			'			    	</a>' +
			'			    	<a role="menuitem" tabindex="-1" href="javascript:{}" ' +
			'			    		class="rule-filter">' +
			'			    		Filter by rule' +
			'			    	</a>';
	}
	
	if (mode == 1) {
		html = html +	
			'			    	<a role="menuitem" tabindex="-1" href="javascript:{}" ' +
			'			    		class="gold-filter">' +
			'			    		Filter by precision/recall errors' +
			'			    	</a>';
	}
	
	html = html +		
			'			    	<a role="menuitem" tabindex="-1" href="javascript:{}" ' +
			'			    		class="featureThresh-filter">' +
			'			    		Filter by feature and threshold' +
			'			    	</a>';
			
	if (mode == 1 || mode == 2 || mode == 3) {
		html = html + 	
			'			    	<a role="menuitem" tabindex="-1" href="javascript:{}" ' +
			'			    		class="sim-filter">' +
			'			    		Find skyline pairs' +
			'			    	</a>			    	';
			
	}
	
	html = html +
			'			    </li>' +
			'			  </ul>' +
			'		</div>';
			
	return html;
}

// handle tree structure clicks.
function afterAjax() {
    $('.tree li:has(ul)').addClass('parent_li').find(' > span').attr('title', 'Collapse this branch');
    $('.tree li.parent_li > span').on('click', function (e) {
        var children = $(this).parent('li.parent_li').find(' > ul > li');
        if (children.is(":visible")) {
            children.hide('fast');
           // $(this).attr('title', 'Expand this branch').find(' > i').addClass('icon-plus-sign').removeClass('icon-minus-sign');
        } else {
            children.show('fast');
           // $(this).attr('title', 'Collapse this branch').find(' > i').addClass('icon-minus-sign').removeClass('icon-plus-sign');
        }
        e.stopPropagation();
    });
    
    $('.debug-hover').tooltip();
    
	$('.features-tooltip').popover({ 
	    html : true, 
	    placement: 'bottom',
	    content: function() {
	      return $(this).siblings('.features-popover').html();
	    }
	});
    
}