/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/201266
 *    Simon McDuff - http://bugs.eclipse.org/210868
 */
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionResolverImpl;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.IStoreChunkReader;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.server.IStoreAccessor.AdditionalRevisionCache;
import org.eclipse.emf.cdo.server.IStoreChunkReader.Chunk;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOList;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalRevisionManager;

import org.eclipse.net4j.util.collection.MoveableList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class RevisionManager extends CDORevisionResolverImpl implements InternalRevisionManager
{
  private InternalRepository repository;

  private AdditionalRevisionCache additionalRevisionCache;

  /**
   * @since 2.0
   */
  public RevisionManager()
  {
  }

  /**
   * @since 2.0
   */
  public void setRepository(InternalRepository repository)
  {
    this.repository = repository;
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
      revision = accessor.verifyRevision(revision);
    }

    ensureChunks(revision, referenceChunk, accessor);
    return revision;
  }

  /**
   * @since 2.0
   */
  protected void ensureChunks(InternalCDORevision revision, int referenceChunk, IStoreAccessor accessor)
  {
    EClass eClass = revision.getEClass();
    EStructuralFeature[] features = CDOModelUtil.getAllPersistentFeatures(eClass);
    for (int i = 0; i < features.length; i++)
    {
      EStructuralFeature feature = features[i];
      if (feature.isMany())
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
  public IStoreAccessor ensureChunk(InternalCDORevision revision, EStructuralFeature feature, int chunkStart,
      int chunkEnd)
  {
    MoveableList<Object> list = revision.getList(feature);
    chunkEnd = Math.min(chunkEnd, list.size());
    return ensureChunk(revision, feature, StoreThreadLocal.getAccessor(), list, chunkStart, chunkEnd);
  }

  protected IStoreAccessor ensureChunk(InternalCDORevision revision, EStructuralFeature feature,
      IStoreAccessor accessor, MoveableList<Object> list, int chunkStart, int chunkEnd)
  {
    IStoreChunkReader chunkReader = null;
    int fromIndex = -1;
    for (int j = chunkStart; j < chunkEnd; j++)
    {
      if (list.get(j) == InternalCDOList.UNINITIALIZED)
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
    return accessor.readRevision(id, referenceChunk, additionalRevisionCache);
  }

  /**
   * @since 2.0
   */
  @Override
  protected InternalCDORevision loadRevisionByTime(CDOID id, int referenceChunk, long timeStamp)
  {
    if (repository.isSupportingAudits())
    {
      IStoreAccessor accessor = StoreThreadLocal.getAccessor();
      return accessor.readRevisionByTime(id, referenceChunk, additionalRevisionCache, timeStamp);
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

    throw new IllegalStateException("No support for auditing mode"); //$NON-NLS-1$
  }

  /**
   * @since 2.0
   */
  @Override
  protected InternalCDORevision loadRevisionByVersion(CDOID id, int referenceChunk, int version)
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    if (repository.isSupportingAudits())
    {
      return accessor.readRevisionByVersion(id, referenceChunk, additionalRevisionCache, version);
    }

    InternalCDORevision revision = loadRevision(id, referenceChunk);
    if (revision.getVersion() == version)
    {
      return revision;
    }

    throw new IllegalStateException("Cannot access object with id " + id + " and version " + version); //$NON-NLS-1$ //$NON-NLS-2$
  }

  @Override
  protected List<InternalCDORevision> loadRevisions(Collection<CDOID> ids, int referenceChunk)
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    List<InternalCDORevision> revisions = new ArrayList<InternalCDORevision>();
    for (CDOID id : ids)
    {
      InternalCDORevision revision = accessor.readRevision(id, referenceChunk, additionalRevisionCache);
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

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    additionalRevisionCache = new IStoreAccessor.AdditionalRevisionCache()
    {
      public void cacheRevision(InternalCDORevision revision)
      {
        addCachedRevision(revision);
      }
    };
  }
}
