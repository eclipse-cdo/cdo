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

import org.eclipse.emf.cdo.tests.model4.MultiNonContainedUnsettableElement;
import org.eclipse.emf.cdo.tests.model4.RefMultiNonContainedUnsettable;
import org.eclipse.emf.cdo.tests.model4.legacy.model4Package;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Multi Non Contained Unsettable Element</b></em>
 * '. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model4.legacy.impl.MultiNonContainedUnsettableElementImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model4.legacy.impl.MultiNonContainedUnsettableElementImpl#getParent <em>Parent</em>}</li>
 * </ul>
 *
 * @generated
 */
public class MultiNonContainedUnsettableElementImpl extends EObjectImpl implements MultiNonContainedUnsettableElement
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
   * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected String name = NAME_EDEFAULT;

  /**
   * The cached value of the '{@link #getParent() <em>Parent</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @see #getParent()
   * @generated
   * @ordered
   */
  protected RefMultiNonContainedUnsettable parent;

  /**
   * This is true if the Parent reference has been set.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  protected boolean parentESet;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected MultiNonContainedUnsettableElementImpl()
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
    return model4Package.eINSTANCE.getMultiNonContainedUnsettableElement();
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
      eNotify(new ENotificationImpl(this, Notification.SET, model4Package.MULTI_NON_CONTAINED_UNSETTABLE_ELEMENT__NAME, oldName, name));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public RefMultiNonContainedUnsettable getParent()
  {
    if (parent != null && parent.eIsProxy())
    {
      InternalEObject oldParent = (InternalEObject)parent;
      parent = (RefMultiNonContainedUnsettable)eResolveProxy(oldParent);
      if (parent != oldParent)
      {
        if (eNotificationRequired())
        {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, model4Package.MULTI_NON_CONTAINED_UNSETTABLE_ELEMENT__PARENT, oldParent, parent));
        }
      }
    }
    return parent;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public RefMultiNonContainedUnsettable basicGetParent()
  {
    return parent;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetParent(RefMultiNonContainedUnsettable newParent, NotificationChain msgs)
  {
    RefMultiNonContainedUnsettable oldParent = parent;
    parent = newParent;
    boolean oldParentESet = parentESet;
    parentESet = true;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, model4Package.MULTI_NON_CONTAINED_UNSETTABLE_ELEMENT__PARENT, oldParent,
          newParent, !oldParentESet);
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
  public void setParent(RefMultiNonContainedUnsettable newParent)
  {
    if (newParent != parent)
    {
      NotificationChain msgs = null;
      if (parent != null)
      {
        msgs = ((InternalEObject)parent).eInverseRemove(this, model4Package.REF_MULTI_NON_CONTAINED_UNSETTABLE__ELEMENTS, RefMultiNonContainedUnsettable.class,
            msgs);
      }
      if (newParent != null)
      {
        msgs = ((InternalEObject)newParent).eInverseAdd(this, model4Package.REF_MULTI_NON_CONTAINED_UNSETTABLE__ELEMENTS, RefMultiNonContainedUnsettable.class,
            msgs);
      }
      msgs = basicSetParent(newParent, msgs);
      if (msgs != null)
      {
        msgs.dispatch();
      }
    }
    else
    {
      boolean oldParentESet = parentESet;
      parentESet = true;
      if (eNotificationRequired())
      {
        eNotify(
            new ENotificationImpl(this, Notification.SET, model4Package.MULTI_NON_CONTAINED_UNSETTABLE_ELEMENT__PARENT, newParent, newParent, !oldParentESet));
      }
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicUnsetParent(NotificationChain msgs)
  {
    RefMultiNonContainedUnsettable oldParent = parent;
    parent = null;
    boolean oldParentESet = parentESet;
    parentESet = false;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.UNSET, model4Package.MULTI_NON_CONTAINED_UNSETTABLE_ELEMENT__PARENT, oldParent,
          null, oldParentESet);
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
  public void unsetParent()
  {
    if (parent != null)
    {
      NotificationChain msgs = null;
      msgs = ((InternalEObject)parent).eInverseRemove(this, model4Package.REF_MULTI_NON_CONTAINED_UNSETTABLE__ELEMENTS, RefMultiNonContainedUnsettable.class,
          msgs);
      msgs = basicUnsetParent(msgs);
      if (msgs != null)
      {
        msgs.dispatch();
      }
    }
    else
    {
      boolean oldParentESet = parentESet;
      parentESet = false;
      if (eNotificationRequired())
      {
        eNotify(new ENotificationImpl(this, Notification.UNSET, model4Package.MULTI_NON_CONTAINED_UNSETTABLE_ELEMENT__PARENT, null, null, oldParentESet));
      }
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetParent()
  {
    return parentESet;
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
    case model4Package.MULTI_NON_CONTAINED_UNSETTABLE_ELEMENT__PARENT:
      if (parent != null)
      {
        msgs = ((InternalEObject)parent).eInverseRemove(this, model4Package.REF_MULTI_NON_CONTAINED_UNSETTABLE__ELEMENTS, RefMultiNonContainedUnsettable.class,
            msgs);
      }
      return basicSetParent((RefMultiNonContainedUnsettable)otherEnd, msgs);
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
    case model4Package.MULTI_NON_CONTAINED_UNSETTABLE_ELEMENT__PARENT:
      return basicUnsetParent(msgs);
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
    case model4Package.MULTI_NON_CONTAINED_UNSETTABLE_ELEMENT__NAME:
      return getName();
    case model4Package.MULTI_NON_CONTAINED_UNSETTABLE_ELEMENT__PARENT:
      if (resolve)
      {
        return getParent();
      }
      return basicGetParent();
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
    case model4Package.MULTI_NON_CONTAINED_UNSETTABLE_ELEMENT__NAME:
      setName((String)newValue);
      return;
    case model4Package.MULTI_NON_CONTAINED_UNSETTABLE_ELEMENT__PARENT:
      setParent((RefMultiNonContainedUnsettable)newValue);
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
    case model4Package.MULTI_NON_CONTAINED_UNSETTABLE_ELEMENT__NAME:
      setName(NAME_EDEFAULT);
      return;
    case model4Package.MULTI_NON_CONTAINED_UNSETTABLE_ELEMENT__PARENT:
      unsetParent();
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
    case model4Package.MULTI_NON_CONTAINED_UNSETTABLE_ELEMENT__NAME:
      return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
    case model4Package.MULTI_NON_CONTAINED_UNSETTABLE_ELEMENT__PARENT:
      return isSetParent();
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

} // MultiNonContainedUnsettableElementImpl
