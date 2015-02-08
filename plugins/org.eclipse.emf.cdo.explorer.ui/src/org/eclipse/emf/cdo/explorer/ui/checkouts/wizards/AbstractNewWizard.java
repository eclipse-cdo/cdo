/*
 * Copyright (c) 2009-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.checkouts.wizards;

import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Eike Stepper
 */
public abstract class AbstractNewWizard extends Wizard implements INewWizard
{
  private final String resourceType;

  private final String title;

  private IStructuredSelection selection;

  private NewWizardPage page;

  protected AbstractNewWizard(String resourceType, String title)
  {
    this.resourceType = resourceType;
    this.title = title;
  }

  public void init(IWorkbench workbench, IStructuredSelection selection)
  {
    this.selection = selection;
  }

  public final String getResourceType()
  {
    return resourceType;
  }

  public final String getTitle()
  {
    return title;
  }

  @Override
  public void addPages()
  {
    page = new NewWizardPage(resourceType, title, selection);
    addPage(page);
  }

  @Override
  public boolean performFinish()
  {
    final Object parent = page.getParent();
    final String name = page.getName();

    new Job(title)
    {
      @Override
      protected IStatus run(IProgressMonitor monitor)
      {
        CDOResourceNode newResourceNode = createNewResourceNode();
        newResourceNode.setName(name);

        CDOResourceNode parentResourceNode;
        if (parent instanceof CDOCheckout)
        {
          CDOCheckout checkout = (CDOCheckout)parent;
          parentResourceNode = (CDOResourceNode)checkout.getRootObject();
        }
        else
        {
          parentResourceNode = (CDOResourceNode)parent;
        }

        CDOView view = parentResourceNode.cdoView();
        CDOTransaction transaction = view.getSession().openTransaction(view.getBranch());

        CDOResourceNode txParent = transaction.getObject(parentResourceNode);
        if (txParent instanceof CDOResourceFolder)
        {
          ((CDOResourceFolder)txParent).getNodes().add(newResourceNode);
        }
        else
        {
          transaction.getRootResource().getContents().add(newResourceNode);
        }

        try
        {
          transaction.commit();
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);

          IStatus status = new Status(IStatus.ERROR, OM.BUNDLE_ID, ex.getMessage(), ex);
          ErrorDialog.openError(getShell(), "Error",
              "An error occured while creating the " + title.toLowerCase() + ".", status);
        }

        return Status.OK_STATUS;
      }
    }.schedule();

    return true;
  }

  protected abstract CDOResourceNode createNewResourceNode();
}
