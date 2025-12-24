/*
 * Copyright (c) 2013, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.ui.internal.admin.actions;

import org.eclipse.emf.cdo.admin.CDOAdminClient;
import org.eclipse.emf.cdo.common.admin.CDOAdmin;
import org.eclipse.emf.cdo.ui.internal.admin.bundle.OM;
import org.eclipse.emf.cdo.ui.internal.admin.messages.Messages;
import org.eclipse.emf.cdo.ui.internal.admin.wizards.CreateRepositoryWizard;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;

import java.util.Map;

/**
 * @author Christian W. Damus (CEA LIST)
 */
public class CreateRepositoryAction extends AdminAction<CDOAdminClient>
{
  private String repositoryName;

  private Map<String, Object> repositoryProperties;

  public CreateRepositoryAction(CDOAdminClient admin)
  {
    super(Messages.CreateRepositoryAction_0, Messages.CreateRepositoryAction_1, OM.getImageDescriptor("icons/full/ctool16/create_repo.gif"), admin); //$NON-NLS-1$
  }

  @Override
  protected void preRun() throws Exception
  {
    CreateRepositoryWizard wizard = new CreateRepositoryWizard();
    WizardDialog wizardDialog = new WizardDialog(getShell(), wizard);
    if (wizardDialog.open() == Window.OK)
    {
      repositoryName = wizard.getRepositoryName();
      repositoryProperties = wizard.getRepositoryProperties();
    }
    else
    {
      cancel();
    }
  }

  @Override
  protected void safeRun(IProgressMonitor progressMonitor) throws Exception
  {
    try
    {
      target.createRepository(repositoryName, CDOAdmin.DEFAULT_TYPE, repositoryProperties);
    }
    finally
    {
      repositoryName = null;
      repositoryProperties = null;
    }
  }

  @Override
  protected String getErrorPattern()
  {
    return Messages.CreateRepositoryAction_2;
  }
}
