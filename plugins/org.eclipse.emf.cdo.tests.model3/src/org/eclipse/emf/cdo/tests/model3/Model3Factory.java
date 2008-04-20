/**
 * <copyright>
 * </copyright>
 *
 * $Id: Model3Factory.java,v 1.1 2008-04-20 09:58:05 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.model3;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a create method for each non-abstract class of
 * the model. <!-- end-user-doc -->
 * 
 * @see org.eclipse.emf.cdo.tests.model3.Model3Package
 * @generated
 */
public interface Model3Factory extends EFactory
{
  /**
   * The singleton instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  Model3Factory eINSTANCE = org.eclipse.emf.cdo.tests.model3.impl.Model3FactoryImpl.init();

  /**
   * Returns a new object of class '<em>Class1</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Class1</em>'.
   * @generated
   */
  Class1 createClass1();

  /**
   * Returns the package supported by this factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the package supported by this factory.
   * @generated
   */
  Model3Package getModel3Package();

} // Model3Factory
