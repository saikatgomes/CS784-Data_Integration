package daos;

import java.io.IOException;

import models.data.Project;
import models.data.Table;

public class Driver {
	
	public static void main(String[] args) throws IOException{
		// create an empty project
		String projectName = "MyProject";
		String projectDescription = "My first project";
		Project p = new Project(projectName, projectDescription);
		
		// save the project
		ProjectDao.save(p);
		
		// now go look into your Constants.ROOT_DIR directory
		
		// import tables from CSV files
		String table1Name = "bowker";
		String table2Name = "walmart";
		String table1Path = "data/books/bowker.csv";
		String table2Path = "data/books/walmart.csv";
		Table table1 = TableDao.importFromCSVWithHeader(projectName, table1Name,
				table1Path);
		Table table2 = TableDao.importFromCSVWithHeader(projectName, table2Name,
				table2Path);
		
		// save the tables - this automatically updates and saves the project
		TableDao.save(table1);
		TableDao.save(table2);
		
		// again go and look into the Constants.ROOT_DIR directory
	}
	
}
