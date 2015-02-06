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
package org.eclipse.emf.cdo.explorer.ui.repositories.wizards;

import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.repositories.CDORepositoryManager;

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

    CDORepositoryManager repositoryManager = CDOExplorerUtil.getRepositoryManager();
    repositoryManager.addRepository(properties);

    return true;
  }
}
