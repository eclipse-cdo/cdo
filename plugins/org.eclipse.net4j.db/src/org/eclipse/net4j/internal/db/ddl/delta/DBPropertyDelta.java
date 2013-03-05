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

import org.eclipse.net4j.db.ddl.delta.IDBDelta.ChangeKind;
import org.eclipse.net4j.db.ddl.delta.IDBPropertyDelta;
import org.eclipse.net4j.spi.db.DBNamedElement;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public final class DBPropertyDelta<T> extends DBNamedElement implements IDBPropertyDelta<T>
{
  private static final long serialVersionUID = 1L;

  private Type type;

  private T value;

  private T oldValue;

  public DBPropertyDelta(String name, Type type, T value, T oldValue)
  {
    super(name);
    this.type = type;
    this.value = value;
    this.oldValue = oldValue;
  }

  /**
   * Constructor for deserialization.
   */
  protected DBPropertyDelta()
  {
  }

  public ChangeKind getChangeKind()
  {
    return DBDelta.getChangeKind(value, oldValue);
  }

  public Type getType()
  {
    return type;
  }

  public T getValue()
  {
    return value;
  }

  public T getOldValue()
  {
    return oldValue;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("DBPropertyDelta[name={0}, kind={1}, type={2}, value={3}, oldValue={4}]", getName(),
        getChangeKind(), getType(), getValue(), getOldValue());
  }
}
