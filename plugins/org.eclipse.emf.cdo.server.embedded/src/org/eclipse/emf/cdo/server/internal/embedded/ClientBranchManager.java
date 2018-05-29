/*
 * Copyright (c) 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.embedded;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchChangedEvent.ChangeKind;
import org.eclipse.emf.cdo.common.branch.CDOBranchHandler;
import org.eclipse.emf.cdo.common.util.CDOTimeProvider;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.session.CDORepositoryInfo;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader.BranchInfo;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

/**
 * @author Eike Stepper
 */
public class ClientBranchManager extends AbstractClientManager<InternalCDOBranchManager> implements InternalCDOBranchManager
{
  public ClientBranchManager(InternalCDOBranchManager delegate)
  {
    super(delegate);
  }

  public void initMainBranch(boolean local, long timestamp)
  {
    // Do nothing.
  }

  public CDOCommonRepository getRepository()
  {
    if (clientSession == null)
    {
      return null;
    }

    return clientSession.getRepositoryInfo();
  }

  public void setRepository(CDOCommonRepository repository)
  {
    initServerSession((CDONet4jSession)((CDORepositoryInfo)repository).getSession());
  }

  public BranchLoader getBranchLoader()
  {
    return delegate.getBranchLoader();
  }

  public void setBranchLoader(BranchLoader branchLoader)
  {
    // Do nothing.
  }

  public CDOTimeProvider getTimeProvider()
  {
    return delegate.getTimeProvider();
  }

  public InternalCDOBranch getMainBranch()
  {
    return delegate.getMainBranch();
  }

  public InternalCDOBranch getBranch(int branchID)
  {
    return delegate.getBranch(branchID);
  }

  public InternalCDOBranch getBranch(int id, String name, InternalCDOBranch baseBranch, long baseTimeStamp)
  {
    return delegate.getBranch(id, name, baseBranch, baseTimeStamp);
  }

  public InternalCDOBranch getBranch(int id, BranchInfo branchInfo)
  {
    return delegate.getBranch(id, branchInfo);
  }

  public InternalCDOBranch getBranch(String path)
  {
    return delegate.getBranch(path);
  }

  public InternalCDOBranch createBranch(int id, String name, InternalCDOBranch baseBranch, long baseTimeStamp)
  {
    return delegate.createBranch(id, name, baseBranch, baseTimeStamp);
  }

  public void handleBranchChanged(InternalCDOBranch branch, ChangeKind changeKind)
  {
    delegate.handleBranchChanged(branch, changeKind);
  }

  public int getBranches(int startID, int endID, CDOBranchHandler handler)
  {
    return delegate.getBranches(startID, endID, handler);
  }

  @Deprecated
  public void setTimeProvider(CDOTimeProvider timeProvider)
  {
    // Do nothing.
  }

  @Deprecated
  public void renameBranch(CDOBranch branch, String newName)
  {
    // Do nothing.
  }

  @Deprecated
  public void handleBranchCreated(InternalCDOBranch branch)
  {
    // Do nothing.
  }

  @Override
  protected InternalRepository getRepository(InternalCDOBranchManager delegate)
  {
    return (InternalRepository)delegate.getRepository();
  }
}
