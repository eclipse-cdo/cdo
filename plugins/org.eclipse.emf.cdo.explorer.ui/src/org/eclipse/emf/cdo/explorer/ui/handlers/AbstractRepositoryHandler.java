/*
 * Copyright (c) 2015 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.util.ui.handlers.AbstractBaseHandler;

import org.eclipse.jface.viewers.ISelection;

/**
 * @author Eike Stepper
 */
public abstract class AbstractRepositoryHandler extends AbstractBaseHandler<CDORepository>
{
  private final Boolean connected;

  public AbstractRepositoryHandler(Boolean multi, Boolean connected)
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
