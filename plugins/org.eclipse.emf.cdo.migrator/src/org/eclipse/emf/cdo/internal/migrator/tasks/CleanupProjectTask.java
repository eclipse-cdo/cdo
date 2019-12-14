/*
 * Copyright (c) 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.migrator.tasks;

import org.eclipse.emf.codegen.ecore.generator.Generator.CleanupScheduler;
import org.eclipse.emf.common.CommonPlugin;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;

import org.apache.tools.ant.BuildException;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class CleanupProjectTask extends CDOTask
{
  private String projectName;

  public void setProjectName(String projectName)
  {
    this.projectName = projectName;
  }

  @Override
  protected void checkAttributes() throws BuildException
  {
    assertTrue("'projectName' must be specified.", projectName != null && projectName.length() != 0);
  }

  @Override
  protected void doExecute() throws Exception
  {
    IProject project = root.getProject(projectName);
    if (!project.exists())
    {
      verbose("Project " + projectName + " does not exist.");
      return;
    }

    verbose("Cleaning up project " + projectName + " ...");
    EclipseHelper.sourceCleanup(this, project);
  }

  /**
   * @author Eike Stepper
   */
  private static class EclipseHelper
  {
    private static final CleanupScheduler SCHEDULER = getCleanupScheduler();

    public static void sourceCleanup(CDOTask task, IProject project)
    {
      if (SCHEDULER != null)
      {
        Set<ICompilationUnit> compilationUnits = collectCompilationUnits(project, task);
        SCHEDULER.schedule(compilationUnits);
      }
    }

    private static Set<ICompilationUnit> collectCompilationUnits(IProject project, CDOTask task)
    {
      Set<ICompilationUnit> compilationUnits = new LinkedHashSet<>();
      IJavaProject javaProject = JavaCore.create(project);

      try
      {
        for (IPackageFragmentRoot packageFragmentRoot : javaProject.getAllPackageFragmentRoots())
        {
          IResource resource = packageFragmentRoot.getCorrespondingResource();
          if (resource instanceof IContainer && project.equals(resource.getProject()))
          {
            collectCompilationUnits((IContainer)resource, compilationUnits, task);
          }
        }
      }
      catch (Exception ex)
      {
        throw new BuildException(ex);
      }

      return compilationUnits;
    }

    private static void collectCompilationUnits(IContainer container, Set<ICompilationUnit> compilationUnits, CDOTask task) throws CoreException
    {
      for (IResource resource : container.members())
      {
        if (resource instanceof IContainer)
        {
          collectCompilationUnits((IContainer)resource, compilationUnits, task);
        }
        else if (resource instanceof IFile)
        {
          ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom((IFile)resource);
          if (compilationUnit != null)
          {
            task.verbose("   " + resource.getProjectRelativePath());
            compilationUnits.add(compilationUnit);
          }
        }
      }
    }

    private static CleanupScheduler getCleanupScheduler()
    {
      try
      {
        Class<?> generatorUIUtilClass = CommonPlugin.loadClass("org.eclipse.emf.codegen.ecore.ui",
            "org.eclipse.emf.codegen.ecore.genmodel.presentation.GeneratorUIUtil");
        return (CleanupScheduler)generatorUIUtilClass.getField("CLEANUP_SCHEDULER").get(null);
      }
      catch (Throwable t)
      {
        return null;
      }
    }
  }
}
