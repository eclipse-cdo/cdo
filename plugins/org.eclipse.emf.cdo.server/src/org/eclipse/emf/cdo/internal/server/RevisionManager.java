/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.internal.protocol.model.CDOClassRefImpl;
import org.eclipse.emf.cdo.internal.protocol.model.resource.CDOPathFeatureImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionResolverImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.revision.CDOReferenceProxy;
import org.eclipse.emf.cdo.server.IRevisionManager;
import org.eclipse.emf.cdo.server.IStoreReader;
import org.eclipse.emf.cdo.server.IStoreWriter;

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

  public void addRevision(ITransaction<IStoreWriter> storeTransaction, CDORevisionImpl revision)
  {
    storeTransaction.execute(new AddRevisionOperation(revision));
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
  protected CDORevisionImpl verifyRevision(CDORevisionImpl revision)
  {
    revision = super.verifyRevision(revision);
    if (repository.isVerifyingRevisions())
    {
      IStoreReader storeReader = StoreUtil.getReader();
      revision = storeReader.verifyRevision(revision);
    }

    return revision;
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

  private void registerObjectType(CDORevisionImpl revision)
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

}
