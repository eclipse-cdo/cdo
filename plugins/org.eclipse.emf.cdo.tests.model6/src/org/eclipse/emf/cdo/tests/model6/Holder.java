/**
 */
package org.eclipse.emf.cdo.tests.model6;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Holder</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.Holder#getHeld <em>Held</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.Holder#getOwned <em>Owned</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.tests.model6.legacy.Model6Package#getHolder()
 * @model
 * @generated
 */
public interface Holder extends Holdable
{
  /**
   * Returns the value of the '<em><b>Held</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.model6.Holdable}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Held</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Held</em>' reference list.
   * @see org.eclipse.emf.cdo.tests.model6.legacy.Model6Package#getHolder_Held()
   * @model transient="true" derived="true"
   *        annotation="http://www.eclipse.org/emf/CDO persistent='true' filter='owned'"
   * @generated
   */
  EList<Holdable> getHeld();

  /**
   * Returns the value of the '<em><b>Owned</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.model6.Holdable}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Owned</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Owned</em>' containment reference list.
   * @see org.eclipse.emf.cdo.tests.model6.legacy.Model6Package#getHolder_Owned()
   * @model containment="true" resolveProxies="true"
   * @generated
   */
  EList<Holdable> getOwned();

} // Holder
