package com.walmart.productgenome.matching.service.explorer;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.ICombinatoricsVector;

import com.walmart.productgenome.matching.models.data.Attribute;
import com.walmart.productgenome.matching.models.data.Attribute.Type;

public class AttrCombTest {

  @Test
  public void attrCombTest() {
    Attribute isbn = new Attribute("isbn", Type.TEXT);
    Attribute author = new Attribute("author", Type.TEXT);
    Attribute binding = new Attribute("binding", Type.TEXT);
    
    List<Attribute> attrs = new ArrayList<Attribute>();
    attrs.add(isbn);
    attrs.add(author);
    attrs.add(binding);
    
    ICombinatoricsVector<Attribute> initialVector = Factory.createVector(attrs);
    
    List<Attribute> newAttrs = (List<Attribute>) initialVector;
    
    System.out.println(newAttrs);

  }

}
