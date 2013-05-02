/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.gitbash.repository;

import org.eclipse.emf.cdo.releng.gitbash.GitBash;

import org.eclipse.swt.widgets.Shell;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class GitCopyrightsAction extends AbstractRepositoryAction
{
  private static final String BEGIN_COMMIT = "--BEGIN-COMMIT--";

  private static final String BEGIN_MESSAGE = "--BEGIN-MESSAGE--";

  // "--BEGIN-COMMIT--
  // committer date
  // message (multiline)
  // --BEGIN-SUMMARY--
  // summary (multiline)"
  private static final String OUTPUT_FORMAT = BEGIN_COMMIT + "%n%ci%n%B%n" + BEGIN_MESSAGE + "%n";

  @Override
  protected void run(Shell shell, File workTree) throws Exception
  {
    // File outFile = File.createTempFile("copyrights-", ".tmp");
    File outFile = new File("/develop/copyrights.txt");

    try
    {
      GitBash.quiet = true;
      GitBash.executeCommand(shell, workTree, "git log --name-only --format=\"" + OUTPUT_FORMAT + "\" > \"/"
          + outFile.getAbsoluteFile().toString().replace(":", "").replace("\\", "/") + "\"");
    }
    finally
    {
      GitBash.quiet = false;
    }

    parseFile(outFile);
  }

  private void parseFile(File file) throws FileNotFoundException, IOException
  {
    Map<String, int[]> years = new HashMap<String, int[]>();

    FileReader fileReader = new FileReader(file);
    BufferedReader bufferedReader = new BufferedReader(fileReader);

    try
    {
      LogEntry logEntry;

      // Start of file. First line has to be "--BEGIN-COMMIT--"
      String line = bufferedReader.readLine();
      if (line == null)
      {
        return; // Empty log
      }

      if (!line.equals(BEGIN_COMMIT))
      {
        throw new IllegalStateException("Read unexpected line " + line + " at beginning of file "
            + file.getAbsolutePath());
      }

      // First line successfully read. Start processing of log entries:

      processing: //
      for (;;)
      {
        String date = readLineSafe(bufferedReader);
        logEntry = new LogEntry(date);

        // Follows the message until the summary marker is read
        StringBuilder builder = new StringBuilder();
        while (!(line = readLineSafe(bufferedReader)).equals(BEGIN_MESSAGE))
        {
          builder.append(line);
          builder.append("\n");
        }

        logEntry.setMessage(builder.toString());

        summaryReading: //
        for (;;)
        {
          line = bufferedReader.readLine();
          if (line == null)
          {
            handleLogEntry(years, logEntry);
            break processing; // End of file reached
          }

          if (line.equals(BEGIN_COMMIT))
          {
            handleLogEntry(years, logEntry);
            break summaryReading; // End of summary section reached
          }

          if (line.trim().length() == 0)
          {
            continue; // Read over empty lines
          }

          // We are in the summary section. Read line should contain a path
          logEntry.getPaths().add(line);
        }
      }
    }
    finally
    {
      bufferedReader.close();
      fileReader.close();
      file.delete();
    }
  }

  private String readLineSafe(BufferedReader bufferedReader) throws IOException
  {
    String result = bufferedReader.readLine();
    if (result == null)
    {
      throw new IllegalStateException("Unexpected end of stream");
    }

    return result;
  }

  private void handleLogEntry(Map<String, int[]> years, LogEntry logEntry)
  {
    if (logEntry.getMessage().equalsIgnoreCase("Update copyrights\n"))
    {
      return;
    }

    int year = Integer.parseInt(logEntry.getDate().substring(0, 4));

    for (String path : logEntry.getPaths())
    {
      handlePath(years, year, path);
    }
  }

  private void handlePath(Map<String, int[]> years, int year, String path)
  {
    int[] array = years.get(path);
    if (array == null)
    {
      years.put(path, new int[] { year });
    }
    else
    {
      for (int i = 0; i < array.length; i++)
      {
        if (array[i] == year)
        {
          return;
        }
      }

      int[] newArray = new int[array.length + 1];
      System.arraycopy(array, 0, newArray, 0, array.length);
      newArray[array.length] = year;

      years.put(path, newArray);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class LogEntry
  {
    private String date;

    private String message;

    private List<String> paths = new ArrayList<String>();

    public LogEntry(String date)
    {
      this.date = date;
    }

    public String getDate()
    {
      return date;
    }

    public void setDate(String date)
    {
      this.date = date;
    }

    public String getMessage()
    {
      return message;
    }

    public void setMessage(String message)
    {
      this.message = message;
    }

    public List<String> getPaths()
    {
      return paths;
    }
  }
}
