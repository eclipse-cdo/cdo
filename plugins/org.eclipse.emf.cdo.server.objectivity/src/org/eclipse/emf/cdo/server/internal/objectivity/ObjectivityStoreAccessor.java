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
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDExternal;
import org.eclipse.emf.cdo.common.lob.CDOLobHandler;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockArea.Handler;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionCacheAdder;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStoreAccessor.DurableLocking;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.internal.objectivity.bundle.OM;
import org.eclipse.emf.cdo.server.internal.objectivity.clustering.ObjyPlacementManagerLocal;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyCommitInfoHandler;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyObject;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyObjectManager;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjyPackageHandler;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjySchema;
import org.eclipse.emf.cdo.server.internal.objectivity.db.ObjySession;
import org.eclipse.emf.cdo.server.internal.objectivity.schema.ObjyBase;
import org.eclipse.emf.cdo.server.internal.objectivity.schema.ObjyBranch;
import org.eclipse.emf.cdo.server.internal.objectivity.schema.ObjyBranchManager;
import org.eclipse.emf.cdo.server.internal.objectivity.schema.ObjyCommitInfo;
import org.eclipse.emf.cdo.server.internal.objectivity.schema.ObjyLockAreaManager;
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
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.ResourceSet;

import com.objy.db.LockNotGrantedException;
import com.objy.db.app.oo;
import com.objy.db.app.ooId;
import com.objy.db.app.ooObj;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

/**
 * @author Simon McDuff
 * @author Ibrahim Sallam
 */
public class ObjectivityStoreAccessor extends StoreAccessor implements IObjectivityStoreAccessor, DurableLocking
{
  private static final ContextTracer TRACER_DEBUG = new ContextTracer(OM.DEBUG, ObjectivityStoreAccessor.class);

  private static final ContextTracer TRACER_INFO = new ContextTracer(OM.INFO, ObjectivityStoreAccessor.class);

  protected ObjySession objySession = null;

  // protected ObjyObjectManager objectManager = null;

  protected boolean isRead = false;

  protected int sessionID = 0; // used to tag the Objy session in the session pool

  boolean zipped = true; // TODO - might make this configurable...

  private HashMap<CDOID, ObjyObject> newObjyObjectsMap = new HashMap<CDOID, ObjyObject>();

  private long readRevisionTime = 0;

  private int readRevisionCount = 0;

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

    // System.out.println(">>>>IS:<<< detachObjects() " + this + " - objy session: " + objySession.toString());
    // objySession.addToLog("IS:>>>", "detachObjects - begin");

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
      objySession.lockContainers(containerToLocks);

      InternalCDORevisionManager revisionManager = getStore().getRepository().getRevisionManager();

      long tStart = System.currentTimeMillis();
      for (CDOID id : detachedObjects)
      {

        InternalCDORevision revision = revisionManager.getRevision(id, branch.getHead(), CDORevision.UNCHUNKED,
            CDORevision.DEPTH_NONE, true);
        int version = ObjectUtil.equals(branch, revision.getBranch()) ? revision.getVersion()
            : CDOBranchVersion.FIRST_VERSION;

        detachObject(id, version, branch, timeStamp);
      }
      // objySession.addToLog("IS:>>>", "detachObjects - end");
      long tDiff = System.currentTimeMillis() - tStart;
      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.trace("detach objects: " + detachedObjects.length + " - time: " + tDiff);
      }

    }
    finally
    {
      monitor.done();
    }

    // System.out.println(">>>>IS:<<< detachObjects() DONE " + this + " - objy session: " + objySession.toString());
  }

  /***
   * @param CDOID
   * @param version
   * @param branch
   * @param timeStamp
   */
  private void detachObject(CDOID id, int version, CDOBranch branch, long timeStamp)
  {
    // ensureSessionBegin();

    ObjyObject objyObject = getObject(id);

    if (TRACER_DEBUG.isEnabled())
    {
      TRACER_DEBUG.trace("Detaching id " + id + " - OID: " + objyObject.ooId().getStoreString() + " verions: "
          + version + " in Branch: " + branch.getID() + " and timeStamp: " + timeStamp);
    }

    if (getStore().isRequiredToSupportAudits())
    {
      if (version > CDOBranchVersion.FIRST_VERSION)
      {
        // pick the proper version of that branch to revise it. Otherwise, the detached version will
        // be in a different branch.
        ObjyObject objyRevision = objyObject.getRevisionByVersion(version, branch.getID(),
            objySession.getObjectManager());
        if (objyRevision.getVersion() < 0)
        {
          TRACER_DEBUG.trace("... OBJ is already detached...");
        }
        objyRevision.setRevisedTime(timeStamp - 1);
      }
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
    // System.out.println("IS: StoreAccessor.doActivate() " + this);
    // getObjySession();
  }

  public ObjySession getObjySession()
  {
    int currSessionID = sessionID;

    if (!isReader())
    {
      // The only reason we do this is because there was a switching of context in the middle
      // of a transaction, and we want to ensure that we continue with the proper session that
      // is holding the data.
      ITransaction cdoTrx = getTransaction();
      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG
            .trace("getObjySession() - context's transaction: " + (cdoTrx == null ? "null" : cdoTrx.toString()));
      }
      if (cdoTrx != null)
      {
        sessionID = cdoTrx.getSession().getSessionID();
      }
    }

    if (objySession != null && currSessionID == sessionID)
    {
      return objySession;
    }

    // get a session name.
    String sessionName = "Session_" + sessionID;

    if (objySession != null)
    {
      objySession.returnSessionToPool();
    }
    if (isReader())
    {
      objySession = getStore().getConnection().getReadSessionFromPool(sessionName + "_" + getRepositoryName());
      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.trace("  getObjySession from read pool, session: " + objySession.toString() + " - isOpen: "
            + objySession.isOpen() + " - sessionName:" + objySession.getName());
      }
    }
    else
    {
      objySession = getStore().getConnection().getWriteSessionFromPool(sessionName + "_" + getRepositoryName());
      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.trace("  getObjySession from write pool, session: " + objySession.toString() + " - isOpen: "
            + objySession.isOpen() + " - sessionName:" + objySession.getName());
      }
    }

    if (!objySession.isOpen())
    {
      objySession.setMrowMode(oo.MROW);
      objySession.setWaitOption(45000);
      objySession.setAllowUnregisterableTypes(true);
      if (isRead)
      {
        objySession.setOpenMode(oo.openReadOnly);
      }
      else
      {
        objySession.setOpenMode(oo.openReadWrite);
      }
      objySession.begin();
      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.trace("     calling session.begin() for " + objySession.toString() + " - isRead: " + isRead);
      }
    }
    else
    {
      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.trace("     session " + objySession.toString() + " already open.");
      }
    }

    return objySession;
  }

  private void returnObjySession()
  {
    // System.out.println("IS: Returning to pool, session: " + objySession + " - name: " +
    // objySession.getName());
    ensureSessionJoin();
    // testDetachWithoutRevision_CheckMainBranch() is crashing because objySession is null.
    // TBD: verify this case!!!
    if (objySession == null)
    {
      return;
    }

    if (objySession.isOpen())
    {
      // System.out.println("IS: commiting session: " + objySession + " - name: " + objySession.getName());
      objySession.commit(); // IS: we might need to abort instead.
    }
    objySession.returnSessionToPool();
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
    // System.out.println("IS: StoreAccessor.doPassivate() " + this);
    returnObjySession();
  }

  @Override
  protected void doUnpassivate() throws Exception
  {
    // System.out.println("IS: StoreAccessor.doUnpassivate() " + this);
    // IS: don't call this now, in case we don't have a context.
    // getObjySession();
  }

  // @Override
  // protected void setContext(Object context)
  // {
  // super.setContext(context);
  // System.out.println("IS: StoreAccessor.setContext() " + this + " - context: " + context.toString());
  // }

  @Override
  protected void doRollback(CommitContext commitContext)
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
    // System.out.println(">>>>IS:<<< writeRevisionDeltas() " + this + " - objy session: " + objySession.toString());
    long start = System.nanoTime();
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
      objySession.lockContainers(containerToLocks);

      for (InternalCDORevisionDelta revisionDelta : revisionDeltas)
      {
        writeRevisionDelta(revisionDelta, branch, created);
      }
    }
    finally
    {
      monitor.done();
    }
    if (TRACER_DEBUG.isEnabled())
    {
      TRACER_DEBUG.trace(" writeRevisionDeltas: " + revisionDeltas.length + " - time: " + (System.nanoTime() - start)
          / 1000000.0);
    }
  }

  /**
   * Called for each revision delta.
   * 
   * @param delta
   * @param created
   * @param branch
   */
  // private void writeRevisionDelta2(InternalCDORevisionDelta delta, CDOBranch branch, long created)
  // {
  // // ensureSessionBegin();
  //
  // int deltaVersion = delta.getVersion();
  // int newVersion = CDOBranchVersion.FIRST_VERSION;
  //
  // ObjyObject objyObject = getObject(delta.getID());
  // if (TRACER_DEBUG.isEnabled())
  // {
  // TRACER_DEBUG.format("writingRevisionDelta getting Object: {0}, v:{1} - BranchId:{2}", objyObject.ooId()
  // .getStoreString(), deltaVersion, delta.getBranch().getID());
  // }
  // ObjyObject objyOriginalRevision = objyObject.getRevisionByVersion(deltaVersion, delta.getBranch().getID(),
  // objySession.getObjectManager());
  //
  // if (branch.getID() == delta.getBranch().getID())
  // {
  // // Same branch, increase version
  // newVersion = deltaVersion + 1;
  // }
  //
  // if (TRACER_DEBUG.isEnabled())
  // {
  // TRACER_DEBUG.format("Writing revision delta: {0}, v:{1} - OID:{2}, v:{3} - BranchId:{4}", delta, deltaVersion,
  // objyObject.ooId().getStoreString(), objyOriginalRevision.getVersion(), objyOriginalRevision.getBranchId());
  // TRACER_DEBUG.format("... delta branch ID: {0} - revision branch ID: {1}", branch.getID(),
  // objyOriginalRevision.getBranchId());
  // }
  // // System.out.println(">>>IS: Delta Writing: " + delta.getID() + " - oid: " + objyObject.ooId().getStoreString());
  // // System.out.println("\t - old version : " + delta.getVersion());
  // // System.out.println("\t - created     : " + created);
  // // System.out.println("\t - delta.branch: " + delta.getBranch().toString());
  // // System.out.println("\t - branch      : " + branch.toString());
  // // System.out.println("\t - branch TS   : " + branch.getPoint(created).getTimeStamp());
  // // System.out.println("\t - delta       : " + delta.toString());
  // // for debugging...
  //
  // if (objyOriginalRevision.getVersion() != deltaVersion)
  // {
  // throw new RuntimeException("ObjecitivityStoreAccessor : Dirty write");
  // }
  //
  // ObjyObject objyNewRevision = null;
  //
  // if (getStore().isRequiredToSupportAudits())
  // {
  // // newObjyRevision = objySession.getObjectManager().copyRevision(this, objyRevision);
  // // objyRevision.setRevisedBranchId(branch.getID();
  // // InternalCDORevision originalRevision = getStore().getRepository().getRevisionManager()
  // // .getRevisionByVersion(delta.getID(), delta, 0, true);
  // InternalCDORevision originalRevision = getStore().getRepository().getRevisionManager()
  // .getRevisionByVersion(delta.getID(), delta.getBranch().getVersion(deltaVersion), 0, true);
  //
  // // 100917-IS: KISS - InternalCDORevision newRevision = originalRevision.copy();
  //
  // // 100917-IS: KISS - newRevision.setVersion(deltaVersion + 1);
  // // 100917-IS: KISS - newRevision.setBranchPoint(delta.getBranch().getPoint(created));
  // // 100917-IS: KISS - newObjyRevision = objySession.getObjectManager().newObject(newRevision.getEClass(),
  // // objyRevision.ooId());
  // // 100917-IS: KISS - objyNewRevision.update(this, newRevision);
  //
  // // create a new object, fill it with the original revision data, then
  // // modify the creation and the branch ID accordingly.
  // objyNewRevision = objySession.getObjectManager().newObject(originalRevision.getEClass(),
  // objyOriginalRevision.ooId());
  // objyNewRevision.update(this, originalRevision);
  // objyNewRevision.setBranchId(delta.getBranch().getID());
  // // the following are done at the end.
  // // objyNewRevision.setVersion(deltaVersion + 1);
  // // objyNewRevision.setCreationTime(created);
  //
  // objyObject.addToRevisions(objyNewRevision);
  //
  // if (getStore().isRequiredToSupportBranches() /* && branch.getID() != CDOBranch.MAIN_BRANCH_ID */)
  // {
  // // add the newObjyRevision to the proper branch.
  // ObjyBranch objyBranch = objySession.getBranchManager(getRepositoryName()).getBranch(branch.getID());
  // ooObj anObj = ooObj.create_ooObj(objyNewRevision.ooId());
  // objyBranch.addRevision(anObj);
  // }
  // if (newVersion > CDORevision.FIRST_VERSION)
  // {
  // // revise the original revision last, otherwise we can end up with the revised date in the new revision.
  // objyOriginalRevision.setRevisedTime(branch.getPoint(created).getTimeStamp() - 1);
  // }
  // }
  // else
  // {
  // objyNewRevision = objyOriginalRevision;
  // }
  //
  // ObjectivityFeatureDeltaWriter visitor = new ObjectivityFeatureDeltaWriter(objyNewRevision);
  //
  // delta.accept(visitor);
  //
  // objyNewRevision.setCreationTime(branch.getPoint(created).getTimeStamp());
  // objyNewRevision.setVersion(newVersion); // TODO - verify with Eike if this is true!!!
  // }

  /**
   * Called for each revision delta.
   * 
   * @param delta
   * @param created
   * @param branch
   */
  private void writeRevisionDelta(InternalCDORevisionDelta delta, CDOBranch branch, long created)
  {
    // ensureSessionBegin();

    int deltaVersion = delta.getVersion();
    int newVersion = CDOBranchVersion.FIRST_VERSION;

    ObjyObject objyObject = getObject(delta.getID());
    TRACER_DEBUG.format("writingRevisionDelta getting Object: {0}, v:{1} - BranchId:{2}", objyObject.ooId()
        .getStoreString(), deltaVersion, delta.getBranch().getID());
    ObjyObject objyOriginalRevision = objyObject.getRevisionByVersion(deltaVersion, delta.getBranch().getID(),
        objySession.getObjectManager());

    if (branch.getID() == delta.getBranch().getID())
    {
      // Same branch, increase version
      newVersion = deltaVersion + 1;
    }

    if (TRACER_DEBUG.isEnabled())
    {
      TRACER_DEBUG.format("Writing revision delta: {0}, v:{1} - OID:{2}, v:{3} - BranchId:{4}", delta, deltaVersion,
          objyObject.ooId().getStoreString(), objyOriginalRevision.getVersion(), objyOriginalRevision.getBranchId());
      TRACER_DEBUG.format("... delta branch ID: {0} - revision branch ID: {1}", branch.getID(),
          objyOriginalRevision.getBranchId());
    }
    // System.out.println(">>>IS: Delta Writing: " + delta.getID() + " - oid: " + objyObject.ooId().getStoreString());
    // System.out.println("\t - old version : " + delta.getVersion());
    // System.out.println("\t - created     : " + created);
    // System.out.println("\t - delta.branch: " + delta.getBranch().toString());
    // System.out.println("\t - branch      : " + branch.toString());
    // System.out.println("\t - branch TS   : " + branch.getPoint(created).getTimeStamp());
    // System.out.println("\t - delta       : " + delta.toString());
    // for debugging...

    if (objyOriginalRevision.getVersion() != deltaVersion)
    {
      throw new RuntimeException("ObjecitivityStoreAccessor : Dirty write");
    }

    ObjyObject objyNewRevision = null;

    if (getStore().isRequiredToSupportAudits())
    {

      ObjyObject objyObjectCopy = objySession.getObjectManager().copyRevision(this, objyOriginalRevision);
      TRACER_DEBUG.format("  created new object:{0} by copying object:{1} - v:{2} - newBranch:{3}", objyObjectCopy
          .ooId().getStoreString(), objyOriginalRevision.ooId().getStoreString(), objyOriginalRevision.getVersion(),
          branch.getID());

      // // newObjyRevision = objySession.getObjectManager().copyRevision(this, objyRevision);
      // // objyRevision.setRevisedBranchId(branch.getID();
      // InternalCDORevision originalRevision = getStore().getRepository().getRevisionManager()
      // .getRevisionByVersion(delta.getID(), delta, CDORevision.UNCHUNKED, true);
      //
      // // 100917-IS: KISS - InternalCDORevision newRevision = originalRevision.copy();
      //
      // // 100917-IS: KISS - newRevision.setVersion(deltaVersion + 1);
      // // 100917-IS: KISS - newRevision.setBranchPoint(delta.getBranch().getPoint(created));
      // // 100917-IS: KISS - objyNewRevision = objySession.getObjectManager().newObject(newRevision.getEClass(),
      // // objyOriginalRevision.ooId());
      // // 100917-IS: KISS - objyNewRevision.update(this, newRevision);
      //
      // // create a new object, fill it with the original revision data, then
      // // modify the creation and the branch ID accordingly.
      // objyNewRevision = objySession.getObjectManager().newObject(originalRevision.getEClass(),
      // objyOriginalRevision.ooId());
      // objyNewRevision.update(this, originalRevision);
      objyNewRevision = objyObjectCopy;
      objyNewRevision.setBranchId(branch.getID());
      // the following are done at the end.
      // objyNewRevision.setVersion(deltaVersion + 1);
      // objyNewRevision.setCreationTime(created);

      objyObject.addToRevisions(objyNewRevision);

      if (getStore().isRequiredToSupportBranches() /* && branch.getID() != CDOBranch.MAIN_BRANCH_ID */)
      {
        // add the newObjyRevision to the proper branch.
        ObjyBranch objyBranch = objySession.getBranchManager(getRepositoryName()).getBranch(branch.getID());
        ooObj anObj = ooObj.create_ooObj(objyNewRevision.ooId());
        objyBranch.addRevision(anObj);
      }
      // revise the original revision last, otherwise we can end up with the revised date in the new revision.
      // IS: it seems that in CDO 4.0 we don't need to do that anymore!!
      if (newVersion > CDORevision.FIRST_VERSION)
      {
        objyOriginalRevision.setRevisedTime(branch.getPoint(created).getTimeStamp() - 1);
      }
    }
    else
    {
      objyNewRevision = objyOriginalRevision;
    }

    ObjectivityFeatureDeltaWriter visitor = new ObjectivityFeatureDeltaWriter(objyNewRevision);

    delta.accept(visitor);

    objyNewRevision.setCreationTime(branch.getPoint(created).getTimeStamp());
    objyNewRevision.setVersion(newVersion); // TODO - verify with Eike if this is true!!!
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
    if (TRACER_DEBUG.isEnabled())
    {
      ObjyObjectManager.getObjectTime = 0;
      ObjyObjectManager.updateObjectTime = 0;
      ObjyObjectManager.resourceCheckAndUpdateTime = 0;
    }
    long start = System.nanoTime();
    ensureSessionBegin();
    // objySession.addToLog("IS:>>>", "writeRevisions - begin");
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
      // long tStart = System.currentTimeMillis();
      for (InternalCDORevision revision : revisions)
      {
        writeRevision(revision, monitor.fork());
      }
      // long tDiff = System.currentTimeMillis() - tStart;
      // System.out.println(">>> IS: writing revisions: " + revisions.length + " - time: " + tDiff);
    }
    finally
    {
      newObjyObjectsMap.clear();
      monitor.done();
    }
    // objySession.addToLog("IS:>>>", "writeRevisions - end");
    if (TRACER_DEBUG.isEnabled())
    {
      TRACER_DEBUG
          .trace(" writeRevisions: " + revisions.length + " - time: " + (System.nanoTime() - start) / 1000000.0);
      // TRACER_DEBUG.trace("   getObjectTime: " + ObjyObjectManager.getObjectTime / 1000000.0);
      // TRACER_DEBUG.trace("   updateObjectTime: " + ObjyObjectManager.updateObjectTime / 1000000.0);
      // TRACER_DEBUG.trace("   resourceCheckAndUpdateTime: " + ObjyObjectManager.resourceCheckAndUpdateTime /
      // 1000000.0);
      // ObjyObjectManager.getObjectTime = 0;
      // ObjyObjectManager.updateObjectTime = 0;
      // ObjyObjectManager.resourceCheckAndUpdateTime = 0;
    }

  }

  /**
   * return an ObjyObject that represent the CDOID base.
   */
  public ObjyObject getObject(CDOID id)
  {
    ObjyObject objyObject = objySession.getObjectManager().getObject(id);
    // make sure we get the base one and not any cached version.
    return objyObject.getBaseObject();
  }

  /*****
   * Use this code for heart beat. Async async = null; try { monitor.begin(getListMappings().size() + 1); async =
   * monitor.forkAsync(); reviseObject(accessor, id, timeStamp); } finally { async.stop(); monitor.done(); } [10:07:02
   * AM] Eike Stepper: that one takes care that the heartbeat goes on [10:07:13 AM] Eike Stepper: for indefinite time
   */

  private void writeRevision(InternalCDORevision revision, OMMonitor monitor)
  {
    Async async = null;

    try
    {
      monitor.begin(1000); // IS: experimental.
      async = monitor.forkAsync();

      // EClass eClass = revision.getEClass();
      long __start = System.nanoTime();
      // ObjyObject objyObject = getObject(revision.getID());
      ObjyObject objyObject = newObjyObjectsMap.get(revision.getID());
      if (objyObject == null)
      {
        objyObject = getObject(revision.getID()); // we shouldn't need to come here.
      }
      ObjyObjectManager.getObjectTime += System.nanoTime() - __start;

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
        // TRACER_DEBUG.format("...Updating other revisions using writeRevision()...");
        ObjyObject oldObjyRevision = objyObject.getRevisionByVersion(revision.getVersion() - 1, revision.getBranch()
            .getID(), objySession.getObjectManager());

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
        ObjyBranch objyBranch = objySession.getBranchManager(getRepositoryName()).getBranch(
            revision.getBranch().getID());
        ooObj anObj = ooObj.create_ooObj(newObjyRevision.ooId());
        try
        {
          objyBranch.addRevision(anObj);
        }
        catch (LockNotGrantedException ex)
        {
          ex.printStackTrace();
        }
      }

      __start = System.nanoTime();
      newObjyRevision.update(this, revision);
      ObjyObjectManager.updateObjectTime += System.nanoTime() - __start;

      // if it's a resource, collect it.
      if (revision.isResourceNode())
      {
        __start = System.nanoTime();
        // TODO - this is temp solution to lock the common resource list
        // Add resource to the list
        ObjyResourceList resourceList = objySession.getResourceList(getRepositoryName());
        ooObj anObj = (ooObj)objySession.getFD().objectFrom(resourceList.ooId());
        SmartLock.lock(anObj.getContainer());

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
        try
        {
          resourceList.add(newObjyRevision);
        }
        catch (LockNotGrantedException ex)
        {
          ex.printStackTrace();
        }
        ObjyObjectManager.resourceCheckAndUpdateTime += System.nanoTime() - __start;
      }
    }
    finally
    {
      async.stop();
      monitor.done();
    }
  }

  @Override
  protected void doCommit(OMMonitor monitor)
  {
    // System.out.println(">>>>IS:<<< doCommit() " + this + " - objy session: " + objySession.toString());

    long start = System.currentTimeMillis();
    Async async = null;
    monitor.begin();

    try
    {
      try
      {
        async = monitor.forkAsync();
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
      finally
      {
        if (async != null)
        {
          async.stop();
        }
      }
    }
    catch (RuntimeException exception)
    {
      TRACER_INFO.trace(exception.getMessage(), exception);
      exception.printStackTrace();
      throw exception;
    }
    finally
    {
      monitor.done();
    }

    // 100920 - IS: for debugging
    if (TRACER_DEBUG.isEnabled())
    {
      // TRACER_DEBUG.trace("doCommit() - new objects created: " + ObjyObjectManager.newObjCount + " - Internal: "
      // + ObjyObjectManager.newInternalObjCount);
      // ObjyObjectManager.newObjCount = 0;
      // ObjyObjectManager.newInternalObjCount = 0;
      TRACER_DEBUG.trace(" readRvisions: " + readRevisionCount + " - time: " + readRevisionTime / 1000000.0);
      TRACER_DEBUG.trace(" fetchCount: " + ObjyObject.fetchCount + " - updateCount: " + ObjyObject.updateCount);
      TRACER_DEBUG.trace("\t commit time: " + (System.currentTimeMillis() - start));
      readRevisionTime = 0;
      readRevisionCount = 0;
      ObjyObject.fetchCount = 0;
      ObjyObject.updateCount = 0;
    }
    // System.out.println("IS:>>> ObjyObject.ctor(): count " + ObjyObject.count + " - totalTime: " + ObjyObject.tDiff);
    // for (ooId oid : ObjyObject.oids)
    // {
    // System.out.println("<" + oid.getStoreString() + "> ");
    // }
    // System.out.println();
    // ObjyObject.count = 0;
    // ObjyObject.tDiff = 0;
    // ObjyObject.oids.clear();
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

    if (TRACER_DEBUG.isEnabled())
    {
      TRACER_DEBUG.format("queryResources() for : " + (pathPrefix == null ? "NULL" : pathPrefix) + " - exactMatch: "
          + exactMatch);
    }
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
          try
          {
            resourceRevision = resource.getRevision(context.getTimeStamp(), context.getBranch().getID(),
                objySession.getObjectManager());
          }
          catch (RuntimeException ex)
          {
            ex.printStackTrace();
          }

        }
        else if (getStore().isRequiredToSupportAudits())
        {
          try
          {
            resourceRevision = resource.getRevision(context.getTimeStamp(), CDOBranch.MAIN_BRANCH_ID,
                objySession.getObjectManager());
          }
          catch (RuntimeException ex)
          {
            ex.printStackTrace();
          }
        }

        if (resourceRevision == null || resourceRevision.getVersion() < 0)
        {
          continue;
        }

        String resourceName = ObjyResourceList.getResourceName(resourceRevision);
        CDOID resourceID = OBJYCDOIDUtil.getCDOID(resource.ooId());
        if (exactMatch && pathPrefix != null && pathPrefix.equals(resourceName) || pathPrefix == null
            && resourceName == null)
        {
          if (!context.addResource(resourceID))
          {
            if (TRACER_DEBUG.isEnabled())
            {
              TRACER_DEBUG.format("   queryResources(1.1) got: " + resource.ooId().getStoreString() + " - version: "
                  + resource.getVersion());
            }
            // No more results allowed
            break; // don't continue
          }
        }
        else if (!exactMatch && resourceName != null)
        {
          if (resourceName.startsWith(pathPrefix))
          {
            context.addResource(resourceID);
            if (TRACER_DEBUG.isEnabled())
            {
              TRACER_DEBUG.format("   queryResources(1.2) got: " + resource.ooId().getStoreString() + " - version: "
                  + resource.getVersion());
            }
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
    ensureSessionBegin();

    Set<CDOID> targetIDs = context.getTargetObjects().keySet();
    Map<EClass, List<EReference>> sourceCandidates = context.getSourceCandidates();

    // get the context branch.
    CDOBranch branch = context.getBranch();
    ObjyBranchManager branchManager = objySession.getBranchManager(getRepositoryName());
    ObjyBranch objyBranch = branchManager.getBranch(branch.getID());

    // iterate over all revision in a branch.
    ObjyObject objyObject = null;
    SortedSet<?> revisions = objyBranch.getRevisions();
    for (Object anObj : revisions)
    {
      // the ooObj we get from revisions is the correct one for that branch.
      objyObject = objySession.getObjectManager().getObject(((ooObj)anObj).getOid());

      // InternalCDORevision revision = getRevision(list, context);
      // if (revision == null || revision instanceof SyntheticCDORevision)
      // {
      // continue;
      // }
      //
      EClass eClass = ObjySchema.getEClass(getStore(), objyObject.objyClass());
      CDOID sourceID = objyObject.getRevisionId();

      List<EReference> eReferences = sourceCandidates.get(eClass);
      if (eReferences != null)
      {
        for (EReference eReference : eReferences)
        {
          if (eReference.isMany())
          {
            List<Object> results = objyObject.fetchList(this, eReference, 0, CDORevision.UNCHUNKED);
            if (results != null)
            {
              try
              {
                int index = 0;
                for (Object id : results)
                {
                  if (!queryXRefs(context, targetIDs, (CDOID)id, sourceID, eReference, index++))
                  {
                    return;
                  }
                }
              }
              catch (Exception ex)
              {
                ex.printStackTrace();
              }
            }
          }
          else
          {
            Object value = objyObject.get(eReference);
            CDOID id = (CDOID)value;
            if (!queryXRefs(context, targetIDs, id, sourceID, eReference, 0))
            {
              return;
            }
          }
        }
      }
    }
  }

  private boolean queryXRefs(QueryXRefsContext context, Set<CDOID> targetIDs, CDOID targetID, CDOID sourceID,
      EReference sourceReference, int index)
  {
    for (CDOID id : targetIDs)
    {
      if (id.equals(targetID))
      {
        if (!context.addXRef(targetID, sourceID, sourceReference, index))
        {
          // No more results allowed
          return false;
        }
      }
    }

    return true;
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
    long __start = System.nanoTime();
    ensureSessionBegin();

    if (TRACER_DEBUG.isEnabled())
    {
      TRACER_DEBUG.format("RR - Read rev for: {0}, TS:{1}", id, branchPoint.getTimeStamp()); //$NON-NLS-1$
    }

    // we shouldn't be doing this!!!
    if (id instanceof CDOIDExternal)
    {
      // 100917 - IS: This must be a bug in CDO, it's throwing a CDOIDExternal at us
      // we'll return null.
      // TRACER_DEBUG.format("objy can't read revision for CDOID: {0}, it's external.", id.toString());
      TRACER_DEBUG.trace("objy can't read revision for external CDOID: " + id.toString());
      return null;
    }

    // we might have a proxy object!!!!

    ObjyObject objyObject = getObject(id);

    if (objyObject == null)
    {
      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.format("RR - objyObject is NULL 4 ID:" + id); //$NON-NLS-1$
      }
      return null;
    }
    // else
    // {
    //      TRACER_DEBUG.format("RR - objyObject is ID:" + id + ", :" + objyObject.ooId().getStoreString()); //$NON-NLS-1$
    // }

    InternalCDORevision revision = createRevision(objyObject, id);
    revision.setBranchPoint(branchPoint);

    ObjyObject objyRevision = objyObject;

    if (getStore().isRequiredToSupportBranches())
    {
      try
      {
        objyRevision = objyObject.getRevision(branchPoint.getTimeStamp(), branchPoint.getBranch().getID(),
            objySession.getObjectManager());
        if (objyRevision == null)
        {
          TRACER_DEBUG
              .format(
                  "RR - branches ID:{0}, OB:{1}, BPB:{2}, BPTS:{3}", id, objyObject.getBranchId(), branchPoint.getBranch().getID(), branchPoint.getTimeStamp()); //$NON-NLS-1$
        }
      }
      catch (RuntimeException ex)
      {
        ex.printStackTrace();
      }
    }
    else if (getStore().isRequiredToSupportAudits())
    {
      try
      {
        objyRevision = objyObject.getRevision(branchPoint.getTimeStamp(), CDOBranch.MAIN_BRANCH_ID,
            objySession.getObjectManager());
        if (objyRevision == null)
        {
          TRACER_DEBUG
              .format(
                  "RR - audit ID:{0}, OB:{1}, BPB:{2}, BPTS:{3}", id, objyObject.getBranchId(), branchPoint.getBranch().getID(), branchPoint.getTimeStamp()); //$NON-NLS-1$
        }
      }
      catch (RuntimeException ex)
      {
        ex.printStackTrace();
      }

    }

    if (objyRevision == null)
    {
      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.format("RR - objyRevision is NULL 4 ID:" + id); //$NON-NLS-1$
      }
      return null;
    }

    // check the version
    if (objyRevision.getVersion() < 0)
    {
      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG
            .format("RR - ...revision for: {0} - OID: {1} is detached.", id, objyObject.ooId().getStoreString()); //$NON-NLS-1$
      }
      EClass eClass = ObjySchema.getEClass(getStore(), objyObject.objyClass());
      return new DetachedCDORevision(eClass, id, branchPoint.getBranch(), -objyRevision.getVersion(),
          objyRevision.getCreationTime());
    }

    CDOBranchPoint branchPoint2 = revision.getBranch().getPoint(objyRevision.getCreationTime());
    revision.setBranchPoint(branchPoint2);

    if (TRACER_DEBUG.isEnabled())
    {
      TRACER_DEBUG
          .format("RR - Fetching revision details for: {0} - OID:{1}", id, objyRevision.ooId().getStoreString()); //$NON-NLS-1$
    }

    boolean ok = objyRevision.fetch(this, revision, listChunk);

    if (!ok)
    {
      TRACER_DEBUG.format("RR - Fetch rev failed 4: {0}, :{1}", id, objyRevision.ooId().getStoreString()); //$NON-NLS-1$
    }

    readRevisionTime += System.nanoTime() - __start;
    readRevisionCount++;

    return ok ? revision : null;
  }

  /**
	 *
	 */
  public InternalCDORevision readRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, int listChunk,
      CDORevisionCacheAdder cache)
  {
    long __start = System.nanoTime();
    ensureSessionBegin();

    if (TRACER_DEBUG.isEnabled())
    {
      TRACER_DEBUG.format("RRBV - Read rev 4: {0}, ver: {1}", id, branchVersion.getVersion()); //$NON-NLS-1$
    }

    // we might have a proxy object!!!!

    ObjyObject objyObject = getObject(id);
    if (objyObject == null)
    {
      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.format("RRBV - objyObject is NULL for ID: " + id); //$NON-NLS-1$
      }
      return null;
    }

    // {
    //      TRACER_DEBUG.format("RRBV - objyObject 4 ID: " + id + ", :" + objyObject.ooId().getStoreString()); //$NON-NLS-1$
    // }

    ObjyObject objyRevision = null;
    objyRevision = objyObject.getRevisionByVersion(branchVersion.getVersion(), branchVersion.getBranch().getID(),
        objySession.getObjectManager());
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
        TRACER_DEBUG.format("RRBV - objyRevision is NULL for ID: ", id); //$NON-NLS-1$
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

    // if (!ok)
    // {
    //      TRACER_DEBUG.format("RRBV - Fetch rev failed 4: {0}, :", id, objyRevision.ooId().getStoreString()); //$NON-NLS-1$
    // }

    readRevisionTime += System.nanoTime() - __start;
    readRevisionCount++;

    return ok ? revision : null;
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
  public void addIDMappings(InternalCommitContext commitContext, OMMonitor monitor)
  {
    long __start = System.nanoTime();
    ensureSessionBegin();
    if (commitContext.getNewObjects().length > 0)
    {
      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.trace("creating " + commitContext.getNewObjects().length + " new objects and assigning new IDs");
      }
      InternalCDORevision[] newObjects = commitContext.getNewObjects();
      try
      {
        monitor.begin(newObjects.length);

        ObjyPlacementManagerLocal placementManager = new ObjyPlacementManagerLocal(getStore(), objySession,
            commitContext, newObjyObjectsMap);

        // iterate over the list and skip the ones we already have created.
        for (InternalCDORevision revision : newObjects)
        {
          try
          {
            placementManager.processRevision(revision);
          }
          catch (com.objy.db.ObjyRuntimeException ex)
          {
            System.out.println("IS: Exception, Session: " + objySession + " open status: " + objySession.isOpen());
            ex.printStackTrace();
          }
          monitor.worked();
        }
      }
      finally
      {
        monitor.done();
      }
    }
    if (TRACER_DEBUG.isEnabled())
    {
      TRACER_DEBUG.trace(" addIDMappings for " + commitContext.getNewObjects().length + " - time: "
          + (System.nanoTime() - __start) / 1000000.0);
      TRACER_DEBUG.trace("  createObjects : " + ObjyObject.createObjectCount + " - time: "
          + ObjyObject.createObjectTime / 1000000.0);
      ObjyObject.createObjectTime = 0;
      ObjyObject.createObjectCount = 0;
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
  public void ensureActiveSession()
  {
    ensureSessionBegin();
  }

  protected void ensureSessionBegin()
  {
    getObjySession();
    if (!objySession.isJoined())
    {
      objySession.join();
    }
    if (!objySession.isOpen())
    {
      if (isRead)
      {
        objySession.setOpenMode(oo.openReadOnly);
      }
      else
      {
        objySession.setOpenMode(oo.openReadWrite);
      }
      objySession.begin();
      if (TRACER_DEBUG.isEnabled())
      {
        TRACER_DEBUG.trace(" ensureBeginSession() called begin() on session: " + objySession + " [name: "
            + objySession.getName() + " - open: " + objySession.isOpen() + "]");
      }
    }
  }

  private void ensureSessionJoin()
  {
    // we better have a session for this store.
    assert objySession != null;
    // testSwitchViewTarget() is crashing because objySession is null.
    // TBD: verify this case!!!
    if (objySession != null && !objySession.isJoined())
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
    try
    {
      ObjyCommitInfoHandler commitInfoHandler = getStore().getCommitInfoHandler();
      commitInfoHandler.writeCommitInfo(branch.getID(), timeStamp, previousTimeStamp, userID, comment);
    }
    catch (com.objy.db.ObjyRuntimeException ex)
    {
      ex.printStackTrace();
    }
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
    boolean convertToUpdate = false;
    // IS: this is a hack to overcome the issue in cdo core where the Accessor is requested for
    // read but it's trying to create stuff.
    if (isRead)
    {
      TRACER_DEBUG.trace("-->> createBranch() - Hack... Hack... changing read to update.");
      // upgrade the session to update.
      objySession.commit();
      objySession.setOpenMode(oo.openReadWrite);
      convertToUpdate = true;
      objySession.begin();
    }
    Pair<Integer, Long> retValue = objySession.getBranchManager(getRepositoryName()).createBranch(branchID, branchInfo);
    if (convertToUpdate)
    {
      // return the session to read.
      objySession.commit();
      objySession.setOpenMode(oo.openReadOnly);
      objySession.begin();
    }
    return retValue;
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
    ensureSessionBegin();
    IRepository repository = getStore().getRepository();
    CDORevisionManager revisionManager = repository.getRevisionManager();
    CDOBranchManager branchManager = repository.getBranchManager();

    // scan FD for ObjyBase which is the base class for all revisions
    Iterator<?> itr = objySession.getFD().scan(ObjyBase.CLASS_NAME);
    ObjyObject objyObject = null;
    while (itr.hasNext())
    {
      objyObject = objySession.getObjectManager().getObject(((ooObj)itr).getOid());
      if (!handleRevision(objyObject, eClass, branch, timeStamp, exactTime, handler, revisionManager, branchManager))
      {
        return;
      }
    }
    // TODO: implement ObjectivityStoreAccessor.handleRevisions(eClass, branch, timeStamp, exactTime, handler)
    throw new UnsupportedOperationException();
  }

  private boolean handleRevision(ObjyObject objyObject, EClass eClass, CDOBranch branch, long timeStamp,
      boolean exactTime, CDORevisionHandler handler, CDORevisionManager revisionManager, CDOBranchManager branchManager)
  {
    if (objyObject.getVersion() < 0) // DetachedCDORevision
    {
      return true;
    }

    if (eClass != null && ObjySchema.getEClass(getStore(), objyObject.objyClass()) != eClass)
    {
      return true;
    }

    if (branch != null && objyObject.getBranchId() != branch.getID())
    {
      return true;
    }

    if (timeStamp != CDOBranchPoint.INVALID_DATE)
    {
      if (exactTime)
      {
        if (timeStamp != CDOBranchPoint.UNSPECIFIED_DATE && objyObject.getCreationTime() != timeStamp)
        {
          return true;
        }
      }
      else
      {
        long startTime = objyObject.getCreationTime();
        long endTime = objyObject.getRevisedTime();
        if (!CDOCommonUtil.isValidTimeStamp(timeStamp, startTime, endTime))
        {
          return true;
        }
      }
    }

    CDOBranchVersion branchVersion = branchManager.getBranch((int)objyObject.getBranchId()).getVersion(
        Math.abs(objyObject.getVersion()));
    InternalCDORevision revision = (InternalCDORevision)revisionManager.getRevisionByVersion(
        OBJYCDOIDUtil.getCDOID(objyObject.ooId()), branchVersion, CDORevision.UNCHUNKED, true);

    return handler.handleRevision(revision);
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
    Async async = null;
    try
    {
      async = monitor.forkAsync();

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
      async.stop();
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

  public void rawExport(CDODataOutput out, int fromBranchID, int toBranchID, long fromCommitTime, long toCommitTime)
      throws IOException
  {
    // TODO: implement ObjectivityStoreAccessor.rawExport();
    throw new UnsupportedOperationException();
  }

  public void rawImport(CDODataInput in, int fromBranchID, int toBranchID, long fromCommitTime, long toCommitTime,
      OMMonitor monitor) throws IOException
  {
    // TODO: implement ObjectivityStoreAccessor.rawImport
    throw new UnsupportedOperationException();
  }

  public void rawStore(InternalCDOPackageUnit[] packageUnits, OMMonitor monitor)
  {
    // TODO: implement ObjectivityStoreAccessor.rawStore
    throw new UnsupportedOperationException();
  }

  public void rawStore(InternalCDORevision revision, OMMonitor monitor)
  {
    // TODO: implement ObjectivityStoreAccessor.rawStore
    throw new UnsupportedOperationException();
  }

  public void rawStore(byte[] id, long size, InputStream inputStream) throws IOException
  {
    // TODO: implement ObjectivityStoreAccessor.rawStore
    throw new UnsupportedOperationException();
  }

  public void rawStore(byte[] id, long size, Reader reader) throws IOException
  {
    // TODO: implement ObjectivityStoreAccessor.rawStore
    throw new UnsupportedOperationException();
  }

  public void rawStore(CDOBranch branch, long timeStamp, long previousTimeStamp, String userID, String comment,
      OMMonitor monitor)
  {
    // TODO: implement ObjectivityStoreAccessor.rawStore
    throw new UnsupportedOperationException();
  }

  public void rawDelete(CDOID id, int version, CDOBranch branch, EClass eClass, OMMonitor monitor)
  {
    // TODO: implement ObjectivityStoreAccessor.rawDelete
    throw new UnsupportedOperationException();
  }

  public void rawCommit(double commitWork, OMMonitor monitor)
  {
    // TODO: implement ObjectivityStoreAccessor.rawCommit
    throw new UnsupportedOperationException();
  }

  public LockArea createLockArea(String userID, CDOBranchPoint branchPoint, boolean readOnly,
      Map<CDOID, LockGrade> locks)
  {
    // TODO: implement ObjectivityStoreAccessor.createLockArea
    throw new UnsupportedOperationException();
  }

  public LockArea getLockArea(String durableLockingID) throws LockAreaNotFoundException
  {
    // TODO: implement ObjectivityStoreAccessor.getLockArea
    throw new UnsupportedOperationException();
  }

  public void getLockAreas(String userIDPrefix, Handler handler)
  {
    ensureSessionBegin();

    InternalCDOBranchManager branchManager = getStore().getRepository().getBranchManager();
    ObjyLockAreaManager objyLockAreaManager = objySession.getLockAreaManager(getRepositoryName());
    objyLockAreaManager.getLockAreas(branchManager, userIDPrefix, handler);
  }

  public void deleteLockArea(String durableLockingID)
  {
    // TODO: implement ObjectivityStoreAccessor.deleteLockArea
    throw new UnsupportedOperationException();
  }

  public void lock(String durableLockingID, LockType type, Collection<? extends Object> objectsToLock)
  {
    // TODO: implement ObjectivityStoreAccessor.lock
    throw new UnsupportedOperationException();
  }

  public void unlock(String durableLockingID, LockType type, Collection<? extends Object> objectsToUnlock)
  {
    // TODO: implement ObjectivityStoreAccessor.unlock
    throw new UnsupportedOperationException();
  }

  public void unlock(String durableLockingID)
  {
    // TODO: implement ObjectivityStoreAccessor.unlock
    throw new UnsupportedOperationException();
  }
}
