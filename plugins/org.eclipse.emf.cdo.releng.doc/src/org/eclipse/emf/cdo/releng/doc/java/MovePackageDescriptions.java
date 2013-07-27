/*
 * Copyright (c) 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.doc.java;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Eike Stepper
 */
public class MovePackageDescriptions
{
  private static final String NL = System.getProperty("line.separator");

  private static final Pattern PATTERN = Pattern.compile("(.*</H2>\\s*)" //
      + "(.*<B>See:</B>.*<A HREF=\"#package_description\"><B>Description</B></A>)" // To be replaced...
      + "(.*)" //
      + "(<A NAME=\"package_description\"><!-- --></A><H2>.*</H2>.*<P>\\s*)" //
      + "(.*)" // ... with full description
      + "(<P>\\s*<P>\\s*<DL>\\s*</DL>\\s*<HR>.*)", //
      Pattern.MULTILINE | Pattern.DOTALL);

  public static void main(String[] args) throws IOException
  {
    String javadocFolder = args[0];
    System.out.println();
    System.out.println("Moving package descriptions in " + new File(javadocFolder).getCanonicalPath());

    movePackageDescriptionsInFolder(new File(javadocFolder));
  }

  private static void movePackageDescriptionsInFolder(File folder) throws IOException
  {
    File[] children = folder.listFiles();
    for (File file : children)
    {
      String name = file.getName();
      if (file.isDirectory())
      {
        if (!name.equals(".svn"))
        {
          movePackageDescriptionsInFolder(file);
        }
      }
      else
      {
        if (name.equals("package-summary.html"))
        {
          movePackageDescription(file);
        }
      }
    }
  }

  private static void movePackageDescription(File file) throws IOException
  {
    FileReader in = null;
    String contents;

    try
    {
      in = new FileReader(file);
      char[] buffer = new char[(int)file.length()];
      in.read(buffer);
      contents = new String(buffer);
    }
    finally
    {
      if (in != null)
      {
        in.close();
      }
    }

    Matcher matcher = PATTERN.matcher(contents);
    if (matcher.matches())
    {
      System.out.println("Modified: " + file.getPath());

      String keepProlog = matcher.group(1);
      String keepMiddle = matcher.group(3);
      String replaceWith = matcher.group(5);
      String keepEpilog = matcher.group(6);

      FileWriter out = null;

      try
      {
        out = new FileWriter(file);
        @SuppressWarnings("resource")
        BufferedWriter writer = new BufferedWriter(out);
        writer.write(keepProlog);
        writer.write("<A NAME=\"package_description\"><!-- --></A>" + NL);
        writer.write(replaceWith);
        writer.write(keepMiddle);
        writer.write(keepEpilog);
        writer.flush();
      }
      finally
      {
        if (out != null)
        {
          out.close();
        }
      }
    }
  }
}
