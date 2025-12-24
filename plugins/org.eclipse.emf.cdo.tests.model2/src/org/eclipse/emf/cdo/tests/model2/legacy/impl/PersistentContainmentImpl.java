/*
 * Copyright (c) 2013, 2015, 2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model2.legacy.impl;

import org.eclipse.emf.cdo.tests.model2.PersistentContainment;
import org.eclipse.emf.cdo.tests.model2.TransientContainer;
import org.eclipse.emf.cdo.tests.model2.legacy.Model2Package;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;

import java.util.Collection;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Persistent Containment</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.legacy.impl.PersistentContainmentImpl#getAttrBefore <em>Attr Before</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.legacy.impl.PersistentContainmentImpl#getChildren <em>Children</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.legacy.impl.PersistentContainmentImpl#getAttrAfter <em>Attr After</em>}</li>
 * </ul>
 *
 * @generated
 */
public class PersistentContainmentImpl extends EObjectImpl implements PersistentContainment
{
  /**
   * The default value of the '{@link #getAttrBefore() <em>Attr Before</em>}' attribute.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #getAttrBefore()
   * @generated
   * @ordered
   */
  protected static final String ATTR_BEFORE_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getAttrBefore() <em>Attr Before</em>}' attribute.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #getAttrBefore()
   * @generated
   * @ordered
   */
  protected String attrBefore = ATTR_BEFORE_EDEFAULT;

  /**
   * The cached value of the '{@link #getChildren() <em>Children</em>}' containment reference list.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @see #getChildren()
   * @generated
   * @ordered
   */
  protected EList<TransientContainer> children;

  /**
   * The default value of the '{@link #getAttrAfter() <em>Attr After</em>}' attribute.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #getAttrAfter()
   * @generated
   * @ordered
   */
  protected static final String ATTR_AFTER_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getAttrAfter() <em>Attr After</em>}' attribute.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #getAttrAfter()
   * @generated
   * @ordered
   */
  protected String attrAfter = ATTR_AFTER_EDEFAULT;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected PersistentContainmentImpl()
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
    return Model2Package.eINSTANCE.getPersistentContainment();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getAttrBefore()
  {
    return attrBefore;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrBefore(String newAttrBefore)
  {
    String oldAttrBefore = attrBefore;
    attrBefore = newAttrBefore;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model2Package.PERSISTENT_CONTAINMENT__ATTR_BEFORE, oldAttrBefore, attrBefore));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<TransientContainer> getChildren()
  {
    if (children == null)
    {
      children = new EObjectContainmentWithInverseEList<>(TransientContainer.class, this, Model2Package.PERSISTENT_CONTAINMENT__CHILDREN,
          Model2Package.TRANSIENT_CONTAINER__PARENT);
    }
    return children;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getAttrAfter()
  {
    return attrAfter;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrAfter(String newAttrAfter)
  {
    String oldAttrAfter = attrAfter;
    attrAfter = newAttrAfter;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model2Package.PERSISTENT_CONTAINMENT__ATTR_AFTER, oldAttrAfter, attrAfter));
    }
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
    case Model2Package.PERSISTENT_CONTAINMENT__CHILDREN:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getChildren()).basicAdd(otherEnd, msgs);
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
    case Model2Package.PERSISTENT_CONTAINMENT__CHILDREN:
      return ((InternalEList<?>)getChildren()).basicRemove(otherEnd, msgs);
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
    case Model2Package.PERSISTENT_CONTAINMENT__ATTR_BEFORE:
      return getAttrBefore();
    case Model2Package.PERSISTENT_CONTAINMENT__CHILDREN:
      return getChildren();
    case Model2Package.PERSISTENT_CONTAINMENT__ATTR_AFTER:
      return getAttrAfter();
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
    case Model2Package.PERSISTENT_CONTAINMENT__ATTR_BEFORE:
      setAttrBefore((String)newValue);
      return;
    case Model2Package.PERSISTENT_CONTAINMENT__CHILDREN:
      getChildren().clear();
      getChildren().addAll((Collection<? extends TransientContainer>)newValue);
      return;
    case Model2Package.PERSISTENT_CONTAINMENT__ATTR_AFTER:
      setAttrAfter((String)newValue);
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
    case Model2Package.PERSISTENT_CONTAINMENT__ATTR_BEFORE:
      setAttrBefore(ATTR_BEFORE_EDEFAULT);
      return;
    case Model2Package.PERSISTENT_CONTAINMENT__CHILDREN:
      getChildren().clear();
      return;
    case Model2Package.PERSISTENT_CONTAINMENT__ATTR_AFTER:
      setAttrAfter(ATTR_AFTER_EDEFAULT);
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
    case Model2Package.PERSISTENT_CONTAINMENT__ATTR_BEFORE:
      return ATTR_BEFORE_EDEFAULT == null ? attrBefore != null : !ATTR_BEFORE_EDEFAULT.equals(attrBefore);
    case Model2Package.PERSISTENT_CONTAINMENT__CHILDREN:
      return children != null && !children.isEmpty();
    case Model2Package.PERSISTENT_CONTAINMENT__ATTR_AFTER:
      return ATTR_AFTER_EDEFAULT == null ? attrAfter != null : !ATTR_AFTER_EDEFAULT.equals(attrAfter);
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy())
    {
      return super.toString();
    }

    StringBuilder result = new StringBuilder(super.toString());
    result.append(" (attrBefore: ");
    result.append(attrBefore);
    result.append(", attrAfter: ");
    result.append(attrAfter);
    result.append(')');
    return result.toString();
  }

} // PersistentContainmentImpl
