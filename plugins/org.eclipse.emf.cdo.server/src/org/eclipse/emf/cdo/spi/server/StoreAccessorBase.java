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
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.ITransaction;

import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public abstract class StoreAccessorBase extends Lifecycle implements IStoreAccessor
{
  private Store store;

  private Object context;

  private boolean reader;

  private StoreAccessorBase(Store store, Object context, boolean reader)
  {
    this.store = store;
    this.context = context;
    this.reader = reader;
  }

  protected StoreAccessorBase(Store store, ISession session)
  {
    this(store, session, true);
  }

  protected StoreAccessorBase(Store store, ITransaction transaction)
  {
    this(store, transaction, false);
  }

  void setContext(Object context)
  {
    this.context = context;
  }

  public Store getStore()
  {
    return store;
  }

  public boolean isReader()
  {
    return reader;
  }

  /**
   * @since 3.0
   */
  public InternalSession getSession()
  {
    if (context instanceof ITransaction)
    {
      return (InternalSession)((ITransaction)context).getSession();
    }

    return (InternalSession)context;
  }

  public ITransaction getTransaction()
  {
    if (context instanceof ITransaction)
    {
      return (ITransaction)context;
    }

    return null;
  }

  public void release()
  {
    store.releaseAccessor(this);
  }

  /**
   * Add ID mappings for all new objects of a transaction to the commit context. The implementor must, for each new
   * object of the commit context, determine a permanent CDOID and make it known to the context by calling
   * {@link CommitContext#addIDMapping(CDOIDTemp, CDOID)}.
   * 
   * @since 3.0
   */
  protected void addIDMappings(InternalCommitContext commitContext, OMMonitor monitor)
  {
    try
    {
      CDORevision[] newObjects = commitContext.getNewObjects();
      monitor.begin(newObjects.length);
      for (CDORevision revision : newObjects)
      {
        CDOID id = revision.getID();
        if (id instanceof CDOIDTemp)
        {
          CDOIDTemp oldID = (CDOIDTemp)id;
          CDOID newID = getNextCDOID(revision);
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

  /**
   * @since 4.0
   */
  protected abstract CDOID getNextCDOID(CDORevision revision);

  protected void doPassivate() throws Exception
  {
  }

  protected void doUnpassivate() throws Exception
  {
  }
}
