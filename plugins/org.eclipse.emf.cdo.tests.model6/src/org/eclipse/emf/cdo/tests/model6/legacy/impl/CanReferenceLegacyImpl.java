/*
 * Copyright (c) 2013-2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christian W. Damus (CEA) - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model6.legacy.impl;

import org.eclipse.emf.cdo.tests.model6.CanReferenceLegacy;
import org.eclipse.emf.cdo.tests.model6.legacy.Model6Package;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

import java.util.Collection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Can Reference Legacy</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.CanReferenceLegacyImpl#getSingleContainment <em>Single Containment</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.CanReferenceLegacyImpl#getMultipleContainment <em>Multiple Containment</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.CanReferenceLegacyImpl#getSingleReference <em>Single Reference</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.CanReferenceLegacyImpl#getMultipleReference <em>Multiple Reference</em>}</li>
 * </ul>
 *
 * @generated
 */
public class CanReferenceLegacyImpl extends EObjectImpl implements CanReferenceLegacy
{
  /**
   * The cached value of the '{@link #getSingleContainment() <em>Single Containment</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSingleContainment()
   * @generated
   * @ordered
   */
  protected EObject singleContainment;

  /**
   * The cached value of the '{@link #getMultipleContainment() <em>Multiple Containment</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getMultipleContainment()
   * @generated
   * @ordered
   */
  protected EList<EObject> multipleContainment;

  /**
   * The cached value of the '{@link #getSingleReference() <em>Single Reference</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSingleReference()
   * @generated
   * @ordered
   */
  protected EObject singleReference;

  /**
   * The cached value of the '{@link #getMultipleReference() <em>Multiple Reference</em>}' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getMultipleReference()
   * @generated
   * @ordered
   */
  protected EList<EObject> multipleReference;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected CanReferenceLegacyImpl()
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
    return Model6Package.eINSTANCE.getCanReferenceLegacy();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObject getSingleContainment()
  {
    if (singleContainment != null && singleContainment.eIsProxy())
    {
      InternalEObject oldSingleContainment = (InternalEObject)singleContainment;
      singleContainment = eResolveProxy(oldSingleContainment);
      if (singleContainment != oldSingleContainment)
      {
        InternalEObject newSingleContainment = (InternalEObject)singleContainment;
        NotificationChain msgs = oldSingleContainment.eInverseRemove(this, EOPPOSITE_FEATURE_BASE - Model6Package.CAN_REFERENCE_LEGACY__SINGLE_CONTAINMENT,
            null, null);
        if (newSingleContainment.eInternalContainer() == null)
        {
          msgs = newSingleContainment.eInverseAdd(this, EOPPOSITE_FEATURE_BASE - Model6Package.CAN_REFERENCE_LEGACY__SINGLE_CONTAINMENT, null, msgs);
        }
        if (msgs != null)
        {
          msgs.dispatch();
        }
        if (eNotificationRequired())
        {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, Model6Package.CAN_REFERENCE_LEGACY__SINGLE_CONTAINMENT, oldSingleContainment,
              singleContainment));
        }
      }
    }
    return singleContainment;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EObject basicGetSingleContainment()
  {
    return singleContainment;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetSingleContainment(EObject newSingleContainment, NotificationChain msgs)
  {
    EObject oldSingleContainment = singleContainment;
    singleContainment = newSingleContainment;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, Model6Package.CAN_REFERENCE_LEGACY__SINGLE_CONTAINMENT,
          oldSingleContainment, newSingleContainment);
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
  @Override
  public void setSingleContainment(EObject newSingleContainment)
  {
    if (newSingleContainment != singleContainment)
    {
      NotificationChain msgs = null;
      if (singleContainment != null)
      {
        msgs = ((InternalEObject)singleContainment).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - Model6Package.CAN_REFERENCE_LEGACY__SINGLE_CONTAINMENT, null,
            msgs);
      }
      if (newSingleContainment != null)
      {
        msgs = ((InternalEObject)newSingleContainment).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - Model6Package.CAN_REFERENCE_LEGACY__SINGLE_CONTAINMENT, null,
            msgs);
      }
      msgs = basicSetSingleContainment(newSingleContainment, msgs);
      if (msgs != null)
      {
        msgs.dispatch();
      }
    }
    else if (eNotificationRequired())
    {
      eNotify(
          new ENotificationImpl(this, Notification.SET, Model6Package.CAN_REFERENCE_LEGACY__SINGLE_CONTAINMENT, newSingleContainment, newSingleContainment));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<EObject> getMultipleContainment()
  {
    if (multipleContainment == null)
    {
      multipleContainment = new EObjectContainmentEList.Resolving<EObject>(EObject.class, this, Model6Package.CAN_REFERENCE_LEGACY__MULTIPLE_CONTAINMENT);
    }
    return multipleContainment;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObject getSingleReference()
  {
    if (singleReference != null && singleReference.eIsProxy())
    {
      InternalEObject oldSingleReference = (InternalEObject)singleReference;
      singleReference = eResolveProxy(oldSingleReference);
      if (singleReference != oldSingleReference)
      {
        if (eNotificationRequired())
        {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, Model6Package.CAN_REFERENCE_LEGACY__SINGLE_REFERENCE, oldSingleReference, singleReference));
        }
      }
    }
    return singleReference;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EObject basicGetSingleReference()
  {
    return singleReference;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setSingleReference(EObject newSingleReference)
  {
    EObject oldSingleReference = singleReference;
    singleReference = newSingleReference;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model6Package.CAN_REFERENCE_LEGACY__SINGLE_REFERENCE, oldSingleReference, singleReference));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<EObject> getMultipleReference()
  {
    if (multipleReference == null)
    {
      multipleReference = new EObjectResolvingEList<EObject>(EObject.class, this, Model6Package.CAN_REFERENCE_LEGACY__MULTIPLE_REFERENCE);
    }
    return multipleReference;
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
    case Model6Package.CAN_REFERENCE_LEGACY__SINGLE_CONTAINMENT:
      return basicSetSingleContainment(null, msgs);
    case Model6Package.CAN_REFERENCE_LEGACY__MULTIPLE_CONTAINMENT:
      return ((InternalEList<?>)getMultipleContainment()).basicRemove(otherEnd, msgs);
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
    case Model6Package.CAN_REFERENCE_LEGACY__SINGLE_CONTAINMENT:
      if (resolve)
      {
        return getSingleContainment();
      }
      return basicGetSingleContainment();
    case Model6Package.CAN_REFERENCE_LEGACY__MULTIPLE_CONTAINMENT:
      return getMultipleContainment();
    case Model6Package.CAN_REFERENCE_LEGACY__SINGLE_REFERENCE:
      if (resolve)
      {
        return getSingleReference();
      }
      return basicGetSingleReference();
    case Model6Package.CAN_REFERENCE_LEGACY__MULTIPLE_REFERENCE:
      return getMultipleReference();
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
    case Model6Package.CAN_REFERENCE_LEGACY__SINGLE_CONTAINMENT:
      setSingleContainment((EObject)newValue);
      return;
    case Model6Package.CAN_REFERENCE_LEGACY__MULTIPLE_CONTAINMENT:
      getMultipleContainment().clear();
      getMultipleContainment().addAll((Collection<? extends EObject>)newValue);
      return;
    case Model6Package.CAN_REFERENCE_LEGACY__SINGLE_REFERENCE:
      setSingleReference((EObject)newValue);
      return;
    case Model6Package.CAN_REFERENCE_LEGACY__MULTIPLE_REFERENCE:
      getMultipleReference().clear();
      getMultipleReference().addAll((Collection<? extends EObject>)newValue);
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
    case Model6Package.CAN_REFERENCE_LEGACY__SINGLE_CONTAINMENT:
      setSingleContainment((EObject)null);
      return;
    case Model6Package.CAN_REFERENCE_LEGACY__MULTIPLE_CONTAINMENT:
      getMultipleContainment().clear();
      return;
    case Model6Package.CAN_REFERENCE_LEGACY__SINGLE_REFERENCE:
      setSingleReference((EObject)null);
      return;
    case Model6Package.CAN_REFERENCE_LEGACY__MULTIPLE_REFERENCE:
      getMultipleReference().clear();
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
    case Model6Package.CAN_REFERENCE_LEGACY__SINGLE_CONTAINMENT:
      return singleContainment != null;
    case Model6Package.CAN_REFERENCE_LEGACY__MULTIPLE_CONTAINMENT:
      return multipleContainment != null && !multipleContainment.isEmpty();
    case Model6Package.CAN_REFERENCE_LEGACY__SINGLE_REFERENCE:
      return singleReference != null;
    case Model6Package.CAN_REFERENCE_LEGACY__MULTIPLE_REFERENCE:
      return multipleReference != null && !multipleReference.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} // CanReferenceLegacyImpl
