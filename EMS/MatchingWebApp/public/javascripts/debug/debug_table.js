function createTableForTuples(tuples) {

	html = '';

	for (tupleId in tuples) {
		tuple = tuples[tupleId];
		switch (mode) {
		    case 1:
		        html = html + getRowForMode1(tuple);
		        break;
		    case 2: 
		    	html = html + getRowForMode2(tuple);
		    	break;
		    case 3: 
		    	html = html + getRowForMode3(tuple);
		    	break;
		    default:
		        //alert("Invalid operation mode");
		}
	}
	
	return html;
}


function getRowForMode1(tuple) {

	item1 = tuple["item1"];
	item2 = tuple["item2"];
	audit = tuple["audit"];
	gold = tuple["gold"];
	features = tuple["features"];
	matchStatus = audit["match-status"];
	inGold = gold["gold-match-status"];
	
	itemBox1 = createItemBox(item1);
	itemBox2 = createItemBox(item2);
	auditBox = createAuditBox(audit, features);
			
	html = createPairRowWithGold(itemBox1 + itemBox2 + auditBox, matchStatus, inGold);
	
	return html;
}

function getRowForMode2(tuple) {

	item1 = tuple["item1"];
	item2 = tuple["item2"];
	audit = tuple["audit"];
	features = tuple["features"];
	matchStatus = audit["match-status"];
	
	itemBox1 = createItemBox(item1);
	itemBox2 = createItemBox(item2);
	auditBox = createAuditBox(audit, features);
			
	html = createPairRow(itemBox1 + itemBox2 + auditBox, matchStatus);
	
	return html;
}

function getRowForMode3(tuple) {

	item1 = tuple["item1"];
	item2 = tuple["item2"];
	features = tuple["features"];
	matchStatus = "NONMATCH";
	
	itemBox1 = createItemBox(item1);
	itemBox2 = createItemBox(item2);
	featureBox = createFeatureBox(features);
			
	html = createPairRow(itemBox1 + itemBox2 + featureBox, matchStatus);
	
	return html;
}

function createPairRow(boxes, matchStatus) {
	html = '';
	
	if(matchStatus == "MATCH") {
		html = html +
		'<div class="item-pair-row matched">';
    } else {
    	html = html +
		'<div class="item-pair-row">';
	}
	
	return html + boxes + '</div>';
}


function createPairRowWithGold(boxes, matchStatus, inGold) {
	html = '';
	
	if(matchStatus == "MATCH") {
		html = html +
		'<div class="item-pair-row matched';
		
		if (inGold) {
			html = html + ' in-gold';
		}
		
		html = html +
									'">';
    } else {
    
    	html = html +
		'<div class="item-pair-row';
		
		if (inGold) {
			html = html + ' in-gold';
		}
		
		html = html +
									'">';
	}
	
	return html + boxes + '</div>';
}

function createItemBox(item) {
	
	tableName = item["table-name"];
	rows = item["rows"];

	itemBox =   '			<div class="item-box">' +
				'					<div class="item-box-header">' +
				'						' + tableName + ' item' +
				'					</div>' +
				'					<div class="item-box-body">' +
				'						<table class="table table-bordered table-condensed table-hover tablesorter">' +
				'							<tbody>';
												for (var i = 0; i < rows.length; i++) {
													row = rows[i];
													itemBox = itemBox + 
				'										<tr>';
														for (var j = 0; j < row.length; j++) {
															itemBox = itemBox + 
															'<td>' + row[j] + '</td>';
														}
													itemBox = itemBox +
				'										</tr>';
												}
	itemBox = itemBox +
				'							</tbody>' +
				'						</table>' +
				'					</div>' +
				'			</div>		';

	
	return itemBox;
}

function createFeatureBox(features) {
	featureBox =   '			<div class="item-box">' +
				'					<div class="item-box-header">' +
				'					Feature Scores'  +
				'					</div>' +
				'					<div class="item-box-body">' +
									createFeatureTable(features) + 
				'					</div>' +
				'			</div>		';
				
	return featureBox;
}

function createAuditBox(audit, features) {

	// Note: I am using MatchStatus class values here.

	auditRules = audit["rules"];
		
	auditBox =  '' +
	'				<div class="item-box">' +
	'					<div class="item-box-header">' +
	'						<span>' +
	'						Matching Details' +
	'						</span>' +
						createFeatureTooptip(features) + 
	'					</div>' +
	'					<div class="item-box-body">' +
	'						<div class="tree well">' +
	'						    <ul>';
								for (ruleName in auditRules) {
									
									rule = auditRules[ruleName];
									ruleStatus = rule["match-status"];
									terms = rule["terms"];
									auditBox = auditBox +								
	'							        <li>';
								        	if (ruleStatus == "MATCH") {
								        		auditBox = auditBox +
	'							            	<span class="matched"> <i class="icon-thumbs-up"></i>';
								            } else {
								            	auditBox = auditBox +
	'							            	<span> <i class="icon-thumbs-down"></i>';
								            }
									auditBox = auditBox +
	'							            ' + ruleName + ' </span>';
											
								            for (termId in terms) {
								            	term = terms[termId];
								            	termStatus = term["match-status"];
								            	calculated = term["calculated"];
								            	feature1 = term["feature1"];
								            	operand = term["operand"];
								            	threshold = term["threshold"];
								            	
								            	auditBox = auditBox +
	'								            <ul>' +
	'								                <li>' +
	'								                	<span class="debug-hover" data-toggle="tooltip" data-placement="right" ' +
	'								                	title="Calculated feature is: ' + calculated + '"> ';
									                	if (termStatus == "MATCH") {
									                		auditBox = auditBox +
	'								                		<span class="matched"><i class="icon-thumbs-up"></i> ';
									                	} else {
															auditBox = auditBox +
	'								                		<span><i class="icon-thumbs-down"></i> ';
									                	}
									                	auditBox = auditBox +
									                	feature1 + ' ' + operand + ' ' +  threshold + ' ' +
	'								                		</span>' +
	'								                	</span>' +
	'								                </li>' +
	'								            </ul>';
	  								        }
	  							auditBox = auditBox +
	'							        </li>';
								}
	auditBox = auditBox +
	'						    </ul>' +
	'						</div>	' +
	'					</div>' +
	'				</div>';
								
	return auditBox;
}

function createFeatureTable(features) {
	content = '' +
	'	<table class="table table-bordered table-condensed table-hover tablesorter">' + 
	' 		<tbody>';
			for (feature in features) {
				content = content + 
	'			<tr>' +
	'				<td>' + feature + '</td>' +
	'				<td>' + features[feature] + '</td>' +
	'			</tr>';
			}
	content = content +
	'		</tbody>' + 
	'	</table>';
	return content;
}

function createFeatureTooptip(features) {

	content = '' +
	'<div class="features-popover" style="display: none">' + 
		createFeatureTable(features) + 
	'</div>';
	
	content = content + 
	'<span class="features-tooltip virtual_link"' + 
	'	data-trigger="hover">' +
	'	Feature scores' + 
	'</span>';
		
	return content;
}