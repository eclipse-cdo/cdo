/*
 * Copyright (c) 2008, 2009, 2011-2013, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model2;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Task</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.Task#getTaskContainer <em>Task Container</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.Task#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.Task#isDone <em>Done</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.tests.model2.Model2Package#getTask()
 * @model
 * @generated
 */
public interface Task extends EObject
{
  /**
   * Returns the value of the '<em><b>Task Container</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.tests.model2.TaskContainer#getTasks <em>Tasks</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Task Container</em>' container reference isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Task Container</em>' container reference.
   * @see #setTaskContainer(TaskContainer)
   * @see org.eclipse.emf.cdo.tests.model2.Model2Package#getTask_TaskContainer()
   * @see org.eclipse.emf.cdo.tests.model2.TaskContainer#getTasks
   * @model opposite="tasks" transient="false"
   * @generated
   */
  TaskContainer getTaskContainer();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model2.Task#getTaskContainer <em>Task Container</em>}' container reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Task Container</em>' container reference.
   * @see #getTaskContainer()
   * @generated
   */
  void setTaskContainer(TaskContainer value);

  /**
   * Returns the value of the '<em><b>Description</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Description</em>' attribute isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Description</em>' attribute.
   * @see #setDescription(String)
   * @see org.eclipse.emf.cdo.tests.model2.Model2Package#getTask_Description()
   * @model
   * @generated
   */
  String getDescription();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model2.Task#getDescription <em>Description</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Description</em>' attribute.
   * @see #getDescription()
   * @generated
   */
  void setDescription(String value);

  /**
   * Returns the value of the '<em><b>Done</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Done</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Done</em>' attribute.
   * @see #setDone(boolean)
   * @see org.eclipse.emf.cdo.tests.model2.Model2Package#getTask_Done()
   * @model
   * @generated
   */
  boolean isDone();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model2.Task#isDone <em>Done</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Done</em>' attribute.
   * @see #isDone()
   * @generated
   */
  void setDone(boolean value);

} // Task
