/*
 * Copyright (c) 2013, 2015, 2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christian W. Damus (CEA) - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model5.legacy.impl;

import org.eclipse.emf.cdo.tests.model5.Child;
import org.eclipse.emf.cdo.tests.model5.Parent;
import org.eclipse.emf.cdo.tests.model5.legacy.Model5Package;
import org.eclipse.emf.cdo.tests.model5.util.IsLoadingTestFixture;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Child</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model5.legacy.impl.ChildImpl#getParent <em>Parent</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model5.legacy.impl.ChildImpl#getPreferredBy <em>Preferred By</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model5.legacy.impl.ChildImpl#getName <em>Name</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ChildImpl extends EObjectImpl implements Child
{
  /**
   * The cached value of the '{@link #getPreferredBy() <em>Preferred By</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getPreferredBy()
   * @generated
   * @ordered
   */
  protected Parent preferredBy;

  /**
   * The default value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected static final String NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected String name = NAME_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ChildImpl()
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
    return Model5Package.eINSTANCE.getChild();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Parent getParent()
  {
    if (eContainerFeatureID() != Model5Package.CHILD__PARENT)
    {
      return null;
    }
    return (Parent)eInternalContainer();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetParent(Parent newParent, NotificationChain msgs)
  {
    msgs = eBasicSetContainer((InternalEObject)newParent, Model5Package.CHILD__PARENT, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setParent(Parent newParent)
  {
    if (newParent != eInternalContainer() || eContainerFeatureID() != Model5Package.CHILD__PARENT && newParent != null)
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
        msgs = ((InternalEObject)newParent).eInverseAdd(this, Model5Package.PARENT__CHILDREN, Parent.class, msgs);
      }
      msgs = basicSetParent(newParent, msgs);
      if (msgs != null)
      {
        msgs.dispatch();
      }
    }
    else if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model5Package.CHILD__PARENT, newParent, newParent));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Parent getPreferredBy()
  {
    if (preferredBy != null && preferredBy.eIsProxy())
    {
      InternalEObject oldPreferredBy = (InternalEObject)preferredBy;
      preferredBy = (Parent)eResolveProxy(oldPreferredBy);
      if (preferredBy != oldPreferredBy)
      {
        if (eNotificationRequired())
        {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, Model5Package.CHILD__PREFERRED_BY, oldPreferredBy, preferredBy));
        }
      }
    }
    return preferredBy;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Parent basicGetPreferredBy()
  {
    return preferredBy;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetPreferredBy(Parent newPreferredBy, NotificationChain msgs)
  {
    Parent oldPreferredBy = preferredBy;
    preferredBy = newPreferredBy;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, Model5Package.CHILD__PREFERRED_BY, oldPreferredBy, newPreferredBy);
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
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setPreferredByGen(Parent newPreferredBy)
  {
    if (newPreferredBy != preferredBy)
    {
      NotificationChain msgs = null;
      if (preferredBy != null)
      {
        msgs = ((InternalEObject)preferredBy).eInverseRemove(this, Model5Package.PARENT__FAVOURITE, Parent.class, msgs);
      }
      if (newPreferredBy != null)
      {
        msgs = ((InternalEObject)newPreferredBy).eInverseAdd(this, Model5Package.PARENT__FAVOURITE, Parent.class, msgs);
      }
      msgs = basicSetPreferredBy(newPreferredBy, msgs);
      if (msgs != null)
      {
        msgs.dispatch();
      }
    }
    else if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model5Package.CHILD__PREFERRED_BY, newPreferredBy, newPreferredBy));
    }
  }

  @Override
  public void setPreferredBy(Parent newPreferredBy)
  {
    IsLoadingTestFixture.reportLoading(eResource(), this);
    setPreferredByGen(newPreferredBy);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getName()
  {
    return name;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setNameGen(String newName)
  {
    String oldName = name;
    name = newName;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model5Package.CHILD__NAME, oldName, name));
    }
  }

  @Override
  public void setName(String newName)
  {
    IsLoadingTestFixture.reportLoading(eResource(), this);
    setNameGen(newName);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case Model5Package.CHILD__PARENT:
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      return basicSetParent((Parent)otherEnd, msgs);
    case Model5Package.CHILD__PREFERRED_BY:
      if (preferredBy != null)
      {
        msgs = ((InternalEObject)preferredBy).eInverseRemove(this, Model5Package.PARENT__FAVOURITE, Parent.class, msgs);
      }
      return basicSetPreferredBy((Parent)otherEnd, msgs);
    }
    return super.eInverseAdd(otherEnd, featureID, msgs);
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
    case Model5Package.CHILD__PARENT:
      return basicSetParent(null, msgs);
    case Model5Package.CHILD__PREFERRED_BY:
      return basicSetPreferredBy(null, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs)
  {
    switch (eContainerFeatureID())
    {
    case Model5Package.CHILD__PARENT:
      return eInternalContainer().eInverseRemove(this, Model5Package.PARENT__CHILDREN, Parent.class, msgs);
    }
    return super.eBasicRemoveFromContainerFeature(msgs);
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
    case Model5Package.CHILD__PARENT:
      return getParent();
    case Model5Package.CHILD__PREFERRED_BY:
      if (resolve)
      {
        return getPreferredBy();
      }
      return basicGetPreferredBy();
    case Model5Package.CHILD__NAME:
      return getName();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case Model5Package.CHILD__PARENT:
      setParent((Parent)newValue);
      return;
    case Model5Package.CHILD__PREFERRED_BY:
      setPreferredBy((Parent)newValue);
      return;
    case Model5Package.CHILD__NAME:
      setName((String)newValue);
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
    case Model5Package.CHILD__PARENT:
      setParent((Parent)null);
      return;
    case Model5Package.CHILD__PREFERRED_BY:
      setPreferredBy((Parent)null);
      return;
    case Model5Package.CHILD__NAME:
      setName(NAME_EDEFAULT);
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
    case Model5Package.CHILD__PARENT:
      return getParent() != null;
    case Model5Package.CHILD__PREFERRED_BY:
      return preferredBy != null;
    case Model5Package.CHILD__NAME:
      return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
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
    result.append(" (name: ");
    result.append(name);
    result.append(')');
    return result.toString();
  }

} // ChildImpl
