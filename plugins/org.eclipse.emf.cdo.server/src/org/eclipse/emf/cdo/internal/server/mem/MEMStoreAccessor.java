/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 *    Simon McDuff - http://bugs.eclipse.org/213402
 */
package org.eclipse.emf.cdo.internal.server.mem;

import org.eclipse.emf.cdo.common.CDOQueryInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.server.IQueryContext;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.server.LongIDStoreAccessor;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * @author Simon McDuff
 */
public class MEMStoreAccessor extends LongIDStoreAccessor
{
  private final IQueryHandler testQueryHandler = new IQueryHandler()
  {
    public void executeQuery(CDOQueryInfo info, IQueryContext queryContext)
    {
      List<Object> filters = new ArrayList<Object>();
      Object context = info.getParameters().get("context"); //$NON-NLS-1$
      Long sleep = (Long)info.getParameters().get("sleep"); //$NON-NLS-1$
      if (context != null)
      {
        if (context instanceof EClass)
        {
          final EClass eClass = (EClass)context;
          filters.add(new Object()
          {
            @Override
            public boolean equals(Object obj)
            {
              InternalCDORevision revision = (InternalCDORevision)obj;
              return revision.getEClass().equals(eClass);
            }
          });
        }
      }

      for (InternalCDORevision revision : getStore().getCurrentRevisions())
      {
        if (sleep != null)
        {
          try
          {
            Thread.sleep(sleep);
          }
          catch (InterruptedException ex)
          {
            throw WrappedException.wrap(ex);
          }
        }

        boolean valid = true;

        for (Object filter : filters)
        {
          if (!filter.equals(revision))
          {
            valid = false;
            break;
          }
        }

        if (valid)
        {
          if (!queryContext.addResult(revision))
          {
            // No more results allowed
            break;
          }
        }
      }
    }
  };

  private List<InternalCDORevision> newRevisions = new ArrayList<InternalCDORevision>();

  public MEMStoreAccessor(MEMStore store, ISession session)
  {
    super(store, session);
  }

  /**
   * @since 2.0
   */
  public MEMStoreAccessor(MEMStore store, ITransaction transaction)
  {
    super(store, transaction);
  }

  @Override
  public MEMStore getStore()
  {
    return (MEMStore)super.getStore();
  }

  /**
   * @since 2.0
   */
  public MEMStoreChunkReader createChunkReader(InternalCDORevision revision, EStructuralFeature feature)
  {
    return new MEMStoreChunkReader(this, revision, feature);
  }

  public Collection<InternalCDOPackageUnit> readPackageUnits()
  {
    return Collections.emptySet();
  }

  public EPackage[] loadPackageUnit(InternalCDOPackageUnit packageUnit)
  {
    throw new UnsupportedOperationException();
  }

  public InternalCDORevision readRevision(CDOID id, int listChunk, AdditionalRevisionCache cache)
  {
    InternalCDORevision storeRevision = getStore().getRevision(id);
    // IRevisionManager revisionManager = getStore().getRepository().getRevisionManager();
    // InternalCDORevision newRevision = new InternalCDORevision(revisionManager, storeRevision.getEClass(),
    // storeRevision
    // .getID());
    // newRevision.setResourceID(storeRevision.getResourceID());
    //
    // for (EStructuralFeature feature : storeRevision.TODO.getAllPersistentFeatures(getEClass()))
    // {
    // if (feature.isMany())
    // {
    // newRevision.setListSize(feature, storeRevision.getList(feature).size());
    // MoveableList<Object> list = newRevision.getList(feature);
    // int size = referenceChunk == CDORevision.UNCHUNKED ? list.size() : referenceChunk;
    // for (int i = 0; i < size; i++)
    // {
    // list.set(i, storeRevision.get(feature, i));
    // }
    // }
    // }
    //
    // return newRevision;
    return storeRevision;
  }

  public InternalCDORevision readRevisionByTime(CDOID id, int listChunk, AdditionalRevisionCache cache, long timeStamp)
  {
    return getStore().getRevisionByTime(id, timeStamp);
  }

  public InternalCDORevision readRevisionByVersion(CDOID id, int listChunk, AdditionalRevisionCache cache, int version)
  {
    return getStore().getRevisionByVersion(id, version);
  }

  /**
   * @since 2.0
   */
  public void commit(OMMonitor monitor)
  {
    // Do nothing
  }

  @Override
  public void write(CommitContext context, OMMonitor monitor)
  {
    MEMStore store = getStore();
    synchronized (store)
    {
      super.write(context, monitor);
    }
  }

  @Override
  protected void rollback(CommitContext context)
  {
    MEMStore store = getStore();
    synchronized (store)
    {
      for (InternalCDORevision revision : newRevisions)
      {
        store.rollbackRevision(revision);
      }
    }
  }

  public void writePackageUnits(InternalCDOPackageUnit[] packageUnits, OMMonitor monitor)
  {
    // Do nothing
  }

  @Override
  protected void writeRevisions(InternalCDORevision[] revisions, OMMonitor monitor)
  {
    for (InternalCDORevision revision : revisions)
    {
      writeRevision(revision);
    }
  }

  protected void writeRevision(InternalCDORevision revision)
  {
    newRevisions.add(revision);
    getStore().addRevision(revision);
  }

  /**
   * @since 2.0
   */
  @Override
  protected void writeRevisionDeltas(InternalCDORevisionDelta[] revisionDeltas, long created, OMMonitor monitor)
  {
    for (InternalCDORevisionDelta revisionDelta : revisionDeltas)
    {
      writeRevisionDelta(revisionDelta, created);
    }
  }

  /**
   * @since 2.0
   */
  protected void writeRevisionDelta(InternalCDORevisionDelta revisionDelta, long created)
  {
    InternalCDORevision revision = getStore().getRevision(revisionDelta.getID());
    if (revision.getVersion() != revisionDelta.getOriginVersion())
    {
      throw new ConcurrentModificationException("Trying to update object " + revisionDelta.getID() //$NON-NLS-1$
          + " that was already modified"); //$NON-NLS-1$
    }

    InternalCDORevision newRevision = (InternalCDORevision)revision.copy();
    revisionDelta.apply(newRevision);
    newRevision.setCreated(created);
    writeRevision(newRevision);
  }

  @Override
  protected void detachObjects(CDOID[] detachedObjects, long revised, OMMonitor monitor)
  {
    for (CDOID id : detachedObjects)
    {
      detachObject(id);
    }
  }

  /**
   * @since 2.0
   */
  protected void detachObject(CDOID id)
  {
    getStore().removeID(id);
  }

  /**
   * @since 2.0
   */
  public void queryResources(QueryResourcesContext context)
  {
    getStore().queryResources(context);
  }

  public IQueryHandler getQueryHandler(CDOQueryInfo info)
  {
    if ("TEST".equals(info.getQueryLanguage())) //$NON-NLS-1$
    {
      return testQueryHandler;
    }

    return null;
  }

  /**
   * @since 2.0
   */
  public void refreshRevisions()
  {
  }

  @Override
  protected void doActivate() throws Exception
  {
    // Do nothing
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    newRevisions.clear();
  }

  @Override
  protected void doPassivate() throws Exception
  {
    // Pooling of store accessors not supported
  }

  @Override
  protected void doUnpassivate() throws Exception
  {
    // Pooling of store accessors not supported
  }

  public void handleRevisions(CDORevisionHandler handler)
  {
    getStore().handleRevisions(handler);
  }
}
