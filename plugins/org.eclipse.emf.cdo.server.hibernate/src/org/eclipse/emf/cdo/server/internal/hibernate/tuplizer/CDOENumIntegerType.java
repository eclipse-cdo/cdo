/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.internal.hibernate.tuplizer;

import org.eclipse.emf.common.util.Enumerator;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;

/**
 * Implements the EMF UserType for an Enum in a dynamic model, for an integer field.
 * 
 * @author <a href="mailto:mtaal@elver.org">Martin Taal</a>
 */
public class CDOENumIntegerType extends CDOENumStringType
{
  /** The sql types used for enums */
  private static final int[] SQL_TYPES = new int[] { Types.INTEGER };

  /** Hashmap with string to enum mappings */
  private final HashMap<Integer, Enumerator> localCache = new HashMap<Integer, Enumerator>();

  /*
   * (non-Javadoc)
   * @see org.hibernate.usertype.UserType#nullSafeGet(java.sql.ResultSet, java.lang.String[], java.lang.Object)
   */
  @Override
  public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor sessionImplementor, Object owner)
      throws HibernateException, SQLException
  {
    final int value = rs.getInt(names[0]);
    if (rs.wasNull())
    {
      return null;
    }

    Integer objValue = new Integer(value);
    Enumerator enumValue = localCache.get(objValue);
    if (enumValue != null)
    {
      return enumValue;
    }

    enumValue = getEEnum().getEEnumLiteral(objValue.intValue());
    localCache.put(objValue, enumValue);
    return enumValue;
  }

  /*
   * (non-Javadoc)
   * @see org.hibernate.usertype.UserType#nullSafeSet(java.sql.PreparedStatement, java.lang.Object, int)
   */
  @Override
  public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor sessionImplementor)
      throws HibernateException, SQLException
  {
    if (value == null)
    {
      st.setNull(index, Types.INTEGER);
    }
    else if (value instanceof Integer)
    {
      st.setInt(index, (Integer)value);
    }
    else
    {
      st.setInt(index, ((Enumerator)value).getValue());
    }
  }

  /** An enum is stored in one varchar */
  @Override
  public int[] sqlTypes()
  {
    return SQL_TYPES;
  }
}
