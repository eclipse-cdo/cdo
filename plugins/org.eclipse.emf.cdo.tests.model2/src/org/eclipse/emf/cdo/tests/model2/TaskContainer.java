/*
 * Copyright (c) 2008, 2009, 2011-2013 Eike Stepper (Berlin, Germany) and others.
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
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Task Container</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.TaskContainer#getTasks <em>Tasks</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.tests.model2.Model2Package#getTaskContainer()
 * @model
 * @generated
 */
public interface TaskContainer extends EObject
{
  /**
   * Returns the value of the '<em><b>Tasks</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.model2.Task}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.tests.model2.Task#getTaskContainer <em>Task Container</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Tasks</em>' containment reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Tasks</em>' containment reference list.
   * @see org.eclipse.emf.cdo.tests.model2.Model2Package#getTaskContainer_Tasks()
   * @see org.eclipse.emf.cdo.tests.model2.Task#getTaskContainer
   * @model opposite="taskContainer" containment="true"
   * @generated
   */
  EList<Task> getTasks();

} // TaskContainer
