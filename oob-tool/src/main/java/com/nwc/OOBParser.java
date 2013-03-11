/*
 * Copyright 2013 Lawson Software. All Rights Reserved.
 */
package com.nwc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.nwc.model.oob.Formation;
import com.nwc.model.oob.Leader;
import com.nwc.model.oob.OOBModification;
import com.nwc.model.oob.SupplyWagons;
import com.nwc.model.oob.Unit;

public class OOBParser {
  public static final String FILE_VERSION = "$Revision$ $HeadURL$";
  List<Formation> formations = new ArrayList<Formation>();
  List<SupplyWagons> wagons = new ArrayList<SupplyWagons>();

  private File inputFile;
  private File newOOBFile;
  private File changesFile;

  public static final String[] Nationalities = new String[]{ "French", "British", "Prussian", "Austrian", "Russian", "Swedish" };
  String version;

  public OOBParser(File inputFile) {
    this.inputFile = inputFile;
    newOOBFile = new File(inputFile.getParentFile(), Tools.transformFilename(inputFile.getName()));
    newOOBFile.delete();
    changesFile = new File(inputFile.getParentFile(), Tools.transformFilename(inputFile.getName(), "obc"));
    changesFile.delete();
  }

  public void parse() {
    BufferedReader br;
    try {
      br = new BufferedReader(new FileReader(inputFile));
      version = br.readLine().trim();
      String line;
      String nationality = null;
      String id = "";
      int index = 1;
      while ((line = br.readLine()) != null) {
        String trimmed = line.trim();
        for(int i = 0; i < Nationalities.length; i++) {
          nationality = Nationalities[i];
          if (line.startsWith(nationality)) {
            trimmed = line.substring(nationality.length() + 1);
            break;
          }
        }
        if (trimmed.startsWith("A")) {
          formations.add(new Formation(nationality, trimmed, br, id + index));
          index++;
        } else if (trimmed.startsWith("S")) {
          wagons.add(new SupplyWagons(trimmed, nationality, id + index));
          index++;
        }
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public HashMap<String, OOBModification> modifyOOB() {
    HashMap<String, OOBModification> addedUnits = new LinkedHashMap<String, OOBModification>();
    for(Formation formation : formations) {
      createSQDSandDivideUnits(formation, addedUnits);
    }
    List<String> lines = new ArrayList<>();
    for(OOBModification id : addedUnits.values()) {            
      StringBuilder sb = new StringBuilder(id.getOobId() + ":" + id.getSize());
      for(String entry : id.getNewUnits().keySet()) {
        sb.append(" " + entry + ":" + id.getNewUnits().get(entry));
      }
//      System.out.println(sb.toString());
      lines.add(sb.toString());
    }
    Tools.writeTextFile(changesFile, lines);
    return addedUnits;
  }

  private void createSQDSandDivideUnits(Formation formation, HashMap<String, OOBModification> addedUnits) {
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
        if (unit.isCavalry()) {
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

//  private void printOOBIDs() {
//    List<String> lines = new ArrayList<String>();
//    String padding = "";
//    for(Formation formation : formations) {
//      printOOBEntityIds(formation, null, padding, lines);
//    }
//    for(SupplyWagons sw : wagons) {
//      lines.add(sw.getOobId() + " " + sw.getName());
//    }
//    writeTextFile(newOOBFile, lines);
//  }
//  private void printOOBEntityIds(Formation formation, String nationality, String padding, List<String> lines) {
//    lines.add(padding + formation.getOobId() + " " + formation.getName());
//    padding = padding + "\t";
//    for(Leader leader : formation.getLeaders()) {
//      lines.add(padding + leader.getOobId() + " " + leader.getName());
//    }
//    for(Formation subFormation : formation.getSubFormations()) {
//      printOOBEntityIds(subFormation, nationality, padding, lines);
//    }
//    for(Unit unit : formation.getUnits()) {
//      lines.add(padding + unit.getOobId() + " " + unit.getName());
//    }
//    for(SupplyWagons sw : formation.getWagons()) {
//      lines.add(padding + sw.getOobId() + " " + sw.getName());
//    }
//  }

  public void printOOB() {
    List<String> lines = new ArrayList<String>();
    lines.add(version);
    String padding = "";
    for(Formation formation : formations) {
      printOOBEntity(formation, null, padding, lines, true);
    }
    for(SupplyWagons sw : wagons) {
      lines.add(sw.getLine(true));
    }
    Tools.writeTextFile(newOOBFile, lines);
  }

  private void printOOBEntity(Formation formation, String nationality, String padding, List<String> lines, boolean printNationality) {
    lines.add(padding + formation.getLine(printNationality));
    padding = padding + "\t";
    lines.add(padding + "Begin");
    for(Leader leader : formation.getLeaders()) {
      lines.add(padding + leader.getLine());
    }
    for(Formation subFormation : formation.getSubFormations()) {
      printOOBEntity(subFormation, nationality, padding, lines, false);
    }
    for(Unit unit : formation.getUnits()) {
      lines.add(padding + unit.getLine());
    }
    for(SupplyWagons sw : formation.getWagons()) {
      lines.add(padding + sw.getLine(false));
    }
    lines.add(padding + "End");
  }

  public static void main(String[] args) {
    OOBParser parser = new OOBParser(new File("c:\\oob\\eckmuhl.oob"));
    parser.parse();
    parser.modifyOOB();
    //parser.printOOBIDs();
    parser.printOOB();
  }

}
