/*
 * Copyright (c) 2018 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.om.log;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.collection.AbstractIterator;
import org.eclipse.net4j.util.collection.CloseableIterator;
import org.eclipse.net4j.util.concurrent.Worker;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.log.RollingLog.LogLine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Eike Stepper
 * @since 3.8
 */
public class RollingLog extends Worker implements Log, Iterable<LogLine>
{
  private final String logFile;

  private final long logSize;

  private final AtomicLong logLineCounter = new AtomicLong(0);

  private int fileNumber;

  private boolean fileAppend;

  private List<LogLine> queue = new ArrayList<LogLine>();

  public RollingLog(String logFile, long logSize, boolean append)
  {
    this.logFile = logFile;
    this.logSize = logSize;
    fileAppend = append;

    setDaemon(true);
  }

  public final String getLogFile()
  {
    return logFile;
  }

  public final long getLogSize()
  {
    return logSize;
  }

  public final void log(String message)
  {
    LogLine logLine = createLogLine(message);

    synchronized (this)
    {
      logLine.id = logLineCounter.incrementAndGet();
      queue.add(logLine);
      notifyAll();
    }
  }

  @Override
  protected final void work(WorkContext context) throws Exception
  {
    List<LogLine> logLines;
    synchronized (this)
    {
      if (queue.isEmpty())
      {
        try
        {
          wait(100);
        }
        catch (InterruptedException ex)
        {
          context.terminate();
        }

        context.nextWork();
      }

      logLines = queue;
      queue = new ArrayList<LogLine>();
    }

    writeLogLines(logLines);
  }

  protected LogLine createLogLine(String message)
  {
    long millis = System.currentTimeMillis();
    String thread = getThreadInfo();

    return new LogLine(millis, thread, message);
  }

  protected void writeLogLines(List<LogLine> logLines)
  {
    if (logFile != null)
    {
      PrintStream out = null;

      try
      {
        File file = getFile();

        out = new PrintStream(new FileOutputStream(file, fileAppend));
        writeLogLines(logLines, out);
        out.close();
      }
      catch (IOException ex)
      {
        OM.LOG.error(ex);
      }
      finally
      {
        fileAppend = true;
        IOUtil.closeSilent(out);
      }
    }
    else
    {
      writeLogLines(logLines, System.out);
    }
  }

  protected void writeLogLines(List<LogLine> logLines, PrintStream out)
  {
    for (LogLine logLine : logLines)
    {
      writeLogLine(logLine, out);
    }
  }

  protected void writeLogLine(LogLine logLine, PrintStream out)
  {
    out.println(logLine);
  }

  protected String getThreadInfo()
  {
    return Thread.currentThread().getName();
  }

  @Override
  protected void doActivate() throws Exception
  {
    if (fileAppend)
    {
      int number = getLastFileNumber();
      if (number == -1)
      {
        fileAppend = false;
      }
      else
      {
        long lastID = 0;

        for (LogIterator iterator = new LogIterator(logFile, number); iterator.hasNext();)
        {
          LogLine logLine = iterator.next();
          lastID = logLine.getID();
        }

        logLineCounter.set(lastID);
      }
    }

    super.doActivate();
  }

  private int getLastFileNumber()
  {
    int lastFileNumber = -1;
    for (int i = 0; i < Integer.MAX_VALUE; i++)
    {
      File file = getFile(logFile, i);
      if (!file.isFile())
      {
        break;
      }

      lastFileNumber = i;
    }

    return lastFileNumber;
  }

  private File getFile()
  {
    File file;

    for (;;)
    {
      file = getFile(logFile, fileNumber);

      if (fileAppend && file.length() > logSize)
      {
        fileNumber++;
        fileAppend = false;
        continue;
      }

      break;
    }
    return file;
  }

  private static File getFile(String logFile, int fileNumber)
  {
    return new File(logFile + String.format("-%04d", fileNumber) + ".txt");
  }

  public final CloseableIterator<LogLine> iterator()
  {
    return iterator(logFile);
  }

  public static CloseableIterator<LogLine> iterator(String logFile)
  {
    return new LogIterator(logFile, 0);
  }

  /**
   * @author Eike Stepper
   */
  private static final class LogIterator extends AbstractIterator<LogLine> implements CloseableIterator<LogLine>
  {
    private static final int CLOSED = -1;

    private final String logFile;

    private int fileNumber;

    private BufferedReader reader;

    public LogIterator(String logFile, int fileNumber)
    {
      this.logFile = logFile;
      this.fileNumber = fileNumber;
    }

    @Override
    protected Object computeNextElement()
    {
      if (fileNumber == CLOSED)
      {
        return END_OF_DATA;
      }

      if (reader == null)
      {
        File file = getFile(logFile, fileNumber++);
        if (file.isFile())
        {
          try
          {
            reader = new BufferedReader(new FileReader(file));
          }
          catch (FileNotFoundException ex)
          {
            OM.LOG.error(ex);
            return END_OF_DATA;
          }
        }
        else
        {
          return END_OF_DATA;
        }
      }

      try
      {
        String string = reader.readLine();
        if (string == null)
        {
          reader.close();
          reader = null;
          return computeNextElement();
        }

        return new LogLine(string);
      }
      catch (IOException ex)
      {
        OM.LOG.error(ex);
        return END_OF_DATA;
      }
    }

    public void close()
    {
      IOUtil.close(reader);
      reader = null;
      fileNumber = CLOSED;
    }

    public boolean isClosed()
    {
      return fileNumber == CLOSED;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class LogLine
  {
    private static final String NL = "\n\r";

    private static final String TAB = "\t";

    private static final String TABNL = TAB + NL;

    private long id;

    private final long millis;

    private final String thread;

    private final String message;

    public LogLine(long millis, String thread, String message)
    {
      this.millis = millis;
      this.thread = StringUtil.translate(thread, TABNL, "   ");
      this.message = StringUtil.translate(message, NL, "  ");
    }

    public LogLine(String string)
    {
      StringTokenizer tokenizer = new StringTokenizer(string, TAB);
      id = Long.parseLong(tokenizer.nextToken());
      millis = Long.parseLong(tokenizer.nextToken());
      thread = tokenizer.nextToken();
      message = tokenizer.nextToken("").substring(1);
    }

    public long getID()
    {
      return id;
    }

    public long getMillis()
    {
      return millis;
    }

    public String getThread()
    {
      return thread;
    }

    public String getMessage()
    {
      return message;
    }

    @Override
    public String toString()
    {
      return id + TAB + millis + TAB + thread + TAB + message;
    }
  }
}
