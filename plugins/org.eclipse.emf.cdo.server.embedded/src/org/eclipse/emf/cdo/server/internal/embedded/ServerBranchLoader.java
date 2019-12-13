/*
 * Copyright (c) 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.embedded;

import org.eclipse.emf.cdo.common.branch.CDOBranchHandler;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.ISessionManager;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader3;
import org.eclipse.emf.cdo.spi.server.InternalSession;

import org.eclipse.net4j.util.collection.Pair;

/**
 * @author Eike Stepper
 */
public final class ServerBranchLoader implements BranchLoader3
{
  private final BranchLoader3 delegate;

  private final ISessionManager sessionManager;

  public ServerBranchLoader(BranchLoader3 delegate)
  {
    this.delegate = delegate;
    sessionManager = ((IRepository)delegate).getSessionManager();
  }

  public BranchLoader3 getDelegate()
  {
    return delegate;
  }

  @Override
  public Pair<Integer, Long> createBranch(int branchID, BranchInfo branchInfo)
  {
    if (!StoreThreadLocal.hasSession())
    {
      try
      {
        StoreThreadLocal.setSession(getServerSession());
        return delegate.createBranch(branchID, branchInfo);
      }
      finally
      {
        StoreThreadLocal.release();
      }
    }

    return delegate.createBranch(branchID, branchInfo);
  }

  @Override
  public BranchInfo loadBranch(int branchID)
  {
    if (!StoreThreadLocal.hasSession())
    {
      try
      {
        StoreThreadLocal.setSession(getServerSession());
        return delegate.loadBranch(branchID);
      }
      finally
      {
        StoreThreadLocal.release();
      }
    }

    return delegate.loadBranch(branchID);
  }

  @Override
  public SubBranchInfo[] loadSubBranches(int branchID)
  {
    if (!StoreThreadLocal.hasSession())
    {
      try
      {
        StoreThreadLocal.setSession(getServerSession());
        return delegate.loadSubBranches(branchID);
      }
      finally
      {
        StoreThreadLocal.release();
      }
    }

    return delegate.loadSubBranches(branchID);
  }

  @Override
  public int loadBranches(int startID, int endID, CDOBranchHandler branchHandler)
  {
    if (!StoreThreadLocal.hasSession())
    {
      try
      {
        StoreThreadLocal.setSession(getServerSession());
        return delegate.loadBranches(startID, endID, branchHandler);
      }
      finally
      {
        StoreThreadLocal.release();
      }
    }

    return delegate.loadBranches(startID, endID, branchHandler);
  }

  @Override
  public void renameBranch(int branchID, String oldName, String newName)
  {
    if (!StoreThreadLocal.hasSession())
    {
      try
      {
        StoreThreadLocal.setSession(getServerSession());
        delegate.renameBranch(branchID, oldName, newName);
      }
      finally
      {
        StoreThreadLocal.release();
      }
    }
    else
    {
      delegate.renameBranch(branchID, oldName, newName);
    }
  }

  @Override
  @Deprecated
  public void deleteBranch(int branchID)
  {
  }

  @Override
  @Deprecated
  public void renameBranch(int branchID, String newName)
  {
  }

  private InternalSession getServerSession()
  {
    InternalSession serverSession = ServerSession.get();
    if (serverSession == null)
    {
      // In contrast to revision loading the branch loading can happen through
      // parent branches and not explicitly through the delegating client branch manager.
      // In these cases the server session is not set, so use any open session.
      serverSession = (InternalSession)sessionManager.getSessions()[0];
    }

    return serverSession;
  }
}
