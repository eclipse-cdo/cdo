/*
 * Copyright (c) 2009, 2011, 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.tests;

import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.io.StringCompressor;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Eike Stepper
 */
public class StringCompressorTest extends AbstractOMTest
{
  private static long SLEEP_WRITER = 1;

  private static long SLEEP_READER = 0;

  private static final String[] strings = createStrings(50, 837456);

  private static final int[] indices = createIndices(100, 50, 9087346);

  private StringCompressor.Counting client;

  private StringCompressor.Counting server;

  public void testSingleStreamToServer() throws Exception
  {
    run(1, 0);
  }

  public void testSingleStreamToClient() throws Exception
  {
    run(0, 1);
  }

  public void testBidi1() throws Exception
  {
    run(1, 1);
  }

  public void testBidi10() throws Exception
  {
    run(10, 10);
  }

  public void testBidi1Plus10() throws Exception
  {
    run(1, 1);
    run(10, 10);
  }

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();
    client = new StringCompressor.Counting(true);
    server = new StringCompressor.Counting(false);
  }

  @Override
  protected void doTearDown() throws Exception
  {
    System.out.println("Strings read by client compressor: " + client.getStringsRead());
    System.out.println("Strings read by server compressor: " + server.getStringsRead());
    System.out.println("Strings written by client compressor: " + client.getStringsWritten());
    System.out.println("Strings written by server compressor: " + server.getStringsWritten());
    super.doTearDown();
  }

  private void run(int toServer, int toClient) throws IOException, InterruptedException
  {
    CountDownLatch latch = new CountDownLatch(toServer + toClient);
    while (toServer > 0 || toClient > 0)
    {
      if (toServer > 0)
      {
        --toServer;
        new Stream(latch, client, server).start();
      }

      if (toClient > 0)
      {
        --toClient;
        new Stream(latch, server, client).start();
      }
    }

    latch.await(300, TimeUnit.SECONDS);
  }

  private static String[] createStrings(int count, long seed)
  {
    Random random = new Random(seed);
    String[] result = new String[count];
    for (int i = 0; i < result.length; i++)
    {
      String str = "";
      int length = next(random) % 13;
      for (int j = 0; j < length + 1; j++)
      {
        int val = next(random);
        str += Integer.toString(val, 36);
      }

      result[i] = str;
    }

    return result;
  }

  private static int[] createIndices(int count, int range, long seed)
  {
    Random random = new Random(seed);
    int[] result = new int[count];
    for (int i = 0; i < result.length; i++)
    {
      result[i] = next(random) % range;
    }

    return result;
  }

  private static int next(Random random)
  {
    for (;;)
    {
      int val = random.nextInt();
      if (val > 0)
      {
        return val;
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class Stream extends Thread
  {
    private CountDownLatch latch;

    private Writer writer;

    private Reader reader;

    public Stream(CountDownLatch latch, StringCompressor writer, StringCompressor reader) throws IOException
    {
      this.latch = latch;
      this.writer = new Writer(writer);
      this.reader = new Reader(reader, this.writer);
    }

    @Override
    public void run()
    {
      try
      {
        reader.start();
        writer.start();

        reader.join();
        writer.join();

        latch.countDown();
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class Writer extends Thread
  {
    private StringCompressor compressor;

    private PipedOutputStream pipe;

    private Exception exception;

    public Writer(StringCompressor compressor)
    {
      this.compressor = compressor;
      pipe = new PipedOutputStream();
    }

    public PipedOutputStream getPipe()
    {
      return pipe;
    }

    public Exception getException()
    {
      return exception;
    }

    @Override
    public void run()
    {
      try
      {
        ExtendedDataOutputStream out = new ExtendedDataOutputStream(pipe);
        for (int i = 0; i < indices.length; i++)
        {
          int index = indices[i];
          msg(getName() + " --> " + i);
          compressor.write(out, strings[index]);
          if (SLEEP_WRITER > 0)
          {
            Thread.sleep(SLEEP_WRITER);
          }
        }
      }
      catch (Exception ex)
      {
        exception = ex;
        ex.printStackTrace();
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class Reader extends Thread
  {
    private StringCompressor compressor;

    private PipedInputStream pipe;

    private Exception exception;

    public Reader(StringCompressor compressor, Writer writer) throws IOException
    {
      this.compressor = compressor;
      pipe = new PipedInputStream(writer.getPipe());
    }

    public Exception getException()
    {
      return exception;
    }

    @Override
    public void run()
    {
      try
      {
        ExtendedDataInput in = new ExtendedDataInputStream(pipe);
        for (int i = 0; i < indices.length; i++)
        {
          int index = indices[i];
          msg(getName() + " --> " + i);
          String toBeRead = strings[index];

          String read = compressor.read(in);
          assertEquals(toBeRead, read);
          if (SLEEP_READER > 0)
          {
            Thread.sleep(SLEEP_READER);
          }
        }
      }
      catch (Exception ex)
      {
        exception = ex;
        ex.printStackTrace();
      }
    }
  }
}
