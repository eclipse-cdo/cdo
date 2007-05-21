/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
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
import org.eclipse.net4j.TransportUtil;
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.trace.PrintTraceHandler;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Eike Stepper
 */
public class TestBufferPool
{
  private static IBufferPool bufferPool = TransportUtil.createBufferPool();

  private static Collection memory = new ArrayList();

  public static void main(String[] args) throws InterruptedException
  {
    OMPlatform.INSTANCE.addTraceHandler(PrintTraceHandler.CONSOLE);
    OMPlatform.INSTANCE.setDebugging(true);
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

    while (TransportUtil.getPooledBuffers(bufferPool) > 0 && allocate())
    {
      Thread.sleep(200);
      ReflectUtil.dump(bufferPool);
    }

    LifecycleUtil.deactivate(bufferPool);
  }

  private static void msg()
  {
    System.out.println("pooledBuffers = " + TransportUtil.getPooledBuffers(bufferPool));
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
