/**
 * <copyright>
 * </copyright>
 *
 * $Id: model4interfacesFactory.java,v 1.1 2008-07-10 15:42:28 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.model4interfaces;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.tests.model4interfaces.model4interfacesPackage
 * @generated
 */
public interface model4interfacesFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	model4interfacesFactory eINSTANCE = org.eclipse.emf.cdo.tests.model4interfaces.impl.model4interfacesFactoryImpl.init();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	model4interfacesPackage getmodel4interfacesPackage();

} //model4interfacesFactory
