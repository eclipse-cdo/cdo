/*
 * Copyright (c) 2013, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *
 * $Id: EObjectToEObjectValueContainedMapImpl.java,v 1.2 2011-01-01 11:01:57 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.model2.legacy.impl;

import org.eclipse.emf.cdo.tests.model2.legacy.Model2Package;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.BasicEMap;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>EObject To EObject Value Contained Map</b></em>
 * '. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.legacy.impl.EObjectToEObjectValueContainedMapImpl#getTypedKey <em>Key</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.legacy.impl.EObjectToEObjectValueContainedMapImpl#getTypedValue <em>Value</em>}</li>
 * </ul>
 *
 * @generated
 */
public class EObjectToEObjectValueContainedMapImpl extends EObjectImpl implements BasicEMap.Entry<EObject, EObject>
{
  /**
   * The cached value of the '{@link #getTypedKey() <em>Key</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @see #getTypedKey()
   * @generated
   * @ordered
   */
  protected EObject key;

  /**
   * The cached value of the '{@link #getTypedValue() <em>Value</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTypedValue()
   * @generated
   * @ordered
   */
  protected EObject value;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected EObjectToEObjectValueContainedMapImpl()
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
    return Model2Package.eINSTANCE.getEObjectToEObjectValueContainedMap();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EObject getTypedKey()
  {
    if (key != null && key.eIsProxy())
    {
      InternalEObject oldKey = (InternalEObject)key;
      key = eResolveProxy(oldKey);
      if (key != oldKey)
      {
        if (eNotificationRequired())
        {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, Model2Package.EOBJECT_TO_EOBJECT_VALUE_CONTAINED_MAP__KEY, oldKey, key));
        }
      }
    }
    return key;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EObject basicGetTypedKey()
  {
    return key;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public void setTypedKey(EObject newKey)
  {
    EObject oldKey = key;
    key = newKey;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model2Package.EOBJECT_TO_EOBJECT_VALUE_CONTAINED_MAP__KEY, oldKey, key));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EObject getTypedValue()
  {
    return value;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetTypedValue(EObject newValue, NotificationChain msgs)
  {
    EObject oldValue = value;
    value = newValue;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, Model2Package.EOBJECT_TO_EOBJECT_VALUE_CONTAINED_MAP__VALUE, oldValue,
          newValue);
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
  public void setTypedValue(EObject newValue)
  {
    if (newValue != value)
    {
      NotificationChain msgs = null;
      if (value != null)
      {
        msgs = ((InternalEObject)value).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - Model2Package.EOBJECT_TO_EOBJECT_VALUE_CONTAINED_MAP__VALUE, null, msgs);
      }
      if (newValue != null)
      {
        msgs = ((InternalEObject)newValue).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - Model2Package.EOBJECT_TO_EOBJECT_VALUE_CONTAINED_MAP__VALUE, null, msgs);
      }
      msgs = basicSetTypedValue(newValue, msgs);
      if (msgs != null)
      {
        msgs.dispatch();
      }
    }
    else if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model2Package.EOBJECT_TO_EOBJECT_VALUE_CONTAINED_MAP__VALUE, newValue, newValue));
    }
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
    case Model2Package.EOBJECT_TO_EOBJECT_VALUE_CONTAINED_MAP__VALUE:
      return basicSetTypedValue(null, msgs);
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
    case Model2Package.EOBJECT_TO_EOBJECT_VALUE_CONTAINED_MAP__KEY:
      if (resolve)
      {
        return getTypedKey();
      }
      return basicGetTypedKey();
    case Model2Package.EOBJECT_TO_EOBJECT_VALUE_CONTAINED_MAP__VALUE:
      return getTypedValue();
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
    case Model2Package.EOBJECT_TO_EOBJECT_VALUE_CONTAINED_MAP__KEY:
      setTypedKey((EObject)newValue);
      return;
    case Model2Package.EOBJECT_TO_EOBJECT_VALUE_CONTAINED_MAP__VALUE:
      setTypedValue((EObject)newValue);
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
    case Model2Package.EOBJECT_TO_EOBJECT_VALUE_CONTAINED_MAP__KEY:
      setTypedKey((EObject)null);
      return;
    case Model2Package.EOBJECT_TO_EOBJECT_VALUE_CONTAINED_MAP__VALUE:
      setTypedValue((EObject)null);
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
    case Model2Package.EOBJECT_TO_EOBJECT_VALUE_CONTAINED_MAP__KEY:
      return key != null;
    case Model2Package.EOBJECT_TO_EOBJECT_VALUE_CONTAINED_MAP__VALUE:
      return value != null;
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected int hash = -1;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int getHash()
  {
    if (hash == -1)
    {
      Object theKey = getKey();
      hash = theKey == null ? 0 : theKey.hashCode();
    }
    return hash;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setHash(int hash)
  {
    this.hash = hash;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObject getKey()
  {
    return getTypedKey();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setKey(EObject key)
  {
    setTypedKey(key);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObject getValue()
  {
    return getTypedValue();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObject setValue(EObject value)
  {
    EObject oldValue = getValue();
    setTypedValue(value);
    return oldValue;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EMap<EObject, EObject> getEMap()
  {
    EObject container = eContainer();
    return container == null ? null : (EMap<EObject, EObject>)container.eGet(eContainmentFeature());
  }

} // EObjectToEObjectValueContainedMapImpl
