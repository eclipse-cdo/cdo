/**
 */
package org.eclipse.emf.cdo.releng.setup;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Install Task</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.P2Task#getP2Repositories <em>P2 Repositories</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.P2Task#getInstallableUnits <em>Installable Units</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getP2Task()
 * @model
 * @generated
 */
public interface P2Task extends SetupTask
{
  /**
   * Returns the value of the '<em><b>Installable Units</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.releng.setup.InstallableUnit}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.releng.setup.InstallableUnit#getP2Task <em>P2 Task</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Installable Units</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Installable Units</em>' containment reference list.
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getP2Task_InstallableUnits()
   * @see org.eclipse.emf.cdo.releng.setup.InstallableUnit#getP2Task
   * @model opposite="p2Task" containment="true" resolveProxies="true" required="true"
   * @generated
   */
  EList<InstallableUnit> getInstallableUnits();

  /**
   * Returns the value of the '<em><b>P2 Repositories</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.releng.setup.P2Repository}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.releng.setup.P2Repository#getP2Task <em>P2 Task</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>P2 Repositories</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>P2 Repositories</em>' containment reference list.
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getP2Task_P2Repositories()
   * @see org.eclipse.emf.cdo.releng.setup.P2Repository#getP2Task
   * @model opposite="p2Task" containment="true" resolveProxies="true" required="true"
   * @generated
   */
  EList<P2Repository> getP2Repositories();

} // InstallTask
