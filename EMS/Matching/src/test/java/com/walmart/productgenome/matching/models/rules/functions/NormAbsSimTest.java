package com.walmart.productgenome.matching.models.rules.functions;

import static org.junit.Assert.*;

import org.junit.Test;

public class NormAbsSimTest {

  @Test
  public void test() {
    String[] args = new String[2];
    args[0] = "2.0";
    args[1] = "2.0";
    Float score = new NormAbsSim("name", "desc").compute(args);
    assertTrue(score == 1.0f);
  }

}
