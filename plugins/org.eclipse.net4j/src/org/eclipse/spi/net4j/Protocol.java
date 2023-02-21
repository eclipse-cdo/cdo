/*
 * Copyright (c) 2008-2012, 2015, 2016, 2019, 2021, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.spi.net4j;

import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.protocol.IProtocol3;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.concurrent.IExecutorServiceProvider;
import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.log.OMLogger;

import java.util.concurrent.ExecutorService;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class Protocol<INFRA_STRUCTURE> extends Lifecycle implements IProtocol3<INFRA_STRUCTURE>, IExecutorServiceProvider
{
  private String type;

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
    }
  };

  private String userID;

  public Protocol(String type)
  {
    this.type = type;
  }

  @Override
  public final String getType()
  {
    return type;
  }

  /**
   * @since 4.2
   */
  @Override
  public int getVersion()
  {
    return UNSPECIFIED_VERSION;
  }

  @Override
  public ExecutorService getExecutorService()
  {
    return executorService;
  }

  @Override
  public void setExecutorService(ExecutorService executorService)
  {
    this.executorService = executorService;
  }

  @Override
  public IBufferProvider getBufferProvider()
  {
    return bufferProvider;
  }

  @Override
  public INFRA_STRUCTURE getInfraStructure()
  {
    return infraStructure;
  }

  @Override
  public void setInfraStructure(INFRA_STRUCTURE infraStructure)
  {
    if (this.infraStructure != infraStructure)
    {
      INFRA_STRUCTURE oldInfraStructure = this.infraStructure;
      this.infraStructure = infraStructure;
      fireEvent(new InfraStructureChangedEvent(oldInfraStructure, infraStructure));
    }
  }

  /**
   * @since 2.0
   */
  @Override
  public Location getLocation()
  {
    return channel.getLocation();
  }

  /**
   * @since 2.0
   */
  @Override
  public boolean isClient()
  {
    return channel.isClient();
  }

  /**
   * @since 2.0
   */
  @Override
  public boolean isServer()
  {
    return channel.isServer();
  }

  @Override
  public IChannel getChannel()
  {
    return channel;
  }

  @Override
  public void setChannel(IChannel newChannel)
  {
    if (channel != newChannel)
    {
      executorService = null;
      bufferProvider = null;
      if (channel != null)
      {
        channel.removeListener(channelListener);
      }

      channel = newChannel;
      if (channel != null)
      {
        channel.addListener(channelListener);
        executorService = ConcurrencyUtil.getExecutorService(channel);
        bufferProvider = (InternalChannel)channel;
      }
    }
  }

  @Override
  public String getUserID()
  {
    if (userID == null && channel != null)
    {
      return channel.getUserID();
    }

    return userID;
  }

  protected void setUserID(String userID)
  {
    this.userID = userID;
  }

  /**
   * @since 2.0
   */
  protected void handleChannelDeactivation()
  {
    LifecycleUtil.deactivate(this, OMLogger.Level.DEBUG);
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(channel, "channel"); //$NON-NLS-1$
    checkState(bufferProvider, "bufferProvider"); //$NON-NLS-1$
    checkState(executorService, "executorService"); //$NON-NLS-1$
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    setChannel(null);
    super.doDeactivate();
  }

  /**
   * @author Eike Stepper
   * @since 4.13
   */
  public final class InfraStructureChangedEvent extends Event
  {
    private static final long serialVersionUID = 1L;

    private final INFRA_STRUCTURE oldInfraStructure;

    private final INFRA_STRUCTURE newInfraStructure;

    private InfraStructureChangedEvent(INFRA_STRUCTURE oldInfraStructure, INFRA_STRUCTURE newInfraStructure)
    {
      super(Protocol.this);
      this.oldInfraStructure = oldInfraStructure;
      this.newInfraStructure = newInfraStructure;
    }

    @Override
    public Protocol<INFRA_STRUCTURE> getSource()
    {
      return Protocol.this;
    }

    public final INFRA_STRUCTURE getOldInfraStructure()
    {
      return oldInfraStructure;
    }

    public final INFRA_STRUCTURE getNewInfraStructure()
    {
      return newInfraStructure;
    }
  }
}
