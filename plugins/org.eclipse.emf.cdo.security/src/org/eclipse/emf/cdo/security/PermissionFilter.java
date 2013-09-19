/**
 */
package org.eclipse.emf.cdo.security;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Permission Filter</b></em>'.
 * @since 4.3
 * <!-- end-user-doc -->
 *
 *
 * @see org.eclipse.emf.cdo.security.SecurityPackage#getPermissionFilter()
 * @model abstract="true"
 * @extends CDOObject
 * @generated
 */
public interface PermissionFilter extends CDOObject
{
  boolean isApplicable(CDORevision revision, CDORevisionProvider revisionProvider, CDOBranchPoint securityContext,
      int level) throws Exception;

  String format();

} // PermissionFilter
