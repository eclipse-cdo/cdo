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

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.delta.IDBDelta;
import org.eclipse.net4j.db.ddl.delta.IDBFieldDelta;
import org.eclipse.net4j.db.ddl.delta.IDBPropertyDelta;
import org.eclipse.net4j.util.ObjectUtil;

/**
 * @author Eike Stepper
 */
public final class DBFieldDelta extends DBTableElementDelta implements IDBFieldDelta
{
  private static final long serialVersionUID = 1L;

  public DBFieldDelta(IDBDelta parent, String name, ChangeKind changeKind)
  {
    super(parent, name, changeKind);
  }

  public DBFieldDelta(DBTableDelta parent, IDBField field, IDBField oldField)
  {
    this(parent, getName(field, oldField), getChangeKind(field, oldField));

    DBType type = field == null ? null : field.getType();
    DBType oldType = oldField == null ? null : oldField.getType();
    if (!ObjectUtil.equals(type, oldType))
    {
      addPropertyDelta(new DBPropertyDelta<DBType>("type", IDBPropertyDelta.Type.STRING, type, oldType));
    }

    Integer precision = field == null ? null : field.getPrecision();
    Integer oldPrecision = oldField == null ? null : oldField.getPrecision();
    if (!ObjectUtil.equals(precision, oldPrecision))
    {
      addPropertyDelta(new DBPropertyDelta<Integer>("precision", IDBPropertyDelta.Type.INTEGER, precision, oldPrecision));
    }

    Integer scale = field == null ? null : field.getScale();
    Integer oldScale = oldField == null ? null : oldField.getScale();
    if (!ObjectUtil.equals(scale, oldScale))
    {
      addPropertyDelta(new DBPropertyDelta<Integer>("scale", IDBPropertyDelta.Type.INTEGER, scale, oldScale));
    }

    Boolean notNull = field == null ? null : field.isNotNull();
    Boolean oldNotNull = oldField == null ? null : oldField.isNotNull();
    if (!ObjectUtil.equals(notNull, oldNotNull))
    {
      addPropertyDelta(new DBPropertyDelta<Boolean>("notNull", IDBPropertyDelta.Type.BOOLEAN, notNull, oldNotNull));
    }

    Integer position = oldField == null ? null : field.getPosition();
    Integer oldPosition = oldField == null ? null : oldField.getPosition();
    if (!ObjectUtil.equals(position, oldPosition))
    {
      addPropertyDelta(new DBPropertyDelta<Integer>("position", IDBPropertyDelta.Type.INTEGER, position, oldPosition));
    }
  }

  /**
   * Constructor for deserialization.
   */
  protected DBFieldDelta()
  {
  }

  public Type getTableElementType()
  {
    return Type.FIELD;
  }
}
