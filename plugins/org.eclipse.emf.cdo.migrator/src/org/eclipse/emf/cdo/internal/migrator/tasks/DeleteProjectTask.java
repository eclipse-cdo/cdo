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
      verbose("Project " + projectName + " does not exist.");
      return;
    }

    verbose("Deleting project " + projectName + " ...");
    project.delete(deleteContent, true, new NullProgressMonitor());
  }
}
