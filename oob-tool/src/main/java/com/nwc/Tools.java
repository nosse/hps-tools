/*
 * Copyright 2013 Lawson Software. All Rights Reserved.
 */
package com.nwc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Period;

public class Tools {
  public static final String FILE_VERSION = "$Revision$ $HeadURL$";

  public static void writeTextFile(File file, List<String> lines) {
    Path path = Paths.get(file.getPath());
    BufferedWriter writer = null;
    try {
      writer = Files.newBufferedWriter(path, Charset.forName("windows-1252"));
      for(String line : lines) {
        writer.write(line);
        writer.newLine();
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (writer != null) {
        try {
          writer.close();
        } catch (IOException e) {
        }
      }
    }
  }

  public static int transformNumberOfTurns(int turns, DateTime startTime, int hourNight, int hourDay) {
    Period dayTurn = Period.minutes(15);
    Period nightTurn = Period.minutes(60);
    DateTime oldEndTime = startTime;
    for(int i = 2; i <= turns; i++) {
      if (oldEndTime.getHourOfDay() >= hourNight || oldEndTime.getHourOfDay() <= hourDay) {
        oldEndTime = oldEndTime.plus(nightTurn);
      } else {
        oldEndTime = oldEndTime.plus(dayTurn);
      }
    }
    int newTurns = 1;
    DateTime newEndTime = startTime;
    Period newDayTurn = Period.minutes(10);
    Period newNightTurn = Period.minutes(30);
    while (newEndTime.isBefore(oldEndTime)) {
      if (newEndTime.getHourOfDay() >= 18 || newEndTime.getHourOfDay() <= 5) {
        newEndTime = newEndTime.plus(newNightTurn);
      } else {
        newEndTime = newEndTime.plus(newDayTurn);
      }      
      newTurns++;
    }
    return newTurns;
  }
  
  public static String transformFilename(String filename) {
    return transformFilename(filename, null);
  }

  
  public static String transformFilename(String filename, String extension) {
    String postfix = "";
    String prefix = "z_M10_CS";
    String[] split = filename.split("\\.");    
    String ext = split[1]; 
    if(extension != null) {
      ext = extension;
    }
    return prefix + split[0] + postfix + "." + ext;
  }
  
  public static void main(String[] args) {
    Tools.transformNumberOfTurns(20, new DateTime(1809, 4, 20, 12, 0), 18, 5);
  }
}
