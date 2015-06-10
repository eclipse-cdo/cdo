/*
 * Copyright (c) 2008-2012, 2015 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDExternal;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateUtil;

import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Persists a CDOID in the DB. Currently {@link CDOIDExternal} is supported.
 */
public class CDOIDUserType implements UserType
{
  private static final int[] SQL_TYPES = { Types.VARCHAR };

  public CDOIDUserType()
  {
  }

  public int[] sqlTypes()
  {
    return SQL_TYPES;
  }

  public Class<?> returnedClass()
  {
    return CDOID.class;
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

  public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor sessionImplementor, Object owner)
      throws SQLException
  {
    final String value = StandardBasicTypes.STRING.nullSafeGet(rs, names[0], sessionImplementor);
    if (rs.wasNull())
    {
      return null;
    }

    return HibernateUtil.getInstance().convertStringToCDOID(value);
  }

  public void nullSafeSet(PreparedStatement statement, Object value, int index, SessionImplementor sessionImplementor)
      throws SQLException
  {
    if (value == null || value instanceof CDOID && ((CDOID)value).isNull())
    {
      statement.setNull(index, Types.VARCHAR);
      return;
    }
    else if (value instanceof CDOIDTemp)
    {
      // try to resolve the temp id
      final CDORevision revision = HibernateUtil.getInstance().getCDORevisionNullable((CDOID)value);
      if (revision != null)
      {
        value = HibernateUtil.getInstance().getCDOID(revision);
      }
    }
    else if (value instanceof CDORevision)
    {
      value = HibernateUtil.getInstance().getCDOIDHibernate((CDORevision)value);
    }

    final String strValue = value instanceof String ? (String)value
        : HibernateUtil.getInstance().convertCDOIDToString((CDOID)value);
    if (strValue == null)
    {
      statement.setNull(index, Types.VARCHAR);
      return;
    }

    statement.setString(index, strValue);
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
