package com.walmart.productgenome.matching.service.debug;

import java.util.HashMap;
import java.util.Map;

import com.walmart.productgenome.matching.models.audit.ItemPairAudit;
import com.walmart.productgenome.matching.models.audit.MatchStatus;
import com.walmart.productgenome.matching.models.data.Tuple;

public class Debug {
  
  public static MatchingSummary getMatchingSummary(Map<Tuple, ItemPairAudit> itemPairAudits) {
    
    Map<String, Integer> totalMatchesByRule = new HashMap<String, Integer>();
    int totalPairs = itemPairAudits.size();
    
    int totalMatches = 0;
    
    for (ItemPairAudit itemPairAudit: itemPairAudits.values()) {
      
      if (itemPairAudit.getStatus() == MatchStatus.MATCH) {
        totalMatches ++;
      }
      
     // System.out.println("[Debug] ItemPairAudit: " + itemPairAudit);
      for (String ruleName: itemPairAudit.getMatchingRuleNames()) {
        if (totalMatchesByRule.containsKey(ruleName)) {
          totalMatchesByRule.put(ruleName, totalMatchesByRule.get(ruleName) + 1);
        } else {
          totalMatchesByRule.put(ruleName, 1);
        }
      }
    }
    
    return new MatchingSummary(totalPairs, totalMatches, totalMatchesByRule);
    
  }

}
