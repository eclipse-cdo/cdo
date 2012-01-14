/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Martin Taal - contributions
 */
package org.eclipse.emf.cdo.server.hibernate;

import org.eclipse.emf.cdo.server.IStore;

import org.eclipse.net4j.db.IDBConnectionProvider;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * @author Eike Stepper
 * @since 4.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IHibernateStore extends IStore, IDBConnectionProvider
{
  /**
   * @since 4.0
   */
  public static final String QUERY_LANGUAGE = "hql"; //$NON-NLS-1$

  /**
   * @since 4.0
   */
  public static final String FIRST_RESULT = "firstResult"; //$NON-NLS-1$

  /**
   * If results should be cached in the query cache, only needed if they are accessed directly as part of the query.
   * 
   * @since 4.0
   */
  public static final String CACHE_RESULTS = "cacheResults"; //$NON-NLS-1$

  public Configuration getHibernateConfiguration();

  public SessionFactory getHibernateSessionFactory();
}
