/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model2;

import org.eclipse.emf.cdo.tests.model1.Model1Package;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc --> The <b>Package</b> for the model. It contains accessors for the meta objects to represent
 * <ul>
 * <li>each class,</li>
 * <li>each feature of each class,</li>
 * <li>each enum,</li>
 * <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.tests.model2.Model2Factory
 * @model kind="package"
 * @generated
 */
public interface Model2Package extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "model2";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://www.eclipse.org/emf/CDO/tests/model2/1.0.0";

  /**
   * The package namespace name.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "model2";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  Model2Package eINSTANCE = org.eclipse.emf.cdo.tests.model2.impl.Model2PackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model2.impl.SpecialPurchaseOrderImpl <em>Special Purchase Order</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model2.impl.SpecialPurchaseOrderImpl
   * @see org.eclipse.emf.cdo.tests.model2.impl.Model2PackageImpl#getSpecialPurchaseOrder()
   * @generated
   */
  int SPECIAL_PURCHASE_ORDER = 0;

  /**
   * The feature id for the '<em><b>Order Details</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int SPECIAL_PURCHASE_ORDER__ORDER_DETAILS = Model1Package.PURCHASE_ORDER__ORDER_DETAILS;

  /**
   * The feature id for the '<em><b>Date</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SPECIAL_PURCHASE_ORDER__DATE = Model1Package.PURCHASE_ORDER__DATE;

  /**
   * The feature id for the '<em><b>Supplier</b></em>' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SPECIAL_PURCHASE_ORDER__SUPPLIER = Model1Package.PURCHASE_ORDER__SUPPLIER;

  /**
   * The feature id for the '<em><b>Discount Code</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SPECIAL_PURCHASE_ORDER__DISCOUNT_CODE = Model1Package.PURCHASE_ORDER_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Shipping Address</b></em>' containment reference.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int SPECIAL_PURCHASE_ORDER__SHIPPING_ADDRESS = Model1Package.PURCHASE_ORDER_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Special Purchase Order</em>' class.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int SPECIAL_PURCHASE_ORDER_FEATURE_COUNT = Model1Package.PURCHASE_ORDER_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model2.impl.TaskContainerImpl <em>Task Container</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model2.impl.TaskContainerImpl
   * @see org.eclipse.emf.cdo.tests.model2.impl.Model2PackageImpl#getTaskContainer()
   * @generated
   */
  int TASK_CONTAINER = 1;

  /**
   * The feature id for the '<em><b>Tasks</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int TASK_CONTAINER__TASKS = 0;

  /**
   * The number of structural features of the '<em>Task Container</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int TASK_CONTAINER_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model2.impl.TaskImpl <em>Task</em>}' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.tests.model2.impl.TaskImpl
   * @see org.eclipse.emf.cdo.tests.model2.impl.Model2PackageImpl#getTask()
   * @generated
   */
  int TASK = 2;

  /**
   * The feature id for the '<em><b>Task Container</b></em>' container reference.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int TASK__TASK_CONTAINER = 0;

  /**
   * The feature id for the '<em><b>Description</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TASK__DESCRIPTION = 1;

  /**
   * The feature id for the '<em><b>Done</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TASK__DONE = 2;

  /**
   * The number of structural features of the '<em>Task</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TASK_FEATURE_COUNT = 3;

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model2.SpecialPurchaseOrder <em>Special Purchase Order</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>Special Purchase Order</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.SpecialPurchaseOrder
   * @generated
   */
  EClass getSpecialPurchaseOrder();

  /**
   * Returns the meta object for the attribute '
   * {@link org.eclipse.emf.cdo.tests.model2.SpecialPurchaseOrder#getDiscountCode <em>Discount Code</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Discount Code</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.SpecialPurchaseOrder#getDiscountCode()
   * @see #getSpecialPurchaseOrder()
   * @generated
   */
  EAttribute getSpecialPurchaseOrder_DiscountCode();

  /**
   * Returns the meta object for the containment reference '
   * {@link org.eclipse.emf.cdo.tests.model2.SpecialPurchaseOrder#getShippingAddress <em>Shipping Address</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the containment reference '<em>Shipping Address</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.SpecialPurchaseOrder#getShippingAddress()
   * @see #getSpecialPurchaseOrder()
   * @generated
   */
  EReference getSpecialPurchaseOrder_ShippingAddress();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model2.TaskContainer <em>Task Container</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>Task Container</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.TaskContainer
   * @generated
   */
  EClass getTaskContainer();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.tests.model2.TaskContainer#getTasks <em>Tasks</em>}'.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return the meta object for the containment reference list '<em>Tasks</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.TaskContainer#getTasks()
   * @see #getTaskContainer()
   * @generated
   */
  EReference getTaskContainer_Tasks();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model2.Task <em>Task</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Task</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.Task
   * @generated
   */
  EClass getTask();

  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.tests.model2.Task#getTaskContainer <em>Task Container</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Task Container</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.Task#getTaskContainer()
   * @see #getTask()
   * @generated
   */
  EReference getTask_TaskContainer();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model2.Task#getDescription <em>Description</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Description</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.Task#getDescription()
   * @see #getTask()
   * @generated
   */
  EAttribute getTask_Description();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model2.Task#isDone <em>Done</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Done</em>'.
   * @see org.eclipse.emf.cdo.tests.model2.Task#isDone()
   * @see #getTask()
   * @generated
   */
  EAttribute getTask_Done();

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  Model2Factory getModel2Factory();

  /**
   * <!-- begin-user-doc --> Defines literals for the meta objects that represent
   * <ul>
   * <li>each class,</li>
   * <li>each feature of each class,</li>
   * <li>each enum,</li>
   * <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
   * @generated
   */
  interface Literals
  {
    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model2.impl.SpecialPurchaseOrderImpl <em>Special Purchase Order</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.model2.impl.SpecialPurchaseOrderImpl
     * @see org.eclipse.emf.cdo.tests.model2.impl.Model2PackageImpl#getSpecialPurchaseOrder()
     * @generated
     */
    EClass SPECIAL_PURCHASE_ORDER = eINSTANCE.getSpecialPurchaseOrder();

    /**
     * The meta object literal for the '<em><b>Discount Code</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    EAttribute SPECIAL_PURCHASE_ORDER__DISCOUNT_CODE = eINSTANCE.getSpecialPurchaseOrder_DiscountCode();

    /**
     * The meta object literal for the '<em><b>Shipping Address</b></em>' containment reference feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference SPECIAL_PURCHASE_ORDER__SHIPPING_ADDRESS = eINSTANCE.getSpecialPurchaseOrder_ShippingAddress();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model2.impl.TaskContainerImpl <em>Task Container</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.model2.impl.TaskContainerImpl
     * @see org.eclipse.emf.cdo.tests.model2.impl.Model2PackageImpl#getTaskContainer()
     * @generated
     */
    EClass TASK_CONTAINER = eINSTANCE.getTaskContainer();

    /**
     * The meta object literal for the '<em><b>Tasks</b></em>' containment reference list feature.
     * <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * @generated
     */
    EReference TASK_CONTAINER__TASKS = eINSTANCE.getTaskContainer_Tasks();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model2.impl.TaskImpl <em>Task</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.model2.impl.TaskImpl
     * @see org.eclipse.emf.cdo.tests.model2.impl.Model2PackageImpl#getTask()
     * @generated
     */
    EClass TASK = eINSTANCE.getTask();

    /**
     * The meta object literal for the '<em><b>Task Container</b></em>' container reference feature.
     * <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * @generated
     */
    EReference TASK__TASK_CONTAINER = eINSTANCE.getTask_TaskContainer();

    /**
     * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    EAttribute TASK__DESCRIPTION = eINSTANCE.getTask_Description();

    /**
     * The meta object literal for the '<em><b>Done</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    EAttribute TASK__DONE = eINSTANCE.getTask_Done();

  }

} // Model2Package
