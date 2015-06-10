/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.ui.defs;

import org.eclipse.net4j.util.defs.Net4jUtilDefsPackage;

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
 * @see org.eclipse.emf.cdo.ui.defs.CDOUIDefsFactory
 * @model kind="package"
 * @generated
 */
public interface CDOUIDefsPackage extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "defs";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://www.eclipse.org/emf/CDO/ui/defs/1.0.0";

  /**
   * The package namespace name.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "cdo.ui.defs";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  CDOUIDefsPackage eINSTANCE = org.eclipse.emf.cdo.ui.defs.impl.CDOUIDefsPackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.ui.defs.impl.EditorDefImpl <em>Editor Def</em>}' class. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.eclipse.emf.cdo.ui.defs.impl.EditorDefImpl
   * @see org.eclipse.emf.cdo.ui.defs.impl.CDOUIDefsPackageImpl#getEditorDef()
   * @generated
   */
  int EDITOR_DEF = 0;

  /**
   * The feature id for the '<em><b>Editor ID</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EDITOR_DEF__EDITOR_ID = Net4jUtilDefsPackage.DEF_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Editor Def</em>' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EDITOR_DEF_FEATURE_COUNT = Net4jUtilDefsPackage.DEF_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.ui.defs.impl.CDOEditorDefImpl <em>CDO Editor Def</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.ui.defs.impl.CDOEditorDefImpl
   * @see org.eclipse.emf.cdo.ui.defs.impl.CDOUIDefsPackageImpl#getCDOEditorDef()
   * @generated
   */
  int CDO_EDITOR_DEF = 1;

  /**
   * The feature id for the '<em><b>Editor ID</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CDO_EDITOR_DEF__EDITOR_ID = EDITOR_DEF__EDITOR_ID;

  /**
   * The feature id for the '<em><b>Cdo View</b></em>' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CDO_EDITOR_DEF__CDO_VIEW = EDITOR_DEF_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Resource Path</b></em>' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CDO_EDITOR_DEF__RESOURCE_PATH = EDITOR_DEF_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>CDO Editor Def</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   * @ordered
   */
  int CDO_EDITOR_DEF_FEATURE_COUNT = EDITOR_DEF_FEATURE_COUNT + 2;

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.ui.defs.EditorDef <em>Editor Def</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Editor Def</em>'.
   * @see org.eclipse.emf.cdo.ui.defs.EditorDef
   * @generated
   */
  EClass getEditorDef();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.ui.defs.EditorDef#getEditorID <em>Editor ID</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Editor ID</em>'.
   * @see org.eclipse.emf.cdo.ui.defs.EditorDef#getEditorID()
   * @see #getEditorDef()
   * @generated
   */
  EAttribute getEditorDef_EditorID();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.ui.defs.CDOEditorDef <em>CDO Editor Def</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>CDO Editor Def</em>'.
   * @see org.eclipse.emf.cdo.ui.defs.CDOEditorDef
   * @generated
   */
  EClass getCDOEditorDef();

  /**
   * Returns the meta object for the reference '{@link org.eclipse.emf.cdo.ui.defs.CDOEditorDef#getCdoView <em>Cdo View</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Cdo View</em>'.
   * @see org.eclipse.emf.cdo.ui.defs.CDOEditorDef#getCdoView()
   * @see #getCDOEditorDef()
   * @generated
   */
  EReference getCDOEditorDef_CdoView();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.ui.defs.CDOEditorDef#getResourcePath <em>Resource Path</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Resource Path</em>'.
   * @see org.eclipse.emf.cdo.ui.defs.CDOEditorDef#getResourcePath()
   * @see #getCDOEditorDef()
   * @generated
   */
  EAttribute getCDOEditorDef_ResourcePath();

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  CDOUIDefsFactory getCDOUIDefsFactory();

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
     * The meta object literal for the '{@link org.eclipse.emf.cdo.ui.defs.impl.EditorDefImpl <em>Editor Def</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.ui.defs.impl.EditorDefImpl
     * @see org.eclipse.emf.cdo.ui.defs.impl.CDOUIDefsPackageImpl#getEditorDef()
     * @generated
     */
    EClass EDITOR_DEF = eINSTANCE.getEditorDef();

    /**
     * The meta object literal for the '<em><b>Editor ID</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    EAttribute EDITOR_DEF__EDITOR_ID = eINSTANCE.getEditorDef_EditorID();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.ui.defs.impl.CDOEditorDefImpl <em>CDO Editor Def</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.ui.defs.impl.CDOEditorDefImpl
     * @see org.eclipse.emf.cdo.ui.defs.impl.CDOUIDefsPackageImpl#getCDOEditorDef()
     * @generated
     */
    EClass CDO_EDITOR_DEF = eINSTANCE.getCDOEditorDef();

    /**
     * The meta object literal for the '<em><b>Cdo View</b></em>' reference feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    EReference CDO_EDITOR_DEF__CDO_VIEW = eINSTANCE.getCDOEditorDef_CdoView();

    /**
     * The meta object literal for the '<em><b>Resource Path</b></em>' attribute feature.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     */
    EAttribute CDO_EDITOR_DEF__RESOURCE_PATH = eINSTANCE.getCDOEditorDef_ResourcePath();

  }

} // CDOUIDefsPackage
