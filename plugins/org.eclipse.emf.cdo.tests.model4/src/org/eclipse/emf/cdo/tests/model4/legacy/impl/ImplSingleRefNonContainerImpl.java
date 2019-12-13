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

import org.eclipse.emf.cdo.tests.model4.ImplSingleRefNonContainer;
import org.eclipse.emf.cdo.tests.model4.legacy.model4Package;
import org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefNonContainedElement;
import org.eclipse.emf.cdo.tests.model4interfaces.legacy.model4interfacesPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Impl Single Ref Non Container</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model4.legacy.impl.ImplSingleRefNonContainerImpl#getElement <em>Element</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ImplSingleRefNonContainerImpl extends EObjectImpl implements ImplSingleRefNonContainer
{
  /**
   * The cached value of the '{@link #getElement() <em>Element</em>}' reference.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #getElement()
   * @generated
   * @ordered
   */
  protected ISingleRefNonContainedElement element;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected ImplSingleRefNonContainerImpl()
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
    return model4Package.eINSTANCE.getImplSingleRefNonContainer();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ISingleRefNonContainedElement getElement()
  {
    if (element != null && element.eIsProxy())
    {
      InternalEObject oldElement = (InternalEObject)element;
      element = (ISingleRefNonContainedElement)eResolveProxy(oldElement);
      if (element != oldElement)
      {
        if (eNotificationRequired())
        {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, model4Package.IMPL_SINGLE_REF_NON_CONTAINER__ELEMENT, oldElement, element));
        }
      }
    }
    return element;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public ISingleRefNonContainedElement basicGetElement()
  {
    return element;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetElement(ISingleRefNonContainedElement newElement, NotificationChain msgs)
  {
    ISingleRefNonContainedElement oldElement = element;
    element = newElement;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, model4Package.IMPL_SINGLE_REF_NON_CONTAINER__ELEMENT, oldElement,
          newElement);
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
  public void setElement(ISingleRefNonContainedElement newElement)
  {
    if (newElement != element)
    {
      NotificationChain msgs = null;
      if (element != null)
      {
        msgs = ((InternalEObject)element).eInverseRemove(this, model4interfacesPackage.ISINGLE_REF_NON_CONTAINED_ELEMENT__PARENT,
            ISingleRefNonContainedElement.class, msgs);
      }
      if (newElement != null)
      {
        msgs = ((InternalEObject)newElement).eInverseAdd(this, model4interfacesPackage.ISINGLE_REF_NON_CONTAINED_ELEMENT__PARENT,
            ISingleRefNonContainedElement.class, msgs);
      }
      msgs = basicSetElement(newElement, msgs);
      if (msgs != null)
      {
        msgs.dispatch();
      }
    }
    else if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, model4Package.IMPL_SINGLE_REF_NON_CONTAINER__ELEMENT, newElement, newElement));
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
    case model4Package.IMPL_SINGLE_REF_NON_CONTAINER__ELEMENT:
      if (element != null)
      {
        msgs = ((InternalEObject)element).eInverseRemove(this, model4interfacesPackage.ISINGLE_REF_NON_CONTAINED_ELEMENT__PARENT,
            ISingleRefNonContainedElement.class, msgs);
      }
      return basicSetElement((ISingleRefNonContainedElement)otherEnd, msgs);
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
    case model4Package.IMPL_SINGLE_REF_NON_CONTAINER__ELEMENT:
      return basicSetElement(null, msgs);
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
    case model4Package.IMPL_SINGLE_REF_NON_CONTAINER__ELEMENT:
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
    case model4Package.IMPL_SINGLE_REF_NON_CONTAINER__ELEMENT:
      setElement((ISingleRefNonContainedElement)newValue);
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
    case model4Package.IMPL_SINGLE_REF_NON_CONTAINER__ELEMENT:
      setElement((ISingleRefNonContainedElement)null);
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
    case model4Package.IMPL_SINGLE_REF_NON_CONTAINER__ELEMENT:
      return element != null;
    }
    return super.eIsSet(featureID);
  }

} // ImplSingleRefNonContainerImpl
