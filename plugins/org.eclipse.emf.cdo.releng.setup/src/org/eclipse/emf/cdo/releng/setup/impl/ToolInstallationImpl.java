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

import org.eclipse.emf.cdo.releng.setup.DirectorCall;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.ToolInstallation;

import org.eclipse.emf.cdo.releng.setup.ToolPreference;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import java.util.Collection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Tool Installation</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.ToolInstallationImpl#getDirectorCalls <em>Director Calls</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.ToolInstallationImpl#getToolPreferences <em>Tool Preferences</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ToolInstallationImpl extends MinimalEObjectImpl.Container implements ToolInstallation
{
  /**
   * The cached value of the '{@link #getDirectorCalls() <em>Director Calls</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getDirectorCalls()
   * @generated
   * @ordered
   */
  protected EList<DirectorCall> directorCalls;

  /**
   * The cached value of the '{@link #getToolPreferences() <em>Tool Preferences</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getToolPreferences()
   * @generated
   * @ordered
   */
  protected EList<ToolPreference> toolPreferences;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ToolInstallationImpl()
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
    return SetupPackage.Literals.TOOL_INSTALLATION;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<DirectorCall> getDirectorCalls()
  {
    if (directorCalls == null)
    {
      directorCalls = new EObjectContainmentEList<DirectorCall>(DirectorCall.class, this,
          SetupPackage.TOOL_INSTALLATION__DIRECTOR_CALLS);
    }
    return directorCalls;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<ToolPreference> getToolPreferences()
  {
    if (toolPreferences == null)
    {
      toolPreferences = new EObjectContainmentEList<ToolPreference>(ToolPreference.class, this,
          SetupPackage.TOOL_INSTALLATION__TOOL_PREFERENCES);
    }
    return toolPreferences;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case SetupPackage.TOOL_INSTALLATION__DIRECTOR_CALLS:
      return ((InternalEList<?>)getDirectorCalls()).basicRemove(otherEnd, msgs);
    case SetupPackage.TOOL_INSTALLATION__TOOL_PREFERENCES:
      return ((InternalEList<?>)getToolPreferences()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
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
    case SetupPackage.TOOL_INSTALLATION__DIRECTOR_CALLS:
      return getDirectorCalls();
    case SetupPackage.TOOL_INSTALLATION__TOOL_PREFERENCES:
      return getToolPreferences();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case SetupPackage.TOOL_INSTALLATION__DIRECTOR_CALLS:
      getDirectorCalls().clear();
      getDirectorCalls().addAll((Collection<? extends DirectorCall>)newValue);
      return;
    case SetupPackage.TOOL_INSTALLATION__TOOL_PREFERENCES:
      getToolPreferences().clear();
      getToolPreferences().addAll((Collection<? extends ToolPreference>)newValue);
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
    case SetupPackage.TOOL_INSTALLATION__DIRECTOR_CALLS:
      getDirectorCalls().clear();
      return;
    case SetupPackage.TOOL_INSTALLATION__TOOL_PREFERENCES:
      getToolPreferences().clear();
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
    case SetupPackage.TOOL_INSTALLATION__DIRECTOR_CALLS:
      return directorCalls != null && !directorCalls.isEmpty();
    case SetupPackage.TOOL_INSTALLATION__TOOL_PREFERENCES:
      return toolPreferences != null && !toolPreferences.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} // ToolInstallationImpl
