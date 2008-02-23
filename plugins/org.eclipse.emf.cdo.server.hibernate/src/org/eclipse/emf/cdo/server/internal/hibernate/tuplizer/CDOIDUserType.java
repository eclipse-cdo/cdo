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

import org.eclipse.emf.cdo.protocol.id.CDOID;
import org.eclipse.emf.cdo.protocol.id.CDOIDTemp;
import org.eclipse.emf.cdo.protocol.revision.CDORevision;
import org.eclipse.emf.cdo.server.hibernate.CDOIDHibernate;
import org.eclipse.emf.cdo.server.internal.hibernate.CDOHibernateUtil;
import org.eclipse.emf.cdo.server.internal.hibernate.CDOIDHibernateImpl;

import org.hibernate.Hibernate;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Persists a cdoid in the db.
 */
public class CDOIDUserType implements UserType
{

  // second varchar is just for informational purposes
  private static final int[] SQL_TYPES = { Types.VARCHAR, Types.VARCHAR, Types.VARBINARY };

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

  public CDOIDUserType()
  {
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
    final String entityName = (String)Hibernate.STRING.nullSafeGet(rs, names[0]);
    if (rs.wasNull() || entityName == null)
    {
      return null;
    }
    final byte[] content = (byte[])Hibernate.BINARY.nullSafeGet(rs, names[2]);
    final CDOIDHibernate cdoID = new CDOIDHibernateImpl();
    cdoID.setContent(content);
    return cdoID;
  }

  public void nullSafeSet(PreparedStatement statement, Object value, int index) throws SQLException
  {
    if (value == null)
    {
      statement.setNull(index, Types.VARCHAR);
      statement.setNull(index, Types.VARCHAR);
      statement.setNull(index, Types.VARBINARY);
    }
    if (value != null)
    {
      final CDOIDHibernate cdoID;
      if (value instanceof CDOIDTemp)
      {
        cdoID = CDOHibernateUtil.getInstance().getCDOIDHibernate((CDOID)value);
      }
      else if (value instanceof CDORevision)
      {
        cdoID = (CDOIDHibernate)((CDORevision)value).getID();
      }
      else
      {
        cdoID = (CDOIDHibernate)value;
      }
      statement.setString(index, cdoID.getEntityName());
      statement.setString(index + 1, cdoID.getId().toString());
      statement.setBytes(index + 2, cdoID.getContent());
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
