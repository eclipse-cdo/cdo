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

import org.eclipse.emf.cdo.core.CDOProtocol;
import org.eclipse.emf.cdo.core.ImplementationError;
import org.eclipse.emf.cdo.core.protocol.AbstractCDOProtocol;
import org.eclipse.emf.cdo.server.Mapper;
import org.eclipse.emf.cdo.server.ServerCDOProtocol;
import org.eclipse.emf.cdo.server.ServerCDOResProtocol;
import org.eclipse.emf.cdo.server.ServerCDOResProtocol.Listener;
import org.eclipse.emf.cdo.server.internal.CDOServer;

import org.eclipse.internal.net4j.transport.AbstractProtocolFactory;

import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collection;
import java.util.Set;


/**
 * @author Eike Stepper
 */
public class ServerCDOProtocolImpl extends AbstractCDOProtocol implements ServerCDOProtocol,
    Listener
{
  protected Mapper mapper;

  protected TransactionTemplate transactionTemplate;

  protected ServerCDOResProtocol serverCDOResProtocol;

  public ServerCDOProtocolImpl(Channel channel)
  {
    super(channel);
  }

  @Override
  protected SignalReactor createSignalReactor(short signalID)
  {
    switch (signalID)
    {
      case ANNOUNCE_PACKAGE:
        return new AnnouncePackageIndication(mapper);
      case DESCRIBE_PACKAGE:
        return new DescribePackageIndication(mapper);
      case RESOURCE_RID:
        return new ResourceRIDIndication(mapper);
      case RESOURCE_PATH:
        return new ResourcePathIndication(mapper);
      case LOAD_RESOURCE:
        return new LoadResourceIndication(mapper);
      case LOAD_OBJECT:
        return new LoadObjectIndication(mapper);
      case COMMIT_TRANSACTION:
        return new CommitTransactionIndication(mapper, transactionTemplate);
      case QUERY_EXTENT:
        return new QueryExtentIndication(mapper);
      case QUERY_XREFS:
        return new QueryXRefsIndication(mapper);
      default:
        throw new ImplementationError("Invalid " + CDOProtocol.PROTOCOL_NAME + " signalID: "
            + signalID);
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

  public ServerCDOResProtocol getServerCDOResProtocol()
  {
    return serverCDOResProtocol;
  }

  public void setServerCDOResProtocol(ServerCDOResProtocol serverCDOResProtocol)
  {
    this.serverCDOResProtocol = serverCDOResProtocol;
  }

  public void notifyRemoval(ServerCDOResProtocol protocol, Collection<Integer> rids)
  {
    fireRemovalNotification(rids);
  }

  public void notifyInvalidation(ServerCDOResProtocol protocol, Collection<Long> modifiedOIDs)
  {
    fireInvalidationNotification(null, modifiedOIDs);
  }

  public void fireRemovalNotification(Collection<Integer> rids)
  {
    for (Channel channel : getCDOServerChannels())
    {
      try
      {
        Request signal = new RemovalNotificationRequest(channel, rids);
        signal.send();
      }
      catch (Exception ex)
      {
        CDOServer.LOG.error("Error while transmitting removal notifications for rids " + rids, ex);
      }
    }
  }

  public void fireInvalidationNotification(Channel initiator, Collection<Long> changedObjectIds)
  {
    for (Channel channel : getCDOServerChannels())
    {
      if (initiator == null || channel != initiator
          && channel.getConnector().getType() == initiator.getConnector().getType())
      {
        try
        {
          Request signal = new InvalidationNotificationRequest(channel, changedObjectIds);
          signal.send();
        }
        catch (Exception ex)
        {
          CDOServer.LOG.error("Error while transmitting invalidation notifications for oids "
              + changedObjectIds, ex);
        }
      }
    }
  }

  protected Collection<Channel> getCDOServerChannels()
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
  protected void onActivate() throws Exception
  {
    super.onActivate();
    if (serverCDOResProtocol != null)
    {
      serverCDOResProtocol.addListener(this);
    }
  }

  @Override
  protected void onDeactivate() throws Exception
  {
    if (serverCDOResProtocol != null)
    {
      serverCDOResProtocol.removeListener(this);
      serverCDOResProtocol = null;
    }

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
        ServerCDOProtocolImpl protocol = new ServerCDOProtocolImpl(channel);
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
