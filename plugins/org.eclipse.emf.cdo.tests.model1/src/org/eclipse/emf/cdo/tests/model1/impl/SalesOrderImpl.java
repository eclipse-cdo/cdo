/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.tests.model1.impl;

import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Sales Order</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model1.impl.SalesOrderImpl#getId <em>Id</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model1.impl.SalesOrderImpl#getCustomer <em>Customer</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class SalesOrderImpl extends OrderImpl implements SalesOrder
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected SalesOrderImpl()
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
    return Model1Package.Literals.SALES_ORDER;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public int getId()
  {
    return ((Integer)eGet(Model1Package.Literals.SALES_ORDER__ID, true)).intValue();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setId(int newId)
  {
    eSet(Model1Package.Literals.SALES_ORDER__ID, new Integer(newId));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Customer getCustomer()
  {
    return (Customer)eGet(Model1Package.Literals.SALES_ORDER__CUSTOMER, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setCustomer(Customer newCustomer)
  {
    eSet(Model1Package.Literals.SALES_ORDER__CUSTOMER, newCustomer);
  }

} // SalesOrderImpl
