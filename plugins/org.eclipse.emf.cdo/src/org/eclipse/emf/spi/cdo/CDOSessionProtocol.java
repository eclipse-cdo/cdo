/***************************************************************************
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.protocol.CDOProtocol;
import org.eclipse.emf.cdo.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.session.remote.CDORemoteSession;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionMessage;
import org.eclipse.emf.cdo.spi.common.CDOCloningContext;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager.CommitInfoLoader;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry.PackageLoader;
import org.eclipse.emf.cdo.spi.common.revision.CDOIDMapper;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager.RevisionLoader;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction.InternalCDOCommitContext;
import org.eclipse.emf.spi.cdo.InternalCDOXATransaction.InternalCDOXACommitContext;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public interface CDOSessionProtocol extends CDOProtocol, PackageLoader, BranchLoader, RevisionLoader, CommitInfoLoader
{
  public RepositoryTimeResult getRepositoryTime();

  public void disablePassiveUpdates();

  public RefreshSessionResult refresh(long lastUpdateTime,
      Map<CDOBranch, Map<CDOID, InternalCDORevision>> viewedRevisions, int initialChunkSize,
      boolean enablePassiveUpdates);

  /**
   * @param accessIndex
   *          Index of the item access at the client (with modifications)
   * @param fetchIndex
   *          Index of the item access at the server (without any modifications)
   * @param fromIndex
   *          Load objects at the client from fromIndex (inclusive)
   * @param toIndex
   *          Load objects at the client to toIndex (inclusive)
   */
  public Object loadChunk(InternalCDORevision revision, EStructuralFeature feature, int accessIndex, int fetchIndex,
      int fromIndex, int toIndex);

  /**
   * @since 3.0
   */
  public void openView(int viewID, CDOBranchPoint branchPoint, boolean readOnly);

  /**
   * @since 3.0
   */
  public boolean[] changeView(int viewID, CDOBranchPoint branchPoint, List<InternalCDOObject> invalidObjects);

  public void closeView(int viewID);

  public void changeSubscription(int viewId, List<CDOID> cdoIDs, boolean subscribeMode, boolean clear);

  /**
   * @since 3.0
   */
  public void query(int viewID, AbstractQueryIterator<?> queryResult);

  public boolean cancelQuery(int queryId);

  /**
   * @since 3.0
   */
  public void lockObjects(long lastUpdateTime, Map<CDOBranch, Map<CDOID, InternalCDORevision>> viewedRevisions,
      int viewID, LockType lockType, long timeout) throws InterruptedException;

  /**
   * @since 3.0
   */
  public void unlockObjects(CDOView view, Collection<? extends CDOObject> objects, LockType lockType);

  /**
   * @since 3.0
   */
  public boolean isObjectLocked(CDOView view, CDOObject object, LockType lockType, boolean byOthers);

  public CommitTransactionResult commitTransaction(InternalCDOCommitContext commitContext, OMMonitor monitor);

  public CommitTransactionResult commitTransactionPhase1(InternalCDOXACommitContext xaContext, OMMonitor monitor);

  public CommitTransactionResult commitTransactionPhase2(InternalCDOXACommitContext xaContext, OMMonitor monitor);

  public CommitTransactionResult commitTransactionPhase3(InternalCDOXACommitContext xaContext, OMMonitor monitor);

  public CommitTransactionResult commitTransactionCancel(InternalCDOXACommitContext xaContext, OMMonitor monitor);

  public List<CDORemoteSession> getRemoteSessions(InternalCDORemoteSessionManager manager, boolean subscribe);

  /**
   * @since 3.0
   */
  public Set<Integer> sendRemoteMessage(CDORemoteSessionMessage message, List<CDORemoteSession> recipients);

  /**
   * @since 3.0
   */
  public boolean unsubscribeRemoteSessions();

  /**
   * @since 3.0
   */
  public void cloneRepository(CDOCloningContext context);

  /**
   * @author Eike Stepper
   */
  public final class OpenSessionResult
  {
    private int sessionID;

    private String repositoryUUID;

    private long repositoryCreationTime;

    private long lastUpdateTime;

    private RepositoryTimeResult repositoryTimeResult;

    private boolean repositorySupportingAudits;

    private boolean repositorySupportingBranches;

    private List<InternalCDOPackageUnit> packageUnits = new ArrayList<InternalCDOPackageUnit>();

    /**
     * @since 3.0
     */
    public OpenSessionResult(int sessionID, String repositoryUUID, long repositoryCreationTime, long lastUpdateTime,
        boolean repositorySupportingAudits, boolean repositorySupportingBranches)
    {
      this.sessionID = sessionID;
      this.repositoryUUID = repositoryUUID;
      this.repositoryCreationTime = repositoryCreationTime;
      this.lastUpdateTime = lastUpdateTime;
      this.repositorySupportingAudits = repositorySupportingAudits;
      this.repositorySupportingBranches = repositorySupportingBranches;
    }

    public int getSessionID()
    {
      return sessionID;
    }

    public String getRepositoryUUID()
    {
      return repositoryUUID;
    }

    public long getRepositoryCreationTime()
    {
      return repositoryCreationTime;
    }

    public boolean isRepositorySupportingAudits()
    {
      return repositorySupportingAudits;
    }

    /**
     * @since 3.0
     */
    public boolean isRepositorySupportingBranches()
    {
      return repositorySupportingBranches;
    }

    public RepositoryTimeResult getRepositoryTimeResult()
    {
      return repositoryTimeResult;
    }

    public void setRepositoryTimeResult(RepositoryTimeResult repositoryTimeResult)
    {
      this.repositoryTimeResult = repositoryTimeResult;
    }

    /**
     * @since 3.0
     */
    public long getLastUpdateTime()
    {
      return lastUpdateTime;
    }

    public List<InternalCDOPackageUnit> getPackageUnits()
    {
      return packageUnits;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class RefreshSessionResult
  {
    private long lastUpdateTime;

    private List<CDOPackageUnit> packageUnits = new ArrayList<CDOPackageUnit>();

    private Map<CDOBranch, List<InternalCDORevision>> changedObjects = new HashMap<CDOBranch, List<InternalCDORevision>>();

    private Map<CDOBranch, List<CDOIDAndVersion>> detachedObjects = new HashMap<CDOBranch, List<CDOIDAndVersion>>();

    public RefreshSessionResult(long lastUpdateTime)
    {
      this.lastUpdateTime = lastUpdateTime;
    }

    public long getLastUpdateTime()
    {
      return lastUpdateTime;
    }

    public List<CDOPackageUnit> getPackageUnits()
    {
      return packageUnits;
    }

    public List<InternalCDORevision> getChangedObjects(CDOBranch branch)
    {
      List<InternalCDORevision> list = changedObjects.get(branch);
      if (list == null)
      {
        return Collections.emptyList();
      }

      return list;
    }

    public List<CDOIDAndVersion> getDetachedObjects(CDOBranch branch)
    {
      List<CDOIDAndVersion> list = detachedObjects.get(branch);
      if (list == null)
      {
        return Collections.emptyList();
      }

      return list;
    }

    public void addPackageUnit(CDOPackageUnit packageUnit)
    {
      packageUnits.add(packageUnit);
    }

    public void addChangedObject(InternalCDORevision revision)
    {
      CDOBranch branch = revision.getBranch();
      List<InternalCDORevision> list = changedObjects.get(branch);
      if (list == null)
      {
        list = new ArrayList<InternalCDORevision>();
        changedObjects.put(branch, list);
      }

      list.add(revision);
    }

    public void addDetachedObject(CDORevisionKey revision)
    {
      CDOBranch branch = revision.getBranch();
      List<CDOIDAndVersion> list = detachedObjects.get(branch);
      if (list == null)
      {
        list = new ArrayList<CDOIDAndVersion>();
        detachedObjects.put(branch, list);
      }

      list.add(revision);
    }
  }

  /**
   * @author Eike Stepper
   */
  public final class RepositoryTimeResult
  {
    private long requested;

    private long indicated;

    private long responded;

    private long confirmed;

    public RepositoryTimeResult()
    {
    }

    public long getRequested()
    {
      return requested;
    }

    public void setRequested(long requested)
    {
      this.requested = requested;
    }

    public long getIndicated()
    {
      return indicated;
    }

    public void setIndicated(long indicated)
    {
      this.indicated = indicated;
    }

    public long getResponded()
    {
      return responded;
    }

    public void setResponded(long responded)
    {
      this.responded = responded;
    }

    public long getConfirmed()
    {
      return confirmed;
    }

    public void setConfirmed(long confirmed)
    {
      this.confirmed = confirmed;
    }

    public long getAproximateRepositoryOffset()
    {
      long latency = confirmed - requested >> 1;
      long shift = confirmed - responded;
      return shift - latency;
    }

    public long getAproximateRepositoryTime()
    {
      long offset = getAproximateRepositoryOffset();
      return System.currentTimeMillis() + offset;
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("RepositoryTime[requested={0}, indicated={1}, responded={2}, confirmed={3}]", //$NON-NLS-1$
          CDOCommonUtil.formatTimeStamp(requested), CDOCommonUtil.formatTimeStamp(indicated), CDOCommonUtil
              .formatTimeStamp(responded), CDOCommonUtil.formatTimeStamp(confirmed));
    }
  }

  /**
   * @author Eike Stepper
   */
  public final class CommitTransactionResult
  {
    private String rollbackMessage;

    private long timeStamp;

    private Map<CDOID, CDOID> idMappings = new HashMap<CDOID, CDOID>();

    private CDOReferenceAdjuster referenceAdjuster;

    private InternalCDOCommitContext commitContext;

    public CommitTransactionResult(InternalCDOCommitContext commitContext, String rollbackMessage)
    {
      this.commitContext = commitContext;
      this.rollbackMessage = rollbackMessage;
    }

    public CommitTransactionResult(InternalCDOCommitContext commitContext, long timeStamp)
    {
      this.commitContext = commitContext;
      this.timeStamp = timeStamp;
    }

    public CDOReferenceAdjuster getReferenceAdjuster()
    {
      if (referenceAdjuster == null)
      {
        referenceAdjuster = createReferenceAdjuster();
      }

      return referenceAdjuster;
    }

    public void setReferenceAdjuster(CDOReferenceAdjuster referenceAdjuster)
    {
      this.referenceAdjuster = referenceAdjuster;
    }

    public InternalCDOCommitContext getCommitContext()
    {
      return commitContext;
    }

    public String getRollbackMessage()
    {
      return rollbackMessage;
    }

    public long getTimeStamp()
    {
      return timeStamp;
    }

    public Map<CDOID, CDOID> getIDMappings()
    {
      return idMappings;
    }

    public void addIDMapping(CDOID oldID, CDOID newID)
    {
      idMappings.put(oldID, newID);
    }

    protected PostCommitReferenceAdjuster createReferenceAdjuster()
    {
      return new PostCommitReferenceAdjuster(commitContext.getTransaction(), new CDOIDMapper(idMappings));
    }

    /**
     * @author Simon McDuff
     */
    protected static class PostCommitReferenceAdjuster implements CDOReferenceAdjuster
    {
      private CDOIDProvider idProvider;

      private CDOIDMapper idMapper;

      public PostCommitReferenceAdjuster(CDOIDProvider idProvider, CDOIDMapper idMapper)
      {
        this.idProvider = idProvider;
        this.idMapper = idMapper;
      }

      public Object adjustReference(Object id)
      {
        if (id == null || id == CDOID.NULL)
        {
          return id;
        }

        if (idProvider != null && (id instanceof CDOID || id instanceof InternalEObject))
        {
          id = idProvider.provideCDOID(id);
        }

        return idMapper.adjustReference(id);
      }
    }
  }
}
