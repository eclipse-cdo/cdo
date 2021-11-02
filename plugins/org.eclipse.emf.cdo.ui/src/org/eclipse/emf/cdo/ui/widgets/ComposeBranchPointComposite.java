/*
 * Copyright (c) 2011, 2012, 2015, 2016, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.ui.CDOItemProvider;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.ui.ValidationContext;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * Composed UI widget offering functionality for users to select {@link org.eclipse.emf.cdo.common.branch.CDOBranch
 * branches} and a {@link org.eclipse.emf.cdo.common.branch.CDOBranchPoint timestamp} within a branch for a particular
 * {@link org.eclipse.emf.cdo.session.CDOSession CDOSession}
 *
 * @author Eike Stepper
 * @since 4.0
 */
public class ComposeBranchPointComposite extends Composite
{
  private boolean allowTimeStamp;

  private CDOBranchPoint branchPoint;

  private TreeViewer branchViewer;

  private Group timeStampGroup;

  private SelectTimeStampComposite timeStampComposite;

  /**
   * @since 4.4
   */
  public ComposeBranchPointComposite(Composite parent, boolean allowTimeStamp, CDOBranchPoint branchPoint)
  {
    this(parent, SWT.NONE, null, branchPoint, allowTimeStamp);
  }

  /**
   * @deprecated As of 4.4 use {@link #ComposeBranchPointComposite(Composite, boolean, CDOBranchPoint)}.
   */
  @Deprecated
  public ComposeBranchPointComposite(Composite parent, int style, CDOSession session, CDOBranchPoint branchPoint, boolean allowTimeStamp)
  {
    super(parent, style);
    this.allowTimeStamp = allowTimeStamp;
    this.branchPoint = branchPoint;

    GridLayout containerGridLayout = new GridLayout(1, false);
    containerGridLayout.marginWidth = 0;
    containerGridLayout.marginHeight = 0;
    containerGridLayout.verticalSpacing = 10;

    setLayout(containerGridLayout);
    createUI();
  }

  /**
   * @since 4.4
   */
  public boolean isAllowTimeStamp()
  {
    return allowTimeStamp;
  }

  /**
   * @since 4.4
   */
  public final void setAllowTimeStamp(boolean allowTimeStamp)
  {
    if (this.allowTimeStamp != allowTimeStamp)
    {
      this.allowTimeStamp = allowTimeStamp;

      if (timeStampGroup != null && !allowTimeStamp)
      {
        timeStampGroup.dispose();
        timeStampGroup = null;
        timeStampComposite = null;
      }
      else if (timeStampGroup == null && allowTimeStamp)
      {
        timeStampComposite = createSelectTimeStampComposite();
      }

      layout();
    }
  }

  public CDOBranchPoint getBranchPoint()
  {
    return branchPoint;
  }

  /**
   * @since 4.4
   */
  public void setBranchPoint(CDOBranchPoint branchPoint)
  {
    this.branchPoint = branchPoint;
    if (branchPoint != null)
    {
      final CDOBranch branch = branchPoint.getBranch();
      final long timeStamp = branchPoint.getTimeStamp();

      if (branchViewer != null)
      {
        setBranchViewerInput();

        getDisplay().asyncExec(new Runnable()
        {
          @Override
          public void run()
          {
            branchViewer.setSelection(new StructuredSelection(branch));
          }
        });
      }

      if (timeStampComposite != null)
      {
        timeStampComposite.setBranch(branch);
        timeStampComposite.setTimeStamp(timeStamp);
      }
    }
  }

  /**
   * @since 4.4
   */
  public TreeViewer getBranchViewer()
  {
    return branchViewer;
  }

  public SelectTimeStampComposite getSelectTimeComposite()
  {
    return timeStampComposite;
  }

  /**
   * @deprecated As of 4.4 use {@link #getBranchViewer()}.
   */
  @Deprecated
  public SelectBranchComposite getSelectBranchComposite()
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @deprecated As of 4.4 no longer supported.
   */
  @Deprecated
  public CDOSession getSession()
  {
    return null;
  }

  /**
   * @since 4.4
   */
  protected void createUI()
  {
    branchViewer = createBranchViewer();
    branchViewer.addSelectionChangedListener(new ISelectionChangedListener()
    {
      @Override
      public void selectionChanged(SelectionChangedEvent event)
      {
        IStructuredSelection selection = (IStructuredSelection)event.getSelection();
        Object element = selection.getFirstElement();
        if (element == null)
        {
          return;
        }

        if (element instanceof CDOBranch)
        {
          CDOBranch branch = (CDOBranch)element;

          if (timeStampComposite != null)
          {
            timeStampComposite.setBranch(branch);
          }

          composeBranchPoint();
        }
      }
    });

    if (allowTimeStamp)
    {
      timeStampComposite = createSelectTimeStampComposite();
    }

    if (branchPoint != null)
    {
      setBranchViewerInput();
    }
  }

  /**
   * @since 4.4
   */
  protected TreeViewer createBranchViewer()
  {
    CDOItemProvider itemProvider = createBranchItemProvider();

    TreeViewer branchViewer = new TreeViewer(this, SWT.BORDER | SWT.SINGLE);
    branchViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    branchViewer.setLabelProvider(itemProvider);
    branchViewer.setContentProvider(itemProvider);

    branchViewer.addDoubleClickListener(new IDoubleClickListener()
    {
      @Override
      public void doubleClick(DoubleClickEvent event)
      {
        doubleClicked();
      }
    });

    return branchViewer;
  }

  /**
   * @since 4.4
   */
  protected CDOItemProvider createBranchItemProvider()
  {
    return new CDOItemProvider(null);
  }

  /**
   * @since 4.4
   */
  protected SelectTimeStampComposite createSelectTimeStampComposite()
  {
    timeStampGroup = new Group(this, SWT.NONE);
    timeStampGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    timeStampGroup.setLayout(new GridLayout(1, false));
    timeStampGroup.setText("Time Stamp:");

    SelectTimeStampComposite timeStampComposite = new SelectTimeStampComposite(timeStampGroup, SWT.NONE, branchPoint.getBranch(), branchPoint.getTimeStamp())
    {
      @Override
      protected void timeStampChanged(long timeStamp)
      {
        composeBranchPoint();
      }
    };

    timeStampComposite.getTimeBrowseButton().setVisible(false);
    timeStampComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    timeStampComposite.setValidationContext(new ValidationContext()
    {
      @Override
      public void setValidationError(Object source, String message)
      {
        timeStampError(message);
      }
    });

    return timeStampComposite;
  }

  /**
   * @deprecated As of 4.4 use {@link #createSelectTimeStampComposite()}.
   */
  @Deprecated
  protected SelectTimeStampComposite createSelectTimeStampComposite(CDOBranch branch, long timeStamp)
  {
    return createSelectTimeStampComposite();
  }

  /**
   * @deprecated As of 4.4 use {@link #createBranchViewer()}.
   */
  @Deprecated
  protected SelectBranchComposite createSelectBranchComposite(CDOSession session, CDOBranch branch)
  {
    return new SelectBranchComposite(this, SWT.NONE, session, branch)
    {
      @Override
      protected void branchChanged(CDOBranch newBranch)
      {
        if (timeStampComposite != null)
        {
          timeStampComposite.setBranch(newBranch);
        }

        composeBranchPoint();
      }
    };
  }

  /**
   * @since 4.4
   */
  protected void timeStampError(String message)
  {
  }

  protected void branchPointChanged(CDOBranchPoint branchPoint)
  {
  }

  /**
   * @since 4.4
   */
  protected void doubleClicked()
  {
  }

  private void setBranchViewerInput()
  {
    CDOBranchManager input = branchPoint.getBranch().getBranchManager();
    if (input != branchViewer.getInput())
    {
      branchViewer.setInput(input);
    }
  }

  private void composeBranchPoint()
  {
    if (branchViewer == null)
    {
      return;
    }

    CDOBranchPoint oldBranchPoint = branchPoint;

    IStructuredSelection selection = (IStructuredSelection)branchViewer.getSelection();
    CDOBranch branch = (CDOBranch)selection.getFirstElement();
    if (branch != null)
    {
      long timeStamp = CDOBranchPoint.UNSPECIFIED_DATE;
      if (allowTimeStamp && timeStampComposite != null)
      {
        timeStamp = timeStampComposite.getTimeStamp();
      }

      branchPoint = branch.getPoint(timeStamp);
      if (!ObjectUtil.equals(branchPoint, oldBranchPoint))
      {
        branchPointChanged(branchPoint);
      }
    }
  }
}
