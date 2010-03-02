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
import org.eclipse.emf.cdo.internal.ui.SharedIcons;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Victor Roldan Betancort
 */
public class BranchSelectionDialog extends TitleAreaDialog
{
  private CDOView view;

  private CDOBranch targetBranch;

  private TreeViewer viewer;

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
        if (selection.getFirstElement().equals(view.getBranch()))
        {
          BranchSelectionDialog.this.setErrorMessage(Messages.getString("BranchSelectionDialog_2")); //$NON-NLS-1$
          BranchSelectionDialog.this.getButton(IDialogConstants.OK_ID).setEnabled(false);
        }
        else
        {
          BranchSelectionDialog.this.setErrorMessage(null);
          BranchSelectionDialog.this.getButton(IDialogConstants.OK_ID).setEnabled(true);
        }
      }
    });

    return composite;
  }

  @Override
  protected void okPressed()
  {
    IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
    Object obj = selection.getFirstElement();

    if (obj instanceof CDOBranch)
    {
      targetBranch = (CDOBranch)obj;
    }

    super.okPressed();
  }

  public CDOBranch getTargetBranch()
  {
    return targetBranch;
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
