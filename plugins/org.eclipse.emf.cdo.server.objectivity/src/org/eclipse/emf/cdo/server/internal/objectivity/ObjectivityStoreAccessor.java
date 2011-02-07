/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Ibrahim Sallam - code refactoring for CDO 3.0
 */
package org.eclipse.emf.cdo.server.internal.objectivity;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchHandler;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lob.CDOLobHandler;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionCacheAdder;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.internal.objectivity.bundle.OM;
import org.eclipse.emf.cdo.server.internal.objectivity.clustering.ObjyPlacementManagerLocal;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyCommitInfoHandler;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyObject;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyObjectManager;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyPackageHandler;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjySchema;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjySession;
import org.eclipse.emf.cdo.server.internal.objectivity.schema.ObjyBranch;
import org.eclipse.emf.cdo.server.internal.objectivity.schema.ObjyBranchManager;
import org.eclipse.emf.cdo.server.internal.objectivity.schema.ObjyCommitInfo;
import org.eclipse.emf.cdo.server.internal.objectivity.schema.ObjyResourceList;
import org.eclipse.emf.cdo.server.internal.objectivity.utils.OBJYCDOIDUtil;
import org.eclipse.emf.cdo.server.internal.objectivity.utils.SmartLock;
import org.eclipse.emf.cdo.server.objectivity.IObjectivityStoreAccessor;
import org.eclipse.emf.cdo.server.objectivity.IObjectivityStoreChunkReader;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.commit.CDOChangeSetSegment;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.DetachedCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;
import org.eclipse.emf.cdo.spi.server.StoreAccessor;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.ResourceSet;

import com.objy.db.app.oo;
import com.objy.db.app.ooId;
import com.objy.db.app.ooObj;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

/**
 * @author Simon McDuff
 * @author Ibrahim Sallam
 */
public class ObjectivityStoreAccessor extends StoreAccessor implements IObjectivityStoreAccessor
{
  private static final ContextTracer TRACER_DEBUG = new ContextTracer(OM.DEBUG, ObjectivityStoreAccessor.class);

  private static final ContextTracer TRACER_ERROR = new ContextTracer(OM.ERROR, ObjectivityStoreAccessor.class);

  private static final ContextTracer TRACER_INFO = new ContextTracer(OM.INFO, ObjectivityStoreAccessor.class);

  protected ObjySession objySession = null;

  // protected ObjyObjectManager objectManager = null;

  protected boolean isRead = false;

  protected int sessionID = 0; // used to tag the Objy session in the session pool

  boolean zipped = true; // TODO - might make this configurable...

  protected ObjectivityStoreAccessor(ObjectivityStore store, ISession cdoSession)
  {
    super(store, cdoSession);

    // for debugging...
    // session.getProtocol();
    // org.eclipse.emf.cdo.net4j.CDOSession.Options.getProtocol()
    // [12:42:23 PM] Eike Stepper says: org.eclipse.net4j.signal.ISignalProtocol.setTimeout(long)
    // [12:42:43 PM] Eike Stepper says: commit is different
    // if (cdoSession != null && cdoSession.getProtocol().getSession() instanceof org.eclipse.emf.cdo.net4j.CDOSession)
    // {
    // CDOCommonSession commonSession = cdoSession.getProtocol().getSession();
    // org.eclipse.emf.cdo.net4j.CDOSession tempSession = (org.eclipse.emf.cdo.net4j.CDOSession)commonSession;
    // tempSession.options().setCommitTimeout(60000);
    // }
    // [12:42:45 PM] Eike Stepper says: org.eclipse.emf.cdo.net4j.CDOSession.Options.setCommitTimeout(int)
    // org.eclipse.emf.cdo.net4j.CDOSession.Options.setProgressInterval(int)

    /* I believe this is a read session */
    isRead = true;
    if (cdoSession != null)
    {
      sessionID = cdoSession.getSessionID();
    }
  }

  public ObjectivityStoreAccessor(ObjectivityStore store, ITransaction transaction)
  {
    super(store, transaction);

    // for debugging...
    // if (transaction != null && transaction.getSession().getProtocol().getSession() instanceof
    // org.eclipse.emf.cdo.net4j.CDOSession)
    // {
    // CDOCommonSession commonSession = transaction.getSession().getProtocol().getSession();
    // org.eclipse.emf.cdo.net4j.CDOSession cdoSession = (org.eclipse.emf.cdo.net4j.CDOSession)commonSession;
    // cdoSession.options().setCommitTimeout(60000);
    // }
    /* I believe this is a write session */
    if (transaction != null)
    {
      sessionID = transaction.getSession().getSessionID();
    }
  }

  @Override
  protected void detachObjects(CDOID[] detachedObjects, CDOBranch branch, long timeStamp, OMMonitor monitor)
  {
    ensureSessionBegin();

    try
    {
      monitor.begin(detachedObjects.length);
      // Find all Objy containers to lock as one unit.
      Set<ooId> containerToLocks = new HashSet<ooId>();
      for (CDOID id : detachedObjects)
      {
        ooId containerID = OBJYCDOIDUtil.getContainerId(id);
        containerToLocks.add(containerID);
      }
      lockContainers(containerToLocks);

      InternalCDORevisionManager revisionManager = getStore().getRepository().getRevisionManager();

      for (CDOID id : detachedObjects)
      {

        InternalCDORevision revision = revisionManager.getRevision(id, branch.getHead(), CDORevision.UNCHUNKED,
            CDORevision.DEPTH_NONE, true);
        int version = ObjectUtil.equals(branch, revision.getBranch()) ? revision.getVersion()
            : CDOBranchVersion.FIRST_VERSION;

        detachObject(id, version, branch, timeStamp);
      }
    }
    finally
    {
      monitor.done();
    }
  }

  /***
   * @param CDOID
   * @param version
   * @param branch
   * @param timeStamp
   */
  private void detachObject(CDOID id, int version, CDOBranch branch, long timeStamp)
  {
    ensureSessionBegin();

    ObjyObject objyObject = getObject(id);

    if (TRACER_DEBUG.isEnabled())
    {
      TRACER_DEBUG.trace("Detaching id " + objyObject.ooId().getStoreString());
    }

    if (getStore().isRequiredToSupportAudits() || getStore().isRequiredToSupportBranches())
    {
      // pick the proper version.
      ObjyObject objyRevision = objyObject.getRevisionByVersion(version);
      objyRevision.setRevisedTime(timeStamp - 1);
      objyObject.detach(version, branch, timeStamp);
    }
    else
    {
      objyObject.setVersion(-version);
      objyObject.delete(this, objySession.getObjectManager());
    }

    // // we'll need to find it's containing object/resource and remove it from there.
    // // TODO - do we need to deal with dependent objects, i.e. delete them as well,
    // // is there a notion of delete propagate?
    // if (ObjySchema.isResource(getStore(), objyObject.objyClass()))
    // {
    // ObjyResourceList resourceList = objySession.getResourceList(getRepositoryName());
    // resourceList.remove(objyObject);
    // }
    objySession.getObjectManager().remove(objyObject); // removed it from the cache.
  }

  @Override
  protected void doActivate() throws Exception
  {
    // System.out.println(">>>>IS:<<<< StoreAccessor.doActivate() " + this);
    // getObjySession();
  }

  private void getObjySession()
  {
    // ISession cdoSession = getSession();
    // System.out.println(">>>>IS:<<< context's session: " + (cdoSession == null ? "null" : cdoSession.toString()));

    if (objySession != null)
    {
      return;
    }

    // get a session name.
    String sessionName = "Session_" + sessionID;

    if (isRead && objySession == null)
    {
      objySession = getStore().getConnection().getReadSessionFromPool(sessionName);
      // System.out.println(">>>>IS:<<<< Getting from Read Pool [name: " + sessionName + " - session:" + objySession +
      // "]");
    }
    else if (objySession == null)
    {
      objySession = getStore().getConnection().getWriteSessionFromPool(sessionName);
      // System.out.println(">>>>IS:<<<< Getting from Write Pool [name: " + sessionName + " - session:" + objySession +
      // "]");
    }
    if (!objySession.isOpen())
    {
      objySession.setMrowMode(oo.MROW);
      objySession.setWaitOption(45000);
      objySession.setAllowUnregisterableTypes(true);
      objySession.begin();
    }
  }

  private void returnObjySession()
  {
    // System.out.println(">>>>IS:<<<< Returning to pool, session: " + objySession);
    ensureSessionJoin();
    // testDetachWithoutRevision_CheckMainBranch() is crashing because objySession is null.
    // TBD: verify this case!!!
    if (objySession == null)
    {
      return;
    }

    if (objySession.isOpen())
    {
      objySession.commit(); // IS: we might need to abort instead.
    }
    // objySession.returnSessionToPool();
    getStore().getConnection().returnSessionToPool(objySession);
    objySession = null;
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    // System.out.println(">>>>IS:<<< StoreAccessor.doDeactivate() " + this);
    returnObjySession();
  }

  @Override
  protected void doPassivate() throws Exception
  {
    // System.out.println(">>>>IS:<<<< StoreAccessor.doPassivate() " + this);
    returnObjySession();
  }

  @Override
  protected void doUnpassivate() throws Exception
  {
    // System.out.println(">>>>IS:<<<< StoreAccessor.doUnpassivate() " + this);
    getObjySession();
  }

  // @Override
  // protected void setContext(Object context)
  // {
  // super.setContext(context);
  // System.out.println(">>>>IS:<<<< StoreAccessor.setContext() " + this + " - context: " + context.toString());
  // }

  @Override
  protected void rollback(CommitContext commitContext)
  {
    try
    {
      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.trace("Rollback session " + objySession);
      }
      // the rollback could be coming from another thread.
      ensureSessionJoin();
      if (objySession.isOpen())
      {
        objySession.abort();
        if (TRACER_DEBUG.isEnabled())
        {
          TRACER_DEBUG.trace("OBJY: session aborted - Session: " + objySession + " - open:" + objySession.isOpen());
        }
      }
    }
    catch (RuntimeException exception)
    {
      TRACER_INFO.trace(exception.getMessage(), exception);
      exception.printStackTrace();
      throw exception;
    }
  }

  @Override
  protected void writeRevisionDeltas(InternalCDORevisionDelta[] revisionDeltas, CDOBranch branch, long created,
      OMMonitor monitor)
  {
    ensureSessionBegin();

    try
    {
      monitor.begin(revisionDeltas.length);
      // Find all Objy containers to lock as one unit.
      Set<ooId> containerToLocks = new HashSet<ooId>();
      for (InternalCDORevisionDelta delta : revisionDeltas)
      {
        ooId containerID = OBJYCDOIDUtil.getContainerId(delta.getID());
        containerToLocks.add(containerID);
      }
      lockContainers(containerToLocks);

      for (InternalCDORevisionDelta revisionDelta : revisionDeltas)
      {
        writeRevisionDelta(revisionDelta, branch, created);
      }
    }
    finally
    {
      monitor.done();
    }
  }

  /**
   * Called for each revision delta.
   * 
   * @param delta
   * @param created
   * @param branch
   */
  private void writeRevisionDelta(InternalCDORevisionDelta delta, CDOBranch branch, long created)
  {
    ensureSessionBegin();

    int deltaVersion = delta.getVersion();

    ObjyObject objyObject = getObject(delta.getID());
    ObjyObject objyRevision = objyObject.getRevisionByVersion(deltaVersion);

    if (TRACER_DEBUG.isEnabled())
    {
      TRACER_DEBUG.format("Writing revision delta: {0}, v:{1} - OID:{2}, v: {3}", delta, deltaVersion, objyObject
          .ooId().getStoreString(), objyRevision.getVersion());
      TRACER_DEBUG.format("... delta branch ID: {0} - revision branch ID: {1}", branch.getID(),
          objyRevision.getBranchId());
    }
    // System.out.println(">>>IS: Delta Writing: " + delta.getID() + " - oid: " + objyObject.ooId().getStoreString());
    // System.out.println("\t - old version : " + delta.getVersion());
    // System.out.println("\t - created     : " + created);
    // System.out.println("\t - delta.branch: " + delta.getBranch().toString());
    // System.out.println("\t - branch      : " + branch.toString());
    // System.out.println("\t - branch TS   : " + branch.getPoint(created).getTimeStamp());
    // System.out.println("\t - delta       : " + delta.toString());
    // for debugging...

    if (objyRevision.getVersion() != deltaVersion)
    {
      throw new RuntimeException("ObjecitivityStoreAccessor : Dirty write");
    }

    ObjyObject newObjyRevision = null;

    if (getStore().isRequiredToSupportAudits())
    {
      // newObjyRevision = objySession.getObjectManager().copyRevision(this, objyRevision);
      objyRevision.setRevisedTime(branch.getPoint(created).getTimeStamp() - 1);
      // objyRevision.setRevisedBranchId(branch.getID();
      InternalCDORevision originalRevision = getStore().getRepository().getRevisionManager()
          .getRevisionByVersion(delta.getID(), delta, 0, true);

      InternalCDORevision newRevision = originalRevision.copy();

      newRevision.setVersion(deltaVersion + 1);
      newRevision.setBranchPoint(delta.getBranch().getPoint(created));
      newObjyRevision = objySession.getObjectManager().newObject(newRevision.getEClass(), objyRevision.ooId());
      newObjyRevision.update(this, newRevision);
      objyObject.addToRevisions(newObjyRevision);

      if (getStore().isRequiredToSupportBranches() /* && branch.getID() != CDOBranch.MAIN_BRANCH_ID */)
      {
        // add the newObjyRevision to the proper branch.
        ObjyBranch objyBranch = objySession.getBranchManager(getRepositoryName()).getBranch(branch.getID());
        ooObj anObj = ooObj.create_ooObj(newObjyRevision.ooId());
        objyBranch.addRevision(anObj);
      }
    }
    else
    {
      newObjyRevision = objyRevision;
    }

    ObjectivityFeatureDeltaWriter visitor = new ObjectivityFeatureDeltaWriter(newObjyRevision);

    delta.accept(visitor);

    newObjyRevision.setCreationTime(branch.getPoint(created).getTimeStamp());
    newObjyRevision.setVersion(deltaVersion + 1); // TODO - verify with Eike if this is true!!!
  }

  @Override
  public ObjectivityStore getStore()
  {
    return (ObjectivityStore)super.getStore();
  }

  /**
   * It seems that it will be called for both new objects and dirty objects.
   */
  @Override
  protected void writeRevisions(InternalCDORevision[] revisions, CDOBranch branch, OMMonitor monitor)
  {
    long start = System.currentTimeMillis();
    ensureSessionBegin();

    try
    {
      monitor.begin(revisions.length);
      // Find all Objy containers to lock as one unit.
      Set<ooId> containersToLock = new HashSet<ooId>();
      for (InternalCDORevision revision : revisions)
      {
        ooId containerID = OBJYCDOIDUtil.getContainerId(revision.getID());
        containersToLock.add(containerID);
      }
      // containersToLock.add(objySession.getBranchManager(getRepositoryName()).getContainer().getOid());
      // lockContainers(containersToLock);

      for (InternalCDORevision revision : revisions)
      {
        writeRevision(revision, monitor.fork());
      }
    }
    finally
    {
      monitor.done();
    }
    if (TRACER_DEBUG.isEnabled())
    {
      TRACER_DEBUG.trace("\t writeRevisions time: " + (System.currentTimeMillis() - start));
    }

  }

  /**
   * return an ObjyObject that represent the CDOID.
   */
  public ObjyObject getObject(CDOID id)
  {
    return objySession.getObjectManager().getObject(id);
  }

  private void lockContainers(Set<ooId> containerToLocks)
  {
    // Locks all containers for modified objects
    if (!containerToLocks.isEmpty())
    {
      ooId idsToLock[] = containerToLocks.toArray(new ooId[containerToLocks.size()]);
      while (true)
      {
        try
        {
          objySession.openContainers(idsToLock, oo.openReadWrite);
          break;
        }
        catch (Exception e)
        {
          TRACER_INFO.trace("Locking problem try again : " + e.getMessage());
          // this.ensureNewBeginSession();
          if (!objySession.isOpen())
          {
            TRACER_INFO.trace("Objy session is not open");
            // System.exit(-1); // TODO - this is temporary for debugging...
          }
        }
      }
    }
  }

  /*****
   * Use this code for heart beat. Async async = null; try { monitor.begin(getListMappings().size() + 1); async =
   * monitor.forkAsync(); reviseObject(accessor, id, timeStamp); } finally { async.stop(); monitor.done(); } [10:07:02
   * AM] Eike Stepper: that one takes care that the heartbeat goes on [10:07:13 AM] Eike Stepper: for indefinite time
   */

  private void writeRevision(InternalCDORevision revision, OMMonitor fork)
  {
    // EClass eClass = revision.getEClass();
    ObjyObject objyObject = getObject(revision.getID());

    if (TRACER_DEBUG.isEnabled())
    {
      TRACER_DEBUG.format("Writing revision: {0} - OID: {1}", revision, objyObject.ooId().getStoreString()); //$NON-NLS-1$
    }

    // System.out.println(">>>IS: Writing: " + revision.getID() + " - oid: " + objyObject.ooId().getStoreString());
    // System.out.println("\t - version    : " + revision.getVersion());
    // System.out.println("\t - timestamp  : " + revision.getTimeStamp());
    // System.out.println("\t - revised    : " + revision.getRevised());
    // System.out.println("\t - resourceId : " + revision.getResourceID());
    // System.out.println("\t - containerId: " + revision.getContainerID());
    // System.out.println("\t - branch     : " + revision.getBranch().toString());
    // System.out.println("\t - revision   : " + revision.toString());

    ObjyObject newObjyRevision = objyObject;

    if (revision.getVersion() > CDOBranchVersion.FIRST_VERSION) // we're updating other versions...
    {
      ObjyObject oldObjyRevision = objyObject.getRevisionByVersion(revision.getVersion() - 1);

      if (oldObjyRevision == null)
      {
        new IllegalStateException("Revision with version: " + (revision.getVersion() - 1) + " is not in the store."); //$NON-NLS-1$
      }
      if (getStore().isRequiredToSupportAudits())
      {
        // if we allow versioning, then create a new one here.
        // IS: I'm not sure if we'll be called here we always go to the writeRevisionDelta call.
        newObjyRevision = objySession.getObjectManager().newObject(revision.getEClass(), oldObjyRevision.ooId());
        objyObject.addToRevisions(newObjyRevision);

      }
      else
      {
        newObjyRevision = oldObjyRevision;
      }
    }

    if (getStore().isRequiredToSupportBranches())
    {
      // add the newObjyRevision to the proper branch.
      ObjyBranch objyBranch = objySession.getBranchManager(getRepositoryName()).getBranch(revision.getBranch().getID());
      ooObj anObj = ooObj.create_ooObj(newObjyRevision.ooId());
      objyBranch.addRevision(anObj);
    }

    newObjyRevision.update(this, revision);

    // if it's a resource, collect it.
    if (revision.isResourceNode())
    {
      // Add resource to the list
      ObjyResourceList resourceList = objySession.getResourceList(getRepositoryName());

      // before we update the data into the object we need to check
      // if it's a resource and we're trying to add a duplicate.
      // TODO - do we need to check for Folder and resouce, or is the isResourceNode()
      // check is enough?!!!
      if (revision.isResourceFolder() || revision.isResource())
      {
        // this call will throw exception if we have a duplicate resource we trying to add.
        resourceList.checkDuplicateResources(this, revision);
      }
      SmartLock.lock(newObjyRevision);
      resourceList.add(newObjyRevision);
    }
  }

  @Override
  protected void doCommit(OMMonitor monitor)
  {
    // ISession cdoSession = getSession();
    // System.out.println(">>>>IS:<<< doCommit() " + this + " - context's session: "
    // + (cdoSession == null ? "null" : cdoSession.toString()));

    long start = System.currentTimeMillis();
    try
    {
      // The commit request might come from a different thread.
      ensureSessionJoin();

      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.trace("Committing ..." + objySession + " nc:" + objySession.nestCount());
      }
      if (objySession.isOpen() == true)
      {
        objySession.commit();
      }
      else
      {
        TRACER_DEBUG.trace("Error: calling objySession.commit() without having an open trx.");
      }

      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.trace("Committed");
      }
    }
    catch (RuntimeException exception)
    {
      TRACER_ERROR.trace(exception.getMessage(), exception);
      exception.printStackTrace();
      throw exception;
    }
    if (TRACER_DEBUG.isEnabled())
    {
      TRACER_DEBUG.trace("\t commit time: " + (System.currentTimeMillis() - start));
    }
  }

  public IObjectivityStoreChunkReader createChunkReader(InternalCDORevision revision, EStructuralFeature feature)
  {
    return new ObjectivityStoreChunkReader(this, revision, feature);
  }

  public EPackage[] loadPackageUnit(InternalCDOPackageUnit packageUnit)
  {
    // using the packageUnit.getID() we'll read the object from the FD and get the bytes.
    byte[] bytes = null;

    ensureSessionBegin();

    if (TRACER_DEBUG.isEnabled())
    {
      TRACER_DEBUG.format("loadPackageUnit for: {0}", packageUnit.getID()); //$NON-NLS-1$
    }

    ObjyPackageHandler objyPackageHandler = getStore().getPackageHandler();

    bytes = objyPackageHandler.readPackageBytes(packageUnit);

    EPackage ePackage = createEPackage(packageUnit, bytes);

    return EMFUtil.getAllPackages(ePackage);
  }

  private EPackage createEPackage(InternalCDOPackageUnit packageUnit, byte[] bytes)
  {
    ResourceSet resourceSet = EMFUtil.newEcoreResourceSet(getPackageRegistry());
    return EMFUtil.createEPackage(packageUnit.getID(), bytes, zipped, resourceSet, false);
  }

  private CDOPackageRegistry getPackageRegistry()
  {
    return getStore().getRepository().getPackageRegistry();
  }

  /**
   * TODO - 1) make sure that we return the root resource when we asked for "null" pathPrefix. 2) Create the "null"
   * resource folder if it doesn't exist, perhaps when we initialize the store.
   */
  public void queryResources(QueryResourcesContext context)
  {
    ensureSessionBegin();

    String pathPrefix = context.getName();
    boolean exactMatch = context.exactMatch();
    // System.out.println(">>>>IS:<<<< queryResources() for : " + (pathPrefix == null ? "NULL" : pathPrefix)
    // + " - exactMatch: " + exactMatch);
    ObjyResourceList resourceList = objySession.getResourceList(getRepositoryName());
    int size = resourceList.size();
    if (size == 0) // nothing yet.
    {
      CDOID resourceID = OBJYCDOIDUtil.getCDOID(null);
      context.addResource(resourceID);
    }

    // TBD: We need to verify the folderID as well!!
    // CDOID folderID = org.eclipse.emf.cdo.common.id.CDOIDUtil.isNull(context.getFolderID()) ? null : context
    // .getFolderID();
    for (int i = 0; i < size; i++)
    {
      ObjyObject resource = resourceList.getResource(i);
      if (resource != null)
      {
        ObjyObject resourceRevision = resource;
        // get the proper revision of the resource (might need to refactor this code, see readRevision())
        if (getStore().isRequiredToSupportBranches())
        {
          resourceRevision = resource.getRevision(context.getTimeStamp(), context.getBranch().getID());
        }
        else if (getStore().isRequiredToSupportAudits())
        {
          resourceRevision = resource.getRevision(context.getTimeStamp(), CDOBranch.MAIN_BRANCH_ID);
        }

        if (resourceRevision == null || resourceRevision.getVersion() < 0)
        {
          continue;
        }

        String resourceName = ObjyResourceList.getResourceName(resourceRevision);
        if (exactMatch && pathPrefix != null && pathPrefix.equals(resourceName))
        {
          CDOID resourceID = OBJYCDOIDUtil.getCDOID(resource.ooId());
          if (!context.addResource(resourceID))
          {
            // System.out.println("  >>IS:<<<< queryResources() got: " +
            // resource.ooId().getStoreString() + " - version: " + resource.getVersion());
            // No more results allowed
            break; // don't continue
          }
        }
        else if (pathPrefix == null && resourceName == null)
        {
          CDOID resourceID = OBJYCDOIDUtil.getCDOID(resource.ooId());
          if (!context.addResource(resourceID))
          {
            // System.out.println("  >>IS:<<<< queryResources() got: " +
            // resource.ooId().getStoreString() + " - version: " + resource.getVersion());
            // No more results allowed
            break; // don't continue
          }
        }
        else if (!exactMatch && resourceName != null)
        {
          if (resourceName.startsWith(pathPrefix))
          {
            CDOID resourceID = OBJYCDOIDUtil.getCDOID(resource.ooId());
            context.addResource(resourceID);
          }
        }
        /***
         * TODO - verify if we need this code, originally from the old impl. CDOID lookupFolderID = (CDOID)
         * objectManager.getEContainer(resource); if (ObjectUtil.equals(lookupFolderID, folderID)) { boolean match =
         * exactMatch || pathPrefix == null; if (match) { CDOID resourceID = CDOIDUtil.getCDOID(resource.ooId()); if
         * (!context.addResource(resourceID)) { break; } } }
         ***/
      }
    }

  }

  public void queryXRefs(QueryXRefsContext context)
  {
    // TODO: implement ObjectivityStoreAccessor.queryXRefs(context)
    throw new UnsupportedOperationException();
  }

  /**
   * Read all package units from the store. It's the opposite of writePackageUnits
   */
  public Collection<InternalCDOPackageUnit> readPackageUnits()
  {

    if (TRACER_DEBUG.isEnabled())
    {
      TRACER_DEBUG.format("readPackageUnits()"); //$NON-NLS-1$
    }

    ensureSessionBegin();

    ObjyPackageHandler objyPackageHandler = getStore().getPackageHandler();

    Collection<InternalCDOPackageUnit> packageUnits = objyPackageHandler.readPackageUnits();

    return packageUnits;
  }

  /**
	 *
	 */
  public InternalCDORevision readRevision(CDOID id, CDOBranchPoint branchPoint, int listChunk,
      CDORevisionCacheAdder cache)
  {
    ensureSessionBegin();

    if (TRACER_DEBUG.isEnabled())
    {
      TRACER_DEBUG.format("Reading revision for: {0}", id); //$NON-NLS-1$
    }

    // we might have a proxy object!!!!

    ObjyObject objyObject = getObject(id);

    if (objyObject == null)
    {
      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.format("objyObject is NULL"); //$NON-NLS-1$
      }
      return null;
    }

    InternalCDORevision revision = createRevision(objyObject, id);
    revision.setBranchPoint(branchPoint);

    ObjyObject objyRevision = objyObject;

    if (getStore().isRequiredToSupportBranches())
    {
      objyRevision = objyObject.getRevision(branchPoint.getTimeStamp(), branchPoint.getBranch().getID());
    }
    else if (getStore().isRequiredToSupportAudits())
    {
      objyRevision = objyObject.getRevision(branchPoint.getTimeStamp(), CDOBranch.MAIN_BRANCH_ID);
    }

    if (objyRevision == null)
    {
      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.format("objyRevision is NULL"); //$NON-NLS-1$
      }
      return null;
    }

    // check the version
    if (objyRevision.getVersion() < 0)
    {
      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.format("...revision for: {0} - OID: {1} is detached.", id, objyObject.ooId().getStoreString()); //$NON-NLS-1$
      }
      EClass eClass = ObjySchema.getEClass(getStore(), objyObject.objyClass());
      return new DetachedCDORevision(eClass, id, branchPoint.getBranch(), -objyRevision.getVersion(),
          objyRevision.getCreationTime());
    }

    CDOBranchPoint branchPoint2 = revision.getBranch().getPoint(objyRevision.getCreationTime());
    revision.setBranchPoint(branchPoint2);

    if (TRACER_DEBUG.isEnabled())
    {
      TRACER_DEBUG.format("Fetching revision details for: {0} - OID: {1}", id, objyRevision.ooId().getStoreString()); //$NON-NLS-1$
    }

    boolean ok = objyRevision.fetch(this, revision, listChunk);

    return ok ? revision : null;
  }

  /**
	 *
	 */
  public InternalCDORevision readRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, int listChunk,
      CDORevisionCacheAdder cache)
  {
    ensureSessionBegin();

    // we might have a proxy object!!!!

    ObjyObject objyObject = getObject(id);
    if (objyObject == null)
    {
      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.format("objyObject is NULL"); //$NON-NLS-1$
      }
      return null;
    }

    ObjyObject objyRevision = null;
    objyRevision = objyObject.getRevisionByVersion(branchVersion.getVersion());
    // if (getStore().isRequiredToSupportAudits())
    // {
    // objyRevision = objyObject.getRevisionByVersion(branchVersion.getVersion());
    if (TRACER_DEBUG.isEnabled())
    {
      TRACER_DEBUG
          .format(
              "Reading revision by version {0} for: {1} - OID: {2}", branchVersion.getVersion(), id, objyObject.ooId().getStoreString()); //$NON-NLS-1$
    }

    // }
    // else
    // {
    // objyRevision = objyObject.getLastRevision();
    //
    // if (TRACER_DEBUG.isEnabled())
    // {
    // TRACER_DEBUG.format(
    //            "(None-Audit) Reading revision by version for: {0} - OID: {1}", id, objyObject.ooId().getStoreString()); //$NON-NLS-1$
    // }
    // }

    if (objyRevision == null)
    {
      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.format("objyRevision is NULL"); //$NON-NLS-1$
      }
      return null;
    }

    InternalCDORevision revision = createRevision(objyRevision, id);

    revision.setVersion(branchVersion.getVersion());
    revision.setBranchPoint(branchVersion.getBranch().getHead());

    // TODO - clean up the following 3 lines...
    // InternalCDOBranchManager branchManager = getStore().getRepository().getBranchManager();
    // CDOBranch mainBranch = branchManager.getBranch(CDOBranch.MAIN_BRANCH_ID);
    // //revision.setBranchPoint(CDOBranchUtil.createBranchPoint(mainBranch, 0));
    // revision.setBranchPoint(mainBranch.getHead());

    boolean ok = objyRevision.fetch(this, revision, listChunk);

    if (ok && objyRevision.getVersion() != branchVersion.getVersion())
    {
      throw new IllegalStateException("Can only retrieve current version " + revision.getVersion() + " for " + //$NON-NLS-1$ //$NON-NLS-2$
          id + " - version requested was " + branchVersion + "."); //$NON-NLS-1$ //$NON-NLS-2$
    }

    return ok ? revision : null;
  }

  /***
   * TODO - I haven't seen this being implemented in other stores. Find out what it suppose to mean? Could it be that we
   * need to refresh all the objects we have in the session, i.e. the weak list in ObjyObjectManager for that session!!
   */
  public void refreshRevisions()
  {
    // TODO Auto-generated method stub
  }

  public void writePackageUnits(InternalCDOPackageUnit[] packageUnits, OMMonitor monitor)
  {
    if (TRACER_DEBUG.isEnabled())
    {
      TRACER_DEBUG.format("writePackageUnits()"); //$NON-NLS-1$
    }

    ensureSessionBegin();

    ObjyPackageHandler objyPackageHandler = getStore().getPackageHandler();
    CDOPackageRegistry packageRegistry = getStore().getRepository().getPackageRegistry();

    for (InternalCDOPackageUnit packageUnit : packageUnits)
    {
      objyPackageHandler.writePackages(packageRegistry, packageUnit, monitor/* .fork() */);
    }
  }

  private InternalCDORevision createRevision(ObjyObject objyObject, CDOID id)
  {
    EClass eClass = ObjySchema.getEClass(getStore(), objyObject.objyClass());

    if (eClass == null)
    {
      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.trace("OBJY: Can't find eClass for id:" + id);
      }
      return null;
    }

    return getStore().createRevision(eClass, id);
  }

  private String getRepositoryName()
  {
    return getStore().getRepository().getName();
  }

  /***
   * For us, this function creates the skeleton objects in Objectivity, to be able to get the new ooId and pass it to
   * the commitContext. The objects data will be updated in the call to writeRevision().
   */
  @Override
  protected void addIDMappings(InternalCommitContext commitContext, OMMonitor monitor)
  {
    long start = System.currentTimeMillis();
    ensureSessionBegin();
    if (commitContext.getNewObjects().length > 0)
    {
      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.trace("creating " + commitContext.getNewObjects().length + " new objects and assigning new IDs");
      }

      InternalCDORevision[] newObjects = commitContext.getNewObjects();
      monitor.begin(newObjects.length);

      ObjyPlacementManagerLocal placementManager = new ObjyPlacementManagerLocal(getStore(), objySession, commitContext);

      // iterate over the list and skip the ones we already have created.
      for (InternalCDORevision revision : newObjects)
      {
        placementManager.processRevision(revision);
      }
    }
    if (TRACER_DEBUG.isEnabled())
    {
      TRACER_DEBUG.trace("addIDMappings time: " + (System.currentTimeMillis() - start));
    }
  }

  @Override
  protected CDOID getNextCDOID(CDORevision revision)
  {
    // Never called
    throw new UnsupportedOperationException();
  }

  /***************************
   * Local Utility functions.
   ***************************/
  protected void ensureSessionBegin()
  {
    getObjySession();
    objySession.join();
    if (!objySession.isOpen())
    {
      objySession.begin();
    }
    // System.out.println(">>>>IS:<<<< ensureBeginSession() - session: " + objySession +
    // " [open: "+ objySession.isOpen() + "]");
  }

  private void ensureSessionJoin()
  {
    // we better have a session for this store.
    assert objySession != null;
    // testSwitchViewTarget() is crashing because objySession is null.
    // TBD: verify this case!!!
    if (objySession != null)
    {
      objySession.join();
    }
  }

  // *********************************************
  // New APIs for CDO 3.0
  // *********************************************

  public void loadCommitInfos(CDOBranch branch, long startTime, long endTime, CDOCommitInfoHandler handler)
  {
    ensureSessionBegin();

    ObjyCommitInfoHandler commitInfoHandler = getStore().getCommitInfoHandler();
    List<ObjyCommitInfo> commitInfoList = commitInfoHandler.getCommitInfo(branch, startTime, endTime);

    InternalSessionManager manager = getSession().getManager();
    InternalRepository repository = manager.getRepository();
    InternalCDOBranchManager branchManager = repository.getBranchManager();
    InternalCDOCommitInfoManager commitInfoManager = repository.getCommitInfoManager();

    for (ObjyCommitInfo ooCommitInfo : commitInfoList)
    {
      long timeStamp = ooCommitInfo.getTimeStamp();
      long previousTimeStamp = ooCommitInfo.getPreviousTimeStamp();
      String userID = ooCommitInfo.getUserId();
      String comment = ooCommitInfo.getComment();
      CDOBranch infoBranch = branch;
      if (infoBranch == null)
      {
        int id = ooCommitInfo.getBranchId();
        infoBranch = branchManager.getBranch(id);
      }

      CDOCommitInfo commitInfo = commitInfoManager.createCommitInfo(infoBranch, timeStamp, previousTimeStamp, userID,
          comment, null);
      handler.handleCommitInfo(commitInfo);
    }

  }

  @Override
  protected void writeCommitInfo(CDOBranch branch, long timeStamp, long previousTimeStamp, String userID,
      String comment, OMMonitor monitor)
  {
    ensureSessionBegin();
    // we need to write the following...
    // ...branch.getID(), timeStamp, userID, comment.
    ObjyCommitInfoHandler commitInfoHandler = getStore().getCommitInfoHandler();
    commitInfoHandler.writeCommitInfo(branch.getID(), timeStamp, previousTimeStamp, userID, comment);
  }

  public IQueryHandler getQueryHandler(org.eclipse.emf.cdo.common.util.CDOQueryInfo info)
  {
    if (ObjectivityQueryHandler.QUERY_LANGUAGE.equals(info.getQueryLanguage()))
    {
      return new ObjectivityQueryHandler(this);
    }

    return null;
  }

  public Pair<Integer, Long> createBranch(int branchID, BranchInfo branchInfo)
  {
    ensureSessionBegin();
    return objySession.getBranchManager(getRepositoryName()).createBranch(branchID, branchInfo);
  }

  public BranchInfo loadBranch(int branchID)
  {
    ensureSessionBegin();
    ObjyBranch objyBranch = objySession.getBranchManager(getRepositoryName()).getBranch(branchID);
    return objyBranch != null ? objyBranch.getBranchInfo() : null;
  }

  public int loadBranches(int startID, int endID, CDOBranchHandler branchHandler)
  {
    int count = 0;
    List<ObjyBranch> branches = objySession.getBranchManager(getRepositoryName()).getBranches(startID, endID);
    InternalCDOBranchManager branchManager = getStore().getRepository().getBranchManager();

    for (ObjyBranch objyBranch : branches)
    {
      InternalCDOBranch branch = branchManager
          .getBranch(objyBranch.getBranchId(), new BranchInfo(objyBranch.getBranchName(), objyBranch.getBaseBranchId(),
              objyBranch.getBaseBranchTimeStamp()));
      branchHandler.handleBranch(branch);
      count++;
    }

    return count;

  }

  public SubBranchInfo[] loadSubBranches(int branchID)
  {
    ensureSessionBegin();
    List<SubBranchInfo> result = new ArrayList<SubBranchInfo>();
    List<ObjyBranch> objyBranchList = objySession.getBranchManager(getRepositoryName()).getSubBranches(branchID);
    for (ObjyBranch objyBranch : objyBranchList)
    {
      SubBranchInfo subBranchInfo = new SubBranchInfo(objyBranch.getBranchId(), objyBranch.getBranchName(),
          objyBranch.getBaseBranchTimeStamp());
      result.add(subBranchInfo);
    }
    return result.toArray(new SubBranchInfo[result.size()]);
  }

  public void handleRevisions(EClass eClass, CDOBranch branch, long timeStamp, boolean exactTime,
      CDORevisionHandler handler)
  {
    // TODO: implement ObjectivityStoreAccessor.handleRevisions(eClass, branch, timeStamp, exactTime, handler)
    throw new UnsupportedOperationException();
  }

  public Set<CDOID> readChangeSet(OMMonitor monitor, CDOChangeSetSegment... segments)
  {
    monitor.begin(segments.length);

    try
    {
      ensureSessionBegin();

      ObjyBranchManager objyBranchManager = objySession.getBranchManager(getRepositoryName());
      ObjyObjectManager objyObjectManager = objySession.getObjectManager();
      Set<CDOID> results = new HashSet<CDOID>();

      // get all revisions that has branchId, and creation timestamp, and perhaps revised before
      // end timestamp or haven't been revised.
      for (CDOChangeSetSegment segment : segments)
      {
        ObjyBranch objyBranch = objyBranchManager.getBranch(segment.getBranch().getID());
        // query the branch revisions for the time range.
        SortedSet<?> revisions = objyBranch.getRevisions();
        readChangeSet(monitor.fork(), segment, objyObjectManager, revisions, results);
      }

      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.trace("ChangeSet " + results.toString());
      }

      return results;
    }
    finally
    {
      monitor.done();
    }
  }

  protected void readChangeSet(OMMonitor monitor, CDOChangeSetSegment segment, ObjyObjectManager objyObjectManager,
      SortedSet<?> revisions, Set<CDOID> results)
  {
    int size = revisions.size();
    monitor.begin(size);

    try
    {
      Iterator<?> objItr = revisions.iterator();
      while (objItr.hasNext())
      {
        ooObj anObj = (ooObj)objItr.next();
        ObjyObject objyObject = objyObjectManager.getObject(anObj.getOid());
        long creationTime = objyObject.getCreationTime();
        long revisedTime = objyObject.getRevisedTime();
        if (creationTime >= segment.getTimeStamp() && (revisedTime <= segment.getEndTime() || revisedTime == 0))
        {
          results.add(objyObject.getRevisionId());
        }

        monitor.worked();
      }
    }
    finally
    {
      monitor.done();
    }
  }

  public void queryLobs(List<byte[]> ids)
  {
    // TODO: implement ObjectivityStoreAccessor.queryLobs(ids)
    throw new UnsupportedOperationException();
  }

  public void handleLobs(long fromTime, long toTime, CDOLobHandler handler) throws IOException
  {
    // TODO: implement ObjectivityStoreAccessor.handleLobs(fromTime, toTime, handler)
    throw new UnsupportedOperationException();
  }

  public void loadLob(byte[] id, OutputStream out) throws IOException
  {
    // TODO: implement ObjectivityStoreAccessor.loadLob(id, out)
    throw new UnsupportedOperationException();
  }

  @Override
  protected void writeBlob(byte[] id, long size, InputStream inputStream) throws IOException
  {
    // TODO: implement ObjectivityStoreAccessor.writeBlob(id, size, inputStream)
    throw new UnsupportedOperationException();
  }

  @Override
  protected void writeClob(byte[] id, long size, Reader reader) throws IOException
  {
    // TODO: implement ObjectivityStoreAccessor.writeClob(id, size, reader)
    throw new UnsupportedOperationException();
  }
}
