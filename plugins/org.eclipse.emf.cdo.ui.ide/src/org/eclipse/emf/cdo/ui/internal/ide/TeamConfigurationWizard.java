/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.ui.internal.ide;

import org.eclipse.emf.cdo.internal.team.RepositoryTeamProvider;
import org.eclipse.emf.cdo.ui.internal.ide.bundle.OM;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.team.core.RepositoryProvider;
import org.eclipse.team.ui.IConfigurationWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Eike Stepper
 */
public class TeamConfigurationWizard extends Wizard implements IConfigurationWizard
{
  private IProject project;

  public TeamConfigurationWizard()
  {
  }

  public void init(IWorkbench workbench, IProject project)
  {
    this.project = project;
  }

  @Override
  public boolean performFinish()
  {
    try
    {
      RepositoryProvider.map(project, RepositoryTeamProvider.PROVIDER_ID);
      return true;
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
      return false;
    }
  }
}
