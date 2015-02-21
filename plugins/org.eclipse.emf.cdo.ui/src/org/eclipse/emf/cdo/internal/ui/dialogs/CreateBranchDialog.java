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
import org.eclipse.emf.cdo.ui.shared.SharedIcons;
import org.eclipse.emf.cdo.ui.widgets.SelectTimeStampComposite;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.ui.ValidationContext;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Eike Stepper
 */
public class CreateBranchDialog extends TitleAreaDialog
{
  public static final String TITLE = "New Branch";

  private CDOBranchPoint base;

  private String name;

  private TreeViewer branchViewer;

  private SelectTimeStampComposite timeStampComposite;

  private String timeStampError;

  private Text nameText;

  public CreateBranchDialog(Shell parentShell, CDOBranchPoint base, String defaultName)
  {
    super(parentShell);
    this.base = base;
    name = StringUtil.safe(defaultName);

    setShellStyle(SWT.CLOSE | SWT.RESIZE | SWT.TITLE | SWT.APPLICATION_MODAL);
  }

  public CDOBranchPoint getBase()
  {
    return base;
  }

  public String getName()
  {
    return name;
  }

  @Override
  protected Point getInitialSize()
  {
    return new Point(450, 450);
  }

  @Override
  protected Control createDialogArea(Composite parent)
  {
    getShell().setText(TITLE);
    setTitle(TITLE);
    setTitleImage(SharedIcons.getImage(SharedIcons.WIZBAN_TARGET_SELECTION));
    setMessage("Select a base point and enter the name of the new branch.");

    Composite area = (Composite)super.createDialogArea(parent);

    GridLayout containerGridLayout = new GridLayout(2, false);
    containerGridLayout.marginWidth = 10;
    containerGridLayout.marginHeight = 10;
    containerGridLayout.verticalSpacing = 10;

    Composite container = new Composite(area, SWT.NONE);
    container.setLayoutData(new GridData(GridData.FILL_BOTH));
    container.setLayout(containerGridLayout);

    CDOBranch branch = base.getBranch();
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

        composeBase();
        validate();
      }
    });

    branchViewer.setSelection(new StructuredSelection(branch));
    branchViewer.setExpandedState(branch, true);

    Group timeStampGroup = new Group(container, SWT.NONE);
    timeStampGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
    timeStampGroup.setLayout(new GridLayout(1, false));
    timeStampGroup.setText("Time Stamp:");

    timeStampComposite = new SelectTimeStampComposite(timeStampGroup, SWT.NONE, branch, base.getTimeStamp())
    {
      @Override
      protected void timeStampChanged(long timeStamp)
      {
        composeBase();
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

    Label newLabel = new Label(container, SWT.NONE);
    newLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
    newLabel.setText("Name:");

    nameText = new Text(container, SWT.BORDER);
    nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    nameText.setText(name);
    nameText.selectAll();
    nameText.setFocus();
    nameText.addModifyListener(new ModifyListener()
    {
      public void modifyText(ModifyEvent e)
      {
        name = nameText.getText();
        validate();
      }
    });

    validate();
    return area;
  }

  protected void validate()
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

  private void doValidate() throws Exception
  {
    if (name.length() == 0)
    {
      throw new Exception("Name is empty.");
    }

    if (name.contains("/") || name.contains("\\"))
    {
      throw new Exception("Name constains a path separator.");
    }

    CDOBranch baseBranch = base.getBranch();
    CDOBranch branch = baseBranch.getBranch(name);
    if (branch != null)
    {
      throw new Exception("Name is not unique within " + baseBranch.getPathName() + ".");
    }
  }

  protected void composeBase()
  {
    if (branchViewer == null || timeStampComposite == null)
    {
      return;
    }

    CDOBranchPoint oldBase = base;

    IStructuredSelection selection = (IStructuredSelection)branchViewer.getSelection();
    CDOBranch branch = (CDOBranch)selection.getFirstElement();

    long timeStamp = timeStampComposite.getTimeStamp();

    base = branch.getPoint(timeStamp);
    if (!ObjectUtil.equals(base, oldBase))
    {
      baseChanged(base);
    }
  }

  protected void baseChanged(CDOBranchPoint base)
  {
  }
}
