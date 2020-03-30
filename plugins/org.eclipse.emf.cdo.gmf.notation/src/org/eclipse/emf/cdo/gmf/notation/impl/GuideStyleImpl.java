/******************************************************************************
 * Copyright (c) 2018-2020 IBM Corporation and others.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 ****************************************************************************/

package org.eclipse.emf.cdo.gmf.notation.impl;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.gmf.runtime.notation.GuideStyle;
import org.eclipse.gmf.runtime.notation.NotationPackage;

import java.util.Collection;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Guide
 * Style</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.GuideStyleImpl#getHorizontalGuides <em>Horizontal Guides</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.GuideStyleImpl#getVerticalGuides <em>Vertical Guides</em>}</li>
 * </ul>
 *
 * @generated
 */
/*
 * @canBeSeenBy org.eclipse.gmf.runtime.notation.*
 */
public class GuideStyleImpl extends CDOObjectImpl implements GuideStyle
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected GuideStyleImpl()
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
    return NotationPackage.Literals.GUIDE_STYLE;
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList getHorizontalGuides()
  {
    return (EList)eDynamicGet(NotationPackage.GUIDE_STYLE__HORIZONTAL_GUIDES, NotationPackage.Literals.GUIDE_STYLE__HORIZONTAL_GUIDES, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList getVerticalGuides()
  {
    return (EList)eDynamicGet(NotationPackage.GUIDE_STYLE__VERTICAL_GUIDES, NotationPackage.Literals.GUIDE_STYLE__VERTICAL_GUIDES, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case NotationPackage.GUIDE_STYLE__HORIZONTAL_GUIDES:
      return ((InternalEList)getHorizontalGuides()).basicRemove(otherEnd, msgs);
    case NotationPackage.GUIDE_STYLE__VERTICAL_GUIDES:
      return ((InternalEList)getVerticalGuides()).basicRemove(otherEnd, msgs);
    }
    return eDynamicInverseRemove(otherEnd, featureID, msgs);
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
    case NotationPackage.GUIDE_STYLE__HORIZONTAL_GUIDES:
      return getHorizontalGuides();
    case NotationPackage.GUIDE_STYLE__VERTICAL_GUIDES:
      return getVerticalGuides();
    }
    return eDynamicGet(featureID, resolve, coreType);
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
    case NotationPackage.GUIDE_STYLE__HORIZONTAL_GUIDES:
      getHorizontalGuides().clear();
      getHorizontalGuides().addAll((Collection)newValue);
      return;
    case NotationPackage.GUIDE_STYLE__VERTICAL_GUIDES:
      getVerticalGuides().clear();
      getVerticalGuides().addAll((Collection)newValue);
      return;
    }
    eDynamicSet(featureID, newValue);
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
    case NotationPackage.GUIDE_STYLE__HORIZONTAL_GUIDES:
      getHorizontalGuides().clear();
      return;
    case NotationPackage.GUIDE_STYLE__VERTICAL_GUIDES:
      getVerticalGuides().clear();
      return;
    }
    eDynamicUnset(featureID);
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
    case NotationPackage.GUIDE_STYLE__HORIZONTAL_GUIDES:
      return !getHorizontalGuides().isEmpty();
    case NotationPackage.GUIDE_STYLE__VERTICAL_GUIDES:
      return !getVerticalGuides().isEmpty();
    }
    return eDynamicIsSet(featureID);
  }

} // GuideStyleImpl
