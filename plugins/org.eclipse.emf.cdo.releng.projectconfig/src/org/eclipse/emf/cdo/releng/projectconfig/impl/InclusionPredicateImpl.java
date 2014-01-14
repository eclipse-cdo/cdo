/*
 * Copyright (c) 2013, 2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.projectconfig.impl;

import org.eclipse.emf.cdo.releng.projectconfig.InclusionPredicate;
import org.eclipse.emf.cdo.releng.projectconfig.PreferenceProfile;
import org.eclipse.emf.cdo.releng.projectconfig.ProjectConfigPackage;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;

import org.eclipse.core.resources.IProject;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Inclusion Predicate</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.projectconfig.impl.InclusionPredicateImpl#getIncludedPreferenceProfiles <em>Included Preference Profiles</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class InclusionPredicateImpl extends MinimalEObjectImpl.Container implements InclusionPredicate
{
  /**
   * The cached value of the '{@link #getIncludedPreferenceProfiles() <em>Included Preference Profiles</em>}' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getIncludedPreferenceProfiles()
   * @generated
   * @ordered
   */
  protected EList<PreferenceProfile> includedPreferenceProfiles;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected InclusionPredicateImpl()
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
    return ProjectConfigPackage.Literals.INCLUSION_PREDICATE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<PreferenceProfile> getIncludedPreferenceProfiles()
  {
    if (includedPreferenceProfiles == null)
    {
      includedPreferenceProfiles = new EObjectResolvingEList<PreferenceProfile>(PreferenceProfile.class, this,
          ProjectConfigPackage.INCLUSION_PREDICATE__INCLUDED_PREFERENCE_PROFILES);
    }
    return includedPreferenceProfiles;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean matches(IProject project)
  {
    for (PreferenceProfile preferenceProfile : getIncludedPreferenceProfiles())
    {
      if (preferenceProfile.matches(project))
      {
        return true;
      }
    }

    return false;
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
    case ProjectConfigPackage.INCLUSION_PREDICATE__INCLUDED_PREFERENCE_PROFILES:
      return getIncludedPreferenceProfiles();
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
    case ProjectConfigPackage.INCLUSION_PREDICATE__INCLUDED_PREFERENCE_PROFILES:
      getIncludedPreferenceProfiles().clear();
      getIncludedPreferenceProfiles().addAll((Collection<? extends PreferenceProfile>)newValue);
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
    case ProjectConfigPackage.INCLUSION_PREDICATE__INCLUDED_PREFERENCE_PROFILES:
      getIncludedPreferenceProfiles().clear();
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
    case ProjectConfigPackage.INCLUSION_PREDICATE__INCLUDED_PREFERENCE_PROFILES:
      return includedPreferenceProfiles != null && !includedPreferenceProfiles.isEmpty();
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
    case ProjectConfigPackage.INCLUSION_PREDICATE___MATCHES__IPROJECT:
      return matches((IProject)arguments.get(0));
    }
    return super.eInvoke(operationID, arguments);
  }

} // InclusionPredicateImpl
