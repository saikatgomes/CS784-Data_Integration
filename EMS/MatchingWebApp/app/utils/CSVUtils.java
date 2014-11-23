package utils;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import models.Constants;
import models.data.Attribute;
import models.data.Item;
import models.data.Table;

public class CSVUtils {

  public static void write(Table table) throws IOException{
    List<Attribute> schema = table.getAttributes();
    List<Item> items = table.getAllItemsInOrder();
    
    String fileName = Constants.ROOT_DIR + table.getProjectName() + "/"
              + table.getName() + "-table.data";
    CSVPrinter p = new CSVPrinter(new BufferedWriter(new PrintWriter(fileName)),null);
    
    //print the header
    for(Attribute a : schema){
      p.print(a.getName());
    }
    p.println();
    
    //print the items
    for(Item i : items){
      for(Attribute a : schema){
        p.print(i.getAttributeValue(a));
      }
      p.println();
    }
    p.close();
  }
  
  public static Table read(String projectName, String tableName, String csvFilePath) throws IOException{
    // assumes the header is the schema with the first attribute being the id
    FileReader r = new FileReader(csvFilePath);
    CSVParser parser = new CSVParser(r);
    List<CSVRecord> records = parser.getRecords();
    r.close();
    
    // header
    CSVRecord header = records.get(0);
    Attribute idAttrib = new Attribute(header.get(0),Attribute.Type.TEXT);
    List<Attribute> attributes = new ArrayList<Attribute>();
    for(int i=0; i < header.size(); i++){
      Attribute a = new Attribute(header.get(i),Attribute.Type.TEXT);
      attributes.add(a);
    }
    
    List<Item> items = new ArrayList<Item>();
    int size = records.size();
    System.out.println("No. of records: " + size);
    for(int i = 1; i < size; i++){
      CSVRecord rec = records.get(i);
      Map<Attribute,Object> attrValMap = new HashMap<Attribute,Object>();
      for(int j=0; j<attributes.size(); j++){
        Attribute a = attributes.get(j);
        String value = rec.get(j);
        attrValMap.put(a, value);
        //System.out.println("i: " + i + ", j: "+ j);
      }
      items.add(new Item(attrValMap));
    }
    System.out.println("Size of items: " + items.size());
    return new Table(tableName,idAttrib,attributes,items,projectName);
  }
  
  public static Table read(String projectName, String tableName,
      String csvFilePath, Attribute idAttrib, List<Attribute> attributes)
          throws IOException {
    FileReader r = new FileReader(csvFilePath);
    CSVParser parser = new CSVParser(r);
    List<CSVRecord> records = parser.getRecords();
    r.close();
    
    List<Item> items = new ArrayList<Item>();
    for(int i=1; i< records.size(); i++){
      CSVRecord rec = records.get(i);
      Map<Attribute,Object> attrValMap = new HashMap<Attribute,Object>();
      for(int j=0; j<attributes.size(); j++){
        Attribute a = attributes.get(j);
        String value = rec.get(j);
        attrValMap.put(a, value);
      }
      items.add(new Item(attrValMap));
    }
    return new Table(tableName,idAttrib,attributes,items,projectName);
  }
  
  public static void main(String[] args) {
    

  }

}
