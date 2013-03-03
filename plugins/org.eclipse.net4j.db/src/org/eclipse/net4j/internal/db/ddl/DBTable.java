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
import org.eclipse.net4j.spi.db.DBSchema;

import java.util.ArrayList;
import java.util.Iterator;
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
      throw new DBException("Field exists: " + name); //$NON-NLS-1$
    }

    int position = fields.size();
    DBField field = new DBField(this, name, type, precision, scale, notNull, position);
    fields.add(field);
    return field;
  }

  public void removeField(IDBField fieldToRemove)
  {
    schema.assertUnlocked();

    boolean found = false;
    for (Iterator<DBField> it = fields.iterator(); it.hasNext();)
    {
      DBField field = it.next();
      if (found)
      {
        field.setPosition(field.getPosition() - 1);
      }
      else if (field == fieldToRemove)
      {
        it.remove();
        found = true;
      }
    }
  }

  public DBField getField(String name)
  {
    for (DBField field : fields)
    {
      if (field.getName().equals(name))
      {
        return field;
      }
    }

    return null;
  }

  public DBField getField(int position)
  {
    return fields.get(position);
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

  public void removeIndex(IDBIndex indexToRemove)
  {
    schema.assertUnlocked();

    boolean found = false;
    for (Iterator<DBIndex> it = indices.iterator(); it.hasNext();)
    {
      DBIndex index = it.next();
      if (found)
      {
        index.setPosition(index.getPosition() - 1);
      }
      else if (index == indexToRemove)
      {
        it.remove();
        found = true;
      }
    }
  }

  public DBIndex getIndex(String name)
  {
    for (DBIndex index : indices)
    {
      if (index.getName().equals(name))
      {
        return index;
      }
    }

    return null;
  }

  public DBIndex getIndex(int position)
  {
    return indices.get(position);
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

  public void remove()
  {
    schema.removeTable(name);
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
}
