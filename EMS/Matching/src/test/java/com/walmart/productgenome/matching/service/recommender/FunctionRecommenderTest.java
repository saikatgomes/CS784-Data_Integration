package com.walmart.productgenome.matching.service.recommender;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.walmart.productgenome.matching.models.data.Attribute;
import com.walmart.productgenome.matching.models.data.Table;
import com.walmart.productgenome.matching.models.rules.functions.CosineSimilarityFunction;
import com.walmart.productgenome.matching.models.rules.functions.Function;
import com.walmart.productgenome.matching.models.rules.functions.NormAbsDiff;
import com.walmart.productgenome.matching.service.MockTable;
import com.walmart.productgenome.matching.service.explorer.AttrComb;
import com.walmart.productgenome.matching.service.explorer.AttrStats;
import com.walmart.productgenome.matching.service.explorer.Explorer;
import com.walmart.productgenome.matching.service.explorer.TableStats;

public class FunctionRecommenderTest {

  Table table;
  TableStats tableStats;
  List<Function> allFunctions;
  Attribute year;
  Attribute title;

  @Before
  public void init() {
    table = new MockTable().getTable();
    tableStats = Explorer.getTableStats(table, 100, 100);
    allFunctions = new ArrayList<Function>();
    allFunctions.add(new CosineSimilarityFunction());
    allFunctions.add(new NormAbsDiff());
    year = table.getAttributeByName("year");
    title = table.getAttributeByName("title");
  }
  
  @Test
  public void testNumericRecommendation(){
    assertTrue(FunctionRecommender.getRecommendedFunctionsForType(
        allFunctions, year.getType()).contains(new NormAbsDiff()));
    assertFalse(FunctionRecommender.getRecommendedFunctionsForType(
        allFunctions, year.getType()).contains(new CosineSimilarityFunction()));
  }
  
  @Test
  public void testStringRecommendation(){
    assertFalse(FunctionRecommender.getRecommendedFunctionsForType(
        allFunctions, title.getType()).contains(new NormAbsDiff()));
    assertTrue(FunctionRecommender.getRecommendedFunctionsForType(
        allFunctions, title.getType()).contains(new CosineSimilarityFunction()));
  }
  
  @Test
  public void testNumericAttributeRecommendation() {
    AttrStats attrStats = tableStats.getAttrStats(new AttrComb(year));
    assertTrue(FunctionRecommender.getRecFunctionsForAttr(
        allFunctions, attrStats).contains(new NormAbsDiff()));
    assertFalse(FunctionRecommender.getRecFunctionsForAttr(
        allFunctions, attrStats).contains(new CosineSimilarityFunction()));
  }
  
  @Test
  public void testStringAttributeRecommendation() {
    AttrStats attrStats = tableStats.getAttrStats(new AttrComb(title));
    assertFalse(FunctionRecommender.getRecFunctionsForAttr(
        allFunctions, attrStats).contains(new NormAbsDiff()));
    assertTrue(FunctionRecommender.getRecFunctionsForAttr(
        allFunctions, attrStats).contains(new CosineSimilarityFunction()));
  }
}
