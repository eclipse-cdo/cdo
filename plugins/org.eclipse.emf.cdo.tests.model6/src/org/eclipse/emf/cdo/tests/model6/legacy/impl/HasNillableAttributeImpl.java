/*
 * Copyright (c) 2013, 2015, 2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model6.legacy.impl;

import org.eclipse.emf.cdo.tests.model6.HasNillableAttribute;
import org.eclipse.emf.cdo.tests.model6.legacy.Model6Package;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Has Nillable Attribute</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.HasNillableAttributeImpl#getNillable <em>Nillable</em>}</li>
 * </ul>
 *
 * @generated
 */
public class HasNillableAttributeImpl extends EObjectImpl implements HasNillableAttribute
{
  /**
   * The default value of the '{@link #getNillable() <em>Nillable</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getNillable()
   * @generated
   * @ordered
   */
  protected static final String NILLABLE_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getNillable() <em>Nillable</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getNillable()
   * @generated
   * @ordered
   */
  protected String nillable = NILLABLE_EDEFAULT;

  /**
   * This is true if the Nillable attribute has been set.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  protected boolean nillableESet;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected HasNillableAttributeImpl()
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
    return Model6Package.eINSTANCE.getHasNillableAttribute();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getNillable()
  {
    return nillable;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setNillable(String newNillable)
  {
    String oldNillable = nillable;
    nillable = newNillable;
    boolean oldNillableESet = nillableESet;
    nillableESet = true;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model6Package.HAS_NILLABLE_ATTRIBUTE__NILLABLE, oldNillable, nillable, !oldNillableESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetNillable()
  {
    String oldNillable = nillable;
    boolean oldNillableESet = nillableESet;
    nillable = NILLABLE_EDEFAULT;
    nillableESet = false;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.UNSET, Model6Package.HAS_NILLABLE_ATTRIBUTE__NILLABLE, oldNillable, NILLABLE_EDEFAULT, oldNillableESet));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetNillable()
  {
    return nillableESet;
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
    case Model6Package.HAS_NILLABLE_ATTRIBUTE__NILLABLE:
      return getNillable();
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
    case Model6Package.HAS_NILLABLE_ATTRIBUTE__NILLABLE:
      setNillable((String)newValue);
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
    case Model6Package.HAS_NILLABLE_ATTRIBUTE__NILLABLE:
      unsetNillable();
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
    case Model6Package.HAS_NILLABLE_ATTRIBUTE__NILLABLE:
      return isSetNillable();
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
    result.append(" (nillable: ");
    if (nillableESet)
    {
      result.append(nillable);
    }
    else
    {
      result.append("<unset>");
    }
    result.append(')');
    return result.toString();
  }

} // HasNillableAttributeImpl
