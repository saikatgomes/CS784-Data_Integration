function createTableForTuples(tuples) {
	
	header = tuples["header"];
	rows = tuples["rows"];
	
	table = '<table id="table-browser" class="table table-bordered table-condensed table-hover tablesorter" width="80%">';
	
	thead = '<thead>' +
			'	<th> ' +
			'		<div class="browser-header-name"> ' +
			'			all' +
			'			<div class="dropdown header-dropdown">' +
			'			  <button class="btn btn-default dropdown-toggle" type="button" id="dropdownMenu1" data-toggle="dropdown">' +
			'			    <span class="caret"></span>' +
			'			  </button>' +
			'			  <ul class="dropdown-menu browser-dropdown-menu" role="menu" aria-labelledby="dropdownMenu1">' +
			'			    <li role="presentation">' +
			'			   	 	<a role="menuitem" tabindex="-1" href="javascript:{}" ' +
			'			    		class="all-explorer">' +
			'			    		Show statistics for table' +
			'			    	</a>' +
			'			    	<a role="menuitem" tabindex="-1" href="#sample-modal" data-toggle="modal"' +
			'			    		class="sample">' +
			'			    		Take a sample of table' +
			'			    	</a>' +
			'			    	<a role="menuitem" tabindex="-1" href="javascript:{}" ' +
			'			    		class="remove-rows">' +
			'			    		Remove selected rows' +
			'			    	</a>' +
			'			    </li>' +
			'			  </ul>' +
			'			</div>' +
			'		</div>' +
			'	</th>';
			
	for (var i = 0; i < header.length ; i++) {
		thead = thead + 
			'		<th> ' +
			'			<div class="browser-header-name" attrName="'+ header[i] + '"> ' +
			'				' + header[i] +
			'				<div class="dropdown header-dropdown">' +
			'				  <button class="btn btn-default dropdown-toggle" type="button" id="dropdownMenu1" data-toggle="dropdown">' +
			'				    <span class="caret"></span>' +
			'				  </button>' +
			'				  <ul class="dropdown-menu  browser-dropdown-menu" role="menu" aria-labelledby="dropdownMenu1">' +
			'				    <li role="presentation">' +
			'				    	<a role="menuitem" tabindex="-1" href="javascript:{}" ' +
			'				    		class="explorer">' +
			'				    		Show statistics for attribute' +
			'				    	</a>' +
			'				    	<a role="menuitem" tabindex="-1" href="javascript:{}" ' +
			'				    		class="sort">' +
			'				    		Sort attribute values' +
			'				    	</a>' +
			'				    	<a role="menuitem" tabindex="-1" href="javascript:{}" ' +
			'				    		class="filter">' +
			'				    		Search for a string in attribute values' +
			'				    	</a>' +
			'				    	<a role="menuitem" tabindex="-1" href="javascript:{}" ' +
			'				    		class="query">' +
			'				    		Query attribute values' +
			'				    	</a>' +
			'				    	<a role="menuitem" tabindex="-1" href="javascript:{}" ' +
			'			    			class="remove-column">' +
			'			    			Remove attribute from table' +
			'			    		</a>' +
			'				    </li>' +
			'				  </ul>' +
			'				</div>' +
			'			</div>' +
			'		</th>';		
	}	
	
	tbody = '<tbody>';
	
	Object.keys(rows).forEach(function (key) {
		tbody = tbody + 
				'			<tr class="item-row" itemid ="' + key + '">' +
				'				<td>' +
				'					<div class="checkbox">' +
				'					    <label>';
									    	if(key in window.selectedIds) {
									    		tbody = tbody +
				'					      		<input type="checkbox" class="row-selector" checked>';
									      	} else {
									      		tbody = tbody +
				'					      		<input type="checkbox" class="row-selector">';
				  				      	    }
		tbody = tbody + 
				'					    </label>' +
				'					</div>' +
				'				</td>';
		row = rows[key];
		for (var i = 0; i < row.length ; i++) {
			tbody = tbody + 
				'					<td class="editable-cell" attrName="' + header[i] + '" itemid ="' + key + '">' +
				'						<div class="edit-container">' +
				'							<a href="javascript:{}" class="data-table-cell-edit" title="Edit this cell">&nbsp;</a>' +
				'						</div>' +
				'						<span class="value"> ' + row[i] + ' </span>' +
				'					</td>';
		}
		tbody = tbody + 
				'			</tr>';
	});				
	
	tbody = tbody + '</tbody>';
	
	table = table + thead + tbody;	
	table = table + '</table>';
	
	return table;
}