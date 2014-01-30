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
import org.eclipse.emf.cdo.releng.internal.setup.targlets.IUGenerator.FeatureIUGenerator;
import org.eclipse.emf.cdo.releng.internal.setup.ui.ErrorDialog;
import org.eclipse.emf.cdo.releng.setup.AutomaticSourceLocator;
import org.eclipse.emf.cdo.releng.setup.InstallableUnit;
import org.eclipse.emf.cdo.releng.setup.P2Repository;
import org.eclipse.emf.cdo.releng.setup.RepositoryList;
import org.eclipse.emf.cdo.releng.setup.SetupFactory;
import org.eclipse.emf.cdo.releng.setup.Targlet;

import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.pde.core.target.ITargetDefinition;
import org.eclipse.pde.core.target.ITargetLocation;
import org.eclipse.pde.core.target.ITargetPlatformService;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import java.io.File;

/**
 * @author Eike Stepper
 */
@SuppressWarnings("unused")
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
      // generateFeatureIU();
      initTargetPlatform();
    }
    catch (Throwable ex)
    {
      Activator.log(ex);
      ErrorDialog.open(ex);
    }
  }

  private static void generateFeatureIU() throws Exception
  {
    IUGenerator generator = FeatureIUGenerator.INSTANCE;

    System.out.println(generator.generateIU(
        new File("C:/develop/cdo/master/git/cdo/features/org.eclipse.emf.cdo.site-feature")).getRequirements());

    System.out.println(generator.generateIU(
        new File("C:/develop/cdo/master/git/cdo/features/org.eclipse.emf.cdo.sdk-feature")).getRequirements());

    System.out.println(generator.generateIU(
        new File("C:/develop/cdo/master/git/cdo/features/org.eclipse.emf.cdo-feature")).getRequirements());
  }

  private static void initTargetPlatform() throws Exception
  {
    RepositoryList repositoryList = SetupFactory.eINSTANCE.createRepositoryList();
    repositoryList.setName("Milestones");
    EList<P2Repository> repos = repositoryList.getP2Repositories();
    // repos.add(repository("http://download.eclipse.org/eclipse/updates/4.4milestones"));
    repos.add(repository("http://download.eclipse.org/tools/orbit/downloads/drops/S20140116105218/repository"));
    repos.add(repository("http://download.eclipse.org/tools/buckminster/updates-4.3"));
    repos.add(repository("http://download.eclipse.org/tools/gef/updates/milestones"));
    repos.add(repository("http://download.eclipse.org/modeling/emf/emf/updates/2.10milestones"));
    repos.add(repository("http://download.eclipse.org/egit/updates-stable-nightly"));
    repos.add(repository("http://download.eclipse.org/mylyn/snapshots/weekly"));
    repos.add(repository("http://download.eclipse.org/technology/nebula/snapshot"));

    Targlet targlet = SetupFactory.eINSTANCE.createTarglet();
    targlet.setName("CDO Releng");
    targlet.setIncludeSources(true);

    EList<InstallableUnit> roots = targlet.getRoots();
    roots.add(component("org.eclipse.emf.cdo.releng.all.feature.group"));
    roots.add(component("org.eclipse.sdk.feature.group"));

    targlet.getSourceLocators().add(sourceLocator("C:/cdo", false));

    targlet.getRepositoryLists().add(repositoryList);
    targlet.setActiveRepositoryList(repositoryList.getName());

    String id = "Test-" + System.currentTimeMillis();
    TargletContainer container = new TargletContainer(id, ECollections.singletonEList(targlet));
    ITargetLocation[] locations = { container };

    @SuppressWarnings("restriction")
    ITargetPlatformService targetService = (ITargetPlatformService)org.eclipse.pde.internal.core.PDECore.getDefault()
        .acquireService(ITargetPlatformService.class.getName());

    // for (ITargetHandle handle : targetService.getTargets(new NullProgressMonitor()))
    // {
    // targetService.deleteTarget(handle);
    // }

    ITargetDefinition target = targetService.newTarget();
    target.setName("Modular Target " + id);
    target.setTargetLocations(locations);
    targetService.saveTargetDefinition(target);
  }

  private static InstallableUnit component(String id)
  {
    InstallableUnit installableUnit = SetupFactory.eINSTANCE.createInstallableUnit();
    installableUnit.setID(id);
    return installableUnit;
  }

  private static AutomaticSourceLocator sourceLocator(String rootFolder, boolean locateNestedProjects)
  {
    AutomaticSourceLocator sourceLocator = SetupFactory.eINSTANCE.createAutomaticSourceLocator();
    sourceLocator.setRootFolder(rootFolder);
    sourceLocator.setLocateNestedProjects(locateNestedProjects);
    return sourceLocator;
  }

  private static P2Repository repository(String url)
  {
    P2Repository p2Repository = SetupFactory.eINSTANCE.createP2Repository();
    p2Repository.setURL(url);
    return p2Repository;
  }
}
