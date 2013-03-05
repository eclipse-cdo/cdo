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

import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.delta.IDBDeltaVisitor;
import org.eclipse.net4j.db.ddl.delta.IDBIndexFieldDelta;
import org.eclipse.net4j.db.ddl.delta.IDBPropertyDelta;
import org.eclipse.net4j.internal.db.ddl.DBIndex;
import org.eclipse.net4j.internal.db.ddl.DBIndexField;
import org.eclipse.net4j.util.ObjectUtil;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public final class DBIndexFieldDelta extends DBDelta implements IDBIndexFieldDelta
{
  private static final long serialVersionUID = 1L;

  public DBIndexFieldDelta(DBDelta parent, ChangeKind changeKind, String name)
  {
    super(parent, name, changeKind);
  }

  public DBIndexFieldDelta(DBIndexDelta parent, DBIndexField indexField, DBIndexField oldIndexField)
  {
    this(parent, getChangeKind(indexField, oldIndexField), getName(indexField, oldIndexField));

    Integer position = indexField == null ? null : indexField.getPosition();
    Integer oldPosition = oldIndexField == null ? null : oldIndexField.getPosition();
    if (!ObjectUtil.equals(position, oldPosition))
    {
      addPropertyDelta(new DBPropertyDelta<Integer>(POSITION_PROPERTY, IDBPropertyDelta.Type.INTEGER, position,
          oldPosition));
    }
  }

  /**
   * Constructor for deserialization.
   */
  protected DBIndexFieldDelta()
  {
  }

  @Override
  public DBIndexDelta getParent()
  {
    return (DBIndexDelta)super.getParent();
  }

  public int compareTo(IDBIndexFieldDelta o)
  {
    return getName().compareTo(o.getName());
  }

  public void accept(IDBDeltaVisitor visitor)
  {
    visitor.visit(this);
  }

  public DBIndexField getElement(IDBSchema schema)
  {
    DBIndex index = getParent().getElement(schema);
    if (index == null)
    {
      return null;
    }

    return index.getIndexField(getName());
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("DBIndexFieldDelta[name={0}, kind={1}, propertyDeltas={2}]", getName(),
        getChangeKind(), getPropertyDeltas().values());
  }
}
