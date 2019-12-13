/*
 * Copyright (c) 2013, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model6.legacy.impl;

import org.eclipse.emf.cdo.tests.model6.A;
import org.eclipse.emf.cdo.tests.model6.B;
import org.eclipse.emf.cdo.tests.model6.D;
import org.eclipse.emf.cdo.tests.model6.legacy.Model6Package;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import java.util.Collection;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>A</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.AImpl#getOwnedDs <em>Owned Ds</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.AImpl#getOwnedBs <em>Owned Bs</em>}</li>
 * </ul>
 *
 * @generated
 */
public class AImpl extends EObjectImpl implements A
{
  /**
   * The cached value of the '{@link #getOwnedDs() <em>Owned Ds</em>}' containment reference list.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @see #getOwnedDs()
   * @generated
   * @ordered
   */
  protected EList<D> ownedDs;

  /**
   * The cached value of the '{@link #getOwnedBs() <em>Owned Bs</em>}' containment reference list.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @see #getOwnedBs()
   * @generated
   * @ordered
   */
  protected EList<B> ownedBs;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected AImpl()
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
    return Model6Package.eINSTANCE.getA();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<D> getOwnedDs()
  {
    if (ownedDs == null)
    {
      ownedDs = new EObjectContainmentEList.Resolving<D>(D.class, this, Model6Package.A__OWNED_DS);
    }
    return ownedDs;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<B> getOwnedBs()
  {
    if (ownedBs == null)
    {
      ownedBs = new EObjectContainmentEList.Resolving<B>(B.class, this, Model6Package.A__OWNED_BS);
    }
    return ownedBs;
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
    case Model6Package.A__OWNED_DS:
      return ((InternalEList<?>)getOwnedDs()).basicRemove(otherEnd, msgs);
    case Model6Package.A__OWNED_BS:
      return ((InternalEList<?>)getOwnedBs()).basicRemove(otherEnd, msgs);
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
    case Model6Package.A__OWNED_DS:
      return getOwnedDs();
    case Model6Package.A__OWNED_BS:
      return getOwnedBs();
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
    case Model6Package.A__OWNED_DS:
      getOwnedDs().clear();
      getOwnedDs().addAll((Collection<? extends D>)newValue);
      return;
    case Model6Package.A__OWNED_BS:
      getOwnedBs().clear();
      getOwnedBs().addAll((Collection<? extends B>)newValue);
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
    case Model6Package.A__OWNED_DS:
      getOwnedDs().clear();
      return;
    case Model6Package.A__OWNED_BS:
      getOwnedBs().clear();
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
    case Model6Package.A__OWNED_DS:
      return ownedDs != null && !ownedDs.isEmpty();
    case Model6Package.A__OWNED_BS:
      return ownedBs != null && !ownedBs.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} // AImpl
