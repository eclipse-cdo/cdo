/*
 * Copyright (c) 2013, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.db.ddl;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.spi.db.ddl.InternalDBField;

/**
 * @author Eike Stepper
 */
public final class DelegatingDBField extends DelegatingDBSchemaElement implements InternalDBField
{
  DelegatingDBField(InternalDBField delegate)
  {
    super(delegate);
  }

  @Override
  public InternalDBField getDelegate()
  {
    return (InternalDBField)super.getDelegate();
  }

  @Override
  public IDBField getWrapper()
  {
    return this;
  }

  @Override
  public int getPosition()
  {
    return getDelegate().getPosition();
  }

  @Override
  public void setPosition(int position)
  {
    getDelegate().setPosition(position);
  }

  @Override
  public Exception getConstructionStackTrace()
  {
    return getDelegate().getConstructionStackTrace();
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
  public DBType getType()
  {
    return getDelegate().getType();
  }

  @Override
  public void setType(DBType type)
  {
    getDelegate().setType(type);
  }

  @Override
  public int getPrecision()
  {
    return getDelegate().getPrecision();
  }

  @Override
  public void setPrecision(int precision)
  {
    getDelegate().setPrecision(precision);
  }

  @Override
  public int getScale()
  {
    return getDelegate().getScale();
  }

  @Override
  public void setScale(int scale)
  {
    getDelegate().setScale(scale);
  }

  @Override
  public boolean isNotNull()
  {
    return getDelegate().isNotNull();
  }

  @Override
  public void setNotNull(boolean notNull)
  {
    getDelegate().setNotNull(notNull);
  }

  @Override
  public String formatPrecision()
  {
    return getDelegate().formatPrecision();
  }

  @Override
  public String formatPrecisionAndScale()
  {
    return getDelegate().formatPrecisionAndScale();
  }
}
