/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package testmodel1;


import org.eclipse.emf.cdo.client.CDOPersistent;

import org.eclipse.emf.common.util.EList;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Root</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link testmodel1.Root#getChildren <em>Children</em>}</li>
 * </ul>
 * </p>
 *
 * @see testmodel1.TestModel1Package#getRoot()
 * @model
 * @generated
 */
public interface Root extends CDOPersistent
{
  /**
   * Returns the value of the '<em><b>Children</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.client.CDOPersistent}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Children</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Children</em>' containment reference list.
   * @see testmodel1.TestModel1Package#getRoot_Children()
   * @model type="org.eclipse.emf.cdo.client.CDOPersistent" containment="true"
   * @generated
   */
  EList getChildren();

} // Root