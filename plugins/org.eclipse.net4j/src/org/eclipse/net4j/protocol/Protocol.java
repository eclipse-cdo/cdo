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
package org.eclipse.net4j.protocol;

import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.util.lifecycle.Lifecycle;

import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 */
public abstract class Protocol extends Lifecycle implements IProtocol
{
  private IChannel channel;

  private IBufferProvider bufferProvider;

  private ExecutorService executorService;

  private Object infraStructure;

  public Protocol()
  {
  }

  public IChannel getChannel()
  {
    return channel;
  }

  public void setChannel(IChannel channel)
  {
    this.channel = channel;
  }

  public IBufferProvider getBufferProvider()
  {
    return bufferProvider;
  }

  public void setBufferProvider(IBufferProvider bufferProvider)
  {
    this.bufferProvider = bufferProvider;
  }

  public ExecutorService getExecutorService()
  {
    return executorService;
  }

  public void setExecutorService(ExecutorService executorService)
  {
    this.executorService = executorService;
  }

  public Object getInfraStructure()
  {
    return infraStructure;
  }

  public void setInfraStructure(Object infraStructure)
  {
    this.infraStructure = infraStructure;
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(channel, "channel");
    checkState(bufferProvider, "bufferProvider");
    checkState(executorService, "executorService");
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    channel = null;
    super.doDeactivate();
  }
}
