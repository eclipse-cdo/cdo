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
import org.eclipse.emf.cdo.lm.ModuleType;
import org.eclipse.emf.cdo.lm.modules.ModulesFactory;
import org.eclipse.emf.cdo.lm.modules.ModulesPackage;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.equinox.p2.metadata.Version;

import java.util.Collection;
import java.util.Objects;

/**
 * <!-- begin-user-doc --> An implementation of the model object
 * '<em><b>Process</b></em>'.
 * @noextend This class is not intended to be subclassed by clients.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.impl.ProcessImpl#getSystem <em>System</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.impl.ProcessImpl#getModuleTypes <em>Module Types</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.impl.ProcessImpl#getDropTypes <em>Drop Types</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.impl.ProcessImpl#getModuleDefinitionPath <em>Module Definition Path</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.impl.ProcessImpl#getInitialModuleVersion <em>Initial Module Version</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ProcessImpl extends ModelElementImpl implements org.eclipse.emf.cdo.lm.Process
{
  /**
   * The default value of the '{@link #getModuleDefinitionPath() <em>Module
   * Definition Path</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @see #getModuleDefinitionPath()
   * @generated
   * @ordered
   */
  protected static final String MODULE_DEFINITION_PATH_EDEFAULT = null;

  /**
   * The default value of the '{@link #getInitialModuleVersion() <em>Initial
   * Module Version</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @see #getInitialModuleVersion()
   * @generated
   * @ordered
   */
  protected static final Version INITIAL_MODULE_VERSION_EDEFAULT = (Version)ModulesFactory.eINSTANCE.createFromString(ModulesPackage.eINSTANCE.getVersion(),
      "0.1.0");

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected ProcessImpl()
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
    return LMPackage.Literals.PROCESS;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public org.eclipse.emf.cdo.lm.System getSystem()
  {
    return (org.eclipse.emf.cdo.lm.System)eDynamicGet(LMPackage.PROCESS__SYSTEM, LMPackage.Literals.PROCESS__SYSTEM, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetSystem(org.eclipse.emf.cdo.lm.System newSystem, NotificationChain msgs)
  {
    msgs = eBasicSetContainer((InternalEObject)newSystem, LMPackage.PROCESS__SYSTEM, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setSystem(org.eclipse.emf.cdo.lm.System newSystem)
  {
    eDynamicSet(LMPackage.PROCESS__SYSTEM, LMPackage.Literals.PROCESS__SYSTEM, newSystem);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public EList<DropType> getDropTypes()
  {
    return (EList<DropType>)eDynamicGet(LMPackage.PROCESS__DROP_TYPES, LMPackage.Literals.PROCESS__DROP_TYPES, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getModuleDefinitionPath()
  {
    return (String)eDynamicGet(LMPackage.PROCESS__MODULE_DEFINITION_PATH, LMPackage.Literals.PROCESS__MODULE_DEFINITION_PATH, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setModuleDefinitionPath(String newModuleDefinitionPath)
  {
    eDynamicSet(LMPackage.PROCESS__MODULE_DEFINITION_PATH, LMPackage.Literals.PROCESS__MODULE_DEFINITION_PATH, newModuleDefinitionPath);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Version getInitialModuleVersion()
  {
    return (Version)eDynamicGet(LMPackage.PROCESS__INITIAL_MODULE_VERSION, LMPackage.Literals.PROCESS__INITIAL_MODULE_VERSION, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setInitialModuleVersion(Version newInitialModuleVersion)
  {
    eDynamicSet(LMPackage.PROCESS__INITIAL_MODULE_VERSION, LMPackage.Literals.PROCESS__INITIAL_MODULE_VERSION, newInitialModuleVersion);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public EList<ModuleType> getModuleTypes()
  {
    return (EList<ModuleType>)eDynamicGet(LMPackage.PROCESS__MODULE_TYPES, LMPackage.Literals.PROCESS__MODULE_TYPES, true, true);
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
    case LMPackage.PROCESS__SYSTEM:
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      return basicSetSystem((org.eclipse.emf.cdo.lm.System)otherEnd, msgs);
    case LMPackage.PROCESS__MODULE_TYPES:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getModuleTypes()).basicAdd(otherEnd, msgs);
    case LMPackage.PROCESS__DROP_TYPES:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getDropTypes()).basicAdd(otherEnd, msgs);
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
    case LMPackage.PROCESS__SYSTEM:
      return basicSetSystem(null, msgs);
    case LMPackage.PROCESS__MODULE_TYPES:
      return ((InternalEList<?>)getModuleTypes()).basicRemove(otherEnd, msgs);
    case LMPackage.PROCESS__DROP_TYPES:
      return ((InternalEList<?>)getDropTypes()).basicRemove(otherEnd, msgs);
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
    case LMPackage.PROCESS__SYSTEM:
      return eInternalContainer().eInverseRemove(this, LMPackage.SYSTEM__PROCESS, org.eclipse.emf.cdo.lm.System.class, msgs);
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
    case LMPackage.PROCESS__SYSTEM:
      return getSystem();
    case LMPackage.PROCESS__MODULE_TYPES:
      return getModuleTypes();
    case LMPackage.PROCESS__DROP_TYPES:
      return getDropTypes();
    case LMPackage.PROCESS__MODULE_DEFINITION_PATH:
      return getModuleDefinitionPath();
    case LMPackage.PROCESS__INITIAL_MODULE_VERSION:
      return getInitialModuleVersion();
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
    case LMPackage.PROCESS__SYSTEM:
      setSystem((org.eclipse.emf.cdo.lm.System)newValue);
      return;
    case LMPackage.PROCESS__MODULE_TYPES:
      getModuleTypes().clear();
      getModuleTypes().addAll((Collection<? extends ModuleType>)newValue);
      return;
    case LMPackage.PROCESS__DROP_TYPES:
      getDropTypes().clear();
      getDropTypes().addAll((Collection<? extends DropType>)newValue);
      return;
    case LMPackage.PROCESS__MODULE_DEFINITION_PATH:
      setModuleDefinitionPath((String)newValue);
      return;
    case LMPackage.PROCESS__INITIAL_MODULE_VERSION:
      setInitialModuleVersion((Version)newValue);
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
    case LMPackage.PROCESS__SYSTEM:
      setSystem((org.eclipse.emf.cdo.lm.System)null);
      return;
    case LMPackage.PROCESS__MODULE_TYPES:
      getModuleTypes().clear();
      return;
    case LMPackage.PROCESS__DROP_TYPES:
      getDropTypes().clear();
      return;
    case LMPackage.PROCESS__MODULE_DEFINITION_PATH:
      setModuleDefinitionPath(MODULE_DEFINITION_PATH_EDEFAULT);
      return;
    case LMPackage.PROCESS__INITIAL_MODULE_VERSION:
      setInitialModuleVersion(INITIAL_MODULE_VERSION_EDEFAULT);
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
    case LMPackage.PROCESS__SYSTEM:
      return getSystem() != null;
    case LMPackage.PROCESS__MODULE_TYPES:
      return !getModuleTypes().isEmpty();
    case LMPackage.PROCESS__DROP_TYPES:
      return !getDropTypes().isEmpty();
    case LMPackage.PROCESS__MODULE_DEFINITION_PATH:
      return MODULE_DEFINITION_PATH_EDEFAULT == null ? getModuleDefinitionPath() != null : !MODULE_DEFINITION_PATH_EDEFAULT.equals(getModuleDefinitionPath());
    case LMPackage.PROCESS__INITIAL_MODULE_VERSION:
      return INITIAL_MODULE_VERSION_EDEFAULT == null ? getInitialModuleVersion() != null : !INITIAL_MODULE_VERSION_EDEFAULT.equals(getInitialModuleVersion());
    }
    return super.eIsSet(featureID);
  }

  @Override
  public DropType getDropType(String name)
  {
    for (DropType dropType : getDropTypes())
    {
      if (Objects.equals(dropType.getName(), name))
      {
        return dropType;
      }
    }

    return null;
  }

} // ProcessImpl
