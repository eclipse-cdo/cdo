/**
 */
package org.eclipse.emf.cdo.evolution.impl;

import org.eclipse.emf.cdo.evolution.ElementChange;
import org.eclipse.emf.cdo.evolution.EvolutionPackage;
import org.eclipse.emf.cdo.evolution.ModelSet;
import org.eclipse.emf.cdo.evolution.ModelSetChange;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EModelElement;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Model Set Change</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.evolution.impl.ModelSetChangeImpl#getOldModelSet <em>Old Model Set</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.impl.ModelSetChangeImpl#getNewModelSet <em>New Model Set</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ModelSetChangeImpl extends ChangeImpl implements ModelSetChange
{
  private final Map<EModelElement, ElementChange> elementChanges = new HashMap<EModelElement, ElementChange>();

  private final Map<EModelElement, EModelElement> newToOldElements = new HashMap<EModelElement, EModelElement>();

  private final Map<EModelElement, Set<EModelElement>> oldToNewElements = new HashMap<EModelElement, Set<EModelElement>>();

  private final Set<EModelElement> addedElements = new HashSet<EModelElement>();

  private final Set<EModelElement> removedElements = new HashSet<EModelElement>();

  private ModelSet[] modelSetChain;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ModelSetChangeImpl()
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
    return EvolutionPackage.Literals.MODEL_SET_CHANGE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ModelSet getOldModelSet()
  {
    return (ModelSet)eDynamicGet(EvolutionPackage.MODEL_SET_CHANGE__OLD_MODEL_SET, EvolutionPackage.Literals.MODEL_SET_CHANGE__OLD_MODEL_SET, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ModelSet basicGetOldModelSet()
  {
    return (ModelSet)eDynamicGet(EvolutionPackage.MODEL_SET_CHANGE__OLD_MODEL_SET, EvolutionPackage.Literals.MODEL_SET_CHANGE__OLD_MODEL_SET, false, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setOldModelSet(ModelSet newOldModelSet)
  {
    eDynamicSet(EvolutionPackage.MODEL_SET_CHANGE__OLD_MODEL_SET, EvolutionPackage.Literals.MODEL_SET_CHANGE__OLD_MODEL_SET, newOldModelSet);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ModelSet getNewModelSet()
  {
    return (ModelSet)eDynamicGet(EvolutionPackage.MODEL_SET_CHANGE__NEW_MODEL_SET, EvolutionPackage.Literals.MODEL_SET_CHANGE__NEW_MODEL_SET, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ModelSet basicGetNewModelSet()
  {
    return (ModelSet)eDynamicGet(EvolutionPackage.MODEL_SET_CHANGE__NEW_MODEL_SET, EvolutionPackage.Literals.MODEL_SET_CHANGE__NEW_MODEL_SET, false, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setNewModelSet(ModelSet newNewModelSet)
  {
    eDynamicSet(EvolutionPackage.MODEL_SET_CHANGE__NEW_MODEL_SET, EvolutionPackage.Literals.MODEL_SET_CHANGE__NEW_MODEL_SET, newNewModelSet);
  }

  @Override
  public ModelSetChange getModelSetChange()
  {
    return this;
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
    case EvolutionPackage.MODEL_SET_CHANGE__OLD_MODEL_SET:
      if (resolve)
      {
        return getOldModelSet();
      }
      return basicGetOldModelSet();
    case EvolutionPackage.MODEL_SET_CHANGE__NEW_MODEL_SET:
      if (resolve)
      {
        return getNewModelSet();
      }
      return basicGetNewModelSet();
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
    case EvolutionPackage.MODEL_SET_CHANGE__OLD_MODEL_SET:
      setOldModelSet((ModelSet)newValue);
      return;
    case EvolutionPackage.MODEL_SET_CHANGE__NEW_MODEL_SET:
      setNewModelSet((ModelSet)newValue);
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
    case EvolutionPackage.MODEL_SET_CHANGE__OLD_MODEL_SET:
      setOldModelSet((ModelSet)null);
      return;
    case EvolutionPackage.MODEL_SET_CHANGE__NEW_MODEL_SET:
      setNewModelSet((ModelSet)null);
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
    case EvolutionPackage.MODEL_SET_CHANGE__OLD_MODEL_SET:
      return basicGetOldModelSet() != null;
    case EvolutionPackage.MODEL_SET_CHANGE__NEW_MODEL_SET:
      return basicGetNewModelSet() != null;
    }
    return super.eIsSet(featureID);
  }

  public Map<EModelElement, ElementChange> getElementChanges()
  {
    return elementChanges;
  }

  public Map<EModelElement, EModelElement> getNewToOldElements()
  {
    return newToOldElements;
  }

  public Map<EModelElement, Set<EModelElement>> getOldToNewElements()
  {
    return oldToNewElements;
  }

  public Set<EModelElement> getAddedElements()
  {
    return addedElements;
  }

  public Set<EModelElement> getRemovedElements()
  {
    return removedElements;
  }

  public ModelSet[] getModelSetChain()
  {
    return modelSetChain;
  }

  public void setModelSetChain(ModelSet[] modelSetChain)
  {
    this.modelSetChain = modelSetChain;
    setOldModelSet(modelSetChain[0]);
    setNewModelSet(modelSetChain[modelSetChain.length - 1]);

  }

  public Map<EModelElement, ElementChange> reset()
  {
    Map<EModelElement, ElementChange> result = new HashMap<EModelElement, ElementChange>(elementChanges);

    for (ElementChange elementChange : elementChanges.values())
    {
      elementChange.getChildren().clear();
    }

    getChildren().clear();
    oldToNewElements.clear();
    newToOldElements.clear();
    addedElements.clear();
    removedElements.clear();
    elementChanges.clear();

    return result;
  }

} // ModelSetChangeImpl
