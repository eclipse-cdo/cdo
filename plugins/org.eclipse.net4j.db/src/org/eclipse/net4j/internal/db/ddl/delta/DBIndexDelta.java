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
import org.eclipse.net4j.db.ddl.delta.IDBDelta;
import org.eclipse.net4j.db.ddl.delta.IDBDeltaVisitor;
import org.eclipse.net4j.db.ddl.delta.IDBIndexDelta;
import org.eclipse.net4j.db.ddl.delta.IDBIndexFieldDelta;
import org.eclipse.net4j.db.ddl.delta.IDBPropertyDelta;
import org.eclipse.net4j.internal.db.ddl.DBIndex;
import org.eclipse.net4j.internal.db.ddl.DBIndexField;
import org.eclipse.net4j.internal.db.ddl.DBTable;
import org.eclipse.net4j.util.ObjectUtil;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public final class DBIndexDelta extends DBDeltaWithProperties implements IDBIndexDelta
{
  private static final long serialVersionUID = 1L;

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
      addPropertyDelta(new DBPropertyDelta<IDBIndex.Type>(this, TYPE_PROPERTY, IDBPropertyDelta.Type.STRING, type,
          oldType));
    }

    IDBIndexField[] indexFields = index == null ? DBIndex.NO_INDEX_FIELDS : index.getIndexFields();
    IDBIndexField[] oldIndexFields = oldIndex == null ? DBIndex.NO_INDEX_FIELDS : oldIndex.getIndexFields();
    compare(indexFields, oldIndexFields, new SchemaElementComparator<IDBIndexField>()
    {
      public void compare(IDBIndexField indexField, IDBIndexField oldIndexField)
      {
        DBIndexFieldDelta indexFieldDelta = new DBIndexFieldDelta(DBIndexDelta.this, (DBIndexField)indexField,
            (DBIndexField)oldIndexField);
        if (!indexFieldDelta.isEmpty())
        {
          addIndexFieldDelta(indexFieldDelta);
        }
      }
    });
  }

  /**
   * Constructor for deserialization.
   */
  protected DBIndexDelta()
  {
  }

  public DeltaType getDeltaType()
  {
    return DeltaType.INDEX;
  }

  @Override
  public DBTableDelta getParent()
  {
    return (DBTableDelta)super.getParent();
  }

  public Map<String, IDBIndexFieldDelta> getIndexFieldDeltas()
  {
    return Collections.unmodifiableMap(indexFieldDeltas);
  }

  public DBIndexFieldDelta[] getIndexFieldDeltasSortedByPosition()
  {
    DBIndexFieldDelta[] result = indexFieldDeltas.values().toArray(new DBIndexFieldDelta[indexFieldDeltas.size()]);
    Arrays.sort(result);
    return result;
  }

  public DBIndex getSchemaElement(IDBSchema schema)
  {
    DBTable table = getParent().getSchemaElement(schema);
    if (table == null)
    {
      return null;
    }

    return table.getIndex(getName());
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("DBIndexDelta[name={0}, kind={1}, propertyDeltas={2}, indexFieldDeltas={3}]",
        getName(), getChangeKind(), getPropertyDeltas().values(), indexFieldDeltas.values());
  }

  public void addIndexFieldDelta(DBIndexFieldDelta indexFieldDelta)
  {
    indexFieldDeltas.put(indexFieldDelta.getName(), indexFieldDelta);
    resetElements();
  }

  @Override
  protected void doAccept(IDBDeltaVisitor visitor)
  {
    visitor.visit(this);
  }

  @Override
  protected void collectElements(List<IDBDelta> elements)
  {
    elements.addAll(indexFieldDeltas.values());
    super.collectElements(elements);
  }
}
