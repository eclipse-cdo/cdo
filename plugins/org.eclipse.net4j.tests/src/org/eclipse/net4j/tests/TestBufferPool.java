/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.tests;

import org.eclipse.net4j.IBuffer;
import org.eclipse.net4j.IBufferPool;
import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.tests.AbstractOMTest;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Eike Stepper
 */
public class TestBufferPool extends AbstractOMTest
{
  private static IBufferPool bufferPool = Net4jUtil.createBufferPool();

  private static Collection<byte[]> memory = new ArrayList<byte[]>();

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
      sleep(200);
      ReflectUtil.dump(bufferPool);
    }

    LifecycleUtil.deactivate(bufferPool);
  }

  private static void msg()
  {
    System.out.println("pooledBuffers = " + Net4jUtil.getPooledBuffers(bufferPool));
  }

  private static boolean allocate()
  {
    try
    {
      System.out.println("allocating from " + Runtime.getRuntime().freeMemory());
      for (int i = 0; i < 10; i++)
      {
        memory.add(new byte[1000000]);
      }

      msg();
      return true;
    }
    catch (Exception ex)
    {
      return false;
    }
  }

  @SuppressWarnings("unused")
  private static void gc()
  {
    msg();
    System.out.println("collecting garbage");
    System.gc();
    msg();
  }
}
