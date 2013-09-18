/**
 */
package org.eclipse.emf.cdo.security.impl;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.security.NotFilter;
import org.eclipse.emf.cdo.security.PermissionFilter;
import org.eclipse.emf.cdo.security.SecurityPackage;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Not Filter</b></em>'.
 * @since 4.3
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class NotFilterImpl extends CombinedFilterImpl implements NotFilter
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected NotFilterImpl()
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
    return SecurityPackage.Literals.NOT_FILTER;
  }

  public boolean isApplicable(CDORevision revision, CDORevisionProvider revisionProvider, CDOBranchPoint securityContext)
  {
    PermissionFilter operand = getOperands().get(0);
    return !operand.isApplicable(revision, revisionProvider, securityContext);
  }

  @Override
  protected String formatOperator()
  {
    return "Not";
  }

} // NotFilterImpl
