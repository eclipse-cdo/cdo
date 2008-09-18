/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 *    Simon McDuff - http://bugs.eclipse.org/213402
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOClassRef;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.common.model.resource.CDOPathFeature;
import org.eclipse.emf.cdo.common.query.CDOQueryInfo;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.server.IPackageManager;
import org.eclipse.emf.cdo.server.IQueryContext;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStoreChunkReader;
import org.eclipse.emf.cdo.server.IStoreReader;
import org.eclipse.emf.cdo.server.IStoreWriter;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.collection.CloseableIterator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Simon McDuff
 */
public class MEMStoreAccessor extends StoreAccessor implements IStoreReader, IStoreWriter
{
  private List<CDORevision> newRevisions = new ArrayList<CDORevision>();

  public MEMStoreAccessor(MEMStore store, ISession session)
  {
    super(store, session);
  }

  public MEMStoreAccessor(MEMStore store, IView view)
  {
    super(store, view);
  }

  @Override
  public MEMStore getStore()
  {
    return (MEMStore)super.getStore();
  }

  public IStoreChunkReader createChunkReader(CDORevision revision, CDOFeature feature)
  {
    return new MEMStoreChunkReader(this, revision, feature);
  }

  public Collection<CDOPackageInfo> readPackageInfos()
  {
    return Collections.emptySet();
  }

  public void readPackage(CDOPackage cdoPackage)
  {
    throw new UnsupportedOperationException();
  }

  public CloseableIterator<CDOID> readObjectIDs()
  {
    throw new UnsupportedOperationException();
  }

  public CDOClassRef readObjectType(CDOID id)
  {
    InternalCDORevision storeRevision = (InternalCDORevision)getStore().getRevision(id);
    return storeRevision.getCDOClass().createClassRef();
  }

  public CDORevision readRevision(CDOID id, int referenceChunk)
  {
    InternalCDORevision storeRevision = (InternalCDORevision)getStore().getRevision(id);
    // IRevisionManager revisionManager = getStore().getRepository().getRevisionManager();
    // InternalCDORevision newRevision = new InternalCDORevision(revisionManager, storeRevision.getCDOClass(),
    // storeRevision
    // .getID());
    // newRevision.setResourceID(storeRevision.getResourceID());
    //
    // for (CDOFeature feature : storeRevision.getCDOClass().getAllFeatures())
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

  public CDORevision readRevisionByTime(CDOID id, int referenceChunk, long timeStamp)
  {
    throw new UnsupportedOperationException();
  }

  public CDORevision readRevisionByVersion(CDOID id, int referenceChunk, int version)
  {
    return getStore().getRevisionByVersion(id, version);
  }

  public CDOID readResourceID(String path)
  {
    CDORevision revision = getStore().getResource(path);
    return revision == null ? null : revision.getID();
  }

  public String readResourcePath(CDOID id)
  {
    CDORevision revision = getStore().getRevision(id);
    return getResourcePath(revision);
  }

  @Override
  public void write(CommitContext context)
  {
    MEMStore store = getStore();
    synchronized (store)
    {
      super.write(context);
    }
  }

  @Override
  public void rollback(CommitContext context)
  {
    MEMStore store = getStore();
    synchronized (store)
    {
      super.rollback(context);
      for (CDORevision revision : newRevisions)
      {
        store.removeRevision(revision);
      }
    }
  }

  @Override
  protected void writePackages(CDOPackage... cdoPackages)
  {
    // Do nothing
  }

  @Override
  protected void writeRevision(CDORevision revision)
  {
    newRevisions.add(revision);
    getStore().addRevision(revision);
  }

  @Override
  protected void writeRevisionDelta(CDORevisionDelta revisionDelta)
  {
    CDORevision revision = getStore().getRevision(revisionDelta.getID());
    CDORevision newRevision = CDORevisionUtil.copy(revision);
    revisionDelta.apply(newRevision);
    writeRevision(newRevision);
  }

  @Override
  protected void detachObject(CDOID id)
  {
    getStore().removeID(id);
  }

  /**
   * @since 2.0
   */
  public void queryResources(QueryResourcesContext context)
  {
    IView view = getView();
    for (CDORevision revision : getStore().getCurrentRevisions())
    {
      if (revision.isResource())
      {
        String path = getResourcePath(revision);
        if (path != null && path.startsWith(context.getPathPrefix()))
        {
          if (!context.addResource(revision.getID()))
          {
            break;
          }
        }
      }
    }
  }

  /**
   * @since 2.0
   */
  public void executeQuery(CDOQueryInfo info, IQueryContext queryContext)
  {
    if (info.getQueryLanguage().equals("TEST"))
    {
      List<Object> filters = new ArrayList<Object>();
      Object context = info.getParameters().get("context");
      Long sleep = (Long)info.getParameters().get("sleep");
      if (context != null)
      {
        if (context instanceof CDOClass)
        {
          final CDOClass cdoClass = (CDOClass)context;
          filters.add(new Object()
          {
            @Override
            public boolean equals(Object obj)
            {
              CDORevision revision = (CDORevision)obj;
              return revision.getCDOClass().equals(cdoClass);
            }
          });
        }
      }

      for (CDORevision revision : getStore().getCurrentRevisions())
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
            break;
          }
        }
      }
    }

    else
    {
      throw new RuntimeException("Unsupported language " + info.getQueryLanguage());
    }
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

  private CDOPathFeature getResourcePathFeature()
  {
    IPackageManager packageManager = getStore().getRepository().getPackageManager();
    return packageManager.getCDOResourcePackage().getCDOResourceClass().getCDOPathFeature();
  }

  private String getResourcePath(CDORevision revision)
  {
    CDOPathFeature pathFeature = getResourcePathFeature();
    return (String)revision.getData().get(pathFeature, 0);
  }
}
