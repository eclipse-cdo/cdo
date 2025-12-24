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
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.equinox.p2.metadata.VersionRange;

import java.util.Collection;

/**
 * <!-- begin-user-doc --> An implementation of the model object
 * '<em><b>Dependency Definition</b></em>'.
 * @noextend This class is not intended to be subclassed by clients.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.modules.impl.DependencyDefinitionImpl#getSource <em>Source</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.modules.impl.DependencyDefinitionImpl#getTargetName <em>Target Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.modules.impl.DependencyDefinitionImpl#getVersionRange <em>Version Range</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DependencyDefinitionImpl extends ModelElementImpl implements DependencyDefinition
{
  /**
   * The default value of the '{@link #getTargetName() <em>Target Name</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getTargetName()
   * @generated
   * @ordered
   */
  protected static final String TARGET_NAME_EDEFAULT = null;

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
  protected DependencyDefinitionImpl()
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
    return ModulesPackage.Literals.DEPENDENCY_DEFINITION;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ModuleDefinition getSource()
  {
    return (ModuleDefinition)eDynamicGet(ModulesPackage.DEPENDENCY_DEFINITION__SOURCE, ModulesPackage.Literals.DEPENDENCY_DEFINITION__SOURCE, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetSource(ModuleDefinition newSource, NotificationChain msgs)
  {
    msgs = eBasicSetContainer((InternalEObject)newSource, ModulesPackage.DEPENDENCY_DEFINITION__SOURCE, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setSource(ModuleDefinition newSource)
  {
    eDynamicSet(ModulesPackage.DEPENDENCY_DEFINITION__SOURCE, ModulesPackage.Literals.DEPENDENCY_DEFINITION__SOURCE, newSource);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getTargetName()
  {
    return (String)eDynamicGet(ModulesPackage.DEPENDENCY_DEFINITION__TARGET_NAME, ModulesPackage.Literals.DEPENDENCY_DEFINITION__TARGET_NAME, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setTargetName(String newTargetName)
  {
    eDynamicSet(ModulesPackage.DEPENDENCY_DEFINITION__TARGET_NAME, ModulesPackage.Literals.DEPENDENCY_DEFINITION__TARGET_NAME, newTargetName);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public VersionRange getVersionRange()
  {
    return (VersionRange)eDynamicGet(ModulesPackage.DEPENDENCY_DEFINITION__VERSION_RANGE, ModulesPackage.Literals.DEPENDENCY_DEFINITION__VERSION_RANGE, true,
        true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setVersionRange(VersionRange newVersionRange)
  {
    eDynamicSet(ModulesPackage.DEPENDENCY_DEFINITION__VERSION_RANGE, ModulesPackage.Literals.DEPENDENCY_DEFINITION__VERSION_RANGE, newVersionRange);
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
    case ModulesPackage.DEPENDENCY_DEFINITION__ANNOTATIONS:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getAnnotations()).basicAdd(otherEnd, msgs);
    case ModulesPackage.DEPENDENCY_DEFINITION__SOURCE:
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      return basicSetSource((ModuleDefinition)otherEnd, msgs);
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
    case ModulesPackage.DEPENDENCY_DEFINITION__ANNOTATIONS:
      return ((InternalEList<?>)getAnnotations()).basicRemove(otherEnd, msgs);
    case ModulesPackage.DEPENDENCY_DEFINITION__SOURCE:
      return basicSetSource(null, msgs);
    }
    return eDynamicInverseRemove(otherEnd, featureID, msgs);
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
    case ModulesPackage.DEPENDENCY_DEFINITION__SOURCE:
      return eInternalContainer().eInverseRemove(this, ModulesPackage.MODULE_DEFINITION__DEPENDENCIES, ModuleDefinition.class, msgs);
    }
    return eDynamicBasicRemoveFromContainer(msgs);
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
    case ModulesPackage.DEPENDENCY_DEFINITION__ANNOTATIONS:
      return getAnnotations();
    case ModulesPackage.DEPENDENCY_DEFINITION__SOURCE:
      return getSource();
    case ModulesPackage.DEPENDENCY_DEFINITION__TARGET_NAME:
      return getTargetName();
    case ModulesPackage.DEPENDENCY_DEFINITION__VERSION_RANGE:
      return getVersionRange();
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
    case ModulesPackage.DEPENDENCY_DEFINITION__ANNOTATIONS:
      getAnnotations().clear();
      getAnnotations().addAll((Collection<? extends Annotation>)newValue);
      return;
    case ModulesPackage.DEPENDENCY_DEFINITION__SOURCE:
      setSource((ModuleDefinition)newValue);
      return;
    case ModulesPackage.DEPENDENCY_DEFINITION__TARGET_NAME:
      setTargetName((String)newValue);
      return;
    case ModulesPackage.DEPENDENCY_DEFINITION__VERSION_RANGE:
      setVersionRange((VersionRange)newValue);
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
    case ModulesPackage.DEPENDENCY_DEFINITION__ANNOTATIONS:
      getAnnotations().clear();
      return;
    case ModulesPackage.DEPENDENCY_DEFINITION__SOURCE:
      setSource((ModuleDefinition)null);
      return;
    case ModulesPackage.DEPENDENCY_DEFINITION__TARGET_NAME:
      setTargetName(TARGET_NAME_EDEFAULT);
      return;
    case ModulesPackage.DEPENDENCY_DEFINITION__VERSION_RANGE:
      setVersionRange(VERSION_RANGE_EDEFAULT);
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
    case ModulesPackage.DEPENDENCY_DEFINITION__ANNOTATIONS:
      return !getAnnotations().isEmpty();
    case ModulesPackage.DEPENDENCY_DEFINITION__SOURCE:
      return getSource() != null;
    case ModulesPackage.DEPENDENCY_DEFINITION__TARGET_NAME:
      return TARGET_NAME_EDEFAULT == null ? getTargetName() != null : !TARGET_NAME_EDEFAULT.equals(getTargetName());
    case ModulesPackage.DEPENDENCY_DEFINITION__VERSION_RANGE:
      return VERSION_RANGE_EDEFAULT == null ? getVersionRange() != null : !VERSION_RANGE_EDEFAULT.equals(getVersionRange());
    }
    return eDynamicIsSet(featureID);
  }

} // DependencyDefinitionImpl
