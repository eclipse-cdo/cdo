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
import org.eclipse.emf.cdo.etypes.impl.ModelElementImpl;
import org.eclipse.emf.cdo.lm.Baseline;
import org.eclipse.emf.cdo.lm.Change;
import org.eclipse.emf.cdo.lm.LMPackage;
import org.eclipse.emf.cdo.lm.Module;
import org.eclipse.emf.cdo.lm.Stream;

import org.eclipse.net4j.util.StringUtil;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import java.lang.reflect.InvocationTargetException;

/**
 * <!-- begin-user-doc --> An implementation of the model object
 * '<em><b>Baseline</b></em>'.
 * @noextend This class is not intended to be subclassed by clients.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.impl.BaselineImpl#getStream <em>Stream</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.impl.BaselineImpl#isFloating <em>Floating</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class BaselineImpl extends ModelElementImpl implements Baseline
{
  /**
   * The default value of the '{@link #isFloating() <em>Floating</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #isFloating()
   * @generated
   * @ordered
   */
  protected static final boolean FLOATING_EDEFAULT = false;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected BaselineImpl()
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
    return LMPackage.Literals.BASELINE;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  @Override
  public org.eclipse.emf.cdo.lm.System getSystem()
  {
    Module module = getModule();
    if (module != null)
    {
      return module.getSystem();
    }

    return null;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case LMPackage.BASELINE__STREAM:
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      return basicSetStream((Stream)otherEnd, msgs);
    }
    return super.eInverseAdd(otherEnd, featureID, msgs);
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
    case LMPackage.BASELINE__STREAM:
      return basicSetStream(null, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs)
  {
    switch (eContainerFeatureID())
    {
    case LMPackage.BASELINE__STREAM:
      return eInternalContainer().eInverseRemove(this, LMPackage.STREAM__CONTENTS, Stream.class, msgs);
    }
    return super.eBasicRemoveFromContainerFeature(msgs);
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
    case LMPackage.BASELINE__STREAM:
      return getStream();
    case LMPackage.BASELINE__FLOATING:
      return isFloating();
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
    case LMPackage.BASELINE__STREAM:
      setStream((Stream)newValue);
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
    case LMPackage.BASELINE__STREAM:
      setStream((Stream)null);
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
    case LMPackage.BASELINE__STREAM:
      return getStream() != null;
    case LMPackage.BASELINE__FLOATING:
      return isFloating() != FLOATING_EDEFAULT;
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
    case LMPackage.BASELINE___GET_NAME:
      return getName();
    case LMPackage.BASELINE___GET_BRANCH_POINT:
      return getBranchPoint();
    case LMPackage.BASELINE___GET_BASE_TIME_STAMP:
      return getBaseTimeStamp();
    case LMPackage.BASELINE___GET_MODULE:
      return getModule();
    case LMPackage.BASELINE___GET_SYSTEM:
      return getSystem();
    }
    return super.eInvoke(operationID, arguments);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  @Override
  public Module getModule()
  {
    Stream stream = getStream();
    if (stream != null)
    {
      return stream.getModule();
    }

    return null;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Stream getStream()
  {
    return (Stream)eDynamicGet(LMPackage.BASELINE__STREAM, LMPackage.Literals.BASELINE__STREAM, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetStream(Stream newStream, NotificationChain msgs)
  {
    msgs = eBasicSetContainer((InternalEObject)newStream, LMPackage.BASELINE__STREAM, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setStream(Stream newStream)
  {
    eDynamicSet(LMPackage.BASELINE__STREAM, LMPackage.Literals.BASELINE__STREAM, newStream);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  @Override
  public abstract boolean isFloating();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  @Override
  public abstract String getName();

  @Override
  public String getTypeName()
  {
    return eClass().getName();
  }

  @Override
  public String getTypeAndName()
  {
    return getTypeName() + " " + getName();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  @Override
  public abstract CDOBranchPointRef getBranchPoint();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  @Override
  public abstract long getBaseTimeStamp();

  public static String name(Object o)
  {
    if (o instanceof Baseline)
    {
      return StringUtil.safe(((Baseline)o).getName());
    }

    return StringUtil.EMPTY;
  }

  public static long time(Object o)
  {
    if (o instanceof Baseline)
    {
      return ((Baseline)o).getBaseTimeStamp();
    }

    return CDOBranchPoint.UNSPECIFIED_DATE;
  }

  public static int change(Object o)
  {
    if (o instanceof Change)
    {
      return 1;
    }

    return 0;
  }

} // BaselineImpl
