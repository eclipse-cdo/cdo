/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.tests.model1;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Supplier</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model1.Supplier#getPurchaseOrders <em>Purchase Orders</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getSupplier()
 * @model
 * @generated
 */
public interface Supplier extends Address
{
  /**
   * Returns the value of the '<em><b>Purchase Orders</b></em>' reference
   * list. The list contents are of type
   * {@link org.eclipse.emf.cdo.tests.model1.PurchaseOrder}. It is
   * bidirectional and its opposite is '{@link org.eclipse.emf.cdo.tests.model1.PurchaseOrder#getSupplier <em>Supplier</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Purchase Orders</em>' reference list isn't
   * clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Purchase Orders</em>' reference list.
   * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getSupplier_PurchaseOrders()
   * @see org.eclipse.emf.cdo.tests.model1.PurchaseOrder#getSupplier
   * @model opposite="supplier"
   * @generated
   */
  EList<PurchaseOrder> getPurchaseOrders();

} // Supplier
