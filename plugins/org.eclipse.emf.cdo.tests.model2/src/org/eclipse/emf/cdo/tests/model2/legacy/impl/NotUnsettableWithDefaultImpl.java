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
 *
 * $Id: NotUnsettableWithDefaultImpl.java,v 1.3 2011-01-01 11:01:57 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.model2.legacy.impl;

import org.eclipse.emf.cdo.tests.model1.VAT;
import org.eclipse.emf.cdo.tests.model2.NotUnsettableWithDefault;
import org.eclipse.emf.cdo.tests.model2.legacy.Model2Package;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import java.util.Date;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Not Unsettable With Default</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.legacy.impl.NotUnsettableWithDefaultImpl#isNotUnsettableBoolean <em>Not Unsettable Boolean</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.legacy.impl.NotUnsettableWithDefaultImpl#getNotUnsettableByte <em>Not Unsettable Byte</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.legacy.impl.NotUnsettableWithDefaultImpl#getNotUnsettableChar <em>Not Unsettable Char</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.legacy.impl.NotUnsettableWithDefaultImpl#getNotUnsettableDate <em>Not Unsettable Date</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.legacy.impl.NotUnsettableWithDefaultImpl#getNotUnsettableDouble <em>Not Unsettable Double</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.legacy.impl.NotUnsettableWithDefaultImpl#getNotUnsettableFloat <em>Not Unsettable Float</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.legacy.impl.NotUnsettableWithDefaultImpl#getNotUnsettableInt <em>Not Unsettable Int</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.legacy.impl.NotUnsettableWithDefaultImpl#getNotUnsettableLong <em>Not Unsettable Long</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.legacy.impl.NotUnsettableWithDefaultImpl#getNotUnsettableShort <em>Not Unsettable Short</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.legacy.impl.NotUnsettableWithDefaultImpl#getNotUnsettableString <em>Not Unsettable String</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.legacy.impl.NotUnsettableWithDefaultImpl#getNotUnsettableVAT <em>Not Unsettable VAT</em>}</li>
 * </ul>
 *
 * @generated
 */
public class NotUnsettableWithDefaultImpl extends EObjectImpl implements NotUnsettableWithDefault
{
  /**
   * The default value of the '{@link #isNotUnsettableBoolean() <em>Not Unsettable Boolean</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #isNotUnsettableBoolean()
   * @generated
   * @ordered
   */
  protected static final boolean NOT_UNSETTABLE_BOOLEAN_EDEFAULT = true;

  /**
   * The cached value of the '{@link #isNotUnsettableBoolean() <em>Not Unsettable Boolean</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #isNotUnsettableBoolean()
   * @generated
   * @ordered
   */
  protected boolean notUnsettableBoolean = NOT_UNSETTABLE_BOOLEAN_EDEFAULT;

  /**
   * The default value of the '{@link #getNotUnsettableByte() <em>Not Unsettable Byte</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getNotUnsettableByte()
   * @generated
   * @ordered
   */
  protected static final byte NOT_UNSETTABLE_BYTE_EDEFAULT = 0x03;

  /**
   * The cached value of the '{@link #getNotUnsettableByte() <em>Not Unsettable Byte</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getNotUnsettableByte()
   * @generated
   * @ordered
   */
  protected byte notUnsettableByte = NOT_UNSETTABLE_BYTE_EDEFAULT;

  /**
   * The default value of the '{@link #getNotUnsettableChar() <em>Not Unsettable Char</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getNotUnsettableChar()
   * @generated
   * @ordered
   */
  protected static final char NOT_UNSETTABLE_CHAR_EDEFAULT = '\'';

  /**
   * The cached value of the '{@link #getNotUnsettableChar() <em>Not Unsettable Char</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getNotUnsettableChar()
   * @generated
   * @ordered
   */
  protected char notUnsettableChar = NOT_UNSETTABLE_CHAR_EDEFAULT;

  /**
   * The default value of the '{@link #getNotUnsettableDate() <em>Not Unsettable Date</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getNotUnsettableDate()
   * @generated
   * @ordered
   */
  protected static final Date NOT_UNSETTABLE_DATE_EDEFAULT = (Date)EcoreFactory.eINSTANCE.createFromString(EcorePackage.eINSTANCE.getEDate(),
      "1979-03-15T07:12:59");

  /**
   * The cached value of the '{@link #getNotUnsettableDate() <em>Not Unsettable Date</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getNotUnsettableDate()
   * @generated
   * @ordered
   */
  protected Date notUnsettableDate = NOT_UNSETTABLE_DATE_EDEFAULT;

  /**
   * The default value of the '{@link #getNotUnsettableDouble() <em>Not Unsettable Double</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getNotUnsettableDouble()
   * @generated
   * @ordered
   */
  protected static final double NOT_UNSETTABLE_DOUBLE_EDEFAULT = 3.3;

  /**
   * The cached value of the '{@link #getNotUnsettableDouble() <em>Not Unsettable Double</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getNotUnsettableDouble()
   * @generated
   * @ordered
   */
  protected double notUnsettableDouble = NOT_UNSETTABLE_DOUBLE_EDEFAULT;

  /**
   * The default value of the '{@link #getNotUnsettableFloat() <em>Not Unsettable Float</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getNotUnsettableFloat()
   * @generated
   * @ordered
   */
  protected static final float NOT_UNSETTABLE_FLOAT_EDEFAULT = 4.4F;

  /**
   * The cached value of the '{@link #getNotUnsettableFloat() <em>Not Unsettable Float</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getNotUnsettableFloat()
   * @generated
   * @ordered
   */
  protected float notUnsettableFloat = NOT_UNSETTABLE_FLOAT_EDEFAULT;

  /**
   * The default value of the '{@link #getNotUnsettableInt() <em>Not Unsettable Int</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getNotUnsettableInt()
   * @generated
   * @ordered
   */
  protected static final int NOT_UNSETTABLE_INT_EDEFAULT = 5;

  /**
   * The cached value of the '{@link #getNotUnsettableInt() <em>Not Unsettable Int</em>}' attribute.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @see #getNotUnsettableInt()
   * @generated
   * @ordered
   */
  protected int notUnsettableInt = NOT_UNSETTABLE_INT_EDEFAULT;

  /**
   * The default value of the '{@link #getNotUnsettableLong() <em>Not Unsettable Long</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getNotUnsettableLong()
   * @generated
   * @ordered
   */
  protected static final long NOT_UNSETTABLE_LONG_EDEFAULT = 6L;

  /**
   * The cached value of the '{@link #getNotUnsettableLong() <em>Not Unsettable Long</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getNotUnsettableLong()
   * @generated
   * @ordered
   */
  protected long notUnsettableLong = NOT_UNSETTABLE_LONG_EDEFAULT;

  /**
   * The default value of the '{@link #getNotUnsettableShort() <em>Not Unsettable Short</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getNotUnsettableShort()
   * @generated
   * @ordered
   */
  protected static final short NOT_UNSETTABLE_SHORT_EDEFAULT = 7;

  /**
   * The cached value of the '{@link #getNotUnsettableShort() <em>Not Unsettable Short</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getNotUnsettableShort()
   * @generated
   * @ordered
   */
  protected short notUnsettableShort = NOT_UNSETTABLE_SHORT_EDEFAULT;

  /**
   * The default value of the '{@link #getNotUnsettableString() <em>Not Unsettable String</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getNotUnsettableString()
   * @generated
   * @ordered
   */
  protected static final String NOT_UNSETTABLE_STRING_EDEFAULT = "\"eike\"";

  /**
   * The cached value of the '{@link #getNotUnsettableString() <em>Not Unsettable String</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getNotUnsettableString()
   * @generated
   * @ordered
   */
  protected String notUnsettableString = NOT_UNSETTABLE_STRING_EDEFAULT;

  /**
   * The default value of the '{@link #getNotUnsettableVAT() <em>Not Unsettable VAT</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getNotUnsettableVAT()
   * @generated
   * @ordered
   */
  protected static final VAT NOT_UNSETTABLE_VAT_EDEFAULT = VAT.VAT15;

  /**
   * The cached value of the '{@link #getNotUnsettableVAT() <em>Not Unsettable VAT</em>}' attribute.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @see #getNotUnsettableVAT()
   * @generated
   * @ordered
   */
  protected VAT notUnsettableVAT = NOT_UNSETTABLE_VAT_EDEFAULT;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected NotUnsettableWithDefaultImpl()
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
    return Model2Package.eINSTANCE.getNotUnsettableWithDefault();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isNotUnsettableBoolean()
  {
    return notUnsettableBoolean;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setNotUnsettableBoolean(boolean newNotUnsettableBoolean)
  {
    boolean oldNotUnsettableBoolean = notUnsettableBoolean;
    notUnsettableBoolean = newNotUnsettableBoolean;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_BOOLEAN, oldNotUnsettableBoolean,
          notUnsettableBoolean));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public byte getNotUnsettableByte()
  {
    return notUnsettableByte;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setNotUnsettableByte(byte newNotUnsettableByte)
  {
    byte oldNotUnsettableByte = notUnsettableByte;
    notUnsettableByte = newNotUnsettableByte;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_BYTE, oldNotUnsettableByte,
          notUnsettableByte));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public char getNotUnsettableChar()
  {
    return notUnsettableChar;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setNotUnsettableChar(char newNotUnsettableChar)
  {
    char oldNotUnsettableChar = notUnsettableChar;
    notUnsettableChar = newNotUnsettableChar;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_CHAR, oldNotUnsettableChar,
          notUnsettableChar));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Date getNotUnsettableDate()
  {
    return notUnsettableDate;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setNotUnsettableDate(Date newNotUnsettableDate)
  {
    Date oldNotUnsettableDate = notUnsettableDate;
    notUnsettableDate = newNotUnsettableDate;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_DATE, oldNotUnsettableDate,
          notUnsettableDate));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public double getNotUnsettableDouble()
  {
    return notUnsettableDouble;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setNotUnsettableDouble(double newNotUnsettableDouble)
  {
    double oldNotUnsettableDouble = notUnsettableDouble;
    notUnsettableDouble = newNotUnsettableDouble;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_DOUBLE, oldNotUnsettableDouble,
          notUnsettableDouble));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public float getNotUnsettableFloat()
  {
    return notUnsettableFloat;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setNotUnsettableFloat(float newNotUnsettableFloat)
  {
    float oldNotUnsettableFloat = notUnsettableFloat;
    notUnsettableFloat = newNotUnsettableFloat;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_FLOAT, oldNotUnsettableFloat,
          notUnsettableFloat));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int getNotUnsettableInt()
  {
    return notUnsettableInt;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setNotUnsettableInt(int newNotUnsettableInt)
  {
    int oldNotUnsettableInt = notUnsettableInt;
    notUnsettableInt = newNotUnsettableInt;
    if (eNotificationRequired())
    {
      eNotify(
          new ENotificationImpl(this, Notification.SET, Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_INT, oldNotUnsettableInt, notUnsettableInt));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public long getNotUnsettableLong()
  {
    return notUnsettableLong;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setNotUnsettableLong(long newNotUnsettableLong)
  {
    long oldNotUnsettableLong = notUnsettableLong;
    notUnsettableLong = newNotUnsettableLong;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_LONG, oldNotUnsettableLong,
          notUnsettableLong));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public short getNotUnsettableShort()
  {
    return notUnsettableShort;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setNotUnsettableShort(short newNotUnsettableShort)
  {
    short oldNotUnsettableShort = notUnsettableShort;
    notUnsettableShort = newNotUnsettableShort;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_SHORT, oldNotUnsettableShort,
          notUnsettableShort));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getNotUnsettableString()
  {
    return notUnsettableString;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setNotUnsettableString(String newNotUnsettableString)
  {
    String oldNotUnsettableString = notUnsettableString;
    notUnsettableString = newNotUnsettableString;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_STRING, oldNotUnsettableString,
          notUnsettableString));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public VAT getNotUnsettableVAT()
  {
    return notUnsettableVAT;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setNotUnsettableVAT(VAT newNotUnsettableVAT)
  {
    VAT oldNotUnsettableVAT = notUnsettableVAT;
    notUnsettableVAT = newNotUnsettableVAT == null ? NOT_UNSETTABLE_VAT_EDEFAULT : newNotUnsettableVAT;
    if (eNotificationRequired())
    {
      eNotify(
          new ENotificationImpl(this, Notification.SET, Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_VAT, oldNotUnsettableVAT, notUnsettableVAT));
    }
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
    case Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_BOOLEAN:
      return isNotUnsettableBoolean();
    case Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_BYTE:
      return getNotUnsettableByte();
    case Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_CHAR:
      return getNotUnsettableChar();
    case Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_DATE:
      return getNotUnsettableDate();
    case Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_DOUBLE:
      return getNotUnsettableDouble();
    case Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_FLOAT:
      return getNotUnsettableFloat();
    case Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_INT:
      return getNotUnsettableInt();
    case Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_LONG:
      return getNotUnsettableLong();
    case Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_SHORT:
      return getNotUnsettableShort();
    case Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_STRING:
      return getNotUnsettableString();
    case Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_VAT:
      return getNotUnsettableVAT();
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
    case Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_BOOLEAN:
      setNotUnsettableBoolean((Boolean)newValue);
      return;
    case Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_BYTE:
      setNotUnsettableByte((Byte)newValue);
      return;
    case Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_CHAR:
      setNotUnsettableChar((Character)newValue);
      return;
    case Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_DATE:
      setNotUnsettableDate((Date)newValue);
      return;
    case Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_DOUBLE:
      setNotUnsettableDouble((Double)newValue);
      return;
    case Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_FLOAT:
      setNotUnsettableFloat((Float)newValue);
      return;
    case Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_INT:
      setNotUnsettableInt((Integer)newValue);
      return;
    case Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_LONG:
      setNotUnsettableLong((Long)newValue);
      return;
    case Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_SHORT:
      setNotUnsettableShort((Short)newValue);
      return;
    case Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_STRING:
      setNotUnsettableString((String)newValue);
      return;
    case Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_VAT:
      setNotUnsettableVAT((VAT)newValue);
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
    case Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_BOOLEAN:
      setNotUnsettableBoolean(NOT_UNSETTABLE_BOOLEAN_EDEFAULT);
      return;
    case Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_BYTE:
      setNotUnsettableByte(NOT_UNSETTABLE_BYTE_EDEFAULT);
      return;
    case Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_CHAR:
      setNotUnsettableChar(NOT_UNSETTABLE_CHAR_EDEFAULT);
      return;
    case Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_DATE:
      setNotUnsettableDate(NOT_UNSETTABLE_DATE_EDEFAULT);
      return;
    case Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_DOUBLE:
      setNotUnsettableDouble(NOT_UNSETTABLE_DOUBLE_EDEFAULT);
      return;
    case Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_FLOAT:
      setNotUnsettableFloat(NOT_UNSETTABLE_FLOAT_EDEFAULT);
      return;
    case Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_INT:
      setNotUnsettableInt(NOT_UNSETTABLE_INT_EDEFAULT);
      return;
    case Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_LONG:
      setNotUnsettableLong(NOT_UNSETTABLE_LONG_EDEFAULT);
      return;
    case Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_SHORT:
      setNotUnsettableShort(NOT_UNSETTABLE_SHORT_EDEFAULT);
      return;
    case Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_STRING:
      setNotUnsettableString(NOT_UNSETTABLE_STRING_EDEFAULT);
      return;
    case Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_VAT:
      setNotUnsettableVAT(NOT_UNSETTABLE_VAT_EDEFAULT);
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
    case Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_BOOLEAN:
      return notUnsettableBoolean != NOT_UNSETTABLE_BOOLEAN_EDEFAULT;
    case Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_BYTE:
      return notUnsettableByte != NOT_UNSETTABLE_BYTE_EDEFAULT;
    case Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_CHAR:
      return notUnsettableChar != NOT_UNSETTABLE_CHAR_EDEFAULT;
    case Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_DATE:
      return NOT_UNSETTABLE_DATE_EDEFAULT == null ? notUnsettableDate != null : !NOT_UNSETTABLE_DATE_EDEFAULT.equals(notUnsettableDate);
    case Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_DOUBLE:
      return notUnsettableDouble != NOT_UNSETTABLE_DOUBLE_EDEFAULT;
    case Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_FLOAT:
      return notUnsettableFloat != NOT_UNSETTABLE_FLOAT_EDEFAULT;
    case Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_INT:
      return notUnsettableInt != NOT_UNSETTABLE_INT_EDEFAULT;
    case Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_LONG:
      return notUnsettableLong != NOT_UNSETTABLE_LONG_EDEFAULT;
    case Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_SHORT:
      return notUnsettableShort != NOT_UNSETTABLE_SHORT_EDEFAULT;
    case Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_STRING:
      return NOT_UNSETTABLE_STRING_EDEFAULT == null ? notUnsettableString != null : !NOT_UNSETTABLE_STRING_EDEFAULT.equals(notUnsettableString);
    case Model2Package.NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_VAT:
      return notUnsettableVAT != NOT_UNSETTABLE_VAT_EDEFAULT;
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
    result.append(" (notUnsettableBoolean: ");
    result.append(notUnsettableBoolean);
    result.append(", notUnsettableByte: ");
    result.append(notUnsettableByte);
    result.append(", notUnsettableChar: ");
    result.append(notUnsettableChar);
    result.append(", notUnsettableDate: ");
    result.append(notUnsettableDate);
    result.append(", notUnsettableDouble: ");
    result.append(notUnsettableDouble);
    result.append(", notUnsettableFloat: ");
    result.append(notUnsettableFloat);
    result.append(", notUnsettableInt: ");
    result.append(notUnsettableInt);
    result.append(", notUnsettableLong: ");
    result.append(notUnsettableLong);
    result.append(", notUnsettableShort: ");
    result.append(notUnsettableShort);
    result.append(", notUnsettableString: ");
    result.append(notUnsettableString);
    result.append(", notUnsettableVAT: ");
    result.append(notUnsettableVAT);
    result.append(')');
    return result.toString();
  }

} // NotUnsettableWithDefaultImpl
