/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup.impl;

import org.eclipse.emf.cdo.releng.setup.InstallableUnit;
import org.eclipse.emf.cdo.releng.setup.SetupFactory;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.equinox.p2.metadata.VersionRange;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Installable Unit</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.InstallableUnitImpl#getID <em>ID</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.InstallableUnitImpl#getVersionRange <em>Version Range</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class InstallableUnitImpl extends MinimalEObjectImpl.Container implements InstallableUnit
{
  /**
   * The default value of the '{@link #getID() <em>ID</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getID()
   * @generated
   * @ordered
   */
  protected static final String ID_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getID() <em>ID</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getID()
   * @generated
   * @ordered
   */
  protected String iD = ID_EDEFAULT;

  /**
   * The default value of the '{@link #getVersionRange() <em>Version Range</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getVersionRange()
   * @generated
   * @ordered
   */
  protected static final VersionRange VERSION_RANGE_EDEFAULT = (VersionRange)SetupFactory.eINSTANCE.createFromString(
      SetupPackage.eINSTANCE.getVersionRange(), "0.0.0");

  /**
  	 * The cached value of the '{@link #getVersionRange() <em>Version Range</em>}' attribute.
  	 * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
  	 * @see #getVersionRange()
  	 * @generated
  	 * @ordered
  	 */
  protected VersionRange versionRange = VERSION_RANGE_EDEFAULT;

  /**
  	 * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
  	 * @generated
  	 */
  protected InstallableUnitImpl()
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
    return SetupPackage.Literals.INSTALLABLE_UNIT;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getID()
  {
    return iD;
  }

  /**
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  public void setID(String newID)
  {
    String oldID = iD;
    iD = newID;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.INSTALLABLE_UNIT__ID, oldID, iD));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public VersionRange getVersionRange()
  {
    return versionRange;
  }

  /**
  	 * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
  	 * @generated
  	 */
  public void setVersionRange(VersionRange newVersionRange)
  {
    VersionRange oldVersionRange = versionRange;
    versionRange = newVersionRange;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.INSTALLABLE_UNIT__VERSION_RANGE,
          oldVersionRange, versionRange));
    }
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
    case SetupPackage.INSTALLABLE_UNIT__ID:
      return getID();
    case SetupPackage.INSTALLABLE_UNIT__VERSION_RANGE:
      return getVersionRange();
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
    case SetupPackage.INSTALLABLE_UNIT__ID:
      setID((String)newValue);
      return;
    case SetupPackage.INSTALLABLE_UNIT__VERSION_RANGE:
      setVersionRange((VersionRange)newValue);
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
    case SetupPackage.INSTALLABLE_UNIT__ID:
      setID(ID_EDEFAULT);
      return;
    case SetupPackage.INSTALLABLE_UNIT__VERSION_RANGE:
      setVersionRange(VERSION_RANGE_EDEFAULT);
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
    case SetupPackage.INSTALLABLE_UNIT__ID:
      return ID_EDEFAULT == null ? iD != null : !ID_EDEFAULT.equals(iD);
    case SetupPackage.INSTALLABLE_UNIT__VERSION_RANGE:
      return VERSION_RANGE_EDEFAULT == null ? versionRange != null : !VERSION_RANGE_EDEFAULT.equals(versionRange);
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
    result.append(" (iD: ");
    result.append(iD);
    result.append(", versionRange: ");
    result.append(versionRange);
    result.append(')');
    return result.toString();
  }

} // InstallableUnitImpl
