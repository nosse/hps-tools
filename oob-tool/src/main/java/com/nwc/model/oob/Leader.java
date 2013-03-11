/*
 * Copyright 2013 Lawson Software. All Rights Reserved.
 */
package com.nwc.model.oob;

public class Leader extends OOBEntity {
  public static final String FILE_VERSION = "$Revision$ $HeadURL$";
  private String command;
  private String leadership;
  private String iconNumber;
  private String name;

  public Leader(String line, String nationality, String oobId) {
    String trimmed = line.trim();
    String[] split = trimmed.split(" ", 5);
    command = split[1];
    leadership = split[2];
    iconNumber = split[3];
    name = split[4];
    this.oobId = oobId;
    this.nationality = nationality;
  }

  public String getLine() {
    return "L " + command + " " + leadership + " " + iconNumber + " " + name;
  }

  public static void main(String[] args) {
//    Leader leader = new Leader("   L 3 4 21 GD Tilly   ");
//    System.out.println(leader.getLine());
  }
  
  public String getCommand() {
    return command;
  }

  public void setCommand(String command) {
    this.command = command;
  }

  public String getLeadership() {
    return leadership;
  }

  public void setLeadership(String leadership) {
    this.leadership = leadership;
  }

  public String getIconNumber() {
    return iconNumber;
  }

  public void setIconNumber(String iconNumber) {
    this.iconNumber = iconNumber;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
