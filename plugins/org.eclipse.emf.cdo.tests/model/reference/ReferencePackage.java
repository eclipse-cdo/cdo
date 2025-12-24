/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package reference;

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
 * @see reference.ReferenceFactory
 * @model kind="package"
 * @generated
 */
public interface ReferencePackage extends EPackage
{
  /**
   * The package name. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  String eNAME = "reference";

  /**
   * The package namespace URI. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  String eNS_URI = "uuid://reference";

  /**
   * The package namespace name. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  String eNS_PREFIX = "reference";

  /**
   * The singleton instance of the package. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  ReferencePackage eINSTANCE = reference.impl.ReferencePackageImpl.init();

  /**
   * The meta object id for the '{@link reference.impl.ReferenceImpl <em>Reference</em>}' class. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   *
   * @see reference.impl.ReferenceImpl
   * @see reference.impl.ReferencePackageImpl#getReference()
   * @generated
   */
  int REFERENCE = 0;

  /**
   * The feature id for the '<em><b>Ref</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int REFERENCE__REF = 0;

  /**
   * The number of structural features of the '<em>Reference</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int REFERENCE_FEATURE_COUNT = 1;

  /**
   * Returns the meta object for class '{@link reference.Reference <em>Reference</em>}'. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @return the meta object for class '<em>Reference</em>'.
   * @see reference.Reference
   * @generated
   */
  EClass getReference();

  /**
   * Returns the meta object for the reference '{@link reference.Reference#getRef <em>Ref</em>}'. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   *
   * @return the meta object for the reference '<em>Ref</em>'.
   * @see reference.Reference#getRef()
   * @see #getReference()
   * @generated
   */
  EReference getReference_Ref();

  /**
   * Returns the factory that creates the instances of the model. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the factory that creates the instances of the model.
   * @generated
   */
  ReferenceFactory getReferenceFactory();

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
     * The meta object literal for the '{@link reference.impl.ReferenceImpl <em>Reference</em>}' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @see reference.impl.ReferenceImpl
     * @see reference.impl.ReferencePackageImpl#getReference()
     * @generated
     */
    EClass REFERENCE = eINSTANCE.getReference();

    /**
     * The meta object literal for the '<em><b>Ref</b></em>' reference feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated
     */
    EReference REFERENCE__REF = eINSTANCE.getReference_Ref();

  }

} // ReferencePackage
