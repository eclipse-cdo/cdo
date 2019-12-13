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

import org.eclipse.emf.cdo.tests.model4.GenRefMapNonContained;
import org.eclipse.emf.cdo.tests.model4.legacy.model4Package;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Gen Ref Map Non Contained</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model4.legacy.impl.GenRefMapNonContainedImpl#getElements <em>Elements</em>}</li>
 * </ul>
 *
 * @generated
 */
public class GenRefMapNonContainedImpl extends EObjectImpl implements GenRefMapNonContained
{
  /**
   * The cached value of the '{@link #getElements() <em>Elements</em>}' map. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @see #getElements()
   * @generated
   * @ordered
   */
  protected EMap<String, EObject> elements;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected GenRefMapNonContainedImpl()
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
    return model4Package.eINSTANCE.getGenRefMapNonContained();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EMap<String, EObject> getElements()
  {
    if (elements == null)
    {
      elements = new EcoreEMap<String, EObject>(model4Package.eINSTANCE.getStringToEObject(), StringToEObjectImpl.class, this,
          model4Package.GEN_REF_MAP_NON_CONTAINED__ELEMENTS);
    }
    return elements;
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
    case model4Package.GEN_REF_MAP_NON_CONTAINED__ELEMENTS:
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
    case model4Package.GEN_REF_MAP_NON_CONTAINED__ELEMENTS:
      if (coreType)
      {
        return getElements();
      }
      else
      {
        return getElements().map();
      }
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
    case model4Package.GEN_REF_MAP_NON_CONTAINED__ELEMENTS:
      ((EStructuralFeature.Setting)getElements()).set(newValue);
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
    case model4Package.GEN_REF_MAP_NON_CONTAINED__ELEMENTS:
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
    case model4Package.GEN_REF_MAP_NON_CONTAINED__ELEMENTS:
      return elements != null && !elements.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} // GenRefMapNonContainedImpl
