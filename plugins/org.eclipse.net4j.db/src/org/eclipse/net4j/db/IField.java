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
package org.eclipse.net4j.db;

/**
 * @author Eike Stepper
 */
public interface IField
{
  public static final int DEFAULT = -1;

  public ITable geTable();

  public String getName();

  public Type getType();

  public int getPrecision();

  public int getScale();

  public boolean isNotNull();

  public int getPosition();

  public String getFullName();

  /**
   * @author Eike Stepper
   */
  public enum Type
  {
    BOOLEAN(16), //
    BIT(-7), //
    TINYINT(-6), //
    SMALLINT(5), //
    INTEGER(4), //
    BIGINT(-5), //
    FLOAT(6), //
    REAL(7), //
    DOUBLE(8), //
    NUMERIC(2), //
    DECIMAL(3), //
    CHAR(1), //
    VARCHAR(12), //
    LONGVARCHAR(-1), //
    DATE(91), //
    TIME(92), //
    TIMESTAMP(93), //
    BINARY(-2), //
    VARBINARY(-3), //
    LONGVARBINARY(-4), //
    BLOB(2004), //
    CLOB(2005); //

    private int code;

    private Type(int code)
    {
      this.code = code;
    }

    public int getCode()
    {
      return code;
    }
  }
}
