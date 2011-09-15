/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.ui.internal.ide.wizards;

import org.eclipse.emf.cdo.internal.team.RepositoryManager;
import org.eclipse.emf.cdo.internal.team.RepositoryTeamProvider;
import org.eclipse.emf.cdo.ui.internal.ide.bundle.OM;
import org.eclipse.emf.cdo.ui.internal.ide.messages.Messages;
import org.eclipse.emf.cdo.ui.widgets.SessionComposite;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.team.ui.IConfigurationWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Eike Stepper
 */
public class TeamConfigurationWizard extends Wizard implements IConfigurationWizard
{
  private IProject project;

  private CDOShareProjectWizardPage page1;

  public TeamConfigurationWizard()
  {
  }

  public void init(IWorkbench workbench, IProject project)
  {
    this.project = project;
    setWindowTitle(Messages.getString("TeamConfigurationWizard_1")); //$NON-NLS-1$
    ImageDescriptor desc = OM.getImageDescriptor("icons/full/wizban/wizard_icon.gif");//$NON-NLS-1$
    setDefaultPageImageDescriptor(desc);
  }

  @Override
  public void addPages()
  {
    page1 = new CDOShareProjectWizardPage("page1"); //$NON-NLS-1$
    page1.setTitle(Messages.getString("TeamConfigurationWizard_2")); //$NON-NLS-1$    
    addPage(page1);
  }

  @Override
  public boolean performFinish()
  {
    try
    {
      SessionComposite sessionComposite = page1.getSessionComposite();
      String sessionDescription = sessionComposite.getSessionDescription();
      sessionComposite.rememberSettings();

      RepositoryTeamProvider.mapProject(project, sessionDescription);
      RepositoryManager.INSTANCE.addElement(project);
      return true;
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
      return false;
    }
  }
}
