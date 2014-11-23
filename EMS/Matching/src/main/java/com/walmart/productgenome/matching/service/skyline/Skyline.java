package com.walmart.productgenome.matching.service.skyline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Skyline {
  
 // TODO: modify code so that sorting is not done at every iteration.
 public static List<Object> getHintTuples(Map<Object, List<Float>> features, SimSelection selection, int simCount) {
   
   List<Object> tuples = getMostLeastSim(features, selection);
      
   // get more if not enough
   while(tuples.size() < simCount && features.size() > 0) {
     // remove the returned pairs from the previous round.
     for (Object t: tuples) {
       features.remove(t);
     }
     tuples.addAll(getMostLeastSim(features, selection));
   }
   
   // truncate if too many
   if (tuples.size() > simCount) {
     for (int i = tuples.size() - 1; i >= simCount; i--) {
       tuples.remove(i);
     }
     
   }
   
   return tuples;   
 }
 
 public static List<Object> getMostLeastSim(Map<Object, List<Float>> features, SimSelection selection) {
   boolean mostSimilar = false;
   
   if (selection == SimSelection.MOST_SIM) {
     mostSimilar = true;
   }

   // TODO: How expensive is this conversion?
   List<Map.Entry<Object, List<Float>>> vectors = new ArrayList<Map.Entry<Object, List<Float>>>();
   vectors.addAll(features.entrySet());
   
   List<Map.Entry<Object, List<Float>>> sortedVectors = sortVectorsBySum(vectors, mostSimilar);
   List<Object> selectedPairs = new ArrayList<Object>();
   List<List<Float>> selectedVectors = new ArrayList<List<Float>>();
   
   for (Map.Entry<Object, List<Float>> sortedVector: sortedVectors){
     
     boolean dominated = false;
     
     // TODO: can we stop earlier and not iterate through all sorted vectors?
     for (List<Float> selectedVector: selectedVectors){
       // If a vector like me was not dominated
               // I also will not be dominated.
       if (selectedVector.equals(sortedVector)) break;
       // If I am dominated
       if (dominant(selectedVector, sortedVector.getValue(), mostSimilar)){
         dominated = true;
         break;
       }
     }     
     // Not dominated by any of the skyline vectors
           // thus it is a skyline.
     if (!dominated){
       selectedPairs.add(sortedVector.getKey());
       selectedVectors.add(sortedVector.getValue());
     }     
   }
   
   ArrayList<Object> hintPairs = new ArrayList<Object>();
   hintPairs.addAll(selectedPairs);
   
   return hintPairs;
 }

 
 private static boolean dominant(List<Float> vector1, List<Float> vector2, 
             boolean mostSimilar) {
     boolean necessary = false;
     
     // Go through all the dimensions and compare      
     for (int i = 0; i < vector1.size(); i++) {
       Float v1 = vector1.get(i);
       Float v2 = vector2.get(i);
       
     if (mostSimilar){
           if (!(v1 >= v2)) return false;
           if (v1 > v2) necessary = true;
     } else {
       if (!(v1 <= v2)) return false;
         if (v1 < v2) necessary = true;
     }
   }        
     // If it meets the necessary condition return True
     // else False
     if (necessary) return true;
     
     return false;
 }
 
 private static List<Map.Entry<Object, List<Float>>> sortVectorsBySum(
     List<Map.Entry<Object, List<Float>>> vectors, boolean mostSimilar) {
   
   // This sort is ascending.
   Comparator<Map.Entry<Object, List<Float>>> vectorComparator = new Comparator<Map.Entry<Object, List<Float>>>() {
     public int compare(Map.Entry<Object, List<Float>> a, Map.Entry<Object, List<Float>> b) {
           if (getVectorSum(a.getValue()) < getVectorSum(b.getValue())) return -1;
           else if (getVectorSum(a.getValue()) > getVectorSum(b.getValue())) return 1;
           else return 0;
    }
    
   };
    
   if (mostSimilar) {
     //sort descending
     Comparator<Map.Entry<Object, List<Float>>>  reversedVectorComparator = 
         Collections.reverseOrder(vectorComparator);
     Collections.sort(vectors, reversedVectorComparator);
   } else {     
     Collections.sort(vectors, vectorComparator);
   }
   return vectors;
 }
 
 private static Float getVectorSum(List<Float> vector) {
    Float sum = 0f;
    for (Float v: vector) {
      sum += v;
    }
    return sum;
  }
 
 public enum SimSelection {
   MOST_SIM, LEAST_SIM
 }

}
