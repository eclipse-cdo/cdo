/*
 * Copyright (c) 2009-2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples.company.impl;

import org.eclipse.emf.cdo.examples.company.Category;
import org.eclipse.emf.cdo.examples.company.Company;
import org.eclipse.emf.cdo.examples.company.CompanyFactory;
import org.eclipse.emf.cdo.examples.company.CompanyPackage;
import org.eclipse.emf.cdo.examples.company.Customer;
import org.eclipse.emf.cdo.examples.company.Order;
import org.eclipse.emf.cdo.examples.company.OrderDetail;
import org.eclipse.emf.cdo.examples.company.Product;
import org.eclipse.emf.cdo.examples.company.PurchaseOrder;
import org.eclipse.emf.cdo.examples.company.SalesOrder;
import org.eclipse.emf.cdo.examples.company.Supplier;
import org.eclipse.emf.cdo.examples.company.VAT;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!-- end-user-doc -->
 * @generated
 */
public class CompanyFactoryImpl extends EFactoryImpl implements CompanyFactory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public static CompanyFactory init()
  {
    try
    {
      CompanyFactory theCompanyFactory = (CompanyFactory)EPackage.Registry.INSTANCE.getEFactory(CompanyPackage.eNS_URI);
      if (theCompanyFactory != null)
      {
        return theCompanyFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new CompanyFactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public CompanyFactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
    case CompanyPackage.COMPANY:
      return createCompany();
    case CompanyPackage.SUPPLIER:
      return createSupplier();
    case CompanyPackage.CUSTOMER:
      return createCustomer();
    case CompanyPackage.ORDER:
      return createOrder();
    case CompanyPackage.ORDER_DETAIL:
      return createOrderDetail();
    case CompanyPackage.PURCHASE_ORDER:
      return createPurchaseOrder();
    case CompanyPackage.SALES_ORDER:
      return createSalesOrder();
    case CompanyPackage.CATEGORY:
      return createCategory();
    case CompanyPackage.PRODUCT:
      return createProduct();
    default:
      throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object createFromString(EDataType eDataType, String initialValue)
  {
    switch (eDataType.getClassifierID())
    {
    case CompanyPackage.VAT:
      return createVATFromString(eDataType, initialValue);
    default:
      throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String convertToString(EDataType eDataType, Object instanceValue)
  {
    switch (eDataType.getClassifierID())
    {
    case CompanyPackage.VAT:
      return convertVATToString(eDataType, instanceValue);
    default:
      throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Supplier createSupplier()
  {
    SupplierImpl supplier = new SupplierImpl();
    return supplier;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public PurchaseOrder createPurchaseOrder()
  {
    PurchaseOrderImpl purchaseOrder = new PurchaseOrderImpl();
    return purchaseOrder;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public OrderDetail createOrderDetail()
  {
    OrderDetailImpl orderDetail = new OrderDetailImpl();
    return orderDetail;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public VAT createVATFromString(EDataType eDataType, String initialValue)
  {
    VAT result = VAT.get(initialValue);
    if (result == null)
    {
      throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    }
    return result;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public String convertVATToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Category createCategory()
  {
    CategoryImpl category = new CategoryImpl();
    return category;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Product createProduct()
  {
    ProductImpl product = new ProductImpl();
    return product;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Company createCompany()
  {
    CompanyImpl company = new CompanyImpl();
    return company;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Customer createCustomer()
  {
    CustomerImpl customer = new CustomerImpl();
    return customer;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Order createOrder()
  {
    OrderImpl order = new OrderImpl();
    return order;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public SalesOrder createSalesOrder()
  {
    SalesOrderImpl salesOrder = new SalesOrderImpl();
    return salesOrder;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public CompanyPackage getCompanyPackage()
  {
    return (CompanyPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static CompanyPackage getPackage()
  {
    return CompanyPackage.eINSTANCE;
  }

} // CompanyFactoryImpl
