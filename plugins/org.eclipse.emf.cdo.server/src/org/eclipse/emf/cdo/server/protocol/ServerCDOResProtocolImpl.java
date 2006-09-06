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

import org.eclipse.emf.cdo.core.protocol.AbstractCDOResProtocol;
import org.eclipse.emf.cdo.core.protocol.ResourceChangeInfo;
import org.eclipse.emf.cdo.server.Mapper;
import org.eclipse.emf.cdo.server.ServerCDOResProtocol;

import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class ServerCDOResProtocolImpl extends AbstractCDOResProtocol implements
    ServerCDOResProtocol
{
  protected Mapper mapper;

  protected TransactionTemplate transactionTemplate;

  protected transient List<InvalidationListener> listeners = new ArrayList<InvalidationListener>();

  public ServerCDOResProtocolImpl()
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
      case QUERY_ALL_RESOURCES:
        return new QueryAllResourcesIndication();

      case DELETE_RESOURCES:
        return new DeleteResourcesIndication();
        
      default:
        throw new ImplementationError("Invalid " + PROTOCOL_NAME + " signalId: " + signalId);
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

  public void fireResourcesChangedNotification(List<ResourceChangeInfo> resourceChanges)
  {
    for (Channel channel : getChannels())
    {
      try
      {
        ResourcesChangedRequest signal = new ResourcesChangedRequest(resourceChanges);
        channel.transmit(signal);
      }
      catch (Exception ex)
      {
        error("Error while notifying resource changes " + resourceChanges, ex);
      }
    }
  }

  public void fireInvalidationNotification(Collection<Long> modifiedOIDs)
  {
    InvalidationListener[] array = listeners.toArray(new InvalidationListener[listeners.size()]);
    for (InvalidationListener listener : array)
    {
      listener.notifyInvalidation(this, modifiedOIDs);
    }
  }

  public void addInvalidationListener(InvalidationListener listener)
  {
    listeners.add(listener);
  }

  public void removeInvalidationListener(InvalidationListener listener)
  {
    listeners.remove(listener);
  }

  protected void validate() throws ValidationException
  {
    super.validate();
    assertNotNull("mapper");
    assertNotNull("transactionTemplate");
  }
}
