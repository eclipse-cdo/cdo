/**
 */
package org.eclipse.emf.cdo.evolution.impl;

import org.eclipse.emf.cdo.evolution.Change;
import org.eclipse.emf.cdo.evolution.ChangeKind;
import org.eclipse.emf.cdo.evolution.EvolutionPackage;
import org.eclipse.emf.cdo.evolution.ModelSet;
import org.eclipse.emf.cdo.evolution.PropertyChange;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Property Change</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.evolution.impl.PropertyChangeImpl#getFeature <em>Feature</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.impl.PropertyChangeImpl#getOldValue <em>Old Value</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.impl.PropertyChangeImpl#getNewValue <em>New Value</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.impl.PropertyChangeImpl#getKind <em>Kind</em>}</li>
 * </ul>
 *
 * @generated
 */
public class PropertyChangeImpl extends ChangeImpl implements PropertyChange
{
  /**
   * The default value of the '{@link #getOldValue() <em>Old Value</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getOldValue()
   * @generated
   * @ordered
   */
  protected static final Object OLD_VALUE_EDEFAULT = null;

  /**
   * The default value of the '{@link #getNewValue() <em>New Value</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getNewValue()
   * @generated
   * @ordered
   */
  protected static final Object NEW_VALUE_EDEFAULT = null;

  /**
   * The default value of the '{@link #getKind() <em>Kind</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getKind()
   * @generated
   * @ordered
   */
  protected static final ChangeKind KIND_EDEFAULT = ChangeKind.NONE;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected PropertyChangeImpl()
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
    return EvolutionPackage.Literals.PROPERTY_CHANGE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EStructuralFeature getFeature()
  {
    return (EStructuralFeature)eDynamicGet(EvolutionPackage.PROPERTY_CHANGE__FEATURE, EvolutionPackage.Literals.PROPERTY_CHANGE__FEATURE, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EStructuralFeature basicGetFeature()
  {
    return (EStructuralFeature)eDynamicGet(EvolutionPackage.PROPERTY_CHANGE__FEATURE, EvolutionPackage.Literals.PROPERTY_CHANGE__FEATURE, false, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setFeature(EStructuralFeature newFeature)
  {
    eDynamicSet(EvolutionPackage.PROPERTY_CHANGE__FEATURE, EvolutionPackage.Literals.PROPERTY_CHANGE__FEATURE, newFeature);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Object getOldValue()
  {
    return eDynamicGet(EvolutionPackage.PROPERTY_CHANGE__OLD_VALUE, EvolutionPackage.Literals.PROPERTY_CHANGE__OLD_VALUE, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setOldValue(Object newOldValue)
  {
    eDynamicSet(EvolutionPackage.PROPERTY_CHANGE__OLD_VALUE, EvolutionPackage.Literals.PROPERTY_CHANGE__OLD_VALUE, newOldValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Object getNewValue()
  {
    return eDynamicGet(EvolutionPackage.PROPERTY_CHANGE__NEW_VALUE, EvolutionPackage.Literals.PROPERTY_CHANGE__NEW_VALUE, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setNewValue(Object newNewValue)
  {
    eDynamicSet(EvolutionPackage.PROPERTY_CHANGE__NEW_VALUE, EvolutionPackage.Literals.PROPERTY_CHANGE__NEW_VALUE, newNewValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public ChangeKind getKind()
  {
    Object oldValue = getOldValue();
    Object newValue = getNewValue();

    if (oldValue == null)
    {
      if (newValue == null)
      {
        return ChangeKind.NONE;
      }

      return ChangeKind.ADDED;
    }
    else
    {
      if (newValue == null)
      {
        return ChangeKind.REMOVED;
      }

      return ChangeKind.CHANGED;
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
    case EvolutionPackage.PROPERTY_CHANGE__FEATURE:
      if (resolve)
      {
        return getFeature();
      }
      return basicGetFeature();
    case EvolutionPackage.PROPERTY_CHANGE__OLD_VALUE:
      return getOldValue();
    case EvolutionPackage.PROPERTY_CHANGE__NEW_VALUE:
      return getNewValue();
    case EvolutionPackage.PROPERTY_CHANGE__KIND:
      return getKind();
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
    case EvolutionPackage.PROPERTY_CHANGE__FEATURE:
      setFeature((EStructuralFeature)newValue);
      return;
    case EvolutionPackage.PROPERTY_CHANGE__OLD_VALUE:
      setOldValue(newValue);
      return;
    case EvolutionPackage.PROPERTY_CHANGE__NEW_VALUE:
      setNewValue(newValue);
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
    case EvolutionPackage.PROPERTY_CHANGE__FEATURE:
      setFeature((EStructuralFeature)null);
      return;
    case EvolutionPackage.PROPERTY_CHANGE__OLD_VALUE:
      setOldValue(OLD_VALUE_EDEFAULT);
      return;
    case EvolutionPackage.PROPERTY_CHANGE__NEW_VALUE:
      setNewValue(NEW_VALUE_EDEFAULT);
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
    case EvolutionPackage.PROPERTY_CHANGE__FEATURE:
      return basicGetFeature() != null;
    case EvolutionPackage.PROPERTY_CHANGE__OLD_VALUE:
      return OLD_VALUE_EDEFAULT == null ? getOldValue() != null : !OLD_VALUE_EDEFAULT.equals(getOldValue());
    case EvolutionPackage.PROPERTY_CHANGE__NEW_VALUE:
      return NEW_VALUE_EDEFAULT == null ? getNewValue() != null : !NEW_VALUE_EDEFAULT.equals(getNewValue());
    case EvolutionPackage.PROPERTY_CHANGE__KIND:
      return getKind() != KIND_EDEFAULT;
    }
    return super.eIsSet(featureID);
  }

  @Override
  public ModelSet getOldModelSet()
  {
    Change parent = getParent();
    if (parent != null)
    {
      return parent.getOldModelSet();
    }

    return null;
  }

  @Override
  public ModelSet getNewModelSet()
  {
    Change parent = getParent();
    if (parent != null)
    {
      return parent.getNewModelSet();
    }

    return null;
  }

} // PropertyChangeImpl
