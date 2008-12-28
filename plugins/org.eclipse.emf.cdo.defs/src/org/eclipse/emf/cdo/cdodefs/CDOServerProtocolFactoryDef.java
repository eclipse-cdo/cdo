/**
 * <copyright>
 * </copyright>
 *
 * $Id: CDOServerProtocolFactoryDef.java,v 1.1 2008-12-28 18:05:24 estepper Exp $
 */
package org.eclipse.emf.cdo.cdodefs;

import org.eclipse.net4j.net4jdefs.ServerProtocolFactoryDef;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>CDO Server Protocol Factory Def</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.cdodefs.CDOServerProtocolFactoryDef#getRepositoryProviderDef <em>Repository Provider Def</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.cdodefs.CDODefsPackage#getCDOServerProtocolFactoryDef()
 * @model
 * @generated
 */
public interface CDOServerProtocolFactoryDef extends ServerProtocolFactoryDef {

	/**
	 * Returns the value of the '<em><b>Repository Provider Def</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Repository Provider Def</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Repository Provider Def</em>' reference.
	 * @see #setRepositoryProviderDef(RepositoryProviderDef)
	 * @see org.eclipse.emf.cdo.cdodefs.CDODefsPackage#getCDOServerProtocolFactoryDef_RepositoryProviderDef()
	 * @model required="true"
	 * @generated
	 */
	RepositoryProviderDef getRepositoryProviderDef();

	/**
	 * Sets the value of the '{@link org.eclipse.emf.cdo.cdodefs.CDOServerProtocolFactoryDef#getRepositoryProviderDef <em>Repository Provider Def</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Repository Provider Def</em>' reference.
	 * @see #getRepositoryProviderDef()
	 * @generated
	 */
	void setRepositoryProviderDef(RepositoryProviderDef value);
} // CDOServerProtocolFactoryDef
