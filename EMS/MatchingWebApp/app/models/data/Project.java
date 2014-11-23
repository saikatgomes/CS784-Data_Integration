package models.data;

import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;

public class Project {

	private static final DateFormat formatter = new SimpleDateFormat();
	
	private String name;	
	private String description;
	private Date createdOn = new Date();
	private Date lastModifiedOn = new Date();
	
	private Set<String> tableNames = new HashSet<String>();
	
	public Project(){
	}

	public Project(String name, String description){
		this.name = name;
		this.description = description;
	}
	
	public String getName(){
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription(){
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getLastModifiedOn() {
		return lastModifiedOn;
	}

	public void setLastModifiedOn(Date lastModifiedOn) {
		this.lastModifiedOn = lastModifiedOn;
	}

	public void setCreatedOn(String createdOn) throws ParseException {
		this.createdOn = (Date)formatter.parse(createdOn);
	}
	
	public void setLastModifiedOn(String lastModifiedOn) throws ParseException {
		this.lastModifiedOn = (Date)formatter.parse(lastModifiedOn);
	}
	
	public Set<String> getTableNames(){
		return tableNames;
	}
	
	public void setTableNames(Collection<String> tableNames){
		this.tableNames.addAll(tableNames);
	}
	
	public void addTableName(String tableName){
		tableNames.add(tableName);
	}
	
	public boolean deleteTableName(String tableName){
		return tableNames.remove(tableName);
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(name);
		sb.append(",");
		sb.append(description);
		sb.append(",");
		sb.append(createdOn);
		sb.append(",");
		sb.append(lastModifiedOn);
		sb.append("]");
		return sb.toString();
	}

	public String toJSONString(){
		Map<String, Object> properties = new HashMap<String, Object>(1);
        properties.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonGeneratorFactory jgf = Json.createGeneratorFactory(properties);
        Writer sw = new StringWriter();
        JsonGenerator jg = jgf.createGenerator(sw);
         
        jg = jg.writeStartObject()                    						// {
            .writeStartObject("project")         							//    "project":{
                .write("name", name)             							//        "name":name,
                .write("description", description)  						//        "description":description,
                .write("createdOn", formatter.format(createdOn))  			//        "createdOn":createdOn,
                .write("lastModifiedOn", formatter.format(lastModifiedOn))	//        "lastModifiedOn":lastModifiedOn
                .writeStartArray("tables");									//		  "tables":[
        for(String s : tableNames){
        	jg = jg.writeStartObject()										//			{
                		.write("name",s)									//				"name":name
                	.writeEnd();											//			}
        }
        jg.writeEnd()														//	  	  ]	
        .writeEnd()															// 		}
        .writeEnd()															// }
        .close();
        
        return sw.toString();
	}
}
