/*
 * Copyright (c) 2008, 2010-2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model3;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-<!-- begin-user-doc --> A representation of the model object '/b></em>'. <!-- end-'. <!--
 * end-user-doc --> e
 * <p>
 * The following features are supported: >{@link or <li>se.emf.cdo.tests.model3.MetaRef#getEPackageRef <em>EPackage Ref
 * </em>}</li> </ul>
 * </p>
 *
 * @see org.ec se.emf.cdo.tests.model3.Model3Package#getMetaRef()
 * @model
 * @generated
 */
public interface MetaRef extends EObject
{
  /**
   * Returns the value of the '<em><b>EPackage Ref</b></em>' reference. ' reference. <!-- begin-user-doc --> he meaning
   * of the '<em>EPackage Ref</em>' reference isn'' reference isn't clear, there really should be more of a description
   * here... </p> doc -->
   *
   * @return the val e of the '<em>EPackage Ref</em>' reference.
   * @see #setEPackageRef(EPackage)
   * @see org.eclipse.emf.cdo.tests.model3.Model3Package#getMetaRef_EPackageRef()
   * @model
   * @generated
   */
  EPackage getEPackageRef();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model3.MetaRef#getEPackageRef <em>EPackage Ref</em>}'
   * reference. ' reference. <!-- begin-user-doc --> <!-- end-user-doc --> h new v value the new value of the '</em>'
   * reference.
   *
   * @see #getEPackageRef()
   * @generated
   */
  void setEPackageRef(EPackage value);

  /**
   * Returns the value of the '<em><b>EClass Ref</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>EClass Ref</em>' reference isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>EClass Ref</em>' reference.
   * @see #setEClassRef(EClass)
   * @see org.eclipse.emf.cdo.tests.model3.Model3Package#getMetaRef_EClassRef()
   * @model
   * @generated
   */
  EClass getEClassRef();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model3.MetaRef#getEClassRef <em>EClass Ref</em>}' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>EClass Ref</em>' reference.
   * @see #getEClassRef()
   * @generated
   */
  void setEClassRef(EClass value);

  /**
   * Returns the value of the '<em><b>EReference Ref</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>EReference Ref</em>' reference isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>EReference Ref</em>' reference.
   * @see #setEReferenceRef(EReference)
   * @see org.eclipse.emf.cdo.tests.model3.Model3Package#getMetaRef_EReferenceRef()
   * @model
   * @generated
   */
  EReference getEReferenceRef();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model3.MetaRef#getEReferenceRef <em>EReference Ref</em>}' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>EReference Ref</em>' reference.
   * @see #getEReferenceRef()
   * @generated
   */
  void setEReferenceRef(EReference value);

} // MetaRef
