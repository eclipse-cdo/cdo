/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
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
import org.eclipse.emf.cdo.internal.protocol.model.CDOClassRefImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOFeatureImpl;
import org.eclipse.emf.cdo.internal.protocol.model.resource.CDOPathFeatureImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionResolverImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl.MoveableList;
import org.eclipse.emf.cdo.internal.protocol.revision.delta.CDORevisionDeltaImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.model.CDOFeature;
import org.eclipse.emf.cdo.protocol.revision.CDODuplicateRevisionException;
import org.eclipse.emf.cdo.protocol.revision.CDOReferenceProxy;
import org.eclipse.emf.cdo.protocol.revision.CDORevision;
import org.eclipse.emf.cdo.server.IRevisionManager;
import org.eclipse.emf.cdo.server.IStoreChunkReader;
import org.eclipse.emf.cdo.server.IStoreReader;
import org.eclipse.emf.cdo.server.IStoreWriter;
import org.eclipse.emf.cdo.server.IStoreChunkReader.Chunk;

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
  public static final String PROP_CURRENT_LRU_CAPACITY = "currentLRUCapacity";

  public static final String PROP_REVISED_LRU_CAPACITY = "revisedLRUCapacity";

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

  public void addRevision(ITransaction<IStoreWriter> storeTransaction, CDORevisionImpl revision)
  {
    storeTransaction.execute(new AddRevisionOperation(revision));
  }

  public void writeRevisionDelta(ITransaction<IStoreWriter> storeTransaction, CDORevisionDeltaImpl revision)
  {
    storeTransaction.execute(new WriteRevisionOperation(revision));
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
  protected CDORevisionImpl verifyRevision(CDORevisionImpl revision, int referenceChunk)
  {
    IStoreReader storeReader = null;
    revision = super.verifyRevision(revision, referenceChunk);
    if (repository.isVerifyingRevisions())
    {
      storeReader = StoreUtil.getReader();
      revision = storeReader.verifyRevision(revision);
    }

    ensureChunks(revision, referenceChunk, storeReader);
    return revision;
  }

  protected void ensureChunks(CDORevisionImpl revision, int referenceChunk, IStoreReader storeReader)
  {
    CDOClassImpl cdoClass = revision.getCDOClass();
    CDOFeatureImpl[] features = cdoClass.getAllFeatures();
    for (int i = 0; i < features.length; i++)
    {
      CDOFeatureImpl feature = features[i];
      if (feature.isReference() && feature.isMany())
      {
        MoveableList list = revision.getList(feature);
        int chunkEnd = Math.min(referenceChunk, list.size());
        storeReader = ensureChunk(revision, feature, storeReader, list, 0, chunkEnd);
      }
    }
  }

  public IStoreReader ensureChunk(CDORevisionImpl revision, CDOFeature feature, int chunkStart, int chunkEnd)
  {
    MoveableList list = revision.getList(feature);
    chunkEnd = Math.min(chunkEnd, list.size());
    return this.ensureChunk(revision, feature, StoreUtil.getReader(), list, chunkStart, chunkEnd);
  }

  protected IStoreReader ensureChunk(CDORevisionImpl revision, CDOFeature feature, IStoreReader storeReader,
      MoveableList list, int chunkStart, int chunkEnd)
  {
    IStoreChunkReader chunkReader = null;
    int fromIndex = -1;
    for (int j = chunkStart; j < chunkEnd; j++)
    {
      if (list.get(j) == CDORevisionImpl.UNINITIALIZED)
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
  protected CDORevisionImpl loadRevision(CDOID id, int referenceChunk)
  {
    IStoreReader storeReader = StoreUtil.getReader();
    CDORevisionImpl revision = (CDORevisionImpl)storeReader.readRevision(id, referenceChunk);
    registerObjectType(revision);
    return revision;
  }

  @Override
  protected CDORevisionImpl loadRevisionByTime(CDOID id, int referenceChunk, long timeStamp)
  {
    IStoreReader storeReader = StoreUtil.getReader();
    CDORevisionImpl revision = (CDORevisionImpl)storeReader.readRevisionByTime(id, referenceChunk, timeStamp);
    registerObjectType(revision);
    return revision;
  }

  @Override
  protected CDORevisionImpl loadRevisionByVersion(CDOID id, int referenceChunk, int version)
  {
    IStoreReader storeReader = StoreUtil.getReader();
    CDORevisionImpl revision = (CDORevisionImpl)storeReader.readRevisionByVersion(id, referenceChunk, version);
    registerObjectType(revision);
    return revision;
  }

  @Override
  protected List<CDORevisionImpl> loadRevisions(Collection<CDOID> ids, int referenceChunk)
  {
    IStoreReader storeReader = StoreUtil.getReader();
    List<CDORevisionImpl> revisions = new ArrayList<CDORevisionImpl>();
    for (CDOID id : ids)
    {
      CDORevisionImpl revision = (CDORevisionImpl)storeReader.readRevision(id, referenceChunk);
      registerObjectType(revision);
    }

    return revisions;
  }

  @Override
  protected List<CDORevisionImpl> loadRevisionsByTime(Collection<CDOID> ids, int referenceChunk, long timeStamp)
  {
    IStoreReader storeReader = StoreUtil.getReader();
    List<CDORevisionImpl> revisions = new ArrayList<CDORevisionImpl>();
    for (CDOID id : ids)
    {
      CDORevisionImpl revision = (CDORevisionImpl)storeReader.readRevisionByTime(id, referenceChunk, timeStamp);
      registerObjectType(revision);
    }

    return revisions;
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    setCurrentLRUCapacity(getLRUCapacity(PROP_CURRENT_LRU_CAPACITY));
    setRevisedLRUCapacity(getLRUCapacity(PROP_REVISED_LRU_CAPACITY));
  }

  protected int getLRUCapacity(String prop)
  {
    String capacity = repository.getProperties().get(prop);
    return capacity == null ? 0 : Integer.valueOf(capacity);
  }

  protected void registerObjectType(CDORevisionImpl revision)
  {
    CDOID id = revision.getID();
    CDOClassRefImpl type = revision.getCDOClass().createClassRef();
    repository.getTypeManager().registerObjectType(id, type);
  }

  /**
   * @author Eike Stepper
   */
  private final class AddRevisionOperation implements ITransactionalOperation<IStoreWriter>
  {
    private CDORevisionImpl revision;

    private AddRevisionOperation(CDORevisionImpl revision)
    {
      this.revision = revision;
    }

    public void phase1(IStoreWriter storeWriter) throws Exception
    {
      storeWriter.writeRevision(revision);
    }

    public void phase2(IStoreWriter storeWriter)
    {
      repository.getTypeManager().registerObjectType(revision.getID(), revision.getCDOClass().createClassRef());
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
  private final class WriteRevisionOperation implements ITransactionalOperation<IStoreWriter>
  {
    private CDORevisionDeltaImpl revisionDelta;

    private CDORevisionImpl dirtyRevision = null;

    private WriteRevisionOperation(CDORevisionDeltaImpl revisionDelta)
    {
      this.revisionDelta = revisionDelta;
      CDORevisionImpl oldRevision = getRevisionByVersion(revisionDelta.getId(), CDORevision.UNCHUNKED, revisionDelta
          .getOriginVersion(), true);
      if (oldRevision == null)
      {
        if (!getRepository().isSupportingRevisionDeltas())
        {
          throw new IllegalArgumentException("Cannot retrieved origin revision");
        }
      }
      else
      {
        dirtyRevision = new CDORevisionImpl(oldRevision);
        revisionDelta.applyChanges(dirtyRevision);
      }
    }

    public void phase1(IStoreWriter storeWriter) throws Exception
    {
      if (getRepository().isSupportingRevisionDeltas())
      {
        // Can throw an exception if duplicate
        storeWriter.writeRevisionDelta(revisionDelta);
      }
      else
      {
        storeWriter.writeRevision(dirtyRevision);
      }

      // Look if we already have it in our cache revision
      CDORevisionImpl currentVersion = getRevisionByVersion(revisionDelta.getId(), 0, revisionDelta.getDirtyVersion(),
          false);

      if (currentVersion != null)
      {
        throw new CDODuplicateRevisionException(currentVersion);
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
