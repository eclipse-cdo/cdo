/**
 */
package org.eclipse.emf.cdo.evolution.impl;

import org.eclipse.emf.cdo.evolution.Change;
import org.eclipse.emf.cdo.evolution.ChangeKind;
import org.eclipse.emf.cdo.evolution.ElementChange;
import org.eclipse.emf.cdo.evolution.EvolutionPackage;
import org.eclipse.emf.cdo.evolution.ModelSet;
import org.eclipse.emf.cdo.evolution.util.DiagnosticID;
import org.eclipse.emf.cdo.evolution.util.IDAnnotation;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EModelElement;

import java.lang.reflect.InvocationTargetException;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Element Change</b></em>'.
 * @implements DiagnosticID.Provider
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.evolution.impl.ElementChangeImpl#getOldElement <em>Old Element</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.impl.ElementChangeImpl#getNewElement <em>New Element</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.evolution.impl.ElementChangeImpl#getKind <em>Kind</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ElementChangeImpl extends ChangeImpl implements ElementChange, DiagnosticID.Provider
{
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
  protected ElementChangeImpl()
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
    return EvolutionPackage.Literals.ELEMENT_CHANGE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EModelElement getOldElement()
  {
    return (EModelElement)eDynamicGet(EvolutionPackage.ELEMENT_CHANGE__OLD_ELEMENT, EvolutionPackage.Literals.ELEMENT_CHANGE__OLD_ELEMENT, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EModelElement basicGetOldElement()
  {
    return (EModelElement)eDynamicGet(EvolutionPackage.ELEMENT_CHANGE__OLD_ELEMENT, EvolutionPackage.Literals.ELEMENT_CHANGE__OLD_ELEMENT, false, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setOldElement(EModelElement newOldElement)
  {
    eDynamicSet(EvolutionPackage.ELEMENT_CHANGE__OLD_ELEMENT, EvolutionPackage.Literals.ELEMENT_CHANGE__OLD_ELEMENT, newOldElement);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EModelElement getNewElement()
  {
    return (EModelElement)eDynamicGet(EvolutionPackage.ELEMENT_CHANGE__NEW_ELEMENT, EvolutionPackage.Literals.ELEMENT_CHANGE__NEW_ELEMENT, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EModelElement basicGetNewElement()
  {
    return (EModelElement)eDynamicGet(EvolutionPackage.ELEMENT_CHANGE__NEW_ELEMENT, EvolutionPackage.Literals.ELEMENT_CHANGE__NEW_ELEMENT, false, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setNewElement(EModelElement newNewElement)
  {
    eDynamicSet(EvolutionPackage.ELEMENT_CHANGE__NEW_ELEMENT, EvolutionPackage.Literals.ELEMENT_CHANGE__NEW_ELEMENT, newNewElement);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ChangeKind getKind()
  {
    return (ChangeKind)eDynamicGet(EvolutionPackage.ELEMENT_CHANGE__KIND, EvolutionPackage.Literals.ELEMENT_CHANGE__KIND, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setKind(ChangeKind newKind)
  {
    eDynamicSet(EvolutionPackage.ELEMENT_CHANGE__KIND, EvolutionPackage.Literals.ELEMENT_CHANGE__KIND, newKind);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public EModelElement getElement()
  {
    EModelElement element = getNewElement();
    if (element == null)
    {
      element = getOldElement();
    }

    return element;
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
    case EvolutionPackage.ELEMENT_CHANGE__OLD_ELEMENT:
      if (resolve)
      {
        return getOldElement();
      }
      return basicGetOldElement();
    case EvolutionPackage.ELEMENT_CHANGE__NEW_ELEMENT:
      if (resolve)
      {
        return getNewElement();
      }
      return basicGetNewElement();
    case EvolutionPackage.ELEMENT_CHANGE__KIND:
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
    case EvolutionPackage.ELEMENT_CHANGE__OLD_ELEMENT:
      setOldElement((EModelElement)newValue);
      return;
    case EvolutionPackage.ELEMENT_CHANGE__NEW_ELEMENT:
      setNewElement((EModelElement)newValue);
      return;
    case EvolutionPackage.ELEMENT_CHANGE__KIND:
      setKind((ChangeKind)newValue);
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
    case EvolutionPackage.ELEMENT_CHANGE__OLD_ELEMENT:
      setOldElement((EModelElement)null);
      return;
    case EvolutionPackage.ELEMENT_CHANGE__NEW_ELEMENT:
      setNewElement((EModelElement)null);
      return;
    case EvolutionPackage.ELEMENT_CHANGE__KIND:
      setKind(KIND_EDEFAULT);
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
    case EvolutionPackage.ELEMENT_CHANGE__OLD_ELEMENT:
      return basicGetOldElement() != null;
    case EvolutionPackage.ELEMENT_CHANGE__NEW_ELEMENT:
      return basicGetNewElement() != null;
    case EvolutionPackage.ELEMENT_CHANGE__KIND:
      return getKind() != KIND_EDEFAULT;
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
    case EvolutionPackage.ELEMENT_CHANGE___GET_ELEMENT:
      return getElement();
    }
    return super.eInvoke(operationID, arguments);
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

  public void extractDiagnosticData(Context context)
  {
    context.add("EC");

    EModelElement newElement = getNewElement();
    if (newElement != null)
    {
      context.add(IDAnnotation.getValue(newElement));
    }

    EModelElement oldElement = getOldElement();
    if (oldElement != null)
    {
      context.add(IDAnnotation.getValue(oldElement));
    }

    context.add(getKind().getName());
  }

} // ElementChangeImpl
