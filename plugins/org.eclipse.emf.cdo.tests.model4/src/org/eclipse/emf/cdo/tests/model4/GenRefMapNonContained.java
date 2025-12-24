/*
 * Copyright (c) 2008, 2009, 2011-2013, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model4;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Gen Ref Map Non Contained</b></em>'. <!--
 * end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model4.GenRefMapNonContained#getElements <em>Elements</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.tests.model4.model4Package#getGenRefMapNonContained()
 * @model
 * @generated
 */
public interface GenRefMapNonContained extends EObject
{
  /**
   * Returns the value of the '<em><b>Elements</b></em>' map.
   * The key is of type {@link java.lang.String},
   * and the value is of type {@link org.eclipse.emf.ecore.EObject},
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Elements</em>' containment reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Elements</em>' map.
   * @see org.eclipse.emf.cdo.tests.model4.model4Package#getGenRefMapNonContained_Elements()
   * @model mapType="org.eclipse.emf.cdo.tests.model4.StringToEObject&lt;org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EObject&gt;"
   * @generated
   */
  EMap<String, EObject> getElements();

} // GenRefMapNonContained
