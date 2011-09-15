/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model4;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Ref Multi Non Contained Unsettable</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model4.RefMultiNonContainedUnsettable#getElements <em>Elements</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.tests.model4.model4Package#getRefMultiNonContainedUnsettable()
 * @model
 * @generated
 */
public interface RefMultiNonContainedUnsettable extends EObject
{
  /**
   * Returns the value of the '<em><b>Elements</b></em>' reference list. The list contents are of type
   * {@link org.eclipse.emf.cdo.tests.model4.MultiNonContainedUnsettableElement}. It is bidirectional and its opposite
   * is '{@link org.eclipse.emf.cdo.tests.model4.MultiNonContainedUnsettableElement#getParent <em>Parent</em>}'. <!--
   * begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Elements</em>' reference list isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Elements</em>' reference list.
   * @see #isSetElements()
   * @see #unsetElements()
   * @see org.eclipse.emf.cdo.tests.model4.model4Package#getRefMultiNonContainedUnsettable_Elements()
   * @see org.eclipse.emf.cdo.tests.model4.MultiNonContainedUnsettableElement#getParent
   * @model opposite="parent" unsettable="true"
   * @generated
   */
  EList<MultiNonContainedUnsettableElement> getElements();

  /**
   * Unsets the value of the '{@link org.eclipse.emf.cdo.tests.model4.RefMultiNonContainedUnsettable#getElements
   * <em>Elements</em>}' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #isSetElements()
   * @see #getElements()
   * @generated
   */
  void unsetElements();

  /**
   * Returns whether the value of the '
   * {@link org.eclipse.emf.cdo.tests.model4.RefMultiNonContainedUnsettable#getElements <em>Elements</em>}' reference
   * list is set. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return whether the value of the '<em>Elements</em>' reference list is set.
   * @see #unsetElements()
   * @see #getElements()
   * @generated
   */
  boolean isSetElements();

} // RefMultiNonContainedUnsettable
