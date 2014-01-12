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
 * A representation of the model object '<em><b>Context Variable Task</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.ContextVariableTask#getType <em>Type</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.ContextVariableTask#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.ContextVariableTask#getValue <em>Value</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.ContextVariableTask#isStringSubstitution <em>String Substitution</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.ContextVariableTask#getLabel <em>Label</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.ContextVariableTask#getChoices <em>Choices</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getContextVariableTask()
 * @model
 * @generated
 */
public interface ContextVariableTask extends SetupTask
{
  public static final int PRIORITY = 0;

  /**
   * Returns the value of the '<em><b>Type</b></em>' attribute.
   * The default value is <code>"STRING"</code>.
   * The literals are from the enumeration {@link org.eclipse.emf.cdo.releng.setup.VariableType}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Type</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Type</em>' attribute.
   * @see org.eclipse.emf.cdo.releng.setup.VariableType
   * @see #setType(VariableType)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getContextVariableTask_Type()
   * @model default="STRING" required="true"
   * @generated
   */
  VariableType getType();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.ContextVariableTask#getType <em>Type</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Type</em>' attribute.
   * @see org.eclipse.emf.cdo.releng.setup.VariableType
   * @see #getType()
   * @generated
   */
  void setType(VariableType value);

  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getContextVariableTask_Name()
   * @model required="true"
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.ContextVariableTask#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Value</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Value</em>' attribute.
   * @see #setValue(String)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getContextVariableTask_Value()
   * @model
   * @generated
   */
  String getValue();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.ContextVariableTask#getValue <em>Value</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Value</em>' attribute.
   * @see #getValue()
   * @generated
   */
  void setValue(String value);

  /**
   * Returns the value of the '<em><b>String Substitution</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>String Substitution</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>String Substitution</em>' attribute.
   * @see #setStringSubstitution(boolean)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getContextVariableTask_StringSubstitution()
   * @model
   * @generated
   */
  boolean isStringSubstitution();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.ContextVariableTask#isStringSubstitution <em>String Substitution</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>String Substitution</em>' attribute.
   * @see #isStringSubstitution()
   * @generated
   */
  void setStringSubstitution(boolean value);

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
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getContextVariableTask_Label()
   * @model
   * @generated
   */
  String getLabel();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.ContextVariableTask#getLabel <em>Label</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Label</em>' attribute.
   * @see #getLabel()
   * @generated
   */
  void setLabel(String value);

  /**
   * Returns the value of the '<em><b>Choices</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.releng.setup.VariableChoice}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Choices</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Choices</em>' containment reference list.
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getContextVariableTask_Choices()
   * @model containment="true" resolveProxies="true"
   * @generated
   */
  EList<VariableChoice> getChoices();

} // ContextVariableTask
