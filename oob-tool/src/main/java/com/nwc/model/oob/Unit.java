/*
 * Copyright 2013 Lawson Software. All Rights Reserved.
 */
package com.nwc.model.oob;

public class Unit extends OOBEntity {
  public static final String FILE_VERSION = "$Revision$ $HeadURL$";
  String size;
  String morale;
  String unitType;
  String weaponCode;
  Integer _2Dicon;
  String _3Dicon;
  String name;

  public Unit(String line, String nationality,  String oobId) {
    String trimmed = line.trim();
    String[] split = trimmed.split(" ", 8);
    size = split[1];
    morale = split[2];
    unitType = split[3];
    weaponCode = split[4];
    _2Dicon = Integer.parseInt(split[5]);
    _3Dicon = split[6];
    name = split[7];
    this.oobId = oobId;
    this.nationality = nationality;
  }

  public String getLine(){
    return "U " + size + " " + morale + " " + unitType + " " + weaponCode + " " + _2Dicon + " " + _3Dicon + " " + name;
  }
  
  public boolean isCavalry() {
    if(unitType.equals("L") || unitType.equals("D") || unitType.equals("H")|| unitType.equals("K")){
      return true;      
    }
    return false;
  }
  
  public boolean isArtillery() {
    if(unitType.equals("A") || unitType.equals("B") || unitType.equals("C")|| unitType.equals("E")){
      return true;
    }
    return false;
  }
  
  public boolean isInfantry() {
    if(unitType.equals("I") || unitType.equals("M") || unitType.equals("V")|| unitType.equals("G")|| unitType.equals("R")|| unitType.equals("T")|| unitType.equals("U") || unitType.equals("F")|| unitType.equals("S")|| unitType.equals("P")){
      return true;
    }
    return false;
  }
  
  public static void main(String[] args) {
//    Unit unit = new Unit("U 8 7 A B 23 0 1 Co Gd Foot Art.");
//    System.out.println(unit.getLine());
  }
  
  public String getSize() {
    return size;
  }

  public void setSize(String size) {
    this.size = size;
  }

  public String getMorale() {
    return morale;
  }

  public void setMorale(String morale) {
    this.morale = morale;
  }

  public String getUnitType() {
    return unitType;
  }

  public void setUnitType(String unitType) {
    this.unitType = unitType;
  }

  public String getWeaponCode() {
    return weaponCode;
  }

  public void setWeaponCode(String weaponCode) {
    this.weaponCode = weaponCode;
  }

  public Integer get_2Dicon() {
    return _2Dicon;
  }

  public void set_2Dicon(Integer _2Dicon) {
    this._2Dicon = _2Dicon;
  }

  public String get_3Dicon() {
    return _3Dicon;
  }

  public void set_3Dicon(String _3Dicon) {
    this._3Dicon = _3Dicon;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
