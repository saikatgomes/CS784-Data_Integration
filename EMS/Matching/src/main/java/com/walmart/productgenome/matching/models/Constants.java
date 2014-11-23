package com.walmart.productgenome.matching.models;

public class Constants { 

	//public static final String ROOT_DIR = "/Users/fpanahi/Desktop/workspace/tmp/ems/";
	public static final String ROOT_DIR = "./ems/";
	//public static final String ROOT_DIR = "/home/fpanahi/Matching/ems/";
	

	/* Global Functions */
	public static final String GLOBAL_FUNCTIONS_FILE = ROOT_DIR + "global/all.functions";
	
	public static final Object MISSING_VALUE = null;
	
	/* File Extensions */
	public static final String PROJECT_EXTENSION = ".project";
	public static final String TABLE_EXTENSION = ".table";
	public static final String MATCHER_EXTENSION = ".matcher";
	public static final String EVALUATION_SUMMARY_EXTENSION = ".evalsum";
	
	/* File names */
	public static final String PROJECT_FUNCTIONS_FILE_NAME = "all.functions";
	public static final String PROJECT_FEATURES_FILE_NAME = "all.features";
	public static final String PROJECT_RULES_FILE_NAME = "all.rules";
	public static final String PROJECT_MATCHERS_FILE_NAME = "all.matchers";
	public static final String GLOBAL_FUNCTIONS_FILE_NAME = "global.functions";
	
	/* Rules */
	public static final String OPERAND_SEPARATOR = ",";
	public static final String TERM_SEPARATOR = " AND ";
	public static final String RULE_SEPARATOR = " OR ";
  
}
