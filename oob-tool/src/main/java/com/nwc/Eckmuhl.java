/*
 * Copyright 2013 Lawson Software. All Rights Reserved.
 */
package com.nwc;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.nwc.model.oob.Formation;
import com.nwc.model.oob.OOBModification;
import com.nwc.model.oob.Unit;

public class Eckmuhl {
  public static final String FILE_VERSION = "$Revision$ $HeadURL$";
  
  public static void main(String[] args) {
    File baseDir = new File("c:\\oob\\eckmuhl");
    File outDir = new File(baseDir, "out");
    HashMap<String, HashMap<String, OOBModification>> oobs = new LinkedHashMap<String, HashMap<String,OOBModification>>();
    for(File oobFile : baseDir.listFiles()) {
      if(oobFile.getName().endsWith(".oob") && !oobFile.getName().startsWith("Soave")) {
        OOBParser parser = new OOBParser(new File(baseDir, oobFile.getName()), outDir);
        parser.parse();        
        HashMap<String, OOBModification> result = modifyOOB(parser);
        oobs.put(oobFile.getName().toLowerCase(), result);
        parser.printOOB();
      }
    }
    for(File file : baseDir.listFiles()) {
      if (file.getName().endsWith(".scn")) {
        SCNParser scnparser = new SCNParser(file,outDir, oobs);
        scnparser.parse();
      }
    }
  }
  
  public static HashMap<String, OOBModification> modifyOOB(OOBParser oob) {
    HashMap<String, OOBModification> addedUnits = new LinkedHashMap<String, OOBModification>();
    for(Formation formation : oob.formations) {
      createSQDSandDivideUnits(formation, addedUnits);
    }
    List<String> lines = new ArrayList<String>();
    for(OOBModification id : addedUnits.values()) {            
      StringBuilder sb = new StringBuilder(id.getOobId() + ":" + id.getSize());
      for(String entry : id.getNewUnits().keySet()) {
        sb.append(" " + entry + ":" + id.getNewUnits().get(entry));
      }
      lines.add(sb.toString());
    }
    return addedUnits;
  }

  private static void createSQDSandDivideUnits(Formation formation, HashMap<String, OOBModification> addedUnits) {
    for(Formation subFormation : formation.getSubFormations()) {
      createSQDSandDivideUnits(subFormation, addedUnits);
    }
    if (!formation.getUnits().isEmpty()) {
      String oobId = formation.getUnits().get(0).getOobId();
      String baseOOBId = oobId.substring(0, oobId.length() - 1);
      int index = Integer.parseInt(oobId.substring(oobId.length() - 1, oobId.length()));
      List<Unit> newUnits = new ArrayList<Unit>();
      for(Unit unit : formation.getUnits()) {
        HashMap<String, Integer> newIds = new LinkedHashMap<String, Integer>();
        if (unit.isCavalry() && Integer.parseInt(unit.getSize()) > 300) {
          int divider = 4;
          if (unit.getNationality().equals("Austrian")) {
            divider = 6;
            if (unit.getUnitType().equals("L")) {
              divider = 8;
            }
          }
          int unitSize = Integer.parseInt(unit.getSize());
          int rest = unitSize % divider;
          int newSize = unitSize / divider;
          for(int i = 1; i <= divider; i++) {
            Unit squad = new Unit(unit.getLine(), unit.getNationality(), unit.getOobId());
            squad.setName(i + "/" + unit.getName());
            int sqnSize = newSize;
            if (rest > 0) {
              sqnSize = newSize + 1;
              rest--;
            }
            squad.setSize(Integer.toString(sqnSize));
            squad.setOobId(baseOOBId + index);
            newIds.put(baseOOBId + index, sqnSize);
            index++;
            newUnits.add(squad);
          }
          OOBModification mod = new OOBModification(unit.getOobId(), unit.getSize());
          mod.setNewUnits(newIds);
          addedUnits.put(unit.getOobId(), mod);
        } else {
          if (!(baseOOBId + index).equals(unit.getOobId())) {
            OOBModification mod = new OOBModification(unit.getOobId(), unit.getSize());
            newIds.put(baseOOBId + index, Integer.parseInt(unit.getSize()));
            mod.setNewUnits(newIds);
            addedUnits.put(unit.getOobId(), mod);
          }
          unit.setOobId(baseOOBId + index);
          index++;
          newUnits.add(unit);
        }
      }
      formation.setUnits(newUnits);
    }
  }
}
