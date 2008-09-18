/**
 * <copyright>
 * </copyright>
 *
 * $Id: MangoFactory.java,v 1.2 2008-09-18 12:56:15 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.legacy.mango;

import org.eclipse.emf.cdo.tests.mango.Parameter;
import org.eclipse.emf.cdo.tests.mango.Value;
import org.eclipse.emf.cdo.tests.mango.ValueList;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a create method for each non-abstract class of
 * the model. <!-- end-user-doc -->
 * 
 * @see org.eclipse.emf.cdo.tests.legacy.mango.MangoPackage
 * @generated NOT
 */
public interface MangoFactory extends org.eclipse.emf.cdo.tests.mango.MangoFactory
{
  /**
   * The singleton instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  MangoFactory eINSTANCE = org.eclipse.emf.cdo.tests.legacy.mango.impl.MangoFactoryImpl.init();

  /**
   * Returns a new object of class '<em>Value List</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Value List</em>'.
   * @generated
   */
  ValueList createValueList();

  /**
   * Returns a new object of class '<em>Value</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Value</em>'.
   * @generated
   */
  Value createValue();

  /**
   * Returns a new object of class '<em>Parameter</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Parameter</em>'.
   * @generated
   */
  Parameter createParameter();

  /**
   * Returns the package supported by this factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the package supported by this factory.
   * @generated
   */
  MangoPackage getMangoPackage();

} // MangoFactory
