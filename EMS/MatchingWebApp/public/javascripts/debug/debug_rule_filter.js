$(document).on("click", ".rule-filter", function(event){
		handleFacetOpen();
    	$("#facets").append(getRuleFilterHtml()); 
});
 
$(document).on("click", "#debug_rule_selector li a", function(event) {
	ruleName = $(this).text();
	setIdsByRuleName(ruleName);	
});

$(document).on("click", ".rule_selector", function(event) {
	ruleName = $(this).attr("value");
	setIdsByRuleName(ruleName);
});

function setIdsByRuleName(ruleName) {
	debugJsRoutes.controllers.debug.DebugController.getIdsByRuleName(ruleName).ajax({
		success : function(data) { 
			window.ids = eval(data);   
			prepareFirstTablePage();
			uncheckRadios();
			updateHeaderMessage(ruleName + " rule matches: " + window.ids.length + " item pairs");
			$('#debug_rule_selection').html(ruleName);
	    },
	    error: function(xhr, status, error) {
			$('body').html(xhr.responseText);
		}
	});
}
 
function getRuleFilterHtml() {
	html =  '<div class="ruleFilterContainer">' +
			'	<div class="ruleFilterHeader">' +
			'		<a href="javascript:{}" class="close">×</a> ' +
			'		<strong> Show matches for rule </strong>' +
			'	</div>' +
			'	<div class="ruleFilterBody">' +
			'		Select rule: ' +
			'		<div class="dropdown" id="debug_rule_selector" style="display:inline">' +
			'			  <button class="btn btn-default dropdown-toggle" type="button" id="dropdownMenu1" data-toggle="dropdown">' +
			'			   	<span id="debug_rule_selection">Not selected</span>' +
			'			    <span class="caret"></span>' +
			'			  </button>' +
			'			  <ul class="dropdown-menu" role="menu" aria-labelledby="dropdownMenu1">';
							ruleNames = getAllRuleNames();
							for (var i = 0; i < ruleNames.length; i++) {
								html = html +
			'				    <li role="presentation"><a role="menuitem" tabindex="-1" href="#">' + ruleNames[i] + '</a></li>';
						    }
			html = html +
			'			  </ul>' +
			'		</div>' +
			'	</div>' +
			'</div>';
	return html;
}