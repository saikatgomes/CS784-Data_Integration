package com.walmart.productgenome.matching.service.explorer;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.walmart.productgenome.matching.Constants;
import com.walmart.productgenome.matching.daos.TableDao;
import com.walmart.productgenome.matching.models.data.Attribute;
import com.walmart.productgenome.matching.models.data.Attribute.Type;
import com.walmart.productgenome.matching.models.data.Table;

public class AttributeSelectorTest {

  Table table;
  TableStats tableStats;

  @Before
  public void readTable() throws IOException {
    String csvFilePath =  Constants.BOOKS_WALMART_PATH;
    table = TableDao.importFromCSVWithHeader("Products", "walmart", csvFilePath);
    tableStats = ExplorerDriver.getTableStats(table, 0, 0);
  }
  
  @Test
  public void getBestAttrCombs() {
        
    AttributeSelector attrSelector = new AttributeSelector(table, tableStats, 100, 100, 3);
    attrSelector.getBestAttrCombs();
        
    Attribute isbn = new Attribute("isbn", Type.TEXT);
    Attribute author = new Attribute("author", Type.TEXT);
    Attribute binding = new Attribute("binding", Type.TEXT);
    
    System.out.println(tableStats.printLayeredCombs()); 
    
   // assertTrue(bestCombsLayered.get(2).get(0).contains(author));
   // assertTrue(bestCombsLayered.get(3).get(0).contains(binding));

  }
  
//  @Test
//  public void getAttrStatForCombTest() {
//    
//    AttributeSelector attrSelector = new AttributeSelector(table, tableStats, 3);
//
//    Attribute author = new Attribute("author", Type.TEXT);
//    Attribute pages = new Attribute("pages", Type.TEXT);
//    
//    List<Attribute> attrs = new ArrayList<Attribute>();
//    attrs.add(author);
//    attrs.add(pages);
//    
//    ICombinatoricsVector<Attribute> comb = Factory.createVector(attrs);
//    
//    AttrStats attrStat = attrSelector.getAttrStatForComb(comb);
//    
//    System.out.println(attrStat);
//    
//  }
//  
//  @Test
//  public void getBestAttrsTest() {
//    
//    AttributeSelector attrSelector = new AttributeSelector(table, tableStats, 1);
//    //List<Attribute> bestAttrs = attrSelector.getBestAttrs();
//    Attribute title = new Attribute("isbn", Type.TEXT);
//    
//    //assertTrue(bestAttrs.contains(title));
//    
//  }
//  
//  @Test
//  public void getWorstAttrsTest() {
//    
//    AttributeSelector attrSelector = new AttributeSelector(table, tableStats, 1);
//    List<Attribute> worstAttrs = attrSelector.getWorstAttrs();
//    Attribute volume = new Attribute("volume", Type.TEXT);
//    
//    assertTrue(worstAttrs.contains(volume));
//    
//  }
//  
//  @Test
//  public void getAttrStatForCombTest() {
//    
//    Attribute title = new Attribute("title", Type.TEXT);
//    Attribute pages = new Attribute("pubYear", Type.TEXT);
//    
//    List<Attribute> attrs = new ArrayList<Attribute>();
//    attrs.add(title);
//    attrs.add(pages);
//    
//    AttributeSelector attrSelector = new AttributeSelector(table, tableStats, 2);
//
//   // AttrStats attrStat = attrSelector.getAttrStatForComb(attrs);
//    
//   // System.out.println(attrStat);
//    
//  }
//  
//  @Test
//  public void zipTest() {
//    
//    List<Object> a = new ArrayList<Object>() {{
//      add("Fatemah");
//      add("Majid");
//    }};
//    
//    List<Object> b = new ArrayList<Object>() {{
//      add(1);
//      add(2);
//    }};
//    
//    List<List<Object>> lists = new ArrayList<List<Object>>();
//    lists.add(a);
//    lists.add(b);
//    
//    List<List<Object>> zipped = AttributeSelector.zip(lists);
//    
//    assertTrue(zipped.get(0).get(0).equals("Fatemah"));
//    assertTrue(zipped.get(0).get(1).equals(1));
//  
//  }
//  
//  @Test
//  public void genAttrCombsTest() {
//    AttributeSelector attrSelector = new AttributeSelector(table, tableStats, 2);
//    attrSelector.genAttrCombs(2);
//  }
  
}
