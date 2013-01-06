/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model4.GenRefMapNonContained#getElements <em>Elements</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.tests.model4.legacy.model4Package#getGenRefMapNonContained()
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
   * @see org.eclipse.emf.cdo.tests.model4.legacy.model4Package#getGenRefMapNonContained_Elements()
   * @model mapType="org.eclipse.emf.cdo.tests.model4.StringToEObject<org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EObject>"
   * @generated
   */
  EMap<String, EObject> getElements();

} // GenRefMapNonContained
