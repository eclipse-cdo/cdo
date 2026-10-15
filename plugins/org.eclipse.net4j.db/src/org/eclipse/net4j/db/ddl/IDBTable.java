/*
 * Copyright (c) 2008, 2011-2013, 2015, 2019, 2025, 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.db.ddl;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.dml.SQL;
import org.eclipse.net4j.db.dml.SQLTableRef;

/**
 * A table specification in a {@link IDBSchema DB schema}.
 *
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IDBTable extends IDBSchemaElement
{
  /**
   * @since 4.2
   */
  @Override
  public IDBSchema getParent();

  public IDBField addField(String name, DBType type);

  public IDBField addField(String name, DBType type, boolean notNull);

  public IDBField addField(String name, DBType type, int precision);

  public IDBField addField(String name, DBType type, int precision, boolean notNull);

  public IDBField addField(String name, DBType type, int precision, int scale);

  public IDBField addField(String name, DBType type, int precision, int scale, boolean notNull);

  /**
   * @since 4.14
   */
  public IDBField ensureField(String name, DBType type, int precision, int scale, boolean notNull);

  /**
   * @since 4.2
   */
  public IDBField getFieldSafe(String name) throws SchemaElementNotFoundException;

  public IDBField getField(String name);

  public IDBField getField(int position);

  public int getFieldCount();

  public IDBField[] getFields();

  /**
   * @since 4.2
   */
  public IDBField[] getFields(String... fieldNames) throws SchemaElementNotFoundException;

  /**
   * @since 4.5
   */
  public boolean hasIndexFor(IDBField... fields);

  /**
   * @since 4.2
   */
  public IDBIndex addIndex(String name, IDBIndex.Type type, IDBField... fields);

  /**
   * @since 4.2
   */
  public IDBIndex addIndex(String name, IDBIndex.Type type, String... fieldNames) throws SchemaElementNotFoundException;

  /**
   * @since 4.2
   */
  public IDBIndex addIndexEmpty(String name, IDBIndex.Type type);

  public IDBIndex addIndex(IDBIndex.Type type, IDBField... fields);

  /**
   * @since 4.2
   */
  public IDBIndex addIndex(IDBIndex.Type type, String... fieldNames) throws SchemaElementNotFoundException;

  /**
   * @since 4.2
   */
  public IDBIndex addIndexEmpty(IDBIndex.Type type);

  /**
   * @since 4.2
   */
  public IDBIndex getIndexSafe(String name) throws SchemaElementNotFoundException;

  /**
   * @since 4.2
   */
  public IDBIndex getIndex(String name);

  /**
   * @since 4.2
   */
  public IDBIndex getIndex(int position);

  public int getIndexCount();

  public IDBIndex[] getIndices();

  public IDBIndex getPrimaryKeyIndex();

  /**
   * Creates a table reference with the given SQL alias.
   *
   * @param alias the alias used to qualify columns and render the table in SQL
   * @return an immutable reference to this table
   * @since 4.15
   */
  public default SQLTableRef as(String alias)
  {
    return SQL.as(this, alias);
  }

  /**
   * Creates legacy INSERT SQL text for this table.
   *
   * @return the INSERT statement text
   * @deprecated Use {@link SQL#insertInto(IDBTable)} to create a structured INSERT definition.
   */
  @Deprecated
  public String sqlInsert();

  /**
   * Supplies one primary schema table to DML factory and fluent APIs. A provider describes the
   * schema table itself; aliases and SQL occurrences remain represented by {@link SQLTableRef}.
   *
   * @author Eike Stepper
   * @since 4.15
   */
  public interface Provider
  {
    /**
     * Returns the primary schema table. DML APIs resolve this method when the provider is supplied.
     *
     * @return the primary schema table
     * @since 4.15
     */
    public IDBTable getTable();
  }
}
