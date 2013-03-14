/*
 * Copyright 2013 Lawson Software. All Rights Reserved.
 */
package com.nwc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class PDTParser {
  public static final String FILE_VERSION = "$Revision$ $HeadURL$";
  private File inputFile;
  private File outFile;
  private File weaponOut;

  public PDTParser(File inputFile, File outDir) {
    this.inputFile = inputFile;
    outDir.mkdirs();
    outFile = new File(outDir, Tools.transformFilename(inputFile.getName()));
    weaponOut = new File(outDir, "weapon.dat");
  }

  public void parse() {
    InputStream is = getClass().getClassLoader().getResourceAsStream("pdt.template");
    BufferedReader sourceReader = null;
    BufferedReader templateReader = null;
    try {
      List<String> result = new ArrayList<String>();
      List<String> source = new ArrayList<String>();
      sourceReader = new BufferedReader(new FileReader(inputFile));
      templateReader = new BufferedReader(new InputStreamReader(is, "windows-1252"));
      while (templateReader.ready()) {
        result.add(templateReader.readLine());
      }
      while (sourceReader.ready()) {
        source.add(sourceReader.readLine());
      }
      //Fix first lines
      result.set(1, source.get(1));
      result.set(2, source.get(2));
      result.set(3, source.get(3));
      boolean found = false;
      for(int i=0; i< source.size(); i++){
        String line = source.get(i);
        if(found) {
          result.add(line);
        }
        if(line.equals("0") && i > 38) {
          found = true;
        }
      }
      
      
      Tools.writeTextFile(outFile, result);
      is = getClass().getClassLoader().getResourceAsStream("weapon.dat");
      BufferedReader weaponReader = new BufferedReader(new InputStreamReader(is, "windows-1252"));
      List<String> lines = new ArrayList<String>();
      while (weaponReader.ready()) {
        lines.add(weaponReader.readLine());
      }
      Tools.writeTextFile(weaponOut, lines);

    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (sourceReader != null) {
        try {
          sourceReader.close();
        } catch (IOException e) {
        }
      }
      if (templateReader != null) {
        try {
          templateReader.close();
        } catch (IOException e) {
        }
      }
    }

  }
}
