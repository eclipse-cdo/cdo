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

import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStoreProvider;

import org.eclipse.net4j.internal.util.factory.Factory;
import org.eclipse.net4j.util.container.IManagedContainer;

/**
 * @author Eike Stepper
 */
public class RepositoryFactory extends Factory<Repository>
{
  public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.server.repositories";

  public static final String TYPE = "default";

  private IStoreProvider storeProvider;

  public RepositoryFactory(IStoreProvider storeProvider)
  {
    super(PRODUCT_GROUP, TYPE);
    this.storeProvider = storeProvider;
  }

  public IStoreProvider getStoreProvider()
  {
    return storeProvider;
  }

  public Repository create(String name)
  {
    IStore store = storeProvider.getStore(name);
    return new Repository(name, store);
  }

  public static Repository get(IManagedContainer container, String name)
  {
    return (Repository)container.getElement(PRODUCT_GROUP, TYPE, name);
  }
}
