/*
 * Copyright (c) 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model3;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Class With Java Object Attribute</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.ClassWithJavaObjectAttribute#getJavaObject <em>Java Object</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.tests.model3.Model3Package#getClassWithJavaObjectAttribute()
 * @model
 * @generated
 */
public interface ClassWithJavaObjectAttribute extends EObject
{
  /**
   * Returns the value of the '<em><b>Java Object</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Java Object</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Java Object</em>' attribute.
   * @see #setJavaObject(Object)
   * @see org.eclipse.emf.cdo.tests.model3.Model3Package#getClassWithJavaObjectAttribute_JavaObject()
   * @model id="true"
   * @generated
   */
  Object getJavaObject();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model3.ClassWithJavaObjectAttribute#getJavaObject <em>Java Object</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Java Object</em>' attribute.
   * @see #getJavaObject()
   * @generated
   */
  void setJavaObject(Object value);

} // ClassWithJavaObjectAttribute
