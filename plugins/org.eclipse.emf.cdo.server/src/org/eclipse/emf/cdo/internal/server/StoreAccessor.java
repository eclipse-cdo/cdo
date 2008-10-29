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
import org.eclipse.emf.cdo.spi.common.InternalCDOClass;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

import org.eclipse.net4j.util.lifecycle.Lifecycle;
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

  public InternalCDORevision verifyRevision(CDORevision revision)
  {
    return (InternalCDORevision)revision;
  }

  /**
   * @since 2.0
   */
  public void write(CommitContext context)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing transaction: {0}", getTransaction());
    }

    commitContexts.add(context);
    long timeStamp = context.getTimeStamp();

    writePackages(context.getNewPackages());
    addIDMappings(context);

    context.applyIDMappings();

    writeRevisions(context.getNewObjects());
    if (store.getRepository().isSupportingRevisionDeltas())
    {
      writeRevisionDeltas(context.getDirtyObjectDeltas(), timeStamp);
    }
    else
    {
      writeRevisions(context.getDirtyObjects());
    }

    detachObjects(context.getDetachedObjects(), timeStamp - 1);
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

  protected abstract void rollback(IStoreAccessor.CommitContext commitContext);

  public final void release()
  {
    store.releaseAccessor(this);
    commitContexts.clear();
  }

  /**
   * @since 2.0
   */
  protected void addIDMappings(IStoreAccessor.CommitContext context)
  {
    if (store instanceof LongIDStore)
    {
      LongIDStore longIDStore = (LongIDStore)getStore();
      for (CDORevision revision : context.getNewObjects())
      {
        CDOIDTemp oldID = (CDOIDTemp)revision.getID();
        CDOID newID = longIDStore.getNextCDOID();
        if (newID == null || newID.isNull() || newID.isTemporary())
        {
          throw new IllegalStateException("newID=" + newID);
        }

        context.addIDMapping(oldID, newID);
      }
    }
  }

  protected void writePackages(CDOPackage... cdoPackages)
  {
    for (CDOPackage cdoPackage : cdoPackages)
    {
      writePackage(cdoPackage);
    }
  }

  protected void writePackage(CDOPackage cdoPackage)
  {
    for (CDOClass cdoClass : cdoPackage.getClasses())
    {
      writeClass((InternalCDOClass)cdoClass);
    }
  }

  protected void writeClass(InternalCDOClass cdoClass)
  {
    for (CDOClassProxy superType : cdoClass.getSuperTypeProxies())
    {
      writeSuperType(cdoClass, superType);
    }

    for (CDOFeature feature : cdoClass.getFeatures())
    {
      writeFeature(feature);
    }
  }

  protected void writeSuperType(InternalCDOClass type, CDOClassProxy superType)
  {
    throw new UnsupportedOperationException();
  }

  protected void writeFeature(CDOFeature feature)
  {
    throw new UnsupportedOperationException();
  }

  protected abstract void writeRevisions(CDORevision[] revisions);

  /**
   * @since 2.0
   */
  protected abstract void writeRevisionDeltas(CDORevisionDelta[] revisionDeltas, long created);

  /**
   * @since 2.0
   */
  protected abstract void detachObjects(CDOID[] detachedObjects, long revised);

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
}
