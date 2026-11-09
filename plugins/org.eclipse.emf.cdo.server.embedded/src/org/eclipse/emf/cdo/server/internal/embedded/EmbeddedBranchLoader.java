/*
 * Copyright (c) 2016, 2019-2021, 2025 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchHandler;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader5;

import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * @author Eike Stepper
 */
public final class EmbeddedBranchLoader implements BranchLoader5
{
  private final BranchLoader5 delegate;

  private final IRepository repository;

  public EmbeddedBranchLoader(BranchLoader5 delegate)
  {
    this.delegate = delegate;
    repository = (IRepository)delegate;
  }

  public BranchLoader5 getDelegate()
  {
    return delegate;
  }

  @Override
  public Pair<Integer, Long> createBranch(int branchID, BranchInfo branchInfo)
  {
    try (EmbeddedStoreContext ignored = getStoreContext())
    {
      return delegate.createBranch(branchID, branchInfo);
    }
  }

  @Override
  public BranchInfo loadBranch(int branchID)
  {
    try (EmbeddedStoreContext ignored = getStoreContext())
    {
      return delegate.loadBranch(branchID);
    }
  }

  @Override
  public SubBranchInfo[] loadSubBranches(int branchID)
  {
    try (EmbeddedStoreContext ignored = getStoreContext())
    {
      return delegate.loadSubBranches(branchID);
    }
  }

  @Override
  public int loadBranches(int startID, int endID, CDOBranchHandler branchHandler)
  {
    try (EmbeddedStoreContext ignored = getStoreContext())
    {
      return delegate.loadBranches(startID, endID, branchHandler);
    }
  }

  @Override
  public void renameBranch(int branchID, String oldName, String newName)
  {
    try (EmbeddedStoreContext ignored = getStoreContext())
    {
      delegate.renameBranch(branchID, oldName, newName);
    }
  }

  @Override
  public CDOBranchPoint changeTag(AtomicInteger modCount, String oldName, String newName, CDOBranchPoint branchPoint)
  {
    try (EmbeddedStoreContext ignored = getStoreContext())
    {
      return delegate.changeTag(modCount, oldName, newName, branchPoint);
    }
  }

  @Override
  public void loadTags(String name, Consumer<BranchInfo> handler)
  {
    try (EmbeddedStoreContext ignored = getStoreContext())
    {
      delegate.loadTags(name, handler);
    }
  }

  @Override
  public CDOBranch[] deleteBranches(int branchID, OMMonitor monitor)
  {
    try (EmbeddedStoreContext ignored = getStoreContext())
    {
      return delegate.deleteBranches(branchID, monitor);
    }
  }

  @Override
  @Deprecated
  public void deleteBranch(int branchID)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public void renameBranch(int branchID, String newName)
  {
    throw new UnsupportedOperationException();
  }

  private EmbeddedStoreContext getStoreContext()
  {
    IStore store = repository.getStore();
    return EmbeddedStoreContext.install(EmbeddedSession.get(), store);
  }
}
