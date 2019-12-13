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

import org.eclipse.emf.cdo.tests.model6.B;
import org.eclipse.emf.cdo.tests.model6.C;
import org.eclipse.emf.cdo.tests.model6.legacy.Model6Package;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>B</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.BImpl#getOwnedC <em>Owned C</em>}</li>
 * </ul>
 *
 * @generated
 */
public class BImpl extends EObjectImpl implements B
{
  /**
   * The cached value of the '{@link #getOwnedC() <em>Owned C</em>}' containment reference.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #getOwnedC()
   * @generated
   * @ordered
   */
  protected C ownedC;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected BImpl()
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
    return Model6Package.eINSTANCE.getB();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public C getOwnedC()
  {
    if (ownedC != null && ownedC.eIsProxy())
    {
      InternalEObject oldOwnedC = (InternalEObject)ownedC;
      ownedC = (C)eResolveProxy(oldOwnedC);
      if (ownedC != oldOwnedC)
      {
        InternalEObject newOwnedC = (InternalEObject)ownedC;
        NotificationChain msgs = oldOwnedC.eInverseRemove(this, EOPPOSITE_FEATURE_BASE - Model6Package.B__OWNED_C, null, null);
        if (newOwnedC.eInternalContainer() == null)
        {
          msgs = newOwnedC.eInverseAdd(this, EOPPOSITE_FEATURE_BASE - Model6Package.B__OWNED_C, null, msgs);
        }
        if (msgs != null)
        {
          msgs.dispatch();
        }
        if (eNotificationRequired())
        {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, Model6Package.B__OWNED_C, oldOwnedC, ownedC));
        }
      }
    }
    return ownedC;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public C basicGetOwnedC()
  {
    return ownedC;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetOwnedC(C newOwnedC, NotificationChain msgs)
  {
    C oldOwnedC = ownedC;
    ownedC = newOwnedC;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, Model6Package.B__OWNED_C, oldOwnedC, newOwnedC);
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
  public void setOwnedC(C newOwnedC)
  {
    if (newOwnedC != ownedC)
    {
      NotificationChain msgs = null;
      if (ownedC != null)
      {
        msgs = ((InternalEObject)ownedC).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - Model6Package.B__OWNED_C, null, msgs);
      }
      if (newOwnedC != null)
      {
        msgs = ((InternalEObject)newOwnedC).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - Model6Package.B__OWNED_C, null, msgs);
      }
      msgs = basicSetOwnedC(newOwnedC, msgs);
      if (msgs != null)
      {
        msgs.dispatch();
      }
    }
    else if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model6Package.B__OWNED_C, newOwnedC, newOwnedC));
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
    case Model6Package.B__OWNED_C:
      return basicSetOwnedC(null, msgs);
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
    case Model6Package.B__OWNED_C:
      if (resolve)
      {
        return getOwnedC();
      }
      return basicGetOwnedC();
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
    case Model6Package.B__OWNED_C:
      setOwnedC((C)newValue);
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
    case Model6Package.B__OWNED_C:
      setOwnedC((C)null);
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
    case Model6Package.B__OWNED_C:
      return ownedC != null;
    }
    return super.eIsSet(featureID);
  }

} // BImpl
