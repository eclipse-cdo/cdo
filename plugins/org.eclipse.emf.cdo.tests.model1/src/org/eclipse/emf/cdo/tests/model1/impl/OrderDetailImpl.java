/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.tests.model1.impl;

import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model1.Order;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.Product1;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Order Detail</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model1.impl.OrderDetailImpl#getOrder <em>Order</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model1.impl.OrderDetailImpl#getProduct <em>Product</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model1.impl.OrderDetailImpl#getPrice <em>Price</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class OrderDetailImpl extends CDOObjectImpl implements OrderDetail
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected OrderDetailImpl()
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
    return Model1Package.Literals.ORDER_DETAIL;
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
  public Order getOrder()
  {
    return (Order)eGet(Model1Package.Literals.ORDER_DETAIL__ORDER, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setOrder(Order newOrder)
  {
    eSet(Model1Package.Literals.ORDER_DETAIL__ORDER, newOrder);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Product1 getProduct()
  {
    return (Product1)eGet(Model1Package.Literals.ORDER_DETAIL__PRODUCT, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setProduct(Product1 newProduct)
  {
    eSet(Model1Package.Literals.ORDER_DETAIL__PRODUCT, newProduct);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public float getPrice()
  {
    return ((Float)eGet(Model1Package.Literals.ORDER_DETAIL__PRICE, true)).floatValue();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setPrice(float newPrice)
  {
    eSet(Model1Package.Literals.ORDER_DETAIL__PRICE, new Float(newPrice));
  }

} // OrderDetailImpl
