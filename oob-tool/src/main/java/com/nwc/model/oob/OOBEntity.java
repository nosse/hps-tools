/*
 * Copyright 2013 Lawson Software. All Rights Reserved.
 */
package com.nwc.model.oob;

public abstract class OOBEntity {
  public static final String FILE_VERSION = "$Revision$ $HeadURL$";
  String oobId;  
  String nationality;
  
  public String getOobId() {
    return oobId;
  }
  
  public void setOobId(String oobId) {
    this.oobId = oobId;
  }

  public String getNationality() {
    return nationality;
  }

  public void setNationality(String nationality) {
    this.nationality = nationality;
  }
  
}
