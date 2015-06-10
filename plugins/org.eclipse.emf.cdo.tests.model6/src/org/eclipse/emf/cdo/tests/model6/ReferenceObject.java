/*
 * Copyright (c) 2011, 2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model6;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Reference Object</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model6.ReferenceObject#getReferenceOptional <em>Reference Optional</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model6.ReferenceObject#getReferenceList <em>Reference List</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.tests.model6.Model6Package#getReferenceObject()
 * @model
 * @generated
 */
public interface ReferenceObject extends BaseObject
{
  /**
   * Returns the value of the '<em><b>Reference Optional</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Reference Optional</em>' reference isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Reference Optional</em>' reference.
   * @see #setReferenceOptional(BaseObject)
   * @see org.eclipse.emf.cdo.tests.model6.Model6Package#getReferenceObject_ReferenceOptional()
   * @model
   * @generated
   */
  BaseObject getReferenceOptional();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model6.ReferenceObject#getReferenceOptional <em>Reference Optional</em>}' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Reference Optional</em>' reference.
   * @see #getReferenceOptional()
   * @generated
   */
  void setReferenceOptional(BaseObject value);

  /**
   * Returns the value of the '<em><b>Reference List</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.model6.BaseObject}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Reference List</em>' reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Reference List</em>' reference list.
   * @see org.eclipse.emf.cdo.tests.model6.Model6Package#getReferenceObject_ReferenceList()
   * @model
   * @generated
   */
  EList<BaseObject> getReferenceList();

} // ReferenceObject
