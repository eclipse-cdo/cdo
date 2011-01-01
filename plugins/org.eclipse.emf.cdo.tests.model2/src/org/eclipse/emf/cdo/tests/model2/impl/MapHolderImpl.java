/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *
 * $Id: MapHolderImpl.java,v 1.2 2011-01-01 11:01:57 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.model2.impl;

import org.eclipse.emf.cdo.tests.model1.Address;
import org.eclipse.emf.cdo.tests.model1.VAT;
import org.eclipse.emf.cdo.tests.model2.MapHolder;
import org.eclipse.emf.cdo.tests.model2.Model2Package;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Map Holder</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model2.impl.MapHolderImpl#getIntegerToStringMap <em>Integer To String Map</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model2.impl.MapHolderImpl#getStringToStringMap <em>String To String Map</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model2.impl.MapHolderImpl#getStringToVATMap <em>String To VAT Map</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model2.impl.MapHolderImpl#getStringToAddressContainmentMap <em>String To Address
 * Containment Map</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model2.impl.MapHolderImpl#getStringToAddressReferenceMap <em>String To Address
 * Reference Map</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model2.impl.MapHolderImpl#getEObjectToEObjectMap <em>EObject To EObject Map
 * </em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model2.impl.MapHolderImpl#getEObjectToEObjectKeyContainedMap <em>EObject To
 * EObject Key Contained Map</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model2.impl.MapHolderImpl#getEObjectToEObjectBothContainedMap <em>EObject To
 * EObject Both Contained Map</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model2.impl.MapHolderImpl#getEObjectToEObjectValueContainedMap <em>EObject To
 * EObject Value Contained Map</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class MapHolderImpl extends CDOObjectImpl implements MapHolder
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected MapHolderImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return Model2Package.Literals.MAP_HOLDER;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected int eStaticFeatureCount()
  {
    return 0;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EMap<Integer, String> getIntegerToStringMap()
  {
    return (EMap<Integer, String>)eGet(Model2Package.Literals.MAP_HOLDER__INTEGER_TO_STRING_MAP, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EMap<String, String> getStringToStringMap()
  {
    return (EMap<String, String>)eGet(Model2Package.Literals.MAP_HOLDER__STRING_TO_STRING_MAP, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EMap<String, VAT> getStringToVATMap()
  {
    return (EMap<String, VAT>)eGet(Model2Package.Literals.MAP_HOLDER__STRING_TO_VAT_MAP, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EMap<String, Address> getStringToAddressContainmentMap()
  {
    return (EMap<String, Address>)eGet(Model2Package.Literals.MAP_HOLDER__STRING_TO_ADDRESS_CONTAINMENT_MAP, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EMap<String, Address> getStringToAddressReferenceMap()
  {
    return (EMap<String, Address>)eGet(Model2Package.Literals.MAP_HOLDER__STRING_TO_ADDRESS_REFERENCE_MAP, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EMap<EObject, EObject> getEObjectToEObjectMap()
  {
    return (EMap<EObject, EObject>)eGet(Model2Package.Literals.MAP_HOLDER__EOBJECT_TO_EOBJECT_MAP, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EMap<EObject, EObject> getEObjectToEObjectKeyContainedMap()
  {
    return (EMap<EObject, EObject>)eGet(Model2Package.Literals.MAP_HOLDER__EOBJECT_TO_EOBJECT_KEY_CONTAINED_MAP, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EMap<EObject, EObject> getEObjectToEObjectBothContainedMap()
  {
    return (EMap<EObject, EObject>)eGet(Model2Package.Literals.MAP_HOLDER__EOBJECT_TO_EOBJECT_BOTH_CONTAINED_MAP, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EMap<EObject, EObject> getEObjectToEObjectValueContainedMap()
  {
    return (EMap<EObject, EObject>)eGet(Model2Package.Literals.MAP_HOLDER__EOBJECT_TO_EOBJECT_VALUE_CONTAINED_MAP, true);
  }

} // MapHolderImpl
