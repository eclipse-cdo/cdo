/*
 * Copyright (c) 2008, 2009, 2011-2013, 2015, 2019, 2023 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.IDBSchemaElement;
import org.eclipse.net4j.db.ddl.IDBSchemaVisitor;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.ddl.SchemaElementNotFoundException;
import org.eclipse.net4j.spi.db.ddl.InternalDBField;
import org.eclipse.net4j.spi.db.ddl.InternalDBSchema;
import org.eclipse.net4j.spi.db.ddl.InternalDBTable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class DBTable extends DBSchemaElement implements InternalDBTable
{
  private static final long serialVersionUID = 1L;

  private IDBSchema schema;

  private List<IDBField> fields = new ArrayList<>();

  private List<IDBIndex> indices = new ArrayList<>();

  public DBTable(IDBSchema schema, String name)
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

  @Override
  public IDBTable getWrapper()
  {
    return (IDBTable)super.getWrapper();
  }

  @Override
  public SchemaElementType getSchemaElementType()
  {
    return SchemaElementType.TABLE;
  }

  @Override
  public IDBSchema getSchema()
  {
    return schema;
  }

  @Override
  public IDBSchema getParent()
  {
    return getSchema();
  }

  @Override
  public IDBField addField(String name, DBType type)
  {
    return addField(name, type, IDBField.DEFAULT, IDBField.DEFAULT, false);
  }

  @Override
  public IDBField addField(String name, DBType type, boolean notNull)
  {
    return addField(name, type, IDBField.DEFAULT, IDBField.DEFAULT, notNull);
  }

  @Override
  public IDBField addField(String name, DBType type, int precision)
  {
    return addField(name, type, precision, IDBField.DEFAULT, false);
  }

  @Override
  public IDBField addField(String name, DBType type, int precision, boolean notNull)
  {
    return addField(name, type, precision, IDBField.DEFAULT, notNull);
  }

  @Override
  public IDBField addField(String name, DBType type, int precision, int scale)
  {
    return addField(name, type, precision, scale, false);
  }

  @Override
  public IDBField addField(String name, DBType type, int precision, int scale, boolean notNull)
  {
    assertUnlocked();

    if (getField(name) != null)
    {
      throw new DBException("Field exists: " + name); //$NON-NLS-1$
    }

    int position = fields.size();
    IDBField field = new DBField(this, name, type, precision, scale, notNull, position);
    fields.add(field);
    resetElements();
    return field;
  }

  @Override
  public void removeField(IDBField fieldToRemove)
  {
    assertUnlocked();

    boolean found = false;
    for (Iterator<IDBField> it = fields.iterator(); it.hasNext();)
    {
      IDBField field = it.next();
      if (found)
      {
        ((InternalDBField)field).setPosition(field.getPosition() - 1);
      }
      else if (field == fieldToRemove)
      {
        it.remove();
        found = true;
      }
    }

    resetElements();
  }

  @Override
  public IDBField getFieldSafe(String name) throws SchemaElementNotFoundException
  {
    IDBField field = getField(name);
    if (field == null)
    {
      throw new SchemaElementNotFoundException(this, SchemaElementType.FIELD, name);
    }

    return field;
  }

  @Override
  public IDBField getField(String name)
  {
    InternalDBSchema schema = (InternalDBSchema)getSchema();
    IDBField[] fields = getFields();
    return findElement(schema, fields, name);
  }

  @Override
  public IDBField getField(int position)
  {
    return fields.get(position);
  }

  @Override
  public int getFieldCount()
  {
    return fields.size();
  }

  @Override
  public IDBField[] getFields()
  {
    return fields.toArray(new IDBField[fields.size()]);
  }

  @Override
  public IDBField[] getFields(String... fieldNames) throws SchemaElementNotFoundException
  {
    List<IDBField> result = new ArrayList<>();
    for (String fieldName : fieldNames)
    {
      IDBField field = getFieldSafe(fieldName);
      result.add(field);
    }

    return result.toArray(new IDBField[result.size()]);
  }

  @Override
  public boolean hasIndexFor(IDBField... fields)
  {
    for (IDBIndex index : indices)
    {
      IDBField[] indexFields = index.getFields();
      if (startsWith(indexFields, fields))
      {
        return true;
      }
    }

    return false;
  }

  @Override
  public IDBIndex addIndex(String name, IDBIndex.Type type, IDBField... fields)
  {
    assertUnlocked();

    if (name == null)
    {
      int position = indices.size();
      name = ((InternalDBSchema)schema).createIndexName(this, type, fields, position);
    }

    if (getIndex(name) != null)
    {
      throw new DBException("Index exists: " + name); //$NON-NLS-1$
    }

    if (type == IDBIndex.Type.PRIMARY_KEY)
    {
      for (IDBIndex index : getIndices())
      {
        if (index.getType() == IDBIndex.Type.PRIMARY_KEY)
        {
          throw new DBException("Primary key exists: " + index); //$NON-NLS-1$
        }
      }
    }

    IDBIndex index = new DBIndex(this, name, type, fields);
    indices.add(index);
    resetElements();
    return index;
  }

  @Override
  public IDBIndex addIndex(String name, IDBIndex.Type type, String... fieldNames)
  {
    return addIndex(name, type, getFields(fieldNames));
  }

  @Override
  public IDBIndex addIndexEmpty(String name, IDBIndex.Type type)
  {
    return addIndex(name, type, NO_FIELDS);
  }

  @Override
  public IDBIndex addIndex(IDBIndex.Type type, IDBField... fields)
  {
    return addIndex(null, type, fields);
  }

  @Override
  public IDBIndex addIndex(IDBIndex.Type type, String... fieldNames)
  {
    IDBField[] fields = getFields(fieldNames);
    return addIndex(type, fields);
  }

  @Override
  public IDBIndex addIndexEmpty(IDBIndex.Type type)
  {
    return addIndex(type, NO_FIELDS);
  }

  @Override
  public void removeIndex(IDBIndex indexToRemove)
  {
    assertUnlocked();
    if (indices.remove(indexToRemove))
    {
      resetElements();
    }
  }

  @Override
  public IDBIndex getIndexSafe(String name) throws SchemaElementNotFoundException
  {
    IDBIndex index = getIndex(name);
    if (index == null)
    {
      throw new SchemaElementNotFoundException(this, SchemaElementType.INDEX, name);
    }

    return index;
  }

  @Override
  public IDBIndex getIndex(String name)
  {
    InternalDBSchema schema = (InternalDBSchema)getSchema();
    IDBIndex[] indices = getIndices();
    return findElement(schema, indices, name);
  }

  @Override
  public IDBIndex getIndex(int position)
  {
    return indices.get(position);
  }

  @Override
  public int getIndexCount()
  {
    return indices.size();
  }

  @Override
  public IDBIndex[] getIndices()
  {
    return indices.toArray(new IDBIndex[indices.size()]);
  }

  @Override
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

  @Override
  public String getFullName()
  {
    return getName();
  }

  @Override
  public void remove()
  {
    schema.removeTable(getName());
  }

  @Override
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
  public String toString()
  {
    String name = super.toString();

    IDBSchema schema = getSchema();
    if (schema != null && schema.isQualifiedTableNames())
    {
      name = DBUtil.quoted(schema.getName()) + '.' + name;
    }

    return name;
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
    ((InternalDBSchema)schema).assertUnlocked();
  }

  private static boolean startsWith(IDBField[] indexFields, IDBField[] fields)
  {
    int length = fields.length;
    if (length <= indexFields.length)
    {
      for (int i = 0; i < length; i++)
      {
        IDBField field = fields[i];
        if (field != indexFields[i])
        {
          return false;
        }
      }

      return true;
    }

    return false;
  }
}
