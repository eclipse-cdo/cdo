/*
 * Copyright (c) 2018, 2019 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.log.RollingLog.LogLine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Eike Stepper
 * @since 3.8
 */
public class RollingLog extends Worker implements Log, Iterable<LogLine>
{
  private static final String PROP_FILE_NUMBER = "RollingLog.fileNumber";

  private static final String PROP_LOG_LINE_COUNTER = "RollingLog.logLineCounter";

  private final String logFile;

  private final long logSize;

  private final AtomicLong logLineCounter = new AtomicLong(0);

  private final AtomicLong lastWrittenID = new AtomicLong(-1);

  private int fileNumber;

  private boolean fileAppend;

  private long writeInterval = 100L;

  private boolean writeBulk = true;

  private List<LogLine> queue = new ArrayList<>();

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

  public final long getLogLineCounter()
  {
    return logLineCounter.get();
  }

  public final int getFileNumber()
  {
    return fileNumber;
  }

  public long getWriteInterval()
  {
    return writeInterval;
  }

  public void setWriteInterval(long writeInterval)
  {
    this.writeInterval = writeInterval;
  }

  public boolean isWriteBulk()
  {
    return writeBulk;
  }

  public void setWriteBulk(boolean writeBulk)
  {
    this.writeBulk = writeBulk;
  }

  @Override
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

  public final void commit() throws InterruptedException
  {
    long id;
    synchronized (this)
    {
      id = logLineCounter.get();
    }

    synchronized (lastWrittenID)
    {
      while (lastWrittenID.get() < id)
      {
        lastWrittenID.wait(100L);
      }
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
          wait(writeInterval);
        }
        catch (InterruptedException ex)
        {
          context.terminate();
        }

        context.nextWork();
      }

      if (writeBulk)
      {
        logLines = queue;
        queue = new ArrayList<>();
      }
      else
      {
        logLines = new ArrayList<>();
        logLines.add(queue.remove(0));
      }
    }

    long lastID = writeLogLines(logLines);
    if (lastID != -1)
    {
      synchronized (lastWrittenID)
      {
        lastWrittenID.set(lastID);
        lastWrittenID.notifyAll();
      }
    }
  }

  protected LogLine createLogLine(String message)
  {
    long millis = System.currentTimeMillis();
    String thread = getThreadInfo();

    return new LogLine(millis, thread, message);
  }

  protected long writeLogLines(List<LogLine> logLines)
  {
    if (logFile != null)
    {
      PrintStream out = null;

      try
      {
        File file = getCurrentLogFile();

        out = new PrintStream(new FileOutputStream(file, fileAppend));
        return writeLogLines(logLines, out);
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
      return writeLogLines(logLines, System.out);
    }

    return -1;
  }

  protected long writeLogLines(List<LogLine> logLines, PrintStream out)
  {
    long lastID = -1;
    for (LogLine logLine : logLines)
    {
      writeLogLine(logLine, out);
      lastID = logLine.getID();
    }

    return lastID;
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
      File propertiesFile = getPropertiesFile();
      if (propertiesFile.isFile())
      {
        load(propertiesFile);
        propertiesFile.delete();
      }
      else
      {
        init();
      }
    }

    super.doActivate();
    log("Log activated");
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    log("Log deactivated");
    commit();

    File propertiesFile = getPropertiesFile();
    save(propertiesFile);

    super.doDeactivate();
  }

  protected void recovery(Properties properties, LogLine logLine)
  {
    // Do nothing.
  }

  protected void load(Properties properties)
  {
    // Do nothing.
  }

  protected void save(Properties properties)
  {
    // Do nothing.
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

  private File getCurrentLogFile()
  {
    File file;

    for (;;)
    {
      file = getFile(logFile, fileNumber);

      if (fileAppend && file.length() > logSize)
      {
        fireEvent(new SplitEvent(this, file, fileNumber));

        fileNumber++;
        fileAppend = false;
        continue;
      }

      break;
    }
    return file;
  }

  private File getPropertiesFile()
  {
    return new File(getLogFile() + ".properties");
  }

  private void init()
  {
    int number = getLastFileNumber();
    if (number == -1)
    {
      fileAppend = false;
    }
    else
    {
      recover(number);
    }
  }

  private void recover(int lastFileNumber)
  {
    Properties properties = new Properties();
    IListener[] listeners = getListeners();
    long lastID = 0;

    for (LogIterator iterator = new LogIterator(logFile, lastFileNumber); iterator.hasNext();)
    {
      LogLine logLine = iterator.next();
      lastID = logLine.getID();

      recovery(properties, logLine);

      if (listeners != null)
      {
        fireEvent(new RecoveryEvent(this, properties, logLine), listeners);
      }
    }

    logLineCounter.set(lastID);
    fileNumber = lastFileNumber;

    loadInternal(properties);
  }

  private void load(File file) throws IOException
  {
    FileInputStream in = new FileInputStream(file);
    Properties properties = new Properties();

    try
    {
      properties.load(in);
    }
    finally
    {
      in.close();
    }

    String logLineCounterStr = (String)properties.remove(PROP_LOG_LINE_COUNTER);
    if (logLineCounterStr != null)
    {
      logLineCounter.set(Long.parseLong(logLineCounterStr));
    }

    String fileNumberStr = (String)properties.remove(PROP_FILE_NUMBER);
    if (fileNumberStr != null)
    {
      fileNumber = Integer.parseInt(fileNumberStr);
    }

    loadInternal(properties);
  }

  private void loadInternal(Properties properties)
  {
    load(properties);
    fireEvent(new PropertiesEvent(this, PropertiesEvent.Type.LOAD, properties));
  }

  private void save(File file) throws IOException
  {
    Properties properties = new Properties();

    fireEvent(new PropertiesEvent(this, PropertiesEvent.Type.SAVE, properties));
    save(properties);
    saveInternal(properties);

    FileOutputStream out = new FileOutputStream(file);

    try
    {
      properties.store(out, RollingLog.class.getSimpleName());
    }
    finally
    {
      out.close();
    }
  }

  private void saveInternal(Properties properties)
  {
    properties.setProperty(PROP_LOG_LINE_COUNTER, Long.toString(logLineCounter.get()));
    properties.setProperty(PROP_FILE_NUMBER, Integer.toString(fileNumber));
  }

  @Override
  public String toString()
  {
    return "RollingLog[" + logFile + "]";
  }

  @Override
  public final CloseableIterator<LogLine> iterator()
  {
    return iterator(logFile);
  }

  public static CloseableIterator<LogLine> iterator(String logFile)
  {
    return new LogIterator(logFile, 0);
  }

  public static void main(String[] args)
  {
    for (CloseableIterator<LogLine> it = iterator(args[0]); it.hasNext();)
    {
      LogLine logLine = it.next();
      System.out.println(logLine);
    }
  }

  private static File getFile(String logFile, int fileNumber)
  {
    return new File(logFile + String.format("-%04d", fileNumber) + ".txt");
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

    @Override
    public void close()
    {
      IOUtil.close(reader);
      reader = null;
      fileNumber = CLOSED;
    }

    @Override
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

  /**
   * @author Eike Stepper
   */
  public static abstract class RollingLogEvent extends Event
  {
    private static final long serialVersionUID = 1L;

    public RollingLogEvent(RollingLog rollingLog)
    {
      super(rollingLog);
    }

    @Override
    public RollingLog getSource()
    {
      return (RollingLog)super.getSource();
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class PropertiesEvent extends RollingLogEvent
  {
    private static final long serialVersionUID = 1L;

    private final Type type;

    private final Properties properties;

    public PropertiesEvent(RollingLog rollingLog, Type type, Properties properties)
    {
      super(rollingLog);
      this.type = type;
      this.properties = properties;
    }

    public Type getType()
    {
      return type;
    }

    public Properties getProperties()
    {
      return properties;
    }

    @Override
    protected String formatAdditionalParameters()
    {
      return "type=" + type + ", properties=" + properties;
    }

    /**
     * @author Eike Stepper
     */
    public static enum Type
    {
      LOAD, SAVE;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class RecoveryEvent extends RollingLogEvent
  {
    private static final long serialVersionUID = 1L;

    private final Properties properties;

    private final LogLine logLine;

    public RecoveryEvent(RollingLog rollingLog, Properties properties, LogLine logLine)
    {
      super(rollingLog);
      this.properties = properties;
      this.logLine = logLine;
    }

    public String getProperty(String key)
    {
      return properties.getProperty(key);
    }

    public void setProperty(String key, String value)
    {
      properties.setProperty(key, value);
    }

    public LogLine getLogLine()
    {
      return logLine;
    }

    @Override
    protected String formatAdditionalParameters()
    {
      return "logLine=" + logLine;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class SplitEvent extends RollingLogEvent
  {
    private static final long serialVersionUID = 1L;

    private final File lastFile;

    private final int lastFileNumber;

    public SplitEvent(RollingLog rollingLog, File lastFile, int lastFileNumber)
    {
      super(rollingLog);
      this.lastFile = lastFile;
      this.lastFileNumber = lastFileNumber;
    }

    public File getLastFile()
    {
      return lastFile;
    }

    public int getLastFileNumber()
    {
      return lastFileNumber;
    }

    @Override
    protected String formatAdditionalParameters()
    {
      return "lastFile=" + lastFile + ", lastFileNumber=" + lastFileNumber;
    }
  }
}
