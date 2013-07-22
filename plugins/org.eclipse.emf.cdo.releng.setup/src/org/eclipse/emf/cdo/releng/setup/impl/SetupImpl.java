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
import org.eclipse.emf.cdo.releng.setup.EclipseVersion;
import org.eclipse.emf.cdo.releng.setup.Preferences;
import org.eclipse.emf.cdo.releng.setup.Setup;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

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
  protected EclipseVersion eclipseVersion;

  /**
   * The cached value of the '{@link #getPreferences() <em>Preferences</em>}' containment reference.
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
  public EclipseVersion getEclipseVersion()
  {
    if (eclipseVersion != null && eclipseVersion.eIsProxy())
    {
      InternalEObject oldEclipseVersion = (InternalEObject)eclipseVersion;
      eclipseVersion = (EclipseVersion)eResolveProxy(oldEclipseVersion);
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
  public EclipseVersion basicGetEclipseVersion()
  {
    return eclipseVersion;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setEclipseVersion(EclipseVersion newEclipseVersion)
  {
    EclipseVersion oldEclipseVersion = eclipseVersion;
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
    return preferences;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetPreferences(Preferences newPreferences, NotificationChain msgs)
  {
    Preferences oldPreferences = preferences;
    preferences = newPreferences;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, SetupPackage.SETUP__PREFERENCES,
          oldPreferences, newPreferences);
      if (msgs == null)
      {
        msgs = notification;
      }
      else
      {
        msgs.add(notification);
      }
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setPreferences(Preferences newPreferences)
  {
    if (newPreferences != preferences)
    {
      NotificationChain msgs = null;
      if (preferences != null)
      {
        msgs = ((InternalEObject)preferences).eInverseRemove(this, EOPPOSITE_FEATURE_BASE
            - SetupPackage.SETUP__PREFERENCES, null, msgs);
      }
      if (newPreferences != null)
      {
        msgs = ((InternalEObject)newPreferences).eInverseAdd(this, EOPPOSITE_FEATURE_BASE
            - SetupPackage.SETUP__PREFERENCES, null, msgs);
      }
      msgs = basicSetPreferences(newPreferences, msgs);
      if (msgs != null)
      {
        msgs.dispatch();
      }
    }
    else if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.SETUP__PREFERENCES, newPreferences,
          newPreferences));
    }
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
    case SetupPackage.SETUP__PREFERENCES:
      return basicSetPreferences(null, msgs);
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
      return getPreferences();
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
      setEclipseVersion((EclipseVersion)newValue);
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
      setEclipseVersion((EclipseVersion)null);
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
