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

import org.eclipse.emf.cdo.releng.setup.Configuration;
import org.eclipse.emf.cdo.releng.setup.Eclipse;
import org.eclipse.emf.cdo.releng.setup.Project;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;

import java.util.Collection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Configuration</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.ConfigurationImpl#getEclipseVersions <em>Eclipse Versions</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.ConfigurationImpl#getProjects <em>Projects</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ConfigurationImpl extends SetupTaskContainerImpl implements Configuration
{
  /**
   * The cached value of the '{@link #getEclipseVersions() <em>Eclipse Versions</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEclipseVersions()
   * @generated
   * @ordered
   */
  protected EList<Eclipse> eclipseVersions;

  /**
  	 * The cached value of the '{@link #getProjects() <em>Projects</em>}' containment reference list.
  	 * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
  	 * @see #getProjects()
  	 * @generated
  	 * @ordered
  	 */
  protected EList<Project> projects;

  /**
   * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
   * @generated
   */
  protected ConfigurationImpl()
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
    return SetupPackage.Literals.CONFIGURATION;
  }

  /**
   * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
   * @generated
   */
  public EList<Project> getProjects()
  {
    if (projects == null)
    {
      projects = new EObjectContainmentWithInverseEList.Resolving<Project>(Project.class, this,
          SetupPackage.CONFIGURATION__PROJECTS, SetupPackage.PROJECT__CONFIGURATION);
    }
    return projects;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Eclipse> getEclipseVersions()
  {
    if (eclipseVersions == null)
    {
      eclipseVersions = new EObjectContainmentWithInverseEList.Resolving<Eclipse>(Eclipse.class, this,
          SetupPackage.CONFIGURATION__ECLIPSE_VERSIONS, SetupPackage.ECLIPSE__CONFIGURATION);
    }
    return eclipseVersions;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case SetupPackage.CONFIGURATION__ECLIPSE_VERSIONS:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getEclipseVersions()).basicAdd(otherEnd, msgs);
    case SetupPackage.CONFIGURATION__PROJECTS:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getProjects()).basicAdd(otherEnd, msgs);
    }
    return super.eInverseAdd(otherEnd, featureID, msgs);
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
    case SetupPackage.CONFIGURATION__ECLIPSE_VERSIONS:
      return ((InternalEList<?>)getEclipseVersions()).basicRemove(otherEnd, msgs);
    case SetupPackage.CONFIGURATION__PROJECTS:
      return ((InternalEList<?>)getProjects()).basicRemove(otherEnd, msgs);
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
    case SetupPackage.CONFIGURATION__ECLIPSE_VERSIONS:
      return getEclipseVersions();
    case SetupPackage.CONFIGURATION__PROJECTS:
      return getProjects();
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
    case SetupPackage.CONFIGURATION__ECLIPSE_VERSIONS:
      getEclipseVersions().clear();
      getEclipseVersions().addAll((Collection<? extends Eclipse>)newValue);
      return;
    case SetupPackage.CONFIGURATION__PROJECTS:
      getProjects().clear();
      getProjects().addAll((Collection<? extends Project>)newValue);
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
    case SetupPackage.CONFIGURATION__ECLIPSE_VERSIONS:
      getEclipseVersions().clear();
      return;
    case SetupPackage.CONFIGURATION__PROJECTS:
      getProjects().clear();
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
    case SetupPackage.CONFIGURATION__ECLIPSE_VERSIONS:
      return eclipseVersions != null && !eclipseVersions.isEmpty();
    case SetupPackage.CONFIGURATION__PROJECTS:
      return projects != null && !projects.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} // ConfigurationImpl
