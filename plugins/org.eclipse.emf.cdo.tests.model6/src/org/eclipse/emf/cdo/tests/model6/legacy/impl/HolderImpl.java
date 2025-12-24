/*
 * Copyright (c) 2013, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model6.legacy.impl;

import org.eclipse.emf.cdo.tests.model6.Holdable;
import org.eclipse.emf.cdo.tests.model6.Holder;
import org.eclipse.emf.cdo.tests.model6.legacy.Model6Package;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

import java.util.Collection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Holder</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.HolderImpl#getHeld <em>Held</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.HolderImpl#getOwned <em>Owned</em>}</li>
 * </ul>
 *
 * @generated
 */
public class HolderImpl extends HoldableImpl implements Holder
{
  /**
   * The cached value of the '{@link #getHeld() <em>Held</em>}' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getHeld()
   * @generated
   * @ordered
   */
  protected EList<Holdable> held;

  /**
   * The cached value of the '{@link #getOwned() <em>Owned</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getOwned()
   * @generated
   * @ordered
   */
  protected EList<Holdable> owned;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected HolderImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return Model6Package.eINSTANCE.getHolder();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<Holdable> getHeld()
  {
    if (held == null)
    {
      held = new EObjectResolvingEList<>(Holdable.class, this, Model6Package.HOLDER__HELD);
    }
    return held;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<Holdable> getOwned()
  {
    if (owned == null)
    {
      owned = new EObjectContainmentEList.Resolving<>(Holdable.class, this, Model6Package.HOLDER__OWNED);
    }
    return owned;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case Model6Package.HOLDER__OWNED:
      return ((InternalEList<?>)getOwned()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case Model6Package.HOLDER__HELD:
      return getHeld();
    case Model6Package.HOLDER__OWNED:
      return getOwned();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case Model6Package.HOLDER__HELD:
      getHeld().clear();
      getHeld().addAll((Collection<? extends Holdable>)newValue);
      return;
    case Model6Package.HOLDER__OWNED:
      getOwned().clear();
      getOwned().addAll((Collection<? extends Holdable>)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case Model6Package.HOLDER__HELD:
      getHeld().clear();
      return;
    case Model6Package.HOLDER__OWNED:
      getOwned().clear();
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case Model6Package.HOLDER__HELD:
      return held != null && !held.isEmpty();
    case Model6Package.HOLDER__OWNED:
      return owned != null && !owned.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} // HolderImpl
