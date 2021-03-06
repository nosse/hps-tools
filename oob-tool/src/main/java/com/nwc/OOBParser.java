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
import java.util.List;

import com.nwc.model.oob.Formation;
import com.nwc.model.oob.Leader;
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

  public OOBParser(File inputFile, File outDir) {
    this.inputFile = inputFile;
    outDir.mkdirs();
    newOOBFile = new File(outDir, Tools.transformFilename(inputFile.getName()));
    newOOBFile.delete();
    changesFile = new File(outDir, Tools.transformFilename(inputFile.getName(), "obc"));
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

  public void printOOBIDs() {
    List<String> lines = new ArrayList<String>();
    String padding = "";
    for(Formation formation : formations) {
      printOOBEntityIds(formation, null, padding, lines);
    }
    for(SupplyWagons sw : wagons) {
      lines.add(sw.getOobId() + " " + sw.getName());
    }
    Tools.writeTextFile(newOOBFile, lines);
  }
  private void printOOBEntityIds(Formation formation, String nationality, String padding, List<String> lines) {
    lines.add(padding + formation.getOobId() + " " + formation.getName());
    padding = padding + "\t";
    for(Leader leader : formation.getLeaders()) {
      lines.add(padding + leader.getOobId() + " " + leader.getName());
    }
    for(Formation subFormation : formation.getSubFormations()) {
      printOOBEntityIds(subFormation, nationality, padding, lines);
    }
    for(Unit unit : formation.getUnits()) {
      lines.add(padding + unit.getOobId() + " " + unit.getName());
    }
    for(SupplyWagons sw : formation.getWagons()) {
      lines.add(padding + sw.getOobId() + " " + sw.getName());
    }
  }

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
    OOBParser parser = new OOBParser(new File("c:\\oob\\eckmuhl.oob"), new File("c:\\oob\\eckmuhl\\out"));
    parser.parse();
    parser.printOOBIDs();
    parser.printOOB();
  }

}
