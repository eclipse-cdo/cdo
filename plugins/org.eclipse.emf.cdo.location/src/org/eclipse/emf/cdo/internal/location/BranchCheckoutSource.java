/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.location;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.location.ICheckoutSource;
import org.eclipse.emf.cdo.location.IRepositoryLocation;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionConfiguration;

import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.container.ISlow;
import org.eclipse.net4j.util.io.IOUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class BranchCheckoutSource extends Container<BranchCheckoutSource> implements ICheckoutSource, ISlow
{
  private BranchCheckoutSource parent;

  private String name;

  private List<BranchCheckoutSource> subBranches;

  public BranchCheckoutSource(BranchCheckoutSource parent, String name)
  {
    this.parent = parent;
    this.name = name;
    activate();
  }

  public IRepositoryLocation getRepositoryLocation()
  {
    return parent.getRepositoryLocation();
  }

  public String getBranchPath()
  {
    if (parent != null)
    {
      return parent.getBranchPath() + "/" + name;
    }

    return name;
  }

  public long getTimeStamp()
  {
    return CDOBranchPoint.UNSPECIFIED_DATE;
  }

  public synchronized BranchCheckoutSource[] getElements()
  {
    if (subBranches == null)
    {
      subBranches = new ArrayList<BranchCheckoutSource>();

      CDOSessionConfiguration config = getRepositoryLocation().createSessionConfiguration();
      CDOSession session = config.openSession();
      if (session.getRepositoryInfo().isSupportingBranches())
      {
        try
        {
          CDOBranch branch = session.getBranchManager().getBranch(getBranchPath());
          CDOBranch[] branches = branch.getBranches();
          for (CDOBranch subBranch : branches)
          {
            subBranches.add(new BranchCheckoutSource(this, subBranch.getName()));
          }
        }
        finally
        {
          IOUtil.close(session);
        }
      }
    }

    return subBranches.toArray(new BranchCheckoutSource[subBranches.size()]);
  }

  @Override
  public String toString()
  {
    return name;
  }

  /**
   * @author Eike Stepper
   */
  public static class Main extends BranchCheckoutSource
  {
    private IRepositoryLocation location;

    public Main(IRepositoryLocation location)
    {
      super(null, CDOBranch.MAIN_BRANCH_NAME);
      this.location = location;
    }

    @Override
    public IRepositoryLocation getRepositoryLocation()
    {
      return location;
    }
  }
}
