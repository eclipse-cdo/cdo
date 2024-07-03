/*
 * Copyright (c) 2009-2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.internal.common.commit.CDOChangeSetDataImpl;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.transaction.CDOCommitContext;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.util.collection.Pair;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.CommitTransactionResult;
import org.eclipse.emf.spi.cdo.InternalCDOSession.CommitToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Simon McDuff
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalCDOTransaction extends CDOTransaction, InternalCDOUserTransaction, InternalCDOView
{
  public InternalCDOCommitContext createCommitContext();

  /**
   * @since 4.5
   */
  public CommitToken getCommitToken();

  /**
   * @since 4.25
   */
  public Map<String, String> getCommitProperties();

  /**
   * @since 4.25
   */
  public void setCommitProperties(Map<String, String> properties);

  /**
   * @since 4.6
   */
  public CDOBranchPoint getCommitMergeSource();

  /**
   * @since 4.6
   */
  public void setCommitMergeSource(CDOBranchPoint mergeSource);

  /**
   * @since 4.3
   */
  public boolean hasMultipleSavepoints();

  /**
   * @since 3.0
   */
  @Override
  public InternalCDOSavepoint setSavepoint();

  /**
   * @since 4.1
   */
  @Override
  public InternalCDOSavepoint getFirstSavepoint();

  /**
   * @since 3.0
   */
  @Override
  public InternalCDOSavepoint getLastSavepoint();

  /**
   * @since 3.0
   */
  public InternalCDOSavepoint handleSetSavepoint();

  /**
   * @since 3.0
   */
  public void handleRollback(InternalCDOSavepoint savepoint);

  public CDOTransactionStrategy getTransactionStrategy();

  public void setTransactionStrategy(CDOTransactionStrategy transactionStrategy);

  /**
   * @return never <code>null</code>;
   */
  public CDOResourceFolder getOrCreateResourceFolder(List<String> names);

  public void detachObject(InternalCDOObject object);

  /**
   * @deprecated {@link #createIDForNewObject(EObject)} is called since 4.1.
   */
  @Deprecated
  public CDOIDTemp getNextTemporaryID();

  /**
   * @since 4.1
   */
  public CDOID createIDForNewObject(EObject object);

  /**
   * @since 4.0
   */
  public void registerAttached(InternalCDOObject object, boolean isNew);

  public void registerDirty(InternalCDOObject object, CDOFeatureDelta featureDelta);

  /**
   * @since 4.3
   */
  public void registerDirty(InternalCDOObject object, CDOFeatureDelta featureDelta, InternalCDORevision cleanRevision);

  public void registerFeatureDelta(InternalCDOObject object, CDOFeatureDelta featureDelta);

  /**
   * @since 4.3
   */
  public void registerFeatureDelta(InternalCDOObject object, CDOFeatureDelta featureDelta, InternalCDORevision cleanRevision);

  public void registerRevisionDelta(CDORevisionDelta revisionDelta);

  /**
   * @since 4.2
   */
  public void setDirty(boolean dirty);

  public void setConflict(InternalCDOObject object);

  /**
   * @since 4.4
   */
  public void removeConflict(InternalCDOObject object);

  /**
   * @param source
   *          May be <code>null</code> if changeSetData does not result from a
   *          {@link #merge(CDOBranchPoint, org.eclipse.emf.cdo.transaction.CDOMerger) merge} or if the merge was not in
   *          a {@link CDOBranch#isLocal() local} branch.
   * @since 4.0
   * @deprecated Use
   *             {@link #applyChangeSet(CDOChangeSetData, CDORevisionProvider, CDORevisionProvider, CDOBranchPoint, boolean)}
   */
  @Deprecated
  public Pair<CDOChangeSetData, Pair<Map<CDOID, CDOID>, List<CDOID>>> applyChangeSetData(CDOChangeSetData changeSetData, CDORevisionProvider targetBaseProvider,
      CDORevisionProvider targetProvider, CDOBranchPoint source);

  /**
   * @param source
   *          May be <code>null</code> if changeSetData does not result from a
   *          {@link #merge(CDOBranchPoint, org.eclipse.emf.cdo.transaction.CDOMerger) merge} or if the merge was not in
   *          a {@link CDOBranch#isLocal() local} branch.
   * @since 4.1
   */
  public ApplyChangeSetResult applyChangeSet(CDOChangeSetData changeSetData, CDORevisionProvider targetBaseProvider, CDORevisionProvider targetProvider,
      CDOBranchPoint source, boolean keepVersions) throws ChangeSetOutdatedException;

  /**
   * @since 4.0
   */
  public Map<InternalCDOObject, InternalCDORevision> getCleanRevisions();

  /**
   * Provides a context for a commit operation.
   *
   * @author Simon McDuff
   * @noimplement This interface is not intended to be implemented by clients.
   * @noextend This interface is not intended to be extended by clients.
   */
  public interface InternalCDOCommitContext extends CDOCommitContext
  {
    @Override
    public InternalCDOTransaction getTransaction();

    public void preCommit();

    public void postCommit(CommitTransactionResult result);
  }

  /**
   * If the meaning of this type isn't clear, there really should be more of a description here...
   *
   * @author Eike Stepper
   * @since 4.1
   */
  public final class ApplyChangeSetResult
  {
    private CDOChangeSetData changeSetData = new CDOChangeSetDataImpl();

    private Map<CDOID, CDOID> idMappings = CDOIDUtil.createMap();

    private List<CDOID> adjustedObjects = new ArrayList<>();

    public ApplyChangeSetResult()
    {
    }

    public CDOChangeSetData getChangeSetData()
    {
      return changeSetData;
    }

    public Map<CDOID, CDOID> getIDMappings()
    {
      return idMappings;
    }

    public List<CDOID> getAdjustedObjects()
    {
      return adjustedObjects;
    }
  }

  /**
   * If the meaning of this type isn't clear, there really should be more of a description here...
   *
   * @author Eike Stepper
   * @since 4.1
   */
  public static final class ChangeSetOutdatedException extends RuntimeException
  {
    private static final long serialVersionUID = 1L;

    public ChangeSetOutdatedException()
    {
      super("Change set is outdated");
    }
  }
}
