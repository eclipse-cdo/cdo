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
package org.eclipse.emf.cdo.security;

import org.eclipse.emf.common.util.Enumerator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Inclusion</b></em>',
 * and utility methods for working with them.
 * @since 4.3
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.security.SecurityPackage#getInclusion()
 * @model
 * @generated
 */
public enum Inclusion implements Enumerator
{
  /**
   * The '<em><b>Exact</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #EXACT_VALUE
   * @generated
   * @ordered
   */
  EXACT(0, "Exact", "Exact"), /**
                              * The '<em><b>Exact And Up</b></em>' literal object.
                              * <!-- begin-user-doc -->
                              * <!-- end-user-doc -->
                              * @see #EXACT_AND_UP_VALUE
                              * @generated
                              * @ordered
                              */
  EXACT_AND_UP(1, "ExactAndUp", "ExactAndUp"), /**
                                               * The '<em><b>Exact And Down</b></em>' literal object.
                                               * <!-- begin-user-doc -->
                                               * <!-- end-user-doc -->
                                               * @see #EXACT_AND_DOWN_VALUE
                                               * @generated
                                               * @ordered
                                               */
  EXACT_AND_DOWN(2, "ExactAndDown", "ExactAndDown"), /**
                                                     * The '<em><b>Regex</b></em>' literal object.
                                                     * <!-- begin-user-doc -->
                                                     * <!-- end-user-doc -->
                                                     * @see #REGEX_VALUE
                                                     * @generated
                                                     * @ordered
                                                     */
  REGEX(3, "Regex", "Regex"); //$NON-NLS-1$ //$NON-NLS-2$

  /**
   * The '<em><b>Exact</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>Exact</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #EXACT
   * @model name="Exact"
   * @generated
   * @ordered
   */
  public static final int EXACT_VALUE = 0;

  /**
   * The '<em><b>Exact And Up</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>Exact And Up</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #EXACT_AND_UP
   * @model name="ExactAndUp"
   * @generated
   * @ordered
   */
  public static final int EXACT_AND_UP_VALUE = 1;

  /**
   * The '<em><b>Exact And Down</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>Exact And Down</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #EXACT_AND_DOWN
   * @model name="ExactAndDown"
   * @generated
   * @ordered
   */
  public static final int EXACT_AND_DOWN_VALUE = 2;

  /**
   * The '<em><b>Regex</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>Regex</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #REGEX
   * @model name="Regex"
   * @generated
   * @ordered
   */
  public static final int REGEX_VALUE = 3;

  /**
   * An array of all the '<em><b>Inclusion</b></em>' enumerators.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static final Inclusion[] VALUES_ARRAY = new Inclusion[] { EXACT, EXACT_AND_UP, EXACT_AND_DOWN, REGEX, };

  /**
   * A public read-only list of all the '<em><b>Inclusion</b></em>' enumerators.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static final List<Inclusion> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

  /**
   * Returns the '<em><b>Inclusion</b></em>' literal with the specified literal value.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static Inclusion get(String literal)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      Inclusion result = VALUES_ARRAY[i];
      if (result.toString().equals(literal))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Inclusion</b></em>' literal with the specified name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static Inclusion getByName(String name)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      Inclusion result = VALUES_ARRAY[i];
      if (result.getName().equals(name))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Inclusion</b></em>' literal with the specified integer value.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static Inclusion get(int value)
  {
    switch (value)
    {
    case EXACT_VALUE:
      return EXACT;
    case EXACT_AND_UP_VALUE:
      return EXACT_AND_UP;
    case EXACT_AND_DOWN_VALUE:
      return EXACT_AND_DOWN;
    case REGEX_VALUE:
      return REGEX;
    }
    return null;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private final int value;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private final String name;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private final String literal;

  /**
   * Only this class can construct instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private Inclusion(int value, String name, String literal)
  {
    this.value = value;
    this.name = name;
    this.literal = literal;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public int getValue()
  {
    return value;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getName()
  {
    return name;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getLiteral()
  {
    return literal;
  }

  /**
   * Returns the literal value of the enumerator, which is its string representation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    return literal;
  }

} // Inclusion
