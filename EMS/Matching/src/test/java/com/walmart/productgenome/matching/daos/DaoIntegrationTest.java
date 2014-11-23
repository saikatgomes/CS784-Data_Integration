package com.walmart.productgenome.matching.daos;

import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.walmart.productgenome.matching.Constants;
import com.walmart.productgenome.matching.models.DefaultType;
import com.walmart.productgenome.matching.models.audit.ItemPairAudit;
import com.walmart.productgenome.matching.models.data.Project;
import com.walmart.productgenome.matching.models.data.Table;
import com.walmart.productgenome.matching.models.data.Tuple;
import com.walmart.productgenome.matching.models.rules.Feature;
import com.walmart.productgenome.matching.models.rules.Matcher;
import com.walmart.productgenome.matching.models.rules.Rule;
import com.walmart.productgenome.matching.models.rules.functions.Function;

/*
 * Integration test for Daos.
 * Creates project, table
 * Generates candset
 * Imports functions, features, rules
 * Generates matches
 * Performs matching and saves matching result.
 */

public class DaoIntegrationTest {

  @Test
  public void daoIntegrationTest() {
    
    String projectName = "books_test";
    String table1Name = "bowker";
    String table2Name = "walmart";
    
    Project project = new Project(projectName,"Walmart-Bowker");
    ProjectDao.save(project);
    
    Table table1 = null;
    Table table2 = null;
    Table candSet = null;
    String attr1Name = "isbn";
    String attr2Name = "isbn";
    String candsetName = "isbn_cand_set";
    String matcherName = "matcher";
    String matchesName = "matches";
    String[] table1Attributes = {};
    String[] table2Attributes = {};
    
    try {
      
      table1 = TableDao.importFromCSVWithHeader(projectName, table1Name, Constants.BOOKS_BOWKER_PATH);
      table2 = TableDao.importFromCSVWithHeader(projectName, table2Name, Constants.BOOKS_WALMART_PATH);
      TableDao.save(table1, DefaultType.TABLE1);
      TableDao.save(table2, DefaultType.TABLE2);
      
      candSet = BlockingDao.block(projectName, table1Name, table2Name,
          attr1Name, attr2Name, candsetName, table1Attributes, table2Attributes);
      TableDao.save(candSet, DefaultType.CAND_SET);
      
      List<Function> functions = RuleDao.importFunctionsFromCSVWithHeader(
          Constants.BOOKS_FUNCTIONS_PATH);
      RuleDao.importFunctions(projectName, functions);
      
      List<Feature> features = RuleDao.importFeaturesFromCSVWithHeader(project, table1, table2, Constants.BOOKS_FEATURES_PATH);
      RuleDao.save(projectName, features);

      List<Rule> rules = RuleDao.importRulesFromCSVWithHeader(project, table1Name, table2Name, Constants.BOOKS_RULES_PATH);
      
      Matcher matcher = new Matcher(matcherName, projectName, table1Name,
           table2Name, rules);
      RuleDao.save(projectName, matcher);
            
      Map<Tuple, ItemPairAudit> itemPairAudits = new HashMap<Tuple, ItemPairAudit>();
      
      Table matches = MatchingDao.match(projectName, candsetName,
          matcherName, matchesName, itemPairAudits);
      TableDao.save(matches, DefaultType.MATCHES);
     
      assertTrue(matches.getSize() == 20);

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (SecurityException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InstantiationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
