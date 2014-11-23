package com.walmart.productgenome.matching.daos;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.walmart.productgenome.matching.models.Constants;
import com.walmart.productgenome.matching.models.DefaultType;
import com.walmart.productgenome.matching.models.data.Attribute;
import com.walmart.productgenome.matching.models.data.Project;
import com.walmart.productgenome.matching.models.data.Table;
import com.walmart.productgenome.matching.models.data.Tuple;
import com.walmart.productgenome.matching.models.loaders.CSVLoader;
import com.walmart.productgenome.matching.models.loaders.TableLoader;
import com.walmart.productgenome.matching.models.savers.TableSaver;

public class TableDao {

	private static Map<String, List<String>> projectTableMap = new HashMap<String, List<String>>();
	private static Map<String, Map<String, Table>> tableCache = new HashMap<String, Map<String, Table>>();

	static {
		List<Project> projects = ProjectDao.findAll();
		for(Project p : projects){
			projectTableMap.put(p.getName(),p.getTableNames());
			tableCache.put(p.getName(), new HashMap<String, Table>());
		}
	}

	public static List<String> listTableNames(String projectName) {
		return projectTableMap.get(projectName);
	}

	public static Table open(String projectName, String tableName) throws IOException {
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

	public static boolean delete(Table table) throws IOException {
		String tableName = table.getName();
		String projectName = table.getProjectName();
		boolean success = false;

		// delete the table file
		File tableFile = new File(Constants.ROOT_DIR + projectName + "/" + tableName + ".table");
		success = tableFile.delete();

		if(!success){
			return success;
		}

		// update project
		Project project = ProjectDao.open(projectName);
		success = project.deleteTableName(tableName);
		ProjectDao.save(project);

		// update map
		projectTableMap.put(projectName,project.getTableNames());

		// update table cache
		Map<String, Table> tableMap = tableCache.get(projectName);
		tableMap.remove(tableName);
		tableCache.put(projectName, tableMap);

		return success;
	}

	public static void saveTable(String projectName, String tableName) {
		Project project = ProjectDao.open(projectName);
		if (!project.isUnsavedTable(tableName)){
			// table is already saved
			return;
		}
		
		// table is in memory but not saved
		Table table = tableCache.get(projectName).get(tableName);
		TableSaver.saveTable(table);
		
		// update project
		project.removeUnsavedTable(tableName);
		ProjectDao.updateProject(project);
	}
	
	public static int saveAllTables(String projectName) {
		Project project = ProjectDao.open(projectName);
		Set<String> unsavedTableNames = project.getUnsavedTables();
		for(String tableName : unsavedTableNames) {
			// table is in memory but not saved
			Table table = tableCache.get(projectName).get(tableName);
			TableSaver.saveTable(table);
			
			// update project
			project.removeUnsavedTable(tableName);
		}
		ProjectDao.updateProject(project);
		return unsavedTableNames.size();
	}
	
	public static void save(Table table, Set<DefaultType> defaultTypes, boolean saveToDisk) throws IOException {

		// add table to project
		String projectName = table.getProjectName();
		String tableName = table.getName();
		Project project = ProjectDao.open(projectName);
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
				/*
				List<Attribute> matchAttributes = table.getAttributes();
				project.setDefaultMatchesIdAttr1(matchAttributes.get(1).getName());
				project.setDefaultMatchesIdAttr2(matchAttributes.get(2).getName());
				project.setDefaultMatchesLabelAttr(matchAttributes.get(matchAttributes.size()-1).getName());
				*/
				break;
			case GOLD:
				// default gold
				project.setDefaultGold(tableName);
				/*
				List<Attribute> goldAttributes = table.getAttributes();
				project.setDefaultGoldIdAttr1(goldAttributes.get(0).getName());
				project.setDefaultGoldIdAttr2(goldAttributes.get(1).getName());
				project.setDefaultGoldLabelAttr(goldAttributes.get(goldAttributes.size()-1).getName());
				*/
				break;
			case NONE:
				// do nothing
				break;
			default:
				// TODO: throw an exception
				// do nothing
			}
		}

		// update map
		projectTableMap.put(projectName, project.getTableNames());

		// update table cache
		Map<String, Table> tableMap = tableCache.get(projectName);
		if(null == tableMap){
			tableMap = new HashMap<String, Table>();
		}
		tableMap.put(tableName, table);
		tableCache.put(projectName, tableMap);
		
		// save to disk
		if(saveToDisk) {
			TableSaver.saveTable(table);
		}
		else {
			// put this table in the unsavedTables
			project.addUnsavedTable(tableName);
		}
	
		// update project
		ProjectDao.updateProject(project);
	}

	public static Table importFromCSVWithHeader(String projectName,
			String tableName, String csvFilePath) throws IOException{
		// assumes the header is the schema with the first attribute being the id
		return CSVLoader.loadTableFromCSVWithHeaderAsSchema(projectName,
				tableName, csvFilePath);
	}

	public static Table importFromCSV(String projectName, String tableName,
			String csvFilePath, Attribute idAttrib, List<Attribute> attributes)
					throws IOException{
		return CSVLoader.loadTableFromCSV(projectName, tableName, csvFilePath,
				idAttrib, attributes);
	}
	
	public static Table importTable(String projectName,
			String tableName, String tableFilePath) throws IOException{
		Table table = TableLoader.loadTable(tableFilePath);
		table.setName(tableName);
		table.setProjectName(projectName);
		return table;
	}
}