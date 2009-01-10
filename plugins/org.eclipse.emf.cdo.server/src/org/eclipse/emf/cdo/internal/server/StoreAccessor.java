/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/201266
 *    Simon McDuff - http://bugs.eclipse.org/213402
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOClassProxy;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.StoreUtil;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOClass;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOFeature;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackage;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
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

  /**
   * @since 2.0
   */
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

  /**
   * @since 2.0
   */
  public ITransaction getTransaction()
  {
    if (context instanceof ITransaction)
    {
      return (ITransaction)context;
    }

    return null;
  }

  /**
   * @since 2.0
   */
  public CDOID readResourceID(CDOID folderID, String name, long timeStamp)
  {
    IStoreAccessor.QueryResourcesContext.ExactMatch context = //
    StoreUtil.createExactMatchContext(folderID, name, timeStamp);

    queryResources(context);
    return context.getResourceID();
  }

  /**
   * @since 2.0
   */
  public InternalCDORevision verifyRevision(CDORevision revision)
  {
    return (InternalCDORevision)revision;
  }

  /**
   * @since 2.0
   */
  public void write(CommitContext context, OMMonitor monitor)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing transaction: {0}", getTransaction());
    }

    commitContexts.add(context);
    long timeStamp = context.getTimeStamp();
    boolean deltas = store.getRepository().isSupportingRevisionDeltas();
    CDOPackage[] newPackages = context.getNewPackages();
    CDORevision[] newObjects = context.getNewObjects();
    CDOID[] detachedObjects = context.getDetachedObjects();
    int dirtyCount = deltas ? context.getDirtyObjectDeltas().length : context.getDirtyObjects().length;

    try
    {
      monitor.begin(newPackages.length + newObjects.length + detachedObjects.length + dirtyCount + 2);
      writePackages(newPackages, monitor.fork(newPackages.length));
      addIDMappings(context, monitor.fork());
      context.applyIDMappings(monitor.fork());

      writeRevisions(newObjects, monitor.fork(newObjects.length));
      if (deltas)
      {
        writeRevisionDeltas(context.getDirtyObjectDeltas(), timeStamp, monitor.fork(dirtyCount));
      }
      else
      {
        writeRevisions(context.getDirtyObjects(), monitor.fork(dirtyCount));
      }

      detachObjects(detachedObjects, timeStamp - 1, monitor.fork(detachedObjects.length));
    }
    finally
    {
      monitor.done();
    }
  }

  /**
   * @since 2.0
   */
  public void rollback()
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Rolling back transaction: {0}", getTransaction());
    }

    for (CommitContext commitContext : commitContexts)
    {
      rollback(commitContext);
    }
  }

  /**
   * @since 2.0
   */
  protected abstract void rollback(IStoreAccessor.CommitContext commitContext);

  public final void release()
  {
    store.releaseAccessor(this);
    commitContexts.clear();
  }

  /**
   * @since 2.0
   */
  protected void addIDMappings(CommitContext context, OMMonitor monitor)
  {
    try
    {
      if (store instanceof LongIDStore)
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
    }
    finally
    {
      monitor.done();
    }
  }

  /**
   * @since 2.0
   */
  protected abstract void writePackages(CDOPackage[] cdoPackages, OMMonitor monitor);

  /**
   * @since 2.0
   */
  protected abstract void writeRevisions(CDORevision[] revisions, OMMonitor monitor);

  /**
   * @since 2.0
   */
  protected abstract void writeRevisionDeltas(CDORevisionDelta[] revisionDeltas, long created, OMMonitor monitor);

  /**
   * @since 2.0
   */
  protected abstract void detachObjects(CDOID[] detachedObjects, long revised, OMMonitor monitor);

  /**
   * @since 2.0
   */
  @Override
  protected abstract void doActivate() throws Exception;

  /**
   * @since 2.0
   */
  @Override
  protected abstract void doDeactivate() throws Exception;

  /**
   * @since 2.0
   */
  protected abstract void doPassivate() throws Exception;

  /**
   * @since 2.0
   */
  protected abstract void doUnpassivate() throws Exception;

  /**
   * @author Eike Stepper
   * @since 2.0
   */
  public static abstract class PackageWriter implements Runnable
  {
    private CDOPackage[] cdoPackages;

    private OMMonitor mainMonitor;

    public PackageWriter(CDOPackage[] cdoPackages, OMMonitor monitor)
    {
      this.cdoPackages = cdoPackages;
      mainMonitor = monitor;
    }

    public CDOPackage[] getCDOPackages()
    {
      return cdoPackages;
    }

    public OMMonitor getMonitor()
    {
      return mainMonitor;
    }

    public void run()
    {
      try
      {
        mainMonitor.begin(cdoPackages.length);
        for (CDOPackage cdoPackage : cdoPackages)
        {
          runPackage(cdoPackage, mainMonitor.fork());
        }
      }
      finally
      {
        mainMonitor.done();
      }
    }

    protected void runPackage(CDOPackage cdoPackage, OMMonitor monitor)
    {
      try
      {
        CDOClass[] classes = cdoPackage.getClasses();
        monitor.begin(1 + classes.length);

        writePackage((InternalCDOPackage)cdoPackage, monitor.fork());
        for (CDOClass cdoClass : classes)
        {
          runClass((InternalCDOClass)cdoClass, monitor.fork());
        }
      }
      finally
      {
        monitor.done();
      }
    }

    protected void runClass(InternalCDOClass cdoClass, OMMonitor monitor)
    {
      try
      {
        List<CDOClassProxy> superTypeProxies = cdoClass.getSuperTypeProxies();
        CDOFeature[] features = cdoClass.getFeatures();
        monitor.begin(1 + superTypeProxies.size() + features.length);

        writeClass(cdoClass, monitor.fork());

        for (CDOClassProxy superType : superTypeProxies)
        {
          writeSuperType(cdoClass, superType, monitor.fork());
        }

        for (CDOFeature feature : features)
        {
          writeFeature((InternalCDOFeature)feature, monitor.fork());
        }
      }
      finally
      {
        monitor.done();
      }
    }

    protected abstract void writePackage(InternalCDOPackage cdoPackage, OMMonitor monitor);

    protected abstract void writeClass(InternalCDOClass cdoClass, OMMonitor monitor);

    protected abstract void writeSuperType(InternalCDOClass type, CDOClassProxy superType, OMMonitor monitor);

    protected abstract void writeFeature(InternalCDOFeature feature, OMMonitor monitor);
  }
}
