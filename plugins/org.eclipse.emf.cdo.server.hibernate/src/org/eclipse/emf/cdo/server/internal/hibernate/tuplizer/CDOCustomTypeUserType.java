/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - initial api
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.internal.hibernate.tuplizer;

import org.hibernate.type.StandardBasicTypes;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

/**
 * Standard user type used for all custom types.
 */
public class CDOCustomTypeUserType implements UserType, ParameterizedType
{
  private static final int[] SQL_TYPES = { Types.VARCHAR };

  public void setParameterValues(Properties arg0)
  {
  }

  public Class<?> returnedClass()
  {
    return Object.class;
  }

  public int[] sqlTypes()
  {
    return SQL_TYPES;
  }

  public boolean isMutable()
  {
    return false;
  }

  public Object deepCopy(Object value)
  {
    return value;
  }

  public boolean equals(Object x, Object y)
  {
    if (x == y)
    {
      return true;
    }

    if (x == null || y == null)
    {
      return false;
    }

    return x.equals(y);
  }

  public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws SQLException
  {
    try
    {
      final String value = StandardBasicTypes.STRING.nullSafeGet(rs, names[0]);
      if (rs.wasNull())
      {
        return null;
      }

      return value;
    }
    catch (Exception e)
    {
      throw new IllegalStateException(e);
    }
  }

  public void nullSafeSet(PreparedStatement statement, Object value, int index) throws SQLException
  {
    try
    {
      if (value == null)
      {
        statement.setNull(index, Types.VARCHAR);
        return;
      }
      statement.setString(index, value.toString());
    }
    catch (Exception e)
    {
      throw new IllegalStateException(e);
    }
  }

  public Serializable disassemble(Object value)
  {
    return (Serializable)value;
  }

  public Object assemble(Serializable cachedValue, Object owner)
  {
    return cachedValue;
  }

  public Object replace(Object original, Object target, Object owner)
  {
    return original;
  }

  public int hashCode(Object x)
  {
    return x.hashCode();
  }
}
