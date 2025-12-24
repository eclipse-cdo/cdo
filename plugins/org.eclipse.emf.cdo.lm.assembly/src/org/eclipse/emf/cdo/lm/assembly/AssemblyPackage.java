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
package org.eclipse.emf.cdo.lm.assembly;

import org.eclipse.emf.cdo.etypes.EtypesPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
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
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.lm.assembly.AssemblyFactory
 * @model kind="package"
 * @generated
 */
public interface AssemblyPackage extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "assembly";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://www.eclipse.org/emf/CDO/lm/assembly/1.0.0";

  /**
   * The package namespace name.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "assembly";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   */
  AssemblyPackage eINSTANCE = org.eclipse.emf.cdo.lm.assembly.impl.AssemblyPackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.lm.assembly.impl.AssemblyImpl <em>Assembly</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.lm.assembly.impl.AssemblyImpl
   * @see org.eclipse.emf.cdo.lm.assembly.impl.AssemblyPackageImpl#getAssembly()
   * @generated
   */
  int ASSEMBLY = 0;

  /**
   * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ASSEMBLY__ANNOTATIONS = EtypesPackage.MODEL_ELEMENT__ANNOTATIONS;

  /**
   * The feature id for the '<em><b>System Name</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int ASSEMBLY__SYSTEM_NAME = EtypesPackage.MODEL_ELEMENT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Modules</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ASSEMBLY__MODULES = EtypesPackage.MODEL_ELEMENT_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Assembly</em>' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int ASSEMBLY_FEATURE_COUNT = EtypesPackage.MODEL_ELEMENT_FEATURE_COUNT + 2;

  /**
   * The operation id for the '<em>Get Annotation</em>' operation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int ASSEMBLY___GET_ANNOTATION__STRING = EtypesPackage.MODEL_ELEMENT___GET_ANNOTATION__STRING;

  /**
   * The number of operations of the '<em>Assembly</em>' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int ASSEMBLY_OPERATION_COUNT = EtypesPackage.MODEL_ELEMENT_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.lm.assembly.impl.AssemblyModuleImpl <em>Module</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.lm.assembly.impl.AssemblyModuleImpl
   * @see org.eclipse.emf.cdo.lm.assembly.impl.AssemblyPackageImpl#getAssemblyModule()
   * @generated
   */
  int ASSEMBLY_MODULE = 1;

  /**
   * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ASSEMBLY_MODULE__ANNOTATIONS = EtypesPackage.MODEL_ELEMENT__ANNOTATIONS;

  /**
   * The feature id for the '<em><b>Assembly</b></em>' container reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int ASSEMBLY_MODULE__ASSEMBLY = EtypesPackage.MODEL_ELEMENT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ASSEMBLY_MODULE__NAME = EtypesPackage.MODEL_ELEMENT_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Version</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int ASSEMBLY_MODULE__VERSION = EtypesPackage.MODEL_ELEMENT_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Branch Point</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int ASSEMBLY_MODULE__BRANCH_POINT = EtypesPackage.MODEL_ELEMENT_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Root</b></em>' attribute.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ASSEMBLY_MODULE__ROOT = EtypesPackage.MODEL_ELEMENT_FEATURE_COUNT + 4;

  /**
   * The number of structural features of the '<em>Module</em>' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int ASSEMBLY_MODULE_FEATURE_COUNT = EtypesPackage.MODEL_ELEMENT_FEATURE_COUNT + 5;

  /**
   * The operation id for the '<em>Get Annotation</em>' operation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int ASSEMBLY_MODULE___GET_ANNOTATION__STRING = EtypesPackage.MODEL_ELEMENT___GET_ANNOTATION__STRING;

  /**
   * The number of operations of the '<em>Module</em>' class.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ASSEMBLY_MODULE_OPERATION_COUNT = EtypesPackage.MODEL_ELEMENT_OPERATION_COUNT + 0;

  /**
   * Returns the meta object for class
   * '{@link org.eclipse.emf.cdo.lm.assembly.Assembly <em>Assembly</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Assembly</em>'.
   * @see org.eclipse.emf.cdo.lm.assembly.Assembly
   * @generated
   */
  EClass getAssembly();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.assembly.Assembly#getSystemName <em>System Name</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>System Name</em>'.
   * @see org.eclipse.emf.cdo.lm.assembly.Assembly#getSystemName()
   * @see #getAssembly()
   * @generated
   */
  EAttribute getAssembly_SystemName();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.lm.assembly.Assembly#getModules <em>Modules</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Modules</em>'.
   * @see org.eclipse.emf.cdo.lm.assembly.Assembly#getModules()
   * @see #getAssembly()
   * @generated
   */
  EReference getAssembly_Modules();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.lm.assembly.AssemblyModule <em>Module</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>Module</em>'.
   * @see org.eclipse.emf.cdo.lm.assembly.AssemblyModule
   * @generated
   */
  EClass getAssemblyModule();

  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.lm.assembly.AssemblyModule#getAssembly <em>Assembly</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Assembly</em>'.
   * @see org.eclipse.emf.cdo.lm.assembly.AssemblyModule#getAssembly()
   * @see #getAssemblyModule()
   * @generated
   */
  EReference getAssemblyModule_Assembly();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.assembly.AssemblyModule#getName <em>Name</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.lm.assembly.AssemblyModule#getName()
   * @see #getAssemblyModule()
   * @generated
   */
  EAttribute getAssemblyModule_Name();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.assembly.AssemblyModule#getVersion <em>Version</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Version</em>'.
   * @see org.eclipse.emf.cdo.lm.assembly.AssemblyModule#getVersion()
   * @see #getAssemblyModule()
   * @generated
   */
  EAttribute getAssemblyModule_Version();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.assembly.AssemblyModule#getBranchPoint <em>Branch Point</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Branch Point</em>'.
   * @see org.eclipse.emf.cdo.lm.assembly.AssemblyModule#getBranchPoint()
   * @see #getAssemblyModule()
   * @generated
   */
  EAttribute getAssemblyModule_BranchPoint();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.assembly.AssemblyModule#isRoot <em>Root</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Root</em>'.
   * @see org.eclipse.emf.cdo.lm.assembly.AssemblyModule#isRoot()
   * @see #getAssemblyModule()
   * @generated
   */
  EAttribute getAssemblyModule_Root();

  /**
   * Returns the factory that creates the instances of the model. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the factory that creates the instances of the model.
   * @generated
   */
  AssemblyFactory getAssemblyFactory();

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
     * The meta object literal for the '{@link org.eclipse.emf.cdo.lm.assembly.impl.AssemblyImpl <em>Assembly</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.lm.assembly.impl.AssemblyImpl
     * @see org.eclipse.emf.cdo.lm.assembly.impl.AssemblyPackageImpl#getAssembly()
     * @generated
     */
    EClass ASSEMBLY = eINSTANCE.getAssembly();

    /**
     * The meta object literal for the '<em><b>System Name</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EAttribute ASSEMBLY__SYSTEM_NAME = eINSTANCE.getAssembly_SystemName();

    /**
     * The meta object literal for the '<em><b>Modules</b></em>' containment reference list feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EReference ASSEMBLY__MODULES = eINSTANCE.getAssembly_Modules();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.lm.assembly.impl.AssemblyModuleImpl <em>Module</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.lm.assembly.impl.AssemblyModuleImpl
     * @see org.eclipse.emf.cdo.lm.assembly.impl.AssemblyPackageImpl#getAssemblyModule()
     * @generated
     */
    EClass ASSEMBLY_MODULE = eINSTANCE.getAssemblyModule();

    /**
     * The meta object literal for the '<em><b>Assembly</b></em>' container reference feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EReference ASSEMBLY_MODULE__ASSEMBLY = eINSTANCE.getAssemblyModule_Assembly();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EAttribute ASSEMBLY_MODULE__NAME = eINSTANCE.getAssemblyModule_Name();

    /**
     * The meta object literal for the '<em><b>Version</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EAttribute ASSEMBLY_MODULE__VERSION = eINSTANCE.getAssemblyModule_Version();

    /**
     * The meta object literal for the '<em><b>Branch Point</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EAttribute ASSEMBLY_MODULE__BRANCH_POINT = eINSTANCE.getAssemblyModule_BranchPoint();

    /**
     * The meta object literal for the '<em><b>Root</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EAttribute ASSEMBLY_MODULE__ROOT = eINSTANCE.getAssemblyModule_Root();

  }

} // AssemblyPackage
