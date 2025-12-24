/*
 * Copyright (c) 2008-2013, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.mango;

import org.eclipse.emf.common.util.Enumerator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <!-- begin-user-doc --> A representation of the literals of the enumeration '<em><b>Parameter Passing</b></em>', and
 * utility methods for working with them. <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.tests.mango.MangoPackage#getParameterPassing()
 * @model
 * @generated
 */
public enum ParameterPassing implements Enumerator
{
  /**
   * The '<em><b>By Value</b></em>' literal object.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #BY_VALUE_VALUE
   * @generated
   * @ordered
   */
  BY_VALUE(0, "ByValue", "ByValue"),

  /**
   * The '<em><b>By Reference</b></em>' literal object.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #BY_REFERENCE_VALUE
   * @generated
   * @ordered
   */
  BY_REFERENCE(1, "ByReference", "ByReference");

  /**
   * The '<em><b>By Value</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>By Value</b></em>' literal object isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #BY_VALUE
   * @model name="ByValue"
   * @generated
   * @ordered
   */
  public static final int BY_VALUE_VALUE = 0;

  /**
   * The '<em><b>By Reference</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>By Reference</b></em>' literal object isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #BY_REFERENCE
   * @model name="ByReference"
   * @generated
   * @ordered
   */
  public static final int BY_REFERENCE_VALUE = 1;

  /**
   * An array of all the '<em><b>Parameter Passing</b></em>' enumerators.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private static final ParameterPassing[] VALUES_ARRAY = new ParameterPassing[] { BY_VALUE, BY_REFERENCE, };

  /**
   * A public read-only list of all the '<em><b>Parameter Passing</b></em>' enumerators.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   */
  public static final List<ParameterPassing> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

  /**
   * Returns the '<em><b>Parameter Passing</b></em>' literal with the specified literal value.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal the literal.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static ParameterPassing get(String literal)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      ParameterPassing result = VALUES_ARRAY[i];
      if (result.toString().equals(literal))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Parameter Passing</b></em>' literal with the specified name.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @param name the name.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static ParameterPassing getByName(String name)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      ParameterPassing result = VALUES_ARRAY[i];
      if (result.getName().equals(name))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Parameter Passing</b></em>' literal with the specified integer value.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the integer value.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static ParameterPassing get(int value)
  {
    switch (value)
    {
    case BY_VALUE_VALUE:
      return BY_VALUE;
    case BY_REFERENCE_VALUE:
      return BY_REFERENCE;
    }
    return null;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private final int value;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private final String name;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private final String literal;

  /**
   * Only this class can construct instances.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  private ParameterPassing(int value, String name, String literal)
  {
    this.value = value;
    this.name = name;
    this.literal = literal;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int getValue()
  {
    return value;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getName()
  {
    return name;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getLiteral()
  {
    return literal;
  }

  /**
   * Returns the literal value of the enumerator, which is its string representation.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    return literal;
  }

} // ParameterPassing
