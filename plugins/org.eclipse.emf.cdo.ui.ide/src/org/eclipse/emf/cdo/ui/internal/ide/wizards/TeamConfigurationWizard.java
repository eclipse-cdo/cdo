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
package org.eclipse.emf.cdo.ui.internal.ide.wizards;

import org.eclipse.emf.cdo.internal.team.RepositoryManager;
import org.eclipse.emf.cdo.internal.team.RepositoryTeamProvider;
import org.eclipse.emf.cdo.ui.internal.ide.bundle.OM;
import org.eclipse.emf.cdo.ui.widgets.SessionComposite;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.team.ui.IConfigurationWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Eike Stepper
 */
public class TeamConfigurationWizard extends Wizard implements IConfigurationWizard
{
  private IProject project;

  private Page1 page1;

  public TeamConfigurationWizard()
  {
  }

  public void init(IWorkbench workbench, IProject project)
  {
    this.project = project;
  }

  @Override
  public void addPages()
  {
    addPage(page1 = new Page1());
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

  /**
   * @author Eike Stepper
   */
  private final class Page1 extends WizardPage
  {
    private SessionComposite sessionComposite;

    private Page1()
    {
      super("page1");
    }

    public SessionComposite getSessionComposite()
    {
      return sessionComposite;
    }

    public void createControl(Composite parent)
    {
      sessionComposite = new SessionComposite(parent, SWT.NONE);
      setControl(sessionComposite);
    }
  }
}
