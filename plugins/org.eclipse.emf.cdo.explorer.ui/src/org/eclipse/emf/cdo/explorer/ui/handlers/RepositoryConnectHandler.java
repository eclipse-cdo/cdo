/*
 * Copyright (c) 2015, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.handlers;

import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.explorer.ui.repositories.CDORepositoriesView;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Eike Stepper
 */
public class RepositoryConnectHandler extends AbstractRepositoryHandler
{
  public RepositoryConnectHandler()
  {
    super(null, false);
  }

  @Override
  protected void doExecute(ExecutionEvent event, IProgressMonitor monitor) throws Exception
  {
    CDORepositoriesView repositoriesView = null;

    IWorkbenchPart part = HandlerUtil.getActivePart(event);
    if (part instanceof CDORepositoriesView)
    {
      repositoriesView = (CDORepositoriesView)part;
    }

    for (CDORepository repository : elements)
    {
      try
      {
        if (repositoriesView != null)
        {
          repositoriesView.connectRepository(repository);
        }
        else
        {
          repository.connect();
        }
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }
    }
  }
}
