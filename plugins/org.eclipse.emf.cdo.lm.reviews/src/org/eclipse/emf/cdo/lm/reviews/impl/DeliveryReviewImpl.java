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

import org.eclipse.emf.cdo.common.branch.CDOBranchPointRef;
import org.eclipse.emf.cdo.common.branch.CDOBranchRef;
import org.eclipse.emf.cdo.etypes.EtypesFactory;
import org.eclipse.emf.cdo.etypes.EtypesPackage;
import org.eclipse.emf.cdo.lm.Change;
import org.eclipse.emf.cdo.lm.Delivery;
import org.eclipse.emf.cdo.lm.FixedBaseline;
import org.eclipse.emf.cdo.lm.FloatingBaseline;
import org.eclipse.emf.cdo.lm.Impact;
import org.eclipse.emf.cdo.lm.LMPackage;
import org.eclipse.emf.cdo.lm.impl.FloatingBaselineImpl;
import org.eclipse.emf.cdo.lm.reviews.DeliveryReview;
import org.eclipse.emf.cdo.lm.reviews.ReviewsPackage;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

import java.util.Collection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Delivery Review</b></em>'.
 * @noextend This class is not intended to be subclassed by clients.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.DeliveryReviewImpl#isClosed <em>Closed</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.DeliveryReviewImpl#getBase <em>Base</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.DeliveryReviewImpl#getImpact <em>Impact</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.DeliveryReviewImpl#getBranch <em>Branch</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.DeliveryReviewImpl#getDeliveries <em>Deliveries</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.DeliveryReviewImpl#getSourceChange <em>Source Change</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.DeliveryReviewImpl#getSourceCommit <em>Source Commit</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.DeliveryReviewImpl#getTargetCommit <em>Target Commit</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.DeliveryReviewImpl#getRebaseCount <em>Rebase Count</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DeliveryReviewImpl extends ReviewImpl implements DeliveryReview
{
  /**
   * The default value of the '{@link #isClosed() <em>Closed</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isClosed()
   * @generated
   * @ordered
   */
  protected static final boolean CLOSED_EDEFAULT = false;

  /**
   * The default value of the '{@link #getImpact() <em>Impact</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getImpact()
   * @generated
   * @ordered
   */
  protected static final Impact IMPACT_EDEFAULT = Impact.MICRO;

  /**
   * The default value of the '{@link #getBranch() <em>Branch</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getBranch()
   * @generated
   * @ordered
   */
  protected static final CDOBranchRef BRANCH_EDEFAULT = (CDOBranchRef)EtypesFactory.eINSTANCE.createFromString(EtypesPackage.eINSTANCE.getBranchRef(), "");

  /**
   * The default value of the '{@link #getSourceCommit() <em>Source Commit</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSourceCommit()
   * @generated
   * @ordered
   */
  protected static final long SOURCE_COMMIT_EDEFAULT = 0L;

  /**
   * The default value of the '{@link #getTargetCommit() <em>Target Commit</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTargetCommit()
   * @generated
   * @ordered
   */
  protected static final long TARGET_COMMIT_EDEFAULT = 0L;

  /**
   * The default value of the '{@link #getRebaseCount() <em>Rebase Count</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRebaseCount()
   * @generated
   * @ordered
   */
  protected static final int REBASE_COUNT_EDEFAULT = 0;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected DeliveryReviewImpl()
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
    return ReviewsPackage.Literals.DELIVERY_REVIEW;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isClosed()
  {
    return (Boolean)eDynamicGet(ReviewsPackage.DELIVERY_REVIEW__CLOSED, LMPackage.Literals.FLOATING_BASELINE__CLOSED, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setClosed(boolean newClosed)
  {
    eDynamicSet(ReviewsPackage.DELIVERY_REVIEW__CLOSED, LMPackage.Literals.FLOATING_BASELINE__CLOSED, newClosed);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Change getSourceChange()
  {
    return (Change)eDynamicGet(ReviewsPackage.DELIVERY_REVIEW__SOURCE_CHANGE, ReviewsPackage.Literals.DELIVERY_REVIEW__SOURCE_CHANGE, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setSourceChange(Change newSourceChange)
  {
    eDynamicSet(ReviewsPackage.DELIVERY_REVIEW__SOURCE_CHANGE, ReviewsPackage.Literals.DELIVERY_REVIEW__SOURCE_CHANGE, newSourceChange);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public long getSourceCommit()
  {
    return (Long)eDynamicGet(ReviewsPackage.DELIVERY_REVIEW__SOURCE_COMMIT, ReviewsPackage.Literals.DELIVERY_REVIEW__SOURCE_COMMIT, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setSourceCommit(long newSourceCommit)
  {
    eDynamicSet(ReviewsPackage.DELIVERY_REVIEW__SOURCE_COMMIT, ReviewsPackage.Literals.DELIVERY_REVIEW__SOURCE_COMMIT, newSourceCommit);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public long getTargetCommit()
  {
    return (Long)eDynamicGet(ReviewsPackage.DELIVERY_REVIEW__TARGET_COMMIT, ReviewsPackage.Literals.DELIVERY_REVIEW__TARGET_COMMIT, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setTargetCommit(long newTargetCommit)
  {
    eDynamicSet(ReviewsPackage.DELIVERY_REVIEW__TARGET_COMMIT, ReviewsPackage.Literals.DELIVERY_REVIEW__TARGET_COMMIT, newTargetCommit);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int getRebaseCount()
  {
    return (Integer)eDynamicGet(ReviewsPackage.DELIVERY_REVIEW__REBASE_COUNT, ReviewsPackage.Literals.DELIVERY_REVIEW__REBASE_COUNT, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setRebaseCount(int newRebaseCount)
  {
    eDynamicSet(ReviewsPackage.DELIVERY_REVIEW__REBASE_COUNT, ReviewsPackage.Literals.DELIVERY_REVIEW__REBASE_COUNT, newRebaseCount);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public FixedBaseline getBase()
  {
    return (FixedBaseline)eDynamicGet(ReviewsPackage.DELIVERY_REVIEW__BASE, ReviewsPackage.Literals.DELIVERY_REVIEW__BASE, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setBase(FixedBaseline newBase)
  {
    eDynamicSet(ReviewsPackage.DELIVERY_REVIEW__BASE, ReviewsPackage.Literals.DELIVERY_REVIEW__BASE, newBase);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Impact getImpact()
  {
    return (Impact)eDynamicGet(ReviewsPackage.DELIVERY_REVIEW__IMPACT, ReviewsPackage.Literals.DELIVERY_REVIEW__IMPACT, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setImpact(Impact newImpact)
  {
    eDynamicSet(ReviewsPackage.DELIVERY_REVIEW__IMPACT, ReviewsPackage.Literals.DELIVERY_REVIEW__IMPACT, newImpact);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public EList<Delivery> getDeliveries()
  {
    return (EList<Delivery>)eDynamicGet(ReviewsPackage.DELIVERY_REVIEW__DELIVERIES, ReviewsPackage.Literals.DELIVERY_REVIEW__DELIVERIES, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public CDOBranchRef getBranch()
  {
    return (CDOBranchRef)eDynamicGet(ReviewsPackage.DELIVERY_REVIEW__BRANCH, ReviewsPackage.Literals.DELIVERY_REVIEW__BRANCH, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setBranch(CDOBranchRef newBranch)
  {
    eDynamicSet(ReviewsPackage.DELIVERY_REVIEW__BRANCH, ReviewsPackage.Literals.DELIVERY_REVIEW__BRANCH, newBranch);
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
    case ReviewsPackage.DELIVERY_REVIEW__CLOSED:
      return isClosed();
    case ReviewsPackage.DELIVERY_REVIEW__BASE:
      return getBase();
    case ReviewsPackage.DELIVERY_REVIEW__IMPACT:
      return getImpact();
    case ReviewsPackage.DELIVERY_REVIEW__BRANCH:
      return getBranch();
    case ReviewsPackage.DELIVERY_REVIEW__DELIVERIES:
      return getDeliveries();
    case ReviewsPackage.DELIVERY_REVIEW__SOURCE_CHANGE:
      return getSourceChange();
    case ReviewsPackage.DELIVERY_REVIEW__SOURCE_COMMIT:
      return getSourceCommit();
    case ReviewsPackage.DELIVERY_REVIEW__TARGET_COMMIT:
      return getTargetCommit();
    case ReviewsPackage.DELIVERY_REVIEW__REBASE_COUNT:
      return getRebaseCount();
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
    case ReviewsPackage.DELIVERY_REVIEW__CLOSED:
      setClosed((Boolean)newValue);
      return;
    case ReviewsPackage.DELIVERY_REVIEW__BASE:
      setBase((FixedBaseline)newValue);
      return;
    case ReviewsPackage.DELIVERY_REVIEW__IMPACT:
      setImpact((Impact)newValue);
      return;
    case ReviewsPackage.DELIVERY_REVIEW__BRANCH:
      setBranch((CDOBranchRef)newValue);
      return;
    case ReviewsPackage.DELIVERY_REVIEW__DELIVERIES:
      getDeliveries().clear();
      getDeliveries().addAll((Collection<? extends Delivery>)newValue);
      return;
    case ReviewsPackage.DELIVERY_REVIEW__SOURCE_CHANGE:
      setSourceChange((Change)newValue);
      return;
    case ReviewsPackage.DELIVERY_REVIEW__SOURCE_COMMIT:
      setSourceCommit((Long)newValue);
      return;
    case ReviewsPackage.DELIVERY_REVIEW__TARGET_COMMIT:
      setTargetCommit((Long)newValue);
      return;
    case ReviewsPackage.DELIVERY_REVIEW__REBASE_COUNT:
      setRebaseCount((Integer)newValue);
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
    case ReviewsPackage.DELIVERY_REVIEW__CLOSED:
      setClosed(CLOSED_EDEFAULT);
      return;
    case ReviewsPackage.DELIVERY_REVIEW__BASE:
      setBase((FixedBaseline)null);
      return;
    case ReviewsPackage.DELIVERY_REVIEW__IMPACT:
      setImpact(IMPACT_EDEFAULT);
      return;
    case ReviewsPackage.DELIVERY_REVIEW__BRANCH:
      setBranch(BRANCH_EDEFAULT);
      return;
    case ReviewsPackage.DELIVERY_REVIEW__DELIVERIES:
      getDeliveries().clear();
      return;
    case ReviewsPackage.DELIVERY_REVIEW__SOURCE_CHANGE:
      setSourceChange((Change)null);
      return;
    case ReviewsPackage.DELIVERY_REVIEW__SOURCE_COMMIT:
      setSourceCommit(SOURCE_COMMIT_EDEFAULT);
      return;
    case ReviewsPackage.DELIVERY_REVIEW__TARGET_COMMIT:
      setTargetCommit(TARGET_COMMIT_EDEFAULT);
      return;
    case ReviewsPackage.DELIVERY_REVIEW__REBASE_COUNT:
      setRebaseCount(REBASE_COUNT_EDEFAULT);
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
    case ReviewsPackage.DELIVERY_REVIEW__CLOSED:
      return isClosed() != CLOSED_EDEFAULT;
    case ReviewsPackage.DELIVERY_REVIEW__BASE:
      return getBase() != null;
    case ReviewsPackage.DELIVERY_REVIEW__IMPACT:
      return getImpact() != IMPACT_EDEFAULT;
    case ReviewsPackage.DELIVERY_REVIEW__BRANCH:
      return BRANCH_EDEFAULT == null ? getBranch() != null : !BRANCH_EDEFAULT.equals(getBranch());
    case ReviewsPackage.DELIVERY_REVIEW__DELIVERIES:
      return !getDeliveries().isEmpty();
    case ReviewsPackage.DELIVERY_REVIEW__SOURCE_CHANGE:
      return getSourceChange() != null;
    case ReviewsPackage.DELIVERY_REVIEW__SOURCE_COMMIT:
      return getSourceCommit() != SOURCE_COMMIT_EDEFAULT;
    case ReviewsPackage.DELIVERY_REVIEW__TARGET_COMMIT:
      return getTargetCommit() != TARGET_COMMIT_EDEFAULT;
    case ReviewsPackage.DELIVERY_REVIEW__REBASE_COUNT:
      return getRebaseCount() != REBASE_COUNT_EDEFAULT;
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
    if (baseClass == FloatingBaseline.class)
    {
      switch (derivedFeatureID)
      {
      case ReviewsPackage.DELIVERY_REVIEW__CLOSED:
        return LMPackage.FLOATING_BASELINE__CLOSED;
      default:
        return -1;
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
    if (baseClass == FloatingBaseline.class)
    {
      switch (baseFeatureID)
      {
      case LMPackage.FLOATING_BASELINE__CLOSED:
        return ReviewsPackage.DELIVERY_REVIEW__CLOSED;
      default:
        return -1;
      }
    }
    return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
  }

  @Override
  public boolean isFloating()
  {
    return true;
  }

  @Override
  public String getTypeName()
  {
    return "Delivery Review";
  }

  @Override
  public String getName()
  {
    return Integer.toString(getId()) + " - " + getSourceChangeName();
  }

  @Override
  public int getSortPriority()
  {
    return 600;
  }

  private String getSourceChangeName()
  {
    Change change = getSourceChange();
    if (change == null)
    {
      return "???";
    }

    return change.getName();
  }

  @Override
  public CDOBranchPointRef getBranchPoint()
  {
    return FloatingBaselineImpl.getBranchPoint(this);
  }

  @Override
  public long getBaseTimeStamp()
  {
    return getTargetCommit();
  }

} // DeliveryReviewImpl
