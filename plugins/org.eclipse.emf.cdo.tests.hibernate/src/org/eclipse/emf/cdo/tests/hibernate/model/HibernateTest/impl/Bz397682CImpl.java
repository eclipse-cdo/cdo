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

import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz397682C;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz397682P;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Bz397682 C</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz397682CImpl#getRefToP <em>Ref To P</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz397682CImpl#getRefToC <em>Ref To C</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz397682CImpl#getDbId <em>Db Id</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class Bz397682CImpl extends EObjectImpl implements Bz397682C
{
  /**
   * The cached value of the '{@link #getRefToC() <em>Ref To C</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRefToC()
   * @generated
   * @ordered
   */
  protected Bz397682C refToC;

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
  protected Bz397682CImpl()
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
    return HibernateTestPackage.Literals.BZ397682_C;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Bz397682P getRefToP()
  {
    if (eContainerFeatureID() != HibernateTestPackage.BZ397682_C__REF_TO_P)
    {
      return null;
    }
    return (Bz397682P)eContainer();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetRefToP(Bz397682P newRefToP, NotificationChain msgs)
  {
    msgs = eBasicSetContainer((InternalEObject)newRefToP, HibernateTestPackage.BZ397682_C__REF_TO_P, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setRefToP(Bz397682P newRefToP)
  {
    if (newRefToP != eInternalContainer() || eContainerFeatureID() != HibernateTestPackage.BZ397682_C__REF_TO_P && newRefToP != null)
    {
      if (EcoreUtil.isAncestor(this, newRefToP))
      {
        throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
      }
      NotificationChain msgs = null;
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      if (newRefToP != null)
      {
        msgs = ((InternalEObject)newRefToP).eInverseAdd(this, HibernateTestPackage.BZ397682_P__LIST_OF_C, Bz397682P.class, msgs);
      }
      msgs = basicSetRefToP(newRefToP, msgs);
      if (msgs != null)
      {
        msgs.dispatch();
      }
    }
    else if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, HibernateTestPackage.BZ397682_C__REF_TO_P, newRefToP, newRefToP));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Bz397682C getRefToC()
  {
    if (refToC != null && refToC.eIsProxy())
    {
      InternalEObject oldRefToC = (InternalEObject)refToC;
      refToC = (Bz397682C)eResolveProxy(oldRefToC);
      if (refToC != oldRefToC)
      {
        if (eNotificationRequired())
        {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, HibernateTestPackage.BZ397682_C__REF_TO_C, oldRefToC, refToC));
        }
      }
    }
    return refToC;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Bz397682C basicGetRefToC()
  {
    return refToC;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setRefToC(Bz397682C newRefToC)
  {
    Bz397682C oldRefToC = refToC;
    refToC = newRefToC;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, HibernateTestPackage.BZ397682_C__REF_TO_C, oldRefToC, refToC));
    }
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
      eNotify(new ENotificationImpl(this, Notification.SET, HibernateTestPackage.BZ397682_C__DB_ID, oldDbId, dbId));
    }
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
    case HibernateTestPackage.BZ397682_C__REF_TO_P:
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      return basicSetRefToP((Bz397682P)otherEnd, msgs);
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
    case HibernateTestPackage.BZ397682_C__REF_TO_P:
      return basicSetRefToP(null, msgs);
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
    case HibernateTestPackage.BZ397682_C__REF_TO_P:
      return eInternalContainer().eInverseRemove(this, HibernateTestPackage.BZ397682_P__LIST_OF_C, Bz397682P.class, msgs);
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
    case HibernateTestPackage.BZ397682_C__REF_TO_P:
      return getRefToP();
    case HibernateTestPackage.BZ397682_C__REF_TO_C:
      if (resolve)
      {
        return getRefToC();
      }
      return basicGetRefToC();
    case HibernateTestPackage.BZ397682_C__DB_ID:
      return getDbId();
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
    case HibernateTestPackage.BZ397682_C__REF_TO_P:
      setRefToP((Bz397682P)newValue);
      return;
    case HibernateTestPackage.BZ397682_C__REF_TO_C:
      setRefToC((Bz397682C)newValue);
      return;
    case HibernateTestPackage.BZ397682_C__DB_ID:
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
    case HibernateTestPackage.BZ397682_C__REF_TO_P:
      setRefToP((Bz397682P)null);
      return;
    case HibernateTestPackage.BZ397682_C__REF_TO_C:
      setRefToC((Bz397682C)null);
      return;
    case HibernateTestPackage.BZ397682_C__DB_ID:
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
    case HibernateTestPackage.BZ397682_C__REF_TO_P:
      return getRefToP() != null;
    case HibernateTestPackage.BZ397682_C__REF_TO_C:
      return refToC != null;
    case HibernateTestPackage.BZ397682_C__DB_ID:
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

} // Bz397682CImpl
