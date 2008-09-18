/**
 * <copyright>
 * </copyright>
 *
 * $Id: SubpackageFactory.java,v 1.2 2008-09-18 12:57:19 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.legacy.model3.subpackage;

import org.eclipse.emf.cdo.tests.model3.subpackage.Class2;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a create method for each non-abstract class of
 * the model. <!-- end-user-doc -->
 * 
 * @see org.eclipse.emf.cdo.tests.legacy.model3.subpackage.SubpackagePackage
 * @generated NOT
 */
public interface SubpackageFactory extends org.eclipse.emf.cdo.tests.model3.subpackage.SubpackageFactory
{
  /**
   * The singleton instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  SubpackageFactory eINSTANCE = org.eclipse.emf.cdo.tests.legacy.model3.subpackage.impl.SubpackageFactoryImpl.init();

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
