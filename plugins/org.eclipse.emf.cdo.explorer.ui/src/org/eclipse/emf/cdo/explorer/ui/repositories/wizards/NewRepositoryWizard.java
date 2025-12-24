/*
 * Copyright (c) 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.repositories.wizards;

import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.explorer.repositories.CDORepositoryManager;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;

import org.eclipse.net4j.util.security.IPasswordCredentials;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import java.util.Properties;

/**
 * @author Eike Stepper
 */
public class NewRepositoryWizard extends Wizard
{
  private RepositoryTypePage typePage;

  private RepositoryRemotePage remotePage;

  private RepositoryClonePage clonePage;

  private RepositoryLocalPage localPage;

  public NewRepositoryWizard()
  {
    setWindowTitle("New Repository");
    setNeedsProgressMonitor(false);
  }

  public final RepositoryTypePage getTypePage()
  {
    return typePage;
  }

  public final RepositoryRemotePage getRemotePage()
  {
    return remotePage;
  }

  public final RepositoryClonePage getClonePage()
  {
    return clonePage;
  }

  public final RepositoryLocalPage getLocalPage()
  {
    return localPage;
  }

  @Override
  public void addPages()
  {
    addPage(typePage = new RepositoryTypePage());
    addPage(remotePage = new RepositoryRemotePage());
    addPage(clonePage = new RepositoryClonePage());
    addPage(localPage = new RepositoryLocalPage());
  }

  @Override
  public boolean canFinish()
  {
    IWizardPage currentPage = getContainer().getCurrentPage();
    return currentPage != typePage && currentPage.isPageComplete();
  }

  @Override
  public boolean performFinish()
  {
    AbstractRepositoryPage page = typePage.getNextPage();
    Properties properties = page.getProperties();
    IPasswordCredentials credentials = page.getCredentials();

    CDORepositoryManager repositoryManager = CDOExplorerUtil.getRepositoryManager();
    final CDORepository repository = repositoryManager.addRepository(properties, credentials);

    new Job("Connect")
    {
      @Override
      protected IStatus run(IProgressMonitor monitor)
      {
        try
        {
          repository.connect();
          return Status.OK_STATUS;
        }
        catch (Exception ex)
        {
          return OM.BUNDLE.getStatus(ex);
        }
      }
    }.schedule();

    return true;
  }
}
