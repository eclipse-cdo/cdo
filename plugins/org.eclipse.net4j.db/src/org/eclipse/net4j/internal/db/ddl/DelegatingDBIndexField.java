/*
 * Copyright (c) 2013, 2019 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex;
import org.eclipse.net4j.db.ddl.IDBIndexField;
import org.eclipse.net4j.spi.db.ddl.InternalDBIndexField;

/**
 * @author Eike Stepper
 */
public final class DelegatingDBIndexField extends DelegatingDBSchemaElement implements InternalDBIndexField
{
  DelegatingDBIndexField(InternalDBIndexField delegate)
  {
    super(delegate);
  }

  @Override
  public InternalDBIndexField getDelegate()
  {
    return (InternalDBIndexField)super.getDelegate();
  }

  @Override
  public IDBIndexField getWrapper()
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
  public IDBIndex getParent()
  {
    return wrap(getDelegate().getParent());
  }

  @Override
  public IDBIndex getIndex()
  {
    return wrap(getDelegate().getIndex());
  }

  @Override
  public IDBField getField()
  {
    return wrap(getDelegate().getField());
  }
}
