/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
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

/**
 * @author Eike Stepper
 */
public class DBField implements IDBField
{
  private static final int DEFAULT_PRECISION = 255;

  private DBTable table;

  private String name;

  private Type type;

  private int precision;

  private int scale;

  private boolean notNull;

  private int position;

  public DBField(DBTable table, String name, Type type, int precision, int scale, boolean notNull, int position)
  {
    this.table = table;
    this.name = name;
    this.type = type;
    this.precision = precision;
    this.scale = scale;
    this.notNull = notNull;
    this.position = position;
  }

  public DBTable geTable()
  {
    return table;
  }

  public String getName()
  {
    return name;
  }

  public Type getType()
  {
    return type;
  }

  public int getPrecision()
  {
    return precision == DEFAULT ? DEFAULT_PRECISION : precision;
  }

  public int getScale()
  {
    return scale;
  }

  public boolean isNotNull()
  {
    return notNull;
  }

  public int getPosition()
  {
    return position;
  }

  public String getFullName()
  {
    return table.getName() + "." + name;
  }

  @Override
  public String toString()
  {
    return getFullName();
  }

  public String formatPrecision()
  {
    return "(" + getPrecision() + ")";
  }

  public String formatPrecisionAndScale()
  {
    if (scale == DEFAULT)
    {
      return "(" + getPrecision() + ")";
    }

    return "(" + getPrecision() + ", " + scale + ")";
  }
}
