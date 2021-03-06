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

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import com.nwc.model.oob.OOBModification;

public class SCNParser {
  public static final String FILE_VERSION = "$Revision$ $HeadURL$";
  private File inputFile;
  private File outFile;
  private File logFile;
  
  private HashMap<String, OOBModification> modifications;
  private HashMap<String, String> sqdsPlaced;
  private HashMap<String, HashMap<String, OOBModification>> oobs;

  public SCNParser(File in, File outDir, HashMap<String, HashMap<String, OOBModification>> oobs) {
    this.inputFile = in;
    this.oobs = oobs;
    outDir.mkdirs();
    outFile = new File(outDir, Tools.transformFilename(inputFile.getName()));
    outFile.delete();
    logFile = new File(outDir, Tools.transformFilename(inputFile.getName(), "log"));
    logFile.delete();
    sqdsPlaced = new LinkedHashMap<String, String>();
  }

  public boolean parse() {
    BufferedReader br = null;
    List<String> lines = new ArrayList<String>();
    try {
      br = new BufferedReader(new FileReader(inputFile));
      String line;
      int index = 1;
      while (br.ready()) {
        line = br.readLine();
        if (index == 3) {
          String[] split = line.split(" ");
          DateTime scenStart = new DateTime(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]), Integer.parseInt(split[4]));
          int turns = Tools.transformNumberOfTurns(Integer.parseInt(split[8]), scenStart, 18, 5);
          split[8] = Integer.toString(turns);
          lines.add(StringUtils.join(split, " "));
        } else if (index == 8) {
          if(oobs.containsKey(line.toLowerCase())) {
            lines.add(Tools.transformFilename(line));
            modifications = oobs.get(line.toLowerCase());
          }else {
            // just modify PDT etc
            lines.add(line);
            modifications = new LinkedHashMap<String, OOBModification>();
          }
        } else if (index == 9) {
          lines.add(Tools.transformFilename(line));
        } else if (index < 13) {
          lines.add(line);
        } else if (index > 12) {
          //Starting units
          if (line.startsWith("1 ")) {
            String[] split = line.split(" ");
            String id = split[1];
            int scenarioSize = Integer.parseInt(split[4]);
            OOBModification modification = modifications.get(id);
            if (modification != null) {
              //Squadron
              //int oldOOBSize = Integer.parseInt(modification.getSize());
              //double strength = scenarioSize / oldOOBSize;
              if (split[7].equals("33816576")) {
                int placedMen = 0;
                for(String unit : modification.getNewUnits().keySet()) {
                  if (sqdsPlaced.keySet().contains(unit)) {
                    continue;
                  }
                  int newSize = modification.getNewUnits().get(unit);
                  if (placedMen + newSize >= scenarioSize) {
                    break;
                  }
                  split[1] = unit;
                  split[4] = Integer.toString(newSize);
                  split[7] = "262144";
                  placedMen += newSize;
                  sqdsPlaced.put(unit, split[8] + " " + split[9]);
                  lines.add(StringUtils.join(split, " "));
                }
              } else {
                for(String unit : modification.getNewUnits().keySet()) {
                  if (sqdsPlaced.keySet().contains(unit)) {
                    continue;
                  }
                  int newSize = modification.getNewUnits().get(unit);
                  split[1] = unit;
                  split[4] = Integer.toString(newSize);
                  lines.add(StringUtils.join(split, " "));
                }
              }
            } else {
              lines.add(line);
            }
          } else if (line.startsWith("2 ")) {
            List<String> rows = new ArrayList<String>();
            String[] reinfLine = line.split(" ");
            int units = Integer.parseInt(reinfLine[10]);
            for(int i = 0; i < units; i++) {
              String[] split = br.readLine().trim().split(" ");
              String id = split[0];
              OOBModification modification = modifications.get(id);
              if (modification != null) {
                for(String unit : modification.getNewUnits().keySet()) {
                  if (sqdsPlaced.keySet().contains(unit)) {
                    continue;
                  }
                  int newSize = modification.getNewUnits().get(unit);
                  split[0] = unit;
                  split[3] = Integer.toString(newSize);
                  rows.add("    " + StringUtils.join(split, " "));
                }
              } else {
                rows.add("    " + StringUtils.join(split, " "));
              }
            }
            reinfLine[10] = Integer.toString(rows.size());
            lines.add(StringUtils.join(reinfLine, " "));
            lines.addAll(rows);
          } else if (line.equals("0")) {
            lines.add(line);
            while (br.ready()) {
              line = br.readLine();
              lines.add(line);
            }
          } else {
            lines.add(line);
          }
        }
        index++;
      }

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (br != null) {
        try {
          br.close();
        } catch (IOException e) {
        }
      }
    }
    List<String> linesToRemove = new ArrayList<String>();
    for(String line : lines) {
      for(String id : sqdsPlaced.keySet()) {
        if (line.contains(" " + id + " ") && !line.endsWith(sqdsPlaced.get(id))) {
          linesToRemove.add(line);
        }
      }
    }
//    if (!linesToRemove.isEmpty()) {
//      Tools.writeTextFile(logFile, linesToRemove);
//    }
    lines.removeAll(linesToRemove);
    Tools.writeTextFile(outFile, lines);
    return true;
  }
}
