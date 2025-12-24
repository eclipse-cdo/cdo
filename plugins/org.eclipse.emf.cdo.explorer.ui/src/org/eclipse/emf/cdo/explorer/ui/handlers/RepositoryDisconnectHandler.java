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

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Eike Stepper
 */
public class RepositoryDisconnectHandler extends AbstractRepositoryHandler
{
  public RepositoryDisconnectHandler()
  {
    super(null, true);
  }

  @Override
  protected void doExecute(IProgressMonitor monitor) throws Exception
  {
    for (CDORepository repository : elements)
    {
      repository.disconnect();
    }
  }
}
