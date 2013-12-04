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
package org.eclipse.emf.cdo.releng.projectconfig.impl;

import org.eclipse.emf.cdo.releng.predicates.Predicate;
import org.eclipse.emf.cdo.releng.projectconfig.PreferenceFilter;
import org.eclipse.emf.cdo.releng.projectconfig.PreferenceProfile;
import org.eclipse.emf.cdo.releng.projectconfig.Project;
import org.eclipse.emf.cdo.releng.projectconfig.ProjectConfigPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Preference Profile</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.projectconfig.impl.PreferenceProfileImpl#getPreferenceFilters <em>Preference Filters</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.projectconfig.impl.PreferenceProfileImpl#getReferentProjects <em>Referent Projects</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.projectconfig.impl.PreferenceProfileImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.projectconfig.impl.PreferenceProfileImpl#getProject <em>Project</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.projectconfig.impl.PreferenceProfileImpl#getPredicates <em>Predicates</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.projectconfig.impl.PreferenceProfileImpl#getPrerequisites <em>Prerequisites</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class PreferenceProfileImpl extends MinimalEObjectImpl.Container implements PreferenceProfile
{
  /**
   * The cached value of the '{@link #getPreferenceFilters() <em>Preference Filters</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getPreferenceFilters()
   * @generated
   * @ordered
   */
  protected EList<PreferenceFilter> preferenceFilters;

  /**
   * The cached value of the '{@link #getReferentProjects() <em>Referent Projects</em>}' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getReferentProjects()
   * @generated
   * @ordered
   */
  protected EList<Project> referentProjects;

  /**
   * The default value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected static final String NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected String name = NAME_EDEFAULT;

  /**
   * The cached value of the '{@link #getPredicates() <em>Predicates</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getPredicates()
   * @generated
   * @ordered
   */
  protected EList<Predicate> predicates;

  /**
   * The cached value of the '{@link #getPrerequisites() <em>Prerequisites</em>}' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getPrerequisites()
   * @generated
   * @ordered
   */
  protected EList<PreferenceProfile> prerequisites;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected PreferenceProfileImpl()
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
    return ProjectConfigPackage.Literals.PREFERENCE_PROFILE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<PreferenceFilter> getPreferenceFilters()
  {
    if (preferenceFilters == null)
    {
      preferenceFilters = new EObjectContainmentWithInverseEList<PreferenceFilter>(PreferenceFilter.class, this,
          ProjectConfigPackage.PREFERENCE_PROFILE__PREFERENCE_FILTERS,
          ProjectConfigPackage.PREFERENCE_FILTER__PREFERENCE_PROFILE);
    }
    return preferenceFilters;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Project> getReferentProjects()
  {
    if (referentProjects == null)
    {
      referentProjects = new EObjectWithInverseResolvingEList.ManyInverse<Project>(Project.class, this,
          ProjectConfigPackage.PREFERENCE_PROFILE__REFERENT_PROJECTS,
          ProjectConfigPackage.PROJECT__PREFERENCE_PROFILE_REFERENCES);
    }
    return referentProjects;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getName()
  {
    return name;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setName(String newName)
  {
    String oldName = name;
    name = newName;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, ProjectConfigPackage.PREFERENCE_PROFILE__NAME, oldName,
          name));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Project getProject()
  {
    if (eContainerFeatureID() != ProjectConfigPackage.PREFERENCE_PROFILE__PROJECT)
    {
      return null;
    }
    return (Project)eInternalContainer();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetProject(Project newProject, NotificationChain msgs)
  {
    msgs = eBasicSetContainer((InternalEObject)newProject, ProjectConfigPackage.PREFERENCE_PROFILE__PROJECT, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setProject(Project newProject)
  {
    if (newProject != eInternalContainer() || eContainerFeatureID() != ProjectConfigPackage.PREFERENCE_PROFILE__PROJECT
        && newProject != null)
    {
      if (EcoreUtil.isAncestor(this, newProject))
      {
        throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
      }
      NotificationChain msgs = null;
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      if (newProject != null)
      {
        msgs = ((InternalEObject)newProject).eInverseAdd(this, ProjectConfigPackage.PROJECT__PREFERENCE_PROFILES,
            Project.class, msgs);
      }
      msgs = basicSetProject(newProject, msgs);
      if (msgs != null)
      {
        msgs.dispatch();
      }
    }
    else if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, ProjectConfigPackage.PREFERENCE_PROFILE__PROJECT,
          newProject, newProject));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Predicate> getPredicates()
  {
    if (predicates == null)
    {
      predicates = new EObjectContainmentEList<Predicate>(Predicate.class, this,
          ProjectConfigPackage.PREFERENCE_PROFILE__PREDICATES);
    }
    return predicates;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<PreferenceProfile> getPrerequisites()
  {
    if (prerequisites == null)
    {
      prerequisites = new EObjectResolvingEList<PreferenceProfile>(PreferenceProfile.class, this,
          ProjectConfigPackage.PREFERENCE_PROFILE__PREREQUISITES);
    }
    return prerequisites;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean requires(PreferenceProfile preferenceProfile)
  {
    return requires(new HashSet<PreferenceProfile>(), getPrerequisites(), preferenceProfile);
  }

  private boolean requires(Set<PreferenceProfile> visited, List<PreferenceProfile> preferenceProfiles,
      PreferenceProfile preferenceProfile)
  {
    for (PreferenceProfile requiredPreferenceProfile : preferenceProfiles)
    {
      if (requiredPreferenceProfile == preferenceProfile)
      {
        return true;
      }
      if (visited.add(requiredPreferenceProfile))
      {
        requires(visited, requiredPreferenceProfile.getPrerequisites(), requiredPreferenceProfile);
      }
    }
    return false;
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
    case ProjectConfigPackage.PREFERENCE_PROFILE__PREFERENCE_FILTERS:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getPreferenceFilters()).basicAdd(otherEnd, msgs);
    case ProjectConfigPackage.PREFERENCE_PROFILE__REFERENT_PROJECTS:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getReferentProjects()).basicAdd(otherEnd, msgs);
    case ProjectConfigPackage.PREFERENCE_PROFILE__PROJECT:
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      return basicSetProject((Project)otherEnd, msgs);
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
    case ProjectConfigPackage.PREFERENCE_PROFILE__PREFERENCE_FILTERS:
      return ((InternalEList<?>)getPreferenceFilters()).basicRemove(otherEnd, msgs);
    case ProjectConfigPackage.PREFERENCE_PROFILE__REFERENT_PROJECTS:
      return ((InternalEList<?>)getReferentProjects()).basicRemove(otherEnd, msgs);
    case ProjectConfigPackage.PREFERENCE_PROFILE__PROJECT:
      return basicSetProject(null, msgs);
    case ProjectConfigPackage.PREFERENCE_PROFILE__PREDICATES:
      return ((InternalEList<?>)getPredicates()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs)
  {
    switch (eContainerFeatureID())
    {
    case ProjectConfigPackage.PREFERENCE_PROFILE__PROJECT:
      return eInternalContainer().eInverseRemove(this, ProjectConfigPackage.PROJECT__PREFERENCE_PROFILES,
          Project.class, msgs);
    }
    return super.eBasicRemoveFromContainerFeature(msgs);
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
    case ProjectConfigPackage.PREFERENCE_PROFILE__PREFERENCE_FILTERS:
      return getPreferenceFilters();
    case ProjectConfigPackage.PREFERENCE_PROFILE__REFERENT_PROJECTS:
      return getReferentProjects();
    case ProjectConfigPackage.PREFERENCE_PROFILE__NAME:
      return getName();
    case ProjectConfigPackage.PREFERENCE_PROFILE__PROJECT:
      return getProject();
    case ProjectConfigPackage.PREFERENCE_PROFILE__PREDICATES:
      return getPredicates();
    case ProjectConfigPackage.PREFERENCE_PROFILE__PREREQUISITES:
      return getPrerequisites();
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
    case ProjectConfigPackage.PREFERENCE_PROFILE__PREFERENCE_FILTERS:
      getPreferenceFilters().clear();
      getPreferenceFilters().addAll((Collection<? extends PreferenceFilter>)newValue);
      return;
    case ProjectConfigPackage.PREFERENCE_PROFILE__REFERENT_PROJECTS:
      getReferentProjects().clear();
      getReferentProjects().addAll((Collection<? extends Project>)newValue);
      return;
    case ProjectConfigPackage.PREFERENCE_PROFILE__NAME:
      setName((String)newValue);
      return;
    case ProjectConfigPackage.PREFERENCE_PROFILE__PROJECT:
      setProject((Project)newValue);
      return;
    case ProjectConfigPackage.PREFERENCE_PROFILE__PREDICATES:
      getPredicates().clear();
      getPredicates().addAll((Collection<? extends Predicate>)newValue);
      return;
    case ProjectConfigPackage.PREFERENCE_PROFILE__PREREQUISITES:
      getPrerequisites().clear();
      getPrerequisites().addAll((Collection<? extends PreferenceProfile>)newValue);
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
    case ProjectConfigPackage.PREFERENCE_PROFILE__PREFERENCE_FILTERS:
      getPreferenceFilters().clear();
      return;
    case ProjectConfigPackage.PREFERENCE_PROFILE__REFERENT_PROJECTS:
      getReferentProjects().clear();
      return;
    case ProjectConfigPackage.PREFERENCE_PROFILE__NAME:
      setName(NAME_EDEFAULT);
      return;
    case ProjectConfigPackage.PREFERENCE_PROFILE__PROJECT:
      setProject((Project)null);
      return;
    case ProjectConfigPackage.PREFERENCE_PROFILE__PREDICATES:
      getPredicates().clear();
      return;
    case ProjectConfigPackage.PREFERENCE_PROFILE__PREREQUISITES:
      getPrerequisites().clear();
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
    case ProjectConfigPackage.PREFERENCE_PROFILE__PREFERENCE_FILTERS:
      return preferenceFilters != null && !preferenceFilters.isEmpty();
    case ProjectConfigPackage.PREFERENCE_PROFILE__REFERENT_PROJECTS:
      return referentProjects != null && !referentProjects.isEmpty();
    case ProjectConfigPackage.PREFERENCE_PROFILE__NAME:
      return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
    case ProjectConfigPackage.PREFERENCE_PROFILE__PROJECT:
      return getProject() != null;
    case ProjectConfigPackage.PREFERENCE_PROFILE__PREDICATES:
      return predicates != null && !predicates.isEmpty();
    case ProjectConfigPackage.PREFERENCE_PROFILE__PREREQUISITES:
      return prerequisites != null && !prerequisites.isEmpty();
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
    case ProjectConfigPackage.PREFERENCE_PROFILE___REQUIRES__PREFERENCEPROFILE:
      return requires((PreferenceProfile)arguments.get(0));
    }
    return super.eInvoke(operationID, arguments);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy())
    {
      return super.toString();
    }

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (name: ");
    result.append(name);
    result.append(')');
    return result.toString();
  }

} // PreferenceProfileImpl
