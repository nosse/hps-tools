/*
 * Copyright 2013 Lawson Software. All Rights Reserved.
 */
package com.nwc.model.oob;

public class SupplyWagons extends OOBEntity {
  public static final String FILE_VERSION = "$Revision$ $HeadURL$";
  String capacity;
  String icon;
  String name;
  String nationality;  
  
  public SupplyWagons(String line, String nationality, String oobId) {
    this.nationality= nationality;
    String trimmed = line.trim();
    String[] split = trimmed.split(" ", 4);
    capacity = split[1];
    icon = split[2];
    name = split[3];    
    this.oobId = oobId;
  }
  public String getLine(boolean printNationality) {
    String base = "";
    if(nationality != null && printNationality) {
      base = nationality + " ";
    }
    return base + "S " + capacity  + " " + icon + " " + name;
  }
  
  public static void main(String[] args) {
//    SupplyWagons supplyWagons = new SupplyWagons("   S 500 35 Wagon/1D/OG  ");
//    System.out.println(supplyWagons.getLine());
  }    
  
  public String getCapacity() {
    return capacity;
  }

  public void setCapacity(String capacity) {
    this.capacity = capacity;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getNationality() {
    return nationality;
  }

  public void setNationality(String nationality) {
    this.nationality = nationality;
  }
}
