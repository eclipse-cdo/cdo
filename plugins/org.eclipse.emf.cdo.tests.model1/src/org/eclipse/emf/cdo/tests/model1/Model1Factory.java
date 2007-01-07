/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.tests.model1;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a
 * create method for each non-abstract class of the model. <!-- end-user-doc -->
 * 
 * @see org.eclipse.emf.cdo.tests.model1.Model1Package
 * @generated
 */
public interface Model1Factory extends EFactory
{
  /**
   * The singleton instance of the factory. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   */
  Model1Factory eINSTANCE = org.eclipse.emf.cdo.tests.model1.impl.Model1FactoryImpl.init();

  /**
   * Returns a new object of class '<em>Supplier</em>'. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Supplier</em>'.
   * @generated
   */
  Supplier createSupplier();

  /**
   * Returns a new object of class '<em>Purchase Order</em>'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Purchase Order</em>'.
   * @generated
   */
  PurchaseOrder createPurchaseOrder();

  /**
   * Returns the package supported by this factory. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @return the package supported by this factory.
   * @generated
   */
  Model1Package getModel1Package();

} // Model1Factory
