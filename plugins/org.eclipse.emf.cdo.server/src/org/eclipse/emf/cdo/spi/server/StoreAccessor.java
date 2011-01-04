/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/201266
 *    Simon McDuff - http://bugs.eclipse.org/213402
 *    Caspar De Groot - https://bugs.eclipse.org/333260
 */
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;

import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class StoreAccessor extends Lifecycle implements IStoreAccessor
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, StoreAccessor.class);

  private List<CommitContext> commitContexts = new ArrayList<CommitContext>();

  private Store store;

  private Object context;

  private boolean reader;

  private StoreAccessor(Store store, Object context, boolean reader)
  {
    this.store = store;
    this.context = context;
    this.reader = reader;
  }

  protected StoreAccessor(Store store, ISession session)
  {
    this(store, session, true);
  }

  protected StoreAccessor(Store store, ITransaction transaction)
  {
    this(store, transaction, false);
  }

  public Store getStore()
  {
    return store;
  }

  public boolean isReader()
  {
    return reader;
  }

  public ISession getSession()
  {
    if (context instanceof ITransaction)
    {
      return ((ITransaction)context).getSession();
    }

    return (ISession)context;
  }

  public ITransaction getTransaction()
  {
    if (context instanceof ITransaction)
    {
      return (ITransaction)context;
    }

    return null;
  }

  public CDOID readResourceID(CDOID folderID, String name, long timeStamp)
  {
    IStoreAccessor.QueryResourcesContext.ExactMatch context = //
    Store.createExactMatchContext(folderID, name, timeStamp);

    queryResources(context);
    return context.getResourceID();
  }

  public InternalCDORevision verifyRevision(InternalCDORevision revision)
  {
    return revision;
  }

  public void write(CommitContext context, OMMonitor monitor)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing transaction: {0}", getTransaction()); //$NON-NLS-1$
    }

    commitContexts.add(context);
    long timeStamp = context.getTimeStamp();
    boolean deltas = store.getRepository().isSupportingRevisionDeltas();
    InternalCDOPackageUnit[] newPackageUnits = context.getNewPackageUnits();
    InternalCDORevision[] newObjects = context.getNewObjects();
    CDOID[] detachedObjects = context.getDetachedObjects();
    int dirtyCount = deltas ? context.getDirtyObjectDeltas().length : context.getDirtyObjects().length;

    try
    {
      monitor.begin(newPackageUnits.length + 2 + newObjects.length + detachedObjects.length + dirtyCount);
      if (newPackageUnits.length != 0)
      {
        writePackageUnits(newPackageUnits, monitor.fork(newPackageUnits.length));
      }

      addIDMappings(context, monitor.fork());
      context.applyIDMappings(monitor.fork());

      if (detachedObjects.length != 0)
      {
        detachObjects(detachedObjects, timeStamp - 1, monitor.fork(detachedObjects.length));
      }

      if (newObjects.length != 0)
      {
        writeRevisions(newObjects, monitor.fork(newObjects.length));
      }

      if (dirtyCount != 0)
      {
        if (deltas)
        {
          writeRevisionDeltas(context.getDirtyObjectDeltas(), timeStamp, monitor.fork(dirtyCount));
        }
        else
        {
          writeRevisions(context.getDirtyObjects(), monitor.fork(dirtyCount));
        }
      }
    }
    finally
    {
      monitor.done();
    }
  }

  public void rollback()
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Rolling back transaction: {0}", getTransaction()); //$NON-NLS-1$
    }

    for (CommitContext commitContext : commitContexts)
    {
      rollback(commitContext);
    }
  }

  protected abstract void rollback(IStoreAccessor.CommitContext commitContext);

  public final void release()
  {
    store.releaseAccessor(this);
    commitContexts.clear();
  }

  /**
   * Add ID mappings for all new objects of a transaction to the commit context. The implementor must, for each new
   * object of the commit context, determine a permanent CDOID and make it known to the context by calling
   * {@link CommitContext#addIDMapping(CDOIDTemp, CDOID)}.
   */
  protected abstract void addIDMappings(CommitContext context, OMMonitor monitor);

  protected abstract void writeRevisions(InternalCDORevision[] revisions, OMMonitor monitor);

  protected abstract void writeRevisionDeltas(InternalCDORevisionDelta[] revisionDeltas, long created, OMMonitor monitor);

  protected abstract void detachObjects(CDOID[] detachedObjects, long revised, OMMonitor monitor);

  @Override
  protected abstract void doActivate() throws Exception;

  @Override
  protected abstract void doDeactivate() throws Exception;

  protected abstract void doPassivate() throws Exception;

  protected abstract void doUnpassivate() throws Exception;

  public void handleRevisions(CDORevisionHandler handler)
  {
    throw new RuntimeException("handleRevisions not supported");
  }
}
