/*
 * Copyright (c) 2013, 2015, 2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model2.legacy.impl;

import org.eclipse.emf.cdo.tests.model2.Task;
import org.eclipse.emf.cdo.tests.model2.TaskContainer;
import org.eclipse.emf.cdo.tests.model2.legacy.Model2Package;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Task</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.legacy.impl.TaskImpl#getTaskContainer <em>Task Container</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.legacy.impl.TaskImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.legacy.impl.TaskImpl#isDone <em>Done</em>}</li>
 * </ul>
 *
 * @generated
 */
public class TaskImpl extends EObjectImpl implements Task
{
  /**
   * The default value of the '{@link #getDescription() <em>Description</em>}' attribute.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #getDescription()
   * @generated
   * @ordered
   */
  protected static final String DESCRIPTION_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getDescription() <em>Description</em>}' attribute.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #getDescription()
   * @generated
   * @ordered
   */
  protected String description = DESCRIPTION_EDEFAULT;

  /**
   * The default value of the '{@link #isDone() <em>Done</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #isDone()
   * @generated
   * @ordered
   */
  protected static final boolean DONE_EDEFAULT = false;

  /**
   * The cached value of the '{@link #isDone() <em>Done</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #isDone()
   * @generated
   * @ordered
   */
  protected boolean done = DONE_EDEFAULT;

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
  public TaskContainer getTaskContainer()
  {
    if (eContainerFeatureID() != Model2Package.TASK__TASK_CONTAINER)
    {
      return null;
    }
    return (TaskContainer)eInternalContainer();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetTaskContainer(TaskContainer newTaskContainer, NotificationChain msgs)
  {
    msgs = eBasicSetContainer((InternalEObject)newTaskContainer, Model2Package.TASK__TASK_CONTAINER, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setTaskContainer(TaskContainer newTaskContainer)
  {
    if (newTaskContainer != eInternalContainer() || eContainerFeatureID() != Model2Package.TASK__TASK_CONTAINER && newTaskContainer != null)
    {
      if (EcoreUtil.isAncestor(this, newTaskContainer))
      {
        throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
      }
      NotificationChain msgs = null;
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      if (newTaskContainer != null)
      {
        msgs = ((InternalEObject)newTaskContainer).eInverseAdd(this, Model2Package.TASK_CONTAINER__TASKS, TaskContainer.class, msgs);
      }
      msgs = basicSetTaskContainer(newTaskContainer, msgs);
      if (msgs != null)
      {
        msgs.dispatch();
      }
    }
    else if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model2Package.TASK__TASK_CONTAINER, newTaskContainer, newTaskContainer));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getDescription()
  {
    return description;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setDescription(String newDescription)
  {
    String oldDescription = description;
    description = newDescription;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model2Package.TASK__DESCRIPTION, oldDescription, description));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isDone()
  {
    return done;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setDone(boolean newDone)
  {
    boolean oldDone = done;
    done = newDone;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model2Package.TASK__DONE, oldDone, done));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case Model2Package.TASK__TASK_CONTAINER:
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      return basicSetTaskContainer((TaskContainer)otherEnd, msgs);
    }
    return super.eInverseAdd(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case Model2Package.TASK__TASK_CONTAINER:
      return basicSetTaskContainer(null, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs)
  {
    switch (eContainerFeatureID())
    {
    case Model2Package.TASK__TASK_CONTAINER:
      return eInternalContainer().eInverseRemove(this, Model2Package.TASK_CONTAINER__TASKS, TaskContainer.class, msgs);
    }
    return super.eBasicRemoveFromContainerFeature(msgs);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case Model2Package.TASK__TASK_CONTAINER:
      return getTaskContainer();
    case Model2Package.TASK__DESCRIPTION:
      return getDescription();
    case Model2Package.TASK__DONE:
      return isDone();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case Model2Package.TASK__TASK_CONTAINER:
      setTaskContainer((TaskContainer)newValue);
      return;
    case Model2Package.TASK__DESCRIPTION:
      setDescription((String)newValue);
      return;
    case Model2Package.TASK__DONE:
      setDone((Boolean)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case Model2Package.TASK__TASK_CONTAINER:
      setTaskContainer((TaskContainer)null);
      return;
    case Model2Package.TASK__DESCRIPTION:
      setDescription(DESCRIPTION_EDEFAULT);
      return;
    case Model2Package.TASK__DONE:
      setDone(DONE_EDEFAULT);
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case Model2Package.TASK__TASK_CONTAINER:
      return getTaskContainer() != null;
    case Model2Package.TASK__DESCRIPTION:
      return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
    case Model2Package.TASK__DONE:
      return done != DONE_EDEFAULT;
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy())
    {
      return super.toString();
    }

    StringBuilder result = new StringBuilder(super.toString());
    result.append(" (description: ");
    result.append(description);
    result.append(", done: ");
    result.append(done);
    result.append(')');
    return result.toString();
  }

} // TaskImpl
