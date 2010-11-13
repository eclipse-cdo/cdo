/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.internal.ui.dialogs;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.ui.internal.icons.SharedIcons;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;

import java.util.Date;

/**
 * @author Victor Roldan Betancort
 */
public class BranchSelectionDialog extends TitleAreaDialog
{
  private CDOView view;

  private CDOBranch targetBranch;

  private CDOBranchPoint targetBranchPoint;

  private TreeViewer viewer;

  private Button headRadio;

  private Button baseRadio;

  private Button timeRadio;

  private Text timeText;

  private long selectedTimeStamp;

  private Button browseTimeButton;

  // private Button pointRadio;

  // private Combo pointCombo;

  public BranchSelectionDialog(IWorkbenchPage page, CDOView view)
  {
    super(page.getWorkbenchWindow().getShell());
    this.view = view;
    setShellStyle(getShellStyle() | SWT.APPLICATION_MODAL | SWT.MAX | SWT.TITLE | SWT.RESIZE);
  }

  @Override
  protected Control createDialogArea(Composite parent)
  {
    Composite composite = (Composite)super.createDialogArea(parent);
    composite.setLayoutData(UIUtil.createGridData());
    composite.setLayout(new GridLayout(1, false));
    getShell().setText(Messages.getString("BranchSelectionDialog_0")); //$NON-NLS-1$
    setTitle(Messages.getString("BranchSelectionDialog_1")); //$NON-NLS-1$
    setTitleImage(SharedIcons.getImage(SharedIcons.WIZBAN_TARGET_SELECTION));

    targetBranchPoint = view.getBranch().getPoint(view.getTimeStamp());

    viewer = new TreeViewer(composite, SWT.BORDER);
    viewer.getControl().setLayoutData(UIUtil.createGridData(true, true));
    viewer.setContentProvider(new BranchContentProvider());
    viewer.setLabelProvider(new BranchLabelProvider());
    viewer.setInput(view.getSession().getBranchManager());
    viewer.addSelectionChangedListener(new ISelectionChangedListener()
    {
      public void selectionChanged(SelectionChangedEvent event)
      {
        IStructuredSelection selection = (IStructuredSelection)event.getSelection();
        checkValidSelection(selection);
      }
    });

    Group pointGroup = new Group(composite, SWT.NONE);
    pointGroup.setText(Messages.getString("BranchSelectionDialog.0")); //$NON-NLS-1$
    pointGroup.setLayout(new GridLayout(3, false));
    pointGroup.setLayoutData(UIUtil.createGridData());
    headRadio = new Button(pointGroup, SWT.RADIO);
    headRadio.setText(Messages.getString("BranchSelectionDialog.1")); //$NON-NLS-1$
    new Label(pointGroup, SWT.NONE);
    new Label(pointGroup, SWT.NONE);
    baseRadio = new Button(pointGroup, SWT.RADIO);
    baseRadio.setText(Messages.getString("BranchSelectionDialog.2")); //$NON-NLS-1$
    new Label(pointGroup, SWT.NONE);
    new Label(pointGroup, SWT.NONE);
    timeRadio = new Button(pointGroup, SWT.RADIO);
    timeRadio.setText(Messages.getString("BranchSelectionDialog.3")); //$NON-NLS-1$
    timeText = new Text(pointGroup, SWT.BORDER);
    timeText.setText(new Date(view.getSession().getRepositoryInfo().getCreationTime()).toString());
    timeText.setEditable(false);
    browseTimeButton = new Button(pointGroup, SWT.NONE);
    browseTimeButton.setImage(SharedIcons.getImage(SharedIcons.ETOOL_TIME_PICK_BUTTON_ICON));
    // pointRadio = new Button(pointGroup, SWT.RADIO);
    //    pointRadio.setText(Messages.getString("BranchSelectionDialog.4")); //$NON-NLS-1$
    // pointCombo = new Combo(pointGroup, SWT.NONE);
    // new Label(pointGroup, SWT.NONE);

    // Selection Listener
    SelectionListener selectionListener = new SelectionListener()
    {

      public void widgetSelected(SelectionEvent e)
      {
        checkValidSelection((IStructuredSelection)viewer.getSelection());
      }

      public void widgetDefaultSelected(SelectionEvent e)
      {
        checkValidSelection((IStructuredSelection)viewer.getSelection());
      }
    };
    headRadio.addSelectionListener(selectionListener);
    baseRadio.addSelectionListener(selectionListener);
    timeRadio.addSelectionListener(selectionListener);
    // pointRadio.addSelectionListener(selectionListener);

    timeText.addModifyListener(new ModifyListener()
    {

      public void modifyText(ModifyEvent e)
      {
        if (timeRadio.getSelection())
        {
          updateSelectedPoint();
        }
      }
    });

    // head as default selection
    headRadio.setSelection(true);

    browseTimeButton.addSelectionListener(new SelectionListener()
    {

      public void widgetSelected(SelectionEvent e)
      {
        OpenAuditDialog dialog = new OpenAuditDialog(UIUtil.getActiveWorkbenchPage());
        if (dialog.open() == IDialogConstants.OK_ID)
        {
          timeText.setText(new Date(dialog.getTimeStamp()).toString());
          selectedTimeStamp = dialog.getTimeStamp();
        }
      }

      public void widgetDefaultSelected(SelectionEvent e)
      {
      }
    });

    viewer.expandAll();

    return composite;
  }

  private void checkValidSelection(IStructuredSelection selection)
  {
    CDOBranch branch = (CDOBranch)selection.getFirstElement();
    if (getSelectedPoint(branch).equals(targetBranchPoint))
    {
      BranchSelectionDialog.this.setErrorMessage(Messages.getString("BranchSelectionDialog_2")); //$NON-NLS-1$
      BranchSelectionDialog.this.getButton(IDialogConstants.OK_ID).setEnabled(false);
    }
    else
    {
      if (branch.isMainBranch() && baseRadio.getSelection())
      {
        BranchSelectionDialog.this.setErrorMessage(Messages.getString("BranchSelectionDialog.5")); //$NON-NLS-1$
        BranchSelectionDialog.this.getButton(IDialogConstants.OK_ID).setEnabled(false);
      }
      else
      {
        targetBranch = branch;
        BranchSelectionDialog.this.setErrorMessage(null);
        BranchSelectionDialog.this.getButton(IDialogConstants.OK_ID).setEnabled(true);
      }

    }
  }

  @Override
  protected void okPressed()
  {
    updateSelectedPoint();
    super.okPressed();
  }

  protected void updateSelectedPoint()
  {
    if (targetBranch != null)
    {
      targetBranchPoint = getSelectedPoint(targetBranch);
    }
  }

  protected CDOBranchPoint getSelectedPoint(CDOBranch branch)
  {
    if (headRadio.getSelection())
    {
      return branch.getHead();
    }
    else if (baseRadio.getSelection())
    {
      return branch.getBase();
    }
    else if (timeRadio.getSelection())
    {
      return branch.getPoint(selectedTimeStamp);
    }
    // else if (pointRadio.getSelection())
    // {
    // return branch.getHead();
    // }
    return branch.getHead();
  }

  public CDOBranchPoint getTargetBranchPoint()
  {
    return targetBranchPoint;
  }

  /**
   * @author Victor Roldan Betancort
   */
  public static class BranchLabelProvider extends BaseLabelProvider implements ILabelProvider
  {
    public BranchLabelProvider()
    {
    }

    public Image getImage(Object element)
    {
      return null;
    }

    public String getText(Object element)
    {
      if (element instanceof CDOBranch)
      {
        return ((CDOBranch)element).getName();
      }

      return element.toString();
    }
  }

  /**
   * @author Victor Roldan Betancort
   */
  public static class BranchContentProvider implements ITreeContentProvider
  {
    private static final Object[] NO_ELEMENTS = {};

    private CDOBranchManager branchManager;

    public BranchContentProvider()
    {
    }

    public void dispose()
    {
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
      if (newInput instanceof CDOBranchManager)
      {
        branchManager = (CDOBranchManager)newInput;
      }
    }

    public Object[] getElements(Object inputElement)
    {
      if (inputElement == branchManager)
      {
        return new Object[] { branchManager.getMainBranch() };
      }

      if (inputElement instanceof CDOBranch)
      {
        return ((CDOBranch)inputElement).getBranches();
      }

      return NO_ELEMENTS;
    }

    public Object[] getChildren(Object parentElement)
    {
      if (parentElement == branchManager)
      {
        return new Object[] { branchManager.getMainBranch() };
      }

      if (parentElement instanceof CDOBranch)
      {
        return ((CDOBranch)parentElement).getBranches();
      }

      return NO_ELEMENTS;
    }

    public Object getParent(Object element)
    {
      if (element == branchManager)
      {
        return null;
      }

      if (element instanceof CDOBranch)
      {
        return ((CDOBranch)element).getBase();
      }

      return NO_ELEMENTS;
    }

    public boolean hasChildren(Object element)
    {
      return getChildren(element).length != 0;
    }
  }
}
