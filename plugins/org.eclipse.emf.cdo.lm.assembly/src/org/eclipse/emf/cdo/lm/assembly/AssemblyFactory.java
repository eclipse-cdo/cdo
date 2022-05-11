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
package org.eclipse.emf.cdo.lm.assembly;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a
 * create method for each non-abstract class of the model.
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.lm.assembly.AssemblyPackage
 * @generated
 */
public interface AssemblyFactory extends EFactory
{
  /**
   * The singleton instance of the factory.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   */
  AssemblyFactory eINSTANCE = org.eclipse.emf.cdo.lm.assembly.impl.AssemblyFactoryImpl.init();

  /**
   * Returns a new object of class '<em>Assembly</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Assembly</em>'.
   * @generated
   */
  Assembly createAssembly();

  /**
   * Returns a new object of class '<em>Module</em>'.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return a new object of class '<em>Module</em>'.
   * @generated
   */
  AssemblyModule createAssemblyModule();

  /**
   * Returns the package supported by this factory.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @return the package supported by this factory.
   * @generated
   */
  AssemblyPackage getAssemblyPackage();

} // AssemblyFactory
