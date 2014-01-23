/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.internal.setup.targlets;

import org.eclipse.emf.cdo.releng.internal.setup.Activator;
import org.eclipse.emf.cdo.releng.internal.setup.ui.ErrorDialog;
import org.eclipse.emf.cdo.releng.setup.InstallableUnit;
import org.eclipse.emf.cdo.releng.setup.P2Repository;
import org.eclipse.emf.cdo.releng.setup.RepositoryList;
import org.eclipse.emf.cdo.releng.setup.SetupFactory;
import org.eclipse.emf.cdo.releng.setup.Targlet;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.pde.core.target.ITargetDefinition;
import org.eclipse.pde.core.target.ITargetHandle;
import org.eclipse.pde.core.target.ITargetLocation;
import org.eclipse.pde.core.target.ITargetPlatformService;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.pde.internal.core.target.TargetPlatformService;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * @author Eike Stepper
 */
@SuppressWarnings("restriction")
public class TestAction implements IWorkbenchWindowActionDelegate
{
  public TestAction()
  {
  }

  public void init(IWorkbenchWindow window)
  {
  }

  public void dispose()
  {
  }

  public void selectionChanged(IAction action, ISelection selection)
  {
  }

  public void run(IAction action)
  {
    try
    {
      initTargetPlatform();
    }
    catch (Throwable ex)
    {
      Activator.log(ex);
      ErrorDialog.open(ex);
    }
  }

  private static void initTargetPlatform() throws Exception
  {
    RepositoryList repositoryList = SetupFactory.eINSTANCE.createRepositoryList();
    repositoryList.setName("Luna");
    repositoryList.getP2Repositories().add(repository("http://download.eclipse.org/releases/luna"));
    // repositoryList.getP2Repositories().add(repository("http://download.eclipse.org/eclipse/updates/4.4milestones"));

    Targlet targlet = SetupFactory.eINSTANCE.createTarglet();
    targlet.setName("EMF");
    // targlet.getRoots().add(component("org.eclipse.emf.ecore"));
    targlet.getRoots().add(component("org.eclipse.emf.ecore.feature.group"));
    // targlet.getRoots().add(component("org.eclipse.platform.ide.feature.group"));
    targlet.getRepositoryLists().add(repositoryList);
    targlet.setActiveRepositoryList(repositoryList.getName());

    TargletBundleContainer container = new TargletBundleContainer();
    container.addTarglet(targlet);
    ITargetLocation[] locations = { container };

    TargetPlatformService targetService = (TargetPlatformService)PDECore.getDefault().acquireService(
        ITargetPlatformService.class.getName());

    for (ITargetHandle handle : targetService.getTargets(new NullProgressMonitor()))
    {
      targetService.deleteTarget(handle);
    }

    ITargetDefinition target = targetService.newTarget();
    target.setName("Modular Target " + System.currentTimeMillis());
    target.setTargetLocations(locations);
    targetService.saveTargetDefinition(target);
  }

  private static InstallableUnit component(String id)
  {
    InstallableUnit installableUnit = SetupFactory.eINSTANCE.createInstallableUnit();
    installableUnit.setID(id);
    return installableUnit;
  }

  private static P2Repository repository(String url)
  {
    P2Repository p2Repository = SetupFactory.eINSTANCE.createP2Repository();
    p2Repository.setURL(url);
    return p2Repository;
  }
}
