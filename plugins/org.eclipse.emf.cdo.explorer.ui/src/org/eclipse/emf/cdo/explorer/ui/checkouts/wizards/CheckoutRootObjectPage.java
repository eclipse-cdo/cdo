/*
 * Copyright (c) 2004-2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.checkouts.wizards;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.explorer.ui.checkouts.CDOCheckoutViewerSorter;
import org.eclipse.emf.cdo.internal.explorer.checkouts.CDOCheckoutImpl;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.ui.CDOItemProvider;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import java.util.Properties;

/**
 * @author Eike Stepper
 */
public class CheckoutRootObjectPage extends CheckoutWizardPage
{
  private CDOID rootID;

  private TreeViewer objectViewer;

  private CDOView view;

  private CDORepository repository;

  private CDOBranchPoint branchPoint;

  public CheckoutRootObjectPage()
  {
    super("Root Object", "Select the root object of the new checkout.");
  }

  public final CDOID getRootID()
  {
    return rootID;
  }

  public final void setRootID(CDOID rootID)
  {
    if (this.rootID != rootID)
    {
      log("Setting root id to " + rootID);
      this.rootID = rootID;
      rootObjectChanged(rootID);
    }
  }

  @Override
  public void dispose()
  {
    closeView();
    super.dispose();
  }

  @Override
  protected void createUI(Composite parent)
  {
    // TODO This is not lazy, async:
    CDOItemProvider itemProvider = new CDOItemProvider(null)
    {
      @Override
      public void fillContextMenu(IMenuManager manager, ITreeSelection selection)
      {
        // Do nothing.
      }
    };

    objectViewer = new TreeViewer(parent, SWT.BORDER);
    objectViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
    objectViewer.setContentProvider(itemProvider);
    objectViewer.setLabelProvider(itemProvider);
    objectViewer.setSorter(new CDOCheckoutViewerSorter());
    objectViewer.addSelectionChangedListener(new ISelectionChangedListener()
    {
      public void selectionChanged(SelectionChangedEvent event)
      {
        validate();
      }
    });
  }

  @Override
  protected boolean doValidate() throws ValidationProblem
  {
    return true;
  }

  @Override
  protected void repositoryChanged(CDORepository repository)
  {
    this.repository = repository;
    closeView();
    super.repositoryChanged(repository);
  }

  @Override
  protected void branchPointChanged(CDOBranchPoint branchPoint)
  {
    this.branchPoint = branchPoint;
    closeView();
    super.branchPointChanged(branchPoint);
  }

  @Override
  protected void pageActivated()
  {
    CDOSession session = repository.getSession();
    view = session.openView(branchPoint);
    objectViewer.setInput(view);
  }

  @Override
  protected void fillProperties(Properties properties)
  {
    properties.setProperty("rootID", CDOCheckoutImpl.getCDOIDString(rootID));
  }

  private void closeView()
  {
    if (view != null)
    {
      if (objectViewer != null)
      {
        objectViewer.setSelection(StructuredSelection.EMPTY);
        objectViewer.setInput(null);
      }

      view.close();
      view = null;
    }
  }
}
