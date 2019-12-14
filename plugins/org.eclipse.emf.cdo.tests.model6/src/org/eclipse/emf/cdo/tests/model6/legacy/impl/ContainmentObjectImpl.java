/*
 * Copyright (c) 2013, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model6.legacy.impl;

import org.eclipse.emf.cdo.tests.model6.BaseObject;
import org.eclipse.emf.cdo.tests.model6.ContainmentObject;
import org.eclipse.emf.cdo.tests.model6.legacy.Model6Package;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import java.util.Collection;

/**
 * <!-- begin-user-doc --> An implementation of the model object ' <em><b>Containment Object</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.ContainmentObjectImpl#getContainmentOptional <em>Containment Optional</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.ContainmentObjectImpl#getContainmentList <em>Containment List</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ContainmentObjectImpl extends BaseObjectImpl implements ContainmentObject
{
  /**
   * The cached value of the '{@link #getContainmentOptional() <em>Containment Optional</em>}' containment reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getContainmentOptional()
   * @generated
   * @ordered
   */
  protected BaseObject containmentOptional;

  /**
   * The cached value of the '{@link #getContainmentList() <em>Containment List</em>}' containment reference list. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getContainmentList()
   * @generated
   * @ordered
   */
  protected EList<BaseObject> containmentList;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected ContainmentObjectImpl()
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
    return Model6Package.eINSTANCE.getContainmentObject();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public BaseObject getContainmentOptional()
  {
    if (containmentOptional != null && containmentOptional.eIsProxy())
    {
      InternalEObject oldContainmentOptional = (InternalEObject)containmentOptional;
      containmentOptional = (BaseObject)eResolveProxy(oldContainmentOptional);
      if (containmentOptional != oldContainmentOptional)
      {
        InternalEObject newContainmentOptional = (InternalEObject)containmentOptional;
        NotificationChain msgs = oldContainmentOptional.eInverseRemove(this, EOPPOSITE_FEATURE_BASE - Model6Package.CONTAINMENT_OBJECT__CONTAINMENT_OPTIONAL,
            null, null);
        if (newContainmentOptional.eInternalContainer() == null)
        {
          msgs = newContainmentOptional.eInverseAdd(this, EOPPOSITE_FEATURE_BASE - Model6Package.CONTAINMENT_OBJECT__CONTAINMENT_OPTIONAL, null, msgs);
        }
        if (msgs != null)
        {
          msgs.dispatch();
        }
        if (eNotificationRequired())
        {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, Model6Package.CONTAINMENT_OBJECT__CONTAINMENT_OPTIONAL, oldContainmentOptional,
              containmentOptional));
        }
      }
    }
    return containmentOptional;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public BaseObject basicGetContainmentOptional()
  {
    return containmentOptional;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetContainmentOptional(BaseObject newContainmentOptional, NotificationChain msgs)
  {
    BaseObject oldContainmentOptional = containmentOptional;
    containmentOptional = newContainmentOptional;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, Model6Package.CONTAINMENT_OBJECT__CONTAINMENT_OPTIONAL,
          oldContainmentOptional, newContainmentOptional);
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
  public void setContainmentOptional(BaseObject newContainmentOptional)
  {
    if (newContainmentOptional != containmentOptional)
    {
      NotificationChain msgs = null;
      if (containmentOptional != null)
      {
        msgs = ((InternalEObject)containmentOptional).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - Model6Package.CONTAINMENT_OBJECT__CONTAINMENT_OPTIONAL,
            null, msgs);
      }
      if (newContainmentOptional != null)
      {
        msgs = ((InternalEObject)newContainmentOptional).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - Model6Package.CONTAINMENT_OBJECT__CONTAINMENT_OPTIONAL,
            null, msgs);
      }
      msgs = basicSetContainmentOptional(newContainmentOptional, msgs);
      if (msgs != null)
      {
        msgs.dispatch();
      }
    }
    else if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model6Package.CONTAINMENT_OBJECT__CONTAINMENT_OPTIONAL, newContainmentOptional,
          newContainmentOptional));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<BaseObject> getContainmentList()
  {
    if (containmentList == null)
    {
      containmentList = new EObjectContainmentEList.Resolving<>(BaseObject.class, this, Model6Package.CONTAINMENT_OBJECT__CONTAINMENT_LIST);
    }
    return containmentList;
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
    case Model6Package.CONTAINMENT_OBJECT__CONTAINMENT_OPTIONAL:
      return basicSetContainmentOptional(null, msgs);
    case Model6Package.CONTAINMENT_OBJECT__CONTAINMENT_LIST:
      return ((InternalEList<?>)getContainmentList()).basicRemove(otherEnd, msgs);
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
    case Model6Package.CONTAINMENT_OBJECT__CONTAINMENT_OPTIONAL:
      if (resolve)
      {
        return getContainmentOptional();
      }
      return basicGetContainmentOptional();
    case Model6Package.CONTAINMENT_OBJECT__CONTAINMENT_LIST:
      return getContainmentList();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case Model6Package.CONTAINMENT_OBJECT__CONTAINMENT_OPTIONAL:
      setContainmentOptional((BaseObject)newValue);
      return;
    case Model6Package.CONTAINMENT_OBJECT__CONTAINMENT_LIST:
      getContainmentList().clear();
      getContainmentList().addAll((Collection<? extends BaseObject>)newValue);
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
    case Model6Package.CONTAINMENT_OBJECT__CONTAINMENT_OPTIONAL:
      setContainmentOptional((BaseObject)null);
      return;
    case Model6Package.CONTAINMENT_OBJECT__CONTAINMENT_LIST:
      getContainmentList().clear();
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
    case Model6Package.CONTAINMENT_OBJECT__CONTAINMENT_OPTIONAL:
      return containmentOptional != null;
    case Model6Package.CONTAINMENT_OBJECT__CONTAINMENT_LIST:
      return containmentList != null && !containmentList.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} // ContainmentObjectImpl
