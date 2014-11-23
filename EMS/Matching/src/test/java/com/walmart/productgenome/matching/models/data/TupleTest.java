package com.walmart.productgenome.matching.models.data;

import org.junit.Test;

import com.walmart.productgenome.matching.models.data.Attribute.Type;

public class TupleTest {

  @Test
  public void equalsTest() {
    Attribute attr1 = new Attribute("id1", Type.TEXT);
    Attribute attr2 = new Attribute("id2", Type.TEXT);
    
    Tuple Tuple1 = new Tuple();
    Tuple1.setAttributeValue(attr1, "12345");
    Tuple1.setAttributeValue(attr2, "34567");
    
    Tuple Tuple2 = new Tuple();
    Tuple2.setAttributeValue(attr1, "12345");
    Tuple2.setAttributeValue(attr2, "34567");
    
    Tuple Tuple3 = new Tuple();
    Tuple3.setAttributeValue(attr1, "12345");
    Tuple3.setAttributeValue(attr2, "34566");
    
    assert(Tuple1.equals(Tuple2) == true);    
    assert(Tuple1.equals(Tuple3) == false);
  }

}
