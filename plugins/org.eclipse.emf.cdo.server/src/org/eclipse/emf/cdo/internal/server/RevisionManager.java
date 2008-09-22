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
 *    Simon McDuff - http://bugs.eclipse.org/210868
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDObjectFactory;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.model.resource.CDOPathFeature;
import org.eclipse.emf.cdo.common.revision.CDOReferenceProxy;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionResolverImpl;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IRevisionManager;
import org.eclipse.emf.cdo.server.IStoreChunkReader;
import org.eclipse.emf.cdo.server.IStoreReader;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.server.IStoreChunkReader.Chunk;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

import org.eclipse.net4j.util.collection.MoveableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class RevisionManager extends CDORevisionResolverImpl implements IRevisionManager
{
  private IRepository repository;

  private CDOPathFeature cdoPathFeature;

  /**
   * @since 2.0
   */
  public RevisionManager()
  {
  }

  /**
   * @since 2.0
   */
  public IRepository getRepository()
  {
    return repository;
  }

  /**
   * @since 2.0
   */
  public void setRepository(IRepository repository)
  {
    this.repository = repository;
    cdoPathFeature = repository.getPackageManager().getCDOResourcePackage().getCDOResourceClass().getCDOPathFeature();
  }

  public CDOIDObjectFactory getCDOIDObjectFactory()
  {
    return repository.getStore().getCDOIDObjectFactory();
  }

  /**
   * @since 2.0
   */
  public CDOID resolveReferenceProxy(CDORevision revision, CDOFeature feature, CDOReferenceProxy proxy, int currentIndex)
  {
    throw new UnsupportedOperationException("Reference proxies not supported on server side");
  }

  /**
   * @since 2.0
   */
  @Override
  public boolean addCachedRevision(InternalCDORevision revision)
  {
    if (revision.isResource())
    {
      String path = (String)revision.get(cdoPathFeature, 0);
      ((Repository)repository).getResourceManager().registerResource(revision.getID(), path);
    }

    return super.addCachedRevision(revision);
  }

  @Override
  protected InternalCDORevision verifyRevision(InternalCDORevision revision, int referenceChunk)
  {
    IStoreReader storeReader = null;
    revision = super.verifyRevision(revision, referenceChunk);
    if (repository.isVerifyingRevisions())
    {
      storeReader = StoreThreadLocal.getStoreReader();
      revision = (InternalCDORevision)storeReader.verifyRevision(revision);
    }

    ensureChunks(revision, referenceChunk, storeReader);
    return revision;
  }

  protected void ensureChunks(InternalCDORevision revision, int referenceChunk, IStoreReader storeReader)
  {
    CDOClass cdoClass = revision.getCDOClass();
    CDOFeature[] features = cdoClass.getAllFeatures();
    for (int i = 0; i < features.length; i++)
    {
      CDOFeature feature = features[i];
      if (feature.isReference() && feature.isMany())
      {
        MoveableList<Object> list = revision.getList(feature);
        int chunkEnd = Math.min(referenceChunk, list.size());
        storeReader = ensureChunk(revision, feature, storeReader, list, 0, chunkEnd);
      }
    }
  }

  public IStoreReader ensureChunk(InternalCDORevision revision, CDOFeature feature, int chunkStart, int chunkEnd)
  {
    MoveableList<Object> list = revision.getList(feature);
    chunkEnd = Math.min(chunkEnd, list.size());
    return ensureChunk(revision, feature, StoreThreadLocal.getStoreReader(), list, chunkStart, chunkEnd);
  }

  protected IStoreReader ensureChunk(InternalCDORevision revision, CDOFeature feature, IStoreReader storeReader,
      MoveableList<Object> list, int chunkStart, int chunkEnd)
  {
    IStoreChunkReader chunkReader = null;
    int fromIndex = -1;
    for (int j = chunkStart; j < chunkEnd; j++)
    {
      if (list.get(j) == InternalCDORevision.UNINITIALIZED)
      {
        if (fromIndex == -1)
        {
          fromIndex = j;
        }
      }
      else
      {
        if (fromIndex != -1)
        {
          if (chunkReader == null)
          {
            if (storeReader == null)
            {
              storeReader = StoreThreadLocal.getStoreReader();
            }

            chunkReader = storeReader.createChunkReader(revision, feature);
          }

          int toIndex = j;
          if (fromIndex == toIndex - 1)
          {
            chunkReader.addSimpleChunk(fromIndex);
          }
          else
          {
            chunkReader.addRangedChunk(fromIndex, toIndex);
          }

          fromIndex = -1;
        }
      }
    }

    // Add last chunk
    if (fromIndex != -1)
    {
      if (chunkReader == null)
      {
        if (storeReader == null)
        {
          storeReader = StoreThreadLocal.getStoreReader();
        }

        chunkReader = storeReader.createChunkReader(revision, feature);
      }

      int toIndex = chunkEnd;
      if (fromIndex == toIndex - 1)
      {
        chunkReader.addSimpleChunk(fromIndex);
      }
      else
      {
        chunkReader.addRangedChunk(fromIndex, toIndex);
      }
    }

    if (chunkReader != null)
    {
      List<Chunk> chunks = chunkReader.executeRead();
      for (Chunk chunk : chunks)
      {
        int startIndex = chunk.getStartIndex();
        for (int indexInChunk = 0; indexInChunk < chunk.size(); indexInChunk++)
        {
          CDOID id = chunk.getID(indexInChunk);
          list.set(startIndex + indexInChunk, id);
        }
      }
    }

    return storeReader;
  }

  @Override
  protected InternalCDORevision loadRevision(CDOID id, int referenceChunk)
  {
    IStoreReader storeReader = StoreThreadLocal.getStoreReader();
    return (InternalCDORevision)storeReader.readRevision(id, referenceChunk);
  }

  @Override
  protected InternalCDORevision loadRevisionByTime(CDOID id, int referenceChunk, long timeStamp)
  {
    if (getRepository().isSupportingAudits())
    {
      IStoreReader storeReader = StoreThreadLocal.getStoreReader();
      return (InternalCDORevision)storeReader.readRevisionByTime(id, referenceChunk, timeStamp);
    }

    // TODO Simon: Is this check necessary here?
    // if (getRepository().getStore().hasAuditingSupport())
    // {
    // throw new UnsupportedOperationException(
    // "Auditing supports isn't activated (see IRepository.Props.PROP_SUPPORTING_AUDITS).");
    // }

    throw new UnsupportedOperationException("No support for auditing mode");
  }

  @Override
  protected InternalCDORevision loadRevisionByVersion(CDOID id, int referenceChunk, int version)
  {
    IStoreReader storeReader = StoreThreadLocal.getStoreReader();
    if (getRepository().isSupportingAudits())
    {
      return (InternalCDORevision)storeReader.readRevisionByVersion(id, referenceChunk, version);
    }

    InternalCDORevision revision = loadRevision(id, referenceChunk);
    if (revision.getVersion() == version)
    {
      return revision;
    }

    throw new IllegalStateException("Cannot access object with id " + id + " and version " + version);
  }

  @Override
  protected List<InternalCDORevision> loadRevisions(Collection<CDOID> ids, int referenceChunk)
  {
    IStoreReader storeReader = StoreThreadLocal.getStoreReader();
    List<InternalCDORevision> revisions = new ArrayList<InternalCDORevision>();
    for (CDOID id : ids)
    {
      InternalCDORevision revision = (InternalCDORevision)storeReader.readRevision(id, referenceChunk);
      revisions.add(revision);
    }

    return revisions;
  }

  @Override
  protected List<InternalCDORevision> loadRevisionsByTime(Collection<CDOID> ids, int referenceChunk, long timeStamp)
  {
    List<InternalCDORevision> revisions = new ArrayList<InternalCDORevision>();
    for (CDOID id : ids)
    {
      InternalCDORevision revision = loadRevisionByTime(id, referenceChunk, timeStamp);
      revisions.add(revision);
    }

    return revisions;
  }

  /**
   * TODO Move this to the cache(s)
   */
  protected int getLRUCapacity(String prop)
  {
    String capacity = repository.getProperties().get(prop);
    return capacity == null ? 0 : Integer.valueOf(capacity);
  }

}
