package com.walmart.productgenome.matching.daos;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.walmart.productgenome.matching.models.Constants;
import com.walmart.productgenome.matching.models.data.Project;
import com.walmart.productgenome.matching.models.rules.functions.Function;
import com.walmart.productgenome.matching.utils.CSVUtils;
import com.walmart.productgenome.matching.utils.JSONUtils;

// import org.apache.commons.io.FileUtils;

public class ProjectDao {
    
    private static Map<String, Project> projectMap = new HashMap<String, Project>();

    static {
        List<Project> projects = ProjectDao.findAll();
        for(Project p : projects){
            projectMap.put(p.getName(), p);
        }
    }

    public static Project open(String name) {
        return projectMap.get(name);
    }
    
    public static List<Project> list() {
        return new ArrayList<Project>(projectMap.values());
    }
    
    /*
    public static boolean delete(Project project) {
        String name = project.getName();
        boolean success = false;
        
        // delete the project metadata file
        File f = new File(Constants.ROOT_DIR + name + "-project.info");
        success = f.delete();
        
        // delete the project directory
        File dir = new File(Constants.ROOT_DIR + name);
        try {
            FileUtils.deleteDirectory(dir);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            success = false;
        }
        
        // update map
        projectMap.remove(project.name);
        return success;
    }
    */
    
    public static void save(Project project) throws IOException {
    	String projectName = project.getName();
    	if(project.hasUnsavedArtifacts()) {
    		if(!project.getUnsavedTables().isEmpty()) {
    			TableDao.saveAllTables(projectName);
    		}
    		if(!project.getUnsavedFunctions().isEmpty()) {
    			RuleDao.saveAllFunctions(projectName);
    		}
    		if(!project.getUnsavedFeatures().isEmpty()) {
    			RuleDao.saveAllFeatures(projectName);
    		}
    		if(!project.getUnsavedRules().isEmpty()) {
    			RuleDao.saveAllRules(projectName);
    		}
    		if(!project.getUnsavedMatchers().isEmpty()) {
    			RuleDao.saveAllMatchers(projectName);
    		}
    	}
        project.setLastModifiedOn(new Date());
        // create a directory if it does not exist
        File projectDir = new File(Constants.ROOT_DIR + projectName);
          // if the directory does not exist, create it
          if (!projectDir.exists()) {
            System.out.println("Creating directory: " + project.getName());
            boolean result = false;

            try{
                projectDir.mkdir();
                result = true;
             } catch(SecurityException se){
                //handle it
                se.printStackTrace();
             }        
             if(result) {    
               System.out.println("DIR created");  
             }
          }
          
        // create the metadata file
        String json = JSONUtils.getProjectJSON(project);
        try {
        	
            FileWriter fw = new FileWriter(Constants.ROOT_DIR + projectName + "/"
            		+ projectName + Constants.PROJECT_EXTENSION);
            fw.write(json);
            fw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        // update map
        projectMap.put(project.getName(), project);
    }
 
    public static void updateProject(Project project) {
    	projectMap.put(project.getName(), project);
    }
    
    static List<Project> findAll(){
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

    public static void createNewProject(String projectName, String projectDescription) throws SecurityException, IllegalArgumentException, IOException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
    	Project project = new Project(projectName, projectDescription);
    	save(project); // TODO: Sanjib this can be avoided -- revisit this later
    	RuleDao.importFunctions(project, Constants.GLOBAL_FUNCTIONS_FILE, true);
		save(project);
    }
    
    public static void setDefaults(String projectName, String table1Name, String table2Name,
    		String candsetName, String matchesName, String goldName, boolean saveToDisk) throws IOException {
    	Project project = open(projectName);
    	List<String> tableNames = project.getTableNames();
    	if(tableNames.contains(table1Name)) {
    		project.setDefaultTable1(table1Name);
    	}
    	if(tableNames.contains(table2Name)) {
    		project.setDefaultTable2(table2Name);
    	}
    	if(tableNames.contains(candsetName)) {
    		project.setDefaultCandset(candsetName);
    	}
    	if(tableNames.contains(matchesName)) {
    		project.setDefaultMatches(matchesName);
    	}
    	if(tableNames.contains(goldName)) {
    		project.setDefaultGold(goldName);
    	}
    	if(saveToDisk) {
    		save(project);
    	}
    }
    
    public static void main(String[] args) throws InterruptedException, IOException {
        ProjectDao.save(new Project("Products","Amazon-Walmart"));
        for(Project p : projectMap.values()){
            System.out.println(p);
        }
        Thread.sleep(2000);
        Project project = ProjectDao.open("Products");
        project.setDescription("Product Matching between Amazon and Walmart");
        ProjectDao.save(project);
        for(Project p : projectMap.values()){
            System.out.println(p);
        }
    }
}
