/*
 * Copyright (c) 2012, 2013, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl;

import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz387752_Enum;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz387752_Main;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Bz387752 Main</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz387752_MainImpl#getStrUnsettable <em>Str Unsettable</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz387752_MainImpl#getStrSettable <em>Str Settable</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz387752_MainImpl#getEnumSettable <em>Enum Settable</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz387752_MainImpl#getEnumUnsettable <em>Enum Unsettable</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class Bz387752_MainImpl extends EObjectImpl implements Bz387752_Main
{
  /**
   * The default value of the '{@link #getStrUnsettable() <em>Str Unsettable</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getStrUnsettable()
   * @generated
   * @ordered
   */
  protected static final String STR_UNSETTABLE_EDEFAULT = "def_value";

  /**
   * The cached value of the '{@link #getStrUnsettable() <em>Str Unsettable</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getStrUnsettable()
   * @generated
   * @ordered
   */
  protected String strUnsettable = STR_UNSETTABLE_EDEFAULT;

  /**
   * This is true if the Str Unsettable attribute has been set.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  protected boolean strUnsettableESet;

  /**
   * The default value of the '{@link #getStrSettable() <em>Str Settable</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getStrSettable()
   * @generated
   * @ordered
   */
  protected static final String STR_SETTABLE_EDEFAULT = "value";

  /**
   * The cached value of the '{@link #getStrSettable() <em>Str Settable</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getStrSettable()
   * @generated
   * @ordered
   */
  protected String strSettable = STR_SETTABLE_EDEFAULT;

  /**
   * The default value of the '{@link #getEnumSettable() <em>Enum Settable</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEnumSettable()
   * @generated
   * @ordered
   */
  protected static final Bz387752_Enum ENUM_SETTABLE_EDEFAULT = Bz387752_Enum.VAL0;

  /**
   * The cached value of the '{@link #getEnumSettable() <em>Enum Settable</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEnumSettable()
   * @generated
   * @ordered
   */
  protected Bz387752_Enum enumSettable = ENUM_SETTABLE_EDEFAULT;

  /**
   * The default value of the '{@link #getEnumUnsettable() <em>Enum Unsettable</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEnumUnsettable()
   * @generated
   * @ordered
   */
  protected static final Bz387752_Enum ENUM_UNSETTABLE_EDEFAULT = Bz387752_Enum.VAL1;

  /**
   * The cached value of the '{@link #getEnumUnsettable() <em>Enum Unsettable</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEnumUnsettable()
   * @generated
   * @ordered
   */
  protected Bz387752_Enum enumUnsettable = ENUM_UNSETTABLE_EDEFAULT;

  /**
   * This is true if the Enum Unsettable attribute has been set.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  protected boolean enumUnsettableESet;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected Bz387752_MainImpl()
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
    return HibernateTestPackage.Literals.BZ387752_MAIN;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getStrUnsettable()
  {
    return strUnsettable;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setStrUnsettable(String newStrUnsettable)
  {
    String oldStrUnsettable = strUnsettable;
    strUnsettable = newStrUnsettable;
    boolean oldStrUnsettableESet = strUnsettableESet;
    strUnsettableESet = true;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, HibernateTestPackage.BZ387752_MAIN__STR_UNSETTABLE, oldStrUnsettable, strUnsettable,
          !oldStrUnsettableESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void unsetStrUnsettable()
  {
    String oldStrUnsettable = strUnsettable;
    boolean oldStrUnsettableESet = strUnsettableESet;
    strUnsettable = STR_UNSETTABLE_EDEFAULT;
    strUnsettableESet = false;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.UNSET, HibernateTestPackage.BZ387752_MAIN__STR_UNSETTABLE, oldStrUnsettable, STR_UNSETTABLE_EDEFAULT,
          oldStrUnsettableESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isSetStrUnsettable()
  {
    return strUnsettableESet;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getStrSettable()
  {
    return strSettable;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setStrSettable(String newStrSettable)
  {
    String oldStrSettable = strSettable;
    strSettable = newStrSettable;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, HibernateTestPackage.BZ387752_MAIN__STR_SETTABLE, oldStrSettable, strSettable));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Bz387752_Enum getEnumSettable()
  {
    return enumSettable;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setEnumSettable(Bz387752_Enum newEnumSettable)
  {
    Bz387752_Enum oldEnumSettable = enumSettable;
    enumSettable = newEnumSettable == null ? ENUM_SETTABLE_EDEFAULT : newEnumSettable;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, HibernateTestPackage.BZ387752_MAIN__ENUM_SETTABLE, oldEnumSettable, enumSettable));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Bz387752_Enum getEnumUnsettable()
  {
    return enumUnsettable;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setEnumUnsettable(Bz387752_Enum newEnumUnsettable)
  {
    Bz387752_Enum oldEnumUnsettable = enumUnsettable;
    enumUnsettable = newEnumUnsettable == null ? ENUM_UNSETTABLE_EDEFAULT : newEnumUnsettable;
    boolean oldEnumUnsettableESet = enumUnsettableESet;
    enumUnsettableESet = true;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, HibernateTestPackage.BZ387752_MAIN__ENUM_UNSETTABLE, oldEnumUnsettable, enumUnsettable,
          !oldEnumUnsettableESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void unsetEnumUnsettable()
  {
    Bz387752_Enum oldEnumUnsettable = enumUnsettable;
    boolean oldEnumUnsettableESet = enumUnsettableESet;
    enumUnsettable = ENUM_UNSETTABLE_EDEFAULT;
    enumUnsettableESet = false;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.UNSET, HibernateTestPackage.BZ387752_MAIN__ENUM_UNSETTABLE, oldEnumUnsettable, ENUM_UNSETTABLE_EDEFAULT,
          oldEnumUnsettableESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isSetEnumUnsettable()
  {
    return enumUnsettableESet;
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
    case HibernateTestPackage.BZ387752_MAIN__STR_UNSETTABLE:
      return getStrUnsettable();
    case HibernateTestPackage.BZ387752_MAIN__STR_SETTABLE:
      return getStrSettable();
    case HibernateTestPackage.BZ387752_MAIN__ENUM_SETTABLE:
      return getEnumSettable();
    case HibernateTestPackage.BZ387752_MAIN__ENUM_UNSETTABLE:
      return getEnumUnsettable();
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
    case HibernateTestPackage.BZ387752_MAIN__STR_UNSETTABLE:
      setStrUnsettable((String)newValue);
      return;
    case HibernateTestPackage.BZ387752_MAIN__STR_SETTABLE:
      setStrSettable((String)newValue);
      return;
    case HibernateTestPackage.BZ387752_MAIN__ENUM_SETTABLE:
      setEnumSettable((Bz387752_Enum)newValue);
      return;
    case HibernateTestPackage.BZ387752_MAIN__ENUM_UNSETTABLE:
      setEnumUnsettable((Bz387752_Enum)newValue);
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
    case HibernateTestPackage.BZ387752_MAIN__STR_UNSETTABLE:
      unsetStrUnsettable();
      return;
    case HibernateTestPackage.BZ387752_MAIN__STR_SETTABLE:
      setStrSettable(STR_SETTABLE_EDEFAULT);
      return;
    case HibernateTestPackage.BZ387752_MAIN__ENUM_SETTABLE:
      setEnumSettable(ENUM_SETTABLE_EDEFAULT);
      return;
    case HibernateTestPackage.BZ387752_MAIN__ENUM_UNSETTABLE:
      unsetEnumUnsettable();
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("null")
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case HibernateTestPackage.BZ387752_MAIN__STR_UNSETTABLE:
      return isSetStrUnsettable();
    case HibernateTestPackage.BZ387752_MAIN__STR_SETTABLE:
      return STR_SETTABLE_EDEFAULT == null ? strSettable != null : !STR_SETTABLE_EDEFAULT.equals(strSettable);
    case HibernateTestPackage.BZ387752_MAIN__ENUM_SETTABLE:
      return enumSettable != ENUM_SETTABLE_EDEFAULT;
    case HibernateTestPackage.BZ387752_MAIN__ENUM_UNSETTABLE:
      return isSetEnumUnsettable();
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

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (strUnsettable: ");
    if (strUnsettableESet)
    {
      result.append(strUnsettable);
    }
    else
    {
      result.append("<unset>");
    }
    result.append(", strSettable: ");
    result.append(strSettable);
    result.append(", enumSettable: ");
    result.append(enumSettable);
    result.append(", enumUnsettable: ");
    if (enumUnsettableESet)
    {
      result.append(enumUnsettable);
    }
    else
    {
      result.append("<unset>");
    }
    result.append(')');
    return result.toString();
  }

} // Bz387752_MainImpl
