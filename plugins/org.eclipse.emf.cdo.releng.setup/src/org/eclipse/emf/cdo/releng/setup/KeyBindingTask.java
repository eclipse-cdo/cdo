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
 * A representation of the model object '<em><b>Key Binding Task</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.KeyBindingTask#getScheme <em>Scheme</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.KeyBindingTask#getContext <em>Context</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.KeyBindingTask#getPlatform <em>Platform</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.KeyBindingTask#getLocale <em>Locale</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.KeyBindingTask#getKeys <em>Keys</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.KeyBindingTask#getCommand <em>Command</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.KeyBindingTask#getCommandParameters <em>Command Parameters</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getKeyBindingTask()
 * @model
 * @generated
 */
public interface KeyBindingTask extends SetupTask
{

  /**
   * Returns the value of the '<em><b>Scheme</b></em>' attribute.
   * The default value is <code>"org.eclipse.ui.defaultAcceleratorConfiguration"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Scheme</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Scheme</em>' attribute.
   * @see #setScheme(String)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getKeyBindingTask_Scheme()
   * @model default="org.eclipse.ui.defaultAcceleratorConfiguration"
   * @generated
   */
  String getScheme();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.KeyBindingTask#getScheme <em>Scheme</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Scheme</em>' attribute.
   * @see #getScheme()
   * @generated
   */
  void setScheme(String value);

  /**
   * Returns the value of the '<em><b>Context</b></em>' attribute.
   * The default value is <code>"org.eclipse.ui.contexts.window"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Context</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Context</em>' attribute.
   * @see #setContext(String)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getKeyBindingTask_Context()
   * @model default="org.eclipse.ui.contexts.window"
   * @generated
   */
  String getContext();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.KeyBindingTask#getContext <em>Context</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Context</em>' attribute.
   * @see #getContext()
   * @generated
   */
  void setContext(String value);

  /**
   * Returns the value of the '<em><b>Platform</b></em>' attribute.
   * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Platform</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
   * @return the value of the '<em>Platform</em>' attribute.
   * @see #setPlatform(String)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getKeyBindingTask_Platform()
   * @model
   * @generated
   */
  String getPlatform();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.KeyBindingTask#getPlatform <em>Platform</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Platform</em>' attribute.
   * @see #getPlatform()
   * @generated
   */
  void setPlatform(String value);

  /**
   * Returns the value of the '<em><b>Locale</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Locale</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Locale</em>' attribute.
   * @see #setLocale(String)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getKeyBindingTask_Locale()
   * @model
   * @generated
   */
  String getLocale();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.KeyBindingTask#getLocale <em>Locale</em>}' attribute.
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @param value the new value of the '<em>Locale</em>' attribute.
   * @see #getLocale()
   * @generated
   */
  void setLocale(String value);

  /**
   * Returns the value of the '<em><b>Keys</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Keys</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Keys</em>' attribute.
   * @see #setKeys(String)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getKeyBindingTask_Keys()
   * @model
   * @generated
   */
  String getKeys();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.KeyBindingTask#getKeys <em>Keys</em>}' attribute.
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @param value the new value of the '<em>Keys</em>' attribute.
   * @see #getKeys()
   * @generated
   */
  void setKeys(String value);

  /**
   * Returns the value of the '<em><b>Command</b></em>' attribute.
   * <!-- begin-user-doc -->
  	 * <p>
  	 * If the meaning of the '<em>Command</em>' attribute isn't clear,
  	 * there really should be more of a description here...
  	 * </p>
  	 * <!-- end-user-doc -->
   * @return the value of the '<em>Command</em>' attribute.
   * @see #setCommand(String)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getKeyBindingTask_Command()
   * @model
   * @generated
   */
  String getCommand();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.KeyBindingTask#getCommand <em>Command</em>}' attribute.
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @param value the new value of the '<em>Command</em>' attribute.
   * @see #getCommand()
   * @generated
   */
  void setCommand(String value);

  /**
   * Returns the value of the '<em><b>Command Parameters</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.releng.setup.CommandParameter}.
   * <!-- begin-user-doc -->
  	 * <p>
  	 * If the meaning of the '<em>Command Parameters</em>' containment reference list isn't clear,
  	 * there really should be more of a description here...
  	 * </p>
  	 * <!-- end-user-doc -->
   * @return the value of the '<em>Command Parameters</em>' containment reference list.
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getKeyBindingTask_CommandParameters()
   * @model containment="true" resolveProxies="true"
   * @generated
   */
  EList<CommandParameter> getCommandParameters();
} // KeyBindingTask
