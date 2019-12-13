/*
 * Copyright (c) 2013, 2015, 2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Transient Container</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.legacy.impl.TransientContainerImpl#getAttrBefore <em>Attr Before</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.legacy.impl.TransientContainerImpl#getParent <em>Parent</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.legacy.impl.TransientContainerImpl#getAttrAfter <em>Attr After</em>}</li>
 * </ul>
 *
 * @generated
 */
public class TransientContainerImpl extends EObjectImpl implements TransientContainer
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
  protected TransientContainerImpl()
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
    return Model2Package.eINSTANCE.getTransientContainer();
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
      eNotify(new ENotificationImpl(this, Notification.SET, Model2Package.TRANSIENT_CONTAINER__ATTR_BEFORE, oldAttrBefore, attrBefore));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public PersistentContainment getParent()
  {
    if (eContainerFeatureID() != Model2Package.TRANSIENT_CONTAINER__PARENT)
    {
      return null;
    }
    return (PersistentContainment)eInternalContainer();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetParent(PersistentContainment newParent, NotificationChain msgs)
  {
    msgs = eBasicSetContainer((InternalEObject)newParent, Model2Package.TRANSIENT_CONTAINER__PARENT, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setParent(PersistentContainment newParent)
  {
    if (newParent != eInternalContainer() || eContainerFeatureID() != Model2Package.TRANSIENT_CONTAINER__PARENT && newParent != null)
    {
      if (EcoreUtil.isAncestor(this, newParent))
      {
        throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
      }
      NotificationChain msgs = null;
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      if (newParent != null)
      {
        msgs = ((InternalEObject)newParent).eInverseAdd(this, Model2Package.PERSISTENT_CONTAINMENT__CHILDREN, PersistentContainment.class, msgs);
      }
      msgs = basicSetParent(newParent, msgs);
      if (msgs != null)
      {
        msgs.dispatch();
      }
    }
    else if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model2Package.TRANSIENT_CONTAINER__PARENT, newParent, newParent));
    }
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
      eNotify(new ENotificationImpl(this, Notification.SET, Model2Package.TRANSIENT_CONTAINER__ATTR_AFTER, oldAttrAfter, attrAfter));
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
    case Model2Package.TRANSIENT_CONTAINER__PARENT:
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      return basicSetParent((PersistentContainment)otherEnd, msgs);
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
    case Model2Package.TRANSIENT_CONTAINER__PARENT:
      return basicSetParent(null, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs)
  {
    switch (eContainerFeatureID())
    {
    case Model2Package.TRANSIENT_CONTAINER__PARENT:
      return eInternalContainer().eInverseRemove(this, Model2Package.PERSISTENT_CONTAINMENT__CHILDREN, PersistentContainment.class, msgs);
    }
    return super.eBasicRemoveFromContainerFeature(msgs);
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
    case Model2Package.TRANSIENT_CONTAINER__ATTR_BEFORE:
      return getAttrBefore();
    case Model2Package.TRANSIENT_CONTAINER__PARENT:
      return getParent();
    case Model2Package.TRANSIENT_CONTAINER__ATTR_AFTER:
      return getAttrAfter();
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
    case Model2Package.TRANSIENT_CONTAINER__ATTR_BEFORE:
      setAttrBefore((String)newValue);
      return;
    case Model2Package.TRANSIENT_CONTAINER__PARENT:
      setParent((PersistentContainment)newValue);
      return;
    case Model2Package.TRANSIENT_CONTAINER__ATTR_AFTER:
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
    case Model2Package.TRANSIENT_CONTAINER__ATTR_BEFORE:
      setAttrBefore(ATTR_BEFORE_EDEFAULT);
      return;
    case Model2Package.TRANSIENT_CONTAINER__PARENT:
      setParent((PersistentContainment)null);
      return;
    case Model2Package.TRANSIENT_CONTAINER__ATTR_AFTER:
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
    case Model2Package.TRANSIENT_CONTAINER__ATTR_BEFORE:
      return ATTR_BEFORE_EDEFAULT == null ? attrBefore != null : !ATTR_BEFORE_EDEFAULT.equals(attrBefore);
    case Model2Package.TRANSIENT_CONTAINER__PARENT:
      return getParent() != null;
    case Model2Package.TRANSIENT_CONTAINER__ATTR_AFTER:
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

} // TransientContainerImpl
