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

import org.eclipse.emf.cdo.tests.model6.PropertiesMap;
import org.eclipse.emf.cdo.tests.model6.PropertiesMapEntryValue;
import org.eclipse.emf.cdo.tests.model6.legacy.Model6Package;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Properties Map</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.PropertiesMapImpl#getLabel <em>Label</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.PropertiesMapImpl#getPersistentMap <em>Persistent Map</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.legacy.impl.PropertiesMapImpl#getTransientMap <em>Transient Map</em>}</li>
 * </ul>
 *
 * @generated
 */
public class PropertiesMapImpl extends EObjectImpl implements PropertiesMap
{
  /**
   * The default value of the '{@link #getLabel() <em>Label</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getLabel()
   * @generated
   * @ordered
   */
  protected static final String LABEL_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getLabel() <em>Label</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getLabel()
   * @generated
   * @ordered
   */
  protected String label = LABEL_EDEFAULT;

  /**
   * The cached value of the '{@link #getPersistentMap() <em>Persistent Map</em>}' map.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getPersistentMap()
   * @generated
   * @ordered
   */
  protected EMap<String, PropertiesMapEntryValue> persistentMap;

  /**
   * The cached value of the '{@link #getTransientMap() <em>Transient Map</em>}' map.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTransientMap()
   * @generated
   * @ordered
   */
  protected EMap<String, PropertiesMapEntryValue> transientMap;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected PropertiesMapImpl()
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
    return Model6Package.eINSTANCE.getPropertiesMap();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getLabel()
  {
    return label;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setLabel(String newLabel)
  {
    String oldLabel = label;
    label = newLabel;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model6Package.PROPERTIES_MAP__LABEL, oldLabel, label));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EMap<String, PropertiesMapEntryValue> getPersistentMap()
  {
    if (persistentMap == null)
    {
      persistentMap = new EcoreEMap<String, PropertiesMapEntryValue>(Model6Package.eINSTANCE.getPropertiesMapEntry(), PropertiesMapEntryImpl.class, this,
          Model6Package.PROPERTIES_MAP__PERSISTENT_MAP);
    }
    return persistentMap;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EMap<String, PropertiesMapEntryValue> getTransientMap()
  {
    if (transientMap == null)
    {
      transientMap = new EcoreEMap<String, PropertiesMapEntryValue>(Model6Package.eINSTANCE.getPropertiesMapEntry(), PropertiesMapEntryImpl.class, this,
          Model6Package.PROPERTIES_MAP__TRANSIENT_MAP);
    }
    return transientMap;
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
    case Model6Package.PROPERTIES_MAP__PERSISTENT_MAP:
      return ((InternalEList<?>)getPersistentMap()).basicRemove(otherEnd, msgs);
    case Model6Package.PROPERTIES_MAP__TRANSIENT_MAP:
      return ((InternalEList<?>)getTransientMap()).basicRemove(otherEnd, msgs);
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
    case Model6Package.PROPERTIES_MAP__LABEL:
      return getLabel();
    case Model6Package.PROPERTIES_MAP__PERSISTENT_MAP:
      if (coreType)
      {
        return getPersistentMap();
      }
      else
      {
        return getPersistentMap().map();
      }
    case Model6Package.PROPERTIES_MAP__TRANSIENT_MAP:
      if (coreType)
      {
        return getTransientMap();
      }
      else
      {
        return getTransientMap().map();
      }
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
    case Model6Package.PROPERTIES_MAP__LABEL:
      setLabel((String)newValue);
      return;
    case Model6Package.PROPERTIES_MAP__PERSISTENT_MAP:
      ((EStructuralFeature.Setting)getPersistentMap()).set(newValue);
      return;
    case Model6Package.PROPERTIES_MAP__TRANSIENT_MAP:
      ((EStructuralFeature.Setting)getTransientMap()).set(newValue);
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
    case Model6Package.PROPERTIES_MAP__LABEL:
      setLabel(LABEL_EDEFAULT);
      return;
    case Model6Package.PROPERTIES_MAP__PERSISTENT_MAP:
      getPersistentMap().clear();
      return;
    case Model6Package.PROPERTIES_MAP__TRANSIENT_MAP:
      getTransientMap().clear();
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
    case Model6Package.PROPERTIES_MAP__LABEL:
      return LABEL_EDEFAULT == null ? label != null : !LABEL_EDEFAULT.equals(label);
    case Model6Package.PROPERTIES_MAP__PERSISTENT_MAP:
      return persistentMap != null && !persistentMap.isEmpty();
    case Model6Package.PROPERTIES_MAP__TRANSIENT_MAP:
      return transientMap != null && !transientMap.isEmpty();
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
    result.append(" (label: ");
    result.append(label);
    result.append(')');
    return result.toString();
  }

} // PropertiesMapImpl
