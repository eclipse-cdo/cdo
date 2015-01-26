/*
 * Copyright (c) 2004-2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer;

import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.internal.explorer.CDOCheckoutManagerImpl;
import org.eclipse.emf.cdo.internal.explorer.CDORepositoryManagerImpl;

import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

/**
 * @author Eike Stepper
 * @since 4.4
 */
public final class CDOExplorerUtil
{
  private static CDORepositoryManager repositoryManager;

  private static CDOCheckoutManager checkoutManager;

  public static synchronized CDOCheckoutManager getCheckoutManager()
  {
    if (checkoutManager == null)
    {
      CDORepository repository = getRepositoryManager().getRepositories()[0];

      checkoutManager = new CDOCheckoutManagerImpl();
      checkoutManager.connect("Repo1 Root", repository, "MAIN", 0, false, CDOIDUtil.createLong(1));
      LifecycleUtil.activate(checkoutManager);
    }

    return checkoutManager;
  }

  public static synchronized void disposeCheckoutManager()
  {
    LifecycleUtil.deactivate(checkoutManager);
    checkoutManager = null;
  }

  public static synchronized CDORepositoryManager getRepositoryManager()
  {
    if (repositoryManager == null)
    {
      // File file = new File(OM.BUNDLE.getStateLocation(), "repositories");
      // FileContainerPersistence<CDORepository> persistence = new FileContainerPersistence<CDORepository>(file);
      //
      // CDORepositoryManagerImpl manager = new CDORepositoryManagerImpl();
      // manager.setPersistence(persistence);
      // manager.activate();
      //
      // repositoryManager = manager;

      repositoryManager = new CDORepositoryManagerImpl();
      repositoryManager.addRemoteRepository("Repo1", "repo1", "tcp", "localhost");

      LifecycleUtil.activate(repositoryManager);
    }

    return repositoryManager;
  }

  public static synchronized void disposeRepositoryManager()
  {
    LifecycleUtil.deactivate(repositoryManager);
    repositoryManager = null;
  }
}
