/*
 * Copyright (c) 2009-2012 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateStoreAccessor;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateThreadContext;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateUtil;

import org.eclipse.net4j.util.WrappedException;

import org.eclipse.emf.ecore.EClass;

import org.hibernate.engine.spi.SessionImplementor;
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
  private static final int[] SQL_TYPES = { Types.VARCHAR, Types.VARCHAR };

  private static final String SEPARATOR = "__;__"; //$NON-NLS-1$

  private static final String EXTERNAL = "EXTERNAL"; //$NON-NLS-1$

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

  public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor sessionImplementor, Object owner)
      throws SQLException
  {
    final String value = StandardBasicTypes.STRING.nullSafeGet(rs, names[1], sessionImplementor);
    if (rs.wasNull())
    {
      return null;
    }
    final String entityName = StandardBasicTypes.STRING.nullSafeGet(rs, names[0], sessionImplementor);
    return deserializeId(entityName, value);
  }

  public void nullSafeSet(PreparedStatement statement, Object value, int index, SessionImplementor sessionImplementor)
      throws SQLException
  {
    final String entityName;
    final CDOID localValue;
    if (value instanceof CDORevision)
    {
      final CDORevision cdoRevision = (CDORevision)value;
      localValue = cdoRevision.getID();
      // cast to object to use correct method from hibernate util
      entityName = HibernateUtil.getInstance().getEntityName((Object)cdoRevision);
    }
    else if (value instanceof CDOID)
    {
      localValue = (CDOID)value;
      entityName = HibernateUtil.getInstance().getEntityName(localValue);
    }
    else
    {
      throw new IllegalArgumentException("Type " + value + " not supported here");
    }

    // the first column is there for backward compatibility, fill it with nulls..
    final String strValue = serializeId(localValue);
    if (strValue == null)
    {
      statement.setNull(index, Types.VARCHAR);
      statement.setNull(index + 1, Types.VARCHAR);
    }
    else
    {
      statement.setString(index, entityName);
      statement.setString(index + 1, strValue);
    }
  }

  protected String serializeId(CDOID id)
  {
    final CDOID cdoID = HibernateUtil.getInstance().resolvePossibleTempId(id);
    if (cdoID == null || cdoID.isNull())
    {
      return null;
    }
    if (cdoID.getType() == CDOID.Type.EXTERNAL_OBJECT)
    {
      return EXTERNAL + SEPARATOR + ((CDOIDExternal)cdoID).getURI();
    }
    final Serializable idValue = HibernateUtil.getInstance().getIdValue(cdoID);
    return idValue + SEPARATOR + idValue.getClass().getName();
  }

  protected CDOID deserializeId(String entityName, String value)
  {
    final int end1 = value.indexOf(SEPARATOR);
    final int start2 = end1 + SEPARATOR.length();

    final String idStr = value.substring(0, end1);
    final String idClassName = value.substring(start2);

    if (EXTERNAL.equals(entityName))
    {
      return CDOIDUtil.createExternal(idStr);
    }

    final Serializable idValue = getId(idStr, idClassName);
    final HibernateStoreAccessor accessor = HibernateThreadContext.getCurrentStoreAccessor();
    final EClass eClass = accessor.getStore().getEClass(entityName);
    return HibernateUtil.getInstance().createCDOID(new CDOClassifierRef(eClass), idValue);
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
