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
import org.eclipse.emf.cdo.server.IStore;

import org.eclipse.net4j.internal.util.lifecycle.Lifecycle;

/**
 * @author Eike Stepper
 */
public abstract class Store extends Lifecycle implements IStore
{
  private String type;

  private IRepository repository;

  public Store(String type)
  {
    this.type = type;
  }

  public String getStoreType()
  {
    return type;
  }

  public IRepository getRepository()
  {
    return repository;
  }

  public void setRepository(IRepository repository)
  {
    this.repository = repository;
  }
}
