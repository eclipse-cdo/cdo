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

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Transient Container</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model2.TransientContainer#getParent <em>Parent</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.tests.model2.Model2Package#getTransientContainer()
 * @model
 * @generated
 */
public interface TransientContainer extends EObject
{
  /**
   * Returns the value of the '<em><b>Parent</b></em>' container reference. It is bidirectional and its opposite is '
   * {@link org.eclipse.emf.cdo.tests.model2.PersistentContainment#getChildren <em>Children</em>}'. <!-- begin-user-doc
   * -->
   * <p>
   * If the meaning of the '<em>Parent</em>' container reference isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Parent</em>' container reference.
   * @see #setParent(PersistentContainment)
   * @see org.eclipse.emf.cdo.tests.model2.Model2Package#getTransientContainer_Parent()
   * @see org.eclipse.emf.cdo.tests.model2.PersistentContainment#getChildren
   * @model opposite="children" transient="false"
   * @generated
   */
  PersistentContainment getParent();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model2.TransientContainer#getParent <em>Parent</em>}'
   * container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Parent</em>' container reference.
   * @see #getParent()
   * @generated
   */
  void setParent(PersistentContainment value);

} // TransientContainer
