/**
 */
package org.eclipse.emf.cdo.releng.predicates;

import org.eclipse.core.resources.IProject;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Predicate</b></em>'.
 * <!-- end-user-doc -->
 *
 *
 * @see org.eclipse.emf.cdo.releng.predicates.PredicatesPackage#getPredicate()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface Predicate extends EObject
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model projectDataType="org.eclipse.emf.cdo.releng.predicates.Project"
   * @generated
   */
  boolean matches(IProject project);

} // Predicate
