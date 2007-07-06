/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.server.RepositoryManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public final class RepositoryManagerImpl implements RepositoryManager
{
  // @Singleton
  public static final RepositoryManagerImpl INSTANCE = new RepositoryManagerImpl();

  private Map<String, RepositoryImpl> repositories = new HashMap();

  private RepositoryManagerImpl()
  {
  }

  public RepositoryImpl getRepository(String name, boolean createOnDemand)
  {
    RepositoryImpl repository = repositories.get(name);
    if (repository == null && createOnDemand)
    {
      repository = openRepository(name);
      repositories.put(name, repository);
    }

    return repository;
  }

  private RepositoryImpl openRepository(String name)
  {
    return new RepositoryImpl(name);
  }

  public boolean isEmpty()
  {
    return repositories.isEmpty();
  }

  public void clear()
  {
    repositories.clear();
  }
}
