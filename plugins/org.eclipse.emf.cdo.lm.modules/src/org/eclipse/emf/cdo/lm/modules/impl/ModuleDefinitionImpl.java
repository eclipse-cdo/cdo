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
package org.eclipse.emf.cdo.lm.modules.impl;

import org.eclipse.emf.cdo.etypes.Annotation;
import org.eclipse.emf.cdo.etypes.impl.ModelElementImpl;
import org.eclipse.emf.cdo.lm.modules.DependencyDefinition;
import org.eclipse.emf.cdo.lm.modules.ModuleDefinition;
import org.eclipse.emf.cdo.lm.modules.ModulesPackage;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.equinox.p2.metadata.Version;

import java.util.Collection;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Module
 * Definition</b></em>'.
 * @noextend This class is not intended to be subclassed by clients.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.modules.impl.ModuleDefinitionImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.modules.impl.ModuleDefinitionImpl#getVersion <em>Version</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.modules.impl.ModuleDefinitionImpl#getDependencies <em>Dependencies</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ModuleDefinitionImpl extends ModelElementImpl implements ModuleDefinition
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
  protected ModuleDefinitionImpl()
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
    return ModulesPackage.Literals.MODULE_DEFINITION;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getName()
  {
    return (String)eDynamicGet(ModulesPackage.MODULE_DEFINITION__NAME, ModulesPackage.Literals.MODULE_DEFINITION__NAME, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setName(String newName)
  {
    eDynamicSet(ModulesPackage.MODULE_DEFINITION__NAME, ModulesPackage.Literals.MODULE_DEFINITION__NAME, newName);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Version getVersion()
  {
    return (Version)eDynamicGet(ModulesPackage.MODULE_DEFINITION__VERSION, ModulesPackage.Literals.MODULE_DEFINITION__VERSION, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setVersion(Version newVersion)
  {
    eDynamicSet(ModulesPackage.MODULE_DEFINITION__VERSION, ModulesPackage.Literals.MODULE_DEFINITION__VERSION, newVersion);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public EList<DependencyDefinition> getDependencies()
  {
    return (EList<DependencyDefinition>)eDynamicGet(ModulesPackage.MODULE_DEFINITION__DEPENDENCIES, ModulesPackage.Literals.MODULE_DEFINITION__DEPENDENCIES,
        true, true);
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
    case ModulesPackage.MODULE_DEFINITION__ANNOTATIONS:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getAnnotations()).basicAdd(otherEnd, msgs);
    case ModulesPackage.MODULE_DEFINITION__DEPENDENCIES:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getDependencies()).basicAdd(otherEnd, msgs);
    }
    return eDynamicInverseAdd(otherEnd, featureID, msgs);
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
    case ModulesPackage.MODULE_DEFINITION__ANNOTATIONS:
      return ((InternalEList<?>)getAnnotations()).basicRemove(otherEnd, msgs);
    case ModulesPackage.MODULE_DEFINITION__DEPENDENCIES:
      return ((InternalEList<?>)getDependencies()).basicRemove(otherEnd, msgs);
    }
    return eDynamicInverseRemove(otherEnd, featureID, msgs);
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
    case ModulesPackage.MODULE_DEFINITION__ANNOTATIONS:
      return getAnnotations();
    case ModulesPackage.MODULE_DEFINITION__NAME:
      return getName();
    case ModulesPackage.MODULE_DEFINITION__VERSION:
      return getVersion();
    case ModulesPackage.MODULE_DEFINITION__DEPENDENCIES:
      return getDependencies();
    }
    return eDynamicGet(featureID, resolve, coreType);
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
    case ModulesPackage.MODULE_DEFINITION__ANNOTATIONS:
      getAnnotations().clear();
      getAnnotations().addAll((Collection<? extends Annotation>)newValue);
      return;
    case ModulesPackage.MODULE_DEFINITION__NAME:
      setName((String)newValue);
      return;
    case ModulesPackage.MODULE_DEFINITION__VERSION:
      setVersion((Version)newValue);
      return;
    case ModulesPackage.MODULE_DEFINITION__DEPENDENCIES:
      getDependencies().clear();
      getDependencies().addAll((Collection<? extends DependencyDefinition>)newValue);
      return;
    }
    eDynamicSet(featureID, newValue);
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
    case ModulesPackage.MODULE_DEFINITION__ANNOTATIONS:
      getAnnotations().clear();
      return;
    case ModulesPackage.MODULE_DEFINITION__NAME:
      setName(NAME_EDEFAULT);
      return;
    case ModulesPackage.MODULE_DEFINITION__VERSION:
      setVersion(VERSION_EDEFAULT);
      return;
    case ModulesPackage.MODULE_DEFINITION__DEPENDENCIES:
      getDependencies().clear();
      return;
    }
    eDynamicUnset(featureID);
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
    case ModulesPackage.MODULE_DEFINITION__ANNOTATIONS:
      return !getAnnotations().isEmpty();
    case ModulesPackage.MODULE_DEFINITION__NAME:
      return NAME_EDEFAULT == null ? getName() != null : !NAME_EDEFAULT.equals(getName());
    case ModulesPackage.MODULE_DEFINITION__VERSION:
      return VERSION_EDEFAULT == null ? getVersion() != null : !VERSION_EDEFAULT.equals(getVersion());
    case ModulesPackage.MODULE_DEFINITION__DEPENDENCIES:
      return !getDependencies().isEmpty();
    }
    return eDynamicIsSet(featureID);
  }

} // ModuleDefinitionImpl
