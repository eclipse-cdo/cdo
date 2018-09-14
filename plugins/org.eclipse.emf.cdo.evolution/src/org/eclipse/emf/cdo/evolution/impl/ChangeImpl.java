/**
 */
package org.eclipse.emf.cdo.evolution.impl;

import org.eclipse.emf.cdo.evolution.Change;
import org.eclipse.emf.cdo.evolution.EvolutionPackage;
import org.eclipse.emf.cdo.evolution.ModelSet;
import org.eclipse.emf.cdo.evolution.ModelSetChange;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.InternalEList;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Set;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Change</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.evolution.impl.ChangeImpl#getParent <em>Parent</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.impl.ChangeImpl#getChildren <em>Children</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class ChangeImpl extends CDOObjectImpl implements Change
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ChangeImpl()
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
    return EvolutionPackage.Literals.CHANGE;
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
  public Change getParent()
  {
    return (Change)eDynamicGet(EvolutionPackage.CHANGE__PARENT, EvolutionPackage.Literals.CHANGE__PARENT, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetParent(Change newParent, NotificationChain msgs)
  {
    msgs = eBasicSetContainer((InternalEObject)newParent, EvolutionPackage.CHANGE__PARENT, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setParent(Change newParent)
  {
    eDynamicSet(EvolutionPackage.CHANGE__PARENT, EvolutionPackage.Literals.CHANGE__PARENT, newParent);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EList<Change> getChildren()
  {
    return (EList<Change>)eDynamicGet(EvolutionPackage.CHANGE__CHILDREN, EvolutionPackage.Literals.CHANGE__CHILDREN, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public abstract ModelSet getOldModelSet();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public abstract ModelSet getNewModelSet();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public ModelSetChange getModelSetChange()
  {
    Change parent = getParent();
    if (parent != null)
    {
      return parent.getModelSetChange();
    }

    return null;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public EModelElement getOldElementFor(EModelElement newElement)
  {
    ModelSetChange modelSetChange = getModelSetChange();
    if (modelSetChange != null)
    {
      return modelSetChange.getNewToOldElements().get(newElement);
    }

    return null;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public EList<EModelElement> getNewElementsFor(EModelElement oldElement)
  {
    ModelSetChange modelSetChange = getModelSetChange();
    if (modelSetChange != null)
    {
      Set<EModelElement> newElements = modelSetChange.getOldToNewElements().get(oldElement);
      if (newElements != null)
      {
        int size = newElements.size();
        if (size == 1)
        {
          return ECollections.singletonEList(newElements.iterator().next());
        }

        if (size > 1)
        {
          return new BasicEList<EModelElement>(newElements);
        }
      }
    }

    return ECollections.emptyEList();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case EvolutionPackage.CHANGE__PARENT:
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      return basicSetParent((Change)otherEnd, msgs);
    case EvolutionPackage.CHANGE__CHILDREN:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getChildren()).basicAdd(otherEnd, msgs);
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
    case EvolutionPackage.CHANGE__PARENT:
      return basicSetParent(null, msgs);
    case EvolutionPackage.CHANGE__CHILDREN:
      return ((InternalEList<?>)getChildren()).basicRemove(otherEnd, msgs);
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
    case EvolutionPackage.CHANGE__PARENT:
      return eInternalContainer().eInverseRemove(this, EvolutionPackage.CHANGE__CHILDREN, Change.class, msgs);
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
    case EvolutionPackage.CHANGE__PARENT:
      return getParent();
    case EvolutionPackage.CHANGE__CHILDREN:
      return getChildren();
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
    case EvolutionPackage.CHANGE__PARENT:
      setParent((Change)newValue);
      return;
    case EvolutionPackage.CHANGE__CHILDREN:
      getChildren().clear();
      getChildren().addAll((Collection<? extends Change>)newValue);
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
    case EvolutionPackage.CHANGE__PARENT:
      setParent((Change)null);
      return;
    case EvolutionPackage.CHANGE__CHILDREN:
      getChildren().clear();
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
    case EvolutionPackage.CHANGE__PARENT:
      return getParent() != null;
    case EvolutionPackage.CHANGE__CHILDREN:
      return !getChildren().isEmpty();
    }
    return super.eIsSet(featureID);
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
    case EvolutionPackage.CHANGE___GET_MODEL_SET_CHANGE:
      return getModelSetChange();
    case EvolutionPackage.CHANGE___GET_OLD_ELEMENT_FOR__EMODELELEMENT:
      return getOldElementFor((EModelElement)arguments.get(0));
    case EvolutionPackage.CHANGE___GET_NEW_ELEMENTS_FOR__EMODELELEMENT:
      return getNewElementsFor((EModelElement)arguments.get(0));
    case EvolutionPackage.CHANGE___GET_OLD_MODEL_SET:
      return getOldModelSet();
    case EvolutionPackage.CHANGE___GET_NEW_MODEL_SET:
      return getNewModelSet();
    }
    return super.eInvoke(operationID, arguments);
  }

} // ChangeImpl
