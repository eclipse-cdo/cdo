/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.http;

import org.eclipse.net4j.buffer.IBuffer;

import org.eclipse.internal.net4j.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * @author Eike Stepper
 */
public class HTTPChannel extends Channel
{
  private long outputOperationCount = 1;// Open channel was 0 implicitely

  private long inputOperationCount;

  private Map<Long, IBuffer> inputOperationQuarantine = new ConcurrentHashMap<Long, IBuffer>();

  private CountDownLatch openAck = new CountDownLatch(1);

  public HTTPChannel()
  {
  }

  public long getOutputOperationCount()
  {
    return outputOperationCount;
  }

  public void increaseOutputOperationCount()
  {
    ++outputOperationCount;
  }

  public long getInputOperationCount()
  {
    return inputOperationCount;
  }

  public void increaseInputOperationCount()
  {
    ++inputOperationCount;
  }

  public void quarantineInputOperation(long count, IBuffer buffer)
  {
    inputOperationQuarantine.put(count, buffer);
  }

  public IBuffer getQuarantinedInputOperation(long count)
  {
    return inputOperationQuarantine.remove(count);
  }

  public void openAck()
  {
    openAck.countDown();
  }

  public void waitForOpenAck()
  {
    try
    {
      openAck.await();
    }
    catch (InterruptedException ignore)
    {
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    for (IBuffer buffer : inputOperationQuarantine.values())
    {
      buffer.release();
    }

    inputOperationQuarantine.clear();
    super.doDeactivate();
  }
}
