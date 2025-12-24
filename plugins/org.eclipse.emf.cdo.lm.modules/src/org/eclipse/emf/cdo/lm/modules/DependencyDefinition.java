/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.modules;

import org.eclipse.emf.cdo.etypes.ModelElement;

import org.eclipse.equinox.p2.metadata.VersionRange;

/**
 * <!-- begin-user-doc --> A representation of the model object
 * '<em><b>Dependency Definition</b></em>'.
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.modules.DependencyDefinition#getSource <em>Source</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.modules.DependencyDefinition#getTargetName <em>Target Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.modules.DependencyDefinition#getVersionRange <em>Version Range</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.lm.modules.ModulesPackage#getDependencyDefinition()
 * @model
 * @generated
 */
public interface DependencyDefinition extends ModelElement
{
  /**
   * Returns the value of the '<em><b>Source</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.lm.modules.ModuleDefinition#getDependencies <em>Dependencies</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the value of the '<em>Source</em>' container reference.
   * @see #setSource(ModuleDefinition)
   * @see org.eclipse.emf.cdo.lm.modules.ModulesPackage#getDependencyDefinition_Source()
   * @see org.eclipse.emf.cdo.lm.modules.ModuleDefinition#getDependencies
   * @model opposite="dependencies" required="true" transient="false"
   * @generated
   */
  ModuleDefinition getSource();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.modules.DependencyDefinition#getSource <em>Source</em>}' container reference.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @param value the new value of the '<em>Source</em>' container reference.
   * @see #getSource()
   * @generated
   */
  void setSource(ModuleDefinition value);

  /**
   * Returns the value of the '<em><b>Target Name</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the value of the '<em>Target Name</em>' attribute.
   * @see #setTargetName(String)
   * @see org.eclipse.emf.cdo.lm.modules.ModulesPackage#getDependencyDefinition_TargetName()
   * @model required="true"
   * @generated
   */
  String getTargetName();

  /**
   * Sets the value of the
   * '{@link org.eclipse.emf.cdo.lm.modules.DependencyDefinition#getTargetName
   * <em>Target Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @param value the new value of the '<em>Target Name</em>' attribute.
   * @see #getTargetName()
   * @generated
   */
  void setTargetName(String value);

  /**
   * Returns the value of the '<em><b>Version Range</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the value of the '<em>Version Range</em>' attribute.
   * @see #setVersionRange(VersionRange)
   * @see org.eclipse.emf.cdo.lm.modules.ModulesPackage#getDependencyDefinition_VersionRange()
   * @model dataType="org.eclipse.emf.cdo.lm.modules.VersionRange"
   *        required="true"
   * @generated
   */
  VersionRange getVersionRange();

  /**
   * Sets the value of the
   * '{@link org.eclipse.emf.cdo.lm.modules.DependencyDefinition#getVersionRange
   * <em>Version Range</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @param value the new value of the '<em>Version Range</em>' attribute.
   * @see #getVersionRange()
   * @generated
   */
  void setVersionRange(VersionRange value);

} // DependencyDefinition
