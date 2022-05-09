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
package org.eclipse.emf.cdo.lm;

import org.eclipse.emf.common.util.Enumerator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <!-- begin-user-doc --> A representation of the literals of the enumeration
 * '<em><b>Impact</b></em>', and utility methods for working with them. <!--
 * end-user-doc -->
 * @see org.eclipse.emf.cdo.lm.LMPackage#getImpact()
 * @model
 * @generated
 */
public enum Impact implements Enumerator
{
  /**
   * The '<em><b>Micro</b></em>' literal object.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #MICRO_VALUE
   * @generated
   * @ordered
   */
  MICRO(0, "Micro", "Micro"),
  /**
   * The '<em><b>Minor</b></em>' literal object.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #MINOR_VALUE
   * @generated
   * @ordered
   */
  MINOR(1, "Minor", "Minor"),
  /**
   * The '<em><b>Major</b></em>' literal object.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #MAJOR_VALUE
   * @generated
   * @ordered
   */
  MAJOR(2, "Major", "Major");

  /**
   * The '<em><b>Micro</b></em>' literal value.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #MICRO
   * @model name="Micro"
   * @generated
   * @ordered
   */
  public static final int MICRO_VALUE = 0;

  /**
   * The '<em><b>Minor</b></em>' literal value.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #MINOR
   * @model name="Minor"
   * @generated
   * @ordered
   */
  public static final int MINOR_VALUE = 1;

  /**
   * The '<em><b>Major</b></em>' literal value.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #MAJOR
   * @model name="Major"
   * @generated
   * @ordered
   */
  public static final int MAJOR_VALUE = 2;

  /**
   * An array of all the '<em><b>Impact</b></em>' enumerators.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   */
  private static final Impact[] VALUES_ARRAY = new Impact[] { MICRO, MINOR, MAJOR, };

  /**
   * A public read-only list of all the '<em><b>Impact</b></em>' enumerators. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public static final List<Impact> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

  /**
   * Returns the '<em><b>Impact</b></em>' literal with the specified literal value.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param literal the literal.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static Impact get(String literal)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      Impact result = VALUES_ARRAY[i];
      if (result.toString().equals(literal))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Impact</b></em>' literal with the specified name. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @param name the name.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static Impact getByName(String name)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      Impact result = VALUES_ARRAY[i];
      if (result.getName().equals(name))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Impact</b></em>' literal with the specified integer value.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the integer value.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static Impact get(int value)
  {
    switch (value)
    {
    case MICRO_VALUE:
      return MICRO;
    case MINOR_VALUE:
      return MINOR;
    case MAJOR_VALUE:
      return MAJOR;
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
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   */
  private Impact(int value, String name, String literal)
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    return literal;
  }

} // Impact
