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
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex;
import org.eclipse.net4j.db.ddl.IDBIndexField;
import org.eclipse.net4j.spi.db.DBSchema;
import org.eclipse.net4j.spi.db.DBSchemaElement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class DBIndex extends DBSchemaElement implements IDBIndex
{
  public static final IDBIndexField[] NO_INDEX_FIELDS = {};

  private static final long serialVersionUID = 1L;

  private DBTable table;

  private Type type;

  private List<DBIndexField> indexFields = new ArrayList<DBIndexField>();

  public int position;

  public DBIndex(DBTable table, String name, Type type, IDBField[] fields, int position)
  {
    super(name);
    this.table = table;
    this.type = type;

    for (int i = 0; i < fields.length; i++)
    {
      IDBField field = fields[i];
      addIndexField(field);
    }

    this.position = position;
  }

  /**
   * Constructor for deserialization.
   */
  protected DBIndex()
  {
  }

  public DBSchema getSchema()
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

  public void setType(Type type)
  {
    table.getSchema().assertUnlocked();
    this.type = type;
  }

  public int getPosition()
  {
    return position;
  }

  public void setPosition(int position)
  {
    table.getSchema().assertUnlocked();
    this.position = position;
  }

  public DBIndexField addIndexField(IDBField field)
  {
    table.getSchema().assertUnlocked();

    if (field.getTable() != table)
    {
      throw new DBException("Index field is from different table: " + field); //$NON-NLS-1$
    }

    String name = field.getName();
    if (getIndexField(name) != null)
    {
      throw new DBException("Index field exists: " + name); //$NON-NLS-1$
    }

    int position = indexFields.size();
    DBIndexField indexField = new DBIndexField(this, (DBField)field, position);
    indexFields.add(indexField);
    return indexField;
  }

  public void removeIndexField(IDBIndexField indexFieldToRemove)
  {
    table.getSchema().assertUnlocked();

    boolean found = false;
    for (Iterator<DBIndexField> it = indexFields.iterator(); it.hasNext();)
    {
      DBIndexField indexField = it.next();
      if (found)
      {
        indexField.setPosition(indexField.getPosition() - 1);
      }
      else if (indexField == indexFieldToRemove)
      {
        it.remove();
        found = true;
      }
    }
  }

  public DBIndexField getIndexField(String name)
  {
    return findElement(getIndexFields(), name);
  }

  public DBIndexField getIndexField(int position)
  {
    return indexFields.get(position);
  }

  public DBField getField(String name)
  {
    name = name(name);
    for (DBIndexField indexField : indexFields)
    {
      if (indexField.getName() == name)
      {
        return indexField.getField();
      }
    }

    return null;
  }

  public DBField getField(int position)
  {
    return indexFields.get(position).getField();
  }

  public int getFieldCount()
  {
    return indexFields.size();
  }

  public DBIndexField[] getIndexFields()
  {
    return indexFields.toArray(new DBIndexField[indexFields.size()]);
  }

  public DBField[] getFields()
  {
    DBField[] fields = new DBField[indexFields.size()];
    for (int i = 0; i < fields.length; i++)
    {
      fields[i] = getField(i);
    }

    return fields;
  }

  public String getFullName()
  {
    return getName();
  }

  public void remove()
  {
    table.removeIndex(this);
  }
}
