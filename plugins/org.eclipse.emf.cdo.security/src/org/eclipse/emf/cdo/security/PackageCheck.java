/**
 */
package org.eclipse.emf.cdo.security;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EPackage;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Package Check</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.security.PackageCheck#getPackages <em>Packages</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.security.SecurityPackage#getPackageCheck()
 * @model
 * @generated
 */
public interface PackageCheck extends Check
{
  /**
   * Returns the value of the '<em><b>Packages</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.ecore.EPackage}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Packages</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Packages</em>' reference list.
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getPackageCheck_Packages()
   * @model required="true"
   * @generated
   */
  EList<EPackage> getPackages();

} // PackageCheck
