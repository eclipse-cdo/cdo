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

import org.eclipse.net4j.util.defs.Def;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Test Def</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.net4j.util.tests.defs.TestDef#getReferences <em>References</em>}</li>
 * <li>{@link org.eclipse.net4j.util.tests.defs.TestDef#getAttribute <em>Attribute</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.net4j.util.tests.defs.TestDefsPackage#getTestDef()
 * @model
 * @generated
 */
public interface TestDef extends Def
{
  /**
   * Returns the value of the '<em><b>References</b></em>' reference list. The list contents are of type
   * {@link org.eclipse.net4j.util.defs.Def}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>References</em>' reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>References</em>' reference list.
   * @see org.eclipse.net4j.util.tests.defs.TestDefsPackage#getTestDef_References()
   * @model
   * @generated
   */
  EList<Def> getReferences();

  /**
   * Returns the value of the '<em><b>Attribute</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Attribute</em>' attribute isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Attribute</em>' attribute.
   * @see #setAttribute(String)
   * @see org.eclipse.net4j.util.tests.defs.TestDefsPackage#getTestDef_Attribute()
   * @model
   * @generated
   */
  String getAttribute();

  /**
   * Sets the value of the '{@link org.eclipse.net4j.util.tests.defs.TestDef#getAttribute <em>Attribute</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Attribute</em>' attribute.
   * @see #getAttribute()
   * @generated
   */
  void setAttribute(String value);

} // TestDef
