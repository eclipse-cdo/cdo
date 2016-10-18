/*
 * Copyright (c) 2011, 2012, 2015 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateUtil;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

import javax.xml.datatype.XMLGregorianCalendar;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Persists the types from the {@link XMLTypePackage}.
 */
public class XMLUserType implements UserType
{
  private static final java.lang.String XML_TYPE_PACKAGE_NSURI = "http://www.eclipse.org/emf/2003/XMLType"; //$NON-NLS-1$

  private EDataType eDataType = null;

  private int[] sqlTypes = null;

  public Object assemble(Serializable arg0, Object arg1) throws HibernateException
  {
    return arg1;
  }

  public Object deepCopy(Object arg0) throws HibernateException
  {
    return arg0;
  }

  public Serializable disassemble(Object arg0) throws HibernateException
  {
    return (Serializable)arg0;
  }

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

    return x.equals(y);
  }

  public int hashCode(Object arg0) throws HibernateException
  {
    return arg0.hashCode();
  }

  public boolean isMutable()
  {
    return false;
  }

  protected java.lang.String convertToString(Object value)
  {
    if (value == null)
    {
      return null;
    }
    if (value instanceof String)
    {
      return (java.lang.String)value;
    }
    if (getEDataType() == null)
    {
      return value.toString();
    }
    final EDataType eDataType = getEDataType();
    return eDataType.getEPackage().getEFactoryInstance().convertToString(eDataType, value);
  }

  protected Object convertToObject(java.lang.String value)
  {
    if (getEDataType() == null || value == null)
    {
      return value;
    }
    final EDataType eDataType = getEDataType();
    return eDataType.getEPackage().getEFactoryInstance().createFromString(eDataType, value);
  }

  private EDataType getEDataType()
  {
    if (eDataType != null)
    {
      return eDataType;
    }
    final CDOPackageRegistry packageRegistry = HibernateUtil.getInstance().getPackageRegistry();
    final EPackage ePackage = packageRegistry.getEPackage(XML_TYPE_PACKAGE_NSURI);
    if (ePackage == null)
    {
      throw new IllegalStateException("EPackage with nsuri " + XML_TYPE_PACKAGE_NSURI + " not found"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    final EClassifier eClassifier = ePackage.getEClassifier(getEDataTypeName());
    if (eClassifier == null || !(eClassifier instanceof EDataType))
    {
      throw new IllegalStateException("EPackage " + ePackage.getName() + " does not have an EEnum with name " //$NON-NLS-1$ //$NON-NLS-2$
          + getEDataTypeName());
    }

    eDataType = (EDataType)eClassifier;

    return eDataType;
  }

  protected java.lang.String getEDataTypeName()
  {
    if (this.getClass() != XMLUserType.class)
    {
      return this.getClass().getSimpleName();
    }

    return "String"; //$NON-NLS-1$
  }

  public Object nullSafeGet(ResultSet arg0, java.lang.String[] arg1, SessionImplementor sessionImplementor, Object arg2) throws HibernateException, SQLException
  {
    final Object value = arg0.getObject(arg1[0]);
    if (arg0.wasNull())
    {
      return null;
    }

    return convertToString(value);
  }

  public void nullSafeSet(PreparedStatement arg0, Object arg1, int arg2, SessionImplementor sessionImplementor) throws HibernateException, SQLException
  {
    if (arg1 == null)
    {
      // st.setString(index, ((Enumerator)getEEnum().getDefaultValue()).getLiteral());
      arg0.setNull(arg2, sqlTypes()[0]);
    }
    else if (arg1 instanceof java.lang.String)
    {
      arg0.setObject(arg2, convertToObject((java.lang.String)arg1), getSqlType());
    }
    else
    {
      arg0.setObject(arg2, arg1, getSqlType());
    }
  }

  public Object replace(Object arg0, Object arg1, Object arg2) throws HibernateException
  {
    return arg0;
  }

  @SuppressWarnings("rawtypes")
  public Class returnedClass()
  {
    if (getEDataType() == null || getEDataType().getInstanceClass() == null)
    {
      return String.class;
    }
    return getEDataType().getInstanceClass();
  }

  public int[] sqlTypes()
  {
    if (sqlTypes == null)
    {
      sqlTypes = new int[] { getSqlType() };
    }
    return sqlTypes;
  }

  protected int getSqlType()
  {
    return Types.VARCHAR;
  }

  public static class AnySimpleType extends XMLUserType
  {

  }

  public static class AnyURI extends XMLUserType
  {

  }

  public static class Base64Binary extends XMLUserType
  {
    @Override
    protected int getSqlType()
    {
      return Types.BLOB;
    }
  }

  public static class Boolean extends XMLUserType
  {
    @Override
    protected int getSqlType()
    {
      return Types.BIT;
    }
  }

  public static class BooleanObject extends XMLUserType
  {
    @Override
    protected int getSqlType()
    {
      return Types.BOOLEAN;
    }
  }

  public static class Byte extends XMLUserType
  {
    @Override
    protected int getSqlType()
    {
      return Types.TINYINT;
    }
  }

  public static class ByteObject extends XMLUserType
  {
    @Override
    protected int getSqlType()
    {
      return Types.TINYINT;
    }
  }

  public static class Date extends XMLUserType
  {
    @Override
    protected java.lang.String convertToString(Object value)
    {
      if (value == null)
      {
        return super.convertToString(value);
      }
      return super.convertToString(HibernateUtil.getInstance().getXMLGregorianCalendarDate((java.util.Date)value, false));
    }

    @Override
    protected Object convertToObject(java.lang.String value)
    {
      return new java.sql.Date(((XMLGregorianCalendar)super.convertToObject(value)).toGregorianCalendar().getTime().getTime());
    }

    @Override
    protected int getSqlType()
    {
      return Types.DATE;
    }
  }

  public static class DateTime extends XMLUserType
  {
    @Override
    protected java.lang.String convertToString(Object value)
    {
      if (value == null)
      {
        return super.convertToString(value);
      }
      return super.convertToString(HibernateUtil.getInstance().getXMLGregorianCalendarDate((java.util.Date)value, true));
    }

    @Override
    protected Object convertToObject(java.lang.String value)
    {
      return new java.sql.Timestamp(((XMLGregorianCalendar)super.convertToObject(value)).toGregorianCalendar().getTime().getTime());
    }

    @Override
    protected int getSqlType()
    {
      return Types.TIMESTAMP;
    }
  }

  public static class Decimal extends XMLUserType
  {
    @Override
    protected int getSqlType()
    {
      return Types.NUMERIC;
    }
  }

  public static class Double extends XMLUserType
  {
    @Override
    protected int getSqlType()
    {
      return Types.DOUBLE;
    }
  }

  public static class DoubleObject extends XMLUserType
  {
    @Override
    protected int getSqlType()
    {
      return Types.DOUBLE;
    }
  }

  public static class Duration extends XMLUserType
  {
  }

  public static class ENTITIES extends XMLUserType
  {
  }

  public static class ENTITIESBase extends XMLUserType
  {
  }

  public static class ENTITY extends XMLUserType
  {
  }

  public static class Float extends XMLUserType
  {
    @Override
    protected int getSqlType()
    {
      return Types.FLOAT;
    }
  }

  public static class FloatObject extends XMLUserType
  {
    @Override
    protected int getSqlType()
    {
      return Types.FLOAT;
    }
  }

  public static class GDay extends XMLUserType
  {
  }

  public static class GMonth extends XMLUserType
  {
  }

  public static class GMonthDay extends XMLUserType
  {
  }

  public static class GYear extends XMLUserType
  {
  }

  public static class GYearMonth extends XMLUserType
  {
  }

  public static class HexBinary extends XMLUserType
  {
  }

  public static class ID extends XMLUserType
  {
  }

  public static class IDREF extends XMLUserType
  {
  }

  public static class IDREFS extends XMLUserType
  {
  }

  public static class IDREFSBase extends XMLUserType
  {
  }

  public static class Int extends XMLUserType
  {
    @Override
    protected int getSqlType()
    {
      return Types.INTEGER;
    }
  }

  public static class Integer extends XMLUserType
  {
    @Override
    protected int getSqlType()
    {
      return Types.BIGINT;
    }
  }

  public static class IntObject extends XMLUserType
  {
    @Override
    protected int getSqlType()
    {
      return Types.BIGINT;
    }
  }

  public static class Language extends XMLUserType
  {
  }

  public static class Long extends XMLUserType
  {
    @Override
    protected int getSqlType()
    {
      return Types.BIGINT;
    }
  }

  public static class LongObject extends XMLUserType
  {
    @Override
    protected int getSqlType()
    {
      return Types.BIGINT;
    }
  }

  public static class Name extends XMLUserType
  {
  }

  public static class NCName extends XMLUserType
  {
  }

  public static class NMTOKEN extends XMLUserType
  {
  }

  public static class NMTOKENS extends XMLUserType
  {
  }

  public static class NMTOKENSBase extends XMLUserType
  {
  }

  public static class String extends XMLUserType
  {
  }

  public static class Token extends XMLUserType
  {
  }

  public static class QName extends XMLUserType
  {
  }

  public static class NOTATION extends XMLUserType
  {
  }

  public static class NegativeInteger extends XMLUserType
  {
    @Override
    protected int getSqlType()
    {
      return Types.BIGINT;
    }
  }

  public static class NonNegativeInteger extends XMLUserType
  {
    @Override
    protected int getSqlType()
    {
      return Types.BIGINT;
    }
  }

  public static class NonPositiveInteger extends XMLUserType
  {
    @Override
    protected int getSqlType()
    {
      return Types.BIGINT;
    }
  }

  public static class PositiveInteger extends XMLUserType
  {
    @Override
    protected int getSqlType()
    {
      return Types.BIGINT;
    }
  }

  public static class Short extends XMLUserType
  {
    @Override
    protected int getSqlType()
    {
      return Types.INTEGER;
    }
  }

  public static class ShortObject extends XMLUserType
  {
    @Override
    protected int getSqlType()
    {
      return Types.INTEGER;
    }
  }

  public static class Time extends XMLUserType
  {
    @Override
    protected int getSqlType()
    {
      return Types.TIME;
    }
  }

  public static class UnsignedByte extends XMLUserType
  {
    @Override
    protected int getSqlType()
    {
      return Types.TINYINT;
    }
  }

  public static class UnsignedByteObject extends XMLUserType
  {
    @Override
    protected int getSqlType()
    {
      return Types.TINYINT;
    }
  }

  public static class UnsignedInt extends XMLUserType
  {
    @Override
    protected int getSqlType()
    {
      return Types.INTEGER;
    }
  }

  public static class UnsignedIntObject extends XMLUserType
  {
    @Override
    protected int getSqlType()
    {
      return Types.INTEGER;
    }
  }

  public static class UnsignedShort extends XMLUserType
  {
    @Override
    protected int getSqlType()
    {
      return Types.INTEGER;
    }
  }

  public static class UnsignedShortObject extends XMLUserType
  {
    @Override
    protected int getSqlType()
    {
      return Types.INTEGER;
    }
  }

  public static class UnsignedLong extends XMLUserType
  {
    @Override
    protected int getSqlType()
    {
      return Types.BIGINT;
    }
  }

  public static class NormalizedString extends XMLUserType
  {
  }
}
