/**
 */
package org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestPackage
 * @generated
 */
public interface HibernateTestFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	HibernateTestFactory eINSTANCE = org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.HibernateTestFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Bz356181 Main</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Bz356181 Main</em>'.
	 * @generated
	 */
	Bz356181_Main createBz356181_Main();

	/**
	 * Returns a new object of class '<em>Bz356181 Transient</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Bz356181 Transient</em>'.
	 * @generated
	 */
	Bz356181_Transient createBz356181_Transient();

	/**
	 * Returns a new object of class '<em>Bz356181 Non Transient</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Bz356181 Non Transient</em>'.
	 * @generated
	 */
	Bz356181_NonTransient createBz356181_NonTransient();

	/**
	 * Returns a new object of class '<em>Bz387752 Main</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Bz387752 Main</em>'.
	 * @generated
	 */
	Bz387752_Main createBz387752_Main();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	HibernateTestPackage getHibernateTestPackage();

} //HibernateTestFactory
