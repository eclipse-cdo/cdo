/**
 */
package org.eclipse.emf.cdo.security.impl;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.security.OrFilter;
import org.eclipse.emf.cdo.security.PermissionFilter;
import org.eclipse.emf.cdo.security.SecurityPackage;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Or Filter</b></em>'.
 * @since 4.3
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class OrFilterImpl extends CombinedFilterImpl implements OrFilter
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected OrFilterImpl()
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
    return SecurityPackage.Literals.OR_FILTER;
  }

  public boolean isApplicable(CDORevision revision, CDORevisionProvider revisionProvider, CDOBranchPoint securityContext)
  {
    for (PermissionFilter operand : getOperands())
    {
      if (operand.isApplicable(revision, revisionProvider, securityContext))
      {
        return true;
      }
    }

    return false;
  }

  @Override
  protected String formatOperator()
  {
    return "Or";
  }

} // OrFilterImpl
