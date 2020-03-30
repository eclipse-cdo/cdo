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

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.gmf.runtime.notation.Guide;
import org.eclipse.gmf.runtime.notation.NotationPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Guide</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.GuideImpl#getPosition <em>Position</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.GuideImpl#getNodeMap <em>Node Map</em>}</li>
 * </ul>
 *
 * @generated
 */
/*
 * @canBeSeenBy org.eclipse.gmf.runtime.notation.*
 */
public class GuideImpl extends CDOObjectImpl implements Guide
{
  /**
  * The default value of the '{@link #getPosition() <em>Position</em>}' attribute.
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @see #getPosition()
  * @generated
  * @ordered
  */
  protected static final int POSITION_EDEFAULT = 0;

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  protected GuideImpl()
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
    return NotationPackage.Literals.GUIDE;
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
  public int getPosition()
  {
    return ((Integer)eDynamicGet(NotationPackage.GUIDE__POSITION, NotationPackage.Literals.GUIDE__POSITION, true, true)).intValue();
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public void setPosition(int newPosition)
  {
    eDynamicSet(NotationPackage.GUIDE__POSITION, NotationPackage.Literals.GUIDE__POSITION, new Integer(newPosition));
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public EMap getNodeMap()
  {
    return (EMap)eDynamicGet(NotationPackage.GUIDE__NODE_MAP, NotationPackage.Literals.GUIDE__NODE_MAP, true, true);
  }

  /**
  * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case NotationPackage.GUIDE__NODE_MAP:
      return ((InternalEList)getNodeMap()).basicRemove(otherEnd, msgs);
    }
    return eDynamicInverseRemove(otherEnd, featureID, msgs);
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
    case NotationPackage.GUIDE__POSITION:
      return new Integer(getPosition());
    case NotationPackage.GUIDE__NODE_MAP:
      if (coreType)
      {
        return getNodeMap();
      }
      else
      {
        return getNodeMap().map();
      }
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
    case NotationPackage.GUIDE__POSITION:
      setPosition(((Integer)newValue).intValue());
      return;
    case NotationPackage.GUIDE__NODE_MAP:
      ((EStructuralFeature.Setting)getNodeMap()).set(newValue);
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
    case NotationPackage.GUIDE__POSITION:
      setPosition(POSITION_EDEFAULT);
      return;
    case NotationPackage.GUIDE__NODE_MAP:
      getNodeMap().clear();
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
    case NotationPackage.GUIDE__POSITION:
      return getPosition() != POSITION_EDEFAULT;
    case NotationPackage.GUIDE__NODE_MAP:
      return !getNodeMap().isEmpty();
    }
    return eDynamicIsSet(featureID);
  }

} // GuideImpl
