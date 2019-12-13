/*
 * Copyright (c) 2013, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.net4j.db.ddl.IDBSchemaElement;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.ddl.SchemaElementNotFoundException;
import org.eclipse.net4j.spi.db.ddl.InternalDBIndex;

/**
 * @author Eike Stepper
 */
public final class DelegatingDBIndex extends DelegatingDBSchemaElement implements InternalDBIndex
{
  DelegatingDBIndex(InternalDBIndex delegate)
  {
    super(delegate);
  }

  @Override
  public InternalDBIndex getDelegate()
  {
    return (InternalDBIndex)super.getDelegate();
  }

  @Override
  public void setDelegate(IDBSchemaElement delegate)
  {
    IDBIndexField[] wrapperIndexFields = getIndexFields();

    IDBIndex delegateIndex = (IDBIndex)delegate;
    super.setDelegate(delegateIndex);

    for (IDBIndexField wrapperIndexField : wrapperIndexFields)
    {
      IDBIndexField delegateIndexField = delegateIndex.getIndexField(wrapperIndexField.getName());
      ((DelegatingDBSchemaElement)wrapperIndexField).setDelegate(delegateIndexField);
    }
  }

  @Override
  public IDBIndex getWrapper()
  {
    return this;
  }

  @Override
  public IDBTable getParent()
  {
    return wrap(getDelegate().getParent());
  }

  @Override
  public IDBTable getTable()
  {
    return wrap(getDelegate().getTable());
  }

  @Override
  public Type getType()
  {
    return getDelegate().getType();
  }

  @Override
  public void setType(Type type)
  {
    getDelegate().setType(type);
  }

  @Override
  public void removeIndexField(IDBIndexField indexFieldToRemove)
  {
    getDelegate().removeIndexField(unwrap(indexFieldToRemove));
  }

  @Override
  public boolean isOptional()
  {
    return getDelegate().isOptional();
  }

  @Override
  public void setOptional(boolean optional)
  {
    getDelegate().setOptional(optional);
  }

  @Override
  @Deprecated
  public int getPosition()
  {
    return getDelegate().getPosition();
  }

  @Override
  public IDBIndexField addIndexField(IDBField field)
  {
    return wrap(getDelegate().addIndexField(unwrap(field)));
  }

  @Override
  public IDBIndexField addIndexField(String name) throws SchemaElementNotFoundException
  {
    return wrap(getDelegate().addIndexField(name));
  }

  @Override
  public IDBIndexField getIndexFieldSafe(String name) throws SchemaElementNotFoundException
  {
    return wrap(getDelegate().getIndexFieldSafe(name));
  }

  @Override
  public IDBIndexField getIndexField(String name)
  {
    return wrap(getDelegate().getIndexField(name));
  }

  @Override
  public IDBIndexField getIndexField(int position)
  {
    return wrap(getDelegate().getIndexField(position));
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
  public IDBIndexField[] getIndexFields()
  {
    return wrap(getDelegate().getIndexFields(), IDBIndexField.class);
  }

  @Override
  public IDBField[] getFields()
  {
    return wrap(getDelegate().getFields(), IDBField.class);
  }
}
