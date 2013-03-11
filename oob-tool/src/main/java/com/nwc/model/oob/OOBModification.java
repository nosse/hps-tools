/*
 * Copyright 2013 Lawson Software. All Rights Reserved.
 */
package com.nwc.model.oob;

import java.util.HashMap;

public class OOBModification {
  public static final String FILE_VERSION = "$Revision$ $HeadURL$";
  private String oobId;
  private String size;
  private HashMap<String, Integer> newUnits;

  public OOBModification(String oobId, String size) {
    this.oobId = oobId;
    this.size = size;
  }
  public String getOobId() {
    return oobId;
  }
  public void setOobId(String oobId) {
    this.oobId = oobId;
  }
  public String getSize() {
    return size;
  }
  public void setSize(String size) {
    this.size = size;
  }
  public HashMap<String, Integer> getNewUnits() {
    return newUnits;
  }
  public void setNewUnits(HashMap<String, Integer> newUnits) {
    this.newUnits = newUnits;
  }
}
