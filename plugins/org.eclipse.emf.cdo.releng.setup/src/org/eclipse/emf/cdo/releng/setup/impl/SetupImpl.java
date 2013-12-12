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
import org.eclipse.emf.cdo.releng.setup.ConfigurableItem;
import org.eclipse.emf.cdo.releng.setup.Configuration;
import org.eclipse.emf.cdo.releng.setup.Eclipse;
import org.eclipse.emf.cdo.releng.setup.Preferences;
import org.eclipse.emf.cdo.releng.setup.Project;
import org.eclipse.emf.cdo.releng.setup.Setup;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.SetupTask;
import org.eclipse.emf.cdo.releng.setup.SetupTaskContainer;
import org.eclipse.emf.cdo.releng.setup.Trigger;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Workspace</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.SetupImpl#getBranch <em>Branch</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.SetupImpl#getEclipseVersion <em>Eclipse Version</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.SetupImpl#getPreferences <em>Preferences</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SetupImpl extends MinimalEObjectImpl.Container implements Setup
{
  /**
   * The cached value of the '{@link #getBranch() <em>Branch</em>}' reference.
   * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
   * @see #getBranch()
   * @generated
   * @ordered
   */
  protected Branch branch;

  /**
   * The cached value of the '{@link #getEclipseVersion() <em>Eclipse Version</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEclipseVersion()
   * @generated
   * @ordered
   */
  protected Eclipse eclipseVersion;

  /**
   * The cached value of the '{@link #getPreferences() <em>Preferences</em>}' reference.
   * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
   * @see #getPreferences()
   * @generated
   * @ordered
   */
  protected Preferences preferences;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected SetupImpl()
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
    return SetupPackage.Literals.SETUP;
  }

  /**
   * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
   * @generated
   */
  public Branch getBranch()
  {
    if (branch != null && branch.eIsProxy())
    {
      InternalEObject oldBranch = (InternalEObject)branch;
      branch = (Branch)eResolveProxy(oldBranch);
      if (branch != oldBranch)
      {
        if (eNotificationRequired())
        {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, SetupPackage.SETUP__BRANCH, oldBranch, branch));
        }
      }
    }
    return branch;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Branch basicGetBranch()
  {
    return branch;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setBranch(Branch newBranch)
  {
    Branch oldBranch = branch;
    branch = newBranch;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.SETUP__BRANCH, oldBranch, branch));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Eclipse getEclipseVersion()
  {
    if (eclipseVersion != null && eclipseVersion.eIsProxy())
    {
      InternalEObject oldEclipseVersion = (InternalEObject)eclipseVersion;
      eclipseVersion = (Eclipse)eResolveProxy(oldEclipseVersion);
      if (eclipseVersion != oldEclipseVersion)
      {
        if (eNotificationRequired())
        {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, SetupPackage.SETUP__ECLIPSE_VERSION,
              oldEclipseVersion, eclipseVersion));
        }
      }
    }
    return eclipseVersion;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Eclipse basicGetEclipseVersion()
  {
    return eclipseVersion;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setEclipseVersion(Eclipse newEclipseVersion)
  {
    Eclipse oldEclipseVersion = eclipseVersion;
    eclipseVersion = newEclipseVersion;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.SETUP__ECLIPSE_VERSION, oldEclipseVersion,
          eclipseVersion));
    }
  }

  /**
   * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
   * @generated
   */
  public Preferences getPreferences()
  {
    if (preferences != null && preferences.eIsProxy())
    {
      InternalEObject oldPreferences = (InternalEObject)preferences;
      preferences = (Preferences)eResolveProxy(oldPreferences);
      if (preferences != oldPreferences)
      {
        if (eNotificationRequired())
        {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, SetupPackage.SETUP__PREFERENCES, oldPreferences,
              preferences));
        }
      }
    }
    return preferences;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Preferences basicGetPreferences()
  {
    return preferences;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setPreferences(Preferences newPreferences)
  {
    Preferences oldPreferences = preferences;
    preferences = newPreferences;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.SETUP__PREFERENCES, oldPreferences,
          preferences));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public EList<SetupTask> getSetupTasks(boolean filterRestrictions, Trigger trigger)
  {
    Map<Integer, EList<SetupTask>> setupTasks = new HashMap<Integer, EList<SetupTask>>();

    Eclipse eclipse = getEclipseVersion();
    Branch branch = getBranch();

    if (!eclipse.eIsProxy() && !branch.eIsProxy())
    {
      Configuration configuration = eclipse.getConfiguration();
      Project project = branch.getProject();

      getSetupTasks(filterRestrictions, trigger, setupTasks, configuration);
      getSetupTasks(filterRestrictions, trigger, setupTasks, eclipse);
      getSetupTasks(filterRestrictions, trigger, setupTasks, project);
      getSetupTasks(filterRestrictions, trigger, setupTasks, branch);

      Preferences preferences = getPreferences();
      if (!preferences.eIsProxy())
      {
        getSetupTasks(filterRestrictions, trigger, setupTasks, preferences);
      }
    }

    Set<Map.Entry<Integer, EList<SetupTask>>> entrySet = setupTasks.entrySet();
    List<Map.Entry<Integer, EList<SetupTask>>> list = new ArrayList<Map.Entry<Integer, EList<SetupTask>>>(entrySet);
    Collections.sort(list, new Comparator<Map.Entry<Integer, EList<SetupTask>>>()
    {
      public int compare(Entry<Integer, EList<SetupTask>> o1, Entry<Integer, EList<SetupTask>> o2)
      {
        return o1.getKey().compareTo(o2.getKey());
      }
    });

    EList<SetupTask> result = new BasicEList<SetupTask>();
    for (Map.Entry<Integer, EList<SetupTask>> entry : list)
    {
      result.addAll(entry.getValue());
    }

    return result;
  }

  private void getSetupTasks(boolean filterRestrictions, Trigger trigger, Map<Integer, EList<SetupTask>> setupTasks,
      SetupTaskContainer setupTaskContainer)
  {
    Branch branch = getBranch();
    Project project = branch.getProject();
    Eclipse eclipse = getEclipseVersion();

    for (SetupTask setupTask : setupTaskContainer.getSetupTasks())
    {
      if (setupTask.isDisabled())
      {
        continue;
      }

      if (trigger != null && !setupTask.getTriggers().contains(trigger))
      {
        continue;
      }

      EList<ConfigurableItem> restrictions = setupTask.getRestrictions();
      if (!restrictions.isEmpty() && !restrictions.contains(branch) && !restrictions.contains(project)
          && !restrictions.contains(eclipse))
      {
        continue;
      }

      if (setupTask instanceof SetupTaskContainer)
      {
        SetupTaskContainer container = (SetupTaskContainer)setupTask;
        getSetupTasks(filterRestrictions, trigger, setupTasks, container);
      }
      else
      {
        int priority = setupTask.getPriority();

        EList<SetupTask> list = setupTasks.get(priority);
        if (list == null)
        {
          list = new BasicEList<SetupTask>();
          setupTasks.put(priority, list);
        }

        list.add(setupTask);
      }
    }
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
    case SetupPackage.SETUP__BRANCH:
      if (resolve)
      {
        return getBranch();
      }
      return basicGetBranch();
    case SetupPackage.SETUP__ECLIPSE_VERSION:
      if (resolve)
      {
        return getEclipseVersion();
      }
      return basicGetEclipseVersion();
    case SetupPackage.SETUP__PREFERENCES:
      if (resolve)
      {
        return getPreferences();
      }
      return basicGetPreferences();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case SetupPackage.SETUP__BRANCH:
      setBranch((Branch)newValue);
      return;
    case SetupPackage.SETUP__ECLIPSE_VERSION:
      setEclipseVersion((Eclipse)newValue);
      return;
    case SetupPackage.SETUP__PREFERENCES:
      setPreferences((Preferences)newValue);
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
    case SetupPackage.SETUP__BRANCH:
      setBranch((Branch)null);
      return;
    case SetupPackage.SETUP__ECLIPSE_VERSION:
      setEclipseVersion((Eclipse)null);
      return;
    case SetupPackage.SETUP__PREFERENCES:
      setPreferences((Preferences)null);
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
    case SetupPackage.SETUP__BRANCH:
      return branch != null;
    case SetupPackage.SETUP__ECLIPSE_VERSION:
      return eclipseVersion != null;
    case SetupPackage.SETUP__PREFERENCES:
      return preferences != null;
    }
    return super.eIsSet(featureID);
  }

} // WorkspaceImpl
