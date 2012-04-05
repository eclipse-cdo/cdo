/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
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
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model6.impl.PropertiesMapEntryImpl <em>Properties Map Entry</em>}' class.
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
   * The number of structural features of the '<em>Properties Map Entry</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROPERTIES_MAP_ENTRY_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.tests.model6.impl.PropertiesMapEntryValueImpl <em>Properties Map Entry Value</em>}' class.
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
   * The number of structural features of the '<em>Properties Map Entry Value</em>' class.
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
   * Returns the meta object for class '{@link java.util.Map.Entry <em>Properties Map Entry</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Properties Map Entry</em>'.
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
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.tests.model6.PropertiesMapEntryValue <em>Properties Map Entry Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Properties Map Entry Value</em>'.
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
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  Model6Factory getModel6Factory();

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
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model6.impl.RootImpl <em>Root</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.model6.impl.RootImpl
     * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getRoot()
     * @generated
     */
    EClass ROOT = eINSTANCE.getRoot();

    /**
     * The meta object literal for the '<em><b>List A</b></em>' containment reference list feature.
     * <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * @generated
     */
    EReference ROOT__LIST_A = eINSTANCE.getRoot_ListA();

    /**
     * The meta object literal for the '<em><b>List B</b></em>' containment reference list feature.
     * <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * @generated
     */
    EReference ROOT__LIST_B = eINSTANCE.getRoot_ListB();

    /**
     * The meta object literal for the '<em><b>List C</b></em>' containment reference list feature.
     * <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * @generated
     */
    EReference ROOT__LIST_C = eINSTANCE.getRoot_ListC();

    /**
     * The meta object literal for the '<em><b>List D</b></em>' containment reference list feature.
     * <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * @generated
     */
    EReference ROOT__LIST_D = eINSTANCE.getRoot_ListD();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model6.impl.BaseObjectImpl <em>Base Object</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
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
     * <!-- begin-user-doc --> <!-- end-user-doc -->
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
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.model6.impl.ContainmentObjectImpl
     * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getContainmentObject()
     * @generated
     */
    EClass CONTAINMENT_OBJECT = eINSTANCE.getContainmentObject();

    /**
     * The meta object literal for the '<em><b>Containment Optional</b></em>' containment reference feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference CONTAINMENT_OBJECT__CONTAINMENT_OPTIONAL = eINSTANCE.getContainmentObject_ContainmentOptional();

    /**
     * The meta object literal for the '<em><b>Containment List</b></em>' containment reference list feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference CONTAINMENT_OBJECT__CONTAINMENT_LIST = eINSTANCE.getContainmentObject_ContainmentList();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model6.impl.UnorderedListImpl <em>Unordered List</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.tests.model6.impl.UnorderedListImpl
     * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getUnorderedList()
     * @generated
     */
    EClass UNORDERED_LIST = eINSTANCE.getUnorderedList();

    /**
     * The meta object literal for the '<em><b>Contained</b></em>' containment reference list feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference UNORDERED_LIST__CONTAINED = eINSTANCE.getUnorderedList_Contained();

    /**
     * The meta object literal for the '<em><b>Referenced</b></em>' reference list feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
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
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model6.impl.PropertiesMapEntryImpl <em>Properties Map Entry</em>}' class.
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
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model6.impl.PropertiesMapEntryValueImpl <em>Properties Map Entry Value</em>}' class.
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
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model6.impl.AImpl <em>A</em>}' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.tests.model6.impl.AImpl
     * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getA()
     * @generated
     */
    EClass A = eINSTANCE.getA();

    /**
     * The meta object literal for the '<em><b>Owned Ds</b></em>' containment reference list feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference A__OWNED_DS = eINSTANCE.getA_OwnedDs();

    /**
     * The meta object literal for the '<em><b>Owned Bs</b></em>' containment reference list feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference A__OWNED_BS = eINSTANCE.getA_OwnedBs();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model6.impl.BImpl <em>B</em>}' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
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
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model6.impl.CImpl <em>C</em>}' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.tests.model6.impl.CImpl
     * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getC()
     * @generated
     */
    EClass C = eINSTANCE.getC();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model6.impl.DImpl <em>D</em>}' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
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
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model6.impl.EImpl <em>E</em>}' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.tests.model6.impl.EImpl
     * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getE()
     * @generated
     */
    EClass E = eINSTANCE.getE();

    /**
     * The meta object literal for the '<em><b>Owned As</b></em>' containment reference list feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference E__OWNED_AS = eINSTANCE.getE_OwnedAs();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.tests.model6.impl.FImpl <em>F</em>}' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.tests.model6.impl.FImpl
     * @see org.eclipse.emf.cdo.tests.model6.impl.Model6PackageImpl#getF()
     * @generated
     */
    EClass F = eINSTANCE.getF();

    /**
     * The meta object literal for the '<em><b>Owned Es</b></em>' containment reference list feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
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

  }

} // Model6Package
