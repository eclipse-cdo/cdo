/*
 * Copyright (c) 2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.internal.setup.util;

import org.eclipse.emf.cdo.releng.internal.setup.Activator;
import org.eclipse.emf.cdo.releng.setup.util.ProjectProvider.Visitor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class BasicProjectAnalyzer<T>
{
  public Map<T, File> analyze(File folder, boolean locateNestedProjects, Visitor<T> visitor, IProgressMonitor monitor)
  {
    Map<T, File> results = new HashMap<T, File>();
    analyze(folder, locateNestedProjects, results, visitor, monitor);
    return results;
  }

  private void analyze(File folder, boolean locateNestedProjects, Map<T, File> results, Visitor<T> visitor,
      IProgressMonitor monitor)
  {
    if (monitor != null && monitor.isCanceled())
    {
      throw new OperationCanceledException();
    }

    File projectFile = new File(folder, ".project");
    if (projectFile.exists())
    {
      try
      {
        T result = analyzeProject(projectFile, folder, visitor, monitor);
        if (result != null)
        {
          File cextFile = new File(folder, "component.ext");
          if (cextFile.exists())
          {
            visitor.visitComponentExtension(cextFile, result, monitor);
          }

          File cspexFile = new File(folder, "buckminster.cspex");
          if (cspexFile.exists())
          {
            visitor.visitCSpex(cspexFile, result, monitor);
          }

          result = filter(result);
          if (result != null)
          {
            results.put(result, folder);
          }

          if (!locateNestedProjects)
          {
            return;
          }
        }
      }
      catch (Exception ex)
      {
        Activator.log(ex);
      }
    }

    File[] listFiles = folder.listFiles();
    if (listFiles != null)
    {
      for (int i = 0; i < listFiles.length; i++)
      {
        File file = listFiles[i];
        if (file.isDirectory())
        {
          analyze(file, locateNestedProjects, results, visitor, monitor);
        }
      }
    }
  }

  private T analyzeProject(File projectFile, File folder, Visitor<T> visitor, IProgressMonitor monitor)
      throws Exception
  {
    File featureFile = new File(folder, "feature.xml");
    if (featureFile.exists())
    {
      return visitor.visitFeature(featureFile, monitor);
    }

    File manifestFile = new File(folder, "META-INF/MANIFEST.MF");
    if (manifestFile.exists())
    {
      return visitor.visitPlugin(manifestFile, monitor);
    }

    File cdefFile = new File(folder, "component.def");
    if (cdefFile.exists())
    {
      return visitor.visitComponentDefinition(cdefFile, monitor);
    }

    File cspecFile = new File(folder, "buckminster.cspec");
    if (cspecFile.exists())
    {
      return visitor.visitCSpec(cspecFile, monitor);
    }

    return null;
  }

  protected T filter(T result)
  {
    return result;
  }
}
