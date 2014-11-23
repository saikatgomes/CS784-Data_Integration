package controllers;

import static play.data.Form.form;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.walmart.productgenome.matching.models.DefaultType;
import com.walmart.productgenome.matching.models.data.Attribute;
import com.walmart.productgenome.matching.models.data.Project;
import com.walmart.productgenome.matching.models.data.Table;
import com.walmart.productgenome.matching.service.BlockingService;
import com.walmart.productgenome.matching.service.ProjectService;
import com.walmart.productgenome.matching.service.TableService;

import play.Logger;
import play.data.DynamicForm;
import play.mvc.Controller;
import play.mvc.Result;

public class BlockingController extends Controller {

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
			Table table1 = TableService.openTable(projectName, table1Name);
			Table table2 = TableService.openTable(projectName, table2Name);
			Attribute table1BlockingAttribute = table1.getAttributeByName(attr1Name);
			Attribute table2BlockingAttribute = table2.getAttributeByName(attr2Name);
			List<Attribute> table1AdditionalAttributes = null;
			if(null != table1AttributeNames) {
				table1AdditionalAttributes = new ArrayList<Attribute>();
				for(String attributeName : table1AttributeNames) {
					Attribute attribute = table1.getAttributeByName(attributeName);
				}
			}
			List<Attribute> table2AdditionalAttributes = null;
			if(null != table2AttributeNames) {
				table2AdditionalAttributes = new ArrayList<Attribute>();
				for(String attributeName : table2AttributeNames) {
					Attribute attribute = table2.getAttributeByName(attributeName);
				}
			}
			
			// get candidate set by performing blocking
			Table candset = BlockingService.block(table1, table2, table1BlockingAttribute,
					table2BlockingAttribute, table1AdditionalAttributes,
					table2AdditionalAttributes, candsetName, projectName);
			
			// add the table to project
			Project project = ProjectService.openProject(projectName);
			TableService.addTableToProject(project, candset, defaultTypes);
			
			if(saveToDisk) {
				TableService.saveTableToDisk(candset);
				ProjectService.saveTable(project, candsetName);
			}
			
			MainController.statusMessage = "Successfully created Candidate Set " + candsetName + " with " + candset.getSize() +
					" pairs.";
		} catch (IOException ioe) {
			flash("error", ioe.getMessage());
			MainController.statusMessage = "Error: " + ioe.getMessage();
		}
		return redirect(controllers.project.routes.ProjectController.showProject(projectName));
	}
}
