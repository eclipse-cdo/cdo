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
package org.eclipse.spi.net4j;

import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.protocol.IProtocol;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;

import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class Protocol<INFRA_STRUCTURE> extends Lifecycle implements IProtocol<INFRA_STRUCTURE>
{
  private ExecutorService executorService;

  private IBufferProvider bufferProvider;

  private INFRA_STRUCTURE infraStructure;

  private IChannel channel;

  @ExcludeFromDump
  private transient IListener channelListener = new LifecycleEventAdapter()
  {
    @Override
    protected void onDeactivated(ILifecycle lifecycle)
    {
      handleChannelDeactivation();
    };
  };

  public Protocol()
  {
  }

  public ExecutorService getExecutorService()
  {
    return executorService;
  }

  public void setExecutorService(ExecutorService executorService)
  {
    this.executorService = executorService;
  }

  public IBufferProvider getBufferProvider()
  {
    return bufferProvider;
  }

  public void setBufferProvider(IBufferProvider bufferProvider)
  {
    this.bufferProvider = bufferProvider;
  }

  public INFRA_STRUCTURE getInfraStructure()
  {
    return infraStructure;
  }

  public void setInfraStructure(INFRA_STRUCTURE infraStructure)
  {
    this.infraStructure = infraStructure;
  }

  /**
   * @since 2.0
   */
  public Location getLocation()
  {
    return channel.getLocation();
  }

  /**
   * @since 2.0
   */
  public boolean isClient()
  {
    return channel.isClient();
  }

  /**
   * @since 2.0
   */
  public boolean isServer()
  {
    return channel.isServer();
  }

  public IChannel getChannel()
  {
    return channel;
  }

  public void setChannel(IChannel newChannel)
  {
    if (channel != newChannel)
    {
      if (channel != null)
      {
        channel.removeListener(channelListener);
      }

      channel = newChannel;
      if (channel != null)
      {
        channel.addListener(channelListener);
      }
    }
  }

  /**
   * @since 2.0
   */
  protected void handleChannelDeactivation()
  {
    deactivate();
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
    setChannel(null);
    super.doDeactivate();
  }
}
