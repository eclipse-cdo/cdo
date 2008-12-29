/**
 * <copyright>
 * </copyright>
 *
 * $Id: EGlobalPackageDefImpl.java,v 1.2 2008-12-29 14:01:19 estepper Exp $
 */
package org.eclipse.emf.cdo.cdodefs.impl;

import org.eclipse.emf.cdo.cdodefs.CDODefsPackage;
import org.eclipse.emf.cdo.cdodefs.EGlobalPackageDef;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object ' <em><b>Native CDO Package Def</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class EGlobalPackageDefImpl extends EPackageDefImpl implements EGlobalPackageDef
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected EGlobalPackageDefImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return CDODefsPackage.Literals.EGLOBAL_PACKAGE_DEF;
  }

  @Override
  public Object createInstance()
  {
    return EPackage.Registry.INSTANCE.getEPackage(getNsURI());
  }
}
