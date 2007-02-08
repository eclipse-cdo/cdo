/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.tests.model1.util;

import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.Product;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import java.util.List;

/**
 * <!-- begin-user-doc --> The <b>Switch</b> for the model's inheritance
 * hierarchy. It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object and proceeding up the
 * inheritance hierarchy until a non-null result is returned, which is the
 * result of the switch. <!-- end-user-doc -->
 * 
 * @see org.eclipse.emf.cdo.tests.model1.Model1Package
 * @generated
 */
public class Model1Switch
{
  /**
   * The cached model package <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected static Model1Package modelPackage;

  /**
   * Creates an instance of the switch. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   */
  public Model1Switch()
  {
    if (modelPackage == null)
    {
      modelPackage = Model1Package.eINSTANCE;
    }
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns
   * a non null result; it yields that result. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @return the first non-null result returned by a <code>caseXXX</code>
   *         call.
   * @generated
   */
  public Object doSwitch(EObject theEObject)
  {
    return doSwitch(theEObject.eClass(), theEObject);
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns
   * a non null result; it yields that result. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @return the first non-null result returned by a <code>caseXXX</code>
   *         call.
   * @generated
   */
  protected Object doSwitch(EClass theEClass, EObject theEObject)
  {
    if (theEClass.eContainer() == modelPackage)
    {
      return doSwitch(theEClass.getClassifierID(), theEObject);
    }
    else
    {
      List eSuperTypes = theEClass.getESuperTypes();
      return eSuperTypes.isEmpty() ? defaultCase(theEObject) : doSwitch((EClass)eSuperTypes.get(0), theEObject);
    }
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns
   * a non null result; it yields that result. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @return the first non-null result returned by a <code>caseXXX</code>
   *         call.
   * @generated
   */
  protected Object doSwitch(int classifierID, EObject theEObject)
  {
    switch (classifierID)
    {
    case Model1Package.SUPPLIER:
    {
      Supplier supplier = (Supplier)theEObject;
      Object result = caseSupplier(supplier);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case Model1Package.PURCHASE_ORDER:
    {
      PurchaseOrder purchaseOrder = (PurchaseOrder)theEObject;
      Object result = casePurchaseOrder(purchaseOrder);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case Model1Package.ORDER_DETAIL:
    {
      OrderDetail orderDetail = (OrderDetail)theEObject;
      Object result = caseOrderDetail(orderDetail);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case Model1Package.PRODUCT:
    {
      Product product = (Product)theEObject;
      Object result = caseProduct(product);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case Model1Package.CATEGORY:
    {
      Category category = (Category)theEObject;
      Object result = caseCategory(category);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case Model1Package.COMPANY:
    {
      Company company = (Company)theEObject;
      Object result = caseCompany(company);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case Model1Package.CUSTOMER:
    {
      Customer customer = (Customer)theEObject;
      Object result = caseCustomer(customer);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case Model1Package.SALES_ORDER:
    {
      SalesOrder salesOrder = (SalesOrder)theEObject;
      Object result = caseSalesOrder(salesOrder);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    default:
      return defaultCase(theEObject);
    }
  }

  /**
   * Returns the result of interpretting the object as an instance of '<em>Supplier</em>'.
   * <!-- begin-user-doc --> This implementation returns null; returning a
   * non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpretting the object as an instance of '<em>Supplier</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public Object caseSupplier(Supplier object)
  {
    return null;
  }

  /**
   * Returns the result of interpretting the object as an instance of '<em>Purchase Order</em>'.
   * <!-- begin-user-doc --> This implementation returns null; returning a
   * non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpretting the object as an instance of '<em>Purchase Order</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public Object casePurchaseOrder(PurchaseOrder object)
  {
    return null;
  }

  /**
   * Returns the result of interpretting the object as an instance of '<em>Order Detail</em>'.
   * <!-- begin-user-doc --> This implementation returns null; returning a
   * non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpretting the object as an instance of '<em>Order Detail</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public Object caseOrderDetail(OrderDetail object)
  {
    return null;
  }

  /**
   * Returns the result of interpretting the object as an instance of '<em>Product</em>'.
   * <!-- begin-user-doc --> This implementation returns null; returning a
   * non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpretting the object as an instance of '<em>Product</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public Object caseProduct(Product object)
  {
    return null;
  }

  /**
   * Returns the result of interpretting the object as an instance of '<em>Category</em>'.
   * <!-- begin-user-doc --> This implementation returns null; returning a
   * non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpretting the object as an instance of '<em>Category</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public Object caseCategory(Category object)
  {
    return null;
  }

  /**
   * Returns the result of interpretting the object as an instance of '<em>Company</em>'.
   * <!-- begin-user-doc --> This implementation returns null; returning a
   * non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpretting the object as an instance of '<em>Company</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public Object caseCompany(Company object)
  {
    return null;
  }

  /**
   * Returns the result of interpretting the object as an instance of '<em>Customer</em>'.
   * <!-- begin-user-doc --> This implementation returns null; returning a
   * non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpretting the object as an instance of '<em>Customer</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public Object caseCustomer(Customer object)
  {
    return null;
  }

  /**
   * Returns the result of interpretting the object as an instance of '<em>Sales Order</em>'.
   * <!-- begin-user-doc --> This implementation returns null; returning a
   * non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpretting the object as an instance of '<em>Sales Order</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public Object caseSalesOrder(SalesOrder object)
  {
    return null;
  }

  /**
   * Returns the result of interpretting the object as an instance of '<em>EObject</em>'.
   * <!-- begin-user-doc --> This implementation returns null; returning a
   * non-null result will terminate the switch, but this is the last case
   * anyway. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpretting the object as an instance of '<em>EObject</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject)
   * @generated
   */
  public Object defaultCase(EObject object)
  {
    return null;
  }

} // Model1Switch
