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

import org.eclipse.emf.cdo.tests.model4.ImplMultiRefNonContainer;
import org.eclipse.emf.cdo.tests.model4.legacy.model4Package;
import org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefNonContainedElement;
import org.eclipse.emf.cdo.tests.model4interfaces.legacy.model4interfacesPackage;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

import java.util.Collection;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Impl Multi Ref Non Container</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model4.legacy.impl.ImplMultiRefNonContainerImpl#getElements <em>Elements</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ImplMultiRefNonContainerImpl extends EObjectImpl implements ImplMultiRefNonContainer
{
  /**
   * The cached value of the '{@link #getElements() <em>Elements</em>}' reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #getElements()
   * @generated
   * @ordered
   */
  protected EList<IMultiRefNonContainedElement> elements;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected ImplMultiRefNonContainerImpl()
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
    return model4Package.eINSTANCE.getImplMultiRefNonContainer();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<IMultiRefNonContainedElement> getElements()
  {
    if (elements == null)
    {
      elements = new EObjectWithInverseResolvingEList<IMultiRefNonContainedElement>(IMultiRefNonContainedElement.class, this,
          model4Package.IMPL_MULTI_REF_NON_CONTAINER__ELEMENTS, model4interfacesPackage.IMULTI_REF_NON_CONTAINED_ELEMENT__PARENT);
    }
    return elements;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case model4Package.IMPL_MULTI_REF_NON_CONTAINER__ELEMENTS:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getElements()).basicAdd(otherEnd, msgs);
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
    case model4Package.IMPL_MULTI_REF_NON_CONTAINER__ELEMENTS:
      return ((InternalEList<?>)getElements()).basicRemove(otherEnd, msgs);
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
    case model4Package.IMPL_MULTI_REF_NON_CONTAINER__ELEMENTS:
      return getElements();
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
    case model4Package.IMPL_MULTI_REF_NON_CONTAINER__ELEMENTS:
      getElements().clear();
      getElements().addAll((Collection<? extends IMultiRefNonContainedElement>)newValue);
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
    case model4Package.IMPL_MULTI_REF_NON_CONTAINER__ELEMENTS:
      getElements().clear();
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
    case model4Package.IMPL_MULTI_REF_NON_CONTAINER__ELEMENTS:
      return elements != null && !elements.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} // ImplMultiRefNonContainerImpl
