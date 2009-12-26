/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model2;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Persistent Containment</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model2.PersistentContainment#getChildren <em>Children</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.tests.model2.Model2Package#getPersistentContainment()
 * @model
 * @generated
 */
public interface PersistentContainment extends EObject
{
  /**
   * Returns the value of the '<em><b>Children</b></em>' containment reference list. The list contents are of type
   * {@link org.eclipse.emf.cdo.tests.model2.TransientContainer}. It is bidirectional and its opposite is '
   * {@link org.eclipse.emf.cdo.tests.model2.TransientContainer#getParent <em>Parent</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Children</em>' containment reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Children</em>' containment reference list.
   * @see org.eclipse.emf.cdo.tests.model2.Model2Package#getPersistentContainment_Children()
   * @see org.eclipse.emf.cdo.tests.model2.TransientContainer#getParent
   * @model opposite="parent" containment="true"
   * @generated
   */
  EList<TransientContainer> getChildren();

} // PersistentContainment
