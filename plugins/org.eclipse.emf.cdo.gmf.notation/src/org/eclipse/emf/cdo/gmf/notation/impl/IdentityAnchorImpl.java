/******************************************************************************
 * Copyright (c) 2018 IBM Corporation and others.
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

import org.eclipse.gmf.runtime.notation.IdentityAnchor;
import org.eclipse.gmf.runtime.notation.NotationPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Identity Anchor</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.IdentityAnchorImpl#getId <em>Id</em>}</li>
 * </ul>
 *
 * @generated
 */
/*
 * @canBeSeenBy %partners
 */
public class IdentityAnchorImpl extends CDOObjectImpl implements IdentityAnchor
{
  /**
  * The default value of the '{@link #getId() <em>Id</em>}' attribute.
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @see #getId()
  * @generated
  * @ordered
  */
  protected static final String ID_EDEFAULT = "anchor"; //$NON-NLS-1$

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  protected IdentityAnchorImpl()
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
    return NotationPackage.Literals.IDENTITY_ANCHOR;
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
  public String getId()
  {
    return (String)eDynamicGet(NotationPackage.IDENTITY_ANCHOR__ID, NotationPackage.Literals.IDENTITY_ANCHOR__ID, true, true);
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  public void setId(String newId)
  {
    eDynamicSet(NotationPackage.IDENTITY_ANCHOR__ID, NotationPackage.Literals.IDENTITY_ANCHOR__ID, newId);
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
    case NotationPackage.IDENTITY_ANCHOR__ID:
      return getId();
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
    case NotationPackage.IDENTITY_ANCHOR__ID:
      setId((String)newValue);
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
    case NotationPackage.IDENTITY_ANCHOR__ID:
      setId(ID_EDEFAULT);
      return;
    }
    eDynamicUnset(featureID);
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
    case NotationPackage.IDENTITY_ANCHOR__ID:
      return ID_EDEFAULT == null ? getId() != null : !ID_EDEFAULT.equals(getId());
    }
    return eDynamicIsSet(featureID);
  }

} // IdentityAnchorImpl
