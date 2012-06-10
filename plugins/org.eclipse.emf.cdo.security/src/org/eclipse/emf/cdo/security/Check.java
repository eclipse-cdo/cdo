/**
 */
package org.eclipse.emf.cdo.security;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Check</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.security.Check#getRole <em>Role</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.Check#getPermission <em>Permission</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.security.SecurityPackage#getCheck()
 * @model abstract="true"
 * @extends CDOObject
 * @generated
 */
public interface Check extends CDOObject
{
  /**
   * Returns the value of the '<em><b>Role</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.security.Role#getChecks <em>Checks</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Role</em>' container reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Role</em>' container reference.
   * @see #setRole(Role)
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getCheck_Role()
   * @see org.eclipse.emf.cdo.security.Role#getChecks
   * @model opposite="checks" required="true" transient="false"
   * @generated
   */
  Role getRole();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.security.Check#getRole <em>Role</em>}' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Role</em>' container reference.
   * @see #getRole()
   * @generated
   */
  void setRole(Role value);

  /**
   * Returns the value of the '<em><b>Permission</b></em>' attribute.
   * The default value is <code>"WRITE"</code>.
   * The literals are from the enumeration {@link org.eclipse.emf.cdo.security.Permission}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Permission</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Permission</em>' attribute.
   * @see org.eclipse.emf.cdo.security.Permission
   * @see #setPermission(Permission)
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getCheck_Permission()
   * @model default="WRITE" required="true"
   * @generated
   */
  Permission getPermission();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.security.Check#getPermission <em>Permission</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Permission</em>' attribute.
   * @see org.eclipse.emf.cdo.security.Permission
   * @see #getPermission()
   * @generated
   */
  void setPermission(Permission value);

  boolean isApplicable(CDORevision revision, CDORevisionProvider revisionProvider, CDOBranchPoint securityContext);

} // Check
