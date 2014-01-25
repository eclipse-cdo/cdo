/**
 */
package org.eclipse.emf.cdo.releng.setup;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Targlet Container</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.TargletContainer#getTarglets <em>Targlets</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getTargletContainer()
 * @model
 * @generated
 */
public interface TargletContainer extends EObject
{
  /**
   * Returns the value of the '<em><b>Targlets</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.releng.setup.Targlet}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Targlets</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Targlets</em>' containment reference list.
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getTargletContainer_Targlets()
   * @model containment="true" resolveProxies="true"
   * @generated
   */
  EList<Targlet> getTarglets();

} // TargletContainer
