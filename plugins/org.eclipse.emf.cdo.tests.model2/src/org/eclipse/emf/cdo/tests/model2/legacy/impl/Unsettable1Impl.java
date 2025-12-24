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
package org.eclipse.emf.cdo.tests.model2.legacy.impl;

import org.eclipse.emf.cdo.tests.model1.VAT;
import org.eclipse.emf.cdo.tests.model2.Unsettable1;
import org.eclipse.emf.cdo.tests.model2.legacy.Model2Package;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import java.util.Date;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Unsettable1</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.legacy.impl.Unsettable1Impl#isUnsettableBoolean <em>Unsettable Boolean</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.legacy.impl.Unsettable1Impl#getUnsettableByte <em>Unsettable Byte</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.legacy.impl.Unsettable1Impl#getUnsettableChar <em>Unsettable Char</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.legacy.impl.Unsettable1Impl#getUnsettableDate <em>Unsettable Date</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.legacy.impl.Unsettable1Impl#getUnsettableDouble <em>Unsettable Double</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.legacy.impl.Unsettable1Impl#getUnsettableFloat <em>Unsettable Float</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.legacy.impl.Unsettable1Impl#getUnsettableInt <em>Unsettable Int</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.legacy.impl.Unsettable1Impl#getUnsettableLong <em>Unsettable Long</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.legacy.impl.Unsettable1Impl#getUnsettableShort <em>Unsettable Short</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.legacy.impl.Unsettable1Impl#getUnsettableString <em>Unsettable String</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.legacy.impl.Unsettable1Impl#getUnsettableVAT <em>Unsettable VAT</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.legacy.impl.Unsettable1Impl#getUnsettableElement <em>Unsettable Element</em>}</li>
 * </ul>
 *
 * @generated
 */
public class Unsettable1Impl extends EObjectImpl implements Unsettable1
{
  /**
   * The default value of the '{@link #isUnsettableBoolean() <em>Unsettable Boolean</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #isUnsettableBoolean()
   * @generated
   * @ordered
   */
  protected static final boolean UNSETTABLE_BOOLEAN_EDEFAULT = false;

  /**
   * The cached value of the '{@link #isUnsettableBoolean() <em>Unsettable Boolean</em>}' attribute.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @see #isUnsettableBoolean()
   * @generated
   * @ordered
   */
  protected boolean unsettableBoolean = UNSETTABLE_BOOLEAN_EDEFAULT;

  /**
   * This is true if the Unsettable Boolean attribute has been set.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  protected boolean unsettableBooleanESet;

  /**
   * The default value of the '{@link #getUnsettableByte() <em>Unsettable Byte</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getUnsettableByte()
   * @generated
   * @ordered
   */
  protected static final byte UNSETTABLE_BYTE_EDEFAULT = 0x00;

  /**
   * The cached value of the '{@link #getUnsettableByte() <em>Unsettable Byte</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getUnsettableByte()
   * @generated
   * @ordered
   */
  protected byte unsettableByte = UNSETTABLE_BYTE_EDEFAULT;

  /**
   * This is true if the Unsettable Byte attribute has been set.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  protected boolean unsettableByteESet;

  /**
   * The default value of the '{@link #getUnsettableChar() <em>Unsettable Char</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getUnsettableChar()
   * @generated
   * @ordered
   */
  protected static final char UNSETTABLE_CHAR_EDEFAULT = '\u0000';

  /**
   * The cached value of the '{@link #getUnsettableChar() <em>Unsettable Char</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getUnsettableChar()
   * @generated
   * @ordered
   */
  protected char unsettableChar = UNSETTABLE_CHAR_EDEFAULT;

  /**
   * This is true if the Unsettable Char attribute has been set.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  protected boolean unsettableCharESet;

  /**
   * The default value of the '{@link #getUnsettableDate() <em>Unsettable Date</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getUnsettableDate()
   * @generated
   * @ordered
   */
  protected static final Date UNSETTABLE_DATE_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getUnsettableDate() <em>Unsettable Date</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getUnsettableDate()
   * @generated
   * @ordered
   */
  protected Date unsettableDate = UNSETTABLE_DATE_EDEFAULT;

  /**
   * This is true if the Unsettable Date attribute has been set.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  protected boolean unsettableDateESet;

  /**
   * The default value of the '{@link #getUnsettableDouble() <em>Unsettable Double</em>}' attribute.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @see #getUnsettableDouble()
   * @generated
   * @ordered
   */
  protected static final double UNSETTABLE_DOUBLE_EDEFAULT = 0.0;

  /**
   * The cached value of the '{@link #getUnsettableDouble() <em>Unsettable Double</em>}' attribute.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @see #getUnsettableDouble()
   * @generated
   * @ordered
   */
  protected double unsettableDouble = UNSETTABLE_DOUBLE_EDEFAULT;

  /**
   * This is true if the Unsettable Double attribute has been set.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  protected boolean unsettableDoubleESet;

  /**
   * The default value of the '{@link #getUnsettableFloat() <em>Unsettable Float</em>}' attribute.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @see #getUnsettableFloat()
   * @generated
   * @ordered
   */
  protected static final float UNSETTABLE_FLOAT_EDEFAULT = 0.0F;

  /**
   * The cached value of the '{@link #getUnsettableFloat() <em>Unsettable Float</em>}' attribute.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @see #getUnsettableFloat()
   * @generated
   * @ordered
   */
  protected float unsettableFloat = UNSETTABLE_FLOAT_EDEFAULT;

  /**
   * This is true if the Unsettable Float attribute has been set.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  protected boolean unsettableFloatESet;

  /**
   * The default value of the '{@link #getUnsettableInt() <em>Unsettable Int</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getUnsettableInt()
   * @generated
   * @ordered
   */
  protected static final int UNSETTABLE_INT_EDEFAULT = 0;

  /**
   * The cached value of the '{@link #getUnsettableInt() <em>Unsettable Int</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getUnsettableInt()
   * @generated
   * @ordered
   */
  protected int unsettableInt = UNSETTABLE_INT_EDEFAULT;

  /**
   * This is true if the Unsettable Int attribute has been set.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  protected boolean unsettableIntESet;

  /**
   * The default value of the '{@link #getUnsettableLong() <em>Unsettable Long</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getUnsettableLong()
   * @generated
   * @ordered
   */
  protected static final long UNSETTABLE_LONG_EDEFAULT = 0L;

  /**
   * The cached value of the '{@link #getUnsettableLong() <em>Unsettable Long</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getUnsettableLong()
   * @generated
   * @ordered
   */
  protected long unsettableLong = UNSETTABLE_LONG_EDEFAULT;

  /**
   * This is true if the Unsettable Long attribute has been set.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  protected boolean unsettableLongESet;

  /**
   * The default value of the '{@link #getUnsettableShort() <em>Unsettable Short</em>}' attribute.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @see #getUnsettableShort()
   * @generated
   * @ordered
   */
  protected static final short UNSETTABLE_SHORT_EDEFAULT = 0;

  /**
   * The cached value of the '{@link #getUnsettableShort() <em>Unsettable Short</em>}' attribute.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @see #getUnsettableShort()
   * @generated
   * @ordered
   */
  protected short unsettableShort = UNSETTABLE_SHORT_EDEFAULT;

  /**
   * This is true if the Unsettable Short attribute has been set.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  protected boolean unsettableShortESet;

  /**
   * The default value of the '{@link #getUnsettableString() <em>Unsettable String</em>}' attribute.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @see #getUnsettableString()
   * @generated
   * @ordered
   */
  protected static final String UNSETTABLE_STRING_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getUnsettableString() <em>Unsettable String</em>}' attribute.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @see #getUnsettableString()
   * @generated
   * @ordered
   */
  protected String unsettableString = UNSETTABLE_STRING_EDEFAULT;

  /**
   * This is true if the Unsettable String attribute has been set.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  protected boolean unsettableStringESet;

  /**
   * The default value of the '{@link #getUnsettableVAT() <em>Unsettable VAT</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getUnsettableVAT()
   * @generated
   * @ordered
   */
  protected static final VAT UNSETTABLE_VAT_EDEFAULT = VAT.VAT0;

  /**
   * The cached value of the '{@link #getUnsettableVAT() <em>Unsettable VAT</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getUnsettableVAT()
   * @generated
   * @ordered
   */
  protected VAT unsettableVAT = UNSETTABLE_VAT_EDEFAULT;

  /**
   * This is true if the Unsettable VAT attribute has been set.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  protected boolean unsettableVATESet;

  /**
   * The cached value of the '{@link #getUnsettableElement() <em>Unsettable Element</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getUnsettableElement()
   * @generated
   * @ordered
   */
  protected EObject unsettableElement;

  /**
   * This is true if the Unsettable Element reference has been set.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  protected boolean unsettableElementESet;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected Unsettable1Impl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return Model2Package.eINSTANCE.getUnsettable1();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isUnsettableBoolean()
  {
    return unsettableBoolean;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setUnsettableBoolean(boolean newUnsettableBoolean)
  {
    boolean oldUnsettableBoolean = unsettableBoolean;
    unsettableBoolean = newUnsettableBoolean;
    boolean oldUnsettableBooleanESet = unsettableBooleanESet;
    unsettableBooleanESet = true;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model2Package.UNSETTABLE1__UNSETTABLE_BOOLEAN, oldUnsettableBoolean, unsettableBoolean,
          !oldUnsettableBooleanESet));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetUnsettableBoolean()
  {
    boolean oldUnsettableBoolean = unsettableBoolean;
    boolean oldUnsettableBooleanESet = unsettableBooleanESet;
    unsettableBoolean = UNSETTABLE_BOOLEAN_EDEFAULT;
    unsettableBooleanESet = false;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.UNSET, Model2Package.UNSETTABLE1__UNSETTABLE_BOOLEAN, oldUnsettableBoolean, UNSETTABLE_BOOLEAN_EDEFAULT,
          oldUnsettableBooleanESet));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetUnsettableBoolean()
  {
    return unsettableBooleanESet;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public byte getUnsettableByte()
  {
    return unsettableByte;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setUnsettableByte(byte newUnsettableByte)
  {
    byte oldUnsettableByte = unsettableByte;
    unsettableByte = newUnsettableByte;
    boolean oldUnsettableByteESet = unsettableByteESet;
    unsettableByteESet = true;
    if (eNotificationRequired())
    {
      eNotify(
          new ENotificationImpl(this, Notification.SET, Model2Package.UNSETTABLE1__UNSETTABLE_BYTE, oldUnsettableByte, unsettableByte, !oldUnsettableByteESet));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetUnsettableByte()
  {
    byte oldUnsettableByte = unsettableByte;
    boolean oldUnsettableByteESet = unsettableByteESet;
    unsettableByte = UNSETTABLE_BYTE_EDEFAULT;
    unsettableByteESet = false;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.UNSET, Model2Package.UNSETTABLE1__UNSETTABLE_BYTE, oldUnsettableByte, UNSETTABLE_BYTE_EDEFAULT,
          oldUnsettableByteESet));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetUnsettableByte()
  {
    return unsettableByteESet;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public char getUnsettableChar()
  {
    return unsettableChar;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setUnsettableChar(char newUnsettableChar)
  {
    char oldUnsettableChar = unsettableChar;
    unsettableChar = newUnsettableChar;
    boolean oldUnsettableCharESet = unsettableCharESet;
    unsettableCharESet = true;
    if (eNotificationRequired())
    {
      eNotify(
          new ENotificationImpl(this, Notification.SET, Model2Package.UNSETTABLE1__UNSETTABLE_CHAR, oldUnsettableChar, unsettableChar, !oldUnsettableCharESet));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetUnsettableChar()
  {
    char oldUnsettableChar = unsettableChar;
    boolean oldUnsettableCharESet = unsettableCharESet;
    unsettableChar = UNSETTABLE_CHAR_EDEFAULT;
    unsettableCharESet = false;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.UNSET, Model2Package.UNSETTABLE1__UNSETTABLE_CHAR, oldUnsettableChar, UNSETTABLE_CHAR_EDEFAULT,
          oldUnsettableCharESet));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetUnsettableChar()
  {
    return unsettableCharESet;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Date getUnsettableDate()
  {
    return unsettableDate;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setUnsettableDate(Date newUnsettableDate)
  {
    Date oldUnsettableDate = unsettableDate;
    unsettableDate = newUnsettableDate;
    boolean oldUnsettableDateESet = unsettableDateESet;
    unsettableDateESet = true;
    if (eNotificationRequired())
    {
      eNotify(
          new ENotificationImpl(this, Notification.SET, Model2Package.UNSETTABLE1__UNSETTABLE_DATE, oldUnsettableDate, unsettableDate, !oldUnsettableDateESet));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetUnsettableDate()
  {
    Date oldUnsettableDate = unsettableDate;
    boolean oldUnsettableDateESet = unsettableDateESet;
    unsettableDate = UNSETTABLE_DATE_EDEFAULT;
    unsettableDateESet = false;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.UNSET, Model2Package.UNSETTABLE1__UNSETTABLE_DATE, oldUnsettableDate, UNSETTABLE_DATE_EDEFAULT,
          oldUnsettableDateESet));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetUnsettableDate()
  {
    return unsettableDateESet;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public double getUnsettableDouble()
  {
    return unsettableDouble;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setUnsettableDouble(double newUnsettableDouble)
  {
    double oldUnsettableDouble = unsettableDouble;
    unsettableDouble = newUnsettableDouble;
    boolean oldUnsettableDoubleESet = unsettableDoubleESet;
    unsettableDoubleESet = true;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model2Package.UNSETTABLE1__UNSETTABLE_DOUBLE, oldUnsettableDouble, unsettableDouble,
          !oldUnsettableDoubleESet));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetUnsettableDouble()
  {
    double oldUnsettableDouble = unsettableDouble;
    boolean oldUnsettableDoubleESet = unsettableDoubleESet;
    unsettableDouble = UNSETTABLE_DOUBLE_EDEFAULT;
    unsettableDoubleESet = false;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.UNSET, Model2Package.UNSETTABLE1__UNSETTABLE_DOUBLE, oldUnsettableDouble, UNSETTABLE_DOUBLE_EDEFAULT,
          oldUnsettableDoubleESet));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetUnsettableDouble()
  {
    return unsettableDoubleESet;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public float getUnsettableFloat()
  {
    return unsettableFloat;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setUnsettableFloat(float newUnsettableFloat)
  {
    float oldUnsettableFloat = unsettableFloat;
    unsettableFloat = newUnsettableFloat;
    boolean oldUnsettableFloatESet = unsettableFloatESet;
    unsettableFloatESet = true;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model2Package.UNSETTABLE1__UNSETTABLE_FLOAT, oldUnsettableFloat, unsettableFloat,
          !oldUnsettableFloatESet));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetUnsettableFloat()
  {
    float oldUnsettableFloat = unsettableFloat;
    boolean oldUnsettableFloatESet = unsettableFloatESet;
    unsettableFloat = UNSETTABLE_FLOAT_EDEFAULT;
    unsettableFloatESet = false;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.UNSET, Model2Package.UNSETTABLE1__UNSETTABLE_FLOAT, oldUnsettableFloat, UNSETTABLE_FLOAT_EDEFAULT,
          oldUnsettableFloatESet));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetUnsettableFloat()
  {
    return unsettableFloatESet;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int getUnsettableInt()
  {
    return unsettableInt;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setUnsettableInt(int newUnsettableInt)
  {
    int oldUnsettableInt = unsettableInt;
    unsettableInt = newUnsettableInt;
    boolean oldUnsettableIntESet = unsettableIntESet;
    unsettableIntESet = true;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model2Package.UNSETTABLE1__UNSETTABLE_INT, oldUnsettableInt, unsettableInt, !oldUnsettableIntESet));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetUnsettableInt()
  {
    int oldUnsettableInt = unsettableInt;
    boolean oldUnsettableIntESet = unsettableIntESet;
    unsettableInt = UNSETTABLE_INT_EDEFAULT;
    unsettableIntESet = false;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.UNSET, Model2Package.UNSETTABLE1__UNSETTABLE_INT, oldUnsettableInt, UNSETTABLE_INT_EDEFAULT,
          oldUnsettableIntESet));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetUnsettableInt()
  {
    return unsettableIntESet;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public long getUnsettableLong()
  {
    return unsettableLong;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setUnsettableLong(long newUnsettableLong)
  {
    long oldUnsettableLong = unsettableLong;
    unsettableLong = newUnsettableLong;
    boolean oldUnsettableLongESet = unsettableLongESet;
    unsettableLongESet = true;
    if (eNotificationRequired())
    {
      eNotify(
          new ENotificationImpl(this, Notification.SET, Model2Package.UNSETTABLE1__UNSETTABLE_LONG, oldUnsettableLong, unsettableLong, !oldUnsettableLongESet));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetUnsettableLong()
  {
    long oldUnsettableLong = unsettableLong;
    boolean oldUnsettableLongESet = unsettableLongESet;
    unsettableLong = UNSETTABLE_LONG_EDEFAULT;
    unsettableLongESet = false;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.UNSET, Model2Package.UNSETTABLE1__UNSETTABLE_LONG, oldUnsettableLong, UNSETTABLE_LONG_EDEFAULT,
          oldUnsettableLongESet));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetUnsettableLong()
  {
    return unsettableLongESet;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public short getUnsettableShort()
  {
    return unsettableShort;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setUnsettableShort(short newUnsettableShort)
  {
    short oldUnsettableShort = unsettableShort;
    unsettableShort = newUnsettableShort;
    boolean oldUnsettableShortESet = unsettableShortESet;
    unsettableShortESet = true;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model2Package.UNSETTABLE1__UNSETTABLE_SHORT, oldUnsettableShort, unsettableShort,
          !oldUnsettableShortESet));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetUnsettableShort()
  {
    short oldUnsettableShort = unsettableShort;
    boolean oldUnsettableShortESet = unsettableShortESet;
    unsettableShort = UNSETTABLE_SHORT_EDEFAULT;
    unsettableShortESet = false;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.UNSET, Model2Package.UNSETTABLE1__UNSETTABLE_SHORT, oldUnsettableShort, UNSETTABLE_SHORT_EDEFAULT,
          oldUnsettableShortESet));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetUnsettableShort()
  {
    return unsettableShortESet;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getUnsettableString()
  {
    return unsettableString;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setUnsettableString(String newUnsettableString)
  {
    String oldUnsettableString = unsettableString;
    unsettableString = newUnsettableString;
    boolean oldUnsettableStringESet = unsettableStringESet;
    unsettableStringESet = true;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model2Package.UNSETTABLE1__UNSETTABLE_STRING, oldUnsettableString, unsettableString,
          !oldUnsettableStringESet));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetUnsettableString()
  {
    String oldUnsettableString = unsettableString;
    boolean oldUnsettableStringESet = unsettableStringESet;
    unsettableString = UNSETTABLE_STRING_EDEFAULT;
    unsettableStringESet = false;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.UNSET, Model2Package.UNSETTABLE1__UNSETTABLE_STRING, oldUnsettableString, UNSETTABLE_STRING_EDEFAULT,
          oldUnsettableStringESet));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetUnsettableString()
  {
    return unsettableStringESet;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public VAT getUnsettableVAT()
  {
    return unsettableVAT;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setUnsettableVAT(VAT newUnsettableVAT)
  {
    VAT oldUnsettableVAT = unsettableVAT;
    unsettableVAT = newUnsettableVAT == null ? UNSETTABLE_VAT_EDEFAULT : newUnsettableVAT;
    boolean oldUnsettableVATESet = unsettableVATESet;
    unsettableVATESet = true;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model2Package.UNSETTABLE1__UNSETTABLE_VAT, oldUnsettableVAT, unsettableVAT, !oldUnsettableVATESet));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetUnsettableVAT()
  {
    VAT oldUnsettableVAT = unsettableVAT;
    boolean oldUnsettableVATESet = unsettableVATESet;
    unsettableVAT = UNSETTABLE_VAT_EDEFAULT;
    unsettableVATESet = false;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.UNSET, Model2Package.UNSETTABLE1__UNSETTABLE_VAT, oldUnsettableVAT, UNSETTABLE_VAT_EDEFAULT,
          oldUnsettableVATESet));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetUnsettableVAT()
  {
    return unsettableVATESet;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObject getUnsettableElement()
  {
    if (unsettableElement != null && unsettableElement.eIsProxy())
    {
      InternalEObject oldUnsettableElement = (InternalEObject)unsettableElement;
      unsettableElement = eResolveProxy(oldUnsettableElement);
      if (unsettableElement != oldUnsettableElement)
      {
        if (eNotificationRequired())
        {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, Model2Package.UNSETTABLE1__UNSETTABLE_ELEMENT, oldUnsettableElement, unsettableElement));
        }
      }
    }
    return unsettableElement;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EObject basicGetUnsettableElement()
  {
    return unsettableElement;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setUnsettableElement(EObject newUnsettableElement)
  {
    EObject oldUnsettableElement = unsettableElement;
    unsettableElement = newUnsettableElement;
    boolean oldUnsettableElementESet = unsettableElementESet;
    unsettableElementESet = true;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model2Package.UNSETTABLE1__UNSETTABLE_ELEMENT, oldUnsettableElement, unsettableElement,
          !oldUnsettableElementESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetUnsettableElement()
  {
    EObject oldUnsettableElement = unsettableElement;
    boolean oldUnsettableElementESet = unsettableElementESet;
    unsettableElement = null;
    unsettableElementESet = false;
    if (eNotificationRequired())
    {
      eNotify(
          new ENotificationImpl(this, Notification.UNSET, Model2Package.UNSETTABLE1__UNSETTABLE_ELEMENT, oldUnsettableElement, null, oldUnsettableElementESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetUnsettableElement()
  {
    return unsettableElementESet;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case Model2Package.UNSETTABLE1__UNSETTABLE_BOOLEAN:
      return isUnsettableBoolean();
    case Model2Package.UNSETTABLE1__UNSETTABLE_BYTE:
      return getUnsettableByte();
    case Model2Package.UNSETTABLE1__UNSETTABLE_CHAR:
      return getUnsettableChar();
    case Model2Package.UNSETTABLE1__UNSETTABLE_DATE:
      return getUnsettableDate();
    case Model2Package.UNSETTABLE1__UNSETTABLE_DOUBLE:
      return getUnsettableDouble();
    case Model2Package.UNSETTABLE1__UNSETTABLE_FLOAT:
      return getUnsettableFloat();
    case Model2Package.UNSETTABLE1__UNSETTABLE_INT:
      return getUnsettableInt();
    case Model2Package.UNSETTABLE1__UNSETTABLE_LONG:
      return getUnsettableLong();
    case Model2Package.UNSETTABLE1__UNSETTABLE_SHORT:
      return getUnsettableShort();
    case Model2Package.UNSETTABLE1__UNSETTABLE_STRING:
      return getUnsettableString();
    case Model2Package.UNSETTABLE1__UNSETTABLE_VAT:
      return getUnsettableVAT();
    case Model2Package.UNSETTABLE1__UNSETTABLE_ELEMENT:
      if (resolve)
      {
        return getUnsettableElement();
      }
      return basicGetUnsettableElement();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case Model2Package.UNSETTABLE1__UNSETTABLE_BOOLEAN:
      setUnsettableBoolean((Boolean)newValue);
      return;
    case Model2Package.UNSETTABLE1__UNSETTABLE_BYTE:
      setUnsettableByte((Byte)newValue);
      return;
    case Model2Package.UNSETTABLE1__UNSETTABLE_CHAR:
      setUnsettableChar((Character)newValue);
      return;
    case Model2Package.UNSETTABLE1__UNSETTABLE_DATE:
      setUnsettableDate((Date)newValue);
      return;
    case Model2Package.UNSETTABLE1__UNSETTABLE_DOUBLE:
      setUnsettableDouble((Double)newValue);
      return;
    case Model2Package.UNSETTABLE1__UNSETTABLE_FLOAT:
      setUnsettableFloat((Float)newValue);
      return;
    case Model2Package.UNSETTABLE1__UNSETTABLE_INT:
      setUnsettableInt((Integer)newValue);
      return;
    case Model2Package.UNSETTABLE1__UNSETTABLE_LONG:
      setUnsettableLong((Long)newValue);
      return;
    case Model2Package.UNSETTABLE1__UNSETTABLE_SHORT:
      setUnsettableShort((Short)newValue);
      return;
    case Model2Package.UNSETTABLE1__UNSETTABLE_STRING:
      setUnsettableString((String)newValue);
      return;
    case Model2Package.UNSETTABLE1__UNSETTABLE_VAT:
      setUnsettableVAT((VAT)newValue);
      return;
    case Model2Package.UNSETTABLE1__UNSETTABLE_ELEMENT:
      setUnsettableElement((EObject)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case Model2Package.UNSETTABLE1__UNSETTABLE_BOOLEAN:
      unsetUnsettableBoolean();
      return;
    case Model2Package.UNSETTABLE1__UNSETTABLE_BYTE:
      unsetUnsettableByte();
      return;
    case Model2Package.UNSETTABLE1__UNSETTABLE_CHAR:
      unsetUnsettableChar();
      return;
    case Model2Package.UNSETTABLE1__UNSETTABLE_DATE:
      unsetUnsettableDate();
      return;
    case Model2Package.UNSETTABLE1__UNSETTABLE_DOUBLE:
      unsetUnsettableDouble();
      return;
    case Model2Package.UNSETTABLE1__UNSETTABLE_FLOAT:
      unsetUnsettableFloat();
      return;
    case Model2Package.UNSETTABLE1__UNSETTABLE_INT:
      unsetUnsettableInt();
      return;
    case Model2Package.UNSETTABLE1__UNSETTABLE_LONG:
      unsetUnsettableLong();
      return;
    case Model2Package.UNSETTABLE1__UNSETTABLE_SHORT:
      unsetUnsettableShort();
      return;
    case Model2Package.UNSETTABLE1__UNSETTABLE_STRING:
      unsetUnsettableString();
      return;
    case Model2Package.UNSETTABLE1__UNSETTABLE_VAT:
      unsetUnsettableVAT();
      return;
    case Model2Package.UNSETTABLE1__UNSETTABLE_ELEMENT:
      unsetUnsettableElement();
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case Model2Package.UNSETTABLE1__UNSETTABLE_BOOLEAN:
      return isSetUnsettableBoolean();
    case Model2Package.UNSETTABLE1__UNSETTABLE_BYTE:
      return isSetUnsettableByte();
    case Model2Package.UNSETTABLE1__UNSETTABLE_CHAR:
      return isSetUnsettableChar();
    case Model2Package.UNSETTABLE1__UNSETTABLE_DATE:
      return isSetUnsettableDate();
    case Model2Package.UNSETTABLE1__UNSETTABLE_DOUBLE:
      return isSetUnsettableDouble();
    case Model2Package.UNSETTABLE1__UNSETTABLE_FLOAT:
      return isSetUnsettableFloat();
    case Model2Package.UNSETTABLE1__UNSETTABLE_INT:
      return isSetUnsettableInt();
    case Model2Package.UNSETTABLE1__UNSETTABLE_LONG:
      return isSetUnsettableLong();
    case Model2Package.UNSETTABLE1__UNSETTABLE_SHORT:
      return isSetUnsettableShort();
    case Model2Package.UNSETTABLE1__UNSETTABLE_STRING:
      return isSetUnsettableString();
    case Model2Package.UNSETTABLE1__UNSETTABLE_VAT:
      return isSetUnsettableVAT();
    case Model2Package.UNSETTABLE1__UNSETTABLE_ELEMENT:
      return isSetUnsettableElement();
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
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
    result.append(" (unsettableBoolean: ");
    if (unsettableBooleanESet)
    {
      result.append(unsettableBoolean);
    }
    else
    {
      result.append("<unset>");
    }
    result.append(", unsettableByte: ");
    if (unsettableByteESet)
    {
      result.append(unsettableByte);
    }
    else
    {
      result.append("<unset>");
    }
    result.append(", unsettableChar: ");
    if (unsettableCharESet)
    {
      result.append(unsettableChar);
    }
    else
    {
      result.append("<unset>");
    }
    result.append(", unsettableDate: ");
    if (unsettableDateESet)
    {
      result.append(unsettableDate);
    }
    else
    {
      result.append("<unset>");
    }
    result.append(", unsettableDouble: ");
    if (unsettableDoubleESet)
    {
      result.append(unsettableDouble);
    }
    else
    {
      result.append("<unset>");
    }
    result.append(", unsettableFloat: ");
    if (unsettableFloatESet)
    {
      result.append(unsettableFloat);
    }
    else
    {
      result.append("<unset>");
    }
    result.append(", unsettableInt: ");
    if (unsettableIntESet)
    {
      result.append(unsettableInt);
    }
    else
    {
      result.append("<unset>");
    }
    result.append(", unsettableLong: ");
    if (unsettableLongESet)
    {
      result.append(unsettableLong);
    }
    else
    {
      result.append("<unset>");
    }
    result.append(", unsettableShort: ");
    if (unsettableShortESet)
    {
      result.append(unsettableShort);
    }
    else
    {
      result.append("<unset>");
    }
    result.append(", unsettableString: ");
    if (unsettableStringESet)
    {
      result.append(unsettableString);
    }
    else
    {
      result.append("<unset>");
    }
    result.append(", unsettableVAT: ");
    if (unsettableVATESet)
    {
      result.append(unsettableVAT);
    }
    else
    {
      result.append("<unset>");
    }
    result.append(')');
    return result.toString();
  }

} // Unsettable1Impl
