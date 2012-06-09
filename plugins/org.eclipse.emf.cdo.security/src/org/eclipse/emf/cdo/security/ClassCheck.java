/**
 */
package org.eclipse.emf.cdo.security;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Class Check</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.security.ClassCheck#getClasses <em>Classes</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.security.SecurityPackage#getClassCheck()
 * @model
 * @generated
 */
public interface ClassCheck extends Check
{
  /**
   * Returns the value of the '<em><b>Classes</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.ecore.EClass}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Classes</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Classes</em>' reference list.
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getClassCheck_Classes()
   * @model required="true"
   * @generated
   */
  EList<EClass> getClasses();

} // ClassCheck
