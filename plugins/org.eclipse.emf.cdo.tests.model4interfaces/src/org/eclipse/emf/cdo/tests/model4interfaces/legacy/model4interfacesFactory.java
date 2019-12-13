/*
 * Copyright (c) 2013, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model4interfaces.legacy;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model. It provides a create method for each non-abstract class of the model.
 * @extends org.eclipse.emf.cdo.tests.model4interfaces.model4interfacesFactory
 * <!-- end-user-doc -->
 *
 * @see org.eclipse.emf.cdo.tests.legacy.model4interfaces.model4interfacesPackage
 * @generated NOT
 */
public interface model4interfacesFactory extends org.eclipse.emf.cdo.tests.model4interfaces.model4interfacesFactory
{
  /**
   * The singleton instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  model4interfacesFactory eINSTANCE = org.eclipse.emf.cdo.tests.model4interfaces.legacy.impl.model4interfacesFactoryImpl.init();

  @Override
  /**
   * Returns the package supported by this factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the package supported by this factory.
   * @generated
   */
  model4interfacesPackage getmodel4interfacesPackage();

} // model4interfacesFactory
