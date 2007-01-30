/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.tests.model1.impl;

import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Model1Package;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.internal.cdo.CDOObjectImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Company</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model1.impl.CompanyImpl#getCategories <em>Categories</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model1.impl.CompanyImpl#getSuppliers <em>Suppliers</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model1.impl.CompanyImpl#getPurchaseOrders <em>Purchase Orders</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class CompanyImpl extends CDOObjectImpl implements Company
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected CompanyImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected EClass eStaticClass()
  {
    return Model1Package.Literals.COMPANY;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EList getCategories()
  {
    return (EList)eGet(Model1Package.Literals.COMPANY__CATEGORIES, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EList getSuppliers()
  {
    return (EList)eGet(Model1Package.Literals.COMPANY__SUPPLIERS, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EList getPurchaseOrders()
  {
    return (EList)eGet(Model1Package.Literals.COMPANY__PURCHASE_ORDERS, true);
  }

} // CompanyImpl
