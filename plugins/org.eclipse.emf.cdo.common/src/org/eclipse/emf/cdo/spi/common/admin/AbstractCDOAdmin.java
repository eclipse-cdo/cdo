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
package org.eclipse.emf.cdo.spi.common.admin;

import org.eclipse.emf.cdo.common.admin.CDOAdmin;
import org.eclipse.emf.cdo.common.admin.CDOAdminRepository;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.concurrent.TimeoutRuntimeException;
import org.eclipse.net4j.util.container.SetContainer;

import java.util.Map;

/**
 * @author Eike Stepper
 * @since 4.1
 */
public abstract class AbstractCDOAdmin extends SetContainer<CDOAdminRepository> implements CDOAdmin
{
  private final long timeout;

  protected AbstractCDOAdmin(long timeout)
  {
    super(CDOAdminRepository.class);
    this.timeout = timeout;
  }

  public final long getTimeout()
  {
    return timeout;
  }

  public boolean isClosed()
  {
    return !isActive();
  }

  public void close()
  {
    deactivate();
  }

  public CDOAdminRepository[] getRepositories()
  {
    return getElements();
  }

  public synchronized CDOAdminRepository getRepository(String name)
  {
    for (CDOAdminRepository repository : getSet())
    {
      if (ObjectUtil.equals(repository.getName(), name))
      {
        return repository;
      }
    }

    return null;
  }

  public CDOAdminRepository createRepository(String name, String type, Map<String, Object> properties)
  {
    checkActive();

    synchronized (this)
    {
      if (!doCreateRepository(name, type, properties))
      {
        return null;
      }

      long end = System.currentTimeMillis() + timeout;
      while (System.currentTimeMillis() < end)
      {
        CDOAdminRepository repository = getRepository(name);
        if (repository != null)
        {
          return repository;
        }

        try
        {
          wait(10);
        }
        catch (InterruptedException ex)
        {
          return null;
        }
      }

      throw new TimeoutRuntimeException();
    }
  }

  public boolean deleteRepository(CDOAdminRepository repository, String type)
  {
    checkActive();
    String name = repository.getName();

    synchronized (this)
    {
      if (!doDeleteRepository(name, type))
      {
        return false;
      }

      long end = System.currentTimeMillis() + timeout;
      while (System.currentTimeMillis() < end)
      {
        if (getRepository(name) == null)
        {
          return true;
        }

        try
        {
          wait(10);
        }
        catch (InterruptedException ex)
        {
          return false;
        }
      }

      throw new TimeoutRuntimeException();
    }
  }

  protected abstract boolean doCreateRepository(String name, String type, Map<String, Object> properties);

  protected abstract boolean doDeleteRepository(String name, String type);
}
