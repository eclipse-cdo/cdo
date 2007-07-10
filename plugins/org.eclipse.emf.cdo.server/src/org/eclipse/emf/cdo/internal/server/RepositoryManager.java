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

import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.server.IRepositoryManager;
import org.eclipse.emf.cdo.server.IStore;

import org.eclipse.net4j.internal.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public final class RepositoryManager extends Lifecycle implements IRepositoryManager
{
  // @Singleton
  public static final RepositoryManager INSTANCE = new RepositoryManager();

  private Map<String, Repository> repositories = new HashMap();

  private RepositoryManager()
  {
  }

  public String[] getRepositoryNames()
  {
    synchronized (repositories)
    {
      return repositories.keySet().toArray(new String[repositories.size()]);
    }
  }

  public Repository[] getRepositories()
  {
    synchronized (repositories)
    {
      return repositories.values().toArray(new Repository[repositories.size()]);
    }
  }

  public Repository getRepository(String name)
  {
    synchronized (repositories)
    {
      Repository repository = repositories.get(name);
      if (repository == null)
      {
        throw new RuntimeException("Repository not found: " + name);
      }
      return repository;
    }
  }

  public Repository addRepository(String name, IStore store)
  {
    synchronized (repositories)
    {
      Repository repository = repositories.get(name);
      if (repository != null)
      {
        throw new RuntimeException("Repository already exists: " + name);
      }
      repository = new Repository(name, store);
      LifecycleUtil.activate(repository);
      repositories.put(name, repository);
      return repository;
    }
  }

  public void clear()
  {
    synchronized (repositories)
    {
      repositories.clear();
    }
  }

  public boolean isEmpty()
  {
    synchronized (repositories)
    {
      return repositories.isEmpty();
    }
  }

  public int size()
  {
    synchronized (repositories)
    {
      return repositories.size();
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    for (Repository repository : getRepositories())
    {
      try
      {
        LifecycleUtil.deactivate(repository);
      }
      catch (Exception ex)
      {
        OM.LOG.warn(ex);
      }
    }

    super.doDeactivate();
  }
}
