/*
 * Copyright (c) 2013, 2015, 2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model6.legacy.impl;

import org.eclipse.emf.cdo.tests.model6.UnsettableAttributes;
import org.eclipse.emf.cdo.tests.model6.legacy.Model6Package;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

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
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.UnsettableAttributesImpl#getAttrBigDecimal <em>Attr Big Decimal</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.UnsettableAttributesImpl#getAttrBigInteger <em>Attr Big Integer</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.UnsettableAttributesImpl#isAttrBoolean <em>Attr Boolean</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.UnsettableAttributesImpl#getAttrBooleanObject <em>Attr Boolean Object</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.UnsettableAttributesImpl#getAttrByte <em>Attr Byte</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.UnsettableAttributesImpl#getAttrByteArray <em>Attr Byte Array</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.UnsettableAttributesImpl#getAttrByteObject <em>Attr Byte Object</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.UnsettableAttributesImpl#getAttrChar <em>Attr Char</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.UnsettableAttributesImpl#getAttrCharacterObject <em>Attr Character Object</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.UnsettableAttributesImpl#getAttrDate <em>Attr Date</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.UnsettableAttributesImpl#getAttrDouble <em>Attr Double</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.UnsettableAttributesImpl#getAttrDoubleObject <em>Attr Double Object</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.UnsettableAttributesImpl#getAttrFloat <em>Attr Float</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.UnsettableAttributesImpl#getAttrFloatObject <em>Attr Float Object</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.UnsettableAttributesImpl#getAttrInt <em>Attr Int</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.UnsettableAttributesImpl#getAttrIntegerObject <em>Attr Integer Object</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.UnsettableAttributesImpl#getAttrJavaClass <em>Attr Java Class</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.UnsettableAttributesImpl#getAttrJavaObject <em>Attr Java Object</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.UnsettableAttributesImpl#getAttrLong <em>Attr Long</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.UnsettableAttributesImpl#getAttrLongObject <em>Attr Long Object</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.UnsettableAttributesImpl#getAttrShort <em>Attr Short</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.UnsettableAttributesImpl#getAttrShortObject <em>Attr Short Object</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.UnsettableAttributesImpl#getAttrString <em>Attr String</em>}</li>
 * </ul>
 *
 * @generated
 */
public class UnsettableAttributesImpl extends EObjectImpl implements UnsettableAttributes
{
  /**
   * The default value of the '{@link #getAttrBigDecimal() <em>Attr Big Decimal</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttrBigDecimal()
   * @generated
   * @ordered
   */
  protected static final BigDecimal ATTR_BIG_DECIMAL_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getAttrBigDecimal() <em>Attr Big Decimal</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttrBigDecimal()
   * @generated
   * @ordered
   */
  protected BigDecimal attrBigDecimal = ATTR_BIG_DECIMAL_EDEFAULT;

  /**
   * This is true if the Attr Big Decimal attribute has been set.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  protected boolean attrBigDecimalESet;

  /**
   * The default value of the '{@link #getAttrBigInteger() <em>Attr Big Integer</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttrBigInteger()
   * @generated
   * @ordered
   */
  protected static final BigInteger ATTR_BIG_INTEGER_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getAttrBigInteger() <em>Attr Big Integer</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttrBigInteger()
   * @generated
   * @ordered
   */
  protected BigInteger attrBigInteger = ATTR_BIG_INTEGER_EDEFAULT;

  /**
   * This is true if the Attr Big Integer attribute has been set.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  protected boolean attrBigIntegerESet;

  /**
   * The default value of the '{@link #isAttrBoolean() <em>Attr Boolean</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isAttrBoolean()
   * @generated
   * @ordered
   */
  protected static final boolean ATTR_BOOLEAN_EDEFAULT = false;

  /**
   * The cached value of the '{@link #isAttrBoolean() <em>Attr Boolean</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isAttrBoolean()
   * @generated
   * @ordered
   */
  protected boolean attrBoolean = ATTR_BOOLEAN_EDEFAULT;

  /**
   * This is true if the Attr Boolean attribute has been set.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  protected boolean attrBooleanESet;

  /**
   * The default value of the '{@link #getAttrBooleanObject() <em>Attr Boolean Object</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttrBooleanObject()
   * @generated
   * @ordered
   */
  protected static final Boolean ATTR_BOOLEAN_OBJECT_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getAttrBooleanObject() <em>Attr Boolean Object</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttrBooleanObject()
   * @generated
   * @ordered
   */
  protected Boolean attrBooleanObject = ATTR_BOOLEAN_OBJECT_EDEFAULT;

  /**
   * This is true if the Attr Boolean Object attribute has been set.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  protected boolean attrBooleanObjectESet;

  /**
   * The default value of the '{@link #getAttrByte() <em>Attr Byte</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttrByte()
   * @generated
   * @ordered
   */
  protected static final byte ATTR_BYTE_EDEFAULT = 0x00;

  /**
   * The cached value of the '{@link #getAttrByte() <em>Attr Byte</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttrByte()
   * @generated
   * @ordered
   */
  protected byte attrByte = ATTR_BYTE_EDEFAULT;

  /**
   * This is true if the Attr Byte attribute has been set.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  protected boolean attrByteESet;

  /**
   * The default value of the '{@link #getAttrByteArray() <em>Attr Byte Array</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttrByteArray()
   * @generated
   * @ordered
   */
  protected static final byte[] ATTR_BYTE_ARRAY_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getAttrByteArray() <em>Attr Byte Array</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttrByteArray()
   * @generated
   * @ordered
   */
  protected byte[] attrByteArray = ATTR_BYTE_ARRAY_EDEFAULT;

  /**
   * This is true if the Attr Byte Array attribute has been set.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  protected boolean attrByteArrayESet;

  /**
   * The default value of the '{@link #getAttrByteObject() <em>Attr Byte Object</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttrByteObject()
   * @generated
   * @ordered
   */
  protected static final Byte ATTR_BYTE_OBJECT_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getAttrByteObject() <em>Attr Byte Object</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttrByteObject()
   * @generated
   * @ordered
   */
  protected Byte attrByteObject = ATTR_BYTE_OBJECT_EDEFAULT;

  /**
   * This is true if the Attr Byte Object attribute has been set.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  protected boolean attrByteObjectESet;

  /**
   * The default value of the '{@link #getAttrChar() <em>Attr Char</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttrChar()
   * @generated
   * @ordered
   */
  protected static final char ATTR_CHAR_EDEFAULT = '\u0000';

  /**
   * The cached value of the '{@link #getAttrChar() <em>Attr Char</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttrChar()
   * @generated
   * @ordered
   */
  protected char attrChar = ATTR_CHAR_EDEFAULT;

  /**
   * This is true if the Attr Char attribute has been set.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  protected boolean attrCharESet;

  /**
   * The default value of the '{@link #getAttrCharacterObject() <em>Attr Character Object</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttrCharacterObject()
   * @generated
   * @ordered
   */
  protected static final Character ATTR_CHARACTER_OBJECT_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getAttrCharacterObject() <em>Attr Character Object</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttrCharacterObject()
   * @generated
   * @ordered
   */
  protected Character attrCharacterObject = ATTR_CHARACTER_OBJECT_EDEFAULT;

  /**
   * This is true if the Attr Character Object attribute has been set.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  protected boolean attrCharacterObjectESet;

  /**
   * The default value of the '{@link #getAttrDate() <em>Attr Date</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttrDate()
   * @generated
   * @ordered
   */
  protected static final Date ATTR_DATE_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getAttrDate() <em>Attr Date</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttrDate()
   * @generated
   * @ordered
   */
  protected Date attrDate = ATTR_DATE_EDEFAULT;

  /**
   * This is true if the Attr Date attribute has been set.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  protected boolean attrDateESet;

  /**
   * The default value of the '{@link #getAttrDouble() <em>Attr Double</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttrDouble()
   * @generated
   * @ordered
   */
  protected static final double ATTR_DOUBLE_EDEFAULT = 0.0;

  /**
   * The cached value of the '{@link #getAttrDouble() <em>Attr Double</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttrDouble()
   * @generated
   * @ordered
   */
  protected double attrDouble = ATTR_DOUBLE_EDEFAULT;

  /**
   * This is true if the Attr Double attribute has been set.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  protected boolean attrDoubleESet;

  /**
   * The default value of the '{@link #getAttrDoubleObject() <em>Attr Double Object</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttrDoubleObject()
   * @generated
   * @ordered
   */
  protected static final Double ATTR_DOUBLE_OBJECT_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getAttrDoubleObject() <em>Attr Double Object</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttrDoubleObject()
   * @generated
   * @ordered
   */
  protected Double attrDoubleObject = ATTR_DOUBLE_OBJECT_EDEFAULT;

  /**
   * This is true if the Attr Double Object attribute has been set.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  protected boolean attrDoubleObjectESet;

  /**
   * The default value of the '{@link #getAttrFloat() <em>Attr Float</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttrFloat()
   * @generated
   * @ordered
   */
  protected static final float ATTR_FLOAT_EDEFAULT = 0.0F;

  /**
   * The cached value of the '{@link #getAttrFloat() <em>Attr Float</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttrFloat()
   * @generated
   * @ordered
   */
  protected float attrFloat = ATTR_FLOAT_EDEFAULT;

  /**
   * This is true if the Attr Float attribute has been set.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  protected boolean attrFloatESet;

  /**
   * The default value of the '{@link #getAttrFloatObject() <em>Attr Float Object</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttrFloatObject()
   * @generated
   * @ordered
   */
  protected static final Float ATTR_FLOAT_OBJECT_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getAttrFloatObject() <em>Attr Float Object</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttrFloatObject()
   * @generated
   * @ordered
   */
  protected Float attrFloatObject = ATTR_FLOAT_OBJECT_EDEFAULT;

  /**
   * This is true if the Attr Float Object attribute has been set.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  protected boolean attrFloatObjectESet;

  /**
   * The default value of the '{@link #getAttrInt() <em>Attr Int</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttrInt()
   * @generated
   * @ordered
   */
  protected static final int ATTR_INT_EDEFAULT = 0;

  /**
   * The cached value of the '{@link #getAttrInt() <em>Attr Int</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttrInt()
   * @generated
   * @ordered
   */
  protected int attrInt = ATTR_INT_EDEFAULT;

  /**
   * This is true if the Attr Int attribute has been set.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  protected boolean attrIntESet;

  /**
   * The default value of the '{@link #getAttrIntegerObject() <em>Attr Integer Object</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttrIntegerObject()
   * @generated
   * @ordered
   */
  protected static final Integer ATTR_INTEGER_OBJECT_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getAttrIntegerObject() <em>Attr Integer Object</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttrIntegerObject()
   * @generated
   * @ordered
   */
  protected Integer attrIntegerObject = ATTR_INTEGER_OBJECT_EDEFAULT;

  /**
   * This is true if the Attr Integer Object attribute has been set.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  protected boolean attrIntegerObjectESet;

  /**
   * The cached value of the '{@link #getAttrJavaClass() <em>Attr Java Class</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttrJavaClass()
   * @generated
   * @ordered
   */
  protected Class<?> attrJavaClass;

  /**
   * This is true if the Attr Java Class attribute has been set.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  protected boolean attrJavaClassESet;

  /**
   * The default value of the '{@link #getAttrJavaObject() <em>Attr Java Object</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttrJavaObject()
   * @generated
   * @ordered
   */
  protected static final Object ATTR_JAVA_OBJECT_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getAttrJavaObject() <em>Attr Java Object</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttrJavaObject()
   * @generated
   * @ordered
   */
  protected Object attrJavaObject = ATTR_JAVA_OBJECT_EDEFAULT;

  /**
   * This is true if the Attr Java Object attribute has been set.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  protected boolean attrJavaObjectESet;

  /**
   * The default value of the '{@link #getAttrLong() <em>Attr Long</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttrLong()
   * @generated
   * @ordered
   */
  protected static final long ATTR_LONG_EDEFAULT = 0L;

  /**
   * The cached value of the '{@link #getAttrLong() <em>Attr Long</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttrLong()
   * @generated
   * @ordered
   */
  protected long attrLong = ATTR_LONG_EDEFAULT;

  /**
   * This is true if the Attr Long attribute has been set.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  protected boolean attrLongESet;

  /**
   * The default value of the '{@link #getAttrLongObject() <em>Attr Long Object</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttrLongObject()
   * @generated
   * @ordered
   */
  protected static final Long ATTR_LONG_OBJECT_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getAttrLongObject() <em>Attr Long Object</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttrLongObject()
   * @generated
   * @ordered
   */
  protected Long attrLongObject = ATTR_LONG_OBJECT_EDEFAULT;

  /**
   * This is true if the Attr Long Object attribute has been set.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  protected boolean attrLongObjectESet;

  /**
   * The default value of the '{@link #getAttrShort() <em>Attr Short</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttrShort()
   * @generated
   * @ordered
   */
  protected static final short ATTR_SHORT_EDEFAULT = 0;

  /**
   * The cached value of the '{@link #getAttrShort() <em>Attr Short</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttrShort()
   * @generated
   * @ordered
   */
  protected short attrShort = ATTR_SHORT_EDEFAULT;

  /**
   * This is true if the Attr Short attribute has been set.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  protected boolean attrShortESet;

  /**
   * The default value of the '{@link #getAttrShortObject() <em>Attr Short Object</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttrShortObject()
   * @generated
   * @ordered
   */
  protected static final Short ATTR_SHORT_OBJECT_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getAttrShortObject() <em>Attr Short Object</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttrShortObject()
   * @generated
   * @ordered
   */
  protected Short attrShortObject = ATTR_SHORT_OBJECT_EDEFAULT;

  /**
   * This is true if the Attr Short Object attribute has been set.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  protected boolean attrShortObjectESet;

  /**
   * The default value of the '{@link #getAttrString() <em>Attr String</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttrString()
   * @generated
   * @ordered
   */
  protected static final String ATTR_STRING_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getAttrString() <em>Attr String</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttrString()
   * @generated
   * @ordered
   */
  protected String attrString = ATTR_STRING_EDEFAULT;

  /**
   * This is true if the Attr String attribute has been set.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  protected boolean attrStringESet;

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
    return Model6Package.eINSTANCE.getUnsettableAttributes();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public BigDecimal getAttrBigDecimal()
  {
    return attrBigDecimal;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrBigDecimal(BigDecimal newAttrBigDecimal)
  {
    BigDecimal oldAttrBigDecimal = attrBigDecimal;
    attrBigDecimal = newAttrBigDecimal;
    boolean oldAttrBigDecimalESet = attrBigDecimalESet;
    attrBigDecimalESet = true;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_BIG_DECIMAL, oldAttrBigDecimal, attrBigDecimal,
          !oldAttrBigDecimalESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetAttrBigDecimal()
  {
    BigDecimal oldAttrBigDecimal = attrBigDecimal;
    boolean oldAttrBigDecimalESet = attrBigDecimalESet;
    attrBigDecimal = ATTR_BIG_DECIMAL_EDEFAULT;
    attrBigDecimalESet = false;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.UNSET, Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_BIG_DECIMAL, oldAttrBigDecimal,
          ATTR_BIG_DECIMAL_EDEFAULT, oldAttrBigDecimalESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetAttrBigDecimal()
  {
    return attrBigDecimalESet;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public BigInteger getAttrBigInteger()
  {
    return attrBigInteger;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrBigInteger(BigInteger newAttrBigInteger)
  {
    BigInteger oldAttrBigInteger = attrBigInteger;
    attrBigInteger = newAttrBigInteger;
    boolean oldAttrBigIntegerESet = attrBigIntegerESet;
    attrBigIntegerESet = true;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_BIG_INTEGER, oldAttrBigInteger, attrBigInteger,
          !oldAttrBigIntegerESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetAttrBigInteger()
  {
    BigInteger oldAttrBigInteger = attrBigInteger;
    boolean oldAttrBigIntegerESet = attrBigIntegerESet;
    attrBigInteger = ATTR_BIG_INTEGER_EDEFAULT;
    attrBigIntegerESet = false;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.UNSET, Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_BIG_INTEGER, oldAttrBigInteger,
          ATTR_BIG_INTEGER_EDEFAULT, oldAttrBigIntegerESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetAttrBigInteger()
  {
    return attrBigIntegerESet;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isAttrBoolean()
  {
    return attrBoolean;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrBoolean(boolean newAttrBoolean)
  {
    boolean oldAttrBoolean = attrBoolean;
    attrBoolean = newAttrBoolean;
    boolean oldAttrBooleanESet = attrBooleanESet;
    attrBooleanESet = true;
    if (eNotificationRequired())
    {
      eNotify(
          new ENotificationImpl(this, Notification.SET, Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_BOOLEAN, oldAttrBoolean, attrBoolean, !oldAttrBooleanESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetAttrBoolean()
  {
    boolean oldAttrBoolean = attrBoolean;
    boolean oldAttrBooleanESet = attrBooleanESet;
    attrBoolean = ATTR_BOOLEAN_EDEFAULT;
    attrBooleanESet = false;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.UNSET, Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_BOOLEAN, oldAttrBoolean, ATTR_BOOLEAN_EDEFAULT,
          oldAttrBooleanESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetAttrBoolean()
  {
    return attrBooleanESet;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Boolean getAttrBooleanObject()
  {
    return attrBooleanObject;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrBooleanObject(Boolean newAttrBooleanObject)
  {
    Boolean oldAttrBooleanObject = attrBooleanObject;
    attrBooleanObject = newAttrBooleanObject;
    boolean oldAttrBooleanObjectESet = attrBooleanObjectESet;
    attrBooleanObjectESet = true;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_BOOLEAN_OBJECT, oldAttrBooleanObject, attrBooleanObject,
          !oldAttrBooleanObjectESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetAttrBooleanObject()
  {
    Boolean oldAttrBooleanObject = attrBooleanObject;
    boolean oldAttrBooleanObjectESet = attrBooleanObjectESet;
    attrBooleanObject = ATTR_BOOLEAN_OBJECT_EDEFAULT;
    attrBooleanObjectESet = false;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.UNSET, Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_BOOLEAN_OBJECT, oldAttrBooleanObject,
          ATTR_BOOLEAN_OBJECT_EDEFAULT, oldAttrBooleanObjectESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetAttrBooleanObject()
  {
    return attrBooleanObjectESet;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public byte getAttrByte()
  {
    return attrByte;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrByte(byte newAttrByte)
  {
    byte oldAttrByte = attrByte;
    attrByte = newAttrByte;
    boolean oldAttrByteESet = attrByteESet;
    attrByteESet = true;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_BYTE, oldAttrByte, attrByte, !oldAttrByteESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetAttrByte()
  {
    byte oldAttrByte = attrByte;
    boolean oldAttrByteESet = attrByteESet;
    attrByte = ATTR_BYTE_EDEFAULT;
    attrByteESet = false;
    if (eNotificationRequired())
    {
      eNotify(
          new ENotificationImpl(this, Notification.UNSET, Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_BYTE, oldAttrByte, ATTR_BYTE_EDEFAULT, oldAttrByteESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetAttrByte()
  {
    return attrByteESet;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public byte[] getAttrByteArray()
  {
    return attrByteArray;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrByteArray(byte[] newAttrByteArray)
  {
    byte[] oldAttrByteArray = attrByteArray;
    attrByteArray = newAttrByteArray;
    boolean oldAttrByteArrayESet = attrByteArrayESet;
    attrByteArrayESet = true;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_BYTE_ARRAY, oldAttrByteArray, attrByteArray,
          !oldAttrByteArrayESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetAttrByteArray()
  {
    byte[] oldAttrByteArray = attrByteArray;
    boolean oldAttrByteArrayESet = attrByteArrayESet;
    attrByteArray = ATTR_BYTE_ARRAY_EDEFAULT;
    attrByteArrayESet = false;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.UNSET, Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_BYTE_ARRAY, oldAttrByteArray, ATTR_BYTE_ARRAY_EDEFAULT,
          oldAttrByteArrayESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetAttrByteArray()
  {
    return attrByteArrayESet;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Byte getAttrByteObject()
  {
    return attrByteObject;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrByteObject(Byte newAttrByteObject)
  {
    Byte oldAttrByteObject = attrByteObject;
    attrByteObject = newAttrByteObject;
    boolean oldAttrByteObjectESet = attrByteObjectESet;
    attrByteObjectESet = true;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_BYTE_OBJECT, oldAttrByteObject, attrByteObject,
          !oldAttrByteObjectESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetAttrByteObject()
  {
    Byte oldAttrByteObject = attrByteObject;
    boolean oldAttrByteObjectESet = attrByteObjectESet;
    attrByteObject = ATTR_BYTE_OBJECT_EDEFAULT;
    attrByteObjectESet = false;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.UNSET, Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_BYTE_OBJECT, oldAttrByteObject,
          ATTR_BYTE_OBJECT_EDEFAULT, oldAttrByteObjectESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetAttrByteObject()
  {
    return attrByteObjectESet;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public char getAttrChar()
  {
    return attrChar;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrChar(char newAttrChar)
  {
    char oldAttrChar = attrChar;
    attrChar = newAttrChar;
    boolean oldAttrCharESet = attrCharESet;
    attrCharESet = true;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_CHAR, oldAttrChar, attrChar, !oldAttrCharESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetAttrChar()
  {
    char oldAttrChar = attrChar;
    boolean oldAttrCharESet = attrCharESet;
    attrChar = ATTR_CHAR_EDEFAULT;
    attrCharESet = false;
    if (eNotificationRequired())
    {
      eNotify(
          new ENotificationImpl(this, Notification.UNSET, Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_CHAR, oldAttrChar, ATTR_CHAR_EDEFAULT, oldAttrCharESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetAttrChar()
  {
    return attrCharESet;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Character getAttrCharacterObject()
  {
    return attrCharacterObject;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrCharacterObject(Character newAttrCharacterObject)
  {
    Character oldAttrCharacterObject = attrCharacterObject;
    attrCharacterObject = newAttrCharacterObject;
    boolean oldAttrCharacterObjectESet = attrCharacterObjectESet;
    attrCharacterObjectESet = true;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_CHARACTER_OBJECT, oldAttrCharacterObject,
          attrCharacterObject, !oldAttrCharacterObjectESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetAttrCharacterObject()
  {
    Character oldAttrCharacterObject = attrCharacterObject;
    boolean oldAttrCharacterObjectESet = attrCharacterObjectESet;
    attrCharacterObject = ATTR_CHARACTER_OBJECT_EDEFAULT;
    attrCharacterObjectESet = false;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.UNSET, Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_CHARACTER_OBJECT, oldAttrCharacterObject,
          ATTR_CHARACTER_OBJECT_EDEFAULT, oldAttrCharacterObjectESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetAttrCharacterObject()
  {
    return attrCharacterObjectESet;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Date getAttrDate()
  {
    return attrDate;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrDate(Date newAttrDate)
  {
    Date oldAttrDate = attrDate;
    attrDate = newAttrDate;
    boolean oldAttrDateESet = attrDateESet;
    attrDateESet = true;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_DATE, oldAttrDate, attrDate, !oldAttrDateESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetAttrDate()
  {
    Date oldAttrDate = attrDate;
    boolean oldAttrDateESet = attrDateESet;
    attrDate = ATTR_DATE_EDEFAULT;
    attrDateESet = false;
    if (eNotificationRequired())
    {
      eNotify(
          new ENotificationImpl(this, Notification.UNSET, Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_DATE, oldAttrDate, ATTR_DATE_EDEFAULT, oldAttrDateESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetAttrDate()
  {
    return attrDateESet;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public double getAttrDouble()
  {
    return attrDouble;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrDouble(double newAttrDouble)
  {
    double oldAttrDouble = attrDouble;
    attrDouble = newAttrDouble;
    boolean oldAttrDoubleESet = attrDoubleESet;
    attrDoubleESet = true;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_DOUBLE, oldAttrDouble, attrDouble, !oldAttrDoubleESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetAttrDouble()
  {
    double oldAttrDouble = attrDouble;
    boolean oldAttrDoubleESet = attrDoubleESet;
    attrDouble = ATTR_DOUBLE_EDEFAULT;
    attrDoubleESet = false;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.UNSET, Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_DOUBLE, oldAttrDouble, ATTR_DOUBLE_EDEFAULT,
          oldAttrDoubleESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetAttrDouble()
  {
    return attrDoubleESet;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Double getAttrDoubleObject()
  {
    return attrDoubleObject;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrDoubleObject(Double newAttrDoubleObject)
  {
    Double oldAttrDoubleObject = attrDoubleObject;
    attrDoubleObject = newAttrDoubleObject;
    boolean oldAttrDoubleObjectESet = attrDoubleObjectESet;
    attrDoubleObjectESet = true;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_DOUBLE_OBJECT, oldAttrDoubleObject, attrDoubleObject,
          !oldAttrDoubleObjectESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetAttrDoubleObject()
  {
    Double oldAttrDoubleObject = attrDoubleObject;
    boolean oldAttrDoubleObjectESet = attrDoubleObjectESet;
    attrDoubleObject = ATTR_DOUBLE_OBJECT_EDEFAULT;
    attrDoubleObjectESet = false;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.UNSET, Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_DOUBLE_OBJECT, oldAttrDoubleObject,
          ATTR_DOUBLE_OBJECT_EDEFAULT, oldAttrDoubleObjectESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetAttrDoubleObject()
  {
    return attrDoubleObjectESet;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public float getAttrFloat()
  {
    return attrFloat;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrFloat(float newAttrFloat)
  {
    float oldAttrFloat = attrFloat;
    attrFloat = newAttrFloat;
    boolean oldAttrFloatESet = attrFloatESet;
    attrFloatESet = true;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_FLOAT, oldAttrFloat, attrFloat, !oldAttrFloatESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetAttrFloat()
  {
    float oldAttrFloat = attrFloat;
    boolean oldAttrFloatESet = attrFloatESet;
    attrFloat = ATTR_FLOAT_EDEFAULT;
    attrFloatESet = false;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.UNSET, Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_FLOAT, oldAttrFloat, ATTR_FLOAT_EDEFAULT,
          oldAttrFloatESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetAttrFloat()
  {
    return attrFloatESet;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Float getAttrFloatObject()
  {
    return attrFloatObject;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrFloatObject(Float newAttrFloatObject)
  {
    Float oldAttrFloatObject = attrFloatObject;
    attrFloatObject = newAttrFloatObject;
    boolean oldAttrFloatObjectESet = attrFloatObjectESet;
    attrFloatObjectESet = true;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_FLOAT_OBJECT, oldAttrFloatObject, attrFloatObject,
          !oldAttrFloatObjectESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetAttrFloatObject()
  {
    Float oldAttrFloatObject = attrFloatObject;
    boolean oldAttrFloatObjectESet = attrFloatObjectESet;
    attrFloatObject = ATTR_FLOAT_OBJECT_EDEFAULT;
    attrFloatObjectESet = false;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.UNSET, Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_FLOAT_OBJECT, oldAttrFloatObject,
          ATTR_FLOAT_OBJECT_EDEFAULT, oldAttrFloatObjectESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetAttrFloatObject()
  {
    return attrFloatObjectESet;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int getAttrInt()
  {
    return attrInt;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrInt(int newAttrInt)
  {
    int oldAttrInt = attrInt;
    attrInt = newAttrInt;
    boolean oldAttrIntESet = attrIntESet;
    attrIntESet = true;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_INT, oldAttrInt, attrInt, !oldAttrIntESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetAttrInt()
  {
    int oldAttrInt = attrInt;
    boolean oldAttrIntESet = attrIntESet;
    attrInt = ATTR_INT_EDEFAULT;
    attrIntESet = false;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.UNSET, Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_INT, oldAttrInt, ATTR_INT_EDEFAULT, oldAttrIntESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetAttrInt()
  {
    return attrIntESet;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Integer getAttrIntegerObject()
  {
    return attrIntegerObject;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrIntegerObject(Integer newAttrIntegerObject)
  {
    Integer oldAttrIntegerObject = attrIntegerObject;
    attrIntegerObject = newAttrIntegerObject;
    boolean oldAttrIntegerObjectESet = attrIntegerObjectESet;
    attrIntegerObjectESet = true;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_INTEGER_OBJECT, oldAttrIntegerObject, attrIntegerObject,
          !oldAttrIntegerObjectESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetAttrIntegerObject()
  {
    Integer oldAttrIntegerObject = attrIntegerObject;
    boolean oldAttrIntegerObjectESet = attrIntegerObjectESet;
    attrIntegerObject = ATTR_INTEGER_OBJECT_EDEFAULT;
    attrIntegerObjectESet = false;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.UNSET, Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_INTEGER_OBJECT, oldAttrIntegerObject,
          ATTR_INTEGER_OBJECT_EDEFAULT, oldAttrIntegerObjectESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetAttrIntegerObject()
  {
    return attrIntegerObjectESet;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Class<?> getAttrJavaClass()
  {
    return attrJavaClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrJavaClass(Class<?> newAttrJavaClass)
  {
    Class<?> oldAttrJavaClass = attrJavaClass;
    attrJavaClass = newAttrJavaClass;
    boolean oldAttrJavaClassESet = attrJavaClassESet;
    attrJavaClassESet = true;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_JAVA_CLASS, oldAttrJavaClass, attrJavaClass,
          !oldAttrJavaClassESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetAttrJavaClass()
  {
    Class<?> oldAttrJavaClass = attrJavaClass;
    boolean oldAttrJavaClassESet = attrJavaClassESet;
    attrJavaClass = null;
    attrJavaClassESet = false;
    if (eNotificationRequired())
    {
      eNotify(
          new ENotificationImpl(this, Notification.UNSET, Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_JAVA_CLASS, oldAttrJavaClass, null, oldAttrJavaClassESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetAttrJavaClass()
  {
    return attrJavaClassESet;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object getAttrJavaObject()
  {
    return attrJavaObject;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrJavaObject(Object newAttrJavaObject)
  {
    Object oldAttrJavaObject = attrJavaObject;
    attrJavaObject = newAttrJavaObject;
    boolean oldAttrJavaObjectESet = attrJavaObjectESet;
    attrJavaObjectESet = true;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_JAVA_OBJECT, oldAttrJavaObject, attrJavaObject,
          !oldAttrJavaObjectESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetAttrJavaObject()
  {
    Object oldAttrJavaObject = attrJavaObject;
    boolean oldAttrJavaObjectESet = attrJavaObjectESet;
    attrJavaObject = ATTR_JAVA_OBJECT_EDEFAULT;
    attrJavaObjectESet = false;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.UNSET, Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_JAVA_OBJECT, oldAttrJavaObject,
          ATTR_JAVA_OBJECT_EDEFAULT, oldAttrJavaObjectESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetAttrJavaObject()
  {
    return attrJavaObjectESet;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public long getAttrLong()
  {
    return attrLong;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrLong(long newAttrLong)
  {
    long oldAttrLong = attrLong;
    attrLong = newAttrLong;
    boolean oldAttrLongESet = attrLongESet;
    attrLongESet = true;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_LONG, oldAttrLong, attrLong, !oldAttrLongESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetAttrLong()
  {
    long oldAttrLong = attrLong;
    boolean oldAttrLongESet = attrLongESet;
    attrLong = ATTR_LONG_EDEFAULT;
    attrLongESet = false;
    if (eNotificationRequired())
    {
      eNotify(
          new ENotificationImpl(this, Notification.UNSET, Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_LONG, oldAttrLong, ATTR_LONG_EDEFAULT, oldAttrLongESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetAttrLong()
  {
    return attrLongESet;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Long getAttrLongObject()
  {
    return attrLongObject;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrLongObject(Long newAttrLongObject)
  {
    Long oldAttrLongObject = attrLongObject;
    attrLongObject = newAttrLongObject;
    boolean oldAttrLongObjectESet = attrLongObjectESet;
    attrLongObjectESet = true;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_LONG_OBJECT, oldAttrLongObject, attrLongObject,
          !oldAttrLongObjectESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetAttrLongObject()
  {
    Long oldAttrLongObject = attrLongObject;
    boolean oldAttrLongObjectESet = attrLongObjectESet;
    attrLongObject = ATTR_LONG_OBJECT_EDEFAULT;
    attrLongObjectESet = false;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.UNSET, Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_LONG_OBJECT, oldAttrLongObject,
          ATTR_LONG_OBJECT_EDEFAULT, oldAttrLongObjectESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetAttrLongObject()
  {
    return attrLongObjectESet;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public short getAttrShort()
  {
    return attrShort;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrShort(short newAttrShort)
  {
    short oldAttrShort = attrShort;
    attrShort = newAttrShort;
    boolean oldAttrShortESet = attrShortESet;
    attrShortESet = true;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_SHORT, oldAttrShort, attrShort, !oldAttrShortESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetAttrShort()
  {
    short oldAttrShort = attrShort;
    boolean oldAttrShortESet = attrShortESet;
    attrShort = ATTR_SHORT_EDEFAULT;
    attrShortESet = false;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.UNSET, Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_SHORT, oldAttrShort, ATTR_SHORT_EDEFAULT,
          oldAttrShortESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetAttrShort()
  {
    return attrShortESet;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Short getAttrShortObject()
  {
    return attrShortObject;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrShortObject(Short newAttrShortObject)
  {
    Short oldAttrShortObject = attrShortObject;
    attrShortObject = newAttrShortObject;
    boolean oldAttrShortObjectESet = attrShortObjectESet;
    attrShortObjectESet = true;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_SHORT_OBJECT, oldAttrShortObject, attrShortObject,
          !oldAttrShortObjectESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetAttrShortObject()
  {
    Short oldAttrShortObject = attrShortObject;
    boolean oldAttrShortObjectESet = attrShortObjectESet;
    attrShortObject = ATTR_SHORT_OBJECT_EDEFAULT;
    attrShortObjectESet = false;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.UNSET, Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_SHORT_OBJECT, oldAttrShortObject,
          ATTR_SHORT_OBJECT_EDEFAULT, oldAttrShortObjectESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetAttrShortObject()
  {
    return attrShortObjectESet;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getAttrString()
  {
    return attrString;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrString(String newAttrString)
  {
    String oldAttrString = attrString;
    attrString = newAttrString;
    boolean oldAttrStringESet = attrStringESet;
    attrStringESet = true;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_STRING, oldAttrString, attrString, !oldAttrStringESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetAttrString()
  {
    String oldAttrString = attrString;
    boolean oldAttrStringESet = attrStringESet;
    attrString = ATTR_STRING_EDEFAULT;
    attrStringESet = false;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.UNSET, Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_STRING, oldAttrString, ATTR_STRING_EDEFAULT,
          oldAttrStringESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetAttrString()
  {
    return attrStringESet;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_BIG_DECIMAL:
      return getAttrBigDecimal();
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_BIG_INTEGER:
      return getAttrBigInteger();
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_BOOLEAN:
      return isAttrBoolean();
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_BOOLEAN_OBJECT:
      return getAttrBooleanObject();
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_BYTE:
      return getAttrByte();
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_BYTE_ARRAY:
      return getAttrByteArray();
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_BYTE_OBJECT:
      return getAttrByteObject();
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_CHAR:
      return getAttrChar();
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_CHARACTER_OBJECT:
      return getAttrCharacterObject();
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_DATE:
      return getAttrDate();
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_DOUBLE:
      return getAttrDouble();
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_DOUBLE_OBJECT:
      return getAttrDoubleObject();
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_FLOAT:
      return getAttrFloat();
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_FLOAT_OBJECT:
      return getAttrFloatObject();
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_INT:
      return getAttrInt();
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_INTEGER_OBJECT:
      return getAttrIntegerObject();
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_JAVA_CLASS:
      return getAttrJavaClass();
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_JAVA_OBJECT:
      return getAttrJavaObject();
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_LONG:
      return getAttrLong();
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_LONG_OBJECT:
      return getAttrLongObject();
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_SHORT:
      return getAttrShort();
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_SHORT_OBJECT:
      return getAttrShortObject();
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_STRING:
      return getAttrString();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_BIG_DECIMAL:
      setAttrBigDecimal((BigDecimal)newValue);
      return;
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_BIG_INTEGER:
      setAttrBigInteger((BigInteger)newValue);
      return;
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_BOOLEAN:
      setAttrBoolean((Boolean)newValue);
      return;
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_BOOLEAN_OBJECT:
      setAttrBooleanObject((Boolean)newValue);
      return;
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_BYTE:
      setAttrByte((Byte)newValue);
      return;
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_BYTE_ARRAY:
      setAttrByteArray((byte[])newValue);
      return;
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_BYTE_OBJECT:
      setAttrByteObject((Byte)newValue);
      return;
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_CHAR:
      setAttrChar((Character)newValue);
      return;
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_CHARACTER_OBJECT:
      setAttrCharacterObject((Character)newValue);
      return;
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_DATE:
      setAttrDate((Date)newValue);
      return;
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_DOUBLE:
      setAttrDouble((Double)newValue);
      return;
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_DOUBLE_OBJECT:
      setAttrDoubleObject((Double)newValue);
      return;
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_FLOAT:
      setAttrFloat((Float)newValue);
      return;
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_FLOAT_OBJECT:
      setAttrFloatObject((Float)newValue);
      return;
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_INT:
      setAttrInt((Integer)newValue);
      return;
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_INTEGER_OBJECT:
      setAttrIntegerObject((Integer)newValue);
      return;
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_JAVA_CLASS:
      setAttrJavaClass((Class<?>)newValue);
      return;
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_JAVA_OBJECT:
      setAttrJavaObject(newValue);
      return;
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_LONG:
      setAttrLong((Long)newValue);
      return;
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_LONG_OBJECT:
      setAttrLongObject((Long)newValue);
      return;
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_SHORT:
      setAttrShort((Short)newValue);
      return;
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_SHORT_OBJECT:
      setAttrShortObject((Short)newValue);
      return;
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_STRING:
      setAttrString((String)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_BIG_DECIMAL:
      unsetAttrBigDecimal();
      return;
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_BIG_INTEGER:
      unsetAttrBigInteger();
      return;
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_BOOLEAN:
      unsetAttrBoolean();
      return;
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_BOOLEAN_OBJECT:
      unsetAttrBooleanObject();
      return;
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_BYTE:
      unsetAttrByte();
      return;
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_BYTE_ARRAY:
      unsetAttrByteArray();
      return;
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_BYTE_OBJECT:
      unsetAttrByteObject();
      return;
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_CHAR:
      unsetAttrChar();
      return;
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_CHARACTER_OBJECT:
      unsetAttrCharacterObject();
      return;
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_DATE:
      unsetAttrDate();
      return;
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_DOUBLE:
      unsetAttrDouble();
      return;
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_DOUBLE_OBJECT:
      unsetAttrDoubleObject();
      return;
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_FLOAT:
      unsetAttrFloat();
      return;
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_FLOAT_OBJECT:
      unsetAttrFloatObject();
      return;
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_INT:
      unsetAttrInt();
      return;
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_INTEGER_OBJECT:
      unsetAttrIntegerObject();
      return;
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_JAVA_CLASS:
      unsetAttrJavaClass();
      return;
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_JAVA_OBJECT:
      unsetAttrJavaObject();
      return;
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_LONG:
      unsetAttrLong();
      return;
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_LONG_OBJECT:
      unsetAttrLongObject();
      return;
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_SHORT:
      unsetAttrShort();
      return;
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_SHORT_OBJECT:
      unsetAttrShortObject();
      return;
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_STRING:
      unsetAttrString();
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_BIG_DECIMAL:
      return isSetAttrBigDecimal();
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_BIG_INTEGER:
      return isSetAttrBigInteger();
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_BOOLEAN:
      return isSetAttrBoolean();
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_BOOLEAN_OBJECT:
      return isSetAttrBooleanObject();
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_BYTE:
      return isSetAttrByte();
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_BYTE_ARRAY:
      return isSetAttrByteArray();
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_BYTE_OBJECT:
      return isSetAttrByteObject();
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_CHAR:
      return isSetAttrChar();
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_CHARACTER_OBJECT:
      return isSetAttrCharacterObject();
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_DATE:
      return isSetAttrDate();
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_DOUBLE:
      return isSetAttrDouble();
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_DOUBLE_OBJECT:
      return isSetAttrDoubleObject();
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_FLOAT:
      return isSetAttrFloat();
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_FLOAT_OBJECT:
      return isSetAttrFloatObject();
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_INT:
      return isSetAttrInt();
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_INTEGER_OBJECT:
      return isSetAttrIntegerObject();
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_JAVA_CLASS:
      return isSetAttrJavaClass();
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_JAVA_OBJECT:
      return isSetAttrJavaObject();
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_LONG:
      return isSetAttrLong();
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_LONG_OBJECT:
      return isSetAttrLongObject();
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_SHORT:
      return isSetAttrShort();
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_SHORT_OBJECT:
      return isSetAttrShortObject();
    case Model6Package.UNSETTABLE_ATTRIBUTES__ATTR_STRING:
      return isSetAttrString();
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy())
    {
      return super.toString();
    }

    StringBuilder result = new StringBuilder(super.toString());
    result.append(" (attrBigDecimal: ");
    if (attrBigDecimalESet)
    {
      result.append(attrBigDecimal);
    }
    else
    {
      result.append("<unset>");
    }
    result.append(", attrBigInteger: ");
    if (attrBigIntegerESet)
    {
      result.append(attrBigInteger);
    }
    else
    {
      result.append("<unset>");
    }
    result.append(", attrBoolean: ");
    if (attrBooleanESet)
    {
      result.append(attrBoolean);
    }
    else
    {
      result.append("<unset>");
    }
    result.append(", attrBooleanObject: ");
    if (attrBooleanObjectESet)
    {
      result.append(attrBooleanObject);
    }
    else
    {
      result.append("<unset>");
    }
    result.append(", attrByte: ");
    if (attrByteESet)
    {
      result.append(attrByte);
    }
    else
    {
      result.append("<unset>");
    }
    result.append(", attrByteArray: ");
    if (attrByteArrayESet)
    {
      result.append(attrByteArray);
    }
    else
    {
      result.append("<unset>");
    }
    result.append(", attrByteObject: ");
    if (attrByteObjectESet)
    {
      result.append(attrByteObject);
    }
    else
    {
      result.append("<unset>");
    }
    result.append(", attrChar: ");
    if (attrCharESet)
    {
      result.append(attrChar);
    }
    else
    {
      result.append("<unset>");
    }
    result.append(", attrCharacterObject: ");
    if (attrCharacterObjectESet)
    {
      result.append(attrCharacterObject);
    }
    else
    {
      result.append("<unset>");
    }
    result.append(", attrDate: ");
    if (attrDateESet)
    {
      result.append(attrDate);
    }
    else
    {
      result.append("<unset>");
    }
    result.append(", attrDouble: ");
    if (attrDoubleESet)
    {
      result.append(attrDouble);
    }
    else
    {
      result.append("<unset>");
    }
    result.append(", attrDoubleObject: ");
    if (attrDoubleObjectESet)
    {
      result.append(attrDoubleObject);
    }
    else
    {
      result.append("<unset>");
    }
    result.append(", attrFloat: ");
    if (attrFloatESet)
    {
      result.append(attrFloat);
    }
    else
    {
      result.append("<unset>");
    }
    result.append(", attrFloatObject: ");
    if (attrFloatObjectESet)
    {
      result.append(attrFloatObject);
    }
    else
    {
      result.append("<unset>");
    }
    result.append(", attrInt: ");
    if (attrIntESet)
    {
      result.append(attrInt);
    }
    else
    {
      result.append("<unset>");
    }
    result.append(", attrIntegerObject: ");
    if (attrIntegerObjectESet)
    {
      result.append(attrIntegerObject);
    }
    else
    {
      result.append("<unset>");
    }
    result.append(", attrJavaClass: ");
    if (attrJavaClassESet)
    {
      result.append(attrJavaClass);
    }
    else
    {
      result.append("<unset>");
    }
    result.append(", attrJavaObject: ");
    if (attrJavaObjectESet)
    {
      result.append(attrJavaObject);
    }
    else
    {
      result.append("<unset>");
    }
    result.append(", attrLong: ");
    if (attrLongESet)
    {
      result.append(attrLong);
    }
    else
    {
      result.append("<unset>");
    }
    result.append(", attrLongObject: ");
    if (attrLongObjectESet)
    {
      result.append(attrLongObject);
    }
    else
    {
      result.append("<unset>");
    }
    result.append(", attrShort: ");
    if (attrShortESet)
    {
      result.append(attrShort);
    }
    else
    {
      result.append("<unset>");
    }
    result.append(", attrShortObject: ");
    if (attrShortObjectESet)
    {
      result.append(attrShortObject);
    }
    else
    {
      result.append("<unset>");
    }
    result.append(", attrString: ");
    if (attrStringESet)
    {
      result.append(attrString);
    }
    else
    {
      result.append("<unset>");
    }
    result.append(')');
    return result.toString();
  }

} // UnsettableAttributesImpl
