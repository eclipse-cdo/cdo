/*
 * Copyright (c) 2018 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Class With Transient Containment</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.ClassWithTransientContainment#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.ClassWithTransientContainment#getTransientChild <em>Transient Child</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.ClassWithTransientContainment#getTransientChildren <em>Transient Children</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.ClassWithTransientContainment#getPersistentChild <em>Persistent Child</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.ClassWithTransientContainment#getPersistentChildren <em>Persistent Children</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.tests.model3.Model3Package#getClassWithTransientContainment()
 * @model
 * @generated
 */
public interface ClassWithTransientContainment extends EObject
{
  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.eclipse.emf.cdo.tests.model3.Model3Package#getClassWithTransientContainment_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model3.ClassWithTransientContainment#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Transient Child</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Transient Child</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Transient Child</em>' containment reference.
   * @see #setTransientChild(ClassWithTransientContainment)
   * @see org.eclipse.emf.cdo.tests.model3.Model3Package#getClassWithTransientContainment_TransientChild()
   * @model containment="true" transient="true"
   * @generated
   */
  ClassWithTransientContainment getTransientChild();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model3.ClassWithTransientContainment#getTransientChild <em>Transient Child</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Transient Child</em>' containment reference.
   * @see #getTransientChild()
   * @generated
   */
  void setTransientChild(ClassWithTransientContainment value);

  /**
   * Returns the value of the '<em><b>Transient Children</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.model3.ClassWithTransientContainment}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Transient Children</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Transient Children</em>' containment reference list.
   * @see org.eclipse.emf.cdo.tests.model3.Model3Package#getClassWithTransientContainment_TransientChildren()
   * @model containment="true" transient="true"
   * @generated
   */
  EList<ClassWithTransientContainment> getTransientChildren();

  /**
   * Returns the value of the '<em><b>Persistent Child</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Persistent Child</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Persistent Child</em>' containment reference.
   * @see #setPersistentChild(ClassWithTransientContainment)
   * @see org.eclipse.emf.cdo.tests.model3.Model3Package#getClassWithTransientContainment_PersistentChild()
   * @model containment="true"
   * @generated
   */
  ClassWithTransientContainment getPersistentChild();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model3.ClassWithTransientContainment#getPersistentChild <em>Persistent Child</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Persistent Child</em>' containment reference.
   * @see #getPersistentChild()
   * @generated
   */
  void setPersistentChild(ClassWithTransientContainment value);

  /**
   * Returns the value of the '<em><b>Persistent Children</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.model3.ClassWithTransientContainment}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Persistent Children</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Persistent Children</em>' containment reference list.
   * @see org.eclipse.emf.cdo.tests.model3.Model3Package#getClassWithTransientContainment_PersistentChildren()
   * @model containment="true"
   * @generated
   */
  EList<ClassWithTransientContainment> getPersistentChildren();

} // ClassWithTransientContainment
