package com.walmart.productgenome.matching.service.explorer;

import java.io.IOException;

import org.junit.Test;

import com.walmart.productgenome.matching.Constants;
import com.walmart.productgenome.matching.daos.TableDao;
import com.walmart.productgenome.matching.models.data.Attribute;
import com.walmart.productgenome.matching.models.data.Attribute.Type;
import com.walmart.productgenome.matching.models.data.Table;

public class ExplorerInegrationTest {
  
  Table table;

  @Test
  public void tableReadTest() throws IOException {
    String csvFilePath =  Constants.BOOKS_BOWKER_PATH;
    table = TableDao.importFromCSVWithHeader("Products", "bowker", csvFilePath);
  }
  
  @Test
  public void getAttrStatsTest() throws IOException {
    // TODO: to be completed.
    tableReadTest();
    Attribute attr = new Attribute("upc", Type.TEXT);
    
    AttrStats attrStats = Explorer.getAttrStats(table, attr, 7, 7); // show all frequencies.
    System.out.println(attrStats);
    
  }

}
