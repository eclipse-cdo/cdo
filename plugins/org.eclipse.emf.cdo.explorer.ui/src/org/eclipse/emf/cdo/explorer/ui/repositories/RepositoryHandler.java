/*
 * Copyright (c) 2009-2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.repositories;

import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.explorer.ui.BaseHandler;

import org.eclipse.jface.viewers.ISelection;

/**
 * @author Eike Stepper
 */
public abstract class RepositoryHandler extends BaseHandler<CDORepository>
{
  private final Boolean connected;

  public RepositoryHandler(Boolean multi, Boolean connected)
  {
    super(CDORepository.class, multi);
    this.connected = connected;
  }

  @Override
  protected boolean updateSelection(ISelection selection)
  {
    boolean result = super.updateSelection(selection);

    if (result && connected != null)
    {
      for (CDORepository repository : elements)
      {
        if (connected != repository.isConnected())
        {
          return false;
        }
      }
    }

    return result;
  }
}
