/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.impl;

import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnGenerator;
import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawngenmodelPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Dawn Generator</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.impl.DawnGeneratorImpl#getConflictColor <em>Conflict Color
 * </em>}</li>
 * <li>{@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.impl.DawnGeneratorImpl#getLocalLockColor <em>Local Lock
 * Color</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.impl.DawnGeneratorImpl#getRemoteLockColor <em>Remote Lock
 * Color</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 * @since 1.0
 */
public class DawnGeneratorImpl extends EObjectImpl implements DawnGenerator
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public static final String copyright = "Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n\r\nContributors:\r\n   Martin Fluegge - initial API and implementation";

  /**
   * The default value of the '{@link #getConflictColor() <em>Conflict Color</em>}' attribute. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @see #getConflictColor()
   * @generated
   * @ordered
   */
  protected static final String CONFLICT_COLOR_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getConflictColor() <em>Conflict Color</em>}' attribute. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @see #getConflictColor()
   * @generated
   * @ordered
   */
  protected String conflictColor = CONFLICT_COLOR_EDEFAULT;

  /**
   * The default value of the '{@link #getLocalLockColor() <em>Local Lock Color</em>}' attribute. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @see #getLocalLockColor()
   * @generated
   * @ordered
   */
  protected static final String LOCAL_LOCK_COLOR_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getLocalLockColor() <em>Local Lock Color</em>}' attribute. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @see #getLocalLockColor()
   * @generated
   * @ordered
   */
  protected String localLockColor = LOCAL_LOCK_COLOR_EDEFAULT;

  /**
   * The default value of the '{@link #getRemoteLockColor() <em>Remote Lock Color</em>}' attribute. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @see #getRemoteLockColor()
   * @generated
   * @ordered
   */
  protected static final String REMOTE_LOCK_COLOR_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getRemoteLockColor() <em>Remote Lock Color</em>}' attribute. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @see #getRemoteLockColor()
   * @generated
   * @ordered
   */
  protected String remoteLockColor = REMOTE_LOCK_COLOR_EDEFAULT;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected DawnGeneratorImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return DawngenmodelPackage.Literals.DAWN_GENERATOR;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public String getConflictColor()
  {
    return conflictColor;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setConflictColor(String newConflictColor)
  {
    String oldConflictColor = conflictColor;
    conflictColor = newConflictColor;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, DawngenmodelPackage.DAWN_GENERATOR__CONFLICT_COLOR,
          oldConflictColor, conflictColor));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public String getLocalLockColor()
  {
    return localLockColor;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setLocalLockColor(String newLocalLockColor)
  {
    String oldLocalLockColor = localLockColor;
    localLockColor = newLocalLockColor;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, DawngenmodelPackage.DAWN_GENERATOR__LOCAL_LOCK_COLOR,
          oldLocalLockColor, localLockColor));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public String getRemoteLockColor()
  {
    return remoteLockColor;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setRemoteLockColor(String newRemoteLockColor)
  {
    String oldRemoteLockColor = remoteLockColor;
    remoteLockColor = newRemoteLockColor;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, DawngenmodelPackage.DAWN_GENERATOR__REMOTE_LOCK_COLOR,
          oldRemoteLockColor, remoteLockColor));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case DawngenmodelPackage.DAWN_GENERATOR__CONFLICT_COLOR:
      return getConflictColor();
    case DawngenmodelPackage.DAWN_GENERATOR__LOCAL_LOCK_COLOR:
      return getLocalLockColor();
    case DawngenmodelPackage.DAWN_GENERATOR__REMOTE_LOCK_COLOR:
      return getRemoteLockColor();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case DawngenmodelPackage.DAWN_GENERATOR__CONFLICT_COLOR:
      setConflictColor((String)newValue);
      return;
    case DawngenmodelPackage.DAWN_GENERATOR__LOCAL_LOCK_COLOR:
      setLocalLockColor((String)newValue);
      return;
    case DawngenmodelPackage.DAWN_GENERATOR__REMOTE_LOCK_COLOR:
      setRemoteLockColor((String)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case DawngenmodelPackage.DAWN_GENERATOR__CONFLICT_COLOR:
      setConflictColor(CONFLICT_COLOR_EDEFAULT);
      return;
    case DawngenmodelPackage.DAWN_GENERATOR__LOCAL_LOCK_COLOR:
      setLocalLockColor(LOCAL_LOCK_COLOR_EDEFAULT);
      return;
    case DawngenmodelPackage.DAWN_GENERATOR__REMOTE_LOCK_COLOR:
      setRemoteLockColor(REMOTE_LOCK_COLOR_EDEFAULT);
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case DawngenmodelPackage.DAWN_GENERATOR__CONFLICT_COLOR:
      return CONFLICT_COLOR_EDEFAULT == null ? conflictColor != null : !CONFLICT_COLOR_EDEFAULT.equals(conflictColor);
    case DawngenmodelPackage.DAWN_GENERATOR__LOCAL_LOCK_COLOR:
      return LOCAL_LOCK_COLOR_EDEFAULT == null ? localLockColor != null : !LOCAL_LOCK_COLOR_EDEFAULT
          .equals(localLockColor);
    case DawngenmodelPackage.DAWN_GENERATOR__REMOTE_LOCK_COLOR:
      return REMOTE_LOCK_COLOR_EDEFAULT == null ? remoteLockColor != null : !REMOTE_LOCK_COLOR_EDEFAULT
          .equals(remoteLockColor);
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
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
    result.append(" (conflictColor: ");
    result.append(conflictColor);
    result.append(", localLockColor: ");
    result.append(localLockColor);
    result.append(", remoteLockColor: ");
    result.append(remoteLockColor);
    result.append(')');
    return result.toString();
  }

} // DawnGeneratorImpl
