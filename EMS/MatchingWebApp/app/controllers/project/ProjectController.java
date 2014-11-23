package controllers.project;

import static play.data.Form.form;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import play.Logger;
import play.Routes;
import play.data.DynamicForm;
import play.libs.Json;
import play.mvc.Call;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.twirl.api.Html;
import views.html.common.common_main;
import views.html.common.common_topnav;
import views.html.project.project_dashboard;
import views.html.project.project_dashboard_topbar;
import views.html.project.project_main;
import views.html.project.project_main_topbar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.walmart.productgenome.matching.daos.BlockingDao;
import com.walmart.productgenome.matching.daos.ProjectDao;
import com.walmart.productgenome.matching.daos.RuleDao;
import com.walmart.productgenome.matching.daos.TableDao;
import com.walmart.productgenome.matching.models.Constants;
import com.walmart.productgenome.matching.models.DefaultType;
import com.walmart.productgenome.matching.models.RelationalOperator;
import com.walmart.productgenome.matching.models.data.Attribute;
import com.walmart.productgenome.matching.models.data.Project;
import com.walmart.productgenome.matching.models.data.ProjectDefaults;
import com.walmart.productgenome.matching.models.data.Table;
import com.walmart.productgenome.matching.models.rules.functions.Function;

public class ProjectController extends Controller {

	public static String statusMessage = "";

	public static Result index() {

		String pageTitle = "pgMatching";

		Html topBar = project_dashboard_topbar.render();

		List<Project> projects = ProjectDao.list();
		Html content = project_dashboard.render(projects);

		List<Call> dynamicJs = new ArrayList<Call>();
		dynamicJs.add(controllers.routes.Assets.at("javascripts/project/project.js"));

		List<Call> dynamicCss = new ArrayList<Call>();
		dynamicCss.add(controllers.routes.Assets.at("stylesheets/project/project.css"));

		Html page =  common_main.render(
				pageTitle, topBar, new Html(""), content, dynamicCss, dynamicJs);

		return ok(page);
	}

	/*
	 * Show a project
	 */
	public static Result showProject(String projectName) {
		final Project project = ProjectDao.open(projectName);
		if (project == null) {
			return notFound(String.format("Project %s does not exist.", projectName));
		}

		String pageTitle = "pgMatching";
		Html topBar = project_main_topbar.render(projectName);
		Html topNav = common_topnav.render(project);
		Html content = project_main.render(project, statusMessage);

		List<Call> dynamicJs = new ArrayList<Call>();
		dynamicJs.add(controllers.project.routes.ProjectController.javascriptRoutes());
		dynamicJs.add(controllers.routes.Assets.at("javascripts/project/project.js"));

		List<Call> dynamicCss = new ArrayList<Call>();
		dynamicCss.add(controllers.routes.Assets.at("stylesheets/project/project.css"));

		Html page = common_main.render(
				pageTitle, topBar, topNav, content, dynamicCss, dynamicJs);

		return ok(page);
	}

	/*
	 * Create a new project
	 */
	public static Result createNewProject() {
		DynamicForm form = form().bindFromRequest();
		Logger.info("PARAMETERS : " + form.data().toString());
		String projectName = form.get("project_name");
		String projectDescription = form.get("project_desc");
		try{
			ProjectDao.createNewProject(projectName, projectDescription);
			statusMessage = String.format("Successfully created Project %s.", projectName);
			flash("success", statusMessage);
		}
		catch(IOException ioe){
			ioe.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return redirect(controllers.project.routes.ProjectController.showProject(projectName));
	}

	/*
	 * Save a project
	 */
	public static Result saveProject(String projectName) {
		Project project = ProjectDao.open(projectName);
		try {
			ProjectDao.save(project);
			statusMessage = String.format("Successfully saved Project %s.", projectName);
			flash("success", statusMessage);
		}
		catch(IOException ioe) {
			flash("error", ioe.getMessage());
			statusMessage = "Error: " + ioe.getMessage();
		}
		return redirect(controllers.project.routes.ProjectController.showProject(projectName));
	}

	/*
	 * Save a project and exit
	 */
	public static Result saveAndExit(String projectName) {
		Project project = ProjectDao.open(projectName);
		try {
			ProjectDao.save(project);
			statusMessage = String.format("Successfully saved Project %s.", projectName);
			flash("success", statusMessage);
		}
		catch(IOException ioe) {
			flash("error", ioe.getMessage());
			statusMessage = "Error: " + ioe.getMessage();
		}
		return redirect(controllers.project.routes.ProjectController.index());
	}

	/*
	public static ObjectNode getJsonResponse(Html content) {
		ObjectNode result = Json.newObject();
		result.put("data", content.toString());
		return result;
	}
	 */

	public static Result importTableFromCSV(String projectName) {
		DynamicForm form = form().bindFromRequest();
		Logger.info("PARAMETERS : " + form.data().toString());
		String tableName = form.get("table_name");

		MultipartFormData body = request().body().asMultipartFormData();
		FilePart fp = body.getFile("csv_file_path");

		if (fp != null) {
			String fileName = fp.getFilename();
			String contentType = fp.getContentType();
			Logger.info("fileName: " + fileName + ", contentType: " + contentType);
			File file = fp.getFile();
			Set<DefaultType> defaultTypes = new HashSet<DefaultType>();
			boolean saveToDisk = false;
			if(null != form.get("table1_default")){
				defaultTypes.add(DefaultType.TABLE1);
			}
			if(null != form.get("table2_default")){
				defaultTypes.add(DefaultType.TABLE2);
			}
			if(null != form.get("candset_default")){
				defaultTypes.add(DefaultType.CAND_SET);
			}
			if(null != form.get("matches_default")){
				defaultTypes.add(DefaultType.MATCHES);
			}
			if(null != form.get("gold_default")){
				defaultTypes.add(DefaultType.GOLD);
			}
			if(null != form.get("save_to_disk")){
				saveToDisk = true;
			}
			try{
				Table table = TableDao.importFromCSVWithHeader(projectName, tableName,
						file.getAbsolutePath());

				// save the table - this automatically updates the project but does not save it
				TableDao.save(table, defaultTypes, saveToDisk);
				statusMessage = "Successfully imported Table " + tableName + " with " + table.getSize() +
						" tuples.";
			}
			catch(IOException ioe){
				flash("error", ioe.getMessage());
				statusMessage = "Error: " + ioe.getMessage();
			}
		} else {
			flash("error", "Missing file");
			statusMessage = "Error: Missing file";
		}
		return redirect(controllers.project.routes.ProjectController.showProject(projectName));
	}

	public static Result importTable(String projectName) {
		DynamicForm form = form().bindFromRequest();
		Logger.info("PARAMETERS : " + form.data().toString());
		String tableName = form.get("table_name");

		MultipartFormData body = request().body().asMultipartFormData();
		FilePart fp = body.getFile("table_file_path");

		if (fp != null) {
			String fileName = fp.getFilename();
			String contentType = fp.getContentType();
			Logger.info("fileName: " + fileName + ", contentType: " + contentType);
			File file = fp.getFile();
			Set<DefaultType> defaultTypes = new HashSet<DefaultType>();
			boolean saveToDisk = false;
			if(null != form.get("table1_default")){
				defaultTypes.add(DefaultType.TABLE1);
			}
			if(null != form.get("table2_default")){
				defaultTypes.add(DefaultType.TABLE2);
			}
			if(null != form.get("candset_default")){
				defaultTypes.add(DefaultType.CAND_SET);
			}
			if(null != form.get("matches_default")){
				defaultTypes.add(DefaultType.MATCHES);
			}
			if(null != form.get("gold_default")){
				defaultTypes.add(DefaultType.GOLD);
			}
			if(null != form.get("save_to_disk")){
				saveToDisk = true;
			}
			try{
				Table table = TableDao.importTable(projectName, tableName,
						file.getAbsolutePath());

				// save the table - this automatically updates the project but does not save it
				TableDao.save(table, defaultTypes, saveToDisk);
				statusMessage = "Successfully imported Table " + tableName + " with " + table.getSize() +
						" tuples.";
			}
			catch(IOException ioe){
				flash("error", ioe.getMessage());
				statusMessage = "Error: " + ioe.getMessage();
			}
		} else {
			flash("error", "Missing file");
			statusMessage = "Error: Missing file";
		}
		return redirect(controllers.project.routes.ProjectController.showProject(projectName));
	}
	
	public static Result saveTable(String projectName) {
		DynamicForm form = form().bindFromRequest();
		Logger.info("PARAMETERS : " + form.data().toString());
		String tableName = form.get("table_name");

		// save the table - this automatically updates the project but does not save it
		TableDao.saveTable(projectName, tableName);
		statusMessage = "Successfully saved Table " + tableName + " to disk.";
		return redirect(controllers.project.routes.ProjectController.showProject(projectName));
	}

	public static Result saveAllTables(String projectName) {
		// save the table - this automatically updates the project but does not save it
		int numTables = TableDao.saveAllTables(projectName);
		statusMessage = "Successfully saved " + numTables + " unsaved tables to disk.";
		return redirect(controllers.project.routes.ProjectController.showProject(projectName));
	}

	public static Result block(String projectName) {
		DynamicForm form = form().bindFromRequest();
		Logger.info("PARAMETERS : " + form.data().toString());
		String table1Name = form.get("table1_name");
		String table2Name = form.get("table2_name");
		String attr1Name = form.get("attr1_name");
		String attr2Name = form.get("attr2_name");
		String candsetName = form.get("candset_name");
		String[] table1AttributeNames = request().body().asFormUrlEncoded().get("attr1_names[]");
		String[] table2AttributeNames = request().body().asFormUrlEncoded().get("attr2_names[]");

		Set<DefaultType> defaultTypes = new HashSet<DefaultType>();
		boolean saveToDisk = false;
		if(null != form.get("candset_default")){
			defaultTypes.add(DefaultType.CAND_SET);
		}
		if(null != form.get("save_to_disk")){
			saveToDisk = true;
		}
		try {
			Table candset = BlockingDao.block(projectName, table1Name,
					table2Name, attr1Name, attr2Name, candsetName, table1AttributeNames,
					table2AttributeNames);
			// save the candset - this automatically updates and saves the project
			TableDao.save(candset, defaultTypes, saveToDisk);
			statusMessage = "Successfully created Candidate Set " + candsetName + " with " + candset.getSize() +
					" pairs.";
		} catch (IOException ioe) {
			flash("error", ioe.getMessage());
			statusMessage = "Error: " + ioe.getMessage();
		}
		return redirect(controllers.project.routes.ProjectController.showProject(projectName));
	}

	public static Result getAttributes(String projectName, String tableName) {
    ObjectNode result = Json.newObject();

	  Project project = ProjectDao.open(projectName);
	  // TODO Sanjib: review.
	  if (project.getTableNames().contains(tableName)) {
  		List<String> attributeNames = new ArrayList<String>();
  		try {
  			Table table = TableDao.open(projectName, tableName);
  			List<Attribute> attributes = table.getAttributes();
  			for(Attribute attribute : attributes) {
  				attributeNames.add(attribute.getName());
  			}
  		} catch (IOException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}
  
  		ObjectMapper mapper = new ObjectMapper();
  		ArrayNode array = mapper.valueToTree(attributeNames);
  		result.putArray("attributes").addAll(array);
	  }
		return ok(result);
	}
	
	public static Result getFeatureNames(String projectName) {
		ObjectNode result = Json.newObject();
		Project project = ProjectDao.open(projectName);
		List<String> featureNames = project.getFeatureNames();
		ObjectMapper mapper = new ObjectMapper();
  		ArrayNode array = mapper.valueToTree(featureNames);
  		result.putArray("features").addAll(array);
  		return ok(result);
	}
	
	public static Result getRelationalOperatorNames() {
		ObjectNode result = Json.newObject();
		List<String> relopNames = RelationalOperator.getOperatorNames();
		ObjectMapper mapper = new ObjectMapper();
  		ArrayNode array = mapper.valueToTree(relopNames);
  		result.putArray("relops").addAll(array);
  		return ok(result);
	}
	
	public static Result setDefaults(String projectName){
		DynamicForm form = form().bindFromRequest();
		Logger.info("PARAMETERS : " + form.data().toString());
		String table1Name = form.get("table1_name_sd");
		String table2Name = form.get("table2_name_sd");
		String candsetName = form.get("candset_name_sd");
		String matchesName = form.get("matches_name_sd");
		String goldName = form.get("gold_name_sd");
		
		boolean saveToDisk = false;
		if(null != form.get("save_to_disk")){
			saveToDisk = true;
		}
		
		try {
			ProjectDao.setDefaults(projectName,table1Name,table2Name,candsetName,matchesName,goldName,saveToDisk);
			statusMessage = "Successfully set the project defaults.";
		} catch (IOException ioe) {
			flash("error", ioe.getMessage());
			statusMessage = "Error: " + ioe.getMessage();
		}
		
		return redirect(controllers.project.routes.ProjectController.showProject(projectName));
  }
	
	 public static Result javascriptRoutes() {
	    response().setContentType("text/javascript");
	    return ok(
	        Routes.javascriptRouter("projectJsRoutes",
	            controllers.project.routes.javascript.ProjectController.getAttributes(),
	            controllers.project.routes.javascript.RuleController.getRuleString(),
              controllers.project.routes.javascript.RuleController.getMatcherString(),
              controllers.project.routes.javascript.RuleController.getFeatureString(),
              controllers.project.routes.javascript.RuleController.getRecommendedFunctions(),
              controllers.project.routes.javascript.ProjectController.getFeatureNames(),
              controllers.project.routes.javascript.ProjectController.getRelationalOperatorNames()
	            )
	        );
	  }
}
