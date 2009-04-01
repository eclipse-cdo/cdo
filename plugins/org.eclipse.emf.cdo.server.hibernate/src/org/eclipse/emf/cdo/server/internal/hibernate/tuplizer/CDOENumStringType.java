/**
 * <copyright>
 *
 * Copyright (c) 2009 Springsite BV (The Netherlands) and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Martin Taal
 * </copyright>
 *
 * $Id: CDOENumStringType.java,v 1.2 2009-04-01 21:38:30 mtaal Exp $
 */

package org.eclipse.emf.cdo.server.internal.hibernate.tuplizer;

import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EPackage;

import org.hibernate.HibernateException;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Properties;

/**
 * Implements the EMF UserType for an Enum
 * 
 * @author <a href="mailto:mtaal@elver.org">Martin Taal</a>
 * @version $Revision: 1.2 $ $Date: 2009-04-01 21:38:30 $
 */

public class CDOENumStringType implements UserType, ParameterizedType
{
  /** The sql types used for enums */
  private static final int[] SQL_TYPES = new int[] { Types.VARCHAR };

  /** The enum type we are handling here */
  protected EEnum enumInstance;

  /** Hashmap with string to enum mappings */
  private final HashMap<String, Enumerator> localCache = new HashMap<String, Enumerator>();

  /*
   * (non-Javadoc)
   * @see org.hibernate.usertype.UserType#assemble(java.io.Serializable, java.lang.Object)
   */
  public Object assemble(Serializable cached, Object owner) throws HibernateException
  {
    return cached;
  }

  /*
   * (non-Javadoc)
   * @see org.hibernate.usertype.UserType#deepCopy(java.lang.Object)
   */
  public Object deepCopy(Object value) throws HibernateException
  {
    return value;
  }

  /*
   * (non-Javadoc)
   * @see org.hibernate.usertype.UserType#disassemble(java.lang.Object)
   */
  public Serializable disassemble(Object value) throws HibernateException
  {
    return (Serializable)value;
  }

  /** Compares the int values of the enumerates */
  public boolean equals(Object x, Object y) throws HibernateException
  {
    // todo: check compare on null values
    if (x == null && y == null)
    {
      return true;
    }

    if (x == null || y == null)
    {
      return false;
    }

    if (x.getClass() != y.getClass())
    {
      return false;
    }
    if (x instanceof Integer && y instanceof Integer)
    {
      return ((Integer)x).intValue() == ((Integer)y).intValue();
    }
    if (x instanceof String && y instanceof String)
    {
      return ((String)x).equals(y);
    }

    return ((Enumerator)x).getValue() == ((Enumerator)y).getValue();
  }

  /*
   * (non-Javadoc)
   * @see org.hibernate.usertype.UserType#hashCode(java.lang.Object)
   */
  public int hashCode(Object x) throws HibernateException
  {
    return x.hashCode();
  }

  /** Not mutable */
  public boolean isMutable()
  {
    return false;
  }

  /*
   * (non-Javadoc)
   * @see org.hibernate.usertype.UserType#nullSafeGet(java.sql.ResultSet, java.lang.String[], java.lang.Object)
   */
  public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException
  {
    final String name = rs.getString(names[0]);
    if (rs.wasNull())
    {
      return null;
    }

    Enumerator enumValue = localCache.get(name);
    if (enumValue != null)
    {
      return enumValue;
    }

    enumValue = enumInstance.getEEnumLiteralByLiteral(name.trim());
    if (enumValue == null)
    {
      throw new IllegalStateException("The enum value " + name + " is not valid for enumerator: "
          + enumInstance.getName());
    }
    localCache.put(name, enumValue);
    return enumValue.getValue();
  }

  /*
   * (non-Javadoc)
   * @see org.hibernate.usertype.UserType#nullSafeSet(java.sql.PreparedStatement, java.lang.Object, int)
   */
  public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException
  {
    if (value == null)
    {
      st.setNull(index, Types.VARCHAR);
    }
    else
    {
      if (value instanceof Integer)
      {
        final EEnumLiteral literal = enumInstance.getEEnumLiteral((Integer)value);
        st.setString(index, literal.getName());
      }
      else if (value instanceof String)
      {
        final EEnumLiteral literal = enumInstance.getEEnumLiteral((String)value);
        st.setString(index, literal.getName());
      }
      else
      {
        st.setString(index, ((Enumerator)value).getName());
      }
    }
  }

  /*
   * (non-Javadoc)
   * @see org.hibernate.usertype.UserType#replace(java.lang.Object, java.lang.Object, java.lang.Object)
   */
  public Object replace(Object original, Object target, Object owner) throws HibernateException
  {
    return original;
  }

  /** Returns the parameterizezd enumType */
  public Class<?> returnedClass()
  {
    return enumInstance.getClass();
  }

  /** An enum is stored in one varchar */
  public int[] sqlTypes()
  {
    return SQL_TYPES;
  }

  /** Sets the enumclass */
  public void setParameterValues(Properties parameters)
  {
    // TODO: externalize
    final String epackUri = parameters.getProperty("epackage");
    final String eclassifier = parameters.getProperty("eclassifier");
    final EPackage epack = EPackage.Registry.INSTANCE.getEPackage(epackUri);
    enumInstance = (EEnum)epack.getEClassifier(eclassifier);
  }
}
