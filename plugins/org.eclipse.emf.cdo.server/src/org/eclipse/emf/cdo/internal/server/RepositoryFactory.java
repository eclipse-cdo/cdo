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

import org.eclipse.emf.cdo.server.RepositoryNotFoundException;

import org.eclipse.net4j.internal.util.factory.Factory;
import org.eclipse.net4j.util.container.IManagedContainer;

/**
 * @author Eike Stepper
 */
public class RepositoryFactory extends Factory<Repository>
{
  public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.server.repositories";

  public static final String TYPE = "default";

  public RepositoryFactory()
  {
    super(PRODUCT_GROUP, TYPE);
  }

  public Repository create(String name)
  {
    // Repositories are not created by factories rather they are put into a
    // container directly. See RepositoryConfigurator
    throw new RepositoryNotFoundException(name);
  }

  public static Repository get(IManagedContainer container, String name)
  {
    return (Repository)container.getElement(PRODUCT_GROUP, TYPE, name);
  }
}
