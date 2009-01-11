/***************************************************************************
 * Copyright (c) 2004 - 2009 Martin Taal and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Martin Taal - initial api
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.server.internal.hibernate.tuplizer;

import org.eclipse.emf.cdo.common.id.CDOIDMeta;
import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;

import org.hibernate.Hibernate;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * @author Martin Taal
 */
public class CDOIDMetaRangeUserType implements UserType
{
  private static final int[] SQL_TYPES = { Types.INTEGER, Types.INTEGER };

  public CDOIDMetaRangeUserType()
  {
  }

  public int[] sqlTypes()
  {
    return SQL_TYPES;
  }

  public Class<?> returnedClass()
  {
    return CDOIDMetaRange.class;
  }

  public boolean isMutable()
  {
    return true;
  }

  public Object deepCopy(Object value)
  {
    if (value == null)
    {
      return null;
    }

    CDOIDMetaRange cdoRange = (CDOIDMetaRange)value;
    CDOIDMeta newCdoIDMeta = CDOIDUtil.createMeta(((CDOIDMeta)cdoRange.getLowerBound()).getLongValue());
    return CDOIDUtil.createMetaRange(newCdoIDMeta, cdoRange.size());
  }

  public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws SQLException
  {
    Integer start = (Integer)Hibernate.INTEGER.nullSafeGet(rs, names[0]);
    if (rs.wasNull())
    {
      return null;
    }

    Integer size = (Integer)Hibernate.INTEGER.nullSafeGet(rs, names[1]);
    if (rs.wasNull())
    {
      return null;
    }

    CDOIDMeta newCdoIDMeta = CDOIDUtil.createMeta(start);
    return CDOIDUtil.createMetaRange(newCdoIDMeta, size);
  }

  public void nullSafeSet(PreparedStatement statement, Object value, int index) throws SQLException
  {
    if (value != null)
    {
      CDOIDMetaRange cdoRange = (CDOIDMetaRange)value;
      statement.setLong(index, ((CDOIDMeta)cdoRange.getLowerBound()).getLongValue());
      statement.setInt(index + 1, cdoRange.size());
    }
    else
    {
      statement.setNull(index, Types.INTEGER);
      statement.setNull(index + 1, Types.INTEGER);
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

  public int hashCode(Object x)
  {
    return x.hashCode();
  }
}
