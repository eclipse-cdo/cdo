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
import org.eclipse.net4j.core.impl.AbstractIndicationWithResponse;

import org.eclipse.emf.cdo.core.CDOResProtocol;
import org.eclipse.emf.cdo.core.CDOResSignals;
import org.eclipse.emf.cdo.core.protocol.ResourceChangeInfo;
import org.eclipse.emf.cdo.server.Mapper;
import org.eclipse.emf.cdo.server.ServerCDOResProtocol;

import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class DeleteResourcesIndication extends AbstractIndicationWithResponse implements
    CDOResSignals
{
  private boolean ok;

  public DeleteResourcesIndication()
  {
  }

  public short getSignalId()
  {
    return DELETE_RESOURCES;
  }

  public void indicate()
  {
    Set<Integer> rids = new HashSet<Integer>();
    for (;;)
    {
      int rid = receiveInt();
      if (rid == CDOResProtocol.NO_MORE_RESOURCES) break;

      if (isDebugEnabled())
      {
        debug("Deleting rid " + rid);
      }

      rids.add(rid);
    }

    Set<Long> modifiedOIDs = deleteResources(rids);
    if (modifiedOIDs != null)
    {
      ok = true;
      transmitInvalidations(modifiedOIDs);
      transmitResourceChanges(rids);
      transmitRemovals(rids);
    }
    else
    {
      ok = false;
    }
  }

  public void respond()
  {
    if (isDebugEnabled())
    {
      debug("Deleted resources: " + ok);
    }

    transmitBoolean(ok);
  }

  private Set<Long> deleteResources(final Set<Integer> rids)
  {
    try
    {
      ServerCDOResProtocol protocol = (ServerCDOResProtocol) getProtocol();
      final Mapper mapper = protocol.getMapper();

      TransactionTemplate transactionTemplate = protocol.getTransactionTemplate();
      return (Set<Long>) transactionTemplate.execute(new TransactionCallback()
      {
        public Object doInTransaction(TransactionStatus status)
        {
          for (Integer rid : rids)
          {
            mapper.deleteResource(rid);
          }

          return mapper.removeStaleReferences();
        }
      });
    }
    catch (TransactionException ex)
    {
      error("Error while committing transaction to database", ex);
    }

    return null;
  }

  private void transmitInvalidations(Collection<Long> changedObjectIds)
  {
    if (!changedObjectIds.isEmpty())
    {
      Channel me = getChannel();
      ServerCDOResProtocol cdores = (ServerCDOResProtocol) me.getProtocol();
      cdores.fireInvalidationNotification(changedObjectIds);
    }
  }

  private void transmitRemovals(Collection<Integer> rids)
  {
    if (!rids.isEmpty())
    {
      Channel me = getChannel();
      ServerCDOResProtocol cdores = (ServerCDOResProtocol) me.getProtocol();
      cdores.fireRemovalNotification(rids);
    }
  }
  
  private void transmitResourceChanges(Collection<Integer> rids)
  {
    if (!rids.isEmpty())
    {
      List<ResourceChangeInfo> resourceChanges = new ArrayList<ResourceChangeInfo>();
      for (Integer rid : rids)
      {
        ResourceChangeInfo info = new ResourceChangeInfo(ResourceChangeInfo.REMOVED, rid, null);
        resourceChanges.add(info);
      }

      Channel me = getChannel();
      ServerCDOResProtocol cdores = (ServerCDOResProtocol) me.getProtocol();
      cdores.fireResourcesChangedNotification(resourceChanges);
    }
  }
}
