/**
 */
package org.eclipse.emf.cdo.releng.setup;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Top Level Element</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.TopLevelElement#getToolVersion <em>Tool Version</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getTopLevelElement()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface TopLevelElement extends EObject
{
  /**
   * Returns the value of the '<em><b>Tool Version</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Tool Version</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Tool Version</em>' attribute.
   * @see #setToolVersion(int)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getTopLevelElement_ToolVersion()
   * @model required="true"
   * @generated
   */
  int getToolVersion();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.TopLevelElement#getToolVersion <em>Tool Version</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Tool Version</em>' attribute.
   * @see #getToolVersion()
   * @generated
   */
  void setToolVersion(int value);

} // TopLevelElement
