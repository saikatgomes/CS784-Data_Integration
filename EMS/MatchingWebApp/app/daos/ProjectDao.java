package daos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import models.Constants;
import models.data.Project;

// import org.apache.commons.io.FileUtils;

public class ProjectDao {
    
    private static Map<String,Project> projectMap = new HashMap<String,Project>();

    static {
        Set<Project> projects = ProjectDao.findAll();
        for(Project p : projects){
            projectMap.put(p.getName(),p);
        }
    }

    public static Project open(String name){
        return projectMap.get(name);
    }
    
    public static Set<Project> list() {
        return new HashSet<Project>(projectMap.values());
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
    
    public static void save(Project project) {
        project.setLastModifiedOn(new Date());
        // create the metadata file
        String json = project.toJSONString();
        try {
            FileWriter fw = new FileWriter(Constants.ROOT_DIR + project.getName() + "-project.info");
            fw.write(json);
            fw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        // create a directory if it does not exist
        File projectDir = new File(Constants.ROOT_DIR + project.getName());
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
        // update map
        projectMap.put(project.getName(),project);
    }
    
    private static Project getProjectFromInfoFile(File f) throws FileNotFoundException{
        JsonReader reader = Json.createReader(new FileInputStream(f));
        JsonObject obj = reader.readObject();
        JsonObject projObj = obj.getJsonObject("project");
        String name = projObj.getString("name");
        String description = projObj.getString("description");
        String createdOn = projObj.getString("createdOn");
        String lastModifiedOn = projObj.getString("lastModifiedOn");
        
        JsonArray tables = projObj.getJsonArray("tables");
        Set<String> tableNames = new HashSet<String>();
        for (JsonObject table : tables.getValuesAs(JsonObject.class)) {
            String tableName = table.getString("name");
            tableNames.add(tableName);
        }
        
        Project p = new Project(name,description);
        p.setTableNames(tableNames);
        try {
            p.setCreatedOn(createdOn);
            p.setLastModifiedOn(lastModifiedOn);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return p;
    }
    
    static Set<Project> findAll(){
        Set<Project> projects = new HashSet<Project>();
        File dir = new File(Constants.ROOT_DIR);
        File[] files = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith("-project.info");
            }
        });
        for(File f : files){
            try {
                Project p = getProjectFromInfoFile(f);
                projects.add(p);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return projects;
    }

    public static void main(String[] args) throws InterruptedException{
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
