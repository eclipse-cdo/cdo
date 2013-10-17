/**
 */
package org.eclipse.emf.cdo.releng.setup;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Task Container</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.SetupTaskContainer#getSetupTasks <em>Setup Tasks</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getSetupTaskContainer()
 * @model abstract="true"
 * @generated
 */
public interface SetupTaskContainer extends EObject
{
  /**
   * Returns the value of the '<em><b>Setup Tasks</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.releng.setup.SetupTask}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Setup Tasks</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Setup Tasks</em>' containment reference list.
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getSetupTaskContainer_SetupTasks()
   * @model containment="true" resolveProxies="true"
   * @generated
   */
  EList<SetupTask> getSetupTasks();

} // SetupTaskContainer
