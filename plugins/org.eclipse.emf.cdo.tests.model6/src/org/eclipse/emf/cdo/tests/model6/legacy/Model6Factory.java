/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model6.legacy;

import org.eclipse.emf.cdo.tests.model6.A;
import org.eclipse.emf.cdo.tests.model6.B;
import org.eclipse.emf.cdo.tests.model6.BaseObject;
import org.eclipse.emf.cdo.tests.model6.C;
import org.eclipse.emf.cdo.tests.model6.CanReferenceLegacy;
import org.eclipse.emf.cdo.tests.model6.ContainmentObject;
import org.eclipse.emf.cdo.tests.model6.D;
import org.eclipse.emf.cdo.tests.model6.E;
import org.eclipse.emf.cdo.tests.model6.EmptyStringDefault;
import org.eclipse.emf.cdo.tests.model6.EmptyStringDefaultUnsettable;
import org.eclipse.emf.cdo.tests.model6.F;
import org.eclipse.emf.cdo.tests.model6.G;
import org.eclipse.emf.cdo.tests.model6.HasNillableAttribute;
import org.eclipse.emf.cdo.tests.model6.Holder;
import org.eclipse.emf.cdo.tests.model6.MyEnumList;
import org.eclipse.emf.cdo.tests.model6.MyEnumListUnsettable;
import org.eclipse.emf.cdo.tests.model6.PropertiesMap;
import org.eclipse.emf.cdo.tests.model6.PropertiesMapEntryValue;
import org.eclipse.emf.cdo.tests.model6.ReferenceObject;
import org.eclipse.emf.cdo.tests.model6.Root;
import org.eclipse.emf.cdo.tests.model6.Thing;
import org.eclipse.emf.cdo.tests.model6.UnorderedList;
import org.eclipse.emf.cdo.tests.model6.UnsettableAttributes;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * @extends org.eclipse.emf.cdo.tests.model6.Model6Factory
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.tests.model6.legacy.Model6Package
 * @generated
 */
public interface Model6Factory extends EFactory, org.eclipse.emf.cdo.tests.model6.Model6Factory
{
  /**
   * The singleton instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  Model6Factory eINSTANCE = org.eclipse.emf.cdo.tests.model6.legacy.impl.Model6FactoryImpl.init();

  /**
   * Returns a new object of class '<em>Root</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Root</em>'.
   * @generated
   */
  Root createRoot();

  /**
   * Returns a new object of class '<em>Base Object</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Base Object</em>'.
   * @generated
   */
  BaseObject createBaseObject();

  /**
   * Returns a new object of class '<em>Reference Object</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Reference Object</em>'.
   * @generated
   */
  ReferenceObject createReferenceObject();

  /**
   * Returns a new object of class '<em>Containment Object</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Containment Object</em>'.
   * @generated
   */
  ContainmentObject createContainmentObject();

  /**
   * Returns a new object of class '<em>Unordered List</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Unordered List</em>'.
   * @generated
   */
  UnorderedList createUnorderedList();

  /**
   * Returns a new object of class '<em>Properties Map</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Properties Map</em>'.
   * @generated
   */
  PropertiesMap createPropertiesMap();

  /**
   * Returns a new object of class '<em>Properties Map Entry Value</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Properties Map Entry Value</em>'.
   * @generated
   */
  PropertiesMapEntryValue createPropertiesMapEntryValue();

  /**
   * Returns a new object of class '<em>A</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>A</em>'.
   * @generated
   */
  A createA();

  /**
   * Returns a new object of class '<em>B</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>B</em>'.
   * @generated
   */
  B createB();

  /**
   * Returns a new object of class '<em>C</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>C</em>'.
   * @generated
   */
  C createC();

  /**
   * Returns a new object of class '<em>D</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>D</em>'.
   * @generated
   */
  D createD();

  /**
   * Returns a new object of class '<em>E</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>E</em>'.
   * @generated
   */
  E createE();

  /**
   * Returns a new object of class '<em>F</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>F</em>'.
   * @generated
   */
  F createF();

  /**
   * Returns a new object of class '<em>G</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>G</em>'.
   * @generated
   */
  G createG();

  /**
   * Returns a new object of class '<em>My Enum List</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>My Enum List</em>'.
   * @generated
   */
  MyEnumList createMyEnumList();

  /**
   * Returns a new object of class '<em>My Enum List Unsettable</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>My Enum List Unsettable</em>'.
   * @generated
   */
  MyEnumListUnsettable createMyEnumListUnsettable();

  /**
   * Returns a new object of class '<em>Holder</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Holder</em>'.
   * @generated
   */
  Holder createHolder();

  /**
   * Returns a new object of class '<em>Thing</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Thing</em>'.
   * @generated
   */
  Thing createThing();

  /**
   * Returns a new object of class '<em>Has Nillable Attribute</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Has Nillable Attribute</em>'.
   * @generated
   */
  HasNillableAttribute createHasNillableAttribute();

  /**
   * Returns a new object of class '<em>Empty String Default</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Empty String Default</em>'.
   * @generated
   */
  EmptyStringDefault createEmptyStringDefault();

  /**
   * Returns a new object of class '<em>Empty String Default Unsettable</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Empty String Default Unsettable</em>'.
   * @generated
   */
  EmptyStringDefaultUnsettable createEmptyStringDefaultUnsettable();

  /**
   * Returns a new object of class '<em>Unsettable Attributes</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Unsettable Attributes</em>'.
   * @generated
   */
  UnsettableAttributes createUnsettableAttributes();

  /**
   * Returns a new object of class '<em>Can Reference Legacy</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Can Reference Legacy</em>'.
   * @generated
   */
  CanReferenceLegacy createCanReferenceLegacy();

  /**
  	 * Returns the package supported by this factory.
  	 * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
  	 * @return the package supported by this factory.
  	 * @generated
  	 */
  Model6Package getModel6Package();

} // Model6Factory
