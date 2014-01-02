/**
 */
package org.eclipse.emf.cdo.releng.projectconfig.impl;

import org.eclipse.emf.cdo.releng.projectconfig.ExclusionPredicate;
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
 * An implementation of the model object '<em><b>Exclusion Predicate</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.projectconfig.impl.ExclusionPredicateImpl#getExcludedPreferenceProfiles <em>Excluded Preference Profiles</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ExclusionPredicateImpl extends MinimalEObjectImpl.Container implements ExclusionPredicate
{
  /**
   * The cached value of the '{@link #getExcludedPreferenceProfiles() <em>Excluded Preference Profiles</em>}' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getExcludedPreferenceProfiles()
   * @generated
   * @ordered
   */
  protected EList<PreferenceProfile> excludedPreferenceProfiles;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ExclusionPredicateImpl()
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
    return ProjectConfigPackage.Literals.EXCLUSION_PREDICATE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<PreferenceProfile> getExcludedPreferenceProfiles()
  {
    if (excludedPreferenceProfiles == null)
    {
      excludedPreferenceProfiles = new EObjectResolvingEList<PreferenceProfile>(PreferenceProfile.class, this,
          ProjectConfigPackage.EXCLUSION_PREDICATE__EXCLUDED_PREFERENCE_PROFILES);
    }
    return excludedPreferenceProfiles;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public boolean matches(IProject project)
  {
    for (PreferenceProfile preferenceProfile : getExcludedPreferenceProfiles())
    {
      if (preferenceProfile.matches(project))
      {
        return false;
      }
    }

    return true;
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
    case ProjectConfigPackage.EXCLUSION_PREDICATE__EXCLUDED_PREFERENCE_PROFILES:
      return getExcludedPreferenceProfiles();
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
    case ProjectConfigPackage.EXCLUSION_PREDICATE__EXCLUDED_PREFERENCE_PROFILES:
      getExcludedPreferenceProfiles().clear();
      getExcludedPreferenceProfiles().addAll((Collection<? extends PreferenceProfile>)newValue);
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
    case ProjectConfigPackage.EXCLUSION_PREDICATE__EXCLUDED_PREFERENCE_PROFILES:
      getExcludedPreferenceProfiles().clear();
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
    case ProjectConfigPackage.EXCLUSION_PREDICATE__EXCLUDED_PREFERENCE_PROFILES:
      return excludedPreferenceProfiles != null && !excludedPreferenceProfiles.isEmpty();
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
    case ProjectConfigPackage.EXCLUSION_PREDICATE___MATCHES__IPROJECT:
      return matches((IProject)arguments.get(0));
    }
    return super.eInvoke(operationID, arguments);
  }

} // ExclusionPredicateImpl
