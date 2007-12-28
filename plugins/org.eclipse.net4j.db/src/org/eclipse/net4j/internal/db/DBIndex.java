/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.db;

import org.eclipse.net4j.db.IDBField;
import org.eclipse.net4j.db.IDBIndex;
import org.eclipse.net4j.db.IDBSchema;

/**
 * @author Eike Stepper
 */
public class DBIndex extends DBElement implements IDBIndex
{
  private DBTable table;

  private Type type;

  private IDBField[] fields;

  public int position;

  public DBIndex(DBTable table, Type type, IDBField[] fields, int position)
  {
    this.table = table;
    this.type = type;
    this.fields = fields;
    this.position = position;
  }

  public IDBSchema getSchema()
  {
    return table.getSchema();
  }

  public DBTable getTable()
  {
    return table;
  }

  public Type getType()
  {
    return type;
  }

  public IDBField getField(int index)
  {
    return fields[index];
  }

  public int getFieldCount()
  {
    return fields.length;
  }

  public IDBField[] getFields()
  {
    return fields;
  }

  public int getPosition()
  {
    return position;
  }

  public String getName()
  {
    return "idx_" + table.getName() + "_" + position;
  }

  public String getFullName()
  {
    return getName();
  }
}
