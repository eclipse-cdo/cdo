/******
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
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Gen Ref Multi NU Non Contained</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model4.GenRefMultiNUNonContained#getElements <em>Elements</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.tests.model4.model4Package#getGenRefMultiNUNonContained()
 * @model
 * @generated
 */
public interface GenRefMultiNUNonContained extends EObject
{
  /**
   * Returns the value of the '<em><b>Elements</b></em>' reference list. The list contents are of type
   * {@link org.eclipse.emf.ecore.EObject}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Elements</em>' reference list isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Elements</em>' reference list.
   * @see org.eclipse.emf.cdo.tests.model4.model4Package#getGenRefMultiNUNonContained_Elements()
   * @model resolveProxies="false"
   * @generated
   */
  EList<EObject> getElements();

} // GenRefMultiNUNonContained
