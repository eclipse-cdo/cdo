/**
 * <copyright>
 * </copyright>
 *
 * $Id: Net4JUIDefsFactory.java,v 1.1 2008-12-28 18:07:29 estepper Exp $
 */
package org.eclipse.net4j.net4juidefs;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.net4j.net4juidefs.Net4JUIDefsPackage
 * @generated
 */
public interface Net4JUIDefsFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	Net4JUIDefsFactory eINSTANCE = org.eclipse.net4j.net4juidefs.impl.Net4JUIDefsFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Interactive Credentials Provider Def</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Interactive Credentials Provider Def</em>'.
	 * @generated
	 */
	InteractiveCredentialsProviderDef createInteractiveCredentialsProviderDef();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	Net4JUIDefsPackage getNet4JUIDefsPackage();

} //Net4JUIDefsFactory
