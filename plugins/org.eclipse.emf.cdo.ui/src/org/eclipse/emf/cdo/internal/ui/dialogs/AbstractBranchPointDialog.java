/*
 * Copyright (c) 2012 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.ui.widgets.SelectTimeStampComposite;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.ui.ValidationContext;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Eike Stepper
 */
public abstract class AbstractBranchPointDialog extends TitleAreaDialog
{
  private final boolean allowTimeStamp;

  private CDOBranchPoint branchPoint;

  private TreeViewer branchViewer;

  private SelectTimeStampComposite timeStampComposite;

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
    CDOBranch branch = branchPoint.getBranch();
    CDOItemProvider itemProvider = new CDOItemProvider(null);

    branchViewer = new TreeViewer(container, SWT.BORDER | SWT.SINGLE);
    branchViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
    branchViewer.setLabelProvider(itemProvider);
    branchViewer.setContentProvider(itemProvider);
    branchViewer.setInput(branch.getBranchManager());
    branchViewer.addSelectionChangedListener(new ISelectionChangedListener()
    {
      public void selectionChanged(SelectionChangedEvent event)
      {
        IStructuredSelection selection = (IStructuredSelection)event.getSelection();
        CDOBranch branch = (CDOBranch)selection.getFirstElement();

        if (timeStampComposite != null)
        {
          timeStampComposite.setBranch(branch);
        }

        composeBranchPoint();
        validate();
      }
    });

    branchViewer.addDoubleClickListener(new IDoubleClickListener()
    {
      public void doubleClick(DoubleClickEvent event)
      {
        doubleClicked();
      }
    });

    branchViewer.setSelection(new StructuredSelection(branch));
    branchViewer.setExpandedState(branch, true);

    if (allowTimeStamp)
    {
      Group timeStampGroup = new Group(container, SWT.NONE);
      timeStampGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
      timeStampGroup.setLayout(new GridLayout(1, false));
      timeStampGroup.setText("Time Stamp:");

      timeStampComposite = new SelectTimeStampComposite(timeStampGroup, SWT.NONE, branch, branchPoint.getTimeStamp())
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
        public void setValidationError(Object source, String message)
        {
          timeStampError = message;
          validate();
        }
      });
    }
  }

  protected final void validate()
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
  }

  protected void doValidate() throws Exception
  {
    // Do nothing.
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

    long timeStamp = CDOBranchPoint.UNSPECIFIED_DATE;
    if (timeStampComposite != null)
    {
      timeStamp = timeStampComposite.getTimeStamp();
    }

    branchPoint = branch.getPoint(timeStamp);
    if (!ObjectUtil.equals(branchPoint, oldBranchPoint))
    {
      branchPointChanged(branchPoint);
    }
  }

  protected void branchPointChanged(CDOBranchPoint branchPoint)
  {
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
