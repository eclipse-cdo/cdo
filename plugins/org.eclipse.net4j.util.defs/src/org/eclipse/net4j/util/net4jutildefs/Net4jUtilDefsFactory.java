/**
 * <copyright>
 * </copyright>
 *
 * $Id: Net4jUtilDefsFactory.java,v 1.1 2008-12-28 18:07:29 estepper Exp $
 */
package org.eclipse.net4j.util.net4jutildefs;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.net4j.util.net4jutildefs.Net4jUtilDefsPackage
 * @generated
 */
public interface Net4jUtilDefsFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	Net4jUtilDefsFactory eINSTANCE = org.eclipse.net4j.util.net4jutildefs.impl.Net4jUtilDefsFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Defs Container</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Defs Container</em>'.
	 * @generated
	 */
	DefsContainer createDefsContainer();

	/**
	 * Returns a new object of class '<em>Thread Pool Def</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Thread Pool Def</em>'.
	 * @generated
	 */
	ThreadPoolDef createThreadPoolDef();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	Net4jUtilDefsPackage getNet4jUtilDefsPackage();

} //Net4jUtilDefsFactory
