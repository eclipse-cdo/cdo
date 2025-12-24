/*
 * Copyright (c) 2013, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *
 * $Id: MapHolderImpl.java,v 1.2 2011-01-01 11:01:57 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.model2.legacy.impl;

import org.eclipse.emf.cdo.tests.model1.Address;
import org.eclipse.emf.cdo.tests.model1.VAT;
import org.eclipse.emf.cdo.tests.model2.MapHolder;
import org.eclipse.emf.cdo.tests.model2.legacy.Model2Package;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Map Holder</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.legacy.impl.MapHolderImpl#getIntegerToStringMap <em>Integer To String Map</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.legacy.impl.MapHolderImpl#getStringToStringMap <em>String To String Map</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.legacy.impl.MapHolderImpl#getStringToVATMap <em>String To VAT Map</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.legacy.impl.MapHolderImpl#getStringToAddressContainmentMap <em>String To Address Containment Map</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.legacy.impl.MapHolderImpl#getStringToAddressReferenceMap <em>String To Address Reference Map</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.legacy.impl.MapHolderImpl#getEObjectToEObjectMap <em>EObject To EObject Map</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.legacy.impl.MapHolderImpl#getEObjectToEObjectKeyContainedMap <em>EObject To EObject Key Contained Map</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.legacy.impl.MapHolderImpl#getEObjectToEObjectBothContainedMap <em>EObject To EObject Both Contained Map</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.legacy.impl.MapHolderImpl#getEObjectToEObjectValueContainedMap <em>EObject To EObject Value Contained Map</em>}</li>
 * </ul>
 *
 * @generated
 */
public class MapHolderImpl extends EObjectImpl implements MapHolder
{
  /**
   * The cached value of the '{@link #getIntegerToStringMap() <em>Integer To String Map</em>}' map.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @see #getIntegerToStringMap()
   * @generated
   * @ordered
   */
  protected EMap<Integer, String> integerToStringMap;

  /**
   * The cached value of the '{@link #getStringToStringMap() <em>String To String Map</em>}' map.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @see #getStringToStringMap()
   * @generated
   * @ordered
   */
  protected EMap<String, String> stringToStringMap;

  /**
   * The cached value of the '{@link #getStringToVATMap() <em>String To VAT Map</em>}' map.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #getStringToVATMap()
   * @generated
   * @ordered
   */
  protected EMap<String, VAT> stringToVATMap;

  /**
   * The cached value of the '{@link #getStringToAddressContainmentMap() <em>String To Address Containment Map</em>}' map.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getStringToAddressContainmentMap()
   * @generated
   * @ordered
   */
  protected EMap<String, Address> stringToAddressContainmentMap;

  /**
   * The cached value of the '{@link #getStringToAddressReferenceMap() <em>String To Address Reference Map</em>}' map.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getStringToAddressReferenceMap()
   * @generated
   * @ordered
   */
  protected EMap<String, Address> stringToAddressReferenceMap;

  /**
   * The cached value of the '{@link #getEObjectToEObjectMap() <em>EObject To EObject Map</em>}' map. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getEObjectToEObjectMap()
   * @generated
   * @ordered
   */
  protected EMap<EObject, EObject> eObjectToEObjectMap;

  /**
   * The cached value of the '{@link #getEObjectToEObjectKeyContainedMap() <em>EObject To EObject Key Contained Map</em>}' map.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getEObjectToEObjectKeyContainedMap()
   * @generated
   * @ordered
   */
  protected EMap<EObject, EObject> eObjectToEObjectKeyContainedMap;

  /**
   * The cached value of the '{@link #getEObjectToEObjectBothContainedMap() <em>EObject To EObject Both Contained Map</em>}' map.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getEObjectToEObjectBothContainedMap()
   * @generated
   * @ordered
   */
  protected EMap<EObject, EObject> eObjectToEObjectBothContainedMap;

  /**
   * The cached value of the '{@link #getEObjectToEObjectValueContainedMap() <em>EObject To EObject Value Contained Map</em>}' map.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getEObjectToEObjectValueContainedMap()
   * @generated
   * @ordered
   */
  protected EMap<EObject, EObject> eObjectToEObjectValueContainedMap;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected MapHolderImpl()
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
    return Model2Package.eINSTANCE.getMapHolder();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EMap<Integer, String> getIntegerToStringMap()
  {
    if (integerToStringMap == null)
    {
      integerToStringMap = new EcoreEMap<>(Model2Package.eINSTANCE.getIntegerToStringMap(), IntegerToStringMapImpl.class, this,
          Model2Package.MAP_HOLDER__INTEGER_TO_STRING_MAP);
    }
    return integerToStringMap;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EMap<String, String> getStringToStringMap()
  {
    if (stringToStringMap == null)
    {
      stringToStringMap = new EcoreEMap<>(Model2Package.eINSTANCE.getStringToStringMap(), StringToStringMapImpl.class, this,
          Model2Package.MAP_HOLDER__STRING_TO_STRING_MAP);
    }
    return stringToStringMap;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EMap<String, VAT> getStringToVATMap()
  {
    if (stringToVATMap == null)
    {
      stringToVATMap = new EcoreEMap<>(Model2Package.eINSTANCE.getStringToVATMap(), StringToVATMapImpl.class, this,
          Model2Package.MAP_HOLDER__STRING_TO_VAT_MAP);
    }
    return stringToVATMap;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EMap<String, Address> getStringToAddressContainmentMap()
  {
    if (stringToAddressContainmentMap == null)
    {
      stringToAddressContainmentMap = new EcoreEMap<>(Model2Package.eINSTANCE.getStringToAddressContainmentMap(),
          StringToAddressContainmentMapImpl.class, this, Model2Package.MAP_HOLDER__STRING_TO_ADDRESS_CONTAINMENT_MAP);
    }
    return stringToAddressContainmentMap;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EMap<String, Address> getStringToAddressReferenceMap()
  {
    if (stringToAddressReferenceMap == null)
    {
      stringToAddressReferenceMap = new EcoreEMap<>(Model2Package.eINSTANCE.getStringToAddressReferenceMap(),
          StringToAddressReferenceMapImpl.class, this, Model2Package.MAP_HOLDER__STRING_TO_ADDRESS_REFERENCE_MAP);
    }
    return stringToAddressReferenceMap;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EMap<EObject, EObject> getEObjectToEObjectMap()
  {
    if (eObjectToEObjectMap == null)
    {
      eObjectToEObjectMap = new EcoreEMap<>(Model2Package.eINSTANCE.getEObjectToEObjectMap(), EObjectToEObjectMapImpl.class, this,
          Model2Package.MAP_HOLDER__EOBJECT_TO_EOBJECT_MAP);
    }
    return eObjectToEObjectMap;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EMap<EObject, EObject> getEObjectToEObjectKeyContainedMap()
  {
    if (eObjectToEObjectKeyContainedMap == null)
    {
      eObjectToEObjectKeyContainedMap = new EcoreEMap<>(Model2Package.eINSTANCE.getEObjectToEObjectKeyContainedMap(),
          EObjectToEObjectKeyContainedMapImpl.class, this, Model2Package.MAP_HOLDER__EOBJECT_TO_EOBJECT_KEY_CONTAINED_MAP);
    }
    return eObjectToEObjectKeyContainedMap;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EMap<EObject, EObject> getEObjectToEObjectBothContainedMap()
  {
    if (eObjectToEObjectBothContainedMap == null)
    {
      eObjectToEObjectBothContainedMap = new EcoreEMap<>(Model2Package.eINSTANCE.getEObjectToEObjectBothContainedMap(),
          EObjectToEObjectBothContainedMapImpl.class, this, Model2Package.MAP_HOLDER__EOBJECT_TO_EOBJECT_BOTH_CONTAINED_MAP);
    }
    return eObjectToEObjectBothContainedMap;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EMap<EObject, EObject> getEObjectToEObjectValueContainedMap()
  {
    if (eObjectToEObjectValueContainedMap == null)
    {
      eObjectToEObjectValueContainedMap = new EcoreEMap<>(Model2Package.eINSTANCE.getEObjectToEObjectValueContainedMap(),
          EObjectToEObjectValueContainedMapImpl.class, this, Model2Package.MAP_HOLDER__EOBJECT_TO_EOBJECT_VALUE_CONTAINED_MAP);
    }
    return eObjectToEObjectValueContainedMap;
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
    case Model2Package.MAP_HOLDER__INTEGER_TO_STRING_MAP:
      return ((InternalEList<?>)getIntegerToStringMap()).basicRemove(otherEnd, msgs);
    case Model2Package.MAP_HOLDER__STRING_TO_STRING_MAP:
      return ((InternalEList<?>)getStringToStringMap()).basicRemove(otherEnd, msgs);
    case Model2Package.MAP_HOLDER__STRING_TO_VAT_MAP:
      return ((InternalEList<?>)getStringToVATMap()).basicRemove(otherEnd, msgs);
    case Model2Package.MAP_HOLDER__STRING_TO_ADDRESS_CONTAINMENT_MAP:
      return ((InternalEList<?>)getStringToAddressContainmentMap()).basicRemove(otherEnd, msgs);
    case Model2Package.MAP_HOLDER__STRING_TO_ADDRESS_REFERENCE_MAP:
      return ((InternalEList<?>)getStringToAddressReferenceMap()).basicRemove(otherEnd, msgs);
    case Model2Package.MAP_HOLDER__EOBJECT_TO_EOBJECT_MAP:
      return ((InternalEList<?>)getEObjectToEObjectMap()).basicRemove(otherEnd, msgs);
    case Model2Package.MAP_HOLDER__EOBJECT_TO_EOBJECT_KEY_CONTAINED_MAP:
      return ((InternalEList<?>)getEObjectToEObjectKeyContainedMap()).basicRemove(otherEnd, msgs);
    case Model2Package.MAP_HOLDER__EOBJECT_TO_EOBJECT_BOTH_CONTAINED_MAP:
      return ((InternalEList<?>)getEObjectToEObjectBothContainedMap()).basicRemove(otherEnd, msgs);
    case Model2Package.MAP_HOLDER__EOBJECT_TO_EOBJECT_VALUE_CONTAINED_MAP:
      return ((InternalEList<?>)getEObjectToEObjectValueContainedMap()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
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
    case Model2Package.MAP_HOLDER__INTEGER_TO_STRING_MAP:
      if (coreType)
      {
        return getIntegerToStringMap();
      }
      else
      {
        return getIntegerToStringMap().map();
      }
    case Model2Package.MAP_HOLDER__STRING_TO_STRING_MAP:
      if (coreType)
      {
        return getStringToStringMap();
      }
      else
      {
        return getStringToStringMap().map();
      }
    case Model2Package.MAP_HOLDER__STRING_TO_VAT_MAP:
      if (coreType)
      {
        return getStringToVATMap();
      }
      else
      {
        return getStringToVATMap().map();
      }
    case Model2Package.MAP_HOLDER__STRING_TO_ADDRESS_CONTAINMENT_MAP:
      if (coreType)
      {
        return getStringToAddressContainmentMap();
      }
      else
      {
        return getStringToAddressContainmentMap().map();
      }
    case Model2Package.MAP_HOLDER__STRING_TO_ADDRESS_REFERENCE_MAP:
      if (coreType)
      {
        return getStringToAddressReferenceMap();
      }
      else
      {
        return getStringToAddressReferenceMap().map();
      }
    case Model2Package.MAP_HOLDER__EOBJECT_TO_EOBJECT_MAP:
      if (coreType)
      {
        return getEObjectToEObjectMap();
      }
      else
      {
        return getEObjectToEObjectMap().map();
      }
    case Model2Package.MAP_HOLDER__EOBJECT_TO_EOBJECT_KEY_CONTAINED_MAP:
      if (coreType)
      {
        return getEObjectToEObjectKeyContainedMap();
      }
      else
      {
        return getEObjectToEObjectKeyContainedMap().map();
      }
    case Model2Package.MAP_HOLDER__EOBJECT_TO_EOBJECT_BOTH_CONTAINED_MAP:
      if (coreType)
      {
        return getEObjectToEObjectBothContainedMap();
      }
      else
      {
        return getEObjectToEObjectBothContainedMap().map();
      }
    case Model2Package.MAP_HOLDER__EOBJECT_TO_EOBJECT_VALUE_CONTAINED_MAP:
      if (coreType)
      {
        return getEObjectToEObjectValueContainedMap();
      }
      else
      {
        return getEObjectToEObjectValueContainedMap().map();
      }
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
    case Model2Package.MAP_HOLDER__INTEGER_TO_STRING_MAP:
      ((EStructuralFeature.Setting)getIntegerToStringMap()).set(newValue);
      return;
    case Model2Package.MAP_HOLDER__STRING_TO_STRING_MAP:
      ((EStructuralFeature.Setting)getStringToStringMap()).set(newValue);
      return;
    case Model2Package.MAP_HOLDER__STRING_TO_VAT_MAP:
      ((EStructuralFeature.Setting)getStringToVATMap()).set(newValue);
      return;
    case Model2Package.MAP_HOLDER__STRING_TO_ADDRESS_CONTAINMENT_MAP:
      ((EStructuralFeature.Setting)getStringToAddressContainmentMap()).set(newValue);
      return;
    case Model2Package.MAP_HOLDER__STRING_TO_ADDRESS_REFERENCE_MAP:
      ((EStructuralFeature.Setting)getStringToAddressReferenceMap()).set(newValue);
      return;
    case Model2Package.MAP_HOLDER__EOBJECT_TO_EOBJECT_MAP:
      ((EStructuralFeature.Setting)getEObjectToEObjectMap()).set(newValue);
      return;
    case Model2Package.MAP_HOLDER__EOBJECT_TO_EOBJECT_KEY_CONTAINED_MAP:
      ((EStructuralFeature.Setting)getEObjectToEObjectKeyContainedMap()).set(newValue);
      return;
    case Model2Package.MAP_HOLDER__EOBJECT_TO_EOBJECT_BOTH_CONTAINED_MAP:
      ((EStructuralFeature.Setting)getEObjectToEObjectBothContainedMap()).set(newValue);
      return;
    case Model2Package.MAP_HOLDER__EOBJECT_TO_EOBJECT_VALUE_CONTAINED_MAP:
      ((EStructuralFeature.Setting)getEObjectToEObjectValueContainedMap()).set(newValue);
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
    case Model2Package.MAP_HOLDER__INTEGER_TO_STRING_MAP:
      getIntegerToStringMap().clear();
      return;
    case Model2Package.MAP_HOLDER__STRING_TO_STRING_MAP:
      getStringToStringMap().clear();
      return;
    case Model2Package.MAP_HOLDER__STRING_TO_VAT_MAP:
      getStringToVATMap().clear();
      return;
    case Model2Package.MAP_HOLDER__STRING_TO_ADDRESS_CONTAINMENT_MAP:
      getStringToAddressContainmentMap().clear();
      return;
    case Model2Package.MAP_HOLDER__STRING_TO_ADDRESS_REFERENCE_MAP:
      getStringToAddressReferenceMap().clear();
      return;
    case Model2Package.MAP_HOLDER__EOBJECT_TO_EOBJECT_MAP:
      getEObjectToEObjectMap().clear();
      return;
    case Model2Package.MAP_HOLDER__EOBJECT_TO_EOBJECT_KEY_CONTAINED_MAP:
      getEObjectToEObjectKeyContainedMap().clear();
      return;
    case Model2Package.MAP_HOLDER__EOBJECT_TO_EOBJECT_BOTH_CONTAINED_MAP:
      getEObjectToEObjectBothContainedMap().clear();
      return;
    case Model2Package.MAP_HOLDER__EOBJECT_TO_EOBJECT_VALUE_CONTAINED_MAP:
      getEObjectToEObjectValueContainedMap().clear();
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
    case Model2Package.MAP_HOLDER__INTEGER_TO_STRING_MAP:
      return integerToStringMap != null && !integerToStringMap.isEmpty();
    case Model2Package.MAP_HOLDER__STRING_TO_STRING_MAP:
      return stringToStringMap != null && !stringToStringMap.isEmpty();
    case Model2Package.MAP_HOLDER__STRING_TO_VAT_MAP:
      return stringToVATMap != null && !stringToVATMap.isEmpty();
    case Model2Package.MAP_HOLDER__STRING_TO_ADDRESS_CONTAINMENT_MAP:
      return stringToAddressContainmentMap != null && !stringToAddressContainmentMap.isEmpty();
    case Model2Package.MAP_HOLDER__STRING_TO_ADDRESS_REFERENCE_MAP:
      return stringToAddressReferenceMap != null && !stringToAddressReferenceMap.isEmpty();
    case Model2Package.MAP_HOLDER__EOBJECT_TO_EOBJECT_MAP:
      return eObjectToEObjectMap != null && !eObjectToEObjectMap.isEmpty();
    case Model2Package.MAP_HOLDER__EOBJECT_TO_EOBJECT_KEY_CONTAINED_MAP:
      return eObjectToEObjectKeyContainedMap != null && !eObjectToEObjectKeyContainedMap.isEmpty();
    case Model2Package.MAP_HOLDER__EOBJECT_TO_EOBJECT_BOTH_CONTAINED_MAP:
      return eObjectToEObjectBothContainedMap != null && !eObjectToEObjectBothContainedMap.isEmpty();
    case Model2Package.MAP_HOLDER__EOBJECT_TO_EOBJECT_VALUE_CONTAINED_MAP:
      return eObjectToEObjectValueContainedMap != null && !eObjectToEObjectValueContainedMap.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} // MapHolderImpl
