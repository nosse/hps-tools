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

  public PDTParser(File inputFile) {
    this.inputFile = inputFile;
  }

  private void parse() {
    InputStream is = getClass().getClassLoader().getResourceAsStream("pdt.template");
    BufferedReader sourceReader = null;
    BufferedReader templateReader = null;
    try {
      List<String> template = new ArrayList<String>();
      List<String> source = new ArrayList<String>();
      sourceReader = new BufferedReader(new FileReader(inputFile));
      templateReader = new BufferedReader(new InputStreamReader(is, "windows-1252"));
      for(int i = 1; i < 38; i++) {
        template.add(templateReader.readLine());
        source.add(sourceReader.readLine());
      }
      //Fix first lines
      template.set(1, source.get(1));
      template.set(2, source.get(2));
      template.set(3, source.get(3));
      template.set(7, source.get(7));
      for(String line : template) {
        System.out.println(line);
      }
    } catch (UnsupportedEncodingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
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

  public static void main(String[] args) {
    new PDTParser(new File("c:\\oob\\frenchfirst.pdt")).parse();
  }

}
