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

import org.eclipse.net4j.db.ddl.IDBIndex;
import org.eclipse.net4j.db.ddl.IDBIndexField;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.delta.IDBDeltaVisitor;
import org.eclipse.net4j.db.ddl.delta.IDBIndexDelta;
import org.eclipse.net4j.db.ddl.delta.IDBIndexFieldDelta;
import org.eclipse.net4j.db.ddl.delta.IDBPropertyDelta;
import org.eclipse.net4j.internal.db.ddl.DBIndex;
import org.eclipse.net4j.internal.db.ddl.DBIndexField;
import org.eclipse.net4j.internal.db.ddl.DBTable;
import org.eclipse.net4j.util.ObjectUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public final class DBIndexDelta extends DBTableElementDelta implements IDBIndexDelta
{
  private static final long serialVersionUID = 1L;

  private IDBIndex.Type type;

  private IDBIndex.Type oldType;

  private Map<String, IDBIndexFieldDelta> indexFieldDeltas = new HashMap<String, IDBIndexFieldDelta>();

  public DBIndexDelta(DBDelta parent, String name, ChangeKind changeKind)
  {
    super(parent, name, changeKind);
  }

  public DBIndexDelta(DBDelta parent, DBIndex index, DBIndex oldIndex)
  {
    this(parent, getName(index, oldIndex), getChangeKind(index, oldIndex));

    IDBIndex.Type type = index == null ? null : index.getType();
    IDBIndex.Type oldType = oldIndex == null ? null : oldIndex.getType();
    if (!ObjectUtil.equals(type, oldType))
    {
      addPropertyDelta(new DBPropertyDelta<IDBIndex.Type>(TYPE_PROPERTY, IDBPropertyDelta.Type.STRING, type, oldType));
    }

    IDBIndexField[] indexFields = index == null ? DBIndex.NO_INDEX_FIELDS : index.getIndexFields();
    IDBIndexField[] oldIndexFields = oldIndex == null ? DBIndex.NO_INDEX_FIELDS : oldIndex.getIndexFields();
    compare(indexFields, oldIndexFields, new SchemaElementComparator<IDBIndexField>()
    {
      public void compare(IDBIndexField indexField, IDBIndexField oldIndexField)
      {
        DBIndexFieldDelta indexFieldDelta = new DBIndexFieldDelta(DBIndexDelta.this, (DBIndexField)indexField,
            (DBIndexField)oldIndexField);
        addIndexFieldDelta(indexFieldDelta);
      }
    });
  }

  /**
   * Constructor for deserialization.
   */
  protected DBIndexDelta()
  {
  }

  public Type getTableElementType()
  {
    return Type.INDEX;
  }

  public IDBIndex.Type getType()
  {
    return type;
  }

  public void setType(IDBIndex.Type type)
  {
    this.type = type;
  }

  public IDBIndex.Type getOldType()
  {
    return oldType;
  }

  public void setOldType(IDBIndex.Type oldType)
  {
    this.oldType = oldType;
  }

  public DBIndexFieldDelta[] getIndexFieldDeltasSortedByPosition()
  {
    DBIndexFieldDelta[] result = indexFieldDeltas.values().toArray(new DBIndexFieldDelta[indexFieldDeltas.size()]);
    Arrays.sort(result, new PositionComparator());
    return result;
  }

  public Map<String, IDBIndexFieldDelta> getIndexFieldDeltas()
  {
    return Collections.unmodifiableMap(indexFieldDeltas);
  }

  public boolean isEmpty()
  {
    return indexFieldDeltas.isEmpty();
  }

  public DBIndexFieldDelta[] getElements()
  {
    return getIndexFieldDeltasSortedByPosition();
  }

  public void addIndexFieldDelta(DBIndexFieldDelta indexFieldDelta)
  {
    indexFieldDeltas.put(indexFieldDelta.getName(), indexFieldDelta);
  }

  public void accept(IDBDeltaVisitor visitor)
  {
    visitor.visit(this);
    for (IDBIndexFieldDelta indexFieldDelta : getIndexFieldDeltasSortedByPosition())
    {
      indexFieldDelta.accept(visitor);
    }
  }

  public DBIndex getElement(IDBSchema schema)
  {
    DBTable table = getParent().getElement(schema);
    if (table == null)
    {
      return null;
    }

    return table.getIndex(getName());
  }
}
