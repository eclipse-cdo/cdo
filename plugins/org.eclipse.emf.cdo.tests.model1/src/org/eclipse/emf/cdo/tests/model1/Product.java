/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.tests.model1;

import org.eclipse.emf.cdo.CDOObject;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Product</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model1.Product#getName <em>Name</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model1.Product#getOrderDetails <em>Order Details</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getProduct()
 * @model
 * @extends CDOObject
 * @generated
 */
public interface Product extends CDOObject
{
  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute. <!--
   * begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear, there
   * really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getProduct_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model1.Product#getName <em>Name</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Order Details</b></em>' reference
   * list. The list contents are of type
   * {@link org.eclipse.emf.cdo.tests.model1.OrderDetail}. It is bidirectional
   * and its opposite is '{@link org.eclipse.emf.cdo.tests.model1.OrderDetail#getProduct <em>Product</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Order Details</em>' reference list isn't
   * clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Order Details</em>' reference list.
   * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getProduct_OrderDetails()
   * @see org.eclipse.emf.cdo.tests.model1.OrderDetail#getProduct
   * @model opposite="product"
   * @generated
   */
  EList<OrderDetail> getOrderDetails();

} // Product
