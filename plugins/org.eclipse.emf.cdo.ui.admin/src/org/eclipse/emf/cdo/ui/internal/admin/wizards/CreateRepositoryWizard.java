/*
 * Copyright (c) 2013, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.ui.internal.admin.wizards;

import org.eclipse.emf.cdo.ui.internal.admin.StoreType;
import org.eclipse.emf.cdo.ui.internal.admin.bundle.OM;
import org.eclipse.emf.cdo.ui.internal.admin.messages.Messages;
import org.eclipse.emf.cdo.ui.internal.admin.wizards.AbstractCreateRepositoryWizardPage.Whiteboard;

import org.eclipse.jface.wizard.Wizard;

import java.util.Map;

/**
 * @author Christian W. Damus (CEA LIST)
 */
public class CreateRepositoryWizard extends Wizard
{
  private CreateRepositoryGeneralPage generalPage;

  private CreateRepositoryStorePage storePage;

  private String repositoryName;

  private Map<String, Object> repositoryProperties;

  public CreateRepositoryWizard()
  {
    setWindowTitle(Messages.CreateRepositoryWizard_0);
    setDefaultPageImageDescriptor(OM.getImageDescriptor("icons/full/wizban/new_repo.png")); //$NON-NLS-1$
    setDialogSettings(OM.Activator.INSTANCE.getDialogSettings(getClass()));
    setHelpAvailable(false);
  }

  @Override
  public void addPages()
  {
    super.addPages();
    final Whiteboard whiteboard = new Whiteboard();

    generalPage = new CreateRepositoryGeneralPage("general", StoreType.getInstances()); //$NON-NLS-1$
    generalPage.bind(whiteboard);
    addPage(generalPage);

    storePage = new CreateRepositoryStorePage("store"); //$NON-NLS-1$
    storePage.bind(whiteboard);
    addPage(storePage);
  }

  @Override
  public boolean performFinish()
  {
    Map<String, Object> repositoryProperties = new java.util.HashMap<>();
    boolean result = generalPage.performFinish(repositoryProperties);
    result = result && storePage.performFinish(repositoryProperties);

    if (result)
    {
      repositoryName = (String)repositoryProperties.remove(CreateRepositoryGeneralPage.PROPERTY_NAME);
      this.repositoryProperties = repositoryProperties;
    }

    return result;
  }

  public String getRepositoryName()
  {
    return repositoryName;
  }

  public Map<String, Object> getRepositoryProperties()
  {
    return repositoryProperties;
  }
}
