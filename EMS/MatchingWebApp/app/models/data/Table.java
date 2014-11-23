package models.data;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;

public class Table { 
	private String name;
	private Attribute idAttrib;
	private List<Attribute> attributes;
	private Map<Object,Item> items;
	private String projectName;
	
	public Table(String name, Attribute idAttrib, List<Attribute> attributes, String projectName){
		this.name = name;
		this.idAttrib = idAttrib;
		this.attributes = attributes;
		items = new LinkedHashMap<Object,Item>();
		this.projectName = projectName;
	}
	
	public Table(String name, Attribute idAttrib, List<Attribute> attributes, List<Item> items, String projectName){
		this.name = name;
		this.idAttrib = idAttrib;
		this.attributes = attributes;
		this.items = new LinkedHashMap<Object,Item>();
		addAllItems(items);
		this.projectName = projectName;
	}
	
	public String getName(){
		return name;
	}
	
	public Attribute getIdAttribute(){
		return idAttrib;
	}
	
	public Item getItem(Object id){
		return items.get(id);
	}
	
	public List<Attribute> getAttributes(){
		return attributes;
	}
	
	public void addItem(Item item){
		//System.out.println("item: " + item);
		Object itemId = item.getAttributeValue(idAttrib);
		//System.out.println("itemId: " + itemId);
		items.put(itemId,item);
	}
	
	public void addAllItems(Collection<Item> items){
		for(Item item : items){
			addItem(item);
		}
	}
	
	public void removeItem(Object id){
		items.remove(id);
	}
	
	public boolean contains(Item item){
		Object itemId = item.getAttributeValue(idAttrib);
		return items.containsKey(itemId);
	}
	
	public boolean isEmpty(){
		return items.isEmpty();
	}
	
	public long getSize(){
		return items.size();
	}
	
	public Collection<Item> getAllItems(){
		return items.values();
	}

	public List<Item> getAllItemsInOrder(){
		List<Item> itemList = new ArrayList<Item>();
		for(Item item : items.values()){
			itemList.add(item);
		}
		return itemList;
	}
	
	public void clear(){
		items.clear();
	}

	public String getProjectName(){
		return projectName;
	}
	
	public List<Object> getAllValuesForAttribute(Attribute attribute){
		List<Object> values = new ArrayList<Object>();
		for(Item item : items.values()){
			Object val = item.getAttributeValue(attribute);
			values.add(val);
		}
		return values;
	}
	
	public String toJSONString(){
		Map<String, Object> properties = new HashMap<String, Object>(1);
        properties.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonGeneratorFactory jgf = Json.createGeneratorFactory(properties);
        Writer sw = new StringWriter();
        JsonGenerator jg = jgf.createGenerator(sw);
         
        jg = jg.writeStartObject()                    			// {
            .writeStartObject("table")         				//    "table":{
                .write("name", name)             				//        "name":name,
                .write("idAttrib", idAttrib.getName())  		//        "idAttrib":idAttrib,
                .write("projectName", projectName)  			//        "projectName":projectName,
                .writeStartArray("attributes");					//        "attributes": [
        for(Attribute attribute : attributes){
        	jg = jg.writeStartObject()              			//        	{
        			.write("name", attribute.getName()) 		//            	"name":name,
        			.write("type", attribute.getType().name())  //            	"type":type
        			.writeEnd();								//        	},
        }
        jg.writeEnd()                          					//    	  ],
        .write("size", items.size())							//		  "size":size	
        .writeEnd()												//	   }
        .writeEnd()                              				// }
        .close();
        
        return sw.toString();
	}
	
	public static void main(String[] args){
		Attribute idAttrib = new Attribute("id",Attribute.Type.TEXT);
		List<Attribute> attributes = new ArrayList<Attribute>();
		attributes.add(idAttrib);
		attributes.add(new Attribute("title", Attribute.Type.TEXT));
		attributes.add(new Attribute("price", Attribute.Type.FLOAT));
		Table t = new Table("Bowker",idAttrib,attributes,"Products");
		System.out.println(t.toJSONString());
	}
}
