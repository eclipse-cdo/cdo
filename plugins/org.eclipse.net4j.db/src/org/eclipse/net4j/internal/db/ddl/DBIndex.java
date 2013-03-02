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

import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex;
import org.eclipse.net4j.db.ddl.IDBIndexField;
import org.eclipse.net4j.db.ddl.IDBSchema;

/**
 * @author Eike Stepper
 */
public class DBIndex extends DBSchemaElement implements IDBIndex
{
  public static final IDBIndexField[] NO_INDEX_FIELDS = {};

  private DBTable table;

  private String name;

  private Type type;

  private IDBField[] fields;

  private IDBIndexField[] indexFields;

  public int position;

  public DBIndex(DBTable table, String name, Type type, IDBField[] fields, int position)
  {
    if (name == null)
    {
      name = "idx_" + table.getName() + "_" + position;
    }

    this.table = table;
    this.name = name;
    this.type = type;
    this.fields = fields;

    indexFields = new DBIndexField[fields.length];
    for (int i = 0; i < fields.length; i++)
    {
      IDBField field = fields[i];
      indexFields[i] = new DBIndexField(this, (DBField)field, i);
    }

    this.position = position;
  }

  public DBIndex(DBTable table, Type type, IDBField[] fields, int position)
  {
    this(table, null, type, fields, position);
  }

  public IDBSchema getSchema()
  {
    return table.getSchema();
  }

  public DBTable getTable()
  {
    return table;
  }

  public Type getType()
  {
    return type;
  }

  public IDBField getField(int position)
  {
    return fields[position];
  }

  public IDBIndexField getIndexField(int position)
  {
    return indexFields[position];
  }

  public int getFieldCount()
  {
    return indexFields.length;
  }

  public IDBField[] getFields()
  {
    return fields;
  }

  public IDBIndexField[] getIndexFields()
  {
    return indexFields;
  }

  public int getPosition()
  {
    return position;
  }

  public String getName()
  {
    return name;
  }

  public String getFullName()
  {
    return getName();
  }
}
