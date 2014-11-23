package com.walmart.productgenome.matching.service.explorer;

import static org.junit.Assert.assertTrue;

import java.text.DecimalFormat;

import org.junit.Before;
import org.junit.Test;


public class FrequencyAnalyzerTest {
  
  FrequencyAnalyzer frequencyAnalyzer;
  
  @Before
  public void init() {
    frequencyAnalyzer = new FrequencyAnalyzer();
    frequencyAnalyzer.addValue("Fatemah");
    frequencyAnalyzer.addValue("Fatemah");
    
    frequencyAnalyzer.addValue("Hossein");
    frequencyAnalyzer.addValue("Hossein");
    frequencyAnalyzer.addValue("Hossein");

    frequencyAnalyzer.addValue("Majid");
    frequencyAnalyzer.addValue("");
  }
 
  @Test
  public void testGetTotalValues() {
    assertTrue(frequencyAnalyzer.getTotalValues() == 7);
  }
  
  @Test
  public void testGetTotalMissing() {
    assertTrue(frequencyAnalyzer.getTotalMissing() == 1);
  }
  
  @Test
  public void testGetTotalUnique() {
    assertTrue(frequencyAnalyzer.getTotalUnique() == 2);
  }
  
  @Test
  public void testGetTotalDistinct() {
    assertTrue(frequencyAnalyzer.getTotalDistinct() == 4);
  }
		
	@Test
  public void testGetFrequencyForValue() {
    assertTrue(frequencyAnalyzer.getFrequencyForValue("Hossein") == 3);
  }
	
	@Test
  public void testGetValuesWithFreq() {
	  assertTrue(frequencyAnalyzer.getValuesWithFreq(1).contains("Majid"));
    assertTrue(frequencyAnalyzer.getValuesWithFreq(2).contains("Fatemah"));
  }
	
	@Test
  public void testGetTopKFrequencies() {
	  assertTrue(frequencyAnalyzer.getTopKFrequencies(2, 2).containsKey("Hossein"));
	  assertTrue(frequencyAnalyzer.getTopKFrequencies(2, 2).containsKey("Fatemah"));
  }
	
	@Test
	public void testGetAvgNumTokens() {
	  assertTrue(frequencyAnalyzer.getAvgNumTokens() == 1.0);
	}
	
	@Test
	public void testGetAvgLength() {
	  DecimalFormat df = new DecimalFormat("#.##");
	  assertTrue(df.format(frequencyAnalyzer.getAvgLength()).equals("5.71"));
	}
		
}
