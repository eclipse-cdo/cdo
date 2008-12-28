/**
 * <copyright>
 * </copyright>
 *
 * $Id: EDynamicPackageDef.java,v 1.1 2008-12-28 18:05:24 estepper Exp $
 */
package org.eclipse.emf.cdo.cdodefs;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Dynamic CDO Package Def</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.cdodefs.EDynamicPackageDef#getResourceURI <em>Resource URI</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.cdodefs.CDODefsPackage#getEDynamicPackageDef()
 * @model
 * @generated
 */
public interface EDynamicPackageDef extends EPackageDef {
	/**
	 * Returns the value of the '<em><b>Resource URI</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Resource URI</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Resource URI</em>' attribute.
	 * @see #setResourceURI(String)
	 * @see org.eclipse.emf.cdo.cdodefs.CDODefsPackage#getEDynamicPackageDef_ResourceURI()
	 * @model required="true"
	 * @generated
	 */
	String getResourceURI();

	/**
	 * Sets the value of the '{@link org.eclipse.emf.cdo.cdodefs.EDynamicPackageDef#getResourceURI <em>Resource URI</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Resource URI</em>' attribute.
	 * @see #getResourceURI()
	 * @generated
	 */
	void setResourceURI(String value);

} // DynamicCDOPackageDef
