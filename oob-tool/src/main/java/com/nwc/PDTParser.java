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
  public PDTParser(File inputFile, File out) {
    InputStream is = getClass().getClassLoader().getResourceAsStream("pdt.template");
    try {
      List<String> lines = new ArrayList<String>();
      BufferedReader src = new BufferedReader(new FileReader(inputFile));
      BufferedReader template = new BufferedReader(new InputStreamReader(is, "windows-1252"));      
      int index = 1;
      while(src.ready()) {
        String line = src.readLine();
        index++;
      }      
    } catch (UnsupportedEncodingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
  }
  
  public static void main(String[] args) {
    new PDTParser(null, null);
  }

}
