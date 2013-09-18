/**
 */
package org.eclipse.emf.cdo.security.impl;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.internal.security.ViewUtil;
import org.eclipse.emf.cdo.security.ObjectFilter;
import org.eclipse.emf.cdo.security.SecurityPackage;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Object Filter</b></em>'.
 * @since 4.3
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public abstract class ObjectFilterImpl extends PermissionFilterImpl implements ObjectFilter
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ObjectFilterImpl()
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
    return SecurityPackage.Literals.OBJECT_FILTER;
  }

  protected CDOView getView(CDORevisionProvider revisionProvider)
  {
    return ViewUtil.getView(revisionProvider);
  }

  /**
   * @ADDED
   */
  public boolean isApplicable(CDORevision revision, CDORevisionProvider revisionProvider, CDOBranchPoint securityContext)
  {
    CDOView view = getView(revisionProvider);
    CDOID id = revision.getID();

    CDOObject object = view.getObject(id);
    return isApplicable(object, securityContext);
  }

  protected abstract boolean isApplicable(CDOObject object, CDOBranchPoint securityContext);

} // ObjectFilterImpl
