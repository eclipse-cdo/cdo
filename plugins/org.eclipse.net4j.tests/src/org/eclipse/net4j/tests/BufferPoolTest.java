/*
 * Copyright (c) 2007, 2009, 2011, 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.tests;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.buffer.IBufferPool;
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.tests.AbstractOMTest;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Eike Stepper
 */
public class BufferPoolTest extends AbstractOMTest
{
  private static IBufferPool bufferPool = Net4jUtil.createBufferPool();

  private static Collection<byte[]> memory = new ArrayList<>();

  @Override
  protected void doTearDown() throws Exception
  {
    memory.clear();
    super.doTearDown();
  }

  public void testBufferPool() throws Exception
  {
    LifecycleUtil.activate(bufferPool);

    IBuffer[] buffers = new IBuffer[10];
    for (int i = 0; i < buffers.length; i++)
    {
      buffers[i] = bufferPool.provideBuffer();
    }

    for (int i = 0; i < buffers.length; i++)
    {
      bufferPool.retainBuffer(buffers[i]);
      buffers[i] = null;
    }

    while (Net4jUtil.getPooledBuffers(bufferPool) > 0 && allocate())
    {
      sleep(10);
      ReflectUtil.dump(bufferPool);
    }

    LifecycleUtil.deactivate(bufferPool);
  }

  private static void msg()
  {
    msg("pooledBuffers = " + Net4jUtil.getPooledBuffers(bufferPool)); //$NON-NLS-1$
  }

  private static boolean allocate()
  {
    try
    {
      IOUtil.OUT().println("allocating from " + Runtime.getRuntime().freeMemory()); //$NON-NLS-1$
      for (int i = 0; i < 10; i++)
      {
        memory.add(new byte[1000000]);
      }

      msg();
      return true;
    }
    catch (Throwable t)
    {
      return false;
    }
  }

  @SuppressWarnings("unused")
  private static void gc()
  {
    msg();
    IOUtil.OUT().println("collecting garbage"); //$NON-NLS-1$
    System.gc();
    msg();
  }
}
