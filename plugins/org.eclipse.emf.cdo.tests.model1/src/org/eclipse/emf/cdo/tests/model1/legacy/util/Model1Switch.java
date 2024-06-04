/**
 * Copyright (c) 2013, 2016, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model1.legacy.util;

import org.eclipse.emf.cdo.tests.model1.Address;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.Order;
import org.eclipse.emf.cdo.tests.model1.OrderAddress;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.tests.model1.legacy.Model1Package;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import java.util.List;
import java.util.Map;

//import org.eclipse.emf.cdo.tests.model1.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.tests.model1.legacy.Model1Package
 * @generated
 */
public class Model1Switch<T>
{
  /**
   * The cached model package
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static Model1Package modelPackage;

  /**
   * Creates an instance of the switch.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
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
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  public T doSwitch(EObject theEObject)
  {
    return doSwitch(theEObject.eClass(), theEObject);
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  protected T doSwitch(EClass theEClass, EObject theEObject)
  {
    if (theEClass.eContainer() == modelPackage)
    {
      return doSwitch(theEClass.getClassifierID(), theEObject);
    }
    else
    {
      List<EClass> eSuperTypes = theEClass.getESuperTypes();
      return eSuperTypes.isEmpty() ? defaultCase(theEObject) : doSwitch(eSuperTypes.get(0), theEObject);
    }
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  protected T doSwitch(int classifierID, EObject theEObject)
  {
    switch (classifierID)
    {
    case Model1Package.ADDRESS:
    {
      Address address = (Address)theEObject;
      T result = caseAddress(address);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model1Package.COMPANY:
    {
      Company company = (Company)theEObject;
      T result = caseCompany(company);
      if (result == null)
      {
        result = caseAddress(company);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model1Package.SUPPLIER:
    {
      Supplier supplier = (Supplier)theEObject;
      T result = caseSupplier(supplier);
      if (result == null)
      {
        result = caseAddress(supplier);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model1Package.CUSTOMER:
    {
      Customer customer = (Customer)theEObject;
      T result = caseCustomer(customer);
      if (result == null)
      {
        result = caseAddress(customer);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model1Package.ORDER:
    {
      Order order = (Order)theEObject;
      T result = caseOrder(order);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model1Package.ORDER_DETAIL:
    {
      OrderDetail orderDetail = (OrderDetail)theEObject;
      T result = caseOrderDetail(orderDetail);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model1Package.PURCHASE_ORDER:
    {
      PurchaseOrder purchaseOrder = (PurchaseOrder)theEObject;
      T result = casePurchaseOrder(purchaseOrder);
      if (result == null)
      {
        result = caseOrder(purchaseOrder);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model1Package.SALES_ORDER:
    {
      SalesOrder salesOrder = (SalesOrder)theEObject;
      T result = caseSalesOrder(salesOrder);
      if (result == null)
      {
        result = caseOrder(salesOrder);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model1Package.CATEGORY:
    {
      Category category = (Category)theEObject;
      T result = caseCategory(category);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model1Package.PRODUCT1:
    {
      Product1 product1 = (Product1)theEObject;
      T result = caseProduct1(product1);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model1Package.ORDER_ADDRESS:
    {
      OrderAddress orderAddress = (OrderAddress)theEObject;
      T result = caseOrderAddress(orderAddress);
      if (result == null)
      {
        result = caseAddress(orderAddress);
      }
      if (result == null)
      {
        result = caseOrder(orderAddress);
      }
      if (result == null)
      {
        result = caseOrderDetail(orderAddress);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case Model1Package.PRODUCT_TO_ORDER:
    {
      @SuppressWarnings("unchecked")
      Map.Entry<Product1, SalesOrder> productToOrder = (Map.Entry<Product1, SalesOrder>)theEObject;
      T result = caseProductToOrder(productToOrder);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    default:
      return defaultCase(theEObject);
    }
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Address</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Address</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAddress(Address object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Company</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Company</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseCompany(Company object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Supplier</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Supplier</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseSupplier(Supplier object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Customer</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Customer</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseCustomer(Customer object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Order</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Order</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOrder(Order object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Order Detail</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Order Detail</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOrderDetail(OrderDetail object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Purchase Order</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Purchase Order</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T casePurchaseOrder(PurchaseOrder object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Sales Order</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Sales Order</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseSalesOrder(SalesOrder object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Category</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Category</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseCategory(Category object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Product1</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Product1</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseProduct1(Product1 object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Order Address</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Order Address</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseOrderAddress(OrderAddress object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Product To Order</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Product To Order</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseProductToOrder(Map.Entry<Product1, SalesOrder> object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch, but this is the last case anyway.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject)
   * @generated
   */
  public T defaultCase(EObject object)
  {
    return null;
  }

} // Model1Switch
