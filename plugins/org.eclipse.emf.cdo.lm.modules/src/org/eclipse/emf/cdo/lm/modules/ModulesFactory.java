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

import org.eclipse.emf.ecore.EFactory;

import org.eclipse.equinox.p2.metadata.VersionRange;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a
 * create method for each non-abstract class of the model.
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.lm.modules.ModulesPackage
 * @generated
 */
public interface ModulesFactory extends EFactory
{
  /**
   * The singleton instance of the factory.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   */
  ModulesFactory eINSTANCE = org.eclipse.emf.cdo.lm.modules.impl.ModulesFactoryImpl.init();

  /**
   * Returns a new object of class '<em>Module Definition</em>'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return a new object of class '<em>Module Definition</em>'.
   * @generated
   */
  ModuleDefinition createModuleDefinition();

  /**
   * Returns a new object of class '<em>Dependency Definition</em>'. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return a new object of class '<em>Dependency Definition</em>'.
   * @generated
   */
  DependencyDefinition createDependencyDefinition();

  DependencyDefinition createDependencyDefinition(String targetName);

  DependencyDefinition createDependencyDefinition(String targetName, VersionRange versionRange);

  /**
   * Returns the package supported by this factory.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return the package supported by this factory.
   * @generated
   */
  ModulesPackage getModulesPackage();

} // ModulesFactory
