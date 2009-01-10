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
import org.eclipse.emf.cdo.common.model.CDOPackageManager;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionResolverImpl;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IRevisionManager;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.IStoreChunkReader;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.server.IStoreChunkReader.Chunk;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

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
  }

  public CDOIDObjectFactory getCDOIDObjectFactory()
  {
    return repository.getStore().getCDOIDObjectFactory();
  }

  /**
   * @since 2.0
   */
  @Override
  protected InternalCDORevision verifyRevision(InternalCDORevision revision, int referenceChunk)
  {
    IStoreAccessor accessor = null;
    revision = super.verifyRevision(revision, referenceChunk);
    if (repository.isVerifyingRevisions())
    {
      accessor = StoreThreadLocal.getAccessor();
      revision = (InternalCDORevision)accessor.verifyRevision(revision);
    }

    ensureChunks(revision, referenceChunk, accessor);
    return revision;
  }

  /**
   * @since 2.0
   */
  protected void ensureChunks(InternalCDORevision revision, int referenceChunk, IStoreAccessor accessor)
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
        accessor = ensureChunk(revision, feature, accessor, list, 0, chunkEnd);
      }
    }
  }

  /**
   * @since 2.0
   */
  public IStoreAccessor ensureChunk(InternalCDORevision revision, CDOFeature feature, int chunkStart, int chunkEnd)
  {
    MoveableList<Object> list = revision.getList(feature);
    chunkEnd = Math.min(chunkEnd, list.size());
    return ensureChunk(revision, feature, StoreThreadLocal.getAccessor(), list, chunkStart, chunkEnd);
  }

  protected IStoreAccessor ensureChunk(InternalCDORevision revision, CDOFeature feature, IStoreAccessor accessor,
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
            if (accessor == null)
            {
              accessor = StoreThreadLocal.getAccessor();
            }

            chunkReader = accessor.createChunkReader(revision, feature);
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
        if (accessor == null)
        {
          accessor = StoreThreadLocal.getAccessor();
        }

        chunkReader = accessor.createChunkReader(revision, feature);
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
          Object id = chunk.get(indexInChunk);
          list.set(startIndex + indexInChunk, id);
        }
      }
    }

    return accessor;
  }

  /**
   * @since 2.0
   */
  @Override
  protected InternalCDORevision loadRevision(CDOID id, int referenceChunk)
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    return (InternalCDORevision)accessor.readRevision(id, referenceChunk);
  }

  /**
   * @since 2.0
   */
  @Override
  protected InternalCDORevision loadRevisionByTime(CDOID id, int referenceChunk, long timeStamp)
  {
    if (getRepository().isSupportingAudits())
    {
      IStoreAccessor accessor = StoreThreadLocal.getAccessor();
      return (InternalCDORevision)accessor.readRevisionByTime(id, referenceChunk, timeStamp);
    }

    // TODO Simon*: Is this check necessary here?
    // TODO Eike: To have better exception message.
    // By knowing if the back-end supports it, it let know the user that it could be achieved by changing its
    // configuration file at the server.
    // if (getRepository().getStore().hasAuditingSupport())
    // {
    // throw new UnsupportedOperationException(
    // "Auditing supports isn't activated (see IRepository.Props.PROP_SUPPORTING_AUDITS).");
    // }

    throw new UnsupportedOperationException("No support for auditing mode");
  }

  /**
   * @since 2.0
   */
  @Override
  protected InternalCDORevision loadRevisionByVersion(CDOID id, int referenceChunk, int version)
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    if (getRepository().isSupportingAudits())
    {
      return (InternalCDORevision)accessor.readRevisionByVersion(id, referenceChunk, version);
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
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    List<InternalCDORevision> revisions = new ArrayList<InternalCDORevision>();
    for (CDOID id : ids)
    {
      InternalCDORevision revision = (InternalCDORevision)accessor.readRevision(id, referenceChunk);
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
   * @since 2.0
   */
  @Override
  protected CDOPackageManager getPackageManager()
  {
    return repository.getPackageManager();
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
