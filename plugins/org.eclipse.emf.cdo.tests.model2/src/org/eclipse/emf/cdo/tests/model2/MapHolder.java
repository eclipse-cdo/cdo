/*
 * Copyright (c) 2010-2013, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *
 * $Id: MapHolder.java,v 1.3 2011-01-01 11:01:58 estepper Exp $
 */
package org.eclipse.emf.cdo.tests.model2;

import org.eclipse.emf.cdo.tests.model1.Address;
import org.eclipse.emf.cdo.tests.model1.VAT;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Map Holder</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.MapHolder#getIntegerToStringMap <em>Integer To String Map</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.MapHolder#getStringToStringMap <em>String To String Map</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.MapHolder#getStringToVATMap <em>String To VAT Map</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.MapHolder#getStringToAddressContainmentMap <em>String To Address Containment Map</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.MapHolder#getStringToAddressReferenceMap <em>String To Address Reference Map</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.MapHolder#getEObjectToEObjectMap <em>EObject To EObject Map</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.MapHolder#getEObjectToEObjectKeyContainedMap <em>EObject To EObject Key Contained Map</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.MapHolder#getEObjectToEObjectBothContainedMap <em>EObject To EObject Both Contained Map</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.MapHolder#getEObjectToEObjectValueContainedMap <em>EObject To EObject Value Contained Map</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.tests.model2.Model2Package#getMapHolder()
 * @model
 * @generated
 */
public interface MapHolder extends EObject
{
  /**
   * Returns the value of the '<em><b>Integer To String Map</b></em>' map.
   * The key is of type {@link java.lang.Integer},
   * and the value is of type {@link java.lang.String},
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Integer To String Map</em>' map isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Integer To String Map</em>' map.
   * @see org.eclipse.emf.cdo.tests.model2.Model2Package#getMapHolder_IntegerToStringMap()
   * @model mapType="org.eclipse.emf.cdo.tests.model2.IntegerToStringMap&lt;org.eclipse.emf.ecore.EIntegerObject, org.eclipse.emf.ecore.EString&gt;"
   * @generated
   */
  EMap<Integer, String> getIntegerToStringMap();

  /**
   * Returns the value of the '<em><b>String To String Map</b></em>' map.
   * The key is of type {@link java.lang.String},
   * and the value is of type {@link java.lang.String},
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>String To String Map</em>' map isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>String To String Map</em>' map.
   * @see org.eclipse.emf.cdo.tests.model2.Model2Package#getMapHolder_StringToStringMap()
   * @model mapType="org.eclipse.emf.cdo.tests.model2.StringToStringMap&lt;org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EString&gt;"
   * @generated
   */
  EMap<String, String> getStringToStringMap();

  /**
   * Returns the value of the '<em><b>String To VAT Map</b></em>' map.
   * The key is of type {@link java.lang.String},
   * and the value is of type {@link org.eclipse.emf.cdo.tests.model1.VAT},
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>String To VAT Map</em>' map isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>String To VAT Map</em>' map.
   * @see org.eclipse.emf.cdo.tests.model2.Model2Package#getMapHolder_StringToVATMap()
   * @model mapType="org.eclipse.emf.cdo.tests.model2.StringToVATMap&lt;org.eclipse.emf.ecore.EString, org.eclipse.emf.cdo.tests.model1.VAT&gt;"
   * @generated
   */
  EMap<String, VAT> getStringToVATMap();

  /**
   * Returns the value of the '<em><b>String To Address Containment Map</b></em>' map. The key is of type
   * {@link java.lang.String}, and the value is of type {@link org.eclipse.emf.cdo.tests.model1.Address}, <!--
   * begin-user-doc -->
   * <p>
   * If the meaning of the '<em>String To Address Containment Map</em>' map isn't clear, there really should be more of
   * a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>String To Address Containment Map</em>' map.
   * @see org.eclipse.emf.cdo.tests.model2.Model2Package#getMapHolder_StringToAddressContainmentMap()
   * @model mapType=
   *        "org.eclipse.emf.cdo.tests.model2.StringToAddressContainmentMap<org.eclipse.emf.ecore.EString, org.eclipse.emf.cdo.tests.model1.Address>"
   * @generated
   */
  EMap<String, Address> getStringToAddressContainmentMap();

  /**
   * Returns the value of the '<em><b>String To Address Reference Map</b></em>' map. The key is of type
   * {@link java.lang.String}, and the value is of type {@link org.eclipse.emf.cdo.tests.model1.Address}, <!--
   * begin-user-doc -->
   * <p>
   * If the meaning of the '<em>String To Address Reference Map</em>' map isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>String To Address Reference Map</em>' map.
   * @see org.eclipse.emf.cdo.tests.model2.Model2Package#getMapHolder_StringToAddressReferenceMap()
   * @model mapType=
   *        "org.eclipse.emf.cdo.tests.model2.StringToAddressReferenceMap<org.eclipse.emf.ecore.EString, org.eclipse.emf.cdo.tests.model1.Address>"
   * @generated
   */
  EMap<String, Address> getStringToAddressReferenceMap();

  /**
   * Returns the value of the '<em><b>EObject To EObject Map</b></em>' map. The key is of type
   * {@link org.eclipse.emf.ecore.EObject}, and the value is of type {@link org.eclipse.emf.ecore.EObject}, <!--
   * begin-user-doc -->
   * <p>
   * If the meaning of the '<em>EObject To EObject Map</em>' map isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>EObject To EObject Map</em>' map.
   * @see org.eclipse.emf.cdo.tests.model2.Model2Package#getMapHolder_EObjectToEObjectMap()
   * @model mapType=
   *        "org.eclipse.emf.cdo.tests.model2.EObjectToEObjectMap<org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject>"
   * @generated
   */
  EMap<EObject, EObject> getEObjectToEObjectMap();

  /**
   * Returns the value of the '<em><b>EObject To EObject Key Contained Map</b></em>' map. The key is of type
   * {@link org.eclipse.emf.ecore.EObject}, and the value is of type {@link org.eclipse.emf.ecore.EObject}, <!--
   * begin-user-doc -->
   * <p>
   * If the meaning of the '<em>EObject To EObject Key Contained Map</em>' map isn't clear, there really should be more
   * of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>EObject To EObject Key Contained Map</em>' map.
   * @see org.eclipse.emf.cdo.tests.model2.Model2Package#getMapHolder_EObjectToEObjectKeyContainedMap()
   * @model mapType=
   *        "org.eclipse.emf.cdo.tests.model2.EObjectToEObjectKeyContainedMap<org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject>"
   * @generated
   */
  EMap<EObject, EObject> getEObjectToEObjectKeyContainedMap();

  /**
   * Returns the value of the '<em><b>EObject To EObject Both Contained Map</b></em>' map. The key is of type
   * {@link org.eclipse.emf.ecore.EObject}, and the value is of type {@link org.eclipse.emf.ecore.EObject}, <!--
   * begin-user-doc -->
   * <p>
   * If the meaning of the '<em>EObject To EObject Both Contained Map</em>' map isn't clear, there really should be more
   * of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>EObject To EObject Both Contained Map</em>' map.
   * @see org.eclipse.emf.cdo.tests.model2.Model2Package#getMapHolder_EObjectToEObjectBothContainedMap()
   * @model mapType=
   *        "org.eclipse.emf.cdo.tests.model2.EObjectToEObjectBothContainedMap<org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject>"
   * @generated
   */
  EMap<EObject, EObject> getEObjectToEObjectBothContainedMap();

  /**
   * Returns the value of the '<em><b>EObject To EObject Value Contained Map</b></em>' map. The key is of type
   * {@link org.eclipse.emf.ecore.EObject}, and the value is of type {@link org.eclipse.emf.ecore.EObject}, <!--
   * begin-user-doc -->
   * <p>
   * If the meaning of the '<em>EObject To EObject Value Contained Map</em>' map isn't clear, there really should be
   * more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>EObject To EObject Value Contained Map</em>' map.
   * @see org.eclipse.emf.cdo.tests.model2.Model2Package#getMapHolder_EObjectToEObjectValueContainedMap()
   * @model mapType=
   *        "org.eclipse.emf.cdo.tests.model2.EObjectToEObjectValueContainedMap<org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject>"
   * @generated
   */
  EMap<EObject, EObject> getEObjectToEObjectValueContainedMap();

} // MapHolder
