/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Manual Source Locator</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.ManualSourceLocator#getLocation <em>Location</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.ManualSourceLocator#getComponentNamePattern <em>Component Name Pattern</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.ManualSourceLocator#getComponentTypes <em>Component Types</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getManualSourceLocator()
 * @model
 * @generated
 */
public interface ManualSourceLocator extends SourceLocator
{
  /**
   * Returns the value of the '<em><b>Location</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Location</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Location</em>' attribute.
   * @see #setLocation(String)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getManualSourceLocator_Location()
   * @model required="true"
   * @generated
   */
  String getLocation();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.ManualSourceLocator#getLocation <em>Location</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Location</em>' attribute.
   * @see #getLocation()
   * @generated
   */
  void setLocation(String value);

  /**
   * Returns the value of the '<em><b>Component Name Pattern</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Component Name Pattern</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Component Name Pattern</em>' attribute.
   * @see #setComponentNamePattern(String)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getManualSourceLocator_ComponentNamePattern()
   * @model
   * @generated
   */
  String getComponentNamePattern();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.ManualSourceLocator#getComponentNamePattern <em>Component Name Pattern</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Component Name Pattern</em>' attribute.
   * @see #getComponentNamePattern()
   * @generated
   */
  void setComponentNamePattern(String value);

  /**
   * Returns the value of the '<em><b>Component Types</b></em>' attribute list.
   * The list contents are of type {@link org.eclipse.emf.cdo.releng.setup.ComponentType}.
   * The literals are from the enumeration {@link org.eclipse.emf.cdo.releng.setup.ComponentType}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Component Types</em>' attribute list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Component Types</em>' attribute list.
   * @see org.eclipse.emf.cdo.releng.setup.ComponentType
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getManualSourceLocator_ComponentTypes()
   * @model required="true"
   * @generated
   */
  EList<ComponentType> getComponentTypes();

} // ManualSourceLocator
