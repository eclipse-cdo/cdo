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
import org.eclipse.emf.cdo.tests.model6.ReferenceObject;
import org.eclipse.emf.cdo.tests.model6.legacy.Model6Package;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;

import java.util.Collection;

/**
 * <!-- begin-user-doc --> An implementation of the model object ' <em><b>Reference Object</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>
 * {@link org.eclipse.emf.cdo.tests.legacy.model6.impl.ReferenceObjectImpl#getReferenceOptional <em>Reference
 * Optional</em>}</li>
 * <li>
 * {@link org.eclipse.emf.cdo.tests.legacy.model6.impl.ReferenceObjectImpl#getReferenceList <em>Reference List</em>}
 * </li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ReferenceObjectImpl extends BaseObjectImpl implements ReferenceObject
{
  /**
   * The cached value of the '{@link #getReferenceOptional() <em>Reference Optional</em>}' reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getReferenceOptional()
   * @generated
   * @ordered
   */
  protected BaseObject referenceOptional;

  /**
   * The cached value of the '{@link #getReferenceList() <em>Reference List</em>}' reference list.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @see #getReferenceList()
   * @generated
   * @ordered
   */
  protected EList<BaseObject> referenceList;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected ReferenceObjectImpl()
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
    return Model6Package.eINSTANCE.getReferenceObject();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public BaseObject getReferenceOptional()
  {
    if (referenceOptional != null && referenceOptional.eIsProxy())
    {
      InternalEObject oldReferenceOptional = (InternalEObject)referenceOptional;
      referenceOptional = (BaseObject)eResolveProxy(oldReferenceOptional);
      if (referenceOptional != oldReferenceOptional)
      {
        if (eNotificationRequired())
        {
          eNotify(
              new ENotificationImpl(this, Notification.RESOLVE, Model6Package.REFERENCE_OBJECT__REFERENCE_OPTIONAL, oldReferenceOptional, referenceOptional));
        }
      }
    }
    return referenceOptional;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public BaseObject basicGetReferenceOptional()
  {
    return referenceOptional;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setReferenceOptional(BaseObject newReferenceOptional)
  {
    BaseObject oldReferenceOptional = referenceOptional;
    referenceOptional = newReferenceOptional;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model6Package.REFERENCE_OBJECT__REFERENCE_OPTIONAL, oldReferenceOptional, referenceOptional));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<BaseObject> getReferenceList()
  {
    if (referenceList == null)
    {
      referenceList = new EObjectResolvingEList<>(BaseObject.class, this, Model6Package.REFERENCE_OBJECT__REFERENCE_LIST);
    }
    return referenceList;
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
    case Model6Package.REFERENCE_OBJECT__REFERENCE_OPTIONAL:
      if (resolve)
      {
        return getReferenceOptional();
      }
      return basicGetReferenceOptional();
    case Model6Package.REFERENCE_OBJECT__REFERENCE_LIST:
      return getReferenceList();
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
    case Model6Package.REFERENCE_OBJECT__REFERENCE_OPTIONAL:
      setReferenceOptional((BaseObject)newValue);
      return;
    case Model6Package.REFERENCE_OBJECT__REFERENCE_LIST:
      getReferenceList().clear();
      getReferenceList().addAll((Collection<? extends BaseObject>)newValue);
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
    case Model6Package.REFERENCE_OBJECT__REFERENCE_OPTIONAL:
      setReferenceOptional((BaseObject)null);
      return;
    case Model6Package.REFERENCE_OBJECT__REFERENCE_LIST:
      getReferenceList().clear();
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
    case Model6Package.REFERENCE_OBJECT__REFERENCE_OPTIONAL:
      return referenceOptional != null;
    case Model6Package.REFERENCE_OBJECT__REFERENCE_LIST:
      return referenceList != null && !referenceList.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} // ReferenceObjectImpl
