/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 * 
 */
package org.eclipse.emf.cdo.dawn.examples.acore;

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
 * 
 * @see org.eclipse.emf.cdo.dawn.examples.acore.AcoreFactory
 * @model kind="package"
 * @generated
 */
public interface AcorePackage extends EPackage
{
  /**
   * The package name. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNAME = "acore";

  /**
   * The package namespace URI. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNS_URI = "http://www.eclipse.org/emf/cdo/dawn/examples/2010/ACore";

  /**
   * The package namespace name. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNS_PREFIX = "acore";

  /**
   * The singleton instance of the package. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  AcorePackage eINSTANCE = org.eclipse.emf.cdo.dawn.examples.acore.impl.AcorePackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.dawn.examples.acore.impl.ABasicClassImpl
   * <em>ABasic Class</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.dawn.examples.acore.impl.ABasicClassImpl
   * @see org.eclipse.emf.cdo.dawn.examples.acore.impl.AcorePackageImpl#getABasicClass()
   * @generated
   */
  int ABASIC_CLASS = 5;

  /**
   * The feature id for the '<em><b>Operations</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ABASIC_CLASS__OPERATIONS = 0;

  /**
   * The feature id for the '<em><b>Attributes</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ABASIC_CLASS__ATTRIBUTES = 1;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ABASIC_CLASS__NAME = 2;

  /**
   * The number of structural features of the '<em>ABasic Class</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int ABASIC_CLASS_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.dawn.examples.acore.impl.AClassImpl <em>AClass</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.dawn.examples.acore.impl.AClassImpl
   * @see org.eclipse.emf.cdo.dawn.examples.acore.impl.AcorePackageImpl#getAClass()
   * @generated
   */
  int ACLASS = 0;

  /**
   * The feature id for the '<em><b>Operations</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ACLASS__OPERATIONS = ABASIC_CLASS__OPERATIONS;

  /**
   * The feature id for the '<em><b>Attributes</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ACLASS__ATTRIBUTES = ABASIC_CLASS__ATTRIBUTES;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ACLASS__NAME = ABASIC_CLASS__NAME;

  /**
   * The feature id for the '<em><b>Sub Classes</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ACLASS__SUB_CLASSES = ABASIC_CLASS_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Implemented Interfaces</b></em>' reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ACLASS__IMPLEMENTED_INTERFACES = ABASIC_CLASS_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Associations</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ACLASS__ASSOCIATIONS = ABASIC_CLASS_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Compositions</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ACLASS__COMPOSITIONS = ABASIC_CLASS_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Aggregations</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ACLASS__AGGREGATIONS = ABASIC_CLASS_FEATURE_COUNT + 4;

  /**
   * The number of structural features of the '<em>AClass</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ACLASS_FEATURE_COUNT = ABASIC_CLASS_FEATURE_COUNT + 5;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.dawn.examples.acore.impl.AInterfaceImpl <em>AInterface</em>}
   * ' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.dawn.examples.acore.impl.AInterfaceImpl
   * @see org.eclipse.emf.cdo.dawn.examples.acore.impl.AcorePackageImpl#getAInterface()
   * @generated
   */
  int AINTERFACE = 1;

  /**
   * The feature id for the '<em><b>Operations</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int AINTERFACE__OPERATIONS = ABASIC_CLASS__OPERATIONS;

  /**
   * The feature id for the '<em><b>Attributes</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int AINTERFACE__ATTRIBUTES = ABASIC_CLASS__ATTRIBUTES;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int AINTERFACE__NAME = ABASIC_CLASS__NAME;

  /**
   * The number of structural features of the '<em>AInterface</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int AINTERFACE_FEATURE_COUNT = ABASIC_CLASS_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.dawn.examples.acore.impl.ACoreRootImpl <em>ACore Root</em>}'
   * class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.dawn.examples.acore.impl.ACoreRootImpl
   * @see org.eclipse.emf.cdo.dawn.examples.acore.impl.AcorePackageImpl#getACoreRoot()
   * @generated
   */
  int ACORE_ROOT = 2;

  /**
   * The feature id for the '<em><b>Title</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ACORE_ROOT__TITLE = 0;

  /**
   * The feature id for the '<em><b>Classes</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ACORE_ROOT__CLASSES = 1;

  /**
   * The feature id for the '<em><b>Interfaces</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ACORE_ROOT__INTERFACES = 2;

  /**
   * The number of structural features of the '<em>ACore Root</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ACORE_ROOT_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.dawn.examples.acore.impl.AClassChildImpl
   * <em>AClass Child</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.dawn.examples.acore.impl.AClassChildImpl
   * @see org.eclipse.emf.cdo.dawn.examples.acore.impl.AcorePackageImpl#getAClassChild()
   * @generated
   */
  int ACLASS_CHILD = 7;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ACLASS_CHILD__NAME = 0;

  /**
   * The feature id for the '<em><b>Accessright</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ACLASS_CHILD__ACCESSRIGHT = 1;

  /**
   * The feature id for the '<em><b>Data Type</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int ACLASS_CHILD__DATA_TYPE = 2;

  /**
   * The number of structural features of the '<em>AClass Child</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   */
  int ACLASS_CHILD_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.dawn.examples.acore.impl.AAttributeImpl <em>AAttribute</em>}
   * ' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.dawn.examples.acore.impl.AAttributeImpl
   * @see org.eclipse.emf.cdo.dawn.examples.acore.impl.AcorePackageImpl#getAAttribute()
   * @generated
   */
  int AATTRIBUTE = 3;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int AATTRIBUTE__NAME = ACLASS_CHILD__NAME;

  /**
   * The feature id for the '<em><b>Accessright</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int AATTRIBUTE__ACCESSRIGHT = ACLASS_CHILD__ACCESSRIGHT;

  /**
   * The feature id for the '<em><b>Data Type</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int AATTRIBUTE__DATA_TYPE = ACLASS_CHILD__DATA_TYPE;

  /**
   * The number of structural features of the '<em>AAttribute</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int AATTRIBUTE_FEATURE_COUNT = ACLASS_CHILD_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.dawn.examples.acore.impl.AOperationImpl <em>AOperation</em>}
   * ' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.dawn.examples.acore.impl.AOperationImpl
   * @see org.eclipse.emf.cdo.dawn.examples.acore.impl.AcorePackageImpl#getAOperation()
   * @generated
   */
  int AOPERATION = 4;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int AOPERATION__NAME = ACLASS_CHILD__NAME;

  /**
   * The feature id for the '<em><b>Accessright</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int AOPERATION__ACCESSRIGHT = ACLASS_CHILD__ACCESSRIGHT;

  /**
   * The feature id for the '<em><b>Data Type</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int AOPERATION__DATA_TYPE = ACLASS_CHILD__DATA_TYPE;

  /**
   * The feature id for the '<em><b>Parameters</b></em>' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int AOPERATION__PARAMETERS = ACLASS_CHILD_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>AOperation</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int AOPERATION_FEATURE_COUNT = ACLASS_CHILD_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.dawn.examples.acore.impl.AParameterImpl <em>AParameter</em>}
   * ' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.dawn.examples.acore.impl.AParameterImpl
   * @see org.eclipse.emf.cdo.dawn.examples.acore.impl.AcorePackageImpl#getAParameter()
   * @generated
   */
  int APARAMETER = 6;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int APARAMETER__NAME = 0;

  /**
   * The feature id for the '<em><b>Type</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int APARAMETER__TYPE = 1;

  /**
   * The number of structural features of the '<em>AParameter</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   */
  int APARAMETER_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.dawn.examples.acore.AccessType <em>Access Type</em>}' enum.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.dawn.examples.acore.AccessType
   * @see org.eclipse.emf.cdo.dawn.examples.acore.impl.AcorePackageImpl#getAccessType()
   * @generated
   */
  int ACCESS_TYPE = 8;

  /**
   * The meta object id for the '<em>Access Type Object</em>' data type. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.dawn.examples.acore.AccessType
   * @see org.eclipse.emf.cdo.dawn.examples.acore.impl.AcorePackageImpl#getAccessTypeObject()
   * @generated
   */
  int ACCESS_TYPE_OBJECT = 9;

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.dawn.examples.acore.AClass <em>AClass</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>AClass</em>'.
   * @see org.eclipse.emf.cdo.dawn.examples.acore.AClass
   * @generated
   */
  EClass getAClass();

  /**
   * Returns the meta object for the reference list '
   * {@link org.eclipse.emf.cdo.dawn.examples.acore.AClass#getSubClasses <em>Sub Classes</em>}'. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @return the meta object for the reference list '<em>Sub Classes</em>'.
   * @see org.eclipse.emf.cdo.dawn.examples.acore.AClass#getSubClasses()
   * @see #getAClass()
   * @generated
   */
  EReference getAClass_SubClasses();

  /**
   * Returns the meta object for the reference list '
   * {@link org.eclipse.emf.cdo.dawn.examples.acore.AClass#getImplementedInterfaces <em>Implemented Interfaces</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the reference list '<em>Implemented Interfaces</em>'.
   * @see org.eclipse.emf.cdo.dawn.examples.acore.AClass#getImplementedInterfaces()
   * @see #getAClass()
   * @generated
   */
  EReference getAClass_ImplementedInterfaces();

  /**
   * Returns the meta object for the reference list '
   * {@link org.eclipse.emf.cdo.dawn.examples.acore.AClass#getAssociations <em>Associations</em>}'. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @return the meta object for the reference list '<em>Associations</em>'.
   * @see org.eclipse.emf.cdo.dawn.examples.acore.AClass#getAssociations()
   * @see #getAClass()
   * @generated
   */
  EReference getAClass_Associations();

  /**
   * Returns the meta object for the reference list '
   * {@link org.eclipse.emf.cdo.dawn.examples.acore.AClass#getCompositions <em>Compositions</em>}'. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @return the meta object for the reference list '<em>Compositions</em>'.
   * @see org.eclipse.emf.cdo.dawn.examples.acore.AClass#getCompositions()
   * @see #getAClass()
   * @generated
   */
  EReference getAClass_Compositions();

  /**
   * Returns the meta object for the reference list '
   * {@link org.eclipse.emf.cdo.dawn.examples.acore.AClass#getAggregations <em>Aggregations</em>}'. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @return the meta object for the reference list '<em>Aggregations</em>'.
   * @see org.eclipse.emf.cdo.dawn.examples.acore.AClass#getAggregations()
   * @see #getAClass()
   * @generated
   */
  EReference getAClass_Aggregations();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.dawn.examples.acore.AInterface <em>AInterface</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>AInterface</em>'.
   * @see org.eclipse.emf.cdo.dawn.examples.acore.AInterface
   * @generated
   */
  EClass getAInterface();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.dawn.examples.acore.ACoreRoot <em>ACore Root</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>ACore Root</em>'.
   * @see org.eclipse.emf.cdo.dawn.examples.acore.ACoreRoot
   * @generated
   */
  EClass getACoreRoot();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.dawn.examples.acore.ACoreRoot#getTitle
   * <em>Title</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Title</em>'.
   * @see org.eclipse.emf.cdo.dawn.examples.acore.ACoreRoot#getTitle()
   * @see #getACoreRoot()
   * @generated
   */
  EAttribute getACoreRoot_Title();

  /**
   * Returns the meta object for the containment reference list '
   * {@link org.eclipse.emf.cdo.dawn.examples.acore.ACoreRoot#getClasses <em>Classes</em>}'. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @return the meta object for the containment reference list '<em>Classes</em>'.
   * @see org.eclipse.emf.cdo.dawn.examples.acore.ACoreRoot#getClasses()
   * @see #getACoreRoot()
   * @generated
   */
  EReference getACoreRoot_Classes();

  /**
   * Returns the meta object for the containment reference list '
   * {@link org.eclipse.emf.cdo.dawn.examples.acore.ACoreRoot#getInterfaces <em>Interfaces</em>}'. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @return the meta object for the containment reference list '<em>Interfaces</em>'.
   * @see org.eclipse.emf.cdo.dawn.examples.acore.ACoreRoot#getInterfaces()
   * @see #getACoreRoot()
   * @generated
   */
  EReference getACoreRoot_Interfaces();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.dawn.examples.acore.AAttribute <em>AAttribute</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>AAttribute</em>'.
   * @see org.eclipse.emf.cdo.dawn.examples.acore.AAttribute
   * @generated
   */
  EClass getAAttribute();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.dawn.examples.acore.AOperation <em>AOperation</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>AOperation</em>'.
   * @see org.eclipse.emf.cdo.dawn.examples.acore.AOperation
   * @generated
   */
  EClass getAOperation();

  /**
   * Returns the meta object for the containment reference list '
   * {@link org.eclipse.emf.cdo.dawn.examples.acore.AOperation#getParameters <em>Parameters</em>}'. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @return the meta object for the containment reference list '<em>Parameters</em>'.
   * @see org.eclipse.emf.cdo.dawn.examples.acore.AOperation#getParameters()
   * @see #getAOperation()
   * @generated
   */
  EReference getAOperation_Parameters();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.dawn.examples.acore.ABasicClass
   * <em>ABasic Class</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>ABasic Class</em>'.
   * @see org.eclipse.emf.cdo.dawn.examples.acore.ABasicClass
   * @generated
   */
  EClass getABasicClass();

  /**
   * Returns the meta object for the containment reference list '
   * {@link org.eclipse.emf.cdo.dawn.examples.acore.ABasicClass#getOperations <em>Operations</em>}'. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @return the meta object for the containment reference list '<em>Operations</em>'.
   * @see org.eclipse.emf.cdo.dawn.examples.acore.ABasicClass#getOperations()
   * @see #getABasicClass()
   * @generated
   */
  EReference getABasicClass_Operations();

  /**
   * Returns the meta object for the containment reference list '
   * {@link org.eclipse.emf.cdo.dawn.examples.acore.ABasicClass#getAttributes <em>Attributes</em>}'. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @return the meta object for the containment reference list '<em>Attributes</em>'.
   * @see org.eclipse.emf.cdo.dawn.examples.acore.ABasicClass#getAttributes()
   * @see #getABasicClass()
   * @generated
   */
  EReference getABasicClass_Attributes();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.dawn.examples.acore.ABasicClass#getName
   * <em>Name</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.dawn.examples.acore.ABasicClass#getName()
   * @see #getABasicClass()
   * @generated
   */
  EAttribute getABasicClass_Name();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.dawn.examples.acore.AParameter <em>AParameter</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>AParameter</em>'.
   * @see org.eclipse.emf.cdo.dawn.examples.acore.AParameter
   * @generated
   */
  EClass getAParameter();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.dawn.examples.acore.AParameter#getName
   * <em>Name</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.dawn.examples.acore.AParameter#getName()
   * @see #getAParameter()
   * @generated
   */
  EAttribute getAParameter_Name();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.dawn.examples.acore.AParameter#getType
   * <em>Type</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Type</em>'.
   * @see org.eclipse.emf.cdo.dawn.examples.acore.AParameter#getType()
   * @see #getAParameter()
   * @generated
   */
  EAttribute getAParameter_Type();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.dawn.examples.acore.AClassChild
   * <em>AClass Child</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>AClass Child</em>'.
   * @see org.eclipse.emf.cdo.dawn.examples.acore.AClassChild
   * @generated
   */
  EClass getAClassChild();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.dawn.examples.acore.AClassChild#getName
   * <em>Name</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.dawn.examples.acore.AClassChild#getName()
   * @see #getAClassChild()
   * @generated
   */
  EAttribute getAClassChild_Name();

  /**
   * Returns the meta object for the attribute '
   * {@link org.eclipse.emf.cdo.dawn.examples.acore.AClassChild#getAccessright <em>Accessright</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Accessright</em>'.
   * @see org.eclipse.emf.cdo.dawn.examples.acore.AClassChild#getAccessright()
   * @see #getAClassChild()
   * @generated
   */
  EAttribute getAClassChild_Accessright();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.dawn.examples.acore.AClassChild#getDataType
   * <em>Data Type</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Data Type</em>'.
   * @see org.eclipse.emf.cdo.dawn.examples.acore.AClassChild#getDataType()
   * @see #getAClassChild()
   * @generated
   */
  EAttribute getAClassChild_DataType();

  /**
   * Returns the meta object for enum '{@link org.eclipse.emf.cdo.dawn.examples.acore.AccessType <em>Access Type</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for enum '<em>Access Type</em>'.
   * @see org.eclipse.emf.cdo.dawn.examples.acore.AccessType
   * @generated
   */
  EEnum getAccessType();

  /**
   * Returns the meta object for data type '{@link org.eclipse.emf.cdo.dawn.examples.acore.AccessType
   * <em>Access Type Object</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for data type '<em>Access Type Object</em>'.
   * @see org.eclipse.emf.cdo.dawn.examples.acore.AccessType
   * @model instanceClass="org.eclipse.emf.cdo.dawn.examples.acore.AccessType"
   *        extendedMetaData="name='AccessType:Object' baseType='AccessType'"
   * @generated
   */
  EDataType getAccessTypeObject();

  /**
   * Returns the factory that creates the instances of the model. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the factory that creates the instances of the model.
   * @generated
   */
  AcoreFactory getAcoreFactory();

  /**
   * <!-- begin-user-doc --> Defines literals for the meta objects that represent
   * <ul>
   * <li>each class,</li>
   * <li>each feature of each class,</li>
   * <li>each enum,</li>
   * <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
   * 
   * @generated
   */
  interface Literals
  {
    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.dawn.examples.acore.impl.AClassImpl <em>AClass</em>}'
     * class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.dawn.examples.acore.impl.AClassImpl
     * @see org.eclipse.emf.cdo.dawn.examples.acore.impl.AcorePackageImpl#getAClass()
     * @generated
     */
    EClass ACLASS = eINSTANCE.getAClass();

    /**
     * The meta object literal for the '<em><b>Sub Classes</b></em>' reference list feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference ACLASS__SUB_CLASSES = eINSTANCE.getAClass_SubClasses();

    /**
     * The meta object literal for the '<em><b>Implemented Interfaces</b></em>' reference list feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference ACLASS__IMPLEMENTED_INTERFACES = eINSTANCE.getAClass_ImplementedInterfaces();

    /**
     * The meta object literal for the '<em><b>Associations</b></em>' reference list feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference ACLASS__ASSOCIATIONS = eINSTANCE.getAClass_Associations();

    /**
     * The meta object literal for the '<em><b>Compositions</b></em>' reference list feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference ACLASS__COMPOSITIONS = eINSTANCE.getAClass_Compositions();

    /**
     * The meta object literal for the '<em><b>Aggregations</b></em>' reference list feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference ACLASS__AGGREGATIONS = eINSTANCE.getAClass_Aggregations();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.dawn.examples.acore.impl.AInterfaceImpl
     * <em>AInterface</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.dawn.examples.acore.impl.AInterfaceImpl
     * @see org.eclipse.emf.cdo.dawn.examples.acore.impl.AcorePackageImpl#getAInterface()
     * @generated
     */
    EClass AINTERFACE = eINSTANCE.getAInterface();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.dawn.examples.acore.impl.ACoreRootImpl
     * <em>ACore Root</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.dawn.examples.acore.impl.ACoreRootImpl
     * @see org.eclipse.emf.cdo.dawn.examples.acore.impl.AcorePackageImpl#getACoreRoot()
     * @generated
     */
    EClass ACORE_ROOT = eINSTANCE.getACoreRoot();

    /**
     * The meta object literal for the '<em><b>Title</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute ACORE_ROOT__TITLE = eINSTANCE.getACoreRoot_Title();

    /**
     * The meta object literal for the '<em><b>Classes</b></em>' containment reference list feature. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference ACORE_ROOT__CLASSES = eINSTANCE.getACoreRoot_Classes();

    /**
     * The meta object literal for the '<em><b>Interfaces</b></em>' containment reference list feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference ACORE_ROOT__INTERFACES = eINSTANCE.getACoreRoot_Interfaces();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.dawn.examples.acore.impl.AAttributeImpl
     * <em>AAttribute</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.dawn.examples.acore.impl.AAttributeImpl
     * @see org.eclipse.emf.cdo.dawn.examples.acore.impl.AcorePackageImpl#getAAttribute()
     * @generated
     */
    EClass AATTRIBUTE = eINSTANCE.getAAttribute();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.dawn.examples.acore.impl.AOperationImpl
     * <em>AOperation</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.dawn.examples.acore.impl.AOperationImpl
     * @see org.eclipse.emf.cdo.dawn.examples.acore.impl.AcorePackageImpl#getAOperation()
     * @generated
     */
    EClass AOPERATION = eINSTANCE.getAOperation();

    /**
     * The meta object literal for the '<em><b>Parameters</b></em>' containment reference list feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference AOPERATION__PARAMETERS = eINSTANCE.getAOperation_Parameters();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.dawn.examples.acore.impl.ABasicClassImpl
     * <em>ABasic Class</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.dawn.examples.acore.impl.ABasicClassImpl
     * @see org.eclipse.emf.cdo.dawn.examples.acore.impl.AcorePackageImpl#getABasicClass()
     * @generated
     */
    EClass ABASIC_CLASS = eINSTANCE.getABasicClass();

    /**
     * The meta object literal for the '<em><b>Operations</b></em>' containment reference list feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference ABASIC_CLASS__OPERATIONS = eINSTANCE.getABasicClass_Operations();

    /**
     * The meta object literal for the '<em><b>Attributes</b></em>' containment reference list feature. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    EReference ABASIC_CLASS__ATTRIBUTES = eINSTANCE.getABasicClass_Attributes();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute ABASIC_CLASS__NAME = eINSTANCE.getABasicClass_Name();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.dawn.examples.acore.impl.AParameterImpl
     * <em>AParameter</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.dawn.examples.acore.impl.AParameterImpl
     * @see org.eclipse.emf.cdo.dawn.examples.acore.impl.AcorePackageImpl#getAParameter()
     * @generated
     */
    EClass APARAMETER = eINSTANCE.getAParameter();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute APARAMETER__NAME = eINSTANCE.getAParameter_Name();

    /**
     * The meta object literal for the '<em><b>Type</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute APARAMETER__TYPE = eINSTANCE.getAParameter_Type();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.dawn.examples.acore.impl.AClassChildImpl
     * <em>AClass Child</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.dawn.examples.acore.impl.AClassChildImpl
     * @see org.eclipse.emf.cdo.dawn.examples.acore.impl.AcorePackageImpl#getAClassChild()
     * @generated
     */
    EClass ACLASS_CHILD = eINSTANCE.getAClassChild();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute ACLASS_CHILD__NAME = eINSTANCE.getAClassChild_Name();

    /**
     * The meta object literal for the '<em><b>Accessright</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute ACLASS_CHILD__ACCESSRIGHT = eINSTANCE.getAClassChild_Accessright();

    /**
     * The meta object literal for the '<em><b>Data Type</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    EAttribute ACLASS_CHILD__DATA_TYPE = eINSTANCE.getAClassChild_DataType();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.dawn.examples.acore.AccessType <em>Access Type</em>}'
     * enum. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.dawn.examples.acore.AccessType
     * @see org.eclipse.emf.cdo.dawn.examples.acore.impl.AcorePackageImpl#getAccessType()
     * @generated
     */
    EEnum ACCESS_TYPE = eINSTANCE.getAccessType();

    /**
     * The meta object literal for the '<em>Access Type Object</em>' data type. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.dawn.examples.acore.AccessType
     * @see org.eclipse.emf.cdo.dawn.examples.acore.impl.AcorePackageImpl#getAccessTypeObject()
     * @generated
     */
    EDataType ACCESS_TYPE_OBJECT = eINSTANCE.getAccessTypeObject();

  }

} // AcorePackage
