/*
 * Copyright (c) 2011-2013, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model6;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
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
 * @see org.eclipse.emf.cdo.tests.model6.Model6Factory
 * @model kind="package"
 * @generated
 */
public interface Model6Package extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "model6";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://www.eclipse.org/emf/CDO/tests/model6/1.0.0";

  /**
   * The package namespace name.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "model6";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  Model6Package eINSTANCE = org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model6.impl.RootImpl <em>Root</em>}' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.eclipse.emf.cdo.tests.model6.impl.RootImpl
   * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getRoot()
   * @generated
   */
  int ROOT = 0;

  /**
   * The feature id for the '<em><b>List A</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int ROOT__LIST_A = 0;

  /**
   * The feature id for the '<em><b>List B</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int ROOT__LIST_B = 1;

  /**
   * The feature id for the '<em><b>List C</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int ROOT__LIST_C = 2;

  /**
   * The feature id for the '<em><b>List D</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int ROOT__LIST_D = 3;

  /**
   * The number of structural features of the '<em>Root</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ROOT_FEATURE_COUNT = 4;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model6.impl.BaseObjectImpl <em>Base Object</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model6.impl.BaseObjectImpl
   * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getBaseObject()
   * @generated
   */
  int BASE_OBJECT = 1;

  /**
   * The feature id for the '<em><b>Attribute Optional</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   * @ordered
   */
  int BASE_OBJECT__ATTRIBUTE_OPTIONAL = 0;

  /**
   * The feature id for the '<em><b>Attribute Required</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   * @ordered
   */
  int BASE_OBJECT__ATTRIBUTE_REQUIRED = 1;

  /**
   * The feature id for the '<em><b>Attribute List</b></em>' attribute list. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   * @ordered
   */
  int BASE_OBJECT__ATTRIBUTE_LIST = 2;

  /**
   * The number of structural features of the '<em>Base Object</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   * @ordered
   */
  int BASE_OBJECT_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model6.impl.ReferenceObjectImpl <em>Reference Object</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model6.impl.ReferenceObjectImpl
   * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getReferenceObject()
   * @generated
   */
  int REFERENCE_OBJECT = 2;

  /**
   * The feature id for the '<em><b>Attribute Optional</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   * @ordered
   */
  int REFERENCE_OBJECT__ATTRIBUTE_OPTIONAL = BASE_OBJECT__ATTRIBUTE_OPTIONAL;

  /**
   * The feature id for the '<em><b>Attribute Required</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   * @ordered
   */
  int REFERENCE_OBJECT__ATTRIBUTE_REQUIRED = BASE_OBJECT__ATTRIBUTE_REQUIRED;

  /**
   * The feature id for the '<em><b>Attribute List</b></em>' attribute list. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   * @ordered
   */
  int REFERENCE_OBJECT__ATTRIBUTE_LIST = BASE_OBJECT__ATTRIBUTE_LIST;

  /**
   * The feature id for the '<em><b>Reference Optional</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   * @ordered
   */
  int REFERENCE_OBJECT__REFERENCE_OPTIONAL = BASE_OBJECT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Reference List</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   * @ordered
   */
  int REFERENCE_OBJECT__REFERENCE_LIST = BASE_OBJECT_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Reference Object</em>' class.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int REFERENCE_OBJECT_FEATURE_COUNT = BASE_OBJECT_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model6.impl.ContainmentObjectImpl <em>Containment Object</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model6.impl.ContainmentObjectImpl
   * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getContainmentObject()
   * @generated
   */
  int CONTAINMENT_OBJECT = 3;

  /**
   * The feature id for the '<em><b>Attribute Optional</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   * @ordered
   */
  int CONTAINMENT_OBJECT__ATTRIBUTE_OPTIONAL = BASE_OBJECT__ATTRIBUTE_OPTIONAL;

  /**
   * The feature id for the '<em><b>Attribute Required</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   * @ordered
   */
  int CONTAINMENT_OBJECT__ATTRIBUTE_REQUIRED = BASE_OBJECT__ATTRIBUTE_REQUIRED;

  /**
   * The feature id for the '<em><b>Attribute List</b></em>' attribute list. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   * @ordered
   */
  int CONTAINMENT_OBJECT__ATTRIBUTE_LIST = BASE_OBJECT__ATTRIBUTE_LIST;

  /**
   * The feature id for the '<em><b>Containment Optional</b></em>' containment reference.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int CONTAINMENT_OBJECT__CONTAINMENT_OPTIONAL = BASE_OBJECT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Containment List</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int CONTAINMENT_OBJECT__CONTAINMENT_LIST = BASE_OBJECT_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Containment Object</em>' class.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int CONTAINMENT_OBJECT_FEATURE_COUNT = BASE_OBJECT_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model6.impl.UnorderedListImpl <em>Unordered List</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model6.impl.UnorderedListImpl
   * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getUnorderedList()
   * @generated
   */
  int UNORDERED_LIST = 4;

  /**
   * The feature id for the '<em><b>Contained</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int UNORDERED_LIST__CONTAINED = 0;

  /**
   * The feature id for the '<em><b>Referenced</b></em>' reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int UNORDERED_LIST__REFERENCED = 1;

  /**
   * The number of structural features of the '<em>Unordered List</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   * @ordered
   */
  int UNORDERED_LIST_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model6.impl.PropertiesMapImpl <em>Properties Map</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model6.impl.PropertiesMapImpl
   * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getPropertiesMap()
   * @generated
   */
  int PROPERTIES_MAP = 5;

  /**
   * The feature id for the '<em><b>Label</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROPERTIES_MAP__LABEL = 0;

  /**
   * The feature id for the '<em><b>Persistent Map</b></em>' map.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROPERTIES_MAP__PERSISTENT_MAP = 1;

  /**
   * The feature id for the '<em><b>Transient Map</b></em>' map.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROPERTIES_MAP__TRANSIENT_MAP = 2;

  /**
   * The number of structural features of the '<em>Properties Map</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROPERTIES_MAP_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model6.impl.PropertiesMapEntryImpl <em>Properties Map Map.Entry</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model6.impl.PropertiesMapEntryImpl
   * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getPropertiesMapEntry()
   * @generated
   */
  int PROPERTIES_MAP_ENTRY = 6;

  /**
   * The feature id for the '<em><b>Key</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROPERTIES_MAP_ENTRY__KEY = 0;

  /**
   * The feature id for the '<em><b>Value</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROPERTIES_MAP_ENTRY__VALUE = 1;

  /**
   * The number of structural features of the '<em>Properties Map Map.Entry</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROPERTIES_MAP_ENTRY_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model6.impl.PropertiesMapEntryValueImpl <em>Properties Map Map.Entry Value</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model6.impl.PropertiesMapEntryValueImpl
   * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getPropertiesMapEntryValue()
   * @generated
   */
  int PROPERTIES_MAP_ENTRY_VALUE = 7;

  /**
   * The feature id for the '<em><b>Label</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROPERTIES_MAP_ENTRY_VALUE__LABEL = 0;

  /**
   * The number of structural features of the '<em>Properties Map Map.Entry Value</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROPERTIES_MAP_ENTRY_VALUE_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model6.impl.AImpl <em>A</em>}' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.eclipse.emf.cdo.tests.model6.impl.AImpl
   * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getA()
   * @generated
   */
  int A = 8;

  /**
   * The feature id for the '<em><b>Owned Ds</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int A__OWNED_DS = 0;

  /**
   * The feature id for the '<em><b>Owned Bs</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int A__OWNED_BS = 1;

  /**
   * The number of structural features of the '<em>A</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int A_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model6.impl.BImpl <em>B</em>}' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.eclipse.emf.cdo.tests.model6.impl.BImpl
   * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getB()
   * @generated
   */
  int B = 9;

  /**
   * The feature id for the '<em><b>Owned C</b></em>' containment reference. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   * @ordered
   */
  int B__OWNED_C = 0;

  /**
   * The number of structural features of the '<em>B</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int B_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model6.impl.CImpl <em>C</em>}' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.eclipse.emf.cdo.tests.model6.impl.CImpl
   * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getC()
   * @generated
   */
  int C = 10;

  /**
   * The number of structural features of the '<em>C</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int C_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model6.impl.DImpl <em>D</em>}' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.eclipse.emf.cdo.tests.model6.impl.DImpl
   * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getD()
   * @generated
   */
  int D = 11;

  /**
   * The feature id for the '<em><b>Data</b></em>' containment reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int D__DATA = 0;

  /**
   * The number of structural features of the '<em>D</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int D_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model6.impl.EImpl <em>E</em>}' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.eclipse.emf.cdo.tests.model6.impl.EImpl
   * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getE()
   * @generated
   */
  int E = 12;

  /**
   * The feature id for the '<em><b>Owned As</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int E__OWNED_AS = 0;

  /**
   * The number of structural features of the '<em>E</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int E_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model6.impl.FImpl <em>F</em>}' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.eclipse.emf.cdo.tests.model6.impl.FImpl
   * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getF()
   * @generated
   */
  int F = 13;

  /**
   * The feature id for the '<em><b>Owned Es</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   * @ordered
   */
  int F__OWNED_ES = 0;

  /**
   * The number of structural features of the '<em>F</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int F_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model6.impl.GImpl <em>G</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model6.impl.GImpl
   * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getG()
   * @generated
   */
  int G = 14;

  /**
   * The feature id for the '<em><b>Dummy</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int G__DUMMY = 0;

  /**
   * The feature id for the '<em><b>Reference</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int G__REFERENCE = 1;

  /**
   * The feature id for the '<em><b>List</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int G__LIST = 2;

  /**
   * The number of structural features of the '<em>G</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int G_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model6.impl.MyEnumListImpl <em>My Enum List</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model6.impl.MyEnumListImpl
   * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getMyEnumList()
   * @generated
   */
  int MY_ENUM_LIST = 15;

  /**
   * The feature id for the '<em><b>My Enum</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MY_ENUM_LIST__MY_ENUM = 0;

  /**
   * The number of structural features of the '<em>My Enum List</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MY_ENUM_LIST_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model6.impl.MyEnumListUnsettableImpl <em>My Enum List Unsettable</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model6.impl.MyEnumListUnsettableImpl
   * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getMyEnumListUnsettable()
   * @generated
   */
  int MY_ENUM_LIST_UNSETTABLE = 16;

  /**
   * The feature id for the '<em><b>My Enum</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MY_ENUM_LIST_UNSETTABLE__MY_ENUM = 0;

  /**
   * The number of structural features of the '<em>My Enum List Unsettable</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MY_ENUM_LIST_UNSETTABLE_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model6.impl.HoldableImpl <em>Holdable</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model6.impl.HoldableImpl
   * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getHoldable()
   * @generated
   */
  int HOLDABLE = 19;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int HOLDABLE__NAME = 0;

  /**
   * The number of structural features of the '<em>Holdable</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int HOLDABLE_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model6.impl.HolderImpl <em>Holder</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model6.impl.HolderImpl
   * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getHolder()
   * @generated
   */
  int HOLDER = 17;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int HOLDER__NAME = HOLDABLE__NAME;

  /**
   * The feature id for the '<em><b>Held</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int HOLDER__HELD = HOLDABLE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Owned</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int HOLDER__OWNED = HOLDABLE_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Holder</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int HOLDER_FEATURE_COUNT = HOLDABLE_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model6.impl.ThingImpl <em>Thing</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model6.impl.ThingImpl
   * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getThing()
   * @generated
   */
  int THING = 18;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int THING__NAME = HOLDABLE__NAME;

  /**
   * The number of structural features of the '<em>Thing</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int THING_FEATURE_COUNT = HOLDABLE_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model6.impl.HasNillableAttributeImpl <em>Has Nillable Attribute</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model6.impl.HasNillableAttributeImpl
   * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getHasNillableAttribute()
   * @generated
   */
  int HAS_NILLABLE_ATTRIBUTE = 20;

  /**
   * The feature id for the '<em><b>Nillable</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int HAS_NILLABLE_ATTRIBUTE__NILLABLE = 0;

  /**
   * The number of structural features of the '<em>Has Nillable Attribute</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int HAS_NILLABLE_ATTRIBUTE_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model6.impl.EmptyStringDefaultImpl <em>Empty String Default</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model6.impl.EmptyStringDefaultImpl
   * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getEmptyStringDefault()
   * @generated
   */
  int EMPTY_STRING_DEFAULT = 21;

  /**
   * The feature id for the '<em><b>Attribute</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EMPTY_STRING_DEFAULT__ATTRIBUTE = 0;

  /**
   * The number of structural features of the '<em>Empty String Default</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EMPTY_STRING_DEFAULT_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model6.impl.EmptyStringDefaultUnsettableImpl <em>Empty String Default Unsettable</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model6.impl.EmptyStringDefaultUnsettableImpl
   * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getEmptyStringDefaultUnsettable()
   * @generated
   */
  int EMPTY_STRING_DEFAULT_UNSETTABLE = 22;

  /**
   * The feature id for the '<em><b>Attribute</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EMPTY_STRING_DEFAULT_UNSETTABLE__ATTRIBUTE = 0;

  /**
   * The number of structural features of the '<em>Empty String Default Unsettable</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EMPTY_STRING_DEFAULT_UNSETTABLE_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model6.impl.UnsettableAttributesImpl <em>Unsettable Attributes</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model6.impl.UnsettableAttributesImpl
   * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getUnsettableAttributes()
   * @generated
   */
  int UNSETTABLE_ATTRIBUTES = 23;

  /**
   * The feature id for the '<em><b>Attr Big Decimal</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int UNSETTABLE_ATTRIBUTES__ATTR_BIG_DECIMAL = 0;

  /**
   * The feature id for the '<em><b>Attr Big Integer</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int UNSETTABLE_ATTRIBUTES__ATTR_BIG_INTEGER = 1;

  /**
   * The feature id for the '<em><b>Attr Boolean</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int UNSETTABLE_ATTRIBUTES__ATTR_BOOLEAN = 2;

  /**
   * The feature id for the '<em><b>Attr Boolean Object</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int UNSETTABLE_ATTRIBUTES__ATTR_BOOLEAN_OBJECT = 3;

  /**
   * The feature id for the '<em><b>Attr Byte</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int UNSETTABLE_ATTRIBUTES__ATTR_BYTE = 4;

  /**
   * The feature id for the '<em><b>Attr Byte Array</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int UNSETTABLE_ATTRIBUTES__ATTR_BYTE_ARRAY = 5;

  /**
   * The feature id for the '<em><b>Attr Byte Object</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int UNSETTABLE_ATTRIBUTES__ATTR_BYTE_OBJECT = 6;

  /**
   * The feature id for the '<em><b>Attr Char</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int UNSETTABLE_ATTRIBUTES__ATTR_CHAR = 7;

  /**
   * The feature id for the '<em><b>Attr Character Object</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int UNSETTABLE_ATTRIBUTES__ATTR_CHARACTER_OBJECT = 8;

  /**
   * The feature id for the '<em><b>Attr Date</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int UNSETTABLE_ATTRIBUTES__ATTR_DATE = 9;

  /**
   * The feature id for the '<em><b>Attr Double</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int UNSETTABLE_ATTRIBUTES__ATTR_DOUBLE = 10;

  /**
   * The feature id for the '<em><b>Attr Double Object</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int UNSETTABLE_ATTRIBUTES__ATTR_DOUBLE_OBJECT = 11;

  /**
   * The feature id for the '<em><b>Attr Float</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int UNSETTABLE_ATTRIBUTES__ATTR_FLOAT = 12;

  /**
   * The feature id for the '<em><b>Attr Float Object</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int UNSETTABLE_ATTRIBUTES__ATTR_FLOAT_OBJECT = 13;

  /**
   * The feature id for the '<em><b>Attr Int</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int UNSETTABLE_ATTRIBUTES__ATTR_INT = 14;

  /**
   * The feature id for the '<em><b>Attr Integer Object</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int UNSETTABLE_ATTRIBUTES__ATTR_INTEGER_OBJECT = 15;

  /**
   * The feature id for the '<em><b>Attr Java Class</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int UNSETTABLE_ATTRIBUTES__ATTR_JAVA_CLASS = 16;

  /**
   * The feature id for the '<em><b>Attr Java Object</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int UNSETTABLE_ATTRIBUTES__ATTR_JAVA_OBJECT = 17;

  /**
   * The feature id for the '<em><b>Attr Long</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int UNSETTABLE_ATTRIBUTES__ATTR_LONG = 18;

  /**
   * The feature id for the '<em><b>Attr Long Object</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int UNSETTABLE_ATTRIBUTES__ATTR_LONG_OBJECT = 19;

  /**
   * The feature id for the '<em><b>Attr Short</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int UNSETTABLE_ATTRIBUTES__ATTR_SHORT = 20;

  /**
   * The feature id for the '<em><b>Attr Short Object</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int UNSETTABLE_ATTRIBUTES__ATTR_SHORT_OBJECT = 21;

  /**
   * The feature id for the '<em><b>Attr String</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int UNSETTABLE_ATTRIBUTES__ATTR_STRING = 22;

  /**
   * The number of structural features of the '<em>Unsettable Attributes</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int UNSETTABLE_ATTRIBUTES_FEATURE_COUNT = 23;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model6.impl.CanReferenceLegacyImpl <em>Can Reference Legacy</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model6.impl.CanReferenceLegacyImpl
   * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getCanReferenceLegacy()
   * @generated
   */
  int CAN_REFERENCE_LEGACY = 24;

  /**
   * The feature id for the '<em><b>Single Containment</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CAN_REFERENCE_LEGACY__SINGLE_CONTAINMENT = 0;

  /**
   * The feature id for the '<em><b>Multiple Containment</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CAN_REFERENCE_LEGACY__MULTIPLE_CONTAINMENT = 1;

  /**
   * The feature id for the '<em><b>Single Reference</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CAN_REFERENCE_LEGACY__SINGLE_REFERENCE = 2;

  /**
   * The feature id for the '<em><b>Multiple Reference</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CAN_REFERENCE_LEGACY__MULTIPLE_REFERENCE = 3;

  /**
   * The number of structural features of the '<em>Can Reference Legacy</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CAN_REFERENCE_LEGACY_FEATURE_COUNT = 4;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model6.MyEnum <em>My Enum</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.tests.model6.MyEnum
   * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getMyEnum()
   * @generated
   */
  int MY_ENUM = 25;

  /**
   * The meta object id for the '<em>My String</em>' data type.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see java.lang.String
   * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getMyString()
   * @generated
   */
  int MY_STRING = 26;

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model6.Root <em>Root</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Root</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.Root
   * @generated
   */
  EClass getRoot();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.tests.model6.Root#getListA <em>List A</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>List A</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.Root#getListA()
   * @see #getRoot()
   * @generated
   */
  EReference getRoot_ListA();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.tests.model6.Root#getListB <em>List B</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>List B</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.Root#getListB()
   * @see #getRoot()
   * @generated
   */
  EReference getRoot_ListB();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.tests.model6.Root#getListC <em>List C</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>List C</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.Root#getListC()
   * @see #getRoot()
   * @generated
   */
  EReference getRoot_ListC();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.tests.model6.Root#getListD <em>List D</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>List D</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.Root#getListD()
   * @see #getRoot()
   * @generated
   */
  EReference getRoot_ListD();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model6.BaseObject <em>Base Object</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Base Object</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.BaseObject
   * @generated
   */
  EClass getBaseObject();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model6.BaseObject#getAttributeOptional <em>Attribute Optional</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Attribute Optional</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.BaseObject#getAttributeOptional()
   * @see #getBaseObject()
   * @generated
   */
  EAttribute getBaseObject_AttributeOptional();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model6.BaseObject#getAttributeRequired <em>Attribute Required</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Attribute Required</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.BaseObject#getAttributeRequired()
   * @see #getBaseObject()
   * @generated
   */
  EAttribute getBaseObject_AttributeRequired();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.emf.cdo.tests.model6.BaseObject#getAttributeList <em>Attribute List</em>}'.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Attribute List</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.BaseObject#getAttributeList()
   * @see #getBaseObject()
   * @generated
   */
  EAttribute getBaseObject_AttributeList();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model6.ReferenceObject <em>Reference Object</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>Reference Object</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.ReferenceObject
   * @generated
   */
  EClass getReferenceObject();

  /**
   * Returns the meta object for the reference '
   * {@link org.eclipse.emf.cdo.tests.model6.ReferenceObject#getReferenceOptional <em>Reference Optional</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the reference '<em>Reference Optional</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.ReferenceObject#getReferenceOptional()
   * @see #getReferenceObject()
   * @generated
   */
  EReference getReferenceObject_ReferenceOptional();

  /**
   * Returns the meta object for the reference list '
   * {@link org.eclipse.emf.cdo.tests.model6.ReferenceObject#getReferenceList <em>Reference List</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the reference list '<em>Reference List</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.ReferenceObject#getReferenceList()
   * @see #getReferenceObject()
   * @generated
   */
  EReference getReferenceObject_ReferenceList();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model6.ContainmentObject <em>Containment Object</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>Containment Object</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.ContainmentObject
   * @generated
   */
  EClass getContainmentObject();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.emf.cdo.tests.model6.ContainmentObject#getContainmentOptional <em>Containment Optional</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Containment Optional</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.ContainmentObject#getContainmentOptional()
   * @see #getContainmentObject()
   * @generated
   */
  EReference getContainmentObject_ContainmentOptional();

  /**
   * Returns the meta object for the containment reference list '
   * {@link org.eclipse.emf.cdo.tests.model6.ContainmentObject#getContainmentList <em>Containment List</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the containment reference list '<em>Containment List</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.ContainmentObject#getContainmentList()
   * @see #getContainmentObject()
   * @generated
   */
  EReference getContainmentObject_ContainmentList();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model6.UnorderedList <em>Unordered List</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>Unordered List</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.UnorderedList
   * @generated
   */
  EClass getUnorderedList();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.tests.model6.UnorderedList#getContained <em>Contained</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Contained</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.UnorderedList#getContained()
   * @see #getUnorderedList()
   * @generated
   */
  EReference getUnorderedList_Contained();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.tests.model6.UnorderedList#getReferenced <em>Referenced</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Referenced</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.UnorderedList#getReferenced()
   * @see #getUnorderedList()
   * @generated
   */
  EReference getUnorderedList_Referenced();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model6.PropertiesMap <em>Properties Map</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Properties Map</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.PropertiesMap
   * @generated
   */
  EClass getPropertiesMap();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model6.PropertiesMap#getLabel <em>Label</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Label</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.PropertiesMap#getLabel()
   * @see #getPropertiesMap()
   * @generated
   */
  EAttribute getPropertiesMap_Label();

  /**
   * Returns the meta object for the map '{@link org.eclipse.emf.cdo.tests.model6.PropertiesMap#getPersistentMap <em>Persistent Map</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the map '<em>Persistent Map</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.PropertiesMap#getPersistentMap()
   * @see #getPropertiesMap()
   * @generated
   */
  EReference getPropertiesMap_PersistentMap();

  /**
   * Returns the meta object for the map '{@link org.eclipse.emf.cdo.tests.model6.PropertiesMap#getTransientMap <em>Transient Map</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the map '<em>Transient Map</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.PropertiesMap#getTransientMap()
   * @see #getPropertiesMap()
   * @generated
   */
  EReference getPropertiesMap_TransientMap();

  /**
   * Returns the meta object for class '{@link java.util.Map.Entry <em>Properties Map Map.Entry</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Properties Map Map.Entry</em>'.
   * @see java.util.Map.Entry
   * @model keyDataType="org.eclipse.emf.ecore.EString"
   *        valueType="org.eclipse.emf.cdo.tests.model6.PropertiesMapEntryValue" valueContainment="true" valueResolveProxies="true"
   * @generated
   */
  EClass getPropertiesMapEntry();

  /**
   * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Key</em>'.
   * @see java.util.Map.Entry
   * @see #getPropertiesMapEntry()
   * @generated
   */
  EAttribute getPropertiesMapEntry_Key();

  /**
   * Returns the meta object for the containment reference '{@link java.util.Map.Entry <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Value</em>'.
   * @see java.util.Map.Entry
   * @see #getPropertiesMapEntry()
   * @generated
   */
  EReference getPropertiesMapEntry_Value();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model6.PropertiesMapEntryValue <em>Properties Map Map.Entry Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Properties Map Map.Entry Value</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.PropertiesMapEntryValue
   * @generated
   */
  EClass getPropertiesMapEntryValue();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model6.PropertiesMapEntryValue#getLabel <em>Label</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Label</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.PropertiesMapEntryValue#getLabel()
   * @see #getPropertiesMapEntryValue()
   * @generated
   */
  EAttribute getPropertiesMapEntryValue_Label();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model6.A <em>A</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>A</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.A
   * @generated
   */
  EClass getA();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.tests.model6.A#getOwnedDs <em>Owned Ds</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Owned Ds</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.A#getOwnedDs()
   * @see #getA()
   * @generated
   */
  EReference getA_OwnedDs();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.tests.model6.A#getOwnedBs <em>Owned Bs</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Owned Bs</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.A#getOwnedBs()
   * @see #getA()
   * @generated
   */
  EReference getA_OwnedBs();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model6.B <em>B</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>B</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.B
   * @generated
   */
  EClass getB();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.emf.cdo.tests.model6.B#getOwnedC <em>Owned C</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Owned C</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.B#getOwnedC()
   * @see #getB()
   * @generated
   */
  EReference getB_OwnedC();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model6.C <em>C</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>C</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.C
   * @generated
   */
  EClass getC();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model6.D <em>D</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>D</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.D
   * @generated
   */
  EClass getD();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.emf.cdo.tests.model6.D#getData <em>Data</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Data</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.D#getData()
   * @see #getD()
   * @generated
   */
  EReference getD_Data();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model6.E <em>E</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>E</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.E
   * @generated
   */
  EClass getE();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.tests.model6.E#getOwnedAs <em>Owned As</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Owned As</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.E#getOwnedAs()
   * @see #getE()
   * @generated
   */
  EReference getE_OwnedAs();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model6.F <em>F</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>F</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.F
   * @generated
   */
  EClass getF();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.tests.model6.F#getOwnedEs <em>Owned Es</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Owned Es</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.F#getOwnedEs()
   * @see #getF()
   * @generated
   */
  EReference getF_OwnedEs();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model6.G <em>G</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>G</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.G
   * @generated
   */
  EClass getG();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model6.G#getDummy <em>Dummy</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Dummy</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.G#getDummy()
   * @see #getG()
   * @generated
   */
  EAttribute getG_Dummy();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.tests.model6.G#getReference <em>Reference</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Reference</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.G#getReference()
   * @see #getG()
   * @generated
   */
  EReference getG_Reference();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.tests.model6.G#getList <em>List</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>List</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.G#getList()
   * @see #getG()
   * @generated
   */
  EReference getG_List();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model6.MyEnumList <em>My Enum List</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>My Enum List</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.MyEnumList
   * @generated
   */
  EClass getMyEnumList();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.emf.cdo.tests.model6.MyEnumList#getMyEnum <em>My Enum</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>My Enum</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.MyEnumList#getMyEnum()
   * @see #getMyEnumList()
   * @generated
   */
  EAttribute getMyEnumList_MyEnum();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model6.MyEnumListUnsettable <em>My Enum List Unsettable</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>My Enum List Unsettable</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.MyEnumListUnsettable
   * @generated
   */
  EClass getMyEnumListUnsettable();

  /**
   * Returns the meta object for the attribute list '{@link org.eclipse.emf.cdo.tests.model6.MyEnumListUnsettable#getMyEnum <em>My Enum</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>My Enum</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.MyEnumListUnsettable#getMyEnum()
   * @see #getMyEnumListUnsettable()
   * @generated
   */
  EAttribute getMyEnumListUnsettable_MyEnum();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model6.Holder <em>Holder</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Holder</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.Holder
   * @generated
   */
  EClass getHolder();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.tests.model6.Holder#getHeld <em>Held</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Held</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.Holder#getHeld()
   * @see #getHolder()
   * @generated
   */
  EReference getHolder_Held();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.tests.model6.Holder#getOwned <em>Owned</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Owned</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.Holder#getOwned()
   * @see #getHolder()
   * @generated
   */
  EReference getHolder_Owned();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model6.Thing <em>Thing</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Thing</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.Thing
   * @generated
   */
  EClass getThing();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model6.Holdable <em>Holdable</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Holdable</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.Holdable
   * @generated
   */
  EClass getHoldable();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model6.Holdable#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.Holdable#getName()
   * @see #getHoldable()
   * @generated
   */
  EAttribute getHoldable_Name();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model6.HasNillableAttribute <em>Has Nillable Attribute</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Has Nillable Attribute</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.HasNillableAttribute
   * @generated
   */
  EClass getHasNillableAttribute();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model6.HasNillableAttribute#getNillable <em>Nillable</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Nillable</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.HasNillableAttribute#getNillable()
   * @see #getHasNillableAttribute()
   * @generated
   */
  EAttribute getHasNillableAttribute_Nillable();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model6.EmptyStringDefault <em>Empty String Default</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Empty String Default</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.EmptyStringDefault
   * @generated
   */
  EClass getEmptyStringDefault();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model6.EmptyStringDefault#getAttribute <em>Attribute</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Attribute</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.EmptyStringDefault#getAttribute()
   * @see #getEmptyStringDefault()
   * @generated
   */
  EAttribute getEmptyStringDefault_Attribute();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model6.EmptyStringDefaultUnsettable <em>Empty String Default Unsettable</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Empty String Default Unsettable</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.EmptyStringDefaultUnsettable
   * @generated
   */
  EClass getEmptyStringDefaultUnsettable();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model6.EmptyStringDefaultUnsettable#getAttribute <em>Attribute</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Attribute</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.EmptyStringDefaultUnsettable#getAttribute()
   * @see #getEmptyStringDefaultUnsettable()
   * @generated
   */
  EAttribute getEmptyStringDefaultUnsettable_Attribute();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model6.UnsettableAttributes <em>Unsettable Attributes</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Unsettable Attributes</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.UnsettableAttributes
   * @generated
   */
  EClass getUnsettableAttributes();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model6.UnsettableAttributes#getAttrBigDecimal <em>Attr Big Decimal</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Attr Big Decimal</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.UnsettableAttributes#getAttrBigDecimal()
   * @see #getUnsettableAttributes()
   * @generated
   */
  EAttribute getUnsettableAttributes_AttrBigDecimal();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model6.UnsettableAttributes#getAttrBigInteger <em>Attr Big Integer</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Attr Big Integer</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.UnsettableAttributes#getAttrBigInteger()
   * @see #getUnsettableAttributes()
   * @generated
   */
  EAttribute getUnsettableAttributes_AttrBigInteger();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model6.UnsettableAttributes#isAttrBoolean <em>Attr Boolean</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Attr Boolean</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.UnsettableAttributes#isAttrBoolean()
   * @see #getUnsettableAttributes()
   * @generated
   */
  EAttribute getUnsettableAttributes_AttrBoolean();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model6.UnsettableAttributes#getAttrBooleanObject <em>Attr Boolean Object</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Attr Boolean Object</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.UnsettableAttributes#getAttrBooleanObject()
   * @see #getUnsettableAttributes()
   * @generated
   */
  EAttribute getUnsettableAttributes_AttrBooleanObject();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model6.UnsettableAttributes#getAttrByte <em>Attr Byte</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Attr Byte</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.UnsettableAttributes#getAttrByte()
   * @see #getUnsettableAttributes()
   * @generated
   */
  EAttribute getUnsettableAttributes_AttrByte();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model6.UnsettableAttributes#getAttrByteArray <em>Attr Byte Array</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Attr Byte Array</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.UnsettableAttributes#getAttrByteArray()
   * @see #getUnsettableAttributes()
   * @generated
   */
  EAttribute getUnsettableAttributes_AttrByteArray();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model6.UnsettableAttributes#getAttrByteObject <em>Attr Byte Object</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Attr Byte Object</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.UnsettableAttributes#getAttrByteObject()
   * @see #getUnsettableAttributes()
   * @generated
   */
  EAttribute getUnsettableAttributes_AttrByteObject();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model6.UnsettableAttributes#getAttrChar <em>Attr Char</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Attr Char</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.UnsettableAttributes#getAttrChar()
   * @see #getUnsettableAttributes()
   * @generated
   */
  EAttribute getUnsettableAttributes_AttrChar();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model6.UnsettableAttributes#getAttrCharacterObject <em>Attr Character Object</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Attr Character Object</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.UnsettableAttributes#getAttrCharacterObject()
   * @see #getUnsettableAttributes()
   * @generated
   */
  EAttribute getUnsettableAttributes_AttrCharacterObject();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model6.UnsettableAttributes#getAttrDate <em>Attr Date</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Attr Date</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.UnsettableAttributes#getAttrDate()
   * @see #getUnsettableAttributes()
   * @generated
   */
  EAttribute getUnsettableAttributes_AttrDate();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model6.UnsettableAttributes#getAttrDouble <em>Attr Double</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Attr Double</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.UnsettableAttributes#getAttrDouble()
   * @see #getUnsettableAttributes()
   * @generated
   */
  EAttribute getUnsettableAttributes_AttrDouble();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model6.UnsettableAttributes#getAttrDoubleObject <em>Attr Double Object</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Attr Double Object</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.UnsettableAttributes#getAttrDoubleObject()
   * @see #getUnsettableAttributes()
   * @generated
   */
  EAttribute getUnsettableAttributes_AttrDoubleObject();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model6.UnsettableAttributes#getAttrFloat <em>Attr Float</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Attr Float</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.UnsettableAttributes#getAttrFloat()
   * @see #getUnsettableAttributes()
   * @generated
   */
  EAttribute getUnsettableAttributes_AttrFloat();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model6.UnsettableAttributes#getAttrFloatObject <em>Attr Float Object</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Attr Float Object</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.UnsettableAttributes#getAttrFloatObject()
   * @see #getUnsettableAttributes()
   * @generated
   */
  EAttribute getUnsettableAttributes_AttrFloatObject();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model6.UnsettableAttributes#getAttrInt <em>Attr Int</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Attr Int</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.UnsettableAttributes#getAttrInt()
   * @see #getUnsettableAttributes()
   * @generated
   */
  EAttribute getUnsettableAttributes_AttrInt();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model6.UnsettableAttributes#getAttrIntegerObject <em>Attr Integer Object</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Attr Integer Object</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.UnsettableAttributes#getAttrIntegerObject()
   * @see #getUnsettableAttributes()
   * @generated
   */
  EAttribute getUnsettableAttributes_AttrIntegerObject();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model6.UnsettableAttributes#getAttrJavaClass <em>Attr Java Class</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Attr Java Class</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.UnsettableAttributes#getAttrJavaClass()
   * @see #getUnsettableAttributes()
   * @generated
   */
  EAttribute getUnsettableAttributes_AttrJavaClass();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model6.UnsettableAttributes#getAttrJavaObject <em>Attr Java Object</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Attr Java Object</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.UnsettableAttributes#getAttrJavaObject()
   * @see #getUnsettableAttributes()
   * @generated
   */
  EAttribute getUnsettableAttributes_AttrJavaObject();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model6.UnsettableAttributes#getAttrLong <em>Attr Long</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Attr Long</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.UnsettableAttributes#getAttrLong()
   * @see #getUnsettableAttributes()
   * @generated
   */
  EAttribute getUnsettableAttributes_AttrLong();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model6.UnsettableAttributes#getAttrLongObject <em>Attr Long Object</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Attr Long Object</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.UnsettableAttributes#getAttrLongObject()
   * @see #getUnsettableAttributes()
   * @generated
   */
  EAttribute getUnsettableAttributes_AttrLongObject();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model6.UnsettableAttributes#getAttrShort <em>Attr Short</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Attr Short</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.UnsettableAttributes#getAttrShort()
   * @see #getUnsettableAttributes()
   * @generated
   */
  EAttribute getUnsettableAttributes_AttrShort();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model6.UnsettableAttributes#getAttrShortObject <em>Attr Short Object</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Attr Short Object</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.UnsettableAttributes#getAttrShortObject()
   * @see #getUnsettableAttributes()
   * @generated
   */
  EAttribute getUnsettableAttributes_AttrShortObject();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.tests.model6.UnsettableAttributes#getAttrString <em>Attr String</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Attr String</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.UnsettableAttributes#getAttrString()
   * @see #getUnsettableAttributes()
   * @generated
   */
  EAttribute getUnsettableAttributes_AttrString();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model6.CanReferenceLegacy <em>Can Reference Legacy</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Can Reference Legacy</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.CanReferenceLegacy
   * @generated
   */
  EClass getCanReferenceLegacy();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.emf.cdo.tests.model6.CanReferenceLegacy#getSingleContainment <em>Single Containment</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Single Containment</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.CanReferenceLegacy#getSingleContainment()
   * @see #getCanReferenceLegacy()
   * @generated
   */
  EReference getCanReferenceLegacy_SingleContainment();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.tests.model6.CanReferenceLegacy#getMultipleContainment <em>Multiple Containment</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Multiple Containment</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.CanReferenceLegacy#getMultipleContainment()
   * @see #getCanReferenceLegacy()
   * @generated
   */
  EReference getCanReferenceLegacy_MultipleContainment();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.tests.model6.CanReferenceLegacy#getSingleReference <em>Single Reference</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Single Reference</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.CanReferenceLegacy#getSingleReference()
   * @see #getCanReferenceLegacy()
   * @generated
   */
  EReference getCanReferenceLegacy_SingleReference();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.tests.model6.CanReferenceLegacy#getMultipleReference <em>Multiple Reference</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Multiple Reference</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.CanReferenceLegacy#getMultipleReference()
   * @see #getCanReferenceLegacy()
   * @generated
   */
  EReference getCanReferenceLegacy_MultipleReference();

  /**
   * Returns the meta object for enum '{@link org.eclipse.emf.cdo.tests.model6.MyEnum <em>My Enum</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>My Enum</em>'.
   * @see org.eclipse.emf.cdo.tests.model6.MyEnum
   * @generated
   */
  EEnum getMyEnum();

  /**
   * Returns the meta object for data type '{@link java.lang.String <em>My String</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for data type '<em>My String</em>'.
   * @see java.lang.String
   * @model instanceClass="java.lang.String"
   * @generated
   */
  EDataType getMyString();

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  Model6Factory getModel6Factory();

  /**
   * <!-- begin-user-doc -->
   * Defines literals for the meta objects that represent
   * <ul>
   *   <li>each class,</li>
   *   <li>each feature of each class,</li>
   *   <li>each enum,</li>
   *   <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
   * @generated
   */
  interface Literals
  {
    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model6.impl.RootImpl <em>Root</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.model6.impl.RootImpl
     * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getRoot()
     * @generated
     */
    EClass ROOT = eINSTANCE.getRoot();

    /**
     * The meta object literal for the '<em><b>List A</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ROOT__LIST_A = eINSTANCE.getRoot_ListA();

    /**
     * The meta object literal for the '<em><b>List B</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ROOT__LIST_B = eINSTANCE.getRoot_ListB();

    /**
     * The meta object literal for the '<em><b>List C</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ROOT__LIST_C = eINSTANCE.getRoot_ListC();

    /**
     * The meta object literal for the '<em><b>List D</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ROOT__LIST_D = eINSTANCE.getRoot_ListD();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model6.impl.BaseObjectImpl <em>Base Object</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.model6.impl.BaseObjectImpl
     * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getBaseObject()
     * @generated
     */
    EClass BASE_OBJECT = eINSTANCE.getBaseObject();

    /**
     * The meta object literal for the '<em><b>Attribute Optional</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute BASE_OBJECT__ATTRIBUTE_OPTIONAL = eINSTANCE.getBaseObject_AttributeOptional();

    /**
     * The meta object literal for the '<em><b>Attribute Required</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute BASE_OBJECT__ATTRIBUTE_REQUIRED = eINSTANCE.getBaseObject_AttributeRequired();

    /**
     * The meta object literal for the '<em><b>Attribute List</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute BASE_OBJECT__ATTRIBUTE_LIST = eINSTANCE.getBaseObject_AttributeList();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model6.impl.ReferenceObjectImpl <em>Reference Object</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.model6.impl.ReferenceObjectImpl
     * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getReferenceObject()
     * @generated
     */
    EClass REFERENCE_OBJECT = eINSTANCE.getReferenceObject();

    /**
     * The meta object literal for the '<em><b>Reference Optional</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference REFERENCE_OBJECT__REFERENCE_OPTIONAL = eINSTANCE.getReferenceObject_ReferenceOptional();

    /**
     * The meta object literal for the '<em><b>Reference List</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference REFERENCE_OBJECT__REFERENCE_LIST = eINSTANCE.getReferenceObject_ReferenceList();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model6.impl.ContainmentObjectImpl <em>Containment Object</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.model6.impl.ContainmentObjectImpl
     * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getContainmentObject()
     * @generated
     */
    EClass CONTAINMENT_OBJECT = eINSTANCE.getContainmentObject();

    /**
     * The meta object literal for the '<em><b>Containment Optional</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CONTAINMENT_OBJECT__CONTAINMENT_OPTIONAL = eINSTANCE.getContainmentObject_ContainmentOptional();

    /**
     * The meta object literal for the '<em><b>Containment List</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CONTAINMENT_OBJECT__CONTAINMENT_LIST = eINSTANCE.getContainmentObject_ContainmentList();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model6.impl.UnorderedListImpl <em>Unordered List</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.model6.impl.UnorderedListImpl
     * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getUnorderedList()
     * @generated
     */
    EClass UNORDERED_LIST = eINSTANCE.getUnorderedList();

    /**
     * The meta object literal for the '<em><b>Contained</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference UNORDERED_LIST__CONTAINED = eINSTANCE.getUnorderedList_Contained();

    /**
     * The meta object literal for the '<em><b>Referenced</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference UNORDERED_LIST__REFERENCED = eINSTANCE.getUnorderedList_Referenced();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model6.impl.PropertiesMapImpl <em>Properties Map</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.model6.impl.PropertiesMapImpl
     * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getPropertiesMap()
     * @generated
     */
    EClass PROPERTIES_MAP = eINSTANCE.getPropertiesMap();

    /**
     * The meta object literal for the '<em><b>Label</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PROPERTIES_MAP__LABEL = eINSTANCE.getPropertiesMap_Label();

    /**
     * The meta object literal for the '<em><b>Persistent Map</b></em>' map feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference PROPERTIES_MAP__PERSISTENT_MAP = eINSTANCE.getPropertiesMap_PersistentMap();

    /**
     * The meta object literal for the '<em><b>Transient Map</b></em>' map feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference PROPERTIES_MAP__TRANSIENT_MAP = eINSTANCE.getPropertiesMap_TransientMap();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model6.impl.PropertiesMapEntryImpl <em>Properties Map Map.Entry</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.model6.impl.PropertiesMapEntryImpl
     * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getPropertiesMapEntry()
     * @generated
     */
    EClass PROPERTIES_MAP_ENTRY = eINSTANCE.getPropertiesMapEntry();

    /**
     * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PROPERTIES_MAP_ENTRY__KEY = eINSTANCE.getPropertiesMapEntry_Key();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference PROPERTIES_MAP_ENTRY__VALUE = eINSTANCE.getPropertiesMapEntry_Value();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model6.impl.PropertiesMapEntryValueImpl <em>Properties Map Map.Entry Value</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.model6.impl.PropertiesMapEntryValueImpl
     * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getPropertiesMapEntryValue()
     * @generated
     */
    EClass PROPERTIES_MAP_ENTRY_VALUE = eINSTANCE.getPropertiesMapEntryValue();

    /**
     * The meta object literal for the '<em><b>Label</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PROPERTIES_MAP_ENTRY_VALUE__LABEL = eINSTANCE.getPropertiesMapEntryValue_Label();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model6.impl.AImpl <em>A</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.model6.impl.AImpl
     * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getA()
     * @generated
     */
    EClass A = eINSTANCE.getA();

    /**
     * The meta object literal for the '<em><b>Owned Ds</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference A__OWNED_DS = eINSTANCE.getA_OwnedDs();

    /**
     * The meta object literal for the '<em><b>Owned Bs</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference A__OWNED_BS = eINSTANCE.getA_OwnedBs();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model6.impl.BImpl <em>B</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.model6.impl.BImpl
     * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getB()
     * @generated
     */
    EClass B = eINSTANCE.getB();

    /**
     * The meta object literal for the '<em><b>Owned C</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference B__OWNED_C = eINSTANCE.getB_OwnedC();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model6.impl.CImpl <em>C</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.model6.impl.CImpl
     * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getC()
     * @generated
     */
    EClass C = eINSTANCE.getC();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model6.impl.DImpl <em>D</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.model6.impl.DImpl
     * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getD()
     * @generated
     */
    EClass D = eINSTANCE.getD();

    /**
     * The meta object literal for the '<em><b>Data</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference D__DATA = eINSTANCE.getD_Data();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model6.impl.EImpl <em>E</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.model6.impl.EImpl
     * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getE()
     * @generated
     */
    EClass E = eINSTANCE.getE();

    /**
     * The meta object literal for the '<em><b>Owned As</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference E__OWNED_AS = eINSTANCE.getE_OwnedAs();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model6.impl.FImpl <em>F</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.model6.impl.FImpl
     * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getF()
     * @generated
     */
    EClass F = eINSTANCE.getF();

    /**
     * The meta object literal for the '<em><b>Owned Es</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference F__OWNED_ES = eINSTANCE.getF_OwnedEs();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model6.impl.GImpl <em>G</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.model6.impl.GImpl
     * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getG()
     * @generated
     */
    EClass G = eINSTANCE.getG();

    /**
     * The meta object literal for the '<em><b>Dummy</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute G__DUMMY = eINSTANCE.getG_Dummy();

    /**
     * The meta object literal for the '<em><b>Reference</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference G__REFERENCE = eINSTANCE.getG_Reference();

    /**
     * The meta object literal for the '<em><b>List</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference G__LIST = eINSTANCE.getG_List();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model6.impl.MyEnumListImpl <em>My Enum List</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.model6.impl.MyEnumListImpl
     * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getMyEnumList()
     * @generated
     */
    EClass MY_ENUM_LIST = eINSTANCE.getMyEnumList();

    /**
     * The meta object literal for the '<em><b>My Enum</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MY_ENUM_LIST__MY_ENUM = eINSTANCE.getMyEnumList_MyEnum();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model6.impl.MyEnumListUnsettableImpl <em>My Enum List Unsettable</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.model6.impl.MyEnumListUnsettableImpl
     * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getMyEnumListUnsettable()
     * @generated
     */
    EClass MY_ENUM_LIST_UNSETTABLE = eINSTANCE.getMyEnumListUnsettable();

    /**
     * The meta object literal for the '<em><b>My Enum</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MY_ENUM_LIST_UNSETTABLE__MY_ENUM = eINSTANCE.getMyEnumListUnsettable_MyEnum();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model6.impl.HolderImpl <em>Holder</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.model6.impl.HolderImpl
     * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getHolder()
     * @generated
     */
    EClass HOLDER = eINSTANCE.getHolder();

    /**
     * The meta object literal for the '<em><b>Held</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference HOLDER__HELD = eINSTANCE.getHolder_Held();

    /**
     * The meta object literal for the '<em><b>Owned</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference HOLDER__OWNED = eINSTANCE.getHolder_Owned();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model6.impl.ThingImpl <em>Thing</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.model6.impl.ThingImpl
     * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getThing()
     * @generated
     */
    EClass THING = eINSTANCE.getThing();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model6.impl.HoldableImpl <em>Holdable</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.model6.impl.HoldableImpl
     * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getHoldable()
     * @generated
     */
    EClass HOLDABLE = eINSTANCE.getHoldable();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute HOLDABLE__NAME = eINSTANCE.getHoldable_Name();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model6.impl.HasNillableAttributeImpl <em>Has Nillable Attribute</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.model6.impl.HasNillableAttributeImpl
     * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getHasNillableAttribute()
     * @generated
     */
    EClass HAS_NILLABLE_ATTRIBUTE = eINSTANCE.getHasNillableAttribute();

    /**
     * The meta object literal for the '<em><b>Nillable</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute HAS_NILLABLE_ATTRIBUTE__NILLABLE = eINSTANCE.getHasNillableAttribute_Nillable();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model6.impl.EmptyStringDefaultImpl <em>Empty String Default</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.model6.impl.EmptyStringDefaultImpl
     * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getEmptyStringDefault()
     * @generated
     */
    EClass EMPTY_STRING_DEFAULT = eINSTANCE.getEmptyStringDefault();

    /**
     * The meta object literal for the '<em><b>Attribute</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute EMPTY_STRING_DEFAULT__ATTRIBUTE = eINSTANCE.getEmptyStringDefault_Attribute();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model6.impl.EmptyStringDefaultUnsettableImpl <em>Empty String Default Unsettable</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.model6.impl.EmptyStringDefaultUnsettableImpl
     * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getEmptyStringDefaultUnsettable()
     * @generated
     */
    EClass EMPTY_STRING_DEFAULT_UNSETTABLE = eINSTANCE.getEmptyStringDefaultUnsettable();

    /**
     * The meta object literal for the '<em><b>Attribute</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute EMPTY_STRING_DEFAULT_UNSETTABLE__ATTRIBUTE = eINSTANCE.getEmptyStringDefaultUnsettable_Attribute();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model6.impl.UnsettableAttributesImpl <em>Unsettable Attributes</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.model6.impl.UnsettableAttributesImpl
     * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getUnsettableAttributes()
     * @generated
     */
    EClass UNSETTABLE_ATTRIBUTES = eINSTANCE.getUnsettableAttributes();

    /**
     * The meta object literal for the '<em><b>Attr Big Decimal</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute UNSETTABLE_ATTRIBUTES__ATTR_BIG_DECIMAL = eINSTANCE.getUnsettableAttributes_AttrBigDecimal();

    /**
     * The meta object literal for the '<em><b>Attr Big Integer</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute UNSETTABLE_ATTRIBUTES__ATTR_BIG_INTEGER = eINSTANCE.getUnsettableAttributes_AttrBigInteger();

    /**
     * The meta object literal for the '<em><b>Attr Boolean</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute UNSETTABLE_ATTRIBUTES__ATTR_BOOLEAN = eINSTANCE.getUnsettableAttributes_AttrBoolean();

    /**
     * The meta object literal for the '<em><b>Attr Boolean Object</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute UNSETTABLE_ATTRIBUTES__ATTR_BOOLEAN_OBJECT = eINSTANCE.getUnsettableAttributes_AttrBooleanObject();

    /**
     * The meta object literal for the '<em><b>Attr Byte</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute UNSETTABLE_ATTRIBUTES__ATTR_BYTE = eINSTANCE.getUnsettableAttributes_AttrByte();

    /**
     * The meta object literal for the '<em><b>Attr Byte Array</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute UNSETTABLE_ATTRIBUTES__ATTR_BYTE_ARRAY = eINSTANCE.getUnsettableAttributes_AttrByteArray();

    /**
     * The meta object literal for the '<em><b>Attr Byte Object</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute UNSETTABLE_ATTRIBUTES__ATTR_BYTE_OBJECT = eINSTANCE.getUnsettableAttributes_AttrByteObject();

    /**
     * The meta object literal for the '<em><b>Attr Char</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute UNSETTABLE_ATTRIBUTES__ATTR_CHAR = eINSTANCE.getUnsettableAttributes_AttrChar();

    /**
     * The meta object literal for the '<em><b>Attr Character Object</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute UNSETTABLE_ATTRIBUTES__ATTR_CHARACTER_OBJECT = eINSTANCE.getUnsettableAttributes_AttrCharacterObject();

    /**
     * The meta object literal for the '<em><b>Attr Date</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute UNSETTABLE_ATTRIBUTES__ATTR_DATE = eINSTANCE.getUnsettableAttributes_AttrDate();

    /**
     * The meta object literal for the '<em><b>Attr Double</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute UNSETTABLE_ATTRIBUTES__ATTR_DOUBLE = eINSTANCE.getUnsettableAttributes_AttrDouble();

    /**
     * The meta object literal for the '<em><b>Attr Double Object</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute UNSETTABLE_ATTRIBUTES__ATTR_DOUBLE_OBJECT = eINSTANCE.getUnsettableAttributes_AttrDoubleObject();

    /**
     * The meta object literal for the '<em><b>Attr Float</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute UNSETTABLE_ATTRIBUTES__ATTR_FLOAT = eINSTANCE.getUnsettableAttributes_AttrFloat();

    /**
     * The meta object literal for the '<em><b>Attr Float Object</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute UNSETTABLE_ATTRIBUTES__ATTR_FLOAT_OBJECT = eINSTANCE.getUnsettableAttributes_AttrFloatObject();

    /**
     * The meta object literal for the '<em><b>Attr Int</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute UNSETTABLE_ATTRIBUTES__ATTR_INT = eINSTANCE.getUnsettableAttributes_AttrInt();

    /**
     * The meta object literal for the '<em><b>Attr Integer Object</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute UNSETTABLE_ATTRIBUTES__ATTR_INTEGER_OBJECT = eINSTANCE.getUnsettableAttributes_AttrIntegerObject();

    /**
     * The meta object literal for the '<em><b>Attr Java Class</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute UNSETTABLE_ATTRIBUTES__ATTR_JAVA_CLASS = eINSTANCE.getUnsettableAttributes_AttrJavaClass();

    /**
     * The meta object literal for the '<em><b>Attr Java Object</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute UNSETTABLE_ATTRIBUTES__ATTR_JAVA_OBJECT = eINSTANCE.getUnsettableAttributes_AttrJavaObject();

    /**
     * The meta object literal for the '<em><b>Attr Long</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute UNSETTABLE_ATTRIBUTES__ATTR_LONG = eINSTANCE.getUnsettableAttributes_AttrLong();

    /**
     * The meta object literal for the '<em><b>Attr Long Object</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute UNSETTABLE_ATTRIBUTES__ATTR_LONG_OBJECT = eINSTANCE.getUnsettableAttributes_AttrLongObject();

    /**
     * The meta object literal for the '<em><b>Attr Short</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute UNSETTABLE_ATTRIBUTES__ATTR_SHORT = eINSTANCE.getUnsettableAttributes_AttrShort();

    /**
     * The meta object literal for the '<em><b>Attr Short Object</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute UNSETTABLE_ATTRIBUTES__ATTR_SHORT_OBJECT = eINSTANCE.getUnsettableAttributes_AttrShortObject();

    /**
     * The meta object literal for the '<em><b>Attr String</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute UNSETTABLE_ATTRIBUTES__ATTR_STRING = eINSTANCE.getUnsettableAttributes_AttrString();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model6.impl.CanReferenceLegacyImpl <em>Can Reference Legacy</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.model6.impl.CanReferenceLegacyImpl
     * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getCanReferenceLegacy()
     * @generated
     */
    EClass CAN_REFERENCE_LEGACY = eINSTANCE.getCanReferenceLegacy();

    /**
     * The meta object literal for the '<em><b>Single Containment</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CAN_REFERENCE_LEGACY__SINGLE_CONTAINMENT = eINSTANCE.getCanReferenceLegacy_SingleContainment();

    /**
     * The meta object literal for the '<em><b>Multiple Containment</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CAN_REFERENCE_LEGACY__MULTIPLE_CONTAINMENT = eINSTANCE.getCanReferenceLegacy_MultipleContainment();

    /**
     * The meta object literal for the '<em><b>Single Reference</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CAN_REFERENCE_LEGACY__SINGLE_REFERENCE = eINSTANCE.getCanReferenceLegacy_SingleReference();

    /**
     * The meta object literal for the '<em><b>Multiple Reference</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CAN_REFERENCE_LEGACY__MULTIPLE_REFERENCE = eINSTANCE.getCanReferenceLegacy_MultipleReference();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model6.MyEnum <em>My Enum</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.model6.MyEnum
     * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getMyEnum()
     * @generated
     */
    EEnum MY_ENUM = eINSTANCE.getMyEnum();

    /**
     * The meta object literal for the '<em>My String</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see java.lang.String
     * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getMyString()
     * @generated
     */
    EDataType MY_STRING = eINSTANCE.getMyString();

  }

} // Model6Package
