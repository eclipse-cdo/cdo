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


import org.eclipse.net4j.signal.IndicationWithResponse;
import org.eclipse.net4j.util.om.ContextTracer;
import org.eclipse.net4j.util.stream.ExtendedDataInputStream;
import org.eclipse.net4j.util.stream.ExtendedDataOutputStream;

import org.eclipse.emf.cdo.core.CDOResProtocol;
import org.eclipse.emf.cdo.core.CDOResSignals;
import org.eclipse.emf.cdo.core.protocol.ResourceChangeInfo;
import org.eclipse.emf.cdo.server.Mapper;
import org.eclipse.emf.cdo.server.ServerCDOResProtocol;
import org.eclipse.emf.cdo.server.internal.CDOServer;

import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.io.IOException;


/**
 * @author Eike Stepper
 */
public class DeleteResourcesIndication extends IndicationWithResponse implements CDOResSignals
{
  private static final ContextTracer TRACER = new ContextTracer(CDOServer.DEBUG_PROTOCOL,
      DeleteResourcesIndication.class);

  private boolean ok;

  private Mapper mapper;

  private TransactionTemplate transactionTemplate;

  public DeleteResourcesIndication(Mapper mapper, TransactionTemplate transactionTemplate)
  {
    this.mapper = mapper;
    this.transactionTemplate = transactionTemplate;
  }

  @Override
  protected short getSignalID()
  {
    return DELETE_RESOURCES;
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws IOException
  {
    Set<Integer> rids = new HashSet<Integer>();
    for (;;)
    {
      int rid = in.readInt();
      if (rid == CDOResProtocol.NO_MORE_RESOURCES)
      {
        break;
      }

      if (TRACER.isEnabled())
      {
        TRACER.trace("Deleting rid " + rid);
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

  @Override
  protected void responding(ExtendedDataOutputStream out) throws IOException
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Deleted resources: " + ok);
    }

    out.writeBoolean(ok);
  }

  private Set<Long> deleteResources(final Set<Integer> rids)
  {
    try
    {
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
      CDOServer.LOG.error("Error while committing transaction to database", ex);
    }

    return null;
  }

  private void transmitInvalidations(Collection<Long> changedObjectIds)
  {
    if (!changedObjectIds.isEmpty())
    {
      ServerCDOResProtocol cdores = (ServerCDOResProtocol) getProtocol();
      cdores.fireInvalidationNotification(changedObjectIds);
    }
  }

  private void transmitRemovals(Collection<Integer> rids)
  {
    if (!rids.isEmpty())
    {
      ServerCDOResProtocol cdores = (ServerCDOResProtocol) getProtocol();
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

      ServerCDOResProtocol cdores = (ServerCDOResProtocol) getProtocol();
      cdores.fireResourcesChangedNotification(resourceChanges);
    }
  }
}
