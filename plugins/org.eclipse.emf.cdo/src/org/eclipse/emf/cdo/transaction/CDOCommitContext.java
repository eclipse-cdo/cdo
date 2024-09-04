/*
  * Copyright (c) 2009-2012, 2014, 2016, 2019, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.transaction;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchProvider;
import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lob.CDOLob;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Provides a context for commit operations.
 *
 * @author Simon McDuff
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOCommitContext extends CDOBranchProvider
{
  /**
   * @since 4.1
   */
  public String getUserID();

  /**
   * @since 4.1
   */
  public int getViewID();

  /**
   * @since 4.1
   */
  @Override
  public CDOBranch getBranch();

  /**
   * Returns the {@link CDOTransaction transaction} associated with this commit context.
   */
  public CDOTransaction getTransaction();

  /**
   * @since 4.0
   */
  public boolean isPartialCommit();

  /**
   * @since 4.1
   */
  public CDOCommitData getCommitData();

  /**
   * @since 4.1
   */
  public String getCommitComment();

  /**
   * @since 4.25
   */
  public Map<String, String> getCommitProperties();

  /**
   * @since 4.6
   */
  public CDOBranchPoint getCommitMergeSource();

  /**
   * Returns a list of the new {@link CDOPackageUnit package units} that are to be committed with this commit context.
   */
  public List<CDOPackageUnit> getNewPackageUnits();

  /**
   * Returns a map of the new {@link CDOObject objects} that are to be committed with this commit context.
   */
  public Map<CDOID, CDOObject> getNewObjects();

  /**
   * Returns a map of the dirty {@link CDOObject objects} that are to be committed with this commit context.
   */
  public Map<CDOID, CDOObject> getDirtyObjects();

  /**
   * Returns a map of the detached {@link CDOObject objects} that are to be committed with this commit context.
   */
  public Map<CDOID, CDOObject> getDetachedObjects();

  /**
   * Returns a map of the {@link CDORevisionDelta revision deltas} that are to be committed with this commit context.
   */
  public Map<CDOID, CDORevisionDelta> getRevisionDeltas();

  /**
   * @since 4.0
   */
  public Collection<CDOLob<?>> getLobs();

  /**
   * @since 4.1
   * @deprecated As of 4.5 no longer supported. See {@link #getIDsToUnlock()}.
   */
  @Deprecated
  public boolean isAutoReleaseLocks();

  /**
   * @since 4.1
   */
  public Collection<CDOLockState> getLocksOnNewObjects();

  /**
   * @since 4.6
   */
  public Collection<CDOID> getIDsToUnlock();
}
