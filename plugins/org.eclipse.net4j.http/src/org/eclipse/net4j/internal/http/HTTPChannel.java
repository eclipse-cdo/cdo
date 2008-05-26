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

/**
 * @author Eike Stepper
 */
public class HTTPChannel extends Channel
{
  private long outputBufferCount;

  private long inputBufferCount;

  private Map<Long, IBuffer> inputBufferQuarantine = new ConcurrentHashMap<Long, IBuffer>();

  public HTTPChannel()
  {
  }

  public long getOutputBufferCount()
  {
    return outputBufferCount;
  }

  public void increaseOutputBufferCount()
  {
    ++outputBufferCount;
  }

  public long getInputBufferCount()
  {
    return inputBufferCount;
  }

  public void increaseInputBufferCount()
  {
    ++inputBufferCount;
  }

  public void quarantineInputBuffer(long count, IBuffer buffer)
  {
    inputBufferQuarantine.put(count, buffer);
  }

  public IBuffer getQuarantinedInputBuffer(long count)
  {
    return inputBufferQuarantine.remove(count);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    for (IBuffer buffer : inputBufferQuarantine.values())
    {
      buffer.release();
    }

    inputBufferQuarantine.clear();
    super.doDeactivate();
  }
}
