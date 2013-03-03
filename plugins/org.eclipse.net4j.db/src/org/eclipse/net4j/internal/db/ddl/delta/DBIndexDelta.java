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
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.ddl.delta.IDBDelta;
import org.eclipse.net4j.db.ddl.delta.IDBDeltaVisitor;
import org.eclipse.net4j.db.ddl.delta.IDBIndexDelta;
import org.eclipse.net4j.db.ddl.delta.IDBIndexFieldDelta;
import org.eclipse.net4j.db.ddl.delta.IDBPropertyDelta;
import org.eclipse.net4j.internal.db.ddl.DBIndex;
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

  public DBIndexDelta(IDBDelta parent, String name, ChangeKind changeKind)
  {
    super(parent, name, changeKind);
  }

  public DBIndexDelta(DBTableDelta parent, IDBIndex index, IDBIndex oldIndex)
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
        DBIndexFieldDelta indexFieldDelta = new DBIndexFieldDelta(DBIndexDelta.this, indexField, oldIndexField);
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

  public Map<String, IDBIndexFieldDelta> getIndexFieldDeltas()
  {
    return Collections.unmodifiableMap(indexFieldDeltas);
  }

  public boolean isEmpty()
  {
    return indexFieldDeltas.isEmpty();
  }

  public IDBIndexFieldDelta[] getElements()
  {
    IDBIndexFieldDelta[] elements = indexFieldDeltas.values().toArray(new IDBIndexFieldDelta[indexFieldDeltas.size()]);
    Arrays.sort(elements);
    return elements;
  }

  public void addIndexFieldDelta(DBIndexFieldDelta indexFieldDelta)
  {
    indexFieldDeltas.put(indexFieldDelta.getName(), indexFieldDelta);
  }

  public void accept(IDBDeltaVisitor visitor)
  {
    visitor.visit(this);
    for (IDBIndexFieldDelta indexFieldDelta : getElements())
    {
      indexFieldDelta.accept(visitor);
    }
  }

  public IDBIndex getElement(IDBSchema schema)
  {
    IDBTable table = getParent().getElement(schema);
    if (table == null)
    {
      return null;
    }

    return table.getIndex(getName());
  }
}
