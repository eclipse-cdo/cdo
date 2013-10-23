/**
 */
package org.eclipse.emf.cdo.releng.workingsets;

import org.eclipse.emf.cdo.releng.predicates.Predicate;
import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Inclusion Predicate</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.workingsets.InclusionPredicate#getIncludedWorkingSets <em>Included Working Sets</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.workingsets.WorkingSetsPackage#getInclusionPredicate()
 * @model
 * @generated
 */
public interface InclusionPredicate extends Predicate
{
  /**
   * Returns the value of the '<em><b>Included Working Sets</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.releng.workingsets.WorkingSet}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Included Working Sets</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Included Working Sets</em>' reference list.
   * @see org.eclipse.emf.cdo.releng.workingsets.WorkingSetsPackage#getInclusionPredicate_IncludedWorkingSets()
   * @model
   * @generated
   */
  EList<WorkingSet> getIncludedWorkingSets();

} // InclusionPredicate
