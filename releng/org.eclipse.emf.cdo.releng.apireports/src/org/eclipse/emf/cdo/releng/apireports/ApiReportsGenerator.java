/*
 * Copyright (c) 2017, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.apireports;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IOrdinaryClassFile;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.pde.api.tools.internal.ApiBaselineManager;
import org.eclipse.pde.api.tools.internal.comparator.DeltaXmlVisitor;
import org.eclipse.pde.api.tools.internal.provisional.Factory;
import org.eclipse.pde.api.tools.internal.provisional.IApiAnnotations;
import org.eclipse.pde.api.tools.internal.provisional.IApiDescription;
import org.eclipse.pde.api.tools.internal.provisional.VisibilityModifiers;
import org.eclipse.pde.api.tools.internal.provisional.comparator.ApiComparator;
import org.eclipse.pde.api.tools.internal.provisional.comparator.ApiScope;
import org.eclipse.pde.api.tools.internal.provisional.comparator.IDelta;
import org.eclipse.pde.api.tools.internal.provisional.model.IApiBaseline;
import org.eclipse.pde.api.tools.internal.provisional.model.IApiComponent;
import org.eclipse.pde.api.tools.internal.provisional.model.IApiScope;
import org.eclipse.pde.api.tools.internal.provisional.model.IApiTypeRoot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Eike Stepper
 */
public final class ApiReportsGenerator
{
  private ApiReportsGenerator()
  {
  }

  public static IStatus generate(String reportFileName, String baselineName, String exclusionPatterns,
      IProgressMonitor monitor)
  {
    if (baselineName == null || baselineName.length() == 0)
    {
      IApiBaseline baseline = ApiBaselineManager.getManager().getDefaultApiBaseline();
      if (baseline != null)
      {
        baselineName = baseline.getName();
      }

      if (baselineName == null || baselineName.length() == 0)
      {
        return Activator.errorStatus("Baseline name not specified");
      }
    }

    if (reportFileName == null || reportFileName.length() == 0)
    {
      reportFileName = System.getProperty("api.report", new File("api.xml").getAbsolutePath());
    }

    SubMonitor progress = SubMonitor.convert(monitor, 100);
    progress.subTask("Collecting elements to compare");

    try
    {
      progress.subTask("Computing deltas...");
      File reportFile = new File(reportFileName);

      try
      {
        progress.worked(25);
        updateMonitor(progress);
        BufferedWriter writer = null;

        try
        {
          if (reportFile.exists())
          {
            reportFile.delete();
          }
          else
          {
            File parent = reportFile.getParentFile();
            if (!parent.exists() && !parent.mkdirs())
            {
              return Activator.errorStatus("Failed to create report directory structure");
            }
          }

          writer = new BufferedWriter(new FileWriter(reportFile));

          List<Object> projects = collectProjects(exclusionPatterns);
          IApiScope scope = walkStructureSelection(projects, monitor);
          IApiBaseline baseline = ApiBaselineManager.getManager().getApiBaseline(baselineName);
          if (baseline == null)
          {
            return Activator.errorStatus("Baseline not found: " + baselineName);
          }

          IDelta delta = ApiComparator.compare(scope, baseline, VisibilityModifiers.API, false, true, monitor);
          if (delta != null)
          {
            progress.worked(25);
            updateMonitor(progress);

            DeltaXmlVisitor visitor = new DeltaXmlVisitor();
            delta.accept(visitor);

            writer.write(visitor.getXML());
            writer.flush();

            progress.worked(25);
          }
        }
        catch (CoreException e)
        {
          Activator.log(e.getStatus());
        }
        catch (Throwable e)
        {
          Activator.log(e);
        }
        finally
        {
          if (writer != null)
          {
            try
            {
              writer.close();
            }
            catch (IOException e)
            {
              // ignore
            }
          }
        }

        progress.worked(25);
        return Status.OK_STATUS;
      }
      catch (OperationCanceledException e)
      {
        // ignore
      }
    }
    finally
    {
      monitor.done();
    }

    return Status.CANCEL_STATUS;
  }

  private static List<Object> collectProjects(String exclusionPatterns)
  {
    Pattern[] patterns = new Pattern[0];
    if (exclusionPatterns != null)
    {
      String[] split = exclusionPatterns.split(",");
      patterns = new Pattern[split.length];

      for (int i = 0; i < split.length; i++)
      {
        patterns[i] = Pattern.compile(split[i].trim());
      }
    }

    List<Object> result = new ArrayList<>();
    for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects())
    {
      if (project.isAccessible())
      {
        String name = project.getName();
        if (!isExcluded(patterns, name))
        {
          IJavaProject javaProject = JavaCore.create(project);
          if (javaProject != null)
          {
            result.add(javaProject);
            Activator.log(Status.info("API report project: " + javaProject));
          }
        }
      }
    }

    return result;
  }

  private static boolean isExcluded(Pattern[] patterns, String name)
  {
    for (Pattern pattern : patterns)
    {
      Matcher matcher = pattern.matcher(name);
      if (matcher.matches())
      {
        return true;
      }
    }

    return false;
  }

  private static IApiScope walkStructureSelection(List<Object> projects, IProgressMonitor monitor)
  {
    ApiScope scope = new ApiScope();
    IApiBaseline workspaceBaseline = ApiBaselineManager.getManager().getWorkspaceBaseline();
    if (workspaceBaseline == null)
    {
      return scope;
    }

    Collections.sort(projects, new Comparator()
    {
      @Override
      public int compare(Object o1, Object o2)
      {
        if (o1 instanceof IJavaElement && o2 instanceof IJavaElement)
        {
          IJavaElement element = (IJavaElement)o1;
          IJavaElement element2 = (IJavaElement)o2;
          return element.getElementType() - element2.getElementType();
        }

        return 0;
      }
    });

    for (Object project : projects)
    {
      if (project instanceof IJavaElement)
      {
        IJavaElement element = (IJavaElement)project;
        IJavaProject javaProject = element.getJavaProject();
        try
        {
          switch (element.getElementType())
          {
          case IJavaElement.COMPILATION_UNIT:
          {
            ICompilationUnit compilationUnit = (ICompilationUnit)element;
            IApiComponent apiComponent = workspaceBaseline.getApiComponent(javaProject.getElementName());
            if (apiComponent != null)
            {
              addElementFor(compilationUnit, apiComponent, scope);
            }

            break;
          }
          case IJavaElement.PACKAGE_FRAGMENT:
          {
            IPackageFragment fragment = (IPackageFragment)element;
            IApiComponent apiComponent = workspaceBaseline.getApiComponent(javaProject.getElementName());
            IPackageFragmentRoot packageFragmentRoot = (IPackageFragmentRoot)fragment
                .getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT);
            boolean isArchive = false;
            if (packageFragmentRoot != null)
            {
              isArchive = packageFragmentRoot.isArchive();
            }

            if (apiComponent != null)
            {
              addElementFor(fragment, isArchive, apiComponent, scope);
            }

            break;
          }
          case IJavaElement.PACKAGE_FRAGMENT_ROOT:
          {
            IPackageFragmentRoot fragmentRoot = (IPackageFragmentRoot)element;
            IApiComponent apiComponent = workspaceBaseline.getApiComponent(javaProject.getElementName());
            if (apiComponent != null)
            {
              addElementFor(fragmentRoot, apiComponent, scope);
            }

            break;
          }
          case IJavaElement.JAVA_PROJECT:
            IApiComponent apiComponent = workspaceBaseline.getApiComponent(javaProject.getElementName());
            if (apiComponent != null)
            {
              scope.addElement(apiComponent);
            }

            break;
          }
        }
        catch (CoreException e)
        {
          Activator.log(e.getStatus());
        }
        catch (Throwable e)
        {
          Activator.log(e);
        }
      }
    }

    return scope;
  }

  private static void addElementFor(IPackageFragmentRoot fragmentRoot, IApiComponent apiComponent, ApiScope scope)
      throws JavaModelException, CoreException
  {
    boolean isArchive = fragmentRoot.isArchive();
    IJavaElement[] packageFragments = fragmentRoot.getChildren();
    for (int j = 0, max2 = packageFragments.length; j < max2; j++)
    {
      IPackageFragment packageFragment = (IPackageFragment)packageFragments[j];
      addElementFor(packageFragment, isArchive, apiComponent, scope);
    }
  }

  private static void addElementFor(IPackageFragment packageFragment, boolean isArchive, IApiComponent apiComponent,
      ApiScope scope) throws JavaModelException, CoreException
  {
    // add package fragment elements only if this is an API package
    IApiDescription apiDescription = apiComponent.getApiDescription();
    IApiAnnotations annotations = apiDescription
        .resolveAnnotations(Factory.packageDescriptor(packageFragment.getElementName()));
    if (annotations == null || !VisibilityModifiers.isAPI(annotations.getVisibility()))
    {
      return;
    }

    if (isArchive)
    {
      IOrdinaryClassFile[] classFiles = packageFragment.getOrdinaryClassFiles();
      for (int i = 0, max = classFiles.length; i < max; i++)
      {
        addElementFor(classFiles[i], apiComponent, scope);
      }
    }
    else
    {
      ICompilationUnit[] units = packageFragment.getCompilationUnits();
      for (int i = 0, max = units.length; i < max; i++)
      {
        addElementFor(units[i], apiComponent, scope);
      }
    }
  }

  private static void addElementFor(IOrdinaryClassFile classFile, IApiComponent apiComponent, ApiScope scope)
  {
    try
    {
      IApiTypeRoot typeRoot = apiComponent.findTypeRoot(classFile.getType().getFullyQualifiedName());
      if (typeRoot != null)
      {
        scope.addElement(typeRoot);
      }
    }
    catch (CoreException e)
    {
      Activator.log(e.getStatus());
    }
    catch (Throwable e)
    {
      Activator.log(e);
    }
  }

  private static void addElementFor(ICompilationUnit compilationUnit, IApiComponent component, ApiScope scope)
      throws JavaModelException
  {
    IType[] types = compilationUnit.getTypes();
    for (int i = 0, max = types.length; i < max; i++)
    {
      try
      {
        IApiTypeRoot typeRoot = component.findTypeRoot(types[i].getFullyQualifiedName());
        if (typeRoot != null)
        {
          scope.addElement(typeRoot);
        }
      }
      catch (CoreException e)
      {
        Activator.log(e.getStatus());
      }
      catch (Throwable e)
      {
        Activator.log(e);
      }
    }
  }

  private static void updateMonitor(IProgressMonitor monitor, int work) throws OperationCanceledException
  {
    if (monitor == null)
    {
      return;
    }

    if (monitor.isCanceled())
    {
      throw new OperationCanceledException();
    }

    monitor.worked(work);
  }

  private static void updateMonitor(IProgressMonitor monitor) throws OperationCanceledException
  {
    updateMonitor(monitor, 0);
  }
}
