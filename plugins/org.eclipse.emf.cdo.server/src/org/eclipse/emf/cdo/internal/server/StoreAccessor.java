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
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.IStoreWriter.CommitContext;
import org.eclipse.emf.cdo.spi.common.InternalCDOClass;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

import org.eclipse.net4j.util.om.trace.ContextTracer;

/**
 * @author Eike Stepper
 */
public class StoreAccessor implements IStoreAccessor
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, StoreAccessor.class);

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

  protected StoreAccessor(Store store, IView view)
  {
    this(store, view, false);
  }

  public final void release()
  {
    store.releaseAccessor(this);
  }

  protected void doRelease()
  {
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
    if (context instanceof IView)
    {
      return ((IView)context).getSession();
    }

    return (ISession)context;
  }

  public IView getView()
  {
    if (context instanceof IView)
    {
      return (IView)context;
    }

    return null;
  }

  public InternalCDORevision verifyRevision(CDORevision revision)
  {
    return (InternalCDORevision)revision;
  }

  public void commit(CommitContext context)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Committing transaction: {0}", getView());
    }

    writePackages(context.getNewPackages());
    addIDMappings(context);
    context.applyIDMappings();
    writeRevisions(context.getNewObjects());
    if (store.hasWriteDeltaSupport() && store.getRepository().isSupportingRevisionDeltas())
    {
      writeRevisionDeltas(context.getDirtyObjectDeltas());
    }
    else
    {
      writeRevisions(context.getDirtyObjects());
    }
  }

  public void rollback(CommitContext context)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Rolling back transaction: {0}", getView());
    }
  }

  protected void addIDMappings(CommitContext context)
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

  protected void writeRevisions(CDORevision[] revisions)
  {
    for (CDORevision revision : revisions)
    {
      writeRevision(revision);
    }
  }

  protected void writeRevision(CDORevision revision)
  {
    throw new UnsupportedOperationException();
  }

  protected void writeRevisionDeltas(CDORevisionDelta[] revisionDeltas)
  {
    for (CDORevisionDelta revisionDelta : revisionDeltas)
    {
      writeRevisionDelta(revisionDelta);
    }
  }

  protected void writeRevisionDelta(CDORevisionDelta revisionDelta)
  {
    throw new UnsupportedOperationException();
  }
}
