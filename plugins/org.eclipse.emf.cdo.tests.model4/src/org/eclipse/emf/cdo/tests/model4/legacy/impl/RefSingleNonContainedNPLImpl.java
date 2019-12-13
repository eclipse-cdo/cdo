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
package org.eclipse.emf.cdo.tests.model4.legacy.impl;

import org.eclipse.emf.cdo.tests.model4.ContainedElementNoOpposite;
import org.eclipse.emf.cdo.tests.model4.RefSingleNonContainedNPL;
import org.eclipse.emf.cdo.tests.model4.legacy.model4Package;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Ref Single Non Contained NPL</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model4.legacy.impl.RefSingleNonContainedNPLImpl#getElement <em>Element</em>}</li>
 * </ul>
 *
 * @generated
 */
public class RefSingleNonContainedNPLImpl extends EObjectImpl implements RefSingleNonContainedNPL
{
  /**
   * The cached value of the '{@link #getElement() <em>Element</em>}' reference.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #getElement()
   * @generated
   * @ordered
   */
  protected ContainedElementNoOpposite element;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected RefSingleNonContainedNPLImpl()
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
    return model4Package.eINSTANCE.getRefSingleNonContainedNPL();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ContainedElementNoOpposite getElement()
  {
    if (element != null && element.eIsProxy())
    {
      InternalEObject oldElement = (InternalEObject)element;
      element = (ContainedElementNoOpposite)eResolveProxy(oldElement);
      if (element != oldElement)
      {
        if (eNotificationRequired())
        {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, model4Package.REF_SINGLE_NON_CONTAINED_NPL__ELEMENT, oldElement, element));
        }
      }
    }
    return element;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public ContainedElementNoOpposite basicGetElement()
  {
    return element;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setElement(ContainedElementNoOpposite newElement)
  {
    ContainedElementNoOpposite oldElement = element;
    element = newElement;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, model4Package.REF_SINGLE_NON_CONTAINED_NPL__ELEMENT, oldElement, element));
    }
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
    case model4Package.REF_SINGLE_NON_CONTAINED_NPL__ELEMENT:
      if (resolve)
      {
        return getElement();
      }
      return basicGetElement();
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
    case model4Package.REF_SINGLE_NON_CONTAINED_NPL__ELEMENT:
      setElement((ContainedElementNoOpposite)newValue);
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
    case model4Package.REF_SINGLE_NON_CONTAINED_NPL__ELEMENT:
      setElement((ContainedElementNoOpposite)null);
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
    case model4Package.REF_SINGLE_NON_CONTAINED_NPL__ELEMENT:
      return element != null;
    }
    return super.eIsSet(featureID);
  }

} // RefSingleNonContainedNPLImpl
