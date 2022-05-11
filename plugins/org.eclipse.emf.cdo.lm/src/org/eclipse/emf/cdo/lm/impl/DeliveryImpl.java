/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.impl;

import org.eclipse.emf.cdo.common.branch.CDOBranchPointRef;
import org.eclipse.emf.cdo.etypes.EtypesFactory;
import org.eclipse.emf.cdo.etypes.EtypesPackage;
import org.eclipse.emf.cdo.lm.Change;
import org.eclipse.emf.cdo.lm.Delivery;
import org.eclipse.emf.cdo.lm.LMPackage;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

/**
 * <!-- begin-user-doc --> An implementation of the model object
 * '<em><b>Delivery</b></em>'.
 * @noextend This class is not intended to be subclassed by clients.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.impl.DeliveryImpl#getChange <em>Change</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.impl.DeliveryImpl#getMergeSource <em>Merge Source</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.impl.DeliveryImpl#getMergeTarget <em>Merge Target</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DeliveryImpl extends FixedBaselineImpl implements Delivery
{
  /**
   * The default value of the '{@link #getMergeSource() <em>Merge Source</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getMergeSource()
   * @generated
   * @ordered
   */
  protected static final CDOBranchPointRef MERGE_SOURCE_EDEFAULT = (CDOBranchPointRef)EtypesFactory.eINSTANCE
      .createFromString(EtypesPackage.eINSTANCE.getBranchPointRef(), "");

  /**
   * The default value of the '{@link #getMergeTarget() <em>Merge Target</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getMergeTarget()
   * @generated
   * @ordered
   */
  protected static final CDOBranchPointRef MERGE_TARGET_EDEFAULT = (CDOBranchPointRef)EtypesFactory.eINSTANCE
      .createFromString(EtypesPackage.eINSTANCE.getBranchPointRef(), "");

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected DeliveryImpl()
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
    return LMPackage.Literals.DELIVERY;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Change getChange()
  {
    return (Change)eDynamicGet(LMPackage.DELIVERY__CHANGE, LMPackage.Literals.DELIVERY__CHANGE, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public Change basicGetChange()
  {
    return (Change)eDynamicGet(LMPackage.DELIVERY__CHANGE, LMPackage.Literals.DELIVERY__CHANGE, false, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetChange(Change newChange, NotificationChain msgs)
  {
    msgs = eDynamicInverseAdd((InternalEObject)newChange, LMPackage.DELIVERY__CHANGE, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setChange(Change newChange)
  {
    eDynamicSet(LMPackage.DELIVERY__CHANGE, LMPackage.Literals.DELIVERY__CHANGE, newChange);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public CDOBranchPointRef getMergeSource()
  {
    return (CDOBranchPointRef)eDynamicGet(LMPackage.DELIVERY__MERGE_SOURCE, LMPackage.Literals.DELIVERY__MERGE_SOURCE, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setMergeSource(CDOBranchPointRef newMergeSource)
  {
    eDynamicSet(LMPackage.DELIVERY__MERGE_SOURCE, LMPackage.Literals.DELIVERY__MERGE_SOURCE, newMergeSource);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public CDOBranchPointRef getMergeTarget()
  {
    return (CDOBranchPointRef)eDynamicGet(LMPackage.DELIVERY__MERGE_TARGET, LMPackage.Literals.DELIVERY__MERGE_TARGET, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setMergeTarget(CDOBranchPointRef newMergeTarget)
  {
    eDynamicSet(LMPackage.DELIVERY__MERGE_TARGET, LMPackage.Literals.DELIVERY__MERGE_TARGET, newMergeTarget);
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
    case LMPackage.DELIVERY__CHANGE:
      Change change = basicGetChange();
      if (change != null)
      {
        msgs = ((InternalEObject)change).eInverseRemove(this, LMPackage.CHANGE__DELIVERIES, Change.class, msgs);
      }
      return basicSetChange((Change)otherEnd, msgs);
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
    case LMPackage.DELIVERY__CHANGE:
      return basicSetChange(null, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
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
    case LMPackage.DELIVERY__CHANGE:
      if (resolve)
      {
        return getChange();
      }
      return basicGetChange();
    case LMPackage.DELIVERY__MERGE_SOURCE:
      return getMergeSource();
    case LMPackage.DELIVERY__MERGE_TARGET:
      return getMergeTarget();
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
    case LMPackage.DELIVERY__CHANGE:
      setChange((Change)newValue);
      return;
    case LMPackage.DELIVERY__MERGE_SOURCE:
      setMergeSource((CDOBranchPointRef)newValue);
      return;
    case LMPackage.DELIVERY__MERGE_TARGET:
      setMergeTarget((CDOBranchPointRef)newValue);
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
    case LMPackage.DELIVERY__CHANGE:
      setChange((Change)null);
      return;
    case LMPackage.DELIVERY__MERGE_SOURCE:
      setMergeSource(MERGE_SOURCE_EDEFAULT);
      return;
    case LMPackage.DELIVERY__MERGE_TARGET:
      setMergeTarget(MERGE_TARGET_EDEFAULT);
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
    case LMPackage.DELIVERY__CHANGE:
      return basicGetChange() != null;
    case LMPackage.DELIVERY__MERGE_SOURCE:
      return MERGE_SOURCE_EDEFAULT == null ? getMergeSource() != null : !MERGE_SOURCE_EDEFAULT.equals(getMergeSource());
    case LMPackage.DELIVERY__MERGE_TARGET:
      return MERGE_TARGET_EDEFAULT == null ? getMergeTarget() != null : !MERGE_TARGET_EDEFAULT.equals(getMergeTarget());
    }
    return super.eIsSet(featureID);
  }

  @Override
  public CDOBranchPointRef getBranchPoint()
  {
    return getMergeTarget();
  }

  @Override
  public String getName()
  {
    Change change = getChange();
    if (change == null)
    {
      return "???";
    }

    return change.getName();
  }

} // DeliveryImpl
