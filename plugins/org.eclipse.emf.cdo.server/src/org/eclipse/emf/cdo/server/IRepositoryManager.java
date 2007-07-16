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
package org.eclipse.emf.cdo.server;

import org.eclipse.emf.cdo.internal.server.RepositoryManager;

/**
 * @author Eike Stepper
 */
public interface IRepositoryManager
{
  public static final IRepositoryManager INSTANCE = RepositoryManager.INSTANCE;

  public String[] getRepositoryNames();

  public IRepository[] getRepositories();

  /**
   * @return Never <code>null</code>
   */
  public IRepository getRepository(String name) throws RepositoryNotFoundException;

  public IRepository addRepository(String name, IStore store);

  public void clear();

  public boolean isEmpty();

  public int size();
}
