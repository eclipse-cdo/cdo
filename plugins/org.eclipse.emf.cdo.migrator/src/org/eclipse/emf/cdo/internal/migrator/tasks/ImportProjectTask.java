/*
 * Copyright (c) 2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
public class ImportProjectTask extends CDOTask
{
  private String projectName;

  private File fromLocation;

  public void setProjectName(String projectName)
  {
    this.projectName = projectName;
  }

  public void setFromLocation(File fromLocation)
  {
    this.fromLocation = fromLocation;
  }

  @Override
  protected void checkAttributes() throws BuildException
  {
    assertTrue("'projectName' must be specified.", projectName != null && projectName.length() != 0);
    assertTrue("'fromLocation' must be specified.", fromLocation != null);
    assertTrue("'fromLocation' must be point to an existing directory.", fromLocation.isDirectory());
  }

  @Override
  protected void doExecute() throws Exception
  {
    IProject project = root.getProject(projectName);
    if (project.exists())
    {
      File existingLocation = new File(project.getLocation().toOSString()).getCanonicalFile();
      if (!existingLocation.equals(fromLocation))
      {
        throw new BuildException("Project " + projectName + " exists in different location: " + existingLocation);
      }

      verbose("Project " + fromLocation + " is already imported.");
      return;
    }

    verbose("Importing project " + fromLocation + " ...");
    IPath locationPath = new Path(fromLocation.getAbsolutePath());

    IProjectDescription projectDescription = workspace.newProjectDescription(projectName);
    projectDescription.setLocation(locationPath);

    project.create(projectDescription, new NullProgressMonitor());

    if (!project.isOpen())
    {
      project.open(new NullProgressMonitor());
    }
  }
}
