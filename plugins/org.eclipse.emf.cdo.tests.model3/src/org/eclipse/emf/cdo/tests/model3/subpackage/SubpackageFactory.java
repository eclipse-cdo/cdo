/**
 * <copyright>
 * </copyright>
 *
 * $Id: SubpackageFactory.java,v 1.1.4.2 2008-07-16 16:34:55 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.model3.subpackage;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a create method for each non-abstract class of
 * the model. <!-- end-user-doc -->
 * 
 * @see org.eclipse.emf.cdo.tests.model3.subpackage.SubpackagePackage
 * @generated
 */
public interface SubpackageFactory extends EFactory
{
  /**
   * The singleton instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  SubpackageFactory eINSTANCE = org.eclipse.emf.cdo.tests.model3.subpackage.impl.SubpackageFactoryImpl.init();

  /**
   * Returns a new object of class '<em>Class2</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Class2</em>'.
   * @generated
   */
  Class2 createClass2();

  /**
   * Returns the package supported by this factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the package supported by this factory.
   * @generated
   */
  SubpackagePackage getSubpackagePackage();

} // SubpackageFactory
