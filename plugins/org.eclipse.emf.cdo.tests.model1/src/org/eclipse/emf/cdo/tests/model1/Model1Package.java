/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.tests.model1;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.tests.model1.Model1Factory
 * @model kind="package"
 * @generated
 */
public interface Model1Package extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "model1";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://www.eclipse.org/emf/CDO/tests/model1/1.0.0";

  /**
   * The package namespace name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "model1";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  Model1Package eINSTANCE = org.eclipse.emf.cdo.tests.model1.impl.Model1PackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model1.impl.SupplierImpl <em>Supplier</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model1.impl.SupplierImpl
   * @see org.eclipse.emf.cdo.tests.model1.impl.Model1PackageImpl#getSupplier()
   * @generated
   */
  int SUPPLIER = 0;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SUPPLIER__NAME = 0;

  /**
   * The number of structural features of the '<em>Supplier</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SUPPLIER_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model1.impl.PurchaseOrderImpl <em>Purchase Order</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model1.impl.PurchaseOrderImpl
   * @see org.eclipse.emf.cdo.tests.model1.impl.Model1PackageImpl#getPurchaseOrder()
   * @generated
   */
  int PURCHASE_ORDER = 1;

  /**
   * The feature id for the '<em><b>Date</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PURCHASE_ORDER__DATE = 0;

  /**
   * The feature id for the '<em><b>Supplier</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PURCHASE_ORDER__SUPPLIER = 1;

  /**
   * The number of structural features of the '<em>Purchase Order</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PURCHASE_ORDER_FEATURE_COUNT = 2;


  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model1.Supplier <em>Supplier</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Supplier</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.Supplier
   * @generated
   */
  EClass getSupplier();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model1.Supplier#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.Supplier#getName()
   * @see #getSupplier()
   * @generated
   */
  EAttribute getSupplier_Name();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model1.PurchaseOrder <em>Purchase Order</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Purchase Order</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.PurchaseOrder
   * @generated
   */
  EClass getPurchaseOrder();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model1.PurchaseOrder#getDate <em>Date</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Date</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.PurchaseOrder#getDate()
   * @see #getPurchaseOrder()
   * @generated
   */
  EAttribute getPurchaseOrder_Date();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.tests.model1.PurchaseOrder#getSupplier <em>Supplier</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Supplier</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.PurchaseOrder#getSupplier()
   * @see #getPurchaseOrder()
   * @generated
   */
  EReference getPurchaseOrder_Supplier();

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  Model1Factory getModel1Factory();

  /**
   * <!-- begin-user-doc -->
   * Defines literals for the meta objects that represent
   * <ul>
   *   <li>each class,</li>
   *   <li>each feature of each class,</li>
   *   <li>each enum,</li>
   *   <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
   * @generated
   */
  interface Literals
  {
    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model1.impl.SupplierImpl <em>Supplier</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.model1.impl.SupplierImpl
     * @see org.eclipse.emf.cdo.tests.model1.impl.Model1PackageImpl#getSupplier()
     * @generated
     */
    EClass SUPPLIER = eINSTANCE.getSupplier();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute SUPPLIER__NAME = eINSTANCE.getSupplier_Name();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model1.impl.PurchaseOrderImpl <em>Purchase Order</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.model1.impl.PurchaseOrderImpl
     * @see org.eclipse.emf.cdo.tests.model1.impl.Model1PackageImpl#getPurchaseOrder()
     * @generated
     */
    EClass PURCHASE_ORDER = eINSTANCE.getPurchaseOrder();

    /**
     * The meta object literal for the '<em><b>Date</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PURCHASE_ORDER__DATE = eINSTANCE.getPurchaseOrder_Date();

    /**
     * The meta object literal for the '<em><b>Supplier</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference PURCHASE_ORDER__SUPPLIER = eINSTANCE.getPurchaseOrder_Supplier();

  }

} //Model1Package
