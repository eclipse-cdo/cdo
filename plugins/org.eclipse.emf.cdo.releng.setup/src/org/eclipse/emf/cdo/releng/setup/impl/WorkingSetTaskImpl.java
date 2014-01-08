/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup.impl;

import org.eclipse.emf.cdo.releng.setup.Branch;
import org.eclipse.emf.cdo.releng.setup.Preferences;
import org.eclipse.emf.cdo.releng.setup.Project;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.SetupTaskContext;
import org.eclipse.emf.cdo.releng.setup.Trigger;
import org.eclipse.emf.cdo.releng.setup.WorkingSetTask;
import org.eclipse.emf.cdo.releng.workingsets.WorkingSet;
import org.eclipse.emf.cdo.releng.workingsets.WorkingSetGroup;
import org.eclipse.emf.cdo.releng.workingsets.util.WorkingSetsUtil;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Set Working Task</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.WorkingSetTaskImpl#getWorkingSets <em>Working Sets</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class WorkingSetTaskImpl extends SetupTaskImpl implements WorkingSetTask
{
  /**
   * The cached value of the '{@link #getWorkingSets() <em>Working Sets</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getWorkingSets()
   * @generated
   * @ordered
   */
  protected EList<WorkingSet> workingSets;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected WorkingSetTaskImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return SetupPackage.Literals.WORKING_SET_TASK;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<WorkingSet> getWorkingSets()
  {
    if (workingSets == null)
    {
      workingSets = new EObjectContainmentEList.Resolving<WorkingSet>(WorkingSet.class, this,
          SetupPackage.WORKING_SET_TASK__WORKING_SETS);
    }
    return workingSets;
  }

  /**
   * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case SetupPackage.WORKING_SET_TASK__WORKING_SETS:
      return ((InternalEList<?>)getWorkingSets()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case SetupPackage.WORKING_SET_TASK__WORKING_SETS:
      return getWorkingSets();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case SetupPackage.WORKING_SET_TASK__WORKING_SETS:
      getWorkingSets().clear();
      getWorkingSets().addAll((Collection<? extends WorkingSet>)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case SetupPackage.WORKING_SET_TASK__WORKING_SETS:
      getWorkingSets().clear();
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case SetupPackage.WORKING_SET_TASK__WORKING_SETS:
      return workingSets != null && !workingSets.isEmpty();
    }
    return super.eIsSet(featureID);
  }

  @Override
  public Set<Trigger> getValidTriggers()
  {
    return Trigger.IDE_TRIGGERS;
  }

  public boolean isNeeded(SetupTaskContext context) throws Exception
  {
    WorkingSetGroup defaultWorkingSetGroup = WorkingSetsUtil.getWorkingSetGroup();
    Set<String> existingIds = new HashSet<String>();
    for (WorkingSet workingSet : defaultWorkingSetGroup.getWorkingSets())
    {
      existingIds.add(workingSet.getId());
    }

    String prefix = "";
    for (EObject eContainer = eContainer(); eContainer != null; eContainer = eContainer.eContainer())
    {
      if (eContainer instanceof Project)
      {
        prefix += ((Project)eContainer).getName() + " ";
      }
      else if (eContainer instanceof Branch)
      {
        prefix += ((Branch)eContainer).getName() + " ";
      }
      else if (eContainer instanceof Preferences)
      {
        prefix += "<user> ";
      }
    }

    Set<String> newIds = new HashSet<String>();
    for (WorkingSet workingSet : getWorkingSets())
    {
      String id = prefix + workingSet.getName();
      workingSet.setId(id);
      newIds.add(id);
    }

    // TODO Do deeper equality check to also perform for changed workingset definitions
    return !existingIds.containsAll(newIds);
  }

  public void perform(SetupTaskContext context) throws Exception
  {
    WorkingSetGroup defaultWorkingSetGroup = WorkingSetsUtil.getWorkingSetGroup();
    Set<String> existingIds = new HashSet<String>();
    EList<WorkingSet> workingSets = defaultWorkingSetGroup.getWorkingSets();
    for (WorkingSet workingSet : workingSets)
    {
      existingIds.add(workingSet.getId());
    }

    EList<WorkingSet> newWorkingSetGroups = getWorkingSets();
    int index = 0;
    for (WorkingSet workingSet : new ArrayList<WorkingSet>(newWorkingSetGroups))
    {
      String id = workingSet.getId();
      if (!existingIds.contains(id))
      {
        workingSets.add(index++, workingSet);
      }
      else
      {
        for (int i = 0, size = workingSets.size(); i < size; ++i)
        {
          if (id.equals(workingSets.get(i).getId()))
          {
            index = i + 1;
            break;
          }
        }
      }
    }

    Resource resource = defaultWorkingSetGroup.eResource();
    resource.save(null);
  }

  @Override
  public int getPriority()
  {
    return PRIORITY;
  }
} // SetWorkingTaskImpl
