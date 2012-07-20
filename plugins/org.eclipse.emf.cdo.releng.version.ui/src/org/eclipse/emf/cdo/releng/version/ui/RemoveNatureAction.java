/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.version.ui;

import org.eclipse.emf.cdo.releng.version.VersionBuilder;
import org.eclipse.emf.cdo.releng.version.VersionNature;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class RemoveNatureAction extends AbstractAction<Boolean>
{
  public RemoveNatureAction()
  {
    super("Remove version tool nature");
  }

  @Override
  protected Boolean promptArguments()
  {
    return true;
  }

  @Override
  protected void runWithArguments(Boolean arguments) throws CoreException
  {
    for (Iterator<?> it = ((IStructuredSelection)selection).iterator(); it.hasNext();)
    {
      Object element = it.next();
      if (element instanceof IProject)
      {
        IProject project = (IProject)element;
        removeNature(project);
      }
    }
  }

  protected void removeNature(IProject project) throws CoreException
  {
    IProjectDescription description = project.getDescription();

    String[] natureIds = description.getNatureIds();
    List<String> ids = new ArrayList<String>(Arrays.asList(natureIds));
    ids.remove(VersionNature.NATURE_ID);
    description.setNatureIds(ids.toArray(new String[ids.size()]));

    ICommand[] buildSpec = description.getBuildSpec();
    List<ICommand> commands = new ArrayList<ICommand>(Arrays.asList(buildSpec));
    for (Iterator<ICommand> it = commands.iterator(); it.hasNext();)
    {
      ICommand command = it.next();
      if (VersionBuilder.BUILDER_ID.equals(command.getBuilderName()))
      {
        it.remove();
        break;
      }
    }

    description.setBuildSpec(commands.toArray(new ICommand[commands.size()]));
    project.setDescription(description, new NullProgressMonitor());
  }
}
