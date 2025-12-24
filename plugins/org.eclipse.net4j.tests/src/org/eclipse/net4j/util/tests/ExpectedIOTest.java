/*
 * Copyright (c) 2010-2013, 2025 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.io.ExpectedFileInputStream;
import org.eclipse.net4j.util.io.ExpectedFileReader;
import org.eclipse.net4j.util.io.IOUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;

/**
 * @author Eike Stepper
 */
public class ExpectedIOTest extends AbstractOMTest
{
  private Exception exception;

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();
    exception = null;
  }

  public void testInputStream() throws Exception
  {
    final File file = createTempFile("ExpectedIOTest", ".tmp");
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
            IOUtil.ERR().println(i);
            ConcurrencyUtil.sleep(10);
          }
        }
        catch (Exception ex)
        {
          if (exception == null)
          {
            exception = ex;
          }
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

          for (int i = 0; i < LOOPS; i++)
          {
            System.out.println(in.readInt());
          }
        }
        catch (Exception ex)
        {
          if (exception == null)
          {
            exception = ex;
          }
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

    if (exception != null)
    {
      throw exception;
    }
  }

  public void testInputStreamEOF() throws Exception
  {
    final File file = createTempFile("ExpectedIOTest", ".tmp");
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
            IOUtil.ERR().println(i);
            ConcurrencyUtil.sleep(10);
          }
        }
        catch (Exception ex)
        {
          if (exception == null)
          {
            exception = ex;
          }
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
        catch (Exception ex)
        {
          if (exception == null)
          {
            exception = ex;
          }
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

    if (!(exception instanceof EOFException))
    {
      throw exception;
    }
  }

  public void testReader() throws Exception
  {
    final File file = createTempFile("ExpectedIOTest", ".tmp");
    final int LOOPS = 30;

    Thread producer = new Thread("PRODUCER")
    {
      @Override
      public void run()
      {
        Writer out = null;

        try
        {
          out = new FileWriter(file);
          for (int i = 0; i < LOOPS; i++)
          {
            int c = 'a' + i;
            out.write(c);
            IOUtil.ERR().println(c);

            out.write(c + 1);
            IOUtil.ERR().println(c + 1);
            ConcurrencyUtil.sleep(10);
          }
        }
        catch (Exception ex)
        {
          if (exception == null)
          {
            exception = ex;
          }
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
        Reader in = null;

        try
        {
          in = new ExpectedFileReader(file, LOOPS * 2);

          for (int i = 0; i < LOOPS; i++)
          {
            int c = in.read();
            if (c == -1)
            {
              throw new EOFException();
            }

            System.out.println(c);
            c = in.read();
            if (c == -1)
            {
              throw new EOFException();
            }

            System.out.println(c);
          }
        }
        catch (Exception ex)
        {
          if (exception == null)
          {
            exception = ex;
          }
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

    if (exception != null)
    {
      throw exception;
    }
  }

  public void testReaderEOF() throws Exception
  {
    final File file = createTempFile("ExpectedIOTest", ".tmp");
    final int LOOPS = 30;

    Thread producer = new Thread("PRODUCER")
    {
      @Override
      public void run()
      {
        Writer out = null;

        try
        {
          out = new FileWriter(file);
          for (int i = 0; i < LOOPS; i++)
          {
            int c = 'a' + i;
            out.write(c);
            IOUtil.ERR().println(c);

            out.write(c + 1);
            IOUtil.ERR().println(c + 1);
            ConcurrencyUtil.sleep(10);
          }
        }
        catch (Exception ex)
        {
          if (exception == null)
          {
            exception = ex;
          }
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
        Reader in = null;

        try
        {
          in = new ExpectedFileReader(file, LOOPS * 2);

          for (int i = 0; i < LOOPS + 10; i++)
          {
            int c = in.read();
            if (c == -1)
            {
              throw new EOFException();
            }

            System.out.println(c);
            c = in.read();
            if (c == -1)
            {
              throw new EOFException();
            }

            System.out.println(c);
          }
        }
        catch (Exception ex)
        {
          if (exception == null)
          {
            exception = ex;
          }
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

    if (!(exception instanceof EOFException))
    {
      throw exception;
    }
  }
}
