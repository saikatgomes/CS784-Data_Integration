package com.walmart.productgenome.matching.service.explorer;

import org.junit.Before;
import org.junit.Test;

import com.walmart.productgenome.matching.models.data.Attribute;
import com.walmart.productgenome.matching.models.data.Table;
import com.walmart.productgenome.matching.service.MockTable;

public class ExplorerTest {

  Table table;
  
  @Before
  public void init() {
    table = new MockTable().getTable();
  }
  
  @Test
  public void testGetAttrStats() {
    for (Attribute attr : table.getAttributes()){
      AttrStats attrStats = Explorer.getAttrStats(table, attr, 
          (int)table.getSize(), (int)table.getSize()); // show all frequencies.
      System.out.println(attrStats);
    }
  }

}
