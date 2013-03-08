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
import org.eclipse.net4j.db.ddl.IDBSchemaElement;
import org.eclipse.net4j.db.ddl.IDBSchemaVisitor;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.spi.db.DBSchema;
import org.eclipse.net4j.spi.db.DBSchemaElement;

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

  private static final long serialVersionUID = 1L;

  private DBSchema schema;

  private List<DBField> fields = new ArrayList<DBField>();

  private List<DBIndex> indices = new ArrayList<DBIndex>();

  public DBTable(DBSchema schema, String name)
  {
    super(name);
    this.schema = schema;
  }

  /**
   * Constructor for deserialization.
   */
  protected DBTable()
  {
  }

  public SchemaElementType getSchemaElementType()
  {
    return SchemaElementType.TABLE;
  }

  public DBSchema getSchema()
  {
    return schema;
  }

  public DBSchema getParent()
  {
    return getSchema();
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
    assertUnlocked();

    if (getField(name) != null)
    {
      throw new DBException("Field exists: " + name); //$NON-NLS-1$
    }

    int position = fields.size();
    DBField field = new DBField(this, name, type, precision, scale, notNull, position);
    fields.add(field);
    resetElements();
    return field;
  }

  public void removeField(IDBField fieldToRemove)
  {
    assertUnlocked();

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

    resetElements();
  }

  public DBField getField(String name)
  {
    return findElement(getFields(), name);
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

  public DBIndex addIndex(Type type, IDBField... fields)
  {
    return addIndex(null, type, fields);
  }

  public DBIndex addIndex(String name, Type type, IDBField... fields)
  {
    assertUnlocked();

    int position = indices.size();
    if (name == null)
    {
      name = schema.createIndexName(this, type, fields, position);
    }

    if (getIndex(name) != null)
    {
      throw new DBException("Index exists: " + name); //$NON-NLS-1$
    }

    if (type == Type.PRIMARY_KEY)
    {
      for (DBIndex index : getIndices())
      {
        if (index.getType() == Type.PRIMARY_KEY)
        {
          throw new DBException("Primary key exists: " + index); //$NON-NLS-1$
        }
      }
    }

    DBIndex index = new DBIndex(this, name, type, fields, position);
    indices.add(index);
    resetElements();
    return index;
  }

  @SuppressWarnings("deprecation")
  public void removeIndex(IDBIndex indexToRemove)
  {
    assertUnlocked();

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

    resetElements();
  }

  public DBIndex getIndex(String name)
  {
    return findElement(getIndices(), name);
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
    return getName();
  }

  public void remove()
  {
    schema.removeTable(getName());
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

  @Override
  protected void collectElements(List<IDBSchemaElement> elements)
  {
    elements.addAll(fields);
    elements.addAll(indices);
  }

  @Override
  protected void doAccept(IDBSchemaVisitor visitor)
  {
    visitor.visit(this);
  }

  private void assertUnlocked()
  {
    schema.assertUnlocked();
  }
}
