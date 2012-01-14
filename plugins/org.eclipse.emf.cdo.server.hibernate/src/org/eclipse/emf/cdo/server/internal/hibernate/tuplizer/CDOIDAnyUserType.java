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

import org.eclipse.emf.cdo.common.id.CDOID;

import org.eclipse.net4j.util.WrappedException;

import org.hibernate.type.StandardBasicTypes;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;

/**
 * Persists a CDOID in the DB in the contents of a resource, a many-to-any mapping.
 */
public class CDOIDAnyUserType implements UserType
{
  private static final int[] SQL_TYPES = { Types.VARCHAR };

  private static final String SEPARATOR = "__;__"; //$NON-NLS-1$

  /** Constructor by id */
  private final HashMap<String, Constructor<?>> constructors = new HashMap<String, Constructor<?>>();

  public CDOIDAnyUserType()
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

  public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws SQLException
  {
    final String value = StandardBasicTypes.STRING.nullSafeGet(rs, names[0]);
    if (rs.wasNull())
    {
      return null;
    }

    final int end1 = value.indexOf(SEPARATOR);
    final int start2 = end1 + SEPARATOR.length();

    final String idStr = value.substring(0, end1);
    final String idClassName = value.substring(start2);
    final Serializable id = getId(idStr, idClassName);
    return id;
  }

  public void nullSafeSet(PreparedStatement statement, Object value, int index) throws SQLException
  {
    if (value == null || value instanceof CDOID && ((CDOID)value).isNull())
    {
      statement.setNull(index, Types.VARCHAR);
    }
    else
    {
      statement.setString(index, value.toString() + SEPARATOR + value.getClass().getName());
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

  /** Creates an id object of the correct type */
  private Serializable getId(String idStr, String idType)
  {
    try
    {
      Constructor<?> constructor = constructors.get(idType);
      if (constructor == null)
      {
        final Class<?> idClass = Thread.currentThread().getContextClassLoader().loadClass(idType);
        constructor = idClass.getConstructor(new Class[] { String.class });
        constructors.put(idType, constructor);
      }

      return (Serializable)constructor.newInstance(new Object[] { idStr });
    }
    catch (Exception e)
    {
      throw WrappedException.wrap(e);
    }
  }
}
