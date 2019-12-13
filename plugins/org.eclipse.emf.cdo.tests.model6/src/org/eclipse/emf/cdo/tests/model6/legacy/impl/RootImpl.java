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

import org.eclipse.emf.cdo.tests.model6.BaseObject;
import org.eclipse.emf.cdo.tests.model6.Root;
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
 * <!-- begin-user-doc --> An implementation of the model object ' <em><b>Root</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.RootImpl#getListA <em>List A</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.RootImpl#getListB <em>List B</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.RootImpl#getListC <em>List C</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.RootImpl#getListD <em>List D</em>}</li>
 * </ul>
 *
 * @generated
 */
public class RootImpl extends EObjectImpl implements Root
{
  /**
   * The cached value of the '{@link #getListA() <em>List A</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getListA()
   * @generated
   * @ordered
   */
  protected EList<BaseObject> listA;

  /**
   * The cached value of the '{@link #getListB() <em>List B</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getListB()
   * @generated
   * @ordered
   */
  protected EList<BaseObject> listB;

  /**
   * The cached value of the '{@link #getListC() <em>List C</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getListC()
   * @generated
   * @ordered
   */
  protected EList<BaseObject> listC;

  /**
   * The cached value of the '{@link #getListD() <em>List D</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getListD()
   * @generated
   * @ordered
   */
  protected EList<BaseObject> listD;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected RootImpl()
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
    return Model6Package.eINSTANCE.getRoot();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<BaseObject> getListA()
  {
    if (listA == null)
    {
      listA = new EObjectContainmentEList.Resolving<BaseObject>(BaseObject.class, this, Model6Package.ROOT__LIST_A);
    }
    return listA;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<BaseObject> getListB()
  {
    if (listB == null)
    {
      listB = new EObjectContainmentEList.Resolving<BaseObject>(BaseObject.class, this, Model6Package.ROOT__LIST_B);
    }
    return listB;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<BaseObject> getListC()
  {
    if (listC == null)
    {
      listC = new EObjectContainmentEList.Resolving<BaseObject>(BaseObject.class, this, Model6Package.ROOT__LIST_C);
    }
    return listC;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<BaseObject> getListD()
  {
    if (listD == null)
    {
      listD = new EObjectContainmentEList.Resolving<BaseObject>(BaseObject.class, this, Model6Package.ROOT__LIST_D);
    }
    return listD;
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
    case Model6Package.ROOT__LIST_A:
      return ((InternalEList<?>)getListA()).basicRemove(otherEnd, msgs);
    case Model6Package.ROOT__LIST_B:
      return ((InternalEList<?>)getListB()).basicRemove(otherEnd, msgs);
    case Model6Package.ROOT__LIST_C:
      return ((InternalEList<?>)getListC()).basicRemove(otherEnd, msgs);
    case Model6Package.ROOT__LIST_D:
      return ((InternalEList<?>)getListD()).basicRemove(otherEnd, msgs);
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
    case Model6Package.ROOT__LIST_A:
      return getListA();
    case Model6Package.ROOT__LIST_B:
      return getListB();
    case Model6Package.ROOT__LIST_C:
      return getListC();
    case Model6Package.ROOT__LIST_D:
      return getListD();
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
    case Model6Package.ROOT__LIST_A:
      getListA().clear();
      getListA().addAll((Collection<? extends BaseObject>)newValue);
      return;
    case Model6Package.ROOT__LIST_B:
      getListB().clear();
      getListB().addAll((Collection<? extends BaseObject>)newValue);
      return;
    case Model6Package.ROOT__LIST_C:
      getListC().clear();
      getListC().addAll((Collection<? extends BaseObject>)newValue);
      return;
    case Model6Package.ROOT__LIST_D:
      getListD().clear();
      getListD().addAll((Collection<? extends BaseObject>)newValue);
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
    case Model6Package.ROOT__LIST_A:
      getListA().clear();
      return;
    case Model6Package.ROOT__LIST_B:
      getListB().clear();
      return;
    case Model6Package.ROOT__LIST_C:
      getListC().clear();
      return;
    case Model6Package.ROOT__LIST_D:
      getListD().clear();
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
    case Model6Package.ROOT__LIST_A:
      return listA != null && !listA.isEmpty();
    case Model6Package.ROOT__LIST_B:
      return listB != null && !listB.isEmpty();
    case Model6Package.ROOT__LIST_C:
      return listC != null && !listC.isEmpty();
    case Model6Package.ROOT__LIST_D:
      return listD != null && !listD.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} // RootImpl
