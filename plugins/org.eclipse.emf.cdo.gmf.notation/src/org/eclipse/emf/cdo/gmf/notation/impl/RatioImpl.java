/******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 ****************************************************************************/

package org.eclipse.emf.cdo.gmf.notation.impl;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.Ratio;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Ratio</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.RatioImpl#getValue <em>Value</em>}</li>
 * </ul>
 *
 * @generated
 */
/*
 * @canBeSeenBy %partners
 */
public class RatioImpl extends CDOObjectImpl implements Ratio
{
  /**
  * The default value of the '{@link #getValue() <em>Value</em>}' attribute.
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @see #getValue()
  * @generated
  * @ordered
  */
  protected static final double VALUE_EDEFAULT = -1.0;

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  protected RatioImpl()
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
    return NotationPackage.Literals.RATIO;
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
  public double getValue()
  {
    return ((Double)eDynamicGet(NotationPackage.RATIO__VALUE, NotationPackage.Literals.RATIO__VALUE, true, true)).doubleValue();
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public void setValue(double newValue)
  {
    eDynamicSet(NotationPackage.RATIO__VALUE, NotationPackage.Literals.RATIO__VALUE, new Double(newValue));
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
    case NotationPackage.RATIO__VALUE:
      return new Double(getValue());
    }
    return eDynamicGet(featureID, resolve, coreType);
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
    case NotationPackage.RATIO__VALUE:
      setValue(((Double)newValue).doubleValue());
      return;
    }
    eDynamicSet(featureID, newValue);
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
    case NotationPackage.RATIO__VALUE:
      setValue(VALUE_EDEFAULT);
      return;
    }
    eDynamicUnset(featureID);
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
    case NotationPackage.RATIO__VALUE:
      return getValue() != VALUE_EDEFAULT;
    }
    return eDynamicIsSet(featureID);
  }

} // RatioImpl
