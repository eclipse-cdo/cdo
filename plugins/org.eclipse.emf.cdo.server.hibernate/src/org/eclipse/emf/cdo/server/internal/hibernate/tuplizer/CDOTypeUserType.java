/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOType;

import org.hibernate.type.StandardBasicTypes;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * @author Martin Taal
 */
public class CDOTypeUserType implements UserType
{
  private static final int[] SQL_TYPES = { Types.INTEGER };

  public CDOTypeUserType()
  {
  }

  public int[] sqlTypes()
  {
    return SQL_TYPES;
  }

  public Class<?> returnedClass()
  {
    return CDOType.class;
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
    Integer value = StandardBasicTypes.INTEGER.nullSafeGet(rs, names[0]);
    if (rs.wasNull())
    {
      return null;
    }

    if (value == null)
    {
      return null;
    }

    return CDOModelUtil.getType((byte)(int)value);
  }

  public void nullSafeSet(PreparedStatement statement, Object value, int index) throws SQLException
  {
    if (value != null)
    {
      statement.setInt(index, ((CDOType)value).getTypeID());
    }
    else
    {
      statement.setNull(index, Types.INTEGER);
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
