/*
Assumptions for every page that uses this js:

	* window.myPageSize is defined
	* window.startRow is defined
	* window.ids is defined
	* getTuplesForIds() is defined
	* createTableForTuples() is defined
	* getAllIds() is defined
	* getFacetsDesc() is defined
	* getFacetsHeader() is defined
	* getDefaultHeaderMessage() is defined
	* afterAjax() is defined
	* beforeLoad() is defined
	
	// TODO: How do I construct abstract classes in javascript?
*/

$(document).ready(function(){
	beforeLoad();
	getAllIds();
	init();
});

function init() {
	$("#facets-desc").html(getFacetsDesc());
	$("#facets-header").html(getFacetsHeader());
	selectPageSize(window.myPageSize);
	prepareFirstTablePage();
	updateHeaderMessage(getDefaultHeaderMessage());
}

function getTotalRows() {
	return window.ids.length;
}

function getShownTable() {

	if (window.startRow < 1) {
		window.startRow = 1;
	}

	isLast = false;
	endRow = window.startRow + window.myPageSize -1;
	if (endRow > ids.length) {
		endRow = ids.length;
		isLast = true;
	}	
	
	shownIds = getIdsWithRowRange(window.ids, window.startRow, endRow);
	
	tuples = getTuplesForIds(shownIds);
	
	result = {}
	
	result["facets-right-content"] = createTableForTuples(tuples);
	result["first"] = window.startRow;
	result["isLast"] = isLast;
	result["last"] = endRow;
		
	return result;
}

function getIdsWithRowRange(inIds, start, end) {
		var returnIds = [];
		for (var i = start - 1; i < end ; i++) {
			returnIds.push(inIds[i]);
		}
		return returnIds;
}

/****** Facets ******/
// TODO: Change the name "Container" to something 
// that mentions facets in it.
// there may be other elements that use the name
// container in the page.
// Same thing for Header.

function handleFacetClose() {
	
	if (!$("[class$='Container']").length) {
 		$("#facets-desc").show();
 		// If there are no filters in place, the original table should be shown.
 		refresh();
 	}
}

function handleFacetOpen() {
	$("#facets-desc").hide();
}

function closeAllFacets() {
	$("[class$='Container']").remove();
	$("#facets-desc").show();
}

$(document).on("click", "[class$='Header'] > .close", function(event) {
 
 	$(this).closest("[class$='Container']").remove();
 	handleFacetClose();
 
});

function refresh() {
	getAllIds();
	prepareFirstTablePage();
	updateHeaderMessage(getDefaultHeaderMessage());
}

function updateHeaderMessage(message) {
	
	$("#header-message").html(message);
}

/****** PAGINATION ******/
$(document).on("click", "#viewpanel-pagesize > a", function(event){
	window.myPageSize = parseInt($(this).text());
	selectPageSize(window.myPageSize);
	prepareChangedPageSizeTable(getShownTable());
});

$(document).on("click", "#getFirst", function(event){
	prepareFirstTablePage();		    
});

$(document).on("click", "#getPrev", function(event){
	preparePrevTablePage();	
});

$(document).on("click", "#getNext", function(event){
	prepareNextTablePage();	
});

$(document).on("click", "#getLast", function(event){
	prepareLastTablePage();	
});

function selectPageSize(pageSize) {
	$("#viewpanel-pagesize > a").removeClass('selected');
	$("#viewpanel-pagesize > a").each(function() {
			thisPageSize = parseInt($(this).text());
  			if (thisPageSize == pageSize) {
  					$(this).addClass('selected');
  			}
	});
}

function prepareChangedPageSizeTable(data) {
	updateRightContent(data);
	if (data["isLast"]) {
		$('#getNext').addClass('inaction');
		$('#getLast').addClass('inaction');
	}
	$('#last').html(data["last"]);
}

function prepareFirstTablePage() {
	window.startRow = 1;
	data = getShownTable();
	updateRightContent(data);
	$('#first').html(data["first"]);
	$('#last').html(data["last"]);
	$('#getPrev').addClass('inaction');
	$('#getFirst').addClass('inaction');
	$('#getNext').removeClass('inaction');
	$('#getLast').removeClass('inaction');
}

function prepareLastTablePage() {
	window.startRow = getTotalRows() - window.myPageSize + 1;
	data = getShownTable();
	updateRightContent(data);
	$('#first').html(data["first"]);
	$('#last').html(data["last"]);
	$('#getNext').addClass('inaction');
	$('#getLast').addClass('inaction');
	$('#getPrev').removeClass('inaction');
	$('#getFirst').removeClass('inaction');
}

function prepareNextTablePage() {
	window.startRow = window.startRow + window.myPageSize;
	data = getShownTable();
	updateRightContent(data);
	$('#first').html(data["first"]);
	$('#last').html(data["last"]);
	$('#getPrev').removeClass('inaction');
	$('#getFirst').removeClass('inaction');
	if (data["isLast"] == true) {
		$('#getNext').addClass('inaction');
		$('#getLast').addClass('inaction');
	}
}

function preparePrevTablePage() {
	window.startRow = window.startRow - window.myPageSize; 
	data = getShownTable();
	updateRightContent(data);
	$('#first').html(data["first"]);
	$('#last').html(data["last"]);
	$('#getNext').removeClass('inaction');
	$('#getLast').removeClass('inaction');
	if (data["first"] == 1) {
		$('#getPrev').addClass('inaction');
		$('#getFirst').addClass('inaction');
	}
}


function updateRightContent(data) {
	$('#facets-right-content').html(data["facets-right-content"]);
	afterAjax();
}