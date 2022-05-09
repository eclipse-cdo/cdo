/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm;

import org.eclipse.emf.cdo.etypes.EtypesPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc --> The <b>Package</b> for the model. It contains
 * accessors for the meta objects to represent
 * <ul>
 * <li>each class,</li>
 * <li>each feature of each class,</li>
 * <li>each enum,</li>
 * <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.lm.LMFactory
 * @model kind="package"
 * @generated
 */
public interface LMPackage extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "lm";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://www.eclipse.org/emf/CDO/lm/1.0.0";

  /**
   * The package namespace name.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "lm";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   */
  LMPackage eINSTANCE = org.eclipse.emf.cdo.lm.impl.LMPackageImpl.init();

  public static final String ANNOTATION_SOURCE = "http://www.eclipse.org/CDO/LM";

  public static final String ANNOTATION_DETAIL_BASELINE_ID = "baselineID";

  public static final String ANNOTATION_DETAIL_BASELINE_TYPE = "baselineType";

  public static final String ANNOTATION_DETAIL_BASELINE_NAME = "baselineName";

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.lm.SystemElement
   * <em>System Element</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @see org.eclipse.emf.cdo.lm.SystemElement
   * @see org.eclipse.emf.cdo.lm.impl.LMPackageImpl#getSystemElement()
   * @generated
   */
  int SYSTEM_ELEMENT = 0;

  /**
   * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SYSTEM_ELEMENT__ANNOTATIONS = EtypesPackage.MODEL_ELEMENT__ANNOTATIONS;

  /**
   * The number of structural features of the '<em>System Element</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SYSTEM_ELEMENT_FEATURE_COUNT = EtypesPackage.MODEL_ELEMENT_FEATURE_COUNT + 0;

  /**
   * The operation id for the '<em>Get Annotation</em>' operation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int SYSTEM_ELEMENT___GET_ANNOTATION__STRING = EtypesPackage.MODEL_ELEMENT___GET_ANNOTATION__STRING;

  /**
   * The operation id for the '<em>Get System</em>' operation.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SYSTEM_ELEMENT___GET_SYSTEM = EtypesPackage.MODEL_ELEMENT_OPERATION_COUNT + 0;

  /**
   * The number of operations of the '<em>System Element</em>' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int SYSTEM_ELEMENT_OPERATION_COUNT = EtypesPackage.MODEL_ELEMENT_OPERATION_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.lm.ProcessElement <em>Process Element</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.lm.ProcessElement
   * @see org.eclipse.emf.cdo.lm.impl.LMPackageImpl#getProcessElement()
   * @generated
   */
  int PROCESS_ELEMENT = 1;

  /**
   * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROCESS_ELEMENT__ANNOTATIONS = SYSTEM_ELEMENT__ANNOTATIONS;

  /**
   * The number of structural features of the '<em>Process Element</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROCESS_ELEMENT_FEATURE_COUNT = SYSTEM_ELEMENT_FEATURE_COUNT + 0;

  /**
   * The operation id for the '<em>Get Annotation</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROCESS_ELEMENT___GET_ANNOTATION__STRING = SYSTEM_ELEMENT___GET_ANNOTATION__STRING;

  /**
   * The operation id for the '<em>Get System</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROCESS_ELEMENT___GET_SYSTEM = SYSTEM_ELEMENT___GET_SYSTEM;

  /**
   * The operation id for the '<em>Get Process</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROCESS_ELEMENT___GET_PROCESS = SYSTEM_ELEMENT_OPERATION_COUNT + 0;

  /**
   * The number of operations of the '<em>Process Element</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROCESS_ELEMENT_OPERATION_COUNT = SYSTEM_ELEMENT_OPERATION_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.lm.ModuleElement
   * <em>Module Element</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @see org.eclipse.emf.cdo.lm.ModuleElement
   * @see org.eclipse.emf.cdo.lm.impl.LMPackageImpl#getModuleElement()
   * @generated
   */
  int MODULE_ELEMENT = 2;

  /**
   * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODULE_ELEMENT__ANNOTATIONS = SYSTEM_ELEMENT__ANNOTATIONS;

  /**
   * The number of structural features of the '<em>Module Element</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODULE_ELEMENT_FEATURE_COUNT = SYSTEM_ELEMENT_FEATURE_COUNT + 0;

  /**
   * The operation id for the '<em>Get Annotation</em>' operation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int MODULE_ELEMENT___GET_ANNOTATION__STRING = SYSTEM_ELEMENT___GET_ANNOTATION__STRING;

  /**
   * The operation id for the '<em>Get System</em>' operation.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODULE_ELEMENT___GET_SYSTEM = SYSTEM_ELEMENT___GET_SYSTEM;

  /**
   * The operation id for the '<em>Get Module</em>' operation.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODULE_ELEMENT___GET_MODULE = SYSTEM_ELEMENT_OPERATION_COUNT + 0;

  /**
   * The number of operations of the '<em>Module Element</em>' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int MODULE_ELEMENT_OPERATION_COUNT = SYSTEM_ELEMENT_OPERATION_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.lm.StreamElement
   * <em>Stream Element</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @see org.eclipse.emf.cdo.lm.StreamElement
   * @see org.eclipse.emf.cdo.lm.impl.LMPackageImpl#getStreamElement()
   * @generated
   */
  int STREAM_ELEMENT = 3;

  /**
   * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STREAM_ELEMENT__ANNOTATIONS = MODULE_ELEMENT__ANNOTATIONS;

  /**
   * The number of structural features of the '<em>Stream Element</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STREAM_ELEMENT_FEATURE_COUNT = MODULE_ELEMENT_FEATURE_COUNT + 0;

  /**
   * The operation id for the '<em>Get Annotation</em>' operation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int STREAM_ELEMENT___GET_ANNOTATION__STRING = MODULE_ELEMENT___GET_ANNOTATION__STRING;

  /**
   * The operation id for the '<em>Get System</em>' operation.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STREAM_ELEMENT___GET_SYSTEM = MODULE_ELEMENT___GET_SYSTEM;

  /**
   * The operation id for the '<em>Get Module</em>' operation.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STREAM_ELEMENT___GET_MODULE = MODULE_ELEMENT___GET_MODULE;

  /**
   * The operation id for the '<em>Get Stream</em>' operation.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STREAM_ELEMENT___GET_STREAM = MODULE_ELEMENT_OPERATION_COUNT + 0;

  /**
   * The number of operations of the '<em>Stream Element</em>' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int STREAM_ELEMENT_OPERATION_COUNT = MODULE_ELEMENT_OPERATION_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.lm.impl.SystemImpl <em>System</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.lm.impl.SystemImpl
   * @see org.eclipse.emf.cdo.lm.impl.LMPackageImpl#getSystem()
   * @generated
   */
  int SYSTEM = 4;

  /**
   * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SYSTEM__ANNOTATIONS = EtypesPackage.MODEL_ELEMENT__ANNOTATIONS;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SYSTEM__NAME = EtypesPackage.MODEL_ELEMENT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Process</b></em>' containment reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int SYSTEM__PROCESS = EtypesPackage.MODEL_ELEMENT_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Modules</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SYSTEM__MODULES = EtypesPackage.MODEL_ELEMENT_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>System</em>' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int SYSTEM_FEATURE_COUNT = EtypesPackage.MODEL_ELEMENT_FEATURE_COUNT + 3;

  /**
   * The operation id for the '<em>Get Annotation</em>' operation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int SYSTEM___GET_ANNOTATION__STRING = EtypesPackage.MODEL_ELEMENT___GET_ANNOTATION__STRING;

  /**
   * The operation id for the '<em>Get Module</em>' operation.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SYSTEM___GET_MODULE__STRING = EtypesPackage.MODEL_ELEMENT_OPERATION_COUNT + 0;

  /**
   * The number of operations of the '<em>System</em>' class.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SYSTEM_OPERATION_COUNT = EtypesPackage.MODEL_ELEMENT_OPERATION_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.lm.impl.ProcessImpl <em>Process</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.lm.impl.ProcessImpl
   * @see org.eclipse.emf.cdo.lm.impl.LMPackageImpl#getProcess()
   * @generated
   */
  int PROCESS = 5;

  /**
   * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROCESS__ANNOTATIONS = SYSTEM_ELEMENT__ANNOTATIONS;

  /**
   * The feature id for the '<em><b>System</b></em>' container reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int PROCESS__SYSTEM = SYSTEM_ELEMENT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Module Types</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROCESS__MODULE_TYPES = SYSTEM_ELEMENT_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Drop Types</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROCESS__DROP_TYPES = SYSTEM_ELEMENT_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Module Definition Path</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROCESS__MODULE_DEFINITION_PATH = SYSTEM_ELEMENT_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Initial Module Version</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROCESS__INITIAL_MODULE_VERSION = SYSTEM_ELEMENT_FEATURE_COUNT + 4;

  /**
   * The number of structural features of the '<em>Process</em>' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int PROCESS_FEATURE_COUNT = SYSTEM_ELEMENT_FEATURE_COUNT + 5;

  /**
   * The operation id for the '<em>Get Annotation</em>' operation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int PROCESS___GET_ANNOTATION__STRING = SYSTEM_ELEMENT___GET_ANNOTATION__STRING;

  /**
   * The operation id for the '<em>Get System</em>' operation.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROCESS___GET_SYSTEM = SYSTEM_ELEMENT___GET_SYSTEM;

  /**
   * The number of operations of the '<em>Process</em>' class.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PROCESS_OPERATION_COUNT = SYSTEM_ELEMENT_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.lm.impl.DropTypeImpl <em>Drop Type</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.lm.impl.DropTypeImpl
   * @see org.eclipse.emf.cdo.lm.impl.LMPackageImpl#getDropType()
   * @generated
   */
  int DROP_TYPE = 7;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.lm.impl.ModuleImpl <em>Module</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.lm.impl.ModuleImpl
   * @see org.eclipse.emf.cdo.lm.impl.LMPackageImpl#getModule()
   * @generated
   */
  int MODULE = 8;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.lm.impl.DependencyImpl <em>Dependency</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.lm.impl.DependencyImpl
   * @see org.eclipse.emf.cdo.lm.impl.LMPackageImpl#getDependency()
   * @generated
   */
  int DEPENDENCY = 16;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.lm.impl.BaselineImpl <em>Baseline</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.lm.impl.BaselineImpl
   * @see org.eclipse.emf.cdo.lm.impl.LMPackageImpl#getBaseline()
   * @generated
   */
  int BASELINE = 9;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.lm.impl.FloatingBaselineImpl <em>Floating Baseline</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.lm.impl.FloatingBaselineImpl
   * @see org.eclipse.emf.cdo.lm.impl.LMPackageImpl#getFloatingBaseline()
   * @generated
   */
  int FLOATING_BASELINE = 10;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.lm.impl.FixedBaselineImpl <em>Fixed Baseline</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.lm.impl.FixedBaselineImpl
   * @see org.eclipse.emf.cdo.lm.impl.LMPackageImpl#getFixedBaseline()
   * @generated
   */
  int FIXED_BASELINE = 11;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.lm.impl.StreamImpl <em>Stream</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.lm.impl.StreamImpl
   * @see org.eclipse.emf.cdo.lm.impl.LMPackageImpl#getStream()
   * @generated
   */
  int STREAM = 12;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.lm.impl.ChangeImpl <em>Change</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.lm.impl.ChangeImpl
   * @see org.eclipse.emf.cdo.lm.impl.LMPackageImpl#getChange()
   * @generated
   */
  int CHANGE = 13;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.lm.impl.DeliveryImpl <em>Delivery</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.lm.impl.DeliveryImpl
   * @see org.eclipse.emf.cdo.lm.impl.LMPackageImpl#getDelivery()
   * @generated
   */
  int DELIVERY = 14;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.lm.impl.DropImpl <em>Drop</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.lm.impl.DropImpl
   * @see org.eclipse.emf.cdo.lm.impl.LMPackageImpl#getDrop()
   * @generated
   */
  int DROP = 15;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.lm.impl.ModuleTypeImpl <em>Module Type</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.lm.impl.ModuleTypeImpl
   * @see org.eclipse.emf.cdo.lm.impl.LMPackageImpl#getModuleType()
   * @generated
   */
  int MODULE_TYPE = 6;

  /**
   * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODULE_TYPE__ANNOTATIONS = PROCESS_ELEMENT__ANNOTATIONS;

  /**
   * The feature id for the '<em><b>Process</b></em>' container reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int MODULE_TYPE__PROCESS = PROCESS_ELEMENT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODULE_TYPE__NAME = PROCESS_ELEMENT_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Module Type</em>' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int MODULE_TYPE_FEATURE_COUNT = PROCESS_ELEMENT_FEATURE_COUNT + 2;

  /**
   * The operation id for the '<em>Get Annotation</em>' operation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int MODULE_TYPE___GET_ANNOTATION__STRING = PROCESS_ELEMENT___GET_ANNOTATION__STRING;

  /**
   * The operation id for the '<em>Get System</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODULE_TYPE___GET_SYSTEM = PROCESS_ELEMENT___GET_SYSTEM;

  /**
   * The operation id for the '<em>Get Process</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODULE_TYPE___GET_PROCESS = PROCESS_ELEMENT___GET_PROCESS;

  /**
   * The number of operations of the '<em>Module Type</em>' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int MODULE_TYPE_OPERATION_COUNT = PROCESS_ELEMENT_OPERATION_COUNT + 0;

  /**
   * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP_TYPE__ANNOTATIONS = PROCESS_ELEMENT__ANNOTATIONS;

  /**
   * The feature id for the '<em><b>Process</b></em>' container reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DROP_TYPE__PROCESS = PROCESS_ELEMENT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP_TYPE__NAME = PROCESS_ELEMENT_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Release</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DROP_TYPE__RELEASE = PROCESS_ELEMENT_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Drop Type</em>' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DROP_TYPE_FEATURE_COUNT = PROCESS_ELEMENT_FEATURE_COUNT + 3;

  /**
   * The operation id for the '<em>Get Annotation</em>' operation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DROP_TYPE___GET_ANNOTATION__STRING = PROCESS_ELEMENT___GET_ANNOTATION__STRING;

  /**
   * The operation id for the '<em>Get System</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP_TYPE___GET_SYSTEM = PROCESS_ELEMENT___GET_SYSTEM;

  /**
   * The operation id for the '<em>Get Process</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP_TYPE___GET_PROCESS = PROCESS_ELEMENT___GET_PROCESS;

  /**
   * The number of operations of the '<em>Drop Type</em>' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DROP_TYPE_OPERATION_COUNT = PROCESS_ELEMENT_OPERATION_COUNT + 0;

  /**
   * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODULE__ANNOTATIONS = SYSTEM_ELEMENT__ANNOTATIONS;

  /**
   * The feature id for the '<em><b>System</b></em>' container reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int MODULE__SYSTEM = SYSTEM_ELEMENT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODULE__NAME = SYSTEM_ELEMENT_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Type</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODULE__TYPE = SYSTEM_ELEMENT_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Streams</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODULE__STREAMS = SYSTEM_ELEMENT_FEATURE_COUNT + 3;

  /**
   * The number of structural features of the '<em>Module</em>' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int MODULE_FEATURE_COUNT = SYSTEM_ELEMENT_FEATURE_COUNT + 4;

  /**
   * The operation id for the '<em>Get Annotation</em>' operation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int MODULE___GET_ANNOTATION__STRING = SYSTEM_ELEMENT___GET_ANNOTATION__STRING;

  /**
   * The operation id for the '<em>Get System</em>' operation.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODULE___GET_SYSTEM = SYSTEM_ELEMENT___GET_SYSTEM;

  /**
   * The number of operations of the '<em>Module</em>' class.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODULE_OPERATION_COUNT = SYSTEM_ELEMENT_OPERATION_COUNT + 0;

  /**
   * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BASELINE__ANNOTATIONS = STREAM_ELEMENT__ANNOTATIONS;

  /**
   * The feature id for the '<em><b>Stream</b></em>' container reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int BASELINE__STREAM = STREAM_ELEMENT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Floating</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int BASELINE__FLOATING = STREAM_ELEMENT_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Baseline</em>' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int BASELINE_FEATURE_COUNT = STREAM_ELEMENT_FEATURE_COUNT + 2;

  /**
   * The operation id for the '<em>Get Annotation</em>' operation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int BASELINE___GET_ANNOTATION__STRING = STREAM_ELEMENT___GET_ANNOTATION__STRING;

  /**
   * The operation id for the '<em>Get System</em>' operation.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BASELINE___GET_SYSTEM = STREAM_ELEMENT___GET_SYSTEM;

  /**
   * The operation id for the '<em>Get Module</em>' operation.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BASELINE___GET_MODULE = STREAM_ELEMENT___GET_MODULE;

  /**
   * The operation id for the '<em>Get Stream</em>' operation.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BASELINE___GET_STREAM = STREAM_ELEMENT___GET_STREAM;

  /**
   * The operation id for the '<em>Get Name</em>' operation.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BASELINE___GET_NAME = STREAM_ELEMENT_OPERATION_COUNT + 0;

  /**
   * The operation id for the '<em>Get Branch Point</em>' operation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int BASELINE___GET_BRANCH_POINT = STREAM_ELEMENT_OPERATION_COUNT + 1;

  /**
   * The operation id for the '<em>Get Base Time Stamp</em>' operation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int BASELINE___GET_BASE_TIME_STAMP = STREAM_ELEMENT_OPERATION_COUNT + 2;

  /**
   * The number of operations of the '<em>Baseline</em>' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int BASELINE_OPERATION_COUNT = STREAM_ELEMENT_OPERATION_COUNT + 3;

  /**
   * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FLOATING_BASELINE__ANNOTATIONS = BASELINE__ANNOTATIONS;

  /**
   * The feature id for the '<em><b>Stream</b></em>' container reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int FLOATING_BASELINE__STREAM = BASELINE__STREAM;

  /**
   * The feature id for the '<em><b>Floating</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int FLOATING_BASELINE__FLOATING = BASELINE__FLOATING;

  /**
   * The feature id for the '<em><b>Closed</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int FLOATING_BASELINE__CLOSED = BASELINE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Floating Baseline</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FLOATING_BASELINE_FEATURE_COUNT = BASELINE_FEATURE_COUNT + 1;

  /**
   * The operation id for the '<em>Get Annotation</em>' operation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int FLOATING_BASELINE___GET_ANNOTATION__STRING = BASELINE___GET_ANNOTATION__STRING;

  /**
   * The operation id for the '<em>Get System</em>' operation.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FLOATING_BASELINE___GET_SYSTEM = BASELINE___GET_SYSTEM;

  /**
   * The operation id for the '<em>Get Module</em>' operation.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FLOATING_BASELINE___GET_MODULE = BASELINE___GET_MODULE;

  /**
   * The operation id for the '<em>Get Stream</em>' operation.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FLOATING_BASELINE___GET_STREAM = BASELINE___GET_STREAM;

  /**
   * The operation id for the '<em>Get Name</em>' operation.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FLOATING_BASELINE___GET_NAME = BASELINE___GET_NAME;

  /**
   * The operation id for the '<em>Get Branch Point</em>' operation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int FLOATING_BASELINE___GET_BRANCH_POINT = BASELINE___GET_BRANCH_POINT;

  /**
   * The operation id for the '<em>Get Base Time Stamp</em>' operation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int FLOATING_BASELINE___GET_BASE_TIME_STAMP = BASELINE___GET_BASE_TIME_STAMP;

  /**
   * The operation id for the '<em>Get Base</em>' operation.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FLOATING_BASELINE___GET_BASE = BASELINE_OPERATION_COUNT + 0;

  /**
   * The operation id for the '<em>Get Deliveries</em>' operation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int FLOATING_BASELINE___GET_DELIVERIES = BASELINE_OPERATION_COUNT + 1;

  /**
   * The operation id for the '<em>Get Branch</em>' operation.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FLOATING_BASELINE___GET_BRANCH = BASELINE_OPERATION_COUNT + 2;

  /**
   * The number of operations of the '<em>Floating Baseline</em>' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int FLOATING_BASELINE_OPERATION_COUNT = BASELINE_OPERATION_COUNT + 3;

  /**
   * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FIXED_BASELINE__ANNOTATIONS = BASELINE__ANNOTATIONS;

  /**
   * The feature id for the '<em><b>Stream</b></em>' container reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int FIXED_BASELINE__STREAM = BASELINE__STREAM;

  /**
   * The feature id for the '<em><b>Floating</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int FIXED_BASELINE__FLOATING = BASELINE__FLOATING;

  /**
   * The feature id for the '<em><b>Version</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int FIXED_BASELINE__VERSION = BASELINE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Dependencies</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FIXED_BASELINE__DEPENDENCIES = BASELINE_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Fixed Baseline</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FIXED_BASELINE_FEATURE_COUNT = BASELINE_FEATURE_COUNT + 2;

  /**
   * The operation id for the '<em>Get Annotation</em>' operation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int FIXED_BASELINE___GET_ANNOTATION__STRING = BASELINE___GET_ANNOTATION__STRING;

  /**
   * The operation id for the '<em>Get System</em>' operation.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FIXED_BASELINE___GET_SYSTEM = BASELINE___GET_SYSTEM;

  /**
   * The operation id for the '<em>Get Module</em>' operation.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FIXED_BASELINE___GET_MODULE = BASELINE___GET_MODULE;

  /**
   * The operation id for the '<em>Get Stream</em>' operation.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FIXED_BASELINE___GET_STREAM = BASELINE___GET_STREAM;

  /**
   * The operation id for the '<em>Get Name</em>' operation.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FIXED_BASELINE___GET_NAME = BASELINE___GET_NAME;

  /**
   * The operation id for the '<em>Get Branch Point</em>' operation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int FIXED_BASELINE___GET_BRANCH_POINT = BASELINE___GET_BRANCH_POINT;

  /**
   * The operation id for the '<em>Get Base Time Stamp</em>' operation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int FIXED_BASELINE___GET_BASE_TIME_STAMP = BASELINE___GET_BASE_TIME_STAMP;

  /**
   * The operation id for the '<em>Get Based Changes</em>' operation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int FIXED_BASELINE___GET_BASED_CHANGES = BASELINE_OPERATION_COUNT + 0;

  /**
   * The number of operations of the '<em>Fixed Baseline</em>' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int FIXED_BASELINE_OPERATION_COUNT = BASELINE_OPERATION_COUNT + 1;

  /**
   * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STREAM__ANNOTATIONS = FLOATING_BASELINE__ANNOTATIONS;

  /**
   * The feature id for the '<em><b>Stream</b></em>' container reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int STREAM__STREAM = FLOATING_BASELINE__STREAM;

  /**
   * The feature id for the '<em><b>Floating</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int STREAM__FLOATING = FLOATING_BASELINE__FLOATING;

  /**
   * The feature id for the '<em><b>Closed</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int STREAM__CLOSED = FLOATING_BASELINE__CLOSED;

  /**
   * The feature id for the '<em><b>Module</b></em>' container reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int STREAM__MODULE = FLOATING_BASELINE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Base</b></em>' reference.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STREAM__BASE = FLOATING_BASELINE_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Start Time Stamp</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int STREAM__START_TIME_STAMP = FLOATING_BASELINE_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Major Version</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int STREAM__MAJOR_VERSION = FLOATING_BASELINE_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Minor Version</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int STREAM__MINOR_VERSION = FLOATING_BASELINE_FEATURE_COUNT + 4;

  /**
   * The feature id for the '<em><b>Code Name</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int STREAM__CODE_NAME = FLOATING_BASELINE_FEATURE_COUNT + 5;

  /**
   * The feature id for the '<em><b>Allowed Changes</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int STREAM__ALLOWED_CHANGES = FLOATING_BASELINE_FEATURE_COUNT + 6;

  /**
   * The feature id for the '<em><b>Mode</b></em>' attribute.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STREAM__MODE = FLOATING_BASELINE_FEATURE_COUNT + 7;

  /**
   * The feature id for the '<em><b>Development Branch</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int STREAM__DEVELOPMENT_BRANCH = FLOATING_BASELINE_FEATURE_COUNT + 8;

  /**
   * The feature id for the '<em><b>Maintenance Branch</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int STREAM__MAINTENANCE_BRANCH = FLOATING_BASELINE_FEATURE_COUNT + 9;

  /**
   * The feature id for the '<em><b>Contents</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STREAM__CONTENTS = FLOATING_BASELINE_FEATURE_COUNT + 10;

  /**
   * The feature id for the '<em><b>Maintenance Time Stamp</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STREAM__MAINTENANCE_TIME_STAMP = FLOATING_BASELINE_FEATURE_COUNT + 11;

  /**
   * The number of structural features of the '<em>Stream</em>' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int STREAM_FEATURE_COUNT = FLOATING_BASELINE_FEATURE_COUNT + 12;

  /**
   * The operation id for the '<em>Get Annotation</em>' operation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int STREAM___GET_ANNOTATION__STRING = FLOATING_BASELINE___GET_ANNOTATION__STRING;

  /**
   * The operation id for the '<em>Get System</em>' operation.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STREAM___GET_SYSTEM = FLOATING_BASELINE___GET_SYSTEM;

  /**
   * The operation id for the '<em>Get Module</em>' operation.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STREAM___GET_MODULE = FLOATING_BASELINE___GET_MODULE;

  /**
   * The operation id for the '<em>Get Stream</em>' operation.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STREAM___GET_STREAM = FLOATING_BASELINE___GET_STREAM;

  /**
   * The operation id for the '<em>Get Name</em>' operation.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STREAM___GET_NAME = FLOATING_BASELINE___GET_NAME;

  /**
   * The operation id for the '<em>Get Branch Point</em>' operation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int STREAM___GET_BRANCH_POINT = FLOATING_BASELINE___GET_BRANCH_POINT;

  /**
   * The operation id for the '<em>Get Base Time Stamp</em>' operation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int STREAM___GET_BASE_TIME_STAMP = FLOATING_BASELINE___GET_BASE_TIME_STAMP;

  /**
   * The operation id for the '<em>Get Base</em>' operation.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STREAM___GET_BASE = FLOATING_BASELINE___GET_BASE;

  /**
   * The operation id for the '<em>Get Deliveries</em>' operation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int STREAM___GET_DELIVERIES = FLOATING_BASELINE___GET_DELIVERIES;

  /**
   * The operation id for the '<em>Get Branch</em>' operation.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STREAM___GET_BRANCH = FLOATING_BASELINE___GET_BRANCH;

  /**
   * The operation id for the '<em>Insert Content</em>' operation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int STREAM___INSERT_CONTENT__BASELINE = FLOATING_BASELINE_OPERATION_COUNT + 0;

  /**
   * The operation id for the '<em>Get Branch Point</em>' operation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int STREAM___GET_BRANCH_POINT__LONG = FLOATING_BASELINE_OPERATION_COUNT + 1;

  /**
   * The operation id for the '<em>Get First Release</em>' operation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int STREAM___GET_FIRST_RELEASE = FLOATING_BASELINE_OPERATION_COUNT + 2;

  /**
   * The operation id for the '<em>Get Last Release</em>' operation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int STREAM___GET_LAST_RELEASE = FLOATING_BASELINE_OPERATION_COUNT + 3;

  /**
   * The operation id for the '<em>Get Releases</em>' operation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int STREAM___GET_RELEASES = FLOATING_BASELINE_OPERATION_COUNT + 4;

  /**
   * The operation id for the '<em>Get Based Changes</em>' operation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int STREAM___GET_BASED_CHANGES = FLOATING_BASELINE_OPERATION_COUNT + 5;

  /**
   * The number of operations of the '<em>Stream</em>' class.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STREAM_OPERATION_COUNT = FLOATING_BASELINE_OPERATION_COUNT + 6;

  /**
   * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CHANGE__ANNOTATIONS = FLOATING_BASELINE__ANNOTATIONS;

  /**
   * The feature id for the '<em><b>Stream</b></em>' container reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int CHANGE__STREAM = FLOATING_BASELINE__STREAM;

  /**
   * The feature id for the '<em><b>Floating</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int CHANGE__FLOATING = FLOATING_BASELINE__FLOATING;

  /**
   * The feature id for the '<em><b>Closed</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int CHANGE__CLOSED = FLOATING_BASELINE__CLOSED;

  /**
   * The feature id for the '<em><b>Base</b></em>' reference.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CHANGE__BASE = FLOATING_BASELINE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Label</b></em>' attribute.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CHANGE__LABEL = FLOATING_BASELINE_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Impact</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int CHANGE__IMPACT = FLOATING_BASELINE_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Branch</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int CHANGE__BRANCH = FLOATING_BASELINE_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Deliveries</b></em>' reference list. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int CHANGE__DELIVERIES = FLOATING_BASELINE_FEATURE_COUNT + 4;

  /**
   * The number of structural features of the '<em>Change</em>' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int CHANGE_FEATURE_COUNT = FLOATING_BASELINE_FEATURE_COUNT + 5;

  /**
   * The operation id for the '<em>Get Annotation</em>' operation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int CHANGE___GET_ANNOTATION__STRING = FLOATING_BASELINE___GET_ANNOTATION__STRING;

  /**
   * The operation id for the '<em>Get System</em>' operation.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CHANGE___GET_SYSTEM = FLOATING_BASELINE___GET_SYSTEM;

  /**
   * The operation id for the '<em>Get Module</em>' operation.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CHANGE___GET_MODULE = FLOATING_BASELINE___GET_MODULE;

  /**
   * The operation id for the '<em>Get Stream</em>' operation.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CHANGE___GET_STREAM = FLOATING_BASELINE___GET_STREAM;

  /**
   * The operation id for the '<em>Get Name</em>' operation.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CHANGE___GET_NAME = FLOATING_BASELINE___GET_NAME;

  /**
   * The operation id for the '<em>Get Branch Point</em>' operation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int CHANGE___GET_BRANCH_POINT = FLOATING_BASELINE___GET_BRANCH_POINT;

  /**
   * The operation id for the '<em>Get Base Time Stamp</em>' operation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int CHANGE___GET_BASE_TIME_STAMP = FLOATING_BASELINE___GET_BASE_TIME_STAMP;

  /**
   * The operation id for the '<em>Get Base</em>' operation.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CHANGE___GET_BASE = FLOATING_BASELINE___GET_BASE;

  /**
   * The operation id for the '<em>Get Deliveries</em>' operation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int CHANGE___GET_DELIVERIES = FLOATING_BASELINE___GET_DELIVERIES;

  /**
   * The operation id for the '<em>Get Branch</em>' operation.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CHANGE___GET_BRANCH = FLOATING_BASELINE___GET_BRANCH;

  /**
   * The number of operations of the '<em>Change</em>' class.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CHANGE_OPERATION_COUNT = FLOATING_BASELINE_OPERATION_COUNT + 0;

  /**
   * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY__ANNOTATIONS = FIXED_BASELINE__ANNOTATIONS;

  /**
   * The feature id for the '<em><b>Stream</b></em>' container reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DELIVERY__STREAM = FIXED_BASELINE__STREAM;

  /**
   * The feature id for the '<em><b>Floating</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DELIVERY__FLOATING = FIXED_BASELINE__FLOATING;

  /**
   * The feature id for the '<em><b>Version</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DELIVERY__VERSION = FIXED_BASELINE__VERSION;

  /**
   * The feature id for the '<em><b>Dependencies</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY__DEPENDENCIES = FIXED_BASELINE__DEPENDENCIES;

  /**
   * The feature id for the '<em><b>Change</b></em>' reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DELIVERY__CHANGE = FIXED_BASELINE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Merge Source</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DELIVERY__MERGE_SOURCE = FIXED_BASELINE_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Merge Target</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DELIVERY__MERGE_TARGET = FIXED_BASELINE_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Delivery</em>' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DELIVERY_FEATURE_COUNT = FIXED_BASELINE_FEATURE_COUNT + 3;

  /**
   * The operation id for the '<em>Get Annotation</em>' operation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DELIVERY___GET_ANNOTATION__STRING = FIXED_BASELINE___GET_ANNOTATION__STRING;

  /**
   * The operation id for the '<em>Get System</em>' operation.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY___GET_SYSTEM = FIXED_BASELINE___GET_SYSTEM;

  /**
   * The operation id for the '<em>Get Module</em>' operation.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY___GET_MODULE = FIXED_BASELINE___GET_MODULE;

  /**
   * The operation id for the '<em>Get Stream</em>' operation.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY___GET_STREAM = FIXED_BASELINE___GET_STREAM;

  /**
   * The operation id for the '<em>Get Name</em>' operation.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELIVERY___GET_NAME = FIXED_BASELINE___GET_NAME;

  /**
   * The operation id for the '<em>Get Branch Point</em>' operation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DELIVERY___GET_BRANCH_POINT = FIXED_BASELINE___GET_BRANCH_POINT;

  /**
   * The operation id for the '<em>Get Base Time Stamp</em>' operation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DELIVERY___GET_BASE_TIME_STAMP = FIXED_BASELINE___GET_BASE_TIME_STAMP;

  /**
   * The operation id for the '<em>Get Based Changes</em>' operation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DELIVERY___GET_BASED_CHANGES = FIXED_BASELINE___GET_BASED_CHANGES;

  /**
   * The number of operations of the '<em>Delivery</em>' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DELIVERY_OPERATION_COUNT = FIXED_BASELINE_OPERATION_COUNT + 0;

  /**
   * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP__ANNOTATIONS = FIXED_BASELINE__ANNOTATIONS;

  /**
   * The feature id for the '<em><b>Stream</b></em>' container reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DROP__STREAM = FIXED_BASELINE__STREAM;

  /**
   * The feature id for the '<em><b>Floating</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DROP__FLOATING = FIXED_BASELINE__FLOATING;

  /**
   * The feature id for the '<em><b>Version</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DROP__VERSION = FIXED_BASELINE__VERSION;

  /**
   * The feature id for the '<em><b>Dependencies</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP__DEPENDENCIES = FIXED_BASELINE__DEPENDENCIES;

  /**
   * The feature id for the '<em><b>Type</b></em>' reference.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP__TYPE = FIXED_BASELINE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Label</b></em>' attribute.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP__LABEL = FIXED_BASELINE_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Branch Point</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DROP__BRANCH_POINT = FIXED_BASELINE_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Drop</em>' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DROP_FEATURE_COUNT = FIXED_BASELINE_FEATURE_COUNT + 3;

  /**
   * The operation id for the '<em>Get Annotation</em>' operation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DROP___GET_ANNOTATION__STRING = FIXED_BASELINE___GET_ANNOTATION__STRING;

  /**
   * The operation id for the '<em>Get System</em>' operation.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP___GET_SYSTEM = FIXED_BASELINE___GET_SYSTEM;

  /**
   * The operation id for the '<em>Get Module</em>' operation.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP___GET_MODULE = FIXED_BASELINE___GET_MODULE;

  /**
   * The operation id for the '<em>Get Stream</em>' operation.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP___GET_STREAM = FIXED_BASELINE___GET_STREAM;

  /**
   * The operation id for the '<em>Get Name</em>' operation.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP___GET_NAME = FIXED_BASELINE___GET_NAME;

  /**
   * The operation id for the '<em>Get Branch Point</em>' operation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DROP___GET_BRANCH_POINT = FIXED_BASELINE___GET_BRANCH_POINT;

  /**
   * The operation id for the '<em>Get Base Time Stamp</em>' operation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DROP___GET_BASE_TIME_STAMP = FIXED_BASELINE___GET_BASE_TIME_STAMP;

  /**
   * The operation id for the '<em>Get Based Changes</em>' operation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DROP___GET_BASED_CHANGES = FIXED_BASELINE___GET_BASED_CHANGES;

  /**
   * The operation id for the '<em>Is Release</em>' operation.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP___IS_RELEASE = FIXED_BASELINE_OPERATION_COUNT + 0;

  /**
   * The operation id for the '<em>Get Based Streams</em>' operation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DROP___GET_BASED_STREAMS = FIXED_BASELINE_OPERATION_COUNT + 1;

  /**
   * The number of operations of the '<em>Drop</em>' class.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DROP_OPERATION_COUNT = FIXED_BASELINE_OPERATION_COUNT + 2;

  /**
   * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEPENDENCY__ANNOTATIONS = STREAM_ELEMENT__ANNOTATIONS;

  /**
   * The feature id for the '<em><b>Target</b></em>' reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DEPENDENCY__TARGET = STREAM_ELEMENT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Version Range</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DEPENDENCY__VERSION_RANGE = STREAM_ELEMENT_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Dependency</em>' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DEPENDENCY_FEATURE_COUNT = STREAM_ELEMENT_FEATURE_COUNT + 2;

  /**
   * The operation id for the '<em>Get Annotation</em>' operation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DEPENDENCY___GET_ANNOTATION__STRING = STREAM_ELEMENT___GET_ANNOTATION__STRING;

  /**
   * The operation id for the '<em>Get System</em>' operation.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEPENDENCY___GET_SYSTEM = STREAM_ELEMENT___GET_SYSTEM;

  /**
   * The operation id for the '<em>Get Module</em>' operation.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEPENDENCY___GET_MODULE = STREAM_ELEMENT___GET_MODULE;

  /**
   * The operation id for the '<em>Get Stream</em>' operation.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEPENDENCY___GET_STREAM = STREAM_ELEMENT___GET_STREAM;

  /**
   * The number of operations of the '<em>Dependency</em>' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DEPENDENCY_OPERATION_COUNT = STREAM_ELEMENT_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.lm.Impact <em>Impact</em>}' enum.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.lm.Impact
   * @see org.eclipse.emf.cdo.lm.impl.LMPackageImpl#getImpact()
   * @generated
   */
  int IMPACT = 17;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.lm.StreamMode <em>Stream Mode</em>}' enum.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.lm.StreamMode
   * @see org.eclipse.emf.cdo.lm.impl.LMPackageImpl#getStreamMode()
   * @generated
   */
  int STREAM_MODE = 18;

  /**
   * The meta object id for the '<em>Base Point</em>' data type. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.eclipse.emf.cdo.lm.BasePoint
   * @see org.eclipse.emf.cdo.lm.impl.LMPackageImpl#getBasePoint()
   * @generated
   */
  int BASE_POINT = 19;

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.lm.SystemElement <em>System Element</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>System Element</em>'.
   * @see org.eclipse.emf.cdo.lm.SystemElement
   * @generated
   */
  EClass getSystemElement();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.lm.SystemElement#getSystem() <em>Get System</em>}' operation.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the '<em>Get System</em>' operation.
   * @see org.eclipse.emf.cdo.lm.SystemElement#getSystem()
   * @generated
   */
  EOperation getSystemElement__GetSystem();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.lm.ProcessElement <em>Process Element</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Process Element</em>'.
   * @see org.eclipse.emf.cdo.lm.ProcessElement
   * @generated
   */
  EClass getProcessElement();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.lm.ProcessElement#getProcess() <em>Get Process</em>}' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the '<em>Get Process</em>' operation.
   * @see org.eclipse.emf.cdo.lm.ProcessElement#getProcess()
   * @generated
   */
  EOperation getProcessElement__GetProcess();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.lm.ModuleElement <em>Module Element</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>Module Element</em>'.
   * @see org.eclipse.emf.cdo.lm.ModuleElement
   * @generated
   */
  EClass getModuleElement();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.lm.ModuleElement#getModule() <em>Get Module</em>}' operation.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the '<em>Get Module</em>' operation.
   * @see org.eclipse.emf.cdo.lm.ModuleElement#getModule()
   * @generated
   */
  EOperation getModuleElement__GetModule();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.lm.StreamElement <em>Stream Element</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>Stream Element</em>'.
   * @see org.eclipse.emf.cdo.lm.StreamElement
   * @generated
   */
  EClass getStreamElement();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.lm.StreamElement#getStream() <em>Get Stream</em>}' operation.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the '<em>Get Stream</em>' operation.
   * @see org.eclipse.emf.cdo.lm.StreamElement#getStream()
   * @generated
   */
  EOperation getStreamElement__GetStream();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.lm.System <em>System</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>System</em>'.
   * @see org.eclipse.emf.cdo.lm.System
   * @generated
   */
  EClass getSystem();

  /**
   * Returns the meta object for the attribute
   * '{@link org.eclipse.emf.cdo.lm.System#getName <em>Name</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.lm.System#getName()
   * @see #getSystem()
   * @generated
   */
  EAttribute getSystem_Name();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.emf.cdo.lm.System#getProcess <em>Process</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Process</em>'.
   * @see org.eclipse.emf.cdo.lm.System#getProcess()
   * @see #getSystem()
   * @generated
   */
  EReference getSystem_Process();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.lm.System#getModules <em>Modules</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Modules</em>'.
   * @see org.eclipse.emf.cdo.lm.System#getModules()
   * @see #getSystem()
   * @generated
   */
  EReference getSystem_Modules();

  /**
   * Returns the meta object for the
   * '{@link org.eclipse.emf.cdo.lm.System#getModule(java.lang.String)
   * <em>Get Module</em>}' operation. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @return the meta object for the '<em>Get Module</em>' operation.
   * @see org.eclipse.emf.cdo.lm.System#getModule(java.lang.String)
   * @generated
   */
  EOperation getSystem__GetModule__String();

  /**
   * Returns the meta object for class
   * '{@link org.eclipse.emf.cdo.lm.Process <em>Process</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Process</em>'.
   * @see org.eclipse.emf.cdo.lm.Process
   * @generated
   */
  EClass getProcess();

  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.lm.Process#getSystem <em>System</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>System</em>'.
   * @see org.eclipse.emf.cdo.lm.Process#getSystem()
   * @see #getProcess()
   * @generated
   */
  EReference getProcess_System();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.lm.Process#getDropTypes <em>Drop Types</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Drop Types</em>'.
   * @see org.eclipse.emf.cdo.lm.Process#getDropTypes()
   * @see #getProcess()
   * @generated
   */
  EReference getProcess_DropTypes();

  /**
   * Returns the meta object for the attribute
   * '{@link org.eclipse.emf.cdo.lm.Process#getModuleDefinitionPath
   * <em>Module Definition Path</em>}'. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @return the meta object for the attribute '<em>Module Definition Path</em>'.
   * @see org.eclipse.emf.cdo.lm.Process#getModuleDefinitionPath()
   * @see #getProcess()
   * @generated
   */
  EAttribute getProcess_ModuleDefinitionPath();

  /**
   * Returns the meta object for the attribute
   * '{@link org.eclipse.emf.cdo.lm.Process#getInitialModuleVersion
   * <em>Initial Module Version</em>}'. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @return the meta object for the attribute '<em>Initial Module Version</em>'.
   * @see org.eclipse.emf.cdo.lm.Process#getInitialModuleVersion()
   * @see #getProcess()
   * @generated
   */
  EAttribute getProcess_InitialModuleVersion();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.lm.Process#getModuleTypes <em>Module Types</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Module Types</em>'.
   * @see org.eclipse.emf.cdo.lm.Process#getModuleTypes()
   * @see #getProcess()
   * @generated
   */
  EReference getProcess_ModuleTypes();

  /**
   * Returns the meta object for class
   * '{@link org.eclipse.emf.cdo.lm.DropType <em>Drop Type</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Drop Type</em>'.
   * @see org.eclipse.emf.cdo.lm.DropType
   * @generated
   */
  EClass getDropType();

  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.lm.DropType#getProcess <em>Process</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Process</em>'.
   * @see org.eclipse.emf.cdo.lm.DropType#getProcess()
   * @see #getDropType()
   * @generated
   */
  EReference getDropType_Process();

  /**
   * Returns the meta object for the attribute
   * '{@link org.eclipse.emf.cdo.lm.DropType#getName <em>Name</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.lm.DropType#getName()
   * @see #getDropType()
   * @generated
   */
  EAttribute getDropType_Name();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.DropType#isRelease <em>Release</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Release</em>'.
   * @see org.eclipse.emf.cdo.lm.DropType#isRelease()
   * @see #getDropType()
   * @generated
   */
  EAttribute getDropType_Release();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.lm.Module <em>Module</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>Module</em>'.
   * @see org.eclipse.emf.cdo.lm.Module
   * @generated
   */
  EClass getModule();

  /**
   * Returns the meta object for the container reference
   * '{@link org.eclipse.emf.cdo.lm.Module#getSystem <em>System</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the container reference '<em>System</em>'.
   * @see org.eclipse.emf.cdo.lm.Module#getSystem()
   * @see #getModule()
   * @generated
   */
  EReference getModule_System();

  /**
   * Returns the meta object for the attribute
   * '{@link org.eclipse.emf.cdo.lm.Module#getName <em>Name</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.lm.Module#getName()
   * @see #getModule()
   * @generated
   */
  EAttribute getModule_Name();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.lm.Module#getType <em>Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Type</em>'.
   * @see org.eclipse.emf.cdo.lm.Module#getType()
   * @see #getModule()
   * @generated
   */
  EReference getModule_Type();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.lm.Module#getStreams <em>Streams</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Streams</em>'.
   * @see org.eclipse.emf.cdo.lm.Module#getStreams()
   * @see #getModule()
   * @generated
   */
  EReference getModule_Streams();

  /**
   * Returns the meta object for class
   * '{@link org.eclipse.emf.cdo.lm.Dependency <em>Dependency</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Dependency</em>'.
   * @see org.eclipse.emf.cdo.lm.Dependency
   * @generated
   */
  EClass getDependency();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.lm.Dependency#getTarget <em>Target</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Target</em>'.
   * @see org.eclipse.emf.cdo.lm.Dependency#getTarget()
   * @see #getDependency()
   * @generated
   */
  EReference getDependency_Target();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.Dependency#getVersionRange <em>Version Range</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Version Range</em>'.
   * @see org.eclipse.emf.cdo.lm.Dependency#getVersionRange()
   * @see #getDependency()
   * @generated
   */
  EAttribute getDependency_VersionRange();

  /**
   * Returns the meta object for class
   * '{@link org.eclipse.emf.cdo.lm.ModuleType <em>Module Type</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Module Type</em>'.
   * @see org.eclipse.emf.cdo.lm.ModuleType
   * @generated
   */
  EClass getModuleType();

  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.lm.ModuleType#getProcess <em>Process</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Process</em>'.
   * @see org.eclipse.emf.cdo.lm.ModuleType#getProcess()
   * @see #getModuleType()
   * @generated
   */
  EReference getModuleType_Process();

  /**
   * Returns the meta object for the attribute
   * '{@link org.eclipse.emf.cdo.lm.ModuleType#getName <em>Name</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.lm.ModuleType#getName()
   * @see #getModuleType()
   * @generated
   */
  EAttribute getModuleType_Name();

  /**
   * Returns the meta object for class
   * '{@link org.eclipse.emf.cdo.lm.Baseline <em>Baseline</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Baseline</em>'.
   * @see org.eclipse.emf.cdo.lm.Baseline
   * @generated
   */
  EClass getBaseline();

  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.lm.Baseline#getStream <em>Stream</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Stream</em>'.
   * @see org.eclipse.emf.cdo.lm.Baseline#getStream()
   * @see #getBaseline()
   * @generated
   */
  EReference getBaseline_Stream();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.Baseline#isFloating <em>Floating</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Floating</em>'.
   * @see org.eclipse.emf.cdo.lm.Baseline#isFloating()
   * @see #getBaseline()
   * @generated
   */
  EAttribute getBaseline_Floating();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.lm.Baseline#getName() <em>Get Name</em>}' operation.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the '<em>Get Name</em>' operation.
   * @see org.eclipse.emf.cdo.lm.Baseline#getName()
   * @generated
   */
  EOperation getBaseline__GetName();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.lm.Baseline#getBranchPoint() <em>Get Branch Point</em>}' operation.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the '<em>Get Branch Point</em>' operation.
   * @see org.eclipse.emf.cdo.lm.Baseline#getBranchPoint()
   * @generated
   */
  EOperation getBaseline__GetBranchPoint();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.lm.Baseline#getBaseTimeStamp() <em>Get Base Time Stamp</em>}' operation.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the '<em>Get Base Time Stamp</em>' operation.
   * @see org.eclipse.emf.cdo.lm.Baseline#getBaseTimeStamp()
   * @generated
   */
  EOperation getBaseline__GetBaseTimeStamp();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.lm.FloatingBaseline <em>Floating Baseline</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>Floating Baseline</em>'.
   * @see org.eclipse.emf.cdo.lm.FloatingBaseline
   * @generated
   */
  EClass getFloatingBaseline();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.FloatingBaseline#isClosed <em>Closed</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Closed</em>'.
   * @see org.eclipse.emf.cdo.lm.FloatingBaseline#isClosed()
   * @see #getFloatingBaseline()
   * @generated
   */
  EAttribute getFloatingBaseline_Closed();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.lm.FloatingBaseline#getBase() <em>Get Base</em>}' operation.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the '<em>Get Base</em>' operation.
   * @see org.eclipse.emf.cdo.lm.FloatingBaseline#getBase()
   * @generated
   */
  EOperation getFloatingBaseline__GetBase();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.lm.FloatingBaseline#getDeliveries() <em>Get Deliveries</em>}' operation.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the '<em>Get Deliveries</em>' operation.
   * @see org.eclipse.emf.cdo.lm.FloatingBaseline#getDeliveries()
   * @generated
   */
  EOperation getFloatingBaseline__GetDeliveries();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.lm.FloatingBaseline#getBranch() <em>Get Branch</em>}' operation.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the '<em>Get Branch</em>' operation.
   * @see org.eclipse.emf.cdo.lm.FloatingBaseline#getBranch()
   * @generated
   */
  EOperation getFloatingBaseline__GetBranch();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.lm.FixedBaseline <em>Fixed Baseline</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>Fixed Baseline</em>'.
   * @see org.eclipse.emf.cdo.lm.FixedBaseline
   * @generated
   */
  EClass getFixedBaseline();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.FixedBaseline#getVersion <em>Version</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Version</em>'.
   * @see org.eclipse.emf.cdo.lm.FixedBaseline#getVersion()
   * @see #getFixedBaseline()
   * @generated
   */
  EAttribute getFixedBaseline_Version();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.lm.FixedBaseline#getDependencies <em>Dependencies</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Dependencies</em>'.
   * @see org.eclipse.emf.cdo.lm.FixedBaseline#getDependencies()
   * @see #getFixedBaseline()
   * @generated
   */
  EReference getFixedBaseline_Dependencies();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.lm.FixedBaseline#getBasedChanges() <em>Get Based Changes</em>}' operation.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the '<em>Get Based Changes</em>' operation.
   * @see org.eclipse.emf.cdo.lm.FixedBaseline#getBasedChanges()
   * @generated
   */
  EOperation getFixedBaseline__GetBasedChanges();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.lm.Stream <em>Stream</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>Stream</em>'.
   * @see org.eclipse.emf.cdo.lm.Stream
   * @generated
   */
  EClass getStream();

  /**
   * Returns the meta object for the container reference
   * '{@link org.eclipse.emf.cdo.lm.Stream#getModule <em>Module</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the container reference '<em>Module</em>'.
   * @see org.eclipse.emf.cdo.lm.Stream#getModule()
   * @see #getStream()
   * @generated
   */
  EReference getStream_Module();

  /**
   * Returns the meta object for the reference
   * '{@link org.eclipse.emf.cdo.lm.Stream#getBase <em>Base</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the reference '<em>Base</em>'.
   * @see org.eclipse.emf.cdo.lm.Stream#getBase()
   * @see #getStream()
   * @generated
   */
  EReference getStream_Base();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.Stream#getStartTimeStamp <em>Start Time Stamp</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Start Time Stamp</em>'.
   * @see org.eclipse.emf.cdo.lm.Stream#getStartTimeStamp()
   * @see #getStream()
   * @generated
   */
  EAttribute getStream_StartTimeStamp();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.Stream#getMajorVersion <em>Major Version</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Major Version</em>'.
   * @see org.eclipse.emf.cdo.lm.Stream#getMajorVersion()
   * @see #getStream()
   * @generated
   */
  EAttribute getStream_MajorVersion();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.Stream#getMinorVersion <em>Minor Version</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Minor Version</em>'.
   * @see org.eclipse.emf.cdo.lm.Stream#getMinorVersion()
   * @see #getStream()
   * @generated
   */
  EAttribute getStream_MinorVersion();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.Stream#getCodeName <em>Code Name</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Code Name</em>'.
   * @see org.eclipse.emf.cdo.lm.Stream#getCodeName()
   * @see #getStream()
   * @generated
   */
  EAttribute getStream_CodeName();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.Stream#getAllowedChanges <em>Allowed Changes</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Allowed Changes</em>'.
   * @see org.eclipse.emf.cdo.lm.Stream#getAllowedChanges()
   * @see #getStream()
   * @generated
   */
  EAttribute getStream_AllowedChanges();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.lm.Stream#getContents <em>Contents</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Contents</em>'.
   * @see org.eclipse.emf.cdo.lm.Stream#getContents()
   * @see #getStream()
   * @generated
   */
  EReference getStream_Contents();

  /**
   * Returns the meta object for the attribute
   * '{@link org.eclipse.emf.cdo.lm.Stream#getMaintenanceTimeStamp
   * <em>Maintenance Time Stamp</em>}'. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @return the meta object for the attribute '<em>Maintenance Time Stamp</em>'.
   * @see org.eclipse.emf.cdo.lm.Stream#getMaintenanceTimeStamp()
   * @see #getStream()
   * @generated
   */
  EAttribute getStream_MaintenanceTimeStamp();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.lm.Stream#insertContent(org.eclipse.emf.cdo.lm.Baseline) <em>Insert Content</em>}' operation.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return the meta object for the '<em>Insert Content</em>' operation.
   * @see org.eclipse.emf.cdo.lm.Stream#insertContent(org.eclipse.emf.cdo.lm.Baseline)
   * @generated
   */
  EOperation getStream__InsertContent__Baseline();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.lm.Stream#getBranchPoint(long) <em>Get Branch Point</em>}' operation.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the '<em>Get Branch Point</em>' operation.
   * @see org.eclipse.emf.cdo.lm.Stream#getBranchPoint(long)
   * @generated
   */
  EOperation getStream__GetBranchPoint__long();

  /**
   * Returns the meta object for the attribute
   * '{@link org.eclipse.emf.cdo.lm.Stream#getMode <em>Mode</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Mode</em>'.
   * @see org.eclipse.emf.cdo.lm.Stream#getMode()
   * @see #getStream()
   * @generated
   */
  EAttribute getStream_Mode();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.Stream#getDevelopmentBranch <em>Development Branch</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Development Branch</em>'.
   * @see org.eclipse.emf.cdo.lm.Stream#getDevelopmentBranch()
   * @see #getStream()
   * @generated
   */
  EAttribute getStream_DevelopmentBranch();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.Stream#getMaintenanceBranch <em>Maintenance Branch</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Maintenance Branch</em>'.
   * @see org.eclipse.emf.cdo.lm.Stream#getMaintenanceBranch()
   * @see #getStream()
   * @generated
   */
  EAttribute getStream_MaintenanceBranch();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.lm.Stream#getFirstRelease() <em>Get First Release</em>}' operation.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the '<em>Get First Release</em>' operation.
   * @see org.eclipse.emf.cdo.lm.Stream#getFirstRelease()
   * @generated
   */
  EOperation getStream__GetFirstRelease();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.lm.Stream#getLastRelease() <em>Get Last Release</em>}' operation.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the '<em>Get Last Release</em>' operation.
   * @see org.eclipse.emf.cdo.lm.Stream#getLastRelease()
   * @generated
   */
  EOperation getStream__GetLastRelease();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.lm.Stream#getReleases() <em>Get Releases</em>}' operation.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the '<em>Get Releases</em>' operation.
   * @see org.eclipse.emf.cdo.lm.Stream#getReleases()
   * @generated
   */
  EOperation getStream__GetReleases();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.lm.Stream#getBasedChanges() <em>Get Based Changes</em>}' operation.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the '<em>Get Based Changes</em>' operation.
   * @see org.eclipse.emf.cdo.lm.Stream#getBasedChanges()
   * @generated
   */
  EOperation getStream__GetBasedChanges();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.lm.Change <em>Change</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>Change</em>'.
   * @see org.eclipse.emf.cdo.lm.Change
   * @generated
   */
  EClass getChange();

  /**
   * Returns the meta object for the reference
   * '{@link org.eclipse.emf.cdo.lm.Change#getBase <em>Base</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the reference '<em>Base</em>'.
   * @see org.eclipse.emf.cdo.lm.Change#getBase()
   * @see #getChange()
   * @generated
   */
  EReference getChange_Base();

  /**
   * Returns the meta object for the attribute
   * '{@link org.eclipse.emf.cdo.lm.Change#getLabel <em>Label</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Label</em>'.
   * @see org.eclipse.emf.cdo.lm.Change#getLabel()
   * @see #getChange()
   * @generated
   */
  EAttribute getChange_Label();

  /**
   * Returns the meta object for the attribute
   * '{@link org.eclipse.emf.cdo.lm.Change#getImpact <em>Impact</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Impact</em>'.
   * @see org.eclipse.emf.cdo.lm.Change#getImpact()
   * @see #getChange()
   * @generated
   */
  EAttribute getChange_Impact();

  /**
   * Returns the meta object for the attribute
   * '{@link org.eclipse.emf.cdo.lm.Change#getBranch <em>Branch</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Branch</em>'.
   * @see org.eclipse.emf.cdo.lm.Change#getBranch()
   * @see #getChange()
   * @generated
   */
  EAttribute getChange_Branch();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.lm.Change#getDeliveries <em>Deliveries</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Deliveries</em>'.
   * @see org.eclipse.emf.cdo.lm.Change#getDeliveries()
   * @see #getChange()
   * @generated
   */
  EReference getChange_Deliveries();

  /**
   * Returns the meta object for class
   * '{@link org.eclipse.emf.cdo.lm.Delivery <em>Delivery</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Delivery</em>'.
   * @see org.eclipse.emf.cdo.lm.Delivery
   * @generated
   */
  EClass getDelivery();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.lm.Delivery#getChange <em>Change</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Change</em>'.
   * @see org.eclipse.emf.cdo.lm.Delivery#getChange()
   * @see #getDelivery()
   * @generated
   */
  EReference getDelivery_Change();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.Delivery#getMergeSource <em>Merge Source</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Merge Source</em>'.
   * @see org.eclipse.emf.cdo.lm.Delivery#getMergeSource()
   * @see #getDelivery()
   * @generated
   */
  EAttribute getDelivery_MergeSource();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.Delivery#getMergeTarget <em>Merge Target</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Merge Target</em>'.
   * @see org.eclipse.emf.cdo.lm.Delivery#getMergeTarget()
   * @see #getDelivery()
   * @generated
   */
  EAttribute getDelivery_MergeTarget();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.lm.Drop <em>Drop</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>Drop</em>'.
   * @see org.eclipse.emf.cdo.lm.Drop
   * @generated
   */
  EClass getDrop();

  /**
   * Returns the meta object for the reference
   * '{@link org.eclipse.emf.cdo.lm.Drop#getType <em>Type</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the reference '<em>Type</em>'.
   * @see org.eclipse.emf.cdo.lm.Drop#getType()
   * @see #getDrop()
   * @generated
   */
  EReference getDrop_Type();

  /**
   * Returns the meta object for the attribute
   * '{@link org.eclipse.emf.cdo.lm.Drop#getLabel <em>Label</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the attribute '<em>Label</em>'.
   * @see org.eclipse.emf.cdo.lm.Drop#getLabel()
   * @see #getDrop()
   * @generated
   */
  EAttribute getDrop_Label();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.Drop#getBranchPoint <em>Branch Point</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Branch Point</em>'.
   * @see org.eclipse.emf.cdo.lm.Drop#getBranchPoint()
   * @see #getDrop()
   * @generated
   */
  EAttribute getDrop_BranchPoint();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.lm.Drop#isRelease() <em>Is Release</em>}' operation.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the '<em>Is Release</em>' operation.
   * @see org.eclipse.emf.cdo.lm.Drop#isRelease()
   * @generated
   */
  EOperation getDrop__IsRelease();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.lm.Drop#getBasedStreams() <em>Get Based Streams</em>}' operation.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the '<em>Get Based Streams</em>' operation.
   * @see org.eclipse.emf.cdo.lm.Drop#getBasedStreams()
   * @generated
   */
  EOperation getDrop__GetBasedStreams();

  /**
   * Returns the meta object for enum '{@link org.eclipse.emf.cdo.lm.Impact <em>Impact</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for enum '<em>Impact</em>'.
   * @see org.eclipse.emf.cdo.lm.Impact
   * @generated
   */
  EEnum getImpact();

  /**
   * Returns the meta object for enum
   * '{@link org.eclipse.emf.cdo.lm.StreamMode <em>Stream Mode</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for enum '<em>Stream Mode</em>'.
   * @see org.eclipse.emf.cdo.lm.StreamMode
   * @generated
   */
  EEnum getStreamMode();

  /**
   * Returns the meta object for data type
   * '{@link org.eclipse.emf.cdo.lm.BasePoint <em>Base Point</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for data type '<em>Base Point</em>'.
   * @see org.eclipse.emf.cdo.lm.BasePoint
   * @model instanceClass="org.eclipse.emf.cdo.lm.BasePoint"
   *        serializeable="false"
   * @generated
   */
  EDataType getBasePoint();

  /**
   * Returns the factory that creates the instances of the model. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the factory that creates the instances of the model.
   * @generated
   */
  LMFactory getLMFactory();

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
     * The meta object literal for the '{@link org.eclipse.emf.cdo.lm.SystemElement <em>System Element</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.lm.SystemElement
     * @see org.eclipse.emf.cdo.lm.impl.LMPackageImpl#getSystemElement()
     * @generated
     */
    EClass SYSTEM_ELEMENT = eINSTANCE.getSystemElement();

    /**
     * The meta object literal for the '<em><b>Get System</b></em>' operation. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    EOperation SYSTEM_ELEMENT___GET_SYSTEM = eINSTANCE.getSystemElement__GetSystem();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.lm.ProcessElement <em>Process Element</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.lm.ProcessElement
     * @see org.eclipse.emf.cdo.lm.impl.LMPackageImpl#getProcessElement()
     * @generated
     */
    EClass PROCESS_ELEMENT = eINSTANCE.getProcessElement();

    /**
     * The meta object literal for the '<em><b>Get Process</b></em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EOperation PROCESS_ELEMENT___GET_PROCESS = eINSTANCE.getProcessElement__GetProcess();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.lm.ModuleElement <em>Module Element</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.lm.ModuleElement
     * @see org.eclipse.emf.cdo.lm.impl.LMPackageImpl#getModuleElement()
     * @generated
     */
    EClass MODULE_ELEMENT = eINSTANCE.getModuleElement();

    /**
     * The meta object literal for the '<em><b>Get Module</b></em>' operation. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    EOperation MODULE_ELEMENT___GET_MODULE = eINSTANCE.getModuleElement__GetModule();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.lm.StreamElement <em>Stream Element</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.lm.StreamElement
     * @see org.eclipse.emf.cdo.lm.impl.LMPackageImpl#getStreamElement()
     * @generated
     */
    EClass STREAM_ELEMENT = eINSTANCE.getStreamElement();

    /**
     * The meta object literal for the '<em><b>Get Stream</b></em>' operation. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    EOperation STREAM_ELEMENT___GET_STREAM = eINSTANCE.getStreamElement__GetStream();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.lm.impl.SystemImpl <em>System</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.lm.impl.SystemImpl
     * @see org.eclipse.emf.cdo.lm.impl.LMPackageImpl#getSystem()
     * @generated
     */
    EClass SYSTEM = eINSTANCE.getSystem();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EAttribute SYSTEM__NAME = eINSTANCE.getSystem_Name();

    /**
     * The meta object literal for the '<em><b>Process</b></em>' containment reference feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EReference SYSTEM__PROCESS = eINSTANCE.getSystem_Process();

    /**
     * The meta object literal for the '<em><b>Modules</b></em>' containment reference list feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EReference SYSTEM__MODULES = eINSTANCE.getSystem_Modules();

    /**
     * The meta object literal for the '<em><b>Get Module</b></em>' operation. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    EOperation SYSTEM___GET_MODULE__STRING = eINSTANCE.getSystem__GetModule__String();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.lm.impl.ProcessImpl <em>Process</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.lm.impl.ProcessImpl
     * @see org.eclipse.emf.cdo.lm.impl.LMPackageImpl#getProcess()
     * @generated
     */
    EClass PROCESS = eINSTANCE.getProcess();

    /**
     * The meta object literal for the '<em><b>System</b></em>' container reference feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EReference PROCESS__SYSTEM = eINSTANCE.getProcess_System();

    /**
     * The meta object literal for the '<em><b>Drop Types</b></em>' containment reference list feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EReference PROCESS__DROP_TYPES = eINSTANCE.getProcess_DropTypes();

    /**
     * The meta object literal for the '<em><b>Module Definition Path</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EAttribute PROCESS__MODULE_DEFINITION_PATH = eINSTANCE.getProcess_ModuleDefinitionPath();

    /**
     * The meta object literal for the '<em><b>Initial Module Version</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EAttribute PROCESS__INITIAL_MODULE_VERSION = eINSTANCE.getProcess_InitialModuleVersion();

    /**
     * The meta object literal for the '<em><b>Module Types</b></em>' containment reference list feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EReference PROCESS__MODULE_TYPES = eINSTANCE.getProcess_ModuleTypes();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.lm.impl.DropTypeImpl <em>Drop Type</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.lm.impl.DropTypeImpl
     * @see org.eclipse.emf.cdo.lm.impl.LMPackageImpl#getDropType()
     * @generated
     */
    EClass DROP_TYPE = eINSTANCE.getDropType();

    /**
     * The meta object literal for the '<em><b>Process</b></em>' container reference feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EReference DROP_TYPE__PROCESS = eINSTANCE.getDropType_Process();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EAttribute DROP_TYPE__NAME = eINSTANCE.getDropType_Name();

    /**
     * The meta object literal for the '<em><b>Release</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EAttribute DROP_TYPE__RELEASE = eINSTANCE.getDropType_Release();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.lm.impl.ModuleImpl <em>Module</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.lm.impl.ModuleImpl
     * @see org.eclipse.emf.cdo.lm.impl.LMPackageImpl#getModule()
     * @generated
     */
    EClass MODULE = eINSTANCE.getModule();

    /**
     * The meta object literal for the '<em><b>System</b></em>' container reference feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EReference MODULE__SYSTEM = eINSTANCE.getModule_System();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EAttribute MODULE__NAME = eINSTANCE.getModule_Name();

    /**
     * The meta object literal for the '<em><b>Type</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MODULE__TYPE = eINSTANCE.getModule_Type();

    /**
     * The meta object literal for the '<em><b>Streams</b></em>' containment reference list feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EReference MODULE__STREAMS = eINSTANCE.getModule_Streams();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.lm.impl.DependencyImpl <em>Dependency</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.lm.impl.DependencyImpl
     * @see org.eclipse.emf.cdo.lm.impl.LMPackageImpl#getDependency()
     * @generated
     */
    EClass DEPENDENCY = eINSTANCE.getDependency();

    /**
     * The meta object literal for the '<em><b>Target</b></em>' reference feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EReference DEPENDENCY__TARGET = eINSTANCE.getDependency_Target();

    /**
     * The meta object literal for the '<em><b>Version Range</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EAttribute DEPENDENCY__VERSION_RANGE = eINSTANCE.getDependency_VersionRange();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.lm.impl.ModuleTypeImpl <em>Module Type</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.lm.impl.ModuleTypeImpl
     * @see org.eclipse.emf.cdo.lm.impl.LMPackageImpl#getModuleType()
     * @generated
     */
    EClass MODULE_TYPE = eINSTANCE.getModuleType();

    /**
     * The meta object literal for the '<em><b>Process</b></em>' container reference feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EReference MODULE_TYPE__PROCESS = eINSTANCE.getModuleType_Process();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EAttribute MODULE_TYPE__NAME = eINSTANCE.getModuleType_Name();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.lm.impl.BaselineImpl <em>Baseline</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.lm.impl.BaselineImpl
     * @see org.eclipse.emf.cdo.lm.impl.LMPackageImpl#getBaseline()
     * @generated
     */
    EClass BASELINE = eINSTANCE.getBaseline();

    /**
     * The meta object literal for the '<em><b>Stream</b></em>' container reference feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EReference BASELINE__STREAM = eINSTANCE.getBaseline_Stream();

    /**
     * The meta object literal for the '<em><b>Floating</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EAttribute BASELINE__FLOATING = eINSTANCE.getBaseline_Floating();

    /**
     * The meta object literal for the '<em><b>Get Name</b></em>' operation. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    EOperation BASELINE___GET_NAME = eINSTANCE.getBaseline__GetName();

    /**
     * The meta object literal for the '<em><b>Get Branch Point</b></em>' operation.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EOperation BASELINE___GET_BRANCH_POINT = eINSTANCE.getBaseline__GetBranchPoint();

    /**
     * The meta object literal for the '<em><b>Get Base Time Stamp</b></em>' operation.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EOperation BASELINE___GET_BASE_TIME_STAMP = eINSTANCE.getBaseline__GetBaseTimeStamp();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.lm.impl.FloatingBaselineImpl <em>Floating Baseline</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.lm.impl.FloatingBaselineImpl
     * @see org.eclipse.emf.cdo.lm.impl.LMPackageImpl#getFloatingBaseline()
     * @generated
     */
    EClass FLOATING_BASELINE = eINSTANCE.getFloatingBaseline();

    /**
     * The meta object literal for the '<em><b>Closed</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EAttribute FLOATING_BASELINE__CLOSED = eINSTANCE.getFloatingBaseline_Closed();

    /**
     * The meta object literal for the '<em><b>Get Base</b></em>' operation. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    EOperation FLOATING_BASELINE___GET_BASE = eINSTANCE.getFloatingBaseline__GetBase();

    /**
     * The meta object literal for the '<em><b>Get Deliveries</b></em>' operation.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EOperation FLOATING_BASELINE___GET_DELIVERIES = eINSTANCE.getFloatingBaseline__GetDeliveries();

    /**
     * The meta object literal for the '<em><b>Get Branch</b></em>' operation. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    EOperation FLOATING_BASELINE___GET_BRANCH = eINSTANCE.getFloatingBaseline__GetBranch();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.lm.impl.FixedBaselineImpl <em>Fixed Baseline</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.lm.impl.FixedBaselineImpl
     * @see org.eclipse.emf.cdo.lm.impl.LMPackageImpl#getFixedBaseline()
     * @generated
     */
    EClass FIXED_BASELINE = eINSTANCE.getFixedBaseline();

    /**
     * The meta object literal for the '<em><b>Version</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EAttribute FIXED_BASELINE__VERSION = eINSTANCE.getFixedBaseline_Version();

    /**
     * The meta object literal for the '<em><b>Dependencies</b></em>' containment reference list feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EReference FIXED_BASELINE__DEPENDENCIES = eINSTANCE.getFixedBaseline_Dependencies();

    /**
     * The meta object literal for the '<em><b>Get Based Changes</b></em>' operation.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EOperation FIXED_BASELINE___GET_BASED_CHANGES = eINSTANCE.getFixedBaseline__GetBasedChanges();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.lm.impl.StreamImpl <em>Stream</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.lm.impl.StreamImpl
     * @see org.eclipse.emf.cdo.lm.impl.LMPackageImpl#getStream()
     * @generated
     */
    EClass STREAM = eINSTANCE.getStream();

    /**
     * The meta object literal for the '<em><b>Module</b></em>' container reference feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EReference STREAM__MODULE = eINSTANCE.getStream_Module();

    /**
     * The meta object literal for the '<em><b>Base</b></em>' reference feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EReference STREAM__BASE = eINSTANCE.getStream_Base();

    /**
     * The meta object literal for the '<em><b>Start Time Stamp</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EAttribute STREAM__START_TIME_STAMP = eINSTANCE.getStream_StartTimeStamp();

    /**
     * The meta object literal for the '<em><b>Major Version</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EAttribute STREAM__MAJOR_VERSION = eINSTANCE.getStream_MajorVersion();

    /**
     * The meta object literal for the '<em><b>Minor Version</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EAttribute STREAM__MINOR_VERSION = eINSTANCE.getStream_MinorVersion();

    /**
     * The meta object literal for the '<em><b>Code Name</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EAttribute STREAM__CODE_NAME = eINSTANCE.getStream_CodeName();

    /**
     * The meta object literal for the '<em><b>Allowed Changes</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EAttribute STREAM__ALLOWED_CHANGES = eINSTANCE.getStream_AllowedChanges();

    /**
     * The meta object literal for the '<em><b>Contents</b></em>' containment reference list feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EReference STREAM__CONTENTS = eINSTANCE.getStream_Contents();

    /**
     * The meta object literal for the '<em><b>Maintenance Time Stamp</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EAttribute STREAM__MAINTENANCE_TIME_STAMP = eINSTANCE.getStream_MaintenanceTimeStamp();

    /**
     * The meta object literal for the '<em><b>Insert Content</b></em>' operation.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EOperation STREAM___INSERT_CONTENT__BASELINE = eINSTANCE.getStream__InsertContent__Baseline();

    /**
     * The meta object literal for the '<em><b>Get Branch Point</b></em>' operation.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EOperation STREAM___GET_BRANCH_POINT__LONG = eINSTANCE.getStream__GetBranchPoint__long();

    /**
     * The meta object literal for the '<em><b>Mode</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EAttribute STREAM__MODE = eINSTANCE.getStream_Mode();

    /**
     * The meta object literal for the '<em><b>Development Branch</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EAttribute STREAM__DEVELOPMENT_BRANCH = eINSTANCE.getStream_DevelopmentBranch();

    /**
     * The meta object literal for the '<em><b>Maintenance Branch</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EAttribute STREAM__MAINTENANCE_BRANCH = eINSTANCE.getStream_MaintenanceBranch();

    /**
     * The meta object literal for the '<em><b>Get First Release</b></em>' operation.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EOperation STREAM___GET_FIRST_RELEASE = eINSTANCE.getStream__GetFirstRelease();

    /**
     * The meta object literal for the '<em><b>Get Last Release</b></em>' operation.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EOperation STREAM___GET_LAST_RELEASE = eINSTANCE.getStream__GetLastRelease();

    /**
     * The meta object literal for the '<em><b>Get Releases</b></em>' operation.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EOperation STREAM___GET_RELEASES = eINSTANCE.getStream__GetReleases();

    /**
     * The meta object literal for the '<em><b>Get Based Changes</b></em>' operation.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EOperation STREAM___GET_BASED_CHANGES = eINSTANCE.getStream__GetBasedChanges();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.lm.impl.ChangeImpl <em>Change</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.lm.impl.ChangeImpl
     * @see org.eclipse.emf.cdo.lm.impl.LMPackageImpl#getChange()
     * @generated
     */
    EClass CHANGE = eINSTANCE.getChange();

    /**
     * The meta object literal for the '<em><b>Base</b></em>' reference feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EReference CHANGE__BASE = eINSTANCE.getChange_Base();

    /**
     * The meta object literal for the '<em><b>Label</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EAttribute CHANGE__LABEL = eINSTANCE.getChange_Label();

    /**
     * The meta object literal for the '<em><b>Impact</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EAttribute CHANGE__IMPACT = eINSTANCE.getChange_Impact();

    /**
     * The meta object literal for the '<em><b>Branch</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EAttribute CHANGE__BRANCH = eINSTANCE.getChange_Branch();

    /**
     * The meta object literal for the '<em><b>Deliveries</b></em>' reference list feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EReference CHANGE__DELIVERIES = eINSTANCE.getChange_Deliveries();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.lm.impl.DeliveryImpl <em>Delivery</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.lm.impl.DeliveryImpl
     * @see org.eclipse.emf.cdo.lm.impl.LMPackageImpl#getDelivery()
     * @generated
     */
    EClass DELIVERY = eINSTANCE.getDelivery();

    /**
     * The meta object literal for the '<em><b>Change</b></em>' reference feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EReference DELIVERY__CHANGE = eINSTANCE.getDelivery_Change();

    /**
     * The meta object literal for the '<em><b>Merge Source</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EAttribute DELIVERY__MERGE_SOURCE = eINSTANCE.getDelivery_MergeSource();

    /**
     * The meta object literal for the '<em><b>Merge Target</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EAttribute DELIVERY__MERGE_TARGET = eINSTANCE.getDelivery_MergeTarget();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.lm.impl.DropImpl <em>Drop</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.lm.impl.DropImpl
     * @see org.eclipse.emf.cdo.lm.impl.LMPackageImpl#getDrop()
     * @generated
     */
    EClass DROP = eINSTANCE.getDrop();

    /**
     * The meta object literal for the '<em><b>Type</b></em>' reference feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EReference DROP__TYPE = eINSTANCE.getDrop_Type();

    /**
     * The meta object literal for the '<em><b>Label</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EAttribute DROP__LABEL = eINSTANCE.getDrop_Label();

    /**
     * The meta object literal for the '<em><b>Branch Point</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EAttribute DROP__BRANCH_POINT = eINSTANCE.getDrop_BranchPoint();

    /**
     * The meta object literal for the '<em><b>Is Release</b></em>' operation. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    EOperation DROP___IS_RELEASE = eINSTANCE.getDrop__IsRelease();

    /**
     * The meta object literal for the '<em><b>Get Based Streams</b></em>' operation.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EOperation DROP___GET_BASED_STREAMS = eINSTANCE.getDrop__GetBasedStreams();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.lm.Impact <em>Impact</em>}' enum.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.lm.Impact
     * @see org.eclipse.emf.cdo.lm.impl.LMPackageImpl#getImpact()
     * @generated
     */
    EEnum IMPACT = eINSTANCE.getImpact();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.lm.StreamMode <em>Stream Mode</em>}' enum.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.lm.StreamMode
     * @see org.eclipse.emf.cdo.lm.impl.LMPackageImpl#getStreamMode()
     * @generated
     */
    EEnum STREAM_MODE = eINSTANCE.getStreamMode();

    /**
     * The meta object literal for the '<em>Base Point</em>' data type. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @see org.eclipse.emf.cdo.lm.BasePoint
     * @see org.eclipse.emf.cdo.lm.impl.LMPackageImpl#getBasePoint()
     * @generated
     */
    EDataType BASE_POINT = eINSTANCE.getBasePoint();
  }

} // LMPackage
