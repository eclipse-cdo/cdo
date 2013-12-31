/**
 */
package org.eclipse.emf.cdo.releng.projectconfig;

import org.eclipse.emf.cdo.releng.predicates.Predicate;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Exclusion Predicate</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.projectconfig.ExclusionPredicate#getExcludedPreferenceProfiles <em>Excluded Preference Profiles</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.projectconfig.ProjectConfigPackage#getExclusionPredicate()
 * @model
 * @generated
 */
public interface ExclusionPredicate extends Predicate
{
  /**
   * Returns the value of the '<em><b>Excluded Preference Profiles</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.releng.projectconfig.PreferenceProfile}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Excluded Preference Profiles</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Excluded Preference Profiles</em>' reference list.
   * @see org.eclipse.emf.cdo.releng.projectconfig.ProjectConfigPackage#getExclusionPredicate_ExcludedPreferenceProfiles()
   * @model
   * @generated
   */
  EList<PreferenceProfile> getExcludedPreferenceProfiles();

} // ExclusionPredicate
