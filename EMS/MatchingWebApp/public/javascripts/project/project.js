/* TODO Sanjib: Fatemah comments:
- Project should be clear from the context. We do not need to save the project inside attributes.
- Instead of triggering a change on load, create a function that will take care of that change and use
it both in load and when change happens. Please see setRecommendedFunctions.
*/

$(document).ready(function(){

	$(window).load(function() {
	
		$('#table1_name').trigger('change');
		$('#table2_name').trigger('change');
		$('#table1_name_m').trigger('change');
		$('#table2_name_m').trigger('change');
		$('#table1_name_d').trigger('change');
		$('#table2_name_d').trigger('change');
		$('#table1_name_f').trigger('change');
		$('#table2_name_f').trigger('change');
		$('#matches_name_e').trigger('change');
		$('#gold_name_e').trigger('change');
		$('#rule_name_e').trigger('change');
		$('#matcher_name_ed').trigger('change');
		$('#feature_name_ed').trigger('change');
		
		 setRecommendedFunctions();
//		 addTableRowHtml();
//		 addTableHeaderHtml();
	}); 	
    
	$('#new_project_btn').click(function() {
		$('#new-project-modal').modal('show');
	});

	$('#projects_table').tablesorter();
	$('#browse_functions_table').tablesorter();
	
	/**** Blocking ****/
	$('#table1_name').on('change', function()  {
		var table1Name = $('#table1_name').val();
		var projectName = $(this).attr("project_name");
		projectJsRoutes.controllers.project.ProjectController.getAttributes(projectName, table1Name).ajax({
			async: false,
    		success : function(data) {
    			var attribs = data["attributes"];
    			$("#attr1_name").html("");
    			$("#attr1_names").html("");
    			$.each(attribs, function(index, attrib) {
    				$("#attr1_name").append("<option value=\"" + attrib + "\">" + attrib +"</option>");
    				if(index != 0) {
    					$("#attr1_names").append("<option value=\"" + attrib + "\">" + attrib +"</option>");
    				}
    			});
		    },
	        error: function(xhr, status, error) {
  				$('body').html(xhr.responseText);
			}
		});
	});

	/**** Blocking ****/
	$('#table2_name').on('change', function()  {
		var table2Name = $('#table2_name').val();
		var projectName = $(this).attr("project_name");
		projectJsRoutes.controllers.project.ProjectController.getAttributes(projectName, table2Name).ajax({
			async: false,
    		success : function(data) {
    			var attribs = data["attributes"];
    			$("#attr2_name").html("");
    			$("#attr2_names").html("");
    			$.each(attribs, function(index, attrib) {
    				$("#attr2_name").append("<option value=\"" + attrib + "\">" + attrib +"</option>");
    				if(index != 0) {
    					$("#attr2_names").append("<option value=\"" + attrib + "\">" + attrib +"</option>");
    				}
    			});
		    },
	        error: function(xhr, status, error) {
  				$('body').html(xhr.responseText);
			}
		});
	});

	/**** Matching normal mode ****/
	$('#table1_name_m').on('change', function()  {
		var table1Name = $('#table1_name_m').val();
		var projectName = $(this).attr("project_name");
		projectJsRoutes.controllers.project.ProjectController.getAttributes(projectName, table1Name).ajax({
			async: false,
    		success : function(data) {
    			var attribs = data["attributes"];
    			$("#attr1_names_m").html("");
    			$.each(attribs, function(index, attrib) {
    				if(index != 0) {
    					$("#attr1_names_m").append("<option value=\"" + attrib + "\">" + attrib +"</option>");
    				}
    			});
		    },
	        error: function(xhr, status, error) {
  				$('body').html(xhr.responseText);
			}
		});
	});

	/**** Matching normal mode ****/
	$('#table2_name_m').on('change', function()  {
		var table2Name = $('#table2_name_m').val();
		var projectName = $(this).attr("project_name");
		projectJsRoutes.controllers.project.ProjectController.getAttributes(projectName, table2Name).ajax({
			async: false,
    		success : function(data) {
    			var attribs = data["attributes"];
    			$("#attr2_names_m").html("");
    			$.each(attribs, function(index, attrib) {
    				if(index != 0) {
    					$("#attr2_names_m").append("<option value=\"" + attrib + "\">" + attrib +"</option>");
    				}
    			});
		    },
	        error: function(xhr, status, error) {
  				$('body').html(xhr.responseText);
			}
		});
	});
	
	/**** Matching debug mode ****/
	$('#table1_name_d').on('change', function()  {
		var table1Name = $('#table1_name_d').val();
		var projectName = $(this).attr("project_name");
		projectJsRoutes.controllers.project.ProjectController.getAttributes(projectName, table1Name).ajax({
			async: false,
    		success : function(data) {
    			var attribs = data["attributes"];
    			$("#attr1_names_d").html("");
    			$.each(attribs, function(index, attrib) {
    				if(index != 0) {
    					$("#attr1_names_d").append("<option value=\"" + attrib + "\">" + attrib +"</option>");
    				}
    			});
		    },
	        error: function(xhr, status, error) {
  				$('body').html(xhr.responseText);
			}
		});
	});

	/**** Matching debug mode ****/
	$('#table2_name_d').on('change', function()  {
		var table2Name = $('#table2_name_d').val();
		var projectName = $(this).attr("project_name");
		projectJsRoutes.controllers.project.ProjectController.getAttributes(projectName, table2Name).ajax({
			async: false,
    		success : function(data) {
    			var attribs = data["attributes"];
    			$("#attr2_names_d").html("");
    			$.each(attribs, function(index, attrib) {
    				if(index != 0) {
    					$("#attr2_names_d").append("<option value=\"" + attrib + "\">" + attrib +"</option>");
    				}
    			});
		    },
	        error: function(xhr, status, error) {
  				$('body').html(xhr.responseText);
			}
		});
	});
	
	/**** Add feature ****/
	$('#table1_name_f').on('change', function()  {
		var table1Name = $('#table1_name_f').val();
		var projectName = $(this).attr("project_name");
		projectJsRoutes.controllers.project.ProjectController.getAttributes(projectName, table1Name).ajax({
			async: false,
    		success : function(data) {
    			var attribs = data["attributes"];
    			$("#attr1_name_f").html("");
    			$.each(attribs, function(index, attrib) {
    				$("#attr1_name_f").append("<option value=\"" + attrib + "\">" + attrib +"</option>");
    			});
		    },
	        error: function(xhr, status, error) {
  				$('body').html(xhr.responseText);
			}
		});
	});

	/**** Add feature ****/
	$('#table2_name_f').on('change', function()  {
		var table2Name = $('#table2_name_f').val();
		var projectName = $(this).attr("project_name");
		projectJsRoutes.controllers.project.ProjectController.getAttributes(projectName, table2Name).ajax({
			async: false,
    		success : function(data) {
    			var attribs = data["attributes"];
    			$("#attr2_name_f").html("");
    			$.each(attribs, function(index, attrib) {
    				$("#attr2_name_f").append("<option value=\"" + attrib + "\">" + attrib +"</option>");
    			});
		    },
	        error: function(xhr, status, error) {
  				$('body').html(xhr.responseText);
			}
		});
	});

	var iCnt = 1;
	$('#btAdd').click(function() {
		var projectName = $(this).attr("project_name");
		var features;
		projectJsRoutes.controllers.project.ProjectController.getFeatureNames(projectName).ajax({
			async: false,
    		success : function(data) {
    			features = data["features"];
		    },
	        error: function(xhr, status, error) {
  				$('body').html(xhr.responseText);
			}
		});
		var relops;
		projectJsRoutes.controllers.project.ProjectController.getRelationalOperatorNames().ajax({
			async: false,
    		success : function(data) {
    			relops = data["relops"];
		    },
	        error: function(xhr, status, error) {
  				$('body').html(xhr.responseText);
			}
		});
        if(iCnt == 0) {
			// Show the table header
        	$('#row0').show();
		}
        
		iCnt = iCnt + 1;
		// Add a row in the table
		var row_html = '<tr id="row' + iCnt + '"><td>' + 
			'<select id="feature' + iCnt + '" name="feature' + iCnt +
			'" style="width:190px">';
		$.each(features, function(index, feature) {
			row_html = row_html + '<option value="' + feature + '">' + feature +
						'</option>';
		});
		row_html = row_html + '</select></td><td>' +
			'<select id="op' + iCnt + '" name="op' + iCnt + '" style="width:210px">';
		$.each(relops, function(index, relop) {
			row_html = row_html + '<option value="' + relop + '">' + relop +
						'</option>';
		});
		row_html = row_html + '</select></td><td>' +
			'<input type="text" id="val' + iCnt + '" name="val' + iCnt +
			'" style="width:50px" required>' +
			'</td></tr>';
			
		$('#rule_table tr:last').after(row_html);
		
		if(iCnt == 1) {
			$('#btRemove').removeAttr('disabled'); 
			$('#btRemove').attr('class', 'bt');
			$('#btRemoveAll').removeAttr('disabled'); 
			$('#btRemoveAll').attr('class', 'bt');
		}
		
    });
	
	$('#btRemove').click(function() {
		$('#row' + iCnt).remove();
        iCnt = iCnt - 1;
        if (iCnt == 0) {
        	// Hide the table header
        	$('#row0').hide();
        	$('#btRemove').attr('class', 'bt-disable'); 
            $('#btRemove').attr('disabled', 'disabled');
            $('#btRemoveAll').attr('class', 'bt-disable'); 
            $('#btRemoveAll').attr('disabled', 'disabled');
        }
    });
	
    $('#btRemoveAll').click(function() {
        while(iCnt > 0) {
        	$('#btRemove').trigger('click');
        }
    });
	
	$('#matches_name_e').on('change', function()  {
		var matchesName = $('#matches_name_e').val();
		var projectName = $(this).attr("project_name");
		var defaultMatchesId1 = $(this).attr("default_matches_id1");
		var defaultMatchesId2 = $(this).attr("default_matches_id2");
		var defaultMatchesLabel = $(this).attr("default_matches_label");
		projectJsRoutes.controllers.project.ProjectController.getAttributes(projectName, matchesName).ajax({
    		success : function(data) {
    			var attribs = data["attributes"];
    			$("#matches_id1").html("");
    			$("#matches_id2").html("");
    			$("#matches_label_id").html("");
    			$.each(attribs, function(index, attrib) {
    				if(defaultMatchesId1 == attrib) {
    					$("#matches_id1").append("<option value=\"" + attrib + "\" selected>" + attrib +"</option>");
    				}
    				else {
    					$("#matches_id1").append("<option value=\"" + attrib + "\">" + attrib +"</option>");
    				}
    				if(defaultMatchesId2 == attrib) {
    					$("#matches_id2").append("<option value=\"" + attrib + "\" selected>" + attrib +"</option>");
    				}
    				else {
    					$("#matches_id2").append("<option value=\"" + attrib + "\">" + attrib +"</option>");
    				}
    				if(defaultMatchesLabel == attrib) {
    					$("#matches_label_id").append("<option value=\"" + attrib + "\" selected>" + attrib +"</option>");
    				}
    				else {
    					$("#matches_label_id").append("<option value=\"" + attrib + "\">" + attrib +"</option>");
    				}
    			});
		    },
	        error: function(xhr, status, error) {
  				$('body').html(xhr.responseText);
			}
		});
	});
	
	$('#gold_name_e').on('change', function()  {
		var goldName = $('#gold_name_e').val();
		var projectName = $(this).attr("project_name");
		var defaultGoldId1 = $(this).attr("default_gold_id1");
		var defaultGoldId2 = $(this).attr("default_gold_id2");
		var defaultGoldLabel = $(this).attr("default_gold_label");
		projectJsRoutes.controllers.project.ProjectController.getAttributes(projectName, goldName).ajax({
    		success : function(data) {
    			var attribs = data["attributes"];
    			$("#gold_id1").html("");
    			$("#gold_id2").html("");
    			$("#gold_label_id").html("");
    			$.each(attribs, function(index, attrib) {
    				if(defaultGoldId1 == attrib) {
    					$("#gold_id1").append("<option value=\"" + attrib + "\" selected>" + attrib +"</option>");
    				}
    				else {
    					$("#gold_id1").append("<option value=\"" + attrib + "\">" + attrib +"</option>");
    				}
    				if(defaultGoldId2 == attrib) {
    					$("#gold_id2").append("<option value=\"" + attrib + "\" selected>" + attrib +"</option>");
    				}
    				else {
    					$("#gold_id2").append("<option value=\"" + attrib + "\">" + attrib +"</option>");
    				}
    				if(defaultGoldLabel == attrib) {
    					$("#gold_label_id").append("<option value=\"" + attrib + "\" selected>" + attrib +"</option>");
    				}
    				else {
    					$("#gold_label_id").append("<option value=\"" + attrib + "\">" + attrib +"</option>");
    				}
    			});
		    },
	        error: function(xhr, status, error) {
  				$('body').html(xhr.responseText);
			}
		});
	});
	
	$('#rule_name_e').on('change', function()  {
		var ruleName = $('#rule_name_e').val();
		var projectName = $(this).attr("project_name");
		projectJsRoutes.controllers.project.RuleController.getRuleString(projectName, ruleName).ajax({
    		success : function(data) {
    			var ruleString = data["ruleString"];
    			$('#rule_string').val(ruleString);
		    },
	        error: function(xhr, status, error) {
  				$('body').html(xhr.responseText);
			}
		});
	});
	
	$('#matcher_name_ed').on('change', function()  {
		var matcherName = $('#matcher_name_ed').val();
		var projectName = $(this).attr("project_name");
		projectJsRoutes.controllers.project.RuleController.getMatcherString(projectName, matcherName).ajax({
    		success : function(data) {
    			var matcherString = data["matcherString"];
    			$('#matcher_string').val(matcherString);
		    },
	        error: function(xhr, status, error) {
  				$('body').html(xhr.responseText);
			}
		});
	});
	
	$('#feature_name_ed').on('change', function()  {
		var featureName = $('#feature_name_ed').val();
		var projectName = $(this).attr("project_name");
		projectJsRoutes.controllers.project.RuleController.getFeatureString(projectName, featureName).ajax({
    		success : function(data) {
    			var featureString = data["featureString"];
    			$('#feature_string').val(featureString);
		    },
	        error: function(xhr, status, error) {
  				$('body').html(xhr.responseText);
			}
		});
	});
	
	
	/**** Function Popovers ****/
	$('.functionName-tooltip').popover({ 
	    html : true, 
	    placement: 'right',
	    content: function() {
	      return $(this).siblings('.functionName-popover').html();
	    }
	});
	
	/**** Function recommendation ****/
	$(document).on("change", "#table1_name_f, #table2_name_f, #attr1_name_f, #attr2_name_f", function(event) {
 		setRecommendedFunctions();
	});
	
});

function setRecommendedFunctions() {
	table1_name_f = $("#table1_name_f").val();
	table2_name_f = $("#table2_name_f").val();
	attr1_name_f = $("#attr1_name_f").val();
	attr2_name_f = $("#attr2_name_f").val();
	projectName = $("select[project_name]").attr("project_name");

	JsonMsg = {
	     "table1_name_f": table1_name_f,
	     "table2_name_f": table2_name_f,
	     "attr1_name_f": attr1_name_f,
	     "attr2_name_f": attr2_name_f
	};
	
	JsonMsg = JSON.stringify(JsonMsg);
	
	projectJsRoutes.controllers.project.RuleController.getRecommendedFunctions(projectName).ajax({
	
		contentType : 'application/json; charset=utf-8',	        
        data: JsonMsg,
        async: false,
		success : function(data) { 
			functionNames = eval(data["functionNames"]);   
		},
        error: function(xhr, status, error) {
			$('body').html(xhr.responseText);
		}
	});	
	
	$("#add_feature_modal").find("#function_name").html("");
	$.each(functionNames, function(index, f) {
		$("#add_feature_modal").find("#function_name").append("<option value=\"" + f + "\">" + f +"</option>");
	});
	$("#function_count").html(functionNames.length);
}

/*
function addTableRowHtml() {
	$('#rule_table tr:last').after('<tr id="row' + iCnt + '"><td>' + 
										'<select id="feature' + iCnt +
										'" name="feature' + iCnt +
										'" style="width:190px">' +
										'</select></td><td>' +
										'<select id="op' + iCnt +
										'" name="op' + iCnt +
										'" style="width:210px">' +
										'</select></td><td>' +
										'<input type="text" id="val' + iCnt +
										'" name="val' + iCnt +
										'" style="width:50px" required>' +
									'</td></tr>');
}

function addTableHeaderHtml() {
	$('#rule_table tr:last').after('<tr id="row' + iCnt + '">'
										'<th>Feature</th>' +
										'<th>Operator</th>' +
										'<th>Value</th>' +
									'</tr>');
}
*/