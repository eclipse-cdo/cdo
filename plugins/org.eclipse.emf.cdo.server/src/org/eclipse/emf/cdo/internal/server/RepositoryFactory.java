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

import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IRepositoryFactory;

import org.eclipse.net4j.util.container.IManagedContainer;

/**
 * @author Eike Stepper
 */
public class RepositoryFactory implements IRepositoryFactory
{
  public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.server.repositories";

  public static final String TYPE = "default";

  public RepositoryFactory()
  {
  }

  public String getRepositoryType()
  {
    return TYPE;
  }

  public Repository createRepository()
  {
    return new Repository();
  }

  public static IRepository get(IManagedContainer container, String name)
  {
    return (Repository)container.getElement(PRODUCT_GROUP, TYPE, name);
  }
}
