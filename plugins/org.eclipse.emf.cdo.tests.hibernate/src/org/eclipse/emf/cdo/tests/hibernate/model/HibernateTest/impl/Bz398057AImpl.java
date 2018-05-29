/*
 * Copyright (c) 2013, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl;

import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057A;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057B;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestPackage;

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
 * An implementation of the model object '<em><b>Bz398057 A</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz398057AImpl#getListOfB <em>List Of B</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz398057AImpl#getDbId <em>Db Id</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class Bz398057AImpl extends EObjectImpl implements Bz398057A
{
  /**
   * The cached value of the '{@link #getListOfB() <em>List Of B</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getListOfB()
   * @generated
   * @ordered
   */
  protected EList<Bz398057B> listOfB;

  /**
   * The default value of the '{@link #getDbId() <em>Db Id</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getDbId()
   * @generated
   * @ordered
   */
  protected static final String DB_ID_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getDbId() <em>Db Id</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getDbId()
   * @generated
   * @ordered
   */
  protected String dbId = DB_ID_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected Bz398057AImpl()
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
    return HibernateTestPackage.Literals.BZ398057_A;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Bz398057B> getListOfB()
  {
    if (listOfB == null)
    {
      listOfB = new EObjectContainmentWithInverseEList<Bz398057B>(Bz398057B.class, this, HibernateTestPackage.BZ398057_A__LIST_OF_B,
          HibernateTestPackage.BZ398057_B__REF_TO_CLASS_A);
    }
    return listOfB;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getDbId()
  {
    return dbId;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setDbId(String newDbId)
  {
    String oldDbId = dbId;
    dbId = newDbId;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, HibernateTestPackage.BZ398057_A__DB_ID, oldDbId, dbId));
    }
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
    case HibernateTestPackage.BZ398057_A__LIST_OF_B:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getListOfB()).basicAdd(otherEnd, msgs);
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
    case HibernateTestPackage.BZ398057_A__LIST_OF_B:
      return ((InternalEList<?>)getListOfB()).basicRemove(otherEnd, msgs);
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
    case HibernateTestPackage.BZ398057_A__LIST_OF_B:
      return getListOfB();
    case HibernateTestPackage.BZ398057_A__DB_ID:
      return getDbId();
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
    case HibernateTestPackage.BZ398057_A__LIST_OF_B:
      getListOfB().clear();
      getListOfB().addAll((Collection<? extends Bz398057B>)newValue);
      return;
    case HibernateTestPackage.BZ398057_A__DB_ID:
      setDbId((String)newValue);
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
    case HibernateTestPackage.BZ398057_A__LIST_OF_B:
      getListOfB().clear();
      return;
    case HibernateTestPackage.BZ398057_A__DB_ID:
      setDbId(DB_ID_EDEFAULT);
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
    case HibernateTestPackage.BZ398057_A__LIST_OF_B:
      return listOfB != null && !listOfB.isEmpty();
    case HibernateTestPackage.BZ398057_A__DB_ID:
      return DB_ID_EDEFAULT == null ? dbId != null : !DB_ID_EDEFAULT.equals(dbId);
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
    result.append(" (dbId: ");
    result.append(dbId);
    result.append(')');
    return result.toString();
  }

} // Bz398057AImpl
