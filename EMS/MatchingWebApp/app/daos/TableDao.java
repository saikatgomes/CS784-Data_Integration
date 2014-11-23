package daos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import models.Constants;
import models.data.Attribute;
import models.data.Project;
import models.data.Table;
import utils.CSVUtils;

public class TableDao {

    private static Map<String,Set<String>> projectTableMap = new HashMap<String,Set<String>>();

    static {
        Set<Project> projects = ProjectDao.findAll();
        for(Project p : projects){
            projectTableMap.put(p.getName(),p.getTableNames());
        }
    }

    public static Set<String> listTableNames(String projectName) {
        return projectTableMap.get(projectName);
    }
    
    public static Table open(String projectName, String tableName) throws IOException{
        Set<String> tableNames = projectTableMap.get(projectName);
        if(!tableNames.contains(tableName)){
            return null;
        }
        return getTableFromInfoAndDataFile(projectName, tableName);
    }
    
    private static Table getTableFromInfoAndDataFile(String projectName,
            String tableName) throws IOException{
        File metadata = new File(Constants.ROOT_DIR + projectName + "/" +
            tableName + "-table.info");
        
        JsonReader reader = Json.createReader(new FileInputStream(metadata));
        JsonObject obj = reader.readObject();
        JsonObject tableObj = obj.getJsonObject("table");
        String name = tableObj.getString("name");
        String idAttribStr = tableObj.getString("idAttrib");
        Attribute idAttrib = new Attribute(idAttribStr,Attribute.Type.TEXT);
        String projectString = tableObj.getString("projectName");
        
        assert projectName.equals(projectString);
        
        JsonArray attribs = tableObj.getJsonArray("attributes");
        List<Attribute> attributes = new ArrayList<Attribute>();
        for (JsonObject attr : attribs.getValuesAs(JsonObject.class)) {
            String attrName = attr.getString("name");
            String type = attr.getString("type");
            Attribute.Type attrType = Attribute.Type.valueOf(type);
            attributes.add(new Attribute(attrName,attrType));
        }
        
        String dataFilePath = Constants.ROOT_DIR + projectName + "/" +
                tableName + "-table.data";
        Table table = CSVUtils.read(projectName, tableName, dataFilePath,
                idAttrib, attributes);
        return table;
    }
    
    public static boolean delete(Table table) {
        String tableName = table.getName();
        String projectName = table.getProjectName();
        boolean success = false;
        
        // delete the table metadata and data file
        File metadata = new File(Constants.ROOT_DIR + projectName + "/" + tableName + "-table.info");
        File data = new File(Constants.ROOT_DIR + projectName + "/" + tableName + "-table.data");
        success = metadata.delete() && data.delete();
            
        // update project
        Project project = ProjectDao.open(projectName);
        success = success && project.deleteTableName(tableName);
        ProjectDao.save(project);
        
        // update map
        projectTableMap.put(projectName,project.getTableNames());
                
        return success;
    }
    
    public static void save(Table table) throws IOException {
        String tableName = table.getName();
        String projectName = table.getProjectName();
        
        // create the metadata file
        String json = table.toJSONString();
        try {
            FileWriter fw = new FileWriter(Constants.ROOT_DIR + projectName + "/" + tableName + "-table.info");
            fw.write(json);
            fw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        // create the data file
        CSVUtils.write(table);
        
        // update project
        Project project = ProjectDao.open(projectName);
        project.addTableName(tableName);
        ProjectDao.save(project);
        
        // update map
        projectTableMap.put(projectName,project.getTableNames());
    }
        
    public static Table importFromCSVWithHeader(String projectName,
            String tableName, String csvFilePath) throws IOException{
        // assumes the header is the schema with the first attribute being the id
        return CSVUtils.read(projectName, tableName, csvFilePath);
    }
    
    public static Table importFromCSV(String projectName, String tableName,
            String csvFilePath, Attribute idAttrib, List<Attribute> attributes)
                    throws IOException{
        return CSVUtils.read(projectName, tableName, csvFilePath, idAttrib, attributes);
    }
    
    public static void main(String[] args) {
        try {
            Table t = importFromCSVWithHeader("Products", "walmart", 
                "./data/books/walmart.csv");
            save(t);
            //Table t = open("Products","walmart");
            //delete(t);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }    
    }
}