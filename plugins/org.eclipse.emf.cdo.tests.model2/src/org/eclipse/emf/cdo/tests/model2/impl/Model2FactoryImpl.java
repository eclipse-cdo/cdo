/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model2.impl;

import org.eclipse.emf.cdo.tests.model2.Model2Factory;
import org.eclipse.emf.cdo.tests.model2.Model2Package;
import org.eclipse.emf.cdo.tests.model2.NotUnsettable;
import org.eclipse.emf.cdo.tests.model2.NotUnsettableWithDefault;
import org.eclipse.emf.cdo.tests.model2.PersistentContainment;
import org.eclipse.emf.cdo.tests.model2.SpecialPurchaseOrder;
import org.eclipse.emf.cdo.tests.model2.Task;
import org.eclipse.emf.cdo.tests.model2.TaskContainer;
import org.eclipse.emf.cdo.tests.model2.TransientContainer;
import org.eclipse.emf.cdo.tests.model2.Unsettable1;
import org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!-- end-user-doc -->
 * 
 * @generated
 */
public class Model2FactoryImpl extends EFactoryImpl implements Model2Factory
{
  /**
   * Creates the default factory implementation. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public static Model2Factory init()
  {
    try
    {
      Model2Factory theModel2Factory = (Model2Factory)EPackage.Registry.INSTANCE
          .getEFactory("http://www.eclipse.org/emf/CDO/tests/model2/1.0.0");
      if (theModel2Factory != null)
      {
        return theModel2Factory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new Model2FactoryImpl();
  }

  /**
   * Creates an instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Model2FactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
    case Model2Package.SPECIAL_PURCHASE_ORDER:
      return createSpecialPurchaseOrder();
    case Model2Package.TASK_CONTAINER:
      return createTaskContainer();
    case Model2Package.TASK:
      return createTask();
    case Model2Package.UNSETTABLE1:
      return createUnsettable1();
    case Model2Package.UNSETTABLE2_WITH_DEFAULT:
      return createUnsettable2WithDefault();
    case Model2Package.PERSISTENT_CONTAINMENT:
      return createPersistentContainment();
    case Model2Package.TRANSIENT_CONTAINER:
      return createTransientContainer();
    case Model2Package.NOT_UNSETTABLE:
      return createNotUnsettable();
    case Model2Package.NOT_UNSETTABLE_WITH_DEFAULT:
      return createNotUnsettableWithDefault();
    default:
      throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public SpecialPurchaseOrder createSpecialPurchaseOrder()
  {
    SpecialPurchaseOrderImpl specialPurchaseOrder = new SpecialPurchaseOrderImpl();
    return specialPurchaseOrder;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public TaskContainer createTaskContainer()
  {
    TaskContainerImpl taskContainer = new TaskContainerImpl();
    return taskContainer;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Task createTask()
  {
    TaskImpl task = new TaskImpl();
    return task;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Unsettable1 createUnsettable1()
  {
    Unsettable1Impl unsettable1 = new Unsettable1Impl();
    return unsettable1;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Unsettable2WithDefault createUnsettable2WithDefault()
  {
    Unsettable2WithDefaultImpl unsettable2WithDefault = new Unsettable2WithDefaultImpl();
    return unsettable2WithDefault;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public PersistentContainment createPersistentContainment()
  {
    PersistentContainmentImpl persistentContainment = new PersistentContainmentImpl();
    return persistentContainment;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public TransientContainer createTransientContainer()
  {
    TransientContainerImpl transientContainer = new TransientContainerImpl();
    return transientContainer;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public NotUnsettable createNotUnsettable()
  {
    NotUnsettableImpl notUnsettable = new NotUnsettableImpl();
    return notUnsettable;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public NotUnsettableWithDefault createNotUnsettableWithDefault()
  {
    NotUnsettableWithDefaultImpl notUnsettableWithDefault = new NotUnsettableWithDefaultImpl();
    return notUnsettableWithDefault;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Model2Package getModel2Package()
  {
    return (Model2Package)getEPackage();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @deprecated
   * @generated
   */
  @Deprecated
  public static Model2Package getPackage()
  {
    return Model2Package.eINSTANCE;
  }

} // Model2FactoryImpl
