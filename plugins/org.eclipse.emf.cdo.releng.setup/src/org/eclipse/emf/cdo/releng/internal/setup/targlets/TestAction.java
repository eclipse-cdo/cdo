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
import org.eclipse.emf.cdo.releng.predicates.PredicatesFactory;
import org.eclipse.emf.cdo.releng.setup.AutomaticSourceLocator;
import org.eclipse.emf.cdo.releng.setup.InstallableUnit;
import org.eclipse.emf.cdo.releng.setup.P2Repository;
import org.eclipse.emf.cdo.releng.setup.RepositoryList;
import org.eclipse.emf.cdo.releng.setup.SetupFactory;
import org.eclipse.emf.cdo.releng.setup.Targlet;

import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.om.monitor.SubMonitor;

import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.pde.core.target.ITargetDefinition;
import org.eclipse.pde.core.target.ITargetHandle;
import org.eclipse.pde.core.target.ITargetLocation;
import org.eclipse.pde.core.target.ITargetPlatformService;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Eike Stepper
 */
@SuppressWarnings("unused")
public class TestAction implements IWorkbenchWindowActionDelegate
{
  private static final String TARGET_NAME = "Modular Target";

  private static final String CONTAINER_ID = "Test";

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
      // testProgress();
      // generateFeatureIU();
      initTargetPlatform();
    }
    catch (Throwable ex)
    {
      Activator.log(ex);
      ErrorDialog.open(ex);
    }
  }

  private static void testProgress() throws Exception
  {
    Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
    new ProgressMonitorDialog(shell).run(true, true, new IRunnableWithProgress()
    {
      public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
      {
        try
        {
          recurse(ResourcesPlugin.getWorkspace().getRoot(), monitor);
        }
        catch (OperationCanceledException ex)
        {
          //$FALL-THROUGH$
        }
        catch (CoreException ex)
        {
          throw new InvocationTargetException(ex);
        }
      }

      public void recurse(IContainer container, IProgressMonitor monitor) throws CoreException
      {
        IResource[] members = container.members();

        SubMonitor progress = SubMonitor.convert(monitor, members.length).detectCancelation();
        progress.subTask(container.getFullPath().toString());

        for (IResource member : members)
        {
          if (member instanceof IContainer)
          {
            ConcurrencyUtil.sleep(2);
            recurse((IContainer)member, progress.newChild());
          }
          else
          {
            progress.skipped();
          }
        }
      }
    });
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
    // Container
    TargletContainer container = getContainer();

    // Name
    String name = "Targlet 1";
    EList<Targlet> targlets = container.getTarglets();
    if (!targlets.isEmpty())
    {
      Targlet targlet = targlets.get(0);
      if (name.equals(targlet.getName()))
      {
        name = "Targlet 2";
      }
    }

    // Targlet
    Targlet targlet = SetupFactory.eINSTANCE.createTarglet();
    targlet.setName(name);
    targlet.setIncludeSources(true);

    // Roots
    EList<InstallableUnit> roots = targlet.getRoots();
    roots.add(component("org.eclipse.emf.cdo.releng.all.feature.group"));
    roots.add(component("org.eclipse.net4j.util.ui.feature.group"));
    roots.add(component("org.eclipse.net4j.db.feature.group"));
    roots.add(component("org.eclipse.sdk.feature.group"));

    // Sources
    AutomaticSourceLocator sourceLocator = sourceLocator("C:/cdo", false);
    sourceLocator.getPredicates().add(
        PredicatesFactory.eINSTANCE.createNotPredicate(PredicatesFactory.eINSTANCE
            .createNamePredicate("org\\.eclipse\\.net4j.*")));
    targlet.getSourceLocators().add(sourceLocator);

    // Repos
    RepositoryList repositoryList = SetupFactory.eINSTANCE.createRepositoryList();
    repositoryList.setName("Milestones");
    EList<P2Repository> repos = repositoryList.getP2Repositories();
    repos.add(repository("http://download.eclipse.org/eclipse/updates/4.4milestones"));
    repos.add(repository("http://download.eclipse.org/tools/orbit/downloads/drops/S20140116105218/repository"));
    repos.add(repository("http://download.eclipse.org/tools/buckminster/updates-4.3"));
    repos.add(repository("http://download.eclipse.org/tools/gef/updates/milestones"));
    repos.add(repository("http://download.eclipse.org/modeling/emf/emf/updates/2.10milestones"));
    repos.add(repository("http://download.eclipse.org/egit/updates-stable-nightly"));
    repos.add(repository("http://download.eclipse.org/mylyn/snapshots/weekly"));
    repos.add(repository("http://download.eclipse.org/technology/nebula/snapshot"));
    repos.add(repository("https://hudson.eclipse.org/hudson/job/emf-cdo-integration/lastSuccessfulBuild/artifact"));
    targlet.getRepositoryLists().add(repositoryList);
    targlet.setActiveRepositoryList(repositoryList.getName());
    container.setTarglets(ECollections.singletonEList(targlet));
  }

  private static ITargetDefinition getTarget() throws CoreException
  {
    @SuppressWarnings("restriction")
    ITargetPlatformService service = (ITargetPlatformService)org.eclipse.pde.internal.core.PDECore.getDefault()
        .acquireService(ITargetPlatformService.class.getName());

    for (ITargetHandle handle : service.getTargets(new NullProgressMonitor()))
    {
      ITargetDefinition target = handle.getTargetDefinition();
      if (TARGET_NAME.equals(target.getName()))
      {
        return target;
      }
    }

    ITargetDefinition target = service.newTarget();
    target.setName(TARGET_NAME);
    return target;
  }

  private static TargletContainer getContainer() throws Exception
  {
    ITargetDefinition target = getTarget();

    ITargetLocation[] locations = target.getTargetLocations();
    if (locations != null)
    {
      for (ITargetLocation location : locations)
      {
        if (location instanceof TargletContainer)
        {
          TargletContainer container = (TargletContainer)location;
          if (CONTAINER_ID.equals(container.getID()))
          {
            return container;
          }
        }
      }
    }

    TargletContainer container = new TargletContainer(CONTAINER_ID);

    ITargetLocation[] newLocations;
    ITargetLocation[] oldLocations = locations;
    if (oldLocations != null && oldLocations.length != 0)
    {
      newLocations = new ITargetLocation[oldLocations.length + 1];
      System.arraycopy(oldLocations, 0, newLocations, 0, oldLocations.length);
      newLocations[oldLocations.length] = container;
    }
    else
    {
      newLocations = new ITargetLocation[] { container };
    }

    target.setTargetLocations(newLocations);
    return container;
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
