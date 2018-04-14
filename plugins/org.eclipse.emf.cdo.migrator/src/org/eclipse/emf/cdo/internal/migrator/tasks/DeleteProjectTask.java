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
import org.eclipse.core.runtime.NullProgressMonitor;

import org.apache.tools.ant.BuildException;

/**
 * @author Eike Stepper
 */
public class DeleteProjectTask extends CDOTask
{
  private String projectName;

  private boolean deleteContent;

  public void setProjectName(String projectName)
  {
    this.projectName = projectName;
  }

  public void setDeleteContent(boolean deleteContent)
  {
    this.deleteContent = deleteContent;
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
      log("Project " + projectName + " does not exist.");
      return;
    }

    log("Deleting project " + projectName + " ...");
    project.delete(deleteContent, true, new NullProgressMonitor());
  }
}
