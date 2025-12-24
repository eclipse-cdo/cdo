/*
 * Copyright (c) 2013-2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model6.impl;

import org.eclipse.emf.cdo.tests.model6.Model6Package;
import org.eclipse.emf.cdo.tests.model6.UnsettableAttributes;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.ecore.EClass;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Unsettable Attributes</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.UnsettableAttributesImpl#getAttrBigDecimal <em>Attr Big Decimal</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.UnsettableAttributesImpl#getAttrBigInteger <em>Attr Big Integer</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.UnsettableAttributesImpl#isAttrBoolean <em>Attr Boolean</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.UnsettableAttributesImpl#getAttrBooleanObject <em>Attr Boolean Object</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.UnsettableAttributesImpl#getAttrByte <em>Attr Byte</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.UnsettableAttributesImpl#getAttrByteArray <em>Attr Byte Array</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.UnsettableAttributesImpl#getAttrByteObject <em>Attr Byte Object</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.UnsettableAttributesImpl#getAttrChar <em>Attr Char</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.UnsettableAttributesImpl#getAttrCharacterObject <em>Attr Character Object</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.UnsettableAttributesImpl#getAttrDate <em>Attr Date</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.UnsettableAttributesImpl#getAttrDouble <em>Attr Double</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.UnsettableAttributesImpl#getAttrDoubleObject <em>Attr Double Object</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.UnsettableAttributesImpl#getAttrFloat <em>Attr Float</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.UnsettableAttributesImpl#getAttrFloatObject <em>Attr Float Object</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.UnsettableAttributesImpl#getAttrInt <em>Attr Int</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.UnsettableAttributesImpl#getAttrIntegerObject <em>Attr Integer Object</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.UnsettableAttributesImpl#getAttrJavaClass <em>Attr Java Class</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.UnsettableAttributesImpl#getAttrJavaObject <em>Attr Java Object</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.UnsettableAttributesImpl#getAttrLong <em>Attr Long</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.UnsettableAttributesImpl#getAttrLongObject <em>Attr Long Object</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.UnsettableAttributesImpl#getAttrShort <em>Attr Short</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.UnsettableAttributesImpl#getAttrShortObject <em>Attr Short Object</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.UnsettableAttributesImpl#getAttrString <em>Attr String</em>}</li>
 * </ul>
 *
 * @generated
 */
public class UnsettableAttributesImpl extends CDOObjectImpl implements UnsettableAttributes
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected UnsettableAttributesImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return Model6Package.Literals.UNSETTABLE_ATTRIBUTES;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected int eStaticFeatureCount()
  {
    return 0;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public BigDecimal getAttrBigDecimal()
  {
    return (BigDecimal)eGet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_BIG_DECIMAL, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrBigDecimal(BigDecimal newAttrBigDecimal)
  {
    eSet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_BIG_DECIMAL, newAttrBigDecimal);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetAttrBigDecimal()
  {
    eUnset(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_BIG_DECIMAL);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetAttrBigDecimal()
  {
    return eIsSet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_BIG_DECIMAL);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public BigInteger getAttrBigInteger()
  {
    return (BigInteger)eGet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_BIG_INTEGER, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrBigInteger(BigInteger newAttrBigInteger)
  {
    eSet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_BIG_INTEGER, newAttrBigInteger);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetAttrBigInteger()
  {
    eUnset(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_BIG_INTEGER);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetAttrBigInteger()
  {
    return eIsSet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_BIG_INTEGER);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isAttrBoolean()
  {
    return (Boolean)eGet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_BOOLEAN, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrBoolean(boolean newAttrBoolean)
  {
    eSet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_BOOLEAN, newAttrBoolean);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetAttrBoolean()
  {
    eUnset(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_BOOLEAN);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetAttrBoolean()
  {
    return eIsSet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_BOOLEAN);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Boolean getAttrBooleanObject()
  {
    return (Boolean)eGet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_BOOLEAN_OBJECT, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrBooleanObject(Boolean newAttrBooleanObject)
  {
    eSet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_BOOLEAN_OBJECT, newAttrBooleanObject);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetAttrBooleanObject()
  {
    eUnset(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_BOOLEAN_OBJECT);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetAttrBooleanObject()
  {
    return eIsSet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_BOOLEAN_OBJECT);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public byte getAttrByte()
  {
    return (Byte)eGet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_BYTE, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrByte(byte newAttrByte)
  {
    eSet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_BYTE, newAttrByte);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetAttrByte()
  {
    eUnset(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_BYTE);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetAttrByte()
  {
    return eIsSet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_BYTE);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public byte[] getAttrByteArray()
  {
    return (byte[])eGet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_BYTE_ARRAY, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrByteArray(byte[] newAttrByteArray)
  {
    eSet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_BYTE_ARRAY, newAttrByteArray);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetAttrByteArray()
  {
    eUnset(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_BYTE_ARRAY);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetAttrByteArray()
  {
    return eIsSet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_BYTE_ARRAY);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Byte getAttrByteObject()
  {
    return (Byte)eGet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_BYTE_OBJECT, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrByteObject(Byte newAttrByteObject)
  {
    eSet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_BYTE_OBJECT, newAttrByteObject);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetAttrByteObject()
  {
    eUnset(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_BYTE_OBJECT);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetAttrByteObject()
  {
    return eIsSet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_BYTE_OBJECT);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public char getAttrChar()
  {
    return (Character)eGet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_CHAR, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrChar(char newAttrChar)
  {
    eSet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_CHAR, newAttrChar);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetAttrChar()
  {
    eUnset(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_CHAR);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetAttrChar()
  {
    return eIsSet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_CHAR);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Character getAttrCharacterObject()
  {
    return (Character)eGet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_CHARACTER_OBJECT, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrCharacterObject(Character newAttrCharacterObject)
  {
    eSet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_CHARACTER_OBJECT, newAttrCharacterObject);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetAttrCharacterObject()
  {
    eUnset(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_CHARACTER_OBJECT);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetAttrCharacterObject()
  {
    return eIsSet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_CHARACTER_OBJECT);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Date getAttrDate()
  {
    return (Date)eGet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_DATE, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrDate(Date newAttrDate)
  {
    eSet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_DATE, newAttrDate);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetAttrDate()
  {
    eUnset(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_DATE);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetAttrDate()
  {
    return eIsSet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_DATE);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public double getAttrDouble()
  {
    return (Double)eGet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_DOUBLE, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrDouble(double newAttrDouble)
  {
    eSet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_DOUBLE, newAttrDouble);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetAttrDouble()
  {
    eUnset(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_DOUBLE);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetAttrDouble()
  {
    return eIsSet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_DOUBLE);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Double getAttrDoubleObject()
  {
    return (Double)eGet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_DOUBLE_OBJECT, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrDoubleObject(Double newAttrDoubleObject)
  {
    eSet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_DOUBLE_OBJECT, newAttrDoubleObject);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetAttrDoubleObject()
  {
    eUnset(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_DOUBLE_OBJECT);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetAttrDoubleObject()
  {
    return eIsSet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_DOUBLE_OBJECT);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public float getAttrFloat()
  {
    return (Float)eGet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_FLOAT, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrFloat(float newAttrFloat)
  {
    eSet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_FLOAT, newAttrFloat);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetAttrFloat()
  {
    eUnset(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_FLOAT);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetAttrFloat()
  {
    return eIsSet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_FLOAT);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Float getAttrFloatObject()
  {
    return (Float)eGet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_FLOAT_OBJECT, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrFloatObject(Float newAttrFloatObject)
  {
    eSet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_FLOAT_OBJECT, newAttrFloatObject);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetAttrFloatObject()
  {
    eUnset(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_FLOAT_OBJECT);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetAttrFloatObject()
  {
    return eIsSet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_FLOAT_OBJECT);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int getAttrInt()
  {
    return (Integer)eGet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_INT, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrInt(int newAttrInt)
  {
    eSet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_INT, newAttrInt);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetAttrInt()
  {
    eUnset(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_INT);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetAttrInt()
  {
    return eIsSet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_INT);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Integer getAttrIntegerObject()
  {
    return (Integer)eGet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_INTEGER_OBJECT, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrIntegerObject(Integer newAttrIntegerObject)
  {
    eSet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_INTEGER_OBJECT, newAttrIntegerObject);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetAttrIntegerObject()
  {
    eUnset(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_INTEGER_OBJECT);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetAttrIntegerObject()
  {
    return eIsSet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_INTEGER_OBJECT);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Class<?> getAttrJavaClass()
  {
    return (Class<?>)eGet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_JAVA_CLASS, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrJavaClass(Class<?> newAttrJavaClass)
  {
    eSet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_JAVA_CLASS, newAttrJavaClass);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetAttrJavaClass()
  {
    eUnset(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_JAVA_CLASS);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetAttrJavaClass()
  {
    return eIsSet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_JAVA_CLASS);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object getAttrJavaObject()
  {
    return eGet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_JAVA_OBJECT, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrJavaObject(Object newAttrJavaObject)
  {
    eSet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_JAVA_OBJECT, newAttrJavaObject);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetAttrJavaObject()
  {
    eUnset(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_JAVA_OBJECT);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetAttrJavaObject()
  {
    return eIsSet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_JAVA_OBJECT);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public long getAttrLong()
  {
    return (Long)eGet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_LONG, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrLong(long newAttrLong)
  {
    eSet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_LONG, newAttrLong);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetAttrLong()
  {
    eUnset(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_LONG);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetAttrLong()
  {
    return eIsSet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_LONG);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Long getAttrLongObject()
  {
    return (Long)eGet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_LONG_OBJECT, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrLongObject(Long newAttrLongObject)
  {
    eSet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_LONG_OBJECT, newAttrLongObject);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetAttrLongObject()
  {
    eUnset(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_LONG_OBJECT);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetAttrLongObject()
  {
    return eIsSet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_LONG_OBJECT);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public short getAttrShort()
  {
    return (Short)eGet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_SHORT, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrShort(short newAttrShort)
  {
    eSet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_SHORT, newAttrShort);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetAttrShort()
  {
    eUnset(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_SHORT);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetAttrShort()
  {
    return eIsSet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_SHORT);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Short getAttrShortObject()
  {
    return (Short)eGet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_SHORT_OBJECT, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrShortObject(Short newAttrShortObject)
  {
    eSet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_SHORT_OBJECT, newAttrShortObject);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetAttrShortObject()
  {
    eUnset(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_SHORT_OBJECT);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetAttrShortObject()
  {
    return eIsSet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_SHORT_OBJECT);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getAttrString()
  {
    return (String)eGet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_STRING, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrString(String newAttrString)
  {
    eSet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_STRING, newAttrString);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetAttrString()
  {
    eUnset(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_STRING);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetAttrString()
  {
    return eIsSet(Model6Package.Literals.UNSETTABLE_ATTRIBUTES__ATTR_STRING);
  }

} // UnsettableAttributesImpl
