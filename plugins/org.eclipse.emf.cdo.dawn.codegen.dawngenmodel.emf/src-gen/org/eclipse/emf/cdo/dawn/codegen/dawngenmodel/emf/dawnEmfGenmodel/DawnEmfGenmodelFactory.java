/*
 * Copyright (c) 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.emf.dawnEmfGenmodel;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a create method for each non-abstract class of
 * the model. <!-- end-user-doc -->
 *
 * @see org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.emf.dawnEmfGenmodel.DawnEmfGenmodelPackage
 * @generated
 * @author Martin Fluegge
 */
public interface DawnEmfGenmodelFactory extends EFactory
{
  /**
   * The singleton instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  DawnEmfGenmodelFactory eINSTANCE = org.eclipse.emf.cdo.dawn.codegen.dawngenmodel.emf.dawnEmfGenmodel.impl.DawnEmfGenmodelFactoryImpl.init();

  /**
   * Returns a new object of class '<em>Dawn EMF Generator</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return a new object of class '<em>Dawn EMF Generator</em>'.
   * @generated
   */
  DawnEMFGenerator createDawnEMFGenerator();

  /**
   * Returns the package supported by this factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the package supported by this factory.
   * @generated
   */
  DawnEmfGenmodelPackage getDawnEmfGenmodelPackage();

} // DawnEmfGenmodelFactory
