/*
 * Copyright (c) 2013, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model2.legacy.impl;

import org.eclipse.emf.cdo.tests.model1.Address;
import org.eclipse.emf.cdo.tests.model1.VAT;
import org.eclipse.emf.cdo.tests.model2.EnumListHolder;
import org.eclipse.emf.cdo.tests.model2.MapHolder;
import org.eclipse.emf.cdo.tests.model2.NotUnsettable;
import org.eclipse.emf.cdo.tests.model2.NotUnsettableWithDefault;
import org.eclipse.emf.cdo.tests.model2.PersistentContainment;
import org.eclipse.emf.cdo.tests.model2.SpecialPurchaseOrder;
import org.eclipse.emf.cdo.tests.model2.Task;
import org.eclipse.emf.cdo.tests.model2.TaskContainer;
import org.eclipse.emf.cdo.tests.model2.TransientContainer;
import org.eclipse.emf.cdo.tests.model2.Unsettable1;
import org.eclipse.emf.cdo.tests.model2.Unsettable2WithDefault;
import org.eclipse.emf.cdo.tests.model2.legacy.Model2Factory;
import org.eclipse.emf.cdo.tests.model2.legacy.Model2Package;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

import java.util.Map;

//import org.eclipse.emf.cdo.tests.model2.*;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!-- end-user-doc -->
 * @generated
 */
public class Model2FactoryImpl extends EFactoryImpl implements Model2Factory
{
  /**
   * Creates the default factory implementation. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  public static Model2Factory init()
  {
    try
    {
      Model2Factory theModel2Factory = (Model2Factory)EPackage.Registry.INSTANCE.getEFactory("http://www.eclipse.org/emf/CDO/tests/legacy/model2/1.0.0");
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
   * Creates an instance of the factory.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public Model2FactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
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
    case Model2Package.MAP_HOLDER:
      return createMapHolder();
    case Model2Package.STRING_TO_STRING_MAP:
      return (EObject)createStringToStringMap();
    case Model2Package.INTEGER_TO_STRING_MAP:
      return (EObject)createIntegerToStringMap();
    case Model2Package.STRING_TO_VAT_MAP:
      return (EObject)createStringToVATMap();
    case Model2Package.STRING_TO_ADDRESS_CONTAINMENT_MAP:
      return (EObject)createStringToAddressContainmentMap();
    case Model2Package.STRING_TO_ADDRESS_REFERENCE_MAP:
      return (EObject)createStringToAddressReferenceMap();
    case Model2Package.EOBJECT_TO_EOBJECT_MAP:
      return (EObject)createEObjectToEObjectMap();
    case Model2Package.EOBJECT_TO_EOBJECT_KEY_CONTAINED_MAP:
      return (EObject)createEObjectToEObjectKeyContainedMap();
    case Model2Package.EOBJECT_TO_EOBJECT_BOTH_CONTAINED_MAP:
      return (EObject)createEObjectToEObjectBothContainedMap();
    case Model2Package.EOBJECT_TO_EOBJECT_VALUE_CONTAINED_MAP:
      return (EObject)createEObjectToEObjectValueContainedMap();
    case Model2Package.ENUM_LIST_HOLDER:
      return createEnumListHolder();
    default:
      throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public SpecialPurchaseOrder createSpecialPurchaseOrder()
  {
    SpecialPurchaseOrderImpl specialPurchaseOrder = new SpecialPurchaseOrderImpl();
    return specialPurchaseOrder;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public TaskContainer createTaskContainer()
  {
    TaskContainerImpl taskContainer = new TaskContainerImpl();
    return taskContainer;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Task createTask()
  {
    TaskImpl task = new TaskImpl();
    return task;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Unsettable1 createUnsettable1()
  {
    Unsettable1Impl unsettable1 = new Unsettable1Impl();
    return unsettable1;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Unsettable2WithDefault createUnsettable2WithDefault()
  {
    Unsettable2WithDefaultImpl unsettable2WithDefault = new Unsettable2WithDefaultImpl();
    return unsettable2WithDefault;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public PersistentContainment createPersistentContainment()
  {
    PersistentContainmentImpl persistentContainment = new PersistentContainmentImpl();
    return persistentContainment;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public TransientContainer createTransientContainer()
  {
    TransientContainerImpl transientContainer = new TransientContainerImpl();
    return transientContainer;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotUnsettable createNotUnsettable()
  {
    NotUnsettableImpl notUnsettable = new NotUnsettableImpl();
    return notUnsettable;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotUnsettableWithDefault createNotUnsettableWithDefault()
  {
    NotUnsettableWithDefaultImpl notUnsettableWithDefault = new NotUnsettableWithDefaultImpl();
    return notUnsettableWithDefault;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public MapHolder createMapHolder()
  {
    MapHolderImpl mapHolder = new MapHolderImpl();
    return mapHolder;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public Map.Entry<String, String> createStringToStringMap()
  {
    StringToStringMapImpl stringToStringMap = new StringToStringMapImpl();
    return stringToStringMap;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public Map.Entry<Integer, String> createIntegerToStringMap()
  {
    IntegerToStringMapImpl integerToStringMap = new IntegerToStringMapImpl();
    return integerToStringMap;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public Map.Entry<String, VAT> createStringToVATMap()
  {
    StringToVATMapImpl stringToVATMap = new StringToVATMapImpl();
    return stringToVATMap;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public Map.Entry<String, Address> createStringToAddressContainmentMap()
  {
    StringToAddressContainmentMapImpl stringToAddressContainmentMap = new StringToAddressContainmentMapImpl();
    return stringToAddressContainmentMap;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public Map.Entry<String, Address> createStringToAddressReferenceMap()
  {
    StringToAddressReferenceMapImpl stringToAddressReferenceMap = new StringToAddressReferenceMapImpl();
    return stringToAddressReferenceMap;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public Map.Entry<EObject, EObject> createEObjectToEObjectMap()
  {
    EObjectToEObjectMapImpl eObjectToEObjectMap = new EObjectToEObjectMapImpl();
    return eObjectToEObjectMap;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public Map.Entry<EObject, EObject> createEObjectToEObjectKeyContainedMap()
  {
    EObjectToEObjectKeyContainedMapImpl eObjectToEObjectKeyContainedMap = new EObjectToEObjectKeyContainedMapImpl();
    return eObjectToEObjectKeyContainedMap;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public Map.Entry<EObject, EObject> createEObjectToEObjectBothContainedMap()
  {
    EObjectToEObjectBothContainedMapImpl eObjectToEObjectBothContainedMap = new EObjectToEObjectBothContainedMapImpl();
    return eObjectToEObjectBothContainedMap;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public Map.Entry<EObject, EObject> createEObjectToEObjectValueContainedMap()
  {
    EObjectToEObjectValueContainedMapImpl eObjectToEObjectValueContainedMap = new EObjectToEObjectValueContainedMapImpl();
    return eObjectToEObjectValueContainedMap;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EnumListHolder createEnumListHolder()
  {
    EnumListHolderImpl enumListHolder = new EnumListHolderImpl();
    return enumListHolder;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Model2Package getModel2Package()
  {
    return (Model2Package)getEPackage();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static Model2Package getPackage()
  {
    return Model2Package.eINSTANCE;
  }

} // Model2FactoryImpl
