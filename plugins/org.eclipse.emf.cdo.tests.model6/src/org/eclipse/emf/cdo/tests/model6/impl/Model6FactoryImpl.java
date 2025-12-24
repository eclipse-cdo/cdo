/*
 * Copyright (c) 2011-2013, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model6.impl;

import org.eclipse.emf.cdo.tests.model6.A;
import org.eclipse.emf.cdo.tests.model6.B;
import org.eclipse.emf.cdo.tests.model6.BaseObject;
import org.eclipse.emf.cdo.tests.model6.C;
import org.eclipse.emf.cdo.tests.model6.CanReferenceLegacy;
import org.eclipse.emf.cdo.tests.model6.ContainmentObject;
import org.eclipse.emf.cdo.tests.model6.D;
import org.eclipse.emf.cdo.tests.model6.E;
import org.eclipse.emf.cdo.tests.model6.EmptyStringDefault;
import org.eclipse.emf.cdo.tests.model6.EmptyStringDefaultUnsettable;
import org.eclipse.emf.cdo.tests.model6.F;
import org.eclipse.emf.cdo.tests.model6.G;
import org.eclipse.emf.cdo.tests.model6.HasNillableAttribute;
import org.eclipse.emf.cdo.tests.model6.Holder;
import org.eclipse.emf.cdo.tests.model6.Model6Factory;
import org.eclipse.emf.cdo.tests.model6.Model6Package;
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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

import java.util.Map;

//import org.eclipse.emf.cdo.tests.model6.*;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!-- end-user-doc -->
 * @generated
 */
public class Model6FactoryImpl extends EFactoryImpl implements Model6Factory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public static Model6Factory init()
  {
    try
    {
      Model6Factory theModel6Factory = (Model6Factory)EPackage.Registry.INSTANCE.getEFactory(Model6Package.eNS_URI);
      if (theModel6Factory != null)
      {
        return theModel6Factory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new Model6FactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public Model6FactoryImpl()
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
    case Model6Package.ROOT:
      return createRoot();
    case Model6Package.BASE_OBJECT:
      return createBaseObject();
    case Model6Package.REFERENCE_OBJECT:
      return createReferenceObject();
    case Model6Package.CONTAINMENT_OBJECT:
      return createContainmentObject();
    case Model6Package.UNORDERED_LIST:
      return createUnorderedList();
    case Model6Package.PROPERTIES_MAP:
      return createPropertiesMap();
    case Model6Package.PROPERTIES_MAP_ENTRY:
      return (EObject)createPropertiesMapEntry();
    case Model6Package.PROPERTIES_MAP_ENTRY_VALUE:
      return createPropertiesMapEntryValue();
    case Model6Package.A:
      return createA();
    case Model6Package.B:
      return createB();
    case Model6Package.C:
      return createC();
    case Model6Package.D:
      return createD();
    case Model6Package.E:
      return createE();
    case Model6Package.F:
      return createF();
    case Model6Package.G:
      return createG();
    case Model6Package.MY_ENUM_LIST:
      return createMyEnumList();
    case Model6Package.MY_ENUM_LIST_UNSETTABLE:
      return createMyEnumListUnsettable();
    case Model6Package.HOLDER:
      return createHolder();
    case Model6Package.THING:
      return createThing();
    case Model6Package.HAS_NILLABLE_ATTRIBUTE:
      return createHasNillableAttribute();
    case Model6Package.EMPTY_STRING_DEFAULT:
      return createEmptyStringDefault();
    case Model6Package.EMPTY_STRING_DEFAULT_UNSETTABLE:
      return createEmptyStringDefaultUnsettable();
    case Model6Package.UNSETTABLE_ATTRIBUTES:
      return createUnsettableAttributes();
    case Model6Package.CAN_REFERENCE_LEGACY:
      return createCanReferenceLegacy();
    default:
      throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object createFromString(EDataType eDataType, String initialValue)
  {
    switch (eDataType.getClassifierID())
    {
    case Model6Package.MY_ENUM:
      return createMyEnumFromString(eDataType, initialValue);
    case Model6Package.MY_STRING:
      return createMyStringFromString(eDataType, initialValue);
    default:
      throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String convertToString(EDataType eDataType, Object instanceValue)
  {
    switch (eDataType.getClassifierID())
    {
    case Model6Package.MY_ENUM:
      return convertMyEnumToString(eDataType, instanceValue);
    case Model6Package.MY_STRING:
      return convertMyStringToString(eDataType, instanceValue);
    default:
      throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Root createRoot()
  {
    RootImpl root = new RootImpl();
    return root;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public BaseObject createBaseObject()
  {
    BaseObjectImpl baseObject = new BaseObjectImpl();
    return baseObject;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ReferenceObject createReferenceObject()
  {
    ReferenceObjectImpl referenceObject = new ReferenceObjectImpl();
    return referenceObject;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ContainmentObject createContainmentObject()
  {
    ContainmentObjectImpl containmentObject = new ContainmentObjectImpl();
    return containmentObject;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public UnorderedList createUnorderedList()
  {
    UnorderedListImpl unorderedList = new UnorderedListImpl();
    return unorderedList;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public PropertiesMap createPropertiesMap()
  {
    PropertiesMapImpl propertiesMap = new PropertiesMapImpl();
    return propertiesMap;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Map.Entry<String, PropertiesMapEntryValue> createPropertiesMapEntry()
  {
    PropertiesMapEntryImpl propertiesMapEntry = new PropertiesMapEntryImpl();
    return propertiesMapEntry;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public PropertiesMapEntryValue createPropertiesMapEntryValue()
  {
    PropertiesMapEntryValueImpl propertiesMapEntryValue = new PropertiesMapEntryValueImpl();
    return propertiesMapEntryValue;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public A createA()
  {
    AImpl a = new AImpl();
    return a;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public B createB()
  {
    BImpl b = new BImpl();
    return b;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public C createC()
  {
    CImpl c = new CImpl();
    return c;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public D createD()
  {
    DImpl d = new DImpl();
    return d;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public E createE()
  {
    EImpl e = new EImpl();
    return e;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public F createF()
  {
    FImpl f = new FImpl();
    return f;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public G createG()
  {
    GImpl g = new GImpl();
    return g;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public MyEnumList createMyEnumList()
  {
    MyEnumListImpl myEnumList = new MyEnumListImpl();
    return myEnumList;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public MyEnumListUnsettable createMyEnumListUnsettable()
  {
    MyEnumListUnsettableImpl myEnumListUnsettable = new MyEnumListUnsettableImpl();
    return myEnumListUnsettable;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Holder createHolder()
  {
    HolderImpl holder = new HolderImpl();
    return holder;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Thing createThing()
  {
    ThingImpl thing = new ThingImpl();
    return thing;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public HasNillableAttribute createHasNillableAttribute()
  {
    HasNillableAttributeImpl hasNillableAttribute = new HasNillableAttributeImpl();
    return hasNillableAttribute;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EmptyStringDefault createEmptyStringDefault()
  {
    EmptyStringDefaultImpl emptyStringDefault = new EmptyStringDefaultImpl();
    return emptyStringDefault;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EmptyStringDefaultUnsettable createEmptyStringDefaultUnsettable()
  {
    EmptyStringDefaultUnsettableImpl emptyStringDefaultUnsettable = new EmptyStringDefaultUnsettableImpl();
    return emptyStringDefaultUnsettable;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public UnsettableAttributes createUnsettableAttributes()
  {
    UnsettableAttributesImpl unsettableAttributes = new UnsettableAttributesImpl();
    return unsettableAttributes;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public CanReferenceLegacy createCanReferenceLegacy()
  {
    CanReferenceLegacyImpl canReferenceLegacy = new CanReferenceLegacyImpl();
    return canReferenceLegacy;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public MyEnum createMyEnumFromString(EDataType eDataType, String initialValue)
  {
    MyEnum result = MyEnum.get(initialValue);
    if (result == null)
    {
      throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    }
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertMyEnumToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String createMyStringFromString(EDataType eDataType, String initialValue)
  {
    return (String)super.createFromString(eDataType, initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertMyStringToString(EDataType eDataType, Object instanceValue)
  {
    return super.convertToString(eDataType, instanceValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Model6Package getModel6Package()
  {
    return (Model6Package)getEPackage();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static Model6Package getPackage()
  {
    return Model6Package.eINSTANCE;
  }

} // Model6FactoryImpl
