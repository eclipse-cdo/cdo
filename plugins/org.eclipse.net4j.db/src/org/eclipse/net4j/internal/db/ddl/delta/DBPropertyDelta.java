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

/**
 * @author Eike Stepper
 */
public final class DBPropertyDelta<T> implements IDBPropertyDelta<T>
{
  private static final long serialVersionUID = 1L;

  private final String name;

  private final Type type;

  private final T value;

  private final T oldValue;

  public DBPropertyDelta(String name, Type type, T value, T oldValue)
  {
    this.name = name;
    this.type = type;
    this.value = value;
    this.oldValue = oldValue;
  }

  public String getName()
  {
    return name;
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
}
