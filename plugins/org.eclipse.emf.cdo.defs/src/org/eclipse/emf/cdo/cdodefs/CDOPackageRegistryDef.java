/**
 * <copyright>
 * </copyright>
 *
 * $Id: CDOPackageRegistryDef.java,v 1.1 2008-12-28 18:05:24 estepper Exp $
 */
package org.eclipse.emf.cdo.cdodefs;

import org.eclipse.net4j.util.net4jutildefs.Def;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>CDO Package Registry Def</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.cdodefs.CDOPackageRegistryDef#getPackages <em>Packages</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.cdodefs.CDODefsPackage#getCDOPackageRegistryDef()
 * @model
 * @generated
 */
public interface CDOPackageRegistryDef extends Def {
	/**
	 * Returns the value of the '<em><b>Packages</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.emf.cdo.cdodefs.EPackageDef}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Packages</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Packages</em>' containment reference list.
	 * @see org.eclipse.emf.cdo.cdodefs.CDODefsPackage#getCDOPackageRegistryDef_Packages()
	 * @model containment="true"
	 * @generated
	 */
	EList<EPackageDef> getPackages();

} // CDOPackageRegistryDef
