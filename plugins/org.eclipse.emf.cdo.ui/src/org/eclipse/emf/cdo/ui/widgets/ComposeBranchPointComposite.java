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
package org.eclipse.emf.cdo.ui.widgets;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public class ComposeBranchPointComposite extends Composite
{
  private CDOSession session;

  private CDOBranchPoint branchPoint;

  private SelectBranchComposite selectBranchComposite;

  private SelectTimeStampComposite selectTimeStampComposite;

  public ComposeBranchPointComposite(Composite parent, int style, CDOSession session, CDOBranchPoint branchPoint,
      boolean allowTimeStamp)
  {
    super(parent, style);
    this.session = session;
    this.branchPoint = branchPoint;

    GridLayout gridLayout = UIUtil.createGridLayout(1);
    gridLayout.verticalSpacing = 5;

    setLayout(gridLayout);

    CDOBranch branch = branchPoint == null ? session.getBranchManager().getMainBranch() : branchPoint.getBranch();
    if (session.getRepositoryInfo().isSupportingBranches())
    {
      selectBranchComposite = createSelectBranchComposite(session, branch);
      selectBranchComposite.setLayoutData(UIUtil.createGridData());
      selectBranchComposite.getBranchViewer().expandAll();
    }

    if (allowTimeStamp)
    {
      long timeStamp = branchPoint == null ? CDOBranchPoint.UNSPECIFIED_DATE : branchPoint.getTimeStamp();
      selectTimeStampComposite = createSelectTimeStampComposite(branch, timeStamp);
      selectTimeStampComposite.setLayoutData(UIUtil.createGridData(true, false));
    }
  }

  public CDOSession getSession()
  {
    return session;
  }

  public CDOBranchPoint getBranchPoint()
  {
    return branchPoint;
  }

  public SelectBranchComposite getSelectBranchComposite()
  {
    return selectBranchComposite;
  }

  public SelectTimeStampComposite getSelectTimeComposite()
  {
    return selectTimeStampComposite;
  }

  protected SelectTimeStampComposite createSelectTimeStampComposite(CDOBranch branch, long timeStamp)
  {
    return new SelectTimeStampComposite(this, SWT.NONE, branch, timeStamp)
    {
      @Override
      protected void timeStampChanged(long timeStamp)
      {
        composeBranchPoint();
      }
    };
  }

  protected SelectBranchComposite createSelectBranchComposite(CDOSession session, CDOBranch branch)
  {
    return new SelectBranchComposite(this, SWT.NONE, session, branch)
    {
      @Override
      protected void branchChanged(CDOBranch newBranch)
      {
        if (selectTimeStampComposite != null)
        {
          selectTimeStampComposite.setBranch(newBranch);
        }
  
        composeBranchPoint();
      }
    };
  }

  protected void branchPointChanged(CDOBranchPoint newBranchPoint)
  {
  }

  private void composeBranchPoint()
  {
    CDOBranchPoint oldBranchPoint = branchPoint;

    CDOBranch branch = session.getBranchManager().getMainBranch();
    if (selectBranchComposite != null)
    {
      branch = selectBranchComposite.getBranch();
    }

    long timeStamp = CDOBranchPoint.UNSPECIFIED_DATE;
    if (selectTimeStampComposite != null)
    {
      timeStamp = selectTimeStampComposite.getTimeStamp();
    }

    branchPoint = branch == null ? null : branch.getPoint(timeStamp);
    if (!ObjectUtil.equals(branchPoint, oldBranchPoint))
    {
      branchPointChanged(branchPoint);
    }
  }
}
