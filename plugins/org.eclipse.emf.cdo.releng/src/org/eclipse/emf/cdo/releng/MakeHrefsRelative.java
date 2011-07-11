/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class MakeHrefsRelative
{
  public static void main(String[] args) throws IOException
  {
    String javadocFolder = args[0];
    System.out.println("Making HREFs relative in " + new File(".").getCanonicalPath() + "/" + javadocFolder);
    makeFolderRelative(new File(javadocFolder), "../..");
  }

  private static void makeFolderRelative(File folder, String prefix) throws IOException
  {
    File[] children = folder.listFiles();
    for (File file : children)
    {
      if (file.isDirectory())
      {
        if (!file.getName().equals(".svn"))
        {
          makeFolderRelative(file, prefix + "/..");
        }
      }
      else
      {
        if (file.getName().endsWith(".html"))
        {
          makeFileRelative(file, prefix);
        }
      }
    }
  }

  private static void makeFileRelative(File file, String prefix) throws IOException
  {
    FileReader in = null;
    int modifiedLines = 0;
    List<String> lines = new ArrayList<String>();

    try
    {
      in = new FileReader(file);
      BufferedReader reader = new BufferedReader(in);

      String line;
      while ((line = reader.readLine()) != null)
      {
        if (line.indexOf("MAKE-RELATIVE") != -1)
        {
          line = line.replace("MAKE-RELATIVE", prefix);
          ++modifiedLines;
        }

        lines.add(line);
      }
    }
    finally
    {
      if (in != null)
      {
        in.close();
      }
    }

    if (modifiedLines != 0)
    {
      System.out.println("Modified: " + file.getPath() + " (" + modifiedLines + ")");
      FileWriter out = null;

      try
      {
        out = new FileWriter(file);
        BufferedWriter writer = new BufferedWriter(out);

        for (String line : lines)
        {
          writer.write(line);
          writer.write("\n");
        }

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
