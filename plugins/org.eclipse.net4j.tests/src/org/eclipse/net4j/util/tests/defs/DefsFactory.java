/*
 * Copyright (c) 2008, 2009, 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.net4j.util.tests.defs;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a create method for each non-abstract class of
 * the model. <!-- end-user-doc -->
 *
 * @see org.eclipse.net4j.util.tests.defs.DefsPackage
 * @generated
 */
public interface DefsFactory extends EFactory
{
  /**
   * The singleton instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  DefsFactory eINSTANCE = org.eclipse.net4j.util.tests.defs.impl.DefsFactoryImpl.init();

  /**
   * Returns a new object of class '<em>Test Def</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return a new object of class '<em>Test Def</em>'.
   * @generated
   */
  TestDef createTestDef();

  /**
   * Returns the package supported by this factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the package supported by this factory.
   * @generated
   */
  DefsPackage getDefsPackage();

} // DefsFactory
