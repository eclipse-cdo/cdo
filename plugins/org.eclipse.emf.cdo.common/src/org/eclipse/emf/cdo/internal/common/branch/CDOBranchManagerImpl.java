/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.common.branch;

import org.eclipse.emf.cdo.common.CDOTimeProvider;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchCreatedEvent;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader.BranchInfo;

import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.ref.ReferenceValueMap;
import org.eclipse.net4j.util.ref.ReferenceValueMap.Soft;

import java.util.Map;

/**
 * @author Eike Stepper
 */
public class CDOBranchManagerImpl extends Lifecycle implements InternalCDOBranchManager
{
  private BranchLoader branchLoader;

  private CDOTimeProvider timeProvider;

  private InternalCDOBranch mainBranch;

  private Map<Integer, InternalCDOBranch> branches = createMap();

  public CDOBranchManagerImpl()
  {
  }

  public BranchLoader getBranchLoader()
  {
    return branchLoader;
  }

  public void setBranchLoader(BranchLoader branchLoader)
  {
    checkInactive();
    this.branchLoader = branchLoader;
  }

  public CDOTimeProvider getTimeProvider()
  {
    return timeProvider;
  }

  public void setTimeProvider(CDOTimeProvider timeProvider)
  {
    this.timeProvider = timeProvider;
  }

  public void initMainBranch(long repositoryCreationTime)
  {
    checkInactive();
    mainBranch = new CDOBranchImpl.Main(this, repositoryCreationTime);
  }

  public void handleBranchCreated(InternalCDOBranch branch)
  {
    CDOBranchPoint base = branch.getBase();
    InternalCDOBranch baseBranch = (InternalCDOBranch)base.getBranch();
    baseBranch.addChild(branch);

    fireEvent(new BranchCreatedEvent(branch));
  }

  public InternalCDOBranch getMainBranch()
  {
    checkActive();
    return mainBranch;
  }

  public InternalCDOBranch getBranch(int branchID)
  {
    checkActive();
    if (branchID == CDOBranch.MAIN_BRANCH_ID)
    {
      return mainBranch;
    }

    InternalCDOBranch branch;
    synchronized (branches)
    {
      branch = branches.get(branchID);
      if (branch == null)
      {
        branch = new CDOBranchImpl(branchID, this);
        putBranch(branch);
      }
    }

    return branch;
  }

  public InternalCDOBranch getBranch(int id, String name, long baseTimeStamp, InternalCDOBranch base)
  {
    synchronized (branches)
    {
      InternalCDOBranch branch = branches.get(id);
      if (branch == null)
      {
        branch = new CDOBranchImpl(id, name, CDOBranchUtil.createBranchPoint(base, baseTimeStamp));
        putBranch(branch);
      }
      else if (branch.isProxy())
      {
        branch.setBranchInfo(name, base, baseTimeStamp);
      }

      return branch;
    }
  }

  public InternalCDOBranch getBranch(String path)
  {
    int sep = path.indexOf(CDOBranch.PATH_SEPARATOR);
    if (sep == -1)
    {
      if (CDOBranch.MAIN_BRANCH_NAME.equals(path))
      {
        return mainBranch;
      }

      return null;
    }

    String name = path.substring(0, sep);
    if (CDOBranch.MAIN_BRANCH_NAME.equals(name))
    {
      String rest = path.substring(sep + 1);
      return mainBranch.getBranch(rest);
    }

    return null;
  }

  public InternalCDOBranch createBranch(String name, InternalCDOBranch baseBranch, long baseTimeStamp)
  {
    checkActive();

    if (baseTimeStamp == CDOBranchPoint.UNSPECIFIED_DATE)
    {
      baseTimeStamp = timeProvider.getTimeStamp();
    }

    int branchID = branchLoader.createBranch(new BranchInfo(name, baseBranch.getID(), baseTimeStamp));
    CDOBranchPoint base = CDOBranchUtil.createBranchPoint(baseBranch, baseTimeStamp);
    InternalCDOBranch branch = new CDOBranchImpl(branchID, name, base);
    synchronized (branches)
    {
      putBranch(branch);
    }

    handleBranchCreated(branch);
    return branch;
  }

  /**
   * {@link #branches} must be synchronized by caller!
   */
  private boolean putBranch(InternalCDOBranch branch)
  {
    int id = branch.getID();
    if (branches.containsKey(id))
    {
      return false;
    }

    branches.put(id, branch);
    return true;
  }

  protected Soft<Integer, InternalCDOBranch> createMap()
  {
    return new ReferenceValueMap.Soft<Integer, InternalCDOBranch>();
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkNull(branchLoader, "branchLoader");
    checkNull(timeProvider, "timeProvider");
    checkNull(mainBranch, "mainBranch");
  }

  /**
   * @author Eike Stepper
   */
  private static final class BranchCreatedEvent extends Event implements CDOBranchCreatedEvent
  {
    private static final long serialVersionUID = 1L;

    private CDOBranch branch;

    public BranchCreatedEvent(CDOBranch branch)
    {
      super(branch.getBranchManager());
      this.branch = branch;
    }

    @Override
    public CDOBranchManager getSource()
    {
      return (CDOBranchManager)super.getSource();
    }

    public CDOBranch getBranch()
    {
      return branch;
    }
  }
}
