/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.common.id.CDOIDMeta;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.revision.cache.CDORevisionCacheAdder;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.internal.objectivity.bundle.OM;
import org.eclipse.emf.cdo.server.internal.objectivity.clustering.ObjyPlacementManagerLocal;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyCommitInfoHandler;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyObject;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyPackageHandler;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjySchema;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyScope;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjySession;
import org.eclipse.emf.cdo.server.internal.objectivity.schema.ObjyBranch;
import org.eclipse.emf.cdo.server.internal.objectivity.schema.ObjyCommitInfo;
import org.eclipse.emf.cdo.server.internal.objectivity.schema.ObjyResourceList;
import org.eclipse.emf.cdo.server.internal.objectivity.utils.OBJYCDOIDUtil;
import org.eclipse.emf.cdo.server.internal.objectivity.utils.ObjyDb;
import org.eclipse.emf.cdo.server.objectivity.IObjectivityStoreAccessor;
import org.eclipse.emf.cdo.server.objectivity.IObjectivityStoreChunkReader;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.commit.CDOChangeSetSegment;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.DetachedCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;
import org.eclipse.emf.cdo.spi.server.StoreAccessor;

import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import com.objy.db.app.oo;
import com.objy.db.app.ooId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Simon McDuff
 * @author Ibrahim Sallam
 */
/**
 * @author Simon McDuff
 */
/**
 * @author Simon McDuff
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

      for (CDOID id : detachedObjects)
      {
        detachObject(id, timeStamp);
      }
    }
    finally
    {
      monitor.done();
    }
  }

  /***
   * @param id
   * @param timeStamp
   */
  private void detachObject(CDOID id, long timeStamp)
  {
    ensureSessionBegin();

    ObjyObject objyObject = getObject(id);

    // if (TRACER_DEBUG.isEnabled())
    {
      TRACER_DEBUG.trace("Detaching id " + objyObject.ooId().getStoreString());
    }

    if (getStore().isRequiredToSupportAudits())
    {
      // just revise.
      objyObject.setRevisedTime(timeStamp);
    }
    else
    {
      // we'll need to find it's containing object/resource and remove it from there.
      // we set the version to (-1) and leave it.
      // TODO - do we need to deal with dependent objects, i.e. delete them as well,
      // is there a notion of delete propagate?
      if (ObjySchema.isResource(getStore(), objyObject.objyClass()))
      {
        ObjyResourceList resourceList = objySession.getResourceList();
        resourceList.remove(objyObject);
      }
      objySession.getObjectManager().delete(this, objyObject);
    }

  }

  @Override
  protected void doActivate() throws Exception
  {
    // System.out.println(">>>>IS:<<<< StoreAccessor.doActivate()");
    // getObjySession();
  }

  private void getObjySession()
  {
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
    if (objySession.isOpen())
    {
      objySession.commit();
    }
    // objySession.returnSessionToPool();
    getStore().getConnection().returnSessionToPool(objySession);
    objySession = null;
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    // System.out.println(">>>>IS:<<< StoreAccessor.doDeactivate()");
    returnObjySession();
  }

  @Override
  protected void doPassivate() throws Exception
  {
    // System.out.println(">>>>IS:<<<< StoreAccessor.doPassivate()");
    returnObjySession();
  }

  @Override
  protected void doUnpassivate() throws Exception
  {
    // System.out.println(">>>>IS:<<<< StoreAccessor.doUnpassivate()");
    getObjySession();
  }

  @Override
  protected void rollback(CommitContext commitContext)
  {
    try
    {
      // if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.trace("Rollback session " + objySession);
      }
      // the rollback could be coming from another thread.
      ensureSessionJoin();
      if (objySession.isOpen())
      {
        objySession.abort();
      }
      System.out.println("OBJY: session aborted - Session: " + objySession + " - open:" + objySession.isOpen());
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

    ObjyObject objyObject = getObject(delta.getID());

    if (TRACER_DEBUG.isEnabled())
    {
      // TRACER_DEBUG.trace("Writing delta for object with id " + objyObject.ooId().getStoreString());
      TRACER_DEBUG.format("Writing revision delta: {0} - OID: {1}", delta, objyObject.ooId().getStoreString()); //$NON-NLS-1$
    }
    // System.out.println(">>>IS: Delta Writing: " + delta.getID() + " - oid: " + objyObject.ooId().getStoreString());
    // System.out.println("\t - old version : " + delta.getVersion());
    // System.out.println("\t - created     : " + created);
    // System.out.println("\t - delta.branch: " + delta.getBranch().toString());
    // System.out.println("\t - branch      : " + branch.toString());
    // System.out.println("\t - branch TS   : " + branch.getPoint(created).getTimeStamp());
    // System.out.println("\t - delta       : " + delta.toString());
    // for debugging...
    ObjyObject objyRevision = objyObject.getLastRevision();
    int deltaVersion = delta.getVersion();

    if (objyRevision.getVersion() != deltaVersion)
    {
      throw new RuntimeException("ObjecitivityStoreAccessor : Dirty write");
    }

    ObjyObject newObjyRevision = null;

    if (getStore().isRequiredToSupportAudits())
    {
      // newObjyRevision = objySession.getObjectManager().copyRevision(this, objyRevision);
      objyRevision.setRevisedTime(branch.getPoint(created).getTimeStamp() - 1);
      int id = branch.getID();
      // objyRevision.setRevisedBranchId(branch.getID();
      InternalCDORevision originalRevision = getStore().getRepository().getRevisionManager().getRevisionByVersion(
          delta.getID(), delta, 0, true);

      InternalCDORevision newRevision = originalRevision.copy();

      newRevision.setVersion(deltaVersion + 1);
      newRevision.setBranchPoint(delta.getBranch().getPoint(created));
      newObjyRevision = objySession.getObjectManager().newObject(newRevision.getEClass(), objyRevision.ooId());
      newObjyRevision.update(this, newRevision);
      objyRevision.addToRevisions(newObjyRevision);
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
      Set<ooId> containerToLocks = new HashSet<ooId>();
      for (InternalCDORevision revision : revisions)
      {
        ooId containerID = OBJYCDOIDUtil.getContainerId(revision.getID());
        containerToLocks.add(containerID);
      }
      lockContainers(containerToLocks);

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
  public ObjyObject getObject(CDOID cdoId)
  {
    return objySession.getObjectManager().getObject(cdoId);
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
            System.exit(-1); // TODO - this is temporary for debugging...
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
      ObjyObject oldObjyRevision = objyObject.getRevision(revision.getVersion() - 1);

      if (oldObjyRevision == null)
      {
        new IllegalStateException("Revision with version: " + (revision.getVersion() - 1) + " is not in the store."); //$NON-NLS-1$
      }
      if (getStore().isRequiredToSupportAudits())
      {
        // if we allow versioning, then create a new one here.
        // objyRevision = objySession.getObjectManager().copyRevision(this, oldObjyRevision);

        InternalCDORevision newRevision = revision.copy();
        newObjyRevision = objySession.getObjectManager().newObject(newRevision.getEClass(), oldObjyRevision.ooId());
        oldObjyRevision.addToRevisions(newObjyRevision);
      }
      else
      {
        newObjyRevision = oldObjyRevision;
      }
    }

    newObjyRevision.update(this, revision);

  }

  @Override
  protected void doCommit(OMMonitor monitor)
  {
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
    System.out.println();

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

    ObjyScope objyScope = new ObjyScope(ObjyDb.CONFIGDB_NAME, ObjyDb.PACKAGESTORE_CONT_NAME);
    ObjyPackageHandler packageHandler = new ObjyPackageHandler(getStore(), objyScope, true);

    bytes = packageHandler.readPackageBytes(packageUnit);

    EPackage ePackage = createEPackage(packageUnit, bytes);

    return EMFUtil.getAllPackages(ePackage);
  }

  private EPackage createEPackage(InternalCDOPackageUnit packageUnit, byte[] bytes)
  {
    return EMFUtil.createEPackage(packageUnit.getID(), bytes, zipped, getPackageRegistry());
  }

  private CDOPackageRegistry getPackageRegistry()
  {
    return getStore().getRepository().getPackageRegistry();
  }

  private InternalCDOPackageRegistry getInternalPackageRegistry()
  {
    return (InternalCDOPackageRegistry)getPackageRegistry();
  }

  // TODO - move the following two calls to perhaps a MetaDataManager (as in cdo.db package).
  // we shouldn't pollute this class with extra stuff.
  // The meta info is definitely useful for FeatureMapEntry details.
  public long getMetaID(EModelElement modelElement)
  {
    CDOID cdoid = getInternalPackageRegistry().getMetaInstanceMapper().lookupMetaInstanceID(
        (InternalEObject)modelElement);
    return CDOIDUtil.getLong(cdoid);
  }

  public EModelElement getMetaInstance(long id)
  {
    CDOIDMeta cdoid = CDOIDUtil.createMeta(id);
    InternalEObject metaInstance = getInternalPackageRegistry().getMetaInstanceMapper().lookupMetaInstance(cdoid);
    return (EModelElement)metaInstance;
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
    System.out.println(">>>>IS:<<<< queryResources() for : " + (pathPrefix == null ? "NULL" : pathPrefix)
        + " - exactMatch: " + exactMatch);
    ObjyResourceList resourceList = objySession.getResourceList();
    int size = resourceList.size();
    if (size == 0) // nothing yet.
    {
      CDOID resourceID = OBJYCDOIDUtil.getCDOID(null);
      context.addResource(resourceID);
    }

    CDOID folderID = org.eclipse.emf.cdo.common.id.CDOIDUtil.isNull(context.getFolderID()) ? null : context
        .getFolderID();
    for (int i = 0; i < size; i++)
    {
      ObjyObject resource = resourceList.getResource(i);
      // ensureBeginSession(resource);
      if (resource != null)
      {
        String resourceName = ObjyResourceList.getResourceName(resource);
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
    ObjyScope objyScope = new ObjyScope(ObjyDb.CONFIGDB_NAME, ObjyDb.PACKAGESTORE_CONT_NAME);

    ObjyPackageHandler packageHandler = new ObjyPackageHandler(getStore(), objyScope, true);

    Collection<InternalCDOPackageUnit> packageUnits = packageHandler.readPackageUnits();

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
      return null;
    }

    ObjyObject objyRevision = objyObject.getLastRevision();

    // check the version of the object first.
    if (objyRevision.getVersion() == -1)
    {
      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.format("...revision for: {0} - OID: {1} is detached.", id, objyObject.ooId().getStoreString()); //$NON-NLS-1$
      }
      EClass eClass = ObjySchema.getEClass(getStore(), objyRevision.objyClass());
      return new DetachedCDORevision(eClass, id, branchPoint.getBranch(), 1, branchPoint.getTimeStamp());
    }

    InternalCDORevision revision = getStore().createRevision(objyObject, id);

    // TODO - clean up the following 3 lines...
    InternalCDOBranchManager branchManager = getStore().getRepository().getBranchManager();
    // CDOBranch mainBranch = branchManager.getBranch(CDOBranch.MAIN_BRANCH_ID);
    revision.setBranchPoint(branchPoint);
    CDOBranchPoint branchPoint2 = revision.getBranch().getPoint(objyObject.getCreationTime());
    revision.setBranchPoint(branchPoint2);

    if (TRACER_DEBUG.isEnabled())
    {
      TRACER_DEBUG.format("Fetching revision details for: {0} - OID: {1}", id, objyObject.ooId().getStoreString()); //$NON-NLS-1$
    }

    boolean ok = objyObject.fetch(this, revision, listChunk);

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
      return null;
    }

    ObjyObject objyRevision = null;
    if (getStore().isRequiredToSupportAudits())
    {
      objyRevision = objyObject.getRevision(branchVersion.getVersion());
    }
    else
    {
      objyRevision = objyObject.getLastRevision();
    }

    if (TRACER_DEBUG.isEnabled())
    {
      TRACER_DEBUG.format("Reading revision by version for: {0} - OID: {1}", id, objyObject.ooId().getStoreString()); //$NON-NLS-1$
    }

    InternalCDORevision revision = getStore().createRevision(objyRevision, id);

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
    ObjyScope objyScope = new ObjyScope(ObjyDb.CONFIGDB_NAME, ObjyDb.PACKAGESTORE_CONT_NAME);

    ObjyPackageHandler packageHandler = new ObjyPackageHandler(getStore(), objyScope, true);

    for (InternalCDOPackageUnit packageUnit : packageUnits)
    {
      packageHandler.writePackages(packageUnit, monitor/* .fork() */);
    }
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
    objySession.join();
  }

  // *********************************************
  // New APIs for CDO 3.0
  // *********************************************

  public void loadCommitInfos(CDOBranch branch, long startTime, long endTime, CDOCommitInfoHandler handler)
  {
    ensureSessionBegin();

    ObjyCommitInfoHandler commitInfoHandler = getStore().getCommitInfoHandle();
    List<ObjyCommitInfo> commitInfoList = commitInfoHandler.getCommitInfo(branch, startTime, endTime);

    InternalSessionManager manager = getSession().getManager();
    InternalRepository repository = manager.getRepository();
    InternalCDOBranchManager branchManager = repository.getBranchManager();
    InternalCDOCommitInfoManager commitInfoManager = repository.getCommitInfoManager();

    for (ObjyCommitInfo ooCommitInfo : commitInfoList)
    {
      long timeStamp = ooCommitInfo.getTimeStamp();
      String userID = ooCommitInfo.getUserId();
      String comment = ooCommitInfo.getComment();
      CDOBranch infoBranch = branch;
      if (infoBranch == null)
      {
        int id = ooCommitInfo.getBranchId();
        infoBranch = branchManager.getBranch(id);
      }

      CDOCommitInfo commitInfo = commitInfoManager.createCommitInfo(infoBranch, timeStamp, userID, comment, null);
      handler.handleCommitInfo(commitInfo);
    }

  }

  @Override
  protected void writeCommitInfo(CDOBranch branch, long timeStamp, String userID, String comment, OMMonitor monitor)
  {
    ensureSessionBegin();
    // we need to write the following...
    // ...branch.getID(), timeStamp, userID, comment.
    ObjyCommitInfoHandler commitInfoHandler = getStore().getCommitInfoHandle();
    commitInfoHandler.writeCommitInfo(branch.getID(), timeStamp, userID, comment);
  }

  public IQueryHandler getQueryHandler(org.eclipse.emf.cdo.common.util.CDOQueryInfo info)
  {
    if (ObjectivityQueryHandler.QUERY_LANGUAGE.equals(info.getQueryLanguage()))
    {
      return new ObjectivityQueryHandler(this);
    }

    return null;
  }

  public int createBranch(BranchInfo branchInfo)
  {
    ensureSessionBegin();
    return objySession.getBranchManager(getRepositoryName()).createBranch(branchInfo);
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
      InternalCDOBranch branch = branchManager.getBranch(objyBranch.getBranchId(), new BranchInfo(objyBranch
          .getBranchName(), objyBranch.getBaseBranchId(), objyBranch.getBaseBranchTimeStamp()));
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
      SubBranchInfo subBranchInfo = new SubBranchInfo(objyBranch.getBranchId(), objyBranch.getBranchName(), objyBranch
          .getBaseBranchTimeStamp());
    }
    return result.toArray(new SubBranchInfo[result.size()]);
  }

  public void handleRevisions(EClass eClass, CDOBranch branch, long timeStamp, CDORevisionHandler handler)
  {
    throw new UnsupportedOperationException();
  }

  public Set<CDOID> readChangeSet(CDOChangeSetSegment... segments)
  {
    throw new UnsupportedOperationException();
  }

}
