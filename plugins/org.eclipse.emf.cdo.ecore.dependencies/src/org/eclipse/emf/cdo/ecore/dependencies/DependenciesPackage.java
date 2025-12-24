/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ecore.dependencies;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.ecore.dependencies.DependenciesFactory
 * @model kind="package"
 * @generated
 */
public interface DependenciesPackage extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "dependencies";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://www.eclipse.org/emf/CDO/dependencies/1.0.0";

  /**
   * The package namespace name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "dependencies";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  DependenciesPackage eINSTANCE = org.eclipse.emf.cdo.ecore.dependencies.impl.DependenciesPackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.ecore.dependencies.Addressable <em>Addressable</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.ecore.dependencies.Addressable
   * @see org.eclipse.emf.cdo.ecore.dependencies.impl.DependenciesPackageImpl#getAddressable()
   * @generated
   */
  int ADDRESSABLE = 0;

  /**
   * The feature id for the '<em><b>Uri</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ADDRESSABLE__URI = 0;

  /**
   * The number of structural features of the '<em>Addressable</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ADDRESSABLE_FEATURE_COUNT = 1;

  /**
   * The number of operations of the '<em>Addressable</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ADDRESSABLE_OPERATION_COUNT = 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.ecore.dependencies.impl.ModelContainerImpl <em>Model Container</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.ecore.dependencies.impl.ModelContainerImpl
   * @see org.eclipse.emf.cdo.ecore.dependencies.impl.DependenciesPackageImpl#getModelContainer()
   * @generated
   */
  int MODEL_CONTAINER = 1;

  /**
   * The feature id for the '<em><b>Models</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_CONTAINER__MODELS = 0;

  /**
   * The number of structural features of the '<em>Model Container</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_CONTAINER_FEATURE_COUNT = 1;

  /**
   * The operation id for the '<em>Get Model</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_CONTAINER___GET_MODEL__URI = 0;

  /**
   * The operation id for the '<em>Get Element</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_CONTAINER___GET_ELEMENT__URI = 1;

  /**
   * The number of operations of the '<em>Model Container</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_CONTAINER_OPERATION_COUNT = 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.ecore.dependencies.impl.ModelImpl <em>Model</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.ecore.dependencies.impl.ModelImpl
   * @see org.eclipse.emf.cdo.ecore.dependencies.impl.DependenciesPackageImpl#getModel()
   * @generated
   */
  int MODEL = 2;

  /**
   * The feature id for the '<em><b>Uri</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL__URI = ADDRESSABLE__URI;

  /**
   * The feature id for the '<em><b>Container</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL__CONTAINER = ADDRESSABLE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>File</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL__FILE = ADDRESSABLE_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Workspace</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL__WORKSPACE = ADDRESSABLE_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Exists</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL__EXISTS = ADDRESSABLE_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Ns URI</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL__NS_URI = ADDRESSABLE_FEATURE_COUNT + 4;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL__NAME = ADDRESSABLE_FEATURE_COUNT + 5;

  /**
   * The feature id for the '<em><b>Elements</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL__ELEMENTS = ADDRESSABLE_FEATURE_COUNT + 6;

  /**
   * The feature id for the '<em><b>Outgoing Links</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL__OUTGOING_LINKS = ADDRESSABLE_FEATURE_COUNT + 7;

  /**
   * The feature id for the '<em><b>Incoming Links</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL__INCOMING_LINKS = ADDRESSABLE_FEATURE_COUNT + 8;

  /**
   * The feature id for the '<em><b>Broken Links</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL__BROKEN_LINKS = ADDRESSABLE_FEATURE_COUNT + 9;

  /**
   * The feature id for the '<em><b>Dependencies</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL__DEPENDENCIES = ADDRESSABLE_FEATURE_COUNT + 10;

  /**
   * The feature id for the '<em><b>Depending Models</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL__DEPENDING_MODELS = ADDRESSABLE_FEATURE_COUNT + 11;

  /**
   * The feature id for the '<em><b>Flat Dependencies</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL__FLAT_DEPENDENCIES = ADDRESSABLE_FEATURE_COUNT + 12;

  /**
   * The feature id for the '<em><b>Flat Depending Models</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL__FLAT_DEPENDING_MODELS = ADDRESSABLE_FEATURE_COUNT + 13;

  /**
   * The number of structural features of the '<em>Model</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_FEATURE_COUNT = ADDRESSABLE_FEATURE_COUNT + 14;

  /**
   * The operation id for the '<em>Depends Upon</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL___DEPENDS_UPON__MODEL = ADDRESSABLE_OPERATION_COUNT + 0;

  /**
   * The operation id for the '<em>Add Dependency</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL___ADD_DEPENDENCY__MODEL = ADDRESSABLE_OPERATION_COUNT + 1;

  /**
   * The operation id for the '<em>Has Broken Links</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL___HAS_BROKEN_LINKS = ADDRESSABLE_OPERATION_COUNT + 2;

  /**
   * The operation id for the '<em>Get Element</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL___GET_ELEMENT__URI = ADDRESSABLE_OPERATION_COUNT + 3;

  /**
   * The number of operations of the '<em>Model</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_OPERATION_COUNT = ADDRESSABLE_OPERATION_COUNT + 4;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.ecore.dependencies.impl.ElementImpl <em>Element</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.ecore.dependencies.impl.ElementImpl
   * @see org.eclipse.emf.cdo.ecore.dependencies.impl.DependenciesPackageImpl#getElement()
   * @generated
   */
  int ELEMENT = 3;

  /**
   * The feature id for the '<em><b>Uri</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ELEMENT__URI = ADDRESSABLE__URI;

  /**
   * The feature id for the '<em><b>Model</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ELEMENT__MODEL = ADDRESSABLE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Exists</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ELEMENT__EXISTS = ADDRESSABLE_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Outgoing Links</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ELEMENT__OUTGOING_LINKS = ADDRESSABLE_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Incoming Links</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ELEMENT__INCOMING_LINKS = ADDRESSABLE_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Broken Links</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ELEMENT__BROKEN_LINKS = ADDRESSABLE_FEATURE_COUNT + 4;

  /**
   * The number of structural features of the '<em>Element</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ELEMENT_FEATURE_COUNT = ADDRESSABLE_FEATURE_COUNT + 5;

  /**
   * The operation id for the '<em>Has Broken Links</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ELEMENT___HAS_BROKEN_LINKS = ADDRESSABLE_OPERATION_COUNT + 0;

  /**
   * The number of operations of the '<em>Element</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ELEMENT_OPERATION_COUNT = ADDRESSABLE_OPERATION_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.ecore.dependencies.impl.LinkImpl <em>Link</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.ecore.dependencies.impl.LinkImpl
   * @see org.eclipse.emf.cdo.ecore.dependencies.impl.DependenciesPackageImpl#getLink()
   * @generated
   */
  int LINK = 4;

  /**
   * The feature id for the '<em><b>Uri</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LINK__URI = ADDRESSABLE__URI;

  /**
   * The feature id for the '<em><b>Source</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LINK__SOURCE = ADDRESSABLE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Target</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LINK__TARGET = ADDRESSABLE_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Reference</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LINK__REFERENCE = ADDRESSABLE_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Broken</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LINK__BROKEN = ADDRESSABLE_FEATURE_COUNT + 3;

  /**
   * The number of structural features of the '<em>Link</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LINK_FEATURE_COUNT = ADDRESSABLE_FEATURE_COUNT + 4;

  /**
   * The number of operations of the '<em>Link</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LINK_OPERATION_COUNT = ADDRESSABLE_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '<em>URI</em>' data type.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.common.util.URI
   * @see org.eclipse.emf.cdo.ecore.dependencies.impl.DependenciesPackageImpl#getURI()
   * @generated
   */
  int URI = 5;

  /**
   * The meta object id for the '<em>File</em>' data type.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.core.resources.IFile
   * @see org.eclipse.emf.cdo.ecore.dependencies.impl.DependenciesPackageImpl#getFile()
   * @generated
   */
  int FILE = 6;

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.ecore.dependencies.Addressable <em>Addressable</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Addressable</em>'.
   * @see org.eclipse.emf.cdo.ecore.dependencies.Addressable
   * @generated
   */
  EClass getAddressable();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.ecore.dependencies.Addressable#getUri <em>Uri</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Uri</em>'.
   * @see org.eclipse.emf.cdo.ecore.dependencies.Addressable#getUri()
   * @see #getAddressable()
   * @generated
   */
  EAttribute getAddressable_Uri();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.ecore.dependencies.ModelContainer <em>Model Container</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Model Container</em>'.
   * @see org.eclipse.emf.cdo.ecore.dependencies.ModelContainer
   * @generated
   */
  EClass getModelContainer();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.ecore.dependencies.ModelContainer#getModels <em>Models</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Models</em>'.
   * @see org.eclipse.emf.cdo.ecore.dependencies.ModelContainer#getModels()
   * @see #getModelContainer()
   * @generated
   */
  EReference getModelContainer_Models();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.ecore.dependencies.ModelContainer#getModel(org.eclipse.emf.common.util.URI) <em>Get Model</em>}' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the '<em>Get Model</em>' operation.
   * @see org.eclipse.emf.cdo.ecore.dependencies.ModelContainer#getModel(org.eclipse.emf.common.util.URI)
   * @generated
   */
  EOperation getModelContainer__GetModel__URI();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.ecore.dependencies.ModelContainer#getElement(org.eclipse.emf.common.util.URI) <em>Get Element</em>}' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the '<em>Get Element</em>' operation.
   * @see org.eclipse.emf.cdo.ecore.dependencies.ModelContainer#getElement(org.eclipse.emf.common.util.URI)
   * @generated
   */
  EOperation getModelContainer__GetElement__URI();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.ecore.dependencies.Model <em>Model</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Model</em>'.
   * @see org.eclipse.emf.cdo.ecore.dependencies.Model
   * @generated
   */
  EClass getModel();

  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.ecore.dependencies.Model#getContainer <em>Container</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Container</em>'.
   * @see org.eclipse.emf.cdo.ecore.dependencies.Model#getContainer()
   * @see #getModel()
   * @generated
   */
  EReference getModel_Container();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.ecore.dependencies.Model#getFile <em>File</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>File</em>'.
   * @see org.eclipse.emf.cdo.ecore.dependencies.Model#getFile()
   * @see #getModel()
   * @generated
   */
  EAttribute getModel_File();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.ecore.dependencies.Model#isWorkspace <em>Workspace</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Workspace</em>'.
   * @see org.eclipse.emf.cdo.ecore.dependencies.Model#isWorkspace()
   * @see #getModel()
   * @generated
   */
  EAttribute getModel_Workspace();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.ecore.dependencies.Model#isExists <em>Exists</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Exists</em>'.
   * @see org.eclipse.emf.cdo.ecore.dependencies.Model#isExists()
   * @see #getModel()
   * @generated
   */
  EAttribute getModel_Exists();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.ecore.dependencies.Model#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.ecore.dependencies.Model#getName()
   * @see #getModel()
   * @generated
   */
  EAttribute getModel_Name();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.ecore.dependencies.Model#getElements <em>Elements</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Elements</em>'.
   * @see org.eclipse.emf.cdo.ecore.dependencies.Model#getElements()
   * @see #getModel()
   * @generated
   */
  EReference getModel_Elements();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.ecore.dependencies.Model#getOutgoingLinks <em>Outgoing Links</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Outgoing Links</em>'.
   * @see org.eclipse.emf.cdo.ecore.dependencies.Model#getOutgoingLinks()
   * @see #getModel()
   * @generated
   */
  EReference getModel_OutgoingLinks();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.ecore.dependencies.Model#getIncomingLinks <em>Incoming Links</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Incoming Links</em>'.
   * @see org.eclipse.emf.cdo.ecore.dependencies.Model#getIncomingLinks()
   * @see #getModel()
   * @generated
   */
  EReference getModel_IncomingLinks();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.ecore.dependencies.Model#getBrokenLinks <em>Broken Links</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Broken Links</em>'.
   * @see org.eclipse.emf.cdo.ecore.dependencies.Model#getBrokenLinks()
   * @see #getModel()
   * @generated
   */
  EReference getModel_BrokenLinks();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.ecore.dependencies.Model#getNsURI <em>Ns URI</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Ns URI</em>'.
   * @see org.eclipse.emf.cdo.ecore.dependencies.Model#getNsURI()
   * @see #getModel()
   * @generated
   */
  EAttribute getModel_NsURI();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.ecore.dependencies.Model#getDependencies <em>Dependencies</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Dependencies</em>'.
   * @see org.eclipse.emf.cdo.ecore.dependencies.Model#getDependencies()
   * @see #getModel()
   * @generated
   */
  EReference getModel_Dependencies();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.ecore.dependencies.Model#getDependingModels <em>Depending Models</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Depending Models</em>'.
   * @see org.eclipse.emf.cdo.ecore.dependencies.Model#getDependingModels()
   * @see #getModel()
   * @generated
   */
  EReference getModel_DependingModels();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.ecore.dependencies.Model#getFlatDependencies <em>Flat Dependencies</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Flat Dependencies</em>'.
   * @see org.eclipse.emf.cdo.ecore.dependencies.Model#getFlatDependencies()
   * @see #getModel()
   * @generated
   */
  EReference getModel_FlatDependencies();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.ecore.dependencies.Model#getFlatDependingModels <em>Flat Depending Models</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Flat Depending Models</em>'.
   * @see org.eclipse.emf.cdo.ecore.dependencies.Model#getFlatDependingModels()
   * @see #getModel()
   * @generated
   */
  EReference getModel_FlatDependingModels();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.ecore.dependencies.Model#dependsUpon(org.eclipse.emf.cdo.ecore.dependencies.Model) <em>Depends Upon</em>}' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the '<em>Depends Upon</em>' operation.
   * @see org.eclipse.emf.cdo.ecore.dependencies.Model#dependsUpon(org.eclipse.emf.cdo.ecore.dependencies.Model)
   * @generated
   */
  EOperation getModel__DependsUpon__Model();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.ecore.dependencies.Model#addDependency(org.eclipse.emf.cdo.ecore.dependencies.Model) <em>Add Dependency</em>}' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the '<em>Add Dependency</em>' operation.
   * @see org.eclipse.emf.cdo.ecore.dependencies.Model#addDependency(org.eclipse.emf.cdo.ecore.dependencies.Model)
   * @generated
   */
  EOperation getModel__AddDependency__Model();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.ecore.dependencies.Model#hasBrokenLinks() <em>Has Broken Links</em>}' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the '<em>Has Broken Links</em>' operation.
   * @see org.eclipse.emf.cdo.ecore.dependencies.Model#hasBrokenLinks()
   * @generated
   */
  EOperation getModel__HasBrokenLinks();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.ecore.dependencies.Model#getElement(org.eclipse.emf.common.util.URI) <em>Get Element</em>}' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the '<em>Get Element</em>' operation.
   * @see org.eclipse.emf.cdo.ecore.dependencies.Model#getElement(org.eclipse.emf.common.util.URI)
   * @generated
   */
  EOperation getModel__GetElement__URI();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.ecore.dependencies.Element <em>Element</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Element</em>'.
   * @see org.eclipse.emf.cdo.ecore.dependencies.Element
   * @generated
   */
  EClass getElement();

  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.ecore.dependencies.Element#getModel <em>Model</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Model</em>'.
   * @see org.eclipse.emf.cdo.ecore.dependencies.Element#getModel()
   * @see #getElement()
   * @generated
   */
  EReference getElement_Model();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.ecore.dependencies.Element#isExists <em>Exists</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Exists</em>'.
   * @see org.eclipse.emf.cdo.ecore.dependencies.Element#isExists()
   * @see #getElement()
   * @generated
   */
  EAttribute getElement_Exists();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.ecore.dependencies.Element#getOutgoingLinks <em>Outgoing Links</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Outgoing Links</em>'.
   * @see org.eclipse.emf.cdo.ecore.dependencies.Element#getOutgoingLinks()
   * @see #getElement()
   * @generated
   */
  EReference getElement_OutgoingLinks();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.ecore.dependencies.Element#getIncomingLinks <em>Incoming Links</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Incoming Links</em>'.
   * @see org.eclipse.emf.cdo.ecore.dependencies.Element#getIncomingLinks()
   * @see #getElement()
   * @generated
   */
  EReference getElement_IncomingLinks();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.ecore.dependencies.Element#getBrokenLinks <em>Broken Links</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Broken Links</em>'.
   * @see org.eclipse.emf.cdo.ecore.dependencies.Element#getBrokenLinks()
   * @see #getElement()
   * @generated
   */
  EReference getElement_BrokenLinks();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.ecore.dependencies.Element#hasBrokenLinks() <em>Has Broken Links</em>}' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the '<em>Has Broken Links</em>' operation.
   * @see org.eclipse.emf.cdo.ecore.dependencies.Element#hasBrokenLinks()
   * @generated
   */
  EOperation getElement__HasBrokenLinks();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.ecore.dependencies.Link <em>Link</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Link</em>'.
   * @see org.eclipse.emf.cdo.ecore.dependencies.Link
   * @generated
   */
  EClass getLink();

  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.ecore.dependencies.Link#getSource <em>Source</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Source</em>'.
   * @see org.eclipse.emf.cdo.ecore.dependencies.Link#getSource()
   * @see #getLink()
   * @generated
   */
  EReference getLink_Source();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.ecore.dependencies.Link#getTarget <em>Target</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Target</em>'.
   * @see org.eclipse.emf.cdo.ecore.dependencies.Link#getTarget()
   * @see #getLink()
   * @generated
   */
  EReference getLink_Target();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.ecore.dependencies.Link#getReference <em>Reference</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Reference</em>'.
   * @see org.eclipse.emf.cdo.ecore.dependencies.Link#getReference()
   * @see #getLink()
   * @generated
   */
  EReference getLink_Reference();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.ecore.dependencies.Link#isBroken <em>Broken</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Broken</em>'.
   * @see org.eclipse.emf.cdo.ecore.dependencies.Link#isBroken()
   * @see #getLink()
   * @generated
   */
  EAttribute getLink_Broken();

  /**
   * Returns the meta object for data type '{@link org.eclipse.emf.common.util.URI <em>URI</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for data type '<em>URI</em>'.
   * @see org.eclipse.emf.common.util.URI
   * @model instanceClass="org.eclipse.emf.common.util.URI"
   * @generated
   */
  EDataType getURI();

  /**
   * Returns the meta object for data type '{@link org.eclipse.core.resources.IFile <em>File</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for data type '<em>File</em>'.
   * @see org.eclipse.core.resources.IFile
   * @model instanceClass="org.eclipse.core.resources.IFile" serializeable="false"
   * @generated
   */
  EDataType getFile();

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  DependenciesFactory getDependenciesFactory();

  /**
   * <!-- begin-user-doc -->
   * Defines literals for the meta objects that represent
   * <ul>
   *   <li>each class,</li>
   *   <li>each feature of each class,</li>
   *   <li>each operation of each class,</li>
   *   <li>each enum,</li>
   *   <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
   * @generated
   */
  interface Literals
  {
    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.ecore.dependencies.Addressable <em>Addressable</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.ecore.dependencies.Addressable
     * @see org.eclipse.emf.cdo.ecore.dependencies.impl.DependenciesPackageImpl#getAddressable()
     * @generated
     */
    EClass ADDRESSABLE = eINSTANCE.getAddressable();

    /**
     * The meta object literal for the '<em><b>Uri</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ADDRESSABLE__URI = eINSTANCE.getAddressable_Uri();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.ecore.dependencies.impl.ModelContainerImpl <em>Model Container</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.ecore.dependencies.impl.ModelContainerImpl
     * @see org.eclipse.emf.cdo.ecore.dependencies.impl.DependenciesPackageImpl#getModelContainer()
     * @generated
     */
    EClass MODEL_CONTAINER = eINSTANCE.getModelContainer();

    /**
     * The meta object literal for the '<em><b>Models</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MODEL_CONTAINER__MODELS = eINSTANCE.getModelContainer_Models();

    /**
     * The meta object literal for the '<em><b>Get Model</b></em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EOperation MODEL_CONTAINER___GET_MODEL__URI = eINSTANCE.getModelContainer__GetModel__URI();

    /**
     * The meta object literal for the '<em><b>Get Element</b></em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EOperation MODEL_CONTAINER___GET_ELEMENT__URI = eINSTANCE.getModelContainer__GetElement__URI();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.ecore.dependencies.impl.ModelImpl <em>Model</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.ecore.dependencies.impl.ModelImpl
     * @see org.eclipse.emf.cdo.ecore.dependencies.impl.DependenciesPackageImpl#getModel()
     * @generated
     */
    EClass MODEL = eINSTANCE.getModel();

    /**
     * The meta object literal for the '<em><b>Container</b></em>' container reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MODEL__CONTAINER = eINSTANCE.getModel_Container();

    /**
     * The meta object literal for the '<em><b>File</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MODEL__FILE = eINSTANCE.getModel_File();

    /**
     * The meta object literal for the '<em><b>Workspace</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MODEL__WORKSPACE = eINSTANCE.getModel_Workspace();

    /**
     * The meta object literal for the '<em><b>Exists</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MODEL__EXISTS = eINSTANCE.getModel_Exists();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MODEL__NAME = eINSTANCE.getModel_Name();

    /**
     * The meta object literal for the '<em><b>Elements</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MODEL__ELEMENTS = eINSTANCE.getModel_Elements();

    /**
     * The meta object literal for the '<em><b>Outgoing Links</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MODEL__OUTGOING_LINKS = eINSTANCE.getModel_OutgoingLinks();

    /**
     * The meta object literal for the '<em><b>Incoming Links</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MODEL__INCOMING_LINKS = eINSTANCE.getModel_IncomingLinks();

    /**
     * The meta object literal for the '<em><b>Broken Links</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MODEL__BROKEN_LINKS = eINSTANCE.getModel_BrokenLinks();

    /**
     * The meta object literal for the '<em><b>Ns URI</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MODEL__NS_URI = eINSTANCE.getModel_NsURI();

    /**
     * The meta object literal for the '<em><b>Dependencies</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MODEL__DEPENDENCIES = eINSTANCE.getModel_Dependencies();

    /**
     * The meta object literal for the '<em><b>Depending Models</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MODEL__DEPENDING_MODELS = eINSTANCE.getModel_DependingModels();

    /**
     * The meta object literal for the '<em><b>Flat Dependencies</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MODEL__FLAT_DEPENDENCIES = eINSTANCE.getModel_FlatDependencies();

    /**
     * The meta object literal for the '<em><b>Flat Depending Models</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MODEL__FLAT_DEPENDING_MODELS = eINSTANCE.getModel_FlatDependingModels();

    /**
     * The meta object literal for the '<em><b>Depends Upon</b></em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EOperation MODEL___DEPENDS_UPON__MODEL = eINSTANCE.getModel__DependsUpon__Model();

    /**
     * The meta object literal for the '<em><b>Add Dependency</b></em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EOperation MODEL___ADD_DEPENDENCY__MODEL = eINSTANCE.getModel__AddDependency__Model();

    /**
     * The meta object literal for the '<em><b>Has Broken Links</b></em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EOperation MODEL___HAS_BROKEN_LINKS = eINSTANCE.getModel__HasBrokenLinks();

    /**
     * The meta object literal for the '<em><b>Get Element</b></em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EOperation MODEL___GET_ELEMENT__URI = eINSTANCE.getModel__GetElement__URI();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.ecore.dependencies.impl.ElementImpl <em>Element</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.ecore.dependencies.impl.ElementImpl
     * @see org.eclipse.emf.cdo.ecore.dependencies.impl.DependenciesPackageImpl#getElement()
     * @generated
     */
    EClass ELEMENT = eINSTANCE.getElement();

    /**
     * The meta object literal for the '<em><b>Model</b></em>' container reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ELEMENT__MODEL = eINSTANCE.getElement_Model();

    /**
     * The meta object literal for the '<em><b>Exists</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ELEMENT__EXISTS = eINSTANCE.getElement_Exists();

    /**
     * The meta object literal for the '<em><b>Outgoing Links</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ELEMENT__OUTGOING_LINKS = eINSTANCE.getElement_OutgoingLinks();

    /**
     * The meta object literal for the '<em><b>Incoming Links</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ELEMENT__INCOMING_LINKS = eINSTANCE.getElement_IncomingLinks();

    /**
     * The meta object literal for the '<em><b>Broken Links</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ELEMENT__BROKEN_LINKS = eINSTANCE.getElement_BrokenLinks();

    /**
     * The meta object literal for the '<em><b>Has Broken Links</b></em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EOperation ELEMENT___HAS_BROKEN_LINKS = eINSTANCE.getElement__HasBrokenLinks();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.ecore.dependencies.impl.LinkImpl <em>Link</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.ecore.dependencies.impl.LinkImpl
     * @see org.eclipse.emf.cdo.ecore.dependencies.impl.DependenciesPackageImpl#getLink()
     * @generated
     */
    EClass LINK = eINSTANCE.getLink();

    /**
     * The meta object literal for the '<em><b>Source</b></em>' container reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference LINK__SOURCE = eINSTANCE.getLink_Source();

    /**
     * The meta object literal for the '<em><b>Target</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference LINK__TARGET = eINSTANCE.getLink_Target();

    /**
     * The meta object literal for the '<em><b>Reference</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference LINK__REFERENCE = eINSTANCE.getLink_Reference();

    /**
     * The meta object literal for the '<em><b>Broken</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute LINK__BROKEN = eINSTANCE.getLink_Broken();

    /**
     * The meta object literal for the '<em>URI</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.common.util.URI
     * @see org.eclipse.emf.cdo.ecore.dependencies.impl.DependenciesPackageImpl#getURI()
     * @generated
     */
    EDataType URI = eINSTANCE.getURI();

    /**
     * The meta object literal for the '<em>File</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.core.resources.IFile
     * @see org.eclipse.emf.cdo.ecore.dependencies.impl.DependenciesPackageImpl#getFile()
     * @generated
     */
    EDataType FILE = eINSTANCE.getFile();

  }

} // DependenciesPackage
