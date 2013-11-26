/**
 */
package org.eclipse.emf.cdo.releng.setup;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Scope Root</b></em>'.
 * <!-- end-user-doc -->
 *
 *
 * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getScopeRoot()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface ScopeRoot extends SetupTaskContainer
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation" required="true"
   * @generated
   */
  SetupTaskScope getScope();

  /**
  	 * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
  	 * @model kind="operation"
  	 * @generated
  	 */
  ScopeRoot getParentScopeRoot();

} // ScopeRoot
