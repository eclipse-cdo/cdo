/**
 */
package org.eclipse.emf.cdo.security.impl;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.security.FilterPermission;
import org.eclipse.emf.cdo.security.PermissionFilter;
import org.eclipse.emf.cdo.security.SecurityPackage;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Filter Permission</b></em>'.
 * @since 4.3
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.FilterPermissionImpl#getFilters <em>Filters</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class FilterPermissionImpl extends PermissionImpl implements FilterPermission
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected FilterPermissionImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return SecurityPackage.Literals.FILTER_PERMISSION;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EList<PermissionFilter> getFilters()
  {
    return (EList<PermissionFilter>)eGet(SecurityPackage.Literals.FILTER_PERMISSION__FILTERS, true);
  }

  public boolean isApplicable(CDORevision revision, CDORevisionProvider revisionProvider, CDOBranchPoint securityContext)
  {
    for (PermissionFilter filter : getFilters())
    {
      if (!filter.isApplicable(revision, revisionProvider, securityContext))
      {
        return false;
      }
    }

    return true;
  }

} // FilterPermissionImpl
