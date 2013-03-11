/*
 * Copyright 2013 Lawson Software. All Rights Reserved.
 */
package com.nwc.model.oob;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Formation extends OOBEntity {
  public static final String FILE_VERSION = "$Revision$ $HeadURL$";
  String type;
  String name;

  List<Formation> subFormations = new ArrayList<Formation>();
  List<Leader> leaders = new ArrayList<Leader>();
  List<Unit> units = new ArrayList<Unit>();
  List<SupplyWagons> wagons = new ArrayList<SupplyWagons>();
  private BufferedReader br;
  List<String> formationTypes = Arrays.asList(new String[]{ "A", "D", "B", "W", "C" });

  public Formation(String nationality, String line, BufferedReader br, String unitId) {
    this.nationality = nationality;
    this.br = br;
    String trimmed = line.trim();
    String[] split = trimmed.split(" ", 2);
    setOobId(unitId);
    if (split.length > 1) {
      type = split[0];
      name = split[1];
    }
    parse();
  }

  private void parse() {
    String line;
    int index = 1;
    try {
      while ((line = br.readLine().trim()) != null) {
        if (line.isEmpty()) {
        } else if (line.startsWith("Begin")) {

        } else if (line.startsWith("End")) {
          return;
        } else if (line.startsWith("L")) {
          leaders.add(new Leader(line, nationality, oobId + "." + index));
          index++;
        } else if (line.startsWith("U")) {
          units.add(new Unit(line, nationality, oobId + "." + index));
          index++;
        } else if (line.startsWith("S")) {
          wagons.add(new SupplyWagons(line, nationality, oobId + "." + index));
          index++;
        } else {
          if (line.length() > 0) {
            String type = line.substring(0, 1);
            if (formationTypes.contains(type)) {
              subFormations.add(new Formation(nationality, line, br, oobId + "." + index));
              index++;
            }
          } else
            return;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String getLine( boolean printNationality) {
    String base = "";
    if (nationality != null && printNationality) {
      base = nationality + " ";
    }
    return base + type + " " + name;
  }

  public void addLeader(Leader leader) {
    leaders.add(leader);
  }
  public void addUnit(Unit unit) {
    units.add(unit);
  }
  public void addFormation(Formation formation) {
    subFormations.add(formation);
  }

  public static void main(String[] args) {
//    Formation formation = new Formation("   W I. Armee-Korps    ", null);
//    System.out.println(formation.getLine());
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
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

  public List<Formation> getSubFormations() {
    return subFormations;
  }

  public void setSubFormations(List<Formation> subFormations) {
    this.subFormations = subFormations;
  }

  public List<Leader> getLeaders() {
    return leaders;
  }

  public void setLeaders(List<Leader> leaders) {
    this.leaders = leaders;
  }

  public List<Unit> getUnits() {
    return units;
  }

  public void setUnits(List<Unit> units) {
    this.units = units;
  }

  public List<SupplyWagons> getWagons() {
    return wagons;
  }

  public void setWagons(List<SupplyWagons> wagons) {
    this.wagons = wagons;
  }
}
