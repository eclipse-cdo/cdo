/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal
 */
package org.eclipse.emf.cdo.tests.hibernate;

import org.eclipse.emf.cdo.server.hibernate.IHibernateStore;
import org.eclipse.emf.cdo.view.CDOQuery;

/**
 * Test HQL queries with server side caching of query list to off.
 * 
 * @author Martin Taal
 */
public class HibernateQueryNoCachingTest extends HibernateQueryTest
{
  @Override
  protected void addCacheParameter(CDOQuery query)
  {
    query.setParameter(IHibernateStore.CACHE_RESULTS, false);
  }
}
