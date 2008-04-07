/**
 * <copyright>
 * </copyright>
 *
 * $Id: Model2Factory.java,v 1.1 2008-04-07 07:36:36 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.model2;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.tests.model2.Model2Package
 * @generated
 */
public interface Model2Factory extends EFactory
{
  /**
   * The singleton instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  Model2Factory eINSTANCE = org.eclipse.emf.cdo.tests.model2.impl.Model2FactoryImpl.init();

  /**
   * Returns a new object of class '<em>Special Purchase Order</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Special Purchase Order</em>'.
   * @generated
   */
  SpecialPurchaseOrder createSpecialPurchaseOrder();

  /**
   * Returns the package supported by this factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the package supported by this factory.
   * @generated
   */
  Model2Package getModel2Package();

} //Model2Factory
