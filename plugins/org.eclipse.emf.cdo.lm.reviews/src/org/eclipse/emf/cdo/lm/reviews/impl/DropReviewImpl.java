/*
 * Copyright (c) 2023, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.reviews.impl;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchPointRef;
import org.eclipse.emf.cdo.common.branch.CDOBranchRef;
import org.eclipse.emf.cdo.lm.Change;
import org.eclipse.emf.cdo.lm.Delivery;
import org.eclipse.emf.cdo.lm.Dependency;
import org.eclipse.emf.cdo.lm.DropType;
import org.eclipse.emf.cdo.lm.FixedBaseline;
import org.eclipse.emf.cdo.lm.LMPackage;
import org.eclipse.emf.cdo.lm.Stream;
import org.eclipse.emf.cdo.lm.impl.FixedBaselineImpl;
import org.eclipse.emf.cdo.lm.reviews.DropReview;
import org.eclipse.emf.cdo.lm.reviews.ReviewsPackage;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.equinox.p2.metadata.Version;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Drop Review</b></em>'.
 * @noextend This class is not intended to be subclassed by clients.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.DropReviewImpl#getVersion <em>Version</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.DropReviewImpl#getDependencies <em>Dependencies</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.DropReviewImpl#getDelivery <em>Delivery</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.DropReviewImpl#getTargetTimeStamp <em>Target Time Stamp</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.DropReviewImpl#getDropType <em>Drop Type</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.DropReviewImpl#getDropLabel <em>Drop Label</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DropReviewImpl extends ReviewImpl implements DropReview
{
  /**
   * The default value of the '{@link #getVersion() <em>Version</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getVersion()
   * @generated
   * @ordered
   */
  protected static final Version VERSION_EDEFAULT = null;

  /**
   * The default value of the '{@link #getTargetTimeStamp() <em>Target Time Stamp</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTargetTimeStamp()
   * @generated
   * @ordered
   */
  protected static final long TARGET_TIME_STAMP_EDEFAULT = 0L;

  /**
   * The default value of the '{@link #getDropLabel() <em>Drop Label</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getDropLabel()
   * @generated
   * @ordered
   */
  protected static final String DROP_LABEL_EDEFAULT = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected DropReviewImpl()
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
    return ReviewsPackage.Literals.DROP_REVIEW;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Version getVersion()
  {
    return (Version)eDynamicGet(ReviewsPackage.DROP_REVIEW__VERSION, LMPackage.Literals.FIXED_BASELINE__VERSION, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setVersion(Version newVersion)
  {
    eDynamicSet(ReviewsPackage.DROP_REVIEW__VERSION, LMPackage.Literals.FIXED_BASELINE__VERSION, newVersion);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public EList<Dependency> getDependencies()
  {
    return (EList<Dependency>)eDynamicGet(ReviewsPackage.DROP_REVIEW__DEPENDENCIES, LMPackage.Literals.FIXED_BASELINE__DEPENDENCIES, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Delivery getDelivery()
  {
    return (Delivery)eDynamicGet(ReviewsPackage.DROP_REVIEW__DELIVERY, ReviewsPackage.Literals.DROP_REVIEW__DELIVERY, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setDelivery(Delivery newDelivery)
  {
    eDynamicSet(ReviewsPackage.DROP_REVIEW__DELIVERY, ReviewsPackage.Literals.DROP_REVIEW__DELIVERY, newDelivery);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public long getTargetTimeStamp()
  {
    Delivery delivery = getDelivery();
    return delivery == null ? CDOBranchPoint.INVALID_DATE : delivery.getBaseTimeStamp();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public DropType getDropType()
  {
    return (DropType)eDynamicGet(ReviewsPackage.DROP_REVIEW__DROP_TYPE, ReviewsPackage.Literals.DROP_REVIEW__DROP_TYPE, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setDropType(DropType newDropType)
  {
    eDynamicSet(ReviewsPackage.DROP_REVIEW__DROP_TYPE, ReviewsPackage.Literals.DROP_REVIEW__DROP_TYPE, newDropType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getDropLabel()
  {
    return (String)eDynamicGet(ReviewsPackage.DROP_REVIEW__DROP_LABEL, ReviewsPackage.Literals.DROP_REVIEW__DROP_LABEL, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setDropLabel(String newDropLabel)
  {
    eDynamicSet(ReviewsPackage.DROP_REVIEW__DROP_LABEL, ReviewsPackage.Literals.DROP_REVIEW__DROP_LABEL, newDropLabel);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public EList<Change> getBasedChanges()
  {
    return FixedBaselineImpl.getBasedChanges(this);
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
      case ReviewsPackage.DROP_REVIEW__DEPENDENCIES:
        return ((InternalEList<?>)getDependencies()).basicRemove(otherEnd, msgs);
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
      case ReviewsPackage.DROP_REVIEW__VERSION:
        return getVersion();
      case ReviewsPackage.DROP_REVIEW__DEPENDENCIES:
        return getDependencies();
      case ReviewsPackage.DROP_REVIEW__DELIVERY:
        return getDelivery();
      case ReviewsPackage.DROP_REVIEW__TARGET_TIME_STAMP:
        return getTargetTimeStamp();
      case ReviewsPackage.DROP_REVIEW__DROP_TYPE:
        return getDropType();
      case ReviewsPackage.DROP_REVIEW__DROP_LABEL:
        return getDropLabel();
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
      case ReviewsPackage.DROP_REVIEW__VERSION:
        setVersion((Version)newValue);
        return;
      case ReviewsPackage.DROP_REVIEW__DEPENDENCIES:
        getDependencies().clear();
        getDependencies().addAll((Collection<? extends Dependency>)newValue);
        return;
      case ReviewsPackage.DROP_REVIEW__DELIVERY:
        setDelivery((Delivery)newValue);
        return;
      case ReviewsPackage.DROP_REVIEW__DROP_TYPE:
        setDropType((DropType)newValue);
        return;
      case ReviewsPackage.DROP_REVIEW__DROP_LABEL:
        setDropLabel((String)newValue);
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
      case ReviewsPackage.DROP_REVIEW__VERSION:
        setVersion(VERSION_EDEFAULT);
        return;
      case ReviewsPackage.DROP_REVIEW__DEPENDENCIES:
        getDependencies().clear();
        return;
      case ReviewsPackage.DROP_REVIEW__DELIVERY:
        setDelivery((Delivery)null);
        return;
      case ReviewsPackage.DROP_REVIEW__DROP_TYPE:
        setDropType((DropType)null);
        return;
      case ReviewsPackage.DROP_REVIEW__DROP_LABEL:
        setDropLabel(DROP_LABEL_EDEFAULT);
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
      case ReviewsPackage.DROP_REVIEW__VERSION:
        return VERSION_EDEFAULT == null ? getVersion() != null : !VERSION_EDEFAULT.equals(getVersion());
      case ReviewsPackage.DROP_REVIEW__DEPENDENCIES:
        return !getDependencies().isEmpty();
      case ReviewsPackage.DROP_REVIEW__DELIVERY:
        return getDelivery() != null;
      case ReviewsPackage.DROP_REVIEW__TARGET_TIME_STAMP:
        return getTargetTimeStamp() != TARGET_TIME_STAMP_EDEFAULT;
      case ReviewsPackage.DROP_REVIEW__DROP_TYPE:
        return getDropType() != null;
      case ReviewsPackage.DROP_REVIEW__DROP_LABEL:
        return DROP_LABEL_EDEFAULT == null ? getDropLabel() != null : !DROP_LABEL_EDEFAULT.equals(getDropLabel());
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass)
  {
    if (baseClass == FixedBaseline.class)
    {
      switch (derivedFeatureID)
      {
        case ReviewsPackage.DROP_REVIEW__VERSION: return LMPackage.FIXED_BASELINE__VERSION;
        case ReviewsPackage.DROP_REVIEW__DEPENDENCIES: return LMPackage.FIXED_BASELINE__DEPENDENCIES;
        default: return -1;
      }
    }
    return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass)
  {
    if (baseClass == FixedBaseline.class)
    {
      switch (baseFeatureID)
      {
        case LMPackage.FIXED_BASELINE__VERSION: return ReviewsPackage.DROP_REVIEW__VERSION;
        case LMPackage.FIXED_BASELINE__DEPENDENCIES: return ReviewsPackage.DROP_REVIEW__DEPENDENCIES;
        default: return -1;
      }
    }
    return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int eDerivedOperationID(int baseOperationID, Class<?> baseClass)
  {
    if (baseClass == FixedBaseline.class)
    {
      switch (baseOperationID)
      {
        case LMPackage.FIXED_BASELINE___GET_BASED_CHANGES: return ReviewsPackage.DROP_REVIEW___GET_BASED_CHANGES;
        default: return -1;
      }
    }
    return super.eDerivedOperationID(baseOperationID, baseClass);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException
  {
    switch (operationID)
    {
      case ReviewsPackage.DROP_REVIEW___GET_BASED_CHANGES:
        return getBasedChanges();
    }
    return super.eInvoke(operationID, arguments);
  }

  @Override
  public boolean isFloating()
  {
    return false;
  }

  @Override
  public String getTypeName()
  {
    return "Drop Review";
  }

  @Override
  public String getName()
  {
    return getDropLabel();
  }

  @Override
  public int getSortPriority()
  {
    DropType dropType = getDropType();
    return dropType != null && dropType.isRelease() ? 275 : 250;
  }

  @Override
  public long getBaseTimeStamp()
  {
    return getTargetTimeStamp();
  }

  @Override
  public CDOBranchPointRef getBranchPoint()
  {
    Stream stream = getStream();
    if (stream == null)
    {
      stream = getContainerStream();
    }

    if (stream == null)
    {
      return null;
    }

    CDOBranchRef targetBranch = stream.getBranch();
    if (targetBranch == null)
    {
      return null;
    }

    long baseTimeStamp = getBaseTimeStamp();
    return targetBranch.getPointRef(baseTimeStamp);
  }

  private Stream getContainerStream()
  {
    EObject container = eContainer();
    while (container != null)
    {
      if (container instanceof Stream)
      {
        return (Stream)container;
      }

      container = container.eContainer();
    }

    return null;
  }

} // DropReviewImpl
