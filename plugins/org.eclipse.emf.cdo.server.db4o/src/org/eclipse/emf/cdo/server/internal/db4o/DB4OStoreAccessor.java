/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.db4o;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchHandler;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.lob.CDOLobHandler;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionCacheAdder;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStoreChunkReader;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.db4o.IDB4OIdentifiableObject;
import org.eclipse.emf.cdo.server.internal.db4o.bundle.OM;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.commit.CDOChangeSetSegment;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.LongIDStoreAccessor;

import org.eclipse.net4j.util.HexUtil;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.monitor.Monitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Victor Roldan Betancort
 */
public class DB4OStoreAccessor extends LongIDStoreAccessor
{
  private ObjectContainer objectContainer;

  public DB4OStoreAccessor(DB4OStore store, ISession session)
  {
    super(store, session);
  }

  public DB4OStoreAccessor(DB4OStore store, ITransaction transaction)
  {
    super(store, transaction);
  }

  @Override
  public DB4OStore getStore()
  {
    return (DB4OStore)super.getStore();
  }

  public ObjectContainer getObjectContainer()
  {
    return objectContainer;
  }

  public EPackage[] loadPackageUnit(InternalCDOPackageUnit packageUnit)
  {
    return packageUnit.getEPackages(true);
  }

  public void writePackageUnits(InternalCDOPackageUnit[] packageUnits, OMMonitor monitor)
  {
    monitor.begin(packageUnits.length);

    try
    {
      DB4OStore store = getStore();
      ObjectContainer objectContainer = getObjectContainer();

      for (InternalCDOPackageUnit packageUnit : packageUnits)
      {
        DB4OPackageUnit primitivePackageUnit = DB4OPackageUnit.getPrimitivePackageUnit(store, packageUnit);
        objectContainer.store(primitivePackageUnit);
        monitor.worked(1);
      }
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }
    finally
    {
      monitor.done();
    }
  }

  public Collection<InternalCDOPackageUnit> readPackageUnits()
  {
    List<InternalCDOPackageUnit> result = new ArrayList<InternalCDOPackageUnit>();
    InternalCDOPackageRegistry packageRegistry = getStore().getRepository().getPackageRegistry();
    Collection<DB4OPackageUnit> primitivePackageUnits = getObjectContainer().query(DB4OPackageUnit.class);

    for (DB4OPackageUnit primitivePackageUnit : primitivePackageUnits)
    {
      InternalCDOPackageUnit packageUnit = DB4OPackageUnit.getPackageUnit(packageRegistry, primitivePackageUnit);
      result.add(packageUnit);
    }

    return result;
  }

  public InternalCDORevision readRevision(CDOID id, CDOBranchPoint branchPoint, int listChunk,
      CDORevisionCacheAdder cache)
  {
    DB4ORevision lastRevision = DB4OStore.getRevision(getObjectContainer(), id);
    if (lastRevision == null)
    {
      // Revision does not exist. Return null to signal inexistent Revision
      return null;
    }

    return DB4ORevision.getCDORevision(getStore(), lastRevision);
  }

  public void queryResources(QueryResourcesContext context)
  {
    final long folderID = CDOIDUtil.getLong(context.getFolderID());
    final String name = context.getName();
    final boolean exactMatch = context.exactMatch();
    final Object rootResourceID = DB4ORevision.getDB4OID(getStore().getRepository().getRootResourceID());

    ObjectSet<DB4ORevision> revisionObjectSet = getObjectContainer().query(new Predicate<DB4ORevision>()
    {
      private static final long serialVersionUID = 1L;

      @Override
      public boolean match(DB4ORevision revision)
      {
        if (!revision.isResourceNode())
        {
          return false;
        }

        if (ObjectUtil.equals(rootResourceID, revision.getID()))
        {
          // is Root resource
          return false;
        }

        if (ObjectUtil.equals(revision.getContainerID(), folderID))
        {
          String candidateName = (String)revision.getValues().get(EresourcePackage.CDO_RESOURCE__NAME);
          if (exactMatch)
          {
            if (candidateName != null && candidateName.equals(name))
            {
              return true;
            }
          }
          else
          {
            // provided name is prefix of the resource name
            if (candidateName != null && candidateName.startsWith(name))
            {
              return true;
            }
          }
        }

        return false;
      }
    });

    for (DB4ORevision revision : revisionObjectSet)
    {
      if (!context.addResource(DB4ORevision.getCDOID(revision.getID())))
      {
        // No more results allowed
        break;
      }
    }

  }

  public IStoreChunkReader createChunkReader(InternalCDORevision revision, EStructuralFeature feature)
  {
    // TODO: implement DB4OStoreAccessor.createChunkReader(revision, feature)
    throw new UnsupportedOperationException();
  }

  public IQueryHandler getQueryHandler(CDOQueryInfo info)
  {
    return null;
  }

  @Override
  protected void doActivate() throws Exception
  {
    objectContainer = getStore().openClient();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    if (objectContainer != null)
    {
      objectContainer.close();
      objectContainer = null;
    }
  }

  @Override
  protected void doPassivate() throws Exception
  {
    if (objectContainer != null)
    {
      objectContainer.rollback();
    }
  }

  @Override
  protected void doUnpassivate() throws Exception
  {
  }

  @Override
  protected void rollback(CommitContext commitContext)
  {
    getObjectContainer().rollback();
  }

  public InternalCDORevision readRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, int listChunk,
      CDORevisionCacheAdder cache)
  {
    DB4ORevision revision = DB4OStore.getRevision(getObjectContainer(), id);
    if (revision == null || revision.getVersion() != branchVersion.getVersion())
    {
      return null;
    }

    return DB4ORevision.getCDORevision(getStore(), revision);
  }

  public void handleRevisions(EClass eClass, CDOBranch branch, long timeStamp, boolean exactTime,
      CDORevisionHandler handler)
  {
    // TODO: implement DB4OStoreAccessor.handleRevisions(eClass, branch, timeStamp, exactTime, handler)
    throw new UnsupportedOperationException();
  }

  public Set<CDOID> readChangeSet(CDOChangeSetSegment... segments)
  {
    // TODO: implement DB4OStoreAccessor.readChangeSet(segments)
    throw new UnsupportedOperationException();
  }

  public void queryXRefs(final QueryXRefsContext context)
  {
    final int branchID = context.getBranch().getID();

    for (final CDOID target : context.getTargetObjects().keySet())
    {
      for (final EClass eClass : context.getSourceCandidates().keySet())
      {
        final String eClassName = eClass.getName();
        final String nsURI = eClass.getEPackage().getNsURI();
        final List<EReference> eReferences = context.getSourceCandidates().get(eClass);
        getObjectContainer().query(new Predicate<DB4ORevision>()
        {
          private static final long serialVersionUID = 1L;

          private boolean moreResults = true;

          @Override
          public boolean match(DB4ORevision revision)
          {
            if (!moreResults)
            {
              return false;
            }

            if (!revision.getClassName().equals(eClassName))
            {
              return false;
            }

            if (!revision.getPackageURI().equals(nsURI))
            {
              return false;
            }

            if (!(revision.getBranchID() == branchID))
            {
              return false;
            }

            CDOID id = DB4ORevision.getCDOID(revision.getID());
            for (EReference eReference : eReferences)
            {
              Object obj = revision.getValues().get(eReference.getFeatureID());
              if (obj instanceof List)
              {
                List<?> list = (List<?>)obj;
                int index = 0;
                for (Object element : list)
                {
                  CDOID ref = DB4ORevision.getCDOID(element);
                  if (ObjectUtil.equals(ref, target))
                  {
                    moreResults = context.addXRef(target, id, eReference, index);
                  }

                  ++index;
                }
              }
              else
              {
                CDOID ref = DB4ORevision.getCDOID(obj);
                if (ObjectUtil.equals(ref, target))
                {
                  moreResults = context.addXRef(target, id, eReference, 0);
                }
              }
            }

            return false;
          }
        });
      }
    }
  }

  public void queryLobs(List<byte[]> ids)
  {
    for (Iterator<byte[]> it = ids.iterator(); it.hasNext();)
    {
      byte[] id = it.next();
      String key = HexUtil.bytesToHex(id);
      if (DB4OStore.getIdentifiableObject(getObjectContainer(), key) != null)
      {
        it.remove();
      }
    }
  }

  public void handleLobs(long fromTime, long toTime, CDOLobHandler handler) throws IOException
  {
    // TODO: implement DB4OStoreAccessor.handleLobs(fromTime, toTime, handler)
    throw new UnsupportedOperationException();
  }

  public void loadLob(byte[] id, OutputStream out) throws IOException
  {
    String key = HexUtil.bytesToHex(id);
    IDB4OIdentifiableObject identifiableObject = DB4OStore.getIdentifiableObject(getObjectContainer(), key);
    if (identifiableObject == null)
    {
      throw new IOException("Lob not found: " + key);
    }

    if (identifiableObject instanceof DB4OBlob)
    {
      DB4OBlob blob = (DB4OBlob)identifiableObject;
      byte[] byteArray = blob.getValue();
      ByteArrayInputStream in = new ByteArrayInputStream(byteArray);
      IOUtil.copyBinary(in, out, byteArray.length);
    }
    else
    {
      DB4OClob clob = (DB4OClob)identifiableObject;
      char[] charArray = clob.getValue();
      CharArrayReader in = new CharArrayReader(charArray);
      IOUtil.copyCharacter(in, new OutputStreamWriter(out), charArray.length);
    }
  }

  @Override
  protected void writeBlob(byte[] id, long size, InputStream inputStream) throws IOException
  {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    IOUtil.copyBinary(inputStream, out, size);
    writeObject(new DB4OBlob(HexUtil.bytesToHex(id), out.toByteArray()), new Monitor());
  }

  @Override
  protected void writeClob(byte[] id, long size, Reader reader) throws IOException
  {
    CharArrayWriter out = new CharArrayWriter();
    IOUtil.copyCharacter(reader, out, size);
    writeObject(new DB4OClob(HexUtil.bytesToHex(id), out.toCharArray()), new Monitor());
  }

  public Pair<Integer, Long> createBranch(int branchID, BranchInfo branchInfo)
  {
    // TODO: implement DB4OStoreAccessor.createBranch(branchID, branchInfo)
    throw new UnsupportedOperationException();
  }

  public BranchInfo loadBranch(int branchID)
  {
    // TODO: implement DB4OStoreAccessor.loadBranch(branchID)
    throw new UnsupportedOperationException();
  }

  public SubBranchInfo[] loadSubBranches(int branchID)
  {
    // TODO: implement DB4OStoreAccessor.loadSubBranches(branchID)
    throw new UnsupportedOperationException();
  }

  public int loadBranches(int startID, int endID, CDOBranchHandler branchHandler)
  {
    // TODO: implement DB4OStoreAccessor.loadBranches(startID, endID, branchHandler)
    throw new UnsupportedOperationException();
  }

  public void loadCommitInfos(final CDOBranch branch, final long startTime, final long endTime,
      CDOCommitInfoHandler handler)
  {
    ObjectSet<DB4OCommitInfo> resultSet = getObjectContainer().query(new Predicate<DB4OCommitInfo>()
    {
      private static final long serialVersionUID = 1L;

      @Override
      public boolean match(DB4OCommitInfo info)
      {
        if (startTime != CDOBranchPoint.UNSPECIFIED_DATE && info.getTimeStamp() < startTime)
        {
          return false;
        }

        if (endTime != CDOBranchPoint.UNSPECIFIED_DATE && info.getTimeStamp() > endTime)
        {
          return false;
        }

        if (branch != null && !(info.getBranchID() == branch.getID()))
        {
          return false;
        }

        return true;
      }
    });

    InternalRepository repository = getStore().getRepository();
    InternalCDOCommitInfoManager commitInfoManager = repository.getCommitInfoManager();
    InternalCDOBranchManager branchManager = repository.getBranchManager();

    // Although not specified in the API, the test suite
    // suggests CommitInfos should be returned ordered by timeStamp
    // TODO Specify this in the API!

    List<DB4OCommitInfo> infos = new ArrayList<DB4OCommitInfo>(resultSet);
    Collections.sort(infos, new Comparator<DB4OCommitInfo>()
    {
      public int compare(DB4OCommitInfo arg0, DB4OCommitInfo arg1)
      {
        return CDOCommonUtil.compareTimeStamps(arg0.getTimeStamp(), arg1.getTimeStamp());
      }
    });

    for (DB4OCommitInfo info : infos)
    {
      info.handle(branchManager, commitInfoManager, handler);
    }
  }

  @Override
  protected void doCommit(OMMonitor monitor)
  {
    monitor.begin();
    Async async = monitor.forkAsync();

    try
    {
      getObjectContainer().commit();
    }
    catch (Exception e)
    {
      OM.LOG.error(e);
    }
    finally
    {
      async.stop();
      monitor.done();
    }
  }

  @Override
  protected void writeCommitInfo(CDOBranch branch, long timeStamp, long previousTimeStamp, String userID,
      String comment, OMMonitor monitor)
  {
    DB4OCommitInfo commitInfo = new DB4OCommitInfo(branch.getID(), timeStamp, previousTimeStamp, userID, comment);
    writeObject(commitInfo, monitor);
  }

  @Override
  protected void writeRevisions(InternalCDORevision[] revisions, CDOBranch branch, OMMonitor monitor)
  {
    monitor.begin(revisions.length);

    try
    {
      for (InternalCDORevision revision : revisions)
      {
        writeRevision(revision, monitor.fork());
      }
    }
    finally
    {
      monitor.done();
    }
  }

  protected void writeRevision(InternalCDORevision revision, OMMonitor monitor)
  {
    Async async = null;
    monitor.begin(10);

    try
    {
      try
      {
        async = monitor.forkAsync();
        if (revision.isResourceNode())
        {
          checkDuplicateResources(revision);
        }
      }
      finally
      {
        if (async != null)
        {
          async.stop();
        }
      }

      // If revision is in the store, remove old, store new
      ObjectContainer objectContainer = getObjectContainer();
      CDOID id = revision.getID();
      DB4ORevision revisionAlreadyInStore = DB4OStore.getRevision(objectContainer, id);
      if (revisionAlreadyInStore != null)
      {
        DB4OStore.removeRevision(objectContainer, id);
      }

      DB4ORevision primitiveRevision = DB4ORevision.getDB4ORevision(revision);
      writeObject(primitiveRevision, monitor);
    }
    finally
    {
      monitor.done();
    }
  }

  protected void writeObject(Object object, OMMonitor monitor)
  {
    monitor.begin();
    Async async = monitor.forkAsync();

    try
    {
      getObjectContainer().store(object);
    }
    catch (Throwable t)
    {
      OM.LOG.error(t);
    }
    finally
    {
      async.stop();
      monitor.done();
    }
  }

  @Override
  protected void writeRevisionDeltas(InternalCDORevisionDelta[] revisionDeltas, CDOBranch branch, long created,
      OMMonitor monitor)
  {
    // TODO: implement DB4OStoreAccessor.writeRevisionDeltas(revisionDeltas, branch, created, monitor)
    throw new UnsupportedOperationException();
  }

  @Override
  protected void detachObjects(CDOID[] detachedObjects, CDOBranch branch, long timeStamp, OMMonitor monitor)
  {
    monitor.begin(detachedObjects.length);

    try
    {
      for (CDOID id : detachedObjects)
      {
        DB4OStore.removeRevision(getObjectContainer(), id);
        monitor.worked();
      }
    }
    finally
    {
      monitor.done();
    }
  }

  protected void checkDuplicateResources(CDORevision revision) throws IllegalStateException
  {
    final long folderID = CDOIDUtil.getLong((CDOID)revision.data().getContainerID());
    final long revisionID = CDOIDUtil.getLong(revision.getID());
    final String name = (String)revision.data().get(EresourcePackage.eINSTANCE.getCDOResourceNode_Name(), 0);

    ObjectSet<DB4ORevision> resultSet = getObjectContainer().query(new Predicate<DB4ORevision>()
    {
      private static final long serialVersionUID = 1L;

      @Override
      public boolean match(DB4ORevision revision)
      {
        if (revision.isResourceNode() && ObjectUtil.equals(revision.getContainerID(), folderID))
        {
          String candidateName = (String)revision.getValues().get(EresourcePackage.CDO_RESOURCE__NAME);
          if (StringUtil.compare(name, candidateName) == 0)
          {
            if (!ObjectUtil.equals(revision.getID(), revisionID))
            {
              return true;
            }
          }
        }

        return false;
      }
    });

    if (!resultSet.isEmpty())
    {
      throw new IllegalStateException("Duplicate resource or folder: " + name + " in folder " + folderID); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }
}
