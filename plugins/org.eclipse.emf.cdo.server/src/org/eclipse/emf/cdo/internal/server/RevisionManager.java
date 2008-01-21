/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - https://bugs.eclipse.org/bugs/show_bug.cgi?id=201266
 *    Simon McDuff - https://bugs.eclipse.org/bugs/show_bug.cgi?id=210868
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.internal.protocol.model.CDOClassImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOFeatureImpl;
import org.eclipse.emf.cdo.internal.protocol.model.resource.CDOPathFeatureImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionResolverImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.InternalCDORevision;
import org.eclipse.emf.cdo.internal.protocol.revision.delta.CDORevisionDeltaImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.model.CDOFeature;
import org.eclipse.emf.cdo.protocol.revision.CDOReferenceProxy;
import org.eclipse.emf.cdo.protocol.revision.CDORevision;
import org.eclipse.emf.cdo.protocol.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IRevisionManager;
import org.eclipse.emf.cdo.server.IStoreChunkReader;
import org.eclipse.emf.cdo.server.IStoreReader;
import org.eclipse.emf.cdo.server.IStoreWriter;
import org.eclipse.emf.cdo.server.IStoreChunkReader.Chunk;

import org.eclipse.net4j.util.collection.MoveableList;
import org.eclipse.net4j.util.transaction.ITransaction;
import org.eclipse.net4j.util.transaction.ITransactionalOperation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class RevisionManager extends CDORevisionResolverImpl implements IRevisionManager
{
  private Repository repository;

  private CDOPathFeatureImpl cdoPathFeature;

  public RevisionManager(Repository repository)
  {
    this.repository = repository;
    cdoPathFeature = repository.getPackageManager().getCDOResourcePackage().getCDOResourceClass().getCDOPathFeature();
  }

  public Repository getRepository()
  {
    return repository;
  }

  public void addRevision(ITransaction<IStoreWriter> storeTransaction, InternalCDORevision revision)
  {
    storeTransaction.execute(new AddRevisionOperation(revision));
  }

  public void addRevisionDelta(ITransaction<IStoreWriter> storeTransaction, CDORevisionDeltaImpl delta)
  {
    storeTransaction.execute(new AddRevisionDeltaOperation(delta));
  }

  public CDOID resolveReferenceProxy(CDOReferenceProxy referenceProxy)
  {
    throw new UnsupportedOperationException("Reference proxies not supported on server side");
  }

  public List<Integer> analyzeReferenceRanges(List<Object> list)
  {
    // There are currently no reference proxies on server side
    return null;
  }

  @Override
  protected InternalCDORevision verifyRevision(InternalCDORevision revision, int referenceChunk)
  {
    IStoreReader storeReader = null;
    revision = super.verifyRevision(revision, referenceChunk);
    if (repository.isVerifyingRevisions())
    {
      storeReader = StoreUtil.getReader();
      revision = (InternalCDORevision)storeReader.verifyRevision(revision);
    }

    ensureChunks(revision, referenceChunk, storeReader);
    return revision;
  }

  protected void ensureChunks(InternalCDORevision revision, int referenceChunk, IStoreReader storeReader)
  {
    CDOClassImpl cdoClass = (CDOClassImpl)revision.getCDOClass();
    CDOFeatureImpl[] features = cdoClass.getAllFeatures();
    for (int i = 0; i < features.length; i++)
    {
      CDOFeatureImpl feature = features[i];
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
    return ensureChunk(revision, feature, StoreUtil.getReader(), list, chunkStart, chunkEnd);
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
              storeReader = StoreUtil.getReader();
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
          storeReader = StoreUtil.getReader();
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
    IStoreReader storeReader = StoreUtil.getReader();
    return (InternalCDORevision)storeReader.readRevision(id, referenceChunk);
  }

  @Override
  protected InternalCDORevision loadRevisionByTime(CDOID id, int referenceChunk, long timeStamp)
  {
    IStoreReader storeReader = StoreUtil.getReader();
    return (InternalCDORevision)storeReader.readRevisionByTime(id, referenceChunk, timeStamp);
  }

  @Override
  protected InternalCDORevision loadRevisionByVersion(CDOID id, int referenceChunk, int version)
  {
    IStoreReader storeReader = StoreUtil.getReader();
    return (InternalCDORevision)storeReader.readRevisionByVersion(id, referenceChunk, version);
  }

  @Override
  protected List<InternalCDORevision> loadRevisions(Collection<CDOID> ids, int referenceChunk)
  {
    IStoreReader storeReader = StoreUtil.getReader();
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
    IStoreReader storeReader = StoreUtil.getReader();
    List<InternalCDORevision> revisions = new ArrayList<InternalCDORevision>();
    for (CDOID id : ids)
    {
      InternalCDORevision revision = (InternalCDORevision)storeReader.readRevisionByTime(id, referenceChunk, timeStamp);
      revisions.add(revision);
    }

    return revisions;
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    setCurrentLRUCapacity(getLRUCapacity(IRepository.PROP_CURRENT_LRU_CAPACITY));
    setRevisedLRUCapacity(getLRUCapacity(IRepository.PROP_REVISED_LRU_CAPACITY));
  }

  protected int getLRUCapacity(String prop)
  {
    String capacity = repository.getProperties().get(prop);
    return capacity == null ? 0 : Integer.valueOf(capacity);
  }

  /**
   * @author Eike Stepper
   */
  private final class AddRevisionOperation implements ITransactionalOperation<IStoreWriter>
  {
    private InternalCDORevision revision;

    private AddRevisionOperation(InternalCDORevision revision)
    {
      this.revision = revision;
    }

    public void phase1(IStoreWriter storeWriter) throws Exception
    {
      // Can throw an exception if duplicate
      storeWriter.writeRevision(revision);
    }

    public void phase2(IStoreWriter storeWriter)
    {
      addRevision(revision);
      if (revision.isResource())
      {
        String path = (String)revision.getData().get(cdoPathFeature, -1);
        repository.getResourceManager().registerResource(revision.getID(), path);
      }
    }

    public void undoPhase1(IStoreWriter storeWriter)
    {
    }
  }

  /**
   * @author Simon McDuff
   */
  private final class AddRevisionDeltaOperation implements ITransactionalOperation<IStoreWriter>
  {
    private CDORevisionDeltaImpl revisionDelta;

    private InternalCDORevision dirtyRevision;

    private AddRevisionDeltaOperation(CDORevisionDeltaImpl revisionDelta)
    {
      this.revisionDelta = revisionDelta;
    }

    public void phase1(IStoreWriter storeWriter) throws Exception
    {
      boolean supportDelta = getRepository().isSupportingRevisionDeltas();
      InternalCDORevision originRevision = getRevisionByVersion(revisionDelta.getID(), CDORevision.UNCHUNKED,
          revisionDelta.getOriginVersion(), !supportDelta);

      if (originRevision != null)
      {
        dirtyRevision = (InternalCDORevision)CDORevisionUtil.copy(originRevision);
        revisionDelta.apply(dirtyRevision);
      }
      else if (!supportDelta)
      {
        // Origin revision need to be accessible for stores that do not support deltas
        throw new IllegalStateException("Can not retrieve origin revision");
      }

      if (supportDelta)
      {
        // Can throw an exception if duplicate
        storeWriter.writeRevisionDelta(revisionDelta);
      }
      else
      {
        // Can throw an exception if duplicate
        storeWriter.writeRevision(dirtyRevision);
      }
    }

    public void phase2(IStoreWriter storeWriter)
    {
      if (dirtyRevision != null)
      {
        addRevision(dirtyRevision);
      }
    }

    public void undoPhase1(IStoreWriter storeWriter)
    {
    }
  }
}
