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
package org.eclipse.emf.cdo.lm.modules;

import org.eclipse.emf.cdo.etypes.ModelElement;

import org.eclipse.emf.common.util.EList;

import org.eclipse.equinox.p2.metadata.Version;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Module
 * Definition</b></em>'.
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.modules.ModuleDefinition#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.modules.ModuleDefinition#getVersion <em>Version</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.modules.ModuleDefinition#getDependencies <em>Dependencies</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.lm.modules.ModulesPackage#getModuleDefinition()
 * @model
 * @generated
 */
public interface ModuleDefinition extends ModelElement
{
  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.eclipse.emf.cdo.lm.modules.ModulesPackage#getModuleDefinition_Name()
   * @model required="true"
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.modules.ModuleDefinition#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Version</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the value of the '<em>Version</em>' attribute.
   * @see #setVersion(Version)
   * @see org.eclipse.emf.cdo.lm.modules.ModulesPackage#getModuleDefinition_Version()
   * @model dataType="org.eclipse.emf.cdo.lm.modules.Version" required="true"
   * @generated
   */
  Version getVersion();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.modules.ModuleDefinition#getVersion <em>Version</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Version</em>' attribute.
   * @see #getVersion()
   * @generated
   */
  void setVersion(Version value);

  /**
   * Returns the value of the '<em><b>Dependencies</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.lm.modules.DependencyDefinition}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.lm.modules.DependencyDefinition#getSource <em>Source</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the value of the '<em>Dependencies</em>' containment reference list.
   * @see org.eclipse.emf.cdo.lm.modules.ModulesPackage#getModuleDefinition_Dependencies()
   * @see org.eclipse.emf.cdo.lm.modules.DependencyDefinition#getSource
   * @model opposite="source" containment="true"
   *        extendedMetaData="name='dependency' kind='element'"
   * @generated
   */
  EList<DependencyDefinition> getDependencies();

} // ModuleDefinition
