/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.db;

import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;

import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.db.ddl.IDBSchema;

import org.eclipse.emf.ecore.EModelElement;

/**
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IDBStore extends IStore
{
  public IMappingStrategy getMappingStrategy();

  public IDBSchema getDBSchema();

  public IDBAdapter getDBAdapter();

  public IDBConnectionProvider getDBConnectionProvider();

  public long getMetaID(EModelElement modelElement);

  public EModelElement getMetaInstance(long id);

  public IDBStoreAccessor getReader(ISession session);

  public IDBStoreAccessor getWriter(ITransaction transaction);
}
