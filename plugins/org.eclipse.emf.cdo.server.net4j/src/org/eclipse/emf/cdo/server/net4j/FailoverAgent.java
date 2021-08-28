/*
 * Copyright (c) 2010-2012, 2016, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.net4j;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IRepositorySynchronizer;
import org.eclipse.emf.cdo.server.ISynchronizableRepository;
import org.eclipse.emf.cdo.session.CDOSessionConfiguration;
import org.eclipse.emf.cdo.session.CDOSessionConfigurationFactory;
import org.eclipse.emf.cdo.spi.server.InternalFailoverParticipant;
import org.eclipse.emf.cdo.spi.server.InternalRepositorySynchronizer;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.signal.Indication;
import org.eclipse.net4j.signal.SignalReactor;
import org.eclipse.net4j.signal.heartbeat.HeartBeatProtocol;
import org.eclipse.net4j.util.concurrent.TimerLifecycle;
import org.eclipse.net4j.util.concurrent.TimerLifecycle.DaemonFactory;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IManagedContainerProvider;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import java.io.IOException;
import java.util.Timer;

/**
 * A repository-side agent for a {@link FailoverMonitor fail-over monitor}.
 *
 * @author Eike Stepper
 * @since 4.0
 */
public abstract class FailoverAgent extends Lifecycle implements CDOSessionConfigurationFactory, IManagedContainerProvider
{
  private IConnector monitorConnector;

  private Timer timer;

  private long rate;

  private long timeout;

  private String group;

  private String connectorDescription;

  private InternalFailoverParticipant repository;

  private long repositoryActivationTimeout = 10000L;

  private Protocol protocol;

  private String masterConnectorDescription;

  private String masterRepositoryName;

  private InternalRepositorySynchronizer synchronizer;

  public FailoverAgent()
  {
  }

  /**
   * @since 4.1
   */
  @Override
  public IManagedContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }

  public IConnector getMonitorConnector()
  {
    return monitorConnector;
  }

  public void setMonitorConnector(IConnector connector)
  {
    checkInactive();
    monitorConnector = connector;
  }

  public Timer getTimer()
  {
    return timer;
  }

  public void setTimer(Timer timer)
  {
    checkInactive();
    this.timer = timer;
  }

  public long getRate()
  {
    return rate;
  }

  public void setRate(long rate)
  {
    checkInactive();
    this.rate = rate;
  }

  public long getTimeout()
  {
    return timeout;
  }

  public void setTimeout(long timeout)
  {
    checkInactive();
    this.timeout = timeout;
  }

  public String getGroup()
  {
    return group;
  }

  public void setGroup(String group)
  {
    checkInactive();
    this.group = group;
  }

  public String getConnectorDescription()
  {
    return connectorDescription;
  }

  public void setConnectorDescription(String connectorDescription)
  {
    checkInactive();
    this.connectorDescription = connectorDescription;
  }

  public ISynchronizableRepository getRepository()
  {
    return repository;
  }

  public void setRepository(ISynchronizableRepository repository)
  {
    checkInactive();

    if (!(repository instanceof InternalFailoverParticipant))
    {
      throw new IllegalArgumentException("Not a failover participant: " + repository);
    }

    if (repository.getSynchronizer() != null)
    {
      throw new IllegalArgumentException("Synchronizer must be null: " + repository);
    }

    this.repository = (InternalFailoverParticipant)repository;
  }

  /**
   * @since 4.1
   */
  public long getRepositoryActivationTimeout()
  {
    return repositoryActivationTimeout;
  }

  /**
   * @since 4.1
   */
  public void setRepositoryActivationTimeout(long repositoryActivationTimeout)
  {
    checkInactive();
    this.repositoryActivationTimeout = repositoryActivationTimeout;
  }

  public Protocol getProtocol()
  {
    return protocol;
  }

  @Override
  public CDOSessionConfiguration createSessionConfiguration()
  {
    return createSessionConfiguration(masterConnectorDescription, masterRepositoryName);
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(monitorConnector, "monitorConnector");
    checkState(group, "group");
    checkState(connectorDescription, "connectorDescription");
    checkState(repository, "repository");
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    IManagedContainer container = getContainer();

    if (timer == null)
    {
      timer = (Timer)container.getElement(TimerLifecycle.PRODUCT_GROUP, DaemonFactory.TYPE, null);
    }

    synchronizer = (InternalRepositorySynchronizer)createRepositorySynchronizer();
    repository.setSynchronizer(synchronizer);
    // setMaster(); // Will be adjusted with the following SIGNAL_PUBLISH_MASTER

    protocol = new Protocol(this);
    protocol.start(rate, timeout);

    // Repository will be activated asynchronously by the agent protocol, so wait
    if (!LifecycleUtil.waitForActive(repository, repositoryActivationTimeout))
    {
      LifecycleUtil.checkActive(repository);
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    protocol.close();
    protocol = null;
    timer = null;
    monitorConnector = null;
    super.doDeactivate();
  }

  protected void setMaster()
  {
    repository.setType(CDOCommonRepository.Type.MASTER);
    masterConnectorDescription = null;
    masterRepositoryName = null;
  }

  protected void setBackup(String connectorDescription, String repositoryName)
  {
    masterConnectorDescription = connectorDescription;
    masterRepositoryName = repositoryName;
    repository.setType(CDOCommonRepository.Type.BACKUP);
  }

  /**
   * @since 4.1
   */
  protected IRepositorySynchronizer createRepositorySynchronizer()
  {
    return CDOServerUtil.createRepositorySynchronizer(this);
  }

  protected abstract CDOSessionConfiguration createSessionConfiguration(String connectorDescription, String repositoryName);

  /**
   * The agent-side implementation of the {@link FailoverMonitor fail-over monitor} protocol.
   *
   * @author Eike Stepper
   */
  public static class Protocol extends HeartBeatProtocol
  {
    private FailoverAgent agent;

    public Protocol(FailoverAgent agent)
    {
      super(FailoverMonitor.PROTOCOL_NAME, agent.getMonitorConnector(), agent.getTimer());
      this.agent = agent;
    }

    public FailoverAgent getAgent()
    {
      return agent;
    }

    @Override
    protected void requestingStart(ExtendedDataOutputStream out, long rate) throws IOException
    {
      out.writeString(agent.getGroup());
      out.writeString(agent.getConnectorDescription());
      out.writeString(agent.getRepository().getName());
      super.requestingStart(out, rate);
    }

    @Override
    protected SignalReactor createSignalReactor(short signalID)
    {
      switch (signalID)
      {
      case FailoverMonitor.SIGNAL_PUBLISH_MASTER:
        return new Indication(this, FailoverMonitor.SIGNAL_PUBLISH_MASTER)
        {
          @Override
          protected void indicating(ExtendedDataInputStream in) throws Exception
          {
            boolean master = in.readBoolean();
            if (master)
            {
              agent.setMaster();
            }
            else
            {
              String connectorDescription = in.readString();
              String repositoryName = in.readString();
              agent.setBackup(connectorDescription, repositoryName);
            }

            IRepository repository = agent.getRepository();
            if (!repository.isActive())
            {
              IManagedContainer container = agent.getContainer();
              CDOServerUtil.addRepository(container, repository);
            }
          }
        };

      default:
        return super.createSignalReactor(signalID);
      }
    }
  }
}
