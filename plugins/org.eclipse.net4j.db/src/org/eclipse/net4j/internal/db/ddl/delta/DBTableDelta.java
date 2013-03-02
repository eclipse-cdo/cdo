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

import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.ddl.delta.IDBDelta;
import org.eclipse.net4j.db.ddl.delta.IDBFieldDelta;
import org.eclipse.net4j.db.ddl.delta.IDBIndexDelta;
import org.eclipse.net4j.db.ddl.delta.IDBSchemaDelta;
import org.eclipse.net4j.db.ddl.delta.IDBTableDelta;
import org.eclipse.net4j.db.ddl.delta.IDBTableElementDelta;
import org.eclipse.net4j.internal.db.ddl.DBTable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public final class DBTableDelta extends DBDelta implements IDBTableDelta
{
  private static final long serialVersionUID = 1L;

  private Map<String, IDBFieldDelta> fieldDeltas = new HashMap<String, IDBFieldDelta>();

  private Map<String, IDBIndexDelta> indexDeltas = new HashMap<String, IDBIndexDelta>();

  public DBTableDelta(IDBDelta parent, String name, ChangeKind changeKind)
  {
    super(parent, name, changeKind);
  }

  public DBTableDelta(DBSchemaDelta parent, IDBTable table, IDBTable oldTable)
  {
    this(parent, table.getName(), oldTable == null ? ChangeKind.ADDED : ChangeKind.CHANGED);

    IDBField[] fields = table.getFields();
    IDBField[] oldFields = oldTable == null ? DBTable.NO_FIELDS : oldTable.getFields();
    compare(fields, oldFields, new SchemaElementComparator<IDBField>()
    {
      public void compare(IDBField field, IDBField oldField)
      {
        DBFieldDelta fieldDelta = new DBFieldDelta(DBTableDelta.this, field, oldField);
        addFieldDelta(fieldDelta);
      }
    });

    IDBIndex[] indices = table.getIndices();
    IDBIndex[] oldIndices = oldTable == null ? DBTable.NO_INDICES : oldTable.getIndices();
    compare(indices, oldIndices, new SchemaElementComparator<IDBIndex>()
    {
      public void compare(IDBIndex index, IDBIndex oldIndex)
      {
        DBIndexDelta indexDelta = new DBIndexDelta(DBTableDelta.this, index, oldIndex);
        addIndexDelta(indexDelta);
      }
    });
  }

  /**
   * Constructor for deserialization.
   */
  protected DBTableDelta()
  {
  }

  @Override
  public IDBSchemaDelta getParent()
  {
    return (IDBSchemaDelta)super.getParent();
  }

  public Map<String, IDBFieldDelta> getFieldDeltas()
  {
    return Collections.unmodifiableMap(fieldDeltas);
  }

  public Map<String, IDBIndexDelta> getIndexDeltas()
  {
    return Collections.unmodifiableMap(indexDeltas);
  }

  public boolean isEmpty()
  {
    return fieldDeltas.isEmpty() && indexDeltas.isEmpty();
  }

  public IDBTableElementDelta[] getElements()
  {
    IDBTableElementDelta[] elements = new IDBTableElementDelta[fieldDeltas.size() + indexDeltas.size()];
    int i = 0;

    for (IDBFieldDelta fieldDelta : fieldDeltas.values())
    {
      elements[i++] = fieldDelta;
    }

    for (IDBIndexDelta indexDelta : indexDeltas.values())
    {
      elements[i++] = indexDelta;
    }

    Arrays.sort(elements);
    return elements;
  }

  public int compareTo(IDBTableDelta o)
  {
    return getName().compareTo(o.getName());
  }

  public void addFieldDelta(IDBFieldDelta fieldDelta)
  {
    fieldDeltas.put(fieldDelta.getName(), fieldDelta);
  }

  public void addIndexDelta(IDBIndexDelta indexDelta)
  {
    indexDeltas.put(indexDelta.getName(), indexDelta);
  }
}
