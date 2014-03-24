/**
 */
package org.eclipse.emf.cdo.releng.setup;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Maven Import Task</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.MavenImportTask#getSourceLocators <em>Source Locators</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getMavenImportTask()
 * @model
 * @generated
 */
public interface MavenImportTask extends SetupTask
{
  /**
   * Returns the value of the '<em><b>Source Locators</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.releng.setup.AutomaticSourceLocator}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Source Locators</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Source Locators</em>' containment reference list.
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getMavenImportTask_SourceLocators()
   * @model containment="true" resolveProxies="true" required="true"
   * @generated
   */
  EList<AutomaticSourceLocator> getSourceLocators();

} // MavenImportTask
