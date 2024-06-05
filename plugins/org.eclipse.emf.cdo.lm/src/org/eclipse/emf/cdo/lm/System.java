/*
 * Copyright (c) 2022, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm;

import org.eclipse.emf.cdo.etypes.ModelElement;

import org.eclipse.emf.common.util.EList;

import java.util.function.Consumer;

/**
 * <!-- begin-user-doc --> A representation of the model object
 * '<em><b>System</b></em>'.
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.System#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.System#getProcess <em>Process</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.System#getModules <em>Modules</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.lm.LMPackage#getSystem()
 * @model
 * @generated
 */
public interface System extends ModelElement
{
  public static final String RESOURCE_PATH = "/system";

  /**
   * @since 1.2
   */
  public static final String SEPARATOR = ":";

  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.eclipse.emf.cdo.lm.LMPackage#getSystem_Name()
   * @model required="true"
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.System#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Process</b></em>' containment reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.lm.Process#getSystem <em>System</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the value of the '<em>Process</em>' containment reference.
   * @see #setProcess(org.eclipse.emf.cdo.lm.Process)
   * @see org.eclipse.emf.cdo.lm.LMPackage#getSystem_Process()
   * @see org.eclipse.emf.cdo.lm.Process#getSystem
   * @model opposite="system" containment="true" required="true"
   * @generated
   */
  org.eclipse.emf.cdo.lm.Process getProcess();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.System#getProcess <em>Process</em>}' containment reference.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @param value the new value of the '<em>Process</em>' containment reference.
   * @see #getProcess()
   * @generated
   */
  void setProcess(org.eclipse.emf.cdo.lm.Process value);

  /**
   * Returns the value of the '<em><b>Modules</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.lm.Module}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.lm.Module#getSystem <em>System</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the value of the '<em>Modules</em>' containment reference list.
   * @see org.eclipse.emf.cdo.lm.LMPackage#getSystem_Modules()
   * @see org.eclipse.emf.cdo.lm.Module#getSystem
   * @model opposite="system" containment="true"
   * @generated
   */
  EList<org.eclipse.emf.cdo.lm.Module> getModules();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @model
   * @generated
   */
  org.eclipse.emf.cdo.lm.Module getModule(String name);

  public java.util.stream.Stream<Baseline> getAllBaselines();

  public void forEachBaseline(Consumer<Baseline> consumer);

} // System
