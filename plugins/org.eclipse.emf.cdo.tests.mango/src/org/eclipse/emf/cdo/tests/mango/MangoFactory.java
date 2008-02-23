/**
 * <copyright>
 * </copyright>
 *
 * $Id: MangoFactory.java,v 1.2 2008-02-23 10:00:33 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.mango;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a create method for each non-abstract class of
 * the model. <!-- end-user-doc -->
 * 
 * @see org.eclipse.emf.cdo.tests.mango.MangoPackage
 * @generated
 */
public interface MangoFactory extends EFactory
{
  /**
   * The singleton instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  MangoFactory eINSTANCE = org.eclipse.emf.cdo.tests.mango.impl.MangoFactoryImpl.init();

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
   * Returns the package supported by this factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the package supported by this factory.
   * @generated
   */
  MangoPackage getMangoPackage();

} // MangoFactory
