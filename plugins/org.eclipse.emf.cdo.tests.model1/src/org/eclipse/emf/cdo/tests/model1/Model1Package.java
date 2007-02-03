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
 * <!-- begin-user-doc --> The <b>Package</b> for the model. It contains
 * accessors for the meta objects to represent
 * <ul>
 * <li>each class,</li>
 * <li>each feature of each class,</li>
 * <li>each enum,</li>
 * <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * 
 * @see org.eclipse.emf.cdo.tests.model1.Model1Factory
 * @model kind="package"
 * @generated
 */
public interface Model1Package extends EPackage
{
  /**
   * The package name. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNAME = "model1";

  /**
   * The package namespace URI. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNS_URI = "http://www.eclipse.org/emf/CDO/tests/model1/1.0.0";

  /**
   * The package namespace name. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNS_PREFIX = "model1";

  /**
   * The singleton instance of the package. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   */
  Model1Package eINSTANCE = org.eclipse.emf.cdo.tests.model1.impl.Model1PackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model1.impl.SupplierImpl <em>Supplier</em>}'
   * class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model1.impl.SupplierImpl
   * @see org.eclipse.emf.cdo.tests.model1.impl.Model1PackageImpl#getSupplier()
   * @generated
   */
  int SUPPLIER = 0;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SUPPLIER__NAME = 0;

  /**
   * The number of structural features of the '<em>Supplier</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int SUPPLIER_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model1.impl.PurchaseOrderImpl <em>Purchase Order</em>}'
   * class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model1.impl.PurchaseOrderImpl
   * @see org.eclipse.emf.cdo.tests.model1.impl.Model1PackageImpl#getPurchaseOrder()
   * @generated
   */
  int PURCHASE_ORDER = 1;

  /**
   * The feature id for the '<em><b>Date</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int PURCHASE_ORDER__DATE = 0;

  /**
   * The feature id for the '<em><b>Supplier</b></em>' reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int PURCHASE_ORDER__SUPPLIER = 1;

  /**
   * The number of structural features of the '<em>Purchase Order</em>'
   * class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int PURCHASE_ORDER_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model1.impl.OrderDetailImpl <em>Order Detail</em>}'
   * class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model1.impl.OrderDetailImpl
   * @see org.eclipse.emf.cdo.tests.model1.impl.Model1PackageImpl#getOrderDetail()
   * @generated
   */
  int ORDER_DETAIL = 2;

  /**
   * The feature id for the '<em><b>Product</b></em>' reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ORDER_DETAIL__PRODUCT = 0;

  /**
   * The feature id for the '<em><b>Price</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ORDER_DETAIL__PRICE = 1;

  /**
   * The number of structural features of the '<em>Order Detail</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ORDER_DETAIL_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model1.impl.ProductImpl <em>Product</em>}'
   * class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model1.impl.ProductImpl
   * @see org.eclipse.emf.cdo.tests.model1.impl.Model1PackageImpl#getProduct()
   * @generated
   */
  int PRODUCT = 3;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int PRODUCT__NAME = 0;

  /**
   * The feature id for the '<em><b>Order Details</b></em>' reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int PRODUCT__ORDER_DETAILS = 1;

  /**
   * The number of structural features of the '<em>Product</em>' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int PRODUCT_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model1.impl.CategoryImpl <em>Category</em>}'
   * class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model1.impl.CategoryImpl
   * @see org.eclipse.emf.cdo.tests.model1.impl.Model1PackageImpl#getCategory()
   * @generated
   */
  int CATEGORY = 4;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CATEGORY__NAME = 0;

  /**
   * The feature id for the '<em><b>Categories</b></em>' containment
   * reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CATEGORY__CATEGORIES = 1;

  /**
   * The feature id for the '<em><b>Products</b></em>' containment
   * reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CATEGORY__PRODUCTS = 2;

  /**
   * The number of structural features of the '<em>Category</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int CATEGORY_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model1.impl.CompanyImpl <em>Company</em>}'
   * class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model1.impl.CompanyImpl
   * @see org.eclipse.emf.cdo.tests.model1.impl.Model1PackageImpl#getCompany()
   * @generated
   */
  int COMPANY = 5;

  /**
   * The feature id for the '<em><b>Categories</b></em>' containment
   * reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int COMPANY__CATEGORIES = 0;

  /**
   * The feature id for the '<em><b>Suppliers</b></em>' containment
   * reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int COMPANY__SUPPLIERS = 1;

  /**
   * The feature id for the '<em><b>Purchase Orders</b></em>' containment
   * reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int COMPANY__PURCHASE_ORDERS = 2;

  /**
   * The number of structural features of the '<em>Company</em>' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int COMPANY_FEATURE_COUNT = 3;

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model1.Supplier <em>Supplier</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Supplier</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.Supplier
   * @generated
   */
  EClass getSupplier();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model1.Supplier#getName <em>Name</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.Supplier#getName()
   * @see #getSupplier()
   * @generated
   */
  EAttribute getSupplier_Name();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model1.PurchaseOrder <em>Purchase Order</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Purchase Order</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.PurchaseOrder
   * @generated
   */
  EClass getPurchaseOrder();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model1.PurchaseOrder#getDate <em>Date</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Date</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.PurchaseOrder#getDate()
   * @see #getPurchaseOrder()
   * @generated
   */
  EAttribute getPurchaseOrder_Date();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.tests.model1.PurchaseOrder#getSupplier <em>Supplier</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the reference '<em>Supplier</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.PurchaseOrder#getSupplier()
   * @see #getPurchaseOrder()
   * @generated
   */
  EReference getPurchaseOrder_Supplier();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model1.OrderDetail <em>Order Detail</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Order Detail</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.OrderDetail
   * @generated
   */
  EClass getOrderDetail();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.tests.model1.OrderDetail#getProduct <em>Product</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the reference '<em>Product</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.OrderDetail#getProduct()
   * @see #getOrderDetail()
   * @generated
   */
  EReference getOrderDetail_Product();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model1.OrderDetail#getPrice <em>Price</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Price</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.OrderDetail#getPrice()
   * @see #getOrderDetail()
   * @generated
   */
  EAttribute getOrderDetail_Price();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model1.Product <em>Product</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Product</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.Product
   * @generated
   */
  EClass getProduct();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model1.Product#getName <em>Name</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.Product#getName()
   * @see #getProduct()
   * @generated
   */
  EAttribute getProduct_Name();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.tests.model1.Product#getOrderDetails <em>Order Details</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the reference list '<em>Order Details</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.Product#getOrderDetails()
   * @see #getProduct()
   * @generated
   */
  EReference getProduct_OrderDetails();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model1.Category <em>Category</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Category</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.Category
   * @generated
   */
  EClass getCategory();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model1.Category#getName <em>Name</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.Category#getName()
   * @see #getCategory()
   * @generated
   */
  EAttribute getCategory_Name();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.tests.model1.Category#getCategories <em>Categories</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the containment reference list '<em>Categories</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.Category#getCategories()
   * @see #getCategory()
   * @generated
   */
  EReference getCategory_Categories();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.tests.model1.Category#getProducts <em>Products</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the containment reference list '<em>Products</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.Category#getProducts()
   * @see #getCategory()
   * @generated
   */
  EReference getCategory_Products();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model1.Company <em>Company</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Company</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.Company
   * @generated
   */
  EClass getCompany();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.tests.model1.Company#getCategories <em>Categories</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the containment reference list '<em>Categories</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.Company#getCategories()
   * @see #getCompany()
   * @generated
   */
  EReference getCompany_Categories();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.tests.model1.Company#getSuppliers <em>Suppliers</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the containment reference list '<em>Suppliers</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.Company#getSuppliers()
   * @see #getCompany()
   * @generated
   */
  EReference getCompany_Suppliers();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.tests.model1.Company#getPurchaseOrders <em>Purchase Orders</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the containment reference list '<em>Purchase Orders</em>'.
   * @see org.eclipse.emf.cdo.tests.model1.Company#getPurchaseOrders()
   * @see #getCompany()
   * @generated
   */
  EReference getCompany_PurchaseOrders();

  /**
   * Returns the factory that creates the instances of the model. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the factory that creates the instances of the model.
   * @generated
   */
  Model1Factory getModel1Factory();

  /**
   * <!-- begin-user-doc --> Defines literals for the meta objects that
   * represent
   * <ul>
   * <li>each class,</li>
   * <li>each feature of each class,</li>
   * <li>each enum,</li>
   * <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
   * 
   * @generated
   */
  interface Literals
  {
    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model1.impl.SupplierImpl <em>Supplier</em>}'
     * class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.tests.model1.impl.SupplierImpl
     * @see org.eclipse.emf.cdo.tests.model1.impl.Model1PackageImpl#getSupplier()
     * @generated
     */
    EClass SUPPLIER = eINSTANCE.getSupplier();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute
     * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute SUPPLIER__NAME = eINSTANCE.getSupplier_Name();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model1.impl.PurchaseOrderImpl <em>Purchase Order</em>}'
     * class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.tests.model1.impl.PurchaseOrderImpl
     * @see org.eclipse.emf.cdo.tests.model1.impl.Model1PackageImpl#getPurchaseOrder()
     * @generated
     */
    EClass PURCHASE_ORDER = eINSTANCE.getPurchaseOrder();

    /**
     * The meta object literal for the '<em><b>Date</b></em>' attribute
     * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute PURCHASE_ORDER__DATE = eINSTANCE.getPurchaseOrder_Date();

    /**
     * The meta object literal for the '<em><b>Supplier</b></em>' reference
     * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference PURCHASE_ORDER__SUPPLIER = eINSTANCE.getPurchaseOrder_Supplier();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model1.impl.OrderDetailImpl <em>Order Detail</em>}'
     * class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.tests.model1.impl.OrderDetailImpl
     * @see org.eclipse.emf.cdo.tests.model1.impl.Model1PackageImpl#getOrderDetail()
     * @generated
     */
    EClass ORDER_DETAIL = eINSTANCE.getOrderDetail();

    /**
     * The meta object literal for the '<em><b>Product</b></em>' reference
     * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference ORDER_DETAIL__PRODUCT = eINSTANCE.getOrderDetail_Product();

    /**
     * The meta object literal for the '<em><b>Price</b></em>' attribute
     * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute ORDER_DETAIL__PRICE = eINSTANCE.getOrderDetail_Price();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model1.impl.ProductImpl <em>Product</em>}'
     * class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.tests.model1.impl.ProductImpl
     * @see org.eclipse.emf.cdo.tests.model1.impl.Model1PackageImpl#getProduct()
     * @generated
     */
    EClass PRODUCT = eINSTANCE.getProduct();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute
     * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute PRODUCT__NAME = eINSTANCE.getProduct_Name();

    /**
     * The meta object literal for the '<em><b>Order Details</b></em>'
     * reference list feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference PRODUCT__ORDER_DETAILS = eINSTANCE.getProduct_OrderDetails();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model1.impl.CategoryImpl <em>Category</em>}'
     * class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.tests.model1.impl.CategoryImpl
     * @see org.eclipse.emf.cdo.tests.model1.impl.Model1PackageImpl#getCategory()
     * @generated
     */
    EClass CATEGORY = eINSTANCE.getCategory();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute
     * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EAttribute CATEGORY__NAME = eINSTANCE.getCategory_Name();

    /**
     * The meta object literal for the '<em><b>Categories</b></em>'
     * containment reference list feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EReference CATEGORY__CATEGORIES = eINSTANCE.getCategory_Categories();

    /**
     * The meta object literal for the '<em><b>Products</b></em>'
     * containment reference list feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EReference CATEGORY__PRODUCTS = eINSTANCE.getCategory_Products();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model1.impl.CompanyImpl <em>Company</em>}'
     * class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.tests.model1.impl.CompanyImpl
     * @see org.eclipse.emf.cdo.tests.model1.impl.Model1PackageImpl#getCompany()
     * @generated
     */
    EClass COMPANY = eINSTANCE.getCompany();

    /**
     * The meta object literal for the '<em><b>Categories</b></em>'
     * containment reference list feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EReference COMPANY__CATEGORIES = eINSTANCE.getCompany_Categories();

    /**
     * The meta object literal for the '<em><b>Suppliers</b></em>'
     * containment reference list feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EReference COMPANY__SUPPLIERS = eINSTANCE.getCompany_Suppliers();

    /**
     * The meta object literal for the '<em><b>Purchase Orders</b></em>'
     * containment reference list feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EReference COMPANY__PURCHASE_ORDERS = eINSTANCE.getCompany_PurchaseOrders();

  }

} // Model1Package
