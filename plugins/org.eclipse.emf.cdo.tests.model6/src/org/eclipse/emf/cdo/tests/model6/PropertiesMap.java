/*
 * Copyright (c) 2012, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model6;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Properties Map</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.PropertiesMap#getLabel <em>Label</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.PropertiesMap#getPersistentMap <em>Persistent Map</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.PropertiesMap#getTransientMap <em>Transient Map</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.tests.model6.Model6Package#getPropertiesMap()
 * @model
 * @generated
 */
public interface PropertiesMap extends EObject
{
  /**
   * Returns the value of the '<em><b>Label</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Label</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Label</em>' attribute.
   * @see #setLabel(String)
   * @see org.eclipse.emf.cdo.tests.model6.Model6Package#getPropertiesMap_Label()
   * @model
   * @generated
   */
  String getLabel();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model6.PropertiesMap#getLabel <em>Label</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Label</em>' attribute.
   * @see #getLabel()
   * @generated
   */
  void setLabel(String value);

  /**
   * Returns the value of the '<em><b>Persistent Map</b></em>' map.
   * The key is of type {@link java.lang.String},
   * and the value is of type {@link org.eclipse.emf.cdo.tests.model6.PropertiesMapEntryValue},
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Persistent Map</em>' map isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Persistent Map</em>' map.
   * @see org.eclipse.emf.cdo.tests.model6.Model6Package#getPropertiesMap_PersistentMap()
   * @model mapType="org.eclipse.emf.cdo.tests.model6.PropertiesMapEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.emf.cdo.tests.model6.PropertiesMapEntryValue&gt;"
   * @generated
   */
  EMap<String, PropertiesMapEntryValue> getPersistentMap();

  /**
   * Returns the value of the '<em><b>Transient Map</b></em>' map.
   * The key is of type {@link java.lang.String},
   * and the value is of type {@link org.eclipse.emf.cdo.tests.model6.PropertiesMapEntryValue},
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Transient Map</em>' map isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Transient Map</em>' map.
   * @see org.eclipse.emf.cdo.tests.model6.Model6Package#getPropertiesMap_TransientMap()
   * @model mapType="org.eclipse.emf.cdo.tests.model6.PropertiesMapEntry&lt;org.eclipse.emf.ecore.EString, org.eclipse.emf.cdo.tests.model6.PropertiesMapEntryValue&gt;" transient="true"
   * @generated
   */
  EMap<String, PropertiesMapEntryValue> getTransientMap();

} // PropertiesMap
