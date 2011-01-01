/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 201266
 *    Simon McDuff - bug 213402
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

  /**
   * @since 3.0
   */
  @Override
  protected void addIDMappings(InternalCommitContext commitContext, OMMonitor monitor)
  {
    try
    {
      LongIDStore longIDStore = (LongIDStore)getStore();
      CDORevision[] newObjects = commitContext.getNewObjects();
      monitor.begin(newObjects.length);
      for (CDORevision revision : newObjects)
      {
        CDOID id = revision.getID();
        if (id instanceof CDOIDTemp)
        {
          CDOIDTemp oldID = (CDOIDTemp)id;
          CDOID newID = longIDStore.getNextCDOID(this, revision);
          if (CDOIDUtil.isNull(newID) || newID.isTemporary())
          {
            throw new IllegalStateException("newID=" + newID); //$NON-NLS-1$
          }

          commitContext.addIDMapping(oldID, newID);
        }

        monitor.worked();
      }
    }
    finally
    {
      monitor.done();
    }
  }
}
