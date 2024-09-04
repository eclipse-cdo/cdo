/*
 * Copyright (c) 2022, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.impl;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPointRef;
import org.eclipse.emf.cdo.common.branch.CDOBranchRef;
import org.eclipse.emf.cdo.lm.Baseline;
import org.eclipse.emf.cdo.lm.Change;
import org.eclipse.emf.cdo.lm.Delivery;
import org.eclipse.emf.cdo.lm.Drop;
import org.eclipse.emf.cdo.lm.Impact;
import org.eclipse.emf.cdo.lm.LMPackage;
import org.eclipse.emf.cdo.lm.Stream;
import org.eclipse.emf.cdo.lm.StreamMode;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.InternalEList;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * <!-- begin-user-doc --> An implementation of the model object
 * '<em><b>Stream</b></em>'.
 * @noextend This class is not intended to be subclassed by clients.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.impl.StreamImpl#getModule <em>Module</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.impl.StreamImpl#getBase <em>Base</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.impl.StreamImpl#getStartTimeStamp <em>Start Time Stamp</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.impl.StreamImpl#getMajorVersion <em>Major Version</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.impl.StreamImpl#getMinorVersion <em>Minor Version</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.impl.StreamImpl#getCodeName <em>Code Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.impl.StreamImpl#getAllowedChanges <em>Allowed Changes</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.impl.StreamImpl#getMode <em>Mode</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.impl.StreamImpl#getDevelopmentBranch <em>Development Branch</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.impl.StreamImpl#getMaintenanceBranch <em>Maintenance Branch</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.impl.StreamImpl#getContents <em>Contents</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.impl.StreamImpl#getMaintenanceTimeStamp <em>Maintenance Time Stamp</em>}</li>
 * </ul>
 *
 * @generated
 */
public class StreamImpl extends FloatingBaselineImpl implements Stream
{
  /**
   * The default value of the '{@link #getStartTimeStamp() <em>Start Time Stamp</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getStartTimeStamp()
   * @generated
   * @ordered
   */
  protected static final long START_TIME_STAMP_EDEFAULT = 0L;

  /**
   * The default value of the '{@link #getMajorVersion() <em>Major Version</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getMajorVersion()
   * @generated
   * @ordered
   */
  protected static final int MAJOR_VERSION_EDEFAULT = 0;

  /**
   * The default value of the '{@link #getMinorVersion() <em>Minor Version</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getMinorVersion()
   * @generated NOT
   * @ordered
   */
  protected static final int MINOR_VERSION_EDEFAULT = 1;

  /**
   * The default value of the '{@link #getCodeName() <em>Code Name</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getCodeName()
   * @generated
   * @ordered
   */
  protected static final String CODE_NAME_EDEFAULT = null;

  /**
   * The default value of the '{@link #getAllowedChanges() <em>Allowed Changes</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getAllowedChanges()
   * @generated
   * @ordered
   */
  protected static final Impact ALLOWED_CHANGES_EDEFAULT = Impact.MINOR;

  /**
   * The default value of the '{@link #getMode() <em>Mode</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getMode()
   * @generated
   * @ordered
   */
  protected static final StreamMode MODE_EDEFAULT = StreamMode.DEVELOPMENT;

  /**
   * The default value of the '{@link #getDevelopmentBranch() <em>Development
   * Branch</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getDevelopmentBranch()
   * @generated NOT
   * @ordered
   */
  protected static final CDOBranchRef DEVELOPMENT_BRANCH_EDEFAULT = new CDOBranchRef(CDOBranch.PATH_SEPARATOR + CDOBranch.MAIN_BRANCH_NAME);

  /**
   * The default value of the '{@link #getMaintenanceBranch() <em>Maintenance
   * Branch</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getMaintenanceBranch()
   * @generated NOT
   * @ordered
   */
  protected static final CDOBranchRef MAINTENANCE_BRANCH_EDEFAULT = null;

  /**
   * The default value of the '{@link #getMaintenanceTimeStamp() <em>Maintenance Time Stamp</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getMaintenanceTimeStamp()
   * @generated
   * @ordered
   */
  protected static final long MAINTENANCE_TIME_STAMP_EDEFAULT = 0L;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected StreamImpl()
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
    return LMPackage.Literals.STREAM;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public org.eclipse.emf.cdo.lm.Module getModule()
  {
    return (org.eclipse.emf.cdo.lm.Module)eDynamicGet(LMPackage.STREAM__MODULE, LMPackage.Literals.STREAM__MODULE, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetModule(org.eclipse.emf.cdo.lm.Module newModule, NotificationChain msgs)
  {
    msgs = eBasicSetContainer((InternalEObject)newModule, LMPackage.STREAM__MODULE, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setModule(org.eclipse.emf.cdo.lm.Module newModule)
  {
    eDynamicSet(LMPackage.STREAM__MODULE, LMPackage.Literals.STREAM__MODULE, newModule);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Drop getBase()
  {
    return (Drop)eDynamicGet(LMPackage.STREAM__BASE, LMPackage.Literals.STREAM__BASE, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public Drop basicGetBase()
  {
    return (Drop)eDynamicGet(LMPackage.STREAM__BASE, LMPackage.Literals.STREAM__BASE, false, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setBase(Drop newBase)
  {
    eDynamicSet(LMPackage.STREAM__BASE, LMPackage.Literals.STREAM__BASE, newBase);
  }

  @Override
  public long getBaseTimeStamp()
  {
    return getStartTimeStamp();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public long getStartTimeStamp()
  {
    return (Long)eDynamicGet(LMPackage.STREAM__START_TIME_STAMP, LMPackage.Literals.STREAM__START_TIME_STAMP, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setStartTimeStamp(long newStartTimeStamp)
  {
    eDynamicSet(LMPackage.STREAM__START_TIME_STAMP, LMPackage.Literals.STREAM__START_TIME_STAMP, newStartTimeStamp);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int getMajorVersion()
  {
    return (Integer)eDynamicGet(LMPackage.STREAM__MAJOR_VERSION, LMPackage.Literals.STREAM__MAJOR_VERSION, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setMajorVersion(int newMajorVersion)
  {
    eDynamicSet(LMPackage.STREAM__MAJOR_VERSION, LMPackage.Literals.STREAM__MAJOR_VERSION, newMajorVersion);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int getMinorVersion()
  {
    return (Integer)eDynamicGet(LMPackage.STREAM__MINOR_VERSION, LMPackage.Literals.STREAM__MINOR_VERSION, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setMinorVersion(int newMinorVersion)
  {
    eDynamicSet(LMPackage.STREAM__MINOR_VERSION, LMPackage.Literals.STREAM__MINOR_VERSION, newMinorVersion);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getCodeName()
  {
    return (String)eDynamicGet(LMPackage.STREAM__CODE_NAME, LMPackage.Literals.STREAM__CODE_NAME, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setCodeName(String newCodeName)
  {
    eDynamicSet(LMPackage.STREAM__CODE_NAME, LMPackage.Literals.STREAM__CODE_NAME, newCodeName);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Impact getAllowedChanges()
  {
    return (Impact)eDynamicGet(LMPackage.STREAM__ALLOWED_CHANGES, LMPackage.Literals.STREAM__ALLOWED_CHANGES, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAllowedChanges(Impact newAllowedChanges)
  {
    eDynamicSet(LMPackage.STREAM__ALLOWED_CHANGES, LMPackage.Literals.STREAM__ALLOWED_CHANGES, newAllowedChanges);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public EList<Baseline> getContents()
  {
    return (EList<Baseline>)eDynamicGet(LMPackage.STREAM__CONTENTS, LMPackage.Literals.STREAM__CONTENTS, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public long getMaintenanceTimeStamp()
  {
    return (Long)eDynamicGet(LMPackage.STREAM__MAINTENANCE_TIME_STAMP, LMPackage.Literals.STREAM__MAINTENANCE_TIME_STAMP, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setMaintenanceTimeStamp(long newMaintenanceTimeStamp)
  {
    eDynamicSet(LMPackage.STREAM__MAINTENANCE_TIME_STAMP, LMPackage.Literals.STREAM__MAINTENANCE_TIME_STAMP, newMaintenanceTimeStamp);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  @Override
  public int insertContent(Baseline baseline)
  {
    EList<Baseline> contents = getContents();

    int index = Collections.binarySearch(contents, baseline, COMPARATOR);
    if (index < 0)
    {
      index = -index - 1;
    }

    contents.add(index, baseline);
    return index;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  @Override
  public CDOBranchPointRef getBranchPoint(long timeStamp)
  {
    CDOBranchRef maintenanceBranch = getMaintenanceBranch();
    if (maintenanceBranch != null)
    {
      long maintenanceTimeStamp = getMaintenanceTimeStamp();
      if (timeStamp >= maintenanceTimeStamp)
      {
        String maintenanceBranchPath = maintenanceBranch.getBranchPath();
        return new CDOBranchPointRef(maintenanceBranchPath, timeStamp);
      }
    }

    String developmentBranchPath = getDevelopmentBranch().getBranchPath();
    return new CDOBranchPointRef(developmentBranchPath, timeStamp);
  }

  @Override
  public EList<Delivery> getDeliveries()
  {
    EList<Delivery> result = new BasicEList<>();

    for (Baseline baseline : getContents())
    {
      if (baseline instanceof Change)
      {
        Change change = (Change)baseline;
        result.addAll(change.getDeliveries());
      }
    }

    return result;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  @Override
  public StreamMode getMode()
  {
    if (isClosed())
    {
      return StreamMode.CLOSED;
    }

    if (getMaintenanceBranch() != null)
    {
      return StreamMode.MAINTENANCE;
    }

    return StreamMode.DEVELOPMENT;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public CDOBranchRef getDevelopmentBranch()
  {
    return (CDOBranchRef)eDynamicGet(LMPackage.STREAM__DEVELOPMENT_BRANCH, LMPackage.Literals.STREAM__DEVELOPMENT_BRANCH, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setDevelopmentBranch(CDOBranchRef newDevelopmentBranch)
  {
    eDynamicSet(LMPackage.STREAM__DEVELOPMENT_BRANCH, LMPackage.Literals.STREAM__DEVELOPMENT_BRANCH, newDevelopmentBranch);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public CDOBranchRef getMaintenanceBranch()
  {
    return (CDOBranchRef)eDynamicGet(LMPackage.STREAM__MAINTENANCE_BRANCH, LMPackage.Literals.STREAM__MAINTENANCE_BRANCH, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setMaintenanceBranch(CDOBranchRef newMaintenanceBranch)
  {
    eDynamicSet(LMPackage.STREAM__MAINTENANCE_BRANCH, LMPackage.Literals.STREAM__MAINTENANCE_BRANCH, newMaintenanceBranch);
  }

  @Override
  public CDOBranchRef getBranch()
  {
    CDOBranchRef maintenanceBranch = getMaintenanceBranch();
    if (maintenanceBranch != null)
    {
      return maintenanceBranch;
    }

    return getDevelopmentBranch();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  @Override
  public Drop getFirstRelease()
  {
    if (getMaintenanceBranch() != null)
    {
      EList<Baseline> contents = getContents();
      int size = contents.size();
      for (int i = 0; i < size; ++i)
      {
        Baseline baseline = contents.get(i);
        if (baseline instanceof Drop)
        {
          Drop drop = (Drop)baseline;
          if (drop.isRelease())
          {
            return drop;
          }
        }
      }
    }

    return null;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  @Override
  public Drop getLastRelease()
  {
    if (getMaintenanceBranch() != null)
    {
      EList<Baseline> contents = getContents();
      int size = contents.size();
      for (int i = size - 1; i >= 0; --i)
      {
        Baseline baseline = contents.get(i);
        if (baseline instanceof Drop)
        {
          Drop drop = (Drop)baseline;
          if (drop.isRelease())
          {
            return drop;
          }
        }
      }
    }

    return null;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  @Override
  public EList<Drop> getReleases()
  {
    EList<Drop> result = new BasicEList<>();

    if (getMaintenanceBranch() != null)
    {
      for (Baseline baseline : getContents())
      {
        if (baseline instanceof Drop)
        {
          Drop drop = (Drop)baseline;
          if (drop.isRelease())
          {
            result.add(drop);
          }
        }
      }
    }

    return result;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  @Override
  public EList<Change> getBasedChanges()
  {
    EList<Change> result = new BasicEList<>();

    for (Baseline baseline : getContents())
    {
      if (baseline instanceof Change)
      {
        Change change = (Change)baseline;
        if (change.getBase() == null)
        {
          result.add(change);
        }
      }
    }

    return result;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case LMPackage.STREAM__MODULE:
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      return basicSetModule((org.eclipse.emf.cdo.lm.Module)otherEnd, msgs);
    case LMPackage.STREAM__CONTENTS:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getContents()).basicAdd(otherEnd, msgs);
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
    case LMPackage.STREAM__MODULE:
      return basicSetModule(null, msgs);
    case LMPackage.STREAM__CONTENTS:
      return ((InternalEList<?>)getContents()).basicRemove(otherEnd, msgs);
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
    case LMPackage.STREAM__MODULE:
      return eInternalContainer().eInverseRemove(this, LMPackage.MODULE__STREAMS, org.eclipse.emf.cdo.lm.Module.class, msgs);
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
    case LMPackage.STREAM__MODULE:
      return getModule();
    case LMPackage.STREAM__BASE:
      if (resolve)
      {
        return getBase();
      }
      return basicGetBase();
    case LMPackage.STREAM__START_TIME_STAMP:
      return getStartTimeStamp();
    case LMPackage.STREAM__MAJOR_VERSION:
      return getMajorVersion();
    case LMPackage.STREAM__MINOR_VERSION:
      return getMinorVersion();
    case LMPackage.STREAM__CODE_NAME:
      return getCodeName();
    case LMPackage.STREAM__ALLOWED_CHANGES:
      return getAllowedChanges();
    case LMPackage.STREAM__MODE:
      return getMode();
    case LMPackage.STREAM__DEVELOPMENT_BRANCH:
      return getDevelopmentBranch();
    case LMPackage.STREAM__MAINTENANCE_BRANCH:
      return getMaintenanceBranch();
    case LMPackage.STREAM__CONTENTS:
      return getContents();
    case LMPackage.STREAM__MAINTENANCE_TIME_STAMP:
      return getMaintenanceTimeStamp();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case LMPackage.STREAM__MODULE:
      setModule((org.eclipse.emf.cdo.lm.Module)newValue);
      return;
    case LMPackage.STREAM__BASE:
      setBase((Drop)newValue);
      return;
    case LMPackage.STREAM__START_TIME_STAMP:
      setStartTimeStamp((Long)newValue);
      return;
    case LMPackage.STREAM__MAJOR_VERSION:
      setMajorVersion((Integer)newValue);
      return;
    case LMPackage.STREAM__MINOR_VERSION:
      setMinorVersion((Integer)newValue);
      return;
    case LMPackage.STREAM__CODE_NAME:
      setCodeName((String)newValue);
      return;
    case LMPackage.STREAM__ALLOWED_CHANGES:
      setAllowedChanges((Impact)newValue);
      return;
    case LMPackage.STREAM__DEVELOPMENT_BRANCH:
      setDevelopmentBranch((CDOBranchRef)newValue);
      return;
    case LMPackage.STREAM__MAINTENANCE_BRANCH:
      setMaintenanceBranch((CDOBranchRef)newValue);
      return;
    case LMPackage.STREAM__CONTENTS:
      getContents().clear();
      getContents().addAll((Collection<? extends Baseline>)newValue);
      return;
    case LMPackage.STREAM__MAINTENANCE_TIME_STAMP:
      setMaintenanceTimeStamp((Long)newValue);
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
    case LMPackage.STREAM__MODULE:
      setModule((org.eclipse.emf.cdo.lm.Module)null);
      return;
    case LMPackage.STREAM__BASE:
      setBase((Drop)null);
      return;
    case LMPackage.STREAM__START_TIME_STAMP:
      setStartTimeStamp(START_TIME_STAMP_EDEFAULT);
      return;
    case LMPackage.STREAM__MAJOR_VERSION:
      setMajorVersion(MAJOR_VERSION_EDEFAULT);
      return;
    case LMPackage.STREAM__MINOR_VERSION:
      setMinorVersion(MINOR_VERSION_EDEFAULT);
      return;
    case LMPackage.STREAM__CODE_NAME:
      setCodeName(CODE_NAME_EDEFAULT);
      return;
    case LMPackage.STREAM__ALLOWED_CHANGES:
      setAllowedChanges(ALLOWED_CHANGES_EDEFAULT);
      return;
    case LMPackage.STREAM__DEVELOPMENT_BRANCH:
      setDevelopmentBranch(DEVELOPMENT_BRANCH_EDEFAULT);
      return;
    case LMPackage.STREAM__MAINTENANCE_BRANCH:
      setMaintenanceBranch(MAINTENANCE_BRANCH_EDEFAULT);
      return;
    case LMPackage.STREAM__CONTENTS:
      getContents().clear();
      return;
    case LMPackage.STREAM__MAINTENANCE_TIME_STAMP:
      setMaintenanceTimeStamp(MAINTENANCE_TIME_STAMP_EDEFAULT);
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
    case LMPackage.STREAM__MODULE:
      return getModule() != null;
    case LMPackage.STREAM__BASE:
      return basicGetBase() != null;
    case LMPackage.STREAM__START_TIME_STAMP:
      return getStartTimeStamp() != START_TIME_STAMP_EDEFAULT;
    case LMPackage.STREAM__MAJOR_VERSION:
      return getMajorVersion() != MAJOR_VERSION_EDEFAULT;
    case LMPackage.STREAM__MINOR_VERSION:
      return getMinorVersion() != MINOR_VERSION_EDEFAULT;
    case LMPackage.STREAM__CODE_NAME:
      return CODE_NAME_EDEFAULT == null ? getCodeName() != null : !CODE_NAME_EDEFAULT.equals(getCodeName());
    case LMPackage.STREAM__ALLOWED_CHANGES:
      return getAllowedChanges() != ALLOWED_CHANGES_EDEFAULT;
    case LMPackage.STREAM__MODE:
      return getMode() != MODE_EDEFAULT;
    case LMPackage.STREAM__DEVELOPMENT_BRANCH:
      return DEVELOPMENT_BRANCH_EDEFAULT == null ? getDevelopmentBranch() != null : !DEVELOPMENT_BRANCH_EDEFAULT.equals(getDevelopmentBranch());
    case LMPackage.STREAM__MAINTENANCE_BRANCH:
      return MAINTENANCE_BRANCH_EDEFAULT == null ? getMaintenanceBranch() != null : !MAINTENANCE_BRANCH_EDEFAULT.equals(getMaintenanceBranch());
    case LMPackage.STREAM__CONTENTS:
      return !getContents().isEmpty();
    case LMPackage.STREAM__MAINTENANCE_TIME_STAMP:
      return getMaintenanceTimeStamp() != MAINTENANCE_TIME_STAMP_EDEFAULT;
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException
  {
    switch (operationID)
    {
    case LMPackage.STREAM___INSERT_CONTENT__BASELINE:
      return insertContent((Baseline)arguments.get(0));
    case LMPackage.STREAM___GET_BRANCH_POINT__LONG:
      return getBranchPoint((Long)arguments.get(0));
    case LMPackage.STREAM___GET_FIRST_RELEASE:
      return getFirstRelease();
    case LMPackage.STREAM___GET_LAST_RELEASE:
      return getLastRelease();
    case LMPackage.STREAM___GET_RELEASES:
      return getReleases();
    case LMPackage.STREAM___GET_BASED_CHANGES:
      return getBasedChanges();
    }
    return super.eInvoke(operationID, arguments);
  }

  @Override
  public String getName()
  {
    return getMajorVersion() + "." + getMinorVersion();
  }

  @Override
  public void forEachBaseline(Consumer<Baseline> consumer)
  {
    consumer.accept(this);

    for (Baseline baseline : getContents())
    {
      consumer.accept(baseline);
    }
  }

  @Override
  public boolean forEachBaseline(Predicate<Baseline> predicate)
  {
    if (predicate.test(this))
    {
      return true;
    }

    for (Baseline baseline : getContents())
    {
      if (predicate.test(baseline))
      {
        return true;
      }
    }

    return false;
  }

  @Override
  public Baseline getBaseline(String baselineName)
  {
    for (Baseline baseline : getContents())
    {
      if (Objects.equals(baseline.getName(), baselineName))
      {
        return baseline;
      }
    }

    return null;
  }

  @Override
  public Delivery getDelivery(Change change)
  {
    for (Baseline baseline : getContents())
    {
      if (baseline instanceof Delivery)
      {
        Delivery delivery = (Delivery)baseline;
        if (delivery.getChange() == change)
        {
          return delivery;
        }
      }
    }

    return null;
  }

} // StreamImpl
