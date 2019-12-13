/*
 * Copyright (c) 2013, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model3.legacy.impl;

import org.eclipse.emf.cdo.tests.model3.MetaRef;
import org.eclipse.emf.cdo.tests.model3.legacy.Model3Package;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Meta Ref</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.legacy.impl.MetaRefImpl#getEPackageRef <em>EPackage Ref</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.legacy.impl.MetaRefImpl#getEClassRef <em>EClass Ref</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.legacy.impl.MetaRefImpl#getEReferenceRef <em>EReference Ref</em>}</li>
 * </ul>
 *
 * @generated
 */
public class MetaRefImpl extends EObjectImpl implements MetaRef
{
  /**
   * The cached value of the '{@link #getEPackageRef() <em>EPackage Ref</em>}' reference.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #getEPackageRef()
   * @generated
   * @ordered
   */
  protected EPackage ePackageRef;

  /**
   * The cached value of the '{@link #getEClassRef() <em>EClass Ref</em>}' reference.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #getEClassRef()
   * @generated
   * @ordered
   */
  protected EClass eClassRef;

  /**
   * The cached value of the '{@link #getEReferenceRef() <em>EReference Ref</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEReferenceRef()
   * @generated
   * @ordered
   */
  protected EReference eReferenceRef;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected MetaRefImpl()
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
    return Model3Package.eINSTANCE.getMetaRef();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EPackage getEPackageRef()
  {
    if (ePackageRef != null && ePackageRef.eIsProxy())
    {
      InternalEObject oldEPackageRef = (InternalEObject)ePackageRef;
      ePackageRef = (EPackage)eResolveProxy(oldEPackageRef);
      if (ePackageRef != oldEPackageRef)
      {
        if (eNotificationRequired())
        {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, Model3Package.META_REF__EPACKAGE_REF, oldEPackageRef, ePackageRef));
        }
      }
    }
    return ePackageRef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EPackage basicGetEPackageRef()
  {
    return ePackageRef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setEPackageRef(EPackage newEPackageRef)
  {
    EPackage oldEPackageRef = ePackageRef;
    ePackageRef = newEPackageRef;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model3Package.META_REF__EPACKAGE_REF, oldEPackageRef, ePackageRef));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getEClassRef()
  {
    if (eClassRef != null && eClassRef.eIsProxy())
    {
      InternalEObject oldEClassRef = (InternalEObject)eClassRef;
      eClassRef = (EClass)eResolveProxy(oldEClassRef);
      if (eClassRef != oldEClassRef)
      {
        if (eNotificationRequired())
        {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, Model3Package.META_REF__ECLASS_REF, oldEClassRef, eClassRef));
        }
      }
    }
    return eClassRef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EClass basicGetEClassRef()
  {
    return eClassRef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setEClassRef(EClass newEClassRef)
  {
    EClass oldEClassRef = eClassRef;
    eClassRef = newEClassRef;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model3Package.META_REF__ECLASS_REF, oldEClassRef, eClassRef));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EReference getEReferenceRef()
  {
    if (eReferenceRef != null && eReferenceRef.eIsProxy())
    {
      InternalEObject oldEReferenceRef = (InternalEObject)eReferenceRef;
      eReferenceRef = (EReference)eResolveProxy(oldEReferenceRef);
      if (eReferenceRef != oldEReferenceRef)
      {
        if (eNotificationRequired())
        {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, Model3Package.META_REF__EREFERENCE_REF, oldEReferenceRef, eReferenceRef));
        }
      }
    }
    return eReferenceRef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EReference basicGetEReferenceRef()
  {
    return eReferenceRef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setEReferenceRef(EReference newEReferenceRef)
  {
    EReference oldEReferenceRef = eReferenceRef;
    eReferenceRef = newEReferenceRef;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model3Package.META_REF__EREFERENCE_REF, oldEReferenceRef, eReferenceRef));
    }
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
    case Model3Package.META_REF__EPACKAGE_REF:
      if (resolve)
      {
        return getEPackageRef();
      }
      return basicGetEPackageRef();
    case Model3Package.META_REF__ECLASS_REF:
      if (resolve)
      {
        return getEClassRef();
      }
      return basicGetEClassRef();
    case Model3Package.META_REF__EREFERENCE_REF:
      if (resolve)
      {
        return getEReferenceRef();
      }
      return basicGetEReferenceRef();
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
    case Model3Package.META_REF__EPACKAGE_REF:
      setEPackageRef((EPackage)newValue);
      return;
    case Model3Package.META_REF__ECLASS_REF:
      setEClassRef((EClass)newValue);
      return;
    case Model3Package.META_REF__EREFERENCE_REF:
      setEReferenceRef((EReference)newValue);
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
    case Model3Package.META_REF__EPACKAGE_REF:
      setEPackageRef((EPackage)null);
      return;
    case Model3Package.META_REF__ECLASS_REF:
      setEClassRef((EClass)null);
      return;
    case Model3Package.META_REF__EREFERENCE_REF:
      setEReferenceRef((EReference)null);
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
    case Model3Package.META_REF__EPACKAGE_REF:
      return ePackageRef != null;
    case Model3Package.META_REF__ECLASS_REF:
      return eClassRef != null;
    case Model3Package.META_REF__EREFERENCE_REF:
      return eReferenceRef != null;
    }
    return super.eIsSet(featureID);
  }

} // MetaRefImpl
