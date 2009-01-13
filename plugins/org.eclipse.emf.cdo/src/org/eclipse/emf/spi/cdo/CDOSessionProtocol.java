/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
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
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDLibraryDescriptor;
import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.common.model.CDOPackageURICompressor;
import org.eclipse.emf.cdo.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.spi.common.revision.CDOIDMapper;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.transaction.CDOTimeStampContext;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.concurrent.RWLockManager.LockType;
import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;
import org.eclipse.net4j.util.io.StringCompressor;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction.InternalCDOCommitContext;
import org.eclipse.emf.spi.cdo.InternalCDOXATransaction.InternalCDOXACommitContext;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public interface CDOSessionProtocol
{
  public OpenSessionResult openSession(String repositoryName, boolean passiveUpdateEnabled);

  public void loadLibraries(Set<String> missingLibraries, File cacheFolder);

  public void setPassiveUpdate(Map<CDOID, CDORevision> allRevisions, int initialChunkSize, boolean passiveUpdateEnabled);

  public RepositoryTimeResult getRepositoryTime();

  public void loadPackage(CDOPackage cdoPackage, boolean onlyEcore);

  public Object loadChunk(InternalCDORevision revision, CDOFeature feature, int accessIndex, int fetchIndex,
      int fromIndex, int toIndex);

  public List<InternalCDORevision> loadRevisions(Collection<CDOID> ids, int referenceChunk);

  public List<InternalCDORevision> loadRevisionsByTime(Collection<CDOID> ids, int referenceChunk, long timeStamp);

  public InternalCDORevision loadRevisionByVersion(CDOID id, int referenceChunk, int version);

  public List<InternalCDORevision> verifyRevision(List<InternalCDORevision> revisions);

  public Collection<CDOTimeStampContext> syncRevisions(Map<CDOID, CDORevision> allRevisions, int initialChunkSize);

  public void openView(int viewId, byte protocolViewType, long timeStamp);

  public void closeView(int viewId);

  public boolean[] setAudit(int viewId, long timeStamp, List<InternalCDOObject> invalidObjects);

  public void changeSubscription(int viewId, List<CDOID> cdoIDs, boolean subscribeMode, boolean clear);

  public List<Object> query(int viewID, AbstractQueryIterator<?> queryResult);

  public boolean cancelQuery(int queryId);

  public void lockObjects(CDOView view, Collection<? extends CDOObject> objects, long timeout, LockType lockType)
      throws InterruptedException;

  public void unlockObjects(CDOView view, Collection<? extends CDOObject> objects, LockType lockType);

  public boolean isObjectLocked(CDOView view, CDOObject object, LockType lockType);

  public CommitTransactionResult commitTransaction(InternalCDOCommitContext commitContext, OMMonitor monitor);

  public CommitTransactionResult commitTransactionPhase1(InternalCDOXACommitContext xaContext, OMMonitor monitor);

  public CommitTransactionResult commitTransactionPhase2(InternalCDOXACommitContext xaContext, OMMonitor monitor);

  public CommitTransactionResult commitTransactionPhase3(InternalCDOXACommitContext xaContext, OMMonitor monitor);

  public CommitTransactionResult commitTransactionCancel(InternalCDOXACommitContext xaContext, OMMonitor monitor);

  /**
   * @author Eike Stepper
   */
  public final class OpenSessionResult implements CDOPackageURICompressor
  {
    private int sessionID;

    private String repositoryUUID;

    private long repositoryCreationTime;

    private RepositoryTimeResult repositoryTimeResult;

    private boolean repositorySupportingAudits;

    private CDOIDLibraryDescriptor libraryDescriptor;

    private List<CDOPackageInfo> packageInfos = new ArrayList<CDOPackageInfo>();

    private StringCompressor compressor = new StringCompressor(true);

    public OpenSessionResult(int sessionID, String repositoryUUID, long repositoryCreationTime,
        boolean repositorySupportingAudits, CDOIDLibraryDescriptor libraryDescriptor)
    {
      this.sessionID = sessionID;
      this.repositoryUUID = repositoryUUID;
      this.repositoryCreationTime = repositoryCreationTime;
      this.repositorySupportingAudits = repositorySupportingAudits;
      this.libraryDescriptor = libraryDescriptor;
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

    public RepositoryTimeResult getRepositoryTimeResult()
    {
      return repositoryTimeResult;
    }

    public void setRepositoryTimeResult(RepositoryTimeResult repositoryTimeResult)
    {
      this.repositoryTimeResult = repositoryTimeResult;
    }

    public CDOIDLibraryDescriptor getLibraryDescriptor()
    {
      return libraryDescriptor;
    }

    public List<CDOPackageInfo> getPackageInfos()
    {
      return packageInfos;
    }

    public void addPackageInfo(String packageURI, boolean dynamic, CDOIDMetaRange metaIDRange, String parentURI)
    {
      packageInfos.add(new CDOPackageInfo(packageURI, dynamic, metaIDRange, parentURI));
    }

    public StringCompressor getCompressor()
    {
      return compressor;
    }

    /**
     * @since 2.0
     */
    public void writePackageURI(ExtendedDataOutput out, String uri) throws IOException
    {
      compressor.write(out, uri);
    }

    /**
     * @since 2.0
     */
    public String readPackageURI(ExtendedDataInput in) throws IOException
    {
      return compressor.read(in);
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
      return MessageFormat
          .format(
              "RepositoryTime[requested={0,date} {0,time}, indicated={1,date} {1,time}, responded={2,date} {2,time}, confirmed={3,date} {3,time}]",
              requested, indicated, responded, confirmed);
    }
  }

  /**
   * @author Eike Stepper
   */
  public final class CommitTransactionResult
  {
    private String rollbackMessage;

    private long timeStamp;

    private Map<CDOIDTemp, CDOID> idMappings = new HashMap<CDOIDTemp, CDOID>();

    private CDOReferenceAdjuster referenceAdjuster;

    private InternalCDOCommitContext commitContext;

    public CommitTransactionResult(InternalCDOCommitContext commitContext, String rollbackMessage)
    {
      this.rollbackMessage = rollbackMessage;
      this.commitContext = commitContext;
    }

    public CommitTransactionResult(InternalCDOCommitContext commitContext, long timeStamp)
    {
      this.timeStamp = timeStamp;
      this.commitContext = commitContext;
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

    public Map<CDOIDTemp, CDOID> getIDMappings()
    {
      return idMappings;
    }

    public void addIDMapping(CDOIDTemp oldID, CDOID newID)
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

      public CDOIDProvider getIdProvider()
      {
        return idProvider;
      }

      public CDOIDMapper getIdMapper()
      {
        return idMapper;
      }

      public Object adjustReference(Object id)
      {
        if (id == null)
        {
          return null;
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
