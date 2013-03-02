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
package org.eclipse.net4j.internal.db.ddl;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex;
import org.eclipse.net4j.db.ddl.IDBIndex.Type;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.ddl.delta.IDBDelta.ChangeKind;
import org.eclipse.net4j.internal.db.ddl.delta.DBSchemaDelta;
import org.eclipse.net4j.internal.db.ddl.delta.DBTableDelta;
import org.eclipse.net4j.spi.db.DBSchema;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class DBTable extends DBSchemaElement implements IDBTable
{
  public static final IDBField[] NO_FIELDS = {};

  public static final IDBIndex[] NO_INDICES = {};

  private DBSchema schema;

  private String name;

  private List<DBField> fields = new ArrayList<DBField>();

  private List<DBIndex> indices = new ArrayList<DBIndex>();

  public DBTable(DBSchema schema, String name)
  {
    this.schema = schema;
    this.name = name;
  }

  public DBSchema getSchema()
  {
    return schema;
  }

  public String getName()
  {
    return name;
  }

  public DBField addField(String name, DBType type)
  {
    return addField(name, type, IDBField.DEFAULT, IDBField.DEFAULT, false);
  }

  public DBField addField(String name, DBType type, boolean notNull)
  {
    return addField(name, type, IDBField.DEFAULT, IDBField.DEFAULT, notNull);
  }

  public DBField addField(String name, DBType type, int precision)
  {
    return addField(name, type, precision, IDBField.DEFAULT, false);
  }

  public DBField addField(String name, DBType type, int precision, boolean notNull)
  {
    return addField(name, type, precision, IDBField.DEFAULT, notNull);
  }

  public DBField addField(String name, DBType type, int precision, int scale)
  {
    return addField(name, type, precision, scale, false);
  }

  public DBField addField(String name, DBType type, int precision, int scale, boolean notNull)
  {
    schema.assertUnlocked();
    if (getField(name) != null)
    {
      throw new DBException("DBField exists: " + name); //$NON-NLS-1$
    }

    DBField field = new DBField(this, name, type, precision, scale, notNull, fields.size());
    fields.add(field);
    return field;
  }

  public DBField getField(String name)
  {
    for (DBField field : fields)
    {
      if (name.equals(field.getName()))
      {
        return field;
      }
    }

    return null;
  }

  public DBField getField(int index)
  {
    return fields.get(index);
  }

  public int getFieldCount()
  {
    return fields.size();
  }

  public DBField[] getFields()
  {
    return fields.toArray(new DBField[fields.size()]);
  }

  public DBIndex addIndex(String name, Type type, IDBField... fields)
  {
    schema.assertUnlocked();
    DBIndex index = new DBIndex(this, name, type, fields, indices.size());
    indices.add(index);
    return index;
  }

  public DBIndex addIndex(Type type, IDBField... fields)
  {
    return addIndex(null, type, fields);
  }

  public int getIndexCount()
  {
    return indices.size();
  }

  public DBIndex[] getIndices()
  {
    return indices.toArray(new DBIndex[indices.size()]);
  }

  public IDBIndex getPrimaryKeyIndex()
  {
    for (IDBIndex index : indices)
    {
      if (index.getType() == IDBIndex.Type.PRIMARY_KEY)
      {
        return index;
      }
    }

    return null;
  }

  public String getFullName()
  {
    return name;
  }

  public String sqlInsert()
  {
    StringBuilder builder = new StringBuilder();
    builder.append("INSERT INTO "); //$NON-NLS-1$
    builder.append(getName());
    builder.append(" VALUES ("); //$NON-NLS-1$

    for (int i = 0; i < fields.size(); i++)
    {
      if (i > 0)
      {
        builder.append(", "); //$NON-NLS-1$
      }

      builder.append("?"); //$NON-NLS-1$
    }

    builder.append(")"); //$NON-NLS-1$
    return builder.toString();
  }

  public DBTableDelta createDelta(DBSchemaDelta parent)
  {
    return new DBTableDelta(parent, name, ChangeKind.ADDED);
  }
}
