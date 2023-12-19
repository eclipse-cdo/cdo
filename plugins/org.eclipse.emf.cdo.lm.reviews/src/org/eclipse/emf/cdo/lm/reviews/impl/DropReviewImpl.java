/**
 */
package org.eclipse.emf.cdo.lm.reviews.impl;

import org.eclipse.emf.cdo.common.branch.CDOBranchPointRef;
import org.eclipse.emf.cdo.lm.Change;
import org.eclipse.emf.cdo.lm.Dependency;
import org.eclipse.emf.cdo.lm.DropType;
import org.eclipse.emf.cdo.lm.FixedBaseline;
import org.eclipse.emf.cdo.lm.LMPackage;
import org.eclipse.emf.cdo.lm.reviews.DropReview;
import org.eclipse.emf.cdo.lm.reviews.ReviewsPackage;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.equinox.p2.metadata.Version;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Drop Review</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.DropReviewImpl#getVersion <em>Version</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.DropReviewImpl#getDependencies <em>Dependencies</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.DropReviewImpl#getTargetTimeStamp <em>Target Time Stamp</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.DropReviewImpl#getDropType <em>Drop Type</em>}</li>
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
  public long getTargetTimeStamp()
  {
    return (Long)eDynamicGet(ReviewsPackage.DROP_REVIEW__TARGET_TIME_STAMP, ReviewsPackage.Literals.DROP_REVIEW__TARGET_TIME_STAMP, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setTargetTimeStamp(long newTargetTimeStamp)
  {
    eDynamicSet(ReviewsPackage.DROP_REVIEW__TARGET_TIME_STAMP, ReviewsPackage.Literals.DROP_REVIEW__TARGET_TIME_STAMP, newTargetTimeStamp);
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
  public DropType basicGetDropType()
  {
    return (DropType)eDynamicGet(ReviewsPackage.DROP_REVIEW__DROP_TYPE, ReviewsPackage.Literals.DROP_REVIEW__DROP_TYPE, false, true);
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
  public EList<Change> getBasedChanges()
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
    case ReviewsPackage.DROP_REVIEW__TARGET_TIME_STAMP:
      return getTargetTimeStamp();
    case ReviewsPackage.DROP_REVIEW__DROP_TYPE:
      if (resolve)
      {
        return getDropType();
      }
      return basicGetDropType();
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
    case ReviewsPackage.DROP_REVIEW__TARGET_TIME_STAMP:
      setTargetTimeStamp((Long)newValue);
      return;
    case ReviewsPackage.DROP_REVIEW__DROP_TYPE:
      setDropType((DropType)newValue);
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
    case ReviewsPackage.DROP_REVIEW__TARGET_TIME_STAMP:
      setTargetTimeStamp(TARGET_TIME_STAMP_EDEFAULT);
      return;
    case ReviewsPackage.DROP_REVIEW__DROP_TYPE:
      setDropType((DropType)null);
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
    case ReviewsPackage.DROP_REVIEW__TARGET_TIME_STAMP:
      return getTargetTimeStamp() != TARGET_TIME_STAMP_EDEFAULT;
    case ReviewsPackage.DROP_REVIEW__DROP_TYPE:
      return basicGetDropType() != null;
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
      case ReviewsPackage.DROP_REVIEW__VERSION:
        return LMPackage.FIXED_BASELINE__VERSION;
      case ReviewsPackage.DROP_REVIEW__DEPENDENCIES:
        return LMPackage.FIXED_BASELINE__DEPENDENCIES;
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
    if (baseClass == FixedBaseline.class)
    {
      switch (baseFeatureID)
      {
      case LMPackage.FIXED_BASELINE__VERSION:
        return ReviewsPackage.DROP_REVIEW__VERSION;
      case LMPackage.FIXED_BASELINE__DEPENDENCIES:
        return ReviewsPackage.DROP_REVIEW__DEPENDENCIES;
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
    if (baseClass == FixedBaseline.class)
    {
      switch (baseOperationID)
      {
      case LMPackage.FIXED_BASELINE___GET_BASED_CHANGES:
        return ReviewsPackage.DROP_REVIEW___GET_BASED_CHANGES;
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

} // DropReviewImpl
