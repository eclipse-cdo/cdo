/**
 * <copyright>
 * </copyright>
 *
 * $Id: TaskContainerImpl.java,v 1.2 2008-10-19 01:28:55 smcduff Exp $
 */
package org.eclipse.emf.cdo.tests.model2.impl;

import org.eclipse.emf.cdo.tests.model2.Model2Package;
import org.eclipse.emf.cdo.tests.model2.Task;
import org.eclipse.emf.cdo.tests.model2.TaskContainer;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Task Container</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model2.impl.TaskContainerImpl#getTasks <em>Tasks</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class TaskContainerImpl extends CDOObjectImpl implements TaskContainer
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected TaskContainerImpl()
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
    return Model2Package.Literals.TASK_CONTAINER;
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
  @SuppressWarnings("unchecked")
  public EList<Task> getTasks()
  {
    return (EList<Task>)eGet(Model2Package.Literals.TASK_CONTAINER__TASKS, true);
  }

} // TaskContainerImpl
