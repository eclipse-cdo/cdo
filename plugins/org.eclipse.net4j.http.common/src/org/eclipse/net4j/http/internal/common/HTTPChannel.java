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
package org.eclipse.net4j.http.internal.common;

import org.eclipse.net4j.http.internal.common.HTTPConnector.ChannelOperation;

import org.eclipse.internal.net4j.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Eike Stepper
 */
public class HTTPChannel extends Channel
{
  private long outputOperationCount = 1;// Open channel was 0 implicitely

  private long inputOperationCount;

  private Map<Long, ChannelOperation> inputOperationQuarantine = new ConcurrentHashMap<Long, ChannelOperation>();

  private CountDownLatch openAck = new CountDownLatch(1);

  private boolean inverseRemoved;

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

  public void quarantineInputOperation(long count, ChannelOperation operation)
  {
    inputOperationQuarantine.put(count, operation);
  }

  public ChannelOperation getQuarantinedInputOperation(long count)
  {
    return inputOperationQuarantine.remove(count);
  }

  public void openAck()
  {
    openAck.countDown();
  }

  public void waitForOpenAck(long timeout)
  {
    try
    {
      openAck.await(timeout, TimeUnit.MILLISECONDS);
    }
    catch (InterruptedException ignore)
    {
    }
  }

  public boolean isInverseRemoved()
  {
    return inverseRemoved;
  }

  public void setInverseRemoved()
  {
    inverseRemoved = true;
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    for (ChannelOperation operation : inputOperationQuarantine.values())
    {
      operation.dispose();
    }

    inputOperationQuarantine.clear();
    super.doDeactivate();
  }
}
