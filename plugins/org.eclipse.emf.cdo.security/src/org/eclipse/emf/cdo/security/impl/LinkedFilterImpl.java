/**
 */
package org.eclipse.emf.cdo.security.impl;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.security.LinkedFilter;
import org.eclipse.emf.cdo.security.PermissionFilter;
import org.eclipse.emf.cdo.security.SecurityPackage;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Reference Filter</b></em>'.
 * @since 4.3
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.LinkedFilterImpl#getFilter <em>Filter</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class LinkedFilterImpl extends PermissionFilterImpl implements LinkedFilter
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected LinkedFilterImpl()
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
    return SecurityPackage.Literals.LINKED_FILTER;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PermissionFilter getFilter()
  {
    return (PermissionFilter)eGet(SecurityPackage.Literals.LINKED_FILTER__FILTER, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setFilter(PermissionFilter newFilter)
  {
    eSet(SecurityPackage.Literals.LINKED_FILTER__FILTER, newFilter);
  }

  public boolean isApplicable(CDORevision revision, CDORevisionProvider revisionProvider, CDOBranchPoint securityContext)
  {
    return getFilter().isApplicable(revision, revisionProvider, securityContext);
  }

  public String format()
  {
    PermissionFilter filter = getFilter();
    if (filter == null)
    {
      return "?";
    }

    return filter.format();
  }
} // ReferenceFilterImpl
