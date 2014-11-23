package com.walmart.productgenome.matching.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.walmart.productgenome.matching.daos.ProjectDao;
import com.walmart.productgenome.matching.models.Constants;
import com.walmart.productgenome.matching.models.DefaultType;
import com.walmart.productgenome.matching.models.data.Project;
import com.walmart.productgenome.matching.models.data.Table;
import com.walmart.productgenome.matching.models.loaders.TableLoader;
import com.walmart.productgenome.matching.models.savers.TableSaver;

public class TableService {

	private static Map<String, List<String>> projectTableMap = new HashMap<String, List<String>>();
	private static Map<String, Map<String, Table>> tableCache = new HashMap<String, Map<String, Table>>();

	static {
		List<Project> projects = ProjectService.loadAllProjects();
		for(Project p : projects){
			projectTableMap.put(p.getName(),p.getTableNames());
			tableCache.put(p.getName(), new HashMap<String, Table>());
		}
	}
	
	public static Table openTable(String projectName, String tableName) throws IOException {
		List<String> tableNames = projectTableMap.get(projectName);
		if(!tableNames.contains(tableName)) {
			// table does not exist
			return null;
		}

		// check if the table is in the cache
		Map<String, Table> tableMap = tableCache.get(projectName);
		Table table = tableMap.get(tableName);
		if(null == table) {
			// load table from disk
			System.out.println("Loading Project " + projectName + " -> Table " + tableName + " from disk");
			String tableFilePath = Constants.ROOT_DIR + projectName + "/" +
					tableName + ".table";

			table = TableLoader.loadTable(tableFilePath);

			// put in cache
			tableMap.put(tableName, table);
			tableCache.put(projectName,tableMap);
		}

		return table;   
	}
	
	public static void saveTableToDisk(Table table) {
		TableSaver.saveTable(table);
	}
	
	public static void addTableToProject(Project project, Table table,
			Set<DefaultType> defaultTypes) {
		String tableName = table.getName();
		// add table to project
		project.addTableName(tableName);

		// set defaults for the project
		for(DefaultType defaultType : defaultTypes){
			switch(defaultType){
			case TABLE1:
				// default table 1
				project.setDefaultTable1(tableName);
				break;
			case TABLE2:
				// default table 2
				project.setDefaultTable2(tableName);
				break;
			case CAND_SET:
				// default candset
				project.setDefaultCandset(tableName);
				break;
			case MATCHES:
				// default matches
				project.setDefaultMatches(tableName);
				break;
			case GOLD:
				// default gold
				project.setDefaultGold(tableName);
				break;
			case NONE:
				// do nothing
				break;
			default:
				// TODO: throw an exception
				// do nothing
			}
		}

		String projectName = project.getName(); 
		// update map
		projectTableMap.put(projectName, project.getTableNames());

		// update table cache
		Map<String, Table> tableMap = tableCache.get(projectName);
		if(null == tableMap) {
			tableMap = new HashMap<String, Table>();
		}
		tableMap.put(tableName, table);
		tableCache.put(projectName, tableMap);

		// put this table in the unsavedTables
		project.addUnsavedTable(tableName);

		// update project
		ProjectService.updateProject(project);
	}
}
