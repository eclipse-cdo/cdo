/*
 * Copyright (c) 2015, 2016, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.checkouts.wizards;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.explorer.ui.checkouts.CDOCheckoutContentProvider;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.InternalEList;

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
public abstract class NewWizard extends Wizard implements INewWizard
{
  private final String resourceType;

  private final String title;

  private IStructuredSelection selection;

  private CDOCheckoutContentProvider contentProvider;

  private NewWizardPage page;

  protected NewWizard(String resourceType, String title)
  {
    this.resourceType = resourceType;
    this.title = title;
  }

  public void setContentProvider(CDOCheckoutContentProvider contentProvider)
  {
    this.contentProvider = contentProvider;
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
  public void init(IWorkbench workbench, IStructuredSelection selection)
  {
    this.selection = selection;
  }

  @Override
  public void addPages()
  {
    page = new NewWizardPage(resourceType, title, selection);
    addPage(page);
  }

  @Override
  public boolean canFinish()
  {
    return page.getNodeInfo() != null && super.canFinish();
  }

  @Override
  public boolean performFinish()
  {
    Pair<CDOCheckout, CDOResourceNode> parentInfo = page.getNodeInfo();
    String name = page.getName();
    String error = "An error occured while creating the " + title.toLowerCase() + ".";

    Job.create(title, monitor -> {
      CDOResourceNode newResourceNode = createNewResourceNode();
      newResourceNode.setName(name);

      CDOCheckout checkout = parentInfo.getElement1();
      CDOResourceNode parentNode = parentInfo.getElement2();

      CDOTransaction transaction = checkout.openTransaction();
      CDOCommitInfo commitInfo = null;
      CDOID newID = null;

      try
      {
        CDOResourceNode txParent = transaction.getObject(parentNode);
        if (txParent instanceof CDOResourceFolder)
        {
          InternalEList<CDOResourceNode> nodes = (InternalEList<CDOResourceNode>)((CDOResourceFolder)txParent).getNodes();
          nodes.addUnique(newResourceNode);
        }
        else
        {
          InternalEList<EObject> contents = (InternalEList<EObject>)transaction.getRootResource().getContents();
          contents.addUnique(newResourceNode);
        }

        commitInfo = transaction.commit();
        newID = newResourceNode.cdoID();
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);

        IStatus status = new Status(IStatus.ERROR, OM.BUNDLE_ID, ex.getMessage(), ex);
        UIUtil.asyncExec(() -> ErrorDialog.openError(getShell(), "Error", error, status));

        return Status.OK_STATUS;
      }
      finally
      {
        transaction.close();
      }

      if (commitInfo != null)
      {
        CDOView view = checkout.getView();
        if (!view.waitForUpdate(commitInfo.getTimeStamp(), 10000))
        {
          OM.LOG.error(error + " Did not receive an update");
          return Status.OK_STATUS;
        }

        CDOObject newObject = view.getObject(newID);

        if (contentProvider != null)
        {
          contentProvider.selectObjects(newObject);
        }
        else
        {
          CDOCheckoutContentProvider.forEachInstance(cp -> cp.selectObjects(newObject));
        }
      }

      return Status.OK_STATUS;
    }).schedule();

    return true;
  }

  protected abstract CDOResourceNode createNewResourceNode();
}
