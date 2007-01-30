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
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Company</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model1.Company#getCategories <em>Categories</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model1.Company#getSuppliers <em>Suppliers</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model1.Company#getPurchaseOrders <em>Purchase Orders</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getCompany()
 * @model
 * @extends CDOObject
 * @generated
 */
public interface Company extends CDOObject
{
  /**
   * Returns the value of the '<em><b>Categories</b></em>' containment
   * reference list. The list contents are of type
   * {@link org.eclipse.emf.cdo.tests.model1.Category}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Categories</em>' containment reference list
   * isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Categories</em>' containment reference
   *         list.
   * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getCompany_Categories()
   * @model type="org.eclipse.emf.cdo.tests.model1.Category" containment="true"
   * @generated
   */
  EList getCategories();

  /**
   * Returns the value of the '<em><b>Suppliers</b></em>' containment
   * reference list. The list contents are of type
   * {@link org.eclipse.emf.cdo.tests.model1.Supplier}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Suppliers</em>' containment reference list
   * isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Suppliers</em>' containment reference
   *         list.
   * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getCompany_Suppliers()
   * @model type="org.eclipse.emf.cdo.tests.model1.Supplier" containment="true"
   * @generated
   */
  EList getSuppliers();

  /**
   * Returns the value of the '<em><b>Purchase Orders</b></em>' containment
   * reference list. The list contents are of type
   * {@link org.eclipse.emf.cdo.tests.model1.PurchaseOrder}. <!--
   * begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Purchase Orders</em>' containment reference
   * list isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Purchase Orders</em>' containment
   *         reference list.
   * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getCompany_PurchaseOrders()
   * @model type="org.eclipse.emf.cdo.tests.model1.PurchaseOrder"
   *        containment="true"
   * @generated
   */
  EList getPurchaseOrders();

} // Company
