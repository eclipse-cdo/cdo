/**
 * <copyright>
 * </copyright>
 *
 * $Id: CDOLazyPackageRegistryDefImpl.java,v 1.1 2008-12-28 18:05:25 estepper Exp $
 */
package org.eclipse.emf.cdo.cdodefs.impl;

import org.eclipse.emf.cdo.cdodefs.CDODefsPackage;
import org.eclipse.emf.cdo.cdodefs.CDOLazyPackageRegistryDef;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.internal.cdo.util.CDOPackageRegistryImpl.Lazy;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Lazy Package Registry Def</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class CDOLazyPackageRegistryDefImpl extends CDOPackageRegistryDefImpl implements CDOLazyPackageRegistryDef
{
  /**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
  protected CDOLazyPackageRegistryDefImpl()
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
		return CDODefsPackage.Literals.CDO_LAZY_PACKAGE_REGISTRY_DEF;
	}

  /**
   * Creates a {@link Lazy}.
   * 
   * @return the CDO package registry
   */
  protected Object createInstance()
  {
    return addPackages(CDOUtil.createLazyPackageRegistry());
  }
} // LazyPackageRegistryDefImpl
