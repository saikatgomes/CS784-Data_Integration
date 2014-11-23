package com.walmart.productgenome.matching.models.loaders;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.walmart.productgenome.matching.models.data.Attribute;
import com.walmart.productgenome.matching.models.data.Table;
import com.walmart.productgenome.matching.models.data.Tuple;
import com.walmart.productgenome.matching.utils.JSONUtils;

public class CSVLoader {

	private static Attribute getAttribute(String attribStr){
		// attribStr is of the form <attrib_name> or <attrib_name:attrib_type>
		// if attrib_type is missing, it is assumed to be TEXT by default
		String[] vals = attribStr.split(":");
		assert vals.length > 0;
		String attribName = vals[0].trim();
		String attribType = "TEXT";
		if(vals.length > 1){
			attribType = vals[1].trim();
		}
		return new Attribute(attribName,Attribute.Type.valueOf(attribType));
	}

	public static Table loadTable(String tableFilePath) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(tableFilePath));

		// skip lines till you encounter @info
		String line;
		while ((line = br.readLine()) != null) {
			if(line.startsWith("@info")){
				break;
			}
		}

		// get the JSON metadata
		StringBuilder sb = new StringBuilder();
		while ((line = br.readLine()) != null) {
			if(line.trim().isEmpty()){
				break;
			}
			sb.append(line);
		}
		String tableJSON = sb.toString();
		Table table = JSONUtils.getTableFromJSON(tableJSON);

		// skip lines till you encounter @data
		while ((line = br.readLine()) != null) {
			if(line.startsWith("@data")){
				break;
			}
		}

		//skip the header line
		line = br.readLine();

		// parse the CSV data
		CSVParser parser = new CSVParser(br,CSVFormat.DEFAULT);
		List<CSVRecord> records = parser.getRecords();
		br.close();

		List<Tuple> tuples = new ArrayList<Tuple>();
		int size = records.size();
		// System.out.println("No. of tuples: " + size);
		List<Attribute> attributes = table.getAttributes();
		for(int i = 0; i < size; i++){
			CSVRecord rec = records.get(i);
			Map<Attribute,Object> attrValMap = new HashMap<Attribute,Object>();
			for(int j=0; j<attributes.size(); j++){
				Attribute a = attributes.get(j);
				String value = rec.get(j);
				attrValMap.put(a, a.convertValueToObject(value));
				//System.out.println("i: " + i + ", j: "+ j);
			}
			tuples.add(new Tuple(attrValMap));
		}
		// System.out.println("Size of tuples: " + tuples.size());
		table.addAllTuples(tuples);
		return table;
	}

	public static Table loadTableFromCSVWithHeaderAsSchema(String projectName,
			String tableName, String csvFilePath) throws IOException{
		// assumes the header is the schema with the first attribute being the id
		FileReader r = new FileReader(csvFilePath);
		CSVParser parser = new CSVParser(r,CSVFormat.DEFAULT);
		List<CSVRecord> records = parser.getRecords();
		r.close();

		// header
		// attr1_name:attr1_type,attr2_name:attr_type,...
		// if attr_type is missing, it is assumed to be TEXT by default
		CSVRecord header = records.get(0); 
		Attribute idAttrib = getAttribute(header.get(0));
		List<Attribute> attributes = new ArrayList<Attribute>();
		attributes.add(idAttrib);
		for(int i=1; i < header.size(); i++){
			Attribute a = getAttribute(header.get(i));
			attributes.add(a);
		}

		List<Tuple> tuples = new ArrayList<Tuple>();
		int size = records.size();
		// System.out.println("No. of tuples: " + size);
		for(int i = 1; i < size; i++){
			CSVRecord rec = records.get(i);
			Map<Attribute,Object> attrValMap = new HashMap<Attribute,Object>();
			for(int j = 0; j < attributes.size(); j++){
				Attribute a = attributes.get(j);
				String value = rec.get(j);
				attrValMap.put(a, a.convertValueToObject(value));
				//System.out.println("i: " + i + ", j: "+ j);
			}
			tuples.add(new Tuple(attrValMap));
		}
		// System.out.println("Size of tuples: " + tuples.size());
		return new Table(tableName,idAttrib,attributes,tuples,projectName);
	}

	public static Table loadTableFromCSV(String projectName, String tableName,
			String csvFilePath, Attribute idAttrib, List<Attribute> attributes)
					throws IOException{	
		FileReader r = new FileReader(csvFilePath);
		CSVParser parser = new CSVParser(r,CSVFormat.DEFAULT);
		List<CSVRecord> records = parser.getRecords();
		r.close();

		List<Tuple> tuples = new ArrayList<Tuple>();
		int size = records.size();
		// System.out.println("No. of tuples: " + size);
		for(int i = 0; i < size; i++){
			CSVRecord rec = records.get(i);
			Map<Attribute,Object> attrValMap = new HashMap<Attribute,Object>();
			for(int j = 0; j < attributes.size(); j++){
				Attribute a = attributes.get(j);
				String value = rec.get(j);
				attrValMap.put(a, a.convertValueToObject(value));
				//System.out.println("i: " + i + ", j: "+ j);
			}
			tuples.add(new Tuple(attrValMap));
		}
		// System.out.println("Size of tuples: " + tuples.size());
		return new Table(tableName, idAttrib, attributes, tuples, projectName);
	}

	public static void main(String[] args) throws IOException{
		String csvFilePath = "test.csv";
		String projectName = "testing";
		String tableName = "test";
		Table t = loadTableFromCSVWithHeaderAsSchema(projectName, tableName, csvFilePath);
		System.out.println(JSONUtils.getTableJSON(t));
	}
}
