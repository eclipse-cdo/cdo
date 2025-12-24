/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.impl;

import org.eclipse.emf.cdo.etypes.impl.ModelElementImpl;
import org.eclipse.emf.cdo.lm.Dependency;
import org.eclipse.emf.cdo.lm.FixedBaseline;
import org.eclipse.emf.cdo.lm.LMPackage;
import org.eclipse.emf.cdo.lm.Stream;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.equinox.p2.metadata.VersionRange;

import java.lang.reflect.InvocationTargetException;

/**
 * <!-- begin-user-doc --> An implementation of the model object
 * '<em><b>Dependency</b></em>'.
 * @noextend This class is not intended to be subclassed by clients.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.impl.DependencyImpl#getTarget <em>Target</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.impl.DependencyImpl#getVersionRange <em>Version Range</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DependencyImpl extends ModelElementImpl implements Dependency
{
  /**
   * The default value of the '{@link #getVersionRange() <em>Version Range</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getVersionRange()
   * @generated
   * @ordered
   */
  protected static final VersionRange VERSION_RANGE_EDEFAULT = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected DependencyImpl()
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
    return LMPackage.Literals.DEPENDENCY;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public org.eclipse.emf.cdo.lm.Module getTarget()
  {
    return (org.eclipse.emf.cdo.lm.Module)eDynamicGet(LMPackage.DEPENDENCY__TARGET, LMPackage.Literals.DEPENDENCY__TARGET, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public org.eclipse.emf.cdo.lm.Module basicGetTarget()
  {
    return (org.eclipse.emf.cdo.lm.Module)eDynamicGet(LMPackage.DEPENDENCY__TARGET, LMPackage.Literals.DEPENDENCY__TARGET, false, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setTarget(org.eclipse.emf.cdo.lm.Module newTarget)
  {
    eDynamicSet(LMPackage.DEPENDENCY__TARGET, LMPackage.Literals.DEPENDENCY__TARGET, newTarget);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public VersionRange getVersionRange()
  {
    return (VersionRange)eDynamicGet(LMPackage.DEPENDENCY__VERSION_RANGE, LMPackage.Literals.DEPENDENCY__VERSION_RANGE, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setVersionRange(VersionRange newVersionRange)
  {
    eDynamicSet(LMPackage.DEPENDENCY__VERSION_RANGE, LMPackage.Literals.DEPENDENCY__VERSION_RANGE, newVersionRange);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  @Override
  public Stream getStream()
  {
    EObject container = eContainer();
    if (container instanceof FixedBaseline)
    {
      FixedBaseline baseline = (FixedBaseline)container;
      return baseline.getStream();
    }

    return null;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  @Override
  public org.eclipse.emf.cdo.lm.Module getModule()
  {
    EObject container = eContainer();
    if (container instanceof FixedBaseline)
    {
      FixedBaseline baseline = (FixedBaseline)container;
      return baseline.getModule();
    }

    return null;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  @Override
  public org.eclipse.emf.cdo.lm.System getSystem()
  {
    EObject container = eContainer();
    if (container instanceof FixedBaseline)
    {
      FixedBaseline baseline = (FixedBaseline)container;
      return baseline.getSystem();
    }

    return null;
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
    case LMPackage.DEPENDENCY__TARGET:
      if (resolve)
      {
        return getTarget();
      }
      return basicGetTarget();
    case LMPackage.DEPENDENCY__VERSION_RANGE:
      return getVersionRange();
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
    case LMPackage.DEPENDENCY__TARGET:
      setTarget((org.eclipse.emf.cdo.lm.Module)newValue);
      return;
    case LMPackage.DEPENDENCY__VERSION_RANGE:
      setVersionRange((VersionRange)newValue);
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
    case LMPackage.DEPENDENCY__TARGET:
      setTarget((org.eclipse.emf.cdo.lm.Module)null);
      return;
    case LMPackage.DEPENDENCY__VERSION_RANGE:
      setVersionRange(VERSION_RANGE_EDEFAULT);
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
    case LMPackage.DEPENDENCY__TARGET:
      return basicGetTarget() != null;
    case LMPackage.DEPENDENCY__VERSION_RANGE:
      return VERSION_RANGE_EDEFAULT == null ? getVersionRange() != null : !VERSION_RANGE_EDEFAULT.equals(getVersionRange());
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
    case LMPackage.DEPENDENCY___GET_STREAM:
      return getStream();
    case LMPackage.DEPENDENCY___GET_MODULE:
      return getModule();
    case LMPackage.DEPENDENCY___GET_SYSTEM:
      return getSystem();
    }
    return super.eInvoke(operationID, arguments);
  }

} // DependencyImpl
