/*
 * Copyright (c) 2016, 2019-2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.embedded;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchChangedEvent.ChangeKind;
import org.eclipse.emf.cdo.common.branch.CDOBranchHandler;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchTag;
import org.eclipse.emf.cdo.common.util.CDOTimeProvider;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.session.CDORepositoryInfo;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader.BranchInfo;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.net4j.util.om.monitor.OMMonitor;

import java.util.LinkedHashSet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Eike Stepper
 */
public class ClientBranchManager extends AbstractClientManager<InternalCDOBranchManager> implements InternalCDOBranchManager
{
  public ClientBranchManager(InternalCDOBranchManager delegate)
  {
    super(delegate);
  }

  @Override
  public void initMainBranch(boolean local, long timestamp)
  {
    // Do nothing.
  }

  @Override
  public CDOCommonRepository getRepository()
  {
    if (clientSession == null)
    {
      return null;
    }

    return clientSession.getRepositoryInfo();
  }

  @Override
  public void setRepository(CDOCommonRepository repository)
  {
    initServerSession((CDONet4jSession)((CDORepositoryInfo)repository).getSession());
  }

  @Override
  public BranchLoader getBranchLoader()
  {
    return delegate.getBranchLoader();
  }

  @Override
  public void setBranchLoader(BranchLoader branchLoader)
  {
    // Do nothing.
  }

  @Override
  public CDOTimeProvider getTimeProvider()
  {
    return delegate.getTimeProvider();
  }

  @Override
  public InternalCDOBranch getMainBranch()
  {
    return delegate.getMainBranch();
  }

  @Override
  public InternalCDOBranch getBranch(int branchID)
  {
    return delegate.getBranch(branchID);
  }

  @Override
  public InternalCDOBranch getBranch(int id, String name, InternalCDOBranch baseBranch, long baseTimeStamp)
  {
    return delegate.getBranch(id, name, baseBranch, baseTimeStamp);
  }

  @Override
  public InternalCDOBranch getBranch(int id, BranchInfo branchInfo)
  {
    return delegate.getBranch(id, branchInfo);
  }

  @Override
  public InternalCDOBranch getBranch(String path)
  {
    return delegate.getBranch(path);
  }

  @Override
  public InternalCDOBranch createBranch(int id, String name, InternalCDOBranch baseBranch, long baseTimeStamp)
  {
    return delegate.createBranch(id, name, baseBranch, baseTimeStamp);
  }

  @Override
  public CDOBranch[] deleteBranches(int id, OMMonitor monitor)
  {
    return delegate.deleteBranches(id, monitor);
  }

  @Override
  public void handleBranchChanged(InternalCDOBranch branch, ChangeKind changeKind, int... branchIDs)
  {
    delegate.handleBranchChanged(branch, changeKind, branchIDs);
  }

  @Override
  public int getBranches(int startID, int endID, CDOBranchHandler handler)
  {
    return delegate.getBranches(startID, endID, handler);
  }

  @Override
  public LinkedHashSet<CDOBranch> getBranches(int rootID)
  {
    return delegate.getBranches(rootID);
  }

  @Override
  public int getTagModCount()
  {
    return delegate.getTagModCount();
  }

  @Override
  public void setTagModCount(int tagModCount)
  {
    delegate.setTagModCount(tagModCount);
  }

  @Override
  public void handleTagChanged(int modCount, String oldName, String newName, CDOBranchPoint branchPoint)
  {
    delegate.handleTagChanged(modCount, oldName, newName, branchPoint);
  }

  @Override
  public CDOBranchPoint changeTagWithModCount(AtomicInteger modCount, String oldName, String newName, CDOBranchPoint branchPoint)
  {
    return delegate.changeTagWithModCount(modCount, oldName, newName, branchPoint);
  }

  @Override
  public CDOBranchTag createTag(String name, CDOBranchPoint branchPoint)
  {
    return delegate.createTag(name, branchPoint);
  }

  @Override
  public void renameTag(String oldName, String newName)
  {
    delegate.renameTag(oldName, newName);
  }

  @Override
  public void moveTag(CDOBranchTag tag, CDOBranchPoint branchPoint)
  {
    delegate.moveTag(tag, branchPoint);
  }

  @Override
  public void deleteTag(CDOBranchTag tag)
  {
    delegate.deleteTag(tag);
  }

  @Override
  public CDOBranchTag getTag(String name)
  {
    return delegate.getTag(name);
  }

  @Override
  public CDOTagList getTagList()
  {
    return delegate.getTagList();
  }

  @Override
  @Deprecated
  public void setTimeProvider(CDOTimeProvider timeProvider)
  {
    // Do nothing.
  }

  @Override
  @Deprecated
  public void renameBranch(CDOBranch branch, String newName)
  {
    // Do nothing.
  }

  @Override
  @Deprecated
  public void handleBranchCreated(InternalCDOBranch branch)
  {
    // Do nothing.
  }

  @Override
  @Deprecated
  public void handleBranchChanged(InternalCDOBranch branch, ChangeKind changeKind)
  {
    // Do nothing.
  }

  @Override
  protected InternalRepository getRepository(InternalCDOBranchManager delegate)
  {
    return (InternalRepository)delegate.getRepository();
  }
}
