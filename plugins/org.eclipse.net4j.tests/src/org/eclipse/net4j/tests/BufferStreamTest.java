/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Teerawat Chaiyakijpichet (No Magic Asia Ltd.) - SSL
 */
package org.eclipse.net4j.tests;

import static org.junit.Assert.assertArrayEquals;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.buffer.BufferInputStream;
import org.eclipse.net4j.buffer.BufferOutputStream;
import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.buffer.IBufferHandler;
import org.eclipse.net4j.buffer.IBufferPool;
import org.eclipse.net4j.tests.data.HugeData;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.tests.AbstractOMTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Eike Stepper
 */
public class BufferStreamTest extends AbstractOMTest
{
  private static final short CHANNEL_ID = 0;

  public void testReadArray() throws Exception
  {
    byte[] data = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };

    BufferInputStream in = new BufferInputStream();
    byte[] result = new byte[2 * data.length];
    AtomicInteger bytesRead = new AtomicInteger();

    IBufferPool bufferPool = Net4jUtil.createBufferPool();
    BufferOutputStream out = new BufferOutputStream(new BufferStreamPipe(in), bufferPool, CHANNEL_ID);

    Thread consumer = new Thread("CONSUMER")
    {
      @Override
      public void run()
      {
        try
        {
          bytesRead.set(in.read(result));
        }
        catch (IOException ex)
        {
          ex.printStackTrace();
        }
      }
    };

    consumer.setDaemon(true);
    consumer.start();

    out.write(data);
    out.flushWithEOS();
    out.close();

    consumer.join(10000000 * DEFAULT_TIMEOUT);

    assertEquals(data.length, bytesRead.get());
    byte[] actual = new byte[bytesRead.get()];
    System.arraycopy(result, 0, actual, 0, bytesRead.get());
    assertArrayEquals(data, actual);
  }

  public void testCopyBinary() throws Exception
  {
    byte[] data = HugeData.getBytes();

    BufferInputStream in = new BufferInputStream();
    ByteArrayOutputStream result = new ByteArrayOutputStream();

    IBufferPool bufferPool = Net4jUtil.createBufferPool();
    BufferOutputStream out = new BufferOutputStream(new BufferStreamPipe(in), bufferPool, CHANNEL_ID);

    Thread consumer = new Thread("CONSUMER")
    {
      @Override
      public void run()
      {
        try
        {
          IOUtil.copyBinary(in, result);
        }
        catch (IOException ex)
        {
          ex.printStackTrace();
        }
      }
    };

    consumer.setDaemon(true);
    consumer.start();

    out.write(data);
    out.flushWithEOS();
    out.close();

    consumer.join(10000000 * DEFAULT_TIMEOUT);
    assertArrayEquals(data, result.toByteArray());
  }

  /**
   * @author Eike Stepper
   */
  private static final class BufferStreamPipe implements IBufferHandler
  {
    private final BufferInputStream in;

    public BufferStreamPipe(BufferInputStream in)
    {
      this.in = in;
    }

    @Override
    public void handleBuffer(IBuffer buffer)
    {
      ByteBuffer byteBuffer = buffer.getByteBuffer();
      short payload = (short)(byteBuffer.position() - IBuffer.HEADER_SIZE);

      byteBuffer.flip();
      byteBuffer.putShort(CHANNEL_ID);
      byteBuffer.putShort(payload);
      in.handleBuffer(buffer);
    }
  }
}
