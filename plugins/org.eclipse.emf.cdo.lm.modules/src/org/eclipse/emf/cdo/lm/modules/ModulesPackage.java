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
package org.eclipse.emf.cdo.lm.modules;

import org.eclipse.emf.cdo.etypes.EtypesPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc --> The <b>Package</b> for the model. It contains
 * accessors for the meta objects to represent
 * <ul>
 * <li>each class,</li>
 * <li>each feature of each class,</li>
 * <li>each operation of each class,</li>
 * <li>each enum,</li>
 * <li>and each data type</li>
 * </ul>
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.lm.modules.ModulesFactory
 * @model kind="package"
 * @generated
 */
public interface ModulesPackage extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "modules";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://www.eclipse.org/emf/CDO/lm/modules/1.0.0";

  /**
   * The package namespace name.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "modules";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   */
  ModulesPackage eINSTANCE = org.eclipse.emf.cdo.lm.modules.impl.ModulesPackageImpl.init();

  /**
   * The meta object id for the
   * '{@link org.eclipse.emf.cdo.lm.modules.impl.ModuleDefinitionImpl
   * <em>Module Definition</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @see org.eclipse.emf.cdo.lm.modules.impl.ModuleDefinitionImpl
   * @see org.eclipse.emf.cdo.lm.modules.impl.ModulesPackageImpl#getModuleDefinition()
   * @generated
   */
  int MODULE_DEFINITION = 0;

  /**
   * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODULE_DEFINITION__ANNOTATIONS = EtypesPackage.MODEL_ELEMENT__ANNOTATIONS;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODULE_DEFINITION__NAME = EtypesPackage.MODEL_ELEMENT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Version</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int MODULE_DEFINITION__VERSION = EtypesPackage.MODEL_ELEMENT_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Dependencies</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODULE_DEFINITION__DEPENDENCIES = EtypesPackage.MODEL_ELEMENT_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Module Definition</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODULE_DEFINITION_FEATURE_COUNT = EtypesPackage.MODEL_ELEMENT_FEATURE_COUNT + 3;

  /**
   * The operation id for the '<em>Get Annotation</em>' operation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int MODULE_DEFINITION___GET_ANNOTATION__STRING = EtypesPackage.MODEL_ELEMENT___GET_ANNOTATION__STRING;

  /**
   * The number of operations of the '<em>Module Definition</em>' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int MODULE_DEFINITION_OPERATION_COUNT = EtypesPackage.MODEL_ELEMENT_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.lm.modules.impl.DependencyDefinitionImpl <em>Dependency Definition</em>}' class.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see org.eclipse.emf.cdo.lm.modules.impl.DependencyDefinitionImpl
   * @see org.eclipse.emf.cdo.lm.modules.impl.ModulesPackageImpl#getDependencyDefinition()
   * @generated
   */
  int DEPENDENCY_DEFINITION = 1;

  /**
   * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEPENDENCY_DEFINITION__ANNOTATIONS = EtypesPackage.MODEL_ELEMENT__ANNOTATIONS;

  /**
   * The feature id for the '<em><b>Source</b></em>' container reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DEPENDENCY_DEFINITION__SOURCE = EtypesPackage.MODEL_ELEMENT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Target Name</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DEPENDENCY_DEFINITION__TARGET_NAME = EtypesPackage.MODEL_ELEMENT_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Version Range</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DEPENDENCY_DEFINITION__VERSION_RANGE = EtypesPackage.MODEL_ELEMENT_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Dependency Definition</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEPENDENCY_DEFINITION_FEATURE_COUNT = EtypesPackage.MODEL_ELEMENT_FEATURE_COUNT + 3;

  /**
   * The operation id for the '<em>Get Annotation</em>' operation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DEPENDENCY_DEFINITION___GET_ANNOTATION__STRING = EtypesPackage.MODEL_ELEMENT___GET_ANNOTATION__STRING;

  /**
   * The number of operations of the '<em>Dependency Definition</em>' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DEPENDENCY_DEFINITION_OPERATION_COUNT = EtypesPackage.MODEL_ELEMENT_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '<em>Version</em>' data type.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @see org.eclipse.equinox.p2.metadata.Version
   * @see org.eclipse.emf.cdo.lm.modules.impl.ModulesPackageImpl#getVersion()
   * @generated
   */
  int VERSION = 2;

  /**
   * The meta object id for the '<em>Version Range</em>' data type. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.eclipse.equinox.p2.metadata.VersionRange
   * @see org.eclipse.emf.cdo.lm.modules.impl.ModulesPackageImpl#getVersionRange()
   * @generated
   */
  int VERSION_RANGE = 3;

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.lm.modules.ModuleDefinition <em>Module Definition</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>Module Definition</em>'.
   * @see org.eclipse.emf.cdo.lm.modules.ModuleDefinition
   * @generated
   */
  EClass getModuleDefinition();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.modules.ModuleDefinition#getName <em>Name</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.lm.modules.ModuleDefinition#getName()
   * @see #getModuleDefinition()
   * @generated
   */
  EAttribute getModuleDefinition_Name();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.modules.ModuleDefinition#getVersion <em>Version</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Version</em>'.
   * @see org.eclipse.emf.cdo.lm.modules.ModuleDefinition#getVersion()
   * @see #getModuleDefinition()
   * @generated
   */
  EAttribute getModuleDefinition_Version();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.lm.modules.ModuleDefinition#getDependencies <em>Dependencies</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Dependencies</em>'.
   * @see org.eclipse.emf.cdo.lm.modules.ModuleDefinition#getDependencies()
   * @see #getModuleDefinition()
   * @generated
   */
  EReference getModuleDefinition_Dependencies();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.lm.modules.DependencyDefinition <em>Dependency Definition</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>Dependency Definition</em>'.
   * @see org.eclipse.emf.cdo.lm.modules.DependencyDefinition
   * @generated
   */
  EClass getDependencyDefinition();

  /**
   * Returns the meta object for the container reference '{@link org.eclipse.emf.cdo.lm.modules.DependencyDefinition#getSource <em>Source</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Source</em>'.
   * @see org.eclipse.emf.cdo.lm.modules.DependencyDefinition#getSource()
   * @see #getDependencyDefinition()
   * @generated
   */
  EReference getDependencyDefinition_Source();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.modules.DependencyDefinition#getTargetName <em>Target Name</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Target Name</em>'.
   * @see org.eclipse.emf.cdo.lm.modules.DependencyDefinition#getTargetName()
   * @see #getDependencyDefinition()
   * @generated
   */
  EAttribute getDependencyDefinition_TargetName();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.modules.DependencyDefinition#getVersionRange <em>Version Range</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Version Range</em>'.
   * @see org.eclipse.emf.cdo.lm.modules.DependencyDefinition#getVersionRange()
   * @see #getDependencyDefinition()
   * @generated
   */
  EAttribute getDependencyDefinition_VersionRange();

  /**
   * Returns the meta object for data type
   * '{@link org.eclipse.equinox.p2.metadata.Version <em>Version</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for data type '<em>Version</em>'.
   * @see org.eclipse.equinox.p2.metadata.Version
   * @model instanceClass="org.eclipse.equinox.p2.metadata.Version"
   * @generated
   */
  EDataType getVersion();

  /**
   * Returns the meta object for data type '{@link org.eclipse.equinox.p2.metadata.VersionRange <em>Version Range</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for data type '<em>Version Range</em>'.
   * @see org.eclipse.equinox.p2.metadata.VersionRange
   * @model instanceClass="org.eclipse.equinox.p2.metadata.VersionRange"
   * @generated
   */
  EDataType getVersionRange();

  /**
   * Returns the factory that creates the instances of the model. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the factory that creates the instances of the model.
   * @generated
   */
  ModulesFactory getModulesFactory();

  /**
   * <!-- begin-user-doc --> Defines literals for the meta objects that represent
   * <ul>
   * <li>each class,</li>
   * <li>each feature of each class,</li>
   * <li>each operation of each class,</li>
   * <li>each enum,</li>
   * <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
   * @generated
   */
  interface Literals
  {
    /**
     * The meta object literal for the
     * '{@link org.eclipse.emf.cdo.lm.modules.impl.ModuleDefinitionImpl
     * <em>Module Definition</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     *
     * @see org.eclipse.emf.cdo.lm.modules.impl.ModuleDefinitionImpl
     * @see org.eclipse.emf.cdo.lm.modules.impl.ModulesPackageImpl#getModuleDefinition()
     * @generated
     */
    EClass MODULE_DEFINITION = eINSTANCE.getModuleDefinition();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EAttribute MODULE_DEFINITION__NAME = eINSTANCE.getModuleDefinition_Name();

    /**
     * The meta object literal for the '<em><b>Version</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EAttribute MODULE_DEFINITION__VERSION = eINSTANCE.getModuleDefinition_Version();

    /**
     * The meta object literal for the '<em><b>Dependencies</b></em>' containment reference list feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EReference MODULE_DEFINITION__DEPENDENCIES = eINSTANCE.getModuleDefinition_Dependencies();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.lm.modules.impl.DependencyDefinitionImpl <em>Dependency Definition</em>}' class.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @see org.eclipse.emf.cdo.lm.modules.impl.DependencyDefinitionImpl
     * @see org.eclipse.emf.cdo.lm.modules.impl.ModulesPackageImpl#getDependencyDefinition()
     * @generated
     */
    EClass DEPENDENCY_DEFINITION = eINSTANCE.getDependencyDefinition();

    /**
     * The meta object literal for the '<em><b>Source</b></em>' container reference feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EReference DEPENDENCY_DEFINITION__SOURCE = eINSTANCE.getDependencyDefinition_Source();

    /**
     * The meta object literal for the '<em><b>Target Name</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EAttribute DEPENDENCY_DEFINITION__TARGET_NAME = eINSTANCE.getDependencyDefinition_TargetName();

    /**
     * The meta object literal for the '<em><b>Version Range</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    EAttribute DEPENDENCY_DEFINITION__VERSION_RANGE = eINSTANCE.getDependencyDefinition_VersionRange();

    /**
     * The meta object literal for the '<em>Version</em>' data type. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @see org.eclipse.equinox.p2.metadata.Version
     * @see org.eclipse.emf.cdo.lm.modules.impl.ModulesPackageImpl#getVersion()
     * @generated
     */
    EDataType VERSION = eINSTANCE.getVersion();

    /**
     * The meta object literal for the '<em>Version Range</em>' data type. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @see org.eclipse.equinox.p2.metadata.VersionRange
     * @see org.eclipse.emf.cdo.lm.modules.impl.ModulesPackageImpl#getVersionRange()
     * @generated
     */
    EDataType VERSION_RANGE = eINSTANCE.getVersionRange();
  }

} // ModulesPackage
