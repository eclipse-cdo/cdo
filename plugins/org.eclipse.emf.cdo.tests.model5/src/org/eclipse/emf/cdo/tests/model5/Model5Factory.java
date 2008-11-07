/**
 * <copyright>
 * </copyright>
 *
 * $Id: Model5Factory.java,v 1.1 2008-11-07 02:50:07 smcduff Exp $
 */
package org.eclipse.emf.cdo.tests.model5;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a create method for each non-abstract class of
 * the model. <!-- end-user-doc -->
 * 
 * @see org.eclipse.emf.cdo.tests.model5.Model5Package
 * @generated
 */
public interface Model5Factory extends EFactory
{
  /**
   * The singleton instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  Model5Factory eINSTANCE = org.eclipse.emf.cdo.tests.model5.impl.Model5FactoryImpl.init();

  /**
   * Returns a new object of class '<em>Test Feature Map</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Test Feature Map</em>'.
   * @generated
   */
  TestFeatureMap createTestFeatureMap();

  /**
   * Returns a new object of class '<em>Manager</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Manager</em>'.
   * @generated
   */
  Manager createManager();

  /**
   * Returns a new object of class '<em>Doctor</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Doctor</em>'.
   * @generated
   */
  Doctor createDoctor();

  /**
   * Returns a new object of class '<em>Gen List Of Int</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Gen List Of Int</em>'.
   * @generated
   */
  GenListOfInt createGenListOfInt();

  /**
   * Returns a new object of class '<em>Gen List Of String</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Gen List Of String</em>'.
   * @generated
   */
  GenListOfString createGenListOfString();

  /**
   * Returns the package supported by this factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the package supported by this factory.
   * @generated
   */
  Model5Package getModel5Package();

} // Model5Factory
