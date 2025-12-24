/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.checkouts.wizards;

import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;

import org.eclipse.net4j.util.collection.Pair;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Eike Stepper
 */
public class LinkedResourceWizard extends Wizard implements INewWizard
{
  private IStructuredSelection selection;

  private LinkedResourceWizardPage page;

  public LinkedResourceWizard()
  {
  }

  @Override
  public void init(IWorkbench workbench, IStructuredSelection selection)
  {
    this.selection = selection;
  }

  @Override
  public void addPages()
  {
    page = new LinkedResourceWizardPage(selection);
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
    Pair<CDOCheckout, CDOResourceNode> info = page.getNodeInfo();
    Job.create("Linked Resource", monitor -> {
      CDOExplorerUtil.createWorkspaceLink(info.getElement1(), info.getElement2().getPath(), page.getTargetContainer(), page.getName(), monitor);
    }).schedule();

    return true;
  }
}
