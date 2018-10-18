/**
 */
package org.eclipse.emf.cdo.evolution.impl;

import org.eclipse.emf.cdo.evolution.EvolutionPackage;
import org.eclipse.emf.cdo.evolution.Migration;
import org.eclipse.emf.cdo.evolution.ModelSet;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Migration</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.evolution.impl.MigrationImpl#getModelSet <em>Model Set</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.impl.MigrationImpl#getDiagnosticID <em>Diagnostic ID</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class MigrationImpl extends CDOObjectImpl implements Migration
{
  /**
   * The default value of the '{@link #getDiagnosticID() <em>Diagnostic ID</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getDiagnosticID()
   * @generated
   * @ordered
   */
  protected static final String DIAGNOSTIC_ID_EDEFAULT = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected MigrationImpl()
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
    return EvolutionPackage.Literals.MIGRATION;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected int eStaticFeatureCount()
  {
    return 0;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ModelSet getModelSet()
  {
    return (ModelSet)eDynamicGet(EvolutionPackage.MIGRATION__MODEL_SET, EvolutionPackage.Literals.MIGRATION__MODEL_SET, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ModelSet basicGetModelSet()
  {
    return (ModelSet)eDynamicGet(EvolutionPackage.MIGRATION__MODEL_SET, EvolutionPackage.Literals.MIGRATION__MODEL_SET, false, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetModelSet(ModelSet newModelSet, NotificationChain msgs)
  {
    msgs = eBasicSetContainer((InternalEObject)newModelSet, EvolutionPackage.MIGRATION__MODEL_SET, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setModelSet(ModelSet newModelSet)
  {
    eDynamicSet(EvolutionPackage.MIGRATION__MODEL_SET, EvolutionPackage.Literals.MIGRATION__MODEL_SET, newModelSet);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getDiagnosticID()
  {
    return (String)eDynamicGet(EvolutionPackage.MIGRATION__DIAGNOSTIC_ID, EvolutionPackage.Literals.MIGRATION__DIAGNOSTIC_ID, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setDiagnosticID(String newDiagnosticID)
  {
    eDynamicSet(EvolutionPackage.MIGRATION__DIAGNOSTIC_ID, EvolutionPackage.Literals.MIGRATION__DIAGNOSTIC_ID, newDiagnosticID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case EvolutionPackage.MIGRATION__MODEL_SET:
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      return basicSetModelSet((ModelSet)otherEnd, msgs);
    }
    return super.eInverseAdd(otherEnd, featureID, msgs);
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
    case EvolutionPackage.MIGRATION__MODEL_SET:
      return basicSetModelSet(null, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs)
  {
    switch (eContainerFeatureID())
    {
    case EvolutionPackage.MIGRATION__MODEL_SET:
      return eInternalContainer().eInverseRemove(this, EvolutionPackage.MODEL_SET__MIGRATIONS, ModelSet.class, msgs);
    }
    return super.eBasicRemoveFromContainerFeature(msgs);
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
    case EvolutionPackage.MIGRATION__MODEL_SET:
      if (resolve)
      {
        return getModelSet();
      }
      return basicGetModelSet();
    case EvolutionPackage.MIGRATION__DIAGNOSTIC_ID:
      return getDiagnosticID();
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
    case EvolutionPackage.MIGRATION__MODEL_SET:
      setModelSet((ModelSet)newValue);
      return;
    case EvolutionPackage.MIGRATION__DIAGNOSTIC_ID:
      setDiagnosticID((String)newValue);
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
    case EvolutionPackage.MIGRATION__MODEL_SET:
      setModelSet((ModelSet)null);
      return;
    case EvolutionPackage.MIGRATION__DIAGNOSTIC_ID:
      setDiagnosticID(DIAGNOSTIC_ID_EDEFAULT);
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
    case EvolutionPackage.MIGRATION__MODEL_SET:
      return basicGetModelSet() != null;
    case EvolutionPackage.MIGRATION__DIAGNOSTIC_ID:
      return DIAGNOSTIC_ID_EDEFAULT == null ? getDiagnosticID() != null : !DIAGNOSTIC_ID_EDEFAULT.equals(getDiagnosticID());
    }
    return super.eIsSet(featureID);
  }

} // MigrationImpl
