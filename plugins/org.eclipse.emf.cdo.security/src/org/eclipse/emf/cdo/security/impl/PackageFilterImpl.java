/**
 */
package org.eclipse.emf.cdo.security.impl;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.security.PackageFilter;
import org.eclipse.emf.cdo.security.SecurityPackage;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Package Filter</b></em>'.
 * @since 4.3
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.PackageFilterImpl#getApplicablePackage <em>Applicable Package</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class PackageFilterImpl extends PermissionFilterImpl implements PackageFilter
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected PackageFilterImpl()
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
    return SecurityPackage.Literals.PACKAGE_FILTER;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EPackage getApplicablePackage()
  {
    return (EPackage)eGet(SecurityPackage.Literals.PACKAGE_FILTER__APPLICABLE_PACKAGE, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setApplicablePackage(EPackage newApplicablePackage)
  {
    eSet(SecurityPackage.Literals.PACKAGE_FILTER__APPLICABLE_PACKAGE, newApplicablePackage);
  }

  public boolean isApplicable(CDORevision revision, CDORevisionProvider revisionProvider, CDOBranchPoint securityContext)
  {
    EPackage actualPackage = revision.getEClass().getEPackage();
    EPackage applicablePackage = getApplicablePackage();
    return actualPackage == applicablePackage;
  }

  public String format()
  {
    String label = "?";

    EPackage applicablePackage = getApplicablePackage();
    if (applicablePackage != null)
    {
      label = applicablePackage.getName();
    }

    return "package==" + label;
  }

} // PackageFilterImpl
