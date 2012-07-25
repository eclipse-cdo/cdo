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

import org.eclipse.emf.cdo.releng.version.IVersionBuilderArguments;
import org.eclipse.emf.cdo.releng.version.VersionBuilderArguments;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;

import java.util.Iterator;

/**
 * @author Eike Stepper
 */
public class AddNatureAction extends AbstractAction<IVersionBuilderArguments>
{
  public AddNatureAction()
  {
    super("Add Version Management");
  }

  @Override
  protected IVersionBuilderArguments promptArguments()
  {
    VersionBuilderArguments arguments = new VersionBuilderArguments();
    ConfigurationDialog dialog = new ConfigurationDialog(shell, arguments);

    if (dialog.open() == ConfigurationDialog.OK)
    {
      return dialog;
    }

    return null;
  }

  @Override
  protected void runWithArguments(IVersionBuilderArguments arguments) throws CoreException
  {
    for (Iterator<?> it = ((IStructuredSelection)selection).iterator(); it.hasNext();)
    {
      Object element = it.next();
      if (element instanceof IProject)
      {
        IProject project = (IProject)element;
        arguments.applyTo(project);
      }
    }
  }
}
