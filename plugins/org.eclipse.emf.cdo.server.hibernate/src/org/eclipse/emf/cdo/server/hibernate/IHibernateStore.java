/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
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
 */
public interface IHibernateStore extends IStore, IDBConnectionProvider
{
  public Configuration getHibernateConfiguration();

  public SessionFactory getHibernateSessionFactory();
}
