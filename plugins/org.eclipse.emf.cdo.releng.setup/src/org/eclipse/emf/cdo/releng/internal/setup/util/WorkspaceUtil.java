/*
 * Copyright (c) Ericsson AB and others.
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

import org.eclipse.net4j.util.XMLUtil;
import org.eclipse.net4j.util.XMLUtil.ElementHandler;
import org.eclipse.net4j.util.om.monitor.SubMonitor;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import java.io.File;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Eike Stepper
 * @author Pascal Rapicault
 */
public final class WorkspaceUtil
{
  private static final IWorkspace WORKSPACE = ResourcesPlugin.getWorkspace();

  private static final IWorkspaceRoot ROOT = WORKSPACE.getRoot();

  private WorkspaceUtil()
  {
  }

  public static String getProjectName(File folder) throws ParserConfigurationException, Exception
  {
    DocumentBuilder documentBuilder = XMLUtil.createDocumentBuilder();
    Element rootElement = XMLUtil.loadRootElement(documentBuilder, new File(folder, ".project"));

    final AtomicReference<String> projectName = new AtomicReference<String>();
    XMLUtil.handleChildElements(rootElement, new ElementHandler()
    {
      public void handleElement(Element element) throws Exception
      {
        if ("name".equals(element.getTagName()))
        {
          projectName.set(element.getTextContent().trim());
        }
      }
    });

    return projectName.get();
  }

  public static int importProjects(final Collection<File> projectLocations, IProgressMonitor monitor)
      throws CoreException
  {
    if (projectLocations.isEmpty())
    {
      return 0;
    }

    final AtomicInteger count = new AtomicInteger();
    WORKSPACE.run(new IWorkspaceRunnable()
    {
      public void run(IProgressMonitor monitor) throws CoreException
      {
        SubMonitor progress = SubMonitor.convert(monitor, projectLocations.size()).detectCancelation();

        try
        {
          for (File folder : projectLocations)
          {
            if (importProject(folder, progress.newChild()))
            {
              count.incrementAndGet();
            }
          }
        }
        catch (Exception ex)
        {
          Activator.coreException(ex);
        }
        finally
        {
          progress.done();
        }
      }
    }, monitor);

    return count.get();
  }

  private static boolean importProject(File folder, IProgressMonitor monitor) throws Exception
  {
    String name = getProjectName(folder);
    if (name != null && name.length() != 0)
    {
      File location = folder.getCanonicalFile();

      IProject project = ROOT.getProject(name);
      if (project.exists())
      {
        File existingLocation = new File(project.getLocation().toOSString()).getCanonicalFile();
        if (!existingLocation.equals(location))
        {
          Activator.log("Project " + name + " exists in different location: " + existingLocation);
          return false;
        }
      }
      else
      {
        monitor.setTaskName("Importing project " + name);

        IProjectDescription projectDescription = WORKSPACE.newProjectDescription(name);
        projectDescription.setLocation(new Path(location.getAbsolutePath()));
        project.create(projectDescription, monitor);
      }

      if (!project.isOpen())
      {
        project.open(monitor);
      }
    }

    return true;
  }
}
