/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.tests.model1;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Order</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model1.Order#getOrderDetails <em>Order Details</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getOrder()
 * @model
 * @generated
 */
public interface Order extends EObject
{
  /**
   * Returns the value of the '<em><b>Order Details</b></em>' containment reference list. The list contents are of type
   * {@link org.eclipse.emf.cdo.tests.model1.OrderDetail}. It is bidirectional and its opposite is '
   * {@link org.eclipse.emf.cdo.tests.model1.OrderDetail#getOrder <em>Order</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Order Details</em>' containment reference list isn't clear, there really should be more
   * of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Order Details</em>' containment reference list.
   * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getOrder_OrderDetails()
   * @see org.eclipse.emf.cdo.tests.model1.OrderDetail#getOrder
   * @model opposite="order" containment="true"
   * @generated
   */
  EList<OrderDetail> getOrderDetails();

} // Order
