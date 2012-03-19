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

import org.eclipse.emf.cdo.common.lob.CDOBlob;
import org.eclipse.emf.cdo.common.lob.CDOClob;
import org.eclipse.emf.cdo.common.lob.CDOLob;

import org.eclipse.net4j.util.HexUtil;

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
 * Base class for persisting {@link CDOBlob} and {@link CDOClob}.
 */
public abstract class CDOLobUserType implements UserType, ParameterizedType
{
  private static final String SEPARATOR = " - ";

  private static final int[] SQL_TYPES = { Types.CLOB };

  public CDOLobUserType()
  {
  }

  public void setParameterValues(Properties arg0)
  {
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

      return convertStringToLob(value);
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
      final CDOLob<?> cdoLob = (CDOLob<?>)value;

      if (value == null || cdoLob.getSize() == 0)
      {
        statement.setNull(index, Types.VARCHAR);
        return;
      }
      statement.setString(index, convertLobToString(cdoLob));
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

  private String convertLobToString(CDOLob<?> lob)
  {
    return HexUtil.bytesToHex(lob.getID()) + SEPARATOR + lob.getSize();
  }

  private Object convertStringToLob(String lobId)
  {
    int pos = lobId.indexOf(SEPARATOR);

    byte[] id = HexUtil.hexToBytes(lobId.substring(0, pos));
    long size = Long.parseLong(lobId.substring(pos + SEPARATOR.length()));
    return createLob(id, size);
  }

  protected abstract Object createLob(byte[] id, long size);

  public Object replace(Object original, Object target, Object owner)
  {
    return original;
  }

  public int hashCode(Object x)
  {
    return x.hashCode();
  }
}
