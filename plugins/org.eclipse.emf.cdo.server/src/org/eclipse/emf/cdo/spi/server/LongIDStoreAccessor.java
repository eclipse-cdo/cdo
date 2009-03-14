/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/201266
 *    Simon McDuff - http://bugs.eclipse.org/213402
 */
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.ITransaction;

import org.eclipse.net4j.util.om.monitor.OMMonitor;

/**
 * @since 2.0
 * @author Eike Stepper
 */
public abstract class LongIDStoreAccessor extends StoreAccessor
{
  protected LongIDStoreAccessor(Store store, ISession session)
  {
    super(store, session);
  }

  protected LongIDStoreAccessor(Store store, ITransaction transaction)
  {
    super(store, transaction);
  }

  @Override
  protected void addIDMappings(CommitContext context, OMMonitor monitor)
  {
    try
    {
      LongIDStore longIDStore = (LongIDStore)getStore();
      CDORevision[] newObjects = context.getNewObjects();
      monitor.begin(newObjects.length);
      for (CDORevision revision : newObjects)
      {
        CDOIDTemp oldID = (CDOIDTemp)revision.getID();
        CDOID newID = longIDStore.getNextCDOID();
        if (CDOIDUtil.isNull(newID) || newID.isTemporary())
        {
          throw new IllegalStateException("newID=" + newID);
        }

        context.addIDMapping(oldID, newID);
        monitor.worked();
      }
    }
    finally
    {
      monitor.done();
    }
  }
}
