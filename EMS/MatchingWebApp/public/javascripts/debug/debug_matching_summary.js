$(document).on("click", ".summary", function(event){
		if (mode == 1 || mode == 2) {
	        summaryJson = getMatchingSummaryJson();
			handleFacetOpen();
	    	$("#facets").append(getSummaryHtml(summaryJson)); 
		} 
		// for mode = 3 we simply do not have a summary because there is no matching.
 });
 
function getMatchingSummaryJson() {
	
	debugJsRoutes.controllers.debug.DebugController.getMatchingSummaryJson().ajax({
		async: false,
		success : function(data) {
			summaryJson = data;
	    },
	    error: function(xhr, status, error) {
			$('body').html(xhr.responseText);
		}
	});

	return summaryJson;
}

function getSummaryHtml(summary, mode) {
	
	evalSum = summary["eval"];
	matching = summary["matching"];
	rulesSum = summary["rulesSummary"];
	
	html =  '<div class="summaryContainer">' +
			'	<div class="summaryHeader">' +
			'		<a href="javascript:{}" class="close">×</a> ' +
			'		<strong> Matching Summary </strong>' +
			'	</div>' +
			'	<div class="summaryBody">';
	
	if (evalSum) {
 		html = html + getEvaluationSummaryHtml(evalSum);
	}
	
	if (matching) {
		html = html + getMatchingSummaryHtml(matching);
		
	}
	
	if (rulesSum) {
			html = html + getRuleSummaryHtml(rulesSum);
	}
	
	html = html +
			'	</div>' +
			'</div>';
	return html;
}

function getRuleSummaryHtml(rulesSum) {

	html = 		'		Rule summary:' +
				'		<table class="table table-bordered table-condensed table-hover tablesorter">';
							for (ruleName in rulesSum) {
									html = html +
				'					<tr>' +
				'						<td> ' +
				'							<span class="virtual_link rule_selector" value="' + ruleName + '">' +
				'								' + ruleName + ' matches ' +
				'							</span>' +
				'						</td>' +
				'						<td> ' + rulesSum[ruleName] + '</td>' + 
				'					</tr>';
							}
						html = html +
				'		</table>';
	return html;
}

function getMatchingSummaryHtml(matching) {
				
	return		'		Matching summary:' +
				'		<table class="table table-bordered table-condensed table-hover tablesorter">' +
				'				<tbody>' +
				'					<tr>' +
				'						<td><span class="virtual_link status_selector" value="ALL" label="All pairs in the candidate set">All pairs in the candidate set</span>: </td>' +
				'						<td>' + matching["total-pairs"] + '</td>' +
				'					</tr>' +
				'					<tr>' +
				'						<td><span class="virtual_link status_selector" value="MATCHED" label="Matched pairs">Total matches</span>: </td>' +
				'						<td>' + matching["match-size"] + '</td>' +
				'					</tr>' +
				'					' +
				'					<tr>' +
				'						<td>Total rules: </td>' +
				'						<td>' + matching["rule-count"] + '</td>' +
				'					</tr>' +
				'					' +
				'				</tbody>' +
				'		</table>';
}


function getEvaluationSummaryHtml(evalSum) {

	return		'		Evaluation summary:' +
				'		<table class="table table-bordered table-condensed table-hover tablesorter">' +
				'				<tr>' +
				'					<td>Precision:</td>' +
				'					<td>' + evalSum["precision"] + '</td>' +
				'				</tr>' +
				'				<tr>' +
				'					<td>Recall:</td>' +
				'					<td>' + evalSum["recall"] + '</td>' +
				'				</tr>' +
				'				<tr>' +
				'					<td>F1 score:</td>' +
				'					<td>' + evalSum["F1"] + '</td>' +
				'				</tr>' +
				'				<tr>' +
				'					<td><span class="virtual_link gold_selector" value="FP" label="Precision errors">Number of precision errors:</span> </td>' +
				'					<td>' + evalSum["FP"] + '</td>' +
				'				</tr>' +
				'				<tr>' +
				'					<td><span class="virtual_link gold_selector" value="FN" label="Recall errors">Number of recall errors:</span> </td>' +
				'					<td>' + evalSum["FN"] + '</td>' +
				'				</tr>' +
				'		</table>';
}

