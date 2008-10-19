/**
 * <copyright>
 * </copyright>
 *
 * $Id: TaskImpl.java,v 1.2 2008-10-19 01:28:55 smcduff Exp $
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
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model2.impl.TaskImpl#getTaskContainer <em>Task Container</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model2.impl.TaskImpl#getDescription <em>Description</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model2.impl.TaskImpl#isDone <em>Done</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class TaskImpl extends CDOObjectImpl implements Task
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected TaskImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return Model2Package.Literals.TASK;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected int eStaticFeatureCount()
  {
    return 0;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public TaskContainer getTaskContainer()
  {
    return (TaskContainer)eGet(Model2Package.Literals.TASK__TASK_CONTAINER, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setTaskContainer(TaskContainer newTaskContainer)
  {
    eSet(Model2Package.Literals.TASK__TASK_CONTAINER, newTaskContainer);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public String getDescription()
  {
    return (String)eGet(Model2Package.Literals.TASK__DESCRIPTION, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setDescription(String newDescription)
  {
    eSet(Model2Package.Literals.TASK__DESCRIPTION, newDescription);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public boolean isDone()
  {
    return ((Boolean)eGet(Model2Package.Literals.TASK__DONE, true)).booleanValue();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setDone(boolean newDone)
  {
    eSet(Model2Package.Literals.TASK__DONE, new Boolean(newDone));
  }

} // TaskImpl
