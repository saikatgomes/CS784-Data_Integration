package com.walmart.productgenome.matching.service.skyline;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.walmart.productgenome.matching.models.data.Attribute;
import com.walmart.productgenome.matching.models.data.Attribute.Type;
import com.walmart.productgenome.matching.models.data.Tuple;
import com.walmart.productgenome.matching.service.skyline.Skyline.SimSelection;

public class SkylineTest {

  @Test
  public void testSkyline() {
    
    Attribute id1 = new Attribute("id1", Type.TEXT);
    Attribute id2 = new Attribute("id2", Type.TEXT);
    
    Tuple tuple1 = new Tuple();
    tuple1.setAttributeValue(id1, "111");
    tuple1.setAttributeValue(id2, "222");
    
    Tuple tuple2 = new Tuple();
    tuple2.setAttributeValue(id1, "333");
    tuple2.setAttributeValue(id2, "444");
    
    Tuple tuple3 = new Tuple();
    tuple3.setAttributeValue(id1, "555");
    tuple3.setAttributeValue(id2, "666");
    
    Tuple tuple4 = new Tuple();
    tuple4.setAttributeValue(id1, "777");
    tuple4.setAttributeValue(id2, "888");
    
    List<Double> vector1 = new ArrayList<Double>(){{
      add(1.0);
      add(1.0);
    }};
    
    List<Double> vector2 = new ArrayList<Double>(){{
      add(1.0);
      add(0.5);
    }};
    
    List<Double> vector3 = new ArrayList<Double>(){{
      add(1.0);
      add(0.8);
    }};
    
    List<Double> vector4 = new ArrayList<Double>(){{
      add(0.9);
      add(0.65);
    }};
    
    Map<Object, List<Double>> generatedFeatures = new HashMap<Object, List<Double>>();
    generatedFeatures.put(tuple1, vector1);
    generatedFeatures.put(tuple2, vector2);
    generatedFeatures.put(tuple3, vector3);
    generatedFeatures.put(tuple4, vector4);
    
    List<Object> mostSimilar = Skyline.getHintTuples(generatedFeatures, SimSelection.MOST_SIM, 1);
    assertTrue(mostSimilar.contains(tuple1));
        
    mostSimilar = Skyline.getHintTuples(generatedFeatures, SimSelection.MOST_SIM, 2);
    assertTrue(mostSimilar.contains(tuple3));
    
    // The returned pairs are ordered by sum of their features. So if I ask for 1, I should get
    // the one with least sum even though both tuple2, tuple1 are least similar and returned in one
    // round.
    List<Object> mostDifferent = Skyline.getHintTuples(generatedFeatures, SimSelection.LEAST_SIM, 1);
    assertTrue(mostDifferent.contains(tuple2));
        
    mostDifferent = Skyline.getHintTuples(generatedFeatures, SimSelection.LEAST_SIM, 2);
    assertTrue(mostDifferent.contains(tuple4));
    
  }

}
