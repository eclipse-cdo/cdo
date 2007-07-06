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

import org.eclipse.emf.cdo.server.IRepositoryManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public final class RepositoryManager implements IRepositoryManager
{
  // @Singleton
  public static final RepositoryManager INSTANCE = new RepositoryManager();

  private Map<String, Repository> repositories = new HashMap();

  private RepositoryManager()
  {
  }

  public Repository getRepository(String name, boolean createOnDemand)
  {
    Repository repository = repositories.get(name);
    if (repository == null && createOnDemand)
    {
      repository = new Repository(name);
      repositories.put(name, repository);
    }

    return repository;
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
