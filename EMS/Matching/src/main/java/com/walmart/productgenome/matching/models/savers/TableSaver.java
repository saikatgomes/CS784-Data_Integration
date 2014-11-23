package com.walmart.productgenome.matching.models.savers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import com.walmart.productgenome.matching.models.Constants;
import com.walmart.productgenome.matching.models.data.Attribute;
import com.walmart.productgenome.matching.models.data.Table;
import com.walmart.productgenome.matching.models.data.Tuple;
import com.walmart.productgenome.matching.utils.JSONUtils;

public class TableSaver {

	public static void saveTable(Table table) {
		String tableName = table.getName();
        String projectName = table.getProjectName();
        String tableFilePath = Constants.ROOT_DIR + projectName + "/" + tableName
        		+ ".table";
        saveTable(table, tableFilePath);
	}
	
	public static void saveTable(Table table, String tableFilePath) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(tableFilePath));
            
            // meta data
            bw.write("@info");
            // bw.newLine();
            String tableJSON = JSONUtils.getTableJSON(table);
            bw.write(tableJSON);
            bw.newLine();
            bw.newLine();
            
            // data
            bw.write("@data");
            bw.newLine();
            
            CSVPrinter dataPrinter = new CSVPrinter(bw,CSVFormat.DEFAULT.toBuilder().withRecordSeparator("\n").build());
            
            // print the header 
            List<Attribute> attributes = table.getAttributes();
            for(Attribute a : attributes){
            	dataPrinter.print(a.getName() + ":" + a.getType().name());
    		}
            dataPrinter.println();
          
    		//print the tuples
    		for(Tuple t : table.getAllTuplesInOrder()){
    			for(Attribute a : attributes){
    				// System.out.println(a + " : " + t.getAttributeValue(a));
    				dataPrinter.print(t.getAttributeValue(a));
    			}
    			dataPrinter.println();
    		}
    		dataPrinter.close();
            bw.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
	}
	
}
