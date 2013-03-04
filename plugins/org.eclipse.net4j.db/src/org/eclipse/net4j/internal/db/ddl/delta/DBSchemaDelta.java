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
package org.eclipse.net4j.internal.db.ddl.delta;

import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.ddl.delta.IDBDeltaVisitor;
import org.eclipse.net4j.db.ddl.delta.IDBSchemaDelta;
import org.eclipse.net4j.db.ddl.delta.IDBTableDelta;
import org.eclipse.net4j.internal.db.ddl.DBTable;
import org.eclipse.net4j.spi.db.DBSchema;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public final class DBSchemaDelta extends DBDelta implements IDBSchemaDelta
{
  private static final long serialVersionUID = 1L;

  private Map<String, IDBTableDelta> tableDeltas = new HashMap<String, IDBTableDelta>();

  public DBSchemaDelta(String name, ChangeKind changeKind)
  {
    super(null, name, changeKind);
  }

  public DBSchemaDelta(DBSchema schema, IDBSchema oldSchema)
  {
    this(schema.getName(), oldSchema == null ? ChangeKind.ADDED : ChangeKind.CHANGED);

    IDBTable[] tables = schema.getTables();
    IDBTable[] oldTables = oldSchema == null ? DBSchema.NO_TABLES : oldSchema.getTables();
    compare(tables, oldTables, new SchemaElementComparator<IDBTable>()
    {
      public void compare(IDBTable table, IDBTable oldTable)
      {
        DBTableDelta tableDelta = new DBTableDelta(DBSchemaDelta.this, (DBTable)table, (DBTable)oldTable);
        addTableDelta(tableDelta);
      }
    });
  }

  /**
   * Constructor for deserialization.
   */
  protected DBSchemaDelta()
  {
  }

  public Map<String, IDBTableDelta> getTableDeltas()
  {
    return Collections.unmodifiableMap(tableDeltas);
  }

  public boolean isEmpty()
  {
    return tableDeltas.isEmpty();
  }

  public DBTableDelta[] getElements()
  {
    DBTableDelta[] elements = tableDeltas.values().toArray(new DBTableDelta[tableDeltas.size()]);
    Arrays.sort(elements);
    return elements;
  }

  public int compareTo(IDBSchemaDelta o)
  {
    return getName().compareTo(o.getName());
  }

  public void addTableDelta(IDBTableDelta tableDelta)
  {
    tableDeltas.put(tableDelta.getName(), tableDelta);
  }

  public void accept(IDBDeltaVisitor visitor)
  {
    visitor.visit(this);
    for (DBDelta tableDelta : getElements())
    {
      tableDelta.accept(visitor);
    }
  }

  public DBSchema getElement(IDBSchema schema)
  {
    return (DBSchema)schema;
  }

  public void applyTo(IDBSchema schema)
  {
    IDBDeltaVisitor visitor = new IDBDeltaVisitor.Applier(schema);
    accept(visitor);
  }
}
