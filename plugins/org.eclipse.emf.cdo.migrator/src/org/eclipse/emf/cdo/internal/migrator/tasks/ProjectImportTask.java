/*
 * Copyright (c) 2018 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.migrator.tasks;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;

import org.apache.tools.ant.BuildException;

import java.io.File;

/**
 * @author Eike Stepper
 */
public class ProjectImportTask extends CDOTask
{
  private String projectName;

  private File location;

  public void setProjectName(String projectName)
  {
    this.projectName = projectName;
  }

  public void setLocation(File location)
  {
    this.location = location;
  }

  @Override
  protected void checkAttributes() throws BuildException
  {
    assertTrue("'projectName' must be specified.", projectName != null && projectName.length() != 0);
    assertTrue("'location' must be specified.", location != null);
    assertTrue("'location' must be point to an existing directory.", location.isDirectory());
  }

  @Override
  protected void doExecute() throws Exception
  {
    IProject project = root.getProject(projectName);
    if (project.exists())
    {
      File existingLocation = new File(project.getLocation().toOSString()).getCanonicalFile();
      if (!existingLocation.equals(location))
      {
        throw new BuildException("Project " + projectName + " exists in different location: " + existingLocation);
      }

      System.out.println("Project " + location + " is already imported.");
      return;
    }

    System.out.println("Importing project " + location + " ...");
    IPath locationPath = new Path(location.getAbsolutePath());

    IProjectDescription projectDescription = workspace.newProjectDescription(projectName);
    projectDescription.setLocation(locationPath);

    project.create(projectDescription, new NullProgressMonitor());

    if (!project.isOpen())
    {
      project.open(new NullProgressMonitor());
    }
  }
}
