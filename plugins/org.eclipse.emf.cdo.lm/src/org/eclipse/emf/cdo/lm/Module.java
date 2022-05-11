/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm;

import org.eclipse.emf.cdo.lm.impl.ModuleImpl;

import org.eclipse.emf.common.util.EList;

import java.util.Comparator;
import java.util.function.Consumer;

/**
 * <!-- begin-user-doc --> A representation of the model object
 * '<em><b>Module</b></em>'.
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.Module#getSystem <em>System</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.Module#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.Module#getType <em>Type</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.Module#getStreams <em>Streams</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.lm.LMPackage#getModule()
 * @model
 * @generated
 */
public interface Module extends SystemElement
{
  public static final Comparator<Object> COMPARATOR = Comparator.comparing(ModuleImpl::name);

  /**
   * Returns the value of the '<em><b>System</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.lm.System#getModules <em>Modules</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the value of the '<em>System</em>' container reference.
   * @see #setSystem(org.eclipse.emf.cdo.lm.System)
   * @see org.eclipse.emf.cdo.lm.LMPackage#getModule_System()
   * @see org.eclipse.emf.cdo.lm.System#getModules
   * @model opposite="modules" required="true" transient="false"
   * @generated
   */
  @Override
  org.eclipse.emf.cdo.lm.System getSystem();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.Module#getSystem <em>System</em>}' container reference.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @param value the new value of the '<em>System</em>' container reference.
   * @see #getSystem()
   * @generated
   */
  void setSystem(org.eclipse.emf.cdo.lm.System value);

  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.eclipse.emf.cdo.lm.LMPackage#getModule_Name()
   * @model required="true"
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.Module#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Streams</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.lm.Stream}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.lm.Stream#getModule <em>Module</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the value of the '<em>Streams</em>' containment reference list.
   * @see org.eclipse.emf.cdo.lm.LMPackage#getModule_Streams()
   * @see org.eclipse.emf.cdo.lm.Stream#getModule
   * @model opposite="module" containment="true"
   * @generated
   */
  EList<Stream> getStreams();

  /**
   * Returns the value of the '<em><b>Module Type</b></em>' reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the value of the '<em>Module Type</em>' reference.
   * @see #setType(ModuleType)
   * @see org.eclipse.emf.cdo.lm.LMPackage#getModule_Type()
   * @model required="true"
   * @generated
   */
  ModuleType getType();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.Module#getType <em>Type</em>}' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Type</em>' reference.
   * @see #getType()
   * @generated
   */
  void setType(ModuleType value);

  public Stream getStream(int majorVersion, int minorVersion);

  public Stream getStream(String codeName);

  public java.util.stream.Stream<Baseline> getAllBaselines();

  public void forEachBaseline(Consumer<Baseline> consumer);

} // Module
