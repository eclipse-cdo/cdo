/*
 * Copyright (c) 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.emf.dawnEmfGenmodel;

import org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.DawngenmodelPackage;

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
 * @see org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.emf.dawnEmfGenmodel.DawnEmfGenmodelFactory
 * @model kind="package"
 * @generated
 * @author Martin Fluegge
 */
public interface DawnEmfGenmodelPackage extends EPackage
{
  /**
   * The package name. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  String eNAME = "dawnEmfGenmodel";

  /**
   * The package namespace URI. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  String eNS_URI = "http://www.eclipse.org/emf/cdo/dawn/2010/GenModel/emf";

  /**
   * The package namespace name. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  String eNS_PREFIX = "dawnEmfGenmodel";

  /**
   * The singleton instance of the package. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  DawnEmfGenmodelPackage eINSTANCE = org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.emf.dawnEmfGenmodel.impl.DawnEmfGenmodelPackageImpl.init();

  /**
   * The meta object id for the '
   * {@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.emf.dawnEmfGenmodel.impl.DawnEMFGeneratorImpl
   * <em>Dawn EMF Generator</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.emf.dawnEmfGenmodel.impl.DawnEMFGeneratorImpl
   * @see org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.emf.dawnEmfGenmodel.impl.DawnEmfGenmodelPackageImpl#getDawnEMFGenerator()
   * @generated
   */
  int DAWN_EMF_GENERATOR = 0;

  /**
   * The feature id for the '<em><b>Fragment Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DAWN_EMF_GENERATOR__FRAGMENT_NAME = DawngenmodelPackage.DAWN_FRAGMENT_GENERATOR__FRAGMENT_NAME;

  /**
   * The feature id for the '<em><b>Dawn Editor Class Name</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DAWN_EMF_GENERATOR__DAWN_EDITOR_CLASS_NAME = DawngenmodelPackage.DAWN_FRAGMENT_GENERATOR__DAWN_EDITOR_CLASS_NAME;

  /**
   * The feature id for the '<em><b>Dawn Generator</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DAWN_EMF_GENERATOR__DAWN_GENERATOR = DawngenmodelPackage.DAWN_FRAGMENT_GENERATOR__DAWN_GENERATOR;

  /**
   * The feature id for the '<em><b>Emf Gen Model</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DAWN_EMF_GENERATOR__EMF_GEN_MODEL = DawngenmodelPackage.DAWN_FRAGMENT_GENERATOR_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Dawn EMF Generator</em>' class. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int DAWN_EMF_GENERATOR_FEATURE_COUNT = DawngenmodelPackage.DAWN_FRAGMENT_GENERATOR_FEATURE_COUNT + 1;

  /**
   * Returns the meta object for class '
   * {@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.emf.dawnEmfGenmodel.DawnEMFGenerator
   * <em>Dawn EMF Generator</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Dawn EMF Generator</em>'.
   * @see org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.emf.dawnEmfGenmodel.DawnEMFGenerator
   * @generated
   */
  EClass getDawnEMFGenerator();

  /**
   * Returns the meta object for the reference '
   * {@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.emf.dawnEmfGenmodel.DawnEMFGenerator#getEmfGenModel
   * <em>Emf Gen Model</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the meta object for the reference '<em>Emf Gen Model</em>'.
   * @see org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.emf.dawnEmfGenmodel.DawnEMFGenerator#getEmfGenModel()
   * @see #getDawnEMFGenerator()
   * @generated
   */
  EReference getDawnEMFGenerator_EmfGenModel();

  /**
   * Returns the factory that creates the instances of the model. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the factory that creates the instances of the model.
   * @generated
   */
  DawnEmfGenmodelFactory getDawnEmfGenmodelFactory();

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
     * The meta object literal for the '
     * {@link org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.emf.dawnEmfGenmodel.impl.DawnEMFGeneratorImpl
     * <em>Dawn EMF Generator</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @see org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.emf.dawnEmfGenmodel.impl.DawnEMFGeneratorImpl
     * @see org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.emf.dawnEmfGenmodel.impl.DawnEmfGenmodelPackageImpl#getDawnEMFGenerator()
     * @generated
     */
    EClass DAWN_EMF_GENERATOR = eINSTANCE.getDawnEMFGenerator();

    /**
     * The meta object literal for the '<em><b>Emf Gen Model</b></em>' reference feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated
     */
    EReference DAWN_EMF_GENERATOR__EMF_GEN_MODEL = eINSTANCE.getDawnEMFGenerator_EmfGenModel();

  }

} // DawnEmfGenmodelPackage
