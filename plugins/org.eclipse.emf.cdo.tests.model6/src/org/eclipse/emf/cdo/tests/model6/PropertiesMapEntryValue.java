/*
 * Copyright (c) 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model6;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Properties Map Map.Entry Value</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.PropertiesMapEntryValue#getLabel <em>Label</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.tests.model6.Model6Package#getPropertiesMapEntryValue()
 * @model
 * @generated
 */
public interface PropertiesMapEntryValue extends EObject
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
   * @see org.eclipse.emf.cdo.tests.model6.Model6Package#getPropertiesMapEntryValue_Label()
   * @model
   * @generated
   */
  String getLabel();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model6.PropertiesMapEntryValue#getLabel <em>Label</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Label</em>' attribute.
   * @see #getLabel()
   * @generated
   */
  void setLabel(String value);

} // PropertiesMapEntryValue
