/**
 * <copyright>
 * </copyright>
 *
 * $Id: MetaRefImpl.java,v 1.2 2008-09-18 12:57:20 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.model3.impl;

import org.eclipse.emf.cdo.tests.model3.MetaRef;
import org.eclipse.emf.cdo.tests.model3.Model3Package;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

/**
 * <!-- import org.eclipse.emf.ecore.EClass; import org.eclipse.emf.ecore.EPackage; begin-user-doc --> An implementation
 * of the model object '<em><b>Meta Ref</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model3.impl.MetaRefImpl#getEPackageRef <em>EPackage Ref</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class MetaRefImpl extends CDOObjectImpl implements MetaRef
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected MetaRefImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return Model3Package.Literals.META_REF;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected int eStaticFeatureCount()
  {
    return 0;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EPackage getEPackageRef()
  {
    return (EPackage)eGet(Model3Package.Literals.META_REF__EPACKAGE_REF, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setEPackageRef(EPackage newEPackageRef)
  {
    eSet(Model3Package.Literals.META_REF__EPACKAGE_REF, newEPackageRef);
  }

} // MetaRefImpl
