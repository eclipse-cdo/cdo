/**
 * <copyright>
 * </copyright>
 *
 * $Id: CDOEagerPackageRegistryDefImpl.java,v 1.2 2008-12-29 14:01:19 estepper Exp $
 */
package org.eclipse.emf.cdo.cdodefs.impl;

import org.eclipse.emf.cdo.cdodefs.CDODefsPackage;
import org.eclipse.emf.cdo.cdodefs.CDOEagerPackageRegistryDef;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.internal.cdo.util.CDOPackageRegistryImpl.Eager;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Eager Package Registry Def</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class CDOEagerPackageRegistryDefImpl extends CDOPackageRegistryDefImpl implements CDOEagerPackageRegistryDef
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected CDOEagerPackageRegistryDefImpl()
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
    return CDODefsPackage.Literals.CDO_EAGER_PACKAGE_REGISTRY_DEF;
  }

  /**
   * Creates a {@link Eager}.
   * 
   * @return the CDO package registry
   */
  protected Object createInstance()
  {
    return addPackages(CDOUtil.createEagerPackageRegistry());
  }

} // EagerPackageRegistryDefImpl
