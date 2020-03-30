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
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.gmf.runtime.notation.Bounds;
import org.eclipse.gmf.runtime.notation.ImageStyle;
import org.eclipse.gmf.runtime.notation.NotationPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Image
 * Style</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ImageStyleImpl#getAntiAlias <em>Anti Alias</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ImageStyleImpl#getMaintainAspectRatio <em>Maintain Aspect Ratio</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ImageStyleImpl#getCropBound <em>Crop Bound</em>}</li>
 * </ul>
 *
 * @generated
 */
/*
 * @canBeSeenBy org.eclipse.gmf.runtime.notation.*
 */
public class ImageStyleImpl extends CDOObjectImpl implements ImageStyle
{
  /**
   * The default value of the '{@link #getAntiAlias() <em>Anti Alias</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getAntiAlias()
   * @generated
   * @ordered
   */
  protected static final Boolean ANTI_ALIAS_EDEFAULT = Boolean.TRUE;

  /**
   * The default value of the '{@link #getMaintainAspectRatio() <em>Maintain Aspect Ratio</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getMaintainAspectRatio()
   * @generated
   * @ordered
   */
  protected static final Boolean MAINTAIN_ASPECT_RATIO_EDEFAULT = Boolean.TRUE;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected ImageStyleImpl()
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
    return NotationPackage.Literals.IMAGE_STYLE;
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
  public Boolean getAntiAlias()
  {
    return (Boolean)eDynamicGet(NotationPackage.IMAGE_STYLE__ANTI_ALIAS, NotationPackage.Literals.IMAGE_STYLE__ANTI_ALIAS, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAntiAlias(Boolean newAntiAlias)
  {
    eDynamicSet(NotationPackage.IMAGE_STYLE__ANTI_ALIAS, NotationPackage.Literals.IMAGE_STYLE__ANTI_ALIAS, newAntiAlias);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Boolean getMaintainAspectRatio()
  {
    return (Boolean)eDynamicGet(NotationPackage.IMAGE_STYLE__MAINTAIN_ASPECT_RATIO, NotationPackage.Literals.IMAGE_STYLE__MAINTAIN_ASPECT_RATIO, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setMaintainAspectRatio(Boolean newMaintainAspectRatio)
  {
    eDynamicSet(NotationPackage.IMAGE_STYLE__MAINTAIN_ASPECT_RATIO, NotationPackage.Literals.IMAGE_STYLE__MAINTAIN_ASPECT_RATIO, newMaintainAspectRatio);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Bounds getCropBound()
  {
    return (Bounds)eDynamicGet(NotationPackage.IMAGE_STYLE__CROP_BOUND, NotationPackage.Literals.IMAGE_STYLE__CROP_BOUND, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetCropBound(Bounds newCropBound, NotificationChain msgs)
  {
    msgs = eDynamicInverseAdd((InternalEObject)newCropBound, NotationPackage.IMAGE_STYLE__CROP_BOUND, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setCropBound(Bounds newCropBound)
  {
    eDynamicSet(NotationPackage.IMAGE_STYLE__CROP_BOUND, NotationPackage.Literals.IMAGE_STYLE__CROP_BOUND, newCropBound);
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
    case NotationPackage.IMAGE_STYLE__CROP_BOUND:
      return basicSetCropBound(null, msgs);
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
    case NotationPackage.IMAGE_STYLE__ANTI_ALIAS:
      return getAntiAlias();
    case NotationPackage.IMAGE_STYLE__MAINTAIN_ASPECT_RATIO:
      return getMaintainAspectRatio();
    case NotationPackage.IMAGE_STYLE__CROP_BOUND:
      return getCropBound();
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
    case NotationPackage.IMAGE_STYLE__ANTI_ALIAS:
      setAntiAlias((Boolean)newValue);
      return;
    case NotationPackage.IMAGE_STYLE__MAINTAIN_ASPECT_RATIO:
      setMaintainAspectRatio((Boolean)newValue);
      return;
    case NotationPackage.IMAGE_STYLE__CROP_BOUND:
      setCropBound((Bounds)newValue);
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
    case NotationPackage.IMAGE_STYLE__ANTI_ALIAS:
      setAntiAlias(ANTI_ALIAS_EDEFAULT);
      return;
    case NotationPackage.IMAGE_STYLE__MAINTAIN_ASPECT_RATIO:
      setMaintainAspectRatio(MAINTAIN_ASPECT_RATIO_EDEFAULT);
      return;
    case NotationPackage.IMAGE_STYLE__CROP_BOUND:
      setCropBound((Bounds)null);
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
    case NotationPackage.IMAGE_STYLE__ANTI_ALIAS:
      return ANTI_ALIAS_EDEFAULT == null ? getAntiAlias() != null : !ANTI_ALIAS_EDEFAULT.equals(getAntiAlias());
    case NotationPackage.IMAGE_STYLE__MAINTAIN_ASPECT_RATIO:
      return MAINTAIN_ASPECT_RATIO_EDEFAULT == null ? getMaintainAspectRatio() != null : !MAINTAIN_ASPECT_RATIO_EDEFAULT.equals(getMaintainAspectRatio());
    case NotationPackage.IMAGE_STYLE__CROP_BOUND:
      return getCropBound() != null;
    }
    return eDynamicIsSet(featureID);
  }

} // ImageStyleImpl
