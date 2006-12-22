/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.protocol;


import org.eclipse.net4j.signal.Request;
import org.eclipse.net4j.signal.SignalReactor;
import org.eclipse.net4j.transport.Channel;
import org.eclipse.net4j.transport.Protocol;
import org.eclipse.net4j.transport.ProtocolFactory;
import org.eclipse.net4j.transport.Connector.Type;
import org.eclipse.net4j.util.Net4jUtil;

import org.eclipse.emf.cdo.core.ImplementationError;
import org.eclipse.emf.cdo.core.protocol.AbstractCDOResProtocol;
import org.eclipse.emf.cdo.core.protocol.ResourceChangeInfo;
import org.eclipse.emf.cdo.server.Mapper;
import org.eclipse.emf.cdo.server.ServerCDOResProtocol;
import org.eclipse.emf.cdo.server.internal.CDOServer;

import org.eclipse.internal.net4j.transport.AbstractProtocolFactory;

import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;


/**
 * @author Eike Stepper
 */
public class ServerCDOResProtocolImpl extends AbstractCDOResProtocol implements
    ServerCDOResProtocol
{
  protected Mapper mapper;

  protected TransactionTemplate transactionTemplate;

  protected transient List<Listener> listeners = new ArrayList<Listener>();

  public ServerCDOResProtocolImpl(Channel channel)
  {
    super(channel);
  }

  @Override
  protected SignalReactor createSignalReactor(short signalID)
  {
    switch (signalID)
    {
      case QUERY_ALL_RESOURCES:
        return new QueryAllResourcesIndication(mapper);
      case DELETE_RESOURCES:
        return new DeleteResourcesIndication(mapper, transactionTemplate);
      default:
        throw new ImplementationError("Invalid " + PROTOCOL_NAME + " signalID: " + signalID);
    }
  }

  public Mapper getMapper()
  {
    return mapper;
  }

  public void setMapper(Mapper mapper)
  {
    this.mapper = mapper;
  }

  public TransactionTemplate getTransactionTemplate()
  {
    return transactionTemplate;
  }

  public void setTransactionTemplate(TransactionTemplate transactionTemplate)
  {
    this.transactionTemplate = transactionTemplate;
  }

  public void fireResourcesChangedNotification(List<ResourceChangeInfo> resourceChanges)
  {
    for (Channel channel : getCDOResServerChannels())
    {
      try
      {
        Request signal = new ResourcesChangedRequest(channel, resourceChanges);
        signal.send();
      }
      catch (Exception ex)
      {
        CDOServer.LOG.error("Error while notifying resource changes " + resourceChanges, ex);
      }
    }
  }

  public void fireInvalidationNotification(Collection<Long> modifiedOIDs)
  {
    Listener[] array = listeners.toArray(new Listener[listeners.size()]);
    for (Listener listener : array)
    {
      listener.notifyInvalidation(this, modifiedOIDs);
    }
  }

  public void fireRemovalNotification(Collection<Integer> rids)
  {
    Listener[] array = listeners.toArray(new Listener[listeners.size()]);
    for (Listener listener : array)
    {
      listener.notifyRemoval(this, rids);
    }
  }

  public void addListener(Listener listener)
  {
    listeners.add(listener);
  }

  public void removeListener(Listener listener)
  {
    listeners.remove(listener);
  }

  protected Collection<Channel> getCDOResServerChannels()
  {
    return Net4jUtil.getChannels(getProtocolID(), ProtocolFactory.FOR_SERVERS);
  }

  @Override
  protected void onAboutToActivate() throws Exception
  {
    super.onAboutToActivate();
    if (mapper == null)
    {
      throw new IllegalStateException("mapper == null");
    }

    if (transactionTemplate == null)
    {
      throw new IllegalStateException("transactionTemplate == null");
    }
  }

  @Override
  protected void onDeactivate() throws Exception
  {
    listeners = null;
    mapper = null;
    transactionTemplate = null;
    super.onDeactivate();
  }


  /**
   * @author Eike Stepper
   */
  public static final class Factory extends AbstractProtocolFactory
  {
    private Mapper mapper;

    private TransactionTemplate transactionTemplate;

    public Factory(Mapper mapper, TransactionTemplate transactionTemplate)
    {
      this.mapper = mapper;
      this.transactionTemplate = transactionTemplate;
    }

    public Protocol createProtocol(Channel channel, Object protocolData)
    {
      try
      {
        ServerCDOResProtocolImpl protocol = new ServerCDOResProtocolImpl(channel);
        protocol.setMapper(mapper);
        protocol.setTransactionTemplate(transactionTemplate);
        protocol.activate();
        return protocol;
      }
      catch (Exception ex)
      {
        CDOServer.LOG.error(ex);
        return null;
      }
    }

    public String getProtocolID()
    {
      return "cdores";
    }

    public Set<Type> getConnectorTypes()
    {
      return ProtocolFactory.FOR_SERVERS;
    }

    public String getID()
    {
      return PROTOCOL_NAME;
    }
  }
}
