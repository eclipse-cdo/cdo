/**
 * <copyright>
 * </copyright>
 *
 * $Id: OrderDetailImpl.java,v 1.2 2008-09-18 12:57:08 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.legacy.model1.impl;

import org.eclipse.emf.cdo.tests.legacy.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model1.Order;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.Product1;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Order Detail</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.legacy.model1.impl.OrderDetailImpl#getOrder <em>Order</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.legacy.model1.impl.OrderDetailImpl#getProduct <em>Product</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.legacy.model1.impl.OrderDetailImpl#getPrice <em>Price</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class OrderDetailImpl extends EObjectImpl implements OrderDetail
{
  /**
   * The cached value of the '{@link #getProduct() <em>Product</em>}' reference. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @see #getProduct()
   * @generated
   * @ordered
   */
  protected Product1 product;

  /**
   * The default value of the '{@link #getPrice() <em>Price</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @see #getPrice()
   * @generated
   * @ordered
   */
  protected static final float PRICE_EDEFAULT = 0.0F;

  /**
   * The cached value of the '{@link #getPrice() <em>Price</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @see #getPrice()
   * @generated
   * @ordered
   */
  protected float price = PRICE_EDEFAULT;

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
  public Order getOrder()
  {
    eFireRead(Model1Package.ORDER_DETAIL__ORDER);
    if (eContainerFeatureID != Model1Package.ORDER_DETAIL__ORDER)
    {
      return null;
    }
    return (Order)eContainer();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public NotificationChain basicSetOrder(Order newOrder, NotificationChain msgs)
  {
    eFireWrite(Model1Package.ORDER_DETAIL__ORDER);
    msgs = eBasicSetContainer((InternalEObject)newOrder, Model1Package.ORDER_DETAIL__ORDER, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setOrder(Order newOrder)
  {
    eFireWrite(Model1Package.ORDER_DETAIL__ORDER);
    if (newOrder != eInternalContainer() || eContainerFeatureID != Model1Package.ORDER_DETAIL__ORDER
        && newOrder != null)
    {
      if (EcoreUtil.isAncestor(this, newOrder))
      {
        throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
      }
      NotificationChain msgs = null;
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      if (newOrder != null)
      {
        msgs = ((InternalEObject)newOrder).eInverseAdd(this, Model1Package.ORDER__ORDER_DETAILS, Order.class, msgs);
      }
      msgs = basicSetOrder(newOrder, msgs);
      if (msgs != null)
      {
        msgs.dispatch();
      }
    }
    else if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model1Package.ORDER_DETAIL__ORDER, newOrder, newOrder));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Product1 getProduct()
  {
    eFireRead(Model1Package.ORDER_DETAIL__PRODUCT);
    if (product != null && product.eIsProxy())
    {
      InternalEObject oldProduct = (InternalEObject)product;
      product = (Product1)eResolveProxy(oldProduct);
      if (product != oldProduct)
      {
        if (eNotificationRequired())
        {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, Model1Package.ORDER_DETAIL__PRODUCT, oldProduct,
              product));
        }
      }
    }
    return product;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Product1 basicGetProduct()
  {
    eFireRead(Model1Package.ORDER_DETAIL__PRODUCT);
    return product;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public NotificationChain basicSetProduct(Product1 newProduct, NotificationChain msgs)
  {
    eFireWrite(Model1Package.ORDER_DETAIL__PRODUCT);
    Product1 oldProduct = product;
    product = newProduct;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
          Model1Package.ORDER_DETAIL__PRODUCT, oldProduct, newProduct);
      if (msgs == null)
      {
        msgs = notification;
      }
      else
      {
        msgs.add(notification);
      }
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setProduct(Product1 newProduct)
  {
    eFireWrite(Model1Package.ORDER_DETAIL__PRODUCT);
    if (newProduct != product)
    {
      NotificationChain msgs = null;
      if (product != null)
      {
        msgs = ((InternalEObject)product).eInverseRemove(this, Model1Package.PRODUCT1__ORDER_DETAILS, Product1.class,
            msgs);
      }
      if (newProduct != null)
      {
        msgs = ((InternalEObject)newProduct).eInverseAdd(this, Model1Package.PRODUCT1__ORDER_DETAILS, Product1.class,
            msgs);
      }
      msgs = basicSetProduct(newProduct, msgs);
      if (msgs != null)
      {
        msgs.dispatch();
      }
    }
    else if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model1Package.ORDER_DETAIL__PRODUCT, newProduct, newProduct));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public float getPrice()
  {
    eFireRead(Model1Package.ORDER_DETAIL__PRICE);
    return price;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setPrice(float newPrice)
  {
    eFireWrite(Model1Package.ORDER_DETAIL__PRICE);
    float oldPrice = price;
    price = newPrice;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model1Package.ORDER_DETAIL__PRICE, oldPrice, price));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case Model1Package.ORDER_DETAIL__ORDER:
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      return basicSetOrder((Order)otherEnd, msgs);
    case Model1Package.ORDER_DETAIL__PRODUCT:
      if (product != null)
      {
        msgs = ((InternalEObject)product).eInverseRemove(this, Model1Package.PRODUCT1__ORDER_DETAILS, Product1.class,
            msgs);
      }
      return basicSetProduct((Product1)otherEnd, msgs);
    }
    return super.eInverseAdd(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case Model1Package.ORDER_DETAIL__ORDER:
      return basicSetOrder(null, msgs);
    case Model1Package.ORDER_DETAIL__PRODUCT:
      return basicSetProduct(null, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs)
  {
    switch (eContainerFeatureID)
    {
    case Model1Package.ORDER_DETAIL__ORDER:
      return eInternalContainer().eInverseRemove(this, Model1Package.ORDER__ORDER_DETAILS, Order.class, msgs);
    }
    return super.eBasicRemoveFromContainerFeature(msgs);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case Model1Package.ORDER_DETAIL__ORDER:
      return getOrder();
    case Model1Package.ORDER_DETAIL__PRODUCT:
      if (resolve)
      {
        return getProduct();
      }
      return basicGetProduct();
    case Model1Package.ORDER_DETAIL__PRICE:
      return new Float(getPrice());
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case Model1Package.ORDER_DETAIL__ORDER:
      setOrder((Order)newValue);
      return;
    case Model1Package.ORDER_DETAIL__PRODUCT:
      setProduct((Product1)newValue);
      return;
    case Model1Package.ORDER_DETAIL__PRICE:
      setPrice(((Float)newValue).floatValue());
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case Model1Package.ORDER_DETAIL__ORDER:
      setOrder((Order)null);
      return;
    case Model1Package.ORDER_DETAIL__PRODUCT:
      setProduct((Product1)null);
      return;
    case Model1Package.ORDER_DETAIL__PRICE:
      setPrice(PRICE_EDEFAULT);
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case Model1Package.ORDER_DETAIL__ORDER:
      return getOrder() != null;
    case Model1Package.ORDER_DETAIL__PRODUCT:
      return product != null;
    case Model1Package.ORDER_DETAIL__PRICE:
      return price != PRICE_EDEFAULT;
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy())
    {
      return super.toString();
    }

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (price: ");
    result.append(price);
    result.append(')');
    return result.toString();
  }

} // OrderDetailImpl
