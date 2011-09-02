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
import java.util.Arrays;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class SortListItems
{
  private static final String PREFIX = "<LI TYPE=\"circle\">";

  private static final String SUFFIX = "</UL>";

  public static void main(String[] args) throws IOException
  {
    String javadocFolder = args[0];
    System.out.println();
    System.out.println("Sorting list items in " + new File(".").getCanonicalPath() + "/" + javadocFolder);

    sortListItemsInFolder(new File(javadocFolder));
  }

  private static void sortListItemsInFolder(File folder) throws IOException
  {
    File[] children = folder.listFiles();
    for (File file : children)
    {
      String name = file.getName();
      if (file.isDirectory())
      {
        if (!name.equals(".svn"))
        {
          sortListItemsInFolder(file);
        }
      }
      else
      {
        if (name.equals("package-tree.html") || name.equals("overview-tree.html"))
        {
          sortListItems(file);
        }
      }
    }
  }

  private static void sortListItems(File file) throws IOException
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
        if (line.startsWith(PREFIX) && line.endsWith(SUFFIX))
        {
          String trimmed = line.trim();
          String truncated = trimmed.substring(PREFIX.length(), line.length() - SUFFIX.length());
          String[] listItems = truncated.split(PREFIX);

          if (listItems.length > 1)
          {
            Arrays.sort(listItems);

            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < listItems.length; i++)
            {
              String listItem = listItems[i];
              builder.append(PREFIX);
              builder.append(listItem);
            }

            builder.append("</UL>");

            String result = builder.toString();
            if (!line.equals(result))
            {
              line = result;
              ++modifiedLines;
            }
          }
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
    }

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
