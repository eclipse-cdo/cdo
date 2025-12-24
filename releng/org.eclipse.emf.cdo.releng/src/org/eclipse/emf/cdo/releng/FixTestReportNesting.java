/*
 * Copyright (c) 2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Eike Stepper
 */
public class FixTestReportNesting
{
  private static final String NL = System.getProperty("line.separator");

  private static final Pattern TESTCASE = Pattern.compile("<testcase name=\"([^\"]*)\" classname=\"([^\"]*)\" time=\"([^\"]*)\"(/?>)");

  private static StringBuilder builder;

  private static String lastClassname;

  private static String scenario;

  private static String artificialClassname;

  private static File folder;

  private static int tests;

  private static int errors;

  private static int failures;

  private static float times;

  public static void main(String[] args) throws Exception
  {
    folder = new File(args[0]);

    File[] files = folder.listFiles();
    if (files != null)
    {
      for (File file : files)
      {
        String name = file.getName();
        if (name.startsWith("TEST-") && name.endsWith(".xml"))
        {
          fix(file);
        }
      }
    }
  }

  private static void fix(File file) throws IOException
  {
    String path = file.getAbsolutePath();
    scenario = path.substring(0, path.length() - ".xml".length());
    scenario = scenario.substring(scenario.lastIndexOf('.') + 1);

    BufferedReader reader = null;

    try
    {
      reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

      String line;
      while ((line = reader.readLine()) != null)
      {
        String trimmed = line.trim();
        if (trimmed.startsWith("<testcase name="))
        {
          Matcher matcher = TESTCASE.matcher(trimmed);
          if (matcher.matches())
          {
            String name = matcher.group(1);
            String classname = matcher.group(2);
            String time = matcher.group(3);
            String ending = matcher.group(4);

            times += Float.parseFloat(time);
            ++tests;

            if (!classname.equals(lastClassname))
            {
              if (lastClassname != null)
              {
                finishTestSuite();
              }
            }

            lastClassname = classname;
            artificialClassname = scenario + "." + lastClassname.substring(lastClassname.lastIndexOf('.') + 1);

            if (builder == null)
            {
              builder = new StringBuilder();
            }

            writeln("  <testcase name=\"" + name + "\" classname=\"" + classname + "\" time=\"" + time + "\"" + ending);
            continue;
          }
        }
        else if (trimmed.startsWith("<error message="))
        {
          ++errors;
        }
        else if (trimmed.startsWith("<failure message="))
        {
          ++failures;
        }
        else if (trimmed.startsWith("</testsuite>"))
        {
          continue;
        }

        writeln(line);
      }

      if (lastClassname != null)
      {
        finishTestSuite();
      }
    }
    finally
    {
      if (reader != null)
      {
        reader.close();
      }
    }
  }

  private static void finishTestSuite() throws IOException
  {
    if (builder != null)
    {
      String text = builder.toString();
      builder = null;

      File tmp = new File(folder, "singles");
      tmp.mkdirs();

      BufferedWriter writer = null;

      try
      {
        writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(tmp, "TEST-" + artificialClassname + ".xml"))));
        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        writer.write(NL);
        writer.write("<testsuite name=\"" + artificialClassname + "\" tests=\"" + tests + "\" errors=\"" + errors + "\" failures=\"" + failures + "\" time=\""
            + times + "\">");
        writer.write(NL);
        writer.write(text);
        writer.write("</testsuite>");
        writer.write(NL);
      }
      finally
      {
        if (writer != null)
        {
          writer.close();
        }
      }

      lastClassname = null;
      tests = 0;
      errors = 0;
      failures = 0;
      times = 0.0f;
    }
  }

  private static void writeln(String line) throws IOException
  {
    if (builder != null)
    {
      builder.append(line);
      builder.append(NL);
    }
  }
}
