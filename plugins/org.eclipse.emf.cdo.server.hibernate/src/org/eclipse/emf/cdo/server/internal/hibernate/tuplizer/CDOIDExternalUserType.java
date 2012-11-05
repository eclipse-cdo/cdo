/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal
 */
package org.eclipse.emf.cdo.server.internal.hibernate.tuplizer;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDExternal;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateUtil;

import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

/**
 * A user type which can handle {@link CDOIDExternal}. It's stored in a single varchar field.
 * 
 * @author <a href="mailto:mtaal@elver.org">Martin Taal</a>
 */
public class CDOIDExternalUserType implements UserType, ParameterizedType
{
  private static final int[] SQL_TYPES = { Types.VARCHAR };

  public CDOIDExternalUserType()
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

  public Object nullSafeGet(ResultSet resultSet, String[] names, SessionImplementor sessionImplementor, Object owner)
      throws SQLException
  {
    final String data = resultSet.getString(names[0]);
    if (data == null)
    {
      return null;
    }

    return CDOIDUtil.createExternal(data);
  }

  public void nullSafeSet(PreparedStatement statement, Object value, int index, SessionImplementor sessionImplementor)
      throws SQLException
  {
    if (value == null)
    {
      statement.setNull(index, Types.VARCHAR);
      return;
    }

    final Object localValue;
    if (value instanceof CDORevision)
    {
      localValue = HibernateUtil.getInstance().getCDOID(value);
    }
    else
    {
      localValue = value;
    }

    if (localValue instanceof CDOIDExternal)
    {
      statement.setString(index, ((CDOIDExternal)localValue).getURI());
    }
    else
    {
      statement.setString(index, "#" + ((CDOID)localValue).toURIFragment());
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

  public void setParameterValues(Properties parameters)
  {
  }
}
