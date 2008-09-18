/**
 * <copyright>
 * </copyright>
 *
 * $Id: Product1Impl.java,v 1.2 2008-09-18 12:57:08 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.legacy.model1.impl;

import org.eclipse.emf.cdo.tests.legacy.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.VAT;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

import java.util.Collection;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Product1</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.legacy.model1.impl.Product1Impl#getName <em>Name</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.legacy.model1.impl.Product1Impl#getOrderDetails <em>Order Details</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.legacy.model1.impl.Product1Impl#getVat <em>Vat</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class Product1Impl extends EObjectImpl implements Product1
{
  /**
   * The default value of the '{@link #getName() <em>Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @see #getName()
   * @generated
   * @ordered
   */
  protected static final String NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getName() <em>Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getName()
   * @generated
   * @ordered
   */
  protected String name = NAME_EDEFAULT;

  /**
   * The cached value of the '{@link #getOrderDetails() <em>Order Details</em>}' reference list. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @see #getOrderDetails()
   * @generated
   * @ordered
   */
  protected EList<OrderDetail> orderDetails;

  /**
   * The default value of the '{@link #getVat() <em>Vat</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getVat()
   * @generated
   * @ordered
   */
  protected static final VAT VAT_EDEFAULT = VAT.VAT15;

  /**
   * The cached value of the '{@link #getVat() <em>Vat</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getVat()
   * @generated
   * @ordered
   */
  protected VAT vat = VAT_EDEFAULT;

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
  public String getName()
  {
    eFireRead(Model1Package.PRODUCT1__NAME);
    return name;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setName(String newName)
  {
    eFireWrite(Model1Package.PRODUCT1__NAME);
    String oldName = name;
    name = newName;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model1Package.PRODUCT1__NAME, oldName, name));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EList<OrderDetail> getOrderDetails()
  {
    if (orderDetails == null)
    {
      orderDetails = new EObjectWithInverseResolvingEList<OrderDetail>(OrderDetail.class, this,
          Model1Package.PRODUCT1__ORDER_DETAILS, Model1Package.ORDER_DETAIL__PRODUCT).readWriteFiringList();
    }
    return orderDetails;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public VAT getVat()
  {
    eFireRead(Model1Package.PRODUCT1__VAT);
    return vat;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setVat(VAT newVat)
  {
    eFireWrite(Model1Package.PRODUCT1__VAT);
    VAT oldVat = vat;
    vat = newVat == null ? VAT_EDEFAULT : newVat;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model1Package.PRODUCT1__VAT, oldVat, vat));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case Model1Package.PRODUCT1__ORDER_DETAILS:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getOrderDetails()).basicAdd(otherEnd, msgs);
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
    case Model1Package.PRODUCT1__ORDER_DETAILS:
      return ((InternalEList<?>)getOrderDetails()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
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
    case Model1Package.PRODUCT1__NAME:
      return getName();
    case Model1Package.PRODUCT1__ORDER_DETAILS:
      return getOrderDetails();
    case Model1Package.PRODUCT1__VAT:
      return getVat();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case Model1Package.PRODUCT1__NAME:
      setName((String)newValue);
      return;
    case Model1Package.PRODUCT1__ORDER_DETAILS:
      getOrderDetails().clear();
      getOrderDetails().addAll((Collection<? extends OrderDetail>)newValue);
      return;
    case Model1Package.PRODUCT1__VAT:
      setVat((VAT)newValue);
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
    case Model1Package.PRODUCT1__NAME:
      setName(NAME_EDEFAULT);
      return;
    case Model1Package.PRODUCT1__ORDER_DETAILS:
      getOrderDetails().clear();
      return;
    case Model1Package.PRODUCT1__VAT:
      setVat(VAT_EDEFAULT);
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
    case Model1Package.PRODUCT1__NAME:
      return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
    case Model1Package.PRODUCT1__ORDER_DETAILS:
      return orderDetails != null && !orderDetails.isEmpty();
    case Model1Package.PRODUCT1__VAT:
      return vat != VAT_EDEFAULT;
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
    result.append(" (name: ");
    result.append(name);
    result.append(", vat: ");
    result.append(vat);
    result.append(')');
    return result.toString();
  }

} // Product1Impl
