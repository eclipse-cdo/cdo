/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.internal.hibernate;

import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStoreFactory;

import org.w3c.dom.Element;

/**
 * @author Eike Stepper
 */
public class HibernateStoreFactory implements IStoreFactory
{
  public HibernateStoreFactory()
  {
  }

  public String getStoreType()
  {
    return HibernateStore.TYPE;
  }

  public IStore createStore(Element storeConfig)
  {
    throw new UnsupportedOperationException(); // TODO Implement me
    // IMappingStrategy mappingStrategy = getMappingStrategy(storeConfig);
    // IDBAdapter dbAdapter = getDBAdapter(storeConfig);
    // DataSource dataSource = getDataSource(storeConfig);
    // IDBConnectionProvider connectionProvider = DBUtil.createConnectionProvider(dataSource);
    // HibernateStore store = new HibernateStore(mappingStrategy, dbAdapter, connectionProvider);
    // mappingStrategy.setStore(store);
    // return store;
  }
}
