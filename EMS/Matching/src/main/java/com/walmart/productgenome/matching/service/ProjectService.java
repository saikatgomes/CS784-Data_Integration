package com.walmart.productgenome.matching.service;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.walmart.productgenome.matching.daos.ProjectDao;
import com.walmart.productgenome.matching.models.Constants;
import com.walmart.productgenome.matching.models.DefaultType;
import com.walmart.productgenome.matching.models.data.Project;
import com.walmart.productgenome.matching.models.data.Table;
import com.walmart.productgenome.matching.utils.JSONUtils;

public class ProjectService {

	private static Map<String, Project> projectMap = new HashMap<String, Project>();
	
	static {
		List<Project> projects = loadAllProjects();
		for(Project p : projects){
			projectMap.put(p.getName(), p);
		}
	}

	static List<Project> loadAllProjects(){
		List<Project> projects = new ArrayList<Project>();
		File dir = new File(Constants.ROOT_DIR);
		File[] files = dir.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.isDirectory();
			}
		});
		for(File f : files){
			try {
				if(f.isDirectory()){
					String projectName = f.getName();
					Project p = JSONUtils.getProjectFromJSONFile(projectName);
					projects.add(p);
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return projects;
	}
	
	public static void saveTable(Project project, String tableName) {
		project.removeUnsavedTable(tableName);
		projectMap.put(project.getName(), project);
	}
	
	public static Project openProject(String projectName) {
		return projectMap.get(projectName);
	}
	
	public static void updateProject(Project project) {
    	projectMap.put(project.getName(), project);
    }
}