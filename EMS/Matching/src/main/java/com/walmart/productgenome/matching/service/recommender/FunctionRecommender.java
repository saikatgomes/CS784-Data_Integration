package com.walmart.productgenome.matching.service.recommender;

import java.util.ArrayList;
import java.util.List;

import com.walmart.productgenome.matching.models.data.Attribute.Type;
import com.walmart.productgenome.matching.models.rules.functions.EuclideanDistanceFunction;
import com.walmart.productgenome.matching.models.rules.functions.Function;
import com.walmart.productgenome.matching.models.rules.functions.Function.ArgType;
import com.walmart.productgenome.matching.models.rules.functions.Jaccard;
import com.walmart.productgenome.matching.models.rules.functions.JaroSimilarityFunction;
import com.walmart.productgenome.matching.models.rules.functions.JaroWinklerSimilarityFunction;
import com.walmart.productgenome.matching.models.rules.functions.LevenshteinSimilarity;
import com.walmart.productgenome.matching.models.rules.functions.MongeElkanSimilarity;
import com.walmart.productgenome.matching.models.rules.functions.NormAbsDiff;
import com.walmart.productgenome.matching.models.rules.functions.SoundexSimilarityFunction;
import com.walmart.productgenome.matching.service.explorer.AttrStats;

public class FunctionRecommender {
  
  // This is a general recommender that just looks at the type.
  public static List<Function> getRecommendedFunctionsForType(List<Function> allFunctions, Type type) {    
    switch (type) {
      // TODO Ask: Are there any features that would work 
      // for boolean, or should they be treated as text?
      case BOOLEAN:
        return getFunctionsForText(allFunctions);
      case FLOAT:
        return getFunctionsForNumeric(allFunctions);
      case INTEGER:
        return getFunctionsForNumeric(allFunctions);
      case LONG:
        return getFunctionsForNumeric(allFunctions);
      case TEXT:
        return getFunctionsForText(allFunctions);
      default:
        return getFunctionsForText(allFunctions);
    }    
  }
  
  private static List<Function> getFunctionsForNumeric(List<Function> allFunctions) {
    List<Function> functions = new ArrayList<Function>();
    
    for (Function function: allFunctions) {
      if (function.getArgType() == ArgType.NUMERIC) {
        functions.add(function);
      }
    }
    
    return functions;
  }
  
  private static List<Function> getFunctionsForText(List<Function> allFunctions) {
    List<Function> functions = new ArrayList<Function>();
    for (Function function: allFunctions) {
      if (function.getArgType() == ArgType.STRING) {
        functions.add(function);
      }
    }
    return functions;
  }
  
  // This recommender looks at type and attribute statistics.
  public static List<Function> getRecFunctionsForAttr(List<Function> allFunctions, AttrStats attrStats) {
    List<Function> functions = new ArrayList<Function>();

    // If attribute type is numeric
    if (attrStats.getType().equals(Type.FLOAT) || attrStats.getType().equals(Type.INTEGER)
        || attrStats.getType().equals(Type.LONG)) {
        functions.addAll(getFunctionsForNumeric(allFunctions));
        // TODO Ask: 
        // - Make sure levenshtein can handle numeric and add here.
        // - Add exact match for numeric.
        //functions.add(new ExactMatch());
        //functions.add(new LevenshteinSimilarity());        
      return functions;
    } else if (attrStats.getType().equals(Type.TEXT)) {
      
      double avgLength = attrStats.getAvgLength();
      double avgNumTokens = attrStats.getAvgNumTokens();
      
      functions.addAll(allFunctions);
      functions.remove(new NormAbsDiff());
      
      // JARO is good only for matching short strings ..
      if(Double.compare(avgLength, 10) > 0) {
        functions.remove(new JaroSimilarityFunction());
        functions.remove(new SoundexSimilarityFunction());
      }
      
      // For long multi-word strings, prefer set-based similarity functions ..
      if(Double.compare(avgLength, 20) > 0 && Double.compare(avgNumTokens, 2) > 0) {
        functions.remove(new JaroWinklerSimilarityFunction());
        functions.remove(new LevenshteinSimilarity());
        functions.remove(new EuclideanDistanceFunction());
        functions.remove(new SoundexSimilarityFunction());
      }
      
      // For single word attributes, set-based similarity functions can be avoided ..
      if(Double.compare(avgNumTokens, 1) <= 0) {
        functions.remove(new Jaccard());
        functions.remove(new MongeElkanSimilarity());
      }
    }
    
    return functions;
  }
}
