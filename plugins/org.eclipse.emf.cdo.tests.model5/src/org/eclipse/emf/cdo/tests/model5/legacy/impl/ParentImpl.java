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
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;

import java.util.Collection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Parent</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model5.legacy.impl.ParentImpl#getChildren <em>Children</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model5.legacy.impl.ParentImpl#getFavourite <em>Favourite</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model5.legacy.impl.ParentImpl#getName <em>Name</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ParentImpl extends EObjectImpl implements Parent
{
  /**
   * The cached value of the '{@link #getChildren() <em>Children</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getChildren()
   * @generated
   * @ordered
   */
  protected EList<Child> children;

  /**
   * The cached value of the '{@link #getFavourite() <em>Favourite</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getFavourite()
   * @generated
   * @ordered
   */
  protected Child favourite;

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
  protected ParentImpl()
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
    return Model5Package.eINSTANCE.getParent();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<Child> getChildren()
  {
    if (children == null)
    {
      children = new EObjectContainmentWithInverseEList<>(Child.class, this, Model5Package.PARENT__CHILDREN, Model5Package.CHILD__PARENT);
    }
    return children;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Child getFavourite()
  {
    if (favourite != null && favourite.eIsProxy())
    {
      InternalEObject oldFavourite = (InternalEObject)favourite;
      favourite = (Child)eResolveProxy(oldFavourite);
      if (favourite != oldFavourite)
      {
        if (eNotificationRequired())
        {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, Model5Package.PARENT__FAVOURITE, oldFavourite, favourite));
        }
      }
    }
    return favourite;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Child basicGetFavourite()
  {
    return favourite;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetFavourite(Child newFavourite, NotificationChain msgs)
  {
    Child oldFavourite = favourite;
    favourite = newFavourite;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, Model5Package.PARENT__FAVOURITE, oldFavourite, newFavourite);
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
  public void setFavouriteGen(Child newFavourite)
  {
    if (newFavourite != favourite)
    {
      NotificationChain msgs = null;
      if (favourite != null)
      {
        msgs = ((InternalEObject)favourite).eInverseRemove(this, Model5Package.CHILD__PREFERRED_BY, Child.class, msgs);
      }
      if (newFavourite != null)
      {
        msgs = ((InternalEObject)newFavourite).eInverseAdd(this, Model5Package.CHILD__PREFERRED_BY, Child.class, msgs);
      }
      msgs = basicSetFavourite(newFavourite, msgs);
      if (msgs != null)
      {
        msgs.dispatch();
      }
    }
    else if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model5Package.PARENT__FAVOURITE, newFavourite, newFavourite));
    }
  }

  @Override
  public void setFavourite(Child newFavourite)
  {
    IsLoadingTestFixture.reportLoading(eResource(), this);
    setFavouriteGen(newFavourite);
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
      eNotify(new ENotificationImpl(this, Notification.SET, Model5Package.PARENT__NAME, oldName, name));
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
  @SuppressWarnings("unchecked")
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case Model5Package.PARENT__CHILDREN:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getChildren()).basicAdd(otherEnd, msgs);
    case Model5Package.PARENT__FAVOURITE:
      if (favourite != null)
      {
        msgs = ((InternalEObject)favourite).eInverseRemove(this, Model5Package.CHILD__PREFERRED_BY, Child.class, msgs);
      }
      return basicSetFavourite((Child)otherEnd, msgs);
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
    case Model5Package.PARENT__CHILDREN:
      return ((InternalEList<?>)getChildren()).basicRemove(otherEnd, msgs);
    case Model5Package.PARENT__FAVOURITE:
      return basicSetFavourite(null, msgs);
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
    case Model5Package.PARENT__CHILDREN:
      return getChildren();
    case Model5Package.PARENT__FAVOURITE:
      if (resolve)
      {
        return getFavourite();
      }
      return basicGetFavourite();
    case Model5Package.PARENT__NAME:
      return getName();
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
    case Model5Package.PARENT__CHILDREN:
      getChildren().clear();
      getChildren().addAll((Collection<? extends Child>)newValue);
      return;
    case Model5Package.PARENT__FAVOURITE:
      setFavourite((Child)newValue);
      return;
    case Model5Package.PARENT__NAME:
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
    case Model5Package.PARENT__CHILDREN:
      getChildren().clear();
      return;
    case Model5Package.PARENT__FAVOURITE:
      setFavourite((Child)null);
      return;
    case Model5Package.PARENT__NAME:
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
    case Model5Package.PARENT__CHILDREN:
      return children != null && !children.isEmpty();
    case Model5Package.PARENT__FAVOURITE:
      return favourite != null;
    case Model5Package.PARENT__NAME:
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

} // ParentImpl
