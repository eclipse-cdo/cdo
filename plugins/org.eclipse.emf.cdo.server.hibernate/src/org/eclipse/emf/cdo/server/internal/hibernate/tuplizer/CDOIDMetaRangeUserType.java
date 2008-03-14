/***************************************************************************
 * Copyright (c) 2008 - 2008 Martin Taal and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Martin Taal - initial api
 **************************************************************************/
package org.eclipse.emf.cdo.server.internal.hibernate.tuplizer;

import org.eclipse.emf.cdo.internal.protocol.id.CDOIDMetaImpl;
import org.eclipse.emf.cdo.internal.protocol.id.CDOIDMetaRangeImpl;
import org.eclipse.emf.cdo.protocol.id.CDOIDMeta;
import org.eclipse.emf.cdo.protocol.id.CDOIDMetaRange;

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
    final CDOIDMetaRange cdoRange = (CDOIDMetaRange)value;
    final CDOIDMeta newCdoIDMeta = new CDOIDMetaImpl(((CDOIDMetaImpl)cdoRange.getLowerBound()).getLongValue());
    return new CDOIDMetaRangeImpl(newCdoIDMeta, cdoRange.size());
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
    final Integer start = (Integer)Hibernate.INTEGER.nullSafeGet(rs, names[0]);
    if (rs.wasNull())
    {
      return null;
    }
    final Integer size = (Integer)Hibernate.INTEGER.nullSafeGet(rs, names[1]);
    if (rs.wasNull())
    {
      return null;
    }

    final CDOIDMeta newCdoIDMeta = new CDOIDMetaImpl(start);
    return new CDOIDMetaRangeImpl(newCdoIDMeta, size);
  }

  public void nullSafeSet(PreparedStatement statement, Object value, int index) throws SQLException
  {
    if (value != null)
    {
      final CDOIDMetaRange cdoRange = (CDOIDMetaRange)value;
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

  public int hashCode(Object x)
  {
    return x.hashCode();
  }
}
