package com.walmart.productgenome.matching.service.explorer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.paukov.combinatorics.ICombinatoricsVector;

import com.walmart.productgenome.matching.models.data.Attribute;
import com.google.common.base.Objects;

/*
 * Defines an attribute combination.
 * Used for AttributeSelector which recommends 
 * attribute combinations for matching.
 */

public class AttrComb implements Iterable<Attribute> {
  
  List<Attribute> attrs;
  
  public AttrComb(ICombinatoricsVector<Attribute> attrs) {
    
    this.attrs = new ArrayList<Attribute>();
    
    for (Attribute attr: attrs) {
      this.attrs.add(attr);
    }
    
  }

  public AttrComb(Attribute attr) {
    attrs = new ArrayList<Attribute>();
    attrs.add(attr);
  }

  public Iterator<Attribute> iterator() {
   return attrs.iterator();
  }

  public boolean contains(Attribute attr) {
    return attrs.contains(attr);
  }
  
  public Attribute get(int index) {
    return attrs.get(index);
  }
  
  public List<Attribute> getAttrs() {
    return attrs;
  }
  
  public String getAttrCombName() { 
    String text = "";
    String delimiter = ""; 
    for (Attribute attr: attrs) {
      text += delimiter + attr.getName();
      delimiter = ", ";
    }
    return text;
  }
  
  public String getAttrCombType() {
    String text = "";
    String delimiter = ""; 
    for (Attribute attr: attrs) {
      text += delimiter + attr.getType();
      delimiter = ", ";
    }
    return text;
  }
  
  public int size() {
    return attrs.size();
  }

  @Override
  public String toString() {
  	return Objects.toStringHelper(this)
  		.add("attrs", attrs)
  		.toString();
  }
  
  @Override
  public int hashCode(){
    return Objects.hashCode(attrs);
  }
  
  @Override
  public boolean equals(Object object){
    // Note: Do not change the logic of the equals.
    if (!(object instanceof AttrComb)) {
      return false;
    }
    
    AttrComb that = (AttrComb) object;
    
    for (Attribute attr: this.attrs) {
      if (!that.attrs.contains(attr)) {
        return false;
      }
    }
    return true;
  }
 
}
