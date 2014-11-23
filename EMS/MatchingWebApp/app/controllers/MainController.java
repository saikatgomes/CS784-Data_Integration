package controllers;

import java.util.ArrayList;
import java.util.List;

import play.mvc.Call;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.Html;
import views.html.common.common_main;
import views.html.project.project_dashboard;
import views.html.project.project_dashboard_topbar;

import com.walmart.productgenome.matching.daos.ProjectDao;
import com.walmart.productgenome.matching.models.data.Project;

public class MainController extends Controller {

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
}
