/*
 * Copyright (c) 2013, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.db.ddl;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex;
import org.eclipse.net4j.db.ddl.IDBIndex.Type;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.IDBSchemaElement;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.ddl.SchemaElementNotFoundException;
import org.eclipse.net4j.spi.db.ddl.InternalDBTable;

/**
 * @author Eike Stepper
 */
public final class DelegatingDBTable extends DelegatingDBSchemaElement implements InternalDBTable
{
  DelegatingDBTable(InternalDBTable delegate)
  {
    super(delegate);
  }

  @Override
  public InternalDBTable getDelegate()
  {
    return (InternalDBTable)super.getDelegate();
  }

  @Override
  public void setDelegate(IDBSchemaElement delegate)
  {
    IDBField[] wrapperFields = getFields();
    IDBIndex[] wrapperIndices = getIndices();

    IDBTable delegateTable = (IDBTable)delegate;
    super.setDelegate(delegateTable);

    for (IDBField wrapperField : wrapperFields)
    {
      IDBField delegateField = delegateTable.getField(wrapperField.getName());
      ((DelegatingDBSchemaElement)wrapperField).setDelegate(delegateField);
    }

    for (IDBIndex wrapperIndex : wrapperIndices)
    {
      IDBIndex delegateIndex = delegateTable.getIndex(wrapperIndex.getName());
      ((DelegatingDBSchemaElement)wrapperIndex).setDelegate(delegateIndex);
    }
  }

  @Override
  public IDBTable getWrapper()
  {
    return this;
  }

  @Override
  public IDBSchema getParent()
  {
    return wrap(getDelegate().getParent());
  }

  @Override
  public IDBField addField(String name, DBType type)
  {
    return wrap(getDelegate().addField(name, type));
  }

  @Override
  public IDBField addField(String name, DBType type, boolean notNull)
  {
    return wrap(getDelegate().addField(name, type, notNull));
  }

  @Override
  public void removeField(IDBField fieldToRemove)
  {
    getDelegate().removeField(unwrap(fieldToRemove));
  }

  @Override
  public IDBField addField(String name, DBType type, int precision)
  {
    return wrap(getDelegate().addField(name, type, precision));
  }

  @Override
  public void removeIndex(IDBIndex indexToRemove)
  {
    getDelegate().removeIndex(unwrap(indexToRemove));
  }

  @Override
  public IDBField addField(String name, DBType type, int precision, boolean notNull)
  {
    return wrap(getDelegate().addField(name, type, precision, notNull));
  }

  @Override
  public IDBField addField(String name, DBType type, int precision, int scale)
  {
    return wrap(getDelegate().addField(name, type, precision, scale));
  }

  @Override
  public IDBField addField(String name, DBType type, int precision, int scale, boolean notNull)
  {
    return wrap(getDelegate().addField(name, type, precision, scale, notNull));
  }

  @Override
  public IDBField getFieldSafe(String name) throws SchemaElementNotFoundException
  {
    return wrap(getDelegate().getFieldSafe(name));
  }

  @Override
  public IDBField getField(String name)
  {
    return wrap(getDelegate().getField(name));
  }

  @Override
  public IDBField getField(int position)
  {
    return wrap(getDelegate().getField(position));
  }

  @Override
  public int getFieldCount()
  {
    return getDelegate().getFieldCount();
  }

  @Override
  public IDBField[] getFields()
  {
    return wrap(getDelegate().getFields(), IDBField.class);
  }

  @Override
  public IDBField[] getFields(String... fieldNames) throws SchemaElementNotFoundException
  {
    return wrap(getDelegate().getFields(fieldNames), IDBField.class);
  }

  @Override
  public boolean hasIndexFor(IDBField... fields)
  {
    return getDelegate().hasIndexFor(unwrap(fields, IDBField.class));
  }

  @Override
  public IDBIndex addIndex(String name, Type type, IDBField... fields)
  {
    return wrap(getDelegate().addIndex(name, type, unwrap(fields, IDBField.class)));
  }

  @Override
  public IDBIndex addIndex(String name, Type type, String... fieldNames) throws SchemaElementNotFoundException
  {
    return wrap(getDelegate().addIndex(name, type, fieldNames));
  }

  @Override
  public IDBIndex addIndexEmpty(String name, Type type)
  {
    return wrap(getDelegate().addIndexEmpty(name, type));
  }

  @Override
  public IDBIndex addIndex(Type type, IDBField... fields)
  {
    return wrap(getDelegate().addIndex(type, unwrap(fields, IDBField.class)));
  }

  @Override
  public IDBIndex addIndex(Type type, String... fieldNames) throws SchemaElementNotFoundException
  {
    return wrap(getDelegate().addIndex(type, fieldNames));
  }

  @Override
  public IDBIndex addIndexEmpty(Type type)
  {
    return wrap(getDelegate().addIndexEmpty(type));
  }

  @Override
  public IDBIndex getIndexSafe(String name) throws SchemaElementNotFoundException
  {
    return wrap(getDelegate().getIndexSafe(name));
  }

  @Override
  public IDBIndex getIndex(String name)
  {
    return wrap(getDelegate().getIndex(name));
  }

  @Override
  public IDBIndex getIndex(int position)
  {
    return wrap(getDelegate().getIndex(position));
  }

  @Override
  public int getIndexCount()
  {
    return getDelegate().getIndexCount();
  }

  @Override
  public IDBIndex[] getIndices()
  {
    return wrap(getDelegate().getIndices(), IDBIndex.class);
  }

  @Override
  public IDBIndex getPrimaryKeyIndex()
  {
    return wrap(getDelegate().getPrimaryKeyIndex());
  }

  @Override
  public String sqlInsert()
  {
    return getDelegate().sqlInsert();
  }
}
