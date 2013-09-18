/**
 */
package org.eclipse.emf.cdo.security;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Combined Filter</b></em>'.
 * @since 4.3
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.security.CombinedFilter#getOperands <em>Operands</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.security.SecurityPackage#getCombinedFilter()
 * @model abstract="true"
 * @generated
 */
public interface CombinedFilter extends PermissionFilter
{
  /**
   * Returns the value of the '<em><b>Operands</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.security.PermissionFilter}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Operands</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Operands</em>' containment reference list.
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getCombinedFilter_Operands()
   * @model containment="true" required="true"
   * @generated
   */
  EList<PermissionFilter> getOperands();

} // CombinedFilter
