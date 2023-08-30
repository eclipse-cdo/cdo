/**
 */
package org.eclipse.emf.cdo.lm.security;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Module Filter</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.security.ModuleFilter#getModuleName <em>Module Name</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.lm.security.LMSecurityPackage#getModuleFilter()
 * @model
 * @generated
 */
public interface ModuleFilter extends LMFilter
{
  /**
   * Returns the value of the '<em><b>Module Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Module Name</em>' attribute.
   * @see #setModuleName(String)
   * @see org.eclipse.emf.cdo.lm.security.LMSecurityPackage#getModuleFilter_ModuleName()
   * @model
   * @generated
   */
  String getModuleName();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.security.ModuleFilter#getModuleName <em>Module Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Module Name</em>' attribute.
   * @see #getModuleName()
   * @generated
   */
  void setModuleName(String value);

} // ModuleFilter
