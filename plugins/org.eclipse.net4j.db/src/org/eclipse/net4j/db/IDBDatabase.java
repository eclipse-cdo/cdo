/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.db;

import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.util.collection.Closeable;
import org.eclipse.net4j.util.container.IContainer;

/**
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 * @since 4.2
 */
public interface IDBDatabase extends IContainer<IDBTransaction>, Closeable
{
  public static final int DEFAULT_STATEMENT_CACHE_CAPACITY = 200;

  public IDBAdapter getAdapter();

  public IDBSchema getSchema();

  public IDBConnectionProvider getConnectionProvider();

  public IDBSchemaTransaction openSchemaTransaction();

  public IDBSchemaTransaction getSchemaTransaction();

  public IDBTransaction openTransaction();

  public IDBTransaction[] getTransactions();

  public int getStatementCacheCapacity();

  public void setStatementCacheCapacity(int statementCacheCapacity);

}
