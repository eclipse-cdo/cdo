/*
 * Copyright (c) 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.security;

import org.eclipse.emf.cdo.security.SecurityPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

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
 * @see org.eclipse.emf.cdo.lm.security.LMSecurityFactory
 * @model kind="package"
 * @generated
 */
public interface LMSecurityPackage extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "security";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://www.eclipse.org/emf/CDO/lm/security/1.0.0";

  /**
   * The package namespace name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "lmsecurity";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  LMSecurityPackage eINSTANCE = org.eclipse.emf.cdo.lm.security.impl.LMSecurityPackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.lm.security.impl.LMFilterImpl <em>LM Filter</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.lm.security.impl.LMFilterImpl
   * @see org.eclipse.emf.cdo.lm.security.impl.LMSecurityPackageImpl#getLMFilter()
   * @generated
   */
  int LM_FILTER = 0;

  /**
   * The feature id for the '<em><b>Regex</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LM_FILTER__REGEX = SecurityPackage.PERMISSION_FILTER_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>LM Filter</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LM_FILTER_FEATURE_COUNT = SecurityPackage.PERMISSION_FILTER_FEATURE_COUNT + 1;

  /**
   * The number of operations of the '<em>LM Filter</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LM_FILTER_OPERATION_COUNT = SecurityPackage.PERMISSION_FILTER_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.lm.security.impl.ModuleFilterImpl <em>Module Filter</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.lm.security.impl.ModuleFilterImpl
   * @see org.eclipse.emf.cdo.lm.security.impl.LMSecurityPackageImpl#getModuleFilter()
   * @generated
   */
  int MODULE_FILTER = 1;

  /**
   * The feature id for the '<em><b>Regex</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODULE_FILTER__REGEX = LM_FILTER__REGEX;

  /**
   * The feature id for the '<em><b>Module Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODULE_FILTER__MODULE_NAME = LM_FILTER_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Module Filter</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODULE_FILTER_FEATURE_COUNT = LM_FILTER_FEATURE_COUNT + 1;

  /**
   * The number of operations of the '<em>Module Filter</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODULE_FILTER_OPERATION_COUNT = LM_FILTER_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.lm.security.impl.ModuleTypeFilterImpl <em>Module Type Filter</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.lm.security.impl.ModuleTypeFilterImpl
   * @see org.eclipse.emf.cdo.lm.security.impl.LMSecurityPackageImpl#getModuleTypeFilter()
   * @generated
   */
  int MODULE_TYPE_FILTER = 2;

  /**
   * The feature id for the '<em><b>Regex</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODULE_TYPE_FILTER__REGEX = LM_FILTER__REGEX;

  /**
   * The feature id for the '<em><b>Module Type Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODULE_TYPE_FILTER__MODULE_TYPE_NAME = LM_FILTER_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Module Type Filter</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODULE_TYPE_FILTER_FEATURE_COUNT = LM_FILTER_FEATURE_COUNT + 1;

  /**
   * The number of operations of the '<em>Module Type Filter</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODULE_TYPE_FILTER_OPERATION_COUNT = LM_FILTER_OPERATION_COUNT + 0;

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.lm.security.LMFilter <em>LM Filter</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>LM Filter</em>'.
   * @see org.eclipse.emf.cdo.lm.security.LMFilter
   * @generated
   */
  EClass getLMFilter();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.security.LMFilter#isRegex <em>Regex</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Regex</em>'.
   * @see org.eclipse.emf.cdo.lm.security.LMFilter#isRegex()
   * @see #getLMFilter()
   * @generated
   */
  EAttribute getLMFilter_Regex();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.lm.security.ModuleFilter <em>Module Filter</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Module Filter</em>'.
   * @see org.eclipse.emf.cdo.lm.security.ModuleFilter
   * @generated
   */
  EClass getModuleFilter();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.security.ModuleFilter#getModuleName <em>Module Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Module Name</em>'.
   * @see org.eclipse.emf.cdo.lm.security.ModuleFilter#getModuleName()
   * @see #getModuleFilter()
   * @generated
   */
  EAttribute getModuleFilter_ModuleName();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.lm.security.ModuleTypeFilter <em>Module Type Filter</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Module Type Filter</em>'.
   * @see org.eclipse.emf.cdo.lm.security.ModuleTypeFilter
   * @generated
   */
  EClass getModuleTypeFilter();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.lm.security.ModuleTypeFilter#getModuleTypeName <em>Module Type Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Module Type Name</em>'.
   * @see org.eclipse.emf.cdo.lm.security.ModuleTypeFilter#getModuleTypeName()
   * @see #getModuleTypeFilter()
   * @generated
   */
  EAttribute getModuleTypeFilter_ModuleTypeName();

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  LMSecurityFactory getLMSecurityFactory();

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
     * The meta object literal for the '{@link org.eclipse.emf.cdo.lm.security.impl.LMFilterImpl <em>LM Filter</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.lm.security.impl.LMFilterImpl
     * @see org.eclipse.emf.cdo.lm.security.impl.LMSecurityPackageImpl#getLMFilter()
     * @generated
     */
    EClass LM_FILTER = eINSTANCE.getLMFilter();

    /**
     * The meta object literal for the '<em><b>Regex</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute LM_FILTER__REGEX = eINSTANCE.getLMFilter_Regex();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.lm.security.impl.ModuleFilterImpl <em>Module Filter</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.lm.security.impl.ModuleFilterImpl
     * @see org.eclipse.emf.cdo.lm.security.impl.LMSecurityPackageImpl#getModuleFilter()
     * @generated
     */
    EClass MODULE_FILTER = eINSTANCE.getModuleFilter();

    /**
     * The meta object literal for the '<em><b>Module Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MODULE_FILTER__MODULE_NAME = eINSTANCE.getModuleFilter_ModuleName();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.lm.security.impl.ModuleTypeFilterImpl <em>Module Type Filter</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.lm.security.impl.ModuleTypeFilterImpl
     * @see org.eclipse.emf.cdo.lm.security.impl.LMSecurityPackageImpl#getModuleTypeFilter()
     * @generated
     */
    EClass MODULE_TYPE_FILTER = eINSTANCE.getModuleTypeFilter();

    /**
     * The meta object literal for the '<em><b>Module Type Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MODULE_TYPE_FILTER__MODULE_TYPE_NAME = eINSTANCE.getModuleTypeFilter_ModuleTypeName();

  }

} // LMSecurityPackage
