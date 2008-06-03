/**
 * <copyright>
 * </copyright>
 *
 * $Id: OrderAddress.java,v 1.2 2008-06-03 06:41:31 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.model1;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Order Address</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model1.OrderAddress#isTestAttribute <em>Test Attribute</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getOrderAddress()
 * @model
 * @generated
 */
public interface OrderAddress extends Address, Order, OrderDetail
{
  /**
   * Returns the value of the '<em><b>Test Attribute</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Test Attribute</em>' attribute isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Test Attribute</em>' attribute.
   * @see #setTestAttribute(boolean)
   * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getOrderAddress_TestAttribute()
   * @model
   * @generated
   */
  boolean isTestAttribute();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model1.OrderAddress#isTestAttribute
   * <em>Test Attribute</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Test Attribute</em>' attribute.
   * @see #isTestAttribute()
   * @generated
   */
  void setTestAttribute(boolean value);

} // OrderAddress
