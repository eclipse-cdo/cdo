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
import org.eclipse.emf.cdo.ui.widgets.ComposeBranchPointComposite;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Eike Stepper
 */
public abstract class AbstractBranchPointDialog extends TitleAreaDialog
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

    setShellStyle(SWT.CLOSE | SWT.RESIZE | SWT.TITLE | SWT.APPLICATION_MODAL);
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

  @Override
  protected Point getInitialSize()
  {
    return new Point(450, 450);
  }

  @Override
  protected Control createDialogArea(Composite parent)
  {
    Composite area = (Composite)super.createDialogArea(parent);

    GridLayout containerGridLayout = new GridLayout(2, false);
    containerGridLayout.marginWidth = 10;
    containerGridLayout.marginHeight = 10;
    containerGridLayout.verticalSpacing = 10;

    Composite container = new Composite(area, SWT.NONE);
    container.setLayoutData(new GridData(GridData.FILL_BOTH));
    container.setLayout(containerGridLayout);

    createUI(container);

    validate();
    return area;
  }

  protected void createUI(Composite container)
  {
    branchPointComposite = new ComposeBranchPointComposite(container, allowTimeStamp, branchPoint)
    {
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

  protected final boolean validate()
  {
    String error = timeStampError;

    if (error == null)
    {
      try
      {
        doValidate();
      }
      catch (Exception ex)
      {
        error = ex.getMessage();
      }
    }

    setErrorMessage(error);

    Button button = getButton(IDialogConstants.OK_ID);
    if (button != null)
    {
      button.setEnabled(error == null);
    }

    return error == null;
  }

  protected void doValidate() throws Exception
  {
    // Do nothing.
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
