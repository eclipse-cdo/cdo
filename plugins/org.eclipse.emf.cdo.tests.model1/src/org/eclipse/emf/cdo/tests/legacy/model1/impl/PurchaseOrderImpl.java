/**
 * <copyright>
 * </copyright>
 *
 * $Id: PurchaseOrderImpl.java,v 1.2 2008-09-18 12:57:07 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.legacy.model1.impl;

import org.eclipse.emf.cdo.tests.legacy.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

import java.util.Date;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Purchase Order</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.legacy.model1.impl.PurchaseOrderImpl#getDate <em>Date</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.legacy.model1.impl.PurchaseOrderImpl#getSupplier <em>Supplier</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class PurchaseOrderImpl extends OrderImpl implements PurchaseOrder
{
  /**
   * The default value of the '{@link #getDate() <em>Date</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @see #getDate()
   * @generated
   * @ordered
   */
  protected static final Date DATE_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getDate() <em>Date</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getDate()
   * @generated
   * @ordered
   */
  protected Date date = DATE_EDEFAULT;

  /**
   * The cached value of the '{@link #getSupplier() <em>Supplier</em>}' reference. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @see #getSupplier()
   * @generated
   * @ordered
   */
  protected Supplier supplier;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected PurchaseOrderImpl()
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
    return Model1Package.Literals.PURCHASE_ORDER;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Date getDate()
  {
    eFireRead(Model1Package.PURCHASE_ORDER__DATE);
    return date;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setDate(Date newDate)
  {
    eFireWrite(Model1Package.PURCHASE_ORDER__DATE);
    Date oldDate = date;
    date = newDate;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model1Package.PURCHASE_ORDER__DATE, oldDate, date));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Supplier getSupplier()
  {
    eFireRead(Model1Package.PURCHASE_ORDER__SUPPLIER);
    if (supplier != null && supplier.eIsProxy())
    {
      InternalEObject oldSupplier = (InternalEObject)supplier;
      supplier = (Supplier)eResolveProxy(oldSupplier);
      if (supplier != oldSupplier)
      {
        if (eNotificationRequired())
        {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, Model1Package.PURCHASE_ORDER__SUPPLIER,
              oldSupplier, supplier));
        }
      }
    }
    return supplier;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Supplier basicGetSupplier()
  {
    eFireRead(Model1Package.PURCHASE_ORDER__SUPPLIER);
    return supplier;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public NotificationChain basicSetSupplier(Supplier newSupplier, NotificationChain msgs)
  {
    eFireWrite(Model1Package.PURCHASE_ORDER__SUPPLIER);
    Supplier oldSupplier = supplier;
    supplier = newSupplier;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
          Model1Package.PURCHASE_ORDER__SUPPLIER, oldSupplier, newSupplier);
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
  public void setSupplier(Supplier newSupplier)
  {
    eFireWrite(Model1Package.PURCHASE_ORDER__SUPPLIER);
    if (newSupplier != supplier)
    {
      NotificationChain msgs = null;
      if (supplier != null)
      {
        msgs = ((InternalEObject)supplier).eInverseRemove(this, Model1Package.SUPPLIER__PURCHASE_ORDERS,
            Supplier.class, msgs);
      }
      if (newSupplier != null)
      {
        msgs = ((InternalEObject)newSupplier).eInverseAdd(this, Model1Package.SUPPLIER__PURCHASE_ORDERS,
            Supplier.class, msgs);
      }
      msgs = basicSetSupplier(newSupplier, msgs);
      if (msgs != null)
      {
        msgs.dispatch();
      }
    }
    else if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model1Package.PURCHASE_ORDER__SUPPLIER, newSupplier,
          newSupplier));
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
    case Model1Package.PURCHASE_ORDER__SUPPLIER:
      if (supplier != null)
      {
        msgs = ((InternalEObject)supplier).eInverseRemove(this, Model1Package.SUPPLIER__PURCHASE_ORDERS,
            Supplier.class, msgs);
      }
      return basicSetSupplier((Supplier)otherEnd, msgs);
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
    case Model1Package.PURCHASE_ORDER__SUPPLIER:
      return basicSetSupplier(null, msgs);
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
    case Model1Package.PURCHASE_ORDER__DATE:
      return getDate();
    case Model1Package.PURCHASE_ORDER__SUPPLIER:
      if (resolve)
      {
        return getSupplier();
      }
      return basicGetSupplier();
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
    case Model1Package.PURCHASE_ORDER__DATE:
      setDate((Date)newValue);
      return;
    case Model1Package.PURCHASE_ORDER__SUPPLIER:
      setSupplier((Supplier)newValue);
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
    case Model1Package.PURCHASE_ORDER__DATE:
      setDate(DATE_EDEFAULT);
      return;
    case Model1Package.PURCHASE_ORDER__SUPPLIER:
      setSupplier((Supplier)null);
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
    case Model1Package.PURCHASE_ORDER__DATE:
      return DATE_EDEFAULT == null ? date != null : !DATE_EDEFAULT.equals(date);
    case Model1Package.PURCHASE_ORDER__SUPPLIER:
      return supplier != null;
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
    result.append(" (date: ");
    result.append(date);
    result.append(')');
    return result.toString();
  }

} // PurchaseOrderImpl
