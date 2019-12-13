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

import org.eclipse.emf.cdo.tests.model6.UnorderedList;
import org.eclipse.emf.cdo.tests.model6.legacy.Model6Package;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

import java.util.Collection;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Unordered List</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.UnorderedListImpl#getContained <em>Contained</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.UnorderedListImpl#getReferenced <em>Referenced</em>}</li>
 * </ul>
 *
 * @generated
 */
public class UnorderedListImpl extends EObjectImpl implements UnorderedList
{
  /**
   * The cached value of the '{@link #getContained() <em>Contained</em>}' containment reference list. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getContained()
   * @generated
   * @ordered
   */
  protected EList<UnorderedList> contained;

  /**
   * The cached value of the '{@link #getReferenced() <em>Referenced</em>}' reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #getReferenced()
   * @generated
   * @ordered
   */
  protected EList<UnorderedList> referenced;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected UnorderedListImpl()
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
    return Model6Package.eINSTANCE.getUnorderedList();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<UnorderedList> getContained()
  {
    if (contained == null)
    {
      contained = new EObjectContainmentEList.Resolving<UnorderedList>(UnorderedList.class, this, Model6Package.UNORDERED_LIST__CONTAINED);
    }
    return contained;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<UnorderedList> getReferenced()
  {
    if (referenced == null)
    {
      referenced = new EObjectResolvingEList<UnorderedList>(UnorderedList.class, this, Model6Package.UNORDERED_LIST__REFERENCED);
    }
    return referenced;
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
    case Model6Package.UNORDERED_LIST__CONTAINED:
      return ((InternalEList<?>)getContained()).basicRemove(otherEnd, msgs);
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
    case Model6Package.UNORDERED_LIST__CONTAINED:
      return getContained();
    case Model6Package.UNORDERED_LIST__REFERENCED:
      return getReferenced();
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
    case Model6Package.UNORDERED_LIST__CONTAINED:
      getContained().clear();
      getContained().addAll((Collection<? extends UnorderedList>)newValue);
      return;
    case Model6Package.UNORDERED_LIST__REFERENCED:
      getReferenced().clear();
      getReferenced().addAll((Collection<? extends UnorderedList>)newValue);
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
    case Model6Package.UNORDERED_LIST__CONTAINED:
      getContained().clear();
      return;
    case Model6Package.UNORDERED_LIST__REFERENCED:
      getReferenced().clear();
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
    case Model6Package.UNORDERED_LIST__CONTAINED:
      return contained != null && !contained.isEmpty();
    case Model6Package.UNORDERED_LIST__REFERENCED:
      return referenced != null && !referenced.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} // UnorderedListImpl
