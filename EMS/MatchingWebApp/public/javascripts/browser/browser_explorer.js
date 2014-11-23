$(document).on("click", ".explorer", function(event){
 	var attrName = $(this).closest("th").children(".browser-header-name").attr("attrName");
 	browserJsRoutes.controllers.browser.BrowserController.getExplorerData(attrName).ajax({
		success : function(data) {
			handleFacetOpen();
	    	$("#facets").append(getExplorer(attrName, data)); 
	    },
        error: function(xhr, status, error) {
			$('body').html(xhr.responseText);
		}
	});
 });
 
$(document).on("click", ".all-explorer", function(event){
 	browserJsRoutes.controllers.browser.BrowserController.getAllExplorerData().ajax({
		success : function(data) {
			handleFacetOpen();
	    	$("#facets").append(getAllExplorer(data)); 
	    },
        error: function(xhr, status, error) {
			$('body').html(xhr.responseText);
		}
	});
});

function getExplorer(attrName, data) {

	percentUnique = data["percent-unique"];
	percentMissing = data["percent-missing"];
	rows = data["rows"];
	
	explorer = '<div class="explorerContainer">' +
				'		<div class="explorerHeader">' +
				'			<a href="javascript:{}" class="close">×</a> ' +
				'			<strong> Statistics for ' + attrName + ' </strong>' +
				'		</div>' +
				'		<div class="explorerDetails">' +
				'			Unique: ' + percentUnique + '% &nbsp; &nbsp;' +
				'			Missing: ' + percentMissing + '%' + 
				'		</div>' +
				'		<div class="explorerBody">' +
				'			<table id="explorerTable" class="table table-bordered table-condensed table-hover tablesorter">' +
				'				<tbody>';
								for (var i = 0; i < rows.length; i++) {
										row = rows[i];
										explorer = explorer + 
				'						<tr>';
										for (var j = 0; j < row.length; j++) {
											explorer = explorer +
				'							<td> ' + row[j] + ' </td>';
										}
											explorer = explorer +
				'						</tr>';
								}
	explorer = explorer +
				'				</tbody>' +
				'			</table>' +
				'		</div>' +
				'</div>';
	return explorer;
}

function getAllExplorer(data) {
	
	tableName = data["table-name"];
	tableSize = data["table-size"];
	attrCount = data["attr-count"];
	rows = data["rows"];

	explorer =  '<div class="explorerContainer">' +
				'		<div class="explorerHeader">' +
				'			<a href="javascript:{}" class="close">×</a> ' +
				'			<strong> Statistics for the '+ tableName + ' table</strong>' +
				'		</div>' +
				'		<div class="explorerDetails">' +
				'			Rows: ' + tableSize + '	&nbsp; &nbsp;' +
				'			Attributes: ' + attrCount +
				'		</div>' +
				'		<div class="explorerBody">' +
				'			<table id="allExplorerTable" class="table table-bordered table-condensed table-hover tablesorter">' +
				'				<thead>' +
				'					<th> Rank </th>' +
				'					<th> Attribute </th>' +
				'					<th> Unique </th>' +
				'					<th> Missing </th>' +
				'				</thead>' +
				'				<tbody>';
				for (var i = 0; i < rows.length; i++) {
					row = rows[i];
					explorer = explorer + 
				'						<tr>';
					for (var j = 0; j < row.length; j++) {
						explorer = explorer + 
				'							<td>' + row[j];
						if (j > 1) {
							explorer = explorer + '%';
						}
				
						explorer = explorer + '</td>';
					}
					explorer = explorer + 
				'						</tr>';
				}
	explorer = explorer +
				'				</tbody>' +
				'			</table>' +
				'		</div>' +
				'</div>';
	return explorer;
}