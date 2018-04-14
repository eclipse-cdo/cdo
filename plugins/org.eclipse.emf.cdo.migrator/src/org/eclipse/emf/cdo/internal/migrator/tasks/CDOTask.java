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

import org.eclipse.ant.core.AntCorePlugin;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * @author Eike Stepper
 */
public abstract class CDOTask extends Task
{
  protected final IWorkspace workspace = ResourcesPlugin.getWorkspace();

  protected final IWorkspaceRoot root = workspace.getRoot();

  @Override
  public final void execute() throws BuildException
  {
    checkAttributes();

    try
    {

      doExecute();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();

      if (ex instanceof BuildException)
      {
        throw (BuildException)ex;
      }

      throw new BuildException(ex);
    }
  }

  /**
   * Subclasses may override.
   */
  protected void checkAttributes() throws BuildException
  {
    // Do nothing.
  }

  protected abstract void doExecute() throws Exception;

  protected final IProgressMonitor getProgressMonitor()
  {
    try
    {
      if (getProject() != null)
      {
        IProgressMonitor progressMonitor = (IProgressMonitor)getProject().getReferences().get(AntCorePlugin.ECLIPSE_PROGRESS_MONITOR);
        if (progressMonitor != null)
        {
          return progressMonitor;
        }
      }
    }
    catch (Exception ex)
    {
      // Do nothing.
    }

    return new NullProgressMonitor();
  }

  public static void assertTrue(String message, boolean expression) throws BuildException
  {
    if (!expression)
    {
      throw new BuildException(message);
    }
  }
}
