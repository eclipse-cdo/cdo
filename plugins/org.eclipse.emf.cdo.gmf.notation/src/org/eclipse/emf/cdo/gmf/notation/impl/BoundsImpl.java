/******************************************************************************
 * Copyright (c) 2018, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 ****************************************************************************/

package org.eclipse.emf.cdo.gmf.notation.impl;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.gmf.runtime.notation.Bounds;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.Size;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Bounds</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.BoundsImpl#getWidth <em>Width</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.BoundsImpl#getHeight <em>Height</em>}</li>
 * </ul>
 *
 * @generated
 */
/*
 * @canBeSeenBy %partners
 */
public class BoundsImpl extends LocationImpl implements Bounds
{
  /**
  * The default value of the '{@link #getWidth() <em>Width</em>}' attribute.
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @see #getWidth()
  * @generated
  * @ordered
  */
  protected static final int WIDTH_EDEFAULT = -1;

  /**
  * The default value of the '{@link #getHeight() <em>Height</em>}' attribute.
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @see #getHeight()
  * @generated
  * @ordered
  */
  protected static final int HEIGHT_EDEFAULT = -1;

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  protected BoundsImpl()
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
    return NotationPackage.Literals.BOUNDS;
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public int getWidth()
  {
    return ((Integer)eDynamicGet(NotationPackage.BOUNDS__WIDTH, NotationPackage.Literals.SIZE__WIDTH, true, true)).intValue();
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public void setWidth(int newWidth)
  {
    eDynamicSet(NotationPackage.BOUNDS__WIDTH, NotationPackage.Literals.SIZE__WIDTH, new Integer(newWidth));
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public int getHeight()
  {
    return ((Integer)eDynamicGet(NotationPackage.BOUNDS__HEIGHT, NotationPackage.Literals.SIZE__HEIGHT, true, true)).intValue();
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public void setHeight(int newHeight)
  {
    eDynamicSet(NotationPackage.BOUNDS__HEIGHT, NotationPackage.Literals.SIZE__HEIGHT, new Integer(newHeight));
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
    case NotationPackage.BOUNDS__X:
      return new Integer(getX());
    case NotationPackage.BOUNDS__Y:
      return new Integer(getY());
    case NotationPackage.BOUNDS__WIDTH:
      return new Integer(getWidth());
    case NotationPackage.BOUNDS__HEIGHT:
      return new Integer(getHeight());
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
    case NotationPackage.BOUNDS__X:
      setX(((Integer)newValue).intValue());
      return;
    case NotationPackage.BOUNDS__Y:
      setY(((Integer)newValue).intValue());
      return;
    case NotationPackage.BOUNDS__WIDTH:
      setWidth(((Integer)newValue).intValue());
      return;
    case NotationPackage.BOUNDS__HEIGHT:
      setHeight(((Integer)newValue).intValue());
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
    case NotationPackage.BOUNDS__X:
      setX(X_EDEFAULT);
      return;
    case NotationPackage.BOUNDS__Y:
      setY(Y_EDEFAULT);
      return;
    case NotationPackage.BOUNDS__WIDTH:
      setWidth(WIDTH_EDEFAULT);
      return;
    case NotationPackage.BOUNDS__HEIGHT:
      setHeight(HEIGHT_EDEFAULT);
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
    case NotationPackage.BOUNDS__X:
      return getX() != X_EDEFAULT;
    case NotationPackage.BOUNDS__Y:
      return getY() != Y_EDEFAULT;
    case NotationPackage.BOUNDS__WIDTH:
      return getWidth() != WIDTH_EDEFAULT;
    case NotationPackage.BOUNDS__HEIGHT:
      return getHeight() != HEIGHT_EDEFAULT;
    }
    return eDynamicIsSet(featureID);
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public int eBaseStructuralFeatureID(int derivedFeatureID, Class baseClass)
  {
    if (baseClass == Size.class)
    {
      switch (derivedFeatureID)
      {
      case NotationPackage.BOUNDS__WIDTH:
        return NotationPackage.SIZE__WIDTH;
      case NotationPackage.BOUNDS__HEIGHT:
        return NotationPackage.SIZE__HEIGHT;
      default:
        return -1;
      }
    }
    return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public int eDerivedStructuralFeatureID(int baseFeatureID, Class baseClass)
  {
    if (baseClass == Size.class)
    {
      switch (baseFeatureID)
      {
      case NotationPackage.SIZE__WIDTH:
        return NotationPackage.BOUNDS__WIDTH;
      case NotationPackage.SIZE__HEIGHT:
        return NotationPackage.BOUNDS__HEIGHT;
      default:
        return -1;
      }
    }
    return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
  }

} // BoundsImpl
