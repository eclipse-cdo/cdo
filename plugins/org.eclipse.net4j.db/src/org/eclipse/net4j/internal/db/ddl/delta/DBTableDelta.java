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
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.delta.IDBDeltaVisitor;
import org.eclipse.net4j.db.ddl.delta.IDBFieldDelta;
import org.eclipse.net4j.db.ddl.delta.IDBIndexDelta;
import org.eclipse.net4j.db.ddl.delta.IDBTableDelta;
import org.eclipse.net4j.internal.db.ddl.DBField;
import org.eclipse.net4j.internal.db.ddl.DBIndex;
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

  public DBTableDelta(DBDelta parent, String name, ChangeKind changeKind)
  {
    super(parent, name, changeKind);
  }

  public DBTableDelta(DBSchemaDelta parent, DBTable table, DBTable oldTable)
  {
    this(parent, table.getName(), oldTable == null ? ChangeKind.ADDED : ChangeKind.CHANGED);

    IDBField[] fields = table.getFields();
    IDBField[] oldFields = oldTable == null ? DBTable.NO_FIELDS : oldTable.getFields();
    compare(fields, oldFields, new SchemaElementComparator<IDBField>()
    {
      public void compare(IDBField field, IDBField oldField)
      {
        DBFieldDelta fieldDelta = new DBFieldDelta(DBTableDelta.this, (DBField)field, (DBField)oldField);
        addFieldDelta(fieldDelta);
      }
    });

    IDBIndex[] indices = table.getIndices();
    IDBIndex[] oldIndices = oldTable == null ? DBTable.NO_INDICES : oldTable.getIndices();
    compare(indices, oldIndices, new SchemaElementComparator<IDBIndex>()
    {
      public void compare(IDBIndex index, IDBIndex oldIndex)
      {
        DBIndexDelta indexDelta = new DBIndexDelta(DBTableDelta.this, (DBIndex)index, (DBIndex)oldIndex);
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
  public DBSchemaDelta getParent()
  {
    return (DBSchemaDelta)super.getParent();
  }

  public DBFieldDelta[] getFieldDeltasSortedByPosition()
  {
    DBFieldDelta[] result = fieldDeltas.values().toArray(new DBFieldDelta[fieldDeltas.size()]);
    Arrays.sort(result, new PositionComparator());
    return result;
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

  public DBTableElementDelta[] getElements()
  {
    DBTableElementDelta[] elements = new DBTableElementDelta[fieldDeltas.size() + indexDeltas.size()];
    int i = 0;

    for (IDBFieldDelta fieldDelta : getFieldDeltasSortedByPosition())
    {
      elements[i++] = (DBTableElementDelta)fieldDelta;
    }

    for (IDBIndexDelta indexDelta : indexDeltas.values())
    {
      elements[i++] = (DBTableElementDelta)indexDelta;
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

  public void accept(IDBDeltaVisitor visitor)
  {
    visitor.visit(this);

    for (IDBFieldDelta fieldDelta : getFieldDeltasSortedByPosition())
    {
      fieldDelta.accept(visitor);
    }

    for (IDBIndexDelta indexDelta : getIndexDeltas().values())
    {
      indexDelta.accept(visitor);
    }
  }

  public DBTable getElement(IDBSchema schema)
  {
    return (DBTable)schema.getTable(getName());
  }
}
