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
package org.eclipse.net4j.internal.db.ddl.delta;

import org.eclipse.net4j.db.ddl.IDBIndexField;
import org.eclipse.net4j.db.ddl.delta.IDBDelta;
import org.eclipse.net4j.db.ddl.delta.IDBIndexDelta;
import org.eclipse.net4j.db.ddl.delta.IDBIndexFieldDelta;
import org.eclipse.net4j.db.ddl.delta.IDBPropertyDelta;
import org.eclipse.net4j.util.ObjectUtil;

/**
 * @author Eike Stepper
 */
public final class DBIndexFieldDelta extends DBDelta implements IDBIndexFieldDelta
{
  private static final long serialVersionUID = 1L;

  public DBIndexFieldDelta(IDBDelta parent, ChangeKind changeKind, String name)
  {
    super(parent, name, changeKind);
  }

  public DBIndexFieldDelta(DBIndexDelta parent, IDBIndexField indexField, IDBIndexField oldIndexField)
  {
    this(parent, getChangeKind(indexField, oldIndexField), getName(indexField, oldIndexField));

    Integer position = oldIndexField == null ? null : indexField.getPosition();
    Integer oldPosition = oldIndexField == null ? null : oldIndexField.getPosition();
    if (!ObjectUtil.equals(position, oldPosition))
    {
      addPropertyDelta(new DBPropertyDelta<Integer>("position", IDBPropertyDelta.Type.INTEGER, position, oldPosition));
    }
  }

  /**
   * Constructor for deserialization.
   */
  protected DBIndexFieldDelta()
  {
  }

  @Override
  public IDBIndexDelta getParent()
  {
    return (IDBIndexDelta)super.getParent();
  }

  public int compareTo(IDBIndexFieldDelta o)
  {
    return getName().compareTo(o.getName());
  }
}
