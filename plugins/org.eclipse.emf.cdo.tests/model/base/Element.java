/*
 * Copyright (c) 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package base;

import org.eclipse.emf.cdo.CDOObject;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Element</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link base.Element#getSubelements <em>Subelements</em>}</li>
 *   <li>{@link base.Element#getParent <em>Parent</em>}</li>
 * </ul>
 *
 * @see base.BasePackage#getElement()
 * @model
 * @extends CDOObject
 * @generated
 */
public interface Element extends CDOObject
{
  /**
   * Returns the value of the '<em><b>Subelements</b></em>' containment reference list.
   * The list contents are of type {@link base.Element}.
   * It is bidirectional and its opposite is '{@link base.Element#getParent <em>Parent</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Subelements</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Subelements</em>' containment reference list.
   * @see base.BasePackage#getElement_Subelements()
   * @see base.Element#getParent
   * @model opposite="parent" containment="true"
   * @generated
   */
  EList<Element> getSubelements();

  /**
   * Returns the value of the '<em><b>Parent</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link base.Element#getSubelements <em>Subelements</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Parent</em>' container reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Parent</em>' container reference.
   * @see #setParent(Element)
   * @see base.BasePackage#getElement_Parent()
   * @see base.Element#getSubelements
   * @model opposite="subelements" transient="false"
   * @generated
   */
  Element getParent();

  /**
   * Sets the value of the '{@link base.Element#getParent <em>Parent</em>}' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Parent</em>' container reference.
   * @see #getParent()
   * @generated
   */
  void setParent(Element value);

} // Element
