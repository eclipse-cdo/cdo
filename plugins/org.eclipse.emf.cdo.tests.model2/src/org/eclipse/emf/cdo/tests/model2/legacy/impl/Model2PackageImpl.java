/*
 * Copyright (c) 2013, 2015, 2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.tests.model1.legacy.Model1Package;
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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;

import java.util.Map;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Package</b>. <!-- end-user-doc -->
 * @generated
 */
public class Model2PackageImpl extends EPackageImpl implements Model2Package
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass specialPurchaseOrderEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass taskContainerEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass taskEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass unsettable1EClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass unsettable2WithDefaultEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass persistentContainmentEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass transientContainerEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass notUnsettableEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass notUnsettableWithDefaultEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass mapHolderEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass stringToStringMapEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass integerToStringMapEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass stringToVATMapEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass stringToAddressContainmentMapEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass stringToAddressReferenceMapEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass eObjectToEObjectMapEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass eObjectToEObjectKeyContainedMapEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass eObjectToEObjectBothContainedMapEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass eObjectToEObjectValueContainedMapEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass enumListHolderEClass = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with {@link org.eclipse.emf.ecore.EPackage.Registry
   * EPackage.Registry} by the package package URI value.
   * <p>
   * Note: the correct way to create the package is via the static factory method {@link #init init()}, which also
   * performs initialization of the package, or returns the registered package, if one already exists. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see org.eclipse.emf.cdo.tests.legacy.model2.Model2Package#eNS_URI
   * @see #init()
   * @generated
   */
  private Model2PackageImpl()
  {
    super(eNS_URI, Model2Factory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   *
   * <p>This method is used to initialize {@link Model2Package#eINSTANCE} when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static Model2Package init()
  {
    if (isInited)
    {
      return (Model2Package)EPackage.Registry.INSTANCE.getEPackage(Model2Package.eNS_URI);
    }

    // Obtain or create and register package
    Object registeredModel2Package = EPackage.Registry.INSTANCE.get(eNS_URI);
    Model2PackageImpl theModel2Package = registeredModel2Package instanceof Model2PackageImpl ? (Model2PackageImpl)registeredModel2Package
        : new Model2PackageImpl();

    isInited = true;

    // Initialize simple dependencies
    Model1Package.eINSTANCE.eClass();

    // Create package meta-data objects
    theModel2Package.createPackageContents();

    // Initialize created meta-data
    theModel2Package.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theModel2Package.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(Model2Package.eNS_URI, theModel2Package);
    return theModel2Package;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getSpecialPurchaseOrder()
  {
    return specialPurchaseOrderEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getSpecialPurchaseOrder_DiscountCode()
  {
    return (EAttribute)specialPurchaseOrderEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getSpecialPurchaseOrder_ShippingAddress()
  {
    return (EReference)specialPurchaseOrderEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getTaskContainer()
  {
    return taskContainerEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getTaskContainer_Tasks()
  {
    return (EReference)taskContainerEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getTask()
  {
    return taskEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getTask_TaskContainer()
  {
    return (EReference)taskEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getTask_Description()
  {
    return (EAttribute)taskEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getTask_Done()
  {
    return (EAttribute)taskEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getUnsettable1()
  {
    return unsettable1EClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUnsettable1_UnsettableBoolean()
  {
    return (EAttribute)unsettable1EClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUnsettable1_UnsettableByte()
  {
    return (EAttribute)unsettable1EClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUnsettable1_UnsettableChar()
  {
    return (EAttribute)unsettable1EClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUnsettable1_UnsettableDate()
  {
    return (EAttribute)unsettable1EClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUnsettable1_UnsettableDouble()
  {
    return (EAttribute)unsettable1EClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUnsettable1_UnsettableFloat()
  {
    return (EAttribute)unsettable1EClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUnsettable1_UnsettableInt()
  {
    return (EAttribute)unsettable1EClass.getEStructuralFeatures().get(6);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUnsettable1_UnsettableLong()
  {
    return (EAttribute)unsettable1EClass.getEStructuralFeatures().get(7);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUnsettable1_UnsettableShort()
  {
    return (EAttribute)unsettable1EClass.getEStructuralFeatures().get(8);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUnsettable1_UnsettableString()
  {
    return (EAttribute)unsettable1EClass.getEStructuralFeatures().get(9);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUnsettable1_UnsettableVAT()
  {
    return (EAttribute)unsettable1EClass.getEStructuralFeatures().get(10);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getUnsettable1_UnsettableElement()
  {
    return (EReference)unsettable1EClass.getEStructuralFeatures().get(11);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getUnsettable2WithDefault()
  {
    return unsettable2WithDefaultEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUnsettable2WithDefault_UnsettableBoolean()
  {
    return (EAttribute)unsettable2WithDefaultEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUnsettable2WithDefault_UnsettableByte()
  {
    return (EAttribute)unsettable2WithDefaultEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUnsettable2WithDefault_UnsettableChar()
  {
    return (EAttribute)unsettable2WithDefaultEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUnsettable2WithDefault_UnsettableDate()
  {
    return (EAttribute)unsettable2WithDefaultEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUnsettable2WithDefault_UnsettableDouble()
  {
    return (EAttribute)unsettable2WithDefaultEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUnsettable2WithDefault_UnsettableFloat()
  {
    return (EAttribute)unsettable2WithDefaultEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUnsettable2WithDefault_UnsettableInt()
  {
    return (EAttribute)unsettable2WithDefaultEClass.getEStructuralFeatures().get(6);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUnsettable2WithDefault_UnsettableLong()
  {
    return (EAttribute)unsettable2WithDefaultEClass.getEStructuralFeatures().get(7);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUnsettable2WithDefault_UnsettableShort()
  {
    return (EAttribute)unsettable2WithDefaultEClass.getEStructuralFeatures().get(8);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUnsettable2WithDefault_UnsettableString()
  {
    return (EAttribute)unsettable2WithDefaultEClass.getEStructuralFeatures().get(9);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUnsettable2WithDefault_UnsettableVAT()
  {
    return (EAttribute)unsettable2WithDefaultEClass.getEStructuralFeatures().get(10);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getPersistentContainment()
  {
    return persistentContainmentEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getPersistentContainment_AttrBefore()
  {
    return (EAttribute)persistentContainmentEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getPersistentContainment_Children()
  {
    return (EReference)persistentContainmentEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getPersistentContainment_AttrAfter()
  {
    return (EAttribute)persistentContainmentEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getTransientContainer()
  {
    return transientContainerEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getTransientContainer_AttrBefore()
  {
    return (EAttribute)transientContainerEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getTransientContainer_Parent()
  {
    return (EReference)transientContainerEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getTransientContainer_AttrAfter()
  {
    return (EAttribute)transientContainerEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getNotUnsettable()
  {
    return notUnsettableEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getNotUnsettable_NotUnsettableBoolean()
  {
    return (EAttribute)notUnsettableEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getNotUnsettable_NotUnsettableByte()
  {
    return (EAttribute)notUnsettableEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getNotUnsettable_NotUnsettableChar()
  {
    return (EAttribute)notUnsettableEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getNotUnsettable_NotUnsettableDate()
  {
    return (EAttribute)notUnsettableEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getNotUnsettable_NotUnsettableDouble()
  {
    return (EAttribute)notUnsettableEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getNotUnsettable_NotUnsettableFloat()
  {
    return (EAttribute)notUnsettableEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getNotUnsettable_NotUnsettableInt()
  {
    return (EAttribute)notUnsettableEClass.getEStructuralFeatures().get(6);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getNotUnsettable_NotUnsettableLong()
  {
    return (EAttribute)notUnsettableEClass.getEStructuralFeatures().get(7);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getNotUnsettable_NotUnsettableShort()
  {
    return (EAttribute)notUnsettableEClass.getEStructuralFeatures().get(8);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getNotUnsettable_NotUnsettableString()
  {
    return (EAttribute)notUnsettableEClass.getEStructuralFeatures().get(9);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getNotUnsettable_NotUnsettableVAT()
  {
    return (EAttribute)notUnsettableEClass.getEStructuralFeatures().get(10);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getNotUnsettableWithDefault()
  {
    return notUnsettableWithDefaultEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getNotUnsettableWithDefault_NotUnsettableBoolean()
  {
    return (EAttribute)notUnsettableWithDefaultEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getNotUnsettableWithDefault_NotUnsettableByte()
  {
    return (EAttribute)notUnsettableWithDefaultEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getNotUnsettableWithDefault_NotUnsettableChar()
  {
    return (EAttribute)notUnsettableWithDefaultEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getNotUnsettableWithDefault_NotUnsettableDate()
  {
    return (EAttribute)notUnsettableWithDefaultEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getNotUnsettableWithDefault_NotUnsettableDouble()
  {
    return (EAttribute)notUnsettableWithDefaultEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getNotUnsettableWithDefault_NotUnsettableFloat()
  {
    return (EAttribute)notUnsettableWithDefaultEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getNotUnsettableWithDefault_NotUnsettableInt()
  {
    return (EAttribute)notUnsettableWithDefaultEClass.getEStructuralFeatures().get(6);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getNotUnsettableWithDefault_NotUnsettableLong()
  {
    return (EAttribute)notUnsettableWithDefaultEClass.getEStructuralFeatures().get(7);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getNotUnsettableWithDefault_NotUnsettableShort()
  {
    return (EAttribute)notUnsettableWithDefaultEClass.getEStructuralFeatures().get(8);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getNotUnsettableWithDefault_NotUnsettableString()
  {
    return (EAttribute)notUnsettableWithDefaultEClass.getEStructuralFeatures().get(9);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getNotUnsettableWithDefault_NotUnsettableVAT()
  {
    return (EAttribute)notUnsettableWithDefaultEClass.getEStructuralFeatures().get(10);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getMapHolder()
  {
    return mapHolderEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getMapHolder_IntegerToStringMap()
  {
    return (EReference)mapHolderEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getMapHolder_StringToStringMap()
  {
    return (EReference)mapHolderEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getMapHolder_StringToVATMap()
  {
    return (EReference)mapHolderEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getMapHolder_StringToAddressContainmentMap()
  {
    return (EReference)mapHolderEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getMapHolder_StringToAddressReferenceMap()
  {
    return (EReference)mapHolderEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getMapHolder_EObjectToEObjectMap()
  {
    return (EReference)mapHolderEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getMapHolder_EObjectToEObjectKeyContainedMap()
  {
    return (EReference)mapHolderEClass.getEStructuralFeatures().get(6);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getMapHolder_EObjectToEObjectBothContainedMap()
  {
    return (EReference)mapHolderEClass.getEStructuralFeatures().get(7);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getMapHolder_EObjectToEObjectValueContainedMap()
  {
    return (EReference)mapHolderEClass.getEStructuralFeatures().get(8);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getStringToStringMap()
  {
    return stringToStringMapEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getStringToStringMap_Key()
  {
    return (EAttribute)stringToStringMapEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getStringToStringMap_Value()
  {
    return (EAttribute)stringToStringMapEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getIntegerToStringMap()
  {
    return integerToStringMapEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getIntegerToStringMap_Key()
  {
    return (EAttribute)integerToStringMapEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getIntegerToStringMap_Value()
  {
    return (EAttribute)integerToStringMapEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getStringToVATMap()
  {
    return stringToVATMapEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getStringToVATMap_Key()
  {
    return (EAttribute)stringToVATMapEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getStringToVATMap_Value()
  {
    return (EAttribute)stringToVATMapEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getStringToAddressContainmentMap()
  {
    return stringToAddressContainmentMapEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getStringToAddressContainmentMap_Key()
  {
    return (EAttribute)stringToAddressContainmentMapEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getStringToAddressContainmentMap_Value()
  {
    return (EReference)stringToAddressContainmentMapEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getStringToAddressReferenceMap()
  {
    return stringToAddressReferenceMapEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getStringToAddressReferenceMap_Key()
  {
    return (EAttribute)stringToAddressReferenceMapEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getStringToAddressReferenceMap_Value()
  {
    return (EReference)stringToAddressReferenceMapEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getEObjectToEObjectMap()
  {
    return eObjectToEObjectMapEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getEObjectToEObjectMap_Key()
  {
    return (EReference)eObjectToEObjectMapEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getEObjectToEObjectMap_Value()
  {
    return (EReference)eObjectToEObjectMapEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getEObjectToEObjectKeyContainedMap()
  {
    return eObjectToEObjectKeyContainedMapEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getEObjectToEObjectKeyContainedMap_Key()
  {
    return (EReference)eObjectToEObjectKeyContainedMapEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getEObjectToEObjectKeyContainedMap_Value()
  {
    return (EReference)eObjectToEObjectKeyContainedMapEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getEObjectToEObjectBothContainedMap()
  {
    return eObjectToEObjectBothContainedMapEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getEObjectToEObjectBothContainedMap_Key()
  {
    return (EReference)eObjectToEObjectBothContainedMapEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getEObjectToEObjectBothContainedMap_Value()
  {
    return (EReference)eObjectToEObjectBothContainedMapEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getEObjectToEObjectValueContainedMap()
  {
    return eObjectToEObjectValueContainedMapEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getEObjectToEObjectValueContainedMap_Key()
  {
    return (EReference)eObjectToEObjectValueContainedMapEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getEObjectToEObjectValueContainedMap_Value()
  {
    return (EReference)eObjectToEObjectValueContainedMapEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getEnumListHolder()
  {
    return enumListHolderEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getEnumListHolder_EnumList()
  {
    return (EAttribute)enumListHolderEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Model2Factory getModel2Factory()
  {
    return (Model2Factory)getEFactoryInstance();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private boolean isCreated = false;

  /**
   * Creates the meta-model objects for the package.  This method is
   * guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public void createPackageContents()
  {
    if (isCreated)
    {
      return;
    }
    isCreated = true;

    // Create classes and their features
    specialPurchaseOrderEClass = createEClass(SPECIAL_PURCHASE_ORDER);
    createEAttribute(specialPurchaseOrderEClass, SPECIAL_PURCHASE_ORDER__DISCOUNT_CODE);
    createEReference(specialPurchaseOrderEClass, SPECIAL_PURCHASE_ORDER__SHIPPING_ADDRESS);

    taskContainerEClass = createEClass(TASK_CONTAINER);
    createEReference(taskContainerEClass, TASK_CONTAINER__TASKS);

    taskEClass = createEClass(TASK);
    createEReference(taskEClass, TASK__TASK_CONTAINER);
    createEAttribute(taskEClass, TASK__DESCRIPTION);
    createEAttribute(taskEClass, TASK__DONE);

    unsettable1EClass = createEClass(UNSETTABLE1);
    createEAttribute(unsettable1EClass, UNSETTABLE1__UNSETTABLE_BOOLEAN);
    createEAttribute(unsettable1EClass, UNSETTABLE1__UNSETTABLE_BYTE);
    createEAttribute(unsettable1EClass, UNSETTABLE1__UNSETTABLE_CHAR);
    createEAttribute(unsettable1EClass, UNSETTABLE1__UNSETTABLE_DATE);
    createEAttribute(unsettable1EClass, UNSETTABLE1__UNSETTABLE_DOUBLE);
    createEAttribute(unsettable1EClass, UNSETTABLE1__UNSETTABLE_FLOAT);
    createEAttribute(unsettable1EClass, UNSETTABLE1__UNSETTABLE_INT);
    createEAttribute(unsettable1EClass, UNSETTABLE1__UNSETTABLE_LONG);
    createEAttribute(unsettable1EClass, UNSETTABLE1__UNSETTABLE_SHORT);
    createEAttribute(unsettable1EClass, UNSETTABLE1__UNSETTABLE_STRING);
    createEAttribute(unsettable1EClass, UNSETTABLE1__UNSETTABLE_VAT);
    createEReference(unsettable1EClass, UNSETTABLE1__UNSETTABLE_ELEMENT);

    unsettable2WithDefaultEClass = createEClass(UNSETTABLE2_WITH_DEFAULT);
    createEAttribute(unsettable2WithDefaultEClass, UNSETTABLE2_WITH_DEFAULT__UNSETTABLE_BOOLEAN);
    createEAttribute(unsettable2WithDefaultEClass, UNSETTABLE2_WITH_DEFAULT__UNSETTABLE_BYTE);
    createEAttribute(unsettable2WithDefaultEClass, UNSETTABLE2_WITH_DEFAULT__UNSETTABLE_CHAR);
    createEAttribute(unsettable2WithDefaultEClass, UNSETTABLE2_WITH_DEFAULT__UNSETTABLE_DATE);
    createEAttribute(unsettable2WithDefaultEClass, UNSETTABLE2_WITH_DEFAULT__UNSETTABLE_DOUBLE);
    createEAttribute(unsettable2WithDefaultEClass, UNSETTABLE2_WITH_DEFAULT__UNSETTABLE_FLOAT);
    createEAttribute(unsettable2WithDefaultEClass, UNSETTABLE2_WITH_DEFAULT__UNSETTABLE_INT);
    createEAttribute(unsettable2WithDefaultEClass, UNSETTABLE2_WITH_DEFAULT__UNSETTABLE_LONG);
    createEAttribute(unsettable2WithDefaultEClass, UNSETTABLE2_WITH_DEFAULT__UNSETTABLE_SHORT);
    createEAttribute(unsettable2WithDefaultEClass, UNSETTABLE2_WITH_DEFAULT__UNSETTABLE_STRING);
    createEAttribute(unsettable2WithDefaultEClass, UNSETTABLE2_WITH_DEFAULT__UNSETTABLE_VAT);

    persistentContainmentEClass = createEClass(PERSISTENT_CONTAINMENT);
    createEAttribute(persistentContainmentEClass, PERSISTENT_CONTAINMENT__ATTR_BEFORE);
    createEReference(persistentContainmentEClass, PERSISTENT_CONTAINMENT__CHILDREN);
    createEAttribute(persistentContainmentEClass, PERSISTENT_CONTAINMENT__ATTR_AFTER);

    transientContainerEClass = createEClass(TRANSIENT_CONTAINER);
    createEAttribute(transientContainerEClass, TRANSIENT_CONTAINER__ATTR_BEFORE);
    createEReference(transientContainerEClass, TRANSIENT_CONTAINER__PARENT);
    createEAttribute(transientContainerEClass, TRANSIENT_CONTAINER__ATTR_AFTER);

    notUnsettableEClass = createEClass(NOT_UNSETTABLE);
    createEAttribute(notUnsettableEClass, NOT_UNSETTABLE__NOT_UNSETTABLE_BOOLEAN);
    createEAttribute(notUnsettableEClass, NOT_UNSETTABLE__NOT_UNSETTABLE_BYTE);
    createEAttribute(notUnsettableEClass, NOT_UNSETTABLE__NOT_UNSETTABLE_CHAR);
    createEAttribute(notUnsettableEClass, NOT_UNSETTABLE__NOT_UNSETTABLE_DATE);
    createEAttribute(notUnsettableEClass, NOT_UNSETTABLE__NOT_UNSETTABLE_DOUBLE);
    createEAttribute(notUnsettableEClass, NOT_UNSETTABLE__NOT_UNSETTABLE_FLOAT);
    createEAttribute(notUnsettableEClass, NOT_UNSETTABLE__NOT_UNSETTABLE_INT);
    createEAttribute(notUnsettableEClass, NOT_UNSETTABLE__NOT_UNSETTABLE_LONG);
    createEAttribute(notUnsettableEClass, NOT_UNSETTABLE__NOT_UNSETTABLE_SHORT);
    createEAttribute(notUnsettableEClass, NOT_UNSETTABLE__NOT_UNSETTABLE_STRING);
    createEAttribute(notUnsettableEClass, NOT_UNSETTABLE__NOT_UNSETTABLE_VAT);

    notUnsettableWithDefaultEClass = createEClass(NOT_UNSETTABLE_WITH_DEFAULT);
    createEAttribute(notUnsettableWithDefaultEClass, NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_BOOLEAN);
    createEAttribute(notUnsettableWithDefaultEClass, NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_BYTE);
    createEAttribute(notUnsettableWithDefaultEClass, NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_CHAR);
    createEAttribute(notUnsettableWithDefaultEClass, NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_DATE);
    createEAttribute(notUnsettableWithDefaultEClass, NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_DOUBLE);
    createEAttribute(notUnsettableWithDefaultEClass, NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_FLOAT);
    createEAttribute(notUnsettableWithDefaultEClass, NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_INT);
    createEAttribute(notUnsettableWithDefaultEClass, NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_LONG);
    createEAttribute(notUnsettableWithDefaultEClass, NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_SHORT);
    createEAttribute(notUnsettableWithDefaultEClass, NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_STRING);
    createEAttribute(notUnsettableWithDefaultEClass, NOT_UNSETTABLE_WITH_DEFAULT__NOT_UNSETTABLE_VAT);

    mapHolderEClass = createEClass(MAP_HOLDER);
    createEReference(mapHolderEClass, MAP_HOLDER__INTEGER_TO_STRING_MAP);
    createEReference(mapHolderEClass, MAP_HOLDER__STRING_TO_STRING_MAP);
    createEReference(mapHolderEClass, MAP_HOLDER__STRING_TO_VAT_MAP);
    createEReference(mapHolderEClass, MAP_HOLDER__STRING_TO_ADDRESS_CONTAINMENT_MAP);
    createEReference(mapHolderEClass, MAP_HOLDER__STRING_TO_ADDRESS_REFERENCE_MAP);
    createEReference(mapHolderEClass, MAP_HOLDER__EOBJECT_TO_EOBJECT_MAP);
    createEReference(mapHolderEClass, MAP_HOLDER__EOBJECT_TO_EOBJECT_KEY_CONTAINED_MAP);
    createEReference(mapHolderEClass, MAP_HOLDER__EOBJECT_TO_EOBJECT_BOTH_CONTAINED_MAP);
    createEReference(mapHolderEClass, MAP_HOLDER__EOBJECT_TO_EOBJECT_VALUE_CONTAINED_MAP);

    stringToStringMapEClass = createEClass(STRING_TO_STRING_MAP);
    createEAttribute(stringToStringMapEClass, STRING_TO_STRING_MAP__KEY);
    createEAttribute(stringToStringMapEClass, STRING_TO_STRING_MAP__VALUE);

    integerToStringMapEClass = createEClass(INTEGER_TO_STRING_MAP);
    createEAttribute(integerToStringMapEClass, INTEGER_TO_STRING_MAP__KEY);
    createEAttribute(integerToStringMapEClass, INTEGER_TO_STRING_MAP__VALUE);

    stringToVATMapEClass = createEClass(STRING_TO_VAT_MAP);
    createEAttribute(stringToVATMapEClass, STRING_TO_VAT_MAP__KEY);
    createEAttribute(stringToVATMapEClass, STRING_TO_VAT_MAP__VALUE);

    stringToAddressContainmentMapEClass = createEClass(STRING_TO_ADDRESS_CONTAINMENT_MAP);
    createEAttribute(stringToAddressContainmentMapEClass, STRING_TO_ADDRESS_CONTAINMENT_MAP__KEY);
    createEReference(stringToAddressContainmentMapEClass, STRING_TO_ADDRESS_CONTAINMENT_MAP__VALUE);

    stringToAddressReferenceMapEClass = createEClass(STRING_TO_ADDRESS_REFERENCE_MAP);
    createEAttribute(stringToAddressReferenceMapEClass, STRING_TO_ADDRESS_REFERENCE_MAP__KEY);
    createEReference(stringToAddressReferenceMapEClass, STRING_TO_ADDRESS_REFERENCE_MAP__VALUE);

    eObjectToEObjectMapEClass = createEClass(EOBJECT_TO_EOBJECT_MAP);
    createEReference(eObjectToEObjectMapEClass, EOBJECT_TO_EOBJECT_MAP__KEY);
    createEReference(eObjectToEObjectMapEClass, EOBJECT_TO_EOBJECT_MAP__VALUE);

    eObjectToEObjectKeyContainedMapEClass = createEClass(EOBJECT_TO_EOBJECT_KEY_CONTAINED_MAP);
    createEReference(eObjectToEObjectKeyContainedMapEClass, EOBJECT_TO_EOBJECT_KEY_CONTAINED_MAP__KEY);
    createEReference(eObjectToEObjectKeyContainedMapEClass, EOBJECT_TO_EOBJECT_KEY_CONTAINED_MAP__VALUE);

    eObjectToEObjectBothContainedMapEClass = createEClass(EOBJECT_TO_EOBJECT_BOTH_CONTAINED_MAP);
    createEReference(eObjectToEObjectBothContainedMapEClass, EOBJECT_TO_EOBJECT_BOTH_CONTAINED_MAP__KEY);
    createEReference(eObjectToEObjectBothContainedMapEClass, EOBJECT_TO_EOBJECT_BOTH_CONTAINED_MAP__VALUE);

    eObjectToEObjectValueContainedMapEClass = createEClass(EOBJECT_TO_EOBJECT_VALUE_CONTAINED_MAP);
    createEReference(eObjectToEObjectValueContainedMapEClass, EOBJECT_TO_EOBJECT_VALUE_CONTAINED_MAP__KEY);
    createEReference(eObjectToEObjectValueContainedMapEClass, EOBJECT_TO_EOBJECT_VALUE_CONTAINED_MAP__VALUE);

    enumListHolderEClass = createEClass(ENUM_LIST_HOLDER);
    createEAttribute(enumListHolderEClass, ENUM_LIST_HOLDER__ENUM_LIST);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private boolean isInitialized = false;

  /**
   * Complete the initialization of the package and its meta-model.  This
   * method is guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public void initializePackageContents()
  {
    if (isInitialized)
    {
      return;
    }
    isInitialized = true;

    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);

    // Obtain other dependent packages
    Model1Package theModel1Package = (Model1Package)EPackage.Registry.INSTANCE.getEPackage(Model1Package.eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    specialPurchaseOrderEClass.getESuperTypes().add(theModel1Package.getPurchaseOrder());

    // Initialize classes and features; add operations and parameters
    initEClass(specialPurchaseOrderEClass, SpecialPurchaseOrder.class, "SpecialPurchaseOrder", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getSpecialPurchaseOrder_DiscountCode(), ecorePackage.getEString(), "discountCode", null, 0, 1, SpecialPurchaseOrder.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getSpecialPurchaseOrder_ShippingAddress(), theModel1Package.getAddress(), null, "shippingAddress", null, 0, 1, SpecialPurchaseOrder.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(taskContainerEClass, TaskContainer.class, "TaskContainer", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getTaskContainer_Tasks(), getTask(), getTask_TaskContainer(), "tasks", null, 0, -1, TaskContainer.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(taskEClass, Task.class, "Task", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getTask_TaskContainer(), getTaskContainer(), getTaskContainer_Tasks(), "taskContainer", null, 0, 1, Task.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getTask_Description(), ecorePackage.getEString(), "description", null, 0, 1, Task.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getTask_Done(), ecorePackage.getEBoolean(), "done", null, 0, 1, Task.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE,
        !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(unsettable1EClass, Unsettable1.class, "Unsettable1", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getUnsettable1_UnsettableBoolean(), ecorePackage.getEBoolean(), "unsettableBoolean", null, 0, 1, Unsettable1.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettable1_UnsettableByte(), ecorePackage.getEByte(), "unsettableByte", null, 0, 1, Unsettable1.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettable1_UnsettableChar(), ecorePackage.getEChar(), "unsettableChar", null, 0, 1, Unsettable1.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettable1_UnsettableDate(), ecorePackage.getEDate(), "unsettableDate", null, 0, 1, Unsettable1.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettable1_UnsettableDouble(), ecorePackage.getEDouble(), "unsettableDouble", null, 0, 1, Unsettable1.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettable1_UnsettableFloat(), ecorePackage.getEFloat(), "unsettableFloat", null, 0, 1, Unsettable1.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettable1_UnsettableInt(), ecorePackage.getEInt(), "unsettableInt", null, 0, 1, Unsettable1.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettable1_UnsettableLong(), ecorePackage.getELong(), "unsettableLong", null, 0, 1, Unsettable1.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettable1_UnsettableShort(), ecorePackage.getEShort(), "unsettableShort", null, 0, 1, Unsettable1.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettable1_UnsettableString(), ecorePackage.getEString(), "unsettableString", null, 0, 1, Unsettable1.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettable1_UnsettableVAT(), theModel1Package.getVAT(), "unsettableVAT", null, 0, 1, Unsettable1.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getUnsettable1_UnsettableElement(), ecorePackage.getEObject(), null, "unsettableElement", null, 0, 1, Unsettable1.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(unsettable2WithDefaultEClass, Unsettable2WithDefault.class, "Unsettable2WithDefault", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getUnsettable2WithDefault_UnsettableBoolean(), ecorePackage.getEBoolean(), "unsettableBoolean", "true", 0, 1, Unsettable2WithDefault.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettable2WithDefault_UnsettableByte(), ecorePackage.getEByte(), "unsettableByte", "3", 0, 1, Unsettable2WithDefault.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettable2WithDefault_UnsettableChar(), ecorePackage.getEChar(), "unsettableChar", "\'x\'", 0, 1, Unsettable2WithDefault.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettable2WithDefault_UnsettableDate(), ecorePackage.getEDate(), "unsettableDate", "2009-12-21T15:12:59", 0, 1,
        Unsettable2WithDefault.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettable2WithDefault_UnsettableDouble(), ecorePackage.getEDouble(), "unsettableDouble", "3.3", 0, 1, Unsettable2WithDefault.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettable2WithDefault_UnsettableFloat(), ecorePackage.getEFloat(), "unsettableFloat", "4.4", 0, 1, Unsettable2WithDefault.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettable2WithDefault_UnsettableInt(), ecorePackage.getEInt(), "unsettableInt", "5", 0, 1, Unsettable2WithDefault.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettable2WithDefault_UnsettableLong(), ecorePackage.getELong(), "unsettableLong", "6", 0, 1, Unsettable2WithDefault.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettable2WithDefault_UnsettableShort(), ecorePackage.getEShort(), "unsettableShort", "7", 0, 1, Unsettable2WithDefault.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettable2WithDefault_UnsettableString(), ecorePackage.getEString(), "unsettableString", "\"eike\"", 0, 1, Unsettable2WithDefault.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettable2WithDefault_UnsettableVAT(), theModel1Package.getVAT(), "unsettableVAT", "vat15", 0, 1, Unsettable2WithDefault.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(persistentContainmentEClass, PersistentContainment.class, "PersistentContainment", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getPersistentContainment_AttrBefore(), ecorePackage.getEString(), "attrBefore", null, 0, 1, PersistentContainment.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getPersistentContainment_Children(), getTransientContainer(), getTransientContainer_Parent(), "children", null, 0, -1,
        PersistentContainment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
        !IS_ORDERED);
    initEAttribute(getPersistentContainment_AttrAfter(), ecorePackage.getEString(), "attrAfter", null, 0, 1, PersistentContainment.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(transientContainerEClass, TransientContainer.class, "TransientContainer", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getTransientContainer_AttrBefore(), ecorePackage.getEString(), "attrBefore", null, 0, 1, TransientContainer.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getTransientContainer_Parent(), getPersistentContainment(), getPersistentContainment_Children(), "parent", null, 0, 1,
        TransientContainer.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
        IS_ORDERED);
    initEAttribute(getTransientContainer_AttrAfter(), ecorePackage.getEString(), "attrAfter", null, 0, 1, TransientContainer.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(notUnsettableEClass, NotUnsettable.class, "NotUnsettable", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getNotUnsettable_NotUnsettableBoolean(), ecorePackage.getEBoolean(), "notUnsettableBoolean", null, 0, 1, NotUnsettable.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getNotUnsettable_NotUnsettableByte(), ecorePackage.getEByte(), "notUnsettableByte", null, 0, 1, NotUnsettable.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getNotUnsettable_NotUnsettableChar(), ecorePackage.getEChar(), "notUnsettableChar", null, 0, 1, NotUnsettable.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getNotUnsettable_NotUnsettableDate(), ecorePackage.getEDate(), "notUnsettableDate", null, 0, 1, NotUnsettable.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getNotUnsettable_NotUnsettableDouble(), ecorePackage.getEDouble(), "notUnsettableDouble", null, 0, 1, NotUnsettable.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getNotUnsettable_NotUnsettableFloat(), ecorePackage.getEFloat(), "notUnsettableFloat", null, 0, 1, NotUnsettable.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getNotUnsettable_NotUnsettableInt(), ecorePackage.getEInt(), "notUnsettableInt", null, 0, 1, NotUnsettable.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getNotUnsettable_NotUnsettableLong(), ecorePackage.getELong(), "notUnsettableLong", null, 0, 1, NotUnsettable.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getNotUnsettable_NotUnsettableShort(), ecorePackage.getEShort(), "notUnsettableShort", null, 0, 1, NotUnsettable.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getNotUnsettable_NotUnsettableString(), ecorePackage.getEString(), "notUnsettableString", null, 0, 1, NotUnsettable.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getNotUnsettable_NotUnsettableVAT(), theModel1Package.getVAT(), "notUnsettableVAT", null, 0, 1, NotUnsettable.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(notUnsettableWithDefaultEClass, NotUnsettableWithDefault.class, "NotUnsettableWithDefault", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getNotUnsettableWithDefault_NotUnsettableBoolean(), ecorePackage.getEBoolean(), "notUnsettableBoolean", "true", 0, 1,
        NotUnsettableWithDefault.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getNotUnsettableWithDefault_NotUnsettableByte(), ecorePackage.getEByte(), "notUnsettableByte", "3", 0, 1, NotUnsettableWithDefault.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getNotUnsettableWithDefault_NotUnsettableChar(), ecorePackage.getEChar(), "notUnsettableChar", "\'x\'", 0, 1, NotUnsettableWithDefault.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getNotUnsettableWithDefault_NotUnsettableDate(), ecorePackage.getEDate(), "notUnsettableDate", "1979-03-15T07:12:59", 0, 1,
        NotUnsettableWithDefault.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getNotUnsettableWithDefault_NotUnsettableDouble(), ecorePackage.getEDouble(), "notUnsettableDouble", "3.3", 0, 1,
        NotUnsettableWithDefault.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getNotUnsettableWithDefault_NotUnsettableFloat(), ecorePackage.getEFloat(), "notUnsettableFloat", "4.4", 0, 1,
        NotUnsettableWithDefault.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getNotUnsettableWithDefault_NotUnsettableInt(), ecorePackage.getEInt(), "notUnsettableInt", "5", 0, 1, NotUnsettableWithDefault.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getNotUnsettableWithDefault_NotUnsettableLong(), ecorePackage.getELong(), "notUnsettableLong", "6", 0, 1, NotUnsettableWithDefault.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getNotUnsettableWithDefault_NotUnsettableShort(), ecorePackage.getEShort(), "notUnsettableShort", "7", 0, 1, NotUnsettableWithDefault.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getNotUnsettableWithDefault_NotUnsettableString(), ecorePackage.getEString(), "notUnsettableString", "\"eike\"", 0, 1,
        NotUnsettableWithDefault.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getNotUnsettableWithDefault_NotUnsettableVAT(), theModel1Package.getVAT(), "notUnsettableVAT", "vat15", 0, 1, NotUnsettableWithDefault.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(mapHolderEClass, MapHolder.class, "MapHolder", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getMapHolder_IntegerToStringMap(), getIntegerToStringMap(), null, "integerToStringMap", null, 0, -1, MapHolder.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getMapHolder_StringToStringMap(), getStringToStringMap(), null, "stringToStringMap", null, 0, -1, MapHolder.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getMapHolder_StringToVATMap(), getStringToVATMap(), null, "stringToVATMap", null, 0, -1, MapHolder.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getMapHolder_StringToAddressContainmentMap(), getStringToAddressContainmentMap(), null, "stringToAddressContainmentMap", null, 0, -1,
        MapHolder.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getMapHolder_StringToAddressReferenceMap(), getStringToAddressReferenceMap(), null, "stringToAddressReferenceMap", null, 0, -1,
        MapHolder.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getMapHolder_EObjectToEObjectMap(), getEObjectToEObjectMap(), null, "eObjectToEObjectMap", null, 0, -1, MapHolder.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getMapHolder_EObjectToEObjectKeyContainedMap(), getEObjectToEObjectKeyContainedMap(), null, "eObjectToEObjectKeyContainedMap", null, 0, -1,
        MapHolder.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getMapHolder_EObjectToEObjectBothContainedMap(), getEObjectToEObjectBothContainedMap(), null, "eObjectToEObjectBothContainedMap", null, 0,
        -1, MapHolder.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getMapHolder_EObjectToEObjectValueContainedMap(), getEObjectToEObjectValueContainedMap(), null, "eObjectToEObjectValueContainedMap", null, 0,
        -1, MapHolder.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(stringToStringMapEClass, Map.Entry.class, "StringToStringMap", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getStringToStringMap_Key(), ecorePackage.getEString(), "key", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getStringToStringMap_Value(), ecorePackage.getEString(), "value", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(integerToStringMapEClass, Map.Entry.class, "IntegerToStringMap", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getIntegerToStringMap_Key(), ecorePackage.getEIntegerObject(), "key", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getIntegerToStringMap_Value(), ecorePackage.getEString(), "value", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(stringToVATMapEClass, Map.Entry.class, "StringToVATMap", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getStringToVATMap_Key(), ecorePackage.getEString(), "key", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getStringToVATMap_Value(), theModel1Package.getVAT(), "value", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(stringToAddressContainmentMapEClass, Map.Entry.class, "StringToAddressContainmentMap", !IS_ABSTRACT, !IS_INTERFACE,
        !IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getStringToAddressContainmentMap_Key(), ecorePackage.getEString(), "key", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getStringToAddressContainmentMap_Value(), theModel1Package.getAddress(), null, "value", null, 0, 1, Map.Entry.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(stringToAddressReferenceMapEClass, Map.Entry.class, "StringToAddressReferenceMap", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getStringToAddressReferenceMap_Key(), ecorePackage.getEString(), "key", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getStringToAddressReferenceMap_Value(), theModel1Package.getAddress(), null, "value", null, 0, 1, Map.Entry.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(eObjectToEObjectMapEClass, Map.Entry.class, "EObjectToEObjectMap", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);
    initEReference(getEObjectToEObjectMap_Key(), ecorePackage.getEObject(), null, "key", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getEObjectToEObjectMap_Value(), ecorePackage.getEObject(), null, "value", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(eObjectToEObjectKeyContainedMapEClass, Map.Entry.class, "EObjectToEObjectKeyContainedMap", !IS_ABSTRACT, !IS_INTERFACE,
        !IS_GENERATED_INSTANCE_CLASS);
    initEReference(getEObjectToEObjectKeyContainedMap_Key(), ecorePackage.getEObject(), null, "key", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getEObjectToEObjectKeyContainedMap_Value(), ecorePackage.getEObject(), null, "value", null, 0, 1, Map.Entry.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(eObjectToEObjectBothContainedMapEClass, Map.Entry.class, "EObjectToEObjectBothContainedMap", !IS_ABSTRACT, !IS_INTERFACE,
        !IS_GENERATED_INSTANCE_CLASS);
    initEReference(getEObjectToEObjectBothContainedMap_Key(), ecorePackage.getEObject(), null, "key", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getEObjectToEObjectBothContainedMap_Value(), ecorePackage.getEObject(), null, "value", null, 0, 1, Map.Entry.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(eObjectToEObjectValueContainedMapEClass, Map.Entry.class, "EObjectToEObjectValueContainedMap", !IS_ABSTRACT, !IS_INTERFACE,
        !IS_GENERATED_INSTANCE_CLASS);
    initEReference(getEObjectToEObjectValueContainedMap_Key(), ecorePackage.getEObject(), null, "key", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getEObjectToEObjectValueContainedMap_Value(), ecorePackage.getEObject(), null, "value", null, 0, 1, Map.Entry.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(enumListHolderEClass, EnumListHolder.class, "EnumListHolder", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getEnumListHolder_EnumList(), theModel1Package.getVAT(), "enumList", null, 0, -1, EnumListHolder.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Create resource
    createResource(eNS_URI);
  }

} // Model2PackageImpl
