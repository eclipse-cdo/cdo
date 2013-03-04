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

import org.eclipse.net4j.db.ddl.delta.IDBTableElementDelta;

/**
 * @author Eike Stepper
 */
public abstract class DBTableElementDelta extends DBDelta implements IDBTableElementDelta
{
  private static final long serialVersionUID = 1L;

  public DBTableElementDelta(DBDelta parent, String name, ChangeKind changeKind)
  {
    super(parent, name, changeKind);
  }

  /**
   * Constructor for deserialization.
   */
  protected DBTableElementDelta()
  {
  }

  @Override
  public DBTableDelta getParent()
  {
    return (DBTableDelta)super.getParent();
  }

  public int compareTo(IDBTableElementDelta o)
  {
    int result = getTableElementType().compareTo(o.getTableElementType());
    if (result == 0)
    {
      result = getName().compareTo(o.getName());
    }

    return result;
  }
}
