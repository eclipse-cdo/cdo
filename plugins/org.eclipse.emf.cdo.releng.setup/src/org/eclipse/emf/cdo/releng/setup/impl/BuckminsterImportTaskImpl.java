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

import org.eclipse.emf.cdo.releng.setup.BuckminsterImportTask;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.SetupTaskContext;
import org.eclipse.emf.cdo.releng.setup.Trigger;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

import java.util.Set;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Buckminster Import Task</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.BuckminsterImportTaskImpl#getMspec <em>Mspec</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class BuckminsterImportTaskImpl extends BasicMaterializationTaskImpl implements BuckminsterImportTask
{
  private static final String[] REQUIRED_IUS = { "org.eclipse.buckminster.core.feature.feature.group",
      "org.eclipse.buckminster.pde.feature.feature.group", "org.eclipse.buckminster.git.feature.feature.group" };

  private static final String[] REQUIRED_REPOSITORIES = { "http://download.eclipse.org/tools/buckminster/updates-4.3" };

  /**
   * The default value of the '{@link #getMspec() <em>Mspec</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getMspec()
   * @generated
   * @ordered
   */
  protected static final String MSPEC_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getMspec() <em>Mspec</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getMspec()
   * @generated
   * @ordered
   */
  protected String mspec = MSPEC_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected BuckminsterImportTaskImpl()
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
    return SetupPackage.Literals.BUCKMINSTER_IMPORT_TASK;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getMspec()
  {
    return mspec;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setMspec(String newMspec)
  {
    String oldMspec = mspec;
    mspec = newMspec;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.BUCKMINSTER_IMPORT_TASK__MSPEC, oldMspec,
          mspec));
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
    case SetupPackage.BUCKMINSTER_IMPORT_TASK__MSPEC:
      return getMspec();
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
    case SetupPackage.BUCKMINSTER_IMPORT_TASK__MSPEC:
      setMspec((String)newValue);
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
    case SetupPackage.BUCKMINSTER_IMPORT_TASK__MSPEC:
      setMspec(MSPEC_EDEFAULT);
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
    case SetupPackage.BUCKMINSTER_IMPORT_TASK__MSPEC:
      return MSPEC_EDEFAULT == null ? mspec != null : !MSPEC_EDEFAULT.equals(mspec);
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
    result.append(" (mspec: ");
    result.append(mspec);
    result.append(')');
    return result.toString();
  }

  @Override
  public Set<Trigger> getValidTriggers()
  {
    return Trigger.IDE_TRIGGERS;
  }

  @Override
  protected String[] getRequiredInstallableUnits()
  {
    return REQUIRED_IUS;
  }

  @Override
  protected String[] getRequiredP2Repositories()
  {
    return REQUIRED_REPOSITORIES;
  }

  @Override
  protected String getMspec(SetupTaskContext context)
  {
    return getMspec();
  }
} // BuckminsterImportTaskImpl
