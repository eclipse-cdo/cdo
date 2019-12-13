/*
 * Copyright (c) 2013, 2015, 2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model4.legacy.impl;

import org.eclipse.emf.cdo.tests.model4.ImplSingleRefNonContainedElement;
import org.eclipse.emf.cdo.tests.model4.legacy.model4Package;
import org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefNonContainer;
import org.eclipse.emf.cdo.tests.model4interfaces.legacy.model4interfacesPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Impl Single Ref Non Contained Element</b></em>
 * '. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model4.legacy.impl.ImplSingleRefNonContainedElementImpl#getParent <em>Parent</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model4.legacy.impl.ImplSingleRefNonContainedElementImpl#getName <em>Name</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ImplSingleRefNonContainedElementImpl extends EObjectImpl implements ImplSingleRefNonContainedElement
{
  /**
   * The cached value of the '{@link #getParent() <em>Parent</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @see #getParent()
   * @generated
   * @ordered
   */
  protected ISingleRefNonContainer parent;

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
   * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected String name = NAME_EDEFAULT;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected ImplSingleRefNonContainedElementImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return model4Package.eINSTANCE.getImplSingleRefNonContainedElement();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ISingleRefNonContainer getParent()
  {
    if (parent != null && parent.eIsProxy())
    {
      InternalEObject oldParent = (InternalEObject)parent;
      parent = (ISingleRefNonContainer)eResolveProxy(oldParent);
      if (parent != oldParent)
      {
        if (eNotificationRequired())
        {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, model4Package.IMPL_SINGLE_REF_NON_CONTAINED_ELEMENT__PARENT, oldParent, parent));
        }
      }
    }
    return parent;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public ISingleRefNonContainer basicGetParent()
  {
    return parent;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetParent(ISingleRefNonContainer newParent, NotificationChain msgs)
  {
    ISingleRefNonContainer oldParent = parent;
    parent = newParent;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, model4Package.IMPL_SINGLE_REF_NON_CONTAINED_ELEMENT__PARENT, oldParent,
          newParent);
      if (msgs == null)
      {
        msgs = notification;
      }
      else
      {
        msgs.add(notification);
      }
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setParent(ISingleRefNonContainer newParent)
  {
    if (newParent != parent)
    {
      NotificationChain msgs = null;
      if (parent != null)
      {
        msgs = ((InternalEObject)parent).eInverseRemove(this, model4interfacesPackage.ISINGLE_REF_NON_CONTAINER__ELEMENT, ISingleRefNonContainer.class, msgs);
      }
      if (newParent != null)
      {
        msgs = ((InternalEObject)newParent).eInverseAdd(this, model4interfacesPackage.ISINGLE_REF_NON_CONTAINER__ELEMENT, ISingleRefNonContainer.class, msgs);
      }
      msgs = basicSetParent(newParent, msgs);
      if (msgs != null)
      {
        msgs.dispatch();
      }
    }
    else if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, model4Package.IMPL_SINGLE_REF_NON_CONTAINED_ELEMENT__PARENT, newParent, newParent));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getName()
  {
    return name;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setName(String newName)
  {
    String oldName = name;
    name = newName;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, model4Package.IMPL_SINGLE_REF_NON_CONTAINED_ELEMENT__NAME, oldName, name));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case model4Package.IMPL_SINGLE_REF_NON_CONTAINED_ELEMENT__PARENT:
      if (parent != null)
      {
        msgs = ((InternalEObject)parent).eInverseRemove(this, model4interfacesPackage.ISINGLE_REF_NON_CONTAINER__ELEMENT, ISingleRefNonContainer.class, msgs);
      }
      return basicSetParent((ISingleRefNonContainer)otherEnd, msgs);
    }
    return super.eInverseAdd(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case model4Package.IMPL_SINGLE_REF_NON_CONTAINED_ELEMENT__PARENT:
      return basicSetParent(null, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case model4Package.IMPL_SINGLE_REF_NON_CONTAINED_ELEMENT__PARENT:
      if (resolve)
      {
        return getParent();
      }
      return basicGetParent();
    case model4Package.IMPL_SINGLE_REF_NON_CONTAINED_ELEMENT__NAME:
      return getName();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case model4Package.IMPL_SINGLE_REF_NON_CONTAINED_ELEMENT__PARENT:
      setParent((ISingleRefNonContainer)newValue);
      return;
    case model4Package.IMPL_SINGLE_REF_NON_CONTAINED_ELEMENT__NAME:
      setName((String)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case model4Package.IMPL_SINGLE_REF_NON_CONTAINED_ELEMENT__PARENT:
      setParent((ISingleRefNonContainer)null);
      return;
    case model4Package.IMPL_SINGLE_REF_NON_CONTAINED_ELEMENT__NAME:
      setName(NAME_EDEFAULT);
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case model4Package.IMPL_SINGLE_REF_NON_CONTAINED_ELEMENT__PARENT:
      return parent != null;
    case model4Package.IMPL_SINGLE_REF_NON_CONTAINED_ELEMENT__NAME:
      return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy())
    {
      return super.toString();
    }

    StringBuilder result = new StringBuilder(super.toString());
    result.append(" (name: ");
    result.append(name);
    result.append(')');
    return result.toString();
  }

} // ImplSingleRefNonContainedElementImpl
