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


import org.eclipse.net4j.core.Channel;
import org.eclipse.net4j.core.Indication;
import org.eclipse.net4j.spring.ValidationException;
import org.eclipse.net4j.util.ImplementationError;

import org.eclipse.emf.cdo.core.CDOProtocol;
import org.eclipse.emf.cdo.core.protocol.AbstractCDOProtocol;
import org.eclipse.emf.cdo.server.Mapper;
import org.eclipse.emf.cdo.server.ServerCDOProtocol;
import org.eclipse.emf.cdo.server.ServerCDOResProtocol;
import org.eclipse.emf.cdo.server.ServerCDOResProtocol.Listener;

import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collection;


public class ServerCDOProtocolImpl extends AbstractCDOProtocol implements ServerCDOProtocol,
    Listener
{
  protected Mapper mapper;

  protected TransactionTemplate transactionTemplate;

  protected ServerCDOResProtocol serverCDOResProtocol;

  public ServerCDOProtocolImpl()
  {
  }

  public int getType()
  {
    return SERVER;
  }

  public Indication createIndication(short signalId)
  {
    switch (signalId)
    {
      case ANNOUNCE_PACKAGE:
        return new AnnouncePackageIndication();

      case DESCRIBE_PACKAGE:
        return new DescribePackageIndication();

      case RESOURCE_RID:
        return new ResourceRIDIndication();

      case RESOURCE_PATH:
        return new ResourcePathIndication();

      case LOAD_RESOURCE:
        return new LoadResourceIndication();

      case LOAD_OBJECT:
        return new LoadObjectIndication();

      case COMMIT_TRANSACTION:
        return new CommitTransactionIndication();

      case QUERY_EXTENT:
        return new QueryExtentIndication();

      case QUERY_XREFS:
        return new QueryXRefsIndication();

      default:
        throw new ImplementationError("Invalid " + CDOProtocol.PROTOCOL_NAME + " signalId: "
            + signalId);
    }
  }

  public Mapper getMapper()
  {
    return mapper;
  }

  public void setMapper(Mapper mapper)
  {
    doSet("mapper", mapper);
  }

  public TransactionTemplate getTransactionTemplate()
  {
    return transactionTemplate;
  }

  public void setTransactionTemplate(TransactionTemplate transactionTemplate)
  {
    doSet("transactionTemplate", transactionTemplate);
  }

  public ServerCDOResProtocol getServerCDOResProtocol()
  {
    return serverCDOResProtocol;
  }

  public void setServerCDOResProtocol(ServerCDOResProtocol serverCDOResProtocol)
  {
    doSet("serverCDOResProtocol", serverCDOResProtocol);
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
    for (Channel channel : getChannels())
    {
      try
      {
        RemovalNotificationRequest signal = new RemovalNotificationRequest(rids);
        channel.transmit(signal);
      }
      catch (Exception ex)
      {
        error("Error while transmitting removal notifications for rids " + rids, ex);
      }
    }
  }

  public void fireInvalidationNotification(Channel initiator, Collection<Long> changedObjectIds)
  {
    for (Channel channel : getChannels())
    {
      if (initiator == null || channel != initiator
          && channel.getConnector().getType() == initiator.getConnector().getType())
      {
        try
        {
          InvalidationNotificationRequest signal = new InvalidationNotificationRequest(
              changedObjectIds);
          channel.transmit(signal);
        }
        catch (Exception ex)
        {
          error("Error while transmitting invalidation notifications for oids " + changedObjectIds, ex);
        }
      }
    }
  }

  @Override
  protected void validate() throws ValidationException
  {
    super.validate();
    assertNotNull("mapper");
    assertNotNull("transactionTemplate");
  }

  @Override
  protected void activate() throws Exception
  {
    super.activate();
    if (serverCDOResProtocol != null)
    {
      serverCDOResProtocol.addListener(this);
    }
  }

  @Override
  protected void deactivate() throws Exception
  {
    if (serverCDOResProtocol != null)
    {
      serverCDOResProtocol.removeListener(this);
    }

    super.deactivate();
  }
}
