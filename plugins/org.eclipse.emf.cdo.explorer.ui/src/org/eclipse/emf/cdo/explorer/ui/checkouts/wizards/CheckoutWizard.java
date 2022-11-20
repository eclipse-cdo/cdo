/*
 * Copyright (c) 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckoutManager;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.explorer.repositories.CDORepositoryElement;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.explorer.ui.checkouts.CDOCheckoutContentProvider;

import org.eclipse.net4j.util.AdapterUtil;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IPageChangeProvider;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.IViewDescriptor;

import java.util.Properties;

/**
 * @author Eike Stepper
 */
public class CheckoutWizard extends Wizard implements IImportWizard, INewWizard
{
  private final IPageChangedListener pageChangedListener = new IPageChangedListener()
  {
    @Override
    public void pageChanged(PageChangedEvent event)
    {
      Object page = event.getSelectedPage();
      if (page instanceof CheckoutWizardPage)
      {
        CheckoutWizardPage checkoutWizardPage = (CheckoutWizardPage)page;
        checkoutWizardPage.pageActivated();
      }
    }
  };

  private IPageChangingListener pageChangingListener;

  private CDORepositoryElement selectedElement;

  private CheckoutRepositoryPage repositoryPage;

  private CheckoutTypePage typePage;

  private CheckoutBranchPointPage branchPointPage;

  private CheckoutRootObjectPage rootObjectPage;

  private CheckoutLabelPage labelPage;

  public CheckoutWizard()
  {
    setNeedsProgressMonitor(true);
    setWindowTitle("New Checkout");
  }

  public final CheckoutRepositoryPage getRepositoryPage()
  {
    return repositoryPage;
  }

  public final CheckoutTypePage getTypePage()
  {
    return typePage;
  }

  public final CheckoutBranchPointPage getBranchPointPage()
  {
    return branchPointPage;
  }

  public final CheckoutRootObjectPage getRootObjectPage()
  {
    return rootObjectPage;
  }

  public final CheckoutLabelPage getLabelPage()
  {
    return labelPage;
  }

  @Override
  public void init(IWorkbench workbench, IStructuredSelection selection)
  {
    if (selection.size() == 1)
    {
      Object element = selection.getFirstElement();
      selectedElement = AdapterUtil.adapt(element, CDORepositoryElement.class);
    }
  }

  @Override
  public void setContainer(IWizardContainer wizardContainer)
  {
    if (getContainer() instanceof IPageChangeProvider)
    {
      ((IPageChangeProvider)getContainer()).removePageChangedListener(pageChangedListener);
    }

    if (getContainer() instanceof WizardDialog && pageChangingListener != null)
    {
      ((WizardDialog)getContainer()).removePageChangingListener(pageChangingListener);
      pageChangingListener = null;
    }

    super.setContainer(wizardContainer);

    if (getContainer() instanceof WizardDialog)
    {
      pageChangingListener = new IPageChangingListener()
      {
        @Override
        public void handlePageChanging(PageChangingEvent event)
        {
          Object page = event.getCurrentPage();
          if (page instanceof CheckoutWizardPage)
          {
            CheckoutWizardPage checkoutWizardPage = (CheckoutWizardPage)page;
            event.doit = checkoutWizardPage.pageAboutToDeactivate(event.getTargetPage());
          }
        }
      };

      ((WizardDialog)getContainer()).addPageChangingListener(pageChangingListener);
    }

    if (getContainer() instanceof WizardDialog)
    {
      ((WizardDialog)getContainer()).addPageChangedListener(pageChangedListener);
    }
  }

  @Override
  public void addPages()
  {
    addPage(repositoryPage = new CheckoutRepositoryPage());
    addPage(typePage = new CheckoutTypePage());
    addPage(branchPointPage = new CheckoutBranchPointPage());
    addPage(rootObjectPage = new CheckoutRootObjectPage());
    addPage(labelPage = new CheckoutLabelPage());

    if (selectedElement != null)
    {
      CDORepository repository = selectedElement.getRepository();
      if (repository != null)
      {
        repositoryPage.setRepository(repository);
        repositoryPage.skip();

        int branchID = selectedElement.getBranchID();
        long timeStamp = selectedElement.getTimeStamp();

        if (timeStamp != CDOBranchPoint.UNSPECIFIED_DATE)
        {
          typePage.setType(CDOCheckout.TYPE_ONLINE_HISTORICAL);
        }

        branchPointPage.setBranchPoint(branchID, timeStamp);
      }
    }
  }

  @Override
  public boolean performFinish()
  {
    final Properties properties = new Properties();
    repositoryPage.fillProperties(properties);
    typePage.fillProperties(properties);
    branchPointPage.fillProperties(properties);
    rootObjectPage.fillProperties(properties);
    labelPage.fillProperties(properties);

    new Job("Checkout")
    {
      @Override
      protected IStatus run(IProgressMonitor monitor)
      {
        try
        {
          CDOCheckoutManager checkoutManager = CDOExplorerUtil.getCheckoutManager();
          CDOCheckout checkout = checkoutManager.addCheckout(properties);
          checkout.open();

          showInProjectExplorer(checkout);
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);

          final IStatus status = new Status(IStatus.ERROR, OM.BUNDLE_ID, ex.getMessage(), ex);
          UIUtil.getDisplay().asyncExec(new Runnable()
          {
            @Override
            public void run()
            {
              ErrorDialog.openError(getShell(), "Error", "An error occured while creating the checkout.", status);
            }
          });

          return Status.OK_STATUS;
        }
        finally
        {
        }

        return Status.OK_STATUS;
      }
    }.schedule();

    return true;
  }

  public static void showInProjectExplorer(final Object... objects)
  {
    UIUtil.getDisplay().asyncExec(new Runnable()
    {
      @Override
      public void run()
      {
        IWorkbench workbench = PlatformUI.getWorkbench();

        IViewDescriptor viewDescriptor = workbench.getViewRegistry().find(CDOCheckoutContentProvider.PROJECT_EXPLORER_ID);
        if (viewDescriptor != null)
        {
          IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
          if (window != null)
          {
            IWorkbenchPage page = window.getActivePage();
            if (page != null)
            {
              try
              {
                page.showView(viewDescriptor.getId());

                CDOCheckoutContentProvider checkoutContentProvider = CDOCheckoutContentProvider.getInstance(CDOCheckoutContentProvider.PROJECT_EXPLORER_ID);
                if (checkoutContentProvider != null)
                {
                  checkoutContentProvider.selectObjects(objects);
                }
              }
              catch (Exception ex)
              {
                OM.LOG.error(ex);
              }
            }
          }
        }
      }
    });
  }
}
