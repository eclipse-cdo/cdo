/*
 * Copyright (c) 2022, 2024 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.lm.Baseline;
import org.eclipse.emf.cdo.lm.LMPackage;
import org.eclipse.emf.cdo.lm.Module;
import org.eclipse.emf.cdo.util.CDOURIUtil;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.InternalEList;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * <!-- begin-user-doc --> An implementation of the model object
 * '<em><b>System</b></em>'.
 * @noextend This class is not intended to be subclassed by clients.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.impl.SystemImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.impl.SystemImpl#getProcess <em>Process</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.impl.SystemImpl#getModules <em>Modules</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SystemImpl extends ModelElementImpl implements org.eclipse.emf.cdo.lm.System
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected SystemImpl()
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
    return LMPackage.Literals.SYSTEM;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getName()
  {
    return (String)eDynamicGet(LMPackage.SYSTEM__NAME, LMPackage.Literals.SYSTEM__NAME, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setName(String newName)
  {
    eDynamicSet(LMPackage.SYSTEM__NAME, LMPackage.Literals.SYSTEM__NAME, newName);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public org.eclipse.emf.cdo.lm.Process getProcess()
  {
    return (org.eclipse.emf.cdo.lm.Process)eDynamicGet(LMPackage.SYSTEM__PROCESS, LMPackage.Literals.SYSTEM__PROCESS, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetProcess(org.eclipse.emf.cdo.lm.Process newProcess, NotificationChain msgs)
  {
    msgs = eDynamicInverseAdd((InternalEObject)newProcess, LMPackage.SYSTEM__PROCESS, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setProcess(org.eclipse.emf.cdo.lm.Process newProcess)
  {
    eDynamicSet(LMPackage.SYSTEM__PROCESS, LMPackage.Literals.SYSTEM__PROCESS, newProcess);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public EList<org.eclipse.emf.cdo.lm.Module> getModules()
  {
    return (EList<org.eclipse.emf.cdo.lm.Module>)eDynamicGet(LMPackage.SYSTEM__MODULES, LMPackage.Literals.SYSTEM__MODULES, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  @Override
  public org.eclipse.emf.cdo.lm.Module getModule(String name)
  {
    if (name != null)
    {
      for (Module module : getModules())
      {
        if (name.equals(module.getName()))
        {
          return module;
        }
      }
    }

    return null;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case LMPackage.SYSTEM__PROCESS:
      org.eclipse.emf.cdo.lm.Process process = getProcess();
      if (process != null)
      {
        msgs = ((InternalEObject)process).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - LMPackage.SYSTEM__PROCESS, null, msgs);
      }
      return basicSetProcess((org.eclipse.emf.cdo.lm.Process)otherEnd, msgs);
    case LMPackage.SYSTEM__MODULES:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getModules()).basicAdd(otherEnd, msgs);
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
    case LMPackage.SYSTEM__PROCESS:
      return basicSetProcess(null, msgs);
    case LMPackage.SYSTEM__MODULES:
      return ((InternalEList<?>)getModules()).basicRemove(otherEnd, msgs);
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
    case LMPackage.SYSTEM__NAME:
      return getName();
    case LMPackage.SYSTEM__PROCESS:
      return getProcess();
    case LMPackage.SYSTEM__MODULES:
      return getModules();
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
    case LMPackage.SYSTEM__NAME:
      setName((String)newValue);
      return;
    case LMPackage.SYSTEM__PROCESS:
      setProcess((org.eclipse.emf.cdo.lm.Process)newValue);
      return;
    case LMPackage.SYSTEM__MODULES:
      getModules().clear();
      getModules().addAll((Collection<? extends org.eclipse.emf.cdo.lm.Module>)newValue);
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
    case LMPackage.SYSTEM__NAME:
      setName(NAME_EDEFAULT);
      return;
    case LMPackage.SYSTEM__PROCESS:
      setProcess((org.eclipse.emf.cdo.lm.Process)null);
      return;
    case LMPackage.SYSTEM__MODULES:
      getModules().clear();
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
    case LMPackage.SYSTEM__NAME:
      return NAME_EDEFAULT == null ? getName() != null : !NAME_EDEFAULT.equals(getName());
    case LMPackage.SYSTEM__PROCESS:
      return getProcess() != null;
    case LMPackage.SYSTEM__MODULES:
      return !getModules().isEmpty();
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
    case LMPackage.SYSTEM___GET_MODULE__STRING:
      return getModule((String)arguments.get(0));
    }
    return super.eInvoke(operationID, arguments);
  }

  @Override
  public java.util.stream.Stream<Baseline> getAllBaselines()
  {
    java.util.stream.Stream<Baseline> result = null;

    for (Module module : getModules())
    {
      java.util.stream.Stream<Baseline> element = module.getAllBaselines();
      if (result == null)
      {
        result = element;
      }
      else
      {
        result = java.util.stream.Stream.concat(result, element);
      }
    }

    return result;
  }

  @Override
  public void forEachBaseline(Consumer<Baseline> consumer)
  {
    for (Module module : getModules())
    {
      module.forEachBaseline(consumer);
    }
  }

  @Override
  public boolean forEachBaseline(Predicate<Baseline> predicate)
  {
    for (Module module : getModules())
    {
      if (module.forEachBaseline(predicate))
      {
        return true;
      }
    }

    return false;
  }

  /**
   * @since 1.2
   */
  public static void checkName(String name) throws IllegalArgumentException
  {
    if (name == null)
    {
      throw new IllegalArgumentException("Name is null");
    }

    String trimmed = name.trim();
    if (!Objects.equals(trimmed, name))
    {
      throw new IllegalArgumentException("Name contains leading or trailing whitespace: " + name);
    }

    if (trimmed.length() == 0)
    {
      throw new IllegalArgumentException("Name is empty");
    }

    if (name.indexOf(CDOURIUtil.SEGMENT_SEPARATOR_CHAR) != -1)
    {
      throw new IllegalArgumentException("Name contains a slash character: " + name);
    }

    if (name.indexOf(SEPARATOR) != -1)
    {
      throw new IllegalArgumentException("Name contains a colon character: " + name);
    }
  }

  /**
   * @since 1.2
   */
  public static boolean isValidName(String name)
  {
    try
    {
      checkName(name);
    }
    catch (IllegalArgumentException ex)
    {
      return false;
    }

    return true;
  }
} // SystemImpl
