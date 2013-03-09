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
import org.eclipse.net4j.db.ddl.IDBSchemaElement;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.ddl.delta.IDBSchemaDelta;
import org.eclipse.net4j.util.collection.Closeable;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.event.IEvent;

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

  public void ensureSchemaElement(RunnableWithSchema updateRunnable, RunnableWithSchema commitRunnable);

  public <T extends IDBSchemaElement, P extends IDBSchemaElement> T ensureSchemaElement(P parent, Class<T> type,
      String name, RunnableWithSchemaElement<T, P> runnable);

  public IDBTable ensureTable(String name, RunnableWithTable runnable);

  public IDBTransaction openTransaction();

  public IDBTransaction[] getTransactions();

  public int getStatementCacheCapacity();

  public void setStatementCacheCapacity(int statementCacheCapacity);

  /**
   * @author Eike Stepper
   */
  public interface SchemaChangedEvent extends IEvent
  {
    public IDBDatabase getSource();

    public IDBSchemaDelta getSchemaDelta();
  }

  /**
   * @author Eike Stepper
   */
  public interface RunnableWithSchema
  {
    public void run(IDBSchema schema);
  }

  /**
   * @author Eike Stepper
   */
  public interface RunnableWithSchemaElement<T extends IDBSchemaElement, P extends IDBSchemaElement>
  {
    public T run(P parent, String name);
  }

  /**
   * @author Eike Stepper
   */
  public interface RunnableWithTable
  {
    public void run(IDBTable table);
  }
}
