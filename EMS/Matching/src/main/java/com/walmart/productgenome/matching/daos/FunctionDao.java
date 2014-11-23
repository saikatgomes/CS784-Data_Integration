package com.walmart.productgenome.matching.daos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.walmart.productgenome.matching.models.data.Project;
import com.walmart.productgenome.matching.models.rules.functions.Function;

public class FunctionDao {

	private static Map<String, List<String>> projectFunctionMap = new HashMap<String, List<String>>();
    private static Map<String, Map<String, Function>> functionCache = new HashMap<String, Map<String, Function>>();
    
    static {
        List<Project> projects = ProjectDao.findAll();
        for(Project p : projects) {
            projectFunctionMap.put(p.getName(), p.getFunctionNames());
            functionCache.put(p.getName(), new HashMap<String, Function>());
        }
    }
}
