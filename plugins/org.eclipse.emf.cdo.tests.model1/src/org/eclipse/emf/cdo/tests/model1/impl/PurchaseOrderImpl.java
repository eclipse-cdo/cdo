/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.tests.model1.impl;

import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;

import org.eclipse.emf.ecore.EClass;

import java.util.Date;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Purchase Order</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model1.impl.PurchaseOrderImpl#getDate <em>Date</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model1.impl.PurchaseOrderImpl#getSupplier <em>Supplier</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class PurchaseOrderImpl extends OrderImpl implements PurchaseOrder
{
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
    return (Date)eGet(Model1Package.Literals.PURCHASE_ORDER__DATE, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setDate(Date newDate)
  {
    eSet(Model1Package.Literals.PURCHASE_ORDER__DATE, newDate);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Supplier getSupplier()
  {
    return (Supplier)eGet(Model1Package.Literals.PURCHASE_ORDER__SUPPLIER, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setSupplier(Supplier newSupplier)
  {
    eSet(Model1Package.Literals.PURCHASE_ORDER__SUPPLIER, newSupplier);
  }

} // PurchaseOrderImpl
