/**
 */
package org.eclipse.emf.cdo.evolution.impl;

import org.eclipse.emf.cdo.evolution.EvolutionPackage;
import org.eclipse.emf.cdo.evolution.FeaturePathMigration;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;

import java.util.Collection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Feature Path Migration</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.evolution.impl.FeaturePathMigrationImpl#getFromClass <em>From Class</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.impl.FeaturePathMigrationImpl#getToClass <em>To Class</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.impl.FeaturePathMigrationImpl#getFeaturePath <em>Feature Path</em>}</li>
 * </ul>
 *
 * @generated
 */
public class FeaturePathMigrationImpl extends MigrationImpl implements FeaturePathMigration
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected FeaturePathMigrationImpl()
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
    return EvolutionPackage.Literals.FEATURE_PATH_MIGRATION;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getFromClass()
  {
    return (EClass)eDynamicGet(EvolutionPackage.FEATURE_PATH_MIGRATION__FROM_CLASS, EvolutionPackage.Literals.FEATURE_PATH_MIGRATION__FROM_CLASS, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass basicGetFromClass()
  {
    return (EClass)eDynamicGet(EvolutionPackage.FEATURE_PATH_MIGRATION__FROM_CLASS, EvolutionPackage.Literals.FEATURE_PATH_MIGRATION__FROM_CLASS, false, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setFromClass(EClass newFromClass)
  {
    eDynamicSet(EvolutionPackage.FEATURE_PATH_MIGRATION__FROM_CLASS, EvolutionPackage.Literals.FEATURE_PATH_MIGRATION__FROM_CLASS, newFromClass);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getToClass()
  {
    return (EClass)eDynamicGet(EvolutionPackage.FEATURE_PATH_MIGRATION__TO_CLASS, EvolutionPackage.Literals.FEATURE_PATH_MIGRATION__TO_CLASS, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass basicGetToClass()
  {
    return (EClass)eDynamicGet(EvolutionPackage.FEATURE_PATH_MIGRATION__TO_CLASS, EvolutionPackage.Literals.FEATURE_PATH_MIGRATION__TO_CLASS, false, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setToClass(EClass newToClass)
  {
    eDynamicSet(EvolutionPackage.FEATURE_PATH_MIGRATION__TO_CLASS, EvolutionPackage.Literals.FEATURE_PATH_MIGRATION__TO_CLASS, newToClass);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EList<EReference> getFeaturePath()
  {
    return (EList<EReference>)eDynamicGet(EvolutionPackage.FEATURE_PATH_MIGRATION__FEATURE_PATH, EvolutionPackage.Literals.FEATURE_PATH_MIGRATION__FEATURE_PATH,
        true, true);
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
    case EvolutionPackage.FEATURE_PATH_MIGRATION__FROM_CLASS:
      if (resolve)
      {
        return getFromClass();
      }
      return basicGetFromClass();
    case EvolutionPackage.FEATURE_PATH_MIGRATION__TO_CLASS:
      if (resolve)
      {
        return getToClass();
      }
      return basicGetToClass();
    case EvolutionPackage.FEATURE_PATH_MIGRATION__FEATURE_PATH:
      return getFeaturePath();
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
    case EvolutionPackage.FEATURE_PATH_MIGRATION__FROM_CLASS:
      setFromClass((EClass)newValue);
      return;
    case EvolutionPackage.FEATURE_PATH_MIGRATION__TO_CLASS:
      setToClass((EClass)newValue);
      return;
    case EvolutionPackage.FEATURE_PATH_MIGRATION__FEATURE_PATH:
      getFeaturePath().clear();
      getFeaturePath().addAll((Collection<? extends EReference>)newValue);
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
    case EvolutionPackage.FEATURE_PATH_MIGRATION__FROM_CLASS:
      setFromClass((EClass)null);
      return;
    case EvolutionPackage.FEATURE_PATH_MIGRATION__TO_CLASS:
      setToClass((EClass)null);
      return;
    case EvolutionPackage.FEATURE_PATH_MIGRATION__FEATURE_PATH:
      getFeaturePath().clear();
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
    case EvolutionPackage.FEATURE_PATH_MIGRATION__FROM_CLASS:
      return basicGetFromClass() != null;
    case EvolutionPackage.FEATURE_PATH_MIGRATION__TO_CLASS:
      return basicGetToClass() != null;
    case EvolutionPackage.FEATURE_PATH_MIGRATION__FEATURE_PATH:
      return !getFeaturePath().isEmpty();
    }
    return super.eIsSet(featureID);
  }

} // FeaturePathMigrationImpl
