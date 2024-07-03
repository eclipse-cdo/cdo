/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.impl;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchPointRef;
import org.eclipse.emf.cdo.common.branch.CDOBranchRef;
import org.eclipse.emf.cdo.lm.Delivery;
import org.eclipse.emf.cdo.lm.FixedBaseline;
import org.eclipse.emf.cdo.lm.FloatingBaseline;
import org.eclipse.emf.cdo.lm.LMPackage;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

import java.lang.reflect.InvocationTargetException;

/**
 * <!-- begin-user-doc --> An implementation of the model object
 * '<em><b>Floating Baseline</b></em>'.
 * @noextend This class is not intended to be subclassed by clients.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.impl.FloatingBaselineImpl#isClosed <em>Closed</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class FloatingBaselineImpl extends BaselineImpl implements FloatingBaseline
{
  /**
   * The default value of the '{@link #isClosed() <em>Closed</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #isClosed()
   * @generated
   * @ordered
   */
  protected static final boolean CLOSED_EDEFAULT = false;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected FloatingBaselineImpl()
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
    return LMPackage.Literals.FLOATING_BASELINE;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  @Override
  public abstract CDOBranchRef getBranch();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isClosed()
  {
    return (Boolean)eDynamicGet(LMPackage.FLOATING_BASELINE__CLOSED, LMPackage.Literals.FLOATING_BASELINE__CLOSED, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setClosed(boolean newClosed)
  {
    eDynamicSet(LMPackage.FLOATING_BASELINE__CLOSED, LMPackage.Literals.FLOATING_BASELINE__CLOSED, newClosed);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  @Override
  public abstract FixedBaseline getBase();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  @Override
  public abstract EList<Delivery> getDeliveries();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case LMPackage.FLOATING_BASELINE__CLOSED:
      return isClosed();
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
    case LMPackage.FLOATING_BASELINE__CLOSED:
      setClosed((Boolean)newValue);
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
    case LMPackage.FLOATING_BASELINE__CLOSED:
      setClosed(CLOSED_EDEFAULT);
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
    case LMPackage.FLOATING_BASELINE__CLOSED:
      return isClosed() != CLOSED_EDEFAULT;
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException
  {
    switch (operationID)
    {
    case LMPackage.FLOATING_BASELINE___GET_BASE:
      return getBase();
    case LMPackage.FLOATING_BASELINE___GET_DELIVERIES:
      return getDeliveries();
    case LMPackage.FLOATING_BASELINE___GET_BRANCH:
      return getBranch();
    }
    return super.eInvoke(operationID, arguments);
  }

  @Override
  public boolean isFloating()
  {
    return true;
  }

  @Override
  public CDOBranchPointRef getBranchPoint()
  {
    return getBranchPoint(this);
  }

  /**
   * @since 1.3
   */
  public static CDOBranchPointRef getBranchPoint(FloatingBaseline floatingBaseline)
  {
    CDOBranchRef branch = floatingBaseline.getBranch();
    if (branch == null)
    {
      return null;
    }

    String branchPath = branch.getBranchPath();
    return new CDOBranchPointRef(branchPath, CDOBranchPoint.UNSPECIFIED_DATE);
  }

} // FloatingBaselineImpl
