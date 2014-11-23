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

public class TableLoader {

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
}
