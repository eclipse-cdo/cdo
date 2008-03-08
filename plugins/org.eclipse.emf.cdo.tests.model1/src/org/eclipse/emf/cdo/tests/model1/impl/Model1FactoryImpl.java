/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.tests.model1.impl;

import org.eclipse.emf.cdo.tests.model1.Address;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model1.Order;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.Product;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.tests.model1.VAT;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!-- end-user-doc -->
 * 
 * @generated
 */
public class Model1FactoryImpl extends EFactoryImpl implements Model1Factory
{
  /**
   * Creates the default factory implementation. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public static Model1Factory init()
  {
    try
    {
      Model1Factory theModel1Factory = (Model1Factory)EPackage.Registry.INSTANCE
          .getEFactory("http://www.eclipse.org/emf/CDO/tests/model1/1.0.0");
      if (theModel1Factory != null)
      {
        return theModel1Factory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new Model1FactoryImpl();
  }

  /**
   * Creates an instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Model1FactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
    case Model1Package.ADDRESS:
      return (EObject)createAddress();
    case Model1Package.COMPANY:
      return (EObject)createCompany();
    case Model1Package.SUPPLIER:
      return (EObject)createSupplier();
    case Model1Package.CUSTOMER:
      return (EObject)createCustomer();
    case Model1Package.ORDER:
      return (EObject)createOrder();
    case Model1Package.ORDER_DETAIL:
      return (EObject)createOrderDetail();
    case Model1Package.PURCHASE_ORDER:
      return (EObject)createPurchaseOrder();
    case Model1Package.SALES_ORDER:
      return (EObject)createSalesOrder();
    case Model1Package.CATEGORY:
      return (EObject)createCategory();
    case Model1Package.PRODUCT:
      return (EObject)createProduct();
    default:
      throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public Object createFromString(EDataType eDataType, String initialValue)
  {
    switch (eDataType.getClassifierID())
    {
    case Model1Package.VAT:
      return createVATFromString(eDataType, initialValue);
    default:
      throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public String convertToString(EDataType eDataType, Object instanceValue)
  {
    switch (eDataType.getClassifierID())
    {
    case Model1Package.VAT:
      return convertVATToString(eDataType, instanceValue);
    default:
      throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Address createAddress()
  {
    AddressImpl address = new AddressImpl();
    return address;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Supplier createSupplier()
  {
    SupplierImpl supplier = new SupplierImpl();
    return supplier;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public PurchaseOrder createPurchaseOrder()
  {
    PurchaseOrderImpl purchaseOrder = new PurchaseOrderImpl();
    return purchaseOrder;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public OrderDetail createOrderDetail()
  {
    OrderDetailImpl orderDetail = new OrderDetailImpl();
    return orderDetail;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Product createProduct()
  {
    ProductImpl product = new ProductImpl();
    return product;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public VAT createVATFromString(EDataType eDataType, String initialValue)
  {
    VAT result = VAT.get(initialValue);
    if (result == null)
      throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '"
          + eDataType.getName() + "'");
    return result;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public String convertVATToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Category createCategory()
  {
    CategoryImpl category = new CategoryImpl();
    return category;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Company createCompany()
  {
    CompanyImpl company = new CompanyImpl();
    return company;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Customer createCustomer()
  {
    CustomerImpl customer = new CustomerImpl();
    return customer;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Order createOrder()
  {
    OrderImpl order = new OrderImpl();
    return order;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public SalesOrder createSalesOrder()
  {
    SalesOrderImpl salesOrder = new SalesOrderImpl();
    return salesOrder;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Model1Package getModel1Package()
  {
    return (Model1Package)getEPackage();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @deprecated
   * @generated
   */
  @Deprecated
  public static Model1Package getPackage()
  {
    return Model1Package.eINSTANCE;
  }

} // Model1FactoryImpl
