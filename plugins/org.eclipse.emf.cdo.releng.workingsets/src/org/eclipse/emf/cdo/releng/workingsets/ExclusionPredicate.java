/**
 */
package org.eclipse.emf.cdo.releng.workingsets;

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
 *   <li>{@link org.eclipse.emf.cdo.releng.workingsets.ExclusionPredicate#getExcludedWorkingSets <em>Excluded Working Sets</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.workingsets.WorkingSetsPackage#getExclusionPredicate()
 * @model
 * @generated
 */
public interface ExclusionPredicate extends Predicate
{
  /**
   * Returns the value of the '<em><b>Excluded Working Sets</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.releng.workingsets.WorkingSet}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Excluded Working Sets</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Excluded Working Sets</em>' reference list.
   * @see org.eclipse.emf.cdo.releng.workingsets.WorkingSetsPackage#getExclusionPredicate_ExcludedWorkingSets()
   * @model
   * @generated
   */
  EList<WorkingSet> getExcludedWorkingSets();

} // ExclusionPredicate
