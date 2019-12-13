/*
 * Copyright (c) 2008, 2009, 2011-2013, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model2.impl;

import org.eclipse.emf.cdo.tests.model2.Model2Package;
import org.eclipse.emf.cdo.tests.model2.Task;
import org.eclipse.emf.cdo.tests.model2.TaskContainer;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Task</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.impl.TaskImpl#getTaskContainer <em>Task Container</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.impl.TaskImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.impl.TaskImpl#isDone <em>Done</em>}</li>
 * </ul>
 *
 * @generated
 */
public class TaskImpl extends CDOObjectImpl implements Task
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected TaskImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return Model2Package.eINSTANCE.getTask();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected int eStaticFeatureCount()
  {
    return 0;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public TaskContainer getTaskContainer()
  {
    return (TaskContainer)eGet(Model2Package.eINSTANCE.getTask_TaskContainer(), true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setTaskContainer(TaskContainer newTaskContainer)
  {
    eSet(Model2Package.eINSTANCE.getTask_TaskContainer(), newTaskContainer);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getDescription()
  {
    return (String)eGet(Model2Package.eINSTANCE.getTask_Description(), true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setDescription(String newDescription)
  {
    eSet(Model2Package.eINSTANCE.getTask_Description(), newDescription);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isDone()
  {
    return (Boolean)eGet(Model2Package.eINSTANCE.getTask_Done(), true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setDone(boolean newDone)
  {
    eSet(Model2Package.eINSTANCE.getTask_Done(), newDone);
  }

} // TaskImpl
