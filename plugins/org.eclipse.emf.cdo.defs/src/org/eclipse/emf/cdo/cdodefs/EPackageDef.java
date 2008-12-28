/**
 * <copyright>
 * </copyright>
 *
 * $Id: EPackageDef.java,v 1.1 2008-12-28 18:05:24 estepper Exp $
 */
package org.eclipse.emf.cdo.cdodefs;

import org.eclipse.net4j.util.net4jutildefs.Def;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>CDO Package Def</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.cdodefs.EPackageDef#getNsURI <em>Ns URI</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.cdodefs.CDODefsPackage#getEPackageDef()
 * @model abstract="true"
 * @generated
 */
public interface EPackageDef extends Def {
	/**
	 * Returns the value of the '<em><b>Ns URI</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Ns URI</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ns URI</em>' attribute.
	 * @see #setNsURI(String)
	 * @see org.eclipse.emf.cdo.cdodefs.CDODefsPackage#getEPackageDef_NsURI()
	 * @model required="true"
	 * @generated
	 */
	String getNsURI();

	/**
	 * Sets the value of the '{@link org.eclipse.emf.cdo.cdodefs.EPackageDef#getNsURI <em>Ns URI</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ns URI</em>' attribute.
	 * @see #getNsURI()
	 * @generated
	 */
	void setNsURI(String value);

} // CDOPackageDef
