/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.tests.model1;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Customer</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model1.Customer#getSalesOrders <em>Sales Orders</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getCustomer()
 * @model
 * @generated
 */
public interface Customer extends Address
{
  /**
   * Returns the value of the '<em><b>Sales Orders</b></em>' reference list. The list contents are of type
   * {@link org.eclipse.emf.cdo.tests.model1.SalesOrder}. It is bidirectional and its opposite is '
   * {@link org.eclipse.emf.cdo.tests.model1.SalesOrder#getCustomer <em>Customer</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Sales Orders</em>' reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Sales Orders</em>' reference list.
   * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getCustomer_SalesOrders()
   * @see org.eclipse.emf.cdo.tests.model1.SalesOrder#getCustomer
   * @model opposite="customer"
   * @generated
   */
  EList<SalesOrder> getSalesOrders();

} // Customer
