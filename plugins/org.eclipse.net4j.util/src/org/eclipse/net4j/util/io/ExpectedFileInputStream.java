/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.io;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.concurrent.TimeoutRuntimeException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Eike Stepper
 * @since 3.1
 */
public class ExpectedFileInputStream extends FileInputStream
{
  public static final long DEFAULT_TIMEOUT = 2500;

  private long timeout = DEFAULT_TIMEOUT;

  private File file;

  private long expectedSize;

  private long pos;

  public ExpectedFileInputStream(File file, long expectedSize) throws FileNotFoundException
  {
    super(file);
    this.file = file;
    this.expectedSize = expectedSize;
  }

  public long getTimeout()
  {
    return timeout;
  }

  public void setTimeout(long timeout)
  {
    this.timeout = timeout;
  }

  @Override
  public long skip(long n) throws IOException
  {
    waitForInput(n);
    return super.skip(n);
  }

  @Override
  public int read() throws IOException
  {
    waitForInput(1L);
    return super.read();
  }

  @Override
  public int read(byte[] b, int off, int len) throws IOException
  {
    waitForInput(len);
    return super.read(b, off, len);
  }

  @Override
  public int read(byte[] b) throws IOException
  {
    return read(b, 0, b.length);
  }

  private void waitForInput(long n) throws IOException
  {
    synchronized (this)
    {
      n = Math.min(n, expectedSize - pos);
      long restSize = file.length() - pos;
      long endTime = 0;

      while (restSize < n)
      {
        long restTime;
        if (endTime == 0)
        {
          endTime = System.currentTimeMillis() + timeout;
          restTime = timeout;
        }
        else
        {
          restTime = endTime - System.currentTimeMillis();
        }

        if (restTime <= 0)
        {
          throw new TimeoutRuntimeException("Timeout while reading from " + file.getAbsolutePath());
        }

        try
        {
          wait(Math.max(100L, restTime));
        }
        catch (InterruptedException ex)
        {
          throw WrappedException.wrap(ex);
        }

        restSize = file.length() - pos;
      }

      pos += n;
    }
  }

  public static void main(String[] args) throws Exception
  {
    final File file = new File(new File(System.getProperty("java.io.tmpdir")), "___TEST.txt");
    final int LOOPS = 30;

    Thread producer = new Thread("PRODUCER")
    {
      @Override
      public void run()
      {
        DataOutputStream out = null;

        try
        {
          out = new DataOutputStream(new FileOutputStream(file));
          for (int i = 0; i < LOOPS; i++)
          {
            out.writeInt(i);
            System.err.println(i);
            ConcurrencyUtil.sleep(100);
          }
        }
        catch (IOException ex)
        {
          ex.printStackTrace();
        }
        finally
        {
          IOUtil.close(out);
        }
      }
    };

    Thread consumer = new Thread("CONSUMER")
    {
      @Override
      public void run()
      {
        DataInputStream in = null;

        try
        {
          in = new DataInputStream(new ExpectedFileInputStream(file, LOOPS * 4));

          for (int i = 0; i < LOOPS + 10; i++)
          {
            System.out.println(in.readInt());
          }
        }
        catch (IOException ex)
        {
          ex.printStackTrace();
        }
        finally
        {
          IOUtil.close(in);
        }
      }
    };

    producer.start();
    ConcurrencyUtil.sleep(500);
    consumer.start();

    producer.join();
    consumer.join();
  }
}
