/**
 */
package org.eclipse.emf.cdo.lm.reviews.impl;

import org.eclipse.emf.cdo.common.branch.CDOBranchPointRef;
import org.eclipse.emf.cdo.common.branch.CDOBranchRef;
import org.eclipse.emf.cdo.lm.Change;
import org.eclipse.emf.cdo.lm.Delivery;
import org.eclipse.emf.cdo.lm.FixedBaseline;
import org.eclipse.emf.cdo.lm.FloatingBaseline;
import org.eclipse.emf.cdo.lm.LMPackage;
import org.eclipse.emf.cdo.lm.reviews.DeliveryReview;
import org.eclipse.emf.cdo.lm.reviews.ReviewsPackage;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

import java.lang.reflect.InvocationTargetException;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Delivery Review</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.DeliveryReviewImpl#isClosed <em>Closed</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.DeliveryReviewImpl#getSourceChange <em>Source Change</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.DeliveryReviewImpl#getSourceCommit <em>Source Commit</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.DeliveryReviewImpl#getTargetCommit <em>Target Commit</em>}</li>
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
  public Change basicGetSourceChange()
  {
    return (Change)eDynamicGet(ReviewsPackage.DELIVERY_REVIEW__SOURCE_CHANGE, ReviewsPackage.Literals.DELIVERY_REVIEW__SOURCE_CHANGE, false, true);
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
  public FixedBaseline getBase()
  {
    // TODO: implement this method
    // Ensure that you remove @generated or mark it @generated NOT
    throw new UnsupportedOperationException();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<Delivery> getDeliveries()
  {
    // TODO: implement this method
    // Ensure that you remove @generated or mark it @generated NOT
    throw new UnsupportedOperationException();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public CDOBranchRef getBranch()
  {
    // TODO: implement this method
    // Ensure that you remove @generated or mark it @generated NOT
    throw new UnsupportedOperationException();
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
    case ReviewsPackage.DELIVERY_REVIEW__SOURCE_CHANGE:
      if (resolve)
      {
        return getSourceChange();
      }
      return basicGetSourceChange();
    case ReviewsPackage.DELIVERY_REVIEW__SOURCE_COMMIT:
      return getSourceCommit();
    case ReviewsPackage.DELIVERY_REVIEW__TARGET_COMMIT:
      return getTargetCommit();
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
    case ReviewsPackage.DELIVERY_REVIEW__CLOSED:
      setClosed((Boolean)newValue);
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
    case ReviewsPackage.DELIVERY_REVIEW__SOURCE_CHANGE:
      setSourceChange((Change)null);
      return;
    case ReviewsPackage.DELIVERY_REVIEW__SOURCE_COMMIT:
      setSourceCommit(SOURCE_COMMIT_EDEFAULT);
      return;
    case ReviewsPackage.DELIVERY_REVIEW__TARGET_COMMIT:
      setTargetCommit(TARGET_COMMIT_EDEFAULT);
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
    case ReviewsPackage.DELIVERY_REVIEW__SOURCE_CHANGE:
      return basicGetSourceChange() != null;
    case ReviewsPackage.DELIVERY_REVIEW__SOURCE_COMMIT:
      return getSourceCommit() != SOURCE_COMMIT_EDEFAULT;
    case ReviewsPackage.DELIVERY_REVIEW__TARGET_COMMIT:
      return getTargetCommit() != TARGET_COMMIT_EDEFAULT;
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

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int eDerivedOperationID(int baseOperationID, Class<?> baseClass)
  {
    if (baseClass == FloatingBaseline.class)
    {
      switch (baseOperationID)
      {
      case LMPackage.FLOATING_BASELINE___GET_BASE:
        return ReviewsPackage.DELIVERY_REVIEW___GET_BASE;
      case LMPackage.FLOATING_BASELINE___GET_DELIVERIES:
        return ReviewsPackage.DELIVERY_REVIEW___GET_DELIVERIES;
      case LMPackage.FLOATING_BASELINE___GET_BRANCH:
        return ReviewsPackage.DELIVERY_REVIEW___GET_BRANCH;
      default:
        return -1;
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
    case ReviewsPackage.DELIVERY_REVIEW___GET_BASE:
      return getBase();
    case ReviewsPackage.DELIVERY_REVIEW___GET_DELIVERIES:
      return getDeliveries();
    case ReviewsPackage.DELIVERY_REVIEW___GET_BRANCH:
      return getBranch();
    }
    return super.eInvoke(operationID, arguments);
  }

  @Override
  public boolean isFloating()
  {
    return true;
  }

  @Override
  public String getName()
  {
    return null;
  }

  @Override
  public CDOBranchPointRef getBranchPoint()
  {
    return null;
  }

  @Override
  public long getBaseTimeStamp()
  {
    return 0;
  }

} // DeliveryReviewImpl
