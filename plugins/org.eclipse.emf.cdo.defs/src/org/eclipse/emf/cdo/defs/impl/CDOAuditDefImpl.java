/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.defs.impl;

import org.eclipse.emf.cdo.defs.CDOAuditDef;
import org.eclipse.emf.cdo.defs.CDODefsPackage;
import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.net4j.util.CheckUtil;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

import java.util.Date;

/**
 * <!-- begin-user-doc --> An implementation of the model object ' <em><b>CDO Audit Def</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.defs.impl.CDOAuditDefImpl#getTimeStamp <em>Time Stamp</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class CDOAuditDefImpl extends CDOViewDefImpl implements CDOAuditDef
{
  /**
   * The default value of the '{@link #getTimeStamp() <em>Time Stamp</em>}' attribute.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #getTimeStamp()
   * @generated
   * @ordered
   */
  protected static final Date TIME_STAMP_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getTimeStamp() <em>Time Stamp</em>}' attribute.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #getTimeStamp()
   * @generated
   * @ordered
   */
  protected Date timeStamp = TIME_STAMP_EDEFAULT;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected CDOAuditDefImpl()
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
    return CDODefsPackage.Literals.CDO_AUDIT_DEF;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public Date getTimeStamp()
  {
    return timeStamp;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public void setTimeStamp(Date newTimeStamp)
  {
    Date oldTimeStamp = timeStamp;
    timeStamp = newTimeStamp;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, CDODefsPackage.CDO_AUDIT_DEF__TIME_STAMP, oldTimeStamp,
          timeStamp));
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
    case CDODefsPackage.CDO_AUDIT_DEF__TIME_STAMP:
      return getTimeStamp();
    }
    return super.eGet(featureID, resolve, coreType);
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
    case CDODefsPackage.CDO_AUDIT_DEF__TIME_STAMP:
      setTimeStamp((Date)newValue);
      return;
    }
    super.eSet(featureID, newValue);
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
    case CDODefsPackage.CDO_AUDIT_DEF__TIME_STAMP:
      setTimeStamp(TIME_STAMP_EDEFAULT);
      return;
    }
    super.eUnset(featureID);
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
    case CDODefsPackage.CDO_AUDIT_DEF__TIME_STAMP:
      return TIME_STAMP_EDEFAULT == null ? timeStamp != null : !TIME_STAMP_EDEFAULT.equals(timeStamp);
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy())
      return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (timeStamp: ");
    result.append(timeStamp);
    result.append(')');
    return result.toString();
  }

  @Override
  protected Object createInstance()
  {
    CDOSession cdoSession = (CDOSession)getCdoSessionDef().getInstance();
    return cdoSession.openView(getTimeStamp().getTime());
  }

  @Override
  protected void validateDefinition()
  {
    super.validateDefinition();
    CheckUtil.checkState(eIsSet(CDODefsPackage.CDO_AUDIT_DEF__TIME_STAMP), "time stamp is not set!");
  }

} // CDOAuditDefImpl
