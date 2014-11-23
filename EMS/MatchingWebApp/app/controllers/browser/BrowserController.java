package controllers.browser;

import static play.data.Form.form;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import play.Logger;
import play.Routes;
import play.data.DynamicForm;
import play.libs.Json;
import play.mvc.Call;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.Html;
import views.html.browser.browser_topbar;
import views.html.common.common_main;
import views.html.common.common_topnav;
import views.html.facets_layout.facets_layout;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.walmart.productgenome.matching.daos.ProjectDao;
import com.walmart.productgenome.matching.daos.TableDao;
import com.walmart.productgenome.matching.models.DefaultType;
import com.walmart.productgenome.matching.models.data.Attribute;
import com.walmart.productgenome.matching.models.data.Project;
import com.walmart.productgenome.matching.models.data.Table;
import com.walmart.productgenome.matching.models.data.Table.QueryOps;
import com.walmart.productgenome.matching.models.data.Tuple;
import com.walmart.productgenome.matching.service.explorer.AttrComb;
import com.walmart.productgenome.matching.service.explorer.AttrStats;
import com.walmart.productgenome.matching.service.explorer.ExplorerDriver;
import com.walmart.productgenome.matching.service.explorer.TableStats;

public class BrowserController extends Controller {

	private static Table table;
	private static Project project;
	private static TableStats tableStats;
	
	/*** RENDER OPERATIONS ***/
	public static Result index(Boolean copy) {

    // If the request was for a copy of the table.
    // make a copy of the table and make sure 
    // the controller is working on the copy.
    // TODO: make sure this is the right way to handle openCopy.
    if (copy) {

      Table copyTable = new Table(table);

      String copyTableName = "Copy of " + table.getName();
      int count = 1;
      while (TableDao.listTableNames(project.getName()).contains(copyTableName)) {
        copyTableName = "Copy of " + table.getName() + " " + String.valueOf(count);
        count ++;
      }

      copyTable.setName(copyTableName);

      // TODO: Can I just reassign? Do I need to explicitly delete?
      table = copyTable;      
    }

    tableStats = ExplorerDriver.getTableStats(table, 100, 100);

    return showBrowser();
  }
	 
	public static Result postIndex() {

		DynamicForm form = form().bindFromRequest();
		String projectName = form.get("project_name");
		String tableName = form.get("table_name");
		Boolean copy = Boolean.valueOf(form.get("copy"));

		readTableProject(projectName, tableName);

		return index(copy);
	}

	public static Result getIndex(String inProjectName, String inTableName, String inCopy) {

		String projectName = inProjectName;
		String tableName = inTableName;
		Boolean copy = Boolean.valueOf(inCopy);

		readTableProject(projectName, tableName);

		return index(copy);
	}

	public static Result getIndexNoCopy(String inProjectName, String inTableName) {

		String projectName = inProjectName;
		String tableName = inTableName;
		Boolean copy = false;

		readTableProject(projectName, tableName);

		return index(copy);
	}
	
	public static Result indexRefresh() {
		return index(false);
	}
	
	/**** BASIC DATA HANDLING FOR FRONT END ****/
	public static Result getAllIds() {
    List<Object> ids = table.getAllIdsInOrder();
    return ok(Json.toJson(ids));
  }
  
  public static Result getTuplesForIds() {
    
    List<Object> ids = new ArrayList<Object>();
    List<Tuple> tuples = new ArrayList<Tuple>();
    Attribute idAttr = table.getIdAttribute();    
        
    JsonNode json = request().body().asJson();
    JsonNode postIds = json.findPath("ids");
    
    for (int i = 0; i < postIds.size(); i++) {
      ids.add(idAttr.convertValueToObject(postIds.get(i).asText()));
    }
        
    for (Object id: ids) {
      Tuple tuple = table.getTuple(id);
      tuples.add(tuple);      
    }   
    
    ObjectNode tuplesJson = Json.newObject();
    
    // put the header.
    List<String> header = new ArrayList<String>();
    
    for (Attribute attr: table.getAttributes()) {
      header.add(attr.getName());
    }
    
    tuplesJson.put("header", Json.toJson(header));
    Map<String, List<String>> rows = new LinkedHashMap<String, List<String>>();
    for (Tuple tuple: tuples) {
      List<String> row = new ArrayList<String>();
      for (Attribute attr: table.getAttributes()) {
          row.add(String.valueOf(tuple.getAttributeValue(attr)));
      }
      rows.put(String.valueOf(tuple.getAttributeValue(table.getIdAttribute())), row);
    }

    tuplesJson.put("rows", Json.toJson(rows));
    
    return ok(tuplesJson);
  }
  
	/**** SAMPLING ****/
	public static Result sample() {
		// TODO: Check if table name exists and error if so.
		// otherwise, we will be overwriting when saving.

		DynamicForm form = form().bindFromRequest();
		Logger.info("PARAMETERS : " + form.data().toString());

		String tableName = form.get("sample_table_name");
		String sampleSize = form.get("sample_size");

		// TODO: raise error if not valid.
		long size = Long.parseLong(sampleSize);
		List<Object> ids = table.getRandomSample(size);

		Table sampleTable = new Table(table, tableName, ids);

		try {
			TableDao.save(sampleTable, new HashSet<DefaultType>(), true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return getIndex(project.getName(), tableName, "false");
	}
	
	/**** SAVE AND OPEN COPY ****/
	public static Result openCopy() {
    return getIndex(project.getName(), table.getName(), "true");
  }
	
	public static Result saveTable() {

    ObjectNode result = Json.newObject();

    try {
      TableDao.save(table, new HashSet<DefaultType>(), true);
    } catch (IOException e) {
      result.put("status", "failure");
    }

    result.put("status", "success");
    result.put("status-message", "Table successfully saved.");

    return ok(result);
  }
	
	/**** CELL EDIT OPERATIONS ****/
  public static Result saveCellEdit() {
    // TODO: Need error handling here. What if the value could not be converted to the object type
    // TODO: Could Id be changed? No

    JsonNode json = request().body().asJson();
    String attrName = json.findPath("attrName").asText();
    String value = json.findPath("value").asText();
    String itemid = json.findPath("itemid").asText();

    Attribute attr = table.getAttributeByName(attrName);
    Object idObj = attr.convertValueToObject(itemid);
    Object valueObj = attr.convertValueToObject(value);
    table.getTuple(idObj).setAttributeValue(attr, valueObj);

    ObjectNode result = Json.newObject();
    result.put("status", "success");

    return ok(result);
  }
		
	/**** FACETS OPERATIONS ****/
  public static Result getExplorerData(String attrName) {

    Attribute attr = table.getAttributeByName(attrName);    
    AttrComb attrComb = new AttrComb(attr);
    AttrStats attrStats = tableStats.getAttrStats(attrComb);
    
    ObjectNode expJson = Json.newObject();
    // TODO Ask: is this bad to do some formatting in the controller?
    DecimalFormat df = new DecimalFormat("##.##");

    expJson.put("percent-unique", df.format(attrStats.getPercentUnique()));
    expJson.put("percent-missing", df.format(attrStats.getPercentMissing()));
    
    List<List<String>> rows = new ArrayList<List<String>>();

    for (Map.Entry<Object, Integer> entry: attrStats.getTopKFrequencies().entrySet()) {
      List<String> row = new ArrayList<String>();
      
      row.add(String.valueOf(entry.getKey()));
      row.add(String.valueOf(entry.getValue()));
   
      rows.add(row);
    }
    
    expJson.put("rows", Json.toJson(rows));
   
    return ok(expJson);
 }
	
	public static Result getAllExplorerData() {

	  // TODO: Do we want to still support attrComb?
    List<AttrComb> allAttrs = new ArrayList<AttrComb>();
    for (Attribute attr: table.getAttributes()) {
      if (!attr.equals(table.getIdAttribute())) {
        allAttrs.add(new AttrComb(attr));
      }
    }

    Collections.sort(allAttrs, new AttrCombComparator());

    ObjectNode expJson = Json.newObject();
    expJson.put("table-name", table.getName());
    expJson.put("table-size", table.getSize());
    expJson.put("attr-count", allAttrs.size());
    
    List<List<String>> rows = new ArrayList<List<String>>();
    DecimalFormat df = new DecimalFormat("##.#");
    
    int count = 1;
    for (AttrComb attrComb: allAttrs) {
      List<String> row = new ArrayList<String>();
      
      row.add(String.valueOf(count));
      row.add(attrComb.getAttrCombName());
      row.add(df.format(tableStats.getAttrStats(attrComb).getPercentUnique()));
      row.add(df.format(tableStats.getAttrStats(attrComb).getPercentMissing()));
   
      rows.add(row);
      count += 1;
    }
    
    expJson.put("rows", Json.toJson(rows));
   
    return ok(expJson);
  }
	
	public static Result getBrowserQueryResults() {

    JsonNode json = request().body().asJson();
    
    JsonNode attrNames = json.findPath("attrNames");
    JsonNode vals = json.findPath("vals");
    JsonNode jsonops = json.findPath("ops");

    List<Attribute> attrs = new ArrayList<Attribute>();
    List<Object> values = new ArrayList<Object>();
    List<QueryOps> ops = new ArrayList<QueryOps>();

    for (int i = 0; i < attrNames.size(); i++) {
      String val = vals.get(i).asText();
      if (!val.isEmpty()) {
        Attribute attr = table.getAttributeByName(attrNames.get(i).asText());
        attrs.add(attr);
        // TODO: raise error if value is not convertable.
        Object value = attr.convertValueToObject(val);
        values.add(value);
        // TODO: raise error if operation is not convertable.
        QueryOps op = QueryOps.valueOf(jsonops.get(i).asText());
        ops.add(op);
      }
    }

    List<Object> ids = table.getAllRowsWithQueryCondition(attrs, values, ops);
    return ok(Json.toJson(ids));
  }
	
	/**** ADD REMOVE OPERATIONS ****/
  public static Result removeColumn(String attrName) {
    Attribute attr = table.getAttributeByName(attrName);
    table.removeAttr(attr);
    
    ObjectNode result = Json.newObject();
    result.put("status", "success");
    
    return ok(result);
  }
  
  public static Result removeRows() {
    
    JsonNode json = request().body().asJson();
    JsonNode postIds = json.findPath("ids");
    Attribute idAttr = table.getIdAttribute();
        
    for (int i = 0; i < postIds.size(); i++) {
      table.removeTuple(idAttr.convertValueToObject(postIds.get(i).asText()));
    }

    ObjectNode result = Json.newObject();
    result.put("status", "success");
    
    return ok(result);
  }
  
  /**** SORTING ****/
  public static Result getSortedIds(String attrName) {
    Attribute attr = table.getAttributeByName(attrName);
    List<Object> ids = table.sortTableByAttribute(table.getAllIdsInOrder(), attr, false);
    return ok(Json.toJson(ids));
  }
  
  /**** JAVASCRIPT ROUTING ****/
  public static Result javascriptRoutes() {
    response().setContentType("text/javascript");
    return ok(
        Routes.javascriptRouter("browserJsRoutes",
            controllers.browser.routes.javascript.BrowserController.getTuplesForIds(),
            controllers.browser.routes.javascript.BrowserController.getAllIds(),
            controllers.browser.routes.javascript.BrowserController.sample(),
            controllers.browser.routes.javascript.BrowserController.openCopy(),
            controllers.browser.routes.javascript.BrowserController.saveTable(),
            controllers.browser.routes.javascript.BrowserController.saveCellEdit(),
            controllers.browser.routes.javascript.BrowserController.getAllExplorerData(),
            controllers.browser.routes.javascript.BrowserController.getExplorerData(),
            controllers.browser.routes.javascript.BrowserController.getBrowserQueryResults(),
            controllers.browser.routes.javascript.BrowserController.removeColumn(),
            controllers.browser.routes.javascript.BrowserController.removeRows(),
            controllers.browser.routes.javascript.BrowserController.getSortedIds()
            )
    );
  }
  
	/**** PRIVATE OPERATIONS ****/
	private static Result showBrowser() {
	  
	  String pageTitle = "Browser";
    Html topbar = browser_topbar.render(project.getName(), table.getName(), 
                                        table.getSize(), isEligibaleForCandsetView());
    Html topnav = common_topnav.render(project);

		Html content = facets_layout.render();
		
		List<Call> dynamicJs = new ArrayList<Call>();
    dynamicJs.add(controllers.project.routes.ProjectController.javascriptRoutes());
    dynamicJs.add(controllers.routes.Assets.at("javascripts/project/project.js"));

		dynamicJs.add(controllers.browser.routes.BrowserController.javascriptRoutes());
    dynamicJs.add(controllers.routes.Assets.at("javascripts/facets_layout/facets_layout.js"));
    dynamicJs.add(controllers.routes.Assets.at("javascripts/browser/browser_facets_layout.js"));
		dynamicJs.add(controllers.routes.Assets.at("javascripts/browser/browser_add_remove.js"));
    dynamicJs.add(controllers.routes.Assets.at("javascripts/browser/browser_cell_edit.js"));
    dynamicJs.add(controllers.routes.Assets.at("javascripts/browser/browser_filter_query.js"));
    dynamicJs.add(controllers.routes.Assets.at("javascripts/browser/browser_explorer.js"));
    dynamicJs.add(controllers.routes.Assets.at("javascripts/browser/browser_save.js"));
    dynamicJs.add(controllers.routes.Assets.at("javascripts/browser/browser_sort.js"));
    dynamicJs.add(controllers.routes.Assets.at("javascripts/browser/browser_table.js"));
    dynamicJs.add(controllers.routes.Assets.at("javascripts/browser/browser.js"));
    
    List<Call> dynamicCss = new ArrayList<Call>();
    dynamicCss.add(controllers.routes.Assets.at("stylesheets/facets_layout/facets_layout.css"));
    dynamicCss.add(controllers.routes.Assets.at("stylesheets/browser/browser.css"));
		
		Html page =  common_main.render(
		    pageTitle, topbar, topnav, content, dynamicCss, dynamicJs);

		return ok(page);
	}

	/** 
	 * Rank the attributes based on heuristics: 
	 * 1) More percent unique is better
	 * 2) Less precent missing is better
	 * @author fpanahi
	 */
	private static class AttrCombComparator implements Comparator<AttrComb> {
		public int compare(AttrComb a, AttrComb b) {

			double uniqueDiff = tableStats.getAttrStats(a).getPercentUnique() - 
					tableStats.getAttrStats(b).getPercentUnique();
			if (uniqueDiff > 0) {
				return -1;
			} else if (uniqueDiff < 0) {
				return 1;
			} else {
				double missingDiff = tableStats.getAttrStats(a).getPercentMissing() - 
						tableStats.getAttrStats(b).getPercentMissing();
				if (missingDiff > 0) {
					return 1;
				} else if (missingDiff < 0) {
					return -1;
				}
			}

			return 0;
		}
	}
	
	/**
	 * Retrieve the table object.
	 * @param projectName
	 * @param tableName
	 */
	private static void readTableProject(String projectName, String tableName) {
	  project = ProjectDao.open(projectName);
    table = null;
    try {
      table = TableDao.open(projectName, tableName);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
	}
	
	
	private static Boolean isEligibaleForCandsetView() {
	  
	  String table1Name = null;
	  String table2Name = null;
	   
   // Assumption: First and second rows that have .id in their attribute name.
   // correspond to first and second table.
   String pattern = "(.*)(.id)(.*)";
   Pattern r = Pattern.compile(pattern);
   
   for (Attribute attr: table.getAttributes()) {
     String name = attr.getName();
     java.util.regex.Matcher m = r.matcher(name);
     boolean found = m.find();
     
     if (found && table1Name == null) {
       table1Name = m.group(1);
       continue;
     }
     if (found && table2Name == null) {
       table2Name = m.group(1);
     }
   }
	  
	  if (table1Name != null && table2Name != null) {
	    return true;
	  }
	  
	  return false;
	}

	/*private static Table readTable() throws IOException {

		String csvFilePath =  "./data/books/bowker.csv";
		Table table = TableDao.importFromCSVWithHeader("Products", "bowker", csvFilePath);

		return table;
	}*/
}
