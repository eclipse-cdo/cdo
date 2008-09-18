/**
 * <copyright>
 * </copyright>
 *
 * $Id: MultiNonContainedElementImpl.java,v 1.2 2008-09-18 12:56:48 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.legacy.model4.impl;

import org.eclipse.emf.cdo.tests.legacy.model4.model4Package;
import org.eclipse.emf.cdo.tests.model4.MultiNonContainedElement;
import org.eclipse.emf.cdo.tests.model4.RefMultiNonContained;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Multi Non Contained Element</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.legacy.model4.impl.MultiNonContainedElementImpl#getName <em>Name</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.legacy.model4.impl.MultiNonContainedElementImpl#getParent <em>Parent</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class MultiNonContainedElementImpl extends EObjectImpl implements MultiNonContainedElement
{
  /**
   * The default value of the '{@link #getName() <em>Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @see #getName()
   * @generated
   * @ordered
   */
  protected static final String NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getName() <em>Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getName()
   * @generated
   * @ordered
   */
  protected String name = NAME_EDEFAULT;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected MultiNonContainedElementImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return model4Package.Literals.MULTI_NON_CONTAINED_ELEMENT;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public String getName()
  {
    eFireRead(model4Package.MULTI_NON_CONTAINED_ELEMENT__NAME);
    return name;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setName(String newName)
  {
    eFireWrite(model4Package.MULTI_NON_CONTAINED_ELEMENT__NAME);
    String oldName = name;
    name = newName;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, model4Package.MULTI_NON_CONTAINED_ELEMENT__NAME, oldName,
          name));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public RefMultiNonContained getParent()
  {
    eFireRead(model4Package.MULTI_NON_CONTAINED_ELEMENT__PARENT);
    if (eContainerFeatureID != model4Package.MULTI_NON_CONTAINED_ELEMENT__PARENT)
    {
      return null;
    }
    return (RefMultiNonContained)eContainer();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public NotificationChain basicSetParent(RefMultiNonContained newParent, NotificationChain msgs)
  {
    eFireWrite(model4Package.MULTI_NON_CONTAINED_ELEMENT__PARENT);
    msgs = eBasicSetContainer((InternalEObject)newParent, model4Package.MULTI_NON_CONTAINED_ELEMENT__PARENT, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setParent(RefMultiNonContained newParent)
  {
    eFireWrite(model4Package.MULTI_NON_CONTAINED_ELEMENT__PARENT);
    if (newParent != eInternalContainer() || eContainerFeatureID != model4Package.MULTI_NON_CONTAINED_ELEMENT__PARENT
        && newParent != null)
    {
      if (EcoreUtil.isAncestor(this, newParent))
      {
        throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
      }
      NotificationChain msgs = null;
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      if (newParent != null)
      {
        msgs = ((InternalEObject)newParent).eInverseAdd(this, model4Package.REF_MULTI_NON_CONTAINED__ELEMENTS,
            RefMultiNonContained.class, msgs);
      }
      msgs = basicSetParent(newParent, msgs);
      if (msgs != null)
      {
        msgs.dispatch();
      }
    }
    else if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, model4Package.MULTI_NON_CONTAINED_ELEMENT__PARENT,
          newParent, newParent));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case model4Package.MULTI_NON_CONTAINED_ELEMENT__PARENT:
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      return basicSetParent((RefMultiNonContained)otherEnd, msgs);
    }
    return super.eInverseAdd(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case model4Package.MULTI_NON_CONTAINED_ELEMENT__PARENT:
      return basicSetParent(null, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs)
  {
    switch (eContainerFeatureID)
    {
    case model4Package.MULTI_NON_CONTAINED_ELEMENT__PARENT:
      return eInternalContainer().eInverseRemove(this, model4Package.REF_MULTI_NON_CONTAINED__ELEMENTS,
          RefMultiNonContained.class, msgs);
    }
    return super.eBasicRemoveFromContainerFeature(msgs);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case model4Package.MULTI_NON_CONTAINED_ELEMENT__NAME:
      return getName();
    case model4Package.MULTI_NON_CONTAINED_ELEMENT__PARENT:
      return getParent();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case model4Package.MULTI_NON_CONTAINED_ELEMENT__NAME:
      setName((String)newValue);
      return;
    case model4Package.MULTI_NON_CONTAINED_ELEMENT__PARENT:
      setParent((RefMultiNonContained)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case model4Package.MULTI_NON_CONTAINED_ELEMENT__NAME:
      setName(NAME_EDEFAULT);
      return;
    case model4Package.MULTI_NON_CONTAINED_ELEMENT__PARENT:
      setParent((RefMultiNonContained)null);
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case model4Package.MULTI_NON_CONTAINED_ELEMENT__NAME:
      return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
    case model4Package.MULTI_NON_CONTAINED_ELEMENT__PARENT:
      return getParent() != null;
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy())
    {
      return super.toString();
    }

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (name: ");
    result.append(name);
    result.append(')');
    return result.toString();
  }

} // MultiNonContainedElementImpl
