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

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchPointRef;
import org.eclipse.emf.cdo.common.branch.CDOBranchRef;
import org.eclipse.emf.cdo.etypes.EtypesFactory;
import org.eclipse.emf.cdo.etypes.EtypesPackage;
import org.eclipse.emf.cdo.lm.BasePoint;
import org.eclipse.emf.cdo.lm.Change;
import org.eclipse.emf.cdo.lm.Delivery;
import org.eclipse.emf.cdo.lm.Drop;
import org.eclipse.emf.cdo.lm.FixedBaseline;
import org.eclipse.emf.cdo.lm.Impact;
import org.eclipse.emf.cdo.lm.LMPackage;
import org.eclipse.emf.cdo.lm.Stream;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.InternalEList;

import java.util.Collection;

/**
 * <!-- begin-user-doc --> An implementation of the model object
 * '<em><b>Change</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.impl.ChangeImpl#getBase <em>Base</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.impl.ChangeImpl#getLabel <em>Label</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.impl.ChangeImpl#getImpact <em>Impact</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.impl.ChangeImpl#getBranch <em>Branch</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.impl.ChangeImpl#getDeliveries <em>Deliveries</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ChangeImpl extends FloatingBaselineImpl implements Change
{
  /**
   * The default value of the '{@link #getLabel() <em>Label</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getLabel()
   * @generated
   * @ordered
   */
  protected static final String LABEL_EDEFAULT = null;

  /**
   * The default value of the '{@link #getImpact() <em>Impact</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getImpact()
   * @generated
   * @ordered
   */
  protected static final Impact IMPACT_EDEFAULT = Impact.MICRO;

  /**
   * The default value of the '{@link #getBranch() <em>Branch</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getBranch()
   * @generated
   * @ordered
   */
  protected static final CDOBranchRef BRANCH_EDEFAULT = (CDOBranchRef)EtypesFactory.eINSTANCE.createFromString(EtypesPackage.eINSTANCE.getBranchRef(), "");

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected ChangeImpl()
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
    return LMPackage.Literals.CHANGE;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public FixedBaseline getBase()
  {
    return (FixedBaseline)eDynamicGet(LMPackage.CHANGE__BASE, LMPackage.Literals.CHANGE__BASE, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public FixedBaseline basicGetBase()
  {
    return (FixedBaseline)eDynamicGet(LMPackage.CHANGE__BASE, LMPackage.Literals.CHANGE__BASE, false, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setBase(FixedBaseline newBase)
  {
    eDynamicSet(LMPackage.CHANGE__BASE, LMPackage.Literals.CHANGE__BASE, newBase);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getLabel()
  {
    return (String)eDynamicGet(LMPackage.CHANGE__LABEL, LMPackage.Literals.CHANGE__LABEL, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setLabel(String newLabel)
  {
    eDynamicSet(LMPackage.CHANGE__LABEL, LMPackage.Literals.CHANGE__LABEL, newLabel);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Impact getImpact()
  {
    return (Impact)eDynamicGet(LMPackage.CHANGE__IMPACT, LMPackage.Literals.CHANGE__IMPACT, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setImpact(Impact newImpact)
  {
    eDynamicSet(LMPackage.CHANGE__IMPACT, LMPackage.Literals.CHANGE__IMPACT, newImpact);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public CDOBranchRef getBranch()
  {
    return (CDOBranchRef)eDynamicGet(LMPackage.CHANGE__BRANCH, LMPackage.Literals.CHANGE__BRANCH, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setBranch(CDOBranchRef newBranch)
  {
    eDynamicSet(LMPackage.CHANGE__BRANCH, LMPackage.Literals.CHANGE__BRANCH, newBranch);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public EList<Delivery> getDeliveries()
  {
    return (EList<Delivery>)eDynamicGet(LMPackage.CHANGE__DELIVERIES, LMPackage.Literals.CHANGE__DELIVERIES, true, true);
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
    case LMPackage.CHANGE__DELIVERIES:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getDeliveries()).basicAdd(otherEnd, msgs);
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
    case LMPackage.CHANGE__DELIVERIES:
      return ((InternalEList<?>)getDeliveries()).basicRemove(otherEnd, msgs);
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
    case LMPackage.CHANGE__BASE:
      if (resolve)
      {
        return getBase();
      }
      return basicGetBase();
    case LMPackage.CHANGE__LABEL:
      return getLabel();
    case LMPackage.CHANGE__IMPACT:
      return getImpact();
    case LMPackage.CHANGE__BRANCH:
      return getBranch();
    case LMPackage.CHANGE__DELIVERIES:
      return getDeliveries();
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
    case LMPackage.CHANGE__BASE:
      setBase((FixedBaseline)newValue);
      return;
    case LMPackage.CHANGE__LABEL:
      setLabel((String)newValue);
      return;
    case LMPackage.CHANGE__IMPACT:
      setImpact((Impact)newValue);
      return;
    case LMPackage.CHANGE__BRANCH:
      setBranch((CDOBranchRef)newValue);
      return;
    case LMPackage.CHANGE__DELIVERIES:
      getDeliveries().clear();
      getDeliveries().addAll((Collection<? extends Delivery>)newValue);
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
    case LMPackage.CHANGE__BASE:
      setBase((FixedBaseline)null);
      return;
    case LMPackage.CHANGE__LABEL:
      setLabel(LABEL_EDEFAULT);
      return;
    case LMPackage.CHANGE__IMPACT:
      setImpact(IMPACT_EDEFAULT);
      return;
    case LMPackage.CHANGE__BRANCH:
      setBranch(BRANCH_EDEFAULT);
      return;
    case LMPackage.CHANGE__DELIVERIES:
      getDeliveries().clear();
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
    case LMPackage.CHANGE__BASE:
      return basicGetBase() != null;
    case LMPackage.CHANGE__LABEL:
      return LABEL_EDEFAULT == null ? getLabel() != null : !LABEL_EDEFAULT.equals(getLabel());
    case LMPackage.CHANGE__IMPACT:
      return getImpact() != IMPACT_EDEFAULT;
    case LMPackage.CHANGE__BRANCH:
      return BRANCH_EDEFAULT == null ? getBranch() != null : !BRANCH_EDEFAULT.equals(getBranch());
    case LMPackage.CHANGE__DELIVERIES:
      return !getDeliveries().isEmpty();
    }
    return super.eIsSet(featureID);
  }

  @Override
  public String getName()
  {
    return getLabel();
  }

  @Override
  public long getBaseTimeStamp()
  {
    FixedBaseline base = getBase();
    if (base == null)
    {
      return CDOBranchPoint.UNSPECIFIED_DATE;
    }

    CDOBranchPointRef branchPoint = base.getBranchPoint();
    if (branchPoint == null)
    {
      return CDOBranchPoint.UNSPECIFIED_DATE;
    }

    return branchPoint.getTimeStamp();
  }

  @Override
  public EList<Stream> getDeliveryStreams()
  {
    EList<Stream> result = new BasicEList<>();

    for (Delivery delivery : getDeliveries())
    {
      result.add(delivery.getStream());
    }

    return result;
  }

  @Override
  public Delivery getDelivery(Stream stream)
  {
    for (Delivery delivery : getDeliveries())
    {
      Stream deliveryStream = delivery.getStream();
      if (deliveryStream == stream)
      {
        return delivery;
      }
    }

    return null;
  }

  @Override
  public BasePoint getDeliveryPoint(Stream stream)
  {
    long baseReleaseTime = Long.MAX_VALUE;

    for (;;)
    {
      Delivery delivery = getDelivery(stream);
      if (delivery != null)
      {
        long deliveryTime = delivery.getBaseTimeStamp();
        if (deliveryTime > baseReleaseTime)
        {
          return null;
        }

        return new BasePoint(stream, deliveryTime);
      }

      Drop baseRelease = stream.getBase();
      if (baseRelease == null)
      {
        return null;
      }

      stream = baseRelease.getStream();
      baseReleaseTime = baseRelease.getBaseTimeStamp();
    }
  }

  @Override
  public EList<Stream> getDeliveryCandidateStreams()
  {
    EList<Stream> result = new BasicEList<>();

    for (Stream stream : getModule().getStreams())
    {
      if (getDelivery(stream) == null)
      {
        result.add(stream);
      }
    }

    return result;
  }

  @Override
  public boolean isDeliverable()
  {
    EList<Stream> candidates = getDeliveryCandidateStreams();
    return !candidates.isEmpty();
  }

} // ChangeImpl
