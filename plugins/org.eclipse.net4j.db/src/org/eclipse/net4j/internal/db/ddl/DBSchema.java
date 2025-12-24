/*
 * Copyright (c) 2008, 2013, 2015, 2016, 2019, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.db.ddl;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.db.IDBRowHandler;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex;
import org.eclipse.net4j.db.ddl.IDBIndexField;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.IDBSchemaElement;
import org.eclipse.net4j.db.ddl.IDBSchemaVisitor;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.ddl.SchemaElementNotFoundException;
import org.eclipse.net4j.db.ddl.delta.IDBSchemaDelta;
import org.eclipse.net4j.internal.db.ddl.delta.DBSchemaDelta;
import org.eclipse.net4j.spi.db.ddl.InternalDBSchema;
import org.eclipse.net4j.util.StringUtil;

import javax.sql.DataSource;

import java.io.PrintStream;
import java.sql.Connection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class DBSchema extends DBSchemaElement implements InternalDBSchema
{
  private static final long serialVersionUID = 1L;

  private static int indexCounter;

  private boolean caseSensitive;

  private boolean qualifiedTableNames;

  private Map<String, IDBTable> tables = new HashMap<>();

  private transient boolean locked;

  public DBSchema(String name, boolean caseSensitive, boolean qualifiedTableNames)
  {
    super(name);
    this.caseSensitive = caseSensitive;
    this.qualifiedTableNames = qualifiedTableNames;
  }

  /**
   * @since 4.2
   */
  public DBSchema(IDBSchema source)
  {
    this(source.getName(), source.isCaseSensitive(), source.isQualifiedTableNames());

    for (IDBTable sourceTable : source.getTables())
    {
      IDBTable table = addTable(sourceTable.getName());

      for (IDBField sourceField : sourceTable.getFields())
      {
        table.addField(sourceField.getName(), sourceField.getType(), sourceField.getPrecision(), sourceField.getScale(), sourceField.isNotNull());
      }

      for (IDBIndex sourceIndex : sourceTable.getIndices())
      {
        IDBIndex index = table.addIndexEmpty(sourceIndex.getName(), sourceIndex.getType());
        DBUtil.setOptional(index, DBUtil.isOptional(sourceIndex));

        for (IDBField sourceField : sourceIndex.getFields())
        {
          IDBField field = table.getField(sourceField.getPosition());
          index.addIndexField(field);
        }
      }
    }
  }

  /**
   * Constructor for deserialization.
   *
   * @since 4.2
   */
  protected DBSchema()
  {
  }

  @Override
  public IDBSchema getWrapper()
  {
    return (IDBSchema)super.getWrapper();
  }

  /**
   * @since 4.2
   */
  @Override
  public SchemaElementType getSchemaElementType()
  {
    return SchemaElementType.SCHEMA;
  }

  @Override
  public IDBSchema getSchema()
  {
    return this;
  }

  /**
   * @since 4.2
   */
  @Override
  public final IDBSchemaElement getParent()
  {
    return null;
  }

  @Override
  public String getFullName()
  {
    return getName();
  }

  @Override
  public boolean isCaseSensitive()
  {
    return caseSensitive;
  }

  @Override
  public boolean isQualifiedTableNames()
  {
    return qualifiedTableNames;
  }

  @Override
  public int compareNames(String name1, String name2)
  {
    name1 = StringUtil.safe(name1);
    name2 = StringUtil.safe(name2);

    if (caseSensitive)
    {
      return name1.compareTo(name2);
    }

    return name1.compareToIgnoreCase(name2);
  }

  @Override
  public boolean equalNames(String name1, String name2)
  {
    name1 = StringUtil.safe(name1);
    name2 = StringUtil.safe(name2);

    if (caseSensitive)
    {
      return name1.equals(name2);
    }

    return name1.equalsIgnoreCase(name2);
  }

  /**
   * @since 4.2
   */
  @Override
  @SuppressWarnings("unchecked")
  public final <T extends IDBSchemaElement> T findElement(IDBSchemaElement prototype)
  {
    SchemaElementType schemaElementType = prototype.getSchemaElementType();
    switch (schemaElementType)
    {
    case SCHEMA:
      return (T)(prototype.equals(this) ? this : null);

    case TABLE:
      return (T)getElement(IDBTable.class, prototype.getName());

    case FIELD:
    {
      IDBTable table = getElement(IDBTable.class, prototype.getParent().getName());
      if (table == null)
      {
        return null;
      }

      return (T)table.getElement(IDBField.class, prototype.getName());
    }

    case INDEX:
    {
      IDBTable table = getElement(IDBTable.class, prototype.getParent().getName());
      if (table == null)
      {
        return null;
      }

      return (T)table.getElement(IDBIndex.class, prototype.getName());
    }

    case INDEX_FIELD:
    {
      IDBTable table = getElement(IDBTable.class, prototype.getParent().getParent().getName());
      if (table == null)
      {
        return null;
      }

      IDBIndex index = table.getElement(IDBIndex.class, prototype.getParent().getName());
      if (index == null)
      {
        return null;
      }

      return (T)index.getElement(IDBIndexField.class, prototype.getName());
    }

    default:
      throw new IllegalStateException("Illegal schema element type: " + schemaElementType);
    }
  }

  /**
   * @since 2.0
   */
  @Override
  public IDBTable addTable(String name) throws DBException
  {
    assertUnlocked();
    String key = makeKey(name);

    IDBTable existingTable = tables.get(key);
    if (existingTable != null)
    {
      throw new DBException("IDBTable exists: " + existingTable.getName()); //$NON-NLS-1$
    }

    IDBTable table = new DBTable(this, name);
    tables.put(key, table);
    resetElements();
    return table;
  }

  /**
   * @since 4.0
   */
  @Override
  public IDBTable removeTable(String name)
  {
    assertUnlocked();
    String key = makeKey(name);

    IDBTable table = tables.remove(key);
    resetElements();
    return table;
  }

  /**
   * @since 4.2
   */
  @Override
  public final IDBTable getTableSafe(String name) throws SchemaElementNotFoundException
  {
    IDBTable table = getTable(name);
    if (table == null)
    {
      throw new SchemaElementNotFoundException(this, SchemaElementType.TABLE, name);
    }

    return table;
  }

  /**
   * @since 2.0
   */
  @Override
  public IDBTable getTable(String name)
  {
    String key = makeKey(name);
    return tables.get(key);
  }

  /**
   * @since 2.0
   */
  @Override
  public IDBTable[] getTables()
  {
    return tables.values().toArray(new IDBTable[tables.size()]);
  }

  /**
   * @since 4.2
   */
  @Override
  public void remove()
  {
    assertUnlocked();
    tables.clear();
  }

  @Override
  public boolean isLocked()
  {
    return locked;
  }

  @Override
  public boolean lock()
  {
    return locked = true;
  }

  /**
   * @since 4.2
   */
  @Override
  public boolean unlock()
  {
    return locked = false;
  }

  @Override
  public void assertUnlocked() throws DBException
  {
    if (locked)
    {
      throw new DBException("Schema locked: " + this); //$NON-NLS-1$
    }
  }

  @Override
  public Set<IDBTable> create(IDBAdapter dbAdapter, Connection connection) throws DBException
  {
    return dbAdapter.createTables(tables.values(), connection);
  }

  @Override
  public Set<IDBTable> create(IDBAdapter dbAdapter, DataSource dataSource) throws DBException
  {
    return create(dbAdapter, dbAdapter.createConnectionProvider(dataSource));
  }

  @Override
  public Set<IDBTable> create(IDBAdapter dbAdapter, IDBConnectionProvider connectionProvider) throws DBException
  {
    Connection connection = null;

    try
    {
      connection = connectionProvider.getConnection();
      if (connection == null)
      {
        throw new DBException("No connection available from " + connectionProvider); //$NON-NLS-1$
      }

      return create(dbAdapter, connection);
    }
    finally
    {
      DBUtil.close(connection);
    }
  }

  @Override
  public void drop(IDBAdapter dbAdapter, Connection connection) throws DBException
  {
    dbAdapter.dropTables(tables.values(), connection);
  }

  @Override
  public void drop(IDBAdapter dbAdapter, DataSource dataSource) throws DBException
  {
    drop(dbAdapter, dbAdapter.createConnectionProvider(dataSource));
  }

  @Override
  public void drop(IDBAdapter dbAdapter, IDBConnectionProvider connectionProvider) throws DBException
  {
    Connection connection = null;

    try
    {
      connection = connectionProvider.getConnection();
      drop(dbAdapter, connection);
    }
    finally
    {
      DBUtil.close(connection);
    }
  }

  @Override
  public void export(Connection connection, PrintStream out) throws DBException
  {
    for (IDBTable table : getTables())
    {
      export(table, connection, out);
    }
  }

  private void export(final IDBTable table, Connection connection, final PrintStream out)
  {
    if (DBUtil.select(connection, new IDBRowHandler()
    {
      @Override
      public boolean handle(int row, Object... values)
      {
        if (row == 0)
        {
          String tableName = table.getName();
          out.println(tableName);
          for (int i = 0; i < tableName.length(); i++)
          {
            out.print("="); //$NON-NLS-1$
          }

          out.println();
        }

        out.println(Arrays.asList(values));
        return true;
      }
    }, table.getFields()) > 0)

    {
      out.println();
    }
  }

  @Override
  public void export(DataSource dataSource, PrintStream out) throws DBException
  {
    export(DBUtil.createConnectionProvider(dataSource), out);
  }

  @Override
  public void export(IDBConnectionProvider connectionProvider, PrintStream out) throws DBException
  {
    Connection connection = null;

    try
    {
      connection = connectionProvider.getConnection();
      export(connection, out);
    }
    finally
    {
      DBUtil.close(connection);
    }
  }

  /**
   * @since 4.2
   */
  @Override
  public IDBSchemaDelta compare(IDBSchema oldSchema)
  {
    return new DBSchemaDelta(this, oldSchema);
  }

  /**
   * @since 4.2
   */
  @Override
  public String createIndexName(IDBTable table, IDBIndex.Type type, IDBField[] fields, int position)
  {
    return "I" + System.currentTimeMillis() + "_" + ++indexCounter;
  }

  /**
   * @since 4.2
   */
  @Override
  protected void collectElements(List<IDBSchemaElement> elements)
  {
    elements.addAll(tables.values());
  }

  /**
   * @since 4.2
   */
  @Override
  protected void doAccept(IDBSchemaVisitor visitor)
  {
    visitor.visit(this);
  }

  public String makeKey(String name)
  {
    if (caseSensitive || name == null)
    {
      return name;
    }

    return name.toUpperCase();
  }
}
