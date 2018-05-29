/*
 * Copyright (c) 2012, 2013, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl;

import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Group;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Person;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz380987_Place;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

import java.util.Collection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Bz380987 Person</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz380987_PersonImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz380987_PersonImpl#getGroup <em>Group</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz380987_PersonImpl#getPlaces <em>Places</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class Bz380987_PersonImpl extends EObjectImpl implements Bz380987_Person
{
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
   * The cached value of the '{@link #getGroup() <em>Group</em>}' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getGroup()
   * @generated
   * @ordered
   */
  protected EList<Bz380987_Group> group;

  /**
   * The cached value of the '{@link #getPlaces() <em>Places</em>}' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getPlaces()
   * @generated
   * @ordered
   */
  protected EList<Bz380987_Place> places;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected Bz380987_PersonImpl()
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
    return HibernateTestPackage.Literals.BZ380987_PERSON;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getName()
  {
    return name;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setName(String newName)
  {
    String oldName = name;
    name = newName;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, HibernateTestPackage.BZ380987_PERSON__NAME, oldName, name));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Bz380987_Group> getGroup()
  {
    if (group == null)
    {
      group = new EObjectWithInverseResolvingEList.ManyInverse<Bz380987_Group>(Bz380987_Group.class, this, HibernateTestPackage.BZ380987_PERSON__GROUP,
          HibernateTestPackage.BZ380987_GROUP__PEOPLE);
    }
    return group;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Bz380987_Place> getPlaces()
  {
    if (places == null)
    {
      places = new EObjectWithInverseResolvingEList.ManyInverse<Bz380987_Place>(Bz380987_Place.class, this, HibernateTestPackage.BZ380987_PERSON__PLACES,
          HibernateTestPackage.BZ380987_PLACE__PEOPLE);
    }
    return places;
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
    case HibernateTestPackage.BZ380987_PERSON__GROUP:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getGroup()).basicAdd(otherEnd, msgs);
    case HibernateTestPackage.BZ380987_PERSON__PLACES:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getPlaces()).basicAdd(otherEnd, msgs);
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
    case HibernateTestPackage.BZ380987_PERSON__GROUP:
      return ((InternalEList<?>)getGroup()).basicRemove(otherEnd, msgs);
    case HibernateTestPackage.BZ380987_PERSON__PLACES:
      return ((InternalEList<?>)getPlaces()).basicRemove(otherEnd, msgs);
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
    case HibernateTestPackage.BZ380987_PERSON__NAME:
      return getName();
    case HibernateTestPackage.BZ380987_PERSON__GROUP:
      return getGroup();
    case HibernateTestPackage.BZ380987_PERSON__PLACES:
      return getPlaces();
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
    case HibernateTestPackage.BZ380987_PERSON__NAME:
      setName((String)newValue);
      return;
    case HibernateTestPackage.BZ380987_PERSON__GROUP:
      getGroup().clear();
      getGroup().addAll((Collection<? extends Bz380987_Group>)newValue);
      return;
    case HibernateTestPackage.BZ380987_PERSON__PLACES:
      getPlaces().clear();
      getPlaces().addAll((Collection<? extends Bz380987_Place>)newValue);
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
    case HibernateTestPackage.BZ380987_PERSON__NAME:
      setName(NAME_EDEFAULT);
      return;
    case HibernateTestPackage.BZ380987_PERSON__GROUP:
      getGroup().clear();
      return;
    case HibernateTestPackage.BZ380987_PERSON__PLACES:
      getPlaces().clear();
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
    case HibernateTestPackage.BZ380987_PERSON__NAME:
      return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
    case HibernateTestPackage.BZ380987_PERSON__GROUP:
      return group != null && !group.isEmpty();
    case HibernateTestPackage.BZ380987_PERSON__PLACES:
      return places != null && !places.isEmpty();
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

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (name: ");
    result.append(name);
    result.append(')');
    return result.toString();
  }

} // Bz380987_PersonImpl
