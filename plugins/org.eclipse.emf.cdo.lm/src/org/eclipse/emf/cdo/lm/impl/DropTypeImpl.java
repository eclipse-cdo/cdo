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

import org.eclipse.emf.cdo.etypes.impl.ModelElementImpl;
import org.eclipse.emf.cdo.lm.DropType;
import org.eclipse.emf.cdo.lm.LMPackage;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import java.lang.reflect.InvocationTargetException;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Drop
 * Type</b></em>'.
 * @noextend This class is not intended to be subclassed by clients.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.impl.DropTypeImpl#getProcess <em>Process</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.impl.DropTypeImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.impl.DropTypeImpl#isRelease <em>Release</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DropTypeImpl extends ModelElementImpl implements DropType
{
  /**
   * The default value of the '{@link #getName() <em>Name</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getName()
   * @generated
   * @ordered
   */
  protected static final String NAME_EDEFAULT = null;

  /**
   * The default value of the '{@link #isRelease() <em>Release</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #isRelease()
   * @generated
   * @ordered
   */
  protected static final boolean RELEASE_EDEFAULT = false;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected DropTypeImpl()
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
    return LMPackage.Literals.DROP_TYPE;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public org.eclipse.emf.cdo.lm.Process getProcess()
  {
    return (org.eclipse.emf.cdo.lm.Process)eDynamicGet(LMPackage.DROP_TYPE__PROCESS, LMPackage.Literals.DROP_TYPE__PROCESS, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetProcess(org.eclipse.emf.cdo.lm.Process newProcess, NotificationChain msgs)
  {
    msgs = eBasicSetContainer((InternalEObject)newProcess, LMPackage.DROP_TYPE__PROCESS, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setProcess(org.eclipse.emf.cdo.lm.Process newProcess)
  {
    eDynamicSet(LMPackage.DROP_TYPE__PROCESS, LMPackage.Literals.DROP_TYPE__PROCESS, newProcess);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getName()
  {
    return (String)eDynamicGet(LMPackage.DROP_TYPE__NAME, LMPackage.Literals.DROP_TYPE__NAME, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setName(String newName)
  {
    eDynamicSet(LMPackage.DROP_TYPE__NAME, LMPackage.Literals.DROP_TYPE__NAME, newName);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isRelease()
  {
    return (Boolean)eDynamicGet(LMPackage.DROP_TYPE__RELEASE, LMPackage.Literals.DROP_TYPE__RELEASE, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setRelease(boolean newRelease)
  {
    eDynamicSet(LMPackage.DROP_TYPE__RELEASE, LMPackage.Literals.DROP_TYPE__RELEASE, newRelease);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public org.eclipse.emf.cdo.lm.System getSystem()
  {
    // TODO: implement this method
    // Ensure that you remove @generated or mark it @generated NOT
    throw new UnsupportedOperationException();
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
    case LMPackage.DROP_TYPE__PROCESS:
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      return basicSetProcess((org.eclipse.emf.cdo.lm.Process)otherEnd, msgs);
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
    case LMPackage.DROP_TYPE__PROCESS:
      return basicSetProcess(null, msgs);
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
    case LMPackage.DROP_TYPE__PROCESS:
      return eInternalContainer().eInverseRemove(this, LMPackage.PROCESS__DROP_TYPES, org.eclipse.emf.cdo.lm.Process.class, msgs);
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
    case LMPackage.DROP_TYPE__PROCESS:
      return getProcess();
    case LMPackage.DROP_TYPE__NAME:
      return getName();
    case LMPackage.DROP_TYPE__RELEASE:
      return isRelease();
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
    case LMPackage.DROP_TYPE__PROCESS:
      setProcess((org.eclipse.emf.cdo.lm.Process)newValue);
      return;
    case LMPackage.DROP_TYPE__NAME:
      setName((String)newValue);
      return;
    case LMPackage.DROP_TYPE__RELEASE:
      setRelease((Boolean)newValue);
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
    case LMPackage.DROP_TYPE__PROCESS:
      setProcess((org.eclipse.emf.cdo.lm.Process)null);
      return;
    case LMPackage.DROP_TYPE__NAME:
      setName(NAME_EDEFAULT);
      return;
    case LMPackage.DROP_TYPE__RELEASE:
      setRelease(RELEASE_EDEFAULT);
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
    case LMPackage.DROP_TYPE__PROCESS:
      return getProcess() != null;
    case LMPackage.DROP_TYPE__NAME:
      return NAME_EDEFAULT == null ? getName() != null : !NAME_EDEFAULT.equals(getName());
    case LMPackage.DROP_TYPE__RELEASE:
      return isRelease() != RELEASE_EDEFAULT;
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException
  {
    switch (operationID)
    {
    case LMPackage.DROP_TYPE___GET_SYSTEM:
      return getSystem();
    }
    return super.eInvoke(operationID, arguments);
  }

} // DropTypeImpl
