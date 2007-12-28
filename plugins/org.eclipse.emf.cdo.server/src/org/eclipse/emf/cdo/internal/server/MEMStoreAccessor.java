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
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.internal.protocol.model.CDOPackageImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.delta.CDORevisionDeltaImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.model.CDOClassRef;
import org.eclipse.emf.cdo.protocol.model.CDOFeature;
import org.eclipse.emf.cdo.protocol.model.CDOPackageInfo;
import org.eclipse.emf.cdo.protocol.revision.CDODuplicateRevisionException;
import org.eclipse.emf.cdo.protocol.revision.CDORevision;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStoreChunkReader;
import org.eclipse.emf.cdo.server.IStoreWriter;
import org.eclipse.emf.cdo.server.IView;

import org.eclipse.net4j.util.io.CloseableIterator;
import org.eclipse.net4j.util.transaction.ITransaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Simon McDuff
 */
public class MEMStoreAccessor extends StoreAccessor
{
  List<CDORevisionImpl> listToCommit = new ArrayList<CDORevisionImpl>();

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

  public void readPackage(CDOPackageImpl cdoPackage)
  {
    throw new UnsupportedOperationException();
  }

  public CloseableIterator<CDOID> readObjectIDs(boolean withTypes)
  {
    throw new UnsupportedOperationException();
  }

  public CDOClassRef readObjectType(CDOID id)
  {
    throw new UnsupportedOperationException();
  }

  public CDORevision readRevision(CDOID id, int referenceChunk)
  {
    CDORevisionImpl revStore = (CDORevisionImpl)getStore().getRevision(id);
    CDORevisionImpl newRevision = new CDORevisionImpl(this.getStore().getRepository().getRevisionManager(), revStore
        .getCDOClass(), revStore.getID());
    newRevision.setResourceID(revStore.getResourceID());

    for (CDOFeature feature : revStore.getCDOClass().getAllFeatures())
    {
      if (feature.isMany())
      {
        newRevision.setListSize(feature, revStore.getList(feature).size());
        for (int i = 0; i < referenceChunk; i++)
        {
          newRevision.getList(feature).set(i, revStore.get(feature, i));
        }
      }
    }
    return newRevision;
  }

  public CDORevision readRevisionByTime(CDOID id, int referenceChunk, long timeStamp)
  {
    throw new UnsupportedOperationException();
  }

  public CDORevision readRevisionByVersion(CDOID id, int referenceChunk, int version)
  {
    throw new UnsupportedOperationException();
  }

  public CDOID readResourceID(String path)
  {
    throw new UnsupportedOperationException();
  }

  public String readResourcePath(CDOID id)
  {
    throw new UnsupportedOperationException();
  }

  public void writePackages(CDOPackageImpl... cdoPackages)
  {
  }

  public void writeRevision(CDORevisionImpl revision)
  {
    getStore().addRevision(revision);
  }

  @Override
  public void writeRevisionDelta(CDORevisionDeltaImpl delta)
  {
    CDORevisionImpl revision2 = (CDORevisionImpl)getStore().getRevision(delta.getId());
    if (delta.getOriginVersion() != revision2.getVersion())
    {
      throw new CDODuplicateRevisionException(revision2);
    }

    CDORevisionImpl newRevision = new CDORevisionImpl(revision2);
    delta.applyChanges(newRevision);
    listToCommit.add(newRevision);
  }

  @Override
  public void release()
  {
    for (CDORevisionImpl rev : listToCommit)
      getStore().addRevision(rev);
  }

  public void rollback(IView view, ITransaction<IStoreWriter> storeTransaction)
  {
    listToCommit.clear();
  }
}
