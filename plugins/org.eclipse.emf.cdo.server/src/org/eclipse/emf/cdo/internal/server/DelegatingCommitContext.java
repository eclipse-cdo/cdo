/*
 * Copyright (c) 2010-2013, 2016, 2019, 2021, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDReference;
import org.eclipse.emf.cdo.common.lock.CDOLockDelta;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.protocol.CDOProtocol.CommitData;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;

import org.eclipse.net4j.util.concurrent.RWOLockManager.LockState;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;

import org.eclipse.emf.ecore.EClass;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Eike Stepper
 */
public abstract class DelegatingCommitContext implements IStoreAccessor.CommitContext
{
  protected abstract CommitContext getDelegate();

  @Override
  public ITransaction getTransaction()
  {
    return getDelegate().getTransaction();
  }

  @Override
  public CDOBranchPoint getBranchPoint()
  {
    return getDelegate().getBranchPoint();
  }

  @Override
  public String getUserID()
  {
    return getDelegate().getUserID();
  }

  @Override
  public String getCommitComment()
  {
    return getDelegate().getCommitComment();
  }

  @Override
  public Map<String, String> getCommitProperties()
  {
    return getDelegate().getCommitProperties();
  }

  @Override
  public InternalCDOPackageRegistry getPackageRegistry()
  {
    return getDelegate().getPackageRegistry();
  }

  @Override
  public InternalCDOPackageUnit[] getNewPackageUnits()
  {
    return getDelegate().getNewPackageUnits();
  }

  @Override
  public InternalCDORevision[] getNewObjects()
  {
    return getDelegate().getNewObjects();
  }

  @Override
  public InternalCDORevision[] getDirtyObjects()
  {
    return getDelegate().getDirtyObjects();
  }

  @Override
  public InternalCDORevisionDelta[] getDirtyObjectDeltas()
  {
    return getDelegate().getDirtyObjectDeltas();
  }

  @Override
  public CDOID[] getDetachedObjects()
  {
    return getDelegate().getDetachedObjects();
  }

  @Override
  public Map<CDOID, EClass> getDetachedObjectTypes()
  {
    return getDelegate().getDetachedObjectTypes();
  }

  @Override
  public CDORevision getRevision(CDOID id)
  {
    return getDelegate().getRevision(id);
  }

  @Override
  public Map<CDOID, CDOID> getIDMappings()
  {
    return getDelegate().getIDMappings();
  }

  @Override
  public long getPreviousTimeStamp()
  {
    return getDelegate().getPreviousTimeStamp();
  }

  @Override
  public long getLastUpdateTime()
  {
    return getDelegate().getLastUpdateTime();
  }

  @Override
  public boolean isClearResourcePathCache()
  {
    return getDelegate().isClearResourcePathCache();
  }

  @Override
  public boolean isUsingEcore()
  {
    return getDelegate().isUsingEcore();
  }

  @Override
  public boolean isUsingEtypes()
  {
    return getDelegate().isUsingEtypes();
  }

  @Override
  public CDOLockState[] getLocksOnNewObjects()
  {
    return getDelegate().getLocksOnNewObjects();
  }

  @Override
  public CDOID[] getIDsToUnlock()
  {
    return getDelegate().getIDsToUnlock();
  }

  @Override
  public List<CDOLockDelta> getLockDeltas()
  {
    return getDelegate().getLockDeltas();
  }

  @Override
  public List<CDOLockState> getLockStates()
  {
    return getDelegate().getLockStates();
  }

  @Override
  public CDOBranchVersion[] getDetachedObjectVersions()
  {
    return getDelegate().getDetachedObjectVersions();
  }

  @Override
  public ExtendedDataInputStream getLobs()
  {
    return getDelegate().getLobs();
  }

  @Override
  public CDOCommitInfo createCommitInfo()
  {
    return getDelegate().createCommitInfo();
  }

  @Override
  public byte getRollbackReason()
  {
    return getDelegate().getRollbackReason();
  }

  @Override
  public String getRollbackMessage()
  {
    return getDelegate().getRollbackMessage();
  }

  @Override
  public List<CDOIDReference> getXRefs()
  {
    return getDelegate().getXRefs();
  }

  @Override
  public CDOBranchPoint getCommitMergeSource()
  {
    return getDelegate().getCommitMergeSource();
  }

  @Override
  public byte getSecurityImpact()
  {
    return getDelegate().getSecurityImpact();
  }

  @Override
  public Map<CDOID, InternalCDORevision> getOldRevisions()
  {
    return getDelegate().getOldRevisions();
  }

  @Override
  public Map<CDOID, InternalCDORevision> getNewRevisions()
  {
    return getDelegate().getNewRevisions();
  }

  @Override
  public CommitData getOriginalCommmitData()
  {
    return getDelegate().getOriginalCommmitData();
  }

  @Override
  public <T> T getData(Object key)
  {
    return getDelegate().getData(key);
  }

  @Override
  public <T> T setData(Object key, T data)
  {
    return getDelegate().setData(key, data);
  }

  @Override
  public void modify(Consumer<ModificationContext> modifier)
  {
    getDelegate().modify(modifier);
  }

  @Override
  @Deprecated
  public boolean isAutoReleaseLocksEnabled()
  {
    return getDelegate().isAutoReleaseLocksEnabled();
  }

  @Override
  @Deprecated
  public List<LockState<Object, IView>> getPostCommmitLockStates()
  {
    return getDelegate().getPostCommmitLockStates();
  }

  /**
   * @author Eike Stepper
   */
  @SuppressWarnings("unused")
  private static final class InternalCompletenessChecker extends DelegatingCommitContext
  {
    @Override
    protected CommitContext getDelegate()
    {
      return null;
    }
  }
}
