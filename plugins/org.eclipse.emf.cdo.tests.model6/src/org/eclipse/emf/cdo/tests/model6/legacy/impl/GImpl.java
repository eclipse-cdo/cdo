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
package org.eclipse.emf.cdo.tests.model6.legacy.impl;

import org.eclipse.emf.cdo.tests.model6.BaseObject;
import org.eclipse.emf.cdo.tests.model6.G;
import org.eclipse.emf.cdo.tests.model6.legacy.Model6Package;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>G</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.GImpl#getDummy <em>Dummy</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.GImpl#getReference <em>Reference</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.GImpl#getList <em>List</em>}</li>
 * </ul>
 *
 * @generated
 */
public class GImpl extends EObjectImpl implements G
{
  /**
   * The default value of the '{@link #getDummy() <em>Dummy</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getDummy()
   * @generated
   * @ordered
   */
  protected static final String DUMMY_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getDummy() <em>Dummy</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getDummy()
   * @generated
   * @ordered
   */
  protected String dummy = DUMMY_EDEFAULT;

  /**
   * The cached value of the '{@link #getReference() <em>Reference</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getReference()
   * @generated
   * @ordered
   */
  protected BaseObject reference;

  /**
   * The cached value of the '{@link #getList() <em>List</em>}' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getList()
   * @generated
   * @ordered
   */
  protected EList<BaseObject> list;

  /**
   * @ADDED
   */
  private List<Notification> notifications = new ArrayList<>();

  /**
   * @ADDED
   */
  private boolean listModified = false;

  /**
   * @ADDED
   */
  private boolean referenceModified = false;

  /**
   * @ADDED
   */
  private boolean attributeModified = false;

  /**
   * @ADDED
   */
  {
    eAdapters().add(new AdapterImpl()
    {
      @Override
      public void notifyChanged(Notification msg)
      {
        notifications.add(msg);

        EStructuralFeature feature = (EStructuralFeature)msg.getFeature();
        if (feature != null)
        {
          if (feature.equals("dummy"))
          {
            attributeModified = true;
          }
          else if (feature.equals("reference"))
          {
            referenceModified = true;
          }
          else if (feature.equals("list"))
          {
            listModified = true;
          }
        }
      }
    });
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected GImpl()
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
    return Model6Package.eINSTANCE.getG();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getDummy()
  {
    return dummy;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setDummy(String newDummy)
  {
    String oldDummy = dummy;
    dummy = newDummy;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model6Package.G__DUMMY, oldDummy, dummy));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public BaseObject getReference()
  {
    if (reference != null && reference.eIsProxy())
    {
      InternalEObject oldReference = (InternalEObject)reference;
      reference = (BaseObject)eResolveProxy(oldReference);
      if (reference != oldReference)
      {
        if (eNotificationRequired())
        {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, Model6Package.G__REFERENCE, oldReference, reference));
        }
      }
    }
    return reference;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public BaseObject basicGetReference()
  {
    return reference;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setReference(BaseObject newReference)
  {
    BaseObject oldReference = reference;
    reference = newReference;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model6Package.G__REFERENCE, oldReference, reference));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<BaseObject> getList()
  {
    if (list == null)
    {
      list = new EObjectResolvingEList<>(BaseObject.class, this, Model6Package.G__LIST);
    }
    return list;
  }

  /**
   * @ADDED
   */
  @Override
  public List<Notification> getNotifications()
  {
    return notifications;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public boolean isAttributeModified()
  {
    return attributeModified;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public boolean isReferenceModified()
  {
    return referenceModified;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public boolean isListModified()
  {
    return listModified;
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
    case Model6Package.G__DUMMY:
      return getDummy();
    case Model6Package.G__REFERENCE:
      if (resolve)
      {
        return getReference();
      }
      return basicGetReference();
    case Model6Package.G__LIST:
      return getList();
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
    case Model6Package.G__DUMMY:
      setDummy((String)newValue);
      return;
    case Model6Package.G__REFERENCE:
      setReference((BaseObject)newValue);
      return;
    case Model6Package.G__LIST:
      getList().clear();
      getList().addAll((Collection<? extends BaseObject>)newValue);
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
    case Model6Package.G__DUMMY:
      setDummy(DUMMY_EDEFAULT);
      return;
    case Model6Package.G__REFERENCE:
      setReference((BaseObject)null);
      return;
    case Model6Package.G__LIST:
      getList().clear();
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
    case Model6Package.G__DUMMY:
      return DUMMY_EDEFAULT == null ? dummy != null : !DUMMY_EDEFAULT.equals(dummy);
    case Model6Package.G__REFERENCE:
      return reference != null;
    case Model6Package.G__LIST:
      return list != null && !list.isEmpty();
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
    result.append(" (dummy: ");
    result.append(dummy);
    result.append(')');
    return result.toString();
  }

} // GImpl
