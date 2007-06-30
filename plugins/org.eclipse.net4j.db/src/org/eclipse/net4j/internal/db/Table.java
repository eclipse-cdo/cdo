/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.db;

import org.eclipse.net4j.db.IField;
import org.eclipse.net4j.db.IIndex;
import org.eclipse.net4j.db.ITable;
import org.eclipse.net4j.db.IIndex.Type;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class Table implements ITable
{
  private Schema schema;

  private String name;

  private List<Field> fields = new ArrayList();

  private List<Index> indices = new ArrayList();

  public Table(Schema schema, String name)
  {
    this.schema = schema;
    this.name = name;
  }

  public Schema getSchema()
  {
    return schema;
  }

  public String getName()
  {
    return name;
  }

  public Field addField(String name, IField.Type type)
  {
    return addField(name, type, IField.DEFAULT, IField.DEFAULT, false);
  }

  public Field addField(String name, IField.Type type, boolean notNull)
  {
    return addField(name, type, IField.DEFAULT, IField.DEFAULT, notNull);
  }

  public Field addField(String name, IField.Type type, int precision)
  {
    return addField(name, type, precision, IField.DEFAULT, false);
  }

  public Field addField(String name, IField.Type type, int precision, boolean notNull)
  {
    return addField(name, type, precision, IField.DEFAULT, notNull);
  }

  public Field addField(String name, IField.Type type, int precision, int scale)
  {
    return addField(name, type, precision, scale, false);
  }

  public Field addField(String name, IField.Type type, int precision, int scale, boolean notNull)
  {
    schema.assertUnlocked();
    if (getField(name) != null)
    {
      throw new IllegalStateException("Field exists: " + name);
    }

    Field field = new Field(this, name, type, precision, scale, notNull, fields.size());
    fields.add(field);
    return field;
  }

  public Field getField(String name)
  {
    for (Field field : fields)
    {
      if (name.equals(field.getName()))
      {
        return field;
      }
    }

    return null;
  }

  public Field getField(int index)
  {
    return fields.get(index);
  }

  public int getFieldCount()
  {
    return fields.size();
  }

  public Field[] getFields()
  {
    return fields.toArray(new Field[fields.size()]);
  }

  public Index addIndex(Type type, IField field)
  {
    IField[] fields = { field };
    return addIndex(type, fields);
  }

  public Index addIndex(Type type, IField[] fields)
  {
    schema.assertUnlocked();
    Index index = new Index(this, type, fields, indices.size());
    indices.add(index);
    return index;
  }

  public int getIndexCount()
  {
    return indices.size();
  }

  public Index[] getIndices()
  {
    return indices.toArray(new Index[indices.size()]);
  }

  public IIndex getPrimaryKeyIndex()
  {
    for (IIndex index : indices)
    {
      if (index.geType() == IIndex.Type.PRIMARY_KEY)
      {
        return index;
      }
    }

    return null;
  }

  public void appendFieldNames(Appendable appendable)
  {
    try
    {
      boolean first = true;
      for (Field field : fields)
      {
        if (first)
        {
          first = false;
        }
        else
        {
          appendable.append(", ");
        }

        appendable.append(field.getName());
      }
    }
    catch (IOException canNotHappen)
    {
    }
  }

  public void appendFieldDefs(Appendable appendable, String[] defs)
  {
    try
    {
      for (int i = 0; i < fields.size(); i++)
      {
        Field field = fields.get(i);
        if (i != 0)
        {
          appendable.append(", ");
        }

        appendable.append(field.getName());
        appendable.append(" ");
        appendable.append(defs[i]);
      }
    }
    catch (IOException canNotHappen)
    {
    }
  }

  @Override
  public String toString()
  {
    return name;
  }
}
