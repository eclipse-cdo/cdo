/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.net4j.syncing;

import org.eclipse.emf.cdo.server.internal.net4j.bundle.OM;

import org.eclipse.net4j.signal.Request;
import org.eclipse.net4j.signal.heartbeat.HeartBeatProtocol;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import org.eclipse.spi.net4j.ServerProtocolFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public class FailoverMonitor extends Container<Pair<String, String>>
{
  public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.server.net4j.failoverMonitors";

  public static final String PROTOCOL_NAME = "failover"; //$NON-NLS-1$

  public static final short SIGNAL_PUBLISH_MASTER = 3;

  private String group;

  private Map<Protocol, Pair<String, String>> agents = new HashMap<Protocol, Pair<String, String>>();

  private Protocol masterAgent;

  public FailoverMonitor()
  {
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

  @SuppressWarnings("unchecked")
  public Pair<String, String>[] getElements()
  {
    synchronized (agents)
    {
      return agents.values().toArray(new Pair[agents.size()]);
    }
  }

  public Map<Protocol, Pair<String, String>> getAgents()
  {
    return Collections.unmodifiableMap(agents);
  }

  public Protocol getMasterAgent()
  {
    synchronized (agents)
    {
      return masterAgent;
    }
  }

  public void registerAgent(Protocol agent, String connectorDescription, String repositoryName)
  {
    Pair<String, String> pair = new Pair<String, String>(connectorDescription, repositoryName);
    synchronized (agents)
    {
      agents.put(agent, pair);
      if (agents.size() == 1)
      {
        masterAgent = agent;
      }

      publishNewMaster(masterAgent);
    }

    fireElementAddedEvent(pair);
  }

  public void deregisterAgent(Protocol agent)
  {
    Pair<String, String> pair = null;
    synchronized (agents)
    {
      pair = agents.remove(agent);
      if (masterAgent == agent)
      {
        if (agents.isEmpty())
        {
          masterAgent = null;
        }
        else
        {
          masterAgent = electNewMaster(agents);
        }

        publishNewMaster(masterAgent);
      }
    }

    if (pair != null)
    {
      fireElementRemovedEvent(pair);
    }
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(group, "group");
  }

  protected Protocol electNewMaster(Map<Protocol, Pair<String, String>> agents)
  {
    return agents.keySet().iterator().next();
  }

  private void publishNewMaster(Protocol masterAgent)
  {
    final Pair<String, String> masterInfos = agents.get(masterAgent);
    for (Protocol agent : agents.keySet())
    {
      final boolean master = agent == masterAgent;

      try
      {
        new Request(agent, SIGNAL_PUBLISH_MASTER)
        {
          @Override
          protected void requesting(ExtendedDataOutputStream out) throws Exception
          {
            if (master)
            {
              out.writeBoolean(true);
            }
            else
            {
              out.writeBoolean(false);
              out.writeString(masterInfos.getElement1());
              out.writeString(masterInfos.getElement2());
            }
          }
        }.sendAsync();
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public interface Provider
  {
    public FailoverMonitor getFailoverMonitor(String group);
  }

  /**
   * @author Eike Stepper
   */
  public static class Factory extends org.eclipse.net4j.util.factory.Factory
  {
    public Factory()
    {
      super(PRODUCT_GROUP, "net4j");
    }

    public FailoverMonitor create(String description) throws ProductCreationException
    {
      FailoverMonitor monitor = new FailoverMonitor();
      monitor.setGroup(description);
      return monitor;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class Protocol extends HeartBeatProtocol.Server
  {
    private FailoverMonitor.Provider failoverMonitorProvider;

    private FailoverMonitor failoverMonitor;

    public Protocol(Provider failOverMonitorProvider)
    {
      super(PROTOCOL_NAME);
      failoverMonitorProvider = failOverMonitorProvider;
    }

    @Override
    protected void indicatingStart(ExtendedDataInputStream in) throws IOException
    {
      String group = in.readString();
      String connectorDescription = in.readString();
      String repositoryName = in.readString();

      failoverMonitor = failoverMonitorProvider.getFailoverMonitor(group);
      if (failoverMonitor == null)
      {
        throw new IllegalStateException("No monitor available for fail-over group " + group);
      }

      failoverMonitor.registerAgent(this, connectorDescription, repositoryName);
      super.indicatingStart(in);
    }

    @Override
    protected void doDeactivate() throws Exception
    {
      failoverMonitor.deregisterAgent(this);
      super.doDeactivate();
    }

    /**
     * @author Eike Stepper
     */
    public static class Factory extends ServerProtocolFactory implements FailoverMonitor.Provider
    {
      private IManagedContainer container;

      public Factory(IManagedContainer container)
      {
        super(PROTOCOL_NAME);
        this.container = container;
      }

      public Factory()
      {
        this(IPluginContainer.INSTANCE);
      }

      public Object create(String description) throws ProductCreationException
      {
        return new FailoverMonitor.Protocol(this);
      }

      public FailoverMonitor getFailoverMonitor(String group)
      {
        return (FailoverMonitor)container.getElement(FailoverMonitor.PRODUCT_GROUP, "net4j", group);
      }
    }
  }
}
