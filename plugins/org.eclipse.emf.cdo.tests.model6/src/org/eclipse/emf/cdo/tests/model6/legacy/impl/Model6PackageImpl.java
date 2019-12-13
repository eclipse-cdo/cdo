/*
 * Copyright (c) 2013, 2015, 2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model6.legacy.impl;

import org.eclipse.emf.cdo.tests.model6.BaseObject;
import org.eclipse.emf.cdo.tests.model6.CanReferenceLegacy;
import org.eclipse.emf.cdo.tests.model6.ContainmentObject;
import org.eclipse.emf.cdo.tests.model6.EmptyStringDefault;
import org.eclipse.emf.cdo.tests.model6.EmptyStringDefaultUnsettable;
import org.eclipse.emf.cdo.tests.model6.HasNillableAttribute;
import org.eclipse.emf.cdo.tests.model6.Holdable;
import org.eclipse.emf.cdo.tests.model6.Holder;
import org.eclipse.emf.cdo.tests.model6.MyEnum;
import org.eclipse.emf.cdo.tests.model6.MyEnumList;
import org.eclipse.emf.cdo.tests.model6.MyEnumListUnsettable;
import org.eclipse.emf.cdo.tests.model6.PropertiesMap;
import org.eclipse.emf.cdo.tests.model6.PropertiesMapEntryValue;
import org.eclipse.emf.cdo.tests.model6.ReferenceObject;
import org.eclipse.emf.cdo.tests.model6.Root;
import org.eclipse.emf.cdo.tests.model6.Thing;
import org.eclipse.emf.cdo.tests.model6.UnorderedList;
import org.eclipse.emf.cdo.tests.model6.UnsettableAttributes;
import org.eclipse.emf.cdo.tests.model6.legacy.Model6Factory;
import org.eclipse.emf.cdo.tests.model6.legacy.Model6Package;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;

import java.util.Map;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Package</b>. <!-- end-user-doc -->
 * @generated
 */
public class Model6PackageImpl extends EPackageImpl implements Model6Package
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass rootEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass baseObjectEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass referenceObjectEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass containmentObjectEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass unorderedListEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass propertiesMapEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass propertiesMapEntryEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass propertiesMapEntryValueEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass aEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass bEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass cEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass dEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass eEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private EClass fEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass gEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass myEnumListEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass myEnumListUnsettableEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass holderEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass thingEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass holdableEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass hasNillableAttributeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass emptyStringDefaultEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass emptyStringDefaultUnsettableEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass unsettableAttributesEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass canReferenceLegacyEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EEnum myEnumEEnum = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EDataType myStringEDataType = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with {@link org.eclipse.emf.ecore.EPackage.Registry
   * EPackage.Registry} by the package package URI value.
   * <p>
   * Note: the correct way to create the package is via the static factory method {@link #init init()}, which also
   * performs initialization of the package, or returns the registered package, if one already exists. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see org.eclipse.emf.cdo.tests.legacy.model6.Model6Package#eNS_URI
   * @see #init()
   * @generated
   */
  private Model6PackageImpl()
  {
    super(eNS_URI, Model6Factory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   *
   * <p>This method is used to initialize {@link Model6Package#eINSTANCE} when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static Model6Package init()
  {
    if (isInited)
    {
      return (Model6Package)EPackage.Registry.INSTANCE.getEPackage(Model6Package.eNS_URI);
    }

    // Obtain or create and register package
    Object registeredModel6Package = EPackage.Registry.INSTANCE.get(eNS_URI);
    Model6PackageImpl theModel6Package = registeredModel6Package instanceof Model6PackageImpl ? (Model6PackageImpl)registeredModel6Package
        : new Model6PackageImpl();

    isInited = true;

    // Create package meta-data objects
    theModel6Package.createPackageContents();

    // Initialize created meta-data
    theModel6Package.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theModel6Package.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(Model6Package.eNS_URI, theModel6Package);
    return theModel6Package;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getRoot()
  {
    return rootEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getRoot_ListA()
  {
    return (EReference)rootEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getRoot_ListB()
  {
    return (EReference)rootEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getRoot_ListC()
  {
    return (EReference)rootEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getRoot_ListD()
  {
    return (EReference)rootEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getBaseObject()
  {
    return baseObjectEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getBaseObject_AttributeOptional()
  {
    return (EAttribute)baseObjectEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getBaseObject_AttributeRequired()
  {
    return (EAttribute)baseObjectEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getBaseObject_AttributeList()
  {
    return (EAttribute)baseObjectEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getReferenceObject()
  {
    return referenceObjectEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getReferenceObject_ReferenceOptional()
  {
    return (EReference)referenceObjectEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getReferenceObject_ReferenceList()
  {
    return (EReference)referenceObjectEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getContainmentObject()
  {
    return containmentObjectEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getContainmentObject_ContainmentOptional()
  {
    return (EReference)containmentObjectEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getContainmentObject_ContainmentList()
  {
    return (EReference)containmentObjectEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getUnorderedList()
  {
    return unorderedListEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getUnorderedList_Contained()
  {
    return (EReference)unorderedListEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getUnorderedList_Referenced()
  {
    return (EReference)unorderedListEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getPropertiesMap()
  {
    return propertiesMapEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getPropertiesMap_Label()
  {
    return (EAttribute)propertiesMapEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getPropertiesMap_PersistentMap()
  {
    return (EReference)propertiesMapEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getPropertiesMap_TransientMap()
  {
    return (EReference)propertiesMapEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getPropertiesMapEntry()
  {
    return propertiesMapEntryEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getPropertiesMapEntry_Key()
  {
    return (EAttribute)propertiesMapEntryEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getPropertiesMapEntry_Value()
  {
    return (EReference)propertiesMapEntryEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getPropertiesMapEntryValue()
  {
    return propertiesMapEntryValueEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getPropertiesMapEntryValue_Label()
  {
    return (EAttribute)propertiesMapEntryValueEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getA()
  {
    return aEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getA_OwnedDs()
  {
    return (EReference)aEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getA_OwnedBs()
  {
    return (EReference)aEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getB()
  {
    return bEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getB_OwnedC()
  {
    return (EReference)bEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getC()
  {
    return cEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getD()
  {
    return dEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getD_Data()
  {
    return (EReference)dEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getE()
  {
    return eEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getE_OwnedAs()
  {
    return (EReference)eEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getF()
  {
    return fEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getF_OwnedEs()
  {
    return (EReference)fEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getG()
  {
    return gEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getG_Dummy()
  {
    return (EAttribute)gEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getG_Reference()
  {
    return (EReference)gEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getG_List()
  {
    return (EReference)gEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getMyEnumList()
  {
    return myEnumListEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getMyEnumList_MyEnum()
  {
    return (EAttribute)myEnumListEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getMyEnumListUnsettable()
  {
    return myEnumListUnsettableEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getMyEnumListUnsettable_MyEnum()
  {
    return (EAttribute)myEnumListUnsettableEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getHolder()
  {
    return holderEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getHolder_Held()
  {
    return (EReference)holderEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getHolder_Owned()
  {
    return (EReference)holderEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getThing()
  {
    return thingEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getHoldable()
  {
    return holdableEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getHoldable_Name()
  {
    return (EAttribute)holdableEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getHasNillableAttribute()
  {
    return hasNillableAttributeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getHasNillableAttribute_Nillable()
  {
    return (EAttribute)hasNillableAttributeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getEmptyStringDefault()
  {
    return emptyStringDefaultEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getEmptyStringDefault_Attribute()
  {
    return (EAttribute)emptyStringDefaultEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getEmptyStringDefaultUnsettable()
  {
    return emptyStringDefaultUnsettableEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getEmptyStringDefaultUnsettable_Attribute()
  {
    return (EAttribute)emptyStringDefaultUnsettableEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getUnsettableAttributes()
  {
    return unsettableAttributesEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUnsettableAttributes_AttrBigDecimal()
  {
    return (EAttribute)unsettableAttributesEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUnsettableAttributes_AttrBigInteger()
  {
    return (EAttribute)unsettableAttributesEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUnsettableAttributes_AttrBoolean()
  {
    return (EAttribute)unsettableAttributesEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUnsettableAttributes_AttrBooleanObject()
  {
    return (EAttribute)unsettableAttributesEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUnsettableAttributes_AttrByte()
  {
    return (EAttribute)unsettableAttributesEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUnsettableAttributes_AttrByteArray()
  {
    return (EAttribute)unsettableAttributesEClass.getEStructuralFeatures().get(5);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUnsettableAttributes_AttrByteObject()
  {
    return (EAttribute)unsettableAttributesEClass.getEStructuralFeatures().get(6);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUnsettableAttributes_AttrChar()
  {
    return (EAttribute)unsettableAttributesEClass.getEStructuralFeatures().get(7);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUnsettableAttributes_AttrCharacterObject()
  {
    return (EAttribute)unsettableAttributesEClass.getEStructuralFeatures().get(8);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUnsettableAttributes_AttrDate()
  {
    return (EAttribute)unsettableAttributesEClass.getEStructuralFeatures().get(9);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUnsettableAttributes_AttrDouble()
  {
    return (EAttribute)unsettableAttributesEClass.getEStructuralFeatures().get(10);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUnsettableAttributes_AttrDoubleObject()
  {
    return (EAttribute)unsettableAttributesEClass.getEStructuralFeatures().get(11);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUnsettableAttributes_AttrFloat()
  {
    return (EAttribute)unsettableAttributesEClass.getEStructuralFeatures().get(12);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUnsettableAttributes_AttrFloatObject()
  {
    return (EAttribute)unsettableAttributesEClass.getEStructuralFeatures().get(13);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUnsettableAttributes_AttrInt()
  {
    return (EAttribute)unsettableAttributesEClass.getEStructuralFeatures().get(14);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUnsettableAttributes_AttrIntegerObject()
  {
    return (EAttribute)unsettableAttributesEClass.getEStructuralFeatures().get(15);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUnsettableAttributes_AttrJavaClass()
  {
    return (EAttribute)unsettableAttributesEClass.getEStructuralFeatures().get(16);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUnsettableAttributes_AttrJavaObject()
  {
    return (EAttribute)unsettableAttributesEClass.getEStructuralFeatures().get(17);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUnsettableAttributes_AttrLong()
  {
    return (EAttribute)unsettableAttributesEClass.getEStructuralFeatures().get(18);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUnsettableAttributes_AttrLongObject()
  {
    return (EAttribute)unsettableAttributesEClass.getEStructuralFeatures().get(19);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUnsettableAttributes_AttrShort()
  {
    return (EAttribute)unsettableAttributesEClass.getEStructuralFeatures().get(20);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUnsettableAttributes_AttrShortObject()
  {
    return (EAttribute)unsettableAttributesEClass.getEStructuralFeatures().get(21);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EAttribute getUnsettableAttributes_AttrString()
  {
    return (EAttribute)unsettableAttributesEClass.getEStructuralFeatures().get(22);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getCanReferenceLegacy()
  {
    return canReferenceLegacyEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getCanReferenceLegacy_SingleContainment()
  {
    return (EReference)canReferenceLegacyEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getCanReferenceLegacy_MultipleContainment()
  {
    return (EReference)canReferenceLegacyEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getCanReferenceLegacy_SingleReference()
  {
    return (EReference)canReferenceLegacyEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getCanReferenceLegacy_MultipleReference()
  {
    return (EReference)canReferenceLegacyEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EEnum getMyEnum()
  {
    return myEnumEEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EDataType getMyString()
  {
    return myStringEDataType;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Model6Factory getModel6Factory()
  {
    return (Model6Factory)getEFactoryInstance();
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
    rootEClass = createEClass(ROOT);
    createEReference(rootEClass, ROOT__LIST_A);
    createEReference(rootEClass, ROOT__LIST_B);
    createEReference(rootEClass, ROOT__LIST_C);
    createEReference(rootEClass, ROOT__LIST_D);

    baseObjectEClass = createEClass(BASE_OBJECT);
    createEAttribute(baseObjectEClass, BASE_OBJECT__ATTRIBUTE_OPTIONAL);
    createEAttribute(baseObjectEClass, BASE_OBJECT__ATTRIBUTE_REQUIRED);
    createEAttribute(baseObjectEClass, BASE_OBJECT__ATTRIBUTE_LIST);

    referenceObjectEClass = createEClass(REFERENCE_OBJECT);
    createEReference(referenceObjectEClass, REFERENCE_OBJECT__REFERENCE_OPTIONAL);
    createEReference(referenceObjectEClass, REFERENCE_OBJECT__REFERENCE_LIST);

    containmentObjectEClass = createEClass(CONTAINMENT_OBJECT);
    createEReference(containmentObjectEClass, CONTAINMENT_OBJECT__CONTAINMENT_OPTIONAL);
    createEReference(containmentObjectEClass, CONTAINMENT_OBJECT__CONTAINMENT_LIST);

    unorderedListEClass = createEClass(UNORDERED_LIST);
    createEReference(unorderedListEClass, UNORDERED_LIST__CONTAINED);
    createEReference(unorderedListEClass, UNORDERED_LIST__REFERENCED);

    propertiesMapEClass = createEClass(PROPERTIES_MAP);
    createEAttribute(propertiesMapEClass, PROPERTIES_MAP__LABEL);
    createEReference(propertiesMapEClass, PROPERTIES_MAP__PERSISTENT_MAP);
    createEReference(propertiesMapEClass, PROPERTIES_MAP__TRANSIENT_MAP);

    propertiesMapEntryEClass = createEClass(PROPERTIES_MAP_ENTRY);
    createEAttribute(propertiesMapEntryEClass, PROPERTIES_MAP_ENTRY__KEY);
    createEReference(propertiesMapEntryEClass, PROPERTIES_MAP_ENTRY__VALUE);

    propertiesMapEntryValueEClass = createEClass(PROPERTIES_MAP_ENTRY_VALUE);
    createEAttribute(propertiesMapEntryValueEClass, PROPERTIES_MAP_ENTRY_VALUE__LABEL);

    aEClass = createEClass(A);
    createEReference(aEClass, A__OWNED_DS);
    createEReference(aEClass, A__OWNED_BS);

    bEClass = createEClass(B);
    createEReference(bEClass, B__OWNED_C);

    cEClass = createEClass(C);

    dEClass = createEClass(D);
    createEReference(dEClass, D__DATA);

    eEClass = createEClass(E);
    createEReference(eEClass, E__OWNED_AS);

    fEClass = createEClass(F);
    createEReference(fEClass, F__OWNED_ES);

    gEClass = createEClass(G);
    createEAttribute(gEClass, G__DUMMY);
    createEReference(gEClass, G__REFERENCE);
    createEReference(gEClass, G__LIST);

    myEnumListEClass = createEClass(MY_ENUM_LIST);
    createEAttribute(myEnumListEClass, MY_ENUM_LIST__MY_ENUM);

    myEnumListUnsettableEClass = createEClass(MY_ENUM_LIST_UNSETTABLE);
    createEAttribute(myEnumListUnsettableEClass, MY_ENUM_LIST_UNSETTABLE__MY_ENUM);

    holderEClass = createEClass(HOLDER);
    createEReference(holderEClass, HOLDER__HELD);
    createEReference(holderEClass, HOLDER__OWNED);

    thingEClass = createEClass(THING);

    holdableEClass = createEClass(HOLDABLE);
    createEAttribute(holdableEClass, HOLDABLE__NAME);

    hasNillableAttributeEClass = createEClass(HAS_NILLABLE_ATTRIBUTE);
    createEAttribute(hasNillableAttributeEClass, HAS_NILLABLE_ATTRIBUTE__NILLABLE);

    emptyStringDefaultEClass = createEClass(EMPTY_STRING_DEFAULT);
    createEAttribute(emptyStringDefaultEClass, EMPTY_STRING_DEFAULT__ATTRIBUTE);

    emptyStringDefaultUnsettableEClass = createEClass(EMPTY_STRING_DEFAULT_UNSETTABLE);
    createEAttribute(emptyStringDefaultUnsettableEClass, EMPTY_STRING_DEFAULT_UNSETTABLE__ATTRIBUTE);

    unsettableAttributesEClass = createEClass(UNSETTABLE_ATTRIBUTES);
    createEAttribute(unsettableAttributesEClass, UNSETTABLE_ATTRIBUTES__ATTR_BIG_DECIMAL);
    createEAttribute(unsettableAttributesEClass, UNSETTABLE_ATTRIBUTES__ATTR_BIG_INTEGER);
    createEAttribute(unsettableAttributesEClass, UNSETTABLE_ATTRIBUTES__ATTR_BOOLEAN);
    createEAttribute(unsettableAttributesEClass, UNSETTABLE_ATTRIBUTES__ATTR_BOOLEAN_OBJECT);
    createEAttribute(unsettableAttributesEClass, UNSETTABLE_ATTRIBUTES__ATTR_BYTE);
    createEAttribute(unsettableAttributesEClass, UNSETTABLE_ATTRIBUTES__ATTR_BYTE_ARRAY);
    createEAttribute(unsettableAttributesEClass, UNSETTABLE_ATTRIBUTES__ATTR_BYTE_OBJECT);
    createEAttribute(unsettableAttributesEClass, UNSETTABLE_ATTRIBUTES__ATTR_CHAR);
    createEAttribute(unsettableAttributesEClass, UNSETTABLE_ATTRIBUTES__ATTR_CHARACTER_OBJECT);
    createEAttribute(unsettableAttributesEClass, UNSETTABLE_ATTRIBUTES__ATTR_DATE);
    createEAttribute(unsettableAttributesEClass, UNSETTABLE_ATTRIBUTES__ATTR_DOUBLE);
    createEAttribute(unsettableAttributesEClass, UNSETTABLE_ATTRIBUTES__ATTR_DOUBLE_OBJECT);
    createEAttribute(unsettableAttributesEClass, UNSETTABLE_ATTRIBUTES__ATTR_FLOAT);
    createEAttribute(unsettableAttributesEClass, UNSETTABLE_ATTRIBUTES__ATTR_FLOAT_OBJECT);
    createEAttribute(unsettableAttributesEClass, UNSETTABLE_ATTRIBUTES__ATTR_INT);
    createEAttribute(unsettableAttributesEClass, UNSETTABLE_ATTRIBUTES__ATTR_INTEGER_OBJECT);
    createEAttribute(unsettableAttributesEClass, UNSETTABLE_ATTRIBUTES__ATTR_JAVA_CLASS);
    createEAttribute(unsettableAttributesEClass, UNSETTABLE_ATTRIBUTES__ATTR_JAVA_OBJECT);
    createEAttribute(unsettableAttributesEClass, UNSETTABLE_ATTRIBUTES__ATTR_LONG);
    createEAttribute(unsettableAttributesEClass, UNSETTABLE_ATTRIBUTES__ATTR_LONG_OBJECT);
    createEAttribute(unsettableAttributesEClass, UNSETTABLE_ATTRIBUTES__ATTR_SHORT);
    createEAttribute(unsettableAttributesEClass, UNSETTABLE_ATTRIBUTES__ATTR_SHORT_OBJECT);
    createEAttribute(unsettableAttributesEClass, UNSETTABLE_ATTRIBUTES__ATTR_STRING);

    canReferenceLegacyEClass = createEClass(CAN_REFERENCE_LEGACY);
    createEReference(canReferenceLegacyEClass, CAN_REFERENCE_LEGACY__SINGLE_CONTAINMENT);
    createEReference(canReferenceLegacyEClass, CAN_REFERENCE_LEGACY__MULTIPLE_CONTAINMENT);
    createEReference(canReferenceLegacyEClass, CAN_REFERENCE_LEGACY__SINGLE_REFERENCE);
    createEReference(canReferenceLegacyEClass, CAN_REFERENCE_LEGACY__MULTIPLE_REFERENCE);

    // Create enums
    myEnumEEnum = createEEnum(MY_ENUM);

    // Create data types
    myStringEDataType = createEDataType(MY_STRING);
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

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    referenceObjectEClass.getESuperTypes().add(getBaseObject());
    containmentObjectEClass.getESuperTypes().add(getBaseObject());
    holderEClass.getESuperTypes().add(getHoldable());
    thingEClass.getESuperTypes().add(getHoldable());

    // Initialize classes and features; add operations and parameters
    initEClass(rootEClass, Root.class, "Root", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getRoot_ListA(), getBaseObject(), null, "listA", null, 0, -1, Root.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
        IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getRoot_ListB(), getBaseObject(), null, "listB", null, 0, -1, Root.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
        IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getRoot_ListC(), getBaseObject(), null, "listC", null, 0, -1, Root.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
        IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getRoot_ListD(), getBaseObject(), null, "listD", null, 0, -1, Root.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
        IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(baseObjectEClass, BaseObject.class, "BaseObject", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getBaseObject_AttributeOptional(), ecorePackage.getEString(), "attributeOptional", null, 0, 1, BaseObject.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getBaseObject_AttributeRequired(), ecorePackage.getEString(), "attributeRequired", null, 1, 1, BaseObject.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getBaseObject_AttributeList(), ecorePackage.getEString(), "attributeList", null, 0, -1, BaseObject.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(referenceObjectEClass, ReferenceObject.class, "ReferenceObject", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getReferenceObject_ReferenceOptional(), getBaseObject(), null, "referenceOptional", null, 0, 1, ReferenceObject.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getReferenceObject_ReferenceList(), getBaseObject(), null, "referenceList", null, 0, -1, ReferenceObject.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(containmentObjectEClass, ContainmentObject.class, "ContainmentObject", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getContainmentObject_ContainmentOptional(), getBaseObject(), null, "containmentOptional", null, 0, 1, ContainmentObject.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getContainmentObject_ContainmentList(), getBaseObject(), null, "containmentList", null, 0, -1, ContainmentObject.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(unorderedListEClass, UnorderedList.class, "UnorderedList", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getUnorderedList_Contained(), getUnorderedList(), null, "contained", null, 0, -1, UnorderedList.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
    initEReference(getUnorderedList_Referenced(), getUnorderedList(), null, "referenced", null, 0, -1, UnorderedList.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

    initEClass(propertiesMapEClass, PropertiesMap.class, "PropertiesMap", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getPropertiesMap_Label(), ecorePackage.getEString(), "label", null, 0, 1, PropertiesMap.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getPropertiesMap_PersistentMap(), getPropertiesMapEntry(), null, "persistentMap", null, 0, -1, PropertiesMap.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getPropertiesMap_TransientMap(), getPropertiesMapEntry(), null, "transientMap", null, 0, -1, PropertiesMap.class, IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(propertiesMapEntryEClass, Map.Entry.class, "PropertiesMapEntry", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getPropertiesMapEntry_Key(), ecorePackage.getEString(), "key", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getPropertiesMapEntry_Value(), getPropertiesMapEntryValue(), null, "value", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(propertiesMapEntryValueEClass, PropertiesMapEntryValue.class, "PropertiesMapEntryValue", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getPropertiesMapEntryValue_Label(), ecorePackage.getEString(), "label", null, 0, 1, PropertiesMapEntryValue.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(aEClass, org.eclipse.emf.cdo.tests.model6.A.class, "A", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getA_OwnedDs(), getD(), null, "ownedDs", null, 0, -1, org.eclipse.emf.cdo.tests.model6.A.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getA_OwnedBs(), getB(), null, "ownedBs", null, 0, -1, org.eclipse.emf.cdo.tests.model6.A.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(bEClass, org.eclipse.emf.cdo.tests.model6.B.class, "B", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getB_OwnedC(), getC(), null, "ownedC", null, 0, 1, org.eclipse.emf.cdo.tests.model6.B.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(cEClass, org.eclipse.emf.cdo.tests.model6.C.class, "C", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(dEClass, org.eclipse.emf.cdo.tests.model6.D.class, "D", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getD_Data(), ecorePackage.getEObject(), null, "data", null, 0, 1, org.eclipse.emf.cdo.tests.model6.D.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(eEClass, org.eclipse.emf.cdo.tests.model6.E.class, "E", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getE_OwnedAs(), getA(), null, "ownedAs", null, 0, -1, org.eclipse.emf.cdo.tests.model6.E.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(fEClass, org.eclipse.emf.cdo.tests.model6.F.class, "F", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getF_OwnedEs(), getE(), null, "ownedEs", null, 0, -1, org.eclipse.emf.cdo.tests.model6.F.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(gEClass, org.eclipse.emf.cdo.tests.model6.G.class, "G", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getG_Dummy(), ecorePackage.getEString(), "dummy", null, 1, 1, org.eclipse.emf.cdo.tests.model6.G.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getG_Reference(), getBaseObject(), null, "reference", null, 1, 1, org.eclipse.emf.cdo.tests.model6.G.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getG_List(), getBaseObject(), null, "list", null, 0, -1, org.eclipse.emf.cdo.tests.model6.G.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    addEOperation(gEClass, ecorePackage.getEBoolean(), "isAttributeModified", 1, 1, IS_UNIQUE, IS_ORDERED);

    addEOperation(gEClass, ecorePackage.getEBoolean(), "isReferenceModified", 1, 1, IS_UNIQUE, IS_ORDERED);

    addEOperation(gEClass, ecorePackage.getEBoolean(), "isListModified", 1, 1, IS_UNIQUE, IS_ORDERED);

    initEClass(myEnumListEClass, MyEnumList.class, "MyEnumList", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getMyEnumList_MyEnum(), getMyEnum(), "myEnum", null, 0, -1, MyEnumList.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE,
        !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(myEnumListUnsettableEClass, MyEnumListUnsettable.class, "MyEnumListUnsettable", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getMyEnumListUnsettable_MyEnum(), getMyEnum(), "myEnum", null, 0, -1, MyEnumListUnsettable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(holderEClass, Holder.class, "Holder", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getHolder_Held(), getHoldable(), null, "held", null, 0, -1, Holder.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE,
        IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
    initEReference(getHolder_Owned(), getHoldable(), null, "owned", null, 0, -1, Holder.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
        IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(thingEClass, Thing.class, "Thing", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(holdableEClass, Holdable.class, "Holdable", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getHoldable_Name(), ecorePackage.getEString(), "name", null, 1, 1, Holdable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
        !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(hasNillableAttributeEClass, HasNillableAttribute.class, "HasNillableAttribute", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getHasNillableAttribute_Nillable(), getMyString(), "nillable", null, 0, 1, HasNillableAttribute.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(emptyStringDefaultEClass, EmptyStringDefault.class, "EmptyStringDefault", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getEmptyStringDefault_Attribute(), ecorePackage.getEString(), "attribute", "", 0, 1, EmptyStringDefault.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(emptyStringDefaultUnsettableEClass, EmptyStringDefaultUnsettable.class, "EmptyStringDefaultUnsettable", !IS_ABSTRACT, !IS_INTERFACE,
        IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getEmptyStringDefaultUnsettable_Attribute(), ecorePackage.getEString(), "attribute", "", 0, 1, EmptyStringDefaultUnsettable.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(unsettableAttributesEClass, UnsettableAttributes.class, "UnsettableAttributes", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getUnsettableAttributes_AttrBigDecimal(), ecorePackage.getEBigDecimal(), "attrBigDecimal", null, 0, 1, UnsettableAttributes.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettableAttributes_AttrBigInteger(), ecorePackage.getEBigInteger(), "attrBigInteger", null, 0, 1, UnsettableAttributes.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettableAttributes_AttrBoolean(), ecorePackage.getEBoolean(), "attrBoolean", null, 0, 1, UnsettableAttributes.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettableAttributes_AttrBooleanObject(), ecorePackage.getEBooleanObject(), "attrBooleanObject", null, 0, 1, UnsettableAttributes.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettableAttributes_AttrByte(), ecorePackage.getEByte(), "attrByte", null, 0, 1, UnsettableAttributes.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettableAttributes_AttrByteArray(), ecorePackage.getEByteArray(), "attrByteArray", null, 0, 1, UnsettableAttributes.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettableAttributes_AttrByteObject(), ecorePackage.getEByteObject(), "attrByteObject", null, 0, 1, UnsettableAttributes.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettableAttributes_AttrChar(), ecorePackage.getEChar(), "attrChar", null, 0, 1, UnsettableAttributes.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettableAttributes_AttrCharacterObject(), ecorePackage.getECharacterObject(), "attrCharacterObject", null, 0, 1,
        UnsettableAttributes.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettableAttributes_AttrDate(), ecorePackage.getEDate(), "attrDate", null, 0, 1, UnsettableAttributes.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettableAttributes_AttrDouble(), ecorePackage.getEDouble(), "attrDouble", null, 0, 1, UnsettableAttributes.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettableAttributes_AttrDoubleObject(), ecorePackage.getEDoubleObject(), "attrDoubleObject", null, 0, 1, UnsettableAttributes.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettableAttributes_AttrFloat(), ecorePackage.getEFloat(), "attrFloat", null, 0, 1, UnsettableAttributes.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettableAttributes_AttrFloatObject(), ecorePackage.getEFloatObject(), "attrFloatObject", null, 0, 1, UnsettableAttributes.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettableAttributes_AttrInt(), ecorePackage.getEInt(), "attrInt", null, 0, 1, UnsettableAttributes.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettableAttributes_AttrIntegerObject(), ecorePackage.getEIntegerObject(), "attrIntegerObject", null, 0, 1, UnsettableAttributes.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    EGenericType g1 = createEGenericType(ecorePackage.getEJavaClass());
    EGenericType g2 = createEGenericType();
    g1.getETypeArguments().add(g2);
    initEAttribute(getUnsettableAttributes_AttrJavaClass(), g1, "attrJavaClass", null, 0, 1, UnsettableAttributes.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettableAttributes_AttrJavaObject(), ecorePackage.getEJavaObject(), "attrJavaObject", null, 0, 1, UnsettableAttributes.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettableAttributes_AttrLong(), ecorePackage.getELong(), "attrLong", null, 0, 1, UnsettableAttributes.class, !IS_TRANSIENT, !IS_VOLATILE,
        IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettableAttributes_AttrLongObject(), ecorePackage.getELongObject(), "attrLongObject", null, 0, 1, UnsettableAttributes.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettableAttributes_AttrShort(), ecorePackage.getEShort(), "attrShort", null, 0, 1, UnsettableAttributes.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettableAttributes_AttrShortObject(), ecorePackage.getEShortObject(), "attrShortObject", null, 0, 1, UnsettableAttributes.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getUnsettableAttributes_AttrString(), ecorePackage.getEString(), "attrString", null, 0, 1, UnsettableAttributes.class, !IS_TRANSIENT,
        !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(canReferenceLegacyEClass, CanReferenceLegacy.class, "CanReferenceLegacy", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getCanReferenceLegacy_SingleContainment(), ecorePackage.getEObject(), null, "singleContainment", null, 0, 1, CanReferenceLegacy.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getCanReferenceLegacy_MultipleContainment(), ecorePackage.getEObject(), null, "multipleContainment", null, 0, -1, CanReferenceLegacy.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getCanReferenceLegacy_SingleReference(), ecorePackage.getEObject(), null, "singleReference", null, 0, 1, CanReferenceLegacy.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getCanReferenceLegacy_MultipleReference(), ecorePackage.getEObject(), null, "multipleReference", null, 0, -1, CanReferenceLegacy.class,
        !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Initialize enums and add enum literals
    initEEnum(myEnumEEnum, MyEnum.class, "MyEnum");
    addEEnumLiteral(myEnumEEnum, MyEnum.ZERO);
    addEEnumLiteral(myEnumEEnum, MyEnum.ONE);
    addEEnumLiteral(myEnumEEnum, MyEnum.TWO);
    addEEnumLiteral(myEnumEEnum, MyEnum.THREE);

    // Initialize data types
    initEDataType(myStringEDataType, String.class, "MyString", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

    // Create resource
    createResource(eNS_URI);

    // Create annotations
    // http://www.eclipse.org/emf/CDO
    createCDOAnnotations();
  }

  /**
   * Initializes the annotations for <b>http://www.eclipse.org/emf/CDO</b>.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected void createCDOAnnotations()
  {
    String source = "http://www.eclipse.org/emf/CDO";
    addAnnotation(getHolder_Held(), source, new String[] { "persistent", "true", "filter", "owned" });
  }

} // Model6PackageImpl
