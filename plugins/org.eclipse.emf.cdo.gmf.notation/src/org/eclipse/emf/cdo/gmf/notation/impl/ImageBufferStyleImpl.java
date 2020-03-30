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

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.gmf.runtime.notation.Bounds;
import org.eclipse.gmf.runtime.notation.Image;
import org.eclipse.gmf.runtime.notation.ImageBufferStyle;
import org.eclipse.gmf.runtime.notation.NotationPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Image
 * Buffer Style</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ImageBufferStyleImpl#getImageBuffer <em>Image Buffer</em>}</li>
 * </ul>
 *
 * @generated
 */
/*
 * @canBeSeenBy org.eclipse.gmf.runtime.notation.*
 */
public class ImageBufferStyleImpl extends ImageStyleImpl implements ImageBufferStyle
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected ImageBufferStyleImpl()
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
    return NotationPackage.Literals.IMAGE_BUFFER_STYLE;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Image getImageBuffer()
  {
    return (Image)eDynamicGet(NotationPackage.IMAGE_BUFFER_STYLE__IMAGE_BUFFER, NotationPackage.Literals.IMAGE_BUFFER_STYLE__IMAGE_BUFFER, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetImageBuffer(Image newImageBuffer, NotificationChain msgs)
  {
    msgs = eDynamicInverseAdd((InternalEObject)newImageBuffer, NotationPackage.IMAGE_BUFFER_STYLE__IMAGE_BUFFER, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setImageBuffer(Image newImageBuffer)
  {
    eDynamicSet(NotationPackage.IMAGE_BUFFER_STYLE__IMAGE_BUFFER, NotationPackage.Literals.IMAGE_BUFFER_STYLE__IMAGE_BUFFER, newImageBuffer);
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
    case NotationPackage.IMAGE_BUFFER_STYLE__CROP_BOUND:
      return basicSetCropBound(null, msgs);
    case NotationPackage.IMAGE_BUFFER_STYLE__IMAGE_BUFFER:
      return basicSetImageBuffer(null, msgs);
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
    case NotationPackage.IMAGE_BUFFER_STYLE__ANTI_ALIAS:
      return getAntiAlias();
    case NotationPackage.IMAGE_BUFFER_STYLE__MAINTAIN_ASPECT_RATIO:
      return getMaintainAspectRatio();
    case NotationPackage.IMAGE_BUFFER_STYLE__CROP_BOUND:
      return getCropBound();
    case NotationPackage.IMAGE_BUFFER_STYLE__IMAGE_BUFFER:
      return getImageBuffer();
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
    case NotationPackage.IMAGE_BUFFER_STYLE__ANTI_ALIAS:
      setAntiAlias((Boolean)newValue);
      return;
    case NotationPackage.IMAGE_BUFFER_STYLE__MAINTAIN_ASPECT_RATIO:
      setMaintainAspectRatio((Boolean)newValue);
      return;
    case NotationPackage.IMAGE_BUFFER_STYLE__CROP_BOUND:
      setCropBound((Bounds)newValue);
      return;
    case NotationPackage.IMAGE_BUFFER_STYLE__IMAGE_BUFFER:
      setImageBuffer((Image)newValue);
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
    case NotationPackage.IMAGE_BUFFER_STYLE__ANTI_ALIAS:
      setAntiAlias(ANTI_ALIAS_EDEFAULT);
      return;
    case NotationPackage.IMAGE_BUFFER_STYLE__MAINTAIN_ASPECT_RATIO:
      setMaintainAspectRatio(MAINTAIN_ASPECT_RATIO_EDEFAULT);
      return;
    case NotationPackage.IMAGE_BUFFER_STYLE__CROP_BOUND:
      setCropBound((Bounds)null);
      return;
    case NotationPackage.IMAGE_BUFFER_STYLE__IMAGE_BUFFER:
      setImageBuffer((Image)null);
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
    case NotationPackage.IMAGE_BUFFER_STYLE__ANTI_ALIAS:
      return ANTI_ALIAS_EDEFAULT == null ? getAntiAlias() != null : !ANTI_ALIAS_EDEFAULT.equals(getAntiAlias());
    case NotationPackage.IMAGE_BUFFER_STYLE__MAINTAIN_ASPECT_RATIO:
      return MAINTAIN_ASPECT_RATIO_EDEFAULT == null ? getMaintainAspectRatio() != null : !MAINTAIN_ASPECT_RATIO_EDEFAULT.equals(getMaintainAspectRatio());
    case NotationPackage.IMAGE_BUFFER_STYLE__CROP_BOUND:
      return getCropBound() != null;
    case NotationPackage.IMAGE_BUFFER_STYLE__IMAGE_BUFFER:
      return getImageBuffer() != null;
    }
    return eDynamicIsSet(featureID);
  }

} // ImageBufferStyleImpl
