/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.codegen.dawngenmodel;

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
 * 
 * @see org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawngenmodelFactory
 * @model kind="package"
 * @generated
 */
public interface DawngenmodelPackage extends EPackage
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String copyright = "Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n\r\nContributors:\r\n   Martin Fluegge - initial API and implementation";

  /**
   * The package name. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNAME = "dawngenmodel";

  /**
   * The package namespace URI. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNS_URI = "http://www.eclipse.org/emf/cdo/dawn/2010/GenModel";

  /**
   * The package namespace name. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String eNS_PREFIX = "dawngenmodel";

  /**
   * The singleton instance of the package. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  DawngenmodelPackage eINSTANCE = org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.impl.DawngenmodelPackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.impl.DawnGeneratorImpl
   * <em>Dawn Generator</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.impl.DawnGeneratorImpl
   * @see org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.impl.DawngenmodelPackageImpl#getDawnGenerator()
   * @generated
   * @since 1.0
   */
  int DAWN_GENERATOR = 0;

  /**
   * The feature id for the '<em><b>Conflict Color</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   * @since 1.0
   */
  int DAWN_GENERATOR__CONFLICT_COLOR = 0;

  /**
   * The feature id for the '<em><b>Local Lock Color</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   * @since 1.0
   */
  int DAWN_GENERATOR__LOCAL_LOCK_COLOR = 1;

  /**
   * The feature id for the '<em><b>Remote Lock Color</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   * @since 1.0
   */
  int DAWN_GENERATOR__REMOTE_LOCK_COLOR = 2;

  /**
   * The number of structural features of the '<em>Dawn Generator</em>' class. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @generated
   * @ordered
   * @since 1.0
   */
  int DAWN_GENERATOR_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.impl.DawnFragmentGeneratorImpl
   * <em>Dawn Fragment Generator</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.impl.DawnFragmentGeneratorImpl
   * @see org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.impl.DawngenmodelPackageImpl#getDawnFragmentGenerator()
   * @generated
   * @since 1.0
   */
  int DAWN_FRAGMENT_GENERATOR = 1;

  /**
   * The feature id for the '<em><b>Fragment Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   * @since 1.0
   */
  int DAWN_FRAGMENT_GENERATOR__FRAGMENT_NAME = 0;

  /**
   * The feature id for the '<em><b>Dawn Editor Class Name</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   * @since 1.0
   */
  int DAWN_FRAGMENT_GENERATOR__DAWN_EDITOR_CLASS_NAME = 1;

  /**
   * The feature id for the '<em><b>Dawn Generator</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   * @ordered
   * @since 1.0
   */
  int DAWN_FRAGMENT_GENERATOR__DAWN_GENERATOR = 2;

  /**
   * The number of structural features of the '<em>Dawn Fragment Generator</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   * @ordered
   * @since 1.0
   */
  int DAWN_FRAGMENT_GENERATOR_FEATURE_COUNT = 3;

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnGenerator
   * <em>Dawn Generator</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Dawn Generator</em>'.
   * @see org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnGenerator
   * @generated
   * @since 1.0
   */
  EClass getDawnGenerator();

  /**
   * Returns the meta object for the attribute '
   * {@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnGenerator#getConflictColor <em>Conflict Color</em>}'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Conflict Color</em>'.
   * @see org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnGenerator#getConflictColor()
   * @see #getDawnGenerator()
   * @generated
   * @since 1.0
   */
  EAttribute getDawnGenerator_ConflictColor();

  /**
   * Returns the meta object for the attribute '
   * {@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnGenerator#getLocalLockColor <em>Local Lock Color</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Local Lock Color</em>'.
   * @see org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnGenerator#getLocalLockColor()
   * @see #getDawnGenerator()
   * @generated
   * @since 1.0
   */
  EAttribute getDawnGenerator_LocalLockColor();

  /**
   * Returns the meta object for the attribute '
   * {@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnGenerator#getRemoteLockColor <em>Remote Lock Color</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Remote Lock Color</em>'.
   * @see org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnGenerator#getRemoteLockColor()
   * @see #getDawnGenerator()
   * @generated
   * @since 1.0
   */
  EAttribute getDawnGenerator_RemoteLockColor();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnFragmentGenerator
   * <em>Dawn Fragment Generator</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for class '<em>Dawn Fragment Generator</em>'.
   * @see org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnFragmentGenerator
   * @generated
   * @since 1.0
   */
  EClass getDawnFragmentGenerator();

  /**
   * Returns the meta object for the attribute '
   * {@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnFragmentGenerator#getFragmentName <em>Fragment Name</em>}
   * '. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Fragment Name</em>'.
   * @see org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnFragmentGenerator#getFragmentName()
   * @see #getDawnFragmentGenerator()
   * @generated
   * @since 1.0
   */
  EAttribute getDawnFragmentGenerator_FragmentName();

  /**
   * Returns the meta object for the attribute '
   * {@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnFragmentGenerator#getDawnEditorClassName
   * <em>Dawn Editor Class Name</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the attribute '<em>Dawn Editor Class Name</em>'.
   * @see org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnFragmentGenerator#getDawnEditorClassName()
   * @see #getDawnFragmentGenerator()
   * @generated
   * @since 1.0
   */
  EAttribute getDawnFragmentGenerator_DawnEditorClassName();

  /**
   * Returns the meta object for the reference '
   * {@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnFragmentGenerator#getDawnGenerator
   * <em>Dawn Generator</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the meta object for the reference '<em>Dawn Generator</em>'.
   * @see org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawnFragmentGenerator#getDawnGenerator()
   * @see #getDawnFragmentGenerator()
   * @generated
   * @since 1.0
   */
  EReference getDawnFragmentGenerator_DawnGenerator();

  /**
   * Returns the factory that creates the instances of the model. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the factory that creates the instances of the model.
   * @generated
   */
  DawngenmodelFactory getDawngenmodelFactory();

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
     * The meta object literal for the '{@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.impl.DawnGeneratorImpl
     * <em>Dawn Generator</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.impl.DawnGeneratorImpl
     * @see org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.impl.DawngenmodelPackageImpl#getDawnGenerator()
     * @generated
     * @since 1.0
     */
    EClass DAWN_GENERATOR = eINSTANCE.getDawnGenerator();

    /**
     * The meta object literal for the '<em><b>Conflict Color</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     * @since 1.0
     */
    EAttribute DAWN_GENERATOR__CONFLICT_COLOR = eINSTANCE.getDawnGenerator_ConflictColor();

    /**
     * The meta object literal for the '<em><b>Local Lock Color</b></em>' attribute feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     * @since 1.0
     */
    EAttribute DAWN_GENERATOR__LOCAL_LOCK_COLOR = eINSTANCE.getDawnGenerator_LocalLockColor();

    /**
     * The meta object literal for the '<em><b>Remote Lock Color</b></em>' attribute feature. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     * @since 1.0
     */
    EAttribute DAWN_GENERATOR__REMOTE_LOCK_COLOR = eINSTANCE.getDawnGenerator_RemoteLockColor();

    /**
     * The meta object literal for the '
     * {@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.impl.DawnFragmentGeneratorImpl
     * <em>Dawn Fragment Generator</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.impl.DawnFragmentGeneratorImpl
     * @see org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.impl.DawngenmodelPackageImpl#getDawnFragmentGenerator()
     * @generated
     * @since 1.0
     */
    EClass DAWN_FRAGMENT_GENERATOR = eINSTANCE.getDawnFragmentGenerator();

    /**
     * The meta object literal for the '<em><b>Fragment Name</b></em>' attribute feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     * @since 1.0
     */
    EAttribute DAWN_FRAGMENT_GENERATOR__FRAGMENT_NAME = eINSTANCE.getDawnFragmentGenerator_FragmentName();

    /**
     * The meta object literal for the '<em><b>Dawn Editor Class Name</b></em>' attribute feature. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * 
     * @generated
     * @since 1.0
     */
    EAttribute DAWN_FRAGMENT_GENERATOR__DAWN_EDITOR_CLASS_NAME = eINSTANCE
        .getDawnFragmentGenerator_DawnEditorClassName();

    /**
     * The meta object literal for the '<em><b>Dawn Generator</b></em>' reference feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     * @since 1.0
     */
    EReference DAWN_FRAGMENT_GENERATOR__DAWN_GENERATOR = eINSTANCE.getDawnFragmentGenerator_DawnGenerator();

  }

} // DawngenmodelPackage
