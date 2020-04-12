/*
 * Copyright (c) 2013, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model5.legacy;

import org.eclipse.emf.cdo.tests.model5.Child;
import org.eclipse.emf.cdo.tests.model5.Doctor;
import org.eclipse.emf.cdo.tests.model5.GenListOfBoolean;
import org.eclipse.emf.cdo.tests.model5.GenListOfChar;
import org.eclipse.emf.cdo.tests.model5.GenListOfDate;
import org.eclipse.emf.cdo.tests.model5.GenListOfDouble;
import org.eclipse.emf.cdo.tests.model5.GenListOfFloat;
import org.eclipse.emf.cdo.tests.model5.GenListOfInt;
import org.eclipse.emf.cdo.tests.model5.GenListOfIntArray;
import org.eclipse.emf.cdo.tests.model5.GenListOfInteger;
import org.eclipse.emf.cdo.tests.model5.GenListOfLong;
import org.eclipse.emf.cdo.tests.model5.GenListOfShort;
import org.eclipse.emf.cdo.tests.model5.GenListOfString;
import org.eclipse.emf.cdo.tests.model5.Manager;
import org.eclipse.emf.cdo.tests.model5.Parent;
import org.eclipse.emf.cdo.tests.model5.WithCustomType;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model. It provides a create method for each non-abstract class of the model.
 * @extends org.eclipse.emf.cdo.tests.model5.Model5Factory
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.tests.model5.legacy.Model5Package
 * @generated
 */
public interface Model5Factory extends EFactory, org.eclipse.emf.cdo.tests.model5.Model5Factory
{
  /**
   * The singleton instance of the factory.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  Model5Factory eINSTANCE = org.eclipse.emf.cdo.tests.model5.legacy.impl.Model5FactoryImpl.init();

  @Override
  /**
   * Returns a new object of class '<em>Manager</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Manager</em>'.
   * @generated
   */
  Manager createManager();

  @Override
  /**
   * Returns a new object of class '<em>Doctor</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Doctor</em>'.
   * @generated
   */
  Doctor createDoctor();

  @Override
  /**
   * Returns a new object of class '<em>Gen List Of String</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Gen List Of String</em>'.
   * @generated
   */
  GenListOfString createGenListOfString();

  @Override
  /**
   * Returns a new object of class '<em>Gen List Of Int</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Gen List Of Int</em>'.
   * @generated
   */
  GenListOfInt createGenListOfInt();

  @Override
  /**
   * Returns a new object of class '<em>Gen List Of Integer</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Gen List Of Integer</em>'.
   * @generated
   */
  GenListOfInteger createGenListOfInteger();

  @Override
  /**
   * Returns a new object of class '<em>Gen List Of Long</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Gen List Of Long</em>'.
   * @generated
   */
  GenListOfLong createGenListOfLong();

  @Override
  /**
   * Returns a new object of class '<em>Gen List Of Boolean</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Gen List Of Boolean</em>'.
   * @generated
   */
  GenListOfBoolean createGenListOfBoolean();

  @Override
  /**
   * Returns a new object of class '<em>Gen List Of Short</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Gen List Of Short</em>'.
   * @generated
   */
  GenListOfShort createGenListOfShort();

  @Override
  /**
   * Returns a new object of class '<em>Gen List Of Float</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Gen List Of Float</em>'.
   * @generated
   */
  GenListOfFloat createGenListOfFloat();

  @Override
  /**
   * Returns a new object of class '<em>Gen List Of Double</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Gen List Of Double</em>'.
   * @generated
   */
  GenListOfDouble createGenListOfDouble();

  @Override
  /**
   * Returns a new object of class '<em>Gen List Of Date</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Gen List Of Date</em>'.
   * @generated
   */
  GenListOfDate createGenListOfDate();

  @Override
  /**
   * Returns a new object of class '<em>Gen List Of Char</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Gen List Of Char</em>'.
   * @generated
   */
  GenListOfChar createGenListOfChar();

  @Override
  /**
   * Returns a new object of class '<em>Gen List Of Int Array</em>'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return a new object of class '<em>Gen List Of Int Array</em>'.
   * @generated
   */
  GenListOfIntArray createGenListOfIntArray();

  @Override
  /**
   * Returns a new object of class '<em>Parent</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Parent</em>'.
   * @generated
   */
  Parent createParent();

  @Override
  /**
   * Returns a new object of class '<em>Child</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Child</em>'.
   * @generated
   */
  Child createChild();

  @Override
  /**
   * Returns a new object of class '<em>With Custom Type</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>With Custom Type</em>'.
   * @generated
   */
  WithCustomType createWithCustomType();

  @Override
  /**
   * Returns the package supported by this factory.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the package supported by this factory.
   * @generated
   */
  Model5Package getModel5Package();

} // Model5Factory
