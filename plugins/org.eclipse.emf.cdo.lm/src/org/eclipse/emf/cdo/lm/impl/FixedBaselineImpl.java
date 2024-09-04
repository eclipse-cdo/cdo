/*
 * Copyright (c) 2022, 2024 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.lm.Baseline;
import org.eclipse.emf.cdo.lm.Change;
import org.eclipse.emf.cdo.lm.Dependency;
import org.eclipse.emf.cdo.lm.FixedBaseline;
import org.eclipse.emf.cdo.lm.LMPackage;
import org.eclipse.emf.cdo.lm.Stream;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.equinox.p2.metadata.Version;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Fixed
 * Baseline</b></em>'.
 * @noextend This class is not intended to be subclassed by clients.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.impl.FixedBaselineImpl#getVersion <em>Version</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.impl.FixedBaselineImpl#getDependencies <em>Dependencies</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class FixedBaselineImpl extends BaselineImpl implements FixedBaseline
{
  /**
   * The default value of the '{@link #getVersion() <em>Version</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getVersion()
   * @generated
   * @ordered
   */
  protected static final Version VERSION_EDEFAULT = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected FixedBaselineImpl()
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
    return LMPackage.Literals.FIXED_BASELINE;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Version getVersion()
  {
    return (Version)eDynamicGet(LMPackage.FIXED_BASELINE__VERSION, LMPackage.Literals.FIXED_BASELINE__VERSION, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setVersion(Version newVersion)
  {
    eDynamicSet(LMPackage.FIXED_BASELINE__VERSION, LMPackage.Literals.FIXED_BASELINE__VERSION, newVersion);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public EList<Dependency> getDependencies()
  {
    return (EList<Dependency>)eDynamicGet(LMPackage.FIXED_BASELINE__DEPENDENCIES, LMPackage.Literals.FIXED_BASELINE__DEPENDENCIES, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  @Override
  public EList<Change> getBasedChanges()
  {
    return getBasedChanges(this);
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
    case LMPackage.FIXED_BASELINE__DEPENDENCIES:
      return ((InternalEList<?>)getDependencies()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
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
    case LMPackage.FIXED_BASELINE__VERSION:
      return getVersion();
    case LMPackage.FIXED_BASELINE__DEPENDENCIES:
      return getDependencies();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case LMPackage.FIXED_BASELINE__VERSION:
      setVersion((Version)newValue);
      return;
    case LMPackage.FIXED_BASELINE__DEPENDENCIES:
      getDependencies().clear();
      getDependencies().addAll((Collection<? extends Dependency>)newValue);
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
    case LMPackage.FIXED_BASELINE__VERSION:
      setVersion(VERSION_EDEFAULT);
      return;
    case LMPackage.FIXED_BASELINE__DEPENDENCIES:
      getDependencies().clear();
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
    case LMPackage.FIXED_BASELINE__VERSION:
      return VERSION_EDEFAULT == null ? getVersion() != null : !VERSION_EDEFAULT.equals(getVersion());
    case LMPackage.FIXED_BASELINE__DEPENDENCIES:
      return !getDependencies().isEmpty();
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
    case LMPackage.FIXED_BASELINE___GET_BASED_CHANGES:
      return getBasedChanges();
    }
    return super.eInvoke(operationID, arguments);
  }

  @Override
  public boolean isFloating()
  {
    return false;
  }

  @Override
  public abstract String getName();

  @Override
  public abstract CDOBranchPointRef getBranchPoint();

  @Override
  public long getBaseTimeStamp()
  {
    CDOBranchPointRef branchPoint = getBranchPoint();
    if (branchPoint == null)
    {
      return CDOBranchPoint.UNSPECIFIED_DATE;
    }

    return branchPoint.getTimeStamp();
  }

  /**
   * @since 1.3
   */
  public static EList<Change> getBasedChanges(FixedBaseline fixedBaseline)
  {
    EList<Change> result = new BasicEList<>();

    for (Stream stream : fixedBaseline.getModule().getStreams())
    {
      for (Baseline baseline : stream.getContents())
      {
        if (baseline instanceof Change)
        {
          Change change = (Change)baseline;
          if (change.getBase() == fixedBaseline)
          {
            result.add(change);
          }
        }
      }
    }

    return result;
  }

} // FixedBaselineImpl
