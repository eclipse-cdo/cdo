/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.mongodb;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchHandler;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lob.CDOLobHandler;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionCacheAdder;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStoreChunkReader;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.internal.mongodb.bundle.OM;
import org.eclipse.emf.cdo.server.mongodb.IMongoDBStoreAccessor;
import org.eclipse.emf.cdo.spi.common.commit.CDOChangeSetSegment;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.Store;
import org.eclipse.emf.cdo.spi.server.StoreAccessorBase;

import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class MongoDBStoreAccessor extends StoreAccessorBase implements IMongoDBStoreAccessor
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, MongoDBStoreAccessor.class);

  public MongoDBStoreAccessor(Store store, ISession session)
  {
    super(store, session);
  }

  public MongoDBStoreAccessor(Store store, ITransaction transaction)
  {
    super(store, transaction);
  }

  @Override
  public MongoDBStore getStore()
  {
    return (MongoDBStore)super.getStore();
  }

  public IStoreChunkReader createChunkReader(InternalCDORevision revision, EStructuralFeature feature)
  {
    // Partial collection loading not supported, yet.
    return null;
  }

  public Collection<InternalCDOPackageUnit> readPackageUnits()
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  public EPackage[] loadPackageUnit(InternalCDOPackageUnit packageUnit)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  public InternalCDORevision readRevision(CDOID id, CDOBranchPoint branchPoint, int listChunk,
      CDORevisionCacheAdder cache)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  public InternalCDORevision readRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, int listChunk,
      CDORevisionCacheAdder cache)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  public CDOID readResourceID(CDOID folderID, String name, CDOBranchPoint branchPoint)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  public void handleRevisions(EClass eClass, CDOBranch branch, long timeStamp, boolean exactTime,
      CDORevisionHandler handler)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  public Set<CDOID> readChangeSet(OMMonitor monitor, CDOChangeSetSegment... segments)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  public void queryResources(QueryResourcesContext context)
  {
    // Only support timestamp in audit mode
    if (context.getTimeStamp() != CDORevision.UNSPECIFIED_DATE && !getStore().getRepository().isSupportingAudits())
    {
      throw new IllegalArgumentException("Auditing not supported");
    }

    EresourcePackage resourcesPackage = EresourcePackage.eINSTANCE;

    // First query folders
    boolean shallContinue = queryResources(context, resourcesPackage.getCDOResourceFolder());

    // Not enough results? -> query resources
    if (shallContinue)
    {
      queryResources(context, resourcesPackage.getCDOResource());
    }
  }

  private boolean queryResources(QueryResourcesContext context, EClass eClass)
  {
    IDHandler idHandler = getStore().getIDHandler();
    Mapper mapper = getStore().getMapper();

    DBCollection collection = mapper.getCollection(eClass);
    DBCursor cursor = null;

    CDOID folderID = context.getFolderID();
    String name = context.getName();
    boolean exactMatch = context.exactMatch();

    try
    {

      DBObject query = createResourcesQuery(folderID, name, exactMatch, context);
      DBObject keys = new BasicDBObject(Commits.REVISIONS_ID, 1);
      cursor = collection.find(query, keys);
      while (cursor.hasNext())
      {
        DBObject doc = cursor.next();

        CDOID id = idHandler.read(doc, "_cdoid");
        if (TRACER.isEnabled())
        {
          TRACER.trace("Resources query returned ID " + id); //$NON-NLS-1$
        }

        if (!context.addResource(id))
        {
          // No more results allowed
          return false; // Don't continue
        }
      }

      return true; // Continue with other results
    }
    finally
    {
    }
  }

  public DBObject createResourcesQuery(CDOID folderID, String name, boolean exactMatch, QueryResourcesContext context)
  {
    DBObject query = new BasicDBObject();
    query.put("_version", new BasicDBObject("$gt", 0));
    getStore().getIDHandler().write(query, "_container", folderID);
    if (name == null)
    {
      query.put("name", new BasicDBObject("$exists", false));
    }
    else if (exactMatch)
    {
      query.put("name", name);
    }
    else
    {
      query.put("name", new BasicDBObject("$regex", "/^" + name + "/"));
    }

    return query;
  }

  public void queryXRefs(QueryXRefsContext context)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  public void queryLobs(List<byte[]> ids)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  public void loadLob(byte[] id, OutputStream out) throws IOException
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  public void handleLobs(long fromTime, long toTime, CDOLobHandler handler) throws IOException
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  public IQueryHandler getQueryHandler(CDOQueryInfo info)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  public Pair<Integer, Long> createBranch(int branchID, BranchInfo branchInfo)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  public BranchInfo loadBranch(int branchID)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  public SubBranchInfo[] loadSubBranches(int branchID)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  public int loadBranches(int startID, int endID, CDOBranchHandler branchHandler)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  public void loadCommitInfos(CDOBranch branch, long startTime, long endTime, CDOCommitInfoHandler handler)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  public CDOCommitData loadCommitData(long timeStamp)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  public void writePackageUnits(InternalCDOPackageUnit[] packageUnits, OMMonitor monitor)
  {
    getStore().getCommits().writePackageUnits(this, packageUnits, monitor);
  }

  public void write(InternalCommitContext context, OMMonitor monitor)
  {
    getStore().getCommits().write(this, context, monitor);
  }

  public void commit(OMMonitor monitor)
  {
    // Do nothing
  }

  public void rollback()
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  @Override
  protected CDOID getNextCDOID(CDORevision revision)
  {
    return getStore().getIDHandler().getNextCDOID(revision);
  }
}
