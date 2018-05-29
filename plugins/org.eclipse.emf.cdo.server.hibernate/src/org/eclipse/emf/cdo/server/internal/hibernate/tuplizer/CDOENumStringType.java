/*
 * Copyright (c) 2009-2012, 2014-2016 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateUtil;

import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EPackage;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
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
 */
public class CDOENumStringType implements UserType, ParameterizedType
{
  private static final String EPACKAGE_META = "epackage"; //$NON-NLS-1$

  private static final String ECLASSIFIER_META = "eclassifier"; //$NON-NLS-1$

  /** The sql types used for enums */
  private static final int[] SQL_TYPES = new int[] { Types.VARCHAR };

  /** The enum type we are handling here */
  protected EEnum eEnum;

  private String ePackageNsUri;

  private String eClassifierName;

  /** Hashmap with string to enum mappings */
  private final HashMap<String, Enumerator> localCache = new HashMap<String, Enumerator>();

  /*
   * (non-Javadoc)
   * @see org.hibernate.usertype.UserType#assemble(java.io.Serializable, java.lang.Object)
   */
  public Object assemble(Serializable cached, Object owner) throws HibernateException
  {
    if (cached instanceof String)
    {
      return getEEnum().getEEnumLiteralByLiteral((String)cached);
    }
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
    if (value instanceof EEnumLiteral)
    {
      return ((EEnumLiteral)value).getLiteral();
    }

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
  public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor sessionImplementor, Object owner) throws HibernateException, SQLException
  {
    final String literal = rs.getString(names[0]);
    if (rs.wasNull())
    {
      return null;
    }

    Enumerator enumValue = localCache.get(literal);
    if (enumValue != null)
    {
      return enumValue;
    }

    enumValue = getEEnum().getEEnumLiteralByLiteral(literal.trim());
    if (enumValue == null)
    {
      throw new IllegalStateException("The enum value " + literal + " is invalid for enumerator: " //$NON-NLS-1$ //$NON-NLS-2$
          + getEEnum().getName());
    }

    localCache.put(literal, enumValue);
    return enumValue;
  }

  /*
   * (non-Javadoc)
   * @see org.hibernate.usertype.UserType#nullSafeSet(java.sql.PreparedStatement, java.lang.Object, int)
   */
  public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor sessionImplementor) throws HibernateException, SQLException
  {
    if (value == null)
    {
      // st.setString(index, ((Enumerator)getEEnum().getDefaultValue()).getLiteral());
      st.setNull(index, Types.VARCHAR);
    }
    else
    {
      if (value instanceof Integer)
      {
        final EEnumLiteral literal = getEEnum().getEEnumLiteral((Integer)value);
        st.setString(index, literal.getLiteral());
      }
      else if (value instanceof String)
      {
        final EEnumLiteral literal = getEEnum().getEEnumLiteral((String)value);
        st.setString(index, literal.getLiteral());
      }
      else
      {
        st.setString(index, ((Enumerator)value).getLiteral());
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
    return getEEnum().getClass();
  }

  /** An enum is stored in one varchar */
  public int[] sqlTypes()
  {
    return SQL_TYPES;
  }

  protected EEnum getEEnum()
  {
    if (eEnum == null)
    {
      final CDOPackageRegistry packageRegistry = HibernateUtil.getInstance().getPackageRegistry();
      final EPackage ePackage = packageRegistry.getEPackage(ePackageNsUri);
      if (ePackage == null)
      {
        throw new IllegalStateException("EPackage with nsuri " + ePackageNsUri + " not found"); //$NON-NLS-1$ //$NON-NLS-2$
      }

      final EClassifier eClassifier = ePackage.getEClassifier(eClassifierName);
      if (eClassifier == null || !(eClassifier instanceof EEnum))
      {
        throw new IllegalStateException("EPackage " + ePackage.getName() + " does not have an EEnum with name " //$NON-NLS-1$ //$NON-NLS-2$
            + eClassifierName);
      }

      eEnum = (EEnum)eClassifier;
    }

    return eEnum;
  }

  /** Sets the enumclass */
  public void setParameterValues(Properties parameters)
  {
    ePackageNsUri = parameters.getProperty(EPACKAGE_META);
    eClassifierName = parameters.getProperty(ECLASSIFIER_META);
  }
}
