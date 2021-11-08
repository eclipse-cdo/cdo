/*
 * Copyright (c) 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.dialogs;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.ui.CDOItemProvider;
import org.eclipse.emf.cdo.ui.widgets.ComposeBranchPointComposite;

import org.eclipse.net4j.util.ui.widgets.AbstractDialog;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Eike Stepper
 */
public abstract class AbstractBranchPointDialog extends AbstractDialog
{
  private final boolean allowTimeStamp;

  private final CDOBranchPoint branchPoint;

  private ComposeBranchPointComposite branchPointComposite;

  private String timeStampError;

  public AbstractBranchPointDialog(Shell parentShell, boolean allowTimeStamp, CDOBranchPoint branchPoint)
  {
    super(parentShell);
    this.allowTimeStamp = allowTimeStamp;
    this.branchPoint = branchPoint;
  }

  public final boolean isAllowTimeStamp()
  {
    return allowTimeStamp;
  }

  public final CDOBranchPoint getBranchPoint()
  {
    if (branchPointComposite != null)
    {
      return branchPointComposite.getBranchPoint();
    }

    return branchPoint;
  }

  public final ComposeBranchPointComposite getBranchPointComposite()
  {
    return branchPointComposite;
  }

  @Override
  protected Point getInitialSize()
  {
    return new Point(450, 450);
  }

  @Override
  protected void createUI(Composite container)
  {
    branchPointComposite = new ComposeBranchPointComposite(container, allowTimeStamp, branchPoint)
    {
      @Override
      protected CDOItemProvider createBranchItemProvider()
      {
        return AbstractBranchPointDialog.this.createBranchItemProvider();
      }

      @Override
      protected Object getBranchViewerInput(CDOBranchPoint branchPoint)
      {
        Object defaultInput = super.getBranchViewerInput(branchPoint);
        return AbstractBranchPointDialog.this.getBranchViewerInput(branchPoint, defaultInput);
      }

      @Override
      protected void timeStampError(String message)
      {
        timeStampError = message;
        validate();
      }

      @Override
      protected void branchPointChanged(CDOBranchPoint branchPoint)
      {
        if (validate())
        {
          AbstractBranchPointDialog.this.branchPointChanged(branchPoint);
        }
      }

      @Override
      protected void doubleClicked()
      {
        AbstractBranchPointDialog.this.doubleClicked();
      }
    };

    branchPointComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
    CDOBranch branch = branchPoint.getBranch();

    TreeViewer branchViewer = branchPointComposite.getBranchViewer();
    branchViewer.setSelection(new StructuredSelection(branch));
    branchViewer.setExpandedState(branch, true);
  }

  protected CDOItemProvider createBranchItemProvider()
  {
    return new CDOItemProvider(null);
  }

  @Override
  protected void doValidate() throws Exception
  {
    if (timeStampError != null)
    {
      throw new Exception(timeStampError);
    }

    super.doValidate();
  }

  protected Object getBranchViewerInput(CDOBranchPoint branchPoint, Object defaultInput)
  {
    return defaultInput;
  }

  protected void branchPointChanged(CDOBranchPoint branchPoint)
  {
    // Do nothing.
  }

  protected void doubleClicked()
  {
    close();
  }

  public static CDOBranchPoint select(Shell shell, boolean allowTimeStamp, CDOBranchPoint branchPoint)
  {
    AbstractBranchPointDialog dialog;
    if (allowTimeStamp)
    {
      dialog = new SelectBranchPointDialog(shell, branchPoint);
    }
    else
    {
      dialog = new SelectBranchDialog(shell, branchPoint);
    }

    if (dialog.open() == AbstractBranchPointDialog.OK)
    {
      return dialog.getBranchPoint();
    }

    return null;
  }
}
