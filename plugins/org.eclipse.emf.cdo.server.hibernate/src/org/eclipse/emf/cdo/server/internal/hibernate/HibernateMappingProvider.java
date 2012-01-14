/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.internal.hibernate;

import org.eclipse.emf.cdo.server.hibernate.IHibernateMappingProvider;
import org.eclipse.emf.cdo.server.hibernate.IHibernateStore;

/**
 * @author Eike Stepper
 */
public abstract class HibernateMappingProvider implements IHibernateMappingProvider
{
  private IHibernateStore hibernateStore;

  public HibernateMappingProvider()
  {
  }

  /**
   * @return the hibernate store, never <code>null</code>.
   * @throws IllegalStateException
   *           if the hibernate store is <code>null</code>.
   */
  public IHibernateStore getHibernateStore()
  {
    if (hibernateStore == null)
    {
      throw new IllegalStateException("hibernateStore is null"); //$NON-NLS-1$
    }

    return hibernateStore;
  }

  public void setHibernateStore(IHibernateStore hibernateStore)
  {
    this.hibernateStore = hibernateStore;
  }
}
