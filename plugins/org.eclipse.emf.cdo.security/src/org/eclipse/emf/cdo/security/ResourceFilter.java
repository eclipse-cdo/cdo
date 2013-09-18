/**
 */
package org.eclipse.emf.cdo.security;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Resource Filter</b></em>'.
 * @since 4.3
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.security.ResourceFilter#getPath <em>Path</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.ResourceFilter#getInclusion <em>Inclusion</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.security.SecurityPackage#getResourceFilter()
 * @model
 * @generated
 */
public interface ResourceFilter extends PermissionFilter
{
  /**
   * Returns the value of the '<em><b>Path</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Path</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Path</em>' attribute.
   * @see #setPath(String)
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getResourceFilter_Path()
   * @model
   * @generated
   */
  String getPath();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.security.ResourceFilter#getPath <em>Path</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Path</em>' attribute.
   * @see #getPath()
   * @generated
   */
  void setPath(String value);

  /**
   * Returns the value of the '<em><b>Inclusion</b></em>' attribute.
   * The default value is <code>"Regex"</code>.
   * The literals are from the enumeration {@link org.eclipse.emf.cdo.security.Inclusion}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Inclusion</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Inclusion</em>' attribute.
   * @see org.eclipse.emf.cdo.security.Inclusion
   * @see #setInclusion(Inclusion)
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getResourceFilter_Inclusion()
   * @model default="Regex"
   * @generated
   */
  Inclusion getInclusion();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.security.ResourceFilter#getInclusion <em>Inclusion</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Inclusion</em>' attribute.
   * @see org.eclipse.emf.cdo.security.Inclusion
   * @see #getInclusion()
   * @generated
   */
  void setInclusion(Inclusion value);

} // ResourceFilter
