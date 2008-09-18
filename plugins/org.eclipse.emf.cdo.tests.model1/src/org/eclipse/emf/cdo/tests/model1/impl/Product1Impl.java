/**
 * <copyright>
 * </copyright>
 *
 * $Id: Product1Impl.java,v 1.2 2008-09-18 12:57:07 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.model1.impl;

import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.VAT;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Product</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model1.impl.Product1Impl#getName <em>Name</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model1.impl.Product1Impl#getOrderDetails <em>Order Details</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model1.impl.Product1Impl#getVat <em>Vat</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class Product1Impl extends CDOObjectImpl implements Product1
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected Product1Impl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return Model1Package.Literals.PRODUCT1;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected int eStaticFeatureCount()
  {
    return 0;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public String getName()
  {
    return (String)eGet(Model1Package.Literals.PRODUCT1__NAME, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setName(String newName)
  {
    eSet(Model1Package.Literals.PRODUCT1__NAME, newName);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EList<OrderDetail> getOrderDetails()
  {
    return (EList<OrderDetail>)eGet(Model1Package.Literals.PRODUCT1__ORDER_DETAILS, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public VAT getVat()
  {
    return (VAT)eGet(Model1Package.Literals.PRODUCT1__VAT, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setVat(VAT newVat)
  {
    eSet(Model1Package.Literals.PRODUCT1__VAT, newVat);
  }

} // ProductImpl
